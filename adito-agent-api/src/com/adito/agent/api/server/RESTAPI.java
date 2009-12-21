/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adito.agent.api.server;

import com.adito.agent.api.server.resources.restlets.ShutdownRestlet;
import com.adito.agent.api.server.resources.restlets.TunnelListRestlet;
import com.adito.agent.api.server.resources.restlets.TunnelStartRestlet;
import java.util.HashMap;
import org.restlet.Component;
import org.restlet.data.Protocol;

/**
 *
 * @author Matthias Jansen / Jansen-Systems
 */
public class RESTAPI {
    private static HashMap<Integer, RESTAPI> instances = new HashMap<Integer, RESTAPI>();
    private int port = 0;
    private APICommandsListener listener = null;

    private RESTAPI(int port, APICommandsListener l) throws Exception {
	// singleton
	this.port = port;
	this.listener = l;
	// Create a new Component.
	Component component = new Component();

	// Add a new HTTP server listening on port 8182.
	component.getServers().add(Protocol.HTTP, port);

	// Attach the sample application.
	// component.getDefaultHost().attach(APIApplication.getInstance());
	component.getDefaultHost().attach("/shutdown",new ShutdownRestlet(listener));
	component.getDefaultHost().attach("/tunnel/list",new TunnelListRestlet(listener));
	component.getDefaultHost().attach("/tunnel/start/{id}", new TunnelStartRestlet(listener));

	// Start the component.
	component.start();
    }

    public static RESTAPI startAPI(int port, APICommandsListener l) throws Exception {
	if (!instances.containsKey(port)) {
	    RESTAPI instance = new RESTAPI(port, l);
	    instances.put(port, instance);
	}
	return instances.get(port);
    }


}
