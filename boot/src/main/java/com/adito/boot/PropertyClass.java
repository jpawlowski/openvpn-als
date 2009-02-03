
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
			
package com.adito.boot;

import java.io.IOException;
import java.util.Collection;


/**
 * Interface to be implemented by all <i>Property Classes</i>.
 */
public interface PropertyClass extends Comparable<PropertyClass> {
    /**
     * Get the name of this property type
     * 
     * @return property type name
     */
    public String getName();
    
    /**
     * Register a new property definition.
     * 
     * @param propertyDefinition definition to register
     */
    public void registerPropertyDefinition(PropertyDefinition propertyDefinition);
    
    /**
     * Deregister an existing property definition
     * 
     * @param propertyDefinitionName name of property definition to deregister
     */
    public void deregisterPropertyDefinition(String propertyDefinitionName);
    
    /**
     * Get a single registered property definition.
     * 
     * @param name name of property definition 
     * @return property definition
     */
    public PropertyDefinition getDefinition(String name);

    /**
     * Get a list of all registered property definitions for this type.
     *  
     * @return property definitions
     */
    public Collection<PropertyDefinition> getDefinitions();

    /**
     * Get the value of a property. If the property has never been set then
     * the default value provided in the {@link PropertyDefinition} will
     * be returned. <code>null</code> should never be returned. 
     *  
     * 
     * @param key property key
     * @return value
     * @throws IllegalArgumentException if property doesn't exist
     */
    public String retrieveProperty(AbstractPropertyKey key) throws IllegalArgumentException;


    /**
     * Retrieve a property as an integer. The value will be retrieved as a string
     * then converted to a primitive int.
     * <p>
     * If the property has never been set then the default value provided in the
     * {@link PropertyDefinition} will be returned.
     * 
     * @param key property key
     * @return value
     * @throws IllegalArgumentException if property doesn't exist
     */
    public int retrievePropertyInt(AbstractPropertyKey key) throws IllegalArgumentException;

    /**
     * Retrieve a property as a long. The value will be retrieved as a string then
     * converted to a primitive long.
     * <p>
     * If the property has never been set then the default value provided in the
     * {@link PropertyDefinition} will be returned.
     * 
     * @param key property key
     * @return value
     * @throws IllegalArgumentException if property doesn't exist
     */
    public long retrievePropertyLong(AbstractPropertyKey key) throws IllegalArgumentException;

    /**
     * Retrieve a property as a boolen. The value will be retrieved as a string then
     * converted to a primitive boolean, <code>true</code> if the string value
     * is <i>true</i> otherwise <code>false</code>.
     * <p>
     * If the property has never been set then the default value provided in the
     * {@link PropertyDefinition} will be returned.
     * <p>
     * 
     * @param key property key
     * @return value
     * @throws IllegalArgumentException if property doesn't exist
     */
    public boolean retrievePropertyBoolean(AbstractPropertyKey key) throws IllegalArgumentException;

    /**
     * Retrieve a property as a list. The value will be retrieved as a string then
     * converted to a {@link PropertyList}.
     * <p>
     * If the property has never been set then the default value provided in the
     * {@link PropertyDefinition} will be returned.
     * 
     * @param key property key
     * @return value
     * @throws IllegalArgumentException if property doesn't exist
     */
    public PropertyList retrievePropertyList(AbstractPropertyKey key) throws IllegalArgumentException;
    
    /**
     * Add a new category definition. Categories may be nested so you must also
     * provide the parent category ID of the category to add. Use <code>-1</code>
     * to add categories to the root of the tree.
     * 
     * @param parentId parent category ID or -1 for root
     * @param category category to add
     */
    public void addPropertyDefinitionCategory(int parentId, PropertyDefinitionCategory category);

    /**
     * Remove a category from its parent. Use -1 to remove categories at the
     * root of the tree.
     * 
     * @param parentId parent category ID or -1 for root
     * @param category category to remove
     */
    public void removePropertyDefinitionCategory(int parentId, PropertyDefinitionCategory category);

    /**
     * Get a list of all property definition categories for this property
     * class
     * 
     * @return list of categories
     */
    public Collection<PropertyDefinitionCategory> getCategories();
    
    /**
     * Get a category definition given its ID. <code>null</code> will be 
     * returned if no such category exists.
     * 
     * @param id id of category definition to retrieve 
     * @return category definition.
     */
    public PropertyDefinitionCategory getPropertyDefinitionCategory(int id);

    /**
     * Set the value of a property. <code>null</code> should never be provided.
     * <p> 
     * Note, if {@link #setAutoCommit(boolean)} has been set to true then
     * the property value should not be persisted to the underlying store until
     * the {@link #commit()} method is called. 
     *  
     * @param key property key
     * @param value value
     * @return old value
     * @throws IllegalArgumentException if property doesn't exist
     */
    public String storeProperty(AbstractPropertyKey key, String value) throws IllegalArgumentException;
    
    /**
     * Get if this property class supports replacement variables in its 
     * values. If its does, the text should be expanded when used and care
     * take never to store expanded values. 
     * 
     * @return supports replacement variables in values
     */
    public boolean isSupportsReplacementVariablesInValues();

    /**
     * Get if the named property definition exists in this property
     * class.
     * 
     * @param name property definition name
     * @return exists
     */
    public boolean isDefinitionExists(String name);
    
    /**
     * Set whether the setting of property values should be persisted immediately 
     * to the underlying store or deferred until the {@link #commit()} method is 
     * called. 
     * 
     * NOTE This is NOT proper transaction support and should only be used
     * in a single user instance of Adito such as the installation wizard.
     * 
     * @param autoCommit <true>code</code> to persist properties immediately
     */
    public void setAutoCommit(boolean autoCommit);
    
    /**
     * Commits any stored properties to the underlying store. 
     * 
     * @see #setAutoCommit(boolean)
     */
    public void commit();

	/**
	 * Remove all property definitions
	 */
	public void clearPropertyDefinitions();

	/**
	 * Remove all property definition categories
	 */
	public void clearPropertyDefinitionCategories();

	/**
	 * Add a collection of property definitions to this call.
	 *  
	 * @param propertyDefinitions property definitions
	 */
	public void addPropertyDefinitions(Collection<PropertyDefinition> propertyDefinitions);

	/**
	 * Add a collection of property definitions to this call.
	 *  
	 * @param propertyDefinitionCategories property definitions
	 */
	public void addPropertyDefinitionCategories(Collection<PropertyDefinitionCategory> propertyDefinitionCategories);
    
    /**
     * Store all definitions and categories so they may be restored using
     * the {@link #restore()} method. This is used to roll back and changes
     * that may have been made to a property class during extension loading.
     * 
     * @throws IOException 
     */
    public void store() throws IOException;
    
    /**
     * Reset any stored definitions and categories.
     */
    public void reset();
    
    /**
     * Restore any stored definitions and categories.
     * 
     * @throws IOException
     */
    public void restore() throws IOException;
}
