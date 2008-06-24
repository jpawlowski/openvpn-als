
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
			
package com.adito.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.adito.security.Role;
import com.adito.security.User;
import com.adito.security.UserDatabase;
import com.adito.security.UserNotFoundException;
import com.adito.testcontainer.AbstractTest;

/**
 * Test the built in database.
 */
public class JDBCUserDatabaseTest extends AbstractTest {

    /**
     * @throws Exception
     */
    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        setUp("");
    }

    /**
     * Ensure that only the super user exists.
     * 
     * @throws Exception
     */
    @Test
    public void checkInitialState() throws Exception {
        User[] listAllUsers = getDefaultUserDatabase().listAllUsers(UserDatabase.WILDCARD_SEARCH, Integer.MAX_VALUE);
        assertEquals("There should only be the one user and he is the super user.", listAllUsers.length, 1);
    }

    /**
     * Create and delete a single user, ensuring that
     * 
     * @throws Exception
     */
    @Test
    public void simpleCreateUser() throws Exception {
        User user = createAccount("jb", "qwqwqw", "james@localhost", "James Robinson");
        assertEquals("There should only be the one user and he is the super user.", getDefaultUserDatabase().listAllUsers(
            UserDatabase.WILDCARD_SEARCH, Integer.MAX_VALUE).length, 2);
        deleteAccount(user);
        assertEquals("There should only be the one user and he is the super user.", getDefaultUserDatabase().listAllUsers(
            UserDatabase.WILDCARD_SEARCH, Integer.MAX_VALUE).length, 1);
    }

    /**
     * Create a number of users
     * 
     * @throws Exception
     */
    @Test
    public void createNUsers() throws Exception {
        User user1 = createAccount("jb1", "qwqwqw", "james@localhost", "James Robinson1");
        User user2 = createAccount("jb2", "qwqwqw", "james@localhost", "James Robinson2");
        User user3 = createAccount("jb3", "qwqwqw", "james@localhost", "James Robinson3");
        assertEquals("There should only be the one user and he is the super user.", getDefaultUserDatabase().listAllUsers(
            UserDatabase.WILDCARD_SEARCH, Integer.MAX_VALUE).length, 4);
        deleteAccount(user1, user2, user3);
        assertEquals("There should only be the one user and he is the super user.", getDefaultUserDatabase().listAllUsers(
            UserDatabase.WILDCARD_SEARCH, Integer.MAX_VALUE).length, 1);
    }

    /**
     * Login succesfull
     * 
     * @throws Exception
     */
    @Test
    public void loginSuccessfull() throws Exception {
        String username = "jb";
        String password = "qwqwqw";
        User user = createAccount(username, password, "james@localhost", "James Robinson");
        User adminUser = getDefaultUserDatabase().getAccount(USERNAME);
        getDefaultUserDatabase().setPassword(user.getPrincipalName(), password, false, adminUser, PASSWORD);
        User loggedONUser = getDefaultUserDatabase().logon(username, password);
        assertNotNull("There should be a valid user.", loggedONUser);
        deleteAccount(user);
    }

    /**
     * Check the passowrd
     * 
     * @throws Exception
     */
    @Test
    public void checkPassword() throws Exception {
        String username = "jb";
        String password = "qwqwqw";
        User user = createAccount(username, password, "james@localhost", "James Robinson");
        User adminUser = getDefaultUserDatabase().getAccount(USERNAME);
        getDefaultUserDatabase().setPassword(user.getPrincipalName(), password, false, adminUser, PASSWORD);
        assertTrue("There password should be checked successfully.", getDefaultUserDatabase().checkPassword(username, password));
        deleteAccount(user);
    }

    /**
     * Login failed
     * 
     * @throws Exception
     */
    @Test
    public void loginFailed() throws Exception {
        String username = "jb";
        String password = "qwqwqw";
        User user = createAccount(username, password, "james@localhost", "James Robinson");
        User adminUser = getDefaultUserDatabase().getAccount(USERNAME);
        getDefaultUserDatabase().setPassword(user.getPrincipalName(), password, false, adminUser, PASSWORD);

        try {
            getDefaultUserDatabase().logon("wrong", password);
            // should never be reached
            fail();
        } catch (Exception e) {
            assertNotNull("An exception should have been thrown", e);
        }
        deleteAccount(user);
    }

    /**
     * Check the passowrd, using a bad passowrd
     * 
     * @throws Exception
     */
    @Test
    public void checkBadPassword() throws Exception {
        String username = "jb";
        String password = "qwqwqw";
        User user = createAccount(username, password, "james@localhost", "James Robinson");
        User adminUser = getDefaultUserDatabase().getAccount(USERNAME);
        getDefaultUserDatabase().setPassword(user.getPrincipalName(), password, false, adminUser, PASSWORD);
        assertFalse("There password should be wrong.", getDefaultUserDatabase().checkPassword(username, "pileof"));
        deleteAccount(user);
    }

    /**
     * Change e-mail address
     * 
     * @throws Exception
     */
    @Test
    public void updateUserChangeEmail() throws Exception {
        String email = "james@localhost";
        String username = "jb";
        User user = createAccount(username, "qwqwqw", email, "James Robinson");
        assertTrue("The email should be set.", user.getEmail().equals(email));
        String newEmail = "jb@localhost";
        updateAccount(user, newEmail, user.getFullname(), user.getRoles());
        user = getAccount(username);
        assertTrue("The email should be set.", user.getEmail().equals(newEmail));
        deleteAccount(user);
    }

    /**
     * Can't change the username
     * 
     * @throws Exception
     */
    @Test
    public void cantChangeUsername() throws Exception {
        String username = "jb";
        User user = createAccount(username, "qwqwqw", "ja,es@localhost", "James Robninson");
        assertTrue("The user name should be set.", user.getPrincipalName().equals(username));
        String newUsername = "jimbob";
        updateAccount(user, user.getFullname(), user.getEmail(), user.getRoles());
        user = getAccount(username);
        assertTrue("The user name should be set.", !user.getPrincipalName().equals(newUsername));
        assertTrue("The user name should be set.", user.getPrincipalName().equals(username));
        deleteAccount(user);
    }

    /**
     * Change full name
     * 
     * @throws Exception
     */
    @Test
    public void updateUserChangeFullName() throws Exception {
        String fullName = "James Robninson";
        String username = "jb";
        User user = createAccount(username, "qwqwqw", "ja,es@localhost", fullName);
        assertTrue("The full name should be set.", user.getFullname().equals(fullName));
        String newFullName = "James Douglas Robinson";
        updateAccount(user, user.getFullname(), newFullName, user.getRoles());
        user = getAccount(username);
        assertTrue("The full name should be set.", user.getFullname().equals(newFullName));
        deleteAccount(user);
    }

    /**
     * Delete an unknown user and ensure an exception is thrown
     * 
     * @throws Exception
     */
    @Test(expected = UserNotFoundException.class)
    public void deleteUnknownUser() throws Exception {
        String fullName = "James Robninson";
        String username = "jb";
        User user = createAccount(username, "qwqwqw", "ja,es@localhost", fullName);
        user = getAccount(username);
        deleteAccount(user, user);
        fail("Delete user should have thrown an exception");
    }

    /**
     * Create a role
     * 
     * @throws Exception
     */
    @Test
    public void createNormalRole() throws Exception {
        Role[] currentRoles = getDefaultUserDatabase().listAllRoles(UserDatabase.WILDCARD_SEARCH, Integer.MAX_VALUE);
        int currentNumberOfRoles = currentRoles.length;
        String roleName = "jb";
        Role role = createRole(roleName);
        assertEquals("There should only be the one user and he is the super user.", getDefaultUserDatabase().listAllRoles(
            UserDatabase.WILDCARD_SEARCH, Integer.MAX_VALUE).length, currentNumberOfRoles + 1);
        deleteRole(role);
        assertEquals("There should only be the one user and he is the super user.", getDefaultUserDatabase().listAllRoles(
            UserDatabase.WILDCARD_SEARCH, Integer.MAX_VALUE).length, currentNumberOfRoles);
    }

    /**
     * Assign a user to a role and unassign, ensuring that the user is in the
     * role.
     * 
     * @throws Exception
     */
    @Test
    public void assignRolesToUser() throws Exception {
        String userName = "jb";
        Role role = createRole("Group1");
        User user = createAccount(userName, "qwqwqw", "james@localhost", "James Robninson");
        user = updateAccountRoles(user, Collections.singleton(role));
        assertEquals("The roles should be the same.", role.getPrincipalName(), user.getRoles()[0].getPrincipalName());
        user = updateAccountRoles(user, Collections.<Role> emptyList());
        User[] usersInRole = getUserService().getDefaultUserDatabase().getUsersInRole(role);
        assertEquals("There should be the user in the list.", usersInRole.length, 0);
        User retrievedUser2 = getUserService().getDefaultUserDatabase().getAccount(user.getPrincipalName());
        List<Role> usersRoles2 = Arrays.asList(retrievedUser2.getRoles());
        assertTrue("The role should not have any users.", usersRoles2.isEmpty());
        deleteAccount(user);
        deleteRole(role);
    }

    /**
     * @throws Exception
     */
    @Test
    public void listingUsers() throws Exception {
        User[] currentUsers = getDefaultUserDatabase().listAllUsers(UserDatabase.WILDCARD_SEARCH, Integer.MAX_VALUE);
        int currentNumberOfUsers = currentUsers.length;
        assertEquals("There should only be the one user and he is the super user.", getDefaultUserDatabase().listAllUsers(
            UserDatabase.WILDCARD_SEARCH, Integer.MAX_VALUE).length, 1);
        // create 6 users
        User user1 = createAccount("aaaa", "aaaa", "aaaa@localhost", "AAAA");
        User user2 = createAccount("abbb", "abbb", "abbb@localhost", "ABBB");
        User user3 = createAccount("aabb", "aabb", "aabb@localhost", "AABB");
        User user4 = createAccount("aaab", "aaab", "aaab@localhost", "AAAB");
        User user5 = createAccount("bbbb", "bbbb", "bbbb@localhost", "BBBB");
        User user6 = createAccount("xaax", "xaax", "xaax@localhost", "XAAX");
        assertEquals("There should be the seven users.", getDefaultUserDatabase().listAllUsers(UserDatabase.WILDCARD_SEARCH,
            Integer.MAX_VALUE).length, currentNumberOfUsers + 6);
        assertEquals("There should be the four users.", getDefaultUserDatabase().listAllUsers("a*", Integer.MAX_VALUE).length, 4);
        assertEquals("There should be the three users.", getDefaultUserDatabase().listAllUsers("aa*", Integer.MAX_VALUE).length, 3);
        assertEquals("There should be the two users.", getDefaultUserDatabase().listAllUsers("aaa*", Integer.MAX_VALUE).length, 2);
        assertEquals("There should be the three users.", getDefaultUserDatabase().listAllUsers("*ab*", Integer.MAX_VALUE).length, 3);
        assertEquals("There should be the zero users.", getDefaultUserDatabase().listAllUsers("*z*", Integer.MAX_VALUE).length, 0);
        assertEquals("There should be the one users.", getDefaultUserDatabase().listAllUsers("xa*", Integer.MAX_VALUE).length, 1);
        deleteAccount(user1, user2, user3, user4, user5, user6);
        assertEquals("There should only be the one user and he is the super user.", getDefaultUserDatabase().listAllUsers(
            UserDatabase.WILDCARD_SEARCH, Integer.MAX_VALUE).length, 1);
    }

    /**
     * @throws Exception
     */
    @Test
    public void listingRoles() throws Exception {
        Role[] currentRoles = getDefaultUserDatabase().listAllRoles(UserDatabase.WILDCARD_SEARCH, Integer.MAX_VALUE);
        int currentNumberOfRoles = currentRoles.length;
        assertEquals("There should only be the 1 role 'Users'.", getDefaultUserDatabase().listAllRoles(
            UserDatabase.WILDCARD_SEARCH, Integer.MAX_VALUE).length, 1);
        // create 6 roles
        Role role1 = createRole("aaaa");
        Role role2 = createRole("abbb");
        Role role3 = createRole("aabb");
        Role role4 = createRole("aaab");
        Role role5 = createRole("bbbb");
        Role role6 = createRole("xaax");
        assertEquals("There should be the seven roles.", getDefaultUserDatabase().listAllRoles(UserDatabase.WILDCARD_SEARCH,
            Integer.MAX_VALUE).length, currentNumberOfRoles + 6);
        assertEquals("There should be the five roles.", getDefaultUserDatabase().listAllRoles("a*", Integer.MAX_VALUE).length, 4);
        assertEquals("There should be the three roles.", getDefaultUserDatabase().listAllRoles("aa*", Integer.MAX_VALUE).length, 3);
        assertEquals("There should be the two roles.", getDefaultUserDatabase().listAllRoles("aaa*", Integer.MAX_VALUE).length, 2);
        assertEquals("There should be the three roles.", getDefaultUserDatabase().listAllRoles("*ab*", Integer.MAX_VALUE).length, 3);
        assertEquals("There should be the zero roles.", getDefaultUserDatabase().listAllRoles("*z*", Integer.MAX_VALUE).length, 0);
        assertEquals("There should be the one roles.", getDefaultUserDatabase().listAllRoles("xa*", Integer.MAX_VALUE).length, 1);
        deleteRole(role1, role2, role3, role4, role5, role6);
        assertEquals("There should only be the 1 role users.", getDefaultUserDatabase().listAllRoles(UserDatabase.WILDCARD_SEARCH,
            Integer.MAX_VALUE).length, 1);
    }
}