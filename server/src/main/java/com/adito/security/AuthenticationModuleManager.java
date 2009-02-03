
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
			
package com.adito.security;

import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Manages registered {@link com.adito.security.AuthenticationModuleDefinition}
 * objects that are used to create {@link com.adito.security.AuthenticationModule}
 * instances to be used in an {@link com.adito.security.AuthenticationScheme}.
 * <p>
 * Both the core and plugins will register definitions such as <i>Password Authentication</i>,
 * <i>Personal Questions</i> and additional modules such as <i>Client Certifcate</i>
 * and <i>Key</i>.
 * <p>
 * To register a module get an instance of this object (which will be created
 * if it doesn't exist) using {@link #getInstance()}. Then use
 * {@link #registerModule(String, Class, String, boolean, boolean, boolean)}
 * to register the module.
 */
public class AuthenticationModuleManager {

    final static Log log = LogFactory.getLog(AuthenticationModuleManager.class);

    private static AuthenticationModuleManager instance;
    private TreeMap modules;

    /**
     * Private contructor to prevent instantiation
     */
    private AuthenticationModuleManager() {
        super();
        modules = new TreeMap();
    }

    /**
     * Register a new module definition.
     * 
     * @param name name of module
     * @param moduleClass class to use to instantiate the module
     * @param messageResourcesKey bundle that contains message resources
     * @param primary primary (accepts username)
     * @param secondary secondary (accepts something other than username)
     * @param system system (used internally, not usable for web logon)
     */
    public void registerModule(String name, Class moduleClass, String messageResourcesKey, boolean primary, boolean secondary, boolean system) {
        modules.put(name, new AuthenticationModuleDefinition(name, moduleClass, messageResourcesKey, primary, secondary, system));
    }

    /**
     * Register a new module definition
     * 
     * @param name
     * @param moduleClass
     * @param messageResourcesKey
     * @param primary
     * @param secondary
     * @param system
     * @param primaryIfSecondaryExists
     */
    public void registerModule(String name, Class moduleClass, String messageResourcesKey, boolean primary, boolean secondary, boolean system, boolean primaryIfSecondaryExists) {
        modules.put(name, new AuthenticationModuleDefinition(name, moduleClass, messageResourcesKey, primary, secondary, system, primaryIfSecondaryExists));
    }
    /**
     * De-register a module
     * 
     * @param module module to de-register
     */
    public void deregisterModule(String module) {
        modules.remove(module);
    }

    /**
     * Get a definition give its module name. <code>null</code> will be
     * returned if no such definition exists.
     * 
     * @param module module name
     * @return definition
     */
    public AuthenticationModuleDefinition getModuleDefinition(String module) {
        return (AuthenticationModuleDefinition)modules.get(module);
    }

    /**
     * Create a new instance of an {@link AuthenticationModule} that may
     * be used by an {@link AuthenticationScheme} for a user to logon given its
     * definition name. 
     * 
     * @param module module name
     * @return authentication module instance
     * @throws InstantiationException if module cannot be created
     * @throws IllegalAccessException 
     */
    public AuthenticationModule createModule(String module) throws InstantiationException, IllegalAccessException {
        AuthenticationModuleDefinition def = getModuleDefinition(module);
        if(def == null) {
            throw new IllegalArgumentException("No module named " + module);
        }
        return (AuthenticationModule)def.getModuleClass().newInstance();
    }

    /**
     * Get an iterator of all registered authentication module definitions.
     * 
     * @return iterator of all registered authentication module definitions
     */
    public Iterator authenticationModuleDefinitions() {
        return modules.values().iterator();
    }

    /**
     * Get an instance of the authentication module mananger. 
     *
     * @return authentication module manager
     */
    public static AuthenticationModuleManager getInstance() {
        if(instance == null) {
            instance = new AuthenticationModuleManager();
        }
        return instance;
    }

    /**
     * Get if a module has been registered given its definition name.
     * 
     * @param module module name
     * @return module registered
     */
    public boolean isRegistered(String module) {
        return modules.containsKey(module);
    }

    /**
     * Get the first {@link AuthenticationScheme} that is using the
     * provided module. <code>null</code> will be returned if no scheme
     * uses the module.
     * 
     * @param module modue
     * @return scheme
     */
    public AuthenticationScheme getSchemeForAuthenticationModuleInUse(String module) {
        try {
            List schemes = SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequences();
            for(Iterator i = schemes.iterator(); i.hasNext(); ) {
                AuthenticationScheme seq = (DefaultAuthenticationScheme)i.next();
                if(seq.hasModule(module)) {
                    return seq;
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

}
