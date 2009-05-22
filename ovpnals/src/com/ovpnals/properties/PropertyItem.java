
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.properties;

import com.ovpnals.boot.PropertyDefinition;

/**
 * Interface for property items.
 */
public interface PropertyItem {

    /**
     * Get the message resource key (i.e. bundle ID) of the bundle that
     * contains the message resources for this property definition.
     * 
     * @return message resources key (bundle)
     */
    public String getMessageResourcesKey();

    /**
     * Get the number of columns appropriate for this property definition. Only
     * applies to property definitions of type
     * {@link PropertyDefinition#TYPE_PASSWORD},
     * {@link PropertyDefinition#TYPE_STRING},
     * {@link PropertyDefinition#TYPE_TEXT_AREA} and
     * {@link PropertyDefinition#TYPE_TIME_IN_MS}
     * 
     * @return number of columns
     */
    public int getColumns();

    /**
     * Get the number of row appropriate for this property definition. Only
     * applies to property definitions of type
     * {@link PropertyDefinition#TYPE_TEXT_AREA}.
     * 
     * @return number of rows
     */
    public int getRows();

    /**
     * Get the property definition this object wraps
     * 
     * @return property definition
     */
    public PropertyDefinition getDefinition();

    /**
     * Set the property definition this object wraps
     * 
     * @param definition property definition
     */
    public void setDefinition(PropertyDefinition definition);

    /**
     * Convenience method to get the name of the property definition this object wraps.
     * 
     * @return the name.
     */
    public String getName();

    /**
     * Convenience method to get the category ID this property definition this 
     * object wraps is in.
     * 
     * @return the category ID.
     */
    public int getCategory();

    /**
     * Convenience method to get the default value of the property definition 
     * this object wraps.
     * 
     * @return Returns the value.
     */
    public String getDefaultValue();

    /**
     * Get the default of the property definition this object wraps as 
     * text.
     * 
     * @return Returns the value.
     */
    public String getDefaultText();

    /**
     * Convenience method to get the property definition 'type meta', the 
     * string that describes and constraints for the type of property definition. 
     * For example 40x5 may be the 'type meta' for a property definition of 
     * type {@link PropertyDefinition#TYPE_TEXT_AREA} means supply a text area
     * with 40 columns and 5 rows.
     * 
     * @return get the type meta
     */
    public String getTypeMeta();

    /**
     * @return Returns the value.
     */
    public Pair[] getListItems();

    /**
     * Get the value as an object
     * 
     * @return the value.
     */
    public Object getValue();

    /**
     * For boolean property definitions such as Checkbox 
     * that will return <code>true</code> if the value is true. 
     * 
     * @return selected.
     */
    public boolean getSelected();

    /**
     * Set the value as a boolean. For boolean property definitions such as 
     * checkbox.
     * 
     *  @param selected selected
     */
    public void setSelected(boolean selected);

    /**
     * Set the value from a generic object. The actual value will
     * be determined depending on the property definitions type.
     * 
     * @param value the value to set.
     */
    public void setValue(Object value);

    /**
     * Convenience method to get the type of the property definition. See
     * {@link PropertyDefinition#getType()} for a more details description.
     * 
     * @return type
     */
    public int getType();

    /**
     * Get the value as an object suitable for displaying with the appropriate
     * component. For most types a {@link String} is returned, but other
     * default struts supported type are also used e.g. {@link Boolean} or
     * {@link Integer}.
     * 
     * @return value
     */
    public Object getPropertyValue();
    
    /**
     * Get the label. This will either the label in the definition itself or a
     * label from a message resource (passed in the constructor) if it exists.
     * 
     * @return label
     */

    public String getLabel();
}