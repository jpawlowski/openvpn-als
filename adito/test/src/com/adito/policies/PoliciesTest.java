
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
import static org.junit.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Test;

import com.adito.core.UserDatabaseManager;
import com.adito.policyframework.Policy;
import com.adito.realms.Realm;
import com.adito.testcontainer.AbstractTest;

/**
 */
public class PoliciesTest extends AbstractTest {
    
    
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
    public void createPolicy() throws Exception {
        Realm realm = getUserService().getRealm(1);
        assertEquals("There should be only one policy", getPolicyService().getPolicies().size(), 1);
        Policy policy = getPolicyService().createPolicy("Policy A", "Policy A description", Policy.TYPE_NORMAL, realm.getRealmID());
        assertEquals("There should be only two policies", getPolicyService().getPolicies().size(), 2);
        getPolicyService().deletePolicy(policy.getResourceId());
        assertEquals("There should be only one policy", getPolicyService().getPolicies().size(), 1);
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void createPersonalPolicy() throws Exception {
        Realm realm = getUserService().getDefaultRealm();
        assertEquals("There should be only one policy (Everyone policy)", getPolicyService().getPoliciesExcludePersonal(realm), getPolicyService().getPolicies(realm));
        Policy policy = getPolicyService().createPolicy("Personal Policy A", "Personal Policy A description", Policy.TYPE_PERSONAL, realm.getRealmID());
        assertEquals("There should be two policies", 2, getPolicyService().getPolicies().size());
        assertEquals("There should be only one global policy", 1, getPolicyService().getPoliciesExcludePersonal(realm).size());
        getPolicyService().deletePolicy(policy.getResourceId());
        assertEquals("There should be only one policy (Everyone policy)", getPolicyService().getPoliciesExcludePersonal(realm), getPolicyService().getPolicies(realm));
    }

    /**
     * @throws Exception
     */
    @Test
    public void updatePolicyName() throws Exception {
        String newPolicyName = "NewName";
        Realm realm = getUserService().getRealm(1);
        Policy policy = createPolicy(realm);
        policy.setResourceName(newPolicyName);
        getPolicyService().updatePolicy(policy);
        Policy updatedPolicy = getPolicyService().getPolicy(policy.getResourceId());
        assertEquals("The new policy name should be " + newPolicyName, newPolicyName, updatedPolicy.getResourceName());
        getPolicyService().deletePolicy(policy.getResourceId());
    }

    /**
     * @throws Exception
     */
    @Test
    public void updatePolicyDescription() throws Exception {
        String newPolicyDescription = "NewDescription";
        Realm realm = getUserService().getRealm(1);
        Policy policy = createPolicy(realm);
        policy.setResourceDescription(newPolicyDescription);
        getPolicyService().updatePolicy(policy);
        Policy updatedPolicy = getPolicyService().getPolicy(policy.getResourceId());
        assertEquals("The new policy description should be " + newPolicyDescription, newPolicyDescription, updatedPolicy.getResourceDescription());
        getPolicyService().deletePolicy(policy.getResourceId());
    }

    /**
     * @throws Exception
     */
    @Test
    public void checkPolicyRetrieval() throws Exception {
        Realm realm = getUserService().getRealm(1);
        Policy policy = createPolicy(realm);
        Policy retrievedById = getPolicyService().getPolicy(policy.getResourceId());
        Policy retrievedByName = getPolicyService().getPolicyByName(policy.getResourceName(), realm.getResourceId());
        assertEquals("The policies should be the same.", retrievedById, retrievedByName);
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