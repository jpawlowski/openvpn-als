
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.CodedException;
import com.adito.boot.PropertyDefinition;
import com.adito.boot.PropertyValidator;
import com.adito.boot.ReplacementEngine;
import com.adito.boot.Replacer;
import com.adito.core.CoreException;
import com.adito.core.stringreplacement.VariableReplacement;


/**
 * {@link PropertyValidator} implementation that excepts two <i>Validator properties</i>.
 * <ul>
 * <li><b>minValue</b> - The minimum integer value. This defaults to <code>zero</code></li>
 * <li><b>maxValue</b> - The maximum integer value. This defaults to {@link Integer#MAX_VALUE}.</li>
 * </ul>
 */
public class IntegerValidator implements PropertyValidator {
    
    final static Log log = LogFactory.getLog(IntegerValidator.class);
    
    protected int defaultMin = 0;
    protected int defaultMax = Integer.MAX_VALUE;
    
    /**
     * Constructor.
     *
     * @param defaultMin default minimum value
     * @param defaultMax default maximum value
     */
    public IntegerValidator(int defaultMin, int defaultMax) {
        this.defaultMin = defaultMin;
        this.defaultMax = defaultMax;
    }
    
   /**
    * Constructor. The by default uses {@link Integer#MIN_VALUE} and
    * {@link Integer#MAX_VALUE}.
    *
    */
    public IntegerValidator() {        
    }

    /* (non-Javadoc)
     * @see com.adito.boot.PropertyValidator#validate(com.adito.boot.PropertyDefinition, java.lang.String, java.util.Properties)
     */
    public void validate(PropertyDefinition definition, String value, Properties properties) throws CodedException {
    	
        // Get the range
        int min = defaultMin;
        try {
            if(properties != null && properties.containsKey("minValue"))
                min = Integer.parseInt(properties.getProperty("minValue"));
        }
        catch(NumberFormatException nfe) {
            log.error("Failed to get minimum value for validator.", nfe);
            throw new CoreException(ErrorConstants.ERR_INTERNAL_ERROR, ErrorConstants.CATEGORY_NAME, ErrorConstants.BUNDLE_NAME, null, value);
        }
        int max = defaultMax;
        try {
            if(properties != null && properties.containsKey("maxValue"))
                max = Integer.parseInt(properties.getProperty("maxValue"));
        }
        catch(NumberFormatException nfe) {
            log.error("Failed to get maximum value for validator.", nfe);
            throw new CoreException(ErrorConstants.ERR_INTERNAL_ERROR, ErrorConstants.CATEGORY_NAME, ErrorConstants.BUNDLE_NAME, null, value);
        }

    	/* We may support replacement variables so
    	 * to validate we must replace with the minimum value 
    	 */     	
    	if(properties != null && "true".equalsIgnoreCase(properties.getProperty("replacementVariables"))) {
	    	Replacer r = new Replacer() {
				public String getReplacement(Pattern pattern, Matcher matcher, String replacementPattern) {
					return replacementPattern;
				}    		
	    	};
	    	ReplacementEngine re = new ReplacementEngine();
	    	re.addPattern(VariableReplacement.VARIABLE_PATTERN, r, String.valueOf(min));
	    	value = re.replace(value);
    	}
        
        // Validate
        try {
            int i  = Integer.parseInt(value); 
            if(i < min || i > max) {
                throw new CoreException(ErrorConstants.ERR_INTEGER_OUT_OF_RANGE, ErrorConstants.CATEGORY_NAME, ErrorConstants.BUNDLE_NAME, null, String.valueOf(min), String.valueOf(max), value, null);                
            }
        }
        catch(NumberFormatException nfe) {
            throw new CoreException(ErrorConstants.ERR_NOT_AN_INTEGER, ErrorConstants.CATEGORY_NAME, ErrorConstants.BUNDLE_NAME, null, String.valueOf(min), String.valueOf(max), value, null);
        }
    }

}
