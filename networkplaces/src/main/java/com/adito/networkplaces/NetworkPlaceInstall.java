
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
			
package com.adito.networkplaces;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.ContextHolder;
import com.adito.extensions.ExtensionInstaller;
import com.adito.extensions.ExtensionInstaller.ExtensionInstallOp;


/**
 * Installer operation to convert and sanity check network place paths.
 */
public class NetworkPlaceInstall implements ExtensionInstallOp {
	
	final static Log log = LogFactory.getLog(NetworkPlaceInstall.class);
    final static String VARIABLE_PATTERN = "\\$\\{[^}]*\\}";
    final static String PROTOTYPE_PATTERN = "_prototype_[^_]*_[^_]*_";

	/* (non-Javadoc)
	 * @see com.adito.extensions.ExtensionInstaller.ExtensionInstallOp#doOp(com.adito.extensions.ExtensionInstaller)
	 */
	public void doOp(ExtensionInstaller install) throws Exception {
		NetworkPlaceDatabase npd = NetworkPlaceDatabaseFactory.getInstance();
		List<NetworkPlace> networkPlaces = npd.getNetworkPlaces();
		int errs = 0;
		File oldTagLib = new File("webapp" + File.separator + "WEB-INF" + File.separator + "vfs.tld");
		if(oldTagLib.exists()) {
			log.info("Deleting old tag lib " + oldTagLib.getAbsolutePath());
			oldTagLib.delete();
		}
		
		for(NetworkPlace networkPlace : networkPlaces) {
			try {
				NetworkPlaceUtil.convertNetworkPlace(networkPlace);
				npd.updateNetworkPlace(networkPlace.getResourceId(), networkPlace.getScheme(), networkPlace.getResourceName(), 
					networkPlace.getResourceDescription(), networkPlace.getHost(), networkPlace.getPath(), networkPlace.getPort(), networkPlace.getUsername(), networkPlace.getPassword(), networkPlace.isReadOnly(), networkPlace.isAllowRecursive(), networkPlace.isNoDelete(), networkPlace.isShowHidden(), networkPlace.isAutoStart());
			}
			catch(Exception e) {
				errs ++;
				log.error("Failed to convert network place '" + networkPlace.getResourceName() + "'. Please manually check and amend.");
			}
		}
		if(errs > 0) {
			throw new Exception("Failed to convert " + errs + " network places. Please check the log for the names of the " +
					"failed network places that failed and then manually amend these resources.");
		}
	}

	/* (non-Javadoc)
	 * @see com.adito.extensions.ExtensionInstaller.ExtensionInstallOp#getPhase()
	 */
	public String getPhase() {
		return ExtensionInstaller.ON_ACTIVATE;
	}

}
