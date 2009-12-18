/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adito.agent.api.server;

import com.adito.agent.api.objects.TunnelList;
import com.adito.agent.api.server.resources.restlets.ShutdownRestlet;
import com.adito.agent.api.server.resources.restlets.TunnelListRestlet;
import com.adito.agent.api.server.resources.restlets.TunnelStartRestlet;
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

	    APICommandsListener listener = new APICommandsListener() {

		public boolean shutdown() {
		    new Thread(new Runnable() {
			public void run() {
			    try {
				Thread.sleep(2000);
				System.exit(0);
			    } catch (Exception e) {

			    }
			}
		    }).start();
		    return true;
		}

		public boolean startTunnel(int id) {
		    return true;
		}

		public boolean stopTunnel(int id) {
		    throw new UnsupportedOperationException("Not supported yet.");
		}

		public TunnelList getTunnelList() {
		    TunnelList t = new TunnelList();
		    // TODO: get the real list
		    t.getTunnels().add("Tunnel 1");
		    t.getTunnels().add("Tunnel 2");
		    t.getTunnels().add("Tunnel 3");
		    return t;
		}
	    };

	    // Attach the sample application.
	    // component.getDefaultHost().attach(APIApplication.getInstance());
	    component.getDefaultHost().attach("/shutdown",new ShutdownRestlet(listener));
	    component.getDefaultHost().attach("/tunnel/list",new TunnelListRestlet(listener));
	    component.getDefaultHost().attach("/tunnel/start/{id}", new TunnelStartRestlet(listener));

	    // Start the component.
	    component.start();
	} catch (Exception e) {
	    // Something is wrong.
	    e.printStackTrace();
	}

    }

}
