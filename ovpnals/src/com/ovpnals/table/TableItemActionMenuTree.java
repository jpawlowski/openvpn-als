
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
			
package com.ovpnals.table;

import javax.servlet.http.HttpServletRequest;

import com.ovpnals.core.AvailableMenuItem;
import com.ovpnals.core.MenuItem;
import com.ovpnals.extensions.itemactions.DisableExtensionAction;
import com.ovpnals.extensions.itemactions.EnableExtensionAction;
import com.ovpnals.extensions.itemactions.ExtensionInformationAction;
import com.ovpnals.extensions.itemactions.InstallExtensionAction;
import com.ovpnals.extensions.itemactions.RemoveExtensionAction;
import com.ovpnals.extensions.itemactions.StartExtensionAction;
import com.ovpnals.extensions.itemactions.StopExtensionAction;
import com.ovpnals.extensions.itemactions.UpdateExtensionAction;
import com.ovpnals.navigation.MenuTree;
import com.ovpnals.navigation.itemactions.RemoveFavoriteAction;
import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.policyframework.ResourceType;
import com.ovpnals.policyframework.itemactions.CloneResourceAction;
import com.ovpnals.policyframework.itemactions.EditResourceAction;
import com.ovpnals.policyframework.itemactions.RemovePolicyAction;
import com.ovpnals.policyframework.itemactions.RemoveResourceAction;
import com.ovpnals.properties.itemactions.RemoveProfileAction;
import com.ovpnals.properties.itemactions.ViewProfileAction;
import com.ovpnals.security.LogonController;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.UserItem;
import com.ovpnals.security.itemactions.DisableAuthenticationSchemeAction;
import com.ovpnals.security.itemactions.EditAccountTableAction;
import com.ovpnals.security.itemactions.EnableAuthenticationSchemeAction;
import com.ovpnals.security.itemactions.MoveAuthenticationSchemeDownAction;
import com.ovpnals.security.itemactions.MoveAuthenticationSchemeUpAction;
import com.ovpnals.security.itemactions.RemoveAuthenticationSchemeAction;

/**
 * Extension of {@link com.ovpnals.navigation.MenuTree} used for the main
 * menu navigation (i.e. the bar on the left in the default UI).
 */
public class TableItemActionMenuTree extends MenuTree {
    /**
     * Menu tree name
     */
    public static final String MENU_TABLE_ITEM_ACTION_MENU_TREE = "tableItemAction";

    /**
     * Constructor.
     */
    public TableItemActionMenuTree() {
        super(TableItemActionMenuTree.MENU_TABLE_ITEM_ACTION_MENU_TREE);
        addFavorites();
        addProfiles();
        addPersonalProfiles();
        addExtensions();
        addAccessRights();
        addUsers();
        addAuthenticationSchemes();
        addPolicies();
    }

    private void addFavorites() {
        addMenuItem(null, new MenuItem("favorites", "navigation", null, 100, false, SessionInfo.USER_CONSOLE_CONTEXT));
        addMenuItem("favorites", new RemoveFavoriteAction());
    }

    private void addProfiles() {
        addMenuItem(null, new MenuItem("profiles", "policyframework", null, 100, false, SessionInfo.ALL_CONTEXTS));
        addMenuItem("profiles", new ViewProfileAction());
        addMenuItem("profiles", new RemoveProfileAction());
        addMenuItem("profiles", new EditResourceAction(SessionInfo.ALL_CONTEXTS, "properties"));
        addMenuItem("profiles", new CloneResourceAction(SessionInfo.ALL_CONTEXTS, "properties"));
    }

    private void addPersonalProfiles() {
        addMenuItem(null, new MenuItem("personalProfiles", "policyframework", null, 100, false, SessionInfo.USER_CONSOLE_CONTEXT));
        addMenuItem("personalProfiles", new ViewProfileAction());
        addMenuItem("personalProfiles", new RemoveProfileAction());
        addMenuItem("personalProfiles", new EditResourceAction(SessionInfo.USER_CONSOLE_CONTEXT, "properties"));
        addMenuItem("personalProfiles", new CloneResourceAction(SessionInfo.USER_CONSOLE_CONTEXT, "properties"));
    }

    private void addAuthenticationSchemes() {
        addMenuItem(null, new MenuItem("authenticationSchemes", "security", null, 100, false, SessionInfo.ALL_CONTEXTS));
        addMenuItem("authenticationSchemes", new RemoveAuthenticationSchemeAction(SessionInfo.MANAGEMENT_CONSOLE_CONTEXT,
                        "security"));
        addMenuItem("authenticationSchemes", new EditResourceAction(SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, "security"));
        addMenuItem("authenticationSchemes", new CloneResourceAction(SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, "security"));
        addMenuItem("authenticationSchemes", new EnableAuthenticationSchemeAction());
        addMenuItem("authenticationSchemes", new DisableAuthenticationSchemeAction());
        addMenuItem("authenticationSchemes", new MoveAuthenticationSchemeUpAction());
        addMenuItem("authenticationSchemes", new MoveAuthenticationSchemeDownAction());
    }

    private void addPolicies() {
        addMenuItem(null, new MenuItem("policies", "security", null, 100, false, SessionInfo.ALL_CONTEXTS));
        addMenuItem("policies", new RemovePolicyAction(SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, "policyframework"));
        addMenuItem("policies", new EditResourceAction(SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, "policyframework"));
        addMenuItem("policies", new CloneResourceAction(SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, "policyframework"));
    }

    private void addExtensions() {
        addMenuItem(null, new MenuItem("extensionStore", "extensions", null, 100, false, SessionInfo.ALL_CONTEXTS));
        addMenuItem("extensionStore", new InstallExtensionAction());
        addMenuItem("extensionStore", new UpdateExtensionAction());
        addMenuItem("extensionStore", new RemoveExtensionAction());
        addMenuItem("extensionStore", new ExtensionInformationAction());
        addMenuItem("extensionStore", new EnableExtensionAction());
        addMenuItem("extensionStore", new DisableExtensionAction());
        addMenuItem("extensionStore", new StartExtensionAction());
        addMenuItem("extensionStore", new StopExtensionAction());
    }

    private void addUsers() {
        addMenuItem(null, new MenuItem("accounts", null, null, 100, false, SessionInfo.ALL_CONTEXTS));
        addMenuItem("accounts", getEditAccountAction());
        addMenuItem("accounts", getDeleteAccountAction());
        addMenuItem("accounts", getSetPasswordAction());
        addMenuItem("accounts", getEnableAccountAction());
        addMenuItem("accounts", getDisableAccountAction());
        addMenuItem("accounts", getResetPrivateKey());
        addMenuItem("accounts", getSendUserMessage());
    }

    private void addAccessRights() {
        addMenuItem(null, new MenuItem("accessRights", "policyframework", null, 100, false, SessionInfo.ALL_CONTEXTS));
        addMenuItem("accessRights", new EditResourceAction(SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, "policyframework"));
        addMenuItem("accessRights", new CloneResourceAction(SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, "policyframework"));
        addMenuItem("accessRights", new RemoveResourceAction(SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, "policyframework"));
    }

    private static TableItemAction getEditAccountAction() {
        return new EditAccountTableAction("edit", 100, true, "/editAccount.do?actionTarget=edit&username={0}");
    }

    private static TableItemAction getDeleteAccountAction() {
        return new EditAccountTableAction("remove", 150, true,
                        "/showAvailableAccounts.do?actionTarget=confirmAccountDeletion&username={0}", false, true) {
            @Override
            public Permission[] getPermissions(ResourceType resourceType) {
                return new Permission[] { resourceType.getPermission(PolicyConstants.PERM_DELETE_ID) };
            }
        };
    }

    private static TableItemAction getSetPasswordAction() {
        return new EditAccountTableAction("setPassword", 210, false,
                        "/showAvailableAccounts.do?actionTarget=password&username={0}", true, false);
    }

    private static TableItemAction getEnableAccountAction() {
        return new EditAccountTableAction("enable", 200, false, "/showAvailableAccounts.do?actionTarget=enable&username={0}") {
            @Override
            public boolean isEnabled(UserItem userItem) throws Exception {
                return LogonController.ACCOUNT_LOCKED == userItem.getStatus()
                                || (LogonController.ACCOUNT_LOCKED != userItem.getStatus() && !userItem.getEnabled());
            }
        };
    }

    private static TableItemAction getDisableAccountAction() {
        return new EditAccountTableAction("disable", 200, false, "/showAvailableAccounts.do?actionTarget=disable&username={0}") {
            @Override
            public boolean isEnabled(UserItem userItem) throws Exception {
                return LogonController.ACCOUNT_LOCKED != userItem.getStatus() && userItem.getEnabled();
            }
        };
    }

    private static TableItemAction getResetPrivateKey() {
        return new EditAccountTableAction("resetPrivateKey", 240, false,
                        "/showAvailableAccounts.do?actionTarget=confirmResetPrivateKey&username={0}") {
            @Override
            public Permission[] getPermissions(ResourceType resourceType) {
                return new Permission[] {};
            }
        };
    }

    private static TableItemAction getSendUserMessage() {
        return new EditAccountTableAction("sendUserMessage", 290, false,
                        "/showAvailableAccounts.do?actionTarget=sendMessage&username={0}") {
            @Override
            public Permission[] getPermissions(ResourceType resourceType) {
                return new Permission[] {};
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.navigation.MenuTree#createAvailableMenuItem(com.ovpnals.core.MenuItem,
     *      com.ovpnals.core.AvailableMenuItem,
     *      javax.servlet.http.HttpServletRequest, java.lang.String, int,
     *      com.ovpnals.security.SessionInfo)
     */
    public AvailableMenuItem createAvailableMenuItem(MenuItem item, AvailableMenuItem parent, HttpServletRequest request,
                                                     String referer, int checkNavigationContext, SessionInfo info) {
        return new AvailableTableItemAction(item, parent, request, referer, checkNavigationContext, info);
    }
}