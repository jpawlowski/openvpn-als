
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

import com.adito.boot.HostService;
import com.adito.policyframework.Resource;

/**
 * Extension of {@link com.adito.policyframework.Resource} that provides
 * instances of <i>SSL Tunnels</i>, one of Aditos main resource types.
 */
public interface Tunnel extends Resource {

    /**
     * The type of tunnel. this may currently only be
     * {@link TransportType#REMOTE_TUNNEL_ID} or
     * {@link TransportType#LOCAL_TUNNEL_ID}
     * 
     * @return tunnel type
     */
    public int getType();

    /**
     * Get if this tunnel should be automatically started when the user logs in.
     * 
     * @return <code>true</code> if autostart tunnel upon login
     */
    public boolean isAutoStart();

    /**
     * Get the transport protocol to tunnel. Currently
     * {@link TransportType#TCP_TUNNEL} and {@link TransportType#UDP_TUNNEL} are
     * supported.
     * 
     * @return transport protocol
     */
    public String getTransport();

    /**
     * Get the user that this tunnel is attached to, or an empty styring if this
     * is a global tunnel to be configured for all users.
     * 
     * @return user id
     */
    public String getUsername();

    /**
     * Get the port that a listener should be opened up on.
     * 
     * @return source port
     */
    public int getSourcePort();

    /**
     * The source interface of the listening socket.
     * 
     * @return source interface
     */
    public String getSourceInterface();

    /**
     * Get the destination
     * 
     * @return destination
     */
    public HostService getDestination();

    /**
     * Set the type of tunnel. this may currently only be
     * {@link TransportType#REMOTE_TUNNEL_ID} or
     * {@link TransportType#LOCAL_TUNNEL_ID}
     * 
     * @param tunnelType tunnel type
     */
    public void setType(int tunnelType);

    /**
     * Set if this tunnel should auto-start when the VPN client is started
     * 
     * @param autoStart auto-start on VPN client start up
     */
    public void setAutoStart(boolean autoStart);

    /**
     * Set the transport protocol to tunnel. Currently
     * {@link TransportType#TCP_TUNNEL} and {@link TransportType#UDP_TUNNEL} are
     * supported.
     * 
     * @param transport transport protocol
     */
    public void setTransport(String transport);

    /**
     * Set the source port of the tunnel
     * 
     * @param sourcePort source port
     */
    public void setSourcePort(int sourcePort);

    public void setSourceInterface(String sourceInterface);

    /**
     * Set the destination host / port of the tunnel
     * 
     * @param destination destination
     */
    public void setDestination(HostService destination);

}