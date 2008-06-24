
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
			
package com.adito.agent.client;


import java.io.IOException;
import java.util.Hashtable;

import com.maverick.http.AuthenticationCancelledException;
import com.maverick.http.HttpException;
import com.maverick.http.UnsupportedAuthenticationException;
import com.adito.agent.client.util.URI;


public class AgentCache implements AgentProvider {
	
	private Hashtable agents;
	private AgentConfiguration agentConfiguration;
	
	public AgentCache(AgentConfiguration agentConfiguration) {
		agents = new Hashtable();
		this.agentConfiguration = agentConfiguration;
	}

	public synchronized Agent getAgent(String ticketUri) throws IOException, HttpException, UnsupportedAuthenticationException, AuthenticationCancelledException {
		Agent agent = (Agent)agents.get(ticketUri);
		if(agent != null) {
			if(!agent.getConnection().isRunning()) {
				agents.remove(ticketUri);
			}
		}
		if(agent == null) {
			agent = new Agent(agentConfiguration);
			URI uri = new URI(ticketUri);
			String username = uri.getUserinfo();
			String password = null;
			int idx = username.indexOf(':');
			if(idx != -1) {
				password = username.substring(idx + 1);
				username = username.substring(0, idx);
			}
			String ticket = uri.getQueryString();
			agent.init();
			boolean connected = false;
			try {
				agent.connect(uri.getHost(), uri.getPort(), uri.getScheme().equalsIgnoreCase("https"), username, password == null ? ticket : password, password != null);
				connected = true;
				agents.put(ticketUri, agent);				
			}
			finally {
				if(!connected) {
					agent.disconnect();
				}
			}
		}
		return agent;
	}
}
