
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
			
package com.ovpnals.boot;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Often a target service may be repesented by a hostname (or IP address) and
 * a port. 
 * <p>
 * This object may be contructed either by provided a host name and port
 * seperately or by providing a string in the format <b>[host]:[port]</b>.   
 */
public class HostService {
    
    // Private instance variables

    private String hostname;
    private int port;


    /**
     * Constructor
     * 
     * @param hostname host
     * @param port port
     */
    public HostService(String hostname, int port)  {
        super();
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Constructor. Takes a string in the format <b>[host]:[port]</b>.
     * 
     * @param uri uri in the format <b>[host]:[port]</b>
     */
    public HostService(String uri) {
        int i = uri.indexOf(':');
        if (i != -1) {
            String addr = uri.substring(0, i);
            if (addr.indexOf('/') > 0)
                addr = addr.substring(addr.indexOf('/') + 1);
            uri = uri.substring(i + 1);
            if (addr.length() > 0) {
                hostname = addr;
            }
        }

        try {
            port = Integer.parseInt(uri);
        }
        catch(NumberFormatException nfe) {
            hostname = uri;
        }
    }

    /**
     * Get the hostname / IP address
     * 
     * @return hostname / IP address
     */
    public String getHost() {
        return hostname;
    }

    /**
     * Set the hostname / IP address
     * 
     * @param hostname hostname / IP address
     */
    public void setHost(String hostname) {
        this.hostname = hostname;
    }

    /**
     * Get the port
     * 
     * @return port.
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the port
     * 
     * @param port port.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Get the hostname as an {@link InetAddress}.
     * 
     * @return address
     * @throws UnknownHostException if hostname is not a valid host
     */
    public InetAddress getAddress() throws UnknownHostException {
        return InetAddress.getByName(getHost());
    }
    
    /**
     * Return a string representation of this host / port
     * 
     * @return string representation
     */
    public String toString() {
        return getHost() + ":" + getPort();
    }
}
