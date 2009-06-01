
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
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class PutMethod extends AsyncHttpMethod {

    ContentSource source;

    public PutMethod(String uri, ContentSource source) throws IOException {
        super("PUT", uri); //$NON-NLS-1$
        this.source = source;
    }

    public PutMethod(String name, String uri, ContentSource source) throws IOException {
        super(name, uri);
        this.source = source;
    }

    public void executeAsync(HttpRequest request, HttpConnection con) throws IOException {

        request.setHeaderField("Host", con.getHostHeaderValue()); //$NON-NLS-1$
        request.setHeaderField("User-Agent", HttpClient.USER_AGENT); //$NON-NLS-1$
        if (source != null)
            source.setHeaders(request, con);
        request.performRequest(this, con);

    }

    public HttpResponse execute(HttpRequest request, HttpConnection con) throws IOException {
        executeAsync(request, con);
        return new HttpResponse(con);
    }
}
