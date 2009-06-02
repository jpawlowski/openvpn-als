
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
			
package net.openvpn.als.agent;

import java.util.ArrayList;
import java.util.Collection;

import net.openvpn.als.boot.RequestHandlerRequest;
import net.openvpn.als.security.User;

/**
 * Provides an implementation of AgentCallback which allows multiple
 * authentication sources.
 */
public abstract class AbstractAgentCallback implements AgentCallback {
    private final Collection<AgentAuthenticator> authenticators_ = new ArrayList<AgentAuthenticator>();

    /**
     * Register a new <code>net.openvpn.als.agent.AgentAuthenticator</code>
     * @param agentAuthenticator - the AgentAuthenticator to register
     */
    public void registerAuthenticator(AgentAuthenticator agentAuthenticator) {
        authenticators_.add(agentAuthenticator);
    }

    public User authenticate(RequestHandlerRequest request) {
        for (AgentAuthenticator authenticator : authenticators_) {
            User user = authenticator.authenticate(request);
            if (null != user) {
                return user;
            }
        }
        return null;
    }
}