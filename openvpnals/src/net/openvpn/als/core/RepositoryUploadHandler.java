
				/*
 *  OpenVPNALS
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			
package net.openvpn.als.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;
import org.apache.struts.upload.FormFile;

import net.openvpn.als.boot.Repository;
import net.openvpn.als.boot.RepositoryFactory;
import net.openvpn.als.boot.RepositoryStore;
import net.openvpn.als.boot.Util;
import net.openvpn.als.vfs.UploadDetails;

public class RepositoryUploadHandler implements UploadHandler {
    public static final String TYPE_REPOSITORY = "REPOSITORY";
    
    public RepositoryUploadHandler() {
        super();
    }

    public ActionForward performUpload(HttpServletRequest request, HttpServletResponse response, UploadDetails upload, FormFile uploadFile)
                    throws Exception {
        try {

            /* If an upload was attempted after session timeout, the login
             * screen would have interrupted the upload. In this case just go
             * straight back to the home page.
             */
            if(upload.getResourcePath() == null || upload.getResourcePath().length()==0) {
                return upload.getUploadedForward();
            }
            
            if(uploadFile.getFileName() == null || uploadFile.getFileName().trim().equals("")) {
                return upload.getUploadedForward();
            }

            // Get the repository store
            Repository repository = RepositoryFactory.getRepository();
            if(upload.getExtraAttribute1() == null || upload.getExtraAttribute1().equals("")) {
                throw new Exception("No store name provided.");
            }
            RepositoryStore store = repository.getStore(upload.getExtraAttribute1());
            
            // Do the upload            
            InputStream in = uploadFile.getInputStream();
            try {
                OutputStream out = store.getEntryOutputStream(upload.getResourcePath()) ;
                try {
                    Util.copy(in, out);
                }
                finally {
                    Util.closeStream(out);
                }
            }
            finally {
                Util.closeStream(in);
            }
            return upload.getUploadedForward();
        }
        catch(Exception e) {
            /* Close the stream so the client gets an error straight away rather
             * than having to wait for the file to upload
             */
            try {
                request.getInputStream().close();
            }
            catch(IOException ioe) {
                throw ioe;
            }
            throw e ;
        }
    }

    public boolean checkFileToUpload(HttpServletRequest request, HttpServletResponse response, UploadDetails fileUpload, FormFile file) throws IOException, Exception {
        // TODO Auto-generated method stub
        return false;
    }
    
}
