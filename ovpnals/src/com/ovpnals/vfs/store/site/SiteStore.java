package com.ovpnals.vfs.store.site;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.vfs.FileObject;

import com.ovpnals.boot.ContextHolder;
import com.ovpnals.extensions.ExtensionBundle;
import com.ovpnals.policyframework.LaunchSession;
import com.ovpnals.security.PasswordCredentials;
import com.ovpnals.vfs.AbstractStore;
import com.ovpnals.vfs.AbstractVFSMount;
import com.ovpnals.vfs.FileObjectVFSResource;
import com.ovpnals.vfs.VFSMount;
import com.ovpnals.vfs.VFSResource;
import com.ovpnals.vfs.utils.URI;
import com.ovpnals.vfs.utils.URI.MalformedURIException;
import com.ovpnals.vfs.webdav.DAVAuthenticationRequiredException;
import com.ovpnals.vfs.webdav.DAVUtilities;

/**
 * Store for site specific resources such as custom icons.
 */
public class SiteStore extends AbstractStore {
	
	private File siteDir;

	/**
	 * Constructor.
	 */
	public SiteStore() {
		super("site", "UTF-8");
		siteDir = new File(ContextHolder.getContext().getConfDirectory(), "site");
		if(!siteDir.exists()) {
			siteDir.mkdirs();
		} 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.webdav.AbstractStore#getMountFromString(java.lang.String,
	 *      com.ovpnals.security.SessionInfo)
	 */
	public VFSMount getMountFromString(String mountName, LaunchSession launchSession) throws DAVAuthenticationRequiredException {
		if(mountName.equals("icons")) {
			return new SiteIconsMount();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.vfs.VFSStore#getMountNames()
	 */
	public Collection<String> getMountNames() throws Exception {
		return Arrays.asList(new String[] { "icons" });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ovpnals.vfs.webdav.DAVStore#validateUserEnteredPath(java.lang.String)
	 */
	public String createURIFromPath(String path) throws IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	class SiteIconsMount extends AbstractVFSMount {

		private ExtensionBundle bundle;

		SiteIconsMount()  {
			super(null, SiteStore.this, "icons", false);
		}

		public VFSResource getResource(String path, PasswordCredentials requestCredentials)
						throws IOException {
			VFSResource parent = null;
			if (path.equals("")) {
				parent = getStore().getStoreResource();
			}
			return new FileObjectVFSResource(getLaunchSession(), this, 
							parent,
							path,
							getStore().getRepository(),
							requestCredentials);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.ovpnals.vfs.AbstractVFSMount#getRootVFSURI(java.lang.String)
		 */
		public URI getRootVFSURI(String charset) throws MalformedURIException {
			File baseDir = new File(siteDir, "icons");
			if(!baseDir.exists()) {
				baseDir.mkdirs();
			}
			return new URI(baseDir.toURI().toString());
		}

		protected FileObject createVFSFileObject(String path, PasswordCredentials credentials) throws IOException {
			URI uri = getRootVFSURI();
			uri.setPath(DAVUtilities.concatenatePaths(uri.getPath(), path));
			FileObject root = getStore().getRepository().getFileSystemManager().resolveFile(uri.toString());
			return root;
		}
	}
}
