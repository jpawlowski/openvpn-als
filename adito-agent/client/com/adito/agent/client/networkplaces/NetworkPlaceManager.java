package com.adito.agent.client.networkplaces;

import java.io.IOException;

import com.maverick.multiplex.Request;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;
import com.adito.agent.client.AbstractResourceManager;
import com.adito.agent.client.Agent;

/**
 * This class manages network places allowing them to be launched from the agent.
 * 
 * @author Lee David Painter
 * 
 */
public class NetworkPlaceManager extends AbstractResourceManager {


	// #ifdef DEBUG
	static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(NetworkPlaceManager.class);
	// #endif
	
	/**
	 * Network pplace resource type ID
	 */
	public final static int NETWORK_PLACE_RESOURCE_TYPE_ID = 2;
	
	/**
	 * Setup and launch web forward
	 */	
	public static final String SETUP_AND_LAUNCH_NETWORK_PLACE = "setupAndLaunchNetworkPlace"; //$NON-NLS-1$

	public NetworkPlaceManager(Agent agent) {
		super(agent);		
	}

	public void getNetworkPlaceResources() {
		super.getResources(NETWORK_PLACE_RESOURCE_TYPE_ID, "Network Places");		
	}

	public void launchResource(int resourceId) {	
		try {
			ByteArrayWriter baw = new ByteArrayWriter();
			baw.writeInt(resourceId);
			Request request = new Request(SETUP_AND_LAUNCH_NETWORK_PLACE, baw.toByteArray());
			if (agent.getConnection().sendRequest(request, true) && request.getRequestData()!=null) {
				ByteArrayReader bar = new ByteArrayReader(request.getRequestData());
				String uri = bar.readString();
				agent.getGUI().openBrowser(uri);
				// #ifdef DEBUG
				log.debug("Network place setup");
				// #endif
//				processLaunchRequest(request);
			} else {
				// #ifdef DEBUG
				log.error("Failed to setup and launch network place");
				// #endif
			}
		} catch (IOException e) {
			// #ifdef DEBUG
			log.error("Failed to setup and launch network place", e);
			// #endif
		}
	}
}
