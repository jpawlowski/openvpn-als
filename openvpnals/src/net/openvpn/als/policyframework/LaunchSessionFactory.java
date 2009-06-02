
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.policyframework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.core.AbstractFactory;


/**
 * Factory implementation for creating and locating the {@link LaunchSessionManager}.
 */
public class LaunchSessionFactory extends AbstractFactory {	 
		
	static Log log = LogFactory.getLog(LaunchSessionFactory.class);

    static LaunchSessionManager instance;
    static Class launchSessionManagerImpl = LaunchSessionManager.class;
    private static boolean locked = false;

    /**
     * Get an instance of the {@link LaunchSessionFactory}, lazily
     * creating it.
     * 
     * @return An instance of the launch session manager.
     */
    public static LaunchSessionManager getInstance() {
        try {
            return instance == null ? instance = (LaunchSessionManager) launchSessionManagerImpl.newInstance() : instance;
        } catch (Exception e) {
            log.error("Could not create instance of class " + launchSessionManagerImpl.getCanonicalName(), e);
            return instance == null ? instance = new LaunchSessionManager() : instance;
        }
    }


    /**
     * Set the factory implementation to use.
     * 
     * @param launchSessionManagerImpl the class of the launch session manager
     * @param lock weather to lock the policy database after setting it.
     * @throws IllegalStateException
     */
    public static void setFactoryImpl(Class launchSessionManagerImpl, boolean lock) throws IllegalStateException {
        if (locked) {
            throw new IllegalStateException("Policy database factory has been locked by another plugin.");
        }
        LaunchSessionFactory.launchSessionManagerImpl = launchSessionManagerImpl;
        locked = lock;
    }
}
