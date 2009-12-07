
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.multiplex.MultiplexedConnectionListener;
import com.adito.agent.AgentTunnel;
import com.adito.policyframework.LaunchSession;
import com.adito.security.SessionInfo;
import com.adito.tunnels.Tunnel;
import com.adito.tunnels.TunnelException;

/**
 * Default implementation of a {@link RemoteTunnelManager}.
 */
public class DefaultRemoteTunnelManager implements RemoteTunnelManager {

    final static Log log = LogFactory.getLog(DefaultRemoteTunnelManager.class);

    private Map<String, RemoteTunnel> tunnels = new HashMap<String, RemoteTunnel>();
    private Map<Integer, List<RemoteTunnel>> tunnelsBySession = new HashMap<Integer, List<RemoteTunnel>>();

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.agent.remote.RemoteTunnelManager#init()
     */
    public void init() {
    }

    /* (non-Javadoc)
     * @see com.adito.tunnels.agent.RemoteTunnelManager#createRemoteTunnel(com.adito.tunnels.Tunnel, com.adito.agent.AgentTunnel, com.adito.policyframework.LaunchSession)
     */
    public RemoteTunnel createRemoteTunnel(Tunnel tunnel, AgentTunnel agent, LaunchSession launchSession) throws TunnelException {
        RemoteTunnel remoteTunnel = null;
        synchronized (tunnels) {
            String tunnelId = tunnel.getSourcePort()
                            + "-"
                            + (tunnel.getSourceInterface() == null || tunnel.getSourceInterface().equals("") ? "" : tunnel
                                            .getSourceInterface());
            remoteTunnel = tunnels.get(tunnelId);
            if (remoteTunnel != null) {
                if (remoteTunnel.getAgent() == agent) {
                    throw new TunnelException(TunnelException.REMOTE_TUNNEL_IN_USE, (Throwable)null, String.valueOf(tunnel.getSourcePort()));
                }
                throw new TunnelException(TunnelException.REMOTE_TUNNEL_LOCKED, (Throwable)null, String.valueOf(tunnel.getSourcePort()));
            }
            try {
                remoteTunnel = new RemoteTunnel(tunnel, agent, this, launchSession);
                agent.addListener(new RemoteTunnelListener(remoteTunnel));
                if (log.isInfoEnabled())
                    log.info("Adding remote tunnel with id of " + tunnelId);
                List<RemoteTunnel> sessionTunnels = tunnelsBySession.get(agent.getSession().getId());
                if (sessionTunnels == null) {
                    sessionTunnels = new ArrayList<RemoteTunnel>();
                    tunnelsBySession.put(agent.getSession().getId(), sessionTunnels);
                }
                sessionTunnels.add(remoteTunnel);
                tunnels.put(tunnelId, remoteTunnel);
            } catch (IOException e) {
                log.error("Failed to create new remote tunnel.", e);
                throw new TunnelException(TunnelException.PORT_ALREADY_IN_USE, (Throwable)null, String.valueOf(tunnel.getSourcePort()));
            }
        }
        return remoteTunnel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.agent.remote.RemoteTunnelManager#getRemoteTunnels()
     */
    public Collection<RemoteTunnel> getRemoteTunnels() {
        return new ArrayList<RemoteTunnel>(tunnels.values());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.agent.remote.RemoteTunnelManager#stopRemoteTunnel(com.adito.agent.remote.RemoteTunnel)
     */
    public void removeRemoteTunnel(RemoteTunnel tunnel) {
        synchronized (tunnels) {
            String tunnelId = tunnel.getTunnel().getSourcePort()
                            + "-"
                            + (tunnel.getTunnel().getSourceInterface() == null
                                            || tunnel.getTunnel().getSourceInterface().equals("") ? "" : tunnel.getTunnel()
                                            .getSourceInterface());
            if (log.isInfoEnabled())
                log.info("Removing remote tunnel with id of " + tunnelId);
            tunnels.remove(tunnelId);
            List<RemoteTunnel> sessionTunnels = tunnelsBySession.get(tunnel.getAgent().getSession().getId());
            sessionTunnels.remove(tunnel);
            if (sessionTunnels.size() == 0) {
                tunnelsBySession.remove(tunnel.getAgent().getSession().getId());
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.agent.remote.RemoteTunnelManager#getRemoteTunnels(com.adito.security.SessionInfo)
     */
    public Collection<RemoteTunnel> getRemoteTunnels(SessionInfo session) {
        return tunnelsBySession.get(session.getId());
    }

    class RemoteTunnelListener implements MultiplexedConnectionListener {
        RemoteTunnel remoteTunnel;

        RemoteTunnelListener(RemoteTunnel remoteTunnel) {
            this.remoteTunnel = remoteTunnel;
        }

        public void onConnectionClose() {
            remoteTunnel.stopListener();
        }

        public void onConnectionOpen() {
        }

    }

    public RemoteTunnel getRemoteTunnel(int resourceId) {
        synchronized (tunnels) {
            for (RemoteTunnel remoteTunnel : tunnels.values()) {
                if (remoteTunnel.getTunnel().getResourceId() == resourceId) {
                    return remoteTunnel;
                }
            }
        }
        return null;
    }

}
