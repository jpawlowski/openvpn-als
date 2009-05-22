
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

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.struts.Globals;

import com.ovpnals.agent.DefaultAgentManager;
import com.ovpnals.boot.PropertyClass;
import com.ovpnals.boot.PropertyClassManager;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.properties.Property;
import com.ovpnals.properties.attributes.AttributeDefinition;
import com.ovpnals.properties.impl.profile.ProfilePropertyKey;
import com.ovpnals.properties.impl.userattributes.UserAttributeKey;
import com.ovpnals.properties.impl.userattributes.UserAttributes;
import com.ovpnals.security.AuthenticationScheme;
import com.ovpnals.security.Constants;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SessionInfo;

public class SessionInfoReplacer extends AbstractReplacementVariableReplacer {
    
    private SessionInfo sessionInfo;
    
    public SessionInfoReplacer(SessionInfo sessionInfo) {
    	super();
        this.sessionInfo = sessionInfo;
    }
    
    public static String replace(SessionInfo session, String input) {
        VariableReplacement r = new VariableReplacement();
        r.setSession(session);
        return r.replace(input);        
    }

	@Override
	public String processReplacementVariable(Pattern pattern, Matcher matcher, String replacementPattern, String type, String key) throws Exception {
		if (type.equalsIgnoreCase("property")) {
            if (sessionInfo == null) {
                return null;
            }
            return Property.getProperty(new ProfilePropertyKey(CoreUtil.getCurrentPropertyProfileId(sessionInfo.getHttpSession()),
                            sessionInfo.getUser().getPrincipalName(),
                            key, sessionInfo.getUser().getRealm().getResourceId()));
        } else if (type.equalsIgnoreCase("session")) {
            if (sessionInfo == null) {
                return null;
            }
            if (key.equals("username")) {
                return sessionInfo.getUser().getPrincipalName();
            } else if (key.equals("email")) {
                return sessionInfo.getUser().getEmail();
            } else if (key.equals("fullname")) {
                return sessionInfo.getUser().getFullname();
            } else if (key.equals("locale")) {
                Locale l = (Locale) sessionInfo.getHttpSession().getAttribute(Globals.LOCALE_KEY);
                ;
                return l == null ? Locale.getDefault().toString() : l.toString();
            } else if (key.equals("clientProxyURL")) {
                String proxyURL = CoreUtil.getProxyURL(sessionInfo.getUser(),
                    CoreUtil.getCurrentPropertyProfileId(sessionInfo.getHttpSession()));
                return proxyURL == null ? "" : proxyURL;
            } else if (key.equals("password")) {
                /**
                 * LDP - This is broken, I'm guessing that the VPN
                 * client session is different from the browser
                 * session so the scheme is not being found
                 */
                AuthenticationScheme scheme = (AuthenticationScheme) sessionInfo.getHttpSession()
                                .getAttribute(Constants.AUTH_SESSION);
                if (scheme != null) {
                    char[] pw = LogonControllerFactory.getInstance().getPasswordFromCredentials(scheme);
                    return pw == null ? "" : new String(pw);
                } else {
                    return "";
                }
            } else if (key.equals("userAgent")) {
                return sessionInfo.getUserAgent();
            } else {
                throw new Exception("Unknown key " + key + " for type " + type + ".");
            }
        } else if (type.equalsIgnoreCase("attr") || type.equals(UserAttributes.NAME)) {
            if (sessionInfo == null) {
                return null;
            }
            PropertyClass propertyClass = PropertyClassManager.getInstance().getPropertyClass(UserAttributes.NAME);
            AttributeDefinition def = (AttributeDefinition) propertyClass.getDefinition(key);
            if (def == null) {
                VariableReplacement.log.warn("Invalid user attribute '" + key + "'");
                return null;
            } else {
                return Property.getProperty(new UserAttributeKey(sessionInfo.getUser(), key));
            }
        } else if (type.equalsIgnoreCase("ticket")) {
            if (sessionInfo == null) {
                return null;
            }
            if (key.equals("id")) {
                return (String) sessionInfo.getHttpSession().getAttribute(Constants.VPN_AUTHORIZATION_TICKET);

            } if(key.equals("new")) {                	
            	String agentAuthenticationTicket = DefaultAgentManager.getInstance().registerPendingAgent(sessionInfo);
            	return agentAuthenticationTicket;
            } else {
                throw new Exception("String replacement pattern for ticket only supports the 'id' key. I.e. ${ticket:id}");
            }
        } 
		return null;
	}
    
}