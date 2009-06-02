
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
			
package net.openvpn.als.boot;

import java.util.Collection;

/**
 * Property definitions may be categorised. Implementations of this interface
 * provide additional data about categories such as the bundle from which to get
 * resources and the path to use for images.
 * 
 * @see net.openvpn.als.properties.PropertyDatabase
 */
public interface PropertyDefinitionCategory {

    /**
     * Get the ID for this property category
     * 
     * @return id
     */
    public int getId();

    /**
     * Get the key for the bundle containing message resources for this category
     * 
     * @return message bundle key
     */
    public String getBundle();

    /**
     * Get the page for the image for this category
     * 
     * @return image page
     */
    public String getImagePath();

    /**
     * Add a new child category to this category
     * 
     * @param category category to add
     * @return this category
     */
    public PropertyDefinitionCategory addCategory(PropertyDefinitionCategory category);

    /**
     * Remove a child category from this category
     * 
     * @param category category to remove
     * @return this category
     */

    public PropertyDefinitionCategory removeCategory(PropertyDefinitionCategory category);

    /**
     * Set the parent or <code>null</code> if at the root.
     * 
     * @param parent parent
     */
    public void setParent(PropertyDefinitionCategory parent);

    /**
     * Get the number of sub-categories
     * 
     * @return number of sub-categories
     */
    public int size();

    /**
     * Get if this category contains another
     * 
     * @param category category to test
     * @return <code>true</code> if this category contains the one specified
     */
    public boolean contains(PropertyDefinitionCategory category);

    /**
     * Get an umodifiable collection of all child categories.
     * 
     * @return child categories
     */
    public Collection<PropertyDefinitionCategory> getCategories();

    /**
     * Enable / disable this category. When disabled it will not appear in the
     * user interface.
     * 
     * @param enabled category enabled
     */
    public void setEnabled(boolean enabled);

    /**
     * Enabled / disable this category. When disabled it will not appear in the
     * user interface.
     * 
     * @return category enabled
     */
    public boolean isEnabled();

    /**
     * Get the property class this category is registered under.
     * 
     * @return property class
     */
    public PropertyClass getPropertyClass();

    /**
     * Set the property class this category is registered under.
     * 
     * @param propertyClass property class
     */
    public void setPropertyClass(PropertyClass propertyClass);

    /**
     * Get this categories parent or <code>null</code> if there is no parent.
     * 
     * @return parent
     */
    public PropertyDefinitionCategory getParent();
    
    /**
     * Get all definitions found in this category
     * 
     * @return definitions
     */
    public Collection<PropertyDefinition> getDefinitions();

}