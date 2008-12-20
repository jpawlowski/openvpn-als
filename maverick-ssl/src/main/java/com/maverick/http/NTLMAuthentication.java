
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
public class NTLMAuthentication extends HttpAuthenticator {

    NTLM ntlm = new NTLM();
    String host = ""; //$NON-NLS-1$
    String domain = ""; //$NON-NLS-1$

    String challenge = null;

    private static final int INITIATED = 1;
    private static final int TYPE1_MSG_GENERATED = 2;
    private static final int TYPE2_MSG_RECEIVED = 3;
    private static final int TYPE3_MSG_GENERATED = 4;
    private static final int FAILED = Integer.MAX_VALUE;

    int state;

    boolean isAuthenticated = false;

    public NTLMAuthentication(String uri, String host, int port, boolean secure) {
        super("NTLM", uri, host, port, secure); //$NON-NLS-1$
        this.state = INITIATED;
    }

    public boolean isStateless() {
        return false;
    }

    public void setChallenge(String challenge) {

    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setCredentials(PasswordCredentials credentials) {

        if (credentials != null && credentials.getUsername() != null && credentials.getUsername().indexOf('\\') > -1) {
            int idx = credentials.getUsername().indexOf('\\');
            domain = credentials.getUsername().substring(0, idx);

            this.credentials = new PasswordCredentials(credentials.getUsername().substring(idx + 1), credentials.getPassword());

        } else
            this.credentials = credentials;
    }

    /**
     * authenticate
     * 
     * @param request HttpRequest
     * @return boolean
     * @throws ProxyException
     * @todo Implement this com.maverick.proxy.http.HttpAuthenticator method
     */
    public void authenticate(HttpRequest request, HttpMethod method) throws IOException {

        switch (state) {
            case INITIATED:
            {
                request.setHeaderField(authorizationHeader, "NTLM " + //$NON-NLS-1$
                    ntlm.getResponseFor(challenge,
                        credentials.getUsername(),
                        credentials.getPassword(),
                        connection.getHost(),
                        domain));
                this.state = TYPE1_MSG_GENERATED;
                break;
            }
            case TYPE2_MSG_RECEIVED:
            {
            	request.setHeaderField(authorizationHeader, "NTLM " + //$NON-NLS-1$
                    ntlm.getResponseFor(challenge,
                        credentials.getUsername(),
                        credentials.getPassword(),
                        connection.getHost(),
                        domain));
                this.state = TYPE3_MSG_GENERATED;
                break;
        	}
            case TYPE3_MSG_GENERATED:
            case TYPE1_MSG_GENERATED:
            default:
                throw new IOException(Messages.getString("NTLMAuthentication.invalidState")); //$NON-NLS-1$

        }
    }

    private void reset() {
        state = INITIATED;
        challenge = null;
        ntlm = new NTLM();
        domain = ""; //$NON-NLS-1$
    }

    public boolean wantsPrompt() {
        return state == INITIATED && super.wantsPrompt();
    }

    public boolean canAuthenticate() {
        return state == INITIATED || state == TYPE2_MSG_RECEIVED;
    }

    public int processResponse(HttpResponse response) {

        if (response.getStatus() >= 200 && response.getStatus() < 400) {
            reset();
            return AUTHENTICATION_COMPLETED;
        }
        String[] challenges = response.getHeaderFields(authenticationHeader);

        challenge = null;

        for (int i = 0; i < challenges.length; i++) {
            if (challenges[i].startsWith("NTLM")) { //$NON-NLS-1$
                challenge = challenges[i];
                break;
            }
        }

        if (challenge == null || challenge.equals("NTLM")) { //$NON-NLS-1$
            reset();
            return AUTHENTICATION_FAILED;
        } else {
            challenge = challenge.substring(5).trim();
            this.state = TYPE2_MSG_RECEIVED;
            return AUTHENTICATION_IN_PROGRESS;
        }
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    void complete() {
        reset();
        super.complete();
    }

	public String getInformation() {
		return domain;
	}

}
