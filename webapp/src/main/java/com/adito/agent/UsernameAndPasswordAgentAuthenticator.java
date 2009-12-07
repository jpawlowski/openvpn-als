
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

    /* This method parses an authentication Request from Agent for credentials. It then
     * passes the credentials to another authenticate() method which verifies them from an
     * user database.
     *
     * @param   request the request containing Agent's credentials
     *
     * @return  If authentication was successful a User object is returned. Otherwise return
     *          null.
     * 
     * @see com.adito.agent.AgentAuthenticator#authenticate(com.adito.boot.RequestHandlerRequest)
     */
    public User authenticate(RequestHandlerRequest request) {
        String authorization = request.getField(AUTHORIZATION_FIELD);
        if (authorization != null) {
            return authenticate(authorization);
        }
        return null;
    }

    /** This method authenticates an Agent instance by parsing given String.
      * Expected input format is Realm/Username:Password or
      * Username:Password. After parsing the credentials from input
      * the user is authenticated against the user database (e.g. PAM, LDAP).
      *
      * @param  authorization   The String object which contains the credentials from the Agent
      *
      * @return If authentication was successful a User object is returned. Otherwise return
      *         null.    
      */
    private static User authenticate(String authorization) {
        
        // Get the authentication method        
        String method = getBefore(authorization, " ");
        if (BASIC_METHOD.equalsIgnoreCase(method)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Using BASIC authentication");
            }

            // Get the credentials method        
            String credentials = new String(Base64.decode(getAfter(authorization, " ")));
            String realmAndUsername = getBefore(credentials, ":");
            String realmName = getBefore(realmAndUsername, "/");
            String username = realmName == null ? realmAndUsername : getAfter(realmAndUsername, "/");
            String password = getAfter(credentials, ":");

            // Try to authenticate the Agent's user from the UserDataBase (e.g. PAM, LDAP)
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

    /** Return the UserDatabase in use in this Realm.
      *
      * @param  realmName   realm name
      *
      * @return the user database being used in this Realm 
      */
    private static UserDatabase getUserDatabase(String realmName) throws Exception {
        String realRealmName = realmName == null ? UserDatabaseManager.DEFAULT_REALM_NAME : realmName;
        Realm realm = UserDatabaseManager.getInstance().getRealm(realRealmName);
        return UserDatabaseManager.getInstance().getUserDatabase(realm);   
    }
    
    /** This auxiliary method is used to extract credentials from Agent authentication
      * Strings.
      */
    private static String getBefore(String value, String toFind) {
        int indexOf = value.indexOf(toFind);
        return indexOf == -1 ? null : value.substring(0, indexOf);
    }

    /** This auxiliary method is used to extract credentials from Agent authentication
      * Strings.
      */
    private static String getAfter(String value, String toFind) {
        int indexOf = value.indexOf(toFind);
        return indexOf == -1 ? null : value.substring(indexOf + 1);
    }
}
