
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

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.multiplex.Request;
import com.maverick.util.ByteArrayWriter;
import com.adito.core.CoreEvent;
import com.adito.core.CoreListener;
import com.adito.core.CoreServlet;
import com.adito.policyframework.ResourceType;

/**
 * Abstract {@link AgentService} implementation for dealing with 
 * the Adito resources.
 * 
 * @author brett
 */
public abstract class AbstractResourceService implements AgentService, CoreListener {

	final static Log log = LogFactory.getLog(AbstractResourceService.class);
	
	private int[] events;
	private ResourceType resourceType;
	
	/**
	 * Constructor
	 * 
	 * @param resourceType resource type
	 * @param events array of event codes to watch for
	 */
	public AbstractResourceService(ResourceType resourceType, int[] events) {
		CoreServlet.getServlet().addCoreListener(this);
		this.resourceType = resourceType;
		this.events = events;
	}

	/* (non-Javadoc)
	 * @see com.adito.core.CoreListener#coreEvent(com.adito.core.CoreEvent)
	 */
	public void coreEvent(CoreEvent evt) {
		// If applications change, we need to tell all of the agents
		for(int i = 0 ; i < events.length ; i++) {
			if(evt.getId() == events[i]) {
				DefaultAgentManager.getInstance().getAgentBroadcastExecutor().execute(new Runnable() {
					public void run() {						
						Collection<AgentTunnel> agents = DefaultAgentManager.getInstance().getAgents();
						synchronized(agents) {
							for(AgentTunnel agent : agents) {
								ByteArrayWriter baw = new ByteArrayWriter();
								try {
									baw.writeInt(resourceType.getResourceTypeId());
									synchronized(agent) {
										agent.sendRequest(new Request(AgentTunnel.UPDATE_RESOURCES_REQUEST, baw.toByteArray()), false);
									}
								}
								catch(IOException ioe) {
									log.warn("Failed to send resource update request to agent " + agent.getId() + ".", ioe);
								}
							}
						}
					}					
				});
				return;
			}
		}	
	}
}
