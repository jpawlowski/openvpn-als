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
package net.openvpn.als.vfs.webdav.methods;

import java.io.IOException;
import java.net.URI;

import net.openvpn.als.vfs.VFSLockManager;
import net.openvpn.als.vfs.VFSResource;
import net.openvpn.als.vfs.webdav.DAVException;
import net.openvpn.als.vfs.webdav.DAVMethod;
import net.openvpn.als.vfs.webdav.DAVMultiStatus;
import net.openvpn.als.vfs.webdav.DAVProcessor;
import net.openvpn.als.vfs.webdav.DAVServlet;
import net.openvpn.als.vfs.webdav.DAVTransaction;
import net.openvpn.als.vfs.webdav.LockedException;

/**
 * <p>
 * <a href="http://www.rfc-editor.org/rfc/rfc2518.txt">WebDAV</a>
 * <code>MOVE</code> metohd implementation.
 * </p>
 * 
 * @author <a href="http://www.betaversion.org/~pier/">Pier Fumagalli</a>
 */
public class MOVE implements DAVMethod {

    /**
     * <p>
     * Create a new {@link MOVE} instance.
     * </p>
     */
    public MOVE() {
        super();
    }

    /**
     * <p>
     * Process the <code>MOVE</code> method.
     * </p>
     * 
     * @throws IOException
     */
    public void process(DAVTransaction transaction, VFSResource resource) throws LockedException, IOException {
        String handle = VFSLockManager.getNewHandle();
        VFSResource dest = null;
        VFSLockManager.getInstance().lock(resource, transaction.getSessionInfo(), true, true, handle);
        
        try {
            try {
                //super.process(transaction, resource);
            	DAVProcessor processor = DAVServlet.getDAVProcessor(transaction.getRequest());
            	URI target = transaction.getDestination();
            	if (target == null)
                    throw new DAVException(412, "No destination");
            	dest = processor.getRepository().getResource(resource.getLaunchSession(), target.getPath(), transaction.getCredentials());
            	
            	VFSLockManager.getInstance().lock(resource, transaction.getSessionInfo(), true, false, handle);
            	
            	resource.move(dest, true);
            	resource.getMount().resourceMoved(resource, dest, transaction, null);                
                transaction.setStatus(204);
            } catch (DAVMultiStatus multistatus) {
                multistatus.write(transaction);
            }
        } catch (Exception e) {
        	resource.getMount().resourceMoved(resource, dest, transaction, e);
        	if(e instanceof LockedException)
        		throw (LockedException) e;
            IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        } finally {
        	VFSLockManager.getInstance().unlock(transaction.getSessionInfo(), handle);
        }
    }
}
