
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
			
package com.adito.networkplaces;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.adito.boot.Util;
import com.adito.core.CoreUtil;
import com.adito.navigation.RequiresSessionPasswordAbstractFavoriteItem;
import com.adito.vfs.utils.URI;
import com.adito.vfs.webdav.DAVUtilities;

/**
 * Implementation of {@link com.adito.navigation.AbstractFavoriteItem} to
 * display the network places in a sortable table.
 */
public class NetworkPlaceItem extends RequiresSessionPasswordAbstractFavoriteItem {

    /**
     * Default window width for popup
     */
    public final static int WINDOW_WIDTH = 790;

    /**
     * Default window width for popup
     */
    public final static int WINDOW_HEIGHT = 480;

    // Private instance variables

    private String mountPath;

    /**
     * Constructor
     * @param networkPlace The network place represented by the NetworkPlaceItem.
     * @param mountPath The DAVMount path.
     * @param policies The policies for the resource.
     * @param requiresSessionPassword if session password is required.
     */
    public NetworkPlaceItem(NetworkPlace networkPlace, String mountPath, List policies, boolean requiresSessionPassword) {
        super(networkPlace, policies, requiresSessionPassword);
        this.mountPath = mountPath;
    }

    /*
     * (non-Javadoc)
     * @see com.adito.navigation.FavoriteItem#getOnClick()
     */
    public String getOnClick(int policy, HttpServletRequest request) {
        return "";
    }

    /*
     * (non-Javadoc)
     * @see com.adito.navigation.FavoriteItem#getLink(java.lang.String)
     */
    public String getLink(int policy, String referer, HttpServletRequest request) {
        return "launchNetworkPlace.do?policy=" + 
                        policy + "&resourceId=" + getResource().getResourceId() +
                        "&returnTo=" + Util.urlEncode(Util.isNullOrTrimmedBlank(referer) ? CoreUtil.getRealRequestURI(request) : referer);
    }

    /*
     * (non-Javadoc)
     * @see com.adito.navigation.FavoriteItem#getTarget()
     */
    public String getTarget() {
        return "_blank";
    }

    /**
     * Get the pathname to use for the web folder.
     * @param policy policy
     * @param request request
     * @return web folder path
     */
    public String getWebFolderPath(int policy, HttpServletRequest request) {
        if (this.getRequiresSessionPassword()) {
            /* TODO this wont work properly as it is. Wait until Lee's
             * new redirecting code is merged
             */ 
            return "/promptForSessionPassword.do?forwardTo=" +
                DAVUtilities.encodePath(getFullWebFolderPath(policy, request));
        } 
        return getFullWebFolderPath(policy, request);
    }

    /**
     * @return The DAVMount path
     */
    public String getMountPath() {
        return mountPath;
    }

    /**
     * @param mountPath The DAVMount path.
     */
    public void setMountPath(String mountPath) {
        this.mountPath = mountPath;
    }

    /*
     * (non-Javadoc)
     * @see com.adito.navigation.FavoriteItem#getName()
     */
    public String getFavoriteName() {
        return getResource().getResourceName();
    }

    /*
     * (non-Javadoc)
     * @see com.adito.navigation.AbstractFavoriteItem#getFavoriteSubType()
     */
    public String getFavoriteSubType() {
        try {
            return new URI(((NetworkPlace) getResource()).getPath()).getScheme().toUpperCase();
        } catch (Exception e) {
            return "";
        }
    }

    /*
     * (non-Javadoc)
     * @see com.adito.navigation.AbstractFavoriteItem#getSmallIconPath(javax.servlet.http.HttpServletRequest)
     */
    public String getSmallIconPath(HttpServletRequest request) {
        return CoreUtil.getThemePath(request.getSession()) + "/images/actions/runNetworkPlace.gif";
    }

    /*
     * (non-Javadoc)
     * @see com.adito.navigation.AbstractFavoriteItem#getLargeIconPath(javax.servlet.http.HttpServletRequest)
     */
    public String getLargeIconPath(HttpServletRequest request) {
        return CoreUtil.getThemePath(request.getSession()) + "/images/actions/runNetworkPlaceLarge.gif";
    }
    
    private String getFullWebFolderPath(int policy, HttpServletRequest request) {
        // Web folder paths MUST be fully qualified
        StringBuffer buf = new StringBuffer();
        buf.append(request.getScheme());
        buf.append("://");
        int port = request.getServerPort();
        buf.append(request.getServerName());
        if( ( port == 443 && !request.getScheme().equals("https") ) ||
            ( port == 80 && !request.getScheme().equals("http") ) ||
            ( port != 80 && port != 443 ) ) {
            buf.append(":");
            buf.append(port);
        }
        buf.append("/fs/");
        buf.append(mountPath); 
        return buf.toString();
        
    }
}