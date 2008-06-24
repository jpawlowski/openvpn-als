
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

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public abstract class HttpAuthenticator {

    protected PasswordCredentials credentials;
    protected String authorizationHeader;
    protected String authenticationHeader;
    protected HttpConnection connection;
    protected boolean hasCompleted = false;
    protected String scheme;
    protected String uri;
    protected String host;
    protected int port;
    protected boolean secure;

    public static final int AUTHENTICATION_FAILED = 1;
    public static final int AUTHENTICATION_IN_PROGRESS = 2;
    public static final int AUTHENTICATION_COMPLETED = 3;

    public HttpAuthenticator(String scheme, String uri, String host, int port, boolean secure) {
        this.scheme = scheme;
        this.uri = uri;
        this.host = host;
        this.port = port;
        this.secure = secure;
    }
    
    public abstract String getInformation();
    
    public boolean isSecure() {
    	return secure;
    }

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
    
    public String getScheme() {
    	return scheme;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public String getURI() {
        return uri;
    }

    public abstract boolean isStateless();

    public String getAuthorizationHeader() {
        return authorizationHeader;
    }

    public void setConnection(HttpConnection connection) {
        this.connection = connection;
    }

    public void setAuthenicationHeader(String authenticationHeader) {
        this.authenticationHeader = authenticationHeader;
    }

    public void setAuthorizationHeader(String authorizationHeader) {
        this.authorizationHeader = authorizationHeader;
    }

    public abstract void setChallenge(String challenge);

    public void setCredentials(PasswordCredentials credentials) {
        this.credentials = credentials;
    }

    public abstract void authenticate(HttpRequest request, HttpMethod method) throws IOException;

    public abstract int processResponse(HttpResponse response);

    public boolean canAuthenticate() {
        return true;
    }

    void complete() {
        hasCompleted = true;
    }

    public boolean wantsPrompt() {
        return !hasCompleted;
    }

}
