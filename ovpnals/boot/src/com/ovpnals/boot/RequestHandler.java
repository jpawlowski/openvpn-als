
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
			
package com.ovpnals.boot;

import java.io.IOException;

/**
 * Handles an HTTP request from the containing web server.
 * <p>
 * Whenever the containing web server receives a request, before passing it on
 * to the main web application it should called the {@link #handle(String, String, RequestHandlerRequest, RequestHandlerResponse)}
 * method on all registered request handlers.
 * <p>
 * Implementations should examine the request based on the arguments provided
 * and if they wish to process the request, do so and return <code>true</code>.
 * <p>
 * Returning <code>false</false> signifies that the handler didnt handle the
 * request and it should be passed on to the next handler (if any).
 */
public interface RequestHandler {

    /**
     * Handle a request
     * 
     * @param pathInContext path extracted from the HTTP method line
     * @param pathParams paramters passed
     * @param request request object
     * @param response response object
     * @return handled <code>true</code> if this handler handled te request
     * @throws RequestHandlerException on any exception associated with the handling of the request
     * @throws IOException on any input / output error
     */
    public boolean handle(String pathInContext, String pathParams, RequestHandlerRequest request, RequestHandlerResponse response) throws
                    IOException, RequestHandlerException;

}
