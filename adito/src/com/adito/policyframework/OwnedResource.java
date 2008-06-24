
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
			
package com.adito.policyframework;

/**
 * Extension of the {@link Resource} interface to be implemented by
 * resources that may be owned by a single user. For example, a user
 * may setup personal profiles which are represented by the 
 * {@link com.adito.properties.PropertyProfile} object.
 * 
 * @since 0.2
 */

public interface OwnedResource extends Resource {

    /**
     * Get the username of the owner of the resource. This should be <code>null</code>
     * is the resource is global.
     *   
     * @return owner username or <code>null</code> if global 
     */
    public String getOwnerUsername();
}
