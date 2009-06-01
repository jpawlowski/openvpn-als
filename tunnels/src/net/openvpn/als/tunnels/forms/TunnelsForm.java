
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
			
package net.openvpn.als.tunnels.forms;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.openvpn.als.agent.DefaultAgentManager;
import net.openvpn.als.policyframework.LaunchSession;
import net.openvpn.als.policyframework.LaunchSessionFactory;
import net.openvpn.als.policyframework.PolicyDatabaseFactory;
import net.openvpn.als.policyframework.ResourceUtil;
import net.openvpn.als.policyframework.forms.AbstractResourcesForm;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.security.User;
import net.openvpn.als.tunnels.Tunnel;
import net.openvpn.als.tunnels.TunnelDatabase;
import net.openvpn.als.tunnels.TunnelDatabaseFactory;
import net.openvpn.als.tunnels.TunnelItemModel;
import net.openvpn.als.tunnels.TunnelPlugin;
import net.openvpn.als.tunnels.TunnelingService;

/**
 * Implementation of {@link AbstractResourcesForm} suitable for listing <i>SSL
 * Tunnels</i>.
 */
public class TunnelsForm extends AbstractResourcesForm {

    /**
     * Constructor.
     * 
     */
    public TunnelsForm() {
        super(new TunnelItemModel("tunnel"));
    }

    /**
     * Initialise.
     * 
     * @param session session
     * @param user user
     * @param defaultSortColumnId default sort column id
     * @throws Exception on any error
     */
    public void initialise(SessionInfo session, User user, String defaultSortColumnId) throws Exception {
        super.initialize(session.getHttpSession(), defaultSortColumnId);
        TunnelDatabase sdb = TunnelDatabaseFactory.getInstance();
        List tunnels = session.getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT ? sdb.getTunnels(session.getUser()
                        .getRealm().getRealmID()) : ResourceUtil.getGrantedResource(session, TunnelPlugin.SSL_TUNNEL_RESOURCE_TYPE);

        if (DefaultAgentManager.getInstance().hasActiveAgent(session)) {
            Set activeTunnels = ((TunnelingService) DefaultAgentManager.getInstance().getService(TunnelingService.class))
                            .getActiveTunnels(session);
            for (Iterator it = tunnels.iterator(); it.hasNext();) {
                Tunnel tunnel = (Tunnel) it.next();
                if (tunnel != null && tunnel.getResourceId() >= 0) {
                    Integer id = new Integer(tunnel.getResourceId());
                    LaunchSession launchSession = activeTunnels.contains(id) ? LaunchSessionFactory.getInstance()
                                    .getFirstLaunchSessionForResource(session, tunnel) : null;
                    TunnelItem ti = new TunnelItem(tunnel, PolicyDatabaseFactory.getInstance().getPoliciesAttachedToResource(
                                    tunnel, user.getRealm()), launchSession);
                    ti.setFavoriteType(getFavoriteType(tunnel.getResourceId()));
                    getModel().addItem(ti);
                }
            }
        } else {
            Iterator i = tunnels.iterator();
            while (i.hasNext()) {
                Tunnel tunnel = (Tunnel) i.next();
                if (tunnel != null) {
                    TunnelItem ti = new TunnelItem(tunnel, PolicyDatabaseFactory.getInstance().getPoliciesAttachedToResource(
                                    tunnel, user.getRealm()), null);
                    ti.setFavoriteType(getFavoriteType(ti.getTunnel().getResourceId()));
                    getModel().addItem(ti);
                }
            }
        }

        checkSort();
        getPager().rebuild(getFilterText());
    }
}
