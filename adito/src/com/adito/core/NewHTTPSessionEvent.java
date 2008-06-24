
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
			
package com.adito.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * Fired when a new HTTP session is created. The session created is 
 * available either from the parameter or from the request object
 * specified.
 */
public class NewHTTPSessionEvent extends CoreEvent {
    
    private HttpServletRequest request;
    private HttpServletResponse response;

    /**
     * Constructor.
     *
     * @param source source of event
     * @param request request
     * @param response response
     */
    public NewHTTPSessionEvent(Object source, HttpServletRequest request, HttpServletResponse response) {
        super(source, CoreEventConstants.NEW_HTTP_SESSION, request.getSession(), null);
        this.request = request;
        this.response = response;
    }

    /**
     * Get the request that caused this new session event. The session
     * itself may be retrieved from this request object.
     *  
     * @return request
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * Get the response for the request that caused this new session event.
     *   
     * @return response
     */
    public HttpServletResponse getResponse() {
        return response;
    }
    
    /**
     * Get the new session that was created.
     * 
     * @return new session
     */
    public HttpSession getSession() {
        return (HttpSession)getParameter();
    }
}
