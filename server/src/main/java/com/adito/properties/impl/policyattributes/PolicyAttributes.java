
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
			
package com.adito.properties.impl.policyattributes;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.JDOMException;

import com.adito.boot.AbstractPropertyKey;
import com.adito.boot.ContextHolder;
import com.adito.boot.PropertyClass;
import com.adito.boot.PropertyDefinition;
import com.adito.properties.ProfilesFactory;
import com.adito.properties.attributes.AbstractXMLDefinedAttributesPropertyClass;
import com.adito.properties.attributes.AttributeDefinition;
import com.adito.properties.attributes.DefaultAttributeDefinition;

/**
 * {@link PropertyClass} implementation for policy attributes.
 */
public class PolicyAttributes extends AbstractXMLDefinedAttributesPropertyClass {

	final static Log log = LogFactory.getLog(PolicyAttributes.class);

	/**
	 * Constant for name
	 */
	public final static String NAME = "policyAttributes";

	/**
	 * Constructor.
	 * 
	 * @throws IOException
	 * @throws JDOMException
	 */
	public PolicyAttributes() throws IOException, JDOMException {
		super(NAME, false, "properties", false);
	}

	public String retrievePropertyImpl(AbstractPropertyKey key)
			throws IllegalArgumentException {
		AttributeDefinition def = (AttributeDefinition) getDefinition(key
				.getName());
		PolicyAttributeKey policyAttrKey = (PolicyAttributeKey) key;
		try {
			String val = ProfilesFactory.getInstance()
					.retrieveAttributeValue(policyAttrKey);

			// Decrypt obfuscated password if of password type
			if (def.getType() == PropertyDefinition.TYPE_PASSWORD
					&& val != null) {
				try {
					val = ContextHolder.getContext().deobfuscatePassword(val);
				} catch (Throwable t) {
					log
							.warn(
									"Password property "
											+ def.getName()
											+ " could not be decoded. It has been result to the default.",
									t);
				}
			}
			// 
			return val == null ? def.getDefaultValue() : val;
		} catch (Exception e) {
			log.error("Failed to retrieve property.", e);
		}
		return null;
	}

	public String storePropertyImpl(AbstractPropertyKey key, String value)
			throws IllegalArgumentException {
		AttributeDefinition def = (AttributeDefinition) getDefinition(key
				.getName());
		PolicyAttributeKey policyAttrKey = (PolicyAttributeKey) key;
		String oldValue = retrieveProperty(key);
		if (def.getDefaultValue().equals(value)) {
			value = null;
		}

		if ((oldValue == null && value != null)
				|| (oldValue != null && value == null)
				|| !oldValue.equals(value)) {

			// Obfuscate the password for storing in the database

			if (def.getType() == PropertyDefinition.TYPE_PASSWORD) {
				try {
					value = ContextHolder.getContext().obfuscatePassword(value);
				} catch (Throwable t) {
					log.warn("Password property " + def.getName()
							+ " could not be encoded.", t);
				}
			}

			// Store to the database
			try {
                ProfilesFactory.getInstance()
						.storeAttributeValue(policyAttrKey, value);
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
