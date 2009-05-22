
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
			
package com.ovpnals.networkplaces;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;

import com.ovpnals.boot.Util;
import com.ovpnals.core.CoreEvent;
import com.ovpnals.core.CoreServlet;
import com.ovpnals.core.stringreplacement.VariableReplacement;
import com.ovpnals.policyframework.LaunchSession;
import com.ovpnals.properties.Property;
import com.ovpnals.properties.impl.systemconfig.SystemConfigKey;
import com.ovpnals.security.AuthenticationScheme;
import com.ovpnals.security.Constants;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.PasswordCredentials;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.vfs.VFSMount;
import com.ovpnals.vfs.VFSProvider;
import com.ovpnals.vfs.VFSResource;
import com.ovpnals.vfs.VFSStore;
import com.ovpnals.vfs.VfsUtils;
import com.ovpnals.vfs.utils.URI;
import com.ovpnals.vfs.utils.URI.MalformedURIException;
import com.ovpnals.vfs.webdav.DAVAuthenticationRequiredException;
import com.ovpnals.vfs.webdav.DAVTransaction;
import com.ovpnals.vfs.webdav.DAVUtilities;

/**
 * An abstract implementation of a {@link VFSMount} that is based upon a
 * configured <i>Network Place</i>.
 * <p>
 * The URI provided in the network place is used as the root for the mount.
 * <p>
 * The {@link VFSResource} instances returned by this mount use the OpenVPN-ALS
 * extensions to <i>Commons VFS</i> as the underlying file system.
 * 
 * @see NetworkPlace
 */
public abstract class AbstractNetworkPlaceMount implements VFSMount {
	final static Log log = LogFactory.getLog(AbstractNetworkPlaceMount.class);

	// Private instance variables

	private VFSStore store;
	private boolean readOnly;
	private boolean tryCurrentUser, tryGuest;
	private LaunchSession launchSession;

	/**
	 * Constructor.
	 * 
	 * @param launchSession launch session
	 * @param store store
	 */
	public AbstractNetworkPlaceMount(LaunchSession launchSession, VFSStore store) {
		this.store = store;
		this.launchSession = launchSession;
		this.readOnly = getNetworkPlace().isReadOnly();
		try {
			tryCurrentUser = Property.getPropertyBoolean(new SystemConfigKey("fileBrowsing.auth.tryCurrentUser"));
			tryGuest = Property.getPropertyBoolean(new SystemConfigKey("fileBrowsing.auth.tryGuest"));

		} catch (Exception e) {
		}
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
		return new NetworkPlaceVFSResource(getLaunchSession(), this,
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
		return this.getStore().getName() + "/" + this.getNetworkPlace().getResourceName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.VFSMount#resourceCopy(com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.webdav.DAVTransaction, java.lang.Throwable)
	 */
	public void resourceCopy(VFSResource resource, VFSResource destination, DAVTransaction transaction, Throwable exception) {
		if (getStore().getProvider().isFireEvents()) {
			CoreEvent evt = NetworkPlaceResourceType.getResourceAccessPasteEvent(this,
				launchSession,
				transaction.getRequest(),
				resource.getFullURI().getPath(),
				getResourceURI(resource),
				exception);
			if (destination != null) {
				NetworkPlaceResourceType.addFileAttribute(evt, destination.getDisplayName(), 1);
			}
			NetworkPlaceResourceType.addOperationType(evt, false);
			CoreServlet.getServlet().fireCoreEvent(evt);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.VFSMount#resourceDelete(com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.webdav.DAVTransaction, java.lang.Throwable)
	 */
	public void resourceDelete(VFSResource resource, DAVTransaction transaction, Throwable exception) {
		if (getStore().getProvider().isFireEvents()) {
			CoreServlet.getServlet().fireCoreEvent(NetworkPlaceResourceType.getResourceAccessDeleteEvent(this,
				launchSession,
				transaction.getRequest(),
				resource.getFullURI().getPath(),
				getResourceURI(resource),
				resource.getDisplayName(),
				exception));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.VFSMount#resourceUpload(com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.webdav.DAVTransaction, java.lang.Throwable)
	 */
	public void resourceUpload(VFSResource resource, DAVTransaction transaction, Throwable exception) {
		if (getStore().getProvider().isFireEvents()) {
			CoreServlet.getServlet().fireCoreEvent(NetworkPlaceResourceType.getResourceAccessUploadEvent(this,
				launchSession,
				transaction.getRequest(),
				resource.getFullPath(),
				getResourceURI(resource),
				resource.getDisplayName(),
				null));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.VFSMount#resourceAccessList(com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.webdav.DAVTransaction, java.lang.Throwable)
	 */
	public void resourceAccessList(VFSResource resource, DAVTransaction transaction, Throwable exception) {
		if (getStore().getProvider().isFireEvents()) {
			CoreServlet.getServlet().fireCoreEvent(NetworkPlaceResourceType.getResourceAccessListEvent(this,
				launchSession,
				transaction.getRequest(),
				resource.getFullURI().getPath(),
				getResourceURI(resource),
				exception));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.VFSMount#resourceCollectionCreated(com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.webdav.DAVTransaction, java.lang.Throwable)
	 */
	public void resourceCollectionCreated(VFSResource resource, DAVTransaction transaction, Throwable exception) {
		if (getStore().getProvider().isFireEvents()) {
			CoreServlet.getServlet().fireCoreEvent(NetworkPlaceResourceType.getResourceAccessMkDirEvent(this,
				launchSession,
				transaction.getRequest(),
				resource.getFullURI().getPath(),
				getResourceURI(resource),
				exception));

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.VFSMount#resourceMoved(com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.webdav.DAVTransaction, java.lang.Throwable)
	 */
	public void resourceMoved(VFSResource resource, VFSResource destination, DAVTransaction transaction, Throwable exception) {
		if (getStore().getProvider().isFireEvents()) {
			CoreEvent evt = NetworkPlaceResourceType.getResourceAccessPasteEvent(this,
				launchSession,
				transaction.getRequest(),
				resource.getFullURI().getPath(),
				getResourceURI(resource),
				exception);
			if (destination != null) {
				NetworkPlaceResourceType.addFileAttribute(evt, destination.getDisplayName(), 1);
			}
			NetworkPlaceResourceType.addOperationType(evt, true);
			CoreServlet.getServlet().fireCoreEvent(evt);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.VFSMount#resourceAccessDownloading(com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.webdav.DAVTransaction)
	 */
	public void resourceAccessDownloading(VFSResource resource, DAVTransaction transaction) {
		if (getStore().getProvider().isFireEvents()) {
			CoreServlet.getServlet().fireCoreEvent(NetworkPlaceResourceType.getResourceAccessDownloadStartedEvent(this,
				launchSession,
				transaction.getRequest(),
				resource.getRelativePath(),
				resource.getRelativeURI().toString()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.VFSMount#resourceAccessDownloadComplete(com.ovpnals.vfs.VFSResource,
	 *      com.ovpnals.vfs.webdav.DAVTransaction, java.lang.Throwable)
	 */
	public void resourceAccessDownloadComplete(VFSResource resource, DAVTransaction transaction, Throwable exception) {
		if (getStore().getProvider().isFireEvents()) {
			if (exception != null) {
				CoreServlet.getServlet().fireCoreEvent(NetworkPlaceResourceType.getResourceAccessDownloadFailedEvent(this,
					launchSession,
					transaction.getRequest(),
					resource.getRelativePath(),
					resource.getRelativeURI().toString(),
					exception));
			} else {
				CoreServlet.getServlet().fireCoreEvent(NetworkPlaceResourceType.getResourceAccessDownloadCompleteEvent(this,
					launchSession,
					transaction.getRequest(),
					resource.getRelativePath(),
					resource.getRelativeURI().toString()));
			}
		}
	}

	/**
	 * Get the network place that backs this mount.
	 * 
	 * @return network place
	 */
	public NetworkPlace getNetworkPlace() {
		return (NetworkPlace)launchSession.getResource();
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
	public URI getRootVFSURI(String charset) throws MalformedURIException {
		
		/* This is where the details from a network place get turned into
		 * a URI. 
		 * 
		 * This is the *only* place replacement and encoding of the network
		 * place URI elements should occur as it is the only time when we
		 * have all the supporting objects (Session, Policy, Resource).
		 */
		
		VariableReplacement r = new VariableReplacement();
		r.setLaunchSession(getLaunchSession());
		
		// User info. Encoded
		String userinfo = null;		
		if(!Util.isNullOrTrimmedBlank(getNetworkPlace().getUsername())) {
		    String username = r.replace(getNetworkPlace().getUsername());
		    String password = null;
		    if(!Util.isNullOrTrimmedBlank(getNetworkPlace().getPassword())) {
		        password = r.replace(getNetworkPlace().getPassword());
		    }
            userinfo = DAVUtilities.encodeURIUserInfo(username + (password == null ? "" : ":" + password)); 
		}
		
		// Host. TODO check host only contains SPACE,a-z,A-Z and - and doesn't begin with -
		String host = null;
		//if(getStore().getProvider().getHostRequirement() == VFSProvider.ELEMENT_REQUIRED) {		
		if(!Util.isNullOrTrimmedBlank(getNetworkPlace().getHost())) {		
			host = r.replace(getNetworkPlace().getHost());
		}

		// Port. Integer. A port of -1 signifies default (0 means default in network place)
		int port = -1;
		// This test seems wrong because as all provider don't have port required.
		// It means that the port saved in the database won't never be used.
		//if(getStore().getProvider().getPortRequirement()  == VFSProvider.ELEMENT_REQUIRED && getNetworkPlace().getPort() > 0) {
		// replace by this one which test if port > 0 (different to the default port).
	    if(getNetworkPlace().getPort() > 0) {		
			port = getNetworkPlace().getPort();
		}

		// Path. Always required. Replaced and encoded.
		String path = DAVUtilities.encodePath(r.replace(getNetworkPlace().getPath().replace('\\', '/')), charset);
        if(!Util.isNullOrTrimmedBlank(path) && !path.startsWith("/") && !path.startsWith("./")) {
        	path = "/" + path;
        }
        
        // Query String. TODO we need to support
        String queryString = null;
		
		// Fragment. TODO we need to support 
		String fragment = null;

		// Create the URI
		URI uri = new URI(getNetworkPlace().getScheme(), userinfo, host, port, path, queryString, fragment);
		if(log.isDebugEnabled())
			log.debug("Creating URI " + VfsUtils.maskSensitiveArguments(uri.toString()));
		return uri;
	}

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
	 * @see com.ovpnals.vfs.VFSMount#createAuthenticatedVFSFileObject(java.lang.String, com.ovpnals.security.PasswordCredentials)
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

			// Current user creds
			if (type == 3) {
				if (!tryCurrentUser) {
					type++;
				} else {

					SessionInfo inf = getStore().getRepository().getSession();

					char[] pw = LogonControllerFactory.getInstance()
									.getPasswordFromCredentials((AuthenticationScheme) inf.getHttpSession()
													.getAttribute(Constants.AUTH_SESSION));

					if (pw == null) {
						if (log.isDebugEnabled())
							log.debug("No password available from current session");
						type++;
					} else {
						credentials = new PasswordCredentials(inf.getUser().getPrincipalName(), pw);

						if (log.isDebugEnabled()) {
							log.debug("Trying current session credentials for " + "/" + getMountString() + path);
						}
					}
				}
			}

			// Guest creds

			if (type == 4) {
				if (!tryGuest) {
					type++;
				} else {
					String guestAccount = getStore().getGuestUsername();
					if (guestAccount == null) {
						type++;
					} else {
						credentials = new PasswordCredentials(guestAccount, getStore().getGuestPassword());

						if (log.isDebugEnabled()) {
							log.debug("Trying guest credentials for " + getMountString() + path);
						}
					}
				}
			}

			// Throw exception. Servlet will then request HTTP
			// authentication
			if (type > 4 && dave != null) {
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