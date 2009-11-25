
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
			
package com.adito.agent;

import java.util.HashMap;
import java.util.Map;

import com.adito.boot.RequestHandlerRequest;
import com.adito.boot.SystemProperties;
import com.adito.security.User;

/** This class manages (creates and removes) remote Agent instances */
public class AgentManager {
    private static final AgentManager instance = new AgentManager();
    private Map<String, AgentCallback> types = new HashMap<String, AgentCallback>();

    AgentManager() {
        // The default type of agent is the Adito Agent
        types.put(DefaultAgentCallback.DEFAULT_AGENT_TYPE, new DefaultAgentCallback());
        if (SystemProperties.get("adito.agent.debug", "").equals("enabled")) {
            types.put(DefaultAgentCallback.DEFAULT_AGENT_TYPE, new DummyAgentCallback());
        } else if (SystemProperties.get("adito.agent.debug", "").equals("interactive")) {
            types.put(DefaultAgentCallback.DEFAULT_AGENT_TYPE, new InteractiveAgentCallback());
        } else if (SystemProperties.get("adito.agent.debug", "").equals("attach")) {
            types.put(DefaultAgentCallback.DEFAULT_AGENT_TYPE, new AttachAgentCallback());
        }
    }

    /**
     * @return AgentManager
     */
    public static AgentManager getInstance() {
        return instance;
    }

    /**
     * @param remoteHost
     * @param user
     * @param type
     * @param connectionParameters
     * @return AgentTunnel
     * @throws AgentException
     */
    public synchronized AgentTunnel createAgent(String remoteHost, User user, String type, RequestHandlerRequest connectionParameters) throws AgentException {
        AgentCallback callback = (AgentCallback) types.get(type);
        if (callback != null) {
            return callback.createAgent(remoteHost, user, type, connectionParameters);
        } else
            return null;
    }

    /**
     * @param tunnel
     * @throws AgentException
     */
    public synchronized void removeAgent(AgentTunnel tunnel) throws AgentException {
        AgentCallback callback = (AgentCallback) types.get(tunnel.getType());
        if (callback != null)
            callback.removeAgent(tunnel);
    }

    /**
     * Register a new agent callback.
     * @param type - the type of agent
     * @param callback - the callback to use
     */
    public void registerAgentType(String type, AgentCallback callback) {
        types.put(type, callback);
    }

    /**
     * Deregister an agent callback
     * @param type - the type of agent
     */
    public void deregisterAgentType(String type) {
        types.remove(type);
    }

    /**
     * @param type
     * @param request
     * @return User
     */
    public User authenticate(String type, RequestHandlerRequest request) {
        AgentCallback callback = (AgentCallback) types.get(type);
        return callback == null ? null : callback.authenticate(request);
    }
}
