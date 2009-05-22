
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.core.RequestParameterMap;

/**
 * Interface to be implemented to provide a single <i>Authentication Modules</i>.
 * <p>
 * Authentication modules provide the logic and the user interface for a single
 * stage in an <i>Authentication Scheme</i> that a user must complete to 
 * be able to logon on use OpenVPN-ALS's services.
 * <p>
 * Each module must have an {@link com.ovpnals.security.AuthenticationModuleDefinition}
 * that must be registed with the {@link com.ovpnals.security.AuthenticationModuleManager}.
 * <p>
 * When authentication is required by the module, the first thing that happens
 * is all of the modules in the scheme are initialised by called their {@link #init(AuthenticationScheme)}
 * methods. 
 * <p>
 * When it is this schemes turn, the {@link #authenticate(HttpServletRequest, RequestParameterMap)}
 * method will be called expecting either a {@link com.ovpnals.security.Credentials}
 * object or any exception to be thrown if the authentication failed.
 * <p>
 * If applicable, each module must return the page to a JSP page that provides
 * the web based user interface for the module.
 * <p>
 * A module may be capable of supporting the entering of a username, in which
 * case it is known as a <i>Primary Authentication Modules</i>. If this 
 * capability is not available, the module is a <i>Secondary Authentication Module</i>
 * and may only be used after a primary has already been used.
 * <p>
 * There is a third type called a <i>System Authentication Module</i> which is
 * used interally by the OpenVPN-ALS or its plugins but never presented to
 * user directly. These are currently used for Webdav and Embedded client
 * logons. 
 * 
 * @see com.ovpnals.security.AuthenticationScheme
 * @see com.ovpnals.security.AuthenticationModuleDefinition
 * @see com.ovpnals.security.AuthenticationModuleManager
 */
public interface AuthenticationModule {
    
    /**
     * Initialise the authenitcation module
     * 
     * @param session authentication scheme 
     */
    public void init(AuthenticationScheme session);
    
    /**
     * Get the name of this module. 
     * 
     * @return module name
     */
    public String getName();
    
    /**
     * Invokeded when all modules in the scheme are complete and the user
     * is now logged on.
     * 
     * @throws SecurityErrorException on any error
     */
    public void authenticationComplete() throws SecurityErrorException ;
    
    /**
     * Invoked when the user submits the authentication information for
     * this module. If the authentication details supplied are not valid
     * then a {@link InvalidLoginCredentialsException} should be thrown.
     * <p>
     * A {@link Credentials} object may be returned that will be stored in
     * the session and possibly used to sign on to external other resources
     * automatically.  
     * 
     * @param request request
     * @param parameters parameters
     * @return credentials
     * @throws InvalidLoginCredentialsException if authentication credentials incorrect
     * @throws AccountLockedException if the account has been lock
     * @throws SecurityErrorException on any other error
     * @throws InputRequiredException 
     */
    public Credentials authenticate(HttpServletRequest request, RequestParameterMap parameters) throws 
    	InvalidLoginCredentialsException,
    	AccountLockedException,
    	SecurityErrorException,
    	InputRequiredException;
    
    /**
     * Get the path to the JSP fragment to be used for collecting the 
     * authentication details from the user. <code>null</code> may be
     * returned if the module is a <i>System Authentication Module</i>.
     *   
     * @return include poage
     */
    public String getInclude();
    
    /**
     * Invoked before authentication for this module begins (i.e. just before
     * the JSP page is displayed). If a module is returning <code>false</code>
     * from {@link #isRequired()} then it may return a forward to move onto
     * instead of going to the authentication JSP page returned by {@link #getInclude()}.
     * This is to allow modules that may require 'first time configuration'
     * such as the personal answers module.
     * 
     * @param mapping mapping
     * @param request request
     * @param response response
     * @return forward page to forward to or <code>null</code> to continue as normal
     * @throws SecurityErrorException
     */
    public ActionForward startAuthentication(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response) throws SecurityErrorException ;
    
    /**
     * Get if this module is required. If it false then it is allowed to forward
     * to a page other than the one return by {@link #getInclude()}.
     * This is to allow modules that may require 'first time configuration'
     * such as the personal answers module.
     * 
     * @return required
     */
    public boolean isRequired(); 

}

