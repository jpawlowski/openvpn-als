
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
import java.util.Hashtable;

import com.adito.agent.client.Agent;

/**
 * Monitors all active tunnels for inactivity, closing them when a certain
 * number of milliseconds have elapsed without any data travelling over them.
 * <p>
 * This monitor must be started after the VPN client has been connected and will
 * automatically stop when the client disconnects.
 */
public class TunnelInactivityMonitor extends Thread {

	// Private instance variables
	private Agent vpnClient;

	// #ifdef DEBUG
	static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(TunnelInactivityMonitor.class);

	// #endif

	/**
	 * Constructor.
	 * 
	 * @param vpnClient client
	 */
	public TunnelInactivityMonitor(Agent vpnClient) {
		this.vpnClient = vpnClient;
		setDaemon(true);
	}

	/*
	 * This thread monitors the inactivity of tunnels. It does this by getting a list (HashTable)
     * of active tunnels from the Agent's TunnelManager and comparing each tunnel's recent activity
     * against configured timeout value periodically. Tunnel activity statistics are
     * stored in instances of LocalTunnelServer, which are created whenever a new tunnel is
     * created. S.c. "temporary single connect tunnels" and "remote tunnels" are handled slightly
     * differently from "temporary tunnels" used by tunneled webforwards.
     *
     * FIXME: This thread is pretty hard to read due to large number of nested if/then and
     *        try/catch segments. Needs some cleanup.
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		// #ifdef DEBUG
		log.info("Starting tunnel inactivity monitor (tunnels " + new Integer(vpnClient.getConfiguration().getTunnelInactivity())
			+ "ms, web forwards "
			+ new Integer(vpnClient.getConfiguration().getWebForwardInactivity())
			+ "ms)");
		// #endif
		while (true) {
			try {
				Thread.sleep(30000);
				if (vpnClient.getState() == Agent.STATE_DISCONNECTED) {
					break;
				}
				// #ifdef DEBUG
				log.info("Checking for tunnel inactivity"); //$NON-NLS-1$
				// #endif

                // Get a HastTable containing active (local) tunnels
				Hashtable activeListeners = vpnClient.getTunnelManager().getActiveLocalTunnels();

				synchronized (activeListeners) {
					long now = System.currentTimeMillis();
					for (Enumeration e = activeListeners.keys(); e.hasMoreElements();) {
						Integer id = (Integer) e.nextElement();
						LocalTunnelServer l = (LocalTunnelServer) activeListeners.get(id);
						try {
                            // Check if a tunnel is listening
							if (l.isListening()) {
								// Temporary single connect tunnels and
								// permanent tunnels
								if (vpnClient.getConfiguration().getTunnelInactivity() != 0 && l.getTunnel().isPermanent()) {
                                    // Check if we should stop the tunnel due to inactivity
									if (now > (l.getDataLastTransferredTime() + vpnClient.getConfiguration().getTunnelInactivity())) {
										// #ifdef DEBUG
										log.info("Permanent tunnel " +  id + " is out of date, closing."); //$NON-NLS-1$
										// #endif
										vpnClient.getTunnelManager().stopLocalTunnel(l.getId());
									}

								}
								// Temporary tunnels that allow multiple
								// connections (i.e.
								// web forwards)
								else if (vpnClient.getConfiguration().getWebForwardInactivity() != 0 && !l.getTunnel().isPermanent()) {
									if (now > (l.getDataLastTransferredTime() + vpnClient.getConfiguration()
													.getWebForwardInactivity())) {
										// #ifdef DEBUG
										log.info("Temporary tunnel " + id + " is out of date, closing."); //$NON-NLS-1$
										// #endif
										vpnClient.getTunnelManager().stopLocalTunnel(l.getId());
									}
								}
							}
						} catch (Throwable t) {
							// #ifdef DEBUG
							log.error("Failed to check state of tunnel " + id, t); //$NON-NLS-1$
							// #endif
						}
					}
				}

			} catch (Throwable t) {
				// #ifdef DEBUG
				log.error("Failed to check state of tunnels", t); //$NON-NLS-1$
				// #endif
			}
		}
		// #ifdef DEBUG
		log.error("Disconnected, so stopping tunnel inactivity monitor."); //$NON-NLS-1$
		// #endif
	}
}
