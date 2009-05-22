
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
			
package com.ovpnals.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ovpnals.policyframework.NoPermissionException;
import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.Principal;
import com.ovpnals.policyframework.Resource;
import com.ovpnals.policyframework.ResourceType;
import com.ovpnals.security.AccountLock;
import com.ovpnals.security.AccountLockedException;
import com.ovpnals.security.InvalidLoginCredentialsException;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.User;

/**
 * A set of utilities used by the policy framework.
 * 
 * @author Brett Smith
 * @since 0.2
 */
public interface PolicyService {

    /**
     * Convenience method for testing if a principal can logon. The basic test
     * is the presence of an enabled AuthentionScheme. System authentication
     * schemes are ignored.
     * 
     * @param principal principal
     * @return can logon
     * @throws Exception on any error
     */
    boolean canLogin(Principal principal) throws Exception;

    /**
     * Convenience method for testing if a user can logon. A check will also be
     * made to see if the user
     * 
     * @param user userprincipal
     * @throws InvalidLoginCredentialsException if invalid credentials
     * @throws AccountLockedException if locked
     */
    void checkLogin(User user) throws InvalidLoginCredentialsException, AccountLockedException;

    /**
     * Convience method to test if a user is enabled or disabled
     * 
     * @param user user to test
     * @return disabled
     * @throws Exception
     */
    boolean isEnabled(User user) throws Exception;

    /**
     * Convience method to set if a user is enabled or disabled
     * 
     * @param user user
     * @param enabled enabled
     * @param lock account lock (if any)
     * @param session session
     * @throws Exception on any error
     */
    void setEnabled(User user, boolean enabled, AccountLock lock, SessionInfo session) throws Exception;

    /**
     * Check if a user has any specified permission, throwing an exception if it
     * doesnt
     * 
     * @param resourceType resource type to check
     * @param permissions permission required
     * @param request request to extract user object from
     * @throws NoPermissionException if permission is denied
     */
    void checkPermissions(ResourceType<?> resourceType, Permission[] permissions, HttpServletRequest request)
                    throws NoPermissionException;

    /**
     * Check if a user has a specified permission, throwing an exception if it
     * doesnt
     * 
     * @param resourceType resource type to check
     * @param permission permission required
     * @param request request to extract user object from
     * @throws NoPermissionException if permission is denied
     */
    void checkPermission(ResourceType<? extends Resource> resourceType, Permission permission, HttpServletRequest request) throws NoPermissionException;

    /**
     * Check if a user has a specified permission, throwing an exception if it
     * doesnt
     * 
     * @param resourceType resource type to check
     * @param permission permission required
     * @param sessionInfo request to extract user object from
     * @throws NoPermissionException if permission is denied
     */
    void checkPermission(ResourceType<? extends Resource> resourceType, Permission permission, SessionInfo sessionInfo) throws NoPermissionException;

    /**
     * Check if a user has a specified permission, throwing an exception if it
     * doesnt
     * 
     * @param resourceType resource type to check
     * @param permission permission required
     * @param session request to extract user object from
     * @throws NoPermissionException if permission is denied
     */
    void checkPermission(ResourceType<? extends Resource> resourceType, Permission permission, HttpSession session) throws NoPermissionException;

    /**
     * Check if a user has a specified permission, throwing an exception if it
     * doesnt
     * 
     * @param resourceType resource type to check
     * @param permission permission required
     * @param user user
     * @throws NoPermissionException if permission is denied
     */
    void checkPermission(ResourceType<? extends Resource> resourceType, Permission permission, User user) throws NoPermissionException;
}