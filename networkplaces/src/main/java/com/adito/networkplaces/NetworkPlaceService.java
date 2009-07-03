
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
			
package com.adito.networkplaces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.multiplex.Channel;
import com.maverick.multiplex.ChannelOpenException;
import com.maverick.multiplex.MultiplexedConnection;
import com.maverick.multiplex.Request;
import com.maverick.multiplex.RequestHandler;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;
import com.adito.agent.AbstractResourceService;
import com.adito.agent.AgentTunnel;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.LaunchSessionFactory;
import com.adito.policyframework.LaunchSessionManager;
import com.adito.policyframework.Policy;
import com.adito.policyframework.ResourceType;

public class NetworkPlaceService extends AbstractResourceService implements RequestHandler {

    public NetworkPlaceService(ResourceType resourceType, int[] events) {
		super(resourceType, events);
		// TODO Auto-generated constructor stub
	}

	static Log log = LogFactory.getLog(NetworkPlaceService.class);

	
	/**
	 * Setup and launch network place
	 */	
	public static final String SETUP_AND_LAUNCH_NETWORK_PLACE = "setupAndLaunchNetworkPlace"; //$NON-NLS-1$

	
    /**
     * Constructor
     */
    public NetworkPlaceService() {
		super(NetworkPlacePlugin.NETWORK_PLACE_RESOURCE_TYPE, new int[] {
				NetworkPlacesEventConstants.CREATE_NETWORK_PLACE,
				NetworkPlacesEventConstants.DELETE_NETWORK_PLACE,
				NetworkPlacesEventConstants.UPDATE_NETWORK_PLACE });
	}
    
	public void performStartup(AgentTunnel tunnel) {
    }

    public void initializeTunnel(AgentTunnel tunnel) {
		tunnel.registerRequestHandler(SETUP_AND_LAUNCH_NETWORK_PLACE, this);
	}

	public boolean processRequest(Request request, MultiplexedConnection connection) {
		AgentTunnel agent = (AgentTunnel) connection;
		if (request.getRequestName().equals(SETUP_AND_LAUNCH_NETWORK_PLACE) && request.getRequestData()!=null) {
			try {
				ByteArrayReader reader = new ByteArrayReader(request.getRequestData());
				int id = (int)reader.readInt();
				NetworkPlace resource = (NetworkPlace)NetworkPlacePlugin.NETWORK_PLACE_RESOURCE_TYPE.getResourceById(id);
				if (resource == null) {
					throw new Exception("No resource with ID " + id);
				}
				Policy policy = LaunchSessionManager.getLaunchRequestPolicy(null, agent.getSession(), resource);
				if (resource.sessionPasswordRequired(agent.getSession())) {
					// TODO: prompt user for credentials through agent!
					return true;
				} else {
					LaunchSession launchSession = LaunchSessionFactory.getInstance().createLaunchSession(agent.getSession(),
						resource,
						policy);
					launchSession.checkAccessRights(null, agent.getSession());
                    String uri = resource.getLaunchUri(launchSession);
                    ByteArrayWriter baw = new ByteArrayWriter();
                    baw.writeString(uri);
                    request.setRequestData(baw.toByteArray());
	                return true;
				}
			} catch (Exception e) {
				log.error("Failed to start tunnel.", e);
				return false;
			}
		} 
		return false;
	}

	public void postReply(MultiplexedConnection connection) {		
	}

	public Channel createChannel(MultiplexedConnection connection, String type) throws ChannelOpenException {
		return null;
	}
}
