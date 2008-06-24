
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
			
package com.adito.navigation;

import static com.adito.navigation.NavigationBar.log;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.agent.DefaultAgentManager;
import com.adito.core.CoreUtil;
import com.adito.core.MenuItem;
import com.adito.policyframework.AccessRight;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.PolicyUtil;
import com.adito.policyframework.ResourceType;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;

/**
 */
public class NavigationBar extends MenuTree {

	public static final String NAV_BAR_MENU_TREE = "navBar";

	final static Log log = LogFactory.getLog(NavigationBar.class);

    /**
     */
	public NavigationBar() {
		super(NAV_BAR_MENU_TREE);
		addMenuItem(null, new LaunchAgentMenuItem());
		addMenuItem(null, new ShutdownAgentMenuItem());
		addMenuItem(null, new MenuItem("home",
						"navigation",
						"/showHome.do",
						200,
						true,
						"_self",
						SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.MANAGEMENT_CONSOLE_CONTEXT) {

			public boolean isAvailable(int checkNavigationContext, SessionInfo info, HttpServletRequest request) {
				boolean available = super.isAvailable(checkNavigationContext, info, request);
				if (available) {
					try {
						available = PolicyUtil.canLogin(info.getUser());
					} catch (Exception e) {
						log.error("Failed to determine delegation rights.", e);
						available = false;
					}
				}
				return available;
			}

		});
		addMenuItem(null, new MenuItem("togglePanelOptions",
						"navigation",
						"javascript: frameToggle('component_panelOptions');",
						0,
						true,
						"_self",
						0));

		addMenuItem(null, new MenuItem("managementConsole",
						"navigation",
						"/managementConsole.do",
						300,
						true,
						"_self",
						SessionInfo.USER_CONSOLE_CONTEXT) {

			public boolean isAvailable(int checkNavigationContext, SessionInfo info, HttpServletRequest request) {
				boolean available = super.isAvailable(checkNavigationContext, info, request);
                if (available) {
                    if (LogonControllerFactory.getInstance().isAdministrator(info.getUser())){
                        return true;
                    }
                    try {
                        available = PolicyDatabaseFactory.getInstance().isAnyAccessRightAllowed(info.getUser(), true, true, false) && CoreUtil.isMenuAvailable(request);
                        if (available) {
                            List<AccessRight> listAccessRight = PolicyDatabaseFactory.getInstance().getAnyAccessRightAllowed(info.getUser(), true ,true, false);
                            for (Iterator iter = listAccessRight.iterator(); iter.hasNext();) {
                                    AccessRight accessRight = (AccessRight) iter.next();
                                    if (!PolicyConstants.PERM_PERSONAL_CREATE_EDIT_AND_DELETE.equals(accessRight.getPermission())) {
                                        return true;
                                    }
                            }
                            return false;
                        }
                    } catch (Exception e) {
                        log.error("Failed to determine delegation rights.", e);
                        available = false;
                    }
                }
				return available;
			}

		});
		addMenuItem(null, new MenuItem("userConsole",
						"navigation",
						"/userConsole.do",
						300,
						true,
						"_self",
						SessionInfo.MANAGEMENT_CONSOLE_CONTEXT) {

			public boolean isAvailable(int checkNavigationContext, SessionInfo info, HttpServletRequest request) {
				boolean available = super.isAvailable(checkNavigationContext, info, request);
				if (available) {
					try {
						available = PolicyDatabaseFactory.getInstance().isAnyAccessRightAllowed(info.getUser(), true, true, false) && CoreUtil.isMenuAvailable(request);
					} catch (Exception e) {
						log.error("Failed to determine delegation rights.", e);
						available = false;
					}
				}
				return available;
			}

		});
		addMenuItem(null,
			new MenuItem("help",
							"navigation",
							"javascript: this.blur(); windowRef = window.open('/help.do?source=help','help_win','left=20,top=20,width=480,height=640,toolbar=0,resizable=1,menubar=0,scrollbars=1'); windowRef.focus()",
							400,
							true,
							"_self",
							SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT
								| SessionInfo.SETUP_CONSOLE_CONTEXT));
		addMenuItem(null, new MenuItem("logoff",
						"navigation",
						"/logoff.do",
						500,
						true,
						"_self",
						SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.MANAGEMENT_CONSOLE_CONTEXT));
	}

	class LaunchAgentMenuItem extends MenuItem {
		LaunchAgentMenuItem() {
			super("launchAgent",
							"navigation",
							"/launchAgent.do",
							100,
							true,
							null,
							SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.MANAGEMENT_CONSOLE_CONTEXT,
							PolicyConstants.AGENT_RESOURCE_TYPE,
							new Permission[] { PolicyConstants.PERM_USE },
							(ResourceType) null);
		}

		public boolean isAvailable(int checkNavigationContext, SessionInfo info, HttpServletRequest request) {
			boolean available = super.isAvailable(checkNavigationContext, info, request);
			if (available) {
				available = !CoreUtil.isInWizard(request.getSession()) && !DefaultAgentManager.getInstance()
								.hasActiveAgent(request)
					&& CoreUtil.isMenuAvailable(request);
			}
			return available;
		}
	}

	class ShutdownAgentMenuItem extends MenuItem {
		ShutdownAgentMenuItem() {
			super("shutdownAgent",
							"navigation",
							"/shutdownAgent.do",
							100,
							true,
							null,
							SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.MANAGEMENT_CONSOLE_CONTEXT,
							PolicyConstants.AGENT_RESOURCE_TYPE,
							new Permission[] { PolicyConstants.PERM_USE },
							null);
		}

		public boolean isAvailable(int checkNavigationContext, SessionInfo info, HttpServletRequest request) {
			boolean available = super.isAvailable(checkNavigationContext, info, request);
			if (available) {
				available = !CoreUtil.isInWizard(request.getSession()) && DefaultAgentManager.getInstance().hasActiveAgent(request)
					&& CoreUtil.isMenuAvailable(request);
			}
			return available;
		}
	}
}
