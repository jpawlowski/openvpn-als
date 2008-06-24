
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

/**
 * Implementations of this interface may be registered with a {@link LocalTunnelConnection}
 * and invoked when various events occur. 
 */
public interface LocalTunnelConnectionEventListener {

    /**
     * The local tunnel has been started
     * 
     * @param tunnel tunnel that started
     */
    public void localTunnelConnectionStarted(LocalTunnelConnection tunnel);

    /**
     * The local tunnel has been stopped
     * 
     * @param tunnel tunnel that stopped
     */
    public void localTunnelConnectionStopped(LocalTunnelConnection tunnel);

    /**
     * Data travelled over this tunnel.
     * 
     * @param tunnel tunnel that data travelled over
     * @param buffer actual data
     * @param count number of bytes
     * @param sent data sent (<code>false</code> is data receive)
     */
    public void localTunnelConnectionDataTransferred(LocalTunnelConnection tunnel, byte[] buffer, int count, boolean sent);
}
