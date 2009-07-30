
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

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.adito.boot.HostService;
import com.adito.policyframework.ResourceType;
import com.adito.testcontainer.policyframework.AbstractTestPolicyEnabledResource;

public class TunnelsTests extends AbstractTestPolicyEnabledResource<Tunnel> {

    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        setUp("agent,tunnels");
    }

    @Override
    public Tunnel getEmptyResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new DefaultTunnel(-1, "", "a ", -1, 0, false, "", "", 8080, new HostService(""), null, calendar, calendar);
    }

    @Override
    public Tunnel getNormalResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new DefaultTunnel(getDefaultRealm().getRealmID(), "tunnel", "a tunnel", -1, 0, false, "TCP", "username", 8080, new HostService("localhost"), null, calendar, calendar);
    }

    @Override
    public Tunnel getNullResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new DefaultTunnel(-1, null, null, -1, 0, false, null, null, 8080, new HostService(""), null, calendar, calendar);
    }

    @Override
    public Tunnel createResource(Tunnel tunnel) throws Exception {
        return getTunnelService().createTunnel(tunnel.getRealmID(), tunnel.getResourceName(), tunnel.getResourceDescription(), tunnel.getType(), tunnel.isAutoStart(),
            tunnel.getTransport(), tunnel.getUsername(), tunnel.getSourcePort(), tunnel.getDestination(), tunnel.getSourceInterface()); 
    }
    
    @Override
    public Tunnel updateResource(Tunnel tunnel) throws Exception {
        getTunnelService().updateTunnel(tunnel.getResourceId(), tunnel.getResourceName(), tunnel.getResourceDescription(), tunnel.getType(), tunnel.isAutoStart(),
            tunnel.getTransport(), tunnel.getUsername(), tunnel.getSourcePort(), tunnel.getDestination(), tunnel.getSourceInterface());
        return getTunnelService().getTunnel(tunnel.getResourceId()); 
    }
    
    @Override
    public ResourceType getResourceType() throws Exception {
      return TunnelPlugin.SSL_TUNNEL_RESOURCE_TYPE;
    }

    @Override
    public Tunnel deleteResource(Tunnel resource) throws Exception {
        return getTunnelService().removeTunnel(resource.getResourceId());
    }

    @Override
    public Tunnel getResource(Tunnel resource) throws Exception {
        return getTunnelService().getTunnel(resource.getResourceId());
    }
    
    protected static TunnelDatabase getTunnelService() throws Exception {
        return TunnelDatabaseFactory.getInstance();
    }
    
    @Override
    public List<Tunnel> getAllResources() throws Exception {
        return getTunnelService().getTunnels();
    }
    
    @Test
    public void createUncommonTunnel() throws Exception {
        DefaultTunnel tunnel = new DefaultTunnel(-1, "р2рг$-_=+$%%^", "р2р-_=+2рг$", -1, 0, false,
            "р2рг$-_=+$%%^w", "р2рг$%$%%^grre-_=+", 8080, new HostService("localhost"), null, Calendar.getInstance(), Calendar.getInstance());
        assertEquals("There should not be any Tunnel", 0, getTunnelService().getTunnels().size());
        Tunnel createdTunnel = createResource(tunnel);
        assertEquals("There should not be any Tunnel", 1, getTunnelService().getTunnels().size());
        deleteResource(createdTunnel);
    }
}
