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
package com.ovpnals.vfs.webdav.methods;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.util.URLUTF8Encoder;
import com.ovpnals.boot.SystemProperties;
import com.ovpnals.vfs.VFSLockManager;
import com.ovpnals.vfs.VFSResource;
import com.ovpnals.vfs.webdav.DAVException;
import com.ovpnals.vfs.webdav.DAVMethod;
import com.ovpnals.vfs.webdav.DAVRedirection;
import com.ovpnals.vfs.webdav.DAVTransaction;
import com.ovpnals.vfs.webdav.DAVUtilities;
import com.ovpnals.vfs.webdav.LockedException;

/**
 * <p>
 * <a href="http://www.rfc-editor.org/rfc/rfc2518.txt">WebDAV</a>
 * <code>PROPFIND</code> metohd implementation.
 * </p>
 * 
 * @author <a href="http://www.betaversion.org/~pier/">Pier Fumagalli</a>
 */
public class PROPFIND implements DAVMethod {

    private static Log log = LogFactory.getLog(PROPFIND.class);

    /**
     * <p>
     * Create a new {@link PROPFIND} instance.
     * </p>
     */
    public PROPFIND() {
        super();
    }

    /**
     * <p>
     * Process the <code>PROPFIND</code> method.
     * </p>
     */
    public void process(DAVTransaction transaction, VFSResource resource) throws LockedException, IOException {
        if (transaction.isRequiredRootRedirect() || !transaction.isResourcePath(resource.getFullPath())) {
            throw new DAVRedirection(false, resource);
        }

        String handle = VFSLockManager.getNewHandle();
        VFSLockManager.getInstance().lock(resource, transaction.getSessionInfo(), false, false, handle);
        try {
            /* Check depth */
            int depth = transaction.getDepth();
            if (depth > 1)
                throw new DAVException(403, "Invalid depth");

            if(SystemProperties.get("ovpnals.disableFolderBrowsing", "true").equals("true") && !resource.isBrowsable()) {
            	transaction.getResponse().sendError(404, "Not Found");
            	return;
            }
            /* What to do on a collection resource */
            transaction.setStatus(207);
            transaction.setContentType("text/xml; charset=\"utf-8\"");
            PrintWriter out = transaction.write("utf-8");

            /* Output the XML declaration and the root document tag */
            out.print("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            out.println("<D:multistatus xmlns:D=\"DAV:\">");

            /* Process this resource's property (always) */
            this.process(transaction, out, resource);

            /* Process this resource's children (if required) */
            if (resource.isCollection() && (depth > 0)) {
                Iterator children = resource.getChildren();
                while (children.hasNext()) {
                    VFSResource child = (VFSResource) children.next();
                    this.process(transaction, out, child);
                }
            }

            /* Close up the XML Multi-Status response */
            out.println("</D:multistatus>");
            out.flush();
            
            // Mount may be null at root
            if(resource.getMount() != null) {
                resource.getMount().resourceAccessList(resource, transaction, null);
            }
            
        } catch (Exception e) {
            // Mount may be null at root
            if(resource.getMount() != null) {
                resource.getMount().resourceAccessList(resource, transaction, e);
            }
            IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        } finally {
        	VFSLockManager.getInstance().unlock(transaction.getSessionInfo(), handle);
        }
    }

    private void process(DAVTransaction txn, PrintWriter out, VFSResource res) throws IOException {
        out.println(" <D:response>");

        if (log.isDebugEnabled())
            log.debug("Returning " + res.getFullURI().toString());

        /**
         * LDP - This was using toASCIIString which was causing problems with drive mapping. Changed to use getFullPath seems to 
         * fix this error and non ascii characters are now being shown correctly in drive mapping, network places and web folders. 
         */
        
        out.println(" <D:href>" + (txn.getRequest().isSecure() || SystemProperties.get("jetty.force.HTTPSRedirect", "false").equals("true") ? "https" : "http") + "://" + txn.getRequest().getHeader("Host")
                // LDP - Why fullpath? this seems to be broken on some stores. Use webfolder path instead        
        		+ URLUTF8Encoder.encode(res.getWebFolderPath(), false) + "</D:href>");
        out.println("  <D:propstat>");

        if (res.isNull()) {
            out.println("   <D:status>HTTP/1.1 404 Not Found</D:status>");

        } else {
            out.println("   <D:prop>");

            /* Figure out what we're dealing with here */
            if (res.isCollection()) {
                this.process(out, "resourcetype", "<D:collection/>");
                this.process(out, "getcontenttype", GET.COLLECTION_MIME_TYPE);
            } else {
                this.process(out, "getcontenttype", res.getContentType());
            }

            this.process(out, "getetag", res.getEntityTag());
            String lmod = DAVUtilities.format(res.getLastModified());
            this.process(out, "getlastmodified", lmod);
            String clen = DAVUtilities.format(res.getContentLength());
            this.process(out, "getcontentlength", clen);

            out.println("   </D:prop>");

            out.println("   <D:status>HTTP/1.1 200 OK</D:status>");

        }

        out.println("  </D:propstat>");
        out.println(" </D:response>");
    }

    private void process(PrintWriter out, String name, String value) {
        if (value == null)
            return;
        out.print("    <D:");
        out.print(name);
        out.print(">");
        out.print(value);
        out.print("</D:");
        out.print(name);
        out.println(">");
    }
    
    
}
