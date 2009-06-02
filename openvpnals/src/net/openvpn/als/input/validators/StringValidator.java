
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.input.validators;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.boot.CodedException;
import net.openvpn.als.boot.PropertyDefinition;
import net.openvpn.als.boot.PropertyValidator;
import net.openvpn.als.boot.Util;
import net.openvpn.als.core.CoreException;

/**
 * {@link PropertyValidator} implementation that validates string input and
 * accepts a number of <i>Validator properties</i>.
 * <ul>
 * <li><b>minLength</b> - The minimum string length. This defaults to
 * <code>zero</code></li>
 * <li><b>maxLength</b> - The maximum string value. This defaults to
 * <code>255</code>
 * <li><b>regExp</b> - A regular expression to validate against. By default no
 * pattern is matched</li>
 * <li><b>pattern</b> - A pattern to validate against. By default no pattern
 * is matched</li>
 * <li><b>trim</b> - Boolean indicating whether to trim before validating
 * (defaults to true)
 * </ul>
 */
public class StringValidator implements PropertyValidator {

	final static Log log = LogFactory.getLog(IntegerValidator.class);

	private int minLength = 0;
	private int maxLength = 255;
	private String regExp = "", pattern = "";
	private boolean trim;
	
	//
	protected int regExpErrCode = ErrorConstants.ERR_STRING_DOESNT_MATCH_REGEXP;

	/**
	 * Constructor.
	 * 
	 * @param minLength minimum length
	 * @param maxLength maximum length
	 * @param regExp regular expression or <code>null</code> not to check
	 * @param pattern simple pattern or <code>null</code> not to check
	 * @param trim trim string before validating
	 */
	public StringValidator(int minLength, int maxLength, String regExp, String pattern, boolean trim) {
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.regExp = regExp;
		this.pattern = pattern;
		this.trim = trim;
	}

	/**
	 * Constructor. By default uses <code>zero</code> and <code>255</code>.
	 * 
	 */
	public StringValidator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.boot.PropertyValidator#validate(net.openvpn.als.boot.PropertyDefinition,
	 *      java.lang.String, java.util.Properties)
	 */
	public void validate(PropertyDefinition definition, String value, Properties properties) throws CodedException {
		// 
		if ("true".equalsIgnoreCase(properties == null ? "true" : properties.getProperty("trim", String.valueOf(trim)))) {
			value = value.trim();
		}

		// Get the range
		int min = minLength;
		try {
			if (properties != null && properties.containsKey("minLength"))
				min = Integer.parseInt(properties.getProperty("minLength"));
		} catch (NumberFormatException nfe) {
			log.error("Failed to get minimum value for validator.", nfe);
			throw new CoreException(ErrorConstants.ERR_INTERNAL_ERROR,
							ErrorConstants.CATEGORY_NAME,
							ErrorConstants.BUNDLE_NAME,
							null,
							value);
		}
		int max = maxLength;
		try {
			if (properties != null && properties.containsKey("maxLength"))
				max = Integer.parseInt(properties.getProperty("maxLength"));
		} catch (NumberFormatException nfe) {
			log.error("Failed to get maximum value for validator.", nfe);
			throw new CoreException(ErrorConstants.ERR_INTERNAL_ERROR,
							ErrorConstants.CATEGORY_NAME,
							ErrorConstants.BUNDLE_NAME,
							null,
							value);
		}

		// Validate
		if (value.length() < min) {
			throw new CoreException(ErrorConstants.ERR_STRING_TOO_SHORT,
							ErrorConstants.CATEGORY_NAME,
							ErrorConstants.BUNDLE_NAME,
							null,
							String.valueOf(min),
							String.valueOf(max),
							value,
							null);
		}
		if (value.length() > max) {
			throw new CoreException(ErrorConstants.ERR_STRING_TOO_LONG,
							ErrorConstants.CATEGORY_NAME,
							ErrorConstants.BUNDLE_NAME,
							null,
							String.valueOf(min),
							String.valueOf(max),
							value,
							null);
		}

		// Regular expression
		String regExp = properties == null ? this.regExp : Util.trimmedOrBlank(properties.getProperty("regExp", this.regExp));
		if (regExp != null && !regExp.equals("") && !value.matches(regExp)) {
			throw new CoreException(regExpErrCode,
							ErrorConstants.CATEGORY_NAME,
							ErrorConstants.BUNDLE_NAME,
							null,
							String.valueOf(regExp),
							value,
							null,
							null);

		}

		// Simple pattern
		String pattern = Util.trimmedOrBlank(properties == null ? this.pattern : properties.getProperty("pattern", this.pattern));
		if (!pattern.equals("")) {
			pattern = Util.parseSimplePatternToRegExp(pattern); 
			if(!value.matches(pattern)) {
				throw new CoreException(ErrorConstants.ERR_STRING_DOESNT_MATCH_SIMPLE_PATTERN,
								ErrorConstants.CATEGORY_NAME,
								ErrorConstants.BUNDLE_NAME,
								null,
								String.valueOf(pattern),
								value,
								null,
								null);
			}
		}
	}

}
