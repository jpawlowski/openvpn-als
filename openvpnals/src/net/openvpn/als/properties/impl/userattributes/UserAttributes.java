
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
			
package net.openvpn.als.properties.impl.userattributes;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.JDOMException;

import net.openvpn.als.boot.AbstractPropertyKey;
import net.openvpn.als.boot.ContextHolder;
import net.openvpn.als.boot.PropertyClass;
import net.openvpn.als.boot.PropertyDefinition;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.properties.ProfilesFactory;
import net.openvpn.als.properties.attributes.AbstractXMLDefinedAttributesPropertyClass;
import net.openvpn.als.properties.attributes.AttributeDefinition;
import net.openvpn.als.properties.attributes.DefaultAttributeDefinition;
import net.openvpn.als.security.PublicKeyStore;

/**
 * {@link PropertyClass} implementation suitable for user attributes.
 */
public class UserAttributes extends AbstractXMLDefinedAttributesPropertyClass  {

    final static Log log = LogFactory.getLog(UserAttributes.class);

    /**
     * Constant for name
     */
    public final static String NAME = "userAttributes";

    /**
     * Constructor.
     * 
     * @throws IOException
     * @throws JDOMException
     */
    public UserAttributes() throws IOException, JDOMException {
        super(NAME, false, "properties", true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.properties.PropertyType#retrieve(net.openvpn.als.properties.PropertyKey)
     */
    public String retrievePropertyImpl(AbstractPropertyKey key) throws IllegalArgumentException {
        AttributeDefinition def = (AttributeDefinition)getDefinition(key.getName());
        UserAttributeKey userAttrKey = (UserAttributeKey) key;
        try {
            String val = ProfilesFactory.getInstance().retrieveAttributeValue(userAttrKey);
            if (val != null) {
                if (def.getVisibility() == AttributeDefinition.USER_CONFIDENTIAL_ATTRIBUTE) {
                    /*
                     * We can only get confidential attributes after the users
                     * private key has been verified. This may not have happened
                     * when a users attributes are first loaded so we must skip
                     * such attributes.
                     */
                    if (PublicKeyStore.getInstance().hasLoadedKey(userAttrKey.getUser().getPrincipalName())) {
                        try {
                            val = PublicKeyStore.getInstance().decryptText(val, userAttrKey.getUser().getPrincipalName());
                        } catch (Throwable t) {
                            log.warn("Failed to decrypt confidential user attribute, probably corrupt. Returning default value", t);
                            return null;
                        }
                    }
                } 
            }

            // Decrypt obfuscated password if of password type
            if (def.getType() == PropertyDefinition.TYPE_PASSWORD && val != null) {
                try {
                    val = ContextHolder.getContext().deobfuscatePassword(val);
                } catch (Throwable t) {
                    log.warn("Password property " + def.getName() + " could not be decoded. It has been result to the default.", t);
                }
            }
            
            // 
            return val == null ? def.getDefaultValue() : val;
        } catch (Exception e) {
            log.error("Failed to retrieve property.", e);
        }
        return null;
    }

    public String storePropertyImpl(AbstractPropertyKey key, String value) throws IllegalArgumentException {
        AttributeDefinition def = (AttributeDefinition)getDefinition(key.getName());
        UserAttributeKey userAttrKey = (UserAttributeKey) key;
        
        /* A null old value would indicate that the key could be not retrieved for
         * some reason. So, we write anyway
         */
        String oldValue = retrieveProperty(key);
        
        /* If the new value is the same as the default value, we remove the persisted item from the database */ 
        
        if (def.getDefaultValue().equals(value)) {
            value = null;
        }
        
        /* Store the attribute always if this is a confidential attribute, the old value could not be retrieved or the value has changed */ 
        if (def.getVisibility() == AttributeDefinition.USER_CONFIDENTIAL_ATTRIBUTE || oldValue == null || !oldValue.equals(value) ) {

            // Obfuscate the password for storing in the database

            if (def.getType() == PropertyDefinition.TYPE_PASSWORD) {
                try {
                    value = ContextHolder.getContext().obfuscatePassword(value);
                } catch (Throwable t) {
                	if(log.isDebugEnabled())
                		log.warn("Password property " + def.getName() + " could not be encoded.", t);
                }
            }
            
            if (value != null) {
                if (def.getVisibility() == AttributeDefinition.USER_CONFIDENTIAL_ATTRIBUTE) {
                    /*
                     * We can only get confidential attributes after the users
                     * private key has been verified. This may not have happened
                     * when a users attributes are first loaded so we must skip
                     * such attributes.
                     */
                    if (PublicKeyStore.getInstance().hasLoadedKey(userAttrKey.getUser().getPrincipalName())) {
                        try {
                            value = PublicKeyStore.getInstance().encryptText(value, userAttrKey.getUser().getPrincipalName());
                        } catch (Throwable t) {
                        	throw new IllegalArgumentException("Failed to decrypt confidential user attributre, probably corrup.", t);
                        }
                    }
                } 
            }

            // Store to the database
            try {
                ProfilesFactory.getInstance().storeAttributeValue(userAttrKey, value);
            } catch (Exception e) {
                log.error("Failed to update user attributes.", e);
            }
        }
        return oldValue;
    }

	public AttributeDefinition createAttributeDefinition(int type, String name,
			String typeMeta, int category, String categoryLabel,
			String defaultValue, int visibility, int sortOrder,
			String messageResourcesKey, boolean hidden, String label,
			String description, boolean system, boolean replaceable,
			String validationString) {
		AttributeDefinition def = new DefaultAttributeDefinition(type, name, typeMeta, category,
				categoryLabel, defaultValue, visibility, sortOrder,
				messageResourcesKey, hidden, label, description, system,
				replaceable, validationString);
		def.init(this);
		return def;
	}
}
