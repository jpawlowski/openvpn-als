
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
			
package com.ovpnals.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.boot.Util;
import com.ovpnals.core.CoreServlet;
import com.ovpnals.policyframework.Principal;
import com.ovpnals.properties.Property;
import com.ovpnals.properties.impl.systemconfig.SystemConfigKey;
import com.ovpnals.realms.Realm;

/**
 * Default implementation of a {@link com.ovpnals.security.UserDatabase}.
 */
public abstract class DefaultUserDatabase implements UserDatabase {
    private static final Log LOG = LogFactory.getLog(DefaultUserDatabase.class);
    private final String description;
    private final boolean supportsAccountCreation;
    private final LogonController logonController;
    protected boolean supportsPasswordChange;
    protected boolean open;
    protected Realm realm;

    /**
     * Constructor.
     * @param description description
     * @param supportsAccountCreation true if concrete user database supports account creation
     * @param supportsPasswordChange true if concrete user database supports password changing
     */
    public DefaultUserDatabase(String description, boolean supportsAccountCreation, boolean supportsPasswordChange) {
        this.description = description;
        this.supportsAccountCreation = supportsAccountCreation;
        this.supportsPasswordChange = supportsPasswordChange;
        this.logonController = LogonControllerFactory.getInstance();
    }

    /*
     * (non-Javadoc)
     * @see com.ovpnals.security.UserDatabase#supportsPasswordChange()
     */
    public boolean supportsPasswordChange() {
        return supportsPasswordChange;
    }

    /*
     * (non-Javadoc)
     * @see com.ovpnals.security.UserDatabase#supportsAccountCreation()
     */
    public boolean supportsAccountCreation() {
        return supportsAccountCreation;
    }

    /*
     * (non-Javadoc)
     * @see com.ovpnals.security.UserDatabase#getDatabaseDescription()
     */
    public String getDatabaseDescription() {
        return description;
    }

    public void open(CoreServlet controllingServlet, Realm realm) throws Exception {
        if (realm == null) {
            throw new IllegalArgumentException("No realm supplied.");
        }
        this.open = true;
        LOG.info("Opening user database " + getClass().getName() + " for realm " + realm.getResourceName());
        this.realm = realm;
    }

    public void open(CoreServlet controllingServlet) throws Exception {
        throw new Exception("User databases must be opened with the realm.");
    }

    /*
     * (non-Javadoc)
     * @see com.ovpnals.security.UserDatabase#changePassword(java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    public void changePassword(String username, String oldPassword, String password, boolean forcePasswordChangeAtLogon)
                    throws UserDatabaseException, InvalidLoginCredentialsException {
        assertSupportsPasswordChange();
        throw new InvalidLoginCredentialsException(
                        "User database is not read-only, but the changePassword() method has not been implemented");
    }

    /*
     * (non-Javadoc)
     * @see com.ovpnals.security.UserDatabase#setPassword(java.lang.String, java.lang.String, boolean, com.ovpnals.security.User, java.lang.String)
     */
    public void setPassword(String username, String password, boolean forcePasswordChangeAtLogon, User adminUser,
                            String adminPassword) throws UserDatabaseException, InvalidLoginCredentialsException {
        assertSupportsPasswordChange();
        throw new InvalidLoginCredentialsException("");

    }

    /*
     * (non-Javadoc)
     * @see com.ovpnals.security.UserDatabase#createAccount(java.lang.String, java.lang.String, java.lang.String, java.lang.String com.ovpnals.security.Role[])
     */
    public User createAccount(String username, String password, String email, String fullname, Role[] roles) throws Exception {
        assertSupportsAccountCreation();
        throw new Exception("User database is not read-only, but the createAccount() method has not been implemented");
    }

    /*
     * (non-Javadoc)
     * @see com.ovpnals.security.UserDatabase#updateAccount(com.ovpnals.security.User, java.lang.String, java.lang.String, com.ovpnals.security.Role[])
     */
    public void updateAccount(User user, String email, String fullname, Role[] roles) throws Exception {
        assertSupportsAccountCreation();
        throw new Exception("User database is not read-only, but the updateAccount() method has not been implemented");
    }

    /*
     * (non-Javadoc)
     * @see com.ovpnals.security.UserDatabase#deleteAccount(com.ovpnals.security.User)
     */
    public final void deleteAccount(User user) throws UserNotFoundException, Exception {
        assertSupportsAccountCreation();
        performDeleteAccount(user);
        logonController.unlockUser(user.getPrincipalName());
    }

    protected void performDeleteAccount(User user) throws Exception, UserNotFoundException {
        throw new UnsupportedOperationException(
                        "User database is not read-only, but the deleteAccount() method has not been implemented");
    }

    /*
     * (non-Javadoc)
     * @see com.ovpnals.security.UserDatabase#createRole(java.lang.String)
     */
    public Role createRole(String rolename) throws Exception {
        assertSupportsAccountCreation();
        throw new Exception("User database is not read-only, but the createRole() method has not been implemented");
    }

    /*
     * (non-Javadoc)
     * @see com.ovpnals.security.UserDatabase#deleteRole(java.lang.String)
     */
    public void deleteRole(String rolename) throws Exception {
        assertSupportsAccountCreation();
        throw new Exception("User database is not read-only, but the deleteRole() method has not been implemented");
    }
    
    protected void assertSupportsPasswordChange() throws InvalidLoginCredentialsException {
        if (!supportsPasswordChange()) {
            throw new InvalidLoginCredentialsException("Database doesn't support password change.");
        }
    }
    
    protected void assertSupportsAccountCreation() {
        if (!supportsAccountCreation()) {
            throw new UnsupportedOperationException("User database is read-only");
        }
    }
    
    public final boolean isAccountNameInUse(String username) throws UserDatabaseException {
        try {
            getAccount(username);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public final User[] listAllUsers(String filter, int maxResults) throws UserDatabaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("List '" + maxResults + "' users with filter '" + filter + "'");
        }
        
        Collection<User> users = filterPrincipals(filter, maxResults, allUsers(), false);
        return users.toArray(new User[users.size()]);
    }
    
    public final int getMaxUserResults() {
        return Property.getPropertyInt(new SystemConfigKey("ui.maxuser.count"));
    }

    public final boolean isRoleNameInUse(String rolename) throws UserDatabaseException {
        try {
            getRole(rolename);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public final Role[] listAllRoles(String filter, int maxResults) throws UserDatabaseException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("List '" + maxResults + "' groups with filter '" + filter + "'");
        }
        
        Collection<Role> roles = filterPrincipals(filter, maxResults, allRoles(), false);
        return roles.toArray(new Role[roles.size()]);
    }
    
    public final int getMaxRoleResults() {
        return Property.getPropertyInt(new SystemConfigKey("ui.maxrole.count"));
    }
    
    private static <T extends Principal> Collection<T> filterPrincipals(String filter, int maxResults, Iterable<T> itr, boolean caseSensitive) {
        Collection<T> principals = new ArrayList<T>();
        String regex = Util.parseSimplePatternToRegExp(filter);
        int patternFlags = caseSensitive ? 0 : Pattern.CASE_INSENSITIVE;
        Pattern pattern = Pattern.compile(regex, patternFlags);
        
        for (T principal : itr) {
            if (principals.size() < maxResults) {
                boolean matches = pattern.matcher(principal.getPrincipalName()).matches();
                if (matches) {
                    principals.add(principal);
                }
            } else {
                break;
            }
        }
        return principals;
    }

    public final User[] getUsersInRole(Role role) throws UserDatabaseException {
        Collection<User> usersWithRole = new ArrayList<User>();
        for (User user : allUsers()) {
            if (user.memberOf(role)) {
                usersWithRole.add(user);
            }
        }
        return (User[]) usersWithRole.toArray(new User[usersWithRole.size()]);
    }
    
    protected static <T> Iterable<T> toIterable(final Iterator<T> itr) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return itr;
            }
        };
    }
    
    /*
     * (non-Javadoc)
     * @see com.ovpnals.core.Database#cleanup()
     */
    public void cleanup() throws Exception {
    }

    /*
     * (non-Javadoc)
     * @see com.ovpnals.security.UserDatabase#getRealm()
     */
    public Realm getRealm() {
        return realm;
    }

    /*
     * (non-Javadoc)
     * @see com.ovpnals.security.UserDatabase#close()
     */
    public void close() throws Exception {
        this.open = false;
        LOG.info("Closing user database " + getClass().getName() + " for realm " + realm.getResourceName());
    }

    /*
     * (non-Javadoc)
     * @see com.ovpnals.security.UserDatabase#isOpen()
     */
    public boolean isOpen() {
        return open;
    }
}