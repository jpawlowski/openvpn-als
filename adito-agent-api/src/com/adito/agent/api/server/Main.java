/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adito.agent.api.server;

import com.adito.agent.api.objects.TunnelList;

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

	    RESTAPI api = RESTAPI.startAPI(8182, listener);
	} catch (Exception e) {
	    // Something is wrong.
	    e.printStackTrace();
	}

	System.out.println("After component start here, to check if it blocks...");

    }

}
