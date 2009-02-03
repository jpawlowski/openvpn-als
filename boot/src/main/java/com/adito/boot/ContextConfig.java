
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.JDOMException;


/**
 * {@link PropertyClass} implementation suitable for context configuration
 * properties
 * 
 */
public class ContextConfig extends AbstractXMLDefinedPropertyClass {

    final static Log log = LogFactory.getLog(ContextConfig.class);

    /**
     * Constant for name
     */
    public final static String NAME = "contextConfig";

    // Private instance variables

    private Properties contextProperties = new Properties();

    /**
     * Constructor.
     * 
     * @throws IOException
     * @throws JDOMException
     */
    public ContextConfig(ClassLoader classloader) throws IOException, JDOMException {
        super(NAME, false, classloader);
        File contextFile = new File(ContextHolder.getContext().getConfDirectory(), "webserver.properties");
        if (contextFile.exists()) {
            contextProperties.load(new FileInputStream(contextFile));
        } 
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.properties.PropertyType#retrieve(com.adito.properties.PropertyKey)
     */
    public String retrievePropertyImpl(AbstractPropertyKey key) throws IllegalArgumentException {
        PropertyDefinition def = getDefinition(key.getName());
        try {
            String val = contextProperties.getProperty(key.getName(), def.getDefaultValue());
            if (def.getType() == PropertyDefinition.TYPE_PASSWORD) {
                try {
                    val = ContextHolder.getContext().deobfuscatePassword(val);
                } catch (Throwable t) {
                    log.warn("Password property " + def.getName() + " could not be decoded. It has been result to the default.", t);
                }
            }
            return val;
        } catch (Exception e) {
            log.error("Failed to retrieve property.", e);
        }
        return null;
    }

    public String storePropertyImpl(AbstractPropertyKey key, String value) throws IllegalArgumentException {
        PropertyDefinition def = getDefinition(key.getName());
        String oldValue = retrieveProperty(key);
        if (def.getType() == PropertyDefinition.TYPE_PASSWORD) {
            try {
                value = ContextHolder.getContext().obfuscatePassword(value);
            } catch (Throwable t) {
                log.warn("Password property " + def.getName() + " could not be encoded.", t);
            }
        }        
        contextProperties.put(key.getName(), value);
        try {
            OutputStream out = new FileOutputStream(new File(ContextHolder.getContext().getConfDirectory(), "webserver.properties"));
            contextProperties.store(out, "Webserver properties");
            Util.closeStream(out);

        } catch (IOException ex) {
            log.error("Failed to save web server properties! Your server may not initialize correctly", ex);
        }
        return oldValue;
    }
}
