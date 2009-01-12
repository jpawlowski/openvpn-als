
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
			
package com.adito.networkplaces;

import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.Resource;

/**
 * <p>
 * Implementation of {@link com.adito.policyframework.Resource} for a
 * network place on a number of different Mounts. i.e. ftp, local files, network
 * resources.
 */
public interface NetworkPlace extends Resource {

    /**
     * Hidden
     */
    public final static int TYPE_HIDDEN = 1;

    /**
     * Normal
     */
    public final static int TYPE_NORMAL = 0;

    /**
     * @return The NetworkPlace's host.
     */
    public String getHost();

    /**
     * @param host The NetworkPlace's host.
     */
    public void setHost(String host);
    
    /**
     * @return The NetworkPlace's uri.
     */
    public String getPath();

    /**
     * @param uri The NetworkPlace's path.
     */
    public void setPath(String path);
    
    /**
     * @return The NetworkPlace's port.
     */
    public int getPort();

    /**
     * @param port The NetworkPlace's port.
     */
    public void setPort(int port);
    
    /**
     * @return The NetworkPlace's username.
     */
    public String getUsername();

    /**
     * @param username The NetworkPlace's username.
     */
    public void setUsername(String username);
    
    /**
     * @return The NetworkPlace's password.
     */
    public String getPassword();

    /**
     * @param password The NetworkPlace's password.
     */
    public void setPassword(String password);

    /**
     * @return The Type of file, Hidden or Normal
     */
    public int getType();

    /**
     * @return Weather the resource can have its folders accessable.
     */
    public boolean isAllowRecursive();

    /**
     * @param allowResursive Weather the resource can have its folders accessable.
     */
    public void setAllowResursive(boolean allowResursive);

    /**
     * @return Weather the resource does not allow deletion.
     */
    public boolean isNoDelete();

    /**
     * @param noDelete Weather the resource does not allow deletion.
     */
    public void setNoDelete(boolean noDelete);

    /**
     * @return Weather the resource is read only.
     */
    public boolean isReadOnly();

    /**
     * @param readOnly Weather the resource is read only.
     */
    public void setReadOnly(boolean readOnly);

    /**
     * @return Weather the resource shows hidden files.
     */
    public boolean isShowHidden();

    /**
     * @param showHidden Weather the resource shows hidden files.
     */
    public void setShowHidden(boolean showHidden);

    /**
     * Get the scheme to use for the path.  This will determine what
     * store will be used 
     * 
     * @return scheme name
     */
    public String getScheme();

    /**
     * Set the scheme portion to use for the path. This will determine what
     * store will be used 
     * 
     * @param scheme scheme
     */
    public void setScheme(String scheme);

    /**
     * Get if this network place should be automatically started when the user logs in.
     * 
     * @return <code>true</code> if auto start network place upon login
     */
    public boolean isAutoStart();
    
    /**
     * Set if this network place should auto-start when the user logs in.
     * 
     * @param autoStart auto start network place upon login
     */
    public void setAutoStart(boolean autoStart);
    
	/**
	 * Get the URI required to launch this network place
	 * 
	 * @param launchSession launch session
	 * @return uri
	 */
	public String getLaunchUri(LaunchSession launchSession);
}