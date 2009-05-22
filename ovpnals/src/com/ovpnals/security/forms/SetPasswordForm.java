
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
			
package com.ovpnals.security.forms;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.ovpnals.core.forms.CoreForm;
import com.ovpnals.properties.Property;
import com.ovpnals.properties.impl.realms.RealmKey;
import com.ovpnals.security.User;
import com.ovpnals.security.UserDatabaseException;

/**
 * Form for setting the users password, with the option to make the user to change the password on logon.
 */
public class SetPasswordForm extends CoreForm {

    private static final long serialVersionUID = -4783370317292273239L;
    /**
     * As this might be redirected for further authentication we have to stuff
     * the information in the session to retrieve it later.  An example of this
     * is a user logging in via PIN authentication then trying to create a user.
     * The logged on user needs to enter their session password to gain access
     * and thus there are a few redirects which means the form is reset.
     */
    public static final String SAVED_PASSWORD = "setPassword.saved.password";
    /**
     * See comment above.
     */
    public static final String SAVED_FORCE_PASSWORD_CHANGE = "setPassword.saved.forceChange";
    
    private String newPassword;
    private String confirmPassword;
    private User user;
    private boolean forceChangePasswordAtLogon;

    /**
     * @param user
     */
    public void initialize(User user) {
        this.user = user;
    }

    /**
     * @return boolean
     */
    public boolean getForceChangePasswordAtLogon() {
        return forceChangePasswordAtLogon;
    }

    /**
     * @param forceChangePasswordAtLogon
     */
    public void setForceChangePasswordAtLogon(boolean forceChangePasswordAtLogon) {
        this.forceChangePasswordAtLogon = forceChangePasswordAtLogon;
    }

    /**
     * @return String
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * @return String
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * @param newPassword
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * @param confirmPassword
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        newPassword = "";
        confirmPassword = "";
        forceChangePasswordAtLogon = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if ("commit".equals(request.getParameter("action"))) {
            HttpSession session = request.getSession();
            String passwordToSet = (String) session.getAttribute(SAVED_PASSWORD);
            if(passwordToSet == null) {
                return validate();
            } else {
                newPassword = passwordToSet;
                confirmPassword = passwordToSet;
                forceChangePasswordAtLogon = (Boolean) session.getAttribute(SAVED_FORCE_PASSWORD_CHANGE);
                session.removeAttribute(SAVED_PASSWORD);
                session.removeAttribute(SAVED_FORCE_PASSWORD_CHANGE);
            }
        }
        return null;
    }
    
    private ActionErrors validate() {
        ActionErrors errors = new ActionErrors();
        try {
            if (getNewPassword().length() == 0) {
                errors.add(Globals.ERROR_KEY, new ActionMessage("setPassword.error.noNewPassword"));
            } else if (!getNewPassword().equals(getConfirmPassword())) {
                errors.add(Globals.ERROR_KEY, new ActionMessage("setPassword.error.newAndConfirmPasswordsDontMatch"));
            } else {
                // Check that the password matches the current policy, if
                // not then request a new one
                try {
                    String pattern = Property.getProperty(new RealmKey("security.password.pattern", getUser().getRealm().getResourceId()));
                    Pattern p = Pattern.compile(pattern);
                    if (!p.matcher(newPassword).matches()) {
                        errors.add(Globals.ERROR_KEY, new ActionMessage("setPassword.error.doesNotMatchPolicy"));
                    }
                } catch (Exception e) {
                    throw new UserDatabaseException("Could not check password against current policy.", e);
                }
            }
        } catch (Exception e) {
            errors.add(Globals.ERROR_KEY, new ActionMessage("setPassword.error.validateFailed", e.getMessage()));
        }
        return errors;
    }

    /**
     * @return User
     */
    public User getUser() {
        return user;
    }
}