
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.Replacer;
import com.adito.boot.Util;

public abstract class AbstractReplacementVariableReplacer implements Replacer {
    
	public final static Log log = LogFactory.getLog(AbstractReplacementVariableReplacer.class);

    public abstract String processReplacementVariable(Pattern pattern, Matcher matcher, String replacementPattern, String type, String key) throws Exception;

    public String getReplacement(Pattern pattern, Matcher matcher, String replacementPattern) {
        String match = matcher.group();
        String key = match.substring(2, match.length() - 1);
        try {
        	// Extract the type and key
            int idx = key.indexOf(":");
            if (idx == -1) {
                throw new Exception("String replacement pattern is in incorrect format for " + key
                    + ". Must be <TYPE>:<key>");
            }
            String type = key.substring(0, idx);
            key = key.substring(idx + 1);
            if (log.isDebugEnabled())
                log.debug("Found replacement variable " + type + ":" + key);
            
            // Determine the encoding the use (if any). Default to 'p' (plain)
            char enc = 'p'; 
            if(type.startsWith("[")) {
            	enc = type.charAt(1);
            	type = type.substring(3);
            }
            String val = processReplacementVariable(pattern, matcher, replacementPattern, type, key);
            if(val != null) {
            	switch(enc) {
            		case 'u':
            			// URL encoded
            			val = Util.urlEncode(val);
            			break;
            		case 'p':
            			// Plain
            			break;
            		default:
            			throw new Exception("Invalid encoding '" + enc + "'");
            	}
            }
            return val; 
        } catch (Exception e) {
            log.error("A replacement failed for " + key + ".", e);
        }
        return null;
    }
    
}
