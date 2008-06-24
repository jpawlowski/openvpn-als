
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

import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.Properties;

import com.adito.boot.CodedException;
import com.adito.boot.PropertyDefinition;
import com.adito.core.CoreException;

/**
 * OK, I know that a regex expression would be quicker here.  The old regex didn't work here and it was too hard to try and get it working.
 * This was much quicker to write, if anyone wants to change it in the future, the validation rules should be very easy to understand.
 */
public class IPV6AddressValidator extends StringValidator {

    @Override
    public void validate(PropertyDefinition definition, String value, Properties properties) throws CodedException {
        super.validate(definition, value, properties);
        if (!isIpAddressExpressionValid(value)) {
            throw new CoreException(ErrorConstants.ERR_STRING_ISNT_IP_ADDRESS, ErrorConstants.CATEGORY_NAME, ErrorConstants.BUNDLE_NAME, null, value);
        }
    }

    /**
     * @param ipAddress
     * @return true
     */
    public static boolean isIpAddressExpressionValid(String ipAddress) {
        return isValidIPV6Address(ipAddress) || isValidAnyAddress(ipAddress) || isValidLoopBackAddress(ipAddress);
    }

    private static boolean isValidIPV6Address(String ipAddress) {
        try {
            Inet6Address.getByName(ipAddress);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    private static boolean isValidAnyAddress(String ipAddress) {
        return ipAddress.equals("::0") || ipAddress.equals("0:0:0:0:0:0:0:0");
    }

    private static boolean isValidLoopBackAddress(String ipAddress) {
        return ipAddress.equals("::1") || ipAddress.equals("0:0:0:0:0:0:0:1");
    }
}
