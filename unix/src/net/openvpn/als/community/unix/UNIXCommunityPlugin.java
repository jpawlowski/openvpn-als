
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
			
package net.openvpn.als.community.unix;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import net.openvpn.als.core.UserDatabaseManager;
import net.openvpn.als.extensions.ExtensionDescriptor;
import net.openvpn.als.extensions.ExtensionException;
import net.openvpn.als.extensions.types.DefaultPlugin;
import net.openvpn.als.extensions.types.PluginDefinition;
import net.openvpn.als.security.UserDatabaseDefinition;

public class UNIXCommunityPlugin extends DefaultPlugin {
    private static final Log LOG = LogFactory.getLog(UNIXCommunityPlugin.class);

    /**
     * Constructor.
     */
    public UNIXCommunityPlugin() {
        super(null, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.extensions.types.Plugin#startPlugin(net.openvpn.als.extensions.types.PluginDefinition,
     *      net.openvpn.als.extensions.ExtensionDescriptor, org.jdom.Element)
     */
    public void startPlugin(PluginDefinition pluginDefinition, ExtensionDescriptor descriptor, Element element)
                    throws ExtensionException {
        super.startPlugin(pluginDefinition, descriptor, element);
        if (new File("/etc/passwd").canRead()) {
            UserDatabaseDefinition definition = new UserDatabaseDefinition(UNIXUserDatabase.class, "unixAuth", "unix", -1);
            UserDatabaseManager.getInstance().registerDatabase(definition);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to register UnixCommunityPlugin, /etc/passwd was not available.");
            }
        }
    }
}