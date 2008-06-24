
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
import java.io.StringWriter;

import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLWriter;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class PropFindMethod extends HttpMethod {

    String depth;

    public PropFindMethod(String uri, String depth) {
        super("PROPFIND", uri); //$NON-NLS-1$
        this.depth = depth;
    }

    public HttpResponse execute(HttpRequest request, HttpConnection con) throws IOException {

        request.setHeaderField("Host", con.getHostHeaderValue()); //$NON-NLS-1$
        if (depth != null)
            request.setHeaderField("Depth", depth); //$NON-NLS-1$
        request.setHeaderField("Content-Type", "text/xml; charset=\"UTF-8\""); //$NON-NLS-1$ //$NON-NLS-2$

        IXMLElement root = new XMLElement(WebDAVConstants.PROPFIND_ELEM, WebDAVConstants.XML_DAV_NAMESPACE);
        root.addChild(new XMLElement(WebDAVConstants.ALLPROP_ELEM));

        StringWriter xml = new StringWriter();
        XMLWriter writer = new XMLWriter(xml);
        writer.write(root);

        String str = WebDAVConstants.XML_TEMPLATE + "\r\n" + xml.toString(); //$NON-NLS-1$
        byte[] content = str.getBytes("UTF8"); //$NON-NLS-1$

        request.setHeaderField("Content-Length", String.valueOf(content.length)); //$NON-NLS-1$

        request.performRequest(this, con);
        con.getOutputStream().write(content);

        return new HttpResponse(con);
    }

}
