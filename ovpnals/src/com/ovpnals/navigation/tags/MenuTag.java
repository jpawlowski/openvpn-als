
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
			
package com.ovpnals.navigation.tags;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.tagext.TagSupport;

import com.ovpnals.core.AvailableMenuItem;
import com.ovpnals.core.CoreMenuTree;
import com.ovpnals.navigation.NavigationBar;
import com.ovpnals.navigation.NavigationManager;
import com.ovpnals.security.Constants;

public class MenuTag extends TagSupport {

    String name;
    String submenu;
    String hide;

    public MenuTag() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public int doStartTag() {
        return (EVAL_BODY_INCLUDE);
    }

    public int doEndTag() {

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        HttpSession session = request.getSession();

        // If the main navigation has not been configured, then configure it
        if (session.getAttribute(Constants.MENU_TREE) == null) {
            session.setAttribute(Constants.MENU_TREE, NavigationManager.getMenuTree(CoreMenuTree.MENU_ITEM_MENU_TREE).rebuildMenus(
                            request));
        }
        pageContext.getRequest().removeAttribute(Constants.SELECTED_MENU);
        List menus = (List) pageContext.getSession().getAttribute(Constants.MENU_TREE);
        if (menus == null) {
        } else {
            AvailableMenuItem it = findAvailableMenuItem(menus);
            if (it != null) {
                pageContext.getRequest().setAttribute(Constants.SELECTED_MENU, it);
            }
        }

        session.setAttribute(Constants.NAV_BAR, NavigationManager.getMenuTree(NavigationBar.NAV_BAR_MENU_TREE)
                        .rebuildMenus(request));

        return (EVAL_PAGE);
    }

    private AvailableMenuItem findAvailableMenuItem(List l) {
        for (Iterator i = l.iterator(); i.hasNext();) {
            AvailableMenuItem item = (AvailableMenuItem) i.next();
            if (item.getMenuItem().getId().equals(name)) {
                return item;
            }
            AvailableMenuItem f = findAvailableMenuItem(item);
            if (f != null) {
                return f;
            }
        }
        return null;
    }

}