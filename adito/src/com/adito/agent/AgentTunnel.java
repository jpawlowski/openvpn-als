package com.adito.agent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.multiplex.Channel;
import com.maverick.multiplex.ChannelFactory;
import com.maverick.multiplex.Message;
import com.maverick.multiplex.MultiplexedConnection;
import com.maverick.multiplex.Request;
import com.maverick.multiplex.RequestHandler;
import com.maverick.multiplex.TimeoutCallback;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;
import com.adito.boot.RequestHandlerTunnel;
import com.adito.boot.VersionInfo;
import com.adito.properties.Property;
import com.adito.properties.impl.profile.ProfilePropertyKey;
import com.adito.properties.impl.realms.RealmKey;
import com.adito.properties.impl.systemconfig.SystemConfigKey;
import com.adito.security.SessionInfo;

/**
 * Extension of {@link MultiplexedConnection} that is used as the
 * server side controller of all agent connections. I.e. there will
 * be one instance of this class for every single active agent.
 */
public class AgentTunnel extends MultiplexedConnection implements
		RequestHandlerTunnel, TimeoutCallback {

    static Log log = LogFactory.getLog(AgentTunnel.class);

	/**
	 * Keep alive request
	 */
	public static final String KEEP_ALIVE = "keepAlive";

	/**
	 * Request agent updates it resources
	 */
	public static final String UPDATE_RESOURCES_REQUEST = "updateResources";

	/**
	 * Synchronised
	 */
	public static final String SYNCHRONIZED_REQUEST = "synchronized";

	/**
	 * Get property
	 */
	public static final String PROPERTY_REQUEST = "getProperty";

    /**
     * Default keep alive timeout
     */
    public static final int KEEP_ALIVE_TIMEOUT = 10000;
    
    // Private instance variables
	private String type;
	private String id;
	private SessionInfo session;
	private SyncHandler syncHandler = new SyncHandler();

	/**
	 * Constructor for agent tunnels that are <strong>not</strong> associated
	 * with a UI session.
	 * 
	 * @param id
	 *            agent ID
	 * @param type
	 * @param factory
	 */
	public AgentTunnel(String id, String type, ChannelFactory factory) {
		this(id, null, type, factory);
	}

	/**
	 * Constructor for agent tunnels that may or may not be associated with a UI
	 * session
	 * 
	 * @param id
	 *            agent ID
	 * @param session
	 *            UI session the tunnel is associated with or <code>null</code>
	 *            if if not associated with a user session
	 * @param type agent type
	 * @param factory channel factory
	 */
	public AgentTunnel(String id, SessionInfo session, String type,
			ChannelFactory factory) {
		super(factory);
		this.id = id;
		this.session = session;
		this.type = type;
		registerRequestHandler(AgentTunnel.SYNCHRONIZED_REQUEST, syncHandler);
		registerRequestHandler(AgentTunnel.PROPERTY_REQUEST, new PropertyRequest(this));
	}

	/**
	 * Get the agent ID (ticket)
	 * 
	 * @return agent ID (ticket)
	 */
	public String getId() {
		return id;
	}

	/**
	 * Get the UI session the agent tunnel is associated with or
	 * <code>null</code> if it is not associated with a tunnel.
	 * 
	 * @return session
	 */
	public SessionInfo getSession() {
		return session;
	}
	
	@Override
    protected void handleRequest(boolean wantReply, Request request) throws IOException {
        super.handleRequest(wantReply, request);
        if(session != null && session.isAttachedToWebSession() && !KEEP_ALIVE.equals(request.getRequestName())) {
            session.access();
        }
    }

    @Override
    protected void handleChannelMessage(Message msg, Channel channel) throws IOException {
        super.handleChannelMessage(msg, channel);
        if(session != null && session.isAttachedToWebSession()) {
            session.access();
        }
    }

    @Override
    public void sendChannelData(Channel channel, byte[] data, int off, int len) throws IOException {
        super.sendChannelData(channel, data, off, len);
        if(session != null && session.isAttachedToWebSession()) {
            session.access();
        }
    }

    @Override
    public boolean sendRequest(Request request, boolean wantReply, int timeoutMs) throws IOException {
        boolean ok = super.sendRequest(request, wantReply, timeoutMs);
        if(session != null && session.isAttachedToWebSession() && !KEEP_ALIVE.equals(request.getRequestName())) {
            session.access();
        }
        return ok;
    }

    /**
     * Get the agent type.
     * 
     * @return agent type
     */
    public String getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see com.adito.boot.RequestHandlerTunnel#tunnel(java.io.InputStream, java.io.OutputStream)
	 */
	public void tunnel(InputStream in, OutputStream out) {
		log.debug("Starting agent tunnel");
		Thread currentThread = Thread.currentThread();
		ClassLoader currentClassLoader = currentThread.getContextClassLoader();
        currentThread.setContextClassLoader(getClass().getClassLoader()); 

        try {
            setTimeoutCallback(this);
            startProtocol(in, out, false);
            log.debug("Stopping agent tunnel");
            
            // Inform all channels they are closing
            for (Channel channel : getActiveChannels()) {
                channel.onChannelClose();
            }
            
            try {
                AgentManager.getInstance().removeAgent(this);
            } catch (AgentException age) {
                log.error("Error removing agent", age);
            }
        } finally {
            // housekeeping, leave things how we found them
            currentThread.setContextClassLoader(currentClassLoader); 
        }
	}

	/**
	 * Wait for the agent to synchronise. 
	 * 
	 * @param ms timeout
	 * @return synchronisation completed ok
	 * @throws InterruptedException if synchronisation interrupted in any way
	 */
	public boolean waitForSync(long ms) throws InterruptedException {
		synchronized (syncHandler) {
			if (syncHandler.isSyncCompleted()) {
				return true;
			}
			syncHandler.wait(ms);
			return syncHandler.isSyncCompleted();
		}
	}
	
	/**
	 * {@link RequestHandler} that deals with agent synchronisation.
	 */
	public class SyncHandler implements RequestHandler {

		private boolean syncCompleted;

		/**
		 * Get if synchronisation is completed.
		 * 
		 * @return sync completed
		 */
		public boolean isSyncCompleted() {
			return syncCompleted;
		}

		/* (non-Javadoc)
		 * @see com.maverick.multiplex.RequestHandler#processRequest(com.maverick.multiplex.Request, com.maverick.multiplex.MultiplexedConnection)
		 */
		public synchronized boolean processRequest(Request request,
				MultiplexedConnection connection) {
			try {
				notifyAll();
				ByteArrayWriter baw = new ByteArrayWriter();
				try {
					baw.writeString(VersionInfo.getVersion().toString());
					request.setRequestData(baw.toByteArray());
					return true;
				} catch (IOException e) {
					log.error("Failed to send back server version.");
					return false;
				}
			} finally {
				syncCompleted = true;
			}
		}

		/* (non-Javadoc)
		 * @see com.maverick.multiplex.RequestHandler#postReply(com.maverick.multiplex.MultiplexedConnection)
		 */
		public void postReply(MultiplexedConnection connection) {
			DefaultAgentManager.getInstance().startServices(
					(AgentTunnel) connection);
		}
	}

    
    /**
     * {@link RequestHandler} that deals with requests for property values.
     */
	public class PropertyRequest implements RequestHandler {
		
		AgentTunnel tunnel;
		
		static final int PROFILE = 1;
		static final int SYSTEM = 2;
		static final int REALM = 3;
		
		PropertyRequest(AgentTunnel tunnel) {
			this.tunnel = tunnel;
		}
		
		public boolean processRequest(Request request, MultiplexedConnection connection) {
			
			try {
				ByteArrayReader reader = new ByteArrayReader(request.getRequestData());
				
				int type = (int)reader.readInt();
				String name = reader.readString();
				ByteArrayWriter baw = new ByteArrayWriter();
				
				switch(type) {
					case PROFILE:
					{
						baw.writeString(Property.getProperty(new ProfilePropertyKey(name, tunnel.getSession())));
						break;
					}
					case SYSTEM:
					{
						baw.writeString(Property.getProperty(new SystemConfigKey(name)));						
						break;
					}
					case REALM:
					{
						baw.writeString(Property.getProperty(new RealmKey(name, tunnel.getSession().getRealmId())));
					}
					default:
					{
						log.error("Agent requested invalid property type!");
						return false;
					}
				}
				
				
				request.setRequestData(baw.toByteArray());
				return true;
			
			} catch(Exception ex) {
				log.error(ex);
				return false;
			}
		}
		
		public void postReply(MultiplexedConnection connection) {
			DefaultAgentManager.getInstance().startServices(
					(AgentTunnel) connection);
		}
	}
	
	public boolean isAlive(MultiplexedConnection con) {
		/*
		 * We will schedule a keep alive packet and disconnect if that
		 * fails. We do not perform that here as it would block the multiplex
		 * thread.
		 */
		Thread t = new Thread("Keep-Alive Thread - " + session.getUser().getPrincipalName() + "," + session.getAddress().getHostAddress()) {
			public void run() {
				Request request = new Request(KEEP_ALIVE);
				if(log.isInfoEnabled())
					log.info("Socket has timed out! Sending keep-alive to agent");
				
				try {
					sendRequest(request, true, KEEP_ALIVE_TIMEOUT);
					
					if(log.isInfoEnabled())
						log.info("Agent is still alive, continue as normal");
					
				} catch (IOException e) {
					
					if(log.isInfoEnabled())
						log.info("Agent did not respond, disconnecting");
					
					disconnect("Keep-alive failed");
				}
			}
		};
		t.start();
		
		/* In the context of Adito returning true here is OK as timeouts
		 * are dealt with differently
		 */
		return true;
	}
}
