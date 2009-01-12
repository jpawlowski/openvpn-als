
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

import com.adito.extensions.types.PluginDatabase;

/**
 * The <i>System Configuration> {@link com.adito.extensions.types.PluginDatabase} implementation
 * is responsible for storing an retrieving Adito's network places resources and
 * configuration.
 */
public interface NetworkPlaceDatabase extends PluginDatabase {

    /**
     * Get all network places.
     * 
     * @return List<NetworkPlace>
     * @throws Exception
     */
    public List<NetworkPlace> getNetworkPlaces() throws Exception;

    /**
     * Get all network places.
     * 
     * @return List<NetworkPlace>
     * @throws Exception
     */
    public List<NetworkPlace> getNetworkPlaces(int realmID) throws Exception;

    /**
     * Get the NetworkPlace for the specified resource_id. 
     * @param resource_id
     * @return NetworkPlace
     * @throws Exception
     */
    public NetworkPlace getNetworkPlace(int resource_id) throws Exception;
    
    /**
     * Get the NetworkPlace for the specified name and realm 
     * @param name
     * @param realmID
     * @return NetworkPlace
     * @throws Exception
     */
    public NetworkPlace getNetworkPlace(String name, int realmID) throws Exception;

    /**
     * Delete the NetworkPlace with the specified resource_id.
     * 
     * @param resourceId
     * @return NetworkPlace
     * @throws Exception
     */
    public NetworkPlace deleteNetworkPlace(int resourceId) throws Exception;

    /**
     * Create a new network place.
     * 
     * @param scheme
     * @param shortName
     * @param description
     * @param host
     * @param uri
     * @param port
     * @param username
     * @param password
     * @param readOnly
     * @param allowResursive
     * @param noDelete
     * @param showHidden
     * @return NetworkPlace
     * @throws Exception
     */
    public NetworkPlace createNetworkPlace(String scheme, String shortName, String description, String host, String uri, int port, String username, String password, boolean readOnly,
                    boolean allowResursive, boolean noDelete, boolean showHidden, boolean autoStart, int realmID) throws Exception;

    /**
     * Update the NetworkPlace.
     * 
     * @param resourceId
     * @param scheme
     * @param resourceName
     * @param resourceDescription
     * @param host
     * @param uri
     * @param port
     * @param username
     * @param password
     * @param readOnly
     * @param allowResursive
     * @param noDelete
     * @param showHidden
     * @throws Exception
     */
    public void updateNetworkPlace(int resourceId, String scheme, String resourceName, String resourceDescription, String host, String uri, int port, String username, String password, boolean readOnly,
                    boolean allowResursive, boolean noDelete, boolean showHidden, boolean autoStart) throws Exception;

}