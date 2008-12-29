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

import com.adito.vfs.VFSLockManager;
import com.adito.vfs.VFSResource;
import com.adito.vfs.webdav.DAVException;
import com.adito.vfs.webdav.DAVMethod;
import com.adito.vfs.webdav.DAVTransaction;
import com.adito.vfs.webdav.LockedException;

/**
 * <p>
 * <a href="http://www.rfc-editor.org/rfc/rfc2518.txt">WebDAV</a>
 * <code>MKCOL</code> metohd implementation.
 * </p>
 * 
 * @author <a href="http://www.betaversion.org/~pier/">Pier Fumagalli</a>
 */
public class MKCOL implements DAVMethod {

    /**
     * <p>
     * Create a new {@link MKCOL} instance.
     * </p>
     */
    public MKCOL() {
        super();
    }

    /**
     * <p>
     * Process the <code>MKCOL</code> method.
     * </p>
     */
    public void process(DAVTransaction transaction, VFSResource resource) throws LockedException, IOException {

    	String handle = VFSLockManager.getNewHandle();
    	VFSLockManager.getInstance().lock(resource, transaction.getSessionInfo(), true, true, handle);
    	
        try {
            transaction.setHeader("content-type", "application/octet-stream");
            /*
             * Unsupported media type, we don't want content
             */
            if (transaction.getInputStream() != null)
                throw new DAVException(415, "No request body allowed in request");

            /* Create the collection */
            resource.makeCollection();
            transaction.setStatus(201);
            resource.getMount().resourceCollectionCreated(resource, transaction, null);
        } catch (Exception e) {
            resource.getMount().resourceCollectionCreated(resource, transaction, e);
            IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        } finally {
        	VFSLockManager.getInstance().unlock(transaction.getSessionInfo(), handle);
        }
    }
}