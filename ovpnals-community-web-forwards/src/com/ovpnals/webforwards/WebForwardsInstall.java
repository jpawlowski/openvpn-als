
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
			
package com.ovpnals.webforwards;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.extensions.ExtensionInstaller;
import com.ovpnals.extensions.ExtensionInstaller.ExtensionInstallOp;

/**
 * Installer operation for web forwards
 */
public class WebForwardsInstall implements ExtensionInstallOp {
	
	final static Log log = LogFactory.getLog(WebForwardsInstall.class);

	/* (non-Javadoc)
	 * @see com.ovpnals.extensions.ExtensionInstaller.ExtensionInstallOp#doOp(com.ovpnals.extensions.ExtensionInstaller)
	 */
	public void doOp(ExtensionInstaller install) throws Exception {
		File oldTagLib = new File("webapp" + File.separator + "WEB-INF" + File.separator + "webforwards.tld");
		if(oldTagLib.exists()) {
			log.info("Deleting old tag lib " + oldTagLib.getAbsolutePath());
			oldTagLib.delete();
		}
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.extensions.ExtensionInstaller.ExtensionInstallOp#getPhase()
	 */
	public String getPhase() {
		return ExtensionInstaller.ON_ACTIVATE;
	}

}
