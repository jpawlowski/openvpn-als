
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

import java.io.File;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.CodedException;
import com.adito.boot.PropertyClassManager;
import com.adito.boot.PropertyDefinition;
import com.adito.boot.PropertyValidator;
import com.adito.core.CoreException;
import com.adito.properties.impl.profile.ProfileProperties;

/**
 * {@link PropertyValidator} implementation that excepts two <i>Validator
 * properties</i>.
 */
public class ThemeValidator extends NonBlankStringValidator {

	final static Log log = LogFactory.getLog(ThemeValidator.class);

	/**
	 * Constructor.
	 * 
	 */
	public ThemeValidator() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.boot.PropertyValidator#validate(com.adito.boot.PropertyDefinition,
	 *      java.lang.String, java.util.Properties)
	 */
	public void validate(PropertyDefinition definition, String value, Properties properties) throws CodedException {
		try {
			super.validate(definition, value, properties);
		} catch (CoreException ce) {
			throw new CoreException(ErrorConstants.ERR_EMPTY_THEME,
							ErrorConstants.CATEGORY_NAME,
							ErrorConstants.BUNDLE_NAME,
							null,
							value,
							PropertyClassManager.getInstance()
											.getPropertyClass(ProfileProperties.NAME)
											.getDefinition("ui.theme")
											.getDefaultValue(),
							null,
							null);
		}
		File themeDirectory = new File(new File("webapp"), value.replace('/', File.separatorChar).replace('\\', File.separatorChar));
		if (!themeDirectory.isDirectory()) {
			throw new CoreException(ErrorConstants.ERR_INVALID_THEME,
							ErrorConstants.CATEGORY_NAME,
							ErrorConstants.BUNDLE_NAME,
							null,
							value);
		}
	}

}
