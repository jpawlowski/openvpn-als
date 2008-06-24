
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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.maverick.multiplex.Channel;
import com.maverick.multiplex.ChannelOpenException;
import com.maverick.multiplex.channels.LocalForwardingChannel;

class LocalForwardingChannelSocket extends Socket {

	private Channel channel;
	private Agent agent;

	public LocalForwardingChannelSocket(Agent agent, String host, int port) throws IOException, ChannelOpenException {
		super();
		this.agent = agent;
		channel = new LocalForwardingChannel(host, port);
		agent.getConnection().openChannel(channel);
	}

	public LocalForwardingChannelSocket(String host, int port) throws IOException {
		super(host, port);
	}

	public InputStream getInputStream() throws IOException {
		return channel.getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		return channel.getOutputStream();
	}

	public synchronized void close() throws IOException {
		DirectTCPSocketFactory.log.info("Channel socket closed.");
		channel.close();
	}
}
