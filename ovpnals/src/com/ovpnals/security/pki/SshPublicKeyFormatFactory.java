
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
			
package com.ovpnals.security.pki;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 *
 * @author $author$
 */
public class SshPublicKeyFormatFactory {
    private static String defaultFormat;
    private static HashMap formatTypes = new HashMap();
    private static Log log = LogFactory.getLog(SshPublicKeyFormatFactory.class);

    static {
        defaultFormat = "SECSH-PublicKey-Base64Encoded";
        formatTypes.put(defaultFormat, SECSHPublicKeyFormat.class);
    }

    /**
     *
     *
     * @param type
     *
     * @return
     *
     * @throws InvalidKeyException
     */
    public static SshPublicKeyFormat newInstance(String type)
        throws InvalidKeyException {
        try {
            if (formatTypes.containsKey(type)) {
                return (SshPublicKeyFormat) ((Class) formatTypes.get(type)).newInstance();
            } else {
                throw new InvalidKeyException("The format type " + type +
                    " is not supported");
            }
        } catch (IllegalAccessException iae) {
            throw new InvalidKeyException(
                "Illegal access to class implementation of " + type);
        } catch (InstantiationException ie) {
            throw new InvalidKeyException(
                "Failed to create instance of format type " + type);
        }
    }

    /**
     *
     *
     * @return
     */
    public static String getDefaultFormatType() {
        return defaultFormat;
    }
}
