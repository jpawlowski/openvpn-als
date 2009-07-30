
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
			
package com.adito.extensions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.adito.boot.Util;
import com.adito.boot.VersionInfo;
import com.adito.boot.VersionInfo.Version;
import com.adito.extensions.store.ExtensionStore;
import com.adito.extensions.types.Plugin;
import com.adito.extensions.types.PluginType;

/**
 * Extension bundles describe a collection of <i>Extension Descriptors</i>.
 * <p>
 * A bundle may come from one of two places, either the <i>Adito Application Store</i>
 * or from an installed extension bundle retrieved via the <i>Repository</i>.
 * <p>
 * If from the Adito Application Store, the descriptor will not contain all
 * details, only enough to determine the name, description, versions,
 * dependencies and a few others pieces of information. Such bundles do not have
 * a life cycle in the way local extension bundles do (i.e. be <i>Started</i>,
 * <i>Stopped</i> or <i>Activated</i>).
 * <p>
 * Local extension bundles must go through 3 or 4 phases.
 * <ul>
 * <li>Phase 1. Loading. The XML extension descriptors are loaded and parsed.
 * No change the state of Adito is made at this stage. Once this is
 * complete we know the order the extensions should be started</li>
 * <li>Phase 2. Starting. All extensions now have their
 * {@link ExtensionDescriptor#start()} method called. This in turn delegates the
 * start to the <i>Extension Type</i> implementation in use. For example, the
 * {@link PluginType} would create the plug-in instance and invoke the
 * {@link Plugin#startPlugin(com.adito.extensions.types.PluginDefinition, ExtensionDescriptor, Element)}.
 * If starting fails, an exception is thrown and the extension will be
 * <i>Stopped</i>. method on it.</li>
 * <li>Phase 3. Activation. Only the <i>Plug-ins</i> really use this as they
 * require two phases of initialisation. If activate fails, an exception is
 * thrown and the extension will be <i>Stopped</i>. </li>
 * <li>Phase 4. Stopping. May occur either after <i>Starting</i> or
 * <i>Activation</i>. During this phase the extension should clean up as much
 * as possible (deregister property definitions, user databases or any other
 * extension point).
 * </ul>
 */
public class ExtensionBundle extends ArrayList<ExtensionDescriptor> implements Comparable {

	final static Log log = LogFactory.getLog(ExtensionBundle.class);

	/**
	 * Extension 'type' code indicating a new version is available from the Adito
	 * Application Store
	 */
	public static final int TYPE_UPDATEABLE = 0;

	/**
	 * Extension 'type' code indicating the most up to date version in Adito
	 * Application Store is already correctly installed 
	 */
	public static final int TYPE_INSTALLED = 1;

	/**
	 * Extension 'type' code indicating the bundle is not currently installed
	 * but available from the Adito Application Store
	 */
	public static final int TYPE_INSTALLABLE = 2;

	/**
	 * Extension 'type' code indicating the bundle is not an installable bundle,
	 * merely a pointer to further instructions as to how to create a complete
	 * bundle (this is for bundles that we cannot legally distribute all
	 * components)
	 */
	public static final int TYPE_CONFIGUREABLE = 3;

	/**
	 * Extension 'type' code indicating the bundle has been removed by the
	 * administrator but cannot yet be deleted from the local file system as it
	 * contains plug-ins that are in use.
	 */
	public static final int TYPE_PENDING_REMOVAL = 4;

	/**
	 * Extension 'type' code indicating a new bundle has been installed but
	 * cannot be started because it contains plug-ins. The administrator should
	 * restart the entire server to complete the installation.
	 */
	public static final int TYPE_PENDING_INSTALLATION = 5;

	/**
	 * Extension 'type' code indicating a bundle has been updated but cannot be
	 * restarted because it contains plug-ins. The administrator should restart
	 * the entire server to complete the update.
	 */
	public static final int TYPE_PENDING_UPDATE = 6;

	/**
	 * Extension 'type' code indicating a bundle state has been changed 
	 * (i.e. enable or disable) but cannot be restarted because it contains 
	 * plug-ins. The administrator should restart the entire server to complete 
	 * the change.
	 */
	public static final int TYPE_PENDING_STATE_CHANGE = 7;

	/**
	 * Status of extension bundle
	 */
	public enum ExtensionBundleStatus {
		/**
		 * The extension is enabled, but stopped
		 */
		ENABLED(0, "enabled"),

		/**
		 * The extension is disabled
		 */
		DISABLED(1, "disabled"),

		/**
		 * The extension has been disabled by the system (cannot be enabled
		 */
		SYSTEM_DISABLED(2, "systemDisabled"),

		/**
		 * The extension is enabled and started
		 */
		STARTED(3, "started"),

		/**
		 * The extension is enabled and activated
		 */
		ACTIVATED(4, "activated"),

        /**
		 * The extension is errored
		 */
		ERROR(6, "error");

		private String name;

		private ExtensionBundleStatus(int state, String name) {
			this.name = name;
		}

		/**
		 * Determine if the state is {@link #STARTED} or {@link #ACTIVATED}.
		 * 
		 * @return started or activated
		 */
		public boolean isStartedOrActivated() {
			return this == ACTIVATED || this == STARTED;
		}

		/**
		 * Get the status name
		 * 
		 * @return name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return boolean
		 */
		public boolean isDisabled() {
			return this == DISABLED || this == SYSTEM_DISABLED;
		}
	}

	// Private instance variables

	private File descriptor;
	private Document doc;
	private String description;
	private String license;
	private String productURL;
	private String instructionsURL;
	private VersionInfo.Version version;
	private int type;
	private int order;
	private String id;
	private String name;
	private boolean startOnSetupMode;
	private String licenseFilePath;
	private VersionInfo.Version requiredHostVersion;
	private ExtensionInstaller installer;
	private String category;
	private boolean mandatoryUpdate;
	private ExtensionBundleStatus status = ExtensionBundleStatus.ENABLED;
	private Collection<String> dependencyNames;
	private Throwable error;
	private boolean hidden;
	private boolean devExtension;
	private Element messageElement;
	private String changes;
	private VersionInfo.Version updateVersion;
	private String platform = "";
	private String arch = "";

	/**
	 * Constructor for when creating a bundle without having a XML extension
	 * descriptor stream.
	 * 
	 * @param version
	 * @param type
	 * @param id
	 * @param name
	 * @param description
	 * @param license
	 * @param productURL
	 * @param instructionsURL
	 * @param requiredHostVersion
	 * @param dependencyNames collection of dependency names or
	 *        <code>null</code> for no dependencies
	 * @param category
	 * @param mandatoryUpdate
	 * @param order
	 * @param changes 
	 * @param password
	 * @param arch
	 */
	public ExtensionBundle(Version version, int type, String id, String name, String description, String license,
							String productURL, String instructionsURL, VersionInfo.Version requiredHostVersion,
							Collection<String> dependencyNames, String category, boolean mandatoryUpdate, int order,
							String changes, String platform, String arch) {
		this.version = version;
		this.type = type;
		this.id = id;
		this.name = name;
		this.description = description;
		this.license = license;
		this.productURL = productURL;
		this.order = order;
		this.instructionsURL = instructionsURL;
		this.requiredHostVersion = requiredHostVersion;
		this.dependencyNames = dependencyNames;
		this.category = category;
		this.mandatoryUpdate = mandatoryUpdate;
		this.changes = changes;
		this.platform = platform;
		this.arch = arch;
		
	}

	/**
	 * Constructor for creating a new bundle given a file that contains an XML
	 * extension bundle descriptor.
	 * 
	 * @param descriptor descriptor
	 * @param devExtension loaded as dev extension
	 */
	public ExtensionBundle(File descriptor, boolean devExtension) {
		this.descriptor = descriptor;
		this.devExtension = devExtension;
	}
	
	/**
	 * Get if this bundle was loaded as a <i>devExtension</i>.
	 * 
	 * @return dev extension
	 */
	public boolean isDevExtension() {
		return devExtension;
	}

	/**
	 * Get if this bundle is hidden
	 * 
	 * @return hidden
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * Get the extension bundle ID.
	 * 
	 * @return bundle ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Determines if the extension bundle is updateable.
	 * @return boolean
	 */
	public boolean isUpdateable() {
		return getType() == ExtensionBundle.TYPE_UPDATEABLE;
	}

	/**
	 * Get the order. This determines which order the bundle will get loaded
	 * started and activated in (in relative to other extensions). The lower the
	 * number the earlier the extension should be loaded
	 * 
	 * @return order
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * Get the english name of this extension bundle.
	 * 
	 * @return english name of bundle
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the extension descriptor file that represents this bundle. If the
	 * bundle is not loaded locally (i.e. it came from the Adito Application
	 * Store) then this will be <code>null</code>.
	 * 
	 * @return extension descriptor file
	 */
	public File getFile() {
		return descriptor;
	}

	/**
	 * Get the category for this bundle. This is used in the extension manager
	 * front end to group available extensions.
	 * 
	 * @return category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Get a collection of the names of the extensions bundles this bundle
	 * depends on. If any of the dependencies are not satisfied the extension
	 * may not be started. If there are no dependencies <code>null</code> will
	 * be returned.
	 * 
	 * @return extension bundle dependency names
	 */
	public Collection<String> getDependencies() {
		return dependencyNames;
	}

	/**
	 * Start all extension bundles. This method will start all extension
	 * descriptors it contains and is the second phase in an extensions life
	 * cycle (after loading).
	 * 
	 * @throws ExtensionException if any bundle could not be started
	 */
	public synchronized void start() throws ExtensionException {

		try {
			if (log.isInfoEnabled()) {
				log.info("Starting extension bundle " + getId());
			}

			// Check we are allowed to start this bundle
			if (getStatus() != ExtensionBundleStatus.ENABLED) {
				throw new ExtensionException(ExtensionException.INVALID_EXTENSION_BUNDLE_STATUS,
								getId(),
								"Bundle is not in enabled state.");
			}

			// Check this bundles dependencies are installed and started
			checkDependenciesStarted(this);

			// Start all extensions in this bundle
			ExtensionException ee = null;
			for (Iterator i = iterator(); i.hasNext();) {
				ExtensionDescriptor d = (ExtensionDescriptor) i.next();
				try {
					d.start();
					
					// Set any bundle messages
					setBundleMessages(d);
					
					status = ExtensionBundleStatus.STARTED;
				} catch (ExtensionException ex) {
					if (ee == null) {
						ee = ex;
					}
                }catch (Throwable t){
                    if (ee == null) {
                        ee = new ExtensionException(ExtensionException.INTERNAL_ERROR, t);
                    }
                }
			}
			if (ee != null) {
				throw ee;
			}
		} catch (ExtensionException ee) {
		    log.error("Failed to start extension. ", ee);
			error = ee;
			status = ExtensionBundleStatus.ERROR;
			throw ee;
		}

		error = null;
	}

	private void setBundleMessages(ExtensionDescriptor d) throws ExtensionException {
		if(messageElement != null) {
			for (Iterator i2 = messageElement.getChildren().iterator(); i2.hasNext();) {
				Element el = (Element) i2.next();
				if (!el.getName().equals("message")) {
					throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
									"<messages> element may only contain <message> elements.");
				}
				String key = el.getAttributeValue("key");
				if (key == null) {
					throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
									"<message> element must have a key attribute.");
				}
				String aKey = "application." + d.getId() + "." + key;
				if (d.getMessageResources() != null) {
					if (!d.getMessageResources().isPresent(key)) {
						d.getMessageResources().setMessage(el.getAttributeValue("locale"), aKey, el.getText());
					}
				}
			}
		}
	}

	void checkDependenciesStarted(ExtensionBundle bundle) throws ExtensionException {
		if (bundle.getDependencies() != null) {
			for (String dep : bundle.getDependencies()) {
				if (!ExtensionStore.getInstance().isExtensionBundleLoaded(dep)) {
					throw new ExtensionException(ExtensionException.DEPENDENCY_NOT_INSTALLED, dep, getId());
				}
				ExtensionBundle depBundle = ExtensionStore.getInstance().getExtensionBundle(dep);
				if (!depBundle.getStatus().isStartedOrActivated()) {
					throw new ExtensionException(ExtensionException.DEPENDENCY_NOT_STARTED, dep, getId());
				}
				checkDependenciesStarted(depBundle);
			}
		}

	}

	void checkDependenciesActivated(ExtensionBundle bundle) throws ExtensionException {
		if (bundle.getDependencies() != null) {
			for (String dep : bundle.getDependencies()) {
				if (!ExtensionStore.getInstance().isExtensionBundleLoaded(dep)) {
					throw new ExtensionException(ExtensionException.DEPENDENCY_NOT_INSTALLED, dep, getId());
				}
				ExtensionBundle depBundle = ExtensionStore.getInstance().getExtensionBundle(dep);
				if (depBundle.getStatus() != ExtensionBundleStatus.ACTIVATED) {
					throw new ExtensionException(ExtensionException.DEPENDENCY_NOT_STARTED, dep, getId());
				}
				checkDependenciesActivated(depBundle);
			}
		}

	}

	/**
	 * Activate all extension bundles. This method will active all extension
	 * descriptors it contains and is the third phase in an extensions life
	 * cycle (after starting).
	 * 
	 * @throws ExtensionException if any bundle could not be activated
	 */
	public synchronized void activate() throws ExtensionException {
		try {
			if (isContainsPlugin() && getStatus() != ExtensionBundleStatus.STARTED) {
				throw new ExtensionException(ExtensionException.INVALID_EXTENSION_BUNDLE_STATUS,
								getId(),
								"Bundle is not in started so cannot be activated.");
			}

			// Check this bundles dependencies are installed and started
			checkDependenciesActivated(this);
			
			ExtensionException ee = null;
			for (Iterator i = iterator(); i.hasNext();) {
				ExtensionDescriptor d = (ExtensionDescriptor) i.next();
				try {
					d.activate();
					status = ExtensionBundleStatus.ACTIVATED;
				} catch (ExtensionException ex) {
					if (ee == null) {
						ee = ex;
					}
				}catch (Throwable t){
                    if (ee == null) {
                        ee = new ExtensionException(ExtensionException.INTERNAL_ERROR, t);
                    }
                }
			}
			if (ee != null) {
				throw ee;
			}
		} catch (ExtensionException ee) {
            log.error("Failed to activate extension bundle. ", ee);
            error = ee;
            status = ExtensionBundleStatus.ERROR;
            throw ee;
		}
		error = null;
	}

	/**
	 * Stop all extension bundles. This method will active all extension
	 * descriptors it contains and is the second or third phase in an extensions
	 * life cycle (after starting or activating).
	 * 
	 * @throws ExtensionException if any bundle could not be stopped
	 */
	public synchronized void stop() throws ExtensionException {
		ExtensionException ee = null;
		try {
			for (Iterator i = iterator(); i.hasNext();) {
				ExtensionDescriptor d = (ExtensionDescriptor) i.next();
				try {
					d.stop();
				} catch (ExtensionException ex) {
					if (ee == null) {
						ee = ex;
					}
					log.error("Failed to stop extension bundle. ", ex);
				}
			}
			if (ee != null) {
				throw ee;
			}
		} finally {
			status = ExtensionBundleStatus.ENABLED;
		}
	}

	/**
	 * Stop all extension bundles. This method will load all extension
	 * descriptors it contains and is the first phase in an extensions life
	 * cycle.
	 * 
	 * @throws ExtensionException on any error loading bundles
	 */
	public synchronized void load() throws ExtensionException {

		try {
			if (log.isInfoEnabled()) {
				log.info("Loading bundle from " + getFile().getAbsolutePath());
			}

			installer = new ExtensionInstaller(this);
			SAXBuilder sax = new SAXBuilder();
			try {
				doc = sax.build(descriptor);
			} catch (JDOMException jde) {
				jde.printStackTrace();
				throw new ExtensionException(ExtensionException.FAILED_TO_PARSE_DESCRIPTOR, jde);
			} catch (IOException ioe) {
				throw new ExtensionException(ExtensionException.INTERNAL_ERROR, ioe,  "Failed to load descriptor for parsing.");
			}

			hidden = "true".equals(doc.getRootElement().getAttributeValue("hidden"));
			license = doc.getRootElement().getAttributeValue("license");
			license = license == null || license.equals("") ? "Unknown" : license;
			if (log.isDebugEnabled())
				log.debug("Application bundle license is " + license);

			productURL = doc.getRootElement().getAttributeValue("productURL");
			instructionsURL = doc.getRootElement().getAttributeValue("instructionsURL");

			// Dependencies if any
			dependencyNames = null;
			String dependencies = doc.getRootElement().getAttributeValue("dependencies");
			if (dependencies != null) {
				log.warn("DEPRECATED. dependencies attribute in bundle " + getFile().getAbsolutePath()
					+ " should now use 'depends'.");
			} else {
				dependencies = doc.getRootElement().getAttributeValue("depends");
			}
			if (!Util.isNullOrTrimmedBlank(dependencies)) {
				dependencyNames = Arrays.asList(dependencies.split(","));
			}

			// Get the required host version
			String requiredHostVersionString = doc.getRootElement().getAttributeValue("requiredHostVersion");
			if (requiredHostVersionString != null && !"any".equalsIgnoreCase(requiredHostVersionString)) {
				requiredHostVersion = new VersionInfo.Version(requiredHostVersionString);
				int dif = requiredHostVersion.compareTo(VersionInfo.getVersion());
				if (dif > 0)
					throw new ExtensionException(ExtensionException.INSUFFICIENT_ADITO_HOST_VERSION,
									getName(),
									requiredHostVersionString);

			} else {
				requiredHostVersion = null;
			}

			String ver = doc.getRootElement().getAttributeValue("version");
			if (ver == null || ver.equals("")) {
				throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
								"<applications> element requires the attribute 'version'.");
			}
			version = new VersionInfo.Version(ver);

			if (doc.getRootElement().getName().equals("bundle")) {

				Attribute a = doc.getRootElement().getAttribute("id");
				id = a == null ? null : a.getValue();

				if (id == null) {
					throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
									"<bundle> element requires attribute 'id'");
				}
				if (log.isDebugEnabled())
					log.debug("Application bundle id is " + id);

				name = doc.getRootElement().getAttribute("name").getValue();
				if (log.isDebugEnabled())
					log.debug("Application bundle name is " + name);

				Attribute orderAttr = doc.getRootElement().getAttribute("order");
				if (orderAttr == null) {
					log.warn("<bundle> element in " + getFile().getPath() + " now requires attribute 'order'. Assuming 99999");
					order = 99999;
				} else {
					try {
						order = orderAttr.getIntValue();
					} catch (DataConversionException dce) {
						throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
										"'order' attribute is invalid. " + dce.getMessage());
					}
				}

				licenseFilePath = doc.getRootElement().getAttributeValue("licenseAgreement");

				if (name == null) {
					throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
									"<bundle> element requires the attribute 'name'");
				}
				
				startOnSetupMode = "true".equals(doc.getRootElement().getAttributeValue("startOnSetupMode"));

				for (Iterator i = doc.getRootElement().getChildren().iterator(); i.hasNext();) {
					Element e = (Element) i.next();
					if (e.getName().equalsIgnoreCase("description")) {
						description = Util.trimmedBothOrBlank(e.getText());
					} else if (e.getName().equalsIgnoreCase("install")) {
						processInstall(e);
					} else if (e.getName().equalsIgnoreCase("messages")) {
						// processed later
						messageElement = e;
					} else if (e.getName().equals("application") || e.getName().equals("extension")) {
						ExtensionDescriptor desc = new ExtensionDescriptor();
						desc.load(this, e);
						add(desc);
					} else {
						throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
										"<bundle> element may only contain <description> or <extension> (or the deprecated <application>) elements.");
					}
				}

			} else if (doc.getRootElement().getName().equals("application") || doc.getRootElement().getName().equals("extension")) {
				log.warn("DEPRECATED. All extensions should now use the <bundle> tag, " + getFile().getPath()
					+ " is using not using this tag.");
				ExtensionDescriptor desc = new ExtensionDescriptor();
				desc.load(this, doc.getRootElement());
				id = desc.getId();
				name = desc.getName();
				description = desc.getDescription();
				order = 99999;
				dependencyNames = Arrays.asList(new String[] { "applications",
					"tunnels" });
				add(desc);
			} else {
				throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
								"Application bundle root element must be <bundle> (or the deprecated <application> or <extension>) elements.");
			}

			// All we know is that the application is
			// installed until the application store is
			// available
			setType(TYPE_INSTALLED);
		} catch (ExtensionException ee) {
			error = ee;
			throw ee;
		}

		error = null;

	}

	/**
	 * Get if this bundle contains any plugins.
	 * 
	 * @return contains plugins
	 */
	public boolean isContainsPlugin() {
		boolean containsPlugin = false;
		for (ExtensionDescriptor descriptor : this) {
			if (descriptor.getTypeName().equals(PluginType.TYPE)) {
				containsPlugin = true;
			}
		}
		return containsPlugin;
	}

	/**
	 * Remove this bundle. If it started, then it will be stopped first. If the
	 * bundle contains plugins the actual removal of the files will be deferred
	 * until restart.
	 * 
	 * @throws Exception
	 */
	public void removeBundle() throws Exception {
		if (log.isInfoEnabled())
			log.info("Removing extension bundle " + getId());
		boolean containsPlugin = isContainsPlugin();
		try {
			// Stop the bundle if no plugins are contained
			if (!containsPlugin && getStatus().isStartedOrActivated()) {
				stop();
			}

			// 
			for (ExtensionDescriptor descriptor : this) {
				descriptor.removeDescriptor();
			}
		} finally {
			if (containsPlugin) {
				setType(ExtensionBundle.TYPE_PENDING_REMOVAL);
			}
		}
	}

	/**
	 * Get the <i>Install</i> (if any) for this extension bundle.
	 * <code>null</code> will be returned if this bundle has no installer.
	 * 
	 * @return extension installer
	 */
	public ExtensionInstaller getInstaller() {
		return installer;
	}

	public String getInstructionsURL() {
		return instructionsURL;
	}

	public String getProductURL() {
		return productURL;
	}

	public VersionInfo.Version getVersion() {
		return version;
	}

    public VersionInfo.Version getDisplayVersion() {
        return isUpdateable() ? updateVersion : version;
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getLicense() {
		return license;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public File getBaseDir() {
		return getFile() == null ? null : getFile().getParentFile();
	}

	/**
	 * @param application
	 * @return
	 */
	public boolean containsApplication(String application) {
		for (Iterator i = iterator(); i.hasNext();) {
			if (((ExtensionDescriptor) i.next()).getId().equals(application)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param id
	 * @return
	 */
	public ExtensionDescriptor getApplicationDescriptor(String id) {
		for (Iterator i = iterator(); i.hasNext();) {
			ExtensionDescriptor app = (ExtensionDescriptor) i.next();
			if (app.getId().equals(id)) {
				return app;
			}
		}
		return null;
	}

	public int compareTo(Object arg0) {
		int c = getType() - ((ExtensionBundle) arg0).getType();
		return c != 0 ? c : name.compareTo(((ExtensionBundle) arg0).name);
	}

	public File getLicenseFile() {
		File baseDir = getBaseDir();
		return baseDir == null || licenseFilePath == null ? null : new File(baseDir, licenseFilePath);
	}

	public VersionInfo.Version getRequiredHostVersion() {
		return requiredHostVersion;
	}

	public void setRequiredHostVersion(VersionInfo.Version requiredHostVersion) {
		this.requiredHostVersion = requiredHostVersion;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public boolean isMandatoryUpdate() {
		return mandatoryUpdate;
	}

	public Throwable getError() {
		return error;
	}

	public boolean canStop() {
		boolean canStop = status.isStartedOrActivated();
		if (!canStop) {
			return false;
		}
		for (Iterator i = iterator(); i.hasNext();) {
			ExtensionDescriptor d = (ExtensionDescriptor) i.next();
			if (!d.canStop()) {
				return false;
			}
		}
		return true;

	}

	public boolean canStart() {
		return ExtensionBundleStatus.ENABLED.equals(status) && getType() != ExtensionBundle.TYPE_PENDING_INSTALLATION
			&& getType() != ExtensionBundle.TYPE_PENDING_REMOVAL
			&& getType() != ExtensionBundle.TYPE_PENDING_UPDATE
			&& getType() != ExtensionBundle.TYPE_PENDING_STATE_CHANGE;
	}

	public boolean canDisable() {
		return !ExtensionBundleStatus.DISABLED.equals(status) && type != TYPE_CONFIGUREABLE && type != TYPE_INSTALLABLE && type != TYPE_PENDING_STATE_CHANGE;
	}

	public boolean canEnable() {
		return ExtensionBundleStatus.DISABLED.equals(status) && type != TYPE_PENDING_STATE_CHANGE;
	}

	public ExtensionBundleStatus getStatus() {
		return status;
	}

	public void setStatus(ExtensionBundleStatus status) {
		this.status = status;
	}

	private void processInstall(Element installElement) throws ExtensionException {
		String when = installElement.getAttributeValue("when");
		when = when == null ? ExtensionInstaller.ON_ACTIVATE : when;
		for (Iterator i = installElement.getChildren().iterator(); i.hasNext();) {
			Element e = (Element) i.next();
			if (e.getName().equalsIgnoreCase("mkdir")) {
				String dir = Util.trimmedBothOrBlank(e.getText());
				if (dir == null || dir.equals("")) {
					throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
									"<mkdir> contents must be the name of a directory to create.");
				}
				installer.addOp(new ExtensionInstaller.MkdirInstallOp(when, dir));
			} else if (e.getName().equalsIgnoreCase("cp")) {
				String from = Util.trimmedBothOrBlank(e.getText());
				String to = e.getAttributeValue("to");
				String toDir = e.getAttributeValue("toDir");
				boolean overwrite = "true".equalsIgnoreCase(e.getAttributeValue("overwrite"));
				if (from == null || from.equals("") || ((to == null || to.equals("")) && (toDir == null || toDir.equals("")))) {
					throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
									"<cp> content must be the source path and the tag must have either a to or toDir attribute.");
				}
				installer.addOp(new ExtensionInstaller.CpInstallOp(when, from, to, toDir, overwrite));
			} else if (e.getName().equalsIgnoreCase("rm")) {
				String path = Util.trimmedBothOrBlank(e.getText());
				if (path == null || path.equals("")) {
					throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
									"<rm> must a path as its content.");
				}
				installer.addOp(new ExtensionInstaller.RmInstallOp(when, path));
			} else if (e.getName().equalsIgnoreCase("custom")) {
				String clazz = Util.trimmedBothOrBlank(e.getText());
				if (clazz == null || clazz.equals("")) {
					throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
									"<custom> must provide a class name that implements ExtensionInstaller.ExtensionInstallOp as its content.");
				}
				try {
					installer.addOp(new ExtensionInstaller.CustomInstallOpWrapper(when, clazz));
				} catch (Exception ex) {
					throw new ExtensionException(ExtensionException.FAILED_TO_PROCESS_DESCRIPTOR,
									ex, "Failed to create <custom> install op.");

				}
			}
		}
	}

	public String getChanges() {
		return changes==null ? "" : changes.trim();
	}
	
	public String toString() {
		return id + " " + version;
	}

	public VersionInfo.Version getUpdateVersion() {
		return updateVersion;
	}

	public void setUpdateVersion(VersionInfo.Version updateVersion) {
		this.updateVersion = updateVersion;
	}

	public void setChanges(String changes) {
		this.changes = changes;
	}

    public boolean isStartOnSetupMode() {
        return startOnSetupMode;
    }

	public String getPlatform() {
		return platform;
	}

	public String getArch() {
		return arch;
	}
	
}
