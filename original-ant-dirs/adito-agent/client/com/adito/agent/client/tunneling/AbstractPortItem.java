
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
			
package com.adito.agent.client.tunneling;

import com.adito.agent.client.util.TunnelConfiguration;


/**
 * Encapsulate a single listener socket that can tunnel 
 * multiple connections of data in either direction. 
 * This is used in the {@link AWTPortMonitorWindow}.
 * 
 * @see PortModel
 * @see AWTPortMonitorWindow
 */
public abstract class AbstractPortItem {
    
    //  Private instance variables
    
    private TunnelConfiguration listeningSocketConfiguration;
    private RemoteTunnelChannel remoteForwardingChannel;
    private int totalConnections, activeConnections;

    /**
     * Constructor for local tunnels
     *
     * @param listeningSocketConfiguration local tunnel
     * @param localListener local tunnel listener
     */
    public AbstractPortItem(TunnelConfiguration listeningSocketConfiguration) {
        super();
        this.listeningSocketConfiguration = listeningSocketConfiguration;
    }
    
    /**
     * Get port type description.
     * 
     * @return tunnel type
     */
    public String getType() {
        return listeningSocketConfiguration.getType() == TunnelConfiguration.LOCAL_TUNNEL ? Messages.getString("PortItem.local") : Messages.getString("PortItem.remote"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    /**
     * Get the tunnel configuration
     * 
     * @return configuration
     */
    public TunnelConfiguration getConfiguration() {
        return listeningSocketConfiguration;        
    }

    /**
     * Get the port name
     * 
     * @return port name
     */
    public String getName() {
        return listeningSocketConfiguration.getName();
    }
    
    /**
     * Get the number of active tunnels on this port.
     * 
     * @return active tunnels on port
     */
    public int getActiveTunnelCount() {
        return activeConnections;
    }

    
    /**
     * Get the total number of tunnels that have ever been
     * open on this port.
     * 
     * @return total tunnels on port
     */
    public int getTotalTunnelCount() {
        return totalConnections;
    }

    /**
     * If this is a remote port, then get the channel otherwise
     * return <code>null</code>
     * 
     * @return remote forwarding channel
     */
    public RemoteTunnelChannel getRemoteForwardingChannel() {
        return remoteForwardingChannel;
    }
    
    /**
     * Increate the number of active connections by 1
     */
    public void increaseActive() {
    	activeConnections++;
    	totalConnections++;
    }
    
    /**
     * Decrease the number of active connections by 1
     */
    public void decreaseActive() {
    	activeConnections--;
    }

	public abstract void stop();

    /**
     * Get the local IP port numberon which the port is running.
     * 
     * @return local port number
     */
    public abstract int getLocalPort();

    /**
     * Get when data was last transferred
     * 
     * @return data last transferred
     */
    public abstract long getDataLastTransferred();
}