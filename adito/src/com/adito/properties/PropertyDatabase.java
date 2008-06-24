
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
			
package com.adito.properties;

import java.util.List;

import com.adito.boot.PropertyDefinition;
import com.adito.core.Database;
import com.adito.properties.attributes.AbstractAttributeKey;
import com.adito.properties.attributes.AttributeDefinition;

/**
 * <p>
 * Property databases are a core component of Adito and are used to store 
 * property definitions, property profiles, property categories and property values.
 * <p>
 * The properties store system wide configuration information, global user
 * configuration and users personal configuration.
 * <p>
 * Many of these methods will be called a <b>lot</b>.  If the storage mechanism
 * for the implementation is slow, you would be advised to maintain some 
 * kind of cache.   
 * 
 * <h3>Property Definitions</h3>
 * 
 * Each property that this database may store is keyed by its <i>id</i>. For
 * each unique <i>id</i> there must have been a single {@link com.adito.boot.PropertyDefinition}
 * registered with this database using {@link #registerPropertyDefinition(PropertyDefinition)}
 * <p>
 * Each definition contains information about the value that will be stored
 * against it. This includes the type of value (string, integer, boolean etc),
 * the bundle in which the human readable name and description is held,
 * its category ID and other information. See {@link com.adito.boot.PropertyDefinition}
 * for more information.
 * 
 *  <h3>Property Categories</h3>
 *  
 *  Each property definition exists under a single category. Categories are
 *  referenced by their <i>id</i> (an integer) and must be registered with
 *  this database using {@link #addPropertyDefinitionCategory(int, PropertyDefinitionCategory)}.
 *  This method also allows categories to be nested within one another by
 *  providing the parent category. Use -1 for a root category.
 *  
 *  <h3>Property Profiles and Values</h3>
 *  
 *  All property values are held in a profile. Profiles provide a way of 
 *  of the user selecting a set of property values depending on the environment
 *  they are in. For example, there may be a profile configured whose properties
 *  are appropriate for using when a user is connecting via the Office Lan. 
 *  The administrator may also have configured a second profile who property 
 *  values are more appropriate when users are connecting from somewhere less
 *  secure, such as an Internet Cafe.
 *  <p>
 *  There are two types of profile, <b>Global</b>, and <b>Personal</b>.  
 *  Global profiles are created by an administrator and then assigned to
 *  users via policies. Many users may share the same profile. Personal profiles
 *  are created by users themselves and are only usable by them. 
 *  <p>
 *  Only property definitions that have a visibility of {@link com.adito.boot.PropertyDefinition#PROFILE}
 *  may exist in global or personal profiles.  
 *  <p>
 *  There also exists a special <b>Default</b> global profile. This profile has an
 *  id of 0 and is also used to the default global profile <strong>and</strong>
 *  system configuration <strong>and</strong> hidden properties. 
 *  
 *  <h3>Context Configuration Properties</h3>
 *  
 *  Properties that are only used for configuring the {@link com.adito.boot.Context}
 *  implementation in use. Properties of this type are different in that their
 *  values are stored and retrieved using {@link com.adito.boot.Context#setContextProperty(String, String)}
 *  and {@link com.adito.boot.Context#getContextProperty(String)}. The 
 *  property definition objects are retrieve using {@link com.adito.boot.Context#getContextPropertyDefinitions()}
 *  (these are cached at start up).
 *  
 *  <h3>System Configuration Properties</h3>
 *  
 *  System configuration properties do not effect users directly. 
 *  Such property definitions have a visibility of 
 *  {@link com.adito.boot.PropertyDefinition#SYSTEM_CONFIGURATION}
 *  
 *  <h3>Hidden Properties</h3>
 *  
 *  Hidden configuration properties do not effect users directly and are used
 *  internally. Such property definitions will return true when 
 *  {@link com.adito.boot.PropertyDefinition#isHidden()} is called.
 */

public interface PropertyDatabase extends Database {

    /**
     * Get a list of all property profiles. If <i>username</i> is <code>null</code>
     * then global profiles are returned. If <i>username</i> is not <code>null</code>
     * then all profiles for the specfied user are returned (in this case, if
     * <i>includeGlobal</i> is <code>true</code> then all global profiles will
     * also be added to the list).
     * 
     * @param username user or <code>null</code> for global profiles
     * @param includeGlobal include global profiles.
     * @param realm ID of the realm it is in.
     * @return List<PropertyProfile> of property profiles
     * @throws Exception
     */
    public List<PropertyProfile> getPropertyProfiles(String username, boolean includeGlobal, int realm) throws Exception;

    /**
     * Get the value of a generic property. If the property has never been set 
     * <code>null</code> will be returned.  
     * <p>
     * Note, this method should never be called directly, to get a property
     * use methods in {@link com.adito.properties.Property}.
     * 
     * @param key1 key 1
     * @param key2 key 2
     * @param key3 key 3
     * @param key4 key 4
     * @param key5 key 5
     * @return value
     * @throws Exception on any error
     */
    public String retrieveGenericProperty(String key1, String key2, String key3, String key4, String key5) throws Exception;

    /**
     * Get a property profile given its ID. <code>null</code> will be returned
     * if no such property profile exists.
     * 
     * @param id id
     * @return property profile
     * @throws Exception
     */
    public PropertyProfile getPropertyProfile(int id) throws Exception;

    /**
     * Set the value of a generic property. 
     * <p>
     * Note, this method should never be called directly, to set a property
     * use methods in {@link com.adito.properties.Property} 
     * as this also fires events.
     * 
     * @param key1 key 1 
     * @param key2 key 2
     * @param key3 key 3
     * @param key4 key 4
     * @param key5 key 5
     * @param value value to set
     * @throws Exception if property cannot be set
     */
    public void storeGenericProperty(String key1, String key2, String key3, String key4, String key5, String value) throws Exception;

    /**
     * Create a new property profile given its basic details
     * 
     * @param username username under which to create the property profile or <code>null</code> for default profiles.
     * @param shortName short name for the profile
     * @param description a description of the profile
     * @param baseOn ID of profile to base values on
     * @param realm th ID of the realm the property is in. 
     * @return created property profile object
     * @throws Exception if profile cannot be created
     */
    public PropertyProfile createPropertyProfile(String username, String shortName, String description, int baseOn, int realm)
                    throws Exception;

    /**
     * Update the basic details of a property profile
     * 
     * @param id id of profile
     * @param shortName short name
     * @param description description
     * @throws Exception if profile cannot be updated
     */
    public void updatePropertyProfile(int id, String shortName, String description) throws Exception;

    /**
     * Delete a property profile given its ID
     * 
     * @param id id of property profile to delete
     * @return deleted profile 
     * @throws Exception if profile cannot be deleted
     */
    public PropertyProfile deletePropertyProfile(int id) throws Exception;

    /**
     * Get a property profile given a username and short name. Profiles should
     * have unique names withing their scope (global / user) and this method
     * is used to check whether a profile of the same name already exists. 
     * <p>
     * <code>null</code> will be returned if no such profile exists.
     * 
     * @param username username or <code>null</code> for global profiles
     * @param name name of profilke
     * @param realm ID for the realm it is in.
     * @return property profile or <code>null</code> if it does not exist.
     * @throws Exception if profile cannot be retrieved
     */
    public PropertyProfile getPropertyProfile(String username, String name, int realm) throws Exception;

    
    /**
     * Set  an attribute. Implementations <strong>must</strong>
     * support the storing of attributes even if they are read-only.
     * <code>null</code> should be used to unset (or remove) the attribute
     * 
     * @param key
     * @param value
     * @throws Exception on any error
     */
    public void storeAttributeValue(AbstractAttributeKey key, String value) throws  Exception;
    
    /**
     * Store a new attribute definition. 
     *
     * @param definition definition to store
     * @throws Exception on any error
     */
    public void createAttributeDefinition(AttributeDefinition definition) throws Exception;
    
    /**
     * Update an existing attribute definition.
     *
     * @param definition definition to update
     * @throws Exception on any error
     */
    public void updateAttributeDefinition(AttributeDefinition definition) throws Exception;
    
    /**
     * Delete an existing attribute definition.
     *
     * @param propertyClassName property class name
     * @param definitionName definition name to delete
     * @throws Exception on any error
     */
    public void deleteAttributeDefinition(String propertyClassName, String definitionName) throws Exception;

    /**
     * Retrieve the value of the attribute. <code>null</code> should be 
     * returned if the attribute isn't set.
     * 
     * @param attrKey attribute key 
     * @return attribute value
     * @throws Exception if attribute cannot be retrieved
     */
    public String retrieveAttributeValue(AbstractAttributeKey attrKey) throws Exception;

}