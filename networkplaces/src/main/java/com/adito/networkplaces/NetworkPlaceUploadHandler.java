
				/*
 *  Adito
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
			
package com.adito.networkplaces;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForward;
import org.apache.struts.upload.FormFile;

import com.adito.core.CoreServlet;
import com.adito.core.UploadHandler;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.LaunchSessionFactory;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.vfs.UploadDetails;
import com.adito.vfs.VFSResource;
import com.adito.vfs.webdav.DAVServlet;


/**
 * Upload handler that uploads to a <i>VFS</i> resource. 
 */
public class NetworkPlaceUploadHandler implements UploadHandler {
	
	public static final String TYPE_VFS = "VFS";

    public ActionForward performUpload(HttpServletRequest request, HttpServletResponse response, UploadDetails upload,
                                       FormFile uploadFile) throws Exception {

        /*
         * If an upload was attempted after session timeout, the login screen
         * would have interrupted the upload. In this case just go straight back
         * to the home page.
         */
        if (upload.getResourcePath() == null || upload.getResourcePath().length() == 0) {
            return upload.getUploadedForward();
        }

        if (uploadFile == null || uploadFile.getFileName() == null || uploadFile.getFileName().trim().equals("")) {
            return upload.getUploadedForward();
        }

        LaunchSession launchSession = null;
        VFSResource res = null;
        try {
            
            // Get the launch session
            SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
            launchSession = session == null ? null : LaunchSessionFactory.getInstance().getLaunchSession(session, upload.getExtraAttribute2());
            if(launchSession == null) {
                throw new Exception("No launch session.");
            }
            launchSession.checkAccessRights(null, session);

            res = DAVServlet.getDAVResource(launchSession, request, response, upload.getResourcePath() + "/" + uploadFile.getFileName());
            
            res.getFile().exists();
            InputStream in = uploadFile.getInputStream();
            OutputStream out = res.getOutputStream();
            try {
                byte[] buf = new byte[4096];
                int read;
                while (true) {
                    read = in.read(buf, 0, buf.length);
                    if (read == -1) {
                        break;
                    }
                    out.write(buf, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }
            if (res.getMount().getStore().getProvider().isFireEvents()) {
                CoreServlet.getServlet().fireCoreEvent(
                    NetworkPlaceResourceType.getResourceAccessUploadEvent(this, launchSession, request, res.getFullPath(), res.getFile().getName().getURI(), uploadFile
                                    .getFileName(), null));
            }
            return upload.getUploadedForward();
        } catch (Exception e) {
            if (res != null && res.getMount().getStore().getProvider().isFireEvents()) {
                CoreServlet.getServlet().fireCoreEvent(
                    NetworkPlaceResourceType.getResourceAccessUploadEvent(this, launchSession, request, res.getFullPath(), res.getFile().getName().getURI(), uploadFile
                                    .getFileName(), e));
            }
            /*
             * Close the stream so the client gets an error straight away rather
             * than having to wait for the file to upload
             */
            try {
                request.getInputStream().close();
            } catch (IOException ioe) {
                throw ioe;
            }
            throw e;
        }
    }

    public boolean checkFileToUpload(HttpServletRequest request, HttpServletResponse response, UploadDetails fileUpload, FormFile file) throws IOException, Exception {

        if (fileUpload.getResourcePath() == null || fileUpload.getResourcePath().length() == 0) {
            return false;
        }

        if (file==null || file.getFileName() == null || file.getFileName().trim().equals("")) {
            return false;
        }
        
        // Get the launch session
        SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
        LaunchSession launchSession = session == null ? null : LaunchSessionFactory.getInstance().getLaunchSession(session, fileUpload.getExtraAttribute2());
        if(launchSession == null) {
            throw new Exception("No launch session.");
        }

        VFSResource res = DAVServlet.getDAVResource(launchSession, request, response, fileUpload.getResourcePath() + "/" + file.getFileName());

        return res.getFile().exists();
    }
    
}
