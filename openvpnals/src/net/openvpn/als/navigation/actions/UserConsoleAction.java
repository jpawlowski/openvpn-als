
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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.core.CoreMenuTree;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.core.actions.AuthenticatedAction;
import net.openvpn.als.navigation.MenuTree;
import net.openvpn.als.navigation.NavigationManager;
import net.openvpn.als.security.SessionInfo;

/**
 * Action to switch to the user console. All users are allowed to do this.
 */

public class UserConsoleAction extends AuthenticatedAction {
    
    /**
     * Constructor
     */
    public UserConsoleAction() {
        super();
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        SessionInfo info = this.getSessionInfo(request);
        info.setNavigationContext(SessionInfo.USER_CONSOLE_CONTEXT);
        CoreUtil.resetMainNavigation(request.getSession());
        MenuTree menuTree = NavigationManager.getMenuTree(CoreMenuTree.MENU_ITEM_MENU_TREE);
        ActionForward fwd = menuTree.getFirstAvailableActionForward(menuTree.rebuildMenus(request));
        if (fwd == null) {
            throw new Exception("Use does not have any permission for using the current navigation context.");
        }
        return fwd;

    }

    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.USER_CONSOLE_CONTEXT;
    }

}