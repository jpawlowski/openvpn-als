
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
			
package com.adito.install.validators;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import com.adito.boot.CodedException;
import com.adito.boot.PropertyDefinition;
import com.adito.boot.PropertyList;
import com.adito.boot.PropertyValidator;
import com.adito.core.CoreException;
import com.adito.input.validators.ErrorConstants;

/**
 */
public class LdapBackupHostnameValidator implements PropertyValidator {

    /* (non-Javadoc)
     * @see com.adito.boot.PropertyValidator#validate(com.adito.boot.PropertyDefinition, java.lang.String, java.util.Properties)
     */
    public void validate(PropertyDefinition definition, String value, Properties properties) throws CodedException {
        PropertyList propertyList = new PropertyList(value);
        for (String propertyValue : propertyList) {
            validate(propertyValue);
        }
    }

    private static void validate(String value) throws CoreException {
        int indexOf = value.indexOf(":");
        if (indexOf == -1) {
            if (!isHostNameValid(value)) {
                throw new CoreException(ErrorConstants.ERR_STRING_ISNT_IP_ADDRESS, ErrorConstants.CATEGORY_NAME, ErrorConstants.BUNDLE_NAME, null, value);
            }
        } else {
            String hostName = value.substring(0, indexOf);
            String port = value.substring(indexOf + 1);
            if (!isHostNameValid(hostName) || !isPortValid(port)) {
                throw new CoreException(ErrorConstants.ERR_STRING_ISNT_IP_ADDRESS, ErrorConstants.CATEGORY_NAME, ErrorConstants.BUNDLE_NAME, null, value);
            }
        }
    }

    private static boolean isHostNameValid(String hostName) {
        try {
            InetAddress.getByName(hostName);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    private static boolean isPortValid(String port) {
        try {
            Integer.parseInt(port);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}