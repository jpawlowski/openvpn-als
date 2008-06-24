
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
			
package com.adito.vfs;

import java.io.IOException;

import org.apache.commons.vfs.FileObject;

import com.adito.security.PasswordCredentials;
import com.adito.vfs.webdav.DAVAuthenticationRequiredException;
import com.adito.vfs.webdav.DAVTransaction;

/**
 * Implementations of this interface provide the root
 * of a single configured <i>Mount</i> to some file system specific file system.
 * <p>
 * The mount is responsible for creating {@link com.adito.vfs.VFSResource}
 * objects suitable for the file system.  
 */
public interface VFSMount {
    
    /**
     * Get the store from which this mount was created.
     * 
     * @return store
     */
    public VFSStore getStore();
    
    /**
     * Get a resource given its path relative to this mount. To get the
     * root resource supply an empty string as the path.
     * 
     * @param path path of resource 
     * @param requestCredentials request credentials
     * @return resource
     * @throws IOException on any error
     */
    public VFSResource getResource(String path, PasswordCredentials requestCredentials) throws IOException;
    
    /**
     * Return the pathname for this mount. It must have a trailing slash (/)
     * and be encoded.
     * 
     * @return mount string
     */
    public String getMountString();
    
    
    /**
     * Get if this mount is read only.
     * 
     * @return read only
     */
    public boolean isReadOnly();
    
	/**
	 * Create a file object. 
	 * 
	 * @param path path
	 * @param requestCredentials credentials
	 * @return file object
	 * @throws IOException on any error
	 * @throws DAVAuthenticationRequiredException
	 */
	public FileObject createAuthenticatedVFSFileObject(String path, PasswordCredentials requestCredentials) throws IOException, DAVAuthenticationRequiredException;
    
    /**
     * Invoked when a resource is copied from this mount. If the copy failed
     * then a non-null exception will provided
     * 
     * @param resource resource
     * @param destination destination resource
     * @param transaction transaction
     * @param exception any error or <code>null</code> if succesful
     */
    public void resourceCopy(VFSResource resource, VFSResource destination, DAVTransaction transaction, Throwable exception);
    
    /**
     * Invoked when a resource is deleted from this mount. If the delete failed
     * then a non-null exception will provided
     * 
     * @param resource resource
     * @param transaction transaction
     * @param exception any error or <code>null</code> if succesful
     */
    public void resourceDelete(VFSResource resource, DAVTransaction transaction, Throwable exception);
    
    /**
     * Invoked when a collection resource is listed from this mount. If the access failed
     * then a non-null exception will provided
     * 
     * @param resource resource
     * @param transaction transaction
     * @param exception any error or <code>null</code> if succesful
     */
    public void resourceAccessList(VFSResource resource, DAVTransaction transaction, Throwable exception);

    /**
     * Invoked when a download of a resource from this mount starts.
     * 
     * @param resource resource
     * @param transaction transaction
     */
    public void resourceAccessDownloading(VFSResource resource, DAVTransaction transaction);
    
    /**
     * Invoked when a download of a resource from this mount starts.
     * 
     * @param resource resource
     * @param transaction transaction
     * @param exception any error or <code>null</code> if succesful
     */
    public void resourceAccessDownloadComplete(VFSResource resource, DAVTransaction transaction, Throwable exception);
    
    /**
     * Invoked when a collection resource is created
     * 
     * @param resource resource
     * @param transaction transaction
     * @param exception any error or <code>null</code> if succesful
     */
    public void resourceCollectionCreated(VFSResource resource, DAVTransaction transaction, Throwable exception);

    /**
     * Invoked when a resource is moved
     * 
     * @param resource resource
     * @param destination destination
     * @param transaction transaction
     * @param exception any error or <code>null</code> if succesful
     */
    public void resourceMoved(VFSResource resource, VFSResource destination, DAVTransaction transaction, Throwable exception);

    /**
     * Invoked when a resource is uploaded
     * 
     * @param resource resource
     * @param transaction transaction
     * @param exception any error or <code>null</code> if succesful
     */
    public void resourceUpload(VFSResource resource, DAVTransaction transaction, Throwable exception);
}
