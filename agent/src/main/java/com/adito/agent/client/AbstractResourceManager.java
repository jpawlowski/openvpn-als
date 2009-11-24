
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

	public void getResources(int resourceType, String menu) {
        // FIXME: remove dependencies on Java-specific Writer classes in ByteArrayWriter
		try {
			ByteArrayWriter baw = new ByteArrayWriter();
			baw.writeInt(resourceType);
			Request request = new Request("getResources", baw.toByteArray());
			if (agent.getConnection().sendRequest(request, true)) {
				if(request.getRequestData()!=null) {
					ByteArrayReader reader = new ByteArrayReader(request.getRequestData());
					int count = (int) reader.readInt();
					if (count > 0) {
	                    if(agent.getGUI().isMenuExists(menu))
	                        agent.getGUI().clearMenu(menu);
	                    else
	                        agent.getGUI().addMenu(menu);
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

	public abstract void launchResource(int resourceId);

	class ResourceLaunchAction implements AgentAction {

		int resourceId;
		String displayName;

		ResourceLaunchAction(int resourceId, String displayName) {
			this.resourceId = resourceId;
			this.displayName = displayName;
		}

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
