
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
			
package com.adito.tunnels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.multiplex.Channel;
import com.maverick.multiplex.MultiplexedConnection;
import com.maverick.multiplex.Request;
import com.maverick.multiplex.RequestHandler;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;
import com.adito.agent.AbstractResourceService;
import com.adito.agent.AgentService;
import com.adito.agent.AgentTunnel;
import com.adito.agent.DefaultAgentManager;
import com.adito.core.BundleActionMessage;
import com.adito.core.CoreEvent;
import com.adito.core.CoreException;
import com.adito.core.CoreServlet;
import com.adito.core.GlobalWarning;
import com.adito.core.GlobalWarningManager;
import com.adito.core.stringreplacement.VariableReplacement;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.LaunchSessionFactory;
import com.adito.policyframework.LaunchSessionManager;
import com.adito.policyframework.NoPermissionException;
import com.adito.policyframework.Policy;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.PolicyException;
import com.adito.policyframework.ResourceAccessEvent;
import com.adito.policyframework.ResourceUtil;
import com.adito.security.SessionInfo;
import com.adito.tunnels.agent.LocalForwardingChannel;
import com.adito.tunnels.agent.RemoteTunnel;
import com.adito.tunnels.agent.RemoteTunnelManagerFactory;

/**
 * {@link AgentService} implementation for dealing with 
 * the <i>SSL Tunnels</i> resource.
 * 
 * @author brett
 */
public class TunnelingService extends AbstractResourceService implements RequestHandler {


	static Log log = LogFactory.getLog(TunnelingService.class);

	/**
	 * Sent by server to start a list of local tunnels. Payload consists of
	 * tunnel resource details including resource ID.
	 */
	public static final String START_LOCAL_TUNNEL = "startLocalTunnel"; //$NON-NLS-1$
	
	/**
	 * Sent by client to request that a tunnel is launched. The server should
	 * then configure a launch session. If the tunnel is local, the server
	 * should reply in the same way of {@link #START_LOCAL_TUNNEL} (in which
	 * case the agent then configures and starts the local tunnel). If the
	 * tunnel is remote, no reply will be sent.
	 */
	public static final String SETUP_AND_LAUNCH_TUNNEL = "setupAndLaunchTunnel"; //$NON-NLS-1$

	/**
	 * Notification request that a remote tunnel has started.
	 */
	public static final String START_REMOTE_TUNNEL = "startRemoteTunnel";
	
	/**
	 * Notification request that a remote tunnel has stopped. 
	 */
	public static final String STOP_REMOTE_TUNNEL = "stopRemoteTunnel";
	
	/**
	 * Sent by server to stop a single local tunnel. Payload consists of
	 * resourceID
	 */
	public static final String STOP_LOCAL_TUNNEL = "stopLocalTunnel"; //$NON-NLS-1$

	/**
	 * Sent by server to request a list of all active local tunnels. Payload to
	 * be sent back to server consists of all active resource IDs
	 */
	public static final String ACTIVE_LOCAL_TUNNELS = "activeLocalTunnels"; //$NON-NLS-1$
	
	//	Private instance variables
	private AgentTunnel agent;
	
    /**
     * Constructor
     */
    public TunnelingService() {
		super(TunnelPlugin.SSL_TUNNEL_RESOURCE_TYPE, new int[] {
				TunnelsEventConstants.CREATE_TUNNEL,
				TunnelsEventConstants.REMOVE_TUNNEL,
				TunnelsEventConstants.UPDATE_TUNNEL });
	}
	
    /**
     * Stop all forwards giving the resource ID of the <i>SSL-Tunnel</i> that
     * started them.
     * 
     * @param launchSession launch session
     * @throws NoPermissionException if not allowed
     * @throws CoreException on any other error
     */
    public void stopTunnels(LaunchSession launchSession) throws NoPermissionException, CoreException {
        if (!DefaultAgentManager.getInstance().hasActiveAgent(launchSession.getSession())) {
            throw new TunnelException(TunnelException.INTERNAL_ERROR, (Throwable)null, "No agent.");
        } 

        Tunnel tunnel = (Tunnel) launchSession.getResource();
        launchSession.checkAccessRights(null, agent.getSession());
        MultiplexedConnection agent = DefaultAgentManager.getInstance().getAgentBySession(launchSession.getSession());

        try {
            if (tunnel.getType() == TransportType.LOCAL_TUNNEL_ID) {
                Collection<Tunnel> l = new ArrayList<Tunnel>();
                l.add(tunnel);
                stopLocalTunnels(agent, l);
            } else if (tunnel.getType() == TransportType.REMOTE_TUNNEL_ID) {
                Collection<Tunnel> l = new ArrayList<Tunnel>();
                l.add(tunnel);
                stopRemoteTunnels(agent, l);
            } else {
                throw new TunnelException(TunnelException.INTERNAL_ERROR, (Throwable)null, "Unknown tunnel type " + tunnel.getType());
            }

            CoreServlet.getServlet().fireCoreEvent(
                            new ResourceAccessEvent(this, TunnelsEventConstants.TUNNEL_CLOSED, launchSession.getResource(),
                                            launchSession.getPolicy(), launchSession.getSession(), CoreEvent.STATE_SUCCESSFUL));

        } catch (TunnelException te) {
            CoreServlet.getServlet().fireCoreEvent(
                            new ResourceAccessEvent(this, TunnelsEventConstants.TUNNEL_CLOSED, launchSession.getResource(),
                                            launchSession.getPolicy(), launchSession.getSession(), te));
            throw te;
        } finally {
			LaunchSessionFactory.getInstance().removeLaunchSession(launchSession);
        }
    }

    /**
     * Start port forwards for the <i>SSL Tunnel</i> specified by the provided
     * resource ID.
     * 
     * @param launchSession launch session
     * @throws NoPermissionException if not allowed
     * @throws TunnelException on any other other
     * @throws PolicyException on any other determininig policy
     */
    public void startTunnel(LaunchSession launchSession) throws NoPermissionException, TunnelException, PolicyException {

        if (!DefaultAgentManager.getInstance().hasActiveAgent(launchSession.getSession())) {
            throw new TunnelException(TunnelException.INTERNAL_ERROR, (Throwable)null, "No agent.");
        } else {
            Tunnel tunnel = (Tunnel) launchSession.getResource();
            launchSession.checkAccessRights(null, agent.getSession());
            AgentTunnel agent = DefaultAgentManager.getInstance().getAgentBySession(launchSession.getSession());

            try {
                if (tunnel.getType() == TransportType.LOCAL_TUNNEL_ID) {
                    startLocalTunnel(agent, tunnel, launchSession);
                } else if (tunnel.getType() == TransportType.REMOTE_TUNNEL_ID) {
                    startRemoteTunnel(agent, tunnel, launchSession);
                } else {
                    throw new TunnelException(TunnelException.INTERNAL_ERROR, (Throwable)null, "Unknown tunnel type " + tunnel.getType());
                }

                // Fire event
                CoreServlet.getServlet().fireCoreEvent(
                                new ResourceAccessEvent(this, TunnelsEventConstants.TUNNEL_OPENED, launchSession.getResource(),
                                                launchSession.getPolicy(), launchSession.getSession(), CoreEvent.STATE_SUCCESSFUL));
            } catch (TunnelException te) {

                // Fire event
                CoreServlet.getServlet().fireCoreEvent(
                                new ResourceAccessEvent(this, TunnelsEventConstants.TUNNEL_OPENED, launchSession.getResource(),
                                                launchSession.getPolicy(), launchSession.getSession(), te));

                throw te;
            }
        }
    }

    /**
     * Get a set of the resource ids of all active tunnels (local and remote).
     * 
     * @param session
     * @return resource IDs of active tunnels
     */
    public Set<Integer> getActiveTunnels(SessionInfo session) {

        try {
            MultiplexedConnection tunnel = DefaultAgentManager.getInstance().getAgentBySession(session);

            if (tunnel == null)
                return null;

            HashSet<Integer> activeTunnelIds = new HashSet<Integer>();

            // The agent keeps track of 'local' tunnels

            Request request = new Request(ACTIVE_LOCAL_TUNNELS);
            if (tunnel.sendRequest(request, true) && request.getRequestData()!=null) {

                ByteArrayReader reader = new ByteArrayReader(request.getRequestData());
                int count = (int) reader.readInt();
                for (int i = 0; i < count; i++) {
                    activeTunnelIds.add(new Integer((int) reader.readInt()));
                }
            }

            // The server keeps track of 'remote' tunnels
            Collection<RemoteTunnel> activeRemoteTunnels = RemoteTunnelManagerFactory.getInstance().getRemoteTunnels(session);
            if (activeRemoteTunnels != null) {
                synchronized (activeRemoteTunnels) {
                    for (RemoteTunnel r : activeRemoteTunnels) {
                        activeTunnelIds.add(r.getTunnel().getResourceId());
                    }
                }
            }

            return activeTunnelIds;
        } catch (IOException e) {
            log.error("Failed to get active tunnel list from agent", e);
            return null;
        }
    }

    public void performStartup(AgentTunnel agent) {
    	this.agent = agent;
        notifyAutoStartTunnels();
    }

    public Channel createChannel(MultiplexedConnection connection, String type) {
        if (type.equals(LocalForwardingChannel.CHANNEL_TYPE)) {
            return new LocalForwardingChannel();
        } else
            return null;
    }

	public void initializeTunnel(AgentTunnel tunnel) {
		tunnel.registerRequestHandler(SETUP_AND_LAUNCH_TUNNEL, this);
	}

	public boolean processRequest(Request request, MultiplexedConnection connection) {
		AgentTunnel agent = (AgentTunnel) connection;
		if (request.getRequestName().equals(SETUP_AND_LAUNCH_TUNNEL) && request.getRequestData()!=null) {
			try {
				ByteArrayReader reader = new ByteArrayReader(request.getRequestData());
				int id = (int)reader.readInt();
				Tunnel resource = (Tunnel)TunnelPlugin.SSL_TUNNEL_RESOURCE_TYPE.getResourceById(id);
				if (resource == null) {
					throw new Exception("No resource with ID " + id);
				}
				Policy policy = LaunchSessionManager.getLaunchRequestPolicy(null, agent.getSession(), resource);
				if (resource.sessionPasswordRequired(agent.getSession())) {
					// TODO: prompt user for credentials through agent!
					return true;
				} else {
					LaunchSession launchSession = LaunchSessionFactory.getInstance().createLaunchSession(agent.getSession(),
						resource,
						policy);
					launchSession.checkAccessRights(null, agent.getSession());
	                if (resource.getType() == TransportType.LOCAL_TUNNEL_ID) {
	                    try {
	                        Request req = buildLocalTunnel(resource, launchSession);
	                        request.setRequestData(req.getRequestData());
		                    return true;
	                    } catch (IOException ioe) {
	                        throw new TunnelException(TunnelException.INTERNAL_ERROR, ioe);
	                    }
	                } else if (resource.getType() == TransportType.REMOTE_TUNNEL_ID) {
	                    startRemoteTunnel(agent, resource, launchSession);
	                    request.setRequestData(null);
	                    return true;
	                } else {
	                    throw new TunnelException(TunnelException.INTERNAL_ERROR, (Throwable)null, "Unknown tunnel type " + resource.getType());
	                }
				}
			} catch (Exception e) {
				log.error("Failed to start tunnel.", e);
				return false;
			}
		} 
		return false;
	}

	public void postReply(MultiplexedConnection connection) {		
	}

    void startRemoteTunnel(AgentTunnel agent, Tunnel tunnel , LaunchSession launchSession) throws TunnelException {
        RemoteTunnel remoteTunnel = RemoteTunnelManagerFactory.getInstance().createRemoteTunnel(tunnel, agent, launchSession);
        remoteTunnel.start();
    }
    
    void startLocalTunnel(AgentTunnel agent, Tunnel tunnel, LaunchSession launchSession) throws TunnelException {
        try {
            Request req = buildLocalTunnel(tunnel, launchSession);
            /* Only require replies if not running on the protocol thread. This allows autostart
             * tunnels to work without blocking
             */            
            if (!agent.sendRequest(req, Thread.currentThread() != agent.getThread())) {
                throw new TunnelException(TunnelException.AGENT_REFUSED_LOCAL_TUNNEL, (Throwable)null);
            }
        } catch (IOException ioe) {
            throw new TunnelException(TunnelException.INTERNAL_ERROR, ioe);
        }
    }

    @SuppressWarnings("unchecked")
    void notifyAutoStartTunnels() {
        try {
            SessionInfo session = agent.getSession();
            List<Tunnel> tunnels = ResourceUtil.getGrantedResource(session, TunnelPlugin.SSL_TUNNEL_RESOURCE_TYPE);
            
            List<BundleActionMessage> tunnelFailures = new ArrayList<BundleActionMessage>();
            for (Tunnel tunnel : tunnels) {
                if (tunnel.isAutoStart()) {
                    try {
                        Policy policy = PolicyDatabaseFactory.getInstance().getGrantingPolicyForUser(session.getUser(), tunnel);
                        LaunchSession launchSession = LaunchSessionFactory.getInstance().createLaunchSession(session, tunnel, policy);
                        startTunnel(launchSession);
                    } catch (TunnelException tne) {
                        log.error("failed to start tunnel: '" + tunnel + "'", tne);
                        tunnelFailures.add(tne.getBundleActionMessage());
                    }
                }
            }
            
            if (!tunnelFailures.isEmpty()) {
                tunnelFailures.add(0, new BundleActionMessage("tunnels", "error.tunnels.autostart", tunnelFailures.size()));
                for (BundleActionMessage actionMessage : tunnelFailures) {
                    GlobalWarning globalWarning = new GlobalWarning(session.getHttpSession(), actionMessage);
                    GlobalWarningManager.getInstance().addToSession(globalWarning);
                }
            }
        } catch (Exception e) {
            log.error("Failed to start auto-start tunnels", e);
        }
    }
    
    Request buildLocalTunnel(Tunnel tunnel, LaunchSession launchSession) throws IOException {
    	// Process destination host and port for replacement variables
    	VariableReplacement r = new VariableReplacement();
    	r.setLaunchSession(launchSession);
    	String destHost = r.replace(tunnel.getDestination().getHost());
    	
        ByteArrayWriter msg = new ByteArrayWriter();
        msg.writeString(launchSession == null ? "" : launchSession.getId());
        msg.writeInt(tunnel.getResourceId());
        msg.writeString(tunnel.getResourceName());
        msg.writeInt(tunnel.getType());
        msg.writeString(tunnel.getTransport());
        msg.writeString(tunnel.getSourceInterface());        
        msg.writeInt(tunnel.getSourcePort());
        msg.writeInt(tunnel.getDestination().getPort());
        msg.writeString(destHost);
        Request req = new Request(START_LOCAL_TUNNEL, msg.toByteArray());
        return req;

    }

    void stopLocalTunnels(MultiplexedConnection agent, Collection<Tunnel> tunnels) throws CoreException {

        CoreException e = null;

        for (Tunnel tunnel : tunnels) {
            try {
                ByteArrayWriter msg = new ByteArrayWriter();
                msg.writeInt(tunnel.getResourceId());
                if (!agent.sendRequest(new Request(STOP_LOCAL_TUNNEL, msg.toByteArray()), false) && e == null) {
                    e = new TunnelException(TunnelException.AGENT_REFUSED_LOCAL_TUNNEL_STOP, (Throwable)null);
                }
            } catch (IOException ex) {
                throw new TunnelException(TunnelException.INTERNAL_ERROR, ex);
            }
        }
        if (e != null) {
            throw e;
        }
    }

    void stopRemoteTunnels(MultiplexedConnection agent, Collection<Tunnel> tunnels) throws CoreException {

        CoreException e = null;

        for (Tunnel tunnel : tunnels) {
            try {
                RemoteTunnel rt = RemoteTunnelManagerFactory.getInstance().getRemoteTunnel(tunnel.getResourceId());
                if (rt != null) {
                    rt.stopListener();
                } else {
                    throw new Exception("No active with ID for " + tunnel.getResourceId());
                }
            } catch (Exception ex) {
                throw new TunnelException(TunnelException.INTERNAL_ERROR, ex);
            }
        }
        if (e != null) {
            throw e;
        }
    }
}
