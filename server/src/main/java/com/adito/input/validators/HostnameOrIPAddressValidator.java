
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

import java.util.regex.Pattern;
 
/**
 * A validator for hostname / ipaddress and port strings in the format
 * <i>hostname</i> or <i>ipAddress</i>.  
 */
public class HostnameOrIPAddressValidator extends StringValidator {
	
	private static final int MAX_HOST_OR_IP_CHARS = 255;
    /**
	 * Regular expression for a string in the format <i>hostname</i> or
	 * <i>ipAddress</i>.
	 */
	final static String HOST_PATTERN = "^[a-zA-Z0-9\\.\\-\\_]*";

	/**
	 * Constructor.
	 */
	public HostnameOrIPAddressValidator() {
		super(1, MAX_HOST_OR_IP_CHARS, HOST_PATTERN, null, true); 
		this.regExpErrCode = ErrorConstants.ERR_STRING_ISNT_HOSTNAME_OR_IPADDRESS;
	}

    /**
     * Static method which validates a host or IP.
     * 
     * @param value
     * @return boolean
     */
    public static boolean isValidAsHostOrIp(String value){
        if (value.length() > MAX_HOST_OR_IP_CHARS)
            return false;
        else
            return Pattern.compile(HOST_PATTERN).matcher(value).matches();
    }

}
