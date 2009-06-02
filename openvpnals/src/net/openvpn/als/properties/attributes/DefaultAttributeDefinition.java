
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
			
package net.openvpn.als.properties.attributes;

import org.jdom.Element;
import org.jdom.JDOMException;


/**
 * Implementation of an {@link XMLAttributeDefinition} that used for 
 * <i>Aser attributes</i>.
 * 
 * @see net.openvpn.als.security.UserDatabase
 */
public class DefaultAttributeDefinition extends XMLAttributeDefinition {    
    /**
     * Construct
     * 
     * @param element XML element
     * @throws JDOMException on parsing error
     */
    public DefaultAttributeDefinition(Element element) throws JDOMException {
        super(element);
    }

    /**
     * Constructor. See class documentation for details of required
     * attributes.
     * 
     * @param type type
     * @param name name
     * @param typeMeta type meta
     * @param category category ID or <code>-1</code> to use the categoryLabel
     * @param categoryLabel category label or <code>null</code>
     * @param defaultValue default value
     * @param visibility visibility. See class description
     * @param sortOrder sort order
     * @param messageResourcesKey 
     * @param hidden hidden
     * @param label label
     * @param description description
     * @param system system
     * @param replaceable 
     * @param validationString validation string
     */

    public DefaultAttributeDefinition(int type, String name, String typeMeta, int category, String categoryLabel, String defaultValue,
                    int visibility, int sortOrder, String messageResourcesKey, boolean hidden, String label, String description, boolean system, boolean replaceable, String validationString) {
        super(type, name, typeMeta, category, categoryLabel, defaultValue,
            visibility, sortOrder, messageResourcesKey, hidden, label, description, system, replaceable, validationString);
    }
}
