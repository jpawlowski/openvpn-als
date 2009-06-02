
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.server;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.boot.ContextHolder;
import net.openvpn.als.boot.ContextKey;
import net.openvpn.als.boot.PropertyClass;

/**
 * {@link java.net.Authenticator} implementation used to authenticate with
 * SOCKS and HTTP proxy servers when the OpenVPNALS server tries to 
 * access web and network resources (for Replacement proxy, extension store etc).
 */

public class ProxyAuthenticator extends Authenticator {
    final static Log log = LogFactory.getLog(ProxyAuthenticator.class);

    /* (non-Javadoc)
     * @see java.net.Authenticator#getPasswordAuthentication()
     */
    public PasswordAuthentication getPasswordAuthentication() {
    	if (log.isInfoEnabled())
    		log.info("Requesting " + getRequestingProtocol() + " proxy authentication for " + getRequestingSite() + " ("
                        + getRequestingHost() + ":" + getRequestingPort() + "), prompt = " + getRequestingPrompt());
        String user = null;
        String pass = null;
        try {
            PropertyClass contextConfiguration = ContextHolder.getContext().getConfig();
            if (getRequestingProtocol().startsWith("SOCKS")) {
                user = contextConfiguration.retrieveProperty(new ContextKey("proxies.socksProxyUser"));
                pass = contextConfiguration.retrieveProperty(new ContextKey("proxies.socksProxyPassword"));
            } else {
                user = contextConfiguration.retrieveProperty(new ContextKey("proxies.http.proxyUser"));
                pass = contextConfiguration.retrieveProperty(new ContextKey("proxies.http.proxyPassword"));
            }
        } catch (Exception e) {
            log.error("Failed to get proxy authentication details.");
            return null;
        }
        return new PasswordAuthentication(user, pass.toCharArray());
    }

}
