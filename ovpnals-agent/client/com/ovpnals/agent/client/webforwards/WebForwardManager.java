package com.ovpnals.agent.client.webforwards;

import java.io.IOException;

import com.maverick.multiplex.Request;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;
import com.ovpnals.agent.client.AbstractResourceManager;
import com.ovpnals.agent.client.Agent;

/**
 * This class manages web forwards allowing them to be launched from the agent.
 * 
 * @author Lee David Painter
 * 
 */
public class WebForwardManager extends AbstractResourceManager {
	
	/**
	 * Web forward resource type ID
	 */
	public final static int WEBFORWARD_RESOURCE_TYPE_ID = 0;


	// #ifdef DEBUG
	static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(WebForwardManager.class);
	// #endif
	
	/**
	 * Setup and launch web forward
	 */	
	public static final String SETUP_AND_LAUNCH_WEB_FORWARD = "setupAndLaunchWebForward"; //$NON-NLS-1$

	public WebForwardManager(Agent agent) {
		super(agent);		
	}

	public void getWebForwardResources() {
		super.getResources(WEBFORWARD_RESOURCE_TYPE_ID, "Web Forwards");		
	}

	public void launchResource(int resourceId) {	
		try {
			ByteArrayWriter baw = new ByteArrayWriter();
			baw.writeInt(resourceId);
			Request request = new Request(SETUP_AND_LAUNCH_WEB_FORWARD, baw.toByteArray());
			if (agent.getConnection().sendRequest(request, true) && request.getRequestData()!=null) {
				ByteArrayReader bar = new ByteArrayReader(request.getRequestData());
				String uri = bar.readString();
				agent.getGUI().openBrowser(uri);
				// #ifdef DEBUG
				log.debug("Application launch setup");
				// #endif
//				processLaunchRequest(request);
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
		// TODO
	}
}
