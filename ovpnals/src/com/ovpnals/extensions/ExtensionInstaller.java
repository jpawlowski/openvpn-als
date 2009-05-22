
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
			
package com.ovpnals.extensions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.boot.Util;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.core.stringreplacement.ExtensionBundleReplacer;
import com.ovpnals.extensions.store.ExtensionStore;

/**
 * Encapsulates an installation process. Implementations of
 * {@link ExtensionInstallOp} should be added using
 * {@link #addOp(com.ovpnals.extensions.ExtensionInstaller.ExtensionInstallOp)}.
 * The entire process may then be run by invoking {@link #doInstall(String)}.
 * <p>
 * Installers may currently be run in two different phases, <i>Extension
 * Activation</i> and <i>Extension Startup</i>. This is determined by the
 * {@link ExtensionInstallOp#getPhase()}.
 * <p>
 * Before calling {@link #doInstall(String)},
 * 
 */
public class ExtensionInstaller {

	final static Log log = LogFactory.getLog(ExtensionInstaller.class);

	/**
	 * State returned by {@link ExtensionInstallOp#getPhase()} for actions that
	 * should be run when extension is activated
	 */
	public static final String ON_ACTIVATE = "activate";

	/**
	 * State returned by {@link ExtensionInstallOp#getPhase()} for actions that
	 * should be run when extension is started
	 */
	public static final String ON_START = "start";

	// Private instance variables

	private ExtensionBundle bundle;
	private List<ExtensionInstallOp> ops;

	/**
	 * Constructor.
	 * 
	 * @param bundle bundle this installer is for
	 */
	public ExtensionInstaller(ExtensionBundle bundle) {
		this.bundle = bundle;
		ops = new ArrayList<ExtensionInstallOp>();
	}

	/**
	 * Get the bundle this installer is for.
	 * 
	 * @return bundle
	 */
	public ExtensionBundle getBundle() {
		return bundle;
	}

	/**
	 * Add a new operation to the installer.
	 * 
	 * @param op operation
	 */
	public void addOp(ExtensionInstallOp op) {
		ops.add(op);
	}

	/**
	 * Utility method to check if it is ok to use a file in the installer.
	 * 
	 * @param file
	 * @return file
	 * @throws IOException on any error
	 */
	public static File checkFile(File file) throws IOException {
		/*
		 * TODO Make sure the file is ok for use (e.g. not a file somewhere
		 * outside of OpenVPN-ALSs tree)
		 */
		return file;
	}

	/**
	 * Start the install.
	 * 
	 * @param phase TODO
	 * 
	 * @throws Exception on any error
	 */
	public void doInstall(String phase) throws Exception {
		if (ops.size() == 0) {
			if (log.isInfoEnabled())
				log.info("Bundle " + bundle.getName() + " has no installer script.");
		} else {
			boolean started = false;
			try {
				for (ExtensionInstallOp op : ops) {
					if(op.getPhase().equals(phase) || ( phase.equals(ExtensionInstaller.ON_ACTIVATE) && op.getPhase() == null) ) {
						if(!started) {
							if (log.isInfoEnabled())
								log.info("Starting installer for " + bundle.getName());
							started = true;
						}
						op.doOp(this);
					}
				}
				if (log.isInfoEnabled())
					log.info("Completed installation for " + bundle.getName());
			} finally {
				ExtensionStore.VERSION_PREFS.put(bundle.getId(), bundle.getVersion().toString());
				ExtensionStore.VERSION_PREFS.flush();
			}
		}
	}

	/**
	 * Get the number of operations in this install
	 * 
	 * @return operations
	 */
	public int getOpCount() {
		return ops.size();
	}

	/**
	 * Interface to be implemented by all extension installer operations
	 * 
	 * @see ExtensionInstaller
	 */
	public static interface ExtensionInstallOp {

		/**
		 * Get the phase this operation should occur in. Will be one of
		 * {@link ExtensionInstaller#ON_ACTIVATE} or
		 * {@link ExtensionInstaller#ON_START}. If this is null it
		 * should be assumed to be {@link ExtensionInstaller#ON_ACTIVATE}.
		 * 
		 * @return phase
		 */
		public String getPhase();

		/**
		 * Perform the install operation.
		 * 
		 * @param install
		 * @throws Exception
		 */
		public void doOp(ExtensionInstaller install) throws Exception;
	}

	/**
	 * Abstract implementation of an {@link ExtensionInstallOp} providing a
	 * constructor for the <i>Phase</i> attribute.
	 */
	public static abstract class AbstractExtensionInstallOp implements ExtensionInstallOp {
		private String phase;

		/**
		 * Constructor.
		 * 
		 * @param phase phase
		 */
		public AbstractExtensionInstallOp(String phase) {
			this.phase = phase;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.ovpnals.extensions.ExtensionInstaller.ExtensionInstallOp#getPhase()
		 */
		public String getPhase() {
			return phase;
		}
	}

	/**
	 * Create a directory.
	 */
	public static class MkdirInstallOp extends AbstractExtensionInstallOp {
		private String path;

		/**
		 * Constructor.
		 * 
		 * @param phase phase
		 * @param path path to create
		 * @throws IllegalArgumentException
		 */
		public MkdirInstallOp(String phase, String path) throws IllegalArgumentException {
			super(phase);
			this.path = path;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.ovpnals.extensions.ExtensionInstaller.ExtensionInstallOp#doOp(com.ovpnals.extensions.ExtensionInstaller)
		 */
		public void doOp(ExtensionInstaller install) throws Exception {
			path = CoreUtil.platformPath(ExtensionBundleReplacer.replace(install.getBundle(), path));
			File f = checkFile(new File(path));
			if (log.isInfoEnabled())
				log.info("Creating directory " + f.getAbsolutePath());
			f.mkdirs();
		}
	}

	/**
	 * Remove a file or directory
	 */
	public static class RmInstallOp extends AbstractExtensionInstallOp {
		private String path;

		/**
		 * Constructor.
		 * 
		 * @param phase phase
		 * @param path path to remove
		 * @throws IllegalArgumentException
		 */
		public RmInstallOp(String phase, String path) throws IllegalArgumentException {
			super(phase);
			this.path = path;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.ovpnals.extensions.ExtensionInstaller.ExtensionInstallOp#doOp(com.ovpnals.extensions.ExtensionInstaller)
		 */
		public void doOp(ExtensionInstaller install) throws Exception {
			path = CoreUtil.platformPath(ExtensionBundleReplacer.replace(install.getBundle(), path));
			File f = checkFile(new File(path));
			Util.delTree(f);
		}
	}

	/**
	 * Copy a file or directory to another file or directory
	 */
	public static class CpInstallOp extends AbstractExtensionInstallOp {
		private String from;
		private String to;
		private String toDir;
		private boolean overwrite;

		/**
		 * Constructor.
		 * 
		 * @param phase phase
		 * @param from file or directory to copy
		 * @param to to file (use <code>null</code> when copying to dir)
		 * @param toDir to directory (use <code>null</code> when copying to
		 *        file)
		 * @param overwrite overwrite target
		 * @throws IllegalArgumentException
		 */
		public CpInstallOp(String phase, String from, String to, String toDir, boolean overwrite) throws IllegalArgumentException {
			super(phase);
			this.from = from;
			this.to = to;
			this.toDir = toDir;
			this.overwrite = overwrite;
		}

		public void doOp(ExtensionInstaller install) throws Exception {
			from = CoreUtil.platformPath(ExtensionBundleReplacer.replace(install.getBundle(), from));
			File f = checkFile(new File(from));
			if (to != null) {
				File t = checkFile(new File(CoreUtil.platformPath(ExtensionBundleReplacer.replace(install.getBundle(), to))));
				if (f.isDirectory()) {
					throw new Exception("Cannot copy directory to a file");
				}
				if (log.isInfoEnabled())
					log.info("Copying " + f.getAbsolutePath() + " to " + t.getAbsolutePath());
				if (t.exists() && !overwrite) {
					log.error("Failed to copy to target because it already exists and overwrite attribute is not true");
				} else {
					Util.copy(f, t);
				}
			} else if (toDir != null) {
				File t = checkFile(new File(ExtensionBundleReplacer.replace(install.getBundle(), toDir)));
				if (log.isInfoEnabled())
					log.info("Copying " + f.getAbsolutePath() + " to " + t.getAbsolutePath());
				if (((!t.isDirectory() && t.exists()) || (t.isDirectory() && new File(t, f.getName()).exists())) && !overwrite) {
					log.error("Failed to copy to target because it already exists and overwrite attribute is not true");
				} else {
					Util.copyToDir(f, t, false, false);
				}
			}
		}
	}

	/**
	 * Provides a wrapper around another {@link ExtensionInstallOp}
	 * implementation that may be specified by its class name.
	 */
	public static class CustomInstallOpWrapper extends AbstractExtensionInstallOp {

		private String clazz;

		/**
		 * Constructor.
		 * 
		 * @param phase phase
		 * @param clazz class name of wrapper operation
		 */
		public CustomInstallOpWrapper(String phase, String clazz) {
			super(phase);
			this.clazz = clazz;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.ovpnals.extensions.ExtensionInstaller.ExtensionInstallOp#doOp(com.ovpnals.extensions.ExtensionInstaller)
		 */
		public void doOp(ExtensionInstaller install) throws Exception {
			if (log.isInfoEnabled())
				log.info("Running custom install op. " + clazz);
			ExtensionInstallOp op = (ExtensionInstallOp) Class.forName(clazz, true, getClass().getClassLoader()).newInstance();
			op.doOp(install);
		}

	}
}
