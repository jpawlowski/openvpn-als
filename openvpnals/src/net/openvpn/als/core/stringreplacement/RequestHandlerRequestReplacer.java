
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
			
package net.openvpn.als.core.stringreplacement;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.openvpn.als.boot.RequestHandlerRequest;
import net.openvpn.als.boot.SystemProperties;

public class RequestHandlerRequestReplacer extends AbstractReplacementVariableReplacer {
    
    private RequestHandlerRequest request;
    
    public RequestHandlerRequestReplacer(RequestHandlerRequest request) {
    	super();
        this.request = request;
    }

	public String processReplacementVariable(Pattern pattern, Matcher matcher, String replacementPattern, String type, String key) throws Exception {
		if (type.equalsIgnoreCase("request")) {
	        Map parameters = request.getParameters();
            if (request == null) {
                return null;
            } else if (key.equals("serverName")) {
                return request.getHost();
            } else if (key.equals("serverPort")) {
                return request.isSecure() ? String.valueOf(request.getPort()) : SystemProperties.get("jetty.force.HTTPSRedirect", "false").equals("true") ? SystemProperties.get("openvpnals.fakeHTTPSPort", "443") : String.valueOf(request.getPort());
            } else if(key.equals("protocol")) {
            	return request.isSecure() ? "https" : SystemProperties.get("jetty.force.HTTPSRedirect", "false").equals("true") ? "https" : "http";
            } else if(key.equals("hostHeader")) {
            	return request.getField("Host");
            } else if (key.startsWith("param.")) {
                return (String) parameters.get(key.substring(6));
            } else if (key.startsWith("attr.")) {
                return request.getAttribute(key.substring(5)).toString();
            } else if (key.equals("userAgent")) {
                return request.getField("User-Agent");
            }
        }
		return null;
	}
}