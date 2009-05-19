package com.adito.agent.client.applications;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.maverick.multiplex.MultiplexedConnection;
import com.maverick.multiplex.Request;
import com.maverick.multiplex.RequestHandler;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;
import com.adito.agent.client.AbstractResourceManager;
import com.adito.agent.client.Agent;
import com.adito.agent.client.Messages;
import com.adito.agent.client.util.TunnelConfiguration;

public class ApplicationManager extends AbstractResourceManager implements RequestHandler {

	public static final String LAUNCH_APPLICATION_REQUEST = "launchApplication";

	// #ifdef DEBUG
	org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ApplicationManager.class);
	// #endif
	
	/**
	 * Application resource type ID
	 */
	public final static int APPLICATION_SHORTCUT_RESOURCE_TYPE_ID = 3;

	Hashtable applicationResources = new Hashtable();

	static ApplicationManager instance;

	public ApplicationManager(Agent agent) {
		super(agent);
		agent.getConnection().registerRequestHandler(LAUNCH_APPLICATION_REQUEST, this);
	}
	
	public void getApplicationResources() {
		super.getResources(APPLICATION_SHORTCUT_RESOURCE_TYPE_ID, "Applications");
	}

	public void launchResource(int resourceId) {
		try {
			ByteArrayWriter baw = new ByteArrayWriter();
			baw.writeInt(resourceId);
			Request request = new Request("setupAndLaunchApplication", baw.toByteArray());
			if (agent.getConnection().sendRequest(request, true)) {
				// #ifdef DEBUG
				log.debug("Application launch setup");
				// #endif
				processLaunchRequest(request);
			} else {
				// #ifdef DEBUG
				log.error("Failed to setup and launch application launch");
				// #endif
			}
		} catch (IOException e) {
			// #ifdef DEBUG
			log.error("Failed to setup and launch application launch", e);
			// #endif
		}
	}

	public boolean processRequest(Request request, MultiplexedConnection con) {

		if (request.getRequestName().equals(LAUNCH_APPLICATION_REQUEST)) {

			try {
				return processLaunchRequest(request);
			} catch (IOException e) {
				// #ifdef DEBUG
				log.error("Failed to process launch application request", e); //$NON-NLS-1$
				// #endif
				return false;
			}
		} else
			return false;
	}

	public void postReply(MultiplexedConnection connection) {		
	}

	boolean processLaunchRequest(Request request) throws IOException {

		if(request.getRequestData()==null)
			return true;
		
		ByteArrayReader msg = new ByteArrayReader(request.getRequestData());
		
		// If this was a server side the no further processing is required
		boolean serverSide = msg.readBoolean();
		if(serverSide) {
			// #ifdef DEBUG
			log.info("Server side launch. No further processing required.");
			// #endif
			return false;
		}

		// Get the application name
		String name = msg.readString();
		msg.readInt(); // shortcut id
		String launchId = msg.readString();
		String descriptor = msg.readString();
		Hashtable parameters = new Hashtable();
		parameters.put("launchId", launchId);
		parameters.put("ticket", launchId);

		Application app = launchApplication(name, descriptor, parameters);
		app.start();
		Vector tunnels = ((AgentApplicationLauncher)app.getLauncher()).getTunnels();		
		ByteArrayWriter baw = new ByteArrayWriter();
		for(Enumeration e = tunnels.elements(); e.hasMoreElements(); ) {
            TunnelConfiguration listeningSocketConfiguration = (TunnelConfiguration) e.nextElement();
            baw.writeString(listeningSocketConfiguration.getName());
            baw.writeString(listeningSocketConfiguration.getSourceInterface());
            baw.writeInt(listeningSocketConfiguration.getSourcePort());			
		}
		request.setRequestData(baw.toByteArray());
		
		return true;
	}

	public Application launchApplication(String name, String descriptor, Hashtable parameters) throws IOException {
		if (agent.getConfiguration().isDisplayInformationPopups()) {
			agent.getGUI().popup(null, MessageFormat.format(Messages.getString("Application.launching"), new Object[] { name }), Messages.getString("Application.name"), "popup-application", -1); //$NON-NLS-1$
		}
		Application app = new Application(agent, parameters, name, descriptor);
		return app;
	}
}
