
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

import com.adito.boot.PropertyValidator;


/**
 * Constants for default {@link PropertyValidator} error codes.
 */
public class ErrorConstants {

    /*
     * Prevent instantiation
     */
    private ErrorConstants() {
    }
    
    /**
     * Category name
     */
    public final static String CATEGORY_NAME = "validation";
    
    /**
     * Bundle
     */
    public final static String BUNDLE_NAME = "validators";
    
    /**
     * Internal error. A validator failed. 
     */
    public final static int ERR_INTERNAL_ERROR = 1;
    
    /**
     * A supplied integer did not fall between the specified minimu
     * and maximum values. <code>arg0</code> will be the minimum allowed,
     * <code>arg1</code> will be the maximum, <code>arg2</code> will be
     * the actual value supplied.
     */
    public final static int ERR_INTEGER_OUT_OF_RANGE = 2;
    
    /**
     * A supplied value was not an integer. <code>arg0</code> will be the
     * actual value supplied.  
     */
    public final static int ERR_NOT_AN_INTEGER = 3;

    /**
     * String length is less than the specified minimum. <code>arg0</code> will
     * contain the minimum length, <code>arg1</code> will contain the maximum
     * and <code>arg2</code> will contain the value.  
     */
    public static final int ERR_STRING_TOO_SHORT = 4;


    /**
     * String length is greater than the specified maximum. <code>arg0</code> will
     * contain the minimum length, <code>arg1</code> will contain the maximum
     * and <code>arg2</code> will contain the value.  
     */
    public static final int ERR_STRING_TOO_LONG = 5;

    /**
     * String doesn't match specifed regular expression. <code>arg0</code> will
     * contain the regular expression, <code>arg1</code> will contain the value
     */
    public static final int ERR_STRING_DOESNT_MATCH_REGEXP = 6;

    /**
     * String doesn't match specifed simple pattern. <code>arg0</code> will
     * contain the pattern, <code>arg1</code> will contain the value
     */
    public static final int ERR_STRING_DOESNT_MATCH_SIMPLE_PATTERN = 7;
    
    /**
     * String isn't a valid IP Address. <code>arg0</code> will contain the value
     */
    public static final int ERR_STRING_ISNT_IP_ADDRESS = 8;

    /**
     * String contains non-ascii characters.
     */
    public static final int ERR_STRING_NON_ASCII = 9;
    
    /**
     * String isn't a valid DN.
     */
    public static final int ERR_STRING_NON_DN = 10;
    
    /**
     * String isn't a valid boolean (as defined by typeMeta).
     */
    public static final int ERR_INVALID_BOOLEAN = 11;
    
    /**
     * Theme can't be empty. The default Theme is /theme/default. 
     * <code>arg0</code> will contain the value, <code>arg1</code> will
     * contain the default
     */
    public static final int ERR_EMPTY_THEME = 12;
    /**
     * String isn't a valid Theme. <code>arg0</code> will contain the value
     */
    public static final int ERR_INVALID_THEME = 13;
    
    /**
     * String doesn't match specifed IP address patter. <code>arg0</code> will
     * contain the regular expression, <code>arg1</code> will contain the value
     */
    public static final int ERR_STRING_ISNT_IP_ADDRESS_PATTERN = 14;

	/**
	 * String isn't a hostname / ip address with an optional port.
	 */
	public static final int ERR_STRING_ISNT_HOSTNAME_OR_IPADDRESS_WITH_PORT = 15;

    /**
	 * String isn't a hostname / ip address.
	 */
	public static final int ERR_STRING_ISNT_HOSTNAME_OR_IPADDRESS = 16;
	
	/**
	 * String isn't a hostname / ip address.
	 */
	public static final int ERR_BOOLEAN_INVALID_META_DATA = 17;
}
