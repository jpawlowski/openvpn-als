
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

import java.util.Enumeration;
import java.util.Vector;

import com.adito.agent.client.util.TunnelConfiguration;

public final class RemotePortItem extends AbstractPortItem {
	private Vector channels = new Vector();

	public RemotePortItem(TunnelConfiguration configuration) {
		super(configuration);
	}
	
	public void addChannel(RemoteTunnelChannel rfc) {
		channels.addElement(rfc);
		increaseActive();
	}
	
	public void removeChannel(RemoteTunnelChannel rfc) {
		channels.removeElement(rfc);
		decreaseActive();
	}

	public int getLocalPort() {
		return getConfiguration().getSourcePort();
	}

	public void stop() {
		synchronized(channels) {
			for(Enumeration e = channels.elements(); e.hasMoreElements(); ) {
				((RemoteTunnelChannel)e.nextElement()).close();
			}
		}
	}

	public long getDataLastTransferred() {
		long lastTfer = -1;
		for(Enumeration e = channels.elements(); e.hasMoreElements(); ) {
			lastTfer = Math.max(lastTfer, ((RemoteTunnelChannel)e.nextElement()).getDataLastTransferredTime());
		}
		return lastTfer;
	}
}