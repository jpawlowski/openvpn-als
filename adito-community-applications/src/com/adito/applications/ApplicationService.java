
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
			
package com.adito.applications;

import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.multiplex.Channel;
import com.maverick.multiplex.MultiplexedConnection;
import com.maverick.multiplex.Request;
import com.maverick.multiplex.RequestHandler;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;
import com.adito.agent.AbstractResourceService;
import com.adito.agent.AgentService;
import com.adito.agent.AgentTunnel;
import com.adito.agent.channels.ApplicationFileChannel;
import com.adito.core.CoreAttributeConstants;
import com.adito.core.CoreEvent;
import com.adito.core.CoreServlet;
import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.ExtensionParser;
import com.adito.extensions.ExtensionBundle.ExtensionBundleStatus;
import com.adito.extensions.store.ExtensionStore;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.LaunchSessionFactory;
import com.adito.policyframework.LaunchSessionManager;
import com.adito.policyframework.Policy;
import com.adito.policyframework.Resource;
import com.adito.policyframework.ResourceAccessEvent;

/**
 * {@link AgentService} implementation for dealing with 
 * the <i>Application Shortcuts</i> resource.
 * 
 * @author brett
 */
public class ApplicationService extends AbstractResourceService implements RequestHandler {

	final static Log log = LogFactory.getLog(ApplicationService.class);
	
	/**
	 * Constructor
	 */
	public ApplicationService() {
		super(ApplicationsPlugin.APPLICATION_SHORTCUT_RESOURCE_TYPE, new int[] { 
				ApplicationShortcutEventConstants.CREATE_APPLICATION_SHORTCUT, 
				ApplicationShortcutEventConstants.REMOVE_APPLICATION_SHORTCUT, 
				ApplicationShortcutEventConstants.UPDATE_APPLICATION_SHORTCUT
		});
	}

	/* (non-Javadoc)
	 * @see com.adito.agent.AgentService#performStartup(com.adito.agent.AgentTunnel)
	 */
	public void performStartup(AgentTunnel agent) {
	}

	/**
	 * Create the request to instruct the agent to launch an application
	 * 
	 * @param httpRequest request
	 * @param launchSession launch session
	 * @return boolean indicating if the agent reported the application was
	 *         launched
	 * @throws Exception
	 */
	public Request launchApplication(LaunchSession launchSession) throws Exception {
		ApplicationShortcut shortcut = (ApplicationShortcut) launchSession.getResource();
		ByteArrayWriter msg = new ByteArrayWriter();
		
		// If this is a service side application launcher then launch now and inform the agent not to go any further
		ExtensionDescriptor descriptor = ExtensionStore.getInstance().getExtensionDescriptor(shortcut.getApplication());
		if(((ApplicationLauncherType)descriptor.getExtensionType()).isServerSide()) {
			msg.writeBoolean(true);


			// Do the launch
			try {
				if(descriptor.getApplicationBundle().getStatus() != ExtensionBundleStatus.ACTIVATED) {
					throw new Exception("Extension bundle " + descriptor.getApplicationBundle().getId() +" is not activated, cannot launch applicaiton.");
				}
				
				((ApplicationLauncherType) descriptor.getExtensionType()).launch(new HashMap<String, String>(),
					descriptor,
					shortcut,
					null,
					launchSession,
					null,
					null);

				CoreServlet.getServlet().fireCoreEvent(new ResourceAccessEvent(this,
								ApplicationShortcutEventConstants.APPLICATION_SHORTCUT_LAUNCHED,
								launchSession.getResource(),
								launchSession.getPolicy(),
								launchSession.getSession(),
								CoreEvent.STATE_SUCCESSFUL).addAttribute(CoreAttributeConstants.EVENT_ATTR_APPLICATION_NAME,
					descriptor.getName()).addAttribute(CoreAttributeConstants.EVENT_ATTR_APPLICATION_ID, descriptor.getId()));

			} catch (Exception ex) {
				CoreServlet.getServlet().fireCoreEvent(new ResourceAccessEvent(this,
						ApplicationShortcutEventConstants.APPLICATION_SHORTCUT_LAUNCHED,
								launchSession.getSession(),
								ex).addAttribute(CoreAttributeConstants.EVENT_ATTR_APPLICATION_NAME, descriptor.getName())
								.addAttribute(CoreAttributeConstants.EVENT_ATTR_APPLICATION_ID, descriptor.getId()));
				throw ex;

			}
		}
		else {		
			msg.writeBoolean(false);
			msg.writeString(shortcut.getApplication());
			msg.writeInt(shortcut.getResourceId());
			msg.writeString(launchSession.getId());
			msg.writeString(ExtensionParser.processApplicationParameters(launchSession,
				new Properties(),
				shortcut.getParameters(),
				shortcut.getApplication()));
		}
		
		return new Request("launchApplication", msg.toByteArray());
	}

	/* (non-Javadoc)
	 * @see com.adito.agent.AgentService#createChannel(com.maverick.multiplex.MultiplexedConnection, java.lang.String)
	 */
	public Channel createChannel(MultiplexedConnection connection, String type) {
		if (type.equals(ApplicationFileChannel.CHANNEL_TYPE)) {
			return new ApplicationFileChannel((AgentTunnel)connection);
		} else
			return null;
	}

	/* (non-Javadoc)
	 * @see com.maverick.multiplex.RequestHandler#processRequest(com.maverick.multiplex.Request, com.maverick.multiplex.MultiplexedConnection)
	 */
	public boolean processRequest(Request request, MultiplexedConnection connection) {

		AgentTunnel agent = (AgentTunnel) connection;

		if (request.getRequestName().equals("setupAndLaunchApplication") && request.getRequestData()!=null) {

			try {
				ByteArrayReader reader = new ByteArrayReader(request.getRequestData());
				int id = (int)reader.readInt();
				Resource resource = ApplicationsPlugin.APPLICATION_SHORTCUT_RESOURCE_TYPE.getResourceById(id);
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

					ApplicationShortcut shortcut = (ApplicationShortcut) launchSession.getResource();
					ExtensionDescriptor descriptor = ExtensionStore.getInstance().getExtensionDescriptor(shortcut.getApplication());

					Request newRequest = launchApplication(launchSession);
					request.setRequestData(newRequest.getRequestData());

					CoreServlet.getServlet().fireCoreEvent(new ResourceAccessEvent(this,
							ApplicationShortcutEventConstants.APPLICATION_SHORTCUT_LAUNCHED,
									launchSession.getResource(),
									launchSession.getPolicy(),
									launchSession.getSession(),
									CoreEvent.STATE_SUCCESSFUL).addAttribute(CoreAttributeConstants.EVENT_ATTR_APPLICATION_NAME,
						descriptor.getName()).addAttribute(CoreAttributeConstants.EVENT_ATTR_APPLICATION_ID, descriptor.getId()));

				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} 
		return false;
	}

	/* (non-Javadoc)
	 * @see com.maverick.multiplex.RequestHandler#postReply(com.maverick.multiplex.MultiplexedConnection)
	 */
	public void postReply(MultiplexedConnection connection) {		
	}

	/* (non-Javadoc)
	 * @see com.adito.agent.AgentService#initializeTunnel(com.adito.agent.AgentTunnel)
	 */
	public void initializeTunnel(AgentTunnel tunnel) {
		tunnel.registerRequestHandler("setupAndLaunchApplication", this);
	}
}
