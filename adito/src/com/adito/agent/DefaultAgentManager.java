package com.adito.agent;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.multiplex.Request;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;
import com.adito.core.CoreException;
import com.adito.policyframework.LaunchSession;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.util.TicketGenerator;

/**
 * Manages <i>Agent</i> instances.
 */
public class DefaultAgentManager {

	static Log log = LogFactory.getLog(DefaultAgentManager.class);

	static DefaultAgentManager instance = null;

	static final String AGENT_ID = "agentId";
	static final String SESSION_INFO = "sessionInfo";
	
	// Private instance variables
	private HashMap<String, AgentTunnel> agentsByAgentId = new HashMap<String, AgentTunnel>();
    private HashMap<String, AgentTunnel> agentsByLogonTicket = new HashMap<String, AgentTunnel>();
	private HashMap<Class, AgentService> agentServices = new HashMap<Class, AgentService>();
	private ResourceRequestHandler resourceRequestHandler = new ResourceRequestHandler();
	private ThreadPoolExecutor agentBroadcastExecutor;

	private DefaultAgentManager() {
		agentBroadcastExecutor = new ThreadPoolExecutor(1, 5, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000));
	}
	
	/**
	 * Get an instance of the agent manager.
	 * 
	 * @return agent manager
	 */
	public static DefaultAgentManager getInstance() {
		return instance == null ? instance = new DefaultAgentManager() : instance;
	}
	
	/**
	 * Get all registered services.
	 * 
	 * @return all registered services
	 */
	public Collection<AgentService> getServices() {
		return agentServices.values();
	}
	
	/**
	 * Get a service given its class.
	 * 
	 * @param cls class
	 * @return server
	 */
	public AgentService getService(Class<?> cls) {
		return agentServices.get(cls);
	}
	
	/**
	 * Get an thread pool executor to use for broadcasting messages
	 * to all Agents. 
	 * 
	 * @return agent broadcast executor
	 */
	public ThreadPoolExecutor getAgentBroadcastExecutor() {
		return agentBroadcastExecutor;
	}
	
	/**
	 * Register an AgentService implementation with the agent.
	 * @param cls
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public void registerService(Class<?> cls) throws InstantiationException, IllegalAccessException {
		agentServices.put(cls, (AgentService)cls.newInstance());
	}
	
	/**
	 * Unregister an AgentService implementation on the agent.
	 * @param cls
	 */
	public void unregisterService(Class<?> cls) {
		agentServices.remove(cls);
	}

	/**
	 * Register a new agent.
	 * 
	 * @param ticket unique ticket
	 * @param session session
	 * @param tunnel agent
	 * @throws AgentException
	 */
	public void registerAgent(String ticket, SessionInfo session, AgentTunnel tunnel) throws AgentException {

		LogonControllerFactory.getInstance().removeAuthorizationTicket(ticket);
		
		if (session != null) {
			
			
			synchronized (session) {
				agentsByAgentId.put(ticket, tunnel);
				agentsByLogonTicket.put(session.getLogonTicket(), tunnel);
				tunnel.addListener(new DefaultAgentStartupListener(session));
				tunnel.registerRequestHandler("getResources", resourceRequestHandler);
				
				// Make sure all services are initialised before agent begins
				if (tunnel != null) {
					for(AgentService service : agentServices.values()) {
						service.initializeTunnel(tunnel);
					}
				}
			}
		} else {
			tunnel.close();
			throw new AgentException("");
		}
	}

	

	/**
	 * Inform the agent to open the specified URL with its local default browser
	 * 
	 * @param agent agent
	 * @param url URL to open
	 * @param launchSession launch session
	 * @return return code
	 * @throws CoreException on any error
	 */
	public int openURL(AgentTunnel agent, URL url, LaunchSession launchSession) throws CoreException {

		try {
			ByteArrayWriter msg = new ByteArrayWriter();
			msg.writeString(url.toExternalForm());
			msg.writeString(launchSession.getId());
			Request request = new Request("openURL", msg.toByteArray());
			if (agent.sendRequest(request, true)) {
				if(request.getRequestData()!=null) {
				ByteArrayReader rdr = new ByteArrayReader(request.getRequestData());
				return (int) rdr.readInt();
				}
			}

		} catch (Exception e) {
			throw new CoreException(0, "", e);
		}
		return -1;
	}

	/**
	 * Unregister an agent. If the agent is running it will be shutdown.
	 * 
	 * @param tunnel
	 */
	public void unregisterAgent(AgentTunnel tunnel) {

		if (tunnel != null) {
			
			if (log.isDebugEnabled())
				log.debug("Unregistering agent with id " + tunnel.getId());

			agentsByAgentId.remove(tunnel.getId());
			agentsByLogonTicket.remove(tunnel.getSession().getLogonTicket());

			if (tunnel.isRunning()) {
				try {
					tunnel.sendRequest(new Request("shutdown", "".getBytes()), false, 10000);
				} catch (IOException e) {
					log.error("Failed to send shutdown request to agent. The agent may not be responding or the network link may now be down.",
						e);
				}
				tunnel.close();
			}
 
			if(tunnel.getSession().getHttpSession() == null) {
				log.info("Non UI session so cleaning up session.");			
				LogonControllerFactory.getInstance().logoff(tunnel.getSession().getLogonTicket());
			}
		}
	}

	/**
	 * Unregister an agent given the session it attached to. If the agent is
	 * running it will be shutdown.
	 * 
	 * @param session session
	 */
	public void unregisterAgent(SessionInfo session) {
		unregisterAgent((AgentTunnel) agentsByLogonTicket.get(session.getLogonTicket()));
	}
	
	/**
	 * Get all active agents.
	 * 
	 * @return all agents
	 */
	public Collection<AgentTunnel> getAgents() {
		return Collections.synchronizedCollection(agentsByAgentId.values());
	}

	/**
	 * Get the agent attached to the specified session
	 * 
	 * @param session session
	 * @return agent
	 */
	public AgentTunnel getAgentBySession(SessionInfo session) {
		if (session == null)
			return null;
		return (AgentTunnel) agentsByLogonTicket.get(session.getLogonTicket());
	}

	/**
	 * Get the session attached to the agent with the provided unique ticket.
	 * 
	 * @param ticket unique ticket of agent
	 * @return session agent is attached to
	 */
	public SessionInfo getSessionByAgentId(String ticket) {
		if (ticket == null)
			return null;

		return (SessionInfo) LogonControllerFactory.getInstance().getAuthorizationTicket(ticket);
	}

	/**
	 * Create a new unique 'pending' ticket for agent authentication and
	 * register it with the logon controller. The returned ticket should be
	 * passed to the agent when it starts up.
	 * 
	 * @param session session
	 * @return pending agent ticket
	 */
	public String registerPendingAgent(SessionInfo session) {

		String ticket = TicketGenerator.getInstance().generateUniqueTicket("AGENT");
		LogonControllerFactory.getInstance().registerAuthorizationTicket(ticket, session);
		return ticket;

	}

	/**
	 * Get if the provided session has an active agent attached to it.
	 * 
	 * @param session session
	 * @return agent attached to session
	 */
	public boolean hasActiveAgent(SessionInfo session) {
		return session != null && agentsByLogonTicket.containsKey(session.getLogonTicket());
	}

	/**
	 * Convenience method to get if the session of the provided request has an
	 * active agent attached to it.
	 * 
	 * @param request request
	 * @return agent attached to session
	 */
	public boolean hasActiveAgent(HttpServletRequest request) {
		SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
		if (session != null)
			return hasActiveAgent(session);
		else
			return false;
	}

	

	/**
	 * Wait for agent registration and synchronization. When this method exits
	 * the agent should be in a fully active state.
	 * <p>
	 * A timeout value (milliseconds) must be provided. If the process has not
	 * completed by this time the method with exit with <code>false</code>.
	 * Note, its is possible the agent may start after this timeout (e.g. if the
	 * client is running on very slow hardware and / or network or if the
	 * timeout value is too low).
	 * 
	 * @param ticket agents unique ticket
	 * @param timeout time in milliseconds to wait for registration and
	 *        synchronization.
	 * @return boolean indicating whether agent is running or not
	 */
	public boolean waitForRegistrationAndSynchronization(String ticket, int timeout) {
		SessionInfo session = getSessionByAgentId(ticket);
		synchronized (session) {

			// Has the agent already registered?
			if (agentsByAgentId.containsKey(ticket))
				return true;

			long left = timeout;
			try {
				while (left >= 0) {
					if (agentsByAgentId.containsKey(ticket)) {
						AgentTunnel agent = agentsByAgentId.get(ticket);
						if (log.isInfoEnabled()) {
							log.info("Client registered, waiting for client synchronized request");
						}
						if(agent.waitForSync(500)) {
							return true;
						}
					}
					session.wait(500);
					left = left - 500;
				}
			} catch (InterruptedException e) {
			}
		}
		return false;
	}

	/**
	 * Start all services for the supplied agent connection.
	 * 
	 * @param tunnel agent connection
	 */
	public void startServices(AgentTunnel tunnel) {
		if (log.isDebugEnabled()) {
			log.debug("Starting agent services for tunnel");
		}
		for(AgentService service : getServices()) {
			service.performStartup(tunnel);
		}			
		if (log.isDebugEnabled()) {
			log.debug("Started agent services for tunnel");
		}
	}
}
