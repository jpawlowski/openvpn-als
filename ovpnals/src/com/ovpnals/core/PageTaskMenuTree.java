
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
			
package com.ovpnals.core;

import javax.servlet.http.HttpServletRequest;

import com.ovpnals.navigation.MenuTree;
import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.security.SessionInfo;

/**
 * Menu tree used for <i>Page Tasks</i>.
 */
public class PageTaskMenuTree extends MenuTree {
	
    /**
     * Menuu tree id
     */
    public static final String PAGE_TASK_MENU_TREE = "pageTask";

    /**
     * Constructor.
     *
     */
    public PageTaskMenuTree() {
        super(PageTaskMenuTree.PAGE_TASK_MENU_TREE);

        // User Profiles Page Tasks
        addMenuItem(null, new MenuItem("showUserProfiles", null, null, 100, false, SessionInfo.ALL_CONTEXTS));
        addMenuItem("showUserProfiles", new MenuItem("createUserProfile", "properties",
            "/showUserProfiles.do?actionTarget=create&profileScope=personal", 100,  true, null,
            SessionInfo.USER_CONSOLE_CONTEXT, PolicyConstants.PERSONAL_PROFILE_RESOURCE_TYPE, new Permission[] {
                PolicyConstants.PERM_MAINTAIN
            }, null));

        // Globa Profiles Page Tasks
        addMenuItem(null, new MenuItem("showGlobalProfiles", null, null, 100, false, SessionInfo.ALL_CONTEXTS));
        addMenuItem("showGlobalProfiles", new MenuItem("createGlobalProfile", "properties",
            "?actionTarget=create&profileScope=global", 100, true, PolicyConstants.PROFILE_RESOURCE_TYPE,
            new Permission[] {
                PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN
            }) {

            public boolean isAvailable(int checkNavigationContext, SessionInfo info, HttpServletRequest request) {
                return super.isAvailable(checkNavigationContext, info, request);
            }
        });

        // Show Available Roles
        addMenuItem(null, new MenuItem("showAvailableRoles", null, null, 100, false, SessionInfo.ALL_CONTEXTS));
        addMenuItem("showAvailableRoles", new MenuItem("createRole", "security",
            "?actionTarget=create", 100, true, 
            PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, new Permission[] {
                    PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN
            }) {
            public boolean isAvailable(int checkNavigationContext, SessionInfo info, HttpServletRequest request) {
                boolean available = super.isAvailable(checkNavigationContext, info, request);
                if (available) {
                    try {
                        available = UserDatabaseManager.getInstance().getUserDatabase(info.getUser().getRealm()).supportsAccountCreation();
                    } catch (Exception e1) {
                        available = false;
                }
                }
                return available;
            }
        });
        
        // Show Available IP Restrictions
        addMenuItem(null, new MenuItem("showAvailableIpRestrictions", null, null, 100, false, SessionInfo.ALL_CONTEXTS));
        addMenuItem("showAvailableIpRestrictions", new MenuItem("createIpRestriction", "security",
            "?actionTarget=create", 100, true, 
            PolicyConstants.IP_RESTRICTIONS_RESOURCE_TYPE, new Permission[] {
                    PolicyConstants.PERM_CREATE
            }));

        // Show Available Accounts
        addMenuItem(null, new MenuItem("showAvailableAccounts", null, null, 100, false, SessionInfo.ALL_CONTEXTS));
        addMenuItem("showAvailableAccounts", new MenuItem("createAccount", "security",
            "?actionTarget=create", 100, true,
            PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, new Permission[] {
                PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN
            }) {
            public boolean isAvailable(int checkNavigationContext, SessionInfo info, HttpServletRequest request) {
                boolean available = super.isAvailable(checkNavigationContext, info, request);
                if (available) {
                    try {
                        available = UserDatabaseManager.getInstance().getUserDatabase(info.getUser().getRealm()).supportsAccountCreation();
                    } catch (Exception e1) {
                        available = false;
                }
                }
                return available;
            }
        });

        // Edit Account Page
        addMenuItem(null, new MenuItem("editAccount", null, null, 100, false, SessionInfo.ALL_CONTEXTS));
        addMenuItem("editAccount", new MenuItem("resetUserAttributes", "security",
                        "javascript: setActionTarget('resetUserAttributes'); document.forms[0].submit();", 200, true, "_self",
                        SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE,
                        new Permission[] { PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN }));
        
        // Authentication Schemes Page
        addMenuItem(null, new MenuItem("showAuthenticationSchemes", null, null, 100, false, SessionInfo.ALL_CONTEXTS));
        addMenuItem("showAuthenticationSchemes", new MenuItem("createAuthenticationScheme", "security",
            "/athenticationSchemeDetails.do", 100, true, "_self",
            SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE, new Permission[] {
                PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN
            }));
        
        // User attribute definitions
        addMenuItem(null, new MenuItem("showAttributeDefinitions", null, null, 100, false, SessionInfo.ALL_CONTEXTS));
        addMenuItem("showAttributeDefinitions", new MenuItem("createAttributeDefinition", "properties",
            "?actionTarget=create&propertyClassName=userAttributes", 100, true, 
            PolicyConstants.ATTRIBUTE_DEFINITIONS_RESOURCE_TYPE, new Permission[] {
                    PolicyConstants.PERM_MAINTAIN
            }));


        // User attributes
        addMenuItem(null, new MenuItem("userAttributes", null, null, 100, false, SessionInfo.ALL_CONTEXTS));
        addMenuItem("userAttributes", new MenuItem("resetAttributes", "security",
                        "?actionTarget=resetUserAttributes", 100, true, "_self",
                        SessionInfo.USER_CONSOLE_CONTEXT, PolicyConstants.ATTRIBUTES_RESOURCE_TYPE, new Permission[] {
                            PolicyConstants.PERM_MAINTAIN
                        }));

        // Policies
        addMenuItem(null, new MenuItem("policies", null, null, 100, false, SessionInfo.ALL_CONTEXTS));
        addMenuItem("policies", new MenuItem("createPolicy", "policyframework", "/policyDetails.do", 100, true, null,
            SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.POLICY_RESOURCE_TYPE, new Permission[] {
                PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN
            }, null));

        // Configure Policies (breaks out from resource creation wizard)
        addMenuItem(null, new MenuItem("configurePolicies", null, null, 100, false, SessionInfo.ALL_CONTEXTS));
        addMenuItem("configurePolicies", new MenuItem("createPolicy", "policyframework", "/policyDetails.do", 100, true,
            null, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.POLICY_RESOURCE_TYPE, new Permission[] {
                PolicyConstants.PERM_CREATE
            }, null));
        addMenuItem("configurePolicies", new MenuItem("cancelConfigurePolicies", "policyframework",
            "javascript: setActionTarget('cancel'); document.forms[0].submit()", 100, true, "_self",
            SessionInfo.MANAGEMENT_CONSOLE_CONTEXT));

        // Resource Permissions
        addMenuItem(null, new MenuItem("accessRightsList", null, null, 100, false, SessionInfo.ALL_CONTEXTS));
        
        addMenuItem("accessRightsList", new MenuItem("createDelegationAccessRights", "policyframework",
                        "/accessRightsDetails.do?class=delegation", 100, true, null, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT,
                        PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE,
                        new Permission[] { PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN }));
        
        addMenuItem("accessRightsList", new MenuItem("createSystemAccessRights", "policyframework",
                        "/accessRightsDetails.do?class=system", 200, true, null, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT,
                        PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE,
                        new Permission[] { PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN }));

        addMenuItem("accessRightsList", new MenuItem("createPersonalAccessRights", "policyframework",
                        "/accessRightsDetails.do?class=personal", 300, true, null, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT,
                        PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE,
                        new Permission[] { PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN }));

        // Message Queue
        addMenuItem(null, new MenuItem("messageQueue", null, null, 100, false, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT));
        addMenuItem("messageQueue", new MenuItem("sendMessage", "setup",
            "?actionTarget=sendMessage", 100, true, null,
            SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.MESSAGE_QUEUE_RESOURCE_TYPE, new Permission[] {
                PolicyConstants.PERM_SEND
            }, null));
        addMenuItem("messageQueue", new MenuItem("clearQueue", "setup",
            "?actionTarget=confirmClearQueue", 200, true, null,
            SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.MESSAGE_QUEUE_RESOURCE_TYPE, new Permission[] {
                PolicyConstants.PERM_CLEAR
            }, null));

        
        // Create Replacements
        addMenuItem(null, new MenuItem("showReplacements", null, null, 100, false, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT));
        addMenuItem("showReplacements", new MenuItem("createReplacement", "services", "/showReplacements.do?actionTarget=create", 100, true,
            "_self", SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.REPLACEMENTS_RESOURCE_TYPE, 
            new Permission[] { PolicyConstants.PERM_CHANGE}));
        
        // Extension Manager
        addMenuItem(null, new MenuItem("showExtensionStore", null, null, 100, false, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT));
        addMenuItem("showExtensionStore", new MenuItem("uploadExtension", "extensions", "/installed.do?subForm=installedExtensionsForm&amp;actionTarget=upload", 100, true,
            "_self", SessionInfo.MANAGEMENT_CONSOLE_CONTEXT));
        addMenuItem("showExtensionStore", new MenuItem("refreshExtensionStore", "extensions", "?subForm=updateableExtensionsForm&amp;actionTarget=refresh", 100, true,
                        "_self", SessionInfo.MANAGEMENT_CONSOLE_CONTEXT));
        
        // SSL Certificates
        addMenuItem(null, new MenuItem("showKeyStore", null, null, 100, false, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT));
        addMenuItem("showKeyStore", new MenuItem("keyStoreImport", "keystore", "/keyStoreImportType.do", 100, true,
            "_self", SessionInfo.MANAGEMENT_CONSOLE_CONTEXT));
        addMenuItem("showKeyStore", new MenuItem("generateServerCertificateCSR", "keystore", "/generateServerCertificateCSR.do", 200, true,
                        "_blank", SessionInfo.MANAGEMENT_CONSOLE_CONTEXT));
        addMenuItem("showKeyStore", new MenuItem("downloadServerCertificateCSR", "keystore", "/downloadServerCertificateCSR.do", 300, true,
                        "_self", SessionInfo.MANAGEMENT_CONSOLE_CONTEXT));

        // Shutdown page
        addMenuItem(null, new MenuItem("showShutdown", null, null, 100, false, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT));
        addMenuItem("showShutdown", new MenuItem("broadcastShutdownMessage", "setup", "javascript: setActionTarget('sendMessage'); document.forms[0].submit();", 100, true,
            "_self", SessionInfo.MANAGEMENT_CONSOLE_CONTEXT));

    }
}
