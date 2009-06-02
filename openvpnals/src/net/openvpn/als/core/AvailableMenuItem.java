
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
			
package net.openvpn.als.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import net.openvpn.als.boot.Util;
import net.openvpn.als.security.SessionInfo;


/**
 * Wraps {@link net.openvpn.als.core.MenuItem} providing a tree of 
 * menu items (which contains all possible menu items) 
 * that are available for the current user, configuration and navigation
 * context.
 * <p>
 * Each users session contains a tree of these objects that are used
 * to build up the various navigation components such as side menu bar,
 * top navigation page, page tasks etc. 
 */
public class AvailableMenuItem extends ArrayList implements Comparable {
    
    private static final long serialVersionUID = -5447780375295861982L;
    
    // Private instance variables
    
    private MenuItem menuItem;
    private AvailableMenuItem parent;
    private String path;
    private HttpServletRequest request;
    private SessionInfo sessionInfo;
    
    /**
     * Constructor.
     *
     * @param menuItem menu item this object wraps
     * @param parent parent or <code>null</code> if root
     * @param request request
     * @param referer referer
     * @param checkNavigationContext current navigation context to check against
     * @param sessionInfo sesion
     */
    public AvailableMenuItem(MenuItem menuItem, AvailableMenuItem parent, HttpServletRequest request, String referer, int checkNavigationContext, SessionInfo sessionInfo) {
    	this.request = request;
    	this.sessionInfo = sessionInfo;
        for(Iterator i = menuItem.availableChildren(checkNavigationContext, sessionInfo, request).iterator(); i.hasNext(); ) {
            MenuItem it = (MenuItem)i.next();
            if(it.isLeaf() || ( !it.isLeaf() && !it.isEmpty())) {
                add(new AvailableMenuItem(it, this, request, referer, checkNavigationContext, sessionInfo));
            }
        }
        Collections.sort(this);
        this.menuItem = menuItem;
        this.parent = parent;       

        path = menuItem.getPath();
        if(path != null) {
            StringBuffer buf = new StringBuffer();
            buf.append(path);
            if(referer != null  && !path.startsWith("javascript:") && menuItem.hasReferrer()) {
                // The referer may already be in the path, strip it out
                while(true) {
                    int idx = path.indexOf("referer="); 
                    if(idx != -1) {
                        int end = path.indexOf('&', idx);
                        path = path.substring(0, idx) + ( end == -1 ? "" : path.substring(end) );
                    }
                    else {
                        break;
                    }
                }
                if(path.indexOf("?") != -1) {
                    buf.append("&");
                }
                else {
                    buf.append("?");
                }
                buf.append("referer=");
                buf.append(Util.urlEncode(referer));
            }
            path = buf.toString();
        }
    }
    
    /**
     * Get if this menu it empty.
     * 
     * @return empty
     */
    public boolean getEmpty() {
        return size() == 0;
    }
    
    /**
     * Get the parent available menu item
     * 
     * @return parent available menu item
     */
    public AvailableMenuItem getParent() {
        return parent;
    }
    
    /**
     * Get the {@link MenuItem} this object wraps.
     * 
     * @return wrapped menu item
     */
    public MenuItem getMenuItem() {
        return menuItem;
    }
    
    /**
     * Get the first available child that is available from the node
     * in the tree.
     * 
     * @return first available menu item
     */
    public AvailableMenuItem getFirstAvailableChild() {
        if(!menuItem.isLeaf() && !menuItem.isEmpty()) {
            return null;
        }
        return (AvailableMenuItem)get(0);
    }
    
    /**
     * Get the path activating the menu item should direct the browser to.
     * 
     * @return path to direct browser to
     */
    public String getPath() {
        return path;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return menuItem == null ? "<no menu item>" : ( menuItem.getId() + " ["  + menuItem.getPath() + "] empty = " + menuItem.isEmpty());
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object arg0) {
        return getMenuItem().compareTo(((AvailableMenuItem)arg0).getMenuItem());
    }
    
    /**
     * Get the request this available menu item was created with
     * 
     * @return request
     */
    public HttpServletRequest getRequest() {
    	return request;
    }
    
    /**
     * Get the session this available menu item was created with
     * 
     * @return session
     */
    public SessionInfo getSessionInfo() {
    	return sessionInfo;
    } 
}