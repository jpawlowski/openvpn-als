
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
			
package com.adito.tunnels;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.agent.DefaultAgentManager;
import com.adito.core.CoreMenuTree;
import com.adito.core.CoreServlet;
import com.adito.core.CoreUtil;
import com.adito.core.MenuItem;
import com.adito.core.PageTaskMenuTree;
import com.adito.extensions.ExtensionException;
import com.adito.extensions.types.DefaultPlugin;
import com.adito.navigation.MenuTree;
import com.adito.navigation.NavigationManager;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.PolicyDatabase;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.PolicyUtil;
import com.adito.policyframework.ResourceType;
import com.adito.policyframework.itemactions.AddToFavoritesAction;
import com.adito.policyframework.itemactions.CloneResourceAction;
import com.adito.policyframework.itemactions.EditResourceAction;
import com.adito.policyframework.itemactions.RemoveFromFavoritesAction;
import com.adito.policyframework.itemactions.RemoveResourceAction;
import com.adito.security.SessionInfo;
import com.adito.table.TableItemActionMenuTree;
import com.adito.tunnels.itemactions.SwitchOffAction;
import com.adito.tunnels.itemactions.SwitchOnAction;

/**
 * Plugin implementation thats the <i>Network Places</i> feature.
 */
public class TunnelPlugin extends DefaultPlugin {

    /**
     * Tunnel resource type ID
     */
    public final static int SSL_TUNNEL_RESOURCE_TYPE_ID = 4;

    /**
     * Tunnel resource type
     */
    public static final ResourceType SSL_TUNNEL_RESOURCE_TYPE = new TunnelResourceType();

    /**
     * Extension bundle ID
     */
    public static final String BUNDLE_ID = "adito-community-tunnels";

    final static Log log = LogFactory.getLog(TunnelPlugin.class);

    /**
     * Message resources key (resource bundle id)
     */
    public static final String MESSAGE_RESOURCES_KEY = "tunnels";

    /**
     * Constructor.
     */
    public TunnelPlugin() {
        super("/WEB-INF/adito-community-tunnels-tiles-defs.xml", true);
    }

    /* (non-Javadoc)
     * @see com.adito.extensions.types.DefaultPlugin#activatePlugin()
     */
    public void activatePlugin() throws ExtensionException {
        super.activatePlugin();
        try {
            initDatabase();
            initPolicyFramework();
            initTableItemActions();
            initMainMenu();
            initPageTasks();
            initService();
            CoreUtil.updateEventsTable(TunnelPlugin.MESSAGE_RESOURCES_KEY, TunnelsEventConstants.class);
        } catch (Exception e) {
            throw new ExtensionException(ExtensionException.INTERNAL_ERROR, e, e.getLocalizedMessage());
        }
    }

	void initDatabase() throws Exception {
        TunnelDatabaseFactory.getInstance().open(CoreServlet.getServlet(), this.getPluginDefinition());
    }

    void initPolicyFramework() throws Exception {
        PolicyDatabase pdb = PolicyDatabaseFactory.getInstance();
        // SSL Tunnel
        pdb.registerResourceType(SSL_TUNNEL_RESOURCE_TYPE);
        SSL_TUNNEL_RESOURCE_TYPE.addPermission(PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN);
        SSL_TUNNEL_RESOURCE_TYPE.addPermission(PolicyConstants.PERM_EDIT_AND_ASSIGN);
        SSL_TUNNEL_RESOURCE_TYPE.addPermission(PolicyConstants.PERM_DELETE);
        SSL_TUNNEL_RESOURCE_TYPE.addPermission(PolicyConstants.PERM_ASSIGN);
        SSL_TUNNEL_RESOURCE_TYPE.addPermission(PolicyConstants.PERM_PERSONAL_CREATE_EDIT_AND_DELETE);
    }

    void initTableItemActions() throws Exception {
        MenuTree tree = NavigationManager.getMenuTree(TableItemActionMenuTree.MENU_TABLE_ITEM_ACTION_MENU_TREE);
        // Tunnels
        tree.addMenuItem(null, new MenuItem("tunnel", MESSAGE_RESOURCES_KEY, null, 100, false, SessionInfo.ALL_CONTEXTS));
        tree.addMenuItem("tunnel", new AddToFavoritesAction(MESSAGE_RESOURCES_KEY));
        tree.addMenuItem("tunnel", new RemoveFromFavoritesAction(MESSAGE_RESOURCES_KEY));
        tree.addMenuItem("tunnel", new RemoveResourceAction(SessionInfo.ALL_CONTEXTS, MESSAGE_RESOURCES_KEY));
        tree.addMenuItem("tunnel", new EditResourceAction(SessionInfo.ALL_CONTEXTS, MESSAGE_RESOURCES_KEY));
        //tree.addMenuItem("tunnel", new SwitchOnAction());
		tree.addMenuItem("tunnel", new CloneResourceAction(SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, MESSAGE_RESOURCES_KEY));
        tree.addMenuItem("tunnel", new SwitchOffAction());
    }

    void initMainMenu() throws Exception {
        MenuTree tree = NavigationManager.getMenuTree(CoreMenuTree.MENU_ITEM_MENU_TREE);

        tree.addMenuItem("resources", new MenuItem("userTunnels",
                        MESSAGE_RESOURCES_KEY,
                        "/showUserTunnels.do",
                        500,
                        true,
                        null,
                        SessionInfo.USER_CONSOLE_CONTEXT,
                        SSL_TUNNEL_RESOURCE_TYPE,
                        new Permission[] { PolicyConstants.PERM_PERSONAL_CREATE_EDIT_AND_DELETE },
                        SSL_TUNNEL_RESOURCE_TYPE));

        tree.addMenuItem("globalResources", new MenuItem("tunnels",
                        MESSAGE_RESOURCES_KEY,
                        "/showTunnels.do",
                        400,
                        true,
                        null,
                        SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, SSL_TUNNEL_RESOURCE_TYPE,
                        new Permission[] { PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN,
                            PolicyConstants.PERM_EDIT_AND_ASSIGN,
                            PolicyConstants.PERM_DELETE,
                            PolicyConstants.PERM_ASSIGN }) {
            public boolean isAvailable(int checkNavigationContext, SessionInfo info, HttpServletRequest request) {
                boolean available = super.isAvailable(checkNavigationContext, info, request);
                if (available) {
                    try {
                        PolicyUtil.checkPermissions(SSL_TUNNEL_RESOURCE_TYPE, new Permission[] {
                                        PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_EDIT_AND_ASSIGN,
                                        PolicyConstants.PERM_DELETE, PolicyConstants.PERM_ASSIGN }, request);
                        available = true;
                    } catch (Exception e1) {
                        available = false;
                    }
                }
                return available;
            }
        });
    }

    void initPageTasks() throws Exception {
        MenuTree tree = NavigationManager.getMenuTree(PageTaskMenuTree.PAGE_TASK_MENU_TREE);

        // Tunnels showTunnels
        tree.addMenuItem(null, new MenuItem("showTunnels", null, null, 100, false, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT));
        tree.addMenuItem("showTunnels", new MenuItem("createTunnel",
                        MESSAGE_RESOURCES_KEY,
                        "/defaultTunnelDetails.do",
                        100,
                        true,
                        "_self",
                        SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, SSL_TUNNEL_RESOURCE_TYPE,
                        new Permission[] { PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN }));
        
        // Networking userNetworkPlaces
        tree.addMenuItem(null, new MenuItem("showUserTunnels", null, null, 100, false, SessionInfo.USER_CONSOLE_CONTEXT));
        tree.addMenuItem("showUserTunnels", new MenuItem("createPersonalTunnel",
                        MESSAGE_RESOURCES_KEY,
                        "/defaultTunnelDetails.do",
                        100,
                        true,
                        "_self",
                        SessionInfo.USER_CONSOLE_CONTEXT,
                        SSL_TUNNEL_RESOURCE_TYPE,
                        new Permission[] { PolicyConstants.PERM_PERSONAL_CREATE_EDIT_AND_DELETE }));

    }

    void initService() throws Exception{
        DefaultAgentManager.getInstance().registerService(TunnelingService.class);
    }
    
    public void stopPlugin() throws ExtensionException {
        super.stopPlugin();
        try {
            stopDatabase();
            stopPolicyFramework();
            removeTableItemActions();
            removeMainMenu();
            removePageTasks();
            stopService();
        } catch (Exception e) {
            throw new ExtensionException(ExtensionException.INTERNAL_ERROR, e);
        }
    }

    private void stopService() {
        DefaultAgentManager.getInstance().unregisterService(TunnelingService.class);
    }

    private void removePageTasks() {
        MenuTree tree = NavigationManager.getMenuTree(PageTaskMenuTree.PAGE_TASK_MENU_TREE);
        tree.removeMenuItem("showTunnels", "createTunnel");
    }

    private void removeMainMenu() {
        MenuTree tree = NavigationManager.getMenuTree(CoreMenuTree.MENU_ITEM_MENU_TREE);
        tree.removeMenuItem("resources", "userTunnels");
        tree.removeMenuItem("globalResources", "tunnels");
    }

    private void removeTableItemActions() {
        MenuTree tree = NavigationManager.getMenuTree(TableItemActionMenuTree.MENU_TABLE_ITEM_ACTION_MENU_TREE);
        tree.removeMenuItem("tunnel", AddToFavoritesAction.TABLE_ITEM_ACTION_ID);
        tree.removeMenuItem("tunnel", RemoveFromFavoritesAction.TABLE_ITEM_ACTION_ID);
        tree.removeMenuItem("tunnel", RemoveResourceAction.TABLE_ITEM_ACTION_ID);
        tree.removeMenuItem("tunnel", EditResourceAction.TABLE_ITEM_ACTION_ID);
        tree.removeMenuItem("tunnel", SwitchOnAction.TABLE_ITEM_ACTION_ID);
        tree.removeMenuItem("tunnel", SwitchOffAction.TABLE_ITEM_ACTION_ID);
    }

    private void stopPolicyFramework() throws Exception {
        PolicyDatabase pdb = PolicyDatabaseFactory.getInstance();
        pdb.deregisterResourceType(SSL_TUNNEL_RESOURCE_TYPE);
    }

    private void stopDatabase() throws Exception {
        TunnelDatabaseFactory.getInstance().close();
    }

}
