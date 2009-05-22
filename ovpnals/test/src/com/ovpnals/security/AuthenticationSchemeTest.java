
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.ovpnals.boot.PropertyList;
import com.ovpnals.policyframework.AccessRights;
import com.ovpnals.policyframework.Policy;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.policyframework.PolicyDatabaseFactory;
import com.ovpnals.policyframework.ResourceType;
import com.ovpnals.realms.Realm;
import com.ovpnals.testcontainer.policyframework.AbstractTestPolicyEnabledResource;

/**
 */
public class AuthenticationSchemeTest extends AbstractTestPolicyEnabledResource<AuthenticationScheme> {
    /**
     * @throws Exception
     */
    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        setUp("");
    }
    
    @Override
    public ResourceType getResourceType() throws Exception {
      return PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE;
    }

    @Override
    public AuthenticationScheme getEmptyResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new DefaultAuthenticationScheme(-1, -1, "", "", calendar, calendar, true, 0);
    }

    @Override
    public AuthenticationScheme getNormalResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new DefaultAuthenticationScheme(getDefaultRealm().getRealmID(), -1, "resourceName", "resourceDescription", calendar, calendar, true, 0);
    }

    @Override
    public AuthenticationScheme getNullResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new DefaultAuthenticationScheme(-1, -1, null, null, calendar, calendar, true, 0);
    }

    @Override
    public AuthenticationScheme createResource(AuthenticationScheme resource) throws Exception {
        return SystemDatabaseFactory.getInstance().createAuthenticationSchemeSequence(resource.getRealmID(), resource.getResourceName(), resource.getResourceDescription(), resource.getModules(), resource.getEnabled(), resource.getPriorityInt());
    }

    @Override
    public AuthenticationScheme updateResource(AuthenticationScheme resource) throws Exception {
        SystemDatabaseFactory.getInstance().updateAuthenticationSchemeSequence((AuthenticationScheme)resource);
        return SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequence(resource.getResourceId());
    }
    
    @Override
    public AuthenticationScheme deleteResource(AuthenticationScheme resource) throws Exception {
        AuthenticationScheme authenticationScheme = getResource(resource);
        SystemDatabaseFactory.getInstance().deleteAuthenticationSchemeSequence(resource.getResourceId());
        return authenticationScheme;
    }

    @Override
    public AuthenticationScheme getResource(AuthenticationScheme resource) throws Exception {
        return SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequence(resource.getResourceId());
    }
    
    @Override
    public List<AuthenticationScheme> getAllResources() throws Exception {
        return SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequences();
    }
    
    @Test
    public void checkResourceAccessRights() throws Exception {
        Realm realm = getUserService().getRealm(1);
        Policy policy = createPolicy(realm);
        User user = createAccount();
        getPolicyService().grantPolicyToPrincipal(policy, user);
        
        AccessRights accessRights = createAssignAccessRights(getResourceType().getPermissionClass());
        PropertyList selectedPolicies = PropertyList.createFromArray(new int[] {policy.getResourceId()});
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(accessRights, selectedPolicies, getSessionInfo());
        AuthenticationScheme resource = createResource();
        assertTrue("Should be, as one already exists.", getPolicyService().isPrincipalGrantedResourcesOfType(user, resource.getResourceType(), null));
        assertEquals("Should be four.", getPolicyService().getGrantedResourcesOfType(user, getResourceType()).size(), 4);
        
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(resource, selectedPolicies, getSessionInfo());
        assertEquals("Should be five.", getPolicyService().getGrantedResourcesOfType(user, getResourceType()).size(), 5);
        
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(resource, getSessionInfo());
        assertEquals("Should be four.", getPolicyService().getGrantedResourcesOfType(user, getResourceType()).size(), 4);
        getPolicyService().revokePolicyFromPrincipal(policy, user);
        getUserService().getDefaultUserDatabase().deleteAccount(user);
        
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(accessRights, getSessionInfo());
        deleteResource(resource);
        getPolicyService().deleteAccessRights(accessRights.getResourceId());
        getPolicyService().deletePolicy(policy.getResourceId());
    }
    
    /**
     * We should not be able to create two authentication scheme with the same priority.
     * However the database doesn't forbidde that at the moment.
     * @throws Exception
     */
    @Test
    public void createAuthSchWithSamePriority() throws Exception {
        Calendar calendar = Calendar.getInstance();
        AuthenticationScheme authenticationScheme = new DefaultAuthenticationScheme(getDefaultRealm().getRealmID(), -1, "resourceName", "resourceDescription", calendar, calendar, true, 1);
        AuthenticationScheme createdAuthenticationScheme = createResource(authenticationScheme);
        assertEquals("There should be only one AuthenticationScheme", 1, getAllResources().size());
        AuthenticationScheme authenticationScheme2 = new DefaultAuthenticationScheme(getDefaultRealm().getRealmID(), -1, "resourceName2", "resourceDescription2", calendar, calendar, true, 1);
        AuthenticationScheme createdAuthenticationScheme2 = createResource(authenticationScheme2);
        assertEquals("There should be two AuthenticationScheme", 2, getAllResources().size());
        deleteResource(createdAuthenticationScheme);
        deleteResource(createdAuthenticationScheme2);
        assertTrue("Should have none", getAllResources().isEmpty());
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void addModulesToAuth() throws Exception {
        AuthenticationScheme authenticationScheme = (DefaultAuthenticationScheme)createResource();
        assertEquals("There should be only one AuthenticationScheme", 1, getAllResources().size());
        
        for (Iterator ite = AuthenticationModuleManager.getInstance().authenticationModuleDefinitions(); ite.hasNext();) {
            AuthenticationModuleDefinition definition = (AuthenticationModuleDefinition)ite.next();
            authenticationScheme.addModule(definition.getName());
        }
        updateResource(authenticationScheme);
        deleteResource(authenticationScheme);
        assertTrue("Should have none", getAllResources().isEmpty());
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void deleteModulesFromAuth() throws Exception {
        AuthenticationScheme authenticationScheme = (DefaultAuthenticationScheme)createResource();
        assertEquals("There should be only one AuthenticationScheme", 1, getAllResources().size());
        
        for (Iterator ite = AuthenticationModuleManager.getInstance().authenticationModuleDefinitions(); ite.hasNext();) {
            AuthenticationModuleDefinition definition = (AuthenticationModuleDefinition)ite.next();
            authenticationScheme.removeModule(definition.getName());
        }
        updateResource(authenticationScheme);
        deleteResource(authenticationScheme);
        assertTrue("Should have none", getAllResources().isEmpty());
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void registerDeregisterModule() throws Exception {
        String module = "EmbeddedClientTest";
        AuthenticationModuleManager.getInstance().registerModule(module, EmbeddedClientAuthenticationModule.class, "security", true, false, true);
        assertTrue("This module should be registered", AuthenticationModuleManager.getInstance().isRegistered(module));
        AuthenticationModuleManager.getInstance().deregisterModule(module);
        assertTrue("This module should not be registered", !AuthenticationModuleManager.getInstance().isRegistered(module));
    }
    
    /**
     * @throws Exception
     */
    @Ignore ("This test should be able to run but there is no control at the moment.")
    @Test
    public void deleteAllAuthenticationScheme() throws Exception {
        List listAuth = SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequences();
        for (Iterator ite = listAuth.iterator(); ite.hasNext();) {
            AuthenticationScheme auth = (AuthenticationScheme)ite.next();
            SystemDatabaseFactory.getInstance().deleteAuthenticationSchemeSequence(auth.getResourceId());
    }
       assertEquals("Should have none", 0, SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequences().size());
    }
}