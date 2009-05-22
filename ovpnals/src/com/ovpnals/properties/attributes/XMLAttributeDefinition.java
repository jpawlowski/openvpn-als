
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
			
package com.ovpnals.properties.attributes;

import java.util.List;

import org.jdom.Element;
import org.jdom.JDOMException;

import com.ovpnals.boot.PropertyDefinition;
import com.ovpnals.boot.XMLPropertyDefinition;

/**
 * Abstract implementation of an {@link AttributeDefinition} that is configured
 * from an XML element.
 * 
 * @see com.ovpnals.security.UserDatabase
 */
public class XMLAttributeDefinition extends XMLPropertyDefinition implements AttributeDefinition {

    // Private instance variables

    private boolean system;
    private boolean replaceable;
    private int visibility;

    /**
     * Construct
     * 
     * @param element XML element
     * @throws JDOMException on parsing error
     */
    public XMLAttributeDefinition(Element element) throws JDOMException {
        super(element);
        setCategoryLabel(element.getAttributeValue("categoryLabel"));
        setDescription(element.getAttributeValue("description"));
        setLabel(element.getAttributeValue("label"));
        replaceable = "true".equalsIgnoreCase(element.getAttributeValue("replaceable"));
        visibility = USER_USEABLE_ATTRIBUTE;
        if (element.getAttribute("visibility") != null) {
            visibility = element.getAttribute("visibility").getIntValue();
        }

        // Because elements are coming from XML they must be system
        system = true;
    }

    /**
     * Constructor.
     * 
     * @param type type
     * @param name name
     * @param typeMeta type meta
     * @param category category ID or <code>-1</code> to use the categoryLabel
     * @param categoryLabel category label or <code>null</code>
     * @param defaultValue default value
     * @param visibility visibility. See class description
     * @param sortOrder sort order
     * @param messageResourcesKey message resource bundle key
     * @param hidden hidden
     * @param label label
     * @param description description
     * @param system system
     * @param replaceable
     * @param validationString
     */
    public XMLAttributeDefinition(int type, String name, String typeMeta, int category, String categoryLabel, String defaultValue,
                                  int visibility, int sortOrder, String messageResourcesKey, boolean hidden, String label,
                                  String description, boolean system, boolean replaceable, String validationString) {
        super(type, name, typeMeta, category, defaultValue, sortOrder, messageResourcesKey, hidden, label, description);
        this.system = system;
        this.hidden = hidden;
        this.replaceable = replaceable;
        this.validationString = validationString;
        this.visibility = visibility;
        setCategoryLabel(categoryLabel);
    }

    /**
     * Set the visibility. This should only be called on initial creation.
     * 
     * @param visibility
     * @see #getVisibility()
     */
    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    /**
     * Get the visibility. This should only be called on initial creation.
     * 
     * @return visibility
     * @see #setVisibility(int)
     */
    public int getVisibility() {
        return visibility;
    }

    /**
     * Get if this is a <i>System</i> attribute definition. If it is then the
     * definition cannot be edited or removed
     * 
     * @return system definition
     */
    public boolean isSystem() {
        return system;
    }

    /**
     * Set if this is a <i>System</i> attribute definition. If it is then the
     * definition cannot be edited or removed
     * 
     * @param system system definition
     */
    public void setSystem(boolean system) {
        this.system = system;
    }

    /**
     * Set whether this this attribute may be used for replacements.
     * 
     * @param replaceable replaceable
     */
    public void setReplaceable(boolean replaceable) {
        this.replaceable = replaceable;
    }

    /**
     * Get whether this this attribute may be used for replacements.
     * 
     * @return replaceable
     */
    public boolean isReplaceable() {
        return replaceable;
    }

    /**
     * Set the validation string.
     * 
     * @param validationString validation string
     */
    public void setValidationString(String validationString) {
        this.validationString = validationString;
    }

    /**
     * Get the value of this item as it should be stored as an attribute.
     * 
     * @return formatted attribute value
     */
    public String formatAttributeValue(Object value) {
        if (getType() == PropertyDefinition.TYPE_BOOLEAN) {
            if (getTypeMetaObject() != null && !getTypeMetaObject().equals("")) {
                String trueVal = (String) (((List) getTypeMetaObject()).get(0));
                return value == Boolean.TRUE ? trueVal : (String) (((List) getTypeMetaObject()).get(1));
            }
        }
        return value.toString();
    }

    /**
     * Get the appropriate object for this type.
     * 
     * @param value
     * @return typed value
     */
    public Object parseValue(String value) {
        if (getType() == PropertyDefinition.TYPE_BOOLEAN) {
            if (getTypeMetaObject() != null) {
                String trueVal = (String) (((List) getTypeMetaObject()).get(0));
                return value.equals(trueVal) ? Boolean.TRUE : Boolean.FALSE;
            }
        }
        return value;
    }

    public String getDescriptionMessageResourceKey() {
        return getPropertyClass().getName() + "." + getName() + ".description";
    }

    public String getNameMessageResourceKey() {
        return getPropertyClass().getName() + "." + getName() + ".title";
    }

}
