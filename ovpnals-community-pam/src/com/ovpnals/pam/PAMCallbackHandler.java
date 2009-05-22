/*
 *  OpenVPN-ALS-PAM
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
package com.ovpnals.pam;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JAAS Callback Handler
 */
public class PAMCallbackHandler implements CallbackHandler {
	
	final static Log LOG = LogFactory.getLog(PAMCallbackHandler.class.getName());
	
	private String username;
	private char[] password;


	/* (non-Javadoc)
	 * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
	 */
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {

        	/* Handles Id request */
        	if (callbacks[i] instanceof NameCallback) {
        		if (LOG.isDebugEnabled())
        			LOG.debug("Handling username callback");
                NameCallback nc = (NameCallback) callbacks[i];
                nc.setName(username);
            }
            
        	/* Handles Credentitial request */
            else if (callbacks[i] instanceof PasswordCallback) {
        		if (LOG.isDebugEnabled())
        			LOG.debug("Handling password callback");
                PasswordCallback pc = (PasswordCallback) callbacks[i];
                pc.setPassword(password);
            }
            
        	/* Cannot Handle other request */
            else {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }
        }
		
	}
	
	/**
	 * This method need to be used to set user Id
	 * @param user the user Id
	 */
	public void setUserId(String user) {
		username = user;
	}

	/**
	 * This method need to be used to set user Credentitial
	 * @param pass the user Credentitial
	 */
	public void setPassword(String pass) {
		password = pass.toCharArray();
	}
}
