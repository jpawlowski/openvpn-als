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
 * ========================================================================== *
 *                                                                            *
 *                                                                            */

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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.DefaultFileReplicator;
import org.apache.commons.vfs.impl.DefaultFileSystemManager;

import com.adito.boot.SystemProperties;
import com.adito.boot.Util;
import com.adito.core.BundleActionMessage;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.LaunchSessionManager;
import com.adito.policyframework.NoPermissionException;
import com.adito.policyframework.PolicyException;
import com.adito.security.Constants;
import com.adito.security.PasswordCredentials;
import com.adito.security.SessionInfo;
import com.adito.vfs.utils.DAVCredentialsCache;
import com.adito.vfs.webdav.DAVAuthenticationRequiredException;
import com.adito.vfs.webdav.DAVBundleActionMessageException;
import com.adito.vfs.webdav.DAVException;
import com.adito.vfs.webdav.DAVListener;
import com.adito.vfs.webdav.DAVUtilities;

/**
 * <p>
 * A simple class representing a {@link File} based WebDAV repository.
 * </p>
 * 
 * @author <a href="http://www.betaversion.org/~pier/">Pier Fumagalli</a>
 */
public class VFSRepository {

    final static Log log = LogFactory.getLog(VFSRepository.class);

    final static String REPOSITORY_ATTR = "networkPlacesRepository";
    /**
     * <p>
     * The {@link Set} of all configured {@link DAVListener}s.
     * </p>
     */
    private Set<DAVListener> listeners = new HashSet<DAVListener>();

    /**
     * <p>
     * The {@link FileSystemManager} used to retrieve resources
     * </p>
     */
    private FileSystemManager fsManager;

    /**
     * <p>
     * The {@link VFSStore} implementations being managed by the repository
     * </p>
     */
    private Map<String,VFSStore> stores;

    /** <p>A cache of resources for the life of this transaction */
    //private Map resourceCache = new HashMap();
    
    private HttpSession session;
    
    /**
     * Constructor.
     *
     * @param session session
     * @throws DAVBundleActionMessageException on any error
     */
    public VFSRepository(HttpSession session) throws DAVBundleActionMessageException {
    	
    	this.session = session;
        try {
            fsManager = VFS.getManager();
            ((DefaultFileSystemManager)fsManager).setBaseFile(new File(SystemProperties.get("user.dir")));
            
        } catch (FileSystemException e1) {
        	log.error(e1);
            throw new DAVBundleActionMessageException(new BundleActionMessage("vfs", "vfs.fsManager.failed", e1.getMessage()));
        }
        
        try {
            stores = VFSProviderManager.getInstance().createStores(this);
        } catch (Exception e) {
            log.error(e);
            throw new DAVBundleActionMessageException(new BundleActionMessage("vfs", "vfs.store.creation.failed"));
        }
    }
    
    /**
     * Get the session the repository was created under.
     * 
     * @return session
     */
    public SessionInfo getSession() {
    	return (SessionInfo)session.getAttribute(Constants.SESSION_INFO);
    }

    /**
     * Return the {@link VFSResource} for the path.
     * 
     * @param launchSession launch session
     * @param path an absolute or relative {@link String} identifying the
     *        resource.
     * @param requestCredentials request credentials
     * @return a <b>non-null</b> {@link VFSResource} instance.
     * @throws IOException
     * @throws DAVBundleActionMessageException
     * @throws NoPermissionException 
     * @throws PolicyException 
     */
    public VFSResource getResource(LaunchSession launchSession, String path, PasswordCredentials requestCredentials/*, DAVTransaction transaction*/) throws DAVBundleActionMessageException, IOException, PolicyException, NoPermissionException, DAVAuthenticationRequiredException {

        if (path == null) {
            log.error("Cannot list store root.");
            throw new DAVBundleActionMessageException(new BundleActionMessage("vfs", "vfs.store.root", path));
        }
        
        if(launchSession == null) {
        	throw new IOException("Must have launch session.");
        }
        
        path = DAVUtilities.stripLeadingSlash(path);
        if(path.startsWith("fs")) {
            path= DAVUtilities.stripLeadingSlash(path.substring(2));
        }

        String storeName = path;
        VFSStore store;
        String mountName = null;
        VFSMount mount;

        // Extract the store
        int idx = path.indexOf('/');
        if (idx != -1) {
            storeName = storeName.substring(0, idx);
            path = path.substring(idx + 1);
        } else {
            path = "";
        }

        if (storeName.equals("")) {
            return getRepositoryResource();
        } else {
            store = (VFSStore) this.getStore(storeName);
            if (store == null) {
                log.error("No store named \"" + storeName + "\".");
                throw new DAVException(404, "No store named \"" + storeName + "\".");
            }
        }

        // Extract the mount
        mountName = path;
        idx = path.indexOf('/', 1);
        if (idx != -1) {
            mountName = mountName.substring(0, idx);
            path = path.substring(idx + 1);
        } else {
            path = "";
        }
        if (mountName.length() == 0) {
            return store.getStoreResource(/*transaction*/);
        }        
        
        // Check the launch session is valid
        if(launchSession.isTracked())
        	launchSession.checkAccessRights(null, getSession());

        //
        try {
            mount = store.getMountFromString(mountName, launchSession);
        }
        catch(DAVAuthenticationRequiredException dare) {
            throw dare;
        }
        catch(Exception e) {
            log.error("Failed to get mount.", e);
            mount = null;
        }
        
        if (mount == null || mount.equals("")) {
            log.error("No mount named \"" + mountName + "\" for store \"" + storeName + "\".");
            throw new DAVException(404, "No mount named \"" + mountName + "\" for store \"" + storeName + "\".");
        }

        
        path = DAVUtilities.stripTrailingSlash(path);


        return mount.getResource(path, requestCredentials);
    }

    /**
     * Add a new {@link DAVListener} to the list of instances notified by this
     * {@link VFSRepository}.
     * 
     * @param listener listener to add
     */
    public void addListener(DAVListener listener) {
        if (listener != null)
            this.listeners.add(listener);
    }

    /**
     * Remove a {@link DAVListener} from the list of instances notified by this
     * {@link VFSRepository}.
     * 
     * @param listener listener to remove
     */
    public void removeListener(DAVListener listener) {
        if (listener != null)
            this.listeners.remove(listener);
    }

    /**
     * Notify all configured {@link DAVListener}s of an event.
     * 
     * @param resource resource 
     * @param event  event code
     */
    public void notify(VFSResource resource, int event) {
        if (resource == null)
            throw new NullPointerException("Null resource");
        if (resource.getMount().getStore().getRepository() != this)
            throw new IllegalArgumentException("Invalid resource");

        Iterator iterator = this.listeners.iterator();
        while (iterator.hasNext())
            try {
                ((DAVListener) iterator.next()).notify(resource, event);
            } catch (RuntimeException exception) {
                // Swallow any RuntimeException thrown by listeners.
            }
    }

    /**
     * Get the <i>Commons VFS</i> {@link FileSystemManager} instance.
     * 
     * @return file system manager
     */
    public FileSystemManager getFileSystemManager() {
        return fsManager;
    }

    /**
     * Get a resource that represents the entire repository.
     * 
     * @return resource repository
     */
    public VFSResource getRepositoryResource() {
        try {
            return new RepositoryResource(new URI(""));
        }
        catch(Exception e) {
            // shouldn't happen
            return null;
        }
    }

    /**
     * Get the object that caches used credentials.
     * 
     * @return credentials cache
     */
    public DAVCredentialsCache getCredentialsCache(){
    	// get the credentials cashe
    	DAVCredentialsCache credentialsCashe = (DAVCredentialsCache) session.getAttribute("CredentialsCashe");
    	// if there is not 1 make 1 then get the cashe
    	if (credentialsCashe == null){
    		session.setAttribute("CredentialsCashe", new DAVCredentialsCache());
    		credentialsCashe = (DAVCredentialsCache) session.getAttribute("CredentialsCashe");
    	}
    	return credentialsCashe;
    }    
    
    /**
     * Create a {@link VFSRepository} repository for the given session. The 
     * repository will be placed in the users session and used for all VFS
     * operations.
     * 
     * @param session
     * @return VFS repository
     * @throws DAVBundleActionMessageException
     * @throws Exception
     */
    public static VFSRepository getRepository(HttpSession session) throws DAVBundleActionMessageException, Exception {
    	VFSRepository repository = (VFSRepository) session.getAttribute(REPOSITORY_ATTR);
        if (repository == null) {
            repository = new VFSRepository(session);            
            session.setAttribute(REPOSITORY_ATTR, repository);
            if (log.isInfoEnabled())
            	log.info("Initialized repository");
        }
        return repository;
    }    
    
    /**
     * Remove a {@link VFSRepository} repository from the given session. 
     * @param session
     */
    public static void removeRepository(SessionInfo session) {
        HttpSession httpSession = session.getHttpSession();
        if(httpSession != null) {
            httpSession.removeAttribute(REPOSITORY_ATTR);
            if (log.isInfoEnabled())
                log.info("Removed repository");            
        }
    }
    
 
    /**
     * Get a store that will handle a given a scheme. 
     * 
     * @param scheme
     * @return store
     */
    public VFSStore getStore(String scheme) {
        for(Iterator i = stores.values().iterator(); i.hasNext(); ) {
            VFSStore s = (VFSStore)i.next();
            if(s.getProvider().willHandle(scheme)) {
                return s;
            }
        }
        return null;
    }
    
    class RepositoryResource extends AbstractVFSResource {

        RepositoryResource(URI relativeUri) {
            super(new LaunchSession(getSession()),
            	relativeUri, true, "", null, VFSRepository.this);
        }

        public Iterator getChildren() {
            List<VFSResource> l = new ArrayList<VFSResource>();
            for(Iterator i = stores.values().iterator(); i.hasNext(); ) {
                l.add(((VFSStore)i.next()).getStoreResource());
            }
            return l.iterator();
        }

		public boolean isBrowsable() throws IOException {
			return false;
		}
        
        
        
    }
}