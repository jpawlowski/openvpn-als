
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
			
package net.openvpn.als.tunnels;

/**
 * Constants used for event attributes
 */
public class TunnelsEventConstants {

    /**
     * Tunnel tcp
     */
    public static final String EVENT_ATTR_TUNNEL_TYPE = "tunnelType";

    /**
     * Tunnel destination
     */
    public static final String EVENT_ATTR_TUNNEL_DESTINATION = "tunnelDestination";

    /**
     * Tunnel transport
     */
    public static final String EVENT_ATTR_TUNNEL_TRANSPORT = "tunnelTransport";
    
    /**
     * Source port
     */
    public static final String EVENT_ATTR_TUNNEL_SOURCE_PORT = "tunnelSourcePort";
    
    
    public static final String EVENT_ATTR_TUNNEL_SOURCE_INTERFACE = "tunnelSourceInterface";

    /**
     * An SSL tunnel has been opened
     */
    public static final int TUNNEL_OPENED = 601;

    /**
     * An SSL tunnel has been closed
     */
    public static final int TUNNEL_CLOSED = 602;

    /**
     * SSL Tunnel created
     */
    public static final int CREATE_TUNNEL = 2009;

    /**
     * SSL Tunnel updated
     */
    public static final int UPDATE_TUNNEL = 2010;

    /**
     * SSL Tunnel removed
     */
    public static final int REMOVE_TUNNEL = 2012;


}
