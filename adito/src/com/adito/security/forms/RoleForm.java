
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

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.adito.boot.PropertyList;
import com.adito.boot.Util;
import com.adito.core.UserDatabaseManager;
import com.adito.core.forms.CoreForm;
import com.adito.realms.Realm;
import com.adito.security.LogonControllerFactory;
import com.adito.security.User;
import com.adito.security.UserDatabase;


/**
 * Implementation of a {@link CoreForm} that allows an administrator to 
 * create or edit a <i>Group</i> of users (previously known as a <i>Role</i>).
 */
public class RoleForm extends CoreForm {
    private String rolename;    
    private PropertyList users;
    
    /**
     * Get the name of the group.
     * 
     * @return group
     */
    public String getRolename() {
        return rolename;
    }

    /**
     * Set the name of the role
     * 
     * @param rolename name of role
     */
    public void setRolename(String rolename) {
        this.rolename = rolename.trim();
    }
    
    /**
     * Get the selected as users as a list of strings
     * 
     * @return selected users as a list of strings
     */
    public List<String> getUserList() {
        return users;
    }
    
    /**
     * Get the list of selected users in <i>Text Field Text</i> format
     * 
     * @return selected users
     * @see PropertyList
     */
    public String getUsers() {
        return users.getAsTextFieldText();
    }
    
    /**
     * Set the list of selected users in <i>Text Field Text</i> format
     * 
     * @param users selected users
     * @see PropertyList
     */
    public void setUsers(String users) {
        this.users.setAsTextFieldText(users);
    }
    
    /**
     * Initialise the form
     * 
     * @param users list of {@link User} objects attached to the role
     */
    public void initialize(Collection<User> users) {
        rolename = "";        
        this.users = new PropertyList();
        for (User user : users) {
            this.users.add(user.getPrincipalName());
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if (isCommiting()) {
           if (Util.isNullOrTrimmedBlank(rolename)) {
                errors.add(Globals.ERROR_KEY, new ActionMessage("availableRoles.noRolenameSupplied"));
            }
            if (rolename.length() > 32) {
                errors.add(Globals.ERROR_KEY, new ActionMessage("availableRoles.roleNameExceeds32Chars"));
            }
            
            try {
                Realm realm = LogonControllerFactory.getInstance().getUser(request).getRealm();
                UserDatabase userDatabase = UserDatabaseManager.getInstance().getUserDatabase(realm.getResourceId());
                if (!getEditing() && userDatabase.isRoleNameInUse(rolename)) {
                    errors.add(Globals.ERROR_KEY, new ActionMessage("availableRoles.roleAlreadyExists", rolename));
                }
                for (String username : users) {
                    if (!userDatabase.isAccountNameInUse(username)) {
                        errors.add(Globals.ERROR_KEY, new ActionMessage("availableRoles.noExistingAccount", username));
                    }
                }
            } catch (Exception expt) {
                errors.add(Globals.ERROR_KEY, new ActionMessage("availableRoles.noUserDatabase"));
            }
        }
        return errors;
    }
}