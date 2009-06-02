
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
			
package net.openvpn.als.security;

import java.util.Date;

import net.openvpn.als.policyframework.Principal;

/**
 * @author Lee David Painter
 * 
 * The attributes of a user who has logged into the system.
 */
public interface User extends Principal, Comparable {

    // User attributes keys
    
    /**
     * Constant for home directory 
     */
    public static final String USER_ATTR_HOME_DIRECTORY = "homeDirectory";
    
    /**
     * Constant home drive
     */
    public static final String USER_ATTR_HOME_DRIVE = "homeDrive";
    
    /**
     * Constants for user enabled
     */
    public static final String USER_ATTR_ENABLED = "openvpnalsUserEnabled";
    
    /**
     * Constant for startup property profile
     */
    public static final String USER_STARTUP_PROFILE = "startupProfile";

    /**
     * If the user database supports password changing, it may return a date the
     * password was last changed. Todays date should be returned if this
     * information is not available and the logon controller will assume the
     * password does not need changing.
     * 
     * @uml.property name="lastPasswordChange" multiplicity="(0 1)"
     * @return Date
     */
    public Date getLastPasswordChange();
    
    /**
     * If the user database supports password changing, it may return 
     * true if the password requires changing.
     * 
     * @uml.property name="requiresPasswordChange" multiplicity="(0 1)"
     * @return Date
     */
    public boolean requiresPasswordChange();

    /**
     * The unique name of the user
     * 
     * @return String
     * 
     * @uml.property name="principalName" multiplicity="(0 1)"
     */
    public String getPrincipalName();

    /**
     * The users email address
     * 
     * @return String
     * 
     * @uml.property name="email" multiplicity="(0 1)"
     */
    public String getEmail();

    /**
     * The users full name.
     * 
     * @return String
     * 
     * @uml.property name="fullname" multiplicity="(0 1)"
     */
    public String getFullname();


    /**
     * Determine the users home network place (or <code>null</code> if no home
     * is available for this user).
     * 
     * @return String
     * 
     * @uml.property name="homeNetworkPlaceUri" multiplicity="(0 1)"
     */
    public String getHomeNetworkPlaceUri();

    /**
     * @param role
     * @return <tt>true</tt> if the supplied role is assigned to the user
     */
    boolean memberOf(Role role);
    
    /**
     * Get a list of all the roles the user is in
     * 
     * @return roles
     */
    public Role[] getRoles();
}