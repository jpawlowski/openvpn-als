/*
 *  Adito-PAM
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
package com.adito.pam;

import java.io.File;

import org.jdom.Element;

import com.adito.core.UserDatabaseManager;
import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.ExtensionException;
import com.adito.extensions.types.DefaultPlugin;
import com.adito.extensions.types.PluginDefinition;
import com.adito.security.UserDatabaseDefinition;

/**
 * This is the entry for PAM Extension. It allows the use of <a href="http://www.kernel.org/pub/linux/libs/pam/">Linux PAM</a> to authenticate users.
 * The startPlugin method (called by Adito extension manager) checks PAM configurations location is readable.
 * Then the method registers PAMUserDatabase to Adito UserDatabaseManager or throws an exception if PAM configuration could not be reached. 
 * 
 */
public class PAMPlugin extends DefaultPlugin {
	
	/**
	 * Default Constructor
	 */
	public PAMPlugin() {
		super(null, false);

	}
	
	/* (non-Javadoc)
	 * @see com.adito.extensions.types.Plugin#startPlugin(com.adito.extensions.types.PluginDefinition, com.adito.extensions.ExtensionDescriptor, org.jdom.Element)
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
