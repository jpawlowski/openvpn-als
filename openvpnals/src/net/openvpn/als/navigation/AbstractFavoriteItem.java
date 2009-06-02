
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
			
package net.openvpn.als.navigation;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.openvpn.als.policyframework.Resource;
import net.openvpn.als.policyframework.ResourceItem;

/**
 * Abstract class to be implemented by resource items that may be added as a
 * <i>Favorite</i>.
 * 
 * @see net.openvpn.als.navigation.Favorite
 */
public abstract class AbstractFavoriteItem extends ResourceItem implements Comparable {

    /**
     * Constant for {@link #getFavoriteType()} and {@link #setFavoriteType(String)}
     * that indiciates the item does not have any type of favorite.
     */
    public final static String NO_FAVORITE = "none";

    /**
     * Constant for {@link #getFavoriteType()} and {@link #setFavoriteType(String)}
     * that indiciates the item has a <i>User Favorite</i>
     */
    public final static String USER_FAVORITE = "user";

    /**
     * Constant for {@link #getFavoriteType()} and {@link #setFavoriteType(String)}
     * that indiciates the item has a <i>Policy Favorite</i>
     */
    public final static String GLOBAL_FAVORITE = "policy";

    // Private instance variables

    private String favoriteType;

    /**
     * Compare this favorite item with another using a resource type / resource
     * ID comparison.
     * 
     * @param o other resource item
     * @return comparison
     */
    public int compareTo(Object o) {
        int i = getResource().getResourceType().compareTo(((ResourceItem) o).getResource().getResourceType());
        return i == 0 ? new Integer(getResource().getResourceId()).compareTo(new Integer(((ResourceItem) o).getResource()
            .getResourceId())) : i;
    }

    /**
     * Constructor.
     * 
     * @param resource resource
     * @param policies policies item is attached to
     */
    public AbstractFavoriteItem(Resource resource, List policies) {
        super(resource, policies);
    }

    /**
     * Return the link to launch this item using the show favorites page as the
     * referer.
     * @param policy TODO
     * @param request equest
     * 
     * @return link
     */
    public String getFavoriteLink(int policy, HttpServletRequest request) {
        return getLink(-1, "/showFavorites.do?actionTarget=unspecified", request);
    }

    /**
     * Get if this item has a favorite and if so what type it is. 
     * The value may be {@link #NO_FAVORITE}, {@link #USER_FAVORITE} or
     * {@link #GLOBAL_FAVORITE}.
     * 
     * @return favorite type
     * 
     */
    public String getFavoriteType() {
        return favoriteType;
    }

    /**
     * Set if this item has a favorite and if so what type it is. 
     * The value may be {@link #NO_FAVORITE}, {@link #USER_FAVORITE} or
     * {@link #GLOBAL_FAVORITE}.
     * 
     * @param favoriteType favorite type
     * 
     */
    public void setFavoriteType(String favoriteType) {
        this.favoriteType = favoriteType;
    }

    /**
     * Get the browsers frame target to use when launching this favorite.
     * 
     * @return browser frame target
     */
    public abstract String getTarget();

    /**
     * Get a javascript fragment to use for the onClick event when launching
     * this item.
     * 
     * @return onclick javascript fragment
     */
    public abstract String getOnClick(int policy, HttpServletRequest request);

    /**
     * Get the link to launch this item using the specified referer as the page
     * to return to.
     * @param referer
     * 
     * @return link
     */
    public abstract String getLink(int policy, String referer, HttpServletRequest request);

    /**
     * Get the name to display on the favorites list.
     * 
     * @return favorite name
     */
    public abstract String getFavoriteName();

    /**
     * Get the sub-type to display on the favorites list
     * 
     * @return favorite sub type
     */
    public abstract String getFavoriteSubType();

    /**
     * Get the path to the small icon to use for this resource for the given
     * request
     * 
     * @param request request
     * @return path to small icon
     */
    public abstract String getSmallIconPath(HttpServletRequest request);

    /**
     * Get the path to the large icon to use for this resource for the given
     * request
     * 
     * @param request request
     * @return path to large icon
     */
    public abstract String getLargeIconPath(HttpServletRequest request);

}
