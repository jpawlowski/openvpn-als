
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
			
package net.openvpn.als.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

import org.apache.struts.util.MessageResourcesFactory;
import org.apache.struts.util.PropertyMessageResources;

/**
 * Extension of the standard struts {@link org.apache.struts.util.PropertyMessageResources}
 * that is capable of loading resources from OpenVPNALS plugins. 
 * 
 * @see net.openvpn.als.core.CoreMessageResourcesFactory
 */
public class CoreMessageResources extends PropertyMessageResources {
    
    // Private instance variables
    
    private ClassLoader classLoader;

    /**
     * Constructor
     * 
     * @param factory factory
     * @param config config
     * @param classLoader class loader 
     */
    public CoreMessageResources(MessageResourcesFactory factory, String config, ClassLoader classLoader) {
        super(factory, config);
        this.classLoader =  classLoader;
    }

    /* (non-Javadoc)
     * @see org.apache.struts.util.PropertyMessageResources#loadLocale(java.lang.String)
     */
    protected synchronized void loadLocale(String localeKey) {

        if (log.isTraceEnabled()) {
            log.trace("loadLocale(" + localeKey + ")");
        }
        
        // Have we already attempted to load messages for this locale?
        if (locales.get(localeKey) != null) {
            return;
        }
        
        locales.put(localeKey, localeKey);

        // Set up to load the property resource for this locale key, if we can
        String name = config.replace('.', '/');
        if (localeKey.length() > 0) {
            name += "_" + localeKey;
        }
        
        name += ".properties";
        InputStream is = null;
        Properties props = new Properties();

        // Load the specified property resource
        if (log.isTraceEnabled()) {
            log.trace("  Loading resource '" + name + "'");
        }
                
        is = classLoader.getResourceAsStream(name);
        if (is != null) {
            try {
                props.load(is);
                
            } catch (IOException e) {
                log.error("loadLocale()", e);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("loadLocale()", e);
                }
            }
        }
        else {
        	if (log.isDebugEnabled())
        		log.debug("Failed to locate message resources " + name + ".");
        }
        
        if (log.isTraceEnabled()) {
            log.trace("  Loading resource completed");
        }

        // Copy the corresponding values into our cache
        if (props.size() < 1) {
            return;
        }
        
        synchronized (messages) {
            Iterator names = props.keySet().iterator();
            while (names.hasNext()) {
                String key = (String) names.next();
                if (log.isTraceEnabled()) {
                    log.trace("  Saving message key '" + messageKey(localeKey, key));
                }
                messages.put(messageKey(localeKey, key), props.getProperty(key));
            }
        }

    }


    /**
     * Get an iterator of all message keys
     * 
     * @return message keys iterator
     */
    public Iterator keys() {
        return messages.keySet().iterator();
    }

    /**
     * Remove a message from the resources
     * 
     * @param key key of message
     */
    public void removeKey(String key) {
        messages.remove(key);        
    }

    /**
     * Get a map of all locales
     * 
     * @return map of locales
     */
    public HashMap getLocales() {
        return locales;
    }
    
    /**
     * Set the value of a message given its locale and key.
     * 
     * @param locale locale
     * @param key key
     * @param value value
     */
    public void setMessage(Locale locale, String key, String value) {
        setMessage(localeKey(locale), key, value);
    }
    
    /**
     * Set the value of a message given its locale key and message key.
     * 
     * @param localeKey locale key
     * @param key key
     * @param value value
     */
    public void setMessage(String localeKey, String key, String value) {
        messages.put(messageKey(localeKey == null ? "" : localeKey, key), value);        
    }

}
