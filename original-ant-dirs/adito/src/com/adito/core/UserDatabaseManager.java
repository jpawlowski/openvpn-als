
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
			
package com.adito.core;

import java.util.Calendar;
import java.util.Collection;
import java.util.TreeMap;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.Branding;
import com.adito.properties.Property;
import com.adito.properties.impl.realms.RealmKey;
import com.adito.realms.DefaultRealm;
import com.adito.realms.Realm;
import com.adito.security.UserDatabase;
import com.adito.security.UserDatabaseDefinition;

/**
 */
public class UserDatabaseManager {
    
    final static Log log = LogFactory.getLog(UserDatabaseManager.class);
    
    private static UserDatabaseManager instance;
    
    protected TreeMap<String, UserDatabaseDefinition> userDatabases;
    protected Realm defaultRealm;
    protected UserDatabase defaultUserDatabase;

    /**
     * Constant name for the default/initial realm.
     */
    public static final String DEFAULT_REALM_NAME = "Default";

    /**
     * Constant description for the default/initial realm.
     */
    public static final String DEFAULT_REALM_DESCRIPTION = "Default " + Branding.PRODUCT_NAME + " Realm";
    
    /**
     * 
     */
    private UserDatabaseManager() {
        super();
        userDatabases = new TreeMap<String, UserDatabaseDefinition>();
    }
    
    public void registerDatabase(UserDatabaseDefinition userDatabaseDefinition) {
    	if (log.isInfoEnabled())
    		log.info("Registering user database " + userDatabaseDefinition.getName() + " with class " + userDatabaseDefinition.getUserDatabaseClass().getName());
        userDatabases.put(userDatabaseDefinition.getName(), userDatabaseDefinition);
    }
    
    public UserDatabaseDefinition getUserDatabaseDefinition(String name) {
        return userDatabases.get(name);
    }
    
    public void closeAll() {
    	if (log.isInfoEnabled())
    		log.info("Closing all user databases");
        try {
            // Realm may be null if initialisation failed for some reason
            if(defaultRealm != null) {
                getUserDatabase(defaultRealm).close();
            }
        } catch (Exception e) {
            log.error("Failed to close userdatabase.", e);
        }
    }
    
    public void close(String realmName) throws Exception {
        UserDatabase udbInstance = getUserDatabase(realmName);
        if(udbInstance != null) {
            udbInstance.close();
        }
        else {
            throw new Exception("No user database with name " + realmName + ".");
        }
    }
    
        /**
     * Gets the initial user database.
     * @return UserDatabase
     */
    public UserDatabase getDefaultUserDatabase() {
        try {
            return getUserDatabase(DEFAULT_REALM_NAME);
        } catch (Exception e) {
            log.error("Failed to retrieve the default realm.", e);
            return null;
        }
    }
    
    
    public UserDatabase getUserDatabase(Realm realm) throws Exception {
    	if(!realm.equals(defaultRealm))
    		throw new Exception("Invalid realm");
    	return defaultUserDatabase;
    }
    
    public UserDatabase getUserDatabase(String realmName) throws Exception {
    	if(!realmName.equals(DEFAULT_REALM_NAME))
    		throw new Exception("Invalid realm " + realmName);
    	return defaultUserDatabase;
    }
    
   /**
     * @param realmId
     * @return UserDatabase
     * @throws Exception
     */
    public UserDatabase getUserDatabase(int realmId) throws Exception {
        
    	if(realmId != 1)
    		throw new Exception("Invalid realm ID " + realmId);
    	
    	return defaultUserDatabase;
    }
    
    public void initialize(boolean isSetupMode) throws ServletException {
        String type = Property.getProperty(new RealmKey("security.userDatabase", 1));
        try {
            if(userDatabases.containsKey(type)) {
                createDefaultUserDatabase(type);
            } else if (isSetupMode) {
                createDefaultUserDatabase("builtIn");
            } else {
                throw new ServletException("Unable to initialise default user database = '" + type + "'.");
            }
        } catch (Exception e) {
            log.error("Unable to initialise default user database.", e);
            // if we can't open the database in setup mode we still have to continue
            if(!isSetupMode) {
                throw new ServletException("Unable to initialise default user database.", e);
            }
        }
    }
    
   /**
     * @param type 
     * @throws Exception
     */
    private void createDefaultUserDatabase(String type) throws Exception {
        UserDatabaseDefinition udd = userDatabases.get(type);
        if (udd == null) {
            throw new Exception("No user database of type " + type + " registered.");
        } else {
        	Calendar now = Calendar.getInstance();
            Class clazz = udd.getUserDatabaseClass();
            defaultUserDatabase = (UserDatabase) clazz.newInstance();
            defaultRealm = new DefaultRealm(type, 1, DEFAULT_REALM_NAME, DEFAULT_REALM_DESCRIPTION, now, now);
            defaultUserDatabase.open(CoreServlet.getServlet(), defaultRealm);
        }
    }    
    
    public UserDatabase createUserDatabase(String type, String realmName, String realmDescription, boolean open) throws Exception {
        UserDatabaseDefinition udd = userDatabases.get(type);
        if (udd == null) {
            throw new Exception("No user database of type " + type + " registered.");
        } else {
        	Calendar now = Calendar.getInstance();
            
            Class clazz = udd.getUserDatabaseClass();
            UserDatabase udb = (UserDatabase) clazz.newInstance();
            Realm realm = new DefaultRealm(type, 1, DEFAULT_REALM_NAME, DEFAULT_REALM_DESCRIPTION, now, now);
            if(!udb.isOpen() && open)
            	udb.open(CoreServlet.getServlet(), realm);
            return udb;
        }
    }
    public static UserDatabaseManager getInstance() {
        if(instance == null) {
            instance = new UserDatabaseManager();
        }
        return instance;
    }

    public Collection<UserDatabaseDefinition> getUserDatabaseDefinitions() {
        return userDatabases.values();
    }
    
    /**
     * @param realmId
     * @return Realm
     */
    public Realm getRealm(int realmId) {
        if (defaultRealm.getResourceId() == realmId) {
            return defaultRealm;
        }
        throw new IllegalArgumentException("No realm exists for the id " + realmId);
    }

    /**
     * @param realmName
     * @return Realm
     */
    public Realm getRealm(String realmName) {
        if (defaultRealm != null && defaultRealm.getResourceName().equals(realmName)) {
            return defaultRealm;
        }
        throw new IllegalArgumentException("No realm exists for the name " + realmName);
    }

	/**
	 * Convenience method to get the default realm.
	 *  
	 * @return default realm
	 * @throws Exception if realm cannot be found
	 */
	public Realm getDefaultRealm() throws Exception {
		return getRealm(DEFAULT_REALM_NAME);
	}

	/**
	 * Convenience method to get the default realm ID
	 *  
	 * @return default realm id
	 * @throws Exception if realm cannot be found
	 */
	public int getDefaultRealmID() throws Exception {
		return getDefaultRealm().getResourceId();
	}
}
