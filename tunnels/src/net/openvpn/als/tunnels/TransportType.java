
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
			
package net.openvpn.als.tunnels;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts.util.LabelValueBean;

import net.openvpn.als.boot.SystemProperties;

public class TransportType {

    public final static int LOCAL_TUNNEL_ID = 0;
    public final static int REMOTE_TUNNEL_ID = 1;

    public final static String LOCAL_TUNNEL = "Local";
    public final static String REMOTE_TUNNEL = "Remote";

    public final static String TCP_TUNNEL = "TCP";
    public final static String UDP_TUNNEL = "UDP"; //

    public final static List<LabelValueBean> TRANSPORTS = new ArrayList<LabelValueBean>();
    public final static List<LabelValueBean> TYPES = new ArrayList<LabelValueBean>();

    static {
        TRANSPORTS.add(new LabelValueBean("TCP", TCP_TUNNEL));
        if (Boolean.valueOf(SystemProperties.get("openvpnals.udp.transport.enabled", "false")).booleanValue()) {
            TRANSPORTS.add(new LabelValueBean("UDP", UDP_TUNNEL));
        }

        TYPES.add(new LabelValueBean(LOCAL_TUNNEL, String.valueOf(LOCAL_TUNNEL_ID)));
        TYPES.add(new LabelValueBean(REMOTE_TUNNEL, String.valueOf(REMOTE_TUNNEL_ID)));
    }

}
