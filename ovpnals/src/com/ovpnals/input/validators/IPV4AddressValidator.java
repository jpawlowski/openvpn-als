
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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.StringTokenizer;

import com.ovpnals.boot.CodedException;
import com.ovpnals.boot.PropertyDefinition;
import com.ovpnals.core.CoreException;
import com.ovpnals.util.CIDRNetwork;

/**
 * OK, I know that a regex expression would be quicker here. The old regex
 * didn't work here and it was too hard to try and get it working. This was much
 * quicker to write, if anyone wants to change it in the future, the validation
 * rules should be very easy to understand.
 */
public class IPV4AddressValidator extends StringValidator {

    private static final String LOW_RANGE = "0.0.0.0";
    private static final String HIGH_RANGE = "255.255.255.255";

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
//        LDP - This is too much validation, 0.0.0.0 is a valid IP address
//        as its the wildcard IP, and so is 255.255.255.255. Ok they are 
//        not valid is IP hosts, but still valid within the IP range and
//        we have several areas that could accept these values. 
    	
//        if (LOW_RANGE.equals(ipAddress) || HIGH_RANGE.equals(ipAddress)) {
//            return false;
//        }
        return isValidAddress(ipAddress) || isValidRegexAddress(ipAddress) || isValidCIDRAddress(ipAddress) || isValidDefaultAddress(ipAddress);
    }

    private static boolean isValidAddress(String ipAddress) {
        try {
            InetAddress.getByName(ipAddress);
            return containsFourNumbericParts(ipAddress);
        } catch (UnknownHostException e) {
            return false;
        }
    }
    
    private static boolean containsFourNumbericParts(String ipAddress) {
        int regionCount = 0;
        for (StringTokenizer tokenizer = new StringTokenizer(ipAddress, "."); tokenizer.hasMoreTokens();) {
            try {
                String token = (String) tokenizer.nextToken();
                Integer.parseInt(token);
                regionCount++;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return 4 == regionCount;
    }

    private static boolean isValidRegexAddress(String ipAddress) {
        String replacedIpAddress = ipAddress.replace("*", "0");
        return isValidAddress(replacedIpAddress);
    }

    private static boolean isValidDefaultAddress(String ipAddress) {
        return ipAddress.equals("*");
    }
    
    private static boolean isValidCIDRAddress(String ipAddress) {
        try {
            new CIDRNetwork(ipAddress);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
