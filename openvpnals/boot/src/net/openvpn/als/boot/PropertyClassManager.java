package net.openvpn.als.boot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class PropertyClassManager {
    
    final static Log log = LogFactory.getLog(PropertyClassManager.class);
    
    // Private statics
    private static PropertyClassManager instance;
    
    
    //  Private instance variables
    private Map<String, PropertyClass> propertyClasses = new HashMap<String, PropertyClass>();
    
    /*
     * Prevent instantiation
     */
    private PropertyClassManager() {        
    }
    
    /**
     * Get an instance of the property class manager, lazily creating it.
     * 
     * @return instance
     */
    public static PropertyClassManager getInstance() {
        
        synchronized(PropertyClassManager.class) {            
            if(instance == null) {
               instance = new PropertyClassManager();
            }
            return instance;
        }        
        
    }

    /**
     * Register a new property class
     * 
     * @param propertyClass property class
     */
    public void registerPropertyClass(PropertyClass propertyClass) {
        propertyClasses.put(propertyClass.getName(), propertyClass);
    }

    /**
     * Deregister an existing property classes.
     * 
     * @param propertyClassName
     */
    public void deregisterPropertyClass(String propertyClassName) {
        propertyClasses.remove(propertyClassName);
    }

    /**
     * Get a property type given its name.
     * 
     * @param name property class name
     * @return property classes
     */
    public PropertyClass getPropertyClass(String name) {
        return propertyClasses.get(name);
    }

    /**
     * Get an unmodifiable collection of all registered
     * property classes.
     * 
     * @return collection of registered property classes
     */
    public Collection<PropertyClass> getPropertyClasses() {
        return propertyClasses.values();
    }
    
    /**
     * Set whether the setting of property values should be persisted immediately 
     * to the underlying store or deferred until the {@link #commit()} method is 
     * called. 
     * 
     * NOTE This is not proper transaction support and should only be used
     * in a single user instance of OpenVPNALS such as the installation wizard.
     * 
     * @param autoCommit <true>code</code> to persist properties immediately
     */
    public synchronized void setAutoCommit(boolean autoCommit) {
        for(PropertyClass propertyClass : propertyClasses.values()) {
            propertyClass.setAutoCommit(autoCommit);
        }
    }

    /**
     * Commits any stored properties to the underlying store. 
     * 
     * @see #setAutoCommit(boolean)
     */
    public synchronized void commit() {
        for(PropertyClass propertyClass : propertyClasses.values()) {
            propertyClass.commit();
        }
    }

	/**
	 * Get all definitions for the specified property class and any property classes
	 * that extend or implement the supplied Java class.
	 *  
	 * @param clazz class
	 * @return list of property definitions
	 */
	public Collection<PropertyDefinition> getDefinitions(Class clazz) {
		List<PropertyDefinition> l = new ArrayList<PropertyDefinition>();
        for(PropertyClass propertyClass : propertyClasses.values()) {
        	if(clazz.isAssignableFrom(propertyClass.getClass())) {
        		l.addAll(propertyClass.getDefinitions());
        	}
        }
        return l;
	}
}
