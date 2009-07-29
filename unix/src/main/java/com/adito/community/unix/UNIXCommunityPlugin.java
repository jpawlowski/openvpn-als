
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
			
package com.adito.community.unix;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import com.adito.core.UserDatabaseManager;
import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.ExtensionException;
import com.adito.extensions.types.DefaultPlugin;
import com.adito.extensions.types.PluginDefinition;
import com.adito.security.UserDatabaseDefinition;

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
     * @see com.adito.extensions.types.Plugin#startPlugin(com.adito.extensions.types.PluginDefinition,
     *      com.adito.extensions.ExtensionDescriptor, org.jdom.Element)
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