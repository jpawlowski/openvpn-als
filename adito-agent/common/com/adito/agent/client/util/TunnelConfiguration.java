
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
			
package com.adito.agent.client.util;


/**
 * Describe the configuration of a <i>Tunnel</i>, be it
 * <i>Remote</i> or <i>Local</i>
 */
public interface TunnelConfiguration {

    /**
     * Local tunnel
     */
    public final static int LOCAL_TUNNEL = 0;
    
    /**
     * Remote tunnel
     */
    public final static int REMOTE_TUNNEL = 1;

    /**
     * TCP tunnel
     */
    public final static String TCP_TUNNEL = "TCP"; //$NON-NLS-1$
    
    /**
     * UDP tunnel
     */
    public final static String UDP_TUNNEL = "UDP"; //$NON-NLS-1$

    /**
     * Is this a permanent listener
     * 
     * @return permanent listener
     */
    public boolean isPermanent();

    /**
     * Get the name of this listener
     * 
     * @return name
     */
    public String getName();

    /**
     * Is this a non permanent tunnel, should we only allow one connection then
     * exit?
     * 
     * @return temporary single connection
     */
    public boolean isTemporarySingleConnect();
    
    /**
     * The unique tunnel id
     * 
     * @return tunnel id
     */
    public int getId();

    /**
     * The type of tunnel. this may currently only be
     * <code>Tunnel.LOCAL_TUNNEL</code> or <code>Tunnel.REMOTE_TUNNEL</code>.
     * 
     * @return tunnel type
     */
    public int getType();

    /**
     * Get the transport protocol to tunnel. Currently
     * <code>Tunnel.TCP_TUNNEL</code> and <code>Tunnel.UDP_TUNNEL</code> are
     * supported.
     * 
     * @return transport protocol
     */
    public String getTransport();


    /**
     * Get the port that a listener should be opened up on.
     * If -1 is returned the listener should attempt to find 
     * a free port.
     * 
     * @return source port
     */
    public int getSourcePort();
    
    /**
     * Get the source interface address to use. This will overide
     * both {@link #isAllowExternalHosts()} and {@link #isLocalhostWorkaround()}.
     * <p>
     * If <code>null</code> or empty string, the address will be determined by the aforementioned
     * flags.
     * 
     * @return source interface address
     */

	public String getSourceInterface();

    /**
     * Get the port that should be forward to
     * 
     * @return destination port
     */
    public int getDestinationPort();

    /**
     * Get the host that should be forward to
     * 
     * @return destination host
     */
    public String getDestinationHost();

	/**
	 * Set the source port being used. 
	 *
	 * @param port port
	 */
	public void setSourcePort(int port);
	
	/**
	 * Get the Id of the launch session this tunnel was launched
	 * under.
	 * 
	 * @return launch session
	 */
	public String getLaunchId();

}
