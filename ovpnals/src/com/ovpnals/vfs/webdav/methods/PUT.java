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
import java.io.InputStream;

import com.ovpnals.vfs.VFSLockManager;
import com.ovpnals.vfs.VFSOutputStream;
import com.ovpnals.vfs.VFSResource;
import com.ovpnals.vfs.webdav.DAVMethod;
import com.ovpnals.vfs.webdav.DAVTransaction;
import com.ovpnals.vfs.webdav.LockedException;

/**
 * <p>
 * <a href="http://www.rfc-editor.org/rfc/rfc2518.txt">WebDAV</a>
 * <code>PUT</code> metohd implementation.
 * </p>
 * 
 * @author <a href="http://www.betaversion.org/~pier/">Pier Fumagalli</a>
 */
public class PUT implements DAVMethod {

    /**
     * <p>
     * Create a new {@link PUT} instance.
     * </p>
     */
    public PUT() {
        super();
    }

    /**
     * <p>
     * Process the <code>PUT</code> method.
     * </p>
     */
    public void process(DAVTransaction transaction, VFSResource resource) throws LockedException, IOException {
    	
        String handle = VFSLockManager.getNewHandle();
        VFSLockManager.getInstance().lock(resource, transaction.getSessionInfo(), true, true, handle);
        try {
            /*
             * The HTTP status code will depend on the existance of the
             * resource: if not found: HTTP/1.1 201 Created if existing:
             * HTTP/1.1 204 No Content
             */
            transaction.setStatus(resource.isNull() ? 201 : 204);

            /* Open the streams for reading and writing */
            InputStream in = transaction.getInputStream();
            VFSOutputStream out = resource.getOutputStream();
        
            /* Write the content from the PUT to the specified resource */
            try {
                byte buffer[] = new byte[32768];
                int k = -1;
                while (in!=null && (k = in.read(buffer)) != -1)
                    out.write(buffer, 0, k);
                out.close();
            } finally {
                out.abort();
            }
            resource.getMount().resourceUpload(resource, transaction, null);
        } catch (Exception e) {
            resource.getMount().resourceUpload(resource, transaction, e);
            IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        } finally {
        	VFSLockManager.getInstance().unlock(transaction.getSessionInfo(), handle);
        }
    }
}
