
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
			
package com.adito.agent.client;

import java.io.IOException;

import com.maverick.multiplex.Request;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;

public abstract class AbstractResourceManager {

	// #ifdef DEBUG
	static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(AbstractResourceManager.class);
	// #endif

	// 	Protected instance variables
		protected Agent agent;

	public AbstractResourceManager(Agent agent) {
		this.agent = agent;
	}

	public Agent getAgent() {
		return agent;
	}


    /** This method asks the server for a list of resources (e.g. tunnels or networkplaces)
     *  and updates the GUI menus accordingly. The request encapsulates a command,
     *  "getResources", and a resource type ID (an integer). This method does not create,
     *  delete or otherwise modify any resources. When the GUI is updated, a ResourceLaunchAction
     *  instance is tied to each entry.
     *
     * @param   resourceType    Type of resources to get. E.g. for a tunnel this is hardcoded as 4
     * @param   menu            The menu name in the GUI for this resource type
     *   
     */
	public void getResources(int resourceType, String menu) {
        // FIXME: remove dependencies on Java-specific Writer classes in ByteArrayWriter
		try {
			ByteArrayWriter baw = new ByteArrayWriter();
            // Encode the resourceType into a ByteArray
			baw.writeInt(resourceType);
            /* Create new Request to send to the server. Request contains the
               command (getResources) and resource type (an integer)
               encoded into a byte array. */ 
			Request request = new Request("getResources", baw.toByteArray());

            // Send the request for resources to the server
			if (agent.getConnection().sendRequest(request, true)) {
				if(request.getRequestData()!=null) {
                    /* Parse the response from the server. The response is a byte array which
                       is parsed using the ByteArrayReader. */
					ByteArrayReader reader = new ByteArrayReader(request.getRequestData());
					int count = (int) reader.readInt();
                    /* If the response contains at least one resource, clear the existing
                       resource (e.g. tunnel) menu in the GUI or create one if it does not
                       exist */
					if (count > 0) {
	                    if(agent.getGUI().isMenuExists(menu))
	                        agent.getGUI().clearMenu(menu);
	                    else
	                        agent.getGUI().addMenu(menu);
                        /* Add the returned resources to the correct menu in GUI */
						for (int i = 0; i < count; i++) {
							int resourceId = (int) reader.readInt();
							agent.getGUI().addMenuItem(menu, new ResourceLaunchAction(resourceId, reader.readString()));
						}
					}
					else {
                        if(agent.getGUI().isMenuExists(menu))
                            agent.getGUI().removeMenu(menu);					    
					}
				}
			}
		} catch (IOException e) {
			// #ifdef DEBUG
			log.error("Failed to get resources.", e);
			// #endif
		}
	}

    /** This abstract method is overridden in various other places, e.g.
      * in TunnelManager and NetworkPlaceManager
      *
      * @param  resourceId  the ID of the resource to launch (e.g. 4 for a tunnel)
      */
	public abstract void launchResource(int resourceId);


    /** VERIFY ME: This inner class is responsible for the actions associated with each Agent GUI
      * item (e.g. a tunnel). Each action the user performs for an item is launched as a
      * separate thread.
      */ 
	class ResourceLaunchAction implements AgentAction {

		int resourceId;
		String displayName;

        /** Constructor */
		ResourceLaunchAction(int resourceId, String displayName) {
			this.resourceId = resourceId;
			this.displayName = displayName;
		}

        /** This method is triggered when user tries to launch an action (e.g. start a tunnel).
          * The resource is launched as a separate Thread which is then started. This avoids
          * the GUI from locking up if launching is slow.
          */
		public void actionPerformed() {
			
			Thread t = new Thread() {
				public void run() {
					launchResource(resourceId);
				}
			};
			t.start();
		}

		public String getAction() {
			return displayName;
		}

	}
}
