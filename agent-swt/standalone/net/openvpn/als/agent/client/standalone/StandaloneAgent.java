
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
			
package net.openvpn.als.agent.client.standalone;

import net.openvpn.als.agent.client.Agent;
import net.openvpn.als.agent.client.AgentConfiguration;

public class StandaloneAgent {
	
	private Agent agent;
	private AgentConfiguration configuration;

	public StandaloneAgent() throws Exception {
		
		configuration = new AgentConfiguration();	
		configuration.setGUIClass("net.openvpn.als.agent.client.standalone.StandaloneGUI");
		agent = new Agent(configuration);
		agent.init();
		
	}
	
	public static void main(String[] args) throws Exception {
		// #ifdef DEBUG
		org.apache.log4j.BasicConfigurator.configure();
		// #endif
		StandaloneAgent agent = new StandaloneAgent();
	}
}
