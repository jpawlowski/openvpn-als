
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
			
package com.ovpnals.boot;

import java.util.Properties;


/**
 * Provides validator for property definitions.
 * 
 * {@link PropertyDefinition} implementations may return an instance a <i>Property Validator</i>.
 * When a user changes the value of the property, an instance of the specified class that 
 * implements this interface is instantiated and the {@link #validate(PropertyDefinition, String, Properties)}
 * method is called.
 * <p>
 * Properties are also extracted from the validation string and passed to the validate method.
 * If no properties were supplied the value may be <code>null</code>. 
 */
public interface PropertyValidator {
    
    /**
     * Validate the property, throwing an exception if the value is not correct.
     *  
     * @param definition definition of property
     * @param value requested value of property
     * @param properties validator properties (may be <code>null</code>
     * @throws CodedException if value is invalid
     */
    public void validate(PropertyDefinition definition, String value, Properties properties) throws CodedException;

}
