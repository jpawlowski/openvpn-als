
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
			
package com.maverick.http;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Vector;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class PostMethod extends HttpMethod {

    public PostMethod(String uri) {
        super("POST", uri); //$NON-NLS-1$
    }

    public HttpResponse execute(HttpRequest request, HttpConnection con) throws IOException {

        request.setHeaderField("Host", con.getHostHeaderValue()); //$NON-NLS-1$
        request.setHeaderField("User-Agent", HttpClient.USER_AGENT); //$NON-NLS-1$

        // Prepare request body
        String encodedParams = ""; //$NON-NLS-1$
        for (Enumeration e = getParameterNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            Vector values = getParameterValueList(name);
            for (Enumeration e2 = values.elements(); e2.hasMoreElements();) {
                String value = (String) e2.nextElement();
                encodedParams += (encodedParams.length() > 0 ? "&" : "") + URLEncoder.encode(name) + "=" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    URLEncoder.encode(value);
            }
        }

        byte[] body = encodedParams.getBytes();
        request.setHeaderField("Content-Type", "application/x-www-form-urlencoded"); //$NON-NLS-1$ //$NON-NLS-2$
        request.setHeaderField("Content-Length", String.valueOf(body.length)); //$NON-NLS-1$

        request.performRequest(this, con);

        con.getOutputStream().write(body);

        return new HttpResponse(con);
    }
}
