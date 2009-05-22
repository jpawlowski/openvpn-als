
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
			
package com.ovpnals.webforwards;


import java.util.List;

import com.ovpnals.extensions.types.PluginDatabase;
import com.ovpnals.replacementproxy.Replacement;
import com.ovpnals.security.User;

/**
 * The <i>System Configuration> {@link com.ovpnals.core.Database} implementation
 * is responsible for storing an retrieving OpenVPN-ALS's web forward resources and
 * configuration.
 */
public interface WebForwardDatabase extends PluginDatabase {

    /**
     * Get web forwards of type.
     * 
     * @param realmID
     * @return List
     * @throws Exception
     */
    public List<WebForward> getWebForwards(int realmID) throws Exception;

    /**
     * Get reverse proxy web forward.
     *  
     * @param user
     * @param pathInContext
     * @return WebForward
     * @throws Exception
     */
    public WebForward getReverseProxyWebForward(User user, String pathInContext) throws Exception;

    /**
     * Get web forward.
     * 
     * @param id
     * @return WebForward
     * @throws Exception
     */
    public WebForward getWebForward(int id) throws Exception;

    /**
     * Does the reverse proxy path exist.
     * 
     * @param path
     * @return boolean
     * @throws Exception
     */
    public boolean reverseProxyPathExists(String path) throws Exception;

    /**
     * Does the reverse proxy path exist for the given web forward.
     * 
     * @param path
     * @param webforward_id
     * @return boolean
     * @throws Exception
     */
    public boolean reverseProxyPathExists(String path, int webforward_id) throws Exception;

    /**
     * Create a new web forward.
     * 
     * @param webForward
     * @return WebForward
     * @throws Exception
     */
    public WebForward createWebForward(WebForward webForward) throws Exception;

    /**
     * Update the specified web forward.
     * @param webForward
     * @throws Exception
     */
    public void updateWebForward(WebForward webForward) throws Exception;

    /**
     * Delete the web forward with the specified resource id.
     * 
     * @param resourceId
     * @return WebForward
     * @throws Exception
     */
    public WebForward deleteWebForward(int resourceId) throws Exception;

    /**
     * Get all web forwards.
     * 
     * @return List<WebForward>
     * @throws Exception
     */
    public List<WebForward> getWebForwards() throws Exception;

    /**
     * Get the web forward with the specified name.
     *  
     * @param name
     * @param realmID
     * @return WebForward
     * @throws Exception
     */
    public WebForward getWebForward(String name, int realmID) throws Exception;
    
    /**
     * @param username
     * @param replaceType
     * @param mimeType
     * @param site
     * @return
     * @throws Exception
     */
    public List<Replacement> getReplacementsForContent(String username, int replaceType, String mimeType, String site) throws Exception;

    public List getReplacements() throws Exception;

    public void updateReplacement(Replacement replacement) throws Exception;

    public void deleteReplacement(int sequence) throws Exception;

    public Replacement getReplacement(int sequence) throws Exception;

    public Replacement createReplacement(Replacement replacement) throws Exception;


}