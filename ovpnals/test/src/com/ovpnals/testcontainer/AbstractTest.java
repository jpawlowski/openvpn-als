
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
			
package com.ovpnals.testcontainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.InetAddress;
import java.util.Collection;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Test;

import com.ovpnals.boot.Context;
import com.ovpnals.core.UserDatabaseManager;
import com.ovpnals.policyframework.Policy;
import com.ovpnals.policyframework.PolicyDatabase;
import com.ovpnals.policyframework.PolicyDatabaseFactory;
import com.ovpnals.policyframework.PolicyUtil;
import com.ovpnals.properties.Property;
import com.ovpnals.properties.impl.realms.RealmKey;
import com.ovpnals.realms.Realm;
import com.ovpnals.security.Role;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.User;
import com.ovpnals.security.UserDatabase;

/**
 */
public abstract class AbstractTest{
    private static final String TEST_PROPERTY = "ovpnals.testing";
    private static final String DEV_CONFIG_PROPERTY = "ovpnals.useDevConfig";
    private static final String DEV_EXTENSIONS_PROPERTY = "ovpnals.devExtensions";
    private static final String DATABASE_TYPE = "builtIn";
    protected static String USERNAME = "testAdministrator";
    protected static String PASSWORD = "newPassword";
    private static Context context_;

    /**
     * @param pluginName
     * @throws Exception
     */
    public static void setUp(String pluginName) throws Exception{
        deleteDatabase();
        System.setProperty(TEST_PROPERTY, "true");
        System.setProperty(DEV_CONFIG_PROPERTY, "true");
        System.setProperty(DEV_EXTENSIONS_PROPERTY, pluginName);
        context_ = TestContext.getTestContext();
        createSuperUser();
        assertPolicies();
        // is there a super user and can login?
        PolicyUtil.checkLogin(getAccount(USERNAME));
    }

    private static void createSuperUser() throws Exception {
        UserDatabase userDatabase = getUserService().createUserDatabase(DATABASE_TYPE, UserDatabaseManager.DEFAULT_REALM_NAME, UserDatabaseManager.DEFAULT_REALM_DESCRIPTION, true);
        User user = userDatabase.createAccount(USERNAME, "", "", "", new Role[] {});
        userDatabase.changePassword(user.getPrincipalName(), "", PASSWORD, false);

        Property.setProperty(new RealmKey("security.userDatabase", userDatabase.getRealm()), DATABASE_TYPE, getSessionInfo());
        Property.setProperty(new RealmKey("security.administrators", userDatabase.getRealm()), USERNAME, getSessionInfo());
    }

    private static void assertPolicies() throws Exception {
        List<Policy> policies = PolicyDatabaseFactory.getInstance().getPolicies();
        assertEquals("There should be only one policy", policies.size(), 1);
        Policy policy = policies.get(0);
        assertEquals("The policy should be called 'Everyone'", policy.getResourceName(), "Everyone");
        assertEquals("The policy id should be '0'", policy.getResourceId(), 0);
    }
    
    /**
     * 
     */
    @AfterClass
    public static void oneTimeTearDown() {
        if (context_ != null){
            context_.shutdown(false);
        }
        deleteDatabase();
    }

    /**
     * This can be called from anywhere as long as the TestContext has been initialised. 
     */
    private static void deleteDatabase() {
        for (File file : TestContext.DB_DIR.listFiles()) {
            file.delete();
        }
    }

    /**
     * @return SessionInfo
     * @throws Exception
     */
    protected static SessionInfo getSessionInfo() throws Exception {
        User account = getAccount(USERNAME);
        // the super user should exist, the name used is the name use
        return SessionInfo.nextSession(null, "testPolicyAdmin", account, InetAddress.getLocalHost(), SessionInfo.ALL_CONTEXTS, "");
    }
    
    /**
     * @return UserDatabase
     * @throws Exception
     */
    public static UserDatabase getDefaultUserDatabase() throws Exception {
        return getUserService().getDefaultUserDatabase();
    }
    
    /**
     * @return Realm
     * @throws Exception
     */
    public static Realm getDefaultRealm() throws Exception {
        return getUserService().getDefaultRealm();
    }
    
    /**
     * @return UserDatabaseManager
     * @throws Exception
     */
    public static UserDatabaseManager getUserService() throws Exception {
        return UserDatabaseManager.getInstance();
    }
    
    /**
     * @param username
     * @return User
     * @throws Exception
     */
    public static User getAccount(String username) throws Exception {
        return getDefaultUserDatabase().getAccount(username);
    }
    
    /**
     * @return User
     * @throws Exception
     */
    public static User createAccount() throws Exception {
        return createAccount("username", "password", "username@company.com", "Full Name");
    }
    
    /**
     * @param username
     * @param password
     * @param email
     * @param fullname
     * @return User
     * @throws Exception
     */
    public static User createAccount(String username, String password, String email, String fullname) throws Exception {
        return getDefaultUserDatabase().createAccount(username, password, email, fullname, new Role[] {});
    }

    /**
     * @param user
     * @param roles
     * @return User
     * @throws Exception
     */
    public static User updateAccountRoles (User user, Collection<Role> roles) throws Exception {
        return updateAccount(user, user.getEmail(), user.getFullname(), roles.toArray(new Role[roles.size()]));
    }
    
    /**
     * @param user
     * @param email
     * @param fullname
     * @param roles
     * @return User
     * @throws Exception
     */
    public static User updateAccount(User user, String email, String fullname, Role[] roles) throws Exception {
        getDefaultUserDatabase().updateAccount(user, email, fullname, roles);
        return getAccount(user.getPrincipalName());
    }

    /**
     * @param users
     * @throws Exception
     */
    public static void deleteAccount(User... users) throws Exception {
        for (User user : users) {
            getDefaultUserDatabase().deleteAccount(user);
        }
    }

    /**
     * @return Role
     * @throws Exception
     */
    public static Role createRole() throws Exception {
        return createRole("testRole");
    }
    
    /**
     * @param rolename
     * @return Role
     * @throws Exception
     */
    public static Role createRole(String rolename) throws Exception {
        return getDefaultUserDatabase().createRole(rolename);
    }
    
    /**
     * @param roles
     * @throws Exception
     */
    public static void deleteRole(Role...roles) throws Exception {
        for (Role role : roles) {
            getDefaultUserDatabase().deleteRole(role.getPrincipalName());
        }
    }
    
    /**
     * @return PolicyDatabase
     * @throws Exception
     */
    public static PolicyDatabase getPolicyService() throws Exception {
        return PolicyDatabaseFactory.getInstance();
    }

    /**
     */
    @Test
    public void finalTestWhichFailsDueToBadTearDown() {
        assertTrue(true);
    }
}