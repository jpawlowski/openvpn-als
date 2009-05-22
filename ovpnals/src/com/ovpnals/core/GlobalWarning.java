
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
			
package com.ovpnals.core;

import javax.servlet.http.HttpSession;

import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.ResourceType;

/**
 * <i>Global Warnings</i> may be shown on a number of different conditions
 * depending on the permissions a user may have.
 * <p>
 * There are currently 4 different ways of determining if a global warning
 * should be displayed :- *
 * <ul>
 * <li>Single session. Message will be displayed to a single session onyl.</li>
 * <li>All users. Message will be displayed to all users and on the logon
 * screen.</li>
 * <li>Super User. Message will be displayed to the super user only.</li>
 * <li>Users With Permissions. Message will be displayed to any users with the
 * any of specified permissions.</li>
 * </ul>
 */
public class GlobalWarning {
	
	/**
	 * This <i>Dismiss</i> link displayed when the messaged is 
	 * rendered has different behaviour depending on the type
	 * of message. This enum defines those types 
	 */
	public enum DismissType {
	    /**
	     * Dismiss for the current session only. If this is 
	     * not a temporary global warning then 
	     */
	    DISMISS_FOR_SESSION,
	    /**
	     * Dismiss the message for current user for the
	     * lifetime of the server. The message will not be 
	     * displayed if the user logs off and on again
	     */
	    DISMISS_FOR_USER,
	    /**
	     * The message cannot be dismissed at all.
	     */
	    NO_DISMISS;
	}
	
    /**
     * Message will be displayed to all users and on the logon screen
     */
    public final static int SINGLE_SESSION = 0;
    
    /**
     * Message will be displayed to all users and on the logon screen
     */
    public final static int ALL_USERS = 1;

    /**
     * Message will be displayed to the super user only.
     */
    public final static int SUPER_USER = 2;

    /**
     * Message will be displayed to all those with access to management
     * console
     */
    public final static int MANAGEMENT_USERS = 3;

    /**
     * Permissions. Message will be displayed to any users with the any of
     * specified permissions.
     */
    public final static int USERS_WITH_PERMISSIONS = 4;

    // Private instance variables
    private int type;
    private BundleActionMessage message;
    private ResourceType requiredResourceType;
    private Permission[] requiredPermissions;
    private HttpSession session;
    private DismissType dismissType;

    /**
     * Constructor for {@link #SINGLE_SESSION} type. This implies
     * {@link DismissType#DISMISS_FOR_SESSION}.
     * 
     * @param session
     * @param message message
     */
    public GlobalWarning(HttpSession session, BundleActionMessage message) {
        this.type = SINGLE_SESSION;
        this.session = session;
        this.message = message;
        this.dismissType = DismissType.DISMISS_FOR_SESSION;
    }

    /**
     * Constructor for types {@link #SUPER_USER}, {@link #MANAGEMENT_USERS} 
     * or {@link #ALL_USERS}.
     * 
     * @param type type
     * @param message message
     * @param dismissType dismiss type.  
     * @throws IllegalArgumentException if incorrect type
     * @see DismissType
     */
    public GlobalWarning(int type, BundleActionMessage message, DismissType dismissType) throws IllegalArgumentException {
        if(type != SUPER_USER && type != MANAGEMENT_USERS && type != ALL_USERS) {
            throw new IllegalArgumentException("Illegal global warning type.");
        }
        this.dismissType = dismissType;
        this.type = type;
        this.message = message;
    }

    /**
     * Constructor for {@link #USERS_WITH_PERMISSIONS} type.
     * 
     * @param requiredResourceType required resource type
     * @param requiredPermissions required permissions
     * @param message message
     * @param dismissType dismiss type.
     * @see DismissType
     */
    public GlobalWarning(ResourceType requiredResourceType, Permission[] requiredPermissions, BundleActionMessage message, DismissType dismissType) {
        this.type = USERS_WITH_PERMISSIONS;
        this.requiredPermissions = requiredPermissions;
        this.requiredResourceType = requiredResourceType;
        this.message = message;
        this.dismissType = dismissType;
    }
    
    /**
     * Get the dismiss type.
     * 
     * @return dismiss type
     */
    public DismissType getDismissType() {
    	return dismissType;
    }

    /**
     * Get the message. Available for all types.
     * 
     * @return message
     */
    public BundleActionMessage getMessage() {
        return message;
    }

    /**
     * Get the required permissions. Available for type of {@link #USERS_WITH_PERMISSIONS}.
     * 
     * @return required permissions
     */
    public Permission[] getRequiredPermissions() {
        return requiredPermissions;
    }


    /**
     * Get the required resource type. Available for type of {@link #USERS_WITH_PERMISSIONS}.
     * 
     * @return resource type
     */
    public ResourceType getRequiredResourceType() {
        return requiredResourceType;
    }


    /**
     * Get the session to display for. Available for type of {@link #SINGLE_SESSION}.
     * 
     * @return resource type
     */
    public HttpSession getSession() {
        return session;
    }

    /**
     * Get the type. May be one of {@link #ALL_USERS}, {@link #SUPER_USER}
     * or {@link #USERS_WITH_PERMISSIONS}, {@link #SINGLE_SESSION}.
     * 
     * @return type
     */
    public int getType() {
        return type;
    }

}
