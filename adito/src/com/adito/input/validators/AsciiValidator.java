
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import com.adito.boot.CodedException;
import com.adito.boot.PropertyDefinition;
import com.adito.boot.PropertyValidator;
import com.adito.core.CoreException;

/**
 * Ensures that the String only contains ASCII characters.
 */
public class AsciiValidator implements PropertyValidator {
    private static final String UTF_8 = "UTF-8";
    
    public void validate(PropertyDefinition definition, String value, Properties properties) throws CodedException {
        assertIsAscii(value);
    }
    
    private static void assertIsAscii (String value) throws CodedException {
        try {
            if (!value.equals(URLEncoder.encode(value, UTF_8))) {
                throw new CoreException(ErrorConstants.ERR_STRING_NON_ASCII, ErrorConstants.CATEGORY_NAME, ErrorConstants.BUNDLE_NAME, null, value);
            }
        } catch (UnsupportedEncodingException e) {
            // ignore, this just isn't going to happen
        }
    }
}