
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
			
package net.openvpn.als.tunnels;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.util.LabelValueBean;

import net.openvpn.als.agent.DefaultAgentManager;
import net.openvpn.als.core.CoreEvent;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.navigation.FavoriteResourceType;
import net.openvpn.als.navigation.WrappedFavoriteItem;
import net.openvpn.als.policyframework.DefaultResourceType;
import net.openvpn.als.policyframework.LaunchSession;
import net.openvpn.als.policyframework.LaunchSessionFactory;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.policyframework.Resource;
import net.openvpn.als.policyframework.ResourceChangeEvent;
import net.openvpn.als.policyframework.ResourceDeleteEvent;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.tunnels.forms.TunnelItem;

/**
 * Implementation of a {@link net.openvpn.als.policyframework.ResourceType} for
 * <i>SSL Tunnel</i> resources.
 */
public class TunnelResourceType extends DefaultResourceType implements FavoriteResourceType {

    final static Log log = LogFactory.getLog(TunnelResourceType.class);

    /**
     * Constructor
     */
    public TunnelResourceType() {
        super(TunnelPlugin.SSL_TUNNEL_RESOURCE_TYPE_ID, "policyframework", PolicyConstants.DELEGATION_CLASS);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.navigation.FavoriteResourceType#createWrappedFavoriteItem(int,
     *      javax.servlet.http.HttpServletRequest, java.lang.String)
     */
    public WrappedFavoriteItem createWrappedFavoriteItem(int resourceId, HttpServletRequest request, String type) throws Exception {
        Tunnel tunnel = TunnelDatabaseFactory.getInstance().getTunnel(resourceId);
        if (tunnel == null) {
            return null;
        }

        if (DefaultAgentManager.getInstance().hasActiveAgent(request)) {
            Set activeTunnels = ((TunnelingService) DefaultAgentManager.getInstance().getService(TunnelingService.class))
                            .getActiveTunnels(LogonControllerFactory.getInstance().getSessionInfo(request));
            SessionInfo sessionInfo = LogonControllerFactory.getInstance().getSessionInfo(request);
            LaunchSession launchSession = activeTunnels.contains(Integer.valueOf(tunnel.getResourceId())) ? LaunchSessionFactory
                            .getInstance().getFirstLaunchSessionForResource(sessionInfo, tunnel) : null;
            return new WrappedFavoriteItem(new TunnelItem(tunnel, null, launchSession), type);
        } else
            return new WrappedFavoriteItem(new TunnelItem(tunnel, null, null), type);

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.navigation.FavoriteResourceType#getResourceById(int)
     */
    public Resource getResourceById(int resourceId) throws Exception {
        return TunnelDatabaseFactory.getInstance().getTunnel(resourceId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.policyframework.DefaultResourceType#getResourceByName(java.lang.String,
     *      net.openvpn.als.security.SessionInfo)
     */
    public Resource getResourceByName(String resourceName, SessionInfo session) throws Exception {
        return TunnelDatabaseFactory.getInstance().getTunnel(resourceName, session.getUser().getRealm().getRealmID());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.policyframework.ResourceType#removeResource(int,
     *      net.openvpn.als.security.SessionInfo)
     */
    public Resource removeResource(int resourceId, SessionInfo session) throws Exception {
        Tunnel resource = null;
        try {
            resource = TunnelDatabaseFactory.getInstance().removeTunnel(resourceId);
            CoreServlet.getServlet().fireCoreEvent(
                            addTunnelAttributes(new ResourceDeleteEvent(this, TunnelsEventConstants.REMOVE_TUNNEL, resource, session,
                                            CoreEvent.STATE_SUCCESSFUL), resource));
            return resource;
        } catch (Exception e) {
            CoreServlet.getServlet().fireCoreEvent(
                            addTunnelAttributes(new ResourceDeleteEvent(this, TunnelsEventConstants.REMOVE_TUNNEL, null, session,
                                            CoreEvent.STATE_UNSUCCESSFUL), resource));
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.policyframework.ResourceType#updateResource(net.openvpn.als.boot.policyframework.Resource,
     *      net.openvpn.als.security.SessionInfo)
     */
    public void updateResource(Resource resource, SessionInfo session) throws Exception {
        Tunnel t = null;
        try {
            t = (Tunnel) resource;
            TunnelDatabaseFactory.getInstance().updateTunnel(t.getResourceId(), t.getResourceName(), t.getResourceDescription(),
                            t.getType(), t.isAutoStart(), t.getTransport(), t.getUsername(), t.getSourcePort(), t.getDestination(),
                            t.getSourceInterface());
            CoreServlet.getServlet().fireCoreEvent(
                            addTunnelAttributes(new ResourceChangeEvent(this, TunnelsEventConstants.UPDATE_TUNNEL, resource, session,
                                            CoreEvent.STATE_SUCCESSFUL), t));
        } catch (Exception e) {
            CoreServlet.getServlet().fireCoreEvent(
                            addTunnelAttributes(new ResourceChangeEvent(this, TunnelsEventConstants.UPDATE_TUNNEL, null, session,
                                            CoreEvent.STATE_UNSUCCESSFUL), t));
            throw e;
        }
    }

    CoreEvent addTunnelAttributes(CoreEvent evt, Tunnel tunnel) {
        evt.addAttribute(TunnelsEventConstants.EVENT_ATTR_TUNNEL_SOURCE_INTERFACE, tunnel.getSourceInterface());
        evt.addAttribute(TunnelsEventConstants.EVENT_ATTR_TUNNEL_SOURCE_PORT, String.valueOf(tunnel.getSourcePort()));
        evt.addAttribute(TunnelsEventConstants.EVENT_ATTR_TUNNEL_DESTINATION, String.valueOf(tunnel.getDestination()));
        evt.addAttribute(TunnelsEventConstants.EVENT_ATTR_TUNNEL_TRANSPORT, String.valueOf(tunnel.getTransport()));
        evt.addAttribute(TunnelsEventConstants.EVENT_ATTR_TUNNEL_TYPE,
                        ((LabelValueBean) TransportType.TYPES.get(tunnel.getType())).getLabel());
        return evt;
    }

}