
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
			
package com.ovpnals.core.stringreplacement;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ovpnals.extensions.ExtensionDescriptor;

public class ExtensionDescriptorReplacer extends AbstractReplacementVariableReplacer  {
    
    private ExtensionDescriptor extensionDescriptor;
    private Map parameters;
    
    public ExtensionDescriptorReplacer(ExtensionDescriptor extensionDescriptor, Map parameters) {
		super();
        this.extensionDescriptor = extensionDescriptor;
        this.parameters = parameters;
    }

	@Override
	public String processReplacementVariable(Pattern pattern, Matcher matcher, String replacementPattern, String type, String key) throws Exception {
		if (type.equalsIgnoreCase("shortcut")) {
            if (parameters == null) {
                return null;
            }
            String val = (String) parameters.get(key);
            if (val == null) {
                throw new Exception("Unknown key " + key + " for type " + type + ".");
            }
            return val;
        } else if (type.equalsIgnoreCase("application")) {
            if (extensionDescriptor == null) {
                return null;
            }
            if (key.equals("id")) {
                return extensionDescriptor.getId();
            } else if (key.equals("name")) {
                return extensionDescriptor.getName();
            } else if (key.equals("description")) {
                return extensionDescriptor.getDescription();
            } else if (key.equals("path")) {
                return "/fs/apps/" + extensionDescriptor.getApplicationBundle().getId();
            } else {
                throw new Exception("Unknown key " + key + " for type " + type + ".");
            }
        }
		return null;
	}
    
}