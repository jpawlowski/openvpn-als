
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


import com.adito.boot.RequestHandlerRequest;
import com.adito.security.User;

/**
 * Implementations of this interface are responsible for managing and
 * authenticating incoming <i>Agent</i> connections.
 */
public interface AgentCallback extends AgentAuthenticator {

	/**
	 * Create a new agent.
	 * 
	 * @param remoteHost remote connecting host
	 * @param user user request
	 * @param type agent type
	 * @param connectionParameters other connection parameters
	 * @return agent instance
	 * @throws AgentException if agent cannot be created
	 */
	AgentTunnel createAgent(String remoteHost, User user, String type, RequestHandlerRequest connectionParameters) throws AgentException;

	/**
	 * Remove an agent. Called when the agent is shutdown for any 
	 * reason (user initiated agent shutdown, logoff, error etc).
	 * 
	 * @param tunnel agent tunnel
	 * @throws AgentException on any error removing the agent
	 */
	void removeAgent(AgentTunnel tunnel) throws AgentException;
}
