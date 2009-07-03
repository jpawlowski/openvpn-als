
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
			
package com.adito.activedirectory;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of {@link javax.security.auth.callback.CallbackHandler} that
 * takes a <i>User name</i> and <i>Password</i>. The only known use of this
 * class is in the
 * {@link com.adito.activedirectory.ActiveDirectoryUserDatabase}.
 */
public class UserPasswordCallbackHandler implements CallbackHandler {
    private static final Log log = LogFactory.getLog(UserPasswordCallbackHandler.class);
    private String username;
    private char[] password;

    /* (non-Javadoc)
     * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
     */
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
            	if (log.isDebugEnabled()) {
            		log.debug("Handling username callback");
                }
                NameCallback cb = (NameCallback) callbacks[i];
                cb.setName(username);

            } else if (callbacks[i] instanceof PasswordCallback) {
            	if (log.isDebugEnabled()) {
            		log.debug("Handling password callback");
                }
                PasswordCallback cb = (PasswordCallback) callbacks[i];
                cb.setPassword(password);

            } else {
                throw new UnsupportedCallbackException(callbacks[i]);
            }
        }
    }

    /**
     * Set the user name
     * @param username user name
     */
    public void setUserId(String username) {
        this.username = username;
    }

    /**
     * Set the password
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password.toCharArray();
    }
}