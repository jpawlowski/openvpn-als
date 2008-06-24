
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
import com.adito.properties.Property;
import com.adito.properties.attributes.AttributeDefinition;
import com.adito.properties.impl.userattributes.UserAttributeKey;
import com.adito.properties.impl.userattributes.UserAttributes;

public class UserAttributesReplacer extends AbstractReplacementVariableReplacer {
    
    private String username;
    private int realm;
    
    public UserAttributesReplacer(String username, int realm) {
    	super();
        this.username = username;            
        this.realm = realm;
    }

	@Override
	public String processReplacementVariable(Pattern pattern, Matcher matcher, String replacementPattern, String type, String key) throws Exception {
		if (type.equalsIgnoreCase("attr") || type.equals("userAttributes")) {
            PropertyClass propertyClass = PropertyClassManager.getInstance().getPropertyClass(UserAttributes.NAME);
            AttributeDefinition def = (AttributeDefinition) propertyClass.getDefinition(key);
            if (def == null) {
                VariableReplacement.log.warn("Invalid user attribute '" + key + "'");
                return null;
            } else {
                String val = Property.getProperty(new UserAttributeKey(username, key, realm));
                if(val.equals("")) {
                	val = def.getDefaultValue(); 
                }
                return val;
            }
        }
		return null;
	}
    
}