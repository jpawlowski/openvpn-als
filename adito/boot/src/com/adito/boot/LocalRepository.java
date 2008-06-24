package com.adito.boot;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of a {@link com.adito.boot.Repository} that uses
 * local files for storage. This implementation would <b>not</b> not 
 * suitable for multiple server setups.
 * 
 */
public class LocalRepository implements Repository {

	static Log log = LogFactory.getLog(LocalRepository.class);
    
    // Private instance variables
	
	private File basedir;
    private HashMap stores = new HashMap();
	
    /**
     * Constructor
     * 
     * @throws IOException on any error
     */
	public LocalRepository() throws IOException {
		
		/**
		 * Setup the repository path
		 */
		if(SystemProperties.get("adito.localRepositoryPath")!=null) {
			basedir = new File(SystemProperties.get("adito.localRepositoryPath"));
			basedir.mkdirs();
		} 
		
		if(basedir!=null && !basedir.exists()) {
		    log.error("Local repository could not be created [" + basedir.getAbsolutePath() + "]");
			basedir = null;
		}
		
		if(basedir==null) {
			basedir = new File(ContextHolder.getContext().getConfDirectory(), "repository");
		    basedir.mkdirs();
		}

		if(!basedir.exists()) {
		    throw new IOException("Default local repository could not be created [" + basedir.getAbsolutePath() + "]");
		}		
		
		/**
		 * Load the available stores in this repository
		 */
		File[] files = basedir.listFiles(new FileFilter() {
		   public boolean accept(File file) {
			   return file.isDirectory();
		   }
		});
		
		for(int i=0;i<files.length;i++) {
			stores.put(files[i].getName(), new LocalRepositoryStore(files[i]));
		}
	}


	/* (non-Javadoc)
	 * @see com.adito.boot.Repository#getStore(java.lang.String)
	 */
	public RepositoryStore getStore(String storeName) {

		if(!stores.containsKey(storeName)) {
			File f = new File(basedir, storeName);
			f.mkdirs();
			stores.put(storeName, new LocalRepositoryStore(f));
		}
		
		return (RepositoryStore) stores.get(storeName);
	}
    
    // Supporting classes
	
	class LocalRepositoryStore implements RepositoryStore {

		File basedir;
		
		LocalRepositoryStore(File basedir) {
			this.basedir = basedir;
		}
		
		public InputStream getEntryInputStream(String name) throws IOException {
		
			File f = new File(basedir, name);
			
			if(!f.exists())
				throw new FileNotFoundException(name + " is not a valid " + basedir.getName() + " entry");
			
			return new FileInputStream(f);
		}

		public OutputStream getEntryOutputStream(String name) throws IOException {
			
			File f = new File(basedir, name);
			
			return new FileOutputStream(f);
		}

		public void removeEntry(String name) throws IOException {
			
			File f = new File(basedir, name);
			
			if(f.exists()) {
			   if(!f.delete())
				   throw new IOException(name + " could not be deleted from the " + basedir.getName() + " store");
			}
			
		}
		
		public boolean hasEntry(String name) {
			return new File(basedir, name).exists();
		}

		public String[] listEntries() {
		   return basedir.list(new FilenameFilter() {
			public boolean accept(File f, String name) {
				return !new File(f, name).isDirectory();
			}
		   });
		}
		
	}


}
