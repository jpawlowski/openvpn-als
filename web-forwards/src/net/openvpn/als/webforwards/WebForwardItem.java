
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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.openvpn.als.boot.HostService;
import net.openvpn.als.boot.Util;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.navigation.RequiresSessionPasswordAbstractFavoriteItem;

/**
 * Wrapper to make a suitable struts bean for
 * {@link net.openvpn.als.webforwards.WebForward} instances.
 */
public class WebForwardItem extends RequiresSessionPasswordAbstractFavoriteItem {

    // Private instance variables

    private String selected;
    private HostService openvpnalsHost;

    /**
     * 6 Construct a new web forward item wrapping a {@link WebForward}.
     * 
     * @param webForward the web forward object
     * @param openvpnalsHost the hostname of this OpenVPN-ALS server as the
     *        client sees it.
     * @param policies list of policies
     * @param requiresPassword requires password
     */
    public WebForwardItem(WebForward webForward, HostService openvpnalsHost, List policies, boolean requiresPassword) {
        /*
         * TODO this needs to use ResourceItem and the actual resource object as /*
         * per NetworkPlaceItem
         */
        super(webForward, policies, requiresPassword);
        this.openvpnalsHost = openvpnalsHost;
    }

    /**
     * Get the host
     * 
     * @return host
     */
    public HostService getHost() {
        return openvpnalsHost;
    }

    /**
     * Get the URL required to 'launch' this web forward. 
     * 
     * @return link
     */
    public String getLink(int policy, String referer, HttpServletRequest request) {
        return "/launchWebForward.do?resourceId=" + getWebForward().getResourceId() + "&policy=" + policy +
        "&returnTo=" + Util.urlEncode(Util.isNullOrTrimmedBlank(referer) ? CoreUtil.getRealRequestURI(request) : referer);
    }

    /**
     * Get a descriptive name for the type of web forward this item represents.
     * 
     * @return type name
     */
    public String getTypeName() {
        return ((WebForwardTypeItem) WebForwardTypes.WEB_FORWARD_TYPES.get(this.getWebForward().getType())).getName();
    }

    /**
     * Set whether this item is selected
     * 
     * @param selected
     */
    public void setSelected(String selected) {
        this.selected = selected;
    }

    /**
     * Get if this item is selected
     * 
     * @return selected
     */
    public String getSelected() {
        return selected;
    }

    /**
     * Get the web forward
     * 
     * @return
     */
    public WebForward getWebForward() {
        return (WebForward) this.getResource();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.navigation.FavoriteItem#getOnClick()
     */
    public String getOnClick(int policy, HttpServletRequest request) {
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.navigation.FavoriteItem#getTarget()
     */
    public String getTarget() {
        return "_blank";
    }

    /**
     * Get the category for this web forward
     * 
     * @return category
     */
    public String getCategory() {
        return ((WebForward) this.getResource()).getCategory();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.table.TableItem#getColumnValue(int)
     */
    public Object getColumnValue(int col) {
        if (col < 2) {
            return super.getColumnValue(col);
        }
        switch (col) {
            case 2:
                return getTypeName();
            default:
                return getCategory();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.navigation.AbstractFavoriteItem#getFavoriteName()
     */
    public String getFavoriteName() {
        return getResource().getResourceName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.navigation.AbstractFavoriteItem#getFavoriteSubType()
     */
    public String getFavoriteSubType() {
        return getTypeName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.navigation.AbstractFavoriteItem#getSmallIconPath(javax.servlet.http.HttpServletRequest)
     */
    public String getSmallIconPath(HttpServletRequest request) {
        return CoreUtil.getThemePath(request.getSession()) + "/images/actions/runWebForward.gif";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.navigation.AbstractFavoriteItem#getLargeIconPath(javax.servlet.http.HttpServletRequest)
     */
    public String getLargeIconPath(HttpServletRequest request) {
        return CoreUtil.getThemePath(request.getSession()) + "/images/actions/runWebForwardLarge.gif";
    }
}