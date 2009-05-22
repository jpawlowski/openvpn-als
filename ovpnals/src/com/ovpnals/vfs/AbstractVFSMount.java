
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.vfs;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;

import com.ovpnals.boot.Util;
import com.ovpnals.policyframework.LaunchSession;
import com.ovpnals.security.PasswordCredentials;
import com.ovpnals.vfs.utils.URI;
import com.ovpnals.vfs.utils.URI.MalformedURIException;
import com.ovpnals.vfs.webdav.DAVAuthenticationRequiredException;
import com.ovpnals.vfs.webdav.DAVTransaction;
import com.ovpnals.vfs.webdav.DAVUtilities;

/**
 * An abstract implementation of a {@link VFSMount}.
 */
public abstract class AbstractVFSMount implements VFSMount {
	final static Log log = LogFactory.getLog(AbstractVFSMount.class);

	// Private instance variables

	private VFSStore store;
	private boolean readOnly;
	private LaunchSession launchSession;
	private String mountName;
	private boolean requiresOvpnAlsAuthentication;

    /**
	 * Constructor.
	 * 
	 * @param launchSession launch session
	 * @param store store
	 * @param mountName mount name
	 * @param readOnly read only
	 */
	public AbstractVFSMount(LaunchSession launchSession, VFSStore store, String mountName, boolean readOnly) {
		this.store = store;
		this.mountName = mountName;
		this.launchSession = launchSession;
		this.readOnly = readOnly;
	}

	/**
	 * Get the launch session
	 * 
	 * @return launch session
	 */
	public LaunchSession getLaunchSession() {
		return launchSession;
	}

	/**
	 * Set whether this mount is read-only. By default this is determined by the
	 * network place resource object.
	 * 
	 * @param readOnly read only
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * Get whether this mount is read-only. By default this is determined by the
	 * network place resource object.
	 * 
	 * @return read only
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * Create
	 * 
	 * @param path path
	 * @param credentials credentials
	 * @return resource
	 * @throws IOException on any error
	 * @throws DAVAuthenticationRequiredException if resources requires
	 *         authentication
	 */
	protected abstract FileObject createVFSFileObject(String path, PasswordCredentials credentials) throws IOException,
					DAVAuthenticationRequiredException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.webdav.DAVMount#getStore()
	 */
	public VFSStore getStore() {
		return store;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.webdav.DAVMount#getResource(java.lang.String,
	 *      com.ovpnals.vfs.webdav.DAVTransaction)
	 */
	public VFSResource getResource(String path, PasswordCredentials requestCredentials) throws IOException,
					DAVAuthenticationRequiredException {
		VFSResource parent = null;
		if(path.equals("")) {
			parent = store.getStoreResource();
		}
		return new FileObjectVFSResource(getLaunchSession(), this,
						parent,
						path,
						store.getRepository(),
						requestCredentials);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.webdav.DAVMount#getMountString()
	 */
	public String getMountString() {
		return this.getStore().getName() + "/" + DAVUtilities.encodePath(mountName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.VFSMount#resourceCopy(com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.webdav.DAVTransaction, java.lang.Throwable)
	 */
	public void resourceCopy(VFSResource resource, VFSResource destination, DAVTransaction transaction, Throwable exception) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.VFSMount#resourceDelete(com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.webdav.DAVTransaction, java.lang.Throwable)
	 */
	public void resourceDelete(VFSResource resource, DAVTransaction transaction, Throwable exception) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.VFSMount#resourceUpload(com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.webdav.DAVTransaction, java.lang.Throwable)
	 */
	public void resourceUpload(VFSResource resource, DAVTransaction transaction, Throwable exception) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.VFSMount#resourceAccessList(com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.webdav.DAVTransaction, java.lang.Throwable)
	 */
	public void resourceAccessList(VFSResource resource, DAVTransaction transaction, Throwable exception) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.VFSMount#resourceCollectionCreated(com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.webdav.DAVTransaction, java.lang.Throwable)
	 */
	public void resourceCollectionCreated(VFSResource resource, DAVTransaction transaction, Throwable exception) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.VFSMount#resourceMoved(com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.webdav.DAVTransaction, java.lang.Throwable)
	 */
	public void resourceMoved(VFSResource resource, VFSResource destination, DAVTransaction transaction, Throwable exception) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.VFSMount#resourceAccessDownloading(com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.webdav.DAVTransaction)
	 */
	public void resourceAccessDownloading(VFSResource resource, DAVTransaction transaction) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.VFSMount#resourceAccessDownloadComplete(com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.webdav.DAVTransaction, java.lang.Throwable)
	 */
	public void resourceAccessDownloadComplete(VFSResource resource, DAVTransaction transaction, Throwable exception) {
	}

	/**
	 * Get the root VFS URI for the current network place. By default this
	 * assumes the path contains a full URI. It is up to the individual mount
	 * implementations to overide this method and return a correct URI if they
	 * support paths other than URIs (<i>file</i> for example supports local
	 * file paths)
	 * 
	 * @param charset character set
	 * @return uri
	 * @throws MalformedURIException
	 */
	public abstract URI getRootVFSURI(String charset) throws MalformedURIException;

	/**
	 * Get the root VFS URI encoded as UTF-8
	 * 
	 * @return root VFS URI as UTF-8
	 * @throws MalformedURIException
	 */
	public URI getRootVFSURI() throws MalformedURIException {
		return getRootVFSURI("UTF-8");
	}

    /* (non-Javadoc)
     * @see com.ovpnals.vfs.VFSMount#isrequiresOvpnAlsAuthentication()
     */
    public boolean isRequiresOvpnAlsAuthentication() {
        return requiresOvpnAlsAuthentication;
    }

    /**
     * Set whether this mount requires OpenVPN-ALS authentication.
     * 
     * @param requiresOvpnAlsAuthentication requires OpenVPN-ALS authentication
     */
    public void setRequiresOvpnAlsAuthentication(boolean requiresOvpnAlsAuthentication) {
        this.requiresOvpnAlsAuthentication = requiresOvpnAlsAuthentication;
    }

	/**
	 * Create a file object. The actual creation is delegated to
	 * {@link #createVFSFileObject}, this methos keeps try that method is an
	 * authenticated object is returned (i.e.
	 * {@link DAVAuthenticationRequiredException} stops getting thrown.
	 * 
	 * @param path path
	 * @param requestCredentials credentials
	 * @return file object
	 * @throws IOException on any error
	 * @throws DAVAuthenticationRequiredException
	 */
	public FileObject createAuthenticatedVFSFileObject(String path, PasswordCredentials requestCredentials) throws IOException, DAVAuthenticationRequiredException {
		// 0 = Current
		// 1 = URI
		// 2 = HTTP authentication response
		// 3 = Current users credentials
		// 4 = Guest
		// 5 = Prompt
		int type = 0;
		DAVAuthenticationRequiredException dave = null;
		PasswordCredentials credentials = null;
		boolean hasCachedCredentials = false;

		if (log.isDebugEnabled())
			log.debug("Trying all available credentials for " + getMountString() + path);

		while (true) {

			// If no credentials are currently set, try those in the cache
			// first

			if (type == 0) {
				credentials = getStore().getRepository().getCredentialsCache().getDAVCredentials(getStore().getName(),
					getMountString());
				if (credentials == null) {
					type++;
				} else {
					if (log.isDebugEnabled())
						log.debug("Trying cached credentials for " + getMountString() + path);

					hasCachedCredentials = true;
				}
			}

			// User info from URI
			if (type == 1) {
				URI uri = getRootVFSURI(store.getEncoding());
				String userInfo = uri.getUserinfo();
				if (userInfo == null || userInfo.equals("")) {
					type++;
				} else {
					String username = null;
					char[] pw = null;
					userInfo = Util.urlDecode(userInfo);
					int idx = userInfo.indexOf(":");
					username = userInfo;
					if (idx != -1) {
						username = userInfo.substring(0, idx);
						pw = userInfo.substring(idx + 1).toCharArray();
					}
					credentials = new PasswordCredentials(username, pw);

					if (log.isDebugEnabled()) {
						log.debug("Trying URI credentials for " + getMountString() + path);
					}
				}
			}

			// HTTP authentication response

			if (type == 2) {
				credentials = requestCredentials;
				if (credentials == null) {
					type++;
				} else if (log.isDebugEnabled()) {
					log.debug("Trying Request credentials for " + getMountString() + path);
				}
			}

			// Throw exception. Servlet will then request HTTP
			// authentication
			if (type > 2 && dave != null) {
				throw dave;
			}

			try {
				FileObject file = createVFSFileObject(path, credentials);

				if (file == null) {
					throw new IOException("Could not create file object.");
				}

				// Cache authentication
				if (credentials != null) {

					if (!hasCachedCredentials) {
						if (log.isDebugEnabled()) {
							log.debug("Caching credentials for " + getMountString());
						}
						getStore().getRepository().getCredentialsCache().addCredentials(getStore().getName(),
							getMountString(),
							credentials);
					}
				}

				return file;
			} catch (DAVAuthenticationRequiredException dare) {
				dave = dare;
				type++;
			}
		}
	}

	String getResourceURI(VFSResource resource) {
		String uri = "Could not retrieve path";
		try {
			uri = resource.getFile().getName().getURI();
		} catch (Exception e) {
		}
		return uri;
	}
}