
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
 * @author Lee David Painter
 */
public class HttpRequest extends HttpHeader {

    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(HttpRequest.class);
    // #endif

    protected HttpMethod method;
    int cycles;

    public HttpRequest() {
    }

    public void performRequest(HttpMethod method, HttpConnection con) throws IOException {

        String request = generateOutput(method.getName() + " " //$NON-NLS-1$
            + method.getURI() + " HTTP/" + method.getVersion()); //$NON-NLS-1$

        // #ifdef DEBUG
        log.debug(request);
        // #endif

        con.getOutputStream().write(request.getBytes());
        con.getOutputStream().flush();
    }

    void reset() {
        clearHeaderFields();
    }

}
