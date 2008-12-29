
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
			
package com.adito.extensions.types;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.ExtensionException;

/**
 * Default implementation of a {@link Plugin} that does nothing.
 */
public class DefaultPlugin extends AbstractPlugin {

    final static Log log = LogFactory.getLog(DefaultPlugin.class);

    private PluginDefinition definition;

    /**
     * Constructor.
     * 
     * @param tilesConfigFile tiles configuration file
     * @param canStop can stop plugin
     */
    public DefaultPlugin(String tilesConfigFile, boolean canStop) {
        super(tilesConfigFile, canStop);
    }

    /* (non-Javadoc)
     * @see com.adito.extensions.types.Plugin#startPlugin(com.adito.extensions.types.PluginDefinition, com.adito.extensions.ExtensionDescriptor, org.jdom.Element)
     */
    public void startPlugin(PluginDefinition definition, ExtensionDescriptor descriptor, Element element) throws ExtensionException {
        this.definition = definition;
        if (log.isInfoEnabled())
            log.info("Starting plugin '" + definition.getName() + "'");
    }

    /* (non-Javadoc)
     * @see com.adito.extensions.types.Plugin#activatePlugin()
     */
    public void activatePlugin() throws ExtensionException {
        if (log.isInfoEnabled())
            log.info("Activating plugin '" + definition.getName() + "'");
    }

    /* (non-Javadoc)
     * @see com.adito.extensions.types.Plugin#stopPlugin()
     */
    public void stopPlugin() throws ExtensionException {
        if (log.isInfoEnabled())
            log.info("Stopping plugin '" + definition.getName() + "'");
    }

    /**
     * Get the plugin definition for this plugin implementation. This will only
     * be available after the plugin has been initialised with
     * {@link #startPlugin}.
     * 
     * @return plugin definition
     */
    public PluginDefinition getPluginDefinition() {
        return definition;
    }

}
