
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
			
package net.openvpn.als.reverseproxy;

import java.util.HashMap;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.maverick.http.HttpClient;

/**
 * Keeps a map of {@link HttpClient} instances that may be reused during
 * <i>Replacement Proxy</i>.
 * <p>
 * This class implements {@link HttpSessionBindingListener} so it should be
 * placed in the session and when the session is invalidated all connections
 * will be closed.
 */
public class SessionClients extends HashMap<String, HttpClient> implements HttpSessionBindingListener {

	public void valueBound(HttpSessionBindingEvent arg0) {
	}

	public void valueUnbound(HttpSessionBindingEvent arg0) {
		for (HttpClient client : values()) {
			client.close();
		}
	}

}