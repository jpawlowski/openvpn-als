
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.install.forms;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.ovpnals.core.UserDatabaseManager;
import com.ovpnals.properties.Property;
import com.ovpnals.properties.impl.realms.RealmKey;
import com.ovpnals.realms.Realm;
import com.ovpnals.security.UserDatabase;
import com.ovpnals.security.UserDatabaseDefinition;
import com.ovpnals.wizard.AbstractWizardSequence;
import com.ovpnals.wizard.forms.DefaultWizardForm;


/**
 * Wizard form implementation allowing the primary user database to
 * be selected.
 */
public class SelectUserDatabaseForm extends DefaultWizardForm {
	
	final static Log log = LogFactory.getLog(SelectUserDatabaseForm.class);
	
    /**
     * Constant for the userDatabase type name
     */
    public final static String ATTR_USER_DATABASE = "userDatabase";
	
    /**
     * Constant for the userDatabase instance
     */
    public final static String ATTR_USER_DATABASE_INSTANCE = "userDatabaseInstance";

    /**
     * Constant for the userDatabase has changed
     */
    public final static String ATTR_USER_DATABASE_CHANGED = "userDatabaseChanged";

    /**
     * Coonstant for the realm name 
     */
    public final static String ATTR_REALM_NAME = "realmName";
    
    // Private instance variables
    private String userDatabase;
    private String oldUserDatabase;

    /**
     * Constructor
     */
    public SelectUserDatabaseForm() {
        super(true, true, "/WEB-INF/jsp/content/install/selectUserDatabase.jspf",
            "", false, false, "selectUserDatabase", "install", "installation.selectUserDatabase", 2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.wizard.forms.AbstractWizardForm#init(com.ovpnals.wizard.AbstractWizardSequence)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {
        userDatabase = (String)sequence.getAttribute(ATTR_USER_DATABASE, 
        	Property.getProperty(new RealmKey("security.userDatabase", UserDatabaseManager.getInstance().getDefaultRealm())));
        oldUserDatabase = userDatabase;
    }
    
    /**
     * Get all registered user database definitions
     * 
     * @return user database definitions
     */
    public Collection<UserDatabaseDefinition> getUserDatabaseDefinitions() {
        return UserDatabaseManager.getInstance().getUserDatabaseDefinitions();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.wizard.forms.AbstractWizardForm#apply(com.ovpnals.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        sequence.putAttribute(ATTR_USER_DATABASE, userDatabase);
        
        // Determine if the user database chosen is different to that selected
        UserDatabase installUserDb = (UserDatabase)sequence.getAttribute(ATTR_USER_DATABASE_INSTANCE, null);
        UserDatabaseDefinition udbDef = UserDatabaseManager.getInstance().getUserDatabaseDefinition(userDatabase);
        UserDatabaseDefinition oldDef = UserDatabaseManager.getInstance().getUserDatabaseDefinition(oldUserDatabase);
        Realm defaultRealm = UserDatabaseManager.getInstance().getDefaultRealm();
        if(installUserDb == null) {
	        if(isUsingDifferentDatabase(oldDef, udbDef)) {
	        	log.info("Selected user database differs from current default, open new database");
	        	installUserDb = UserDatabaseManager.getInstance().createUserDatabase(udbDef.getName(), defaultRealm.getResourceName(), defaultRealm.getResourceDescription(), false);
                sequence.putAttribute(ATTR_USER_DATABASE_CHANGED, Boolean.TRUE);
	        }
	        else {
	        	log.info("User database is same as current.");
	        	installUserDb = UserDatabaseManager.getInstance().getDefaultUserDatabase();
	        }
        }
        else {        	
	        if(!installUserDb.getClass().equals(udbDef.getUserDatabaseClass())) {
	        	if(installUserDb.isOpen()) {
		        	log.info("Closing current user database " + installUserDb.getClass() + ".");
		        	installUserDb.close();
	        	}
                log.info("Selected user database differs from current install userdatabase, open new database");
                sequence.putAttribute(ATTR_USER_DATABASE_CHANGED, Boolean.TRUE);
                installUserDb = UserDatabaseManager.getInstance().createUserDatabase(udbDef.getName(), defaultRealm.getResourceName(), defaultRealm.getResourceDescription(), false);
	        }
	        else {
	        	log.info("Not changing current user database, already in use");	        	
	        }
        }
    	log.info("Using user database " + installUserDb.getClass() + ".");
        sequence.putAttribute(ATTR_USER_DATABASE_INSTANCE, installUserDb);
    }

    private static boolean isUsingDifferentDatabase(UserDatabaseDefinition oldDefinition, UserDatabaseDefinition newDefinition) {
        if (oldDefinition == null) {
            return true;
        }
        return !oldDefinition.getUserDatabaseClass().equals(newDefinition.getUserDatabaseClass());
    }
    
    /**
     * @return Returns the userDatabase.
     */
    public String getUserDatabase() {
        return userDatabase;
    }

    /**
     * @param userDatabase The userDatabase to set.
     */
    public void setUserDatabase(String userDatabase) {
        this.userDatabase = userDatabase;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(!isCommiting()) {
            return null;
        }
        
        UserDatabaseDefinition databaseDefinition = UserDatabaseManager.getInstance().getUserDatabaseDefinition(userDatabase);
        if(databaseDefinition == null) {
            ActionErrors errors = new ActionErrors();
            errors.add(Globals.ERROR_KEY, new ActionMessage("installation.configureUserDatabase.error.not.selected"));
            return errors;
        }
        return super.validate(mapping, request);
    }
}
