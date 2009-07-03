
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

import java.util.Calendar;

import com.adito.boot.HostService;
import com.adito.policyframework.AbstractResource;
import com.adito.policyframework.Resource.LaunchRequirement;

public class DefaultTunnel extends AbstractResource implements Tunnel {

    int type;
    String username;
    boolean autoStart;
    String transport;
    int sourcePort;
    String sourceInterface;
    HostService destination;

    /**
     * @param id tunnel id
     * @param type type
     * @param autoStart automatically start tunnel upon login
     * @param transport transport
     * @param username username
     * @param sourcePort source port
     * @param destination destination
     * @param sourceInterface source interface
     * @param dateCreated date created
     * @param dateAmended date amended
     */
    public DefaultTunnel(int realmID, String name, String description, int id, int type, boolean autoStart, String transport, String username,
                    int sourcePort, HostService destination, String sourceInterface, Calendar dateCreated, Calendar dateAmended) {
        super(realmID, TunnelPlugin.SSL_TUNNEL_RESOURCE_TYPE, id, name, description, dateCreated, dateAmended);
        setLaunchRequirement(LaunchRequirement.LAUNCHABLE);
        this.type = type;
        this.autoStart = autoStart;
        this.transport = transport;
        this.username = username;
        this.sourcePort = sourcePort;
        this.sourceInterface = sourceInterface;
        this.destination = destination;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.forwarding.Tunnel#getType()
     */
    public int getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.forwarding.Tunnel#isAutoStart()
     */
    public boolean isAutoStart() {
        return autoStart;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.forwarding.Tunnel#getTransport()
     */
    public String getTransport() {
        return transport;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.forwarding.Tunnel#getUsername()
     */
    public String getUsername() {
        return username;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.forwarding.Tunnel#getSourcePort()
     */
    public int getSourcePort() {
        return sourcePort;
    }

    public String getSourceInterface() {
        return sourceInterface;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.forwarding.Tunnel#getDestinationHost()
     */
    public HostService getDestination() {
        return destination;
    }

    /**
     * @param autoStart The autoStart to set.
     */
    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    /**
     * @param destinationHost The destinationHost to set.
     */
    public void setDestination(HostService destination) {
        this.destination = destination;
    }

    /**
     * @param sourcePort The sourcePort to set.
     */
    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public void setSourceInterface(String sourceInterface) {
        this.sourceInterface = sourceInterface;
    }

    /**
     * @param transport The transport to set.
     */
    public void setTransport(String transport) {
        this.transport = transport;
    }

    /**
     * @param type The type to set.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
