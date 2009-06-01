
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.activedirectory;

import org.jdom.Element;

import net.openvpn.als.core.UserDatabaseManager;
import net.openvpn.als.extensions.ExtensionDescriptor;
import net.openvpn.als.extensions.ExtensionException;
import net.openvpn.als.extensions.types.DefaultPlugin;
import net.openvpn.als.extensions.types.PluginDefinition;
import net.openvpn.als.security.UserDatabaseDefinition;

/**
 * Adds a Microsoft Active Directory <i>User Database</i> to OpenVPNALS.
 */
public class ActiveDirectoryCommunityPlugin extends DefaultPlugin {

	/**
	 * Constructor.
	 */
	public ActiveDirectoryCommunityPlugin() {
		super(null, false);
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.extensions.types.Plugin#startPlugin(net.openvpn.als.extensions.types.PluginDefinition, net.openvpn.als.extensions.ExtensionDescriptor, org.jdom.Element)
	 */
	public void startPlugin(PluginDefinition definition, ExtensionDescriptor descriptor, Element element) throws ExtensionException {
		super.startPlugin(definition, descriptor, element);
        UserDatabaseDefinition databaseDefinition = new UserDatabaseDefinition(ActiveDirectoryUserDatabase.class, "activeDirectory", "activeDirectory", 80);
        UserDatabaseManager.getInstance().registerDatabase(databaseDefinition);
    }
}