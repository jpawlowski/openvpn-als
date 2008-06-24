
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

import javax.servlet.http.HttpServletRequest;

import com.adito.boot.SystemProperties;

public class ServletRequestReplacer extends AbstractReplacementVariableReplacer {

	private HttpServletRequest request;

	public ServletRequestReplacer(HttpServletRequest request) {
		super();
		this.request = request;
	}

	@Override
	public String processReplacementVariable(Pattern pattern, Matcher matcher,
			String replacementPattern, String type, String key)
			throws Exception {
		if (type.equalsIgnoreCase("request")) {
			if (request == null) {
				return null;
			} else if (key.equals("serverName")) {
				return request.getServerName();
			} else if (key.equals("serverPort")) {
				return request.isSecure() ? String.valueOf(request.getServerPort()) : SystemProperties.get("jetty.force.HTTPSRedirect", "false").equals("true") ? SystemProperties.get("adito.fakeHTTPSPort", "443") : String.valueOf(request.getServerPort());
			} else if(key.equals("protocol")) {
            	return request.isSecure() ? "https" : SystemProperties.get("jetty.force.HTTPSRedirect", "false").equals("true") ? "https" : "http";
            } else if(key.equals("hostHeader")) {
            	return request.getHeader("Host");
            } else if (key.startsWith("param.")) {
				return request.getParameter(key.substring(6));
			} else if (key.startsWith("attr.")) {
				return request.getAttribute(key.substring(5)).toString();
			} else if (key.equals("userAgent")) {
				return request.getHeader("User-Agent");
			}
		}
		return null;
	}
}