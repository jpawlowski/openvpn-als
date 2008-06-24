
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

import java.text.MessageFormat;

import com.maverick.multiplex.Channel;
import com.maverick.multiplex.ChannelListener;
import com.adito.agent.client.Agent;
import com.adito.agent.client.PortMonitor;

public class RemoteTunnelChannelListener implements ChannelListener {

	private Agent agent;
	private PortMonitor portMonitor;

	public RemoteTunnelChannelListener(Agent agent) {
		this.agent = agent;
		portMonitor = this.agent.getGUI().getPortMonitor();
	}

	public void onChannelClose(Channel channel) {
		RemoteTunnelChannel rfc = (RemoteTunnelChannel) channel;
		if(agent.getConfiguration().isDisplayInformationPopups()) {
			agent.getGUI()
							.popup(null,
								MessageFormat.format(Messages.getString("RemoteForwardingChannelListener.closingRemoteTunnel"), new Object[] { rfc.getConfiguration().getName(),//$NON-NLS-1$  
										String.valueOf(rfc.getConfiguration().getSourcePort()),
										rfc.getConfiguration().getDestinationHost() + ":" + rfc.getConfiguration().getDestinationPort() }), //$NON-NLS-1$ 
								Messages.getString("Agent.title"), //$NON-NLS-1$
								"popup-tunnel", -1); //$NON-NLS-1$PortMonitor pm = WebForwardManager.getPortMonitor();
		}
		synchronized (portMonitor) {
			int idx = portMonitor.getIndexForId(rfc.getConfiguration().getId());
			AbstractPortItem portItem = idx == -1 ? null : portMonitor.getItemAt(idx);
			if (portItem != null) {
				portItem.decreaseActive();
				portMonitor.updateItemAt(idx);
			}
		}
	}

	public void onChannelData(Channel channel, byte[] buf, int off, int len) {
		RemoteTunnelChannel rfc = (RemoteTunnelChannel) channel;
		synchronized (portMonitor) {
			int idx = portMonitor.getIndexForId(rfc.getConfiguration().getId());
			if (idx != -1) {
				portMonitor.updateItemAt(idx);
			}
		}
	}

	public void onChannelOpen(Channel channel) {
		final RemoteTunnelChannel rfc = (RemoteTunnelChannel) channel;
		if(agent.getConfiguration().isDisplayInformationPopups()) {
			agent.getGUI()
							.popup(null,
								MessageFormat.format(Messages.getString("RemoteForwardingChannelListener.openedRemoteTunnel"), new Object[] { rfc.getConfiguration().getName(), String.valueOf(rfc.getConfiguration().getSourcePort()), //$NON-NLS-1$$ 
										rfc.getConfiguration().getDestinationHost() + ":" + rfc.getConfiguration().getDestinationPort() }), //$NON-NLS-1$$
								Messages.getString("Agent.title"), //$NON-NLS-1$
								"popup-tunnel", -1); //$NON-NLS-1$
		}
		
		synchronized (portMonitor) {
			int idx = portMonitor.getIndexForId(rfc.getConfiguration().getId());
			RemotePortItem portItem = idx == -1 ? null : (RemotePortItem)portMonitor.getItemAt(idx);
			if (portItem == null) {
				portItem = new RemotePortItem(rfc.getConfiguration());
				portItem.addChannel(rfc);
				portMonitor.addPortItem(portItem);
			} else {
				portItem.addChannel(rfc);
				portMonitor.updateItemAt(idx);
			}
		}
	}

}
