
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.boot;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Implementations of this interface are able to take over the input / output
 * streams connected to a client during the processing of an HTTP request.
 * <p>
 * This allows OpenVPNALS functionality such as the VPN client or the 
 * nEXT client to provide their tunneling functionality.
 * <p>
 * For this to work, a custom {@link RequestHandler} would examine the request
 * sent to it, decide if it should start a tunnel and if so call 
 * {@link net.openvpn.als.boot.RequestHandlerRequest#setTunnel(RequestHandlerTunnel)}
 * providing a tunnel instance.
 * <p>
 * Then, which the ready the {@link #tunnel(InputStream, OutputStream)} method
 * will be called allow the streams to be accessed and used directly.  
 */
public interface RequestHandlerTunnel {
    
    /**
     * Start tunneling.
     * 
     * @param in input stream
     * @param out output stream
     */
    public void tunnel(InputStream in, OutputStream out);

    /**
     * Close the tunnel
     */
    public void close();

}

