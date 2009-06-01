
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
			
package net.openvpn.als.agent.client.tunneling;

import java.io.IOException;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Vector;

import com.maverick.multiplex.Channel;
import net.openvpn.als.agent.client.util.IOStreamConnector;
import net.openvpn.als.agent.client.util.IOStreamConnectorListener;
import net.openvpn.als.agent.client.util.TunnelConfiguration;

/**
 * Represents a single <i>Local Tunnel Connection</i>, i.e. one connection
 * that has been spawned from a {@link LocalTunnelServer}. 
 */
public class LocalTunnelConnection {

    // Private instance variables
    private Channel tunnel;
    private Socket client;
    private IOStreamConnector tx;
    private IOStreamConnector rx;
    private IOStreamListener rxStreamListener = new IOStreamListener(false);
    private IOStreamListener txStreamListener = new IOStreamListener(false);
    private Vector listeners;
    private IOStreamConnectorListener txListener;
    private IOStreamConnectorListener rxListener;
    private LocalTunnelServer vpnConnectionListener;
    private boolean hasStopped = false;
    private boolean stopping;
    // #ifdef DEBUG
    org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(LocalTunnelConnection.class);
    // #endif

    /**
     * Constructor.
     *
     * @param vpnConnectionListener connection listener
     * @param tunnel socket
     * @param client client
     * @param configuration tunnel configuration
     * @param txListener
     * @param rxListener
     */
    public LocalTunnelConnection(LocalTunnelServer vpnConnectionListener, Channel tunnel, Socket client, TunnelConfiguration configuration, IOStreamConnectorListener txListener, IOStreamConnectorListener rxListener) {
        this.tunnel = tunnel;
        this.client = client;
        this.txListener = txListener;
        this.rxListener = rxListener;
        this.vpnConnectionListener = vpnConnectionListener;
        this.listeners = new Vector();
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vpn.base.VPNTunnel#getClientHost()
     */
    public String getClientHost() {
        return client.getInetAddress().getHostName();
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vpn.base.VPNTunnel#getVPNConnectionListener()
     */
    public LocalTunnelServer getVPNConnectionListener() {
        return vpnConnectionListener;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vpn.base.VPNTunnel#start()
     */
    public void start() throws IOException {
        try {
            rx = new IOStreamConnector();
            rx.addListener(rxListener);
            rx.addListener(rxStreamListener);
            rx.connect(tunnel.getInputStream(), client.getOutputStream());

            tx = new IOStreamConnector();
            tx.addListener(txListener);
            tx.addListener(txStreamListener);
            tx.connect(client.getInputStream(), tunnel.getOutputStream());

            for (int i = 0; i < listeners.size(); i++)
                ((LocalTunnelConnectionEventListener) listeners.elementAt(i)).localTunnelConnectionStarted(this);
            
        } catch (Throwable ex) {
            // #ifdef DEBUG
            log.error("ActiveTunnel threads failed to start", ex);
            // #endif

            try {
                client.close();
            } catch (Throwable e) {
            }

            try {
                tunnel.close();
            } catch (Throwable e) {
            }

            throw new IOException(MessageFormat.format(Messages.getString("LocalTCPTunnel.tunnelFailedToStart"), new Object[] { ex.getMessage() })); //$NON-NLS-1$
        }
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.vpn.base.VPNTunnel#stop()
     */
    public synchronized void stop() {

    	if(!hasStopped) {
    		stopping = true;
	        if (!rx.isClosed()) {
	            rx.close();
	        }	
	        if (!tx.isClosed()) {
	            tx.close();
	        }
	        for (int i = 0; i < listeners.size(); i++)
	            ((LocalTunnelConnectionEventListener) listeners.elementAt(i)).localTunnelConnectionStopped(this);
    	}
    	
    	try {
			client.close();
		} catch (IOException e) {
		}

		tunnel.close();

    	stopping = false;
    	hasStopped = true;
    }

    protected void addListener(LocalTunnelConnectionEventListener listener) {
        if (listener != null)
            listeners.addElement(listener);
    }

    class IOStreamListener implements IOStreamConnectorListener {
    	
    	private boolean send;
    	
    	public IOStreamListener(boolean send) {
    		this.send = send;
		}

        public void connectorClosed(IOStreamConnector connector) {
            try {
                client.close();
            } catch (IOException ex) {
            }
            try {
                tunnel.close();
            } catch (Exception ex1) {
            }
	        if (connector != rx) {
	            rx.close();
	        }	
	        if (connector != tx) {
	            tx.close();
	        } 
            if(!stopping) {
            	stop();
            }
        }

        public void dataTransfered(byte[] buffer, int count) {
            for (int i = listeners.size() - 1; i >= 0; i--)
                ((LocalTunnelConnectionEventListener) listeners.elementAt(i)).localTunnelConnectionDataTransferred(LocalTunnelConnection.this, buffer, count, send);
        }
    }

}
