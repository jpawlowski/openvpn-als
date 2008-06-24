
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
			
package com.adito.networkplaces.store.cifs;

import java.io.IOException;
import java.net.UnknownHostException;

import jcifs.smb.SmbException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;

import com.adito.boot.SystemProperties;
import com.adito.networkplaces.AbstractNetworkPlaceMount;
import com.adito.policyframework.LaunchSession;
import com.adito.security.PasswordCredentials;
import com.adito.vfs.VFSStore;
import com.adito.vfs.utils.URI;
import com.adito.vfs.utils.URI.MalformedURIException;
import com.adito.vfs.webdav.DAVAuthenticationRequiredException;
import com.adito.vfs.webdav.DAVUtilities;

/**
 * <p>
 * CIFSMount is an implementation of <@link
 * com.adito.vfs.webdav.AbstractMount> for use with smb paths and also
 * Windows Network Neighborhood.
 */
public class CIFSMount extends AbstractNetworkPlaceMount {

    final static Log log = LogFactory.getLog(CIFSMount.class);

    /**
     * <p>
     * Constructor for creating the mount.
     * 
     * @param launchSession launch session
     * @param store
     */
    public CIFSMount(LaunchSession launchSession, VFSStore store) {
        super(launchSession, store);
    }
    
//    /* (non-Javadoc)
//     * @see com.adito.vfs.webdav.AbstractNetworkPlaceMount#getRootVFSURI(com.adito.vfs.webdav.DAVTransaction)
//     */
    public URI getRootVFSURI(String charset) throws MalformedURIException {
        try {
            return super.getRootVFSURI(charset);
        }
        catch(MalformedURIException muri) {
            /**
		    * This assumes the path is \\fileserver\share
            */
            String npath ="smb://" + getNetworkPlace().getUsername() + ":" + getNetworkPlace().getPassword() + "@" + getNetworkPlace().getPath().replace("\\\\", "").replace("//", "").replace("\\", "/");
            return DAVUtilities.processAndEncodeURI(npath, getStore().getRepository().getSession(), charset);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.vfs.webdav.DAVMount#createRoot(java.lang.String,
     *      com.adito.vfs.webdav.DAVTransaction)
     */
    public FileObject createVFSFileObject(String path, PasswordCredentials credentials/*, DAVTransaction transaction*/) throws IOException, DAVAuthenticationRequiredException {

        super.getStore().getName();
        
        URI uri = getRootVFSURI(SystemProperties.get("jcifs.encoding", "cp860"));

        try {
            uri.setScheme("smb");
            if (credentials != null) {
                uri.setUserinfo(DAVUtilities.encodeURIUserInfo(credentials.getUsername() + (credentials.getPassword() != null ? ":" + new String(credentials.getPassword()) : "")));
            }
            uri.setPath(uri.getPath() + (uri.getPath().endsWith("/") ? "" : "/") + DAVUtilities.encodePath(path, SystemProperties.get("jcifs.encoding", "cp860")));
            FileObject root = getStore().getRepository().getFileSystemManager().resolveFile(uri.toString());
            if (root.getType().equals(FileType.FOLDER)) {
                // Extra check so that the correct exception is thrown.
                root.getChildren();
            }
            return root;
        } catch (FileSystemException fse) {
            if (fse.getCause().getClass().getName().equals("jcifs.smb.SmbAuthException")) {
                throw new DAVAuthenticationRequiredException(getMountString());
            }
            if (fse.getCause() != null && fse.getCause() instanceof SmbException && ((SmbException) fse.getCause()).getRootCause() != null
                            && "Connection timeout".equals(((SmbException) fse.getCause()).getRootCause().getMessage())) {
                throw new UnknownHostException(uri.getHost());
            }
            if(log.isDebugEnabled())
                log.debug("File system exception! ", fse);
            throw fse;
        }
    }
}