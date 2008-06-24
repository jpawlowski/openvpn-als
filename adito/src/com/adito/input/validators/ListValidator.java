
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

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.CodedException;
import com.adito.boot.PropertyDefinition;
import com.adito.boot.PropertyList;
import com.adito.boot.PropertyValidator;
import com.adito.core.CoreException;

public class ListValidator implements PropertyValidator {
	
	final static Log log = LogFactory.getLog(ListValidator.class);

	public void validate(PropertyDefinition definition, String value, Properties properties) throws CodedException {
		PropertyList pl = new PropertyList(value);		
		String className = properties.getProperty("className");
		Properties p = new Properties(properties);
		p.remove("className");
		try {
			Class clazz = getClass().forName(className);
			PropertyValidator pv = (PropertyValidator)clazz.newInstance();
			for(String item : pl) {
				pv.validate(definition, item, p);
			}
		}
		catch(CoreException ce) {
			throw ce;
		}
		catch(Exception e) {
            log.error("Invalid or missing class name", e);
            throw new CoreException(ErrorConstants.ERR_INTERNAL_ERROR, ErrorConstants.CATEGORY_NAME, ErrorConstants.BUNDLE_NAME, null, value);
		}
	}

}
