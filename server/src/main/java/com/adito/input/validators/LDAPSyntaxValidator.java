
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

import java.util.Properties;

import com.adito.boot.CodedException;
import com.adito.boot.PropertyDefinition;
import com.adito.boot.PropertyValidator;
import com.adito.core.CoreException;

/**
 */
public class LDAPSyntaxValidator implements PropertyValidator {
    private static final String COMMON_NAME = "cn=";
    private static final String ORGANISATIONAL_UNIT = "ou=";
    
    public void validate(PropertyDefinition definition, String value, Properties properties) throws CodedException {
        if (!startsWith(value, COMMON_NAME) && !startsWith(value, ORGANISATIONAL_UNIT)) {
            throw new CoreException(ErrorConstants.ERR_STRING_NON_DN, ErrorConstants.CATEGORY_NAME, ErrorConstants.BUNDLE_NAME, null, value);
        }
    }
    
    private static boolean startsWith(String toCheck, String required) {
    	return toCheck.toLowerCase().startsWith(required);
    }
}