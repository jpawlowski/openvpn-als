
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
			
package com.ovpnals.navigation;

import javax.servlet.http.HttpServletRequest;

import com.ovpnals.policyframework.Resource;
import com.ovpnals.policyframework.ResourceType;

/**
 * Extension of {@link com.ovpnals.policyframework.ResourceType} to be
 * used by resources that are capable of supporting favorites.
 */
public interface FavoriteResourceType<T extends Resource> extends ResourceType<T> {

    /**
     * Create a {@link WrappedFavoriteItem} from a policy assigned favorite
     * given its resource Id. This method should return <code>null</null> if
     * the resource doesn't exist. This causes the favorite to be deleted. 
     * 
     * @param resourceId resource id
     * @param request request
     * @param type will be one on {@link AbstractFavoriteItem#GLOBAL_FAVORITE} or
     *        {@link AbstractFavoriteItem#USER_FAVORITE}.
     * @return wrapped favorite item or <code>null</code> if resource doesn't exist
     * @throws Exception on any error
     */
    public WrappedFavoriteItem createWrappedFavoriteItem(int resourceId, HttpServletRequest request, String type) throws Exception;

}
