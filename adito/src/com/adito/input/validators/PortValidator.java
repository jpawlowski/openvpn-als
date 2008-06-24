
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
			
package com.adito.input.validators;

import java.net.ServerSocket;
import java.util.Properties;

import com.adito.boot.AbstractPropertyKey;
import com.adito.boot.CodedException;
import com.adito.boot.PropertyClass;
import com.adito.boot.PropertyDefinition;
import com.adito.core.CoreException;

/**
 * Extension of {@link IntegerValidator} appropriate for validating TCP/IP port
 * numbers in the range of 0 to 65535,
 */
public class PortValidator extends IntegerValidator {
    private static final int MIN_PORT_RANGE = 0;
    private static final int MAX_PORT_RANGE = 65535;

    /**
     * Constructor.
     */
    public PortValidator() {
        super(MIN_PORT_RANGE, MAX_PORT_RANGE);
    }

    @Override
    public void validate(PropertyDefinition definition, String value, Properties properties) throws CodedException {
        // validate the integer range.
        super.validate(definition, value, properties);
        if (definition.getTypeMeta().equals("true")) {
            int portInt = Integer.parseInt(value);
            // now open a server socket to the port to ensure that it is
            // available if the property has a true value for its meta data, and
            // if the value has changed
            PropertyClass propertyClass = definition.getPropertyClass();
            AbstractPropertyKey propertyKey = new AbstractPropertyKey(definition.getName(), propertyClass.getName());
            int oldValue = propertyClass.retrievePropertyInt(propertyKey);
            if (oldValue != portInt) {
                try {
                    ServerSocket socket = new ServerSocket(portInt);
                    socket.close();
                } catch (Exception e) {
                    log.error("Failed to open server socket.", e);
                    throw new CoreException(ErrorConstants.ERR_INTERNAL_ERROR, ErrorConstants.CATEGORY_NAME,
                                    ErrorConstants.BUNDLE_NAME, e, value);
                }
            }
        }
    }
}