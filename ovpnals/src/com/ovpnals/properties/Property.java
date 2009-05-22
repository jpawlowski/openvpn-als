
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.boot.AbstractPropertyKey;
import com.ovpnals.boot.PropertyClass;
import com.ovpnals.boot.PropertyClassManager;
import com.ovpnals.boot.PropertyDefinition;
import com.ovpnals.boot.PropertyList;
import com.ovpnals.core.CoreEventConstants;
import com.ovpnals.core.CoreServlet;
import com.ovpnals.properties.impl.profile.ProfilePropertyKey;
import com.ovpnals.security.SessionInfo;

/**
 * Utility methods for dealing dealing with property values and definition. In
 * general, all client code should access properties via these methods.
 */
public class Property {

    final static Log log = LogFactory.getLog(Property.class);

    /*
     * Prevent instantiation
     */
    private Property() {
    }

    /**
     * Convenience method to search all property types for the specified
     * definition. If no such definition can be found in any type
     * <code>null</code> will be returned.
     * 
     * @param key key
     * @return definition or <code>null</codE> if no definition could be found
     */
    public static PropertyDefinition getDefinition(AbstractPropertyKey key) {
        PropertyClassManager mgr = PropertyClassManager.getInstance();
        PropertyClass propertyClass = mgr.getPropertyClass(key.getPropertyClassName());
        if (propertyClass == null) {
            throw new IllegalArgumentException("Invalid property class " + key.getPropertyClassName() + ".");
        }
        return propertyClass.getDefinition(key.getName());
    }

    /**
     * Set the value for property and fire the appropriate events. An event will
     * only be fired if the value of the property changes. <code>null</code>
     * will be returned if the property cannot be set for some reason.
     * 
     * @param key property key
     * @param newValue new value
     * @param sessionInfo session info
     * @return old value
     */
    public static String setProperty(AbstractPropertyKey key, String newValue, SessionInfo sessionInfo) {

        PropertyDefinition def = getDefinition(key);
        PropertyProfile p = null;
        try {
            PropertyClass t = def.getPropertyClass();
            String oldVal = t.storeProperty(key, newValue);
            if ( ( oldVal == null && newValue != null ) || !oldVal.equals(newValue)) {
                if (key instanceof ProfilePropertyKey) {
                    p = ProfilesFactory.getInstance().getPropertyProfile(((ProfilePropertyKey) key).getProfile());
                }
                CoreServlet.getServlet().fireCoreEvent(new PropertyChangeEvent(def,
                                CoreEventConstants.PROPERTY_CHANGED,
                                def,
                                sessionInfo,
                                p,
                                oldVal,
                                newValue,
                                PropertyChangeEvent.STATE_SUCCESSFUL));
            }
            return oldVal;
        } catch (Exception e) {
            log.error("Failed to set property.", e);
            CoreServlet.getServlet().fireCoreEvent(new PropertyChangeEvent(def,
                            CoreEventConstants.PROPERTY_CHANGED,
                            def,
                            sessionInfo,
                            p,
                            null,
                            newValue,
                            PropertyChangeEvent.STATE_UNSUCCESSFUL));
        }
        return null;
    }

    /**
     * Set the value for property and fire the appropriate events. An event will
     * only be fired if the value of the property changes. <code>null</code>
     * will be returned if the property cannot be set for some reason.
     * 
     * @param key property key
     * @param newValue new value
     * @param sessionInfo session info
     * @return old value
     */
    public static boolean setProperty(AbstractPropertyKey key, boolean newValue, SessionInfo sessionInfo) {
        String oldVal = setProperty(key, String.valueOf(newValue), sessionInfo);
        assert oldVal != null; // booleans must have default
        return Boolean.parseBoolean(oldVal);
    }

    /**
     * 
     * Set the value for property and fire the appropriate events. An event will
     * only be fired if the value of the property changes. <code>null</code>
     * will be returned if the property cannot be set for some reason.
     * 
     * @param key key
     * @param newValue new value
     * @param sessionInfo session info
     * @return old value
     */
    public static PropertyList setProperty(AbstractPropertyKey key, PropertyList newValue, SessionInfo sessionInfo) {
        String oldVal = setProperty(key, newValue.getAsPropertyText(), sessionInfo);
        return oldVal == null ? null : new PropertyList(oldVal);

    }

    /**
     * 
     * Set the value for property and fire the appropriate events. An event will
     * only be fired if the value of the property changes. <code>-1</code>
     * will be returned if the property cannot be set for some reason.
     * 
     * @param key key
     * @param newValue new value
     * @param sessionInfo session info
     * @return old value
     */
    public static int setProperty(AbstractPropertyKey key, int newValue, SessionInfo sessionInfo) {
        String oldVal = setProperty(key, String.valueOf(newValue), sessionInfo);
        return oldVal == null ? -1 : Integer.parseInt(oldVal);
    }

    /**
     * 
     * Set the value for property and fire the appropriate events. An event will
     * only be fired if the value of the property changes. <code>-1</code>
     * will be returned if the property cannot be set for some reason.
     * 
     * @param key key
     * @param newValue new value
     * @param sessionInfo session info
     * @return old value
     */
    public static long setProperty(AbstractPropertyKey key, long newValue, SessionInfo sessionInfo) {
        String oldVal = setProperty(key, String.valueOf(newValue), sessionInfo);
        return oldVal == null ? -1 : Long.parseLong(oldVal);
    }

    /**
     * Get a property as an string.
     * <p>
     * If the property has never been set then the default value provided in the
     * {@link PropertyDefinition} will be returned.
     * 
     * @param key property key
     * @return value
     * @throws IllegalArgumentException if property doesn't exist
     */
    public static String getProperty(AbstractPropertyKey key) throws IllegalArgumentException {
        PropertyDefinition def = getDefinition(key);
        if (def == null) {
            throw new IllegalArgumentException("Invalid key. " + key);
        }
        PropertyClass t = def.getPropertyClass();
        return t.retrieveProperty(key);
    }

    /**
     * Get a property as an integer. The value will be retrieved as a string
     * then converted to a primitive int.
     * <p>
     * If the property has never been set then the default value provided in the
     * {@link PropertyDefinition} will be returned.
     * 
     * @param key property key
     * @return value
     * @throws IllegalArgumentException if property doesn't exist
     */
    public static int getPropertyInt(AbstractPropertyKey key) throws IllegalArgumentException {
        return Integer.parseInt(getProperty(key));
    }

    /**
     * Get a property as a long. The value will be retrieved as a string then
     * converted to a primitive long.
     * <p>
     * If the property has never been set then the default value provided in the
     * {@link PropertyDefinition} will be returned.
     * 
     * @param key property key
     * @return value
     * @throws IllegalArgumentException if property doesn't exist
     */
    public static long getPropertyLong(AbstractPropertyKey key) throws IllegalArgumentException {
        return Long.parseLong(getProperty(key));
    }

    /**
     * Get a property as a boolen. The value will be retrieved as a string then
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
    public static boolean getPropertyBoolean(AbstractPropertyKey key) throws IllegalArgumentException {
        return Boolean.parseBoolean(getProperty(key));
    }

    /**
     * Get a property as a list. The value will be retrieved as a string then
     * converted to a {@link PropertyList}.
     * <p>
     * If the property has never been set then the default value provided in the
     * {@link PropertyDefinition} will be returned.
     * 
     * @param key property key
     * @return value
     * @throws IllegalArgumentException if property doesn't exist
     */
    public static PropertyList getPropertyList(AbstractPropertyKey key) throws IllegalArgumentException {
        return new PropertyList(getProperty(key));
    }

}
