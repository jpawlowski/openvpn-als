
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
			
package com.ovpnals.jdbc;

import java.util.Collection;

import com.ovpnals.extensions.types.PluginDatabase;
import com.ovpnals.policyframework.Resource;

/**
 * @param <T> 
 */
public interface ResourceDatabase<T extends Resource> extends PluginDatabase {
    
    /**
     * 
     * @param selectedRealmId
     * @return 
     * @throws DataAccessException
     */
    Collection<T> getResources(int selectedRealmId);
    
    /**
     * @param resourceName
     * @param selectedRealmId
     * @return
     */
    boolean isResourceNameInUse(String resourceName, int selectedRealmId);
    
    /**
     * 
     * @param resourceId
     * @return
     * @throws DataAccessException
     */
    T getResourceById(int resourceId);

    /**
     * 
     * @param resourceName
     * @param selectedRealmId
     * @return
     * @throws DataAccessException
     */
    T getResourceByName(String resourceName, int selectedRealmId);

    /**
     * 
     * @param resource
     * @return
     * @throws DataAccessException
     */
    int insertResource(T resource);
    
    /**
     * 
     * @param resource
     * @throws DataAccessException
     */
    void updateResource(T resource);

    /**
     * @param resourceId
     * @return
     * @throws DataAccessException
     */
    T removeResource(int resourceId);
}