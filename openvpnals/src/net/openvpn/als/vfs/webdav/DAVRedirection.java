/* ========================================================================== *
 * Copyright (C) 2004-2005 Pier Fumagalli <http://www.betaversion.org/~pier/> *
 *                            All rights reserved.                            *
 * ========================================================================== *
 *                                                                            *
 * Licensed under the  Apache License, Version 2.0  (the "License").  You may *
 * not use this file except in compliance with the License.  You may obtain a *
 * copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>.       *
 *                                                                            *
 * Unless  required  by applicable  law or  agreed  to  in writing,  software *
 * distributed under the License is distributed on an  "AS IS" BASIS, WITHOUT *
 * WARRANTIES OR  CONDITIONS OF ANY KIND, either express or implied.  See the *
 * License for the  specific language  governing permissions  and limitations *
 * under the License.                                                         *
 *                                                                            *
 * ========================================================================== */
package net.openvpn.als.vfs.webdav;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

import net.openvpn.als.vfs.VFSResource;


/**
 * <p>A simple {@link DAVException} encapsulating an
 * <a href="http://www.rfc-editor.org/rfc/rfc2616.txt">HTTP</a> redirection
 * to a given {@link URI}.</p>
 *
 * @author <a href="http://www.betaversion.org/~pier/">Pier Fumagalli</a>
 */
public class DAVRedirection extends DAVException {

    private VFSResource location = null;

    /**
     * <p>Create a new {@link DAVRedirection} instance.</p>
     */
    public DAVRedirection(boolean permanent, VFSResource location) {
        super(permanent? 301: 302, "Redirection requested");
        this.location = location;
    }
    
    /**
     * <p>Return the target {@link URI} of the redirection.</p>
     */
    public VFSResource getLocation() {
        return this.location;
    }

    /**
     * <p>Write the body of this {@link DAVRedirection} to the specified
     * {@link DAVTransaction}'s output.</p>
     */
    public void write(DAVTransaction transaction)
    throws IOException {
        transaction.setContentType("text/html; charset=\"utf-8\"");
        transaction.setStatus(this.getStatus());

        /* Write the error message to the client */
        PrintWriter out = transaction.write("utf-8");
        out.println("<html>");
        out.print("<head><title>Redirection requested</title></head>");
        out.println("<body>");
        out.print("<p><b>The requested resource has moved ");
        out.print(this.getStatus() == 301? "permanently": "temporarily");
        out.println("</b></p>");

        if(this.location != null) {
	        transaction.setHeader("Location", location.getFullURI().toASCIIString());
	        out.print("<p>The location for the requested resource is <a href=\"");
	        out.print("/fs" + location.getFullURI().toASCIIString());
	        out.println("\">");
	        out.print("/fs" + location.getFullPath());
	        out.println("</a></p>");
        }
        out.println("</body>");
        out.println("</html>");
        out.flush();
    }
}
