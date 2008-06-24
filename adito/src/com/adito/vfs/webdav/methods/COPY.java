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
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.vfs.VFSLockManager;
import com.adito.vfs.VFSResource;
import com.adito.vfs.webdav.DAVException;
import com.adito.vfs.webdav.DAVMethod;
import com.adito.vfs.webdav.DAVMultiStatus;
import com.adito.vfs.webdav.DAVProcessor;
import com.adito.vfs.webdav.DAVServlet;
import com.adito.vfs.webdav.DAVTransaction;
import com.adito.vfs.webdav.LockedException;

/**
 * <p>
 * <a href="http://www.rfc-editor.org/rfc/rfc2518.txt">WebDAV</a>
 * <code>COPY</code> metohd implementation.
 * </p>
 * 
 * @author <a href="http://www.betaversion.org/~pier/">Pier Fumagalli</a>
 */
public class COPY implements DAVMethod {

    final static Log log = LogFactory.getLog(COPY.class);

    /**
     * <p>
     * Create a new {@link COPY} instance.
     * </p>
     */
    public COPY() {
        super();
    }

    /**
     * <p>
     * Process the <code>COPY</code> method.
     * </p>
     * 
     * @throws IOException
     */
    public void process(DAVTransaction transaction, VFSResource resource) throws LockedException, IOException {
    	
    	String handle = VFSLockManager.getNewHandle();
    	VFSLockManager.getInstance().lock(resource, transaction.getSessionInfo(), false, true, handle);
   
        String action = (String) transaction.getRequest().getMethod();

        VFSResource dest = null;
        
		try {
	        
	        URI target = transaction.getDestination();

	        if (target == null)
	            throw new DAVException(412, "No destination");
	        if(log.isDebugEnabled())
	            log.debug("Target " + target.getPath());
	        
			try {
	            DAVProcessor processor = DAVServlet.getDAVProcessor(transaction.getRequest());
	            dest = processor.getRepository().getResource(resource.getLaunchSession(), target.getPath(), transaction.getCredentials()/*, transaction*/);
	            VFSLockManager.getInstance().lock(dest, transaction.getSessionInfo(), true, false, handle);

	        } catch (Exception ex) {
	            log.error("Failed to get resource. ", ex);
	            transaction.setStatus(500);
	            return;
	        }
	        
		    int depth = transaction.getDepth();
		    boolean recursive = false;
		    if (depth == 0) {
		        recursive = false;
		    } else if (depth == DAVTransaction.INFINITY) {
		        recursive = true;
		    } else {
		        throw new DAVException(412, "Invalid Depth specified");
		    }
		    try {
		        resource.copy(dest, transaction.getOverwrite(), recursive);
		        transaction.setStatus(204);
		    } catch (DAVMultiStatus multistatus) {
		        multistatus.write(transaction);
		    }
		    if (action.equals("COPY")) {
		        resource.getMount().resourceCopy(resource, dest, transaction, null);
		    }
		} catch (Exception e) {
		    if (action.equals("COPY")) {
		        resource.getMount().resourceCopy(resource, dest, transaction, e);
		    }
		    IOException ioe = new IOException(e.getMessage());
		    ioe.initCause(e);
		    throw ioe;
		} finally {
			VFSLockManager.getInstance().unlock(transaction.getSessionInfo(), handle);
		}
		
    }
}
