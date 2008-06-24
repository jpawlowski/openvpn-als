
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
			
package com.adito.core;

import javax.servlet.http.HttpServletRequest;

import com.adito.navigation.MenuTree;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.PolicyUtil;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;


/**
 * Extension of {@link com.adito.navigation.MenuTree} used for the main
 * menu navigation (i.e. the bar on the left in the default UI).
 */
public class CoreMenuTree extends MenuTree {
    /**
     * Menu tree name
     */
    public static final String MENU_ITEM_MENU_TREE = "menuItem";

    /**
     * Constructor.     *
     */
    public CoreMenuTree() {
        super(CoreMenuTree.MENU_ITEM_MENU_TREE);

        // Menu Bar

        addMenuItem(null, new MenuItem("myAccount", "navigation", null, 100, false, SessionInfo.USER_CONSOLE_CONTEXT));
        addMenuItem(null, new MenuItem("resources", "navigation", null, 200, false, SessionInfo.USER_CONSOLE_CONTEXT));

        // My Account Menu

        addMenuItem("myAccount", new MenuItem("favorites", "navigation", "/showFavorites.do", 100, true, null,
            SessionInfo.USER_CONSOLE_CONTEXT, null, null, null));

        addMenuItem("myAccount", new ShowChangePasswordMenuItem());

        addMenuItem("myAccount", new MenuItem("userAttributes",
				"navigation", "/userAttributes.do", 500, true, null,
				SessionInfo.USER_CONSOLE_CONTEXT,
				PolicyConstants.ATTRIBUTES_RESOURCE_TYPE,
				new Permission[] { PolicyConstants.PERM_MAINTAIN }, null));
        
        // Resources menu

        addMenuItem("resources", new MenuItem("userProfiles", "navigation", "/showUserProfiles.do", 100, true, null,
            SessionInfo.USER_CONSOLE_CONTEXT, PolicyConstants.PERSONAL_PROFILE_RESOURCE_TYPE, new Permission[] {
                PolicyConstants.PERM_MAINTAIN
            }, PolicyConstants.PROFILE_RESOURCE_TYPE));

        // Administration

        addMenuItem(null, new MenuItem("configuration", "navigation", null, 100, false, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT));
        addMenuItem(null, new MenuItem("accessControl", "navigation", null, 200, false, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT));
        addMenuItem(null, new MenuItem("globalResources", "navigation", null, 300, false, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT));
        addMenuItem(null, new MenuItem("system", "navigation", null, 300, false, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT));
        
        
        // System menu
        addMenuItem("system", new MenuItem("status", "navigation", "/status.do", 100, true, null,
            SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.STATUS_TYPE_RESOURCE_TYPE, new Permission[] {
                PolicyConstants.PERM_VIEW
            }, null));
        addMenuItem("system", new MenuItem("messageQueue", "navigation", "/messageQueue.do", 200, true, null,
            SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.MESSAGE_QUEUE_RESOURCE_TYPE, new Permission[] {
                            PolicyConstants.PERM_VIEW, PolicyConstants.PERM_CONTROL, PolicyConstants.PERM_CLEAR,
                            PolicyConstants.PERM_SEND
            }, null));
        addMenuItem("system", new MenuItem("shutdown", "navigation", "/showShutdown.do", 300, true, null,
            SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.SERVICE_CONTROL_RESOURCE_TYPE, new Permission[] {
                            PolicyConstants.PERM_SHUTDOWN, PolicyConstants.PERM_RESTART
            }, null));

        // Configuration

        addMenuItem("configuration", new MenuItem("systemConfiguration", "navigation", "/showSystemConfiguration.do", 100, 
            true, null, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.SYSTEM_CONFIGURATION_RESOURCE_TYPE,
            new Permission[] {
                PolicyConstants.PERM_CHANGE
            }, null));

        addMenuItem("configuration", new MenuItem("extensionStore", "navigation", "/showExtensionStore.do?actionTarget=list", 200,
             true, null, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.EXTENSIONS_RESOURCE_TYPE,
            new Permission[] {PolicyConstants.PERM_CHANGE}, null));

        addMenuItem("configuration", new MenuItem("keyStore", "navigation", "/showKeyStore.do", 300, true, null,
                SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.KEYSTORE_RESOURCE_TYPE, new Permission[] {
                    PolicyConstants.PERM_CHANGE
                }, null));
        
        addMenuItem("configuration", new MenuItem("attributeDefinitions", "navigation", "/showAttributeDefinitions.do", 700, 
            true, null, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.ATTRIBUTE_DEFINITIONS_RESOURCE_TYPE,
            new Permission[] {
                PolicyConstants.PERM_MAINTAIN
            }, null));

        // Access Control

        addMenuItem("accessControl", new MenuItem("authorizedPrincipals", "navigation", "/showAvailableAccounts.do", 100, 
            true, null, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE,
            new Permission[] {
                            PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_DELETE
            }, null));

        addMenuItem("accessControl", new MenuItem("availableRoles", "navigation", "/showAvailableRoles.do", 200,  true, null,
            SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.ACCOUNTS_AND_GROUPS_RESOURCE_TYPE, new Permission[] {
                            PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_DELETE
            }, null));
        addMenuItem("accessControl", new MenuItem("policies", "navigation", "/policies.do", 300,  true, null,
            SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.POLICY_RESOURCE_TYPE, new Permission[] {
                            PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_EDIT_AND_ASSIGN, PolicyConstants.PERM_DELETE, PolicyConstants.PERM_ASSIGN
                            
            }, null));
        
        addMenuItem("accessControl", new AccessRightsListMenuItem());
        
        addMenuItem("accessControl", new MenuItem("authenticationSchemes", "navigation", "/showAuthenticationSchemes.do", 500,
                true, null, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE,
               new Permission[] {
                               PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_EDIT_AND_ASSIGN, PolicyConstants.PERM_DELETE,
                               PolicyConstants.PERM_ASSIGN
               }, null));
        
        addMenuItem("accessControl", new MenuItem("ipRestrictions", "navigation", "/showAvailableIpRestrictions.do", 600, true, null,
            SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.IP_RESTRICTIONS_RESOURCE_TYPE, new Permission[] {
                            PolicyConstants.PERM_CREATE, PolicyConstants.PERM_DELETE
            }, null));

        // Global Resources
        addMenuItem("globalResources", new MenuItem("globalProfiles", "navigation", "/showGlobalProfiles.do", 500, true,
                null, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.PROFILE_RESOURCE_TYPE, new Permission[] {
                                PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_EDIT_AND_ASSIGN,
                                PolicyConstants.PERM_DELETE, PolicyConstants.PERM_ASSIGN
                }));
        
        // Help

        addMenuItem(null, new MenuItem("help", "navigation", null, 100, false, SessionInfo.HELP_CONTEXT));

        addMenuItem("help", new MenuItem("about", "navigation", "/showAbout.do", 200, true, SessionInfo.HELP_CONTEXT));

        addMenuItem("help", new MenuItem("referenceGuidePDF", "navigation", "http://localhost/products/adito/documentation/Adito_Administrators_Guide.pdf", 300, true, "_blank", SessionInfo.HELP_CONTEXT));
        
        addMenuItem("help", new MenuItem("referenceGuideHTML", "navigation", "http://localhost/products/adito/documentation/html/w2wfrm.htm", 300, true, "_blank", SessionInfo.HELP_CONTEXT));
        
        // Documentation
        addMenuItem("help", new MenuItem("knowledgeBase", "navigation", "http://localhost/kb", 400, true, "_blank",
            SessionInfo.HELP_CONTEXT));

        addMenuItem("help", new MenuItem("support", "navigation", "http://localhost/showAdito.do", 500, true, "_blank", SessionInfo.HELP_CONTEXT));
        
        
        addMenuItem("help", new MenuItem("forums", "navigation", "http://localhost/forums", 600, true, "_blank",
                SessionInfo.HELP_CONTEXT));


        addMenuItem("help", new MenuItem("communityWiki", "navigation", "http://localhost/", 700, true, "_blank",SessionInfo.HELP_CONTEXT));
    }
    
    class ShowChangePasswordMenuItem extends MenuItem {
        ShowChangePasswordMenuItem() {
            super("changePassword", "navigation", "/showChangePassword.do", 300, true, null,
            SessionInfo.USER_CONSOLE_CONTEXT, PolicyConstants.PASSWORD_RESOURCE_TYPE, new Permission[] {
                PolicyConstants.PERM_CHANGE
            }, null);
        }
         
        public boolean isAvailable(int checkNavigationContext, SessionInfo info, HttpServletRequest request) {
            boolean available = super.isAvailable(checkNavigationContext, info, request); 
            if(available) {
                try {
                    available = UserDatabaseManager.getInstance().getUserDatabase(info.getUser().getRealm()).supportsPasswordChange();
                } catch (Exception e) {
                    log.error("Failed to initialise database.", e);
            	}
            }
            return available; 
        }
        
    }
    
    class AccessRightsListMenuItem extends MenuItem {
        AccessRightsListMenuItem() {
            super("accessRightsList", "navigation", "/accessRightsList.do", 400,  true,
                null, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE, new Permission[] {
                                PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_EDIT_AND_ASSIGN,
                                PolicyConstants.PERM_DELETE, PolicyConstants.PERM_ASSIGN
                });
        }
         
        public boolean isAvailable(int checkNavigationContext, SessionInfo info, HttpServletRequest request) {
            boolean available = super.isAvailable(checkNavigationContext, info, request);
            if (available) {
                if (LogonControllerFactory.getInstance().isAdministrator(info.getUser())) {
                    available = true;
                } else {
                    try {
                        PolicyUtil.checkPermissions(PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE, new Permission[] {
                                PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN,
                                PolicyConstants.PERM_EDIT_AND_ASSIGN,
                                PolicyConstants.PERM_DELETE,
                                PolicyConstants.PERM_ASSIGN }, request);
        available = true;
                    } catch (Exception e1) {
                        available = false;
                    }
                }
            }
            return available;
        }
        
    }
}
