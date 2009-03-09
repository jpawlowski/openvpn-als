package com.adito.vfs.store.downloads;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.vfs.FileObject;

import com.adito.boot.ContextHolder;
import com.adito.boot.SystemProperties;
import com.adito.core.CoreServlet;
import com.adito.core.CoreUtil;
import com.adito.policyframework.LaunchSession;
import com.adito.security.PasswordCredentials;
import com.adito.security.WebDAVAuthenticationModule;
import com.adito.vfs.AbstractStore;
import com.adito.vfs.AbstractVFSMount;
import com.adito.vfs.VFSMount;
import com.adito.vfs.utils.URI;
import com.adito.vfs.utils.URI.MalformedURIException;
import com.adito.vfs.webdav.DAVAuthenticationRequiredException;
import com.adito.vfs.webdav.DAVException;
import com.adito.vfs.webdav.DAVStatus;
import com.adito.vfs.webdav.DAVUtilities;

/**
 * {@link AbstractStore} implementation that creates a mount which acts as a
 * session store for any files downloaded in a given session, so that once the
 * download has expired the file is still downloadable.
 */
public class TempStore extends AbstractStore {

    /**
     * Constant for the mount name.
     */
    public static final String TEMP_DOWNLOAD_MOUNT_NAME = "downloads";
    
    /**
     * Constructor.
     */
    public TempStore() {
        super("temp", "UTF-8");
    }

    /* (non-Javadoc)
     * @see com.adito.vfs.VFSStore#getMountFromString(java.lang.String, com.adito.policyframework.LaunchSession)
     */
    public VFSMount getMountFromString(String mountName, LaunchSession launchSession) throws DAVException, DAVAuthenticationRequiredException {

        // Will always require Adito Authentication
        if(launchSession.getSession() == null) {
            throw new DAVAuthenticationRequiredException(WebDAVAuthenticationModule.DEFAULT_REALM);
        }
        
    	try {
	        File tempDownloadDirectory = CoreUtil.getTempDownloadDirectory(getRepository().getSession());
	        if(!mountName.equals(tempDownloadDirectory.getName())) {
	        	throw new Exception("No permission.");
	        }
	        return new DownloadsStoreMount(launchSession, tempDownloadDirectory);
    	}
    	catch(Exception e) {
        	throw new DAVException(DAVStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    	}
    }

    /* (non-Javadoc)
     * @see com.adito.vfs.VFSStore#getMountNames()
     */
    public Collection<String> getMountNames() throws Exception {
    	// PLUNDEN: Removing the context
		// File tempDownloadDirectory = new File(ContextHolder.getContext().getTempDirectory(), TempStore.TEMP_DOWNLOAD_MOUNT_NAME);
    	File tempDownloadDirectory = new File(CoreServlet.getServlet().getServletContext().getRealPath("/") + "/WEB-INF/" + SystemProperties.get("adito.directories.tmp", "tmp"), TempStore.TEMP_DOWNLOAD_MOUNT_NAME);
	    // end change
        File[] dirs = tempDownloadDirectory.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
        });
        List<String> l = new ArrayList<String>();
        if(dirs != null) {
	        for(int i = 0 ; i < dirs.length; i++) {
	            l.add(dirs[i].getName());
	        }
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

    /**
     * Class which represents the stores mount.
     */
    class DownloadsStoreMount extends AbstractVFSMount {
    	
    	private File dir;
        
        DownloadsStoreMount(LaunchSession launchSession, File dir) {
        	super(launchSession, TempStore.this, dir.getName(), true);
        	this.dir = dir;
        }

        /* (non-Javadoc)
         * @see com.adito.vfs.AbstractVFSMount#createVFSFileObject(java.lang.String, com.adito.security.PasswordCredentials)
         */
        public FileObject createVFSFileObject(String path, PasswordCredentials credentials) throws IOException {
            URI uri = new URI(getRootVFSURI());
            uri.setPath(DAVUtilities.concatenatePaths(uri.getPath(), path));
            FileObject root = getStore().getRepository().getFileSystemManager().resolveFile(uri.toString());
            return root;
        }

        /* (non-Javadoc)
         * @see com.adito.vfs.AbstractVFSMount#getRootVFSURI(java.lang.String)
         */
        public URI getRootVFSURI(String charset) throws MalformedURIException {
        	return new URI(dir.toURI().toString());
        }
    }
}
