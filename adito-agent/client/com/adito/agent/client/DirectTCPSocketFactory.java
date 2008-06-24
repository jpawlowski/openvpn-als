
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
			
package com.adito.agent.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.multiplex.ChannelOpenException;

public class DirectTCPSocketFactory {
	final static Log log = LogFactory.getLog(DirectTCPSocketFactory.class);

	private Agent agent;

	public DirectTCPSocketFactory(Agent agent)  {
		this.agent = agent;
	}

	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		try {
			return new LocalForwardingChannelSocket(agent, host, port);
		} catch (ChannelOpenException e) {
			rethrow(e);
		}
		return null;
	}

	void rethrow(Exception e) throws IOException {
		IOException ioe = new IOException("Failed to create socket. " + e.getMessage());
		throw ioe;
	}

	public Socket createSocket(InetAddress host, int port) throws IOException {
		try {
			return new LocalForwardingChannelSocket(agent, host.getHostAddress(), port);
		} catch (ChannelOpenException e) {
			rethrow(e);
		}
		return null;
	}

	public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException,
					UnknownHostException {
		try {
			return new LocalForwardingChannelSocket(agent, host, port);
		} catch (ChannelOpenException e) {
			rethrow(e);
		}
		return null;
	}

	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
		try {
			return new LocalForwardingChannelSocket(agent, address.getHostAddress(), port);
		} catch (ChannelOpenException e) {
			rethrow(e);
		}
		return null;
	}

}
