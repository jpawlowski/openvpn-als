
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
			
package com.ovpnals.applications;

import java.util.Map;

import com.ovpnals.policyframework.Resource;

/**
 * Extension of the {@link Resource} interface to describe 
 * <i>Application Shortcuts</i>. 
 * <p>
 * These resources allow the user to create shortcuts to the installed
 * <i>Application Extensions</i>
 * <p>
 * Each Application Extension will support a number of parameters. This shortcut
 * will also store the values that have been configured.
 * 
 * @see com.ovpnals.extensions.ApplicationLauncher
 */
public interface ApplicationShortcut extends Resource {
    /**
     * Get the parameters for this shortcut.
     * 
     * @return parameters
     */
    Map<String, String> getParameters();
    
    /**
     * Get the ID of the application launcher this shortcut requires.
     * 
     * @return application
     */
    String getApplication();
    
    /**
     * Get if this application shortcut should be automatically started when the user logs in.
     * 
     * @return <code>true</code> if auto start application shortcut upon login
     */
    boolean isAutoStart();
    
    /**
     * Set if this application shortcut should auto-start when the user logs in.
     * 
     * @param autoStart auto start application shortcut upon login
     */
    void setAutoStart(boolean autoStart);
}