
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
			
package com.ovpnals.properties.impl.resource;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.JDOMException;

import com.ovpnals.boot.AbstractPropertyKey;
import com.ovpnals.boot.ContextHolder;
import com.ovpnals.boot.PropertyClass;
import com.ovpnals.boot.PropertyDefinition;
import com.ovpnals.properties.ProfilesFactory;
import com.ovpnals.properties.attributes.AbstractXMLDefinedAttributesPropertyClass;
import com.ovpnals.properties.attributes.AttributeDefinition;
import com.ovpnals.properties.attributes.DefaultAttributeDefinition;

/**
 * {@link PropertyClass} implementation suitable for system configuration
 * properties
 */
public class ResourceAttributes extends AbstractXMLDefinedAttributesPropertyClass {

    final static Log log = LogFactory.getLog(ResourceAttributes.class);

    /**
     * Constant for name
     */
    public final static String NAME = "resourceAttributes";

    /**
     * Constructor.
     * 
     * @throws IOException
     * @throws JDOMException
     */
    public ResourceAttributes() throws IOException, JDOMException {
        super(NAME, false, "properties", false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.properties.PropertyType#retrieve(com.ovpnals.properties.PropertyKey)
     */
    public String retrievePropertyImpl(AbstractPropertyKey key) throws IllegalArgumentException {
        PropertyDefinition def = getDefinition(key.getName());
        ResourceKey resourceKey = (ResourceKey) key;
        try {
            String val = ProfilesFactory.getInstance().retrieveAttributeValue(resourceKey);
            if (def.getType() == PropertyDefinition.TYPE_PASSWORD) {
                try {
                    val = ContextHolder.getContext().deobfuscatePassword(val);
                } catch (Throwable t) {
                    log.warn("Password property " + def.getName() + " could not be decoded. It has been result to the default.", t);
                }
            }
            return val == null ? def.getDefaultValue() : val;
        } catch (Exception e) {
            log.error("Failed to retrieve property.", e);
        }
        return null;
    }

    public String storePropertyImpl(AbstractPropertyKey key, String value) throws IllegalArgumentException {
        PropertyDefinition def = getDefinition(key.getName());
        ResourceKey resourceKey = (ResourceKey) key;
        String oldValue = retrieveProperty(key);
        if (def.getDefaultValue().equals(value)) {
            value = null;
        }

        if ((oldValue == null && value != null) || (oldValue != null && value == null) || !oldValue.equals(value)) {

            if (def.getType() == PropertyDefinition.TYPE_PASSWORD) {
                try {
                    value = ContextHolder.getContext().obfuscatePassword(value);
                } catch (Throwable t) {
                    log.warn("Password property " + def.getName() + " could not be encoded.", t);
                }
            }
            try {
                ProfilesFactory.getInstance()
                .storeAttributeValue(resourceKey, value);
            } catch (Exception e) {
                log.error("Could not store properties in database.");
            }
        }
        return oldValue;
    }

    public AttributeDefinition createAttributeDefinition(int type, String name, String typeMeta, int category,
                                                         String categoryLabel, String defaultValue, int visibility, int sortOrder,
                                                         String messageResourcesKey, boolean hidden, String label,
                                                         String description, boolean system, boolean replaceable,
                                                         String validationString) {
        AttributeDefinition def = new DefaultAttributeDefinition(type, name, typeMeta, category, categoryLabel, defaultValue,
                        visibility, sortOrder, messageResourcesKey, hidden, label, description, system, replaceable,
                        validationString);
        def.init(this);
        return def;
    }
}
