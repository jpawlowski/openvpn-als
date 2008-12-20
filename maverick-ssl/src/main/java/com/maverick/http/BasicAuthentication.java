
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

import java.io.IOException;
import java.util.Hashtable;

import com.maverick.crypto.encoders.Base64;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class BasicAuthentication extends HttpAuthenticator {

    String realm;

    public BasicAuthentication(String uri, String host, int port, boolean secure) {
        super("Basic", uri, host, port, secure); //$NON-NLS-1$
    }

    public String getRealm() {
        return realm;
    }

    public boolean isStateless() {
        return true;
    }

    /**
     * authenticate
     * 
     * @param credentials ProxyCredentials
     * @todo Implement this com.maverick.proxy.ProxyAuthenticator method
     */
    public void authenticate(HttpRequest request, HttpMethod method) {
        String str = credentials.getUsername() + ":" + credentials.getPassword(); //$NON-NLS-1$
        request.setHeaderField(authorizationHeader, "Basic " + new String(Base64.encode(str.getBytes()))); //$NON-NLS-1$
    }

    public int processResponse(HttpResponse response) {
        return (hasCompleted = response.getStatus() >= 200 && response.getStatus() < 400) ? AUTHENTICATION_COMPLETED
            : AUTHENTICATION_FAILED;
    }

    public void setChallenge(String challenge) {
        try {
            Hashtable params = ParameterParser.extractParams(challenge);
            this.realm = (String) params.get("realm"); //$NON-NLS-1$
        } catch (IOException ex) {
            this.realm = ""; //$NON-NLS-1$
        }
    }

	public String getInformation() {
		return getRealm();
	}
}
