
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
			
package com.ovpnals.input.validators;

import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import com.ovpnals.boot.CodedException;
import com.ovpnals.boot.PropertyDefinition;
import com.ovpnals.boot.PropertyValidator;
import com.ovpnals.core.CoreException;


/**
 * Validator for strings in a <i>boolean</i> format, i.e. true or false.
 */
public class BooleanValidator implements PropertyValidator {

	public void validate(PropertyDefinition definition, String value, Properties properties) throws CodedException {
        // the values to validate can be in either typeMetaObject or typeMeta,
        // so we need to validate both.
        boolean typeMetaObjectInvalid = false;
        boolean typeMetaInvalid = false;
        String trueVal = "true";
        String falseVal = "false";
        if (definition.getTypeMetaObject() != null && !definition.getTypeMetaObject().equals("")
                        && ((List) definition.getTypeMetaObject()).size() == 2) {
            trueVal = (String) ((List) definition.getTypeMetaObject()).get(0);
            falseVal = (String) ((List) definition.getTypeMetaObject()).get(1);
        } else {
            typeMetaObjectInvalid = true;
        }

        StringTokenizer st = new StringTokenizer(definition.getTypeMeta(), ",");
        if (definition.getTypeMeta() != null && !definition.getTypeMeta().equals("") && st.countTokens() == 2) {
            trueVal = st.nextToken();
            falseVal = st.nextToken();
        } else {
            typeMetaInvalid = true;
        }

        if (typeMetaInvalid & typeMetaObjectInvalid) {
            throw new CoreException(ErrorConstants.ERR_BOOLEAN_INVALID_META_DATA,
                ErrorConstants.CATEGORY_NAME,
                ErrorConstants.BUNDLE_NAME,
                null,
                definition.getTypeMeta(),
                null,
                null,
                null);
        }

        if (!trueVal.equals(value) && !falseVal.equals(value)) {
            throw new CoreException(ErrorConstants.ERR_INVALID_BOOLEAN,
                ErrorConstants.CATEGORY_NAME,
                ErrorConstants.BUNDLE_NAME,
                null,
                value,
                trueVal,
                falseVal,
                null);
        }
    }
}
