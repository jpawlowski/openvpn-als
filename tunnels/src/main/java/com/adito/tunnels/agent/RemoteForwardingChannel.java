
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
import java.net.Socket;

import com.maverick.multiplex.Channel;
import com.maverick.multiplex.IOStreamConnector;
import com.maverick.util.ByteArrayWriter;
import com.adito.policyframework.LaunchSession;
import com.adito.tunnels.Tunnel;

public class RemoteForwardingChannel extends Channel {

    public static final String CHANNEL_TYPE = "remote-tunnel";

    private Tunnel tunnel;

    Socket incomingConnection = null;
    IOStreamConnector input;
    IOStreamConnector output;
    RemoteTunnel remoteTunnel;
    LaunchSession launchSession;

    public RemoteForwardingChannel(RemoteTunnel remoteTunnel, Socket incomingConnection, Tunnel tunnel, LaunchSession launchSession) {
        super(CHANNEL_TYPE, 32768, 35000);
        this.launchSession = launchSession;
        this.remoteTunnel = remoteTunnel;
        this.tunnel = tunnel;
        this.incomingConnection = incomingConnection;
    }

    public RemoteTunnel getRemoteTunnel() {
        return remoteTunnel;
    }

    public byte[] create() throws IOException {
        ByteArrayWriter msg = new ByteArrayWriter();
        msg.writeString(launchSession == null ? "" : launchSession.getId());
        msg.writeInt(tunnel.getResourceId());
        msg.writeString(tunnel.getResourceName());
        msg.writeInt(tunnel.getType());
        msg.writeString(tunnel.getTransport());
        msg.writeString(tunnel.getSourceInterface());
        msg.writeInt(tunnel.getSourcePort());
        msg.writeInt(tunnel.getDestination().getPort());
        msg.writeString(tunnel.getDestination().getHost());
        msg.writeBoolean(false);
        return msg.toByteArray();
    }

    public void onChannelClose() {
        if (input != null)
            input.close();
        if (output != null)
            output.close();
        try {
            if (incomingConnection != null)
                incomingConnection.close();
        } catch (IOException e) {
        }
    }

    public void onChannelOpen(byte[] data) {
        if (incomingConnection != null) {
            try {
                input = new IOStreamConnector(getInputStream(), incomingConnection.getOutputStream());
                output = new IOStreamConnector(incomingConnection.getInputStream(), getOutputStream());
            } catch (IOException ex) {
                close();
            }
        }
    }

    public byte[] open(byte[] data) throws IOException {
        return null;
    }

}
