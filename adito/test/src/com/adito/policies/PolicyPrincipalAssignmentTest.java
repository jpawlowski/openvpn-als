
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
			
package com.adito.policies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.BeforeClass;
import org.junit.Test;

import com.adito.core.UserDatabaseManager;
import com.adito.policyframework.Policy;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.realms.Realm;
import com.adito.security.Role;
import com.adito.security.User;
import com.adito.security.UserDatabase;
import com.adito.testcontainer.AbstractTest;

/**
 */
public class PolicyPrincipalAssignmentTest extends AbstractTest {

    /**
     * @throws Exception
     */
    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        setUp("");
    }

    /**
     * @throws Exception
     */
    @Test
    public void attachDetachPolicyToUser() throws Exception {
        Realm realm = getUserService().getDefaultRealm();
        Policy policy = createPolicy(realm);
        int users = getUserService().getDefaultUserDatabase().listAllUsers(UserDatabase.WILDCARD_SEARCH, Integer.MAX_VALUE).length;
        User user = createAccount();
        assertEquals(
            getUserService().getDefaultUserDatabase().listAllUsers(UserDatabase.WILDCARD_SEARCH, Integer.MAX_VALUE).length,
            users + 1);
        getPolicyService().grantPolicyToPrincipal(policy, user);
        assertTrue("The policy should be granted", PolicyDatabaseFactory.getInstance().isPolicyGrantedToUser(policy, user));
        getPolicyService().revokePolicyFromPrincipal(policy, user);
        assertFalse("The policy should not be granted", PolicyDatabaseFactory.getInstance().isPolicyGrantedToUser(policy, user));
        getUserService().getDefaultUserDatabase().deleteAccount(user);
        assertEquals(
            getUserService().getDefaultUserDatabase().listAllUsers(UserDatabase.WILDCARD_SEARCH, Integer.MAX_VALUE).length, users);
        getPolicyService().deletePolicy(policy.getResourceId());
    }

    /**
     * @throws Exception
     */
    @Test
    public void attachDetachPolicyToRole() throws Exception {
        Realm realm = getUserService().getDefaultRealm();
        Policy policy = createPolicy(realm);
        User user = createAccount();
        Role role = createRole("Group1");
        user = updateAccountRoles(user, Collections.singleton(role));
        getPolicyService().grantPolicyToPrincipal(policy, role);
        assertTrue("The policy should be granted", PolicyDatabaseFactory.getInstance().isPolicyGrantedToUser(policy, user));
        getPolicyService().revokePolicyFromPrincipal(policy, role);
        assertFalse("The policy should not be granted", PolicyDatabaseFactory.getInstance().isPolicyGrantedToUser(policy, user));
        getUserService().getDefaultUserDatabase().deleteAccount(user);
        getPolicyService().deletePolicy(policy.getResourceId());
    }

    /**
     * @return UserDatabaseManager
     * @throws Exception
     */
    public static UserDatabaseManager getUserService() throws Exception {
        return UserDatabaseManager.getInstance();
    }

    private static Policy createPolicy(Realm realm) throws Exception {
        return createPolicy("Policy A", "Policy A description", Policy.TYPE_NORMAL, realm);
    }

    private static Policy createPolicy(String name, String description, int type, Realm realm) throws Exception {
        return getPolicyService().createPolicy(name, description, type, realm.getRealmID());
    }
}