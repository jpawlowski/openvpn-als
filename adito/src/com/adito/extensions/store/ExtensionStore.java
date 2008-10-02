
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
			
package com.adito.extensions.store;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;
import org.jdom.JDOMException;

import com.adito.boot.Context;
import com.adito.boot.ContextHolder;
import com.adito.boot.PropertyList;
import com.adito.boot.RepositoryFactory;
import com.adito.boot.RepositoryStore;
import com.adito.boot.SystemProperties;
import com.adito.boot.Util;
import com.adito.boot.VersionInfo;
import com.adito.core.BundleActionMessage;
import com.adito.core.CoreAttributeConstants;
import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.core.CoreMessageResources;
import com.adito.core.CoreServlet;
import com.adito.core.CoreUtil;
import com.adito.core.GlobalWarning;
import com.adito.core.GlobalWarningManager;
import com.adito.core.LicenseAgreement;
import com.adito.core.GlobalWarning.DismissType;
import com.adito.extensions.ExtensionBundle;
import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.ExtensionException;
import com.adito.extensions.ExtensionInstaller;
import com.adito.extensions.ExtensionType;
import com.adito.extensions.ExtensionBundle.ExtensionBundleStatus;
import com.adito.setup.LicenseAgreementCallback;
import com.adito.tasks.Task;
import com.adito.tasks.TaskHttpServletRequest;
import com.adito.tasks.TaskInputStream;
import com.adito.tasks.TaskProgressBar;
import com.adito.util.ZipExtract;

/**
 * Manages all aspects of <i>Extensions</i> including loading, starting,
 * activating, disabling, installing, remove and updating.
 * <p>
 * This class also manages the interaction with the <i>Adito Extension Store</i>,
 * including update checking and installing from the store.
 */
public class ExtensionStore {

    public static final String HTTP_3SP_COM_APPSTORE = "http://download.localhost/appstore/";
    private static final Log log = LogFactory.getLog(ExtensionStore.class);
	private static final String DIRS_TO_REMOVE = "dirsToRemove";
	private static String currentEdition = "Community Edition";

    public static final String UPDATEABLE = "Updateable";

	/**
	 * Name of store in repository for extension bundle archives
	 */
	public static final String ARCHIVE_STORE = "archives";

	/**
	 * 'Installed' category in extension manager
	 */
	public static final String INSTALLED_CATEGORY = "Installed";

	/**
	 * The extension bundle id for the Agent.
	 */
	public static final String AGENT_EXTENSION_BUNDLE_ID = "adito-agent";

	/**
	 * Preferences node for storing current extension version and other
	 * extension related details
	 */
	public static final Preferences PREFS = ContextHolder.getContext().getPreferences().node("extensions");

	/**
	 * Preferences node with the extensions node for storing extension store
	 * related details.
	 */
	public static final Preferences STORE_PREF = PREFS.node("store");

	/**
	 * Preferences node with the extensions node for storing extension version
	 * details.
	 */
	public static final Preferences VERSION_PREFS = PREFS.node("versions");

	/**
	 * Extension store connect timeout
	 */
	public static final int CONNECT_TIMEOUT = 30000;

	/**
	 * Extension store read timeout
	 */
	public static final int READ_TIMEOUT = 30000;

	// Private instance variables

	private File basedir;
	private Map<String, ExtensionBundle> extensionBundles;
	private List<ExtensionBundle> extensionBundlesList;
	private List<ExtensionInstaller> extensionBundleInstallList;
	private ExtensionStoreDescriptor downloadableExtensions;
	private Calendar downloadableExtensionsLastUpdated;
	private boolean repositoryBacked;
	private static ExtensionStore instance;
    //private UpdateChecker updateChecker;

	/**
	 * Get an instance of the extension store.
	 * 
	 * @return instance
	 */
	public static ExtensionStore getInstance() {
		if (instance == null) {
			instance = new ExtensionStore();
		}
		return instance;
	}

	/**
	 * Get the update checker that checks for update to the core, extensions and
	 * loads the RSS feeds
	 * 
	 * @return update checker
	 */
    //public UpdateChecker getUpdateChecker() {
    //   return updateChecker;
    //}

	/**
	 * Get the directory where expanded extensions are stored.
	 * 
	 * @return the extension store directory
	 */
	public File getExtensionStoreDirectory() {
		return basedir;
	}

	/**
	 * Get if the extension store is 'Repository Backed'. See class description
	 * for details.
	 * 
	 * @return true if it is repository backed
	 */
	public boolean isRepositoryBacked() {
		return repositoryBacked;
	}

	/**
	 * Initialise the extension store.
	 * 
	 * @param basedir
	 * @throws IOException
	 */
	public void init(File basedir) throws IOException {

        //updateChecker = new UpdateChecker();

		// Get if the application store comes from the repository
		repositoryBacked = "true".equals(SystemProperties.get("adito.extensions.repositoryBacked", "true"));

		this.basedir = basedir;

		extensionBundles = new HashMap<String, ExtensionBundle>();
		extensionBundlesList = new ArrayList<ExtensionBundle>();
		extensionBundleInstallList = new ArrayList<ExtensionInstaller>();

		if (isRepositoryBacked()) {
			initialiseRepository();
		}

		try {
			loadAll();
			// TODO display errors to use somehow
		} catch (Exception e) {
			log.error("Failed extract extension bundles from repository.", e);
		}

		/*
		 * A lot of plugins were made incompatible at 0.2.10. Here we make sure
		 * any incompatible extensions are disabled
		 */
		VersionInfo.Version sslxVersion = ContextHolder.getContext().getVersion();
		if (sslxVersion.getMajor() == 0 && sslxVersion.getMinor() == 2 && sslxVersion.getBuild() == 10) {
			StringBuffer buf = new StringBuffer();
			for (ExtensionBundle bundle : extensionBundlesList) {
				if (bundle.getRequiredHostVersion() == null || bundle.getRequiredHostVersion().compareTo(sslxVersion) < 0) {
					log.warn("Extension " + bundle.getId()
						+ " has a required host version of "
						+ bundle.getRequiredHostVersion()
						+ " where as "
						+ "this version is "
						+ sslxVersion
						+ ". This plugin will be disabled.");
					ExtensionStoreStatusManager.systemDisableExtension(bundle.getId());
					if (buf.length() > 0) {
						buf.append(",");
					}
					buf.append(bundle.getName());
				}
			}
			if (buf.length() > 0) {
                GlobalWarningManager.getInstance().addMultipleGlobalWarning(
                    new GlobalWarning(GlobalWarning.MANAGEMENT_USERS, new BundleActionMessage("extensions",
                                    "startup.disabledExtensions", buf.toString()), DismissType.DISMISS_FOR_USER));
            }
		}

		/*
		 * First remove any plugins whose uninstallation may have been deferred
		 * until restart
		 */
		if (!isRepositoryBacked()) {

			String dirsToRemove = STORE_PREF.get(DIRS_TO_REMOVE, "");
			if (!dirsToRemove.equals("")) {
				StringTokenizer t = new StringTokenizer(dirsToRemove, ",");
				while (t.hasMoreTokens()) {
					File dir = new File(t.nextToken());
					if (dir.exists()) {
						if (log.isInfoEnabled())
							log.info("Removing extension " + dir.getAbsolutePath());
						Util.delTree(dir);
					}
				}
				STORE_PREF.remove(DIRS_TO_REMOVE);
			}

			/*
			 * Check for extension updates
			 */
			File updatedExtensionsDir = getUpdatedExtensionsDirectory();
			File[] extensions = updatedExtensionsDir.listFiles();
			if (extensions != null) {
				for (int i = 0; i < extensions.length; i++) {
					File destDir = new File(ContextHolder.getContext().getApplicationDirectory(), extensions[i].getName());
					if (destDir.exists()) {
						if (log.isInfoEnabled())
							log.info("Removing extension " + destDir.getAbsolutePath());
						if (!Util.delTree(destDir)) {
							throw new IOException("Failed to remove old extension " + destDir.getAbsolutePath());
						}
					}
					if (log.isInfoEnabled())
						log.info("Moving " + extensions[i].getAbsolutePath() + " to " + destDir.getAbsolutePath());
					if (!extensions[i].renameTo(destDir)) {
						throw new IOException("Failed to rename extension " + extensions[i].getAbsolutePath()
							+ " to "
							+ destDir.getAbsolutePath());
					}
				}
			}
		}

		// Add any additional class path elements
		addAdditionalClasspath();
		addAdditionalWebResource();

		// Check for any mandatory updates
        //try {
        //    updateChecker.initialise();
        //} catch (Exception e) {
			/*
			 * There is no need to prevent start up if we fail to get the
			 * available versions.
			 */
        //    log.error("Failed to check for any extension updates.", e);
        //}
	}

	/**
	 * @return the available extension bundles
	 */
	@SuppressWarnings("unchecked")
	public List<ExtensionBundle> getAllAvailableExtensionBundles() {
		List<ExtensionBundle> all = new ArrayList<ExtensionBundle>(extensionBundlesList);
		try {
			ExtensionStoreDescriptor descriptor = getDownloadableExtensionStoreDescriptor(downloadableExtensions != null, getWorkingVersion());
			if (descriptor != null && descriptor.getExtensionBundles() != null) {
				for (Iterator itr = descriptor.getExtensionBundles().iterator(); itr.hasNext();) {
					ExtensionBundle bundle = (ExtensionBundle) itr.next();
					// If the app is already installed, remove dont include it
					// in the list
					if (!extensionBundles.containsKey(bundle.getId())) {
						all.add(bundle);
					}
				}
			}
		} catch (Exception e) {
			log.error("Failed to get downloadable extensions.", e);
		}
		Collections.sort(all);
		return all;
	}

	/**
     * Get all the available extension bundles fro a given category.
     * 
	 * @param category 
	 * @return the available extension bundles
	 */
	@SuppressWarnings("unchecked")
	public List<ExtensionBundle> getAllAvailableExtensionBundles(String category) {
        // just get the installed ones.
        if (category.equals(INSTALLED_CATEGORY)){
            return extensionBundlesList;
        }
        
        List<ExtensionBundle> all = new ArrayList<ExtensionBundle>();
        if (category.equals(UPDATEABLE)){
            for (ExtensionBundle bundle : extensionBundles.values()) {
                // add all the updateable extensions
                if (bundle.isUpdateable()){
                    all.add(bundle);
                }
            }
        }
        
	    try {
	        ExtensionStoreDescriptor descriptor = getDownloadableExtensionStoreDescriptor(downloadableExtensions != null, getWorkingVersion());
	        if (descriptor != null && descriptor.getExtensionBundles() != null) {
	            for (Iterator itr = descriptor.getExtensionBundles().iterator(); itr.hasNext();) {
	                ExtensionBundle bundle = (ExtensionBundle) itr.next();
	                // If the app is already installed, remove dont include it
	                // in the list
	                if (!extensionBundles.containsKey(bundle.getId()) && bundle.getCategory().equals(category)) {
	                    all.add(bundle);
	                }
	            }
	        }
	    } catch (Exception e) {
	        log.error("Failed to get downloadable extensions.", e);
	    }
	    Collections.sort(all);
	    return all;
	}
	
	/**
	 * @param id
	 * @param version 
	 * @return URLConnection
	 * @throws IOException
	 */
	public URLConnection downloadExtension(String id, String version) throws IOException {
		URL downloadURL = getDownloadURL(id, version);
		if (downloadURL != null) {
			if (log.isInfoEnabled())
				log.info("Downloading extension from " + downloadURL.toExternalForm());
			URLConnection con = downloadURL.openConnection();
			con.setConnectTimeout(CONNECT_TIMEOUT);
			con.setReadTimeout(READ_TIMEOUT);
			con.connect();
			return con;
		} else {
			throw new IOException("No valid download location for " + id);
		}
	}

	/**
	 * Start all extensions bundles
	 * 
	 * @throws ExtensionException any error starting a bundle. If multiple
	 *         extensions are started then only the first exception thrown by
	 *         the bundle will be thrown from this method, an attempt will be
	 *         made to start all other extensions bundles.
	 */
	public void start() throws ExtensionException {
		if (log.isInfoEnabled())
			log.info("Starting extension store. Extensions will start in the following order .. ");
		Collections.sort(extensionBundlesList, new BundleComparator());
		for (ExtensionBundle bundle : extensionBundlesList) {
			log.info("    " + bundle.getId() + " (" + bundle.getOrder() + ")");
		}

		for (ExtensionBundle bundle : extensionBundlesList) {
		    boolean setupMode = ContextHolder.getContext().isSetupMode();
            if (!setupMode || (setupMode && bundle.isStartOnSetupMode())) {
    			ContextHolder.getContext().getBootProgressMonitor().updateMessage("Starting " + bundle.getName());
    			ContextHolder.getContext()
    							.getBootProgressMonitor()
    							.updateProgress((int) (30 + (10 * ((float) extensionBundlesList.indexOf(bundle) / extensionBundlesList.size()))));
    
    			// Start the bundle
    			try {
    			    if (bundle.getStatus() == ExtensionBundleStatus.ENABLED) {
    			        bundle.start();
    			    }
    			} catch (Throwable t) {
    				/*
    				 * Catch throwable to prevent bad extensions from interferring
    				 * with the core (e.g. NoClassDefFoundError,
    				 * ClassNotFoundException)
    				 */
    				log.error("Failed to start extension bundle.", t);
    			}
		    }
		}

		/*
		 * First check which extensions should have their installers run
		 */
		checkExtensionsForInstallation();

		/*
		 * Now run the installer for the start phase
		 */
		performInstalls(ExtensionInstaller.ON_START);

	}

	/**
	 * Stop all extensions bundles
	 * 
	 * @throws ExtensionException any error stopping a bundle. If multiple
	 *         extensions are started then only the first exception thrown by
	 *         the bundle will be thrown from this method, an attempt will be
	 *         made to start all other extensions bundles.
	 */
	public void stop() throws ExtensionException {
		if (log.isInfoEnabled())
			log.info("Stopping extensions");

		// ensure all threads are finished.
        //updateChecker.end();

		// Stop extensions in the reverse order they were started
		if(extensionBundlesList != null) {
    		Collections.reverse(extensionBundlesList);
    
    		ExtensionException ee = null;
    		for (ExtensionBundle bundle : extensionBundlesList) {
    			try {
    				bundle.stop();
    			} catch (ExtensionException e) {
    				if (ee == null) {
    					ee = e;
    				}
    				log.error("Failed to stop extension bundle.", ee);
    			}
    		}
    		if (ee != null) {
    			throw ee;
    		}
		}
	}

	/**
	 * Activate all extensions bundles
	 * 
	 * @throws ExtensionException any error activating a bundle. If multiple
	 *         extensions are started then only the first exception thrown by
	 *         the bundle will be thrown from this method, an attempt will be
	 *         made to start all other extensions bundles.
	 */
	public void activate() throws ExtensionException {
		if (log.isInfoEnabled())
			log.info("Activating extension store.");

		StringBuffer buf = new StringBuffer();
		for (ExtensionBundle bundle : extensionBundlesList) {
			try {
				if (bundle.getStatus() == ExtensionBundleStatus.STARTED) {

					ContextHolder.getContext().getBootProgressMonitor().updateMessage("Activating " + bundle.getName());
					ContextHolder.getContext()
									.getBootProgressMonitor()
									.updateProgress((int) (65 + (10 * ((float) extensionBundlesList.indexOf(bundle) / extensionBundlesList.size()))));

					bundle.activate();
					
					if(buf.length() != 0)
						buf.append(",");
					buf.append(bundle.getId());
				}
			} catch (Throwable t) {
				/*
				 * Catch throwable to prevent bad extensions from interferring
				 * with the core (e.g. NoClassDefFoundError,
				 * ClassNotFoundException)
				 */
				log.error("Failed to activate extension bundle.", t);
			}
		}

		/*
		 * Remove the versions of any extensions that no longer exist
		 */
		String[] k;
		try {
			k = VERSION_PREFS.keys();
			for (int i = 0; i < k.length; i++) {
				if (!isExtensionBundleLoaded(k[i])) {
					VERSION_PREFS.remove(k[i]);
				}
			}
		} catch (BackingStoreException e) {
			log.warn("Could not clean up extension versions preferences node.", e);
		}

		// Start watching for version updates and RSS updates (if enabled)
        //updateChecker.start();

		/*
		 * Now run the installer for the start phase
		 */
		performInstalls(ExtensionInstaller.ON_ACTIVATE);
		
		/**
		 * If activat plugins has changed since the last full activation
		 * then clear out the compiled JSP files
		 */
		if(!buf.toString().equals(PREFS.get("lastActivatedPlugins", ""))) {
			Util.delTree(new File(ContextHolder.getContext().getTempDirectory(), "org"));
		}
		PREFS.put("lastActivatedPlugins", buf.toString());

		/* Flush these preferences now incase the server is terminated before 
		 * it gets a chance to write the preferences. When running in a development,
		 * its possible for the server to get confused about when it should
		 * clear out the temporary temporary directory. 
		 */
		try {
			PREFS.flush();
		}
		catch(BackingStoreException bse) {			
		}
	}

	/**
	 * reset
	 */
	public void resetExtensionStoreUpdate() {
		downloadableExtensions = null;
	}

	/**
	 * @param connect
	 * @return ExtensionStoreDescriptor
	 * @throws IOException
	 * @throws JDOMException
	 */
	public ExtensionStoreDescriptor getDownloadableExtensionStoreDescriptor(boolean connect) throws IOException, JDOMException {
		return getDownloadableExtensionStoreDescriptor(connect, getWorkingVersion());
	}
	
	/**
	 * @param connect
	 * @param version
	 * @return ExtensionStoreDescriptor
	 * @throws IOException
	 * @throws JDOMException
	 */
	public ExtensionStoreDescriptor getDownloadableExtensionStoreDescriptor(boolean connect, VersionInfo.Version version) throws IOException, JDOMException {
		if (downloadableExtensions != null && downloadableExtensionsLastUpdated != null) {
			Calendar calendar = ((Calendar) downloadableExtensionsLastUpdated.clone());
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			if (new GregorianCalendar().after(calendar)) {
				if (log.isInfoEnabled())
					log.info("Downloadable extensions are out of date, will contact the update site again.");
				downloadableExtensions = null;
			}
		}

		if (downloadableExtensions == null && connect) {
			URL storeURL = getStoreDownloadURL(ExtensionStore.HTTP_3SP_COM_APPSTORE, version);
			if (storeURL != null) {
				if (log.isInfoEnabled())
					log.info("Loading extension store descriptor from " + storeURL.getHost());

				downloadableExtensions = new ExtensionStoreDescriptor(storeURL);
				downloadableExtensionsLastUpdated = new GregorianCalendar();

				for (ExtensionBundle extensionBundle : downloadableExtensions.getExtensionBundles()) {
					try {
						ExtensionBundle installedApp = getExtensionBundle(extensionBundle.getId());
						if (!installedApp.isDevExtension() && installedApp.getVersion().compareTo(extensionBundle.getVersion()) < 0) {
							if (log.isInfoEnabled())
								log.info("Update found for extenions " + extensionBundle.getId());
							installedApp.setType(ExtensionBundle.TYPE_UPDATEABLE);
							installedApp.setUpdateVersion(extensionBundle.getVersion());
							installedApp.setChanges(extensionBundle.getChanges());
						}
					} catch (Exception e) {
		                if (log.isInfoEnabled())
		                    log.info("Extension " + extensionBundle.getId() + " is not installed.");
					}
				}

				if (log.isInfoEnabled())
					log.info("Extension store descriptor loaded from " + storeURL.getHost());
			}
		}
		return downloadableExtensions;
	}

	void performInstalls(String phase) {
		for (ExtensionInstaller installer : extensionBundleInstallList) {
			try {
				if (log.isInfoEnabled()) {
					log.info("Performing installer for " + installer.getBundle().getName() + " phase " + phase);
				}
				installer.doInstall(phase);
			} catch (Exception e) {
				log.warn("Installer for " + installer.getBundle().getName() + " phase " + phase + " failed.", e);
			}
		}
	}

	void checkExtensionsForInstallation() {
		extensionBundleInstallList.clear();
		PropertyList forceInstalls = new PropertyList(SystemProperties.get("adito.forceInstallers", ""));
		for (ExtensionBundle bundle : extensionBundlesList) {
			if (bundle.getInstaller().getOpCount() > 0 && bundle.getStatus().isStartedOrActivated()) {
				String ver = VERSION_PREFS.get(bundle.getId(), "");
				boolean force = forceInstalls.contains(bundle.getId());
				if (force || ver.equals("") || !ver.equals(bundle.getVersion().toString())) {
				    if(force) {
                        log.info("Will run installer for " + bundle.getId() + " because it has been forced by the adito.foreceInstallers property.");
				    }
				    else if (ver.equals("")) {
						log.info("Will run installer for " + bundle.getId() + " because this is its first install");
					} else {
						log.info("Will run installer for " + bundle.getId()
							+ " because the last installed version "
							+ ver
							+ " has been upgraded to "
							+ bundle.getVersion().toString());
					}
					extensionBundleInstallList.add(bundle.getInstaller());
				}
			}
		}
	}

	private static URL getDownloadURL(String id, String version) {
		try {
            String location = SystemProperties.get("adito.downloadableApplicationStore.location", ExtensionStore.HTTP_3SP_COM_APPSTORE);
            location += Util.urlEncode(id) + "/" + Util.urlEncode(version) + "/" + Util.urlEncode(id) + ".zip";
			return new URL(location);
		} catch (MalformedURLException murle) {
			try {
				String path = SystemProperties.get("adito.downloadableApplications.location");
				path = path.replaceAll("\\$\\{id\\}", id);
                path = path.replaceAll("\\$\\{version\\}", version);
				return new File(path).toURL();
			} catch (MalformedURLException e) {
				log.error("Invalid downloadable extension location specified in system property adito.downloadableApplicationStore.location, '" + SystemProperties.get("adito.downloadableApplicationStore.location")
					+ "'. Must be either a URL or the file path of the store descriptor file.");
			}
		}
		return null;
	}

	public static URL getStoreDownloadURL(String appStoreLocation, VersionInfo.Version version) {
		try {
			String location = SystemProperties.get("adito.downloadableApplicationStore.location", appStoreLocation);
            location += "core/" + Util.urlEncode(version.toString()) + "/store.xml";
			return new URL(location);
		} catch (MalformedURLException murle) {
			try {
				return new File(SystemProperties.get("adito.downloadableApplicationStore.location")).toURL();
			} catch (MalformedURLException e) {
				log.error("Invalid downloadable extension store location specified in system property adito.downloadableApplicationStore.location, '" + SystemProperties.get("adito.downloadableApplicationStore.location")
					+ "'. Must be either a URL or the file path of the store descriptor file.");
			}
		}
		return null;
	}
	
	/**
	 * 
	 */
	public void deregisterApplicationPermissions() {
		Util.toDo("Deregister application permissions");
	}

	private synchronized void reloadAll() throws Exception {
		CoreMessageResources resources = CoreServlet.getServlet().getExtensionStoreResources();
		for (ExtensionBundle extensionBundle : extensionBundles.values()) {
			for (ExtensionDescriptor descriptor : extensionBundle) {
				Collection<String> toRemove = new ArrayList<String>();

				for (Iterator itr = resources.keys(); itr.hasNext();) {
					String key = (String) itr.next();
					if (key.startsWith("application." + descriptor.getId() + ".")) {
						toRemove.add(key);
					}
				}

				for (Iterator itr = toRemove.iterator(); itr.hasNext();) {
					resources.removeKey((String) itr.next());
				}
			}
		}

		extensionBundles.clear();
		extensionBundlesList.clear();

		loadAll();
	}

	@SuppressWarnings("unchecked")
	private void loadAll() throws Exception {

		if (log.isInfoEnabled())
			log.info("Loading applications");

		if (!basedir.exists()) {
			basedir.mkdirs();
		}

		// Load dev extensions first
		loadDevExtensions();

		File[] files = basedir.listFiles();
		for (int index = 0; index < files.length; index++) {
			try {
				loadDir(files[index]);
			} catch (Exception e) {
				log.error("Failed to load " + files[index].getName(), e);
			}
		}

		String descriptors = SystemProperties.get("adito.additionalDescriptors", "");
		// Load any additional descriptors
		for (StringTokenizer tokenizer = new StringTokenizer(descriptors, ","); tokenizer.hasMoreTokens();) {
			File file = new File(tokenizer.nextToken());
			if (file.exists()) {
				try {
					loadBundle(file, false);
				} catch (Exception e) {
					log.error("Failed to load " + file.getAbsolutePath(), e);
				}
			}
		}

		Collections.sort(extensionBundlesList);
	}

	private void loadDir(File dir) throws ExtensionException {
		if (dir.isDirectory()) {
			File[] descriptors = dir.listFiles(new FilenameFilter() {
				public boolean accept(File f, String filename) {
					return filename.equals("application.xml") || filename.equals("extension.xml");
				}
			});

			if (descriptors.length == 0) {
				log.warn("Extension folder " + dir.getName() + " found with no extension.xml (or the deprecated application.xml)");
				return;
			} else if (descriptors.length > 1) {
				// Should never happen if its case sensitive
				log.warn("Extension folder " + dir.getName()
					+ " found with too many extension.xml (or deprecated application.xml) files. Please remove one. This extensions will be ignored.");
				return;
			}
			if (log.isInfoEnabled())
				log.info("Found application bundle " + dir.getName());

			if (descriptors[0].getName().equals("application.xml")) {
				log.warn("DEPRECATED. Application descriptor file " + descriptors[0]
					+ "  is no longer used, please use extension.xml instead.");
			}
			loadBundle(descriptors[0], false);
		}
	}

	private void loadBundle(File descriptor, boolean devExtension) throws ExtensionException {
		ExtensionBundle bundle = new ExtensionBundle(descriptor, devExtension);
		loadBundle(bundle);
	}

	private void loadBundle(ExtensionBundle bundle) throws ExtensionException {

		bundle.load();
		ExtensionBundle oldBundle = (ExtensionBundle) extensionBundles.get(bundle.getId());

		if (oldBundle != null && oldBundle.isDevExtension()) {
			throw new ExtensionException(ExtensionException.CANNOT_REPLACE_DEV_EXTENSION, bundle.getId());
		}

		bundle.setCategory(ExtensionStore.INSTALLED_CATEGORY);
		try {
			ExtensionBundleStatus extensionStatus = ExtensionStoreStatusManager.getExtensionStatus(bundle.getId());
			bundle.setStatus(extensionStatus);
		} catch (IOException ioe) {
			throw new ExtensionException(ExtensionException.INTERNAL_ERROR, ioe, "Failed to add bundle.");
		}

		for (ExtensionDescriptor descriptor : bundle) {
			if (log.isInfoEnabled())
				log.info("Extension " + descriptor.getName() + " has been loaded");
		}
		extensionBundlesList.remove(oldBundle);
		extensionBundles.put(bundle.getId(), bundle);
		extensionBundlesList.add(bundle);
	}

	/**
	 * @param bundleId
	 * @throws ExtensionException
	 * @throws IOException
	 */
	public void systemDisableExtension(String bundleId) throws ExtensionException, IOException {
		ExtensionBundle extensionBundle = getExtensionBundle(bundleId);
		extensionBundle.setStatus(ExtensionBundle.ExtensionBundleStatus.SYSTEM_DISABLED);
		ExtensionStoreStatusManager.systemDisableExtension(bundleId);
	}

	/**
	 * @param bundleId
	 * @throws Exception
	 */
	public void disableExtension(String bundleId) throws Exception {
		ExtensionBundle extensionBundle = getExtensionBundle(bundleId);
		extensionBundle.setStatus(ExtensionBundle.ExtensionBundleStatus.DISABLED);
		ExtensionStoreStatusManager.disableExtension(bundleId);
		if(extensionBundle.isContainsPlugin()) {
			extensionBundle.setType(ExtensionBundle.TYPE_PENDING_STATE_CHANGE);
		}
	}

	/**
	 * @param bundleId
	 * @throws Exception
	 */
	public void enableExtension(String bundleId) throws Exception {
		ExtensionBundle extensionBundle = getExtensionBundle(bundleId);
		extensionBundle.setStatus(ExtensionBundle.ExtensionBundleStatus.ENABLED);
		ExtensionStoreStatusManager.enableExtension(bundleId);
		if(extensionBundle.isContainsPlugin()) {
			extensionBundle.setType(ExtensionBundle.TYPE_PENDING_STATE_CHANGE);
		}
	}

	private static void installExtension(ExtensionBundle extensionBundle) throws IOException {
		if (ExtensionBundle.ExtensionBundleStatus.SYSTEM_DISABLED.equals(extensionBundle.getStatus())) {
			ExtensionStoreStatusManager.installExtension(extensionBundle.getId());
			extensionBundle.setStatus(ExtensionBundle.ExtensionBundleStatus.ENABLED);
		}
	}

	/**
	 * @return List
	 */
	public List<ExtensionBundle> getExtensionBundles() {
		return extensionBundlesList;
	}

	/**
	 * @param name
	 * @return true if the extension bundle has been loaded
	 */
	public boolean isExtensionBundleLoaded(String name) {
		return extensionBundles.containsKey(name);
	}

	/**
	 * @return ExtensionDescriptor
	 */
	public ExtensionDescriptor getAgentApplication() {
		try {
			ExtensionBundle bundle = getExtensionBundle(AGENT_EXTENSION_BUNDLE_ID);
			return bundle != null ? (ExtensionDescriptor) bundle.getApplicationDescriptor(AGENT_EXTENSION_BUNDLE_ID) : null;
		} catch (Exception e) {
			log.error("Failed to get agent descriptor. Loaded?", e);
			return null;
		}
	}

	/**
	 * @param id
	 * @return ExtensionDescriptor
	 */
	public ExtensionDescriptor getExtensionDescriptor(String id) {
		for (ExtensionBundle bundle : extensionBundlesList) {
			ExtensionDescriptor descriptor = bundle.getApplicationDescriptor(id);
			if (descriptor != null && descriptor instanceof ExtensionDescriptor) {
				return (ExtensionDescriptor) descriptor;
			}
		}
		return null;
	}

	/**
	 * Get an extension bundle given its ID.
	 * 
	 * @param id extension bundle id
	 * @return extension bundle
	 * @throws ExtensionException if bundle could not be located ({@link ExtensionException#INVALID_EXTENSION}).
	 */
	public ExtensionBundle getExtensionBundle(String id) throws ExtensionException {
		if (!extensionBundles.containsKey(id)) {
			throw new ExtensionException(ExtensionException.INVALID_EXTENSION, id);
		}
		return (ExtensionBundle) extensionBundles.get(id);
	}

	/**
	 * Reload all
	 * 
	 * @throws Exception
	 */
	public void reload() throws Exception {
		if (log.isInfoEnabled())
			log.info("Reloading all application bundles");
		boolean reconnect = downloadableExtensions != null;
		downloadableExtensions = null;
		downloadableExtensionsLastUpdated = null;
		deregisterApplicationPermissions();

		reloadAll();
		if (reconnect) {
            getDownloadableExtensionStoreDescriptor(true);
		}
	}

    public static VersionInfo.Version getWorkingVersion() {
        VersionInfo.Version version = new VersionInfo.Version(SystemProperties.get("adito.forceVersion", ContextHolder.getContext().getVersion().toString()));
        return version;
    }

	/**
	 * @param id
	 * @throws ExtensionException
	 */
	@SuppressWarnings("unchecked")
	public void reload(String id) throws ExtensionException {
		if (log.isInfoEnabled())
			log.info("Reloading application bundle " + id);
		if (isExtensionLoaded(id)) {
			ExtensionBundle bundle = getExtensionBundle(id);
			try {
				bundle.load();
			} catch (ExtensionException ee) {
				log.warn("Failed to reload extension descriptor.", ee);
				extensionBundles.remove(id);
				extensionBundlesList.remove(bundle);
				throw ee;
			}
		} else {
			loadDir(new File(basedir, id));
		}
		Collections.sort(extensionBundlesList);
	}

	/**
	 * @return File
	 * @throws IOException
	 */
	public File getUpdatedExtensionsDirectory() throws IOException {
		File updatedExtensionsDir = new File(ContextHolder.getContext().getConfDirectory(), "updated-extensions");
		if (!updatedExtensionsDir.exists() && !updatedExtensionsDir.mkdirs()) {
			throw new IOException("The extension update directory " + updatedExtensionsDir.getAbsolutePath()
				+ " could not be created.");
		}
		return updatedExtensionsDir;
	}

	/**
	 * Remove an extension bundle. The bundle will be stopped if it is started
	 * and events will be fired. Global warnings will also be created if the
	 * bundle contains any plugins informing the administrator the server must
	 * be restarted.
	 * 
	 * @param bundle bundle to remove
	 * @throws Exception on any error
	 */
	@SuppressWarnings("unchecked")
	public void removeExtensionBundle(ExtensionBundle bundle) throws Exception {
		if (log.isInfoEnabled())
			log.info("Removing extension bundle " + bundle.getId());
		boolean containsPlugin = bundle.isContainsPlugin();
		try {
			CoreServlet.getServlet()
							.fireCoreEvent(new CoreEvent(this,
											CoreEventConstants.REMOVING_EXTENSION,
											bundle,
											null,
											CoreEvent.STATE_SUCCESSFUL).addAttribute(CoreAttributeConstants.EVENT_ATTR_EXTENSION_ID,
								bundle.getId())
											.addAttribute(CoreAttributeConstants.EVENT_ATTR_EXTENSION_NAME, bundle.getName()));
			bundle.removeBundle();
			VERSION_PREFS.remove(bundle.getId());
			CoreServlet.getServlet()
							.fireCoreEvent(new CoreEvent(this,
											CoreEventConstants.REMOVE_EXTENSION,
											bundle,
											null,
											CoreEvent.STATE_SUCCESSFUL).addAttribute(CoreAttributeConstants.EVENT_ATTR_EXTENSION_ID,
								bundle.getId())
											.addAttribute(CoreAttributeConstants.EVENT_ATTR_EXTENSION_NAME, bundle.getName()));
			if (containsPlugin) {
				if (!ExtensionStore.getInstance().isRepositoryBacked()) {
					if (log.isInfoEnabled())
						log.info("Extension " + bundle.getId() + " contains plugins, deferring removal until restart.");
					StringBuffer toRemove = new StringBuffer(STORE_PREF.get(DIRS_TO_REMOVE, ""));
					if (toRemove.length() > 0) {
						toRemove.append(",");
					}
					toRemove.append(bundle.getBaseDir());
					STORE_PREF.put(DIRS_TO_REMOVE, toRemove.toString());
				}
			}
			ExtensionStoreStatusManager.removeExtension(bundle.getId());
		} catch (Exception e) {
			CoreServlet.getServlet()
							.fireCoreEvent(new CoreEvent(this, CoreEventConstants.REMOVE_EXTENSION, null, null, e).addAttribute(CoreAttributeConstants.EVENT_ATTR_EXTENSION_ID,
								bundle.getId())
											.addAttribute(CoreAttributeConstants.EVENT_ATTR_EXTENSION_NAME, bundle.getName()));

			throw e;
		} finally {
			if (!containsPlugin) {
				extensionBundles.remove(bundle.getId());
				extensionBundlesList.remove(bundle);
				if (log.isInfoEnabled())
					log.info("Extension Zip file " + bundle.getId() + ".zip" + " has been deleted.");
			}
			if (ExtensionStore.getInstance().isRepositoryBacked()) {
				RepositoryFactory.getRepository().getStore(ExtensionStore.ARCHIVE_STORE).removeEntry(bundle.getId() + ".zip");
			}
			Collections.sort(extensionBundlesList);
		}
	}

	/**
	 * Determine if an extension is installed given its extension bundle id.
	 * 
	 * @param id extension bundle id
	 * @return true if the extension has been loaded
	 */
	public boolean isExtensionLoaded(String id) {
		for (Iterator i = extensionBundlesList.iterator(); i.hasNext();) {
			ExtensionBundle bundle = (ExtensionBundle) i.next();
			if (bundle.containsApplication(id)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Install an extension directly from the Adito Extension Store given its id
	 * and an input stream providing the extension bundle archive in zip format.
	 * This method will also attempt to install all of the bundles dependencies.
	 * 
	 * @param id extension bundle id
	 * @param in input stream of extension bundle archive (Zip format)
	 * @param request request object
     * @param contentLength size of extension bundle if known (-1 when unavailable - used for progress bar)
	 * @return ExtensionBundle installed extension bundle
	 * @throws ExtensionException on any error
	 */
	public ExtensionBundle installExtensionFromStore(final String id, InputStream in, HttpServletRequest request, long contentLength)
					throws ExtensionException {
		ExtensionStoreDescriptor store;
		try {
			// Get the application store descriptor
			store = getDownloadableExtensionStoreDescriptor(true);
			if (store == null) {
				throw new ExtensionException(ExtensionException.INTERNAL_ERROR, "No downloadable applications.");
			}

			ExtensionBundle bundle = store.getApplicationBundle(id);
			if (bundle == null) {
				throw new ExtensionException(ExtensionException.INVALID_EXTENSION, id);
			}

			// Check host version
			Context context = ContextHolder.getContext();
			if (bundle.getRequiredHostVersion() != null && bundle.getRequiredHostVersion().compareTo(context.getVersion()) > 0) {
				throw new ExtensionException(ExtensionException.INSUFFICIENT_ADITO_HOST_VERSION,
								bundle.getId(),
								bundle.getRequiredHostVersion().toString());
			}  

			// Install all dependencies
			if (bundle.getDependencies() != null) {
				for (String dep : bundle.getDependencies()) {
					if (isExtensionBundleLoaded(dep)) {
						ExtensionBundle current = getExtensionBundle(dep);
						ExtensionBundle available = store.getApplicationBundle(dep);
						if(available != null) {
							if (!current.isDevExtension() && isNewerVersionAvailable(available, current)) {
								if (log.isInfoEnabled())
									log.info("Found a dependency (" + dep + "), that needs upgrading. " + current.getVersion().toString() + " is the current version, " +  available.getVersion().toString() + " is available. Installing now");
								installExtensionFromStore(current.getId(), available.getVersion().toString(), request);
							}
						}
					} else {
						try {
							if (log.isInfoEnabled())
								log.info("Found a dependency (" + dep + "), that is not installed. Installing now");
							installExtensionFromStore(dep, store.getApplicationBundle(dep).getVersion().toString(), request);
						} catch (Exception e) {
							throw new ExtensionException(ExtensionException.INTERNAL_ERROR, "Failed to install dependency " + dep);
						}
					}
				}
			}

            // This action may be wrapped in a task progress monitor
            Task task = (Task)request.getAttribute(TaskHttpServletRequest.ATTR_TASK);
            if(task != null && request.getAttribute(TaskHttpServletRequest.ATTR_TASK_PROGRESS_HANDLED_EXTERNALLY) == null) {
                TaskProgressBar bar = new TaskProgressBar("installExtension", 0, (int)contentLength, 0); // TODO should accept longs
                task.clearProgressBars();
                task.addProgressBar(bar);
                in = new TaskInputStream(bar, in);
                ((TaskInputStream)in).getProgressBar().setNote(new BundleActionMessage("extensions", "taskProgress.downloadExtension.note", id));
                if(!task.isConfigured())
                    task.configured();
            }          

			return installExtension(id, in);
		} catch (IOException jde) {
			throw new ExtensionException(ExtensionException.INTERNAL_ERROR, "Failed to load descriptor.");
		} catch (JDOMException jde) {
			throw new ExtensionException(ExtensionException.FAILED_TO_PARSE_DESCRIPTOR);
		}
	}

	/**
	 * Install an extension directly from the Adito Extension Store given its id.
	 * This method will also attempt to install all of the bundles dependencies.
	 * 
	 * @param id extension bundle ID
	 * @param version 
	 * @param request request object
	 * @throws ExtensionException extension errors
	 * @throws IOException io errors
	 */
	public void installExtensionFromStore(String id, String version, HttpServletRequest request) throws IOException, ExtensionException {
		URLConnection connection = downloadExtension(id, version);
		InputStream inputStream = connection.getInputStream();
		installExtensionFromStore(id, inputStream, request, connection.getContentLength());
	}

	/**
	 * Install an extension givens its bundle and an input stream providing the
	 * extension bundle archive in zip format.
	 * 
	 * @param id extension bundle ID
	 * @param in input stream provided the extension bundle archive (in Zip
	 *        format)
	 * @return ExtensionBundle
	 * @throws IOException io errors
	 * @throws ExtensionException extension errors
	 */
	public ExtensionBundle installExtension(final String id, InputStream in) throws IOException, ExtensionException {
		streamToRepositoryStore(in, id);

		try {
			RepositoryStore repStore = RepositoryFactory.getRepository().getStore(ARCHIVE_STORE);
			ZipExtract.extractZipFile(getExtensionStoreDirectory(), repStore.getEntryInputStream(id + ".zip"));
			reload(id);
			ExtensionBundle newBundle = getExtensionBundle(id);
			installExtension(newBundle);
			fireBundleEvent(CoreEventConstants.INSTALL_EXTENSION, newBundle);
		} catch (IOException e) {
			CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this,
							CoreEventConstants.INSTALL_EXTENSION,
							null,
							null,
							CoreEvent.STATE_UNSUCCESSFUL).addAttribute(CoreAttributeConstants.EVENT_ATTR_EXTENSION_ID, id));
			throw e;
		} catch (ExtensionException e) {
			CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this,
							CoreEventConstants.INSTALL_EXTENSION,
							null,
							null,
							CoreEvent.STATE_UNSUCCESSFUL).addAttribute(CoreAttributeConstants.EVENT_ATTR_EXTENSION_ID, id));
			throw e;
		}
		return getExtensionBundle(id);
	}

	/**
	 * Check the license for a given bundle, adding a page redirect to ask the
	 * user to accept a license agreement if required.
	 * <p>
	 * This should be called just after an extenion is installed either from the
	 * Adito Application Store or manually.
	 * 
	 * @param newBundle newly installed bundle
	 * @param request request object
	 * @param installedForward the forward to redirect to after license
	 *        agreement has been show
	 * @throws Exception on any error
	 */
	public void licenseCheck(final ExtensionBundle newBundle, HttpServletRequest request, final ActionForward installedForward)
					throws Exception {

		final RepositoryStore repStore = RepositoryFactory.getRepository().getStore(ARCHIVE_STORE);
		// If installing, there may be a license agreement to handle
		File licenseFile = newBundle.getLicenseFile();
		if (licenseFile != null && licenseFile.exists()) {
			LicenseAgreement licenseAgreement = getLicenseAgreement(newBundle, repStore, licenseFile, installedForward);
			CoreUtil.requestLicenseAgreement(request.getSession(), licenseAgreement);
		}
	}

	/**
	 * Invoked after an extension has been installed, this method adds a global
	 * warning if the bundle contains any plugins indicating a restart is neeed.
	 * Also, if the bundle doesn't contain any plugins it will immediately run
	 * the 'installer'.
	 * 
	 * @param newBundle newly installed extension bundle
	 * @param request request
	 * @throws Exception on any error
	 */
	public void postInstallExtension(final ExtensionBundle newBundle, HttpServletRequest request) throws Exception {
		boolean containsPlugin = newBundle.isContainsPlugin();

		if (containsPlugin) {
		    GlobalWarningManager.getInstance().addMultipleGlobalWarning(new GlobalWarning(GlobalWarning.MANAGEMENT_USERS, new BundleActionMessage("extensions",
		        "extensionStore.message.pluginInstalledRestartRequired"), DismissType.DISMISS_FOR_USER));
			newBundle.setType(ExtensionBundle.TYPE_PENDING_INSTALLATION);
		} else {
			newBundle.start();

			// Installer must be run after start as it may have custom tasks
			if (newBundle.getInstaller() != null) {
				newBundle.getInstaller().doInstall(null);
			}

			newBundle.activate();
		}
	}

	/**
	 * Update an extension givens its bundle and an input stream providing the
	 * extension bundle archive in zip format.
	 * 
	 * @param id extension bundle id
	 * @param in input stream of extension bundle archive (Zip format)
	 * @param request reuqest object
     * @param contentLength content length
	 * @return ExtensionBundle newly loaded extension bundle
	 * @throws Exception on any error
	 */
	public ExtensionBundle updateExtension(String id, InputStream in, HttpServletRequest request, long contentLength) throws Exception {
		ExtensionStoreDescriptor store;
		// Get the application store descriptor
		/**
		 * LDP - Why does this require the extension store descriptor? Extensions should be 
		 * updatable even if the extension store is not available!!!!!!!!
		 */
//		store = getDownloadableExtensionStoreDescriptor(true);
//		if (store == null) {
//			throw new ExtensionException(ExtensionException.INTERNAL_ERROR, "No downloadable applications.");
//		}


        // This action may be wrapped in a task progress monitor
        Task task = (Task)request.getAttribute(TaskHttpServletRequest.ATTR_TASK);
        if(task != null && request.getAttribute(TaskHttpServletRequest.ATTR_TASK_PROGRESS_HANDLED_EXTERNALLY) == null) {
            TaskProgressBar bar = new TaskProgressBar("updateExtension", 0, (int)contentLength, 0);
            task.addProgressBar(bar);
            in = new TaskInputStream(bar, in);
            ((TaskInputStream)in).getProgressBar().setNote(new BundleActionMessage("extensions", "taskProgress.downloadExtension.note", id));
            task.configured();
        }            

		ExtensionBundle currentBundle = getExtensionBundle(id);
		if (currentBundle == null) {
			throw new ExtensionException(ExtensionException.INVALID_EXTENSION, id);
		}

		try {
			return updateExtension(currentBundle, in, request);
		} catch (ExtensionException ee) {
			CoreServlet.getServlet()
							.fireCoreEvent(new CoreEvent(this,
											CoreEventConstants.UPDATE_EXTENSION,
											null,
											null,
											CoreEvent.STATE_SUCCESSFUL).addAttribute(CoreAttributeConstants.EVENT_ATTR_EXTENSION_ID,
                                                currentBundle.getId())
											.addAttribute(CoreAttributeConstants.EVENT_ATTR_EXTENSION_NAME, currentBundle.getName())
											.addAttribute(CoreAttributeConstants.EVENT_ATTR_EXTENSION_VERSION,
                                                currentBundle.getVersion().toString()));
			throw ee;
		}
	}

	private ExtensionBundle updateExtension(ExtensionBundle currentBundle, InputStream in,
											HttpServletRequest request) throws Exception {

		// Check host version
		Context context = ContextHolder.getContext();
		if (currentBundle.getRequiredHostVersion() != null && currentBundle.getRequiredHostVersion().compareTo(context.getVersion()) > 0) {
			throw new ExtensionException(ExtensionException.INSUFFICIENT_ADITO_HOST_VERSION,
                currentBundle.getId(),
                currentBundle.getRequiredHostVersion().toString());
		}

		boolean containsPlugin = currentBundle.isContainsPlugin();

		// Remove Extension Bundle;
		try {
			currentBundle.removeBundle();
			VERSION_PREFS.remove(currentBundle.getId());
		} catch (Exception e) {
			throw e;
		} finally {
			if (!containsPlugin) {
				extensionBundles.remove(currentBundle.getId());
				extensionBundlesList.remove(currentBundle);
				if (log.isInfoEnabled())
					log.info("Extension Zip file " + currentBundle.getId() + ".zip" + " has been deleted.");
			}
		}

		// Install Extension;
		streamToRepositoryStore(in, currentBundle.getId());
		RepositoryStore repStore = RepositoryFactory.getRepository().getStore(ARCHIVE_STORE);
		ZipExtract.extractZipFile(getExtensionStoreDirectory(), repStore.getEntryInputStream(currentBundle.getId() + ".zip"));

		if (containsPlugin) {
			currentBundle.setType(ExtensionBundle.TYPE_PENDING_UPDATE);
			fireBundleEvent(CoreEventConstants.UPDATE_EXTENSION, currentBundle);
			return currentBundle;
		} else {
			reload(currentBundle.getId());
			ExtensionBundle newBundle = getExtensionBundle(currentBundle.getId());
			installExtension(newBundle);
			postInstallExtension(newBundle, request);
			if (newBundle.getStatus().isStartedOrActivated()) {
				newBundle.stop();
				newBundle.start();
				newBundle.activate();
			}
			fireBundleEvent(CoreEventConstants.UPDATE_EXTENSION, newBundle);
			return newBundle;
		}
	}

	/**
	 * Set the <i>Edition</i>, i.e. GPL, Community or Enterprise.
	 * 
	 * @param currentEdition edition
	 */
	public static void setCurrentEdition(String currentEdition) {
		ExtensionStore.currentEdition = currentEdition;
	}

	private LicenseAgreement getLicenseAgreement(final ExtensionBundle newBundle, final RepositoryStore repStore,
													final File licenseFile, final ActionForward installedForward) {
		return new LicenseAgreement(newBundle.getName(), licenseFile, new LicenseAgreementCallback() {
			public void licenseAccepted(HttpServletRequest request) {
				// Dont care
			}

			public void licenseRejected(HttpServletRequest request) {
				// Remove the repository entry if it is in
				// use
				if (isRepositoryBacked()) {
					try {
						repStore.removeEntry(newBundle.getId() + ".zip");
					} catch (IOException ex) {
					}
				}

				// Remove the expanded bundle
				if (newBundle.getBaseDir().exists()) {
					Util.delTree(newBundle.getBaseDir());
				}

				// Reload the extension store
				try {
					reload(newBundle.getId());
				} catch (Exception e) {
					log.error("Failed to reload extension store.");
				}
			}
		}, installedForward);
	}

	private static boolean isNewerVersionAvailable(ExtensionBundle available, ExtensionBundle current) {
		VersionInfo.Version v1 = new VersionInfo.Version(available.getVersion().toString());
		VersionInfo.Version v2 = new VersionInfo.Version(current.getVersion().toString());
		return v1.compareTo(v2) > 0;
	}

	private static void streamToRepositoryStore(InputStream in, String bundleId) throws IOException {
		RepositoryStore repStore = RepositoryFactory.getRepository().getStore(ARCHIVE_STORE);
		OutputStream out = null;
		try {
			out = repStore.getEntryOutputStream(bundleId + ".zip");
			Util.copy(in, out);
		} finally {
			Util.closeStream(in);
			Util.closeStream(out);
		}
	}

	private void fireBundleEvent(int eventType, ExtensionBundle bundle) {
		String extensionType = getExtensionType(bundle);
		CoreServlet.getServlet()
						.fireCoreEvent(new CoreEvent(this, eventType, null, null, CoreEvent.STATE_SUCCESSFUL).addAttribute(CoreAttributeConstants.EVENT_ATTR_EXTENSION_ID,
							bundle.getId())
										.addAttribute(CoreAttributeConstants.EVENT_ATTR_EXTENSION_NAME, bundle.getName())
										.addAttribute(CoreAttributeConstants.EVENT_ATTR_EXTENSION_VERSION,
											bundle.getVersion().toString())
										.addAttribute(CoreAttributeConstants.EVENT_ATTR_EXTENSION_TYPE, extensionType));
	}

	private static String getExtensionType(ExtensionBundle bundle) {
		for (Iterator itr = bundle.iterator(); itr.hasNext();) {
			ExtensionDescriptor descriptor = (ExtensionDescriptor) itr.next();
			if (descriptor.getExtensionType() instanceof ExtensionType) {
				return descriptor.getExtensionType().getType();
			}
		}
		return null;
	}

	private void addAdditionalWebResource() {
		//
		String additionalWebResources = SystemProperties.get("adito.additionalWebResourceDirectories", "");
		if (additionalWebResources != null) {
			StringTokenizer t = new StringTokenizer(additionalWebResources, ",");
			while (t.hasMoreTokens()) {
				try {
					URL u = null;
					String dir = t.nextToken();
					if (dir.endsWith("]")) {
						int idx = dir.indexOf('[');
						if (idx != -1) {
							dir = dir.substring(0, idx);
							u = new File(dir).getCanonicalFile().toURL();
							log.warn("Associating additional web resource directories with plugins is no longer supported.");
						}
					}
					if (u == null) {
						u = new File(dir).getCanonicalFile().toURL();
					}
					ContextHolder.getContext().addResourceBase(u);
				} catch (Exception e) {
					log.error("Failed to add additional web resources directory.", e);
				}
			}
		}
	}

	private void addAdditionalClasspath() {

		// Add any additional class path elements
		StringTokenizer t = new StringTokenizer(SystemProperties.get("adito.additionalClasspath", ""), ",");
		while (t.hasMoreTokens()) {
			try {
				String sf = t.nextToken();
				File[] f = null;
				if (sf.endsWith("/*.jar")) {
					f = new File(sf.substring(0, sf.length() - 6)).listFiles(new FileFilter() {
						public boolean accept(File pathname) {
							return pathname.getName().toLowerCase().endsWith(".jar");
						}
					});
				} else {
					f = new File[1];
					f[0] = new File(sf);
				}
				for (int j = 0; f != null && j < f.length; j++) {
					if (f[j].exists() && (f[j].isDirectory() || f[j].getName().toLowerCase().endsWith(".jar"))) {
						URL u = f[j].toURL();
						ContextHolder.getContext().addContextLoaderURL(u);
					}
				}
			} catch (MalformedURLException murle) {
				log.warn("Invalid element in additional classpaths");
			}

		}
	}

	private void initialiseRepository() {
		RepositoryStore store = RepositoryFactory.getRepository().getStore("archives");
		// Remove the existing extensions
		if (basedir.exists()) {
			Util.delTree(basedir);
		}

		// Now recreate all extensions from the repository
		basedir.mkdirs();
		String[] archives = store.listEntries();
		for (int i = 0; i < archives.length; i++) {
			if (log.isInfoEnabled()) {
				log.info("Extracting archive " + archives[i]);
			}
			try {
				ZipExtract.extractZipFile(basedir, store.getEntryInputStream(archives[i]));
				if (log.isInfoEnabled()) {
					log.info("Completed archive extraction for extension " + archives[i]);
				}
			} catch (IOException ex) {
				log.error("Error extracting archive for extension " + archives[i], ex);
				Util.delTree(new File(basedir, archives[i]));
			}
		}
	}

	private void loadDevExtensions() {
		List<String> devExtensions = new ArrayList<String>();
		String extensionList = SystemProperties.get("adito.devExtensions", "");
		StringTokenizer t = new StringTokenizer(extensionList, ",");
		while (t.hasMoreTokens()) {
			String ext = t.nextToken();
			if (ext.equalsIgnoreCase("all")) {
				File f = new File(SystemProperties.get("user.dir")).getParentFile();
				File[] dirs = f.listFiles(new FileFilter() {
					public boolean accept(File pathname) {
						File f = new File(pathname, "extensions");
						return f.exists() && f.isDirectory();
					}
				});
				for (int i = 0; dirs != null && i < dirs.length; i++) {
					devExtensions.add(dirs[i].getName());
				}
			} else if (ext.equalsIgnoreCase("enterprise")) {
				File f = new File(SystemProperties.get("user.dir")).getParentFile();
				File[] dirs = f.listFiles(new FileFilter() {
					public boolean accept(File pathname) {
						File f = new File(pathname, "extensions");
						return f.exists() && f.isDirectory() && pathname.getName().indexOf("adito-enterprise-") != -1;
					}
				});
				for (int i = 0; dirs != null && i < dirs.length; i++) {
					devExtensions.add(dirs[i].getName());
				}
			} else if (ext.equalsIgnoreCase("community")) {
				File f = new File(SystemProperties.get("user.dir")).getParentFile();
				File[] dirs = f.listFiles(new FileFilter() {
					public boolean accept(File pathname) {
						File f = new File(pathname, "extensions");
						return f.exists() && f.isDirectory() && pathname.getName().indexOf("adito-community-") != -1;
					}
				});
				for (int i = 0; dirs != null && i < dirs.length; i++) {
					devExtensions.add(dirs[i].getName());
				}
			} else {
				if (ext.startsWith("!")) {
					devExtensions.remove(ext.substring(1));
				} else {
					devExtensions.add(ext);
				}
			}
		}

		for (Iterator it = devExtensions.iterator(); it.hasNext();) {
			String ext = (String) it.next();
			File d = new File(new File(SystemProperties.get("user.dir")).getParentFile(), ext);
			File extensionDir = new File(new File(d, "extensions"), d.getName());
			File extensionDescriptor = new File(extensionDir, "extension.xml");
			if (extensionDescriptor.exists()) {
				try {
					loadBundle(extensionDescriptor, true);
				} catch (Exception e) {
					log.error("Failed to load dev extension " + extensionDescriptor.getAbsolutePath(), e);
				}
			}
		}
	}

	class BundleComparator implements Comparator<ExtensionBundle> {
		public int compare(ExtensionBundle o1, ExtensionBundle o2) {
			int i = new Integer(o1.getOrder()).compareTo(new Integer(o2.getOrder()));
			return i == 0 ? o1.getId().compareTo(o2.getId()) : i;
		}
	}

}