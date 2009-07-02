
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

import org.jdom.Element;

import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.ExtensionException;

/**
 * Interface to be implemented by all extension components that provide one or
 * more <i>Plugin</i>.
 * <p>
 * When the core server starts up, first all plugins have their {@link #startPlugin(PluginDefinition, ExtensionDescriptor, Element)}
 * method called. Any plugins which throw an exception at this point are discarded
 * and will not be available for the remaining lifetime of the server. 
 * <p>
 * Any plugins that successfully initialised are then checked to see if the
 * have been used before. If they haven't then the {@link #installPlugin()} method
 * is invoked. Here the plugin may perform any one-off tasks such as adding
 * default resources.  
 * <p>
 * Next, the {@link #activatePlugin()} method is called. 
 * <p>
 * When the server shuts down, the {@link #canStopPlugin()} method is called,
 * if this is <code>true</code> then the {@link #stopPlugin()} method is called.
 * <p>
 * Plugins are defined in the <i>Extension Description</i> for the extension 
 * they are part of. Information required for plugin includes the fully
 * qualified class name of this interface implementation, sort order, names
 * and descriptions and dependencies.
 */
public interface Plugin {

    /**
     * This is the first call to the plugin, made when the server is first 
     * starting up. If an exception is thrown at this point, the plugin will
     * first be stopped, then ignored for the remainder of the servers lifetime. 
     * 
     * @param definition plugin definition to stop
     * @param descriptor descriptor
     * @param element configuration element
     * @throws ExtensionException on any error
     */
    public void startPlugin(PluginDefinition definition, ExtensionDescriptor descriptor, Element element) throws ExtensionException;
    
    /**
     * Start the plugin. Invoked after <i>all</i> registered plugins have had their 
     * {@link #startPlugin(PluginDefinition, ExtensionDescriptor, Element)} method called.
     * 
     * @throws ExtensionException on any error
     */
    public void activatePlugin() throws ExtensionException;

    /**
     * Get if this plugin may be stopped. This is invoked when the server is
     * shutting down or when a plugin fails to initialise. If <code>true</code> is returned, then {@link #stopPlugin()}
     * will be called next. If <code>false</code> is returned, then {@link #stopPlugin()}
     * will not be called and the plugin manager will attempt to stop the next 
     * registered plugin. 
     * 
     * @return can stop plugin
     */
    public boolean canStopPlugin();

    /**
     * Stop the plugin. This is invoked when the server is
     * shutting down or when a plugin fails to initialise. It will only be called
     * if {@link #canStopPlugin()} returned <code>true</code>
     * @throws ExtensionException TODO
     */
    public void stopPlugin() throws ExtensionException;

    /**
     * If this plugin contributes any new tiles, this method returns the 
     * path to the tiles configure file (within the context of the webapp).
     * 
     * @return path to tiles configuration resource
     */
    public String getTilesConfigFile();
}
