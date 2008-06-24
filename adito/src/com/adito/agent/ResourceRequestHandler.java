
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.multiplex.MultiplexedConnection;
import com.maverick.multiplex.Request;
import com.maverick.multiplex.RequestHandler;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.Resource;
import com.adito.policyframework.ResourceType;
import com.adito.policyframework.ResourceUtil;
import com.adito.policyframework.Resource.LaunchRequirement;

/**
 * {@link RequestHandler} that deals with generic <i>Resource</i> related
 * Agent requests.
 */
public class ResourceRequestHandler implements RequestHandler {
    
    /**
     * Request Name that gets a list of launch-able resources.
     * <p>
     * The request data will consist of a single <code>int</code>,
     * specifying the resource type ID to list. 
     */
    public static final String GET_RESOURCES = "getResources";

    static Log log = LogFactory.getLog(ResourceRequestHandler.class);

	/* (non-Javadoc)
	 * @see com.maverick.multiplex.RequestHandler#processRequest(com.maverick.multiplex.Request, com.maverick.multiplex.MultiplexedConnection)
	 */
	public boolean processRequest(Request request, MultiplexedConnection connection) {
		AgentTunnel agent = (AgentTunnel) connection;
		if (request.getRequestName().equals(GET_RESOURCES)) {
			try {
				if(request.getRequestData()!=null) {
				    // Read the resource type requested.
					ByteArrayReader bar = new ByteArrayReader(request.getRequestData());
					int resourceTypeId = (int)bar.readInt();
					ResourceType<?> resourceType = PolicyDatabaseFactory.getInstance().getResourceType(resourceTypeId);
					if(resourceType == null) {
						throw new Exception("Request for list of resources of an unknown type (" + resourceTypeId + ")");
					}		
					
					// Filter the list of available resources so that only launch-able resources are available 
					List<Resource> filteredResources = new ArrayList<Resource>();
					for(Iterator resourceIterator = ResourceUtil.getGrantedResource(agent.getSession(), resourceType).iterator(); resourceIterator.hasNext(); ) {
					    Resource resource = (Resource)resourceIterator.next();
					    if(resource.getLaunchRequirement() == LaunchRequirement.LAUNCHABLE ||
					                    ( resource.getLaunchRequirement() == LaunchRequirement.REQUIRES_WEB_SESSION && agent.getSession().getHttpSession() != null)) {
					        filteredResources.add(resource);
					    }
					}
					
					// Send the list of resource IDs and names back to the agent
					ByteArrayWriter response = new ByteArrayWriter();
					response.writeInt(filteredResources.size());
					for (Resource resource : filteredResources) {
						response.writeInt(resource.getResourceId());
						response.writeString(resource.getResourceDisplayName());
					}
					request.setRequestData(response.toByteArray());
					return true;
				}
			} catch (Exception e) {
				log.error("Failed to get resources.", e);
			}

		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.maverick.multiplex.RequestHandler#postReply(com.maverick.multiplex.MultiplexedConnection)
	 */
	public void postReply(MultiplexedConnection connection) {		
	}
}
