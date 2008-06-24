
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
			
package com.adito.tunnels.agent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.multiplex.Channel;
import com.maverick.multiplex.ChannelOpenException;
import com.maverick.multiplex.Request;
import com.maverick.util.ByteArrayWriter;
import com.adito.agent.AgentTunnel;
import com.adito.boot.CustomServerSocketFactory;
import com.adito.boot.Util;
import com.adito.core.stringreplacement.VariableReplacement;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.LaunchSessionFactory;
import com.adito.tunnels.Tunnel;
import com.adito.tunnels.TunnelingService;

/**
 * Manages the life cycle of a single remote tunnel on the server end. This
 * object sets up a server socket and listens for conenctions. Whenever a new
 * connection is made a channel is created to the associated agent which in turn
 * opens the connection to the required service.
 */
public class RemoteTunnel extends Thread {

    final static Log log = LogFactory.getLog(RemoteTunnel.class);

    private AgentTunnel agent;
    private Tunnel tunnel;
    private ServerSocket listeningSocket;
    private boolean running;
    private RemoteTunnelManager tunnelManager;
    private LaunchSession launchSession;

    /**
     * Constructor.
     * 
     * @param tunnel tunnel
     * @param agent agent
     * @param tunnelManager tunnel manager
     * @param launchSession launch session
     * @throws IOException
     */
    public RemoteTunnel(Tunnel tunnel, AgentTunnel agent, RemoteTunnelManager tunnelManager, LaunchSession launchSession) throws IOException {
        this.agent = agent;
        this.tunnel = tunnel;
        this.launchSession = launchSession;
        this.tunnelManager = tunnelManager;
        listeningSocket = CustomServerSocketFactory.getDefault().createServerSocket(
                        tunnel.getSourcePort(),
                        50,
                        Util.isNullOrTrimmedBlank(tunnel.getSourceInterface()) ? null : InetAddress.getByName(tunnel
                                        .getSourceInterface()));

    }

    /**
     * Get the agent that is dealing with this tunnel.
     * 
     * @return agent
     */
    public AgentTunnel getAgent() {
        return agent;
    }

    /**
     * Get the tunnel configuration.
     * 
     * @return tunnel
     */
    public Tunnel getTunnel() {
        return tunnel;
    }

    /**
     * Get if this remote tunnel is currently running.
     * 
     * @return running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Stop the listening socket.
     */
    public void stopListener() {
        if (running) {
            if (log.isInfoEnabled())
                log.info("Stopping remote listener on " + tunnel.getSourcePort());
            running = false;
            try {
                listeningSocket.close();
            } catch (IOException ioe) {
                log.error("Failed to stop listening socket for remote tunnel.", ioe);
            }
            if (log.isInfoEnabled())
                log.info("Stopped remote listener on " + tunnel.getSourcePort());

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread#run()
     */
    public void run() {
    	// Process destination host and port for replacement variables
    	VariableReplacement r = new VariableReplacement();
    	r.setLaunchSession(launchSession);
    	String destHost = r.replace(tunnel.getDestination().getHost());
    	
    	ByteArrayWriter msg = new ByteArrayWriter();
    	
    	try {
    		
    		msg.writeInt(tunnel.getResourceId());
    		msg.writeString(tunnel.getSourceInterface());
    		msg.writeString(launchSession.getId());
    		msg.writeString(tunnel.getSourceInterface());
    		msg.writeInt(tunnel.getSourcePort());
    		msg.writeString(destHost);
    		msg.writeInt(tunnel.getDestination().getPort());
    		
    		
    		Request request = new Request(TunnelingService.START_REMOTE_TUNNEL, msg.toByteArray());
    		agent.sendRequest(request, false);
    		
    	} catch(IOException ex) { 
    		ex.printStackTrace();
    	}
    	
        running = true;
        if (log.isInfoEnabled())
            log.info("Starting remote listener on " + tunnel.getSourcePort());
        try {
            while (running) {
                try {
                    Socket s = listeningSocket.accept();
                    if (log.isInfoEnabled())
                        log.info("Received new connection on " + tunnel.getSourcePort() + " from " + s.getInetAddress());
                    RemoteForwardingChannel channel = new RemoteForwardingChannel(this, s, tunnel, launchSession);
                    try {
                        agent.openChannel(channel);
                    } catch (ChannelOpenException e) {
                        log.error("Error opening channel. Remote tunnel remaining open but closing connection.", e);
                        try {
                            s.close();
                        } catch (IOException ioe) {
                        }
                    }
                } catch (IOException e) {
                    if (running) {
                        log.error("IO error waiting for connection, stopping remote tunnel.", e);
                    }
                }
            }
        } finally {
        	
    		Request request = new Request(TunnelingService.STOP_REMOTE_TUNNEL, msg.toByteArray());
    		try {
				agent.sendRequest(request, false);
			} catch (IOException e) {
			}
    		
            Channel[] c = agent.getActiveChannels();
            if (c != null) {
                for (int i = 0; i < c.length; i++) {
                    if (c[i] instanceof RemoteForwardingChannel) {
                        RemoteForwardingChannel rfc = (RemoteForwardingChannel) c[i];
                        if (rfc.getRemoteTunnel() == this && rfc.getConnection() != null) {
                            try {
                                rfc.close();
                            } catch (Throwable t) {
                                // TODO workaround for NPE
                                log.error("Failed to close channel.", t);
                            }
                        }
                    }
                }
            }
            tunnelManager.removeRemoteTunnel(this);
            LaunchSessionFactory.getInstance().removeLaunchSession(launchSession);
            running = false;
        }
    }

}