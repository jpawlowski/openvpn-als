package net.openvpn.als.agent.client.tunneling;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;

import com.maverick.multiplex.MultiplexedConnection;
import com.maverick.multiplex.Request;
import com.maverick.multiplex.RequestHandler;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;
import net.openvpn.als.agent.client.AbstractResourceManager;
import net.openvpn.als.agent.client.Agent;
import net.openvpn.als.agent.client.PortMonitor;
import net.openvpn.als.agent.client.util.TunnelConfiguration;

/**
 * This class manages tunnels for the OpenVPNALS agent. Using the multiplexed
 * protocol it will respond to requests from the server to start and stop tunnel
 * listeners.
 * 
 * @author Lee David Painter
 * 
 */
public class TunnelManager extends AbstractResourceManager implements RequestHandler, LocalTunnelServerListener {

	// #ifdef DEBUG
	static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(TunnelManager.class);
	// #endif
	
	/**
	 * Tunnel resource type ID
	 */
	public final static int TUNNEL_RESOURCE_TYPE_ID = 4;

	/**
	 * Sent by server to start a list of local tunnels. Payload consists of
	 * tunnel resource details including resource ID.
	 */
	public static final String START_LOCAL_TUNNEL = "startLocalTunnel"; //$NON-NLS-1$

	/**
	 * Sent by server to stop a single local tunnel. Payload consists of
	 * resourceID
	 */
	public static final String STOP_LOCAL_TUNNEL = "stopLocalTunnel"; //$NON-NLS-1$

	/**
	 * Notification request that a remote tunnel has started.
	 */
	public static final String START_REMOTE_TUNNEL = "startRemoteTunnel";
	
	/**
	 * Notification request that a remote tunnel has stopped. 
	 */
	public static final String STOP_REMOTE_TUNNEL = "stopRemoteTunnel";
	
	/**
	 * Sent by server to request a list of all active local tunnels. Payload to
	 * be sent back to server consists of all active resource IDs
	 */
	public static final String ACTIVE_LOCAL_TUNNELS = "activeLocalTunnels"; //$NON-NLS-1$

	/**
	 * Sent by client to request the server to close a local tunnel. The server
	 * then sends a {@link STOP_LOCAL_FORWARDING} message back to the client
	 */
	public static final String CLOSE_LOCAL_TUNNEL = "closeLocalTunnel"; //$NON-NLS-1$

	/**
	 * Sent by client to request that a tunnel is launched. The server should
	 * then configure a launch session. If the tunnel is local, the server
	 * should reply in the same way of {@link #START_LOCAL_TUNNEL} (in which
	 * case the agent then configures and starts the local tunnel). If the
	 * tunnel is remote, no reply will be sent.
	 */
	public static final String SETUP_AND_LAUNCH_TUNNEL = "setupAndLaunchTunnel"; //$NON-NLS-1$

	// Private statics

	private static int temporaryTunnelId = -1;

	// Protected instance variables

	/** The collection of active socket listeners * */
	protected Hashtable activeLocalTunnels = new Hashtable();

	/**
	 * Constructor.
	 * 
	 * @param agent
	 */
	public TunnelManager(Agent agent) {
		super(agent);
		agent.getConnection().registerRequestHandler(START_LOCAL_TUNNEL, this);
		agent.getConnection().registerRequestHandler(STOP_LOCAL_TUNNEL, this);
		agent.getConnection().registerRequestHandler(START_REMOTE_TUNNEL, this);
		agent.getConnection().registerRequestHandler(STOP_REMOTE_TUNNEL, this);
		agent.getConnection().registerRequestHandler(ACTIVE_LOCAL_TUNNELS, this);
	}

	/**
	 * Retrieve all tunnel resources and add them to the GUI.
	 */
	public void getTunnelResources() {
		super.getResources(TUNNEL_RESOURCE_TYPE_ID, "Tunnels");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.agent.client.AbstractResourceManager#launchResource(int)
	 */
	public void launchResource(int resourceId) {
		try {
			ByteArrayWriter baw = new ByteArrayWriter();
			baw.writeInt(resourceId);
			Request request = new Request(SETUP_AND_LAUNCH_TUNNEL, baw.toByteArray());
			if (agent.getConnection().sendRequest(request, true)) {
				// #ifdef DEBUG
				log.debug("Tunnel launch setup");
				// #endif
				processLaunchRequest(request);
			} else {
				// #ifdef DEBUG
				log.error("Failed to setup and launch tunnel launch");
				// #endif
			}
		} catch (IOException e) {
			// #ifdef DEBUG
			log.error("Failed to setup and launch tunnel launch", e);
			// #endif
		}
	}

	/**
	 * Request that the server closes a local tunnel
	 * 
	 * @param id
	 */
	public void closeLocalTunnel(int id) {
		try {
			ByteArrayWriter baw = new ByteArrayWriter();
			baw.writeInt(id);
			Request request = new Request(CLOSE_LOCAL_TUNNEL, baw.toByteArray());
			agent.getConnection().sendRequest(request, false);
		} catch (IOException e) {
			// #ifdef DEBUG
			log.error("Failed to setup and launch tunnel launch", e);
			// #endif
		}

	}

	/**
	 * Get a map of all active {@link LocalTunnelServerListener} keyed by
	 * resource ID.
	 * 
	 * @return active local tunnels
	 */
	public Hashtable getActiveLocalTunnels() {
		return activeLocalTunnels;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.maverick.multiplex.RequestHandler#processRequest(com.maverick.multiplex.Request,
	 *      com.maverick.multiplex.MultiplexedConnection)
	 */
	public boolean processRequest(Request request, MultiplexedConnection con) {
		// #ifdef DEBUG
		log.info("Processing request " + request.getRequestName());
		// #endif
		if (request.getRequestName().equals(START_LOCAL_TUNNEL)) {
			return startLocalTunnel(request.getRequestData());
		} else if (request.getRequestName().equals(STOP_LOCAL_TUNNEL)) {
			stopLocalTunnel(request);
			return true;
		} else if (request.getRequestName().equals(ACTIVE_LOCAL_TUNNELS)) {

			try {
				Hashtable l = getActiveLocalTunnels();

				ByteArrayWriter msg = new ByteArrayWriter();
				msg.writeInt(l.size());
				for (Enumeration e = l.keys(); e.hasMoreElements();) {
					msg.writeInt(((Integer) e.nextElement()).intValue());
				}

				request.setRequestData(msg.toByteArray());
				return true;
			} catch (IOException e) {
				return false;
			}

		} else if((request.getRequestName().equals(START_REMOTE_TUNNEL) 
				|| request.getRequestName().equals(STOP_REMOTE_TUNNEL)) 
				&& request.getRequestData()!=null) {
			

//			if (portItem.getActiveTunnelCount() == 0) {
//				portMonitor.removeItemAt(idx);
//			} else {
			
			try {
				ByteArrayReader bar = new ByteArrayReader(request.getRequestData());
				int resourceId = (int)bar.readInt();
				String resourceName = bar.readString();
				String launchId = bar.readString();
				String listeningInterface = bar.readString();
				int listeningPort = (int) bar.readInt();
				String destinationHost = bar.readString();
				int destinationPort = (int) bar.readInt();
				
				/**
				 * This code should be called after this method but from the agent
				 * class.
				 */
				
				if (agent.getConfiguration().isDisplayInformationPopups()) {
					agent.getGUI()
						.popup(null,
							MessageFormat.format(Messages.getString(
									request.getRequestName().equals(START_REMOTE_TUNNEL) ? "TunnelManager.openedRemoteTunnel" : "TunnelManager.closingRemoteTunnel"), new Object[] { 
								    listeningInterface, String.valueOf(listeningPort), //$NON-NLS-1$$ 
									destinationHost + ":" + destinationPort }), 
							Messages.getString("Agent.title"), //$NON-NLS-1$
							"popup-tunnel", -1); //$NON-NLS-1$
				}
				agent.updateInformation();
				
				/*
				 * Update the port monitor to show a remote tunnel with
				 * no active connections when starting or remove it when
				 * stopping
				 */
				PortMonitor portMonitor = agent.getGUI().getPortMonitor();
				if(request.getRequestName().equals(START_REMOTE_TUNNEL)) {
					TunnelConfiguration conf = new DefaultTunnel(
							resourceId, TunnelConfiguration.REMOTE_TUNNEL, TunnelConfiguration.TCP_TUNNEL, listeningInterface, listeningPort, destinationPort, destinationHost, true, false, resourceName, launchId);
					RemotePortItem portItem = new RemotePortItem(conf);
					portMonitor.addPortItem(portItem);
				}
				else {
					int idx = portMonitor.getIndexForId(resourceId);
					if(idx != -1) {
						portMonitor.removeItemAt(idx);
					}
				}
				
				return true;
			} catch (IOException e) {
				// #ifdef DEBUG
				log.error("Failed to process remote tunnel request", e);
				// #endif
			}
			
		} 

		return false;
	}

	/* (non-Javadoc)
	 * @see com.maverick.multiplex.RequestHandler#postReply(com.maverick.multiplex.MultiplexedConnection)
	 */
	public void postReply(MultiplexedConnection connection) {		
	}

	/**
	 * Configure and start a temporary local tunnel.
	 * 
	 * @param name name
	 * @param hostToConnect host to connect to
	 * @param sourceInterface source interface
	 * @param portToConnect port to connect to
	 * @param usePreferredPort use the preferred port
	 * @param singleConnection single connection (tunnel closes when done)
	 * @param launchId launch ID
	 * @return local tunnel
	 * @throws IOException
	 */
	public LocalTunnelServer startTemporaryLocalTunnel(String name, String hostToConnect, String sourceInterface,
															int portToConnect, boolean usePreferredPort, boolean singleConnection,
															String launchId) throws IOException {
			DefaultTunnel t = new DefaultTunnel(getTemporaryTunnelId(),
							TunnelConfiguration.LOCAL_TUNNEL,
							TunnelConfiguration.TCP_TUNNEL,
							sourceInterface,
							(usePreferredPort ? portToConnect : 0),
							portToConnect,
							hostToConnect,
							false,
							singleConnection,
							name,
							launchId);

			return startLocalTunnel(t);
	}

	/**
	 * Start a local tunnel given its configuration.
	 * 
	 * @param conf configuration
	 * @return tunnel
	 * @throws IOException if tunnel cannot be started
	 */
	public LocalTunnelServer startLocalTunnel(TunnelConfiguration conf) throws IOException {

		if (conf.getType() != TunnelConfiguration.LOCAL_TUNNEL)
			throw new IOException("Invalid tunnel type " + conf.getType()); //$NON-NLS-1$

		LocalTunnelServer listener = new LocalTunnelServer(agent, agent.getTXIOListener(), agent.getRXIOListener(), conf);
		listener.addListener(this);
		listener.start();
		return listener;
	}

	/**
	 * Send a request to the server to stop the tunnel. This will in turn send a
	 * request back to this agent to stop the listening socket.
	 * 
	 * @param id
	 */
	public void stopLocalTunnel(int id) {
		stopLocalTunnel(new Integer(id), true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.agent.client.tunneling.LocalForwardingServerListener#activeTunnelDataTransferred(net.openvpn.als.agent.client.tunneling.LocalForwardingServer,
	 *      net.openvpn.als.agent.client.tunneling.ActiveTunnel, byte[], int,
	 *      boolean)
	 */
	public void localTunnelConnectionDataTransferred(LocalTunnelServer localForwardingServer, LocalTunnelConnection activeTunnel, byte[] buffer,
											int count, boolean sent) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.agent.client.tunneling.LocalForwardingServerListener#activeTunnelStarted(net.openvpn.als.agent.client.tunneling.LocalForwardingServer,
	 *      net.openvpn.als.agent.client.tunneling.ActiveTunnel)
	 */
	public void localTunnelConnectionStarted(LocalTunnelServer localForwardingServer, LocalTunnelConnection activeTunnel) {
		// Update port monitor
		PortMonitor portMonitor = agent.getGUI().getPortMonitor();
		synchronized (portMonitor) {
			int idx = portMonitor.getIndexForId(localForwardingServer.getId());
			AbstractPortItem item = portMonitor.getItemAt(idx);
			item.increaseActive();
			portMonitor.updateItemAt(idx);
		}
		agent.updateInformation();
		// #ifdef DEBUG
		TunnelManager.log.info("Tunnel has been opened on " + activeTunnel.getClientHost() //$NON-NLS-1$
			+ " to " + localForwardingServer.getTunnel().getDestinationHost() //$NON-NLS-1$
			+ ":" + localForwardingServer.getTunnel().getDestinationPort()); //$NON-NLS-1$
		// #endif
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.agent.client.tunneling.LocalForwardingServerListener#activeTunnelStopped(net.openvpn.als.agent.client.tunneling.LocalForwardingServer,
	 *      net.openvpn.als.agent.client.tunneling.ActiveTunnel)
	 */
	public void localTunnelConnectionStopped(LocalTunnelServer localForwardingServer, LocalTunnelConnection activeTunnel) {

		// Update port monitor
		PortMonitor portMonitor = agent.getGUI().getPortMonitor();
		synchronized (portMonitor) {
			int idx = portMonitor.getIndexForId(localForwardingServer.getId());
			if (idx != -1) {
				AbstractPortItem item = portMonitor.getItemAt(idx);
				item.decreaseActive();
				portMonitor.updateItemAt(idx);
			}
		}

		agent.updateInformation();

		// #ifdef DEBUG
		TunnelManager.log.info("Tunnel has been closed on " + activeTunnel.getClientHost() //$NON-NLS-1$
			+ " to " + localForwardingServer.getTunnel().getDestinationHost() //$NON-NLS-1$
			+ ":" + localForwardingServer.getTunnel().getDestinationPort() + " ( single connect = " + localForwardingServer.getTunnel().isTemporarySingleConnect()  + ", permanent = " + localForwardingServer.getTunnel().isPermanent() + " )"); //$NON-NLS-1$               
		// #endif

		if (!localForwardingServer.isStopping() && localForwardingServer.getTunnel().isTemporarySingleConnect()) {
			// #ifdef DEBUG
			TunnelManager.log.info("Closing listening temporary listening socket"); //$NON-NLS-1$
			// #endif
			localForwardingServer.stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.agent.client.tunneling.LocalForwardingServerListener#localForwardingDataTransferred(net.openvpn.als.agent.client.tunneling.LocalForwardingServer,
	 *      byte[], int, boolean)
	 */
	public void localTunnelDataTransferred(LocalTunnelServer localForwardingServer, byte[] buffer, int count, boolean sent) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.agent.client.tunneling.LocalForwardingServerListener#localForwardingServerStarted(net.openvpn.als.agent.client.tunneling.LocalForwardingServer)
	 */
	public void localTunnelServerStarted(LocalTunnelServer localForwardingServer) {

		// Save the listener to our table
		activeLocalTunnels.put(new Integer(localForwardingServer.getId()), localForwardingServer);

		/**
		 * This code should be called after this method but from the agent
		 * class.
		 */
		AbstractPortItem pi = new LocalPortItem(this, localForwardingServer.getTunnel(), localForwardingServer);
		agent.getGUI().getPortMonitor().addPortItem(pi);
		if (agent.getConfiguration().isDisplayInformationPopups()) {
			agent.getGUI()
							.popup(null,
								MessageFormat.format(Messages.getString("TunnelManager.openedLocalTunnel"), new Object[] { localForwardingServer.getTunnel().getName(), String.valueOf(localForwardingServer.getTunnel().getSourcePort()), //$NON-NLS-1$$ 
										localForwardingServer.getTunnel().getDestinationHost() + ":" + localForwardingServer.getTunnel().getDestinationPort() }), //$NON-NLS-1$$
								Messages.getString("Agent.title"), //$NON-NLS-1$
								"popup-tunnel", -1); //$NON-NLS-1$
		}
		agent.updateInformation();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.agent.client.tunneling.LocalForwardingServerListener#localForwardingStopped(net.openvpn.als.agent.client.tunneling.LocalForwardingServer)
	 */
	public void localTunnelStopped(LocalTunnelServer localForwardingServer) {
		activeLocalTunnels.remove(new Integer(localForwardingServer.getId()));
		synchronized (agent.getGUI().getPortMonitor()) {
			int idx = agent.getGUI().getPortMonitor().getIndexForId(localForwardingServer.getId());
			if (idx != -1) {
				agent.getGUI().getPortMonitor().removeItemAt(idx);
			}
		}
		TunnelConfiguration listeningSocketConfiguration = localForwardingServer.getTunnel();
		if (agent.getConfiguration().isDisplayInformationPopups()) {
			agent.getGUI()
							.popup(null,
								MessageFormat.format(Messages.getString("TunnelManager.closingLocalTunnel"), new Object[] { listeningSocketConfiguration.getName(),//$NON-NLS-1$  
										String.valueOf(listeningSocketConfiguration.getSourcePort()),
										listeningSocketConfiguration.getDestinationHost() + ":" + listeningSocketConfiguration.getDestinationPort() }), //$NON-NLS-1$ 
								Messages.getString("Agent.title"), //$NON-NLS-1$
								"popup-tunnel", -1); //$NON-NLS-1$
		}
		agent.updateInformation();
	}

	// Supporting methods

	synchronized int getTemporaryTunnelId() {
		return temporaryTunnelId--;
	}

	void processLaunchRequest(Request request) throws IOException {
		/*
		 * If there is no returned request data, then the launched tunnnel was
		 * remote and we do not need to do any more
		 */
		if (request.getRequestData() == null) {
			// #ifdef DEBUG
			log.info("No request data returned, assuming launch was a remote tunnel");
			// #endif
			return;
		}
		startLocalTunnel(request.getRequestData());
	}

	void stopLocalTunnel(Request request) {

		if (request.getRequestData() == null)
			return;
		try {
			ByteArrayReader msg = new ByteArrayReader(request.getRequestData());
			stopLocalTunnel(new Integer((int) msg.readInt()), false);
			request.setRequestData(null);

		} catch (IOException ex) {
			// #ifdef DEBUG
			log.error("Failed to read tunnel id from request", ex);
			// #endif
		}
	}

	void stopLocalTunnel(Integer id, boolean informServer) {

		// #ifdef DEBUG
		log.info("Stopping local tunnel with id" + id); //$NON-NLS-1$
		// #endif DEBUG

		LocalTunnelServer listener = (LocalTunnelServer) activeLocalTunnels.get(id);

		if (listener != null) {
			listener.stop();
		} else {
			// #ifdef DEBUG
			log.error("Request to close unknown local tunnel " + id);
			// #endif
		}

	}

	boolean startLocalTunnel(byte[] configurationData) {

		try {

			/**
			 * Start a local forwarding. This creates a listening socket on the
			 * client and will forward sockets opened through the multiplexed
			 * connection to the OpenVPNALS server.
			 */
			ByteArrayReader reply = new ByteArrayReader(configurationData);
			String launchId = reply.readString();

			int id = (int) reply.readInt();
			String name = reply.readString();
			int type = (int) reply.readInt();
			String transport = reply.readString();
			String sourceInterface = reply.readString();
			int sourcePort = (int) reply.readInt();
			int destinationPort = (int) reply.readInt();
			String destinationHost = reply.readString();

			// #ifdef DEBUG
			log.info("Received permanent tunnel named " + name + " for " + destinationHost + ":" + destinationPort);
			// #endif

			DefaultTunnel t = new DefaultTunnel(id,
							type,
							transport,
							sourceInterface,
							sourcePort,
							destinationPort,
							destinationHost,
							true,
							false,
							name,
							launchId);

			startLocalTunnel(t);
			return true;

		} catch (IOException e) {
			// #ifdef DEBUG
			log.error("Request permanent tunnels operation failed", e);
			// #endif
			return false;
		}
	}
}
