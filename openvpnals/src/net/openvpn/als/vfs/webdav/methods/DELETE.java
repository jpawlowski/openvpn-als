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

import org.apache.commons.vfs.FileSystemException;

import net.openvpn.als.vfs.VFSLockManager;
import net.openvpn.als.vfs.VFSResource;
import net.openvpn.als.vfs.webdav.DAVMethod;
import net.openvpn.als.vfs.webdav.DAVMultiStatus;
import net.openvpn.als.vfs.webdav.DAVTransaction;
import net.openvpn.als.vfs.webdav.LockedException;

/**
 * <a href="http://www.rfc-editor.org/rfc/rfc2518.txt">WebDAV</a>
 * <code>DELETE</code> method implementation.
 * 
 * @author <a href="http://www.betaversion.org/~pier/">Pier Fumagalli</a>
 */
public class DELETE implements DAVMethod {

    /**
     * <p>
     * Create a new {@link DELETE} instance.
     * </p>
     */
    public DELETE() {
        super();
    }

    /**
     * <p>
     * Process the <code>DELETE</code> method.
     * </p>
     */
    public void process(DAVTransaction transaction, VFSResource resource) throws LockedException, IOException {
    	
    	String handle = VFSLockManager.getNewHandle();
    	VFSLockManager.getInstance().lock(resource, transaction.getSessionInfo(), true, true, handle);
    	
        try {
            try {
                resource.delete();
                transaction.setStatus(204);
                resource.getMount().resourceDelete(resource, transaction, null);
            } catch (FileSystemException ex) {
                transaction.setStatus(423);
                DAVMultiStatus s = new DAVMultiStatus();
                throw s;
            }
        } catch (DAVMultiStatus multistatus) {
            multistatus.write(transaction);
            resource.getMount().resourceDelete(resource, transaction, multistatus);
        } finally {
        	VFSLockManager.getInstance().unlock(transaction.getSessionInfo(), handle);
        }
    }
}
