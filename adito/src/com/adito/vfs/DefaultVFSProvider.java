
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

import java.util.List;

public class DefaultVFSProvider extends AbstractVFSProvider {

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
	public DefaultVFSProvider(String scheme, boolean fireEvents, boolean hiddenFilesSupported, int hostRequirement, int portRequirement, int userInfoRequirement, int pathRequirement, Class storeClass, String bundle) {
		super(scheme, fireEvents, hiddenFilesSupported, hostRequirement, portRequirement, userInfoRequirement, pathRequirement, storeClass, bundle);
	}

	/**
	 * Constructor.
	 *
	 * @param scheme scheme
	 * @param storeClass store class
	 * @param bundle bundle
	 */
	public DefaultVFSProvider(String scheme, Class storeClass, String bundle) {
		super(scheme, storeClass, bundle);
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
	public DefaultVFSProvider(String scheme, List<String> handle, boolean fireEvents, boolean hiddenFilesSupported, int hostRequirement, int portRequirement, int userInfoRequirement, int pathRequirement, Class storeClass, String bundle) {
		super(scheme,
						handle,
						fireEvents,
						hiddenFilesSupported,
						hostRequirement,
						portRequirement,
						userInfoRequirement,
						pathRequirement,
						storeClass, bundle);
	}

}
