
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
			
package com.ovpnals.extensions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.ovpnals.boot.PropertyClassManager;
import com.ovpnals.boot.PropertyDefinition;
import com.ovpnals.boot.XMLPropertyDefinition;

/**
 * Implementation of {@link com.ovpnals.boot.XMLPropertyDefinition}
 * to be used for <i>Application Shortcut Parameters</i>.
 * <p>
 * These parameters are constructed from the the entries in an 
 * <i>Application Descriptor</i> that is embedded in the 
 * <i>Extension Descriptor</i>.
 * <p>
 * As the interface used implies, shortcut parameters support <i>Confidentials Properties</i>,
 * in the case known as <i>Confidential Application Shortcut Parameters</i>.
 */
public class ApplicationParameterDefinition extends XMLPropertyDefinition {
    
    final static Log log = LogFactory.getLog(ApplicationParameterDefinition.class);
    
    // Protected instance variables
    protected boolean optional;

    /**
     * Constructor
     * 
     * @param element XML element to construct parameter from
     * @throws JDOMException on invalid XML
     */
    public ApplicationParameterDefinition(Element element) throws JDOMException {
        super(element);
        
        // DEPRECATED attributes
        if(element.getAttribute("sequence") != null) {
            try {
                log.warn("DEPRECATED. Application parameter definition element now user 'sortOrder' instead of 'sequence'.");
                sortOrder = Integer.parseInt(element.getAttributeValue("sequence"));
            } catch (Exception e) {
                sortOrder = 0;
            }            
        }
        if(element.getAttribute("default") != null) {
            log.warn("DEPRECATED. Application parameter definition element now user 'defaultValue' instead of 'default'.");
            defaultValue = element.getAttributeValue("default");
            defaultValue = defaultValue == null ? PropertyDefinition.UNDEFINED_PARAMETER : defaultValue;
        }
        
        // Additional attributes
        optional = "true".equalsIgnoreCase(element.getAttributeValue("optional"));
        
        //
        init(PropertyClassManager.getInstance().getPropertyClass(ApplicationParameters.NAME));
    }

    /**
     * Get if this is an optional parameter.
     * 
     * @return optional
     */
    public boolean isOptional() {
        return optional;
    }

}