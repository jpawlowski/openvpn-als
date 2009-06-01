
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
			
package com.maverick.http;

import java.text.MessageFormat;

/**
 * Implementation of an {@link HttpMethod} that is specified to <i>Adito</i>
 * and used for SSL-Tunnnels.
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class HttpAuthenticatorFactory {

    public static final String NTLM = "NTLM"; //$NON-NLS-1$
    public static final String BASIC = "Basic"; //$NON-NLS-1$
    public static final String DIGEST = "Digest"; //$NON-NLS-1$
    public static final String NONE = "None"; //$NON-NLS-1$

    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(HttpAuthenticatorFactory.class);

    // #endif

    public static HttpAuthenticator createAuthenticator(HttpConnection con, String[] challenges, String authenticationHeader,
                                                        String authorizationHeader, String pref, String uri)
                    throws UnsupportedAuthenticationException {

        HttpAuthenticator authenticator = null;

        String actualChallenge = ""; //$NON-NLS-1$

        if (pref != null) {

            boolean prefAvailable = false;
            for (int x = 0; pref != null && x < challenges.length; x++) {
                if (challenges[x].toLowerCase().startsWith(pref.toLowerCase())) {
                    prefAvailable = true;
                    actualChallenge = challenges[x];
                    break;
                }
            }

            if (prefAvailable) {
                if (pref.equalsIgnoreCase(BASIC)) {
                    authenticator = new BasicAuthentication(uri, con.getHost(), con.getPort(), con.isSecure());
                } else if (pref.equalsIgnoreCase(NTLM)) {
                    authenticator = new NTLMAuthentication(uri, con.getHost(), con.getPort(), con.isSecure());
                } else if (pref.equalsIgnoreCase(DIGEST)) {
                    authenticator = new DigestAuthentication(uri, con.getHost(), con.getPort(), con.isSecure());
                }
            }
        } 

        if(authenticator==null) {
        	
        	// No prefered method available so look in challenges and pick one
            for (int i = 0; i < challenges.length; i++) {
                String method = getAuthenticationMethod(challenges[i]);

                if (method.equalsIgnoreCase(BASIC)) {
                    authenticator = new BasicAuthentication(uri, con.getHost(), con.getPort(), con.isSecure());
                } else if (method.equalsIgnoreCase(NTLM)) {
                    authenticator = new NTLMAuthentication(uri, con.getHost(), con.getPort(), con.isSecure());
                } else if (method.equalsIgnoreCase(DIGEST)) {
                    authenticator = new DigestAuthentication(uri, con.getHost(), con.getPort(), con.isSecure());
                }

                if (authenticator != null) {
                    actualChallenge = challenges[i];
                    break;
                }
            }
        }
        if (authenticator != null) {
            // #ifdef DEBUG
            log.info(MessageFormat.format(Messages.getString("HttpAuthenticatorFactory.created"), new Object[] { authenticator.getScheme() })); //$NON-NLS-1$
            // #endif

            authenticator.setConnection(con);
            authenticator.setChallenge(actualChallenge);
            authenticator.setAuthenicationHeader(authenticationHeader);
            authenticator.setAuthorizationHeader(authorizationHeader);

            return authenticator;
        }

        if (pref == null)
            throw new UnsupportedAuthenticationException(challenges);
        else
            throw new UnsupportedAuthenticationException(challenges,
                MessageFormat.format(Messages.getString("HttpAuthenticatorFactory.notSupported"), new Object[] { pref })); //$NON-NLS-1$
    }

    public static String getAuthenticationMethod(String challenge) {
        String method = challenge;

        if (method != null) {
            int n = method.indexOf(' ');
            if (n > -1)
                method = method.substring(0, n);
        }

        return method;
    }

    public String getAuthenticationRealm(String challenge) {
        String auth = challenge;
        String realm = ""; //$NON-NLS-1$

        if (auth != null) {
            int l;
            int r = auth.indexOf('=');

            while (r >= 0) {
                l = auth.lastIndexOf(' ', r);
                realm = auth.substring(l + 1, r);

                if (realm.equalsIgnoreCase("realm")) { //$NON-NLS-1$
                    l = r + 2;
                    r = auth.indexOf('"', l);
                    realm = auth.substring(l, r);

                    break;
                }

                r = auth.indexOf('=', r + 1);
            }
        }

        return realm;
    }
}
