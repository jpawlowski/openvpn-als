
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.adito.boot.CodedException;
import com.adito.boot.ContextHolder;
import com.adito.boot.PropertyClass;
import com.adito.boot.PropertyClassManager;
import com.adito.boot.PropertyDefinition;
import com.adito.boot.PropertyList;
import com.adito.core.CoreException;
import com.adito.core.UserDatabaseManager;
import com.adito.core.forms.CoreForm;
import com.adito.policyframework.PolicyUtil;
import com.adito.properties.Property;
import com.adito.properties.attributes.AttributeDefinition;
import com.adito.properties.attributes.AttributeValueItem;
import com.adito.properties.impl.userattributes.UserAttributeKey;
import com.adito.properties.impl.userattributes.UserAttributes;
import com.adito.security.LogonControllerFactory;
import com.adito.security.Role;
import com.adito.security.User;
import com.adito.security.UserDatabase;
import com.adito.tabs.TabModel;

/**
 * Implementation of a tabbed {@link com.adito.core.forms.CoreForm} that
 * allows an administrator to edit account details.
 * <p>
 * The amount of editable details will depend on whether the underlying user
 * database supports account creation / editing.
 * <p>
 * Editing of user attributes and the enabled flag will always be available, the
 * rest such as name, fullname, email etc will only be available when an
 * appropriate user database is used.
 */
public class UserAccountForm extends CoreForm implements TabModel {
    static Log log = LogFactory.getLog(UserAccountForm.class);

    // Private instance variables

    private String username;
    private String email;
    private String fullname;
    private boolean setPassword;
    private boolean enabled;
    private PropertyList roles;
    private List userAttributeValueItems;
    private String selectedTab = "details";
    private List categoryIds;
    private List categoryTitles;
    private PropertyClass propertyClass;
    private String realmName;

    /**
     * Constructor
     */
    public UserAccountForm() {
        propertyClass = PropertyClassManager.getInstance().getPropertyClass(UserAttributes.NAME);
    }

    /**
     * @return String
     */
    public String getRealmName() {
        return realmName;
    }

    /**
     * @param realmName
     */
    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    /**
     * Get the username
     * 
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username
     * 
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username.trim();
    }

    /**
     * Get the full name
     * 
     * @return full name
     */
    public String getFullname() {
        return fullname;
    }

    /**
     * Set the full name
     * 
     * @param fullname full name
     */
    public void setFullname(String fullname) {
        this.fullname = fullname.trim();
    }

    /**
     * Get whether this account should be enabled or not.
     * 
     * @return enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set whether this account should be enabled or not.
     * 
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Get the email address
     * 
     * @return email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email address
     * 
     * @param email email address
     */
    public void setEmail(String email) {
        this.email = email.trim();
    }

    /**
     * Set whether the password should be set when this form is committed.
     * 
     * @param setPassword set password when committed
     */
    public void setSetPassword(boolean setPassword) {
        this.setPassword = setPassword;
    }

    /**
     * Get whether the password should be set when this form is committed.
     * 
     * @return set password when committed
     */
    public boolean isSetPassword() {
        return setPassword;
    }

    /**
     * Initialise the form
     * 
     * @param user account to edit
     * @param editing editing
     * @param request request
     * @throws Exception on any error
     */
    public void initialize(User user, boolean editing, HttpServletRequest request) throws Exception {

        username = user == null ? "" : user.getPrincipalName();
        realmName = user == null ? "" : user.getRealm().getResourceName();
        email = user == null ? "" : user.getEmail();
        fullname = user == null ? "" : user.getFullname();
        try {
            enabled = user == null ? true : PolicyUtil.isEnabled(user);
        } catch (Exception e) {
            log.warn("Failed to determine if user is enabled, defaulting to disabled.");
            enabled = false;
        }
        setActionTarget("commit");
        setPassword = false;
        this.editing = editing;
        roles = new PropertyList();
        Role[] allRoles = user == null ? new Role[0] : user.getRoles();
        for (int i = 0; i < allRoles.length; i++) {
            roles.add(allRoles[i].getPrincipalName());
        }

        /*
         * Get all of the user attribute definitions and wrap them in item
         * objects
         */

        userAttributeValueItems = new ArrayList();
        for (PropertyDefinition d : propertyClass.getDefinitions()) {
            AttributeDefinition def = (AttributeDefinition) d;
            if (!def.isHidden()) {
                if (def.getVisibility() != AttributeDefinition.USER_CONFIDENTIAL_ATTRIBUTE) {
                    String value = def.getDefaultValue();
                    if (user != null) {
                        value = Property.getProperty(new UserAttributeKey(user, def.getName()));
                    }
                    AttributeValueItem item = new AttributeValueItem(def, request, value);
                    userAttributeValueItems.add(item);
                }
            }
        }

        /*
         * Sort the list of items and build up the list of categories
         */

        Collections.sort(userAttributeValueItems);
        categoryIds = new ArrayList();
        categoryTitles = new ArrayList();
        for (Iterator i = userAttributeValueItems.iterator(); i.hasNext();) {
            AttributeValueItem item = (AttributeValueItem) i.next();
            int idx = categoryIds.indexOf(item.getCategoryId());
            if (idx == -1) {
                categoryIds.add(item.getCategoryId());
                categoryTitles.add(item.getCategoryLabel());
            }
        }
    }

    /**
     * Get a list of the category ids
     * 
     * @return category ids
     */
    public List getCategoryIds() {
        return categoryIds;
    }

    /**
     * Get the list of user attribute value items
     * 
     * @return user attribute value items
     */
    public List getAttributeValueItems() {
        return userAttributeValueItems;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, javax.servlet.http.HttpServletRequest request) {
        enabled = false;
        if (userAttributeValueItems != null) {
            for (Iterator i = userAttributeValueItems.iterator(); i.hasNext();) {
                AttributeValueItem item = (AttributeValueItem) i.next();
                if (item.getDefinition().getType() == PropertyDefinition.TYPE_BOOLEAN) {
                    item.setSelected(false);
                }
            }
        }
    }

    /**
     * Get whether the username is disabled or not
     * 
     * TODO is this required, can't editing be used?
     * 
     * @return username disabled
     */
    public String getUsernameDisabled() {
        return String.valueOf(getEditing());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if (isCommiting()) {
            UserDatabase udb;
            try {
                udb = UserDatabaseManager.getInstance().getUserDatabase(
                    LogonControllerFactory.getInstance().getUser(request).getRealm());
            } catch (Exception e1) {
                errors.add(Globals.ERROR_KEY, new ActionMessage("availableRoles.noUserDatabase"));
                return errors;
            }
            if (username == null || username.length() == 0) {
                errors.add(Globals.ERROR_KEY, new ActionMessage("createAccount.error.noUsername"));
            }
            if (username.length() > 32) {
                errors.add(Globals.ERROR_KEY, new ActionMessage("createAccount.error.usernameExceeds32Chars"));
            }
            if (udb.supportsAccountCreation()) {
                if (fullname == null || fullname.length() == 0) {
                    errors.add(Globals.ERROR_KEY, new ActionMessage("createAccount.error.noFullName"));
                }
                if (fullname.length() > 32) {
                    errors.add(Globals.ERROR_KEY, new ActionMessage("createAccount.error.fullNameExceeds32Chars"));
                }
            }
            User currentUser;
            try {
                currentUser = ContextHolder.getContext().isSetupMode() ? null : LogonControllerFactory.getInstance().getUser(
                    request);
                if (currentUser != null && getEditing() && currentUser.getPrincipalName().equals(getUsername())) {
                    // Make sure there is at least one enabled account
                    if (!isEnabled()) {
                        errors.add(Globals.ERROR_KEY, new ActionMessage("createAccount.error.cantDisableYourself"));
                    }
                }
                if (!editing) {
                    try {
                        udb.getAccount(username);
                        errors.add(Globals.ERROR_KEY, new ActionMessage("createAccount.error.userAlreadyExists", username));
                    } catch (Exception e) {
                    }
                }
                for (Iterator i = userAttributeValueItems.iterator(); i.hasNext();) {
                    AttributeValueItem item = (AttributeValueItem) i.next();
                    PropertyDefinition def = item.getDefinition();
                    try {
                        def.validate(item.getValue().toString(), getClass().getClassLoader());
                    } catch (CoreException ce) {
                        ce.getBundleActionMessage().setArg3(item.getLabel());
                        errors.add(Globals.ERROR_KEY, ce.getBundleActionMessage());
                    } catch (CodedException ce) {
                        errors.add(Globals.ERROR_KEY, new ActionMessage("[Err:" + ce.getCode() + "] " + ce.getMessage()));
                    }
                }

                // Validate selected groups
                for (String role : getRolesList()) {
                    try {
                        UserDatabaseManager.getInstance().getDefaultUserDatabase().getRole(role);
                    } catch (Exception e) {
                        errors.add(Globals.ERROR_KEY, new ActionMessage("createAccount.error.invalidRole", role));
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to validate user form.", e);
                e.printStackTrace();
            }
        }
        return errors;
    }

    /**
     * Get the list of roles this account is attached (as strings)
     * 
     * @return roles
     */
    public PropertyList getRolesList() {
        return roles;
    }

    /**
     * Get a list of selected roles as a list in <i>Text Field Text</i> format.
     * 
     * @return list of roles
     */
    public String getSelectedRoles() {
        return roles.getAsTextFieldText();
    }

    /**
     * Set a list of selected roles as a list in <i>Text Field Text</i> format.
     * 
     * @param selectedRoles selected roles
     */
    public void setSelectedRoles(String selectedRoles) {
        roles.setAsTextFieldText(selectedRoles);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#getTabCount()
     */
    public int getTabCount() {
        return 1 + (categoryIds.size());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#getTabName(int)
     */
    public String getTabName(int idx) {
        switch (idx) {
            case 0:
                return "details";
            default:
                return (String) categoryIds.get(idx - 1);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#getSelectedTab()
     */
    public String getSelectedTab() {
        return selectedTab;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#setSelectedTab(java.lang.String)
     */
    public void setSelectedTab(String selectedTab) {
        this.selectedTab = selectedTab;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#getTabTitle(int)
     */
    public String getTabTitle(int idx) {
        switch (idx) {
            case 0:
                return null;
            default:
                return (String) categoryTitles.get(idx - 1);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.tabs.TabModel#getTabBundle(int)
     */
    public String getTabBundle(int idx) {
        return null;
    }
}
