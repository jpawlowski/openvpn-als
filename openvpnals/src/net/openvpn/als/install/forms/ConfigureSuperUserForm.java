
				/*
 *  OpenVPNALS
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			
package net.openvpn.als.install.forms;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import net.openvpn.als.boot.Util;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.core.UserDatabaseManager;
import net.openvpn.als.jdbc.JDBCUserDatabase;
import net.openvpn.als.realms.DefaultRealm;
import net.openvpn.als.realms.Realm;
import net.openvpn.als.security.User;
import net.openvpn.als.security.UserDatabase;
import net.openvpn.als.security.UserDatabaseException;
import net.openvpn.als.wizard.AbstractWizardSequence;
import net.openvpn.als.wizard.forms.DefaultWizardForm;


/**
 * Wizard for for selecting and configuring the super user account to
 * use.
 */
public class ConfigureSuperUserForm extends DefaultWizardForm {

    final static Log log = LogFactory.getLog(ConfigureSuperUserForm.class);

    /**
     * Super user password
     */
    public final static String ATTR_SUPER_USER_PASSWORD = "superUserPassword";
    
    /**
     * Super user password
     */
    public final static String ATTR_SUPER_USER = "account";
    
    /**
     * Super user email
     */
    public final static String ATTR_SUPER_USER_EMAIL = "email";

    // Statics
    final static String DUMMY_PASSWORD = "**********";

    // Private instance variables
    private String confirmSuperUserPassword;
    private String superUserPassword;
    private String account;
    private String email;
    private String databaseType;
    private UserDatabase userDatabase;

    /**
     * Constructor.
     *
     */
    public ConfigureSuperUserForm() {
        super(true, true, "/WEB-INF/jsp/content/install/configureSuperUser.jspf", "account", false, false, "configureSuperUser",
            "install", "installation.configureSuperUser", 3);
    }

    private boolean setDefaultUser(String[] toMatch) throws Exception {
        for (String username : toMatch) {
            if (!Util.isNullOrTrimmedBlank(username) && userDatabase.isAccountNameInUse(username)) {
                User user = userDatabase.getAccount(username);
                account = user.getPrincipalName();
                email = user.getEmail();
                return true;
            }
        }
        return false;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#init(net.openvpn.als.wizard.AbstractWizardSequence)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {
        databaseType = (String) sequence.getAttribute(SelectUserDatabaseForm.ATTR_USER_DATABASE, JDBCUserDatabase.DATABASE_TYPE);
        userDatabase = (UserDatabase) sequence.getAttribute(SelectUserDatabaseForm.ATTR_USER_DATABASE_INSTANCE, null);
    	
        // Temporarily load the selected user database
    	if(!userDatabase.isOpen()) {
    		log.info("Opening user databse");
        	Calendar now = Calendar.getInstance();
            Realm realm = new DefaultRealm((String)sequence.getAttribute(SelectUserDatabaseForm.ATTR_USER_DATABASE, null), 1, UserDatabaseManager.DEFAULT_REALM_NAME, UserDatabaseManager.DEFAULT_REALM_DESCRIPTION, now, now);
    		userDatabase.open(CoreServlet.getServlet(), realm);
    	}
    	
        try {
            boolean setDefaultUser = setDefaultUser(new String[]{account, "admin", "root", "Administrator"});
            if(!setDefaultUser) {
                User[] users = userDatabase.listAllUsers(UserDatabase.WILDCARD_SEARCH, 1);
                if (users.length > 0) {
                    account = users[0].getPrincipalName();
                    email = users[0].getEmail();
                }
            }
        } catch (UserDatabaseException e) {
            log.error("Failed to load user database.", e);
        }
            
        /* DUMMY_PASSWORD is sent back because we don't want password
         * values sent back in HTML
         */
        String password = (String) sequence.getAttribute(ATTR_SUPER_USER_PASSWORD, "");
        if (!password.equals("")) {
            superUserPassword = DUMMY_PASSWORD;
            confirmSuperUserPassword = DUMMY_PASSWORD;
        } else {
            superUserPassword = "";
            confirmSuperUserPassword = "";
        }
        
        String foundUsername = (String) sequence.getAttribute(ATTR_SUPER_USER, "");
        account = Util.isNullOrTrimmedBlank(foundUsername) ? account : foundUsername;
        String foundEmail = (String) sequence.getAttribute(ATTR_SUPER_USER_EMAIL, email);
        email = Util.isNullOrTrimmedBlank(email) ? email : foundEmail;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#apply(net.openvpn.als.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        String password = getSuperUserPassword(sequence);
        sequence.putAttribute(ATTR_SUPER_USER_PASSWORD, password);
        sequence.putAttribute(ATTR_SUPER_USER, account);
        sequence.putAttribute(ATTR_SUPER_USER_EMAIL, email);
    }
    
    private String getSuperUserPassword(AbstractWizardSequence sequence) {
        if(!getSuperUserCreationSupported()) {
            return "";
        }
        
        if (DUMMY_PASSWORD.equals(superUserPassword)) {
            return (String) sequence.getAttribute(ATTR_SUPER_USER_PASSWORD, "");
        } else {
            return superUserPassword;
        }      
    }

    /**
     * Set the super user password
     * 
     * @param superUserPassword super user password
     */
    public void setSuperUserPassword(String superUserPassword) {
        this.superUserPassword = superUserPassword;
    }

    /**
     * Get the super user password
     * 
     * @return super user password
     */
    public String getSuperUserPassword() {
        return superUserPassword;
    }

    /**
     * Set the confirmed super user password
     * 
     * @param confirmSuperUserPassword confirmed super user password
     */
    public void setConfirmSuperUserPassword(String confirmSuperUserPassword) {
        this.confirmSuperUserPassword = confirmSuperUserPassword;
    }

    /**
     * Get the confirmed super user password
     * 
     * @return confirmed super user password
     */
    public String getConfirmSuperUserPassword() {
        return confirmSuperUserPassword;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if (isCommiting() && superUserPassword != null) {
            try {
                // keypasswords do not match
                if (!( superUserPassword.equals(DUMMY_PASSWORD) && confirmSuperUserPassword.equals(DUMMY_PASSWORD)) && !superUserPassword.equals(confirmSuperUserPassword)) {
                    throw new Exception("passwordsDoNotMatch");
                }
                if (account.equals("")) {
                    throw new Exception("noSuperUserSpecified");
                } 
                
                if (!userDatabase.isAccountNameInUse(account)) {
                    if (getSuperUserCreationSupported()) {
                        if (superUserPassword.equals("")) {
                            throw new Exception("noPassword");
                        }
                    } else {
                        throw new Exception("superUserDoesntExist");
                    }
                }
            } catch (Exception e) {
                // Always report to user when an error is encountered
                ActionErrors errs = new ActionErrors();
                errs.add(Globals.ERROR_KEY, new ActionMessage("installation.configureSuperUser.error." + e.getMessage()));
                return errs;
            }
        }
        return null;
    }

    /**
     * Get the super user account name
     * 
     * @return super user account name
     */
    public String getAccount() {
        return account;
    }

    /**
     * Get the super user account email address
     * 
     * @return super user account email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the super user account name
     * 
     * @param account super user account name
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * Set the super user account email address
     * 
     * @param email super user account email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Returns the userDatabase.
     */
    public UserDatabase getUserDatabase() {
        return userDatabase;
    }

    /**
     * Get if account creation is supported by the current user
     * database. For this the underlying user database implementation
     * must support both account creation and password change
     * 
     * @return account creation supported by current user database
     */
    public boolean getSuperUserCreationSupported() {
        return JDBCUserDatabase.DATABASE_TYPE.equals(databaseType);
    }
}
