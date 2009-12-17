/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adito.agent.api.server;

import com.adito.agent.api.server.resources.restlets.ShutdownRestlet;
import com.adito.agent.api.server.resources.restlets.TunnelListRestlet;
import org.restlet.Component;
import org.restlet.data.Protocol;

/**
 *
 * @author Matthias Jansen / Jansen-Systems
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
	try {
	    // Create a new Component.
	    Component component = new Component();

	    // Add a new HTTP server listening on port 8182.
	    component.getServers().add(Protocol.HTTP, 8182);

	    // Attach the sample application.
	    // component.getDefaultHost().attach(APIApplication.getInstance());
	    component.getDefaultHost().attach("/shutdown",new ShutdownRestlet());
	    component.getDefaultHost().attach("/tunnelList",new TunnelListRestlet());

	    // Start the component.
	    component.start();
	} catch (Exception e) {
	    // Something is wrong.
	    e.printStackTrace();
	}

    }

}
