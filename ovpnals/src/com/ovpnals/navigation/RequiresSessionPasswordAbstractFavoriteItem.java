
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

import java.util.List;

import com.ovpnals.policyframework.Resource;

/**
 * <p>
 * Abstract class which adds the requires session attribute.
 * 
 * 
 */
public abstract class RequiresSessionPasswordAbstractFavoriteItem extends AbstractFavoriteItem implements Comparable {

    private boolean requiresSessionPassword = false;

    /**
     * @param resource
     * @param policies
     * @param requiresSessionPassword
     */
    public RequiresSessionPasswordAbstractFavoriteItem(Resource resource, List policies, boolean requiresSessionPassword) {
        super(resource, policies);
        this.requiresSessionPassword = requiresSessionPassword;
    }

    /**
     * <p>
     * Weather the resource needs a session password.
     * 
     * @return requiresSessionPassword
     */
    public boolean getRequiresSessionPassword() {
        return requiresSessionPassword;
    }
}
