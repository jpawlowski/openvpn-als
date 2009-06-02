
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
     * @see net.openvpn.als.properties.PropertyDefinitionCategory#getParent()
     */
    public PropertyDefinitionCategory getParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.properties.PropertyDefinitionCategory#getId()
     */
    public int getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.properties.PropertyDefinitionCategory#getBundle()
     */
    public String getBundle() {
        return bundle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.properties.PropertyDefinitionCategory#getImagePath()
     */
    public String getImagePath() {
        return imagePath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.properties.PropertyDefinitionCategory#addCategory(net.openvpn.als.properties.PropertyDefinitionCategory)
     */
    public PropertyDefinitionCategory addCategory(PropertyDefinitionCategory category) {
        children.add(category);
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.properties.PropertyDefinitionCategory#removeCategory(net.openvpn.als.properties.PropertyDefinitionCategory)
     */
    public PropertyDefinitionCategory removeCategory(PropertyDefinitionCategory category) {
        children.remove(category);
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.properties.PropertyDefinitionCategory#setParent(net.openvpn.als.properties.PropertyDefinitionCategory)
     */
    public void setParent(PropertyDefinitionCategory parent) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.properties.PropertyDefinitionCategory#size()
     */
    public int size() {
        return children.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.properties.PropertyDefinitionCategory#getCategories()
     */
    public List<PropertyDefinitionCategory> getCategories() {
        return children;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.properties.PropertyDefinitionCategory#contains(net.openvpn.als.properties.PropertyDefinitionCategory)
     */
    public boolean contains(PropertyDefinitionCategory category) {
        return children.contains(category);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.properties.PropertyDefinitionCategory#isEnabled()
     */
    public boolean isEnabled() {
        return enabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.properties.PropertyDefinitionCategory#setEnabled(boolean)
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
     * @see net.openvpn.als.properties.PropertyDefinitionCategory#getPropertyClass()
     */
    public PropertyClass getPropertyClass() {
        return propertyClass;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.properties.PropertyDefinitionCategory#setPropertyClass(net.openvpn.als.boot.PropertyClass)
     */
    public void setPropertyClass(PropertyClass propertyClass) {
        this.propertyClass = propertyClass;
    }

	/* (non-Javadoc)
	 * @see net.openvpn.als.boot.PropertyDefinitionCategory#getDefinitions()
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
