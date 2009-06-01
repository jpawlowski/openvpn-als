
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
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;

import com.maverick.multiplex.Channel;
import com.maverick.multiplex.channels.LocalForwardingChannel;
import net.openvpn.als.agent.client.Agent;
import net.openvpn.als.agent.client.util.IOStreamConnectorListener;
import net.openvpn.als.agent.client.util.TunnelConfiguration;


/**
 * Sets up maintains a single listening server socket to support <i>Local
 * Tunnels</i>. 
 * <p>
 * This listener will accept connections from either only localhost or from
 * any host (depending on how the tunnel was configured) and forward them
 * to the OpenVPNALS server the Agent is connected to.
 * <p>
 * When constructed, two {@link IOStreamConnectorListener}s must be provided.
 * These are used to monitor events such as when data travels 
 * through the listener in either direction.
 * <p>
 * You must also provide a {@link TunnelConfiguration}. The listener is configured from
 * the details obtained from this object. The listener will be running on
 * the port specified in {@link TunnelConfiguration#getSourcePort()}.
 * <p>
 * Before any connections can be made to this listener, it must be started.
 * Invoked the {@link #start()} method. The listener may be stopped at
 * any time using the {@link #stop()} method.
 * <p>
 * NOTE UDP tunneling does not currently work.
 */
public class LocalTunnelServer implements LocalTunnelConnectionEventListener {
    
    // Private instance variables
    private Agent vpn;
    private ServerSocket server;
    private Thread thread;
    private boolean listening;
    private Vector activeTunnels;
    private IOStreamConnectorListener txListener;
    private IOStreamConnectorListener rxListener;
    private String ticket;
    private TunnelConfiguration listeningSocketConfiguration;
    private long dataLastTransferred;
    private DatagramSocket datagramSocket;
    private boolean stopping = false;
    private int totalTunnels;
    private Vector listeners;

    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(LocalTunnelServer.class);
    // #endif

    /**
     * Constructor.
     *
     * @param vpn vpn
     * @param txListener transmit listener
     * @param rxListener receive listener
     * @param listeningSocketConfiguration tunnel to configure listener from.
     */
    public LocalTunnelServer(Agent vpn, IOStreamConnectorListener txListener, IOStreamConnectorListener rxListener,
                                 TunnelConfiguration listeningSocketConfiguration) {
        this.vpn = vpn;
        this.listeningSocketConfiguration = listeningSocketConfiguration;
        this.txListener = txListener;
        this.rxListener = rxListener;
        listeners = new Vector();
        this.activeTunnels = new Vector();
        dataLastTransferred = System.currentTimeMillis();
    }

    /**
     * Add a listener
     * 
     * @param listener
     */
    public void addListener(LocalTunnelServerListener listener) {
        if (listener != null)
            listeners.addElement(listener);
    }
    
    /**
     * Get the number of tunnels that are currently active on this listener.
     * 
     * @return active tunnel count
     */
    public int getActiveTunnelCount() {
        return activeTunnels.size();
    }
    
    /**
     * Get the total number of tunnels that have ever been connected to
     * this listener.
     * 
     * @return total tunnel count
     */
    public int getTotalTunnelCount() {
        return totalTunnels;
    }

    /**
     * Get the tunnel that was used to configured this listener.
     * 
     * @return tunnel
     */
    public TunnelConfiguration getTunnel() {
        return listeningSocketConfiguration;
    }

    /**
     * Get the resource ID of the tunnel. This will not be known until the
     * tunnel has been started.
     * 
     * @return resource ID
     */
    public int getId() {
        return listeningSocketConfiguration.getId();
    }

    /**
     * Get the port on which the listener is running. This should be the
     * same as the source port specified in the {@link TunnelConfiguration} provided during
     * construction.
     * 
     * @return local port
     */
    public int getLocalPort() {
        return (server == null) ? (datagramSocket == null ? -1 : datagramSocket.getLocalPort()) : server.getLocalPort();
    }

    /**
     * Get if this listener is currently accepting connections. Note, it may
     * be possible for a listener <b>not</b> to be listening but to still
     * be running ({@link #isRunning()}. This may happen while the listener
     * is shutting down.
     * 
     * @return listening
     */
    public boolean isListening() {
        return listening;
    }

    /**
     * Get the time (in MS since Jan. 1 1970) data was last transferred over
     * this listener.
     * 
     * @return time (in MS since Jan. 1 1970) data was last transferred over
     * this listener.
     */
    public long getDataLastTransferredTime() {
        return dataLastTransferred;
    }

    /**
     * Get if this listener is currently running. Note, it may
     * be possible for a listener to be running but to <b>not</b>
     * be listenign ({@link #isListening()}. This may happen while the listener
     * is shutting down.
     *  
     * @return running
     */
    public boolean isRunning() {
        return (thread != null) && thread.isAlive();
    }

    /**
     * Get the ticket assigned to this listener by the OpenVPNALS server.
     * This will be <code>null</code> until the listener has been started.
     *  
     * @return ticket
     */
    public String getTicket() {
        return ticket;
    }

    /**
     * Start listening for incoming connections to this listener. When
     * successful, this method will return immediately. 

     * @throws IOException
     */
    public void start() throws IOException {
    	if(stopping) {
    		throw new IOException("Local forwarding is currently stopping.");
    	}
    	if(isListening()) {
    		throw new IOException("Local forwarding is already listening.");
    	}
    	
        dataLastTransferred = System.currentTimeMillis();

        // #ifdef DEBUG
        if(listeningSocketConfiguration.isPermanent()) {
            log.info("Starting permanent listening socket on port " + listeningSocketConfiguration.getSourcePort()); //$NON-NLS-1$
        }
        else {
            log.info("Starting temporary listening socket on port " + listeningSocketConfiguration.getSourcePort()); //$NON-NLS-1$            
        }
        // #endif

        /* Bind server socket */
        if (listeningSocketConfiguration.getTransport().equals(TunnelConfiguration.UDP_TUNNEL)) {
            // #ifdef DEBUG
            log.info("Creating UDP server socket on port " + listeningSocketConfiguration.getSourcePort()); //$NON-NLS-1$
            // #endif
            datagramSocket = new DatagramSocket(listeningSocketConfiguration.getSourcePort());
        } else {
            // #ifdef DEBUG
        	if(listeningSocketConfiguration.getSourcePort() == 0)
        		log.info("Creating TCP server socket random port") ; //$NON-NLS-1$
        	else
        		log.info("Creating TCP server socket on port " + listeningSocketConfiguration.getSourcePort()) ; //$NON-NLS-1$
            // #endif
    		/* If the specified port is 0 then ServerSocket will select the
    		 * next free port. We then need to store the port actually used
    		 * back into the configuration so application launching can
    		 * work.
    		 */
    		boolean resetPort = listeningSocketConfiguration.getSourcePort() == 0;
            server = new ServerSocket(listeningSocketConfiguration.getSourcePort(), 50, InetAddress.getByName(getAddressToBind()));
            if(resetPort) {
                // #ifdef DEBUG
        		log.info("Chosen port " + server.getLocalPort()) ; //$NON-NLS-1$
                // #endif
            	listeningSocketConfiguration.setSourcePort(server.getLocalPort());
            }
        }

        fireLocalTunnelServerStarted();
        thread = new Thread(new Runnable() {
        	public void run() {
        		tunnelTCP();
        	}
        });
        thread.setDaemon(true);
        thread.setName("SocketListener " + getAddressToBind() + ":" + String.valueOf(listeningSocketConfiguration.getSourcePort())); //$NON-NLS-1$ //$NON-NLS-2$
        thread.start();
    }
    
    /**
     * Get if this listener is currently stopping
     * 
     * @return stopping
     */
    public boolean isStopping() {
    	return stopping;
    }

    /**
     * Stop accepting connections to this listener. All current connections
     * will be severed.
     * <p>
     * When this method exist, the listener will no longer be listening,
     * ({@link #isListening()})  but may still be running ({@link #isRunning()}).
     */
    public void stop() {
        try {
            stopping = true;

            // #ifdef DEBUG
            if(listeningSocketConfiguration.isPermanent()) {
                log.info("Stopping permanent listening socket on port " + listeningSocketConfiguration.getSourcePort()); //$NON-NLS-1$
            }
            else {
                log.info("Stopping temporary listening socket on port " + listeningSocketConfiguration.getSourcePort()); //$NON-NLS-1$            
            }
            // #endif

            /* Stop all of the tunnels */
            /******
             * LDP - Why close all the tunnels??? This will kill connections open that are active. 
             */
//            for (Enumeration e = new Vector(activeTunnels).elements(); e.hasMoreElements();) {
//                ((LocalTunnelConnection) e.nextElement()).stop();
//            }

            /* Close the server socket to prevent new connections */
            if (server != null) {
                // #ifdef DEBUG
            	log.info("Closing server socket on port " + server.getLocalPort());
            	// #endif 
                server.close();
            }
        } catch (IOException ioe) {
        }
        
        server = null;
        thread = null;
        listening = false;
        fireLocalTunnelServerStopped();
        stopping = false;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
         tunnelTCP();
    }

    private Channel openChannel(TunnelConfiguration conf) throws IOException {
    	
    	try {
			LocalForwardingChannel channel = new LocalForwardingChannel(conf.getDestinationHost(), conf.getDestinationPort());			
			vpn.getConnection().openChannel(channel);
			return channel;
		} catch (Exception e) {
			throw new IOException("Failed to open direct-tcpip channel to " + conf.getDestinationHost() + ":" + conf.getDestinationPort());
		}
    }

    void tunnelTCP() {
    	stopping = false;
        Socket socket = null;
        try {

            listening = true;

            while (listening) {
                try {
                    socket = server.accept();
                    if (!listening || (socket == null)) {
                        break;
                    }
    
                    try {
                        // Open an SSL tunnel and connect the socket to the tunnel
                        LocalTunnelConnection vpntunnel = new LocalTunnelConnection(this, openChannel(listeningSocketConfiguration), socket, getTunnel(), txListener, rxListener);    
                        vpntunnel.addListener(this); 
                        vpntunnel.start();
    
                    } catch (Throwable ex) {
                        // #ifdef DEBUG
                        log.info(Messages.getString("LocalTunnelConnectionListener.failedToConnectTunnelingRequest"), ex); //$NON-NLS-1$
                        // #endif
                        try {
                            socket.close();
                        } catch (IOException ioe) {
                        }
                        
                        if (listeningSocketConfiguration.isTemporarySingleConnect()) {
                        	throw ex;
                        }
                    }
    
                    if(listeningSocketConfiguration.isTemporarySingleConnect()) {
                        // #ifdef DEBUG
                        log.info(Messages.getString("LocalTunnelConnectionListener.notAcceptingMoreAsTemp")); //$NON-NLS-1$
                        // #endif
                        break;
                    }
                } catch (IOException ioe) {
                    // #ifdef DEBUG
                    log.info(Messages.getString("LocalTunnelConnectionListener.failedToConnectTunnelingRequest"), ioe); //$NON-NLS-1$
                    // #endif
                }
            }
        } catch (Throwable ex) {
            if (!stopping) {
                // #ifdef DEBUG
                log.info(Messages.getString("LocalTunnelConnectionListener.connectionListenerThreadFailed"), ex); //$NON-NLS-1$
                // #endif
                stop();
            }
        } 
    }
    
    void fireLocalTunnelServerStopped() {
    	for(Enumeration e = listeners.elements(); e.hasMoreElements(); ) {
    		((LocalTunnelServerListener)e.nextElement()).localTunnelStopped(this);
    	}
    }
    
    void fireLocalTunnelServerStarted() {
    	for(Enumeration e = listeners.elements(); e.hasMoreElements(); ) {
    		((LocalTunnelServerListener)e.nextElement()).localTunnelServerStarted(this);
    	}
    }
    
    void fireLocalTunnelDataTransferred(byte[] buf, int count, boolean sent) {
    	for(Enumeration e = listeners.elements(); e.hasMoreElements(); ) {
    		((LocalTunnelServerListener)e.nextElement()).localTunnelDataTransferred(this, buf, count, sent);
    	}
    }
    
    void fireActiveTunnelStarted(LocalTunnelConnection activeTunnel) {
    	for(Enumeration e = listeners.elements(); e.hasMoreElements(); ) {
    		((LocalTunnelServerListener)e.nextElement()).localTunnelConnectionStarted(this, activeTunnel);
    	}
    }
    
    void fireActiveTunnelDataTransferred(LocalTunnelConnection activeTunnel, byte[] buf, int count, boolean sent) {
    	for(Enumeration e = listeners.elements(); e.hasMoreElements(); ) {
    		((LocalTunnelServerListener)e.nextElement()).localTunnelConnectionDataTransferred(this, activeTunnel, buf, count, sent);
    	}
    }
    
    void fireActiveTunnelStopped(LocalTunnelConnection activeTunnel) {
    	for(Enumeration e = listeners.elements(); e.hasMoreElements(); ) {
    		((LocalTunnelServerListener)e.nextElement()).localTunnelConnectionStopped(this, activeTunnel);
    	}
    }

    String getAddressToBind() {
    	if(listeningSocketConfiguration.getSourceInterface() != null &&
    			!listeningSocketConfiguration.getSourceInterface().equals("")) {  //$NON-NLS-1$
    		return listeningSocketConfiguration.getSourceInterface();
    	}
    	else {
    		return "0.0.0.0";
    	}
    }
        
    public void localTunnelConnectionStarted(LocalTunnelConnection tunnel) {
        synchronized (activeTunnels) {
            totalTunnels++;
            activeTunnels.addElement(tunnel);
            fireActiveTunnelStarted(tunnel);
        }
    }

    public void localTunnelConnectionStopped(LocalTunnelConnection tunnel) {
        synchronized (activeTunnels) {
        	if(!stopping)
        		activeTunnels.removeElement(tunnel);
            fireActiveTunnelStopped(tunnel); 
        }
    }

    public void localTunnelConnectionDataTransferred(LocalTunnelConnection tunnel, byte[] buffer, int count, boolean sent) {
        dataLastTransferred = System.currentTimeMillis();
        fireActiveTunnelDataTransferred(tunnel, buffer, count, sent);
        fireLocalTunnelDataTransferred(buffer, count, sent);
    }
}
