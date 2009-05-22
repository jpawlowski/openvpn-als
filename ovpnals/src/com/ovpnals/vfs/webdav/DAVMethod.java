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
package com.ovpnals.vfs.webdav;

import java.io.IOException;

import com.ovpnals.vfs.VFSResource;


/**
 * <p>An interface describing the implementation of a 
 * <a href="http://www.rfc-editor.org/rfc/rfc2518.txt">WebDAV</a>
 * method.</p> 
 *
 * @author <a href="http://www.betaversion.org/~pier/">Pier Fumagalli</a>
 */
public interface DAVMethod {

    /**
     * <p>Process the specified {@link DAVTransaction}.</p>
     * 
     * @param transaction An object encapsulaing a WebDAV request/response.
     * @param resource The {@link VFSResource} to process.
     * @throws IOException If an I/O error occurred.
     */
    public void process(DAVTransaction transaction, VFSResource resource)
    throws LockedException, IOException;

}
