
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
			
package net.openvpn.als.boot;

import org.jdom.Element;
import org.jdom.JDOMException;

/**
 * Extensions of {@link net.openvpn.als.boot.DefaultPropertyDefinition} that
 * allows property definitions to be created from XML {@link Element} objects.
 */
public class XMLPropertyDefinition extends DefaultPropertyDefinition {

    /**
     * Constructor.
     * 
     * @param type
     * @param name
     * @param typeMeta
     * @param category
     * @param defaultValue
     * @param sortOrder
     * @param messageResourcesKey
     * @param hidden
     * @param label
     * @param description
     */
    public XMLPropertyDefinition(int type, String name, String typeMeta, int category, String defaultValue, int sortOrder,
                    String messageResourcesKey, boolean hidden, String label, String description) {
        super(type, name, typeMeta, category, defaultValue, sortOrder, messageResourcesKey, hidden, label, description);
    }

    /**
     * Constructor
     * 
     * @param element XML element to construct parameter from
     * @throws JDOMException on invalid XML
     */
    public XMLPropertyDefinition(Element element) throws JDOMException {
        super();

        // 'name' - Property name. Required
        name = element.getAttributeValue("name");
        if (name == null || name.equals("")) {
            throw new JDOMException("Missing or empty name attribute in <parameter>");
        }

        // 'type' - Property type. Required
        try {
            type = Integer.parseInt(element.getAttributeValue("type"));
        } catch (Exception e) {
            throw new JDOMException("Missing or invalid type attribute in <parameter>");
        }

        // 'sortOrder' - Property sort order. Optional
        try {
            sortOrder = Integer.parseInt(element.getAttributeValue("sortOrder"));
        } catch (Exception e) {
            sortOrder = 0;
        }

        // 'typeMeta' - Property type meta information Optional.
        typeMeta = Util.trimmedOrBlank(element.getAttributeValue("typeMeta"));

        // 'validation' - Property validation string
        validationString = element.getAttributeValue("validation");

        // 'hidden' - Hidden property
        hidden = "true".equalsIgnoreCase(element.getAttributeValue("hidden"));

        // 'category' - Property category
        try {
            String c = element.getAttributeValue("category");
            if (c != null) {
                category = Integer.parseInt(c);
            }
        } catch (Exception e) {
            throw new JDOMException("Invalid category attribute in <parameter>");
        }

        // restart required
        restartRequired = "true".equalsIgnoreCase(element.getAttributeValue("restartRequired"));

        // 'bundle' - Property bundle
        messageResourcesKey = element.getAttributeValue("messageResourcesKey");
        messageResourcesKey = messageResourcesKey == null ? "properties" : messageResourcesKey;

        // 'defaultValue' - Default value
        defaultValue = element.getAttributeValue("defaultValue");
        defaultValue = defaultValue == null ? UNDEFINED_PARAMETER : defaultValue;
        defaultValue = defaultValue.replaceAll("\\\\n", "\n");

        // Additional type
    }

}