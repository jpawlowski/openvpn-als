
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
 
/**
 * IP Address validator that uses easy to understand when you read
 * them regular expressions. 
 * <p>
 * It matches IP address patterns, where a string may contain either
 * a specific address, CIDR network string or wildcard IP address.  
 */
public class IPAddressPatternValidator extends StringValidator {
	
	/**
	 * Regular expression for specific IP address in the format [n].[n].[n].[n]
	 * where 'n' is a number between 1 and 3 characters in length.
	 */
	final static String IP_ADDRESS_PATTERN_0 = "[\\*]";
	
	/**
	 * Regular expression for specific IP address in the format [n].[n].[n].[n]
	 * where 'n' is a number between 1 and 3 characters in length.
	 */
	final static String IP_ADDRESS_PATTERN_1 = "^[\\d]{1,3}+\\.[\\d]{1,3}+\\.[\\d]{1,3}+\\.[\\d]{1,3}+$";
	
	/**
	 * Regular expression for IP address in the CIDR format [n].[n].[n].[n]/[X]
	 * where 'n' is a number between 1 and 3 characters in length and 'X'
	 * is a number between between 1 and 3 characters in length
	 */
	final static String IP_ADDRESS_PATTERN_2 = "^[\\d]{1,3}+\\.[\\d]{1,3}+\\.[\\d]{1,3}+\\.[\\d]{1,3}+/[\\d]{1,3}";
	
	/**
	 * Regular expression for wildcard IP address in CIDR format [n].[n].[n].[n]
	 * where 'n' is a number between 1 and 3 characters OR a '*' character.
	 */
	final static String IP_ADDRESS_PATTERN_3 = "^[\\d\\*]{1,3}+\\.([\\d\\*]|\\*){1,3}+\\.([\\d\\*]|\\*){1,3}+\\.([\\d\\*]|\\*){1,3}+";
	
	/**
	 * Compound regular expression that matches if ANY of the IP address
	 * patterns match 
	 */
	final static String IP_ADDRESS_PATTERN_REGEXP = 
		"(" + IP_ADDRESS_PATTERN_0 + ")|" + 
		"(" + IP_ADDRESS_PATTERN_1 + ")|" + 
		"(" + IP_ADDRESS_PATTERN_2 + ")|" + 
		"(" + IP_ADDRESS_PATTERN_3 + ")";

	/**
	 * Constructor.
	 */
	public IPAddressPatternValidator() {
		super(0, 99, IP_ADDRESS_PATTERN_REGEXP, null, true);
		this.regExpErrCode = ErrorConstants.ERR_STRING_ISNT_IP_ADDRESS_PATTERN;
	}

    
}
