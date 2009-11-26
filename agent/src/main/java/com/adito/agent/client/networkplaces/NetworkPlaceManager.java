package com.adito.agent.client.networkplaces;

import java.io.IOException;

import com.maverick.multiplex.Request;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;
import com.adito.agent.client.AbstractResourceManager;
import com.adito.agent.client.Agent;

/**
 * This class manages network places allowing them to be launched from the Agent.
 * Basically the only thing this class does is open a browser window containing
 * a networkplace which user clicked in the Agent GUI.
 * 
 * @author Lee David Painter
 * 
 */
public class NetworkPlaceManager extends AbstractResourceManager {


	// #ifdef DEBUG
	static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(NetworkPlaceManager.class);
	// #endif
	
	/**
	 * Network place resource type ID
	 */
	public final static int NETWORK_PLACE_RESOURCE_TYPE_ID = 2;
	
	/**
	 * The command used to launch a network place
	 */	
	public static final String SETUP_AND_LAUNCH_NETWORK_PLACE = "setupAndLaunchNetworkPlace"; //$NON-NLS-1$

   /**
	 * This constructors calls the constructor of it's parent, AbstractResourceManager,
     * which wraps an Agent instance into a protected instance variable. Unlike with
     * TunnelManager which registers a lot of RequestHandlers, nothing else gets done here.
     */
	public NetworkPlaceManager(Agent agent) {
		super(agent);
	}

    /** Get a list of networkplaces from the server and update the GUI accordingly */
	public void getNetworkPlaceResources() {
		super.getResources(NETWORK_PLACE_RESOURCE_TYPE_ID, "Network Places");		
	}

    /**
      * Launch a networkplace resource. This is done by encapsulating the command
      * (SETUP_AND_LAUNCH_NETWORK_PLACE) and resource ID into a Request that's
      * sent to server. Unless something goes wrong, a browser window showing
      * the just launched networkplace is shown to the user.
      */
	public void launchResource(int resourceId) {	
		try {
			ByteArrayWriter baw = new ByteArrayWriter();
			baw.writeInt(resourceId);
			Request request = new Request(SETUP_AND_LAUNCH_NETWORK_PLACE, baw.toByteArray());
			if (agent.getConnection().sendRequest(request, true) && request.getRequestData()!=null) {
				ByteArrayReader bar = new ByteArrayReader(request.getRequestData());
				String uri = bar.readString();
                // Open a www-browser showing the launched networkplace URL
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
