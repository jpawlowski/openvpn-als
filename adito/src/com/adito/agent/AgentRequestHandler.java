
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.HttpConstants;
import com.adito.boot.RequestHandler;
import com.adito.boot.RequestHandlerException;
import com.adito.boot.RequestHandlerRequest;
import com.adito.boot.RequestHandlerResponse;
import com.adito.properties.Property;
import com.adito.properties.impl.profile.ProfilePropertyKey;
import com.adito.security.User;

public class AgentRequestHandler implements RequestHandler {

	Log log = LogFactory.getLog(AgentRequestHandler.class);
	
	public boolean handle(String pathInContext, String pathParams,
			RequestHandlerRequest request, RequestHandlerResponse response)
			throws IOException, RequestHandlerException {


		if(request.getPath().startsWith("/agent")) {
			
			String type = (String) request.getParameters().get("agentType");

			if(type!=null) {
				
				
				User user = AgentManager.getInstance().authenticate(type, request);
				
				if(user == null) {
					if(request.getField("Authorization") != null || request.getParameters().get("ticket") != null) { 
					
						// Authentication failed
						if(log.isDebugEnabled())
		                    log.debug("Authentication failed. Sending 403 - FORBIDDEN");
		                response.sendError(HttpConstants.RESP_403_FORBIDDEN, "Access forbidden");
					}
					else {
						// Authentication required
						if(log.isDebugEnabled())
		                    log.debug("Authentication required. Sending 401 - Unauthorized");
				        response.setField("WWW-Authenticate", "Basic realm=\"Agent\"");
		                response.sendError(HttpConstants.RESP_401_UNAUTHORIZED, "Unauthorized");
						
					}
	                
				} else {
					
					AgentTunnel agent = null;
					try {
						agent = AgentManager.getInstance().createAgent(request.getRemoteHost(), user, type, request);
						
						/**
						 * LDP - We set the socket timeout to twice the value of the 
						 * keep-alive interval. This means we should only ever timeout
						 * if the agent becomes unresponsive. 
						 */
						int timeoutMs = Property.getPropertyInt(new ProfilePropertyKey("client.heartbeat.interval", agent.getSession()));
						request.setTunnel(agent, (timeoutMs * 2));
						
					} catch (AgentException e) {
						log.error("Could not create agent tunnel of type " + type, e);
						response.sendError(HttpConstants.RESP_500_INTERNAL_SERVER_ERROR, e.getMessage());
					}
					
					return true;
				}
			} else {
				// No credentials to authenticate
                log.error("Agent did not sent agentType parameter in request");
                response.sendError(HttpConstants.RESP_403_FORBIDDEN, "Incorrect request");
			}
			
			return true;
			
		} else {
			return false;
		}
	}


    @SuppressWarnings( { "unchecked" })
    private static Map getParameters(RequestHandlerRequest request) {
        Map parameters = new HashMap(request.getParameters());
        for (Enumeration fieldNames = request.getFieldNames(); fieldNames.hasMoreElements();) {
            String fieldName = (String) fieldNames.nextElement();
            Enumeration fieldValues = request.getFieldValues(fieldName);
            if (fieldValues.hasMoreElements())
                parameters.put(fieldName, fieldValues.nextElement());
        }
        return parameters;
    }
}