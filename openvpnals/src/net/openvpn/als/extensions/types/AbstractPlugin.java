
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
			
package net.openvpn.als.extensions.types;

/**
 * Abstract implementation for a plugin.
 */
public abstract class AbstractPlugin implements Plugin {

    // Private instance variables
    private boolean canStop;
    private String tilesConfigFile;

    /**
     * Constructor
     * 
     * @param tilesConfigFile tiles configuration file
     * @param canStop can stop plugin
     */
    public AbstractPlugin(String tilesConfigFile, boolean canStop) {
        this.tilesConfigFile = tilesConfigFile;
        this.canStop = canStop;
    }

    /**
     * Set whether the plugin can be stopped
     * 
     * @param canStop can stop plugin
     */
    public void setCanStopPlugin(boolean canStop) {
        this.canStop = canStop;
    }


    /* (non-Javadoc)
     * @see net.openvpn.als.extensions.types.Plugin#canStopPlugin()
     */
    public boolean canStopPlugin() {
        return canStop;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.extensions.types.Plugin#getTilesConfigFile()
     */
    public String getTilesConfigFile() {
        return tilesConfigFile;
    }

}
