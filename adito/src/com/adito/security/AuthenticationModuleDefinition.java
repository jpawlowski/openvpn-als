
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


/**
 * Provides information about how to create and the capabilities of an
 * <i>Authentication Module</i>
 * <p>
 * For every {@link com.adito.security.AuthenticationModule} to be used
 * and instance of this call must be registered with the 
 * {@link com.adito.security.AuthenticationModuleManager}.
 * <p>
 * A module may be capable of supporting the entering of a username, in which
 * case it is known as a <i>Primary Authentication Modules</i>. If this 
 * capability is not available, the module is a <i>Secondary Authentication Module</i>
 * and may only be used after a primary has already been used.
 * <p>
 * There is a third type called a <i>System Authentication Module</i> which is
 * used interally by the Adito or its plugins but never presented to
 * user directly. These are currently used for Webdav and Embedded client
 * logons. 
 * 
 * @see com.adito.security.AuthenticationModuleManager
 * @see com.adito.security.AuthenticationModule
 * @see com.adito.security.AuthenticationScheme
 */
public class AuthenticationModuleDefinition {
    
    // Private instance variables
    
    private String name;
    private Class moduleClass;
    private String messageResourcesKey;
    private boolean primary, secondary, system, primaryIfSecondaryExists;
    
    /**
     * Constructor
     * 
     * @param name module name
     * @param moduleClass class of module
     * @param messageResourcesKey message resources bundle id 
     * @param primary 
     * @param secondary
     * @param system
     */
    public AuthenticationModuleDefinition(String name, Class moduleClass, String messageResourcesKey, boolean primary, boolean secondary, boolean system) {
    	this(name, moduleClass, messageResourcesKey, primary, secondary, system, false);
    }

    /**
     * Constructor
     * 
     * @param name
     * @param moduleClass
     * @param messageResourcesKey
     * @param primary
     * @param secondary
     * @param system
     * @param primaryIfSecondaryExists
     */
    public AuthenticationModuleDefinition(String name, Class moduleClass, String messageResourcesKey, boolean primary, boolean secondary, boolean system, boolean primaryIfSecondaryExists) {
        this.name = name;
        this.moduleClass = moduleClass;
        this.messageResourcesKey = messageResourcesKey;
        this.primary = primary;
        this.secondary = secondary;
        this.system = system;
        this.primaryIfSecondaryExists = primaryIfSecondaryExists;
    }
    
    /**
     * Get if this module is a <i>Primary Authentication Module</i>. If it
     * is then it is capable of providing a username.
     * 
     * @return primary authentication module
     */
    public boolean getPrimary() {
        return primary;
    }
    
    /**
     * 
     * @return
     */
    public boolean getPrimaryIfSecondardExists() {
    	return primaryIfSecondaryExists;
    }
    
    /**
     * Get if this module is a <i>Secondary Authentication Module</i>. If it
     * is then it is capable of providing some authentication details other
     * than the username.
     * 
     * @return secondary authentication module
     */
    public boolean getSecondary() {
        return secondary;
    }
    
    /**
     * Get if this module is a <i>System Authentication Module</i>. If it
     * is then it is used only internally and for supporting other user / password
     * only sub-systems such as the embedded VPN client or WebDAV.
     * 
     * @return system authentication module
     */
    public boolean getSystem() {
        return system;
    }
    
    /**
     * Get the Id of the message resource bundle where the additional
     * resource for name / description of this module may be found.
     * 
     * @return message resource keys
     */
    public String getMessageResourcesKey() {
        return messageResourcesKey;
    }
    
    /**
     * Get the name of this module.
     * 
     * @return name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get the class that may be used to instantiate this module.
     *  
     * @return class of module
     */
    public Class getModuleClass() {
        return moduleClass;
    }
}