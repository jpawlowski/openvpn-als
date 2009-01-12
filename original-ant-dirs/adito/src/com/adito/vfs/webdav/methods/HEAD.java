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
package com.adito.vfs.webdav.methods;

import java.io.IOException;
import java.util.Date;

import com.adito.boot.Util;
import com.adito.vfs.VFSLockManager;
import com.adito.vfs.VFSResource;
import com.adito.vfs.webdav.DAVMethod;
import com.adito.vfs.webdav.DAVRedirection;
import com.adito.vfs.webdav.DAVTransaction;
import com.adito.vfs.webdav.DAVUtilities;
import com.adito.vfs.webdav.LockedException;

/**
 * <p><a href="http://www.rfc-editor.org/rfc/rfc2616.txt">HTTP</a>
 * <code>HEAD</code> metohd implementation.</p> 
 *
 * @author <a href="http://www.betaversion.org/~pier/">Pier Fumagalli</a>
 */
public class HEAD implements DAVMethod {
    public static final String COLLECTION_MIME_TYPE = "text/html"; 
    /**
     * <p>Create a new {@link HEAD} instance.</p>
     */
    public HEAD() {
        super();
    }

    /**
     * <p>Process the <code>HEAD</code> method.</p>
     */
    public void process(DAVTransaction transaction, VFSResource resource)
    throws LockedException, IOException {
    	String handle = VFSLockManager.getNewHandle();
    	VFSLockManager.getInstance().lock(resource, transaction.getSessionInfo(), false, true, handle);
    	try {
    		doHead(transaction, resource);
    	} finally {
    		VFSLockManager.getInstance().unlock(transaction.getSessionInfo(), handle);
    	}
    }
    
    protected void doHead(DAVTransaction transaction, VFSResource resource)
    throws LockedException, IOException {
        /* Check if we have to force a resource not found or a redirection */
        if (resource.isNull()) {
         
            /**
             * LDP - Don't throw a DAVException but set some headers, this is
             * to make Windows Webfolders work
             */
            String mime = COLLECTION_MIME_TYPE + "; charset=\"utf-8\"";
            transaction.setContentType(mime);
            Util.noCache(transaction.getResponse());
            
            transaction.setStatus(404);
            
            return;
        }
        if (transaction.isRequiredRootRedirect() || !transaction.isResourcePath(resource.getFullPath())) {
           throw new DAVRedirection(false, resource);
        }

        /* Check if this is a conditional (processable only for resources) */
        Date ifmod = transaction.getIfModifiedSince();
        Date lsmod = resource.getLastModified();
        if (resource.isResource() && (ifmod != null) && (lsmod != null)) {
            /* HTTP doesn't send milliseconds, but Java does, so, reset them */
            lsmod = new Date(((long)(lsmod.getTime() / 1000)) * 1000);
           // if (!ifmod.before(lsmod)) throw new DAVNotModified(resource);
        }

        /* Set the headers of this method */
        String ctyp = resource.getContentType();
        String etag = resource.getEntityTag();
        String lmod = DAVUtilities.format(resource.getLastModified());
        String clen = DAVUtilities.format(resource.getContentLength());
        
        /* Set the normal headers that are required for a GET */
        if (ctyp != null) transaction.setContentType(ctyp == null ? "application/octet-stream" : ctyp);
        if (etag != null) transaction.setHeader("ETag", etag);
        if (lmod != null) transaction.setHeader("Last-Modified", lmod);
        if (clen != null) transaction.setHeader("Content-Length", clen);    	
    }
}
