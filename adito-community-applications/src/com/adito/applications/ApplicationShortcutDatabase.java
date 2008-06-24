
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
			
package com.adito.applications;

import java.util.List;
import java.util.Map;

import com.adito.extensions.types.PluginDatabase;

/**
 * The <i>System Configuration> {@link com.adito.core.Database} implementation
 * is responsible for storing an retrieving Adito's Application Shortcut resources and
 * configuration.
 */
public interface ApplicationShortcutDatabase extends PluginDatabase {

    /**
     * Create a new application shortcut.
     * 
     * @param application
     * @param name
     * @param description
     * @param settings
     * @return int the newly created resource id.
     * @throws Exception
     */
    public int createApplicationShortcut(String application, String name, String description, Map settings, boolean autoStart, int realmID)
                    throws Exception;

    /**
     * Get the application shortcuts with the specified application type.
     *  
     * @param application
     * @return List<ApplicationShortcut>
     * @throws Exception
     */
    public List<ApplicationShortcut> getShortcuts() throws Exception;

    /**
     * Get the application shortcuts with the specified application type.
     *  
     * @param application
     * @return List<ApplicationShortcut>
     * @throws Exception
     */
    public List<ApplicationShortcut> getShortcuts(int realmID) throws Exception;

    /**
     * Get the ApplicationShortcut with the specified resaource id.
     * 
     * @param shortcutId
     * @return ApplicationShortcut
     * @throws Exception
     */
    public ApplicationShortcut getShortcut(int shortcutId) throws Exception;

    /**
     * Get the ApplicationShortcut with the specified resaource id.
     * @param name
     * @param realmID
     * @return ApplicationShortcut
     * @throws Exception
     */
    public ApplicationShortcut getShortcut(String name, int realmID) throws Exception;

    /**
     * Delete the ApplicationShortcut with the specified resource id.
     *  
     * @param shortcutId
     * @return ApplicationShortcut
     * @throws Exception
     */
    public ApplicationShortcut deleteShortcut(int shortcutId) throws Exception;

    /**
     * Update the ApplicationShortcut.
     * 
     * @param id
     * @param name
     * @param description
     * @param settings
     * @throws Exception
     */
    public void updateApplicationShortcut(int id, String name, String description, Map settings, boolean autoStart)
                    throws Exception;

    /**
     * Remove the Application Shortcuts for a given application.
     * 
     * @param applicationId
     * @throws Exception
     */
    public void removeApplicationShortcuts(String applicationId) throws Exception;
}