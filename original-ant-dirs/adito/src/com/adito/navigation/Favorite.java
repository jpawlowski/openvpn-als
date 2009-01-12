
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

/**
 * Implementations of this interface point to a particular resource of a
 * particular type. These will then be made available on a users <i>Favorites</i>
 * page.
 * <p>
 * Favorites will be one of two types, a <i>User Favorite</i> or a <i>Global
 * Favorite</i>.
 * <p>
 * A user may configure their own <i>User Favorites</i> by clicking on the
 * appropriate icon in a resource listing page such as <i>Web Forwards</i> or
 * <i>Network Places</i>.
 * <p>
 * An administrator may configure a <i>Global Favorite</i> at the time of
 * creating the resource. This will be accessable to all users that have the
 * correct <i>Policy</i>.
 */
public interface Favorite {

    /**
     * Get the favorite id
     * 
     * @return id
     */
    public int getId();

    /**
     * Get the username if this is a <i>User Favorite</i>
     * or <code>null</code> if it is a <i>Global Favorite</i> 
     * 
     * @return username
     */
    public String getUsername();

    /**
     * Get the type of favorite. This maps directly to the <i>Resource Type</i>
     * id ({@link com.adito.policyframework.ResourceType#getResourceTypeId()})
     * of the {@link com.adito.policyframework.Resource} the favorite
     * points to.
     * 
     * @return favorite type
     */
    public int getType();

    /**
     * Get the ID of the {@link com.adito.policyframework.Resource} that
     * the favorite points to.
     * 
     * @return favorite key
     */
    public int getFavoriteKey();
}
