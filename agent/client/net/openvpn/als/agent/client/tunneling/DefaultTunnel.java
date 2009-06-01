
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

import net.openvpn.als.agent.client.util.TunnelConfiguration;

/**
 * Defaul implementation of a {@link TunnelConfiguration}.
 * 
 * @version $Revision: 1.3.2.1 $
 */
public class DefaultTunnel implements TunnelConfiguration {
    
    // Private instance variables

    private int id, type;
    private String name;
    private String transport;
    private String sourceInterface;
    private int sourcePort;
    private int destinationPort;
    private String destinationHost;
    private boolean permanent;
    private boolean temporarySingleConnect;
    private String launchId;

    /**
     * Constructor
     * 
     * @param id tunnel id
     * @param type type
     * @param transport transport
     * @param sourceInterface source interface
     * @param sourcePort source port
     * @param destinationPort destination port
     * @param destinationHost destination host
     * @param permanent permanent
     * @param name tunnel name
     * @param launchId id of launch session
     */
    public DefaultTunnel(int id, int type, String transport, String sourceInterface, int sourcePort,
                         int destinationPort, String destinationHost, boolean permanent,
                         boolean temporarySingleConnect, String name,
                         String launchId) {
        this.id = id;
        this.type = type;
        this.transport = transport;
        this.permanent = permanent;
        this.sourceInterface = sourceInterface;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
        this.destinationHost = destinationHost;
        this.temporarySingleConnect = temporarySingleConnect;
        this.name = name;
        this.launchId = launchId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.forwarding.Tunnel#getId()
     */
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.forwarding.Tunnel#getType()
     */
    public int getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.forwarding.Tunnel#isPermanent()
     */
    public boolean isPermanent() {
        return permanent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.forwarding.Tunnel#getTransport()
     */
    public String getTransport() {
        return transport;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.forwarding.Tunnel#getSourcePort()
     */
    public int getSourcePort() {
        return sourcePort;
    }

    
    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.forwarding.Tunnel#getSourceInterface()
     */
    public String getSourceInterface() {
    	return sourceInterface;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.forwarding.Tunnel#getDestinationPort()
     */
    public int getDestinationPort() {
        return destinationPort;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.forwarding.Tunnel#getDestinationHost()
     */
    public String getDestinationHost() {
        return destinationHost;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.vpn.base.Tunnel#isTemporarySingleConnect()
     */
    public boolean isTemporarySingleConnect() {
        return temporarySingleConnect;
    }

    /**
     * Set the destination hostname of the tunnel. 
     * 
     * @param destinationHost destination hostname of tunnel
     */
    public void setDestinationHost(String destinationHost) {
        this.destinationHost = destinationHost;
    }

    /**
     * Set the destination port of the tunnel
     * 
     * @param destinationPort destination port of tunnel
     */
    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    /**
     * Set the OpenVPNALS resource id of the tunnel.
     * 
     * @param id resource Id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Set the source port of the tunnel.
     * 
     * @param sourcePort tunnel source port.
     */
    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    /**
     * Set the source interface of the tunnel.
     * 
     * @param sourceInterface tunnel source interface.
     */
    public void setSourcePort(String sourceInterface) {
        this.sourceInterface = sourceInterface;
    }

    /**
     * Set the transport type of the tunnel. This may be one of {@link TunnelConfiguration#TCP_TUNNEL}
     * or {@link TunnelConfiguration#UDP_TUNNEL}.
     * 
     * @param transport The transport to set.
     */
    public void setTransport(String transport) {
        this.transport = transport;
    }

    /**
     * Set the tunnel type. This may be one of {@link TunnelConfiguration#LOCAL_TUNNEL} or
     * {@link TunnelConfiguration#REMOTE_TUNNEL}.
     * 
     * @param type tunnel type.
     */
    public void setType(int type) {
        this.type = type;
    }

	/* (non-Javadoc)
	 * @see net.openvpn.als.agent.util.ListeningSocketConfiguration#getLaunchId()
	 */
	public String getLaunchId() {
		return launchId;
	}
}
