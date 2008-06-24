
				/*
 *  Adito
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
			
package com.adito.security.forms;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.adito.core.FieldValidationException;
import com.adito.core.UserDatabaseManager;
import com.adito.core.forms.CoreForm;
import com.adito.properties.Property;
import com.adito.properties.impl.realms.RealmKey;
import com.adito.security.LogonControllerFactory;
import com.adito.security.User;
import com.adito.security.UserDatabase;
import com.adito.security.UserDatabaseException;

public class ChangePasswordForm extends CoreForm {

    String oldPassword, newPassword, confirmPassword;
    String username;

    public ChangePasswordForm() {
    }

    public void init(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword.trim();
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword.trim();
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword.trim();
    }

    public void reset(ActionMapping mapping, javax.servlet.http.HttpServletRequest request) {
        oldPassword = null;
        newPassword = null;
        confirmPassword = null;
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {

        ActionErrors errors = new ActionErrors();
        try {
            User user = LogonControllerFactory.getInstance().getUser(request);
            UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(user.getRealm());
            if (getOldPassword().length() == 0) {
                throw new FieldValidationException("noOldPassword");
            }
            if (getOldPassword().equals(getNewPassword())) {
                throw new FieldValidationException("newAndOldPasswordMatch");
            }
            if (!getNewPassword().equals(getConfirmPassword())) {
                throw new FieldValidationException("newAndConfirmPasswordsDontMatch");
            }
            if (getNewPassword().length() == 0) {
                throw new FieldValidationException("noNewPassword");
            }
            if (!udb.checkPassword(user.getPrincipalName(), getOldPassword())) {
                throw new FieldValidationException("oldPasswordIncorrect");
            } else {
                // Check that the password matches the current policy, if not
                // then request a new one
                try {
                    String pattern = Property.getProperty(new RealmKey("security.password.pattern", user.getRealm().getResourceId()));
                    Pattern p = Pattern.compile(pattern);
                    if (!p.matcher(newPassword).matches()) {
                        throw new FieldValidationException("doesNotMatchPolicy");
                    }
                } catch(FieldValidationException fve) {
                	throw fve;
                } catch (Exception e) {
                    throw new UserDatabaseException("Could not check password against current policy.", e);
                }
            }
        } catch (FieldValidationException fve) {
            errors.add(Globals.ERROR_KEY, new ActionMessage("changePassword.error." + fve.getResourceKey()));
        } catch (Exception e) {
            errors.add(Globals.ERROR_KEY, new ActionMessage("changePassword.error.validateFailed", e.getMessage()));
        }
        return errors;
    }
}