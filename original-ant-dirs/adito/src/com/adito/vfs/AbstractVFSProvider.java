
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

import java.util.Arrays;
import java.util.List;


/**
 * Abstract implementation of a {@link VFSProvider}.
 */
public abstract class AbstractVFSProvider implements VFSProvider {

	private String bundle;
	private String scheme;
	private List<String> handle;
	private boolean fireEvents;
	private boolean hiddenFilesSupported;
	private int hostRequirement, portRequirement, userInfoRequirement, pathRequirement;
	private Class storeClass;
	
	/**
	 * Constructor for providers that only handle stores with scheme
	 * specified, do not fire events and support hidden files and do not support host, port
	 * or userinfo
	 *
	 * @param scheme
	 * @param storeClass
	 * @param bundle bundle
	 */
	public AbstractVFSProvider(String scheme, Class storeClass, String bundle) {
		this(scheme, false, true, ELEMENT_NOT_APPLICABLE, ELEMENT_NOT_APPLICABLE, ELEMENT_NOT_APPLICABLE, ELEMENT_REQUIRED, storeClass, bundle);
	}

	/**
	 * Constructor.
	 *
	 * @param scheme scheme
	 * @param fireEvents fire events
	 * @param hiddenFilesSupported hidden files supported
	 * @param hostRequirement host requirement
	 * @param portRequirement port requirement
	 * @param userInfoRequirement user info requirement
	 * @param pathRequirement path requirement
	 * @param storeClass store class
	 * @param bundle bundle
	 */
	public AbstractVFSProvider(String scheme, boolean fireEvents, boolean hiddenFilesSupported, int hostRequirement, int portRequirement, int userInfoRequirement, int pathRequirement, Class storeClass, String bundle) {
		this(scheme, Arrays.asList(new String[] { scheme }), fireEvents, hiddenFilesSupported, hostRequirement, portRequirement, userInfoRequirement, pathRequirement, storeClass, bundle);
	}

	/**
	 * Constructor.
	 *
	 * @param scheme scheme
	 * @param handle handle
	 * @param fireEvents fire events
	 * @param hiddenFilesSupported hidden files supported
	 * @param hostRequirement host requirement
	 * @param portRequirement port requirement
	 * @param userInfoRequirement user info requirement
	 * @param pathRequirement path requirement
	 * @param storeClass store class
	 * @param bundle bundle
	 */
	public AbstractVFSProvider(String scheme, List<String> handle, boolean fireEvents, boolean hiddenFilesSupported, int hostRequirement, int portRequirement, int userInfoRequirement, int pathRequirement, Class storeClass, String bundle) {
		super();
		this.scheme = scheme;
		this.handle = handle;
		this.fireEvents = fireEvents;
		this.hiddenFilesSupported = hiddenFilesSupported;
		this.hostRequirement = hostRequirement;
		this.portRequirement = portRequirement;
		this.userInfoRequirement = userInfoRequirement;
		this.pathRequirement = pathRequirement;
		this.storeClass = storeClass;
		this.bundle = bundle;
	}
	
	/* (non-Javadoc)
	 * @see com.adito.vfs.VFSProvider#getBundle()
	 */
	public String getBundle() {
		return bundle;
	}

	/* (non-Javadoc)
	 * @see com.adito.vfs.VFSProvider#getScheme()
	 */
	public String getScheme() {
		return scheme;
	}

	/* (non-Javadoc)
	 * @see com.adito.vfs.VFSProvider#getStoreClass()
	 */
	public Class getStoreClass() {
		return storeClass;
	}

	/* (non-Javadoc)
	 * @see com.adito.vfs.VFSProvider#isFireEvents()
	 */
	public boolean isFireEvents() {
		return fireEvents;
	}

	/* (non-Javadoc)
	 * @see com.adito.vfs.VFSProvider#isHiddenFilesSupported()
	 */
	public boolean isHiddenFilesSupported() {
		return hiddenFilesSupported;
	}

	/* (non-Javadoc)
	 * @see com.adito.vfs.VFSProvider#isRequiredHost()
	 */
	public int getHostRequirement() {
		return hostRequirement;
	}

	/* (non-Javadoc)
	 * @see com.adito.vfs.VFSProvider#isRequiredPort()
	 */
	public int getPortRequirement() {
		return portRequirement;
	}

	/* (non-Javadoc)
	 * @see com.adito.vfs.VFSProvider#isRequiredUserInfo()
	 */
	public int getUserInfoRequirement() {
		return userInfoRequirement;
	}

	/* (non-Javadoc)
	 * @see com.adito.vfs.VFSProvider#willHandle(java.lang.String)
	 */
	public boolean willHandle(String scheme) {
		return handle.contains(scheme);
	}

	/* (non-Javadoc)
	 * @see com.adito.vfs.VFSProvider#getPathRequirement()
	 */
	public int getPathRequirement() {
		return pathRequirement;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(VFSProvider o) {
		return getScheme().compareTo(o.getScheme());
	}

}
