
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
			
package net.openvpn.als.notification.agent;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.multiplex.Request;
import com.maverick.util.ByteArrayWriter;
import net.openvpn.als.agent.AgentTunnel;
import net.openvpn.als.agent.DefaultAgentManager;
import net.openvpn.als.notification.AbstractMessageSender;
import net.openvpn.als.notification.Message;
import net.openvpn.als.notification.MessageSink;
import net.openvpn.als.notification.Notifier;
import net.openvpn.als.notification.Recipient;
import net.openvpn.als.security.LogonController;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;

/**
 */
public class AgentMessageSink implements MessageSink {
    private static final Log LOG = LogFactory.getLog(AgentMessageSink.class);
    private Notifier notifier;

    public void start(Notifier notifier) throws Exception {
        this.notifier = notifier;
    }

    public void stop() throws Exception {
    }

    public boolean send(final Message message) throws Exception {
        List<Recipient> recipients = message.getRecipients();
        int sent = notifier.send(recipients, new AbstractMessageSender() {
            public int performSendMessage(Recipient recipient) throws Exception {
                return send(message, recipient);
            }
        });
        return sent > 0;
    }
    
    private int send(Message message, Recipient recipient) throws IOException {
        int sent = 0;
        LogonController logonController = LogonControllerFactory.getInstance();
        List<SessionInfo> sessions = logonController.getSessionInfo(recipient.getRecipientAlias(), SessionInfo.UI);
        if (sessions != null) {
            for (SessionInfo info : sessions) {
                if (send(message, recipient, info)) {
                    sent++;
                }
            }
        }
        return sent;
    }
    
    private boolean send(Message message, Recipient recipient, SessionInfo info) throws IOException {
        ByteArrayWriter msg = new ByteArrayWriter();
        msg.writeString(message.getSubject());
        msg.writeInt(0);
        msg.writeString(message.getContent());

        DefaultAgentManager agentManager = DefaultAgentManager.getInstance();
        if (agentManager.hasActiveAgent(info) && info.getUser().getPrincipalName().equals(recipient.getRecipientAlias())) {
            try {
                Request request = new Request("agentMessage", msg.toByteArray());
                AgentTunnel tunnel = agentManager.getAgentBySession(info);
                if (tunnel != null) {
                    tunnel.sendRequest(request, false, 0);
                    return true;
                }
            } catch (IOException e) {
                LOG.error("Failed to send message to agent. Did it disconnect before we could send it?", e);
            }
        }
        return false;
    }
    
    public String getName() {
        return "AGENT";
    }

    public String getShortNameKey() {
        return "notification.vpnClient.shortName";
    }

    public String getDescriptionKey() {
        return "notification.vpnClient.description";
    }

    public String getBundle() {
        return "setup";
    }
}