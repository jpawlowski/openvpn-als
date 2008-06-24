
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
			
package com.adito.agent.client.util;

import java.util.Hashtable;

public class ApplicationTypeManager {

	private static ApplicationTypeManager instance ;
	
	private Hashtable agentTypes;
	
	private ApplicationTypeManager() {
		agentTypes = new Hashtable();
	}
	
	public void registerApplicationType(String name, String className) {
		agentTypes.put(name, className);
	}
	
	public void deregisterApplicationType(String name) {
		agentTypes.remove(name);
	}
	
	public ApplicationType createType(String name) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String className = (String)agentTypes.get(name);
		if(className == null) {
        	className = "com.adito.agent.client.util.types." + (String.valueOf(name.charAt(0)).toUpperCase() + name.substring(1) ) + "ApplicationType";
		}
        return (ApplicationType) Class.forName(className).newInstance();
	}
	
	public static ApplicationTypeManager getInstance() {
		if(instance == null) {
			instance = new ApplicationTypeManager();
		}
		return instance;
	}
}
