package com.adito.vfs.store.apps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;

import com.adito.boot.SystemProperties;
import com.adito.extensions.ExtensionBundle;
import com.adito.extensions.store.ExtensionStore;
import com.adito.policyframework.LaunchSession;
import com.adito.security.PasswordCredentials;
import com.adito.vfs.AbstractStore;
import com.adito.vfs.AbstractVFSMount;
import com.adito.vfs.FileObjectVFSResource;
import com.adito.vfs.VFSMount;
import com.adito.vfs.VFSResource;
import com.adito.vfs.utils.URI;
import com.adito.vfs.utils.URI.MalformedURIException;
import com.adito.vfs.webdav.DAVAuthenticationRequiredException;
import com.adito.vfs.webdav.DAVUtilities;

/**
 * {@link AbstractStore} implementation that creates mounts based on the
 * applications available in the <i>Extension Store</i>.
 * <p>
 * This is used by HTML applications and the Agent (for Java application) to
 * download files required.
 * <p>
 * It hides folders called <i>private</i> or <i>upgrade</i>.
 */
public class ApplicationStore extends AbstractStore {
    
    final static Log log = LogFactory.getLog(ApplicationStore.class);

	/**
	 * Constructor.
	 */
	public ApplicationStore() {
		super("apps", "UTF-8");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.vfs.webdav.AbstractStore#getMountFromString(java.lang.String,
	 *      com.adito.security.SessionInfo)
	 */
	public VFSMount getMountFromString(String mountName, LaunchSession launchSession) throws DAVAuthenticationRequiredException {
		try {
			if (ExtensionStore.getInstance().getExtensionBundle(mountName) != null) {
				return new ApplicationStoreMount(launchSession, ExtensionStore.getInstance().getExtensionBundle(mountName));
			} else
				return null;
		} catch (Exception e) {
            log.error("Failed to create application store mount.", e);
			return null;
		}

	}

	public Collection<String> getMountNames() throws Exception {
		Iterator itr = ExtensionStore.getInstance().getExtensionBundles().iterator();
		List<String> l = new ArrayList<String>();
		while (itr.hasNext()) {
			ExtensionBundle b = (ExtensionBundle) itr.next();
			l.add(b.getId());
		}
		return l;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.vfs.webdav.DAVStore#validateUserEnteredPath(java.lang.String)
	 */
	public String createURIFromPath(String path) throws IllegalArgumentException {
		throw new IllegalArgumentException();
	}

	class ApplicationStoreMount extends AbstractVFSMount {

		private ExtensionBundle bundle;

		ApplicationStoreMount(LaunchSession launchSession, ExtensionBundle bundle) throws MalformedURIException {
			super(launchSession, ApplicationStore.this, bundle.getId(), true);
			this.bundle = bundle;
			setRequiresAditoAuthentication(false);
		}

		public VFSResource getResource(String path, PasswordCredentials requestCredentials)
						throws IOException {
			if (path.equalsIgnoreCase("private") || path.equalsIgnoreCase("upgrade")) {
				throw new IOException("Permission denied.");
			}
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
		 * @see com.adito.vfs.AbstractVFSMount#getRootVFSURI(java.lang.String)
		 */
		public URI getRootVFSURI(String charset) throws MalformedURIException {
			File baseDir = bundle.getBaseDir();
	        if("true".equals(SystemProperties.get("adito.useDevConfig"))) {
	        	String basedir = ".." + File.separator + bundle.getId() + File.separator + "build" + File.separator + "extension";
        		File f = new File(basedir);
        		if(f.exists()) {
        			baseDir = f;
        		}
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
