
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
			
package com.adito.networkplaces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * System database factory for creating network place databases.
 */
public class NetworkPlaceDatabaseFactory {
    static Log log = LogFactory.getLog(NetworkPlaceDatabaseFactory.class);

    static NetworkPlaceDatabase instance;
    static Class networkPlaceDatabaseImpl = JDBCNetworkPaceDatabase.class;
    private static boolean locked = false;

    /**
     * @return An instance of the network place database factory.
     */
    public static NetworkPlaceDatabase getInstance() {
        try {
            return instance == null ? instance = (NetworkPlaceDatabase) networkPlaceDatabaseImpl.newInstance() : instance;
        } catch (Exception e) {
            log.error("Could not create instance of class " + networkPlaceDatabaseImpl.getCanonicalName(), e);
            return instance == null ? instance = new JDBCNetworkPaceDatabase() : instance;
        }
    }

    /**
     * @param networkPlaceDatabaseImpl the class of the NetworkPlace database
     * @param lock weather to lock the NetworkPlace database after setting it.
     * @throws IllegalStateException
     */
    public static void setFactoryImpl(Class networkPlaceDatabaseImpl, boolean lock) throws IllegalStateException {
        if (locked) {
            throw new IllegalStateException("NetworkPlace database factory has been locked by another plugin.");
        }
        NetworkPlaceDatabaseFactory.networkPlaceDatabaseImpl = networkPlaceDatabaseImpl;
        locked = lock;
    }
}
