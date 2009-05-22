
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
			
package com.ovpnals.policyframework;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.core.CoreAttributeConstants;
import com.ovpnals.core.CoreEvent;
import com.ovpnals.core.CoreEventConstants;
import com.ovpnals.core.CoreServlet;
import com.ovpnals.properties.Property;
import com.ovpnals.properties.impl.userattributes.UserAttributeKey;
import com.ovpnals.security.AccountLock;
import com.ovpnals.security.AccountLockedException;
import com.ovpnals.security.AuthenticationScheme;
import com.ovpnals.security.InvalidLoginCredentialsException;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.SystemDatabaseFactory;
import com.ovpnals.security.User;

/**
 * A set of utilities used by the policy framework.
 * 
 * @author Brett Smith
 * @since 0.2
 */

public class PolicyUtil {

    final static Log log = LogFactory.getLog(PolicyUtil.class);

    /**
     * Convenience method for testing if a principal can logon. The basic test
     * is the presence of an enabled AuthentionScheme. System authentication
     * schemes are ignored.
     * 
     * @param principal principal
     * @return can logon
     * @throws Exception on any error
     */
    public static boolean canLogin(Principal principal) throws Exception {
        PolicyDatabase policyDatabase = PolicyDatabaseFactory.getInstance();
        List<Integer> grantedResourcesOfType = policyDatabase.getGrantedResourcesOfType(principal,
            PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE);
        for (Integer schemeId : grantedResourcesOfType) {
            AuthenticationScheme scheme = SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequence(schemeId);
            if (scheme != null && !scheme.isSystemScheme() && scheme.getEnabled()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convenience method for testing if a user can logon. A check will also be
     * made to see if the user
     * 
     * @param user userprincipal
     * @throws InvalidLoginCredentialsException if invalid credentials
     * @throws AccountLockedException if locked
     */
    public static void checkLogin(User user) throws InvalidLoginCredentialsException, AccountLockedException {
        try {
            if (!canLogin(user)) {
                throw new InvalidLoginCredentialsException("You do not have permission to logon.");
            }
            if (!isEnabled(user)) {
                throw new AccountLockedException(user.getPrincipalName(), "Account locked. Please contact your administrator.",
                                true, 0);
            }
        } catch (InvalidLoginCredentialsException lce) {
            throw lce;
        } catch (AccountLockedException ale) {
            throw ale;
        } catch (Exception e) {
            log.error("Failed to test if logon for " + user.getPrincipalName() + " is allowed.", e);
            throw new InvalidLoginCredentialsException("You do not have permission to logon.");
        }
    }

    /**
     * Convience method to test if a user is enabled or disabled
     * 
     * @param user user to test
     * @return disabled
     * @throws Exception
     */
    public static boolean isEnabled(User user) throws Exception {
        return Property.getPropertyBoolean(new UserAttributeKey(user, User.USER_ATTR_ENABLED));
    }

    /**
     * Convience method to set if a user is enabled or disabled
     * 
     * @param user user
     * @param enabled enabled
     * @param lock account lock (if any)
     * @param session session
     * @throws Exception on any error
     */
    public static void setEnabled(User user, boolean enabled, AccountLock lock, SessionInfo session) throws Exception {
        CoreServlet servlet = CoreServlet.getServlet();
        try {
            servlet.fireCoreEvent(new CoreEvent(servlet, CoreEventConstants.ACCOUNT_LOCKED, lock, session));
            Property.setProperty(new UserAttributeKey(user, User.USER_ATTR_ENABLED), enabled, session);
            servlet.fireCoreEvent(new CoreEvent(CoreServlet.getServlet(), enabled ? CoreEventConstants.GRANT_ACCESS
                            : CoreEventConstants.REVOKE_ACCESS, null, session, CoreEvent.STATE_SUCCESSFUL).addAttribute(
                CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID, user.getPrincipalName()));
        } catch (Exception e) {
            servlet.fireCoreEvent(new CoreEvent(servlet, enabled ? CoreEventConstants.GRANT_ACCESS
                            : CoreEventConstants.REVOKE_ACCESS, null, session, CoreEvent.STATE_UNSUCCESSFUL).addAttribute(
                CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID, user.getPrincipalName()));
            throw e;
        }

    }

    /**
     * Check if a user has any specified permission, throwing an exception if it
     * doesnt
     * 
     * @param resourceType resource type to check
     * @param permissions permission required
     * @param request request to extract user object from
     * @throws NoPermissionException if permission is denied
     */
    public static void checkPermissions(ResourceType<?> resourceType, Permission[] permissions, HttpServletRequest request)
                    throws NoPermissionException {
        for (int i = 0; i < permissions.length; i++) {
            try {
                checkPermission(resourceType, permissions[i], request);
                break;
            } catch (NoPermissionException npe) {
                if (i == (permissions.length - 1)) {
                    throw npe;
                }
            }
        }
    }

    /**
     * Check if a user has a specified permission, throwing an exception if it
     * doesnt
     * 
     * @param resourceType resource type to check
     * @param permission permission required
     * @param request request to extract user object from
     * @throws NoPermissionException if permission is denied
     */
    public static void checkPermission(ResourceType<?> resourceType, Permission permission, HttpServletRequest request)
                    throws NoPermissionException {
        checkPermission(resourceType, permission, request.getSession());
    }

    /**
     * Check if a user has a specified permission, throwing an exception if it
     * doesnt
     * 
     * @param resourceType resource type to check
     * @param permission permission required
     * @param sessionInfo 
     * @throws NoPermissionException if permission is denied
     */
    public static void checkPermission(ResourceType<?> resourceType, Permission permission, SessionInfo sessionInfo)
                    throws NoPermissionException {
        checkPermission(resourceType, permission, sessionInfo.getHttpSession());
    }

    /**
     * Check if a user has a specified permission, throwing an exception if it
     * doesnt
     * 
     * @param resourceType resource type to check
     * @param permission permission required
     * @param session 
     * @throws NoPermissionException if permission is denied
     */
    public static void checkPermission(ResourceType<?> resourceType, Permission permission, HttpSession session)
                    throws NoPermissionException {
        try {
            User user = LogonControllerFactory.getInstance().getUser(session, null);
            checkPermission(resourceType, permission, user);
        } catch (NoPermissionException npe) {
            throw npe;
        } catch (Exception e) {
            throw new NoPermissionException("Failed to check permission. ", e, null, resourceType);
        }
    }

    /**
     * Check if a user has a specified permission, throwing an exception if it
     * doesnt
     * 
     * @param resourceType resource type to check
     * @param permission permission required
     * @param user user
     * @throws NoPermissionException if permission is denied
     */
    public static void checkPermission(ResourceType<?> resourceType, Permission permission, User user) throws NoPermissionException {
        try {
            PolicyDatabase policyDatabase = PolicyDatabaseFactory.getInstance();
            if (user == null) {
                throw new NoPermissionException("Failed to get user.", null, resourceType);
            }
            if (!policyDatabase.isPermitted(resourceType, new Permission[] { permission }, user, false)) {
                throw new NoPermissionException("Permission denied.", user, resourceType);
            }
        } catch (NoPermissionException npe) {
            throw npe;
        } catch (Exception e) {
            throw new NoPermissionException("Failed to check permission. ", e, null, resourceType);
        }
    }
    
    /**
     * Return a personal policy name made with the username.
     * @param username
     * @return personal policy name
     */
    public static String getPersonalPolicyName(String username) {
        return (username + PolicyConstants.PERSONAL_PREFIX);
    }
}
