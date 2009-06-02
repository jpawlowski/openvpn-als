
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
			
package net.openvpn.als.properties.impl.profile;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.JDOMException;

import net.openvpn.als.boot.AbstractPropertyKey;
import net.openvpn.als.boot.AbstractXMLDefinedPropertyClass;
import net.openvpn.als.boot.ContextHolder;
import net.openvpn.als.boot.PropertyClass;
import net.openvpn.als.boot.PropertyDefinition;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.properties.ProfilesFactory;

/**
 * {@link PropertyClass} implementation suitable for properties that are stored
 * against a either global or user profiles.
 */
public class ProfileProperties extends AbstractXMLDefinedPropertyClass {

    final static Log log = LogFactory.getLog(ProfileProperties.class);

    /**
     * Constant for name
     */
    public final static String NAME = "profileProperties";

    /**
     * Constructor.
     * 
     * @throws IOException
     * @throws JDOMException
     */
    public ProfileProperties() throws IOException, JDOMException {
        super(NAME, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.properties.PropertyType#retrieve(net.openvpn.als.properties.PropertyKey)
     */
    public String retrievePropertyImpl(AbstractPropertyKey key) throws IllegalArgumentException {
        if (!(key instanceof ProfilePropertyKey)) {
            throw new IllegalArgumentException("Property key is not an instanceof ProfilePropertyKey");
        }
        PropertyDefinition def = getDefinition(key.getName());
        ProfilePropertyKey profilesKey = (ProfilePropertyKey) key;
        try {
            String val = ProfilesFactory.getInstance().retrieveGenericProperty(profilesKey.getName(),
                profilesKey.isUserSpecific() ? profilesKey.getUsername() : "",
                String.valueOf(profilesKey.getProfile()),
                String.valueOf(profilesKey.getRealm()),
                "");
            // If a username was supplied, then now try the global profiles
            if(val == null && profilesKey.isUserSpecific()) {
                val = ProfilesFactory.getInstance().retrieveGenericProperty(profilesKey.getName(),
                    "",
                    String.valueOf(profilesKey.getProfile()),
                    String.valueOf(profilesKey.getRealm()),
                    "");
            }
            
            // Fallback to defaults
            if (val == null) {
                val = def.getDefaultValue();
            } else {
                if (def.getType() == PropertyDefinition.TYPE_PASSWORD) {
                    try {
                        val = ContextHolder.getContext().deobfuscatePassword(val);
                    } catch (Throwable t) {
                        log.warn("Password property " + def.getName() + " could not be decoded. It has been result to the default.",
                            t);
                    }
                }
            }
            return val;
        } catch (Exception e) {
            log.error("Failed to retrieve property.", e);
        }
        return null;
    }

    public String storePropertyImpl(AbstractPropertyKey key, String value) throws IllegalArgumentException {
        if (!(key instanceof ProfilePropertyKey)) {
            throw new IllegalArgumentException("Property key is not an instanceof ProfilePropertyKey");
        }
        PropertyDefinition def = getDefinition(key.getName());
        ProfilePropertyKey profilesKey = (ProfilePropertyKey) key;

        String oldValue = retrieveProperty(key);

        if (def.getType() == PropertyDefinition.TYPE_PASSWORD) {
            try {
                value = ContextHolder.getContext().obfuscatePassword(value);
            } catch (Throwable t) {
                log.warn("Password property " + def.getName() + " could not be encoded.", t);
            }
        }

        // If the definitions shows a visibilitiy of CONTEXT_CONFIGURATION then
        // set the property value in the context
        try {
            ProfilesFactory.getInstance().storeGenericProperty(profilesKey.getName(),
                profilesKey.isUserSpecific() ? profilesKey.getUsername() : "",
                String.valueOf(profilesKey.getProfile()),
                String.valueOf(profilesKey.getRealm()),
                "",
                value);
        } catch (Exception e) {
        	log.error("Could not store properties in database.", e);
        }
        return oldValue;
    }
}
