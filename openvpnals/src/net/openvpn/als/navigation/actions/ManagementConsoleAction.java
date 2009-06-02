
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.navigation.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import net.openvpn.als.core.CoreMenuTree;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.core.RedirectWithMessages;
import net.openvpn.als.core.actions.AuthenticatedAction;
import net.openvpn.als.navigation.MenuTree;
import net.openvpn.als.navigation.NavigationManager;
import net.openvpn.als.policyframework.PolicyDatabaseFactory;
import net.openvpn.als.security.SessionInfo;

/**
 * Action to launch switch to the management console (if allowed).
 */

public class ManagementConsoleAction extends AuthenticatedAction {
    
    /**
     * Constructor
     */
    public ManagementConsoleAction() {
        super();
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        SessionInfo info = this.getSessionInfo(request);
        if(!PolicyDatabaseFactory.getInstance().isAnyAccessRightAllowed(info.getUser(), true, true, false)) {
            throw new Exception("You do not have permission to use the management console.");
        }
        info.setNavigationContext(SessionInfo.MANAGEMENT_CONSOLE_CONTEXT);
        CoreUtil.resetMainNavigation(request.getSession());
        MenuTree menuTree = NavigationManager.getMenuTree(CoreMenuTree.MENU_ITEM_MENU_TREE);
        ActionForward fwd = menuTree.getFirstAvailableActionForward(menuTree.rebuildMenus(request));
        if (fwd == null) {
            // Not capabilities left on management console so switching to user console
            info.setNavigationContext(SessionInfo.USER_CONSOLE_CONTEXT);
            return mapping.findForward("home");
        }
        return fwd;

    }

    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

}