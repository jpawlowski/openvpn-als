
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
			
package com.adito.security;

import com.adito.core.CoreServlet;
import com.adito.core.Database;
import com.adito.realms.Realm;

/**
 * <p>Implementations of this interface will provide basic user and role related
 * services such as retrieving a user account, retrieving a role, authenticating
 * a user etc.
 * 
 * <p>Some implementations will now support account creation or password
 * changing and as such should return appropriate values for {@link #supportsAccountCreation()}
 * and {@link #supportsPasswordChange()}.
 */
public interface UserDatabase extends Database {
    /**
     */
    String WILDCARD_SEARCH = "*";

    /**
     * Get a readable description of the database.
     * 
     * @return description
     */
    public String getDatabaseDescription();

    /**
     * Get if the database is currently 'open'.
     * 
     * @return open
     */
    public boolean isOpen();

    /**
     * Authenticates the given username/password pair, returning the user object
     * on success or <tt>null</tt> on failure.
     * 
     * @param username
     * @param password
     * @return user object
     * @throws UserDatabaseException
     * @throws InvalidLoginCredentialsException
     * @throws AccountLockedException
     */
    public User logon(String username, String password) throws UserDatabaseException, InvalidLoginCredentialsException,
                    AccountLockedException;

    /**
     * Check the given username/password pair but do not actually logon.
     * <tt>true</tt> is returned on success and <tt>false</tt> on failure.
     * 
     * @param username
     * @param password
     * @return password ok
     * @throws UserDatabaseException
     * @throws InvalidLoginCredentialsException
     */
    public boolean checkPassword(String username, String password) throws UserDatabaseException, InvalidLoginCredentialsException;

    /**
     * Change your password. This method is used by a user to change their own passwords. It
     * is recommended that implementations verify that the old password is correct.
     * 
     * @param username username
     * @param oldPassword 
     * @param password new password
     * @param forcePasswordChangeAtLogon force password change at next logon
     * @throws UserDatabaseException
     * @throws InvalidLoginCredentialsException
     */
    public void changePassword(String username, String oldPassword, String password, boolean forcePasswordChangeAtLogon) throws UserDatabaseException,
                    InvalidLoginCredentialsException;

    /**
     * Set a users password. This is used from the admin pages and requires admin user and password
     * to complete.
     * @param username
     * @param password
     * @param forcePasswordChangeAtLogon
     * @param adminUser
     * @param adminPassword
     * @throws UserDatabaseException
     * @throws InvalidLoginCredentialsException
     */
    public void setPassword(String username, String password, boolean forcePasswordChangeAtLogon, User adminUser, String adminPassword) throws UserDatabaseException, InvalidLoginCredentialsException;
    
    /**
     * Get if this implementation supports changine of passwords
     * 
     * @return password change supported
     */
    public boolean supportsPasswordChange();

    /**
     * Logout a user.
     * 
     * @param user
     */
    public void logout(User user);

    /**
     * @return
     * @throws UserDatabaseException
     */
    Iterable<User> allUsers() throws UserDatabaseException;
    
    /**
     * List all the users currently registered with the system. This is the list
     * of all users rather than those that are granted access
     * 
     * @param filter filter a filter to apply to the search
     * @param maxResults 
     * @return an array of {@link User}s
     * @throws UserDatabaseException
     */
    User[] listAllUsers(String filter, int maxResults) throws UserDatabaseException;

    /**
     * The number of users retrieved and displayed for the provided search
     * criteria.
     * 
     * @return maximum number of users
     */
    int getMaxUserResults();
    
    /**
     * Does the username already belong to another user?
     * 
     * @param username
     * @return <tt>true</tt> if an account already matches the supplied
     *         username.
     * @throws UserDatabaseException
     */
    boolean isAccountNameInUse(String username) throws UserDatabaseException;


    /**
     * Get the account details that belong to the given username.
     * 
     * @param username
     * @return user
     * @throws UserNotFoundException if the user could not be found
     * @throws Exception on all other errors
     */
    public User getAccount(String username) throws UserNotFoundException, Exception;

    /**
     * Identify whether this implementation supports the creation of user
     * accounts.
     * 
     * @return <tt>true</tt> if account creation is supported, otherwise
     *         <tt>false</tt>.
     */
    public boolean supportsAccountCreation();

    /**
     * Create a new {@link User}account. This method is optional and should
     * only work when {@link #supportsAccountCreation()} returns <tt>true</tt>.
     * 
     * @param username username
     * @param password password
     * @param email email address
     * @param fullname full name
     * @param roles array of roles
     * @return user user object
     * @throws Exception on any error
     */
    public User createAccount(String username, String password, String email, String fullname, Role[] roles) throws Exception;

    /**
     * Update the details of a {@link User}account. This method is optional and
     * should only work when {@link #supportsAccountCreation()} returns
     * <tt>true</tt>.#
     * 
     * @param user
     * @param email
     * @param fullname
     * @param roles
     * @throws Exception
     */
    public void updateAccount(User user, String email, String fullname, Role[] roles) throws Exception;

    /**
     * Delete a {@link User} account. This method is optional and should only
     * work when {@link #supportsAccountCreation()} returns <tt>true</tt>.
     * 
     * @param user
     * @throws Exception
     * @throws UserNotFoundException 
     */
    public void deleteAccount(User user) throws Exception, UserNotFoundException;


    /**
     * Get a single role given its name
     * 
     * @param rolename role name
     * @return role
     * @throws Exception on any error
     */
    public Role getRole(String rolename) throws Exception;

    /**
     * @return
     * @throws UserDatabaseException 
     */
    Iterable<Role> allRoles() throws UserDatabaseException;
    
    /**
     * List all available roles
     * 
     * @param filter filter
     * @param maxResults 
     * @return array of roles
     * @throws UserDatabaseException
     */
    Role[] listAllRoles(String filter, int maxResults) throws UserDatabaseException;

    /**
     * The number of roles retrieved and displayed for the provided search
     * criteria.
     * 
     * @return maximum number of roles
     */
     int getMaxRoleResults();
    
    /**
     * Does the rolename already belong to another role?
     * 
     * @param rolename
     * @return <tt>true</tt> if a role already matches the supplied
     *         rolename.
     * @throws UserDatabaseException
     */
    boolean isRoleNameInUse(String rolename) throws UserDatabaseException;
    

    /**
     * Create a new role if the underlying database supports it.
     * 
     * @param rolename role name
     * @return role object
     * @throws Exception on any error
     */
    public Role createRole(String rolename) throws Exception;

    /**
     * Delete a new role
     * 
     * @param rolename role name
     * @throws Exception on any error
     */
    public void deleteRole(String rolename) throws Exception;
    
    /**
     * Get the a list of {@link com.adito.security.User}s that are in
     * a specified role
     * 
     * @param role role
     * @return users in role
     * @throws Exception on any error
     */
    public User[] getUsersInRole(Role role) throws Exception;
    
    /**
     * @return Realm
     */
    public Realm getRealm();

    /**
     * @param controllingServlet
     * @param realm
     * @throws Exception 
     */
    public void open(CoreServlet controllingServlet, Realm realm) throws Exception;
}