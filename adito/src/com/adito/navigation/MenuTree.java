
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;

import com.adito.core.AvailableMenuItem;
import com.adito.core.CoreUtil;
import com.adito.core.MenuItem;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;

public class MenuTree {

    /**
     * 
     */
	protected List menus;
    protected HashMap menuMap;
    protected String name;
    
    public MenuTree(String name) {
        this.name = name;
        menus = new ArrayList();
        menuMap = new HashMap();
    }
    
    /**
     * Get the name of this menu tree
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Add a menu item to the menu tree.
     * 
     * @param parentId parent to add menu item to (or <code>null</code> for top level items)
     * @param menuItem menuItem object
     */
    public void addMenuItem(String parentId, MenuItem menuItem) {
        MenuItem parent = parentId == null ? null : (MenuItem) menuMap.get(parentId);
        if (parent != null) {
            menuItem.setParent(parent);
            parent.addChild(menuItem);
        } else {
            menus.add(menuItem);
        }
        menuMap.put(menuItem.getId(), menuItem);
    }


    /**
     * Remove a menu item from the menu tree
     * 
     * @param parentId parent to remove menu item from (or <code>null</code> for top level items)
     * @param itemId menu item ID
     */
    public void removeMenuItem(String parentId, String itemId) {
        MenuItem parent = parentId == null ? null : (MenuItem) menuMap.get(parentId);
        MenuItem item = getMenuItem(itemId);
        if (parent != null) {
            parent.removeChild(parent.getChild(itemId));
        }
        else {
            if(item != null) {
                menus.remove(item);
            }
        }
        menuMap.remove(itemId);
    }


    /**
     * @param name
     * @return
     */
    public MenuItem getMenuItem(String id) {
        return (MenuItem) menuMap.get(id);
    }


    /**
     * Rebuild the navigation menu structure, checking each menu item to see if
     * is available for the current state.
     * 
     * @param menuItem root menu item
     * @param checkNavigationContext navigation context
     * @param info info
     * @param request request
     * @param referer referer
     * @return available root menus
     */
    public List rebuildMenus(MenuItem menuItem, int checkNavigationContext, SessionInfo info, HttpServletRequest request, String referer) {
        List availableMenus = new ArrayList();
        for (Iterator i = menuItem == null ? menus.iterator() : menuItem.availableChildren(checkNavigationContext, info, request).iterator(); i.hasNext();) {
            MenuItem it = (MenuItem) i.next();
            if (it.isAvailable(checkNavigationContext, info, request)) {
                AvailableMenuItem nm = createAvailableMenuItem(it, null, request, referer, checkNavigationContext, info);
                if (it.isLeaf() || (!it.isLeaf() && !nm.isEmpty())) {
                    availableMenus.add(nm);
                }
            }
        }
        Collections.sort(availableMenus);
        return availableMenus;
    }
    
    /**
     * Called when rebuilding the menus, create an {@link AvailableMenuItem} for
     * the provided {@link MenuItem}.
     *  
     * @param item
     * @param parent
     * @param request
     * @param referer
     * @param checkNavigationContext
     * @param info
     * @return available menu item
     */
    public AvailableMenuItem createAvailableMenuItem(MenuItem item, AvailableMenuItem parent, HttpServletRequest request, String referer, int checkNavigationContext, SessionInfo info) {
    	return new AvailableMenuItem(item, parent, request, referer, checkNavigationContext, info);    
    }

    /**
     * Rebuild the navigation menu structure, checking each menu item to see if
     * is available for the current state.
     *
     * @param checkNavigationContext navigation context
     * @param info info
     * @param request request
     * @param referer referer
     * @return available menus
     */
    public List rebuildMenus(int checkNavigationContext, SessionInfo info, HttpServletRequest request, String referer) {
        return rebuildMenus(null, checkNavigationContext, info, request, referer);
    }

    /**
     * Rebuild the navigation menu structure, checking each menu item to see if
     * is available for the current state.
     * 
     * @param request request
     * @return available root menus
     */
    public List rebuildMenus(HttpServletRequest request) {
        return rebuildMenus(null, request);
        
    }

    /**
     * Rebuild the navigation menu structure, checking each menu item to see if
     * is available for the current state.
     * 
     * @param menuItem root menu item
     * @param request request
     * @return available root menus
     */
    public List rebuildMenus(MenuItem menuItem, HttpServletRequest request) {
        SessionInfo info = LogonControllerFactory.getInstance().getSessionInfo(request);
            return rebuildMenus(menuItem, info == null ?0 : 
                info.getNavigationContext(), info, request,
                                CoreUtil.getReferer(request));
    }

    public ActionForward getFirstAvailableActionForward(List availableMenuItems) {
        for(Iterator i = availableMenuItems.iterator(); i.hasNext(); ) {
            AvailableMenuItem it = getFirstAvailableMenuLeaf((AvailableMenuItem)i.next());
            if(it != null) {
                return new ActionForward(it.getPath(), true);
            }            
        }
        return null;
    }

    AvailableMenuItem getFirstAvailableMenuLeaf(AvailableMenuItem item) {
        if(item.getMenuItem().isLeaf()) {
            return item;
        }
        else for(Iterator i = item.iterator(); i.hasNext(); ) {
            AvailableMenuItem it = getFirstAvailableMenuLeaf((AvailableMenuItem)i.next());
            if(it != null) {
                return it;
            }            
        }
        return null;
    }
}