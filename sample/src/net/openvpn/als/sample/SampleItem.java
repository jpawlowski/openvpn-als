
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
			
package net.openvpn.als.sample;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.openvpn.als.navigation.AbstractFavoriteItem;

/**
 * <p>
 * The SampleItem which is used for sorting and viewing.
 * 
 * @author James D Robinson <a href="mailto:james@localhost">&lt;james@localhost&gt;</a>
 * 
 */
public class SampleItem extends AbstractFavoriteItem {

    /**
     * @param sample The sample object.
     * @param policies The List of policies.
     */
    public SampleItem(Sample sample, List policies) {
        super(sample, policies);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.navigation.FavoriteItem#getId()
     */
    public String getFavoriteId() {
        return String.valueOf(((Sample) this.getResource()).getResourceId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.navigation.FavoriteItem#getOnClick()
     */
    public String getOnClick() {
        return "NO ON CLICK";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.navigation.FavoriteItem#getLink()
     */
    public String getLink(String referer) {
        return "NO CLICK";
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.navigation.FavoriteItem#getTarget()
     */
    public String getTarget() {
        return "_self";
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.navigation.AbstractFavoriteItem#getFavoriteName()
     */
    public String getFavoriteName() {
        return getResource().getResourceName();
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.navigation.AbstractFavoriteItem#getFavoriteSubType()
     */
    public String getFavoriteSubType() {
        return "";
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.navigation.AbstractFavoriteItem#getSmallIconPath(javax.servlet.http.HttpServletRequest)
     */
    public String getSmallIconPath(HttpServletRequest request) {
        return null;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.navigation.AbstractFavoriteItem#getLargeIconPath(javax.servlet.http.HttpServletRequest)
     */
    public String getLargeIconPath(HttpServletRequest request) {
        return null;
    }
}
