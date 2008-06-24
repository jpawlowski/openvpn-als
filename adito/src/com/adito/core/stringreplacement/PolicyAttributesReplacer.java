
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
			
package com.adito.core.stringreplacement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.adito.boot.PropertyClass;
import com.adito.boot.PropertyClassManager;
import com.adito.policyframework.Policy;
import com.adito.properties.Property;
import com.adito.properties.attributes.AttributeDefinition;
import com.adito.properties.impl.policyattributes.PolicyAttributeKey;
import com.adito.properties.impl.policyattributes.PolicyAttributes;

public class PolicyAttributesReplacer extends AbstractReplacementVariableReplacer {
    
    private Policy policy;
    
    public PolicyAttributesReplacer(Policy policy) {
    	super();
        this.policy = policy;            
    }

	@Override
	public String processReplacementVariable(Pattern pattern, Matcher matcher, String replacementPattern, String type, String key) throws Exception {
		if (type.equalsIgnoreCase(PolicyAttributes.NAME)) {
            PropertyClass propertyClass = PropertyClassManager.getInstance().getPropertyClass(type);
            AttributeDefinition def = (AttributeDefinition) propertyClass.getDefinition(key);
            if (def == null) {
                VariableReplacement.log.warn("Invalid policy attribute '" + key + "'");
                return null;
            } else {
                return Property.getProperty(new PolicyAttributeKey(policy.getResourceId(), key));
            }
        }
		return null;
	}
    
}