
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
			
package com.adito.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.util.Base64;
import com.adito.boot.RequestHandlerRequest;
import com.adito.core.UserDatabaseManager;
import com.adito.realms.Realm;
import com.adito.security.InvalidLoginCredentialsException;
import com.adito.security.User;
import com.adito.security.UserDatabase;

/**
 * Provides a concrete implementation of an AgentAuthenticator that uses the
 * Username and Password.
 */
public class UsernameAndPasswordAgentAuthenticator implements AgentAuthenticator {
    private static final Log logger = LogFactory.getLog(UsernameAndPasswordAgentAuthenticator.class);
    private static final String AUTHORIZATION_FIELD = "Authorization";
    private static final String BASIC_METHOD = "basic";

    /*
     * (non-Javadoc)
     * @see com.adito.agent.AgentAuthenticator#authenticate(com.adito.boot.RequestHandlerRequest)
     */
    public User authenticate(RequestHandlerRequest request) {
        String authorization = request.getField(AUTHORIZATION_FIELD);
        if (authorization != null) {
            return authenticate(authorization);
        }
        return null;
    }

    // expected format is Realm/Username:Password or Username:Password
    private static User authenticate(String authorization) {
        String method = getBefore(authorization, " ");
        if (BASIC_METHOD.equalsIgnoreCase(method)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Using BASIC authentication");
            }

            String credentials = new String(Base64.decode(getAfter(authorization, " ")));
            String realmAndUsername = getBefore(credentials, ":");
            String realmName = getBefore(realmAndUsername, "/");
            String username = realmName == null ? realmAndUsername : getAfter(realmAndUsername, "/");
            String password = getAfter(credentials, ":");

            try {
                UserDatabase userDatabase = getUserDatabase(realmName);
                if (userDatabase.checkPassword(username, password)) {
                    return userDatabase.getAccount(username);
                }
            } catch (InvalidLoginCredentialsException e) {
                logger.info("Authentication failed for user " + username);
            } catch (Exception e) {
                logger.error("An error occurred", e);
            }
        }
        return null;
    }

    private static UserDatabase getUserDatabase(String realmName) throws Exception {
        String realRealmName = realmName == null ? UserDatabaseManager.DEFAULT_REALM_NAME : realmName;
        Realm realm = UserDatabaseManager.getInstance().getRealm(realRealmName);
        return UserDatabaseManager.getInstance().getUserDatabase(realm);   
    }
    
    private static String getBefore(String value, String toFind) {
        int indexOf = value.indexOf(toFind);
        return indexOf == -1 ? null : value.substring(0, indexOf);
    }

    private static String getAfter(String value, String toFind) {
        int indexOf = value.indexOf(toFind);
        return indexOf == -1 ? null : value.substring(indexOf + 1);
    }
}