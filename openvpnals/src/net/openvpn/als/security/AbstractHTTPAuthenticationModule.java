
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
			
package net.openvpn.als.security;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.maverick.crypto.encoders.Base64;
import net.openvpn.als.core.RequestParameterMap;
import net.openvpn.als.core.UserDatabaseManager;

/**
 * Abstract extension of {@link net.openvpn.als.security.AbstractPasswordAuthenticationModule}
 * that is suitable for authentication modules that use HTTP authentication. To
 * date this includes the HTTP authentication module and WebDAV.
 */
public abstract class AbstractHTTPAuthenticationModule extends AbstractPasswordAuthenticationModule {

    final static Log log = LogFactory.getLog(AbstractHTTPAuthenticationModule.class);
    
    // Protected instance variables
    
    protected String defaultRealm;

    /**
     * Constructor.
     * 
     * @param module module
     * @param required required
     * @param defaultRealm default authentication realm
     */
    public AbstractHTTPAuthenticationModule(String module, boolean required, String defaultRealm) {
        super(module, required);
        this.defaultRealm = defaultRealm;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.security.AuthenticationModule#startAuthentication(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward startAuthentication(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response)
                    throws SecurityErrorException {
        //	If the authentication request has already been sent then just
        // continue to logon
        if (request.getParameter("auth") != null) {
            if (request.getSession().getAttribute(Constants.AUTH_SENT) == null) {
                boolean hasAuthorization = request.getHeader("Authorization") != null
                                && Boolean.TRUE.equals(request.getSession().getAttribute(Constants.AUTH_SENT));
                try {
                    if (!hasAuthorization) {
                        sendAuthorizationError(request, response, defaultRealm);
                        return null;
                    }
                } catch (Exception e) {
                    throw new SecurityErrorException(SecurityErrorException.INTERNAL_ERROR, e);
                }
            }
            return new ActionForward("/logon.do", true);
        } else {
            return mapping.findForward("display");
        }
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AbstractPasswordAuthenticationModule#authenticate(javax.servlet.http.HttpServletRequest, net.openvpn.als.core.RequestParameterMap)
     */
    public Credentials authenticate(HttpServletRequest request, RequestParameterMap parameterMap)
                    throws InvalidLoginCredentialsException, SecurityErrorException, AccountLockedException, InputRequiredException {

        try {
            String authorization = request.getHeader("Authorization");
            if (authorization == null) {
                throw new SecurityErrorException(SecurityErrorException.INTERNAL_ERROR, "No credentials supplied.");
            }

            request.getSession().removeAttribute(Constants.AUTH_SENT);

            int idx = authorization.indexOf(' ');

            if (idx == -1 || idx == authorization.length() - 1) {
                throw new InvalidLoginCredentialsException("No Authorization provided.");
            }

            // Authenticate the user
            String method = authorization.substring(0, idx);

            if (!method.equalsIgnoreCase("basic")) {
                throw new InvalidLoginCredentialsException("Only HTTP Basic authentication is currently supported.");
            }

            // Extract the credentials - should be ticket:tunnel
            String encoded = authorization.substring(idx + 1);
            String httpCredentials = new String(Base64.decode(encoded));

            idx = httpCredentials.indexOf(':');

            if (idx == 0 || idx == -1 || idx == httpCredentials.length() - 1) {
                throw new InvalidLoginCredentialsException("Invalid authorization.");
            }

            // Get the user credentials
            String username = httpCredentials.substring(0, idx);
            String password = httpCredentials.substring(idx + 1);
            
            // See if there is a realm in the username
            idx = username.indexOf('\\');
            UserDatabase udb = null;
            if(idx != -1) {
            	String realmName = username.substring(0, idx);
            	try {
            		udb = UserDatabaseManager.getInstance().getUserDatabase(realmName);
                	username = username.substring(idx + 1);
            	}
            	catch(Exception e) {
            	}
            } else {
            	udb = UserDatabaseManager.getInstance().getDefaultUserDatabase();
            }

            try {
            	User account = udb.getAccount(username);
                scheme.setUser(account);
                if (password == null || password.equals("")) {
                    throw new InvalidLoginCredentialsException("No password supplied.");
                }
                account = doLogon(username, password, scheme.getUser().getRealm().getResourceName());
            } catch (InvalidLoginCredentialsException ilce) {
                throw ilce;
            } catch (UserNotFoundException unfe) {
                throw new InvalidLoginCredentialsException();
            } catch (Exception e) {
                throw new SecurityErrorException(SecurityErrorException.INTERNAL_ERROR, e);            	
            }
            credentials = new PasswordCredentials(username, password.toCharArray());
        } finally {
            request.getSession().removeAttribute(Constants.AUTH_SENT);
        }
        return credentials;
    }

    /**
     * @param request
     * @param response
     * @param realm
     * @throws IOException
     */
    public static void sendAuthorizationError(HttpServletRequest request, HttpServletResponse response, String realm)
                    throws IOException {
    	if (log.isInfoEnabled())
    		log.info("Sending auth request for realm " + realm);
        response.setHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        request.getSession().setAttribute(Constants.AUTH_SENT, Boolean.TRUE);
    }

}