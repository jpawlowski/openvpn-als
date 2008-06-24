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

import com.adito.vfs.VFSResource;
import com.adito.vfs.webdav.DAVMethod;
import com.adito.vfs.webdav.DAVProcessor;
import com.adito.vfs.webdav.DAVTransaction;

/**
 * <p><a href="http://www.rfc-editor.org/rfc/rfc2616.txt">HTTP</a>
 * <code>OPTIONS</code> metohd implementation.</p> 
 *
 * @author <a href="http://www.betaversion.org/~pier/">Pier Fumagalli</a>
 */
public class OPTIONS implements DAVMethod {

    /**
     * <p>Create a new {@link OPTIONS} instance.</p>
     */
    public OPTIONS() {
        super();
    }

    /**
     * <p>Process the <code>OPTIONS</code> method.</p>
     */
    public void process(DAVTransaction transaction, VFSResource resource)
    throws IOException {
        transaction.setHeader("Allow", DAVProcessor.METHODS);
        transaction.setHeader("DAV", "1");
        transaction.setStatus(200);
    }

}
