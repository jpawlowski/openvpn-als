/*
 *  OpenVPN-ALS-PAM
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
package com.ovpnals.pam;

import java.io.File;

import org.jdom.Element;

import com.ovpnals.core.UserDatabaseManager;
import com.ovpnals.extensions.ExtensionDescriptor;
import com.ovpnals.extensions.ExtensionException;
import com.ovpnals.extensions.types.DefaultPlugin;
import com.ovpnals.extensions.types.PluginDefinition;
import com.ovpnals.security.UserDatabaseDefinition;

/**
 * This is the entry for PAM Extension. It allows the use of <a href="http://www.kernel.org/pub/linux/libs/pam/">Linux PAM</a> to authenticate users.
 * The startPlugin method (called by OpenVPN-ALS extension manager) checks PAM configurations location is readable.
 * Then the method registers PAMUserDatabase to OpenVPN-ALS UserDatabaseManager or throws an exception if PAM configuration could not be reached. 
 * 
 */
public class PAMCommunityPlugin extends DefaultPlugin {
	
	/**
	 * Default Constructor
	 */
	public PAMCommunityPlugin() {
		super(null, false);

	}
	
	/* (non-Javadoc)
	 * @see com.ovpnals.extensions.types.Plugin#startPlugin(com.ovpnals.extensions.types.PluginDefinition, com.ovpnals.extensions.ExtensionDescriptor, org.jdom.Element)
	 */
	public void startPlugin(PluginDefinition definition, ExtensionDescriptor descriptor, Element element) throws ExtensionException {
		super.startPlugin(definition, descriptor, element);
		
		if(new File("/etc/pam.d").canRead()){
			/* If last parameter of UserDatabaseDefinition specifie where the plugin configuration screens should be displayed :
			 * -1 : displayed in system configuration.
			 * 2010 : displayed during install. This is the container Id set in extension.xml
			 * 
			 */
	        UserDatabaseDefinition databaseDefinition = new UserDatabaseDefinition(PAMUserDatabase.class, "pam", "pam", 2010);
	        UserDatabaseManager.getInstance().registerDatabase(databaseDefinition);
		
		} else {
			throw new ExtensionException(ExtensionException.FAILED_TO_LAUNCH,"PAM not currently supported on this platform or PAM not installed");
		}
		
    }

}
