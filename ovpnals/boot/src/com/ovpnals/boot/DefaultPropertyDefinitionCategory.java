
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
			
package com.ovpnals.boot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Default implementation of a {@link PropertyDefinitionCategory}.
 * 
 */
public class DefaultPropertyDefinitionCategory implements PropertyDefinitionCategory, Serializable {

    // Private instance variables

    private int id;
    private String imagePath;
    private String bundle;
    private List<PropertyDefinitionCategory> children;
    private PropertyDefinitionCategory parent;
    private boolean enabled = true;
    private transient PropertyClass propertyClass;

    /**
     * Constructor.
     * 
     * @param id category id
     * @param bundle bundle
     * @param imagePath path for image
     */
    public DefaultPropertyDefinitionCategory(int id, String bundle, String imagePath) {
        this.id = id;
        this.imagePath = imagePath;
        this.bundle = bundle;
        children = new ArrayList<PropertyDefinitionCategory>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        return o instanceof PropertyDefinitionCategory && ((PropertyDefinitionCategory) o).getId() == getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyDefinitionCategory#getParent()
     */
    public PropertyDefinitionCategory getParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyDefinitionCategory#getId()
     */
    public int getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyDefinitionCategory#getBundle()
     */
    public String getBundle() {
        return bundle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyDefinitionCategory#getImagePath()
     */
    public String getImagePath() {
        return imagePath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyDefinitionCategory#addCategory(com.ovpnals.properties.PropertyDefinitionCategory)
     */
    public PropertyDefinitionCategory addCategory(PropertyDefinitionCategory category) {
        children.add(category);
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyDefinitionCategory#removeCategory(com.ovpnals.properties.PropertyDefinitionCategory)
     */
    public PropertyDefinitionCategory removeCategory(PropertyDefinitionCategory category) {
        children.remove(category);
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyDefinitionCategory#setParent(com.ovpnals.properties.PropertyDefinitionCategory)
     */
    public void setParent(PropertyDefinitionCategory parent) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyDefinitionCategory#size()
     */
    public int size() {
        return children.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyDefinitionCategory#getCategories()
     */
    public List<PropertyDefinitionCategory> getCategories() {
        return children;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyDefinitionCategory#contains(com.ovpnals.properties.PropertyDefinitionCategory)
     */
    public boolean contains(PropertyDefinitionCategory category) {
        return children.contains(category);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyDefinitionCategory#isEnabled()
     */
    public boolean isEnabled() {
        return enabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyDefinitionCategory#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        for(PropertyDefinitionCategory cat : children) {
            cat.setEnabled(enabled);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyDefinitionCategory#getPropertyClass()
     */
    public PropertyClass getPropertyClass() {
        return propertyClass;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyDefinitionCategory#setPropertyClass(com.ovpnals.boot.PropertyClass)
     */
    public void setPropertyClass(PropertyClass propertyClass) {
        this.propertyClass = propertyClass;
    }

	/* (non-Javadoc)
	 * @see com.ovpnals.boot.PropertyDefinitionCategory#getDefinitions()
	 */
	public Collection<PropertyDefinition> getDefinitions() {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
		if(propertyClass == null) {
			return l;
		}
		for(PropertyDefinition def : propertyClass.getDefinitions()) {
			if(def.getCategory() == getId()) {
				l.add(def);
			}
		}
		Collections.sort(l);
		return l;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[parent=" + getParent() + ", id=" + getId() + ", bundle=" + getBundle() + ", image=" + getImagePath() + "]";
	}
}
