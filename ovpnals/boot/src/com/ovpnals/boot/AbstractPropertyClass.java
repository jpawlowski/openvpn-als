
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Default {@link PropertyClass}.
 * 
 */
public abstract class AbstractPropertyClass implements PropertyClass {
    
    final static Log log = LogFactory.getLog(AbstractPropertyClass.class);

    // Private instance variables
    private Map<String, PropertyDefinition> definitions;
    private String name;
    private List<PropertyDefinitionCategory> categories;
    private Map<String, PropertyDefinitionCategory> categoryMap;
    private boolean supportsReplacementVariablesInValues;
    private boolean autoCommit = true;
    private Map<AbstractPropertyKey,String> values = new HashMap<AbstractPropertyKey, String>();
    private ByteArrayOutputStream store;

	/**
     * Constructor.
     * 
     * @param name property type name
     * @param supportsReplacementVariablesInValues 
     * @throws IllegalArgumentException on illegal type name
     */
    public AbstractPropertyClass(String name, boolean supportsReplacementVariablesInValues) {
        if(name.contains(" ")) {
            throw new IllegalArgumentException("Property type name may not contain spaces.");
        } 
        this.supportsReplacementVariablesInValues = supportsReplacementVariablesInValues;
        definitions = new TreeMap<String, PropertyDefinition>();
        categories = new ArrayList<PropertyDefinitionCategory>();
        categoryMap = new HashMap<String, PropertyDefinitionCategory>();
        this.name = name;
    }
    
    public void store() throws IOException {
    	if(store != null) {
    		throw new IllegalStateException("Already storing property class. Either restor or reset first.");
    	}
    	store = new ByteArrayOutputStream();
    	ObjectOutputStream oos = new ObjectOutputStream(store);
    	try {
	    	oos.writeObject(definitions);
	    	oos.writeObject(categories);
	    	oos.writeObject(categoryMap);
    	}
    	finally {
    		oos.close();
    	}
    }
    
    public void reset() {
    	store = null;
    }
    
    public void restore() throws IOException {
    	log.info("Restoring property class " + getName());
    	if(store == null) {
    		throw new IllegalStateException("Nothing stored for " + getName());
    	}
    	ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(store.toByteArray())) {
            protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                try {
                    return Class.forName(desc.getName(), false, AbstractPropertyClass.this.getClass().getClassLoader());
                } catch (ClassNotFoundException ex) {
                    return super.resolveClass(desc);
                }
            }
        };
    	try {
    		definitions = (Map<String, PropertyDefinition>) ois.readObject();
    		categories = (List<PropertyDefinitionCategory>) ois.readObject();
    		categoryMap = (Map<String, PropertyDefinitionCategory>) ois.readObject();
    		store = null;

    		
    		// PropertyClass member variable is transient so we need to reinitialise
    		for(PropertyDefinition def : definitions.values())
    			def.init(this);
    		for(PropertyDefinitionCategory cat : categories)
    			cat.setPropertyClass(this);
    	}
    	catch(ClassNotFoundException cnfe) {
    		throw new IOException("Deserialisation failed. " + cnfe.getMessage());
    	}
    	finally {
    		ois.close();
    	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyType#addPropertyDefinitionCategory(int,
     *      com.ovpnals.properties.PropertyDefinitionCategory)
     */
    public void addPropertyDefinitionCategory(int parentId, PropertyDefinitionCategory category) {
        PropertyDefinitionCategory parent = parentId == -1 ? null : categoryMap.get(String.valueOf(parentId));
        if (parent != null) {
            category.setParent(parent);
            if (parent.contains(category)) {
            	parent.removeCategory(category);
            }
            parent.addCategory(category);
        } else {
        	if(categories.contains(category)) {
                categories.remove(category);
        	}
            categories.add(category);
        }
        categoryMap.put(String.valueOf(category.getId()), category);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyType#getPropertyDefinitionCategory(int)
     */
    public PropertyDefinitionCategory getPropertyDefinitionCategory(int id) {
        return categoryMap.get(String.valueOf(id));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyType#removePropertyDefinitionCategory(int,
     *      com.ovpnals.properties.PropertyDefinitionCategory)
     */
    public void removePropertyDefinitionCategory(int parentId, PropertyDefinitionCategory category) {
        PropertyDefinitionCategory parent = parentId == -1 ? null : categoryMap.get(String.valueOf(parentId));
        if (parent != null) {
            parent.removeCategory(category);
        }

    }

    /* (non-Javadoc)
     * @see com.ovpnals.properties.PropertyType#deregisterPropertyDefinition(java.lang.String)
     */
    public void deregisterPropertyDefinition(String propertyDefinitionName) {
        definitions.remove(propertyDefinitionName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyType#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyType#getPropertyDefinition(java.lang.String)
     */
    public PropertyDefinition getDefinition(String name) {
        return definitions.get(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyType#getPropertyDefinitions()
     */
    public Collection<PropertyDefinition> getDefinitions() {
        List<PropertyDefinition> l = new ArrayList<PropertyDefinition>(definitions.values());
        Collections.sort(l);
        return l;
    }

    /* (non-Javadoc)
     * @see com.ovpnals.boot.PropertyClass#isDefinitionExists(java.lang.String)
     */
    public boolean isDefinitionExists(String name) {
        return definitions.containsKey(name);
    }

    /* (non-Javadoc)
     * @see com.ovpnals.boot.PropertyClass#retrieveProperty(com.ovpnals.boot.AbstractPropertyKey)
     */
    public String retrieveProperty(AbstractPropertyKey key) throws IllegalArgumentException {
        synchronized(values) {
            if(values.containsKey(key)) {
                return (String)values.get(key);
            }
        }
        return retrievePropertyImpl(key);
    }

    /**
     * Retrieve the property from the underlying store.
     *  
     * @param key key 
     * @return value
     * @throws IllegalArgumentException if key is incorrect
     */
    protected abstract String retrievePropertyImpl(AbstractPropertyKey key) throws IllegalArgumentException;

    /* (non-Javadoc)
     * @see com.ovpnals.boot.PropertyClass#storeProperty(com.ovpnals.boot.AbstractPropertyKey, java.lang.String)
     */
    public synchronized String storeProperty(AbstractPropertyKey key, String value) throws IllegalArgumentException {
        if(!autoCommit) {
            if(values.containsKey(key)) {
                return values.put(key, value);
            }
            else {
                String val = retrieveProperty(key);
                values.put(key, value);
                return val;
            }
        }
        else {
            return storePropertyImpl(key, value);
        }
    }
    
    /* (non-Javadoc)
     * @see com.ovpnals.boot.PropertyClass#storeProperty(com.ovpnals.boot.AbstractPropertyKey, java.lang.String)
     */
    protected abstract String storePropertyImpl(AbstractPropertyKey key, String value) throws IllegalArgumentException;

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyType#registerPropertyDefinition(com.ovpnals.boot.PropertyDefinition)
     */
    public void registerPropertyDefinition(PropertyDefinition propertyDefinition) {
    	propertyDefinition.init(this);
        definitions.put(propertyDefinition.getName(), propertyDefinition);
    }

    /* (non-Javadoc)
     * @see com.ovpnals.boot.PropertyClass#commit()
     */
    public synchronized void commit() {
        for(AbstractPropertyKey key : values.keySet()) {
            storePropertyImpl(key, values.get(key));
        }
        values.clear();
    }

    /* (non-Javadoc)
     * @see com.ovpnals.boot.PropertyClass#setAutoCommit(boolean)
     */
    public synchronized void setAutoCommit(boolean autoCommit) {
        if(!autoCommit && this.autoCommit && values.size() > 0) {
            throw new RuntimeException("Cannot unset auto commit when there are values to be commited.");
        }
        this.autoCommit = autoCommit;
    }

    /* (non-Javadoc)
     * @see com.ovpnals.boot.PropertyClass#getCategories()
     */
    public Collection<PropertyDefinitionCategory> getCategories() {
        return categories;
    }

    /* (non-Javadoc)
     * @see com.ovpnals.boot.PropertyClass#isSupportsReplacementVariablesInValues()
     */
    public boolean isSupportsReplacementVariablesInValues() {
        return supportsReplacementVariablesInValues;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(PropertyClass o) {
		return getName().compareTo(o.getName());
	}

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
    public int retrievePropertyInt(AbstractPropertyKey key) throws IllegalArgumentException {
        return Integer.parseInt(retrieveProperty(key));
    }

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
    public long retrievePropertyLong(AbstractPropertyKey key) throws IllegalArgumentException {
        return Long.parseLong(retrieveProperty(key));
    }

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
    public boolean retrievePropertyBoolean(AbstractPropertyKey key) throws IllegalArgumentException {
        return Boolean.parseBoolean(retrieveProperty(key));
    }

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
    public PropertyList retrievePropertyList(AbstractPropertyKey key) throws IllegalArgumentException {
        return new PropertyList(retrieveProperty(key));
    }

	/* (non-Javadoc)
	 * @see com.ovpnals.boot.PropertyClass#addPropertyDefinitionCategories(java.util.Collection)
	 */
	public void addPropertyDefinitionCategories(Collection<PropertyDefinitionCategory> propertyDefinitionCategories) {
		for(PropertyDefinitionCategory cat : propertyDefinitionCategories) {
			addPropertyDefinitionCategory(cat.getParent() == null ? -1 : cat.getParent().getId(), cat);
		}		
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.boot.PropertyClass#addPropertyDefinitions(java.util.Collection)
	 */
	public void addPropertyDefinitions(Collection<PropertyDefinition> propertyDefinitions) {
		for(PropertyDefinition def : propertyDefinitions) {
			registerPropertyDefinition(def);
		}		
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.boot.PropertyClass#clearPropertyDefinitionCategories()
	 */
	public void clearPropertyDefinitionCategories() {
		categories.clear();
		categoryMap.clear();
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.boot.PropertyClass#clearPropertyDefinitions()
	 */
	public void clearPropertyDefinitions() {
		definitions.clear();				
	}
}
