
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
			
package com.adito.tunnels;

import java.util.List;

import com.adito.boot.HostService;
import com.adito.extensions.types.PluginDatabase;

/**
 * The <i>System Configuration> {@link com.adito.extensions.types.PluginDatabase}
 * implementation is responsible for storing an retrieving Adito's tunnel
 * resource and configuration.
 */
public interface TunnelDatabase extends PluginDatabase {
	

    /**
     * Method to create a tunnel.
     * 
     * @param name
     * @param description
     * @param type
     * @param autoStart
     * @param transport
     * @param username
     * @param sourcePort
     * @param destination
     * @param sourceInterface
     * @return Tunnel
     * @throws Exception
     */
    public Tunnel createTunnel(int realmID, String name, String description, int type, boolean autoStart, String transport, String username,
                    int sourcePort, HostService destination, String sourceInterface) throws Exception;

    /**
     * Method to update the tunnel.
     * 
     * @param id
     * @param name
     * @param description
     * @param type
     * @param autoStart
     * @param transport
     * @param username
     * @param sourcePort
     * @param destination
     * @param sourceInterface
     * @throws Exception
     */
    public void updateTunnel(int id, String name, String description, int type, boolean autoStart, String transport,
                    String username, int sourcePort, HostService destination, String sourceInterface) throws Exception;

    /**
     * Get the tunnels
     * 
     * @return List<Tunnel> all tunnels
     * @throws Exception
     */
    public List<Tunnel> getTunnels() throws Exception;

    /**
     * Get the tunnels
     * 
     * @return List<Tunnel> all tunnels
     * @throws Exception
     */
    public List<Tunnel> getTunnels(int realmID) throws Exception;

    /**
     * Remove a tunnel
     * 
     * @param id
     * @return Tunnel the tunnel removed
     * @throws Exception
     */
    public Tunnel removeTunnel(int id) throws Exception;

    /**
     * Get the tunnel with a given resource id.
     * 
     * @param id the tunnel resource id
     * @return Tunnel get the tunnel
     * @throws Exception
     */
    public Tunnel getTunnel(int id) throws Exception;
    
    /**
     * Get the tunnel with a given resource name.
     * 
     * @param name
     * @param realmID
     * @return
     * @throws Exception
     */
    public Tunnel getTunnel(String name, int realmID) throws Exception;

}