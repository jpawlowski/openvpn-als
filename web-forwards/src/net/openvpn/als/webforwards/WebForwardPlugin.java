
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
			
package net.openvpn.als.webforwards;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.agent.DefaultAgentManager;
import net.openvpn.als.boot.ContextHolder;
import net.openvpn.als.core.CoreMenuTree;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.core.MenuItem;
import net.openvpn.als.core.PageTaskMenuTree;
import net.openvpn.als.extensions.ExtensionException;
import net.openvpn.als.extensions.types.DefaultPlugin;
import net.openvpn.als.navigation.MenuTree;
import net.openvpn.als.navigation.NavigationManager;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.policyframework.PolicyDatabase;
import net.openvpn.als.policyframework.PolicyDatabaseFactory;
import net.openvpn.als.policyframework.PolicyUtil;
import net.openvpn.als.policyframework.ResourceType;
import net.openvpn.als.policyframework.itemactions.AddToFavoritesAction;
import net.openvpn.als.policyframework.itemactions.CloneResourceAction;
import net.openvpn.als.policyframework.itemactions.EditResourceAction;
import net.openvpn.als.policyframework.itemactions.RemoveFromFavoritesAction;
import net.openvpn.als.policyframework.itemactions.RemoveResourceAction;
import net.openvpn.als.replacementproxy.ReplacementProxyMethodHandler;
import net.openvpn.als.reverseproxy.ReverseProxyMethodHandler;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.table.TableItemActionMenuTree;

/**
 * Plugin implementation thats the <i>Network Places</i> feature.
 */
public class WebForwardPlugin extends DefaultPlugin {

	/**
	 * Web Forward resource type ID
	 */
	public final static int WEBFORWARD_RESOURCE_TYPE_ID = 0;

	/**
	 * Web Forward resource type
	 */
	public final static ResourceType WEBFORWARD_RESOURCE_TYPE = new WebForwardResourceType();

	/**
	 * Extension bundle ID
	 */
	public static final String BUNDLE_ID = "web-forwards";

	final static Log log = LogFactory.getLog(WebForwardPlugin.class);

	/**
	 * Message resources key (resource bundle id)
	 */
	public static final String MESSAGE_RESOURCES_KEY = "webForwards";

	 /**
     * Handler
     */
    public static final ReverseProxyMethodHandler REVERSE_PROXY_HANDLER = new ReverseProxyMethodHandler();

    public static final ReplacementProxyMethodHandler REPLACEMENT_PROXY_HANDLER =new ReplacementProxyMethodHandler();
	/**
	 * Constructor.
	 */
	public WebForwardPlugin() {
		super("/WEB-INF/web-forwards-tiles-defs.xml", true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.plugin.DefaultPlugin#startPlugin()
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
			initTagLib();
			CoreUtil.updateEventsTable(WebForwardPlugin.MESSAGE_RESOURCES_KEY, WebForwardEventConstants.class);
		} catch (Exception e) {
			throw new ExtensionException(ExtensionException.INTERNAL_ERROR, e);
		}
	}

	void initDatabase() throws Exception {
		WebForwardDatabaseFactory.getInstance().open(CoreServlet.getServlet(), this.getPluginDefinition());
	}

	void initPolicyFramework() throws Exception {
		PolicyDatabase pdb = PolicyDatabaseFactory.getInstance();
		// Web Forward
		pdb.registerResourceType(WEBFORWARD_RESOURCE_TYPE);
		WEBFORWARD_RESOURCE_TYPE.addPermission(PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN);
		WEBFORWARD_RESOURCE_TYPE.addPermission(PolicyConstants.PERM_EDIT_AND_ASSIGN);
		WEBFORWARD_RESOURCE_TYPE.addPermission(PolicyConstants.PERM_DELETE);
		WEBFORWARD_RESOURCE_TYPE.addPermission(PolicyConstants.PERM_ASSIGN);
        WEBFORWARD_RESOURCE_TYPE.addPermission(PolicyConstants.PERM_PERSONAL_CREATE_EDIT_AND_DELETE);
	}

	void initTableItemActions() throws Exception {
		MenuTree tree = NavigationManager.getMenuTree(TableItemActionMenuTree.MENU_TABLE_ITEM_ACTION_MENU_TREE);
		// Web Forwards
        tree.addMenuItem(null, new MenuItem("webForward", WebForwardPlugin.MESSAGE_RESOURCES_KEY, null, 100, false, SessionInfo.ALL_CONTEXTS));
        tree.addMenuItem("webForward", new AddToFavoritesAction(WebForwardPlugin.MESSAGE_RESOURCES_KEY));
        tree.addMenuItem("webForward", new RemoveFromFavoritesAction(WebForwardPlugin.MESSAGE_RESOURCES_KEY));
        tree.addMenuItem("webForward", new RemoveResourceAction(SessionInfo.ALL_CONTEXTS, WebForwardPlugin.MESSAGE_RESOURCES_KEY));
        tree.addMenuItem("webForward", new EditResourceAction(SessionInfo.ALL_CONTEXTS, WebForwardPlugin.MESSAGE_RESOURCES_KEY));
        tree.addMenuItem("webForward", new CloneResourceAction(SessionInfo.MANAGEMENT_CONSOLE_CONTEXT, WebForwardPlugin.MESSAGE_RESOURCES_KEY));
	}

	void initMainMenu() throws Exception {
		MenuTree tree = NavigationManager.getMenuTree(CoreMenuTree.MENU_ITEM_MENU_TREE);

		tree.addMenuItem("resources", new MenuItem("userWebForwards",
						"webForwards",
						"/showUserWebForwards.do",
						50,
						true,
						null,
						SessionInfo.USER_CONSOLE_CONTEXT,
                        WEBFORWARD_RESOURCE_TYPE,
                        new Permission[] { PolicyConstants.PERM_PERSONAL_CREATE_EDIT_AND_DELETE },
                        WEBFORWARD_RESOURCE_TYPE));

		tree.addMenuItem("globalResources", new MenuItem("managementWebForwards",
						"webForwards",
						"/showWebForwards.do",
						100,
						true,
						null,
						SessionInfo.MANAGEMENT_CONSOLE_CONTEXT,
						WEBFORWARD_RESOURCE_TYPE,
						new Permission[] { PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN,
							PolicyConstants.PERM_EDIT_AND_ASSIGN,
							PolicyConstants.PERM_DELETE,
							PolicyConstants.PERM_ASSIGN }) {
			public boolean isAvailable(int checkNavigationContext, SessionInfo info, HttpServletRequest request) {
				boolean available = super.isAvailable(checkNavigationContext, info, request);
				if (available) {
					try {
						PolicyUtil.checkPermissions(WEBFORWARD_RESOURCE_TYPE,
							new Permission[] { PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN,
								PolicyConstants.PERM_EDIT_AND_ASSIGN,
								PolicyConstants.PERM_DELETE,
								PolicyConstants.PERM_ASSIGN },
							request);
						available = true;
					} catch (Exception e1) {
						available = false;
					}
				}
				return available;
			}
		});
	}
	
	void initTagLib() {
		ContextHolder.getContext().setResourceAlias("/server/taglibs/webforwards", "/WEB-INF/webforwards.tld");
	}

	void initPageTasks() throws Exception {
		MenuTree tree = NavigationManager.getMenuTree(PageTaskMenuTree.PAGE_TASK_MENU_TREE);
		tree.addMenuItem(null, new MenuItem("showWebForwards", null, null, 100, false, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT));
        tree.addMenuItem("showWebForwards", new MenuItem("createWebForward",
                        WebForwardPlugin.MESSAGE_RESOURCES_KEY,
                        "/webForwardTypeSelection.do",
                        100,
                        true,
                        "_self",
                        SessionInfo.MANAGEMENT_CONSOLE_CONTEXT,
                        WEBFORWARD_RESOURCE_TYPE,
                        new Permission[] { PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN },
                        null)); 
        tree.addMenuItem("showWebForwards", new MenuItem("replacements",
                        WebForwardPlugin.MESSAGE_RESOURCES_KEY,
                        "/showReplacements.do",
                        600,
                        true,
                        null,
                        SessionInfo.MANAGEMENT_CONSOLE_CONTEXT,
                        PolicyConstants.REPLACEMENTS_RESOURCE_TYPE,
                        new Permission[] { PolicyConstants.PERM_CHANGE },
                        null));
        
        // userWebForwards
        tree.addMenuItem(null, new MenuItem("showUserWebForwards", null, null, 100, false, SessionInfo.USER_CONSOLE_CONTEXT));
        tree.addMenuItem("showUserWebForwards", new MenuItem("createPersonalWebForward",
                        WebForwardPlugin.MESSAGE_RESOURCES_KEY,
                        "/webForwardTypeSelection.do",
                        100,
                        true,
                        "_self",
                        SessionInfo.USER_CONSOLE_CONTEXT,
                        WEBFORWARD_RESOURCE_TYPE,
                        new Permission[] { PolicyConstants.PERM_PERSONAL_CREATE_EDIT_AND_DELETE }));

    }

	void initService() throws InstantiationException, IllegalAccessException {
		// Register CONNECT handler
		if (!ContextHolder.getContext().isSetupMode()) {
			ContextHolder.getContext().registerRequestHandler(REVERSE_PROXY_HANDLER);
			ContextHolder.getContext().registerRequestHandler(REPLACEMENT_PROXY_HANDLER);
		}
		DefaultAgentManager.getInstance().registerService(WebForwardService.class);
	}

	@Override
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
			throw new ExtensionException(ExtensionException.INTERNAL_ERROR, e, "Failed to start.");
		}
	}

	private void stopService() {
		// Register CONNECT handler
		if (!ContextHolder.getContext().isSetupMode()) {
			ContextHolder.getContext().deregisterRequestHandler(REVERSE_PROXY_HANDLER);
			ContextHolder.getContext().deregisterRequestHandler(REPLACEMENT_PROXY_HANDLER);
		}
	}

	private void removePageTasks() {
		MenuTree tree = NavigationManager.getMenuTree(PageTaskMenuTree.PAGE_TASK_MENU_TREE);
		tree.removeMenuItem("showWebForwards", "createTunneledWebForward");
		tree.removeMenuItem("showWebForwards", "createReplacementWebForward");
		tree.removeMenuItem("showWebForwards", "createReverseProxyWebForward");
	}

	private void removeMainMenu() {
		MenuTree tree = NavigationManager.getMenuTree(CoreMenuTree.MENU_ITEM_MENU_TREE);
		tree.removeMenuItem("resources", "userWebForwards");
		tree.removeMenuItem("globalResources", "managementWebForwards");
		tree.removeMenuItem("configuration", "replacements");
	}

	private void removeTableItemActions() {
		MenuTree tree = NavigationManager.getMenuTree(TableItemActionMenuTree.MENU_TABLE_ITEM_ACTION_MENU_TREE);
		tree.removeMenuItem("webForward", AddToFavoritesAction.TABLE_ITEM_ACTION_ID);
		tree.removeMenuItem("webForward", RemoveFromFavoritesAction.TABLE_ITEM_ACTION_ID);
		tree.removeMenuItem("webForward", RemoveResourceAction.TABLE_ITEM_ACTION_ID);
		tree.removeMenuItem("webForward", EditResourceAction.TABLE_ITEM_ACTION_ID);
	}

	private void stopPolicyFramework() throws Exception {
		PolicyDatabase pdb = PolicyDatabaseFactory.getInstance();
		pdb.deregisterResourceType(WEBFORWARD_RESOURCE_TYPE);
	}

	private void stopDatabase() throws Exception {
		WebForwardDatabaseFactory.getInstance().close();
	}
}