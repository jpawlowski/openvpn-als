
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
			
package com.adito.webforwards;

import java.io.IOException;
import java.io.OutputStream;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.RequestHandler;
import com.adito.boot.RequestHandlerRequest;
import com.adito.boot.RequestHandlerResponse;
import com.adito.boot.SystemProperties;
import com.adito.boot.Util;
import com.adito.core.stringreplacement.SessionInfoReplacer;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.LaunchSessionFactory;
import com.adito.replacementproxy.ProxiedRequestDispatcher;
import com.adito.security.Constants;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;

public abstract class AbstractAuthenticatingWebForwardHandler implements RequestHandler {

	final static Log log = LogFactory.getLog(AbstractAuthenticatingWebForwardHandler.class);

	/**
	 * Launch session attribute for storing whether authentication has been
	 * posted yet
	 */
	public static final String LAUNCH_ATTR_AUTH_POSTED = "authPosted";

	protected final static String sessionCookie = SystemProperties.get("adito.cookie", "JSESSIONID");
	

	protected SessionInfo locateSession(RequestHandlerRequest request,
								RequestHandlerResponse response) {
		/*
		 * When not authenticated, dont reverse proxy anything. We use the logon
		 * ticket to get the HttpSession in use
		 */
		SessionInfo session = null;
		
		Cookie[] cookies = request.getCookies();
		String sessionId = null;
		
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equalsIgnoreCase(sessionCookie)) {
					sessionId = cookies[i].getValue();
					session = LogonControllerFactory.getInstance().getSessionInfoBySessionId(cookies[i].getValue());					
					if (session != null) {
						break;
					}
				}
				if(cookies[i].getName().equalsIgnoreCase(Constants.DOMAIN_LOGON_TICKET) 
						|| cookies[i].getName().equalsIgnoreCase(Constants.LOGON_TICKET)) {
					session = LogonControllerFactory.getInstance().getSessionInfo(cookies[i].getValue());
					if (session != null) {
						break;
					}
				}

			}
		}
		
		if(session==null) {
			// LDP - Fallback position, if no session check for a launchId parameter.
			// WARNING - this may break web forward encoding!!
			LaunchSession ls = LaunchSessionFactory.getInstance().getLaunchSession((String)request.getParameters().get("launchId"));
			if(ls!=null) {
				session = ls.getSession();
				LogonControllerFactory.getInstance().attachSession(sessionId, session);
			}
		}

		return session;
	}
    
    public long addJavaScriptAuthenticationCode(LaunchSession launchSession, OutputStream out, long length) throws IOException {
    	AbstractAuthenticatingWebForward webForward = (AbstractAuthenticatingWebForward)launchSession.getResource();

   		StringBuffer buf = new StringBuffer();
   		buf.append("<script type=\"text/javascript\">\n");
   		buf.append("<!--\n");
   		buf.append("function sslxAutoAuthenticate() {\n");
   		buf.append("var fctl;\n");
        StringTokenizer tokens = new StringTokenizer(webForward.getFormParameters(), "\n");
        String param;
        while (tokens.hasMoreTokens()) {
            param = SessionInfoReplacer.replace(launchSession.getSession(), tokens.nextToken().trim());
            int idx = param.indexOf('=');
            String val = "";
            if (idx > -1) {
                val = param.substring(idx + 1);
            	param = param.substring(0, idx);
            } 
            buf.append("fctl = document.forms[0].");
            buf.append(Util.escapeForJavascriptString(param));
            buf.append(";\n");
            buf.append("if(fctl) { fctl.value = '");                    
            buf.append(Util.escapeForJavascriptString(val));
            buf.append("';");
            buf.append("} else { alert('Could not locate form parameter \"");
            buf.append(Util.escapeForJavascriptString(param));
            buf.append("\", please check your web forward configuration.'");
            buf.append("); }\n");
        }                
        launchSession.setAttribute(ProxiedRequestDispatcher.LAUNCH_ATTR_AUTH_POSTED, Boolean.TRUE);
   		buf.append("document.forms[0].submit();\n");
   		buf.append("}\n");
   		buf.append("setTimeout('sslxAutoAuthenticate()', 1000);\n");
   		buf.append("-->\n");
   		buf.append("</script>");
   		byte[] b = buf.toString().getBytes();
   		out.write(b);
   		length += b.length;
   		return length;
    	
    }
}
