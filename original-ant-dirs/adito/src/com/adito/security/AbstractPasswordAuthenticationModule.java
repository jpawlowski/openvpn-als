
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
			
package com.adito.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.core.RequestParameterMap;
import com.adito.core.UserDatabaseManager;

/**
 * Abstract implementation of an {@link com.adito.security.AuthenticationModule}
 * that simply checks the supplied authentication details against the information
 * in the current {@link com.adito.security.UserDatabase}.
 */
public abstract class AbstractPasswordAuthenticationModule implements AuthenticationModule {

    // Protected instance variables

    protected AuthenticationScheme scheme;
    protected PasswordCredentials credentials;
    protected String moduleName;
    protected boolean required;
    private HttpServletRequest request ;

    /**
     * Constructor
     * 
     * @param moduleName module name
     * @param required required
     */
    public AbstractPasswordAuthenticationModule(String moduleName, boolean required) {
        this.moduleName = moduleName;
        this.required = required;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.adito.security.AuthenticationModule#getName()
     */
    public String getName() {
        return moduleName;
    }

    /* (non-Javadoc)
     * @see com.adito.security.AuthenticationModule#authenticate(javax.servlet.http.HttpServletRequest, com.adito.core.RequestParameterMap)
     */
    public Credentials authenticate(HttpServletRequest request, RequestParameterMap parameterMap)
                    throws InvalidLoginCredentialsException, SecurityErrorException, AccountLockedException, InputRequiredException {
        this.request = request;
    	
        if (scheme.getUser() == null) {
            // If no username has been supplied then just return to the logon
            // screen
        	UserDatabase udb = null;
        	try {
        		// TODO is getting the default realm correct here?
        		udb = UserDatabaseManager.getInstance().getUserDatabase(UserDatabaseManager.getInstance().getDefaultRealm());
                String username = parameterMap.getParameter("username");
                if (username==null || username.equals("")) {
                    throw new InvalidLoginCredentialsException();
                }
                try {
                    scheme.setUser(udb.getAccount(username));
                } catch (Exception e1) {
                	throw new InvalidLoginCredentialsException("Failed to load user.", e1);
                }
        	}
        	catch(Exception e) {
                throw new SecurityErrorException(SecurityErrorException.INTERNAL_ERROR, e, "Failed to logon.");
        	}
        }

        try {
            String password = parameterMap.getParameter("password");
            if (password == null || password.equals("")) {
                throw new InvalidLoginCredentialsException("No password supplied.");
            }

            try {
                User user = doLogon(scheme.getUsername(), password, scheme.getUser().getRealm().getResourceName());
                if (scheme.getUser() == null && user != null) {
                    scheme.setUser(user);
                }
            } catch (UserDatabaseException e) {
                throw new SecurityErrorException(SecurityErrorException.INTERNAL_ERROR, "Failed to logon.");
            }
            credentials = new PasswordCredentials(scheme.getUsername(), password.toCharArray());
            return credentials;
        } catch (InvalidLoginCredentialsException ilce) {
            throw ilce;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.adito.security.AuthenticationModule#init(com.adito.security.AuthenticationSession)
     */
    public void init(AuthenticationScheme scheme) {
        this.scheme = scheme;
    }

    protected User doLogon(String username, String password, String realmName) throws UserDatabaseException, InvalidLoginCredentialsException,
                    AccountLockedException {
        try {
            return UserDatabaseManager.getInstance().getUserDatabase(realmName).logon(username, password);
        } catch (Exception e) {
            if (e instanceof InvalidLoginCredentialsException){
                throw ((InvalidLoginCredentialsException)e);
    }
            throw new UserDatabaseException("Failed to initialise user database.", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.adito.security.AuthenticationModule#startAuthentication(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward startAuthentication(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response)
                    throws SecurityErrorException {
        return mapping.findForward("display");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.adito.security.AuthenticationModule#isRequired()
     */
    public boolean isRequired() {
        return required;
    }

    /* (non-Javadoc)
     * @see com.adito.security.AuthenticationModule#authenticationComplete()
     */
    public void authenticationComplete() throws SecurityErrorException {
        
    }

    /* (non-Javadoc)
     * @see com.adito.security.AuthenticationModule#getInclude()
     */
    public abstract String getInclude();

    public HttpServletRequest getRequest() {
        return request;
}
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }
}
