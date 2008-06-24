
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
			
package com.adito.testcontainer.policyframework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

import com.adito.boot.PropertyList;
import com.adito.navigation.Favorite;
import com.adito.policyframework.AccessRights;
import com.adito.policyframework.Permission;
import com.adito.policyframework.Policy;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.Resource;
import com.adito.realms.Realm;
import com.adito.security.Role;
import com.adito.security.SystemDatabaseFactory;
import com.adito.security.User;
import com.adito.security.UserDatabase;

/**
 * @param <T> 
 */
public abstract class AbstractTestPolicyEnabledResource<T extends Resource> extends AbstractTestResource<T> {
    /**
     * @throws Exception
     */
    @Test
    public void checkAllAccessRights() throws Exception {
        Realm realm = getDefaultRealm();
        Policy policy = createPolicy(realm);
        User user = createAccount();
        getPolicyService().grantPolicyToPrincipal(policy, user);
        AccessRights accessRights = createAllAccessRights(getResourceType().getPermissionClass());
        PropertyList selectedPolicies = PropertyList.createFromArray(new int[] {policy.getResourceId()});
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(accessRights, selectedPolicies, getSessionInfo());
        
        assertEquals("The newly created and default.", getPolicyService().getPermittingAccessRights(null, null, null, user).size(), getDefaultAccessRightCount() + 1);
        Permission[] permissions = new Permission[] {PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_DELETE};
        assertTrue("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        
        getPolicyService().revokePolicyFromPrincipal(policy, user);
        deleteAccount(user);
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(accessRights, getSessionInfo());
        getPolicyService().deleteAccessRights(accessRights.getResourceId());
        getPolicyService().deletePolicy(policy.getResourceId());
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void checkNoAccessRights() throws Exception {
        Realm realm = getDefaultRealm();
        Policy policy = createPolicy(realm);
        User user = createAccount();
        getPolicyService().grantPolicyToPrincipal(policy, user);
        AccessRights accessRights = createAllAccessRights(getResourceType().getPermissionClass());
        PropertyList selectedPolicies = PropertyList.createFromArray(new int[] {policy.getResourceId()});
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(accessRights, selectedPolicies, getSessionInfo());
        
        Permission[] permissions = new Permission[] {};
        assertFalse("The permissions should not be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        
        getPolicyService().revokePolicyFromPrincipal(policy, user);
        deleteAccount(user);
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(accessRights, getSessionInfo());
        getPolicyService().deleteAccessRights(accessRights.getResourceId());
        getPolicyService().deletePolicy(policy.getResourceId());
    }

    /**
     * @throws Exception
     */
    @Test
    public void checkDeleteAccessRights() throws Exception {
        Realm realm = getDefaultRealm();
        Policy policy = createPolicy(realm);
        User user = createAccount();
        getPolicyService().grantPolicyToPrincipal(policy, user);
        AccessRights accessRights = createDeleteAccessRights(getResourceType().getPermissionClass());
        PropertyList selectedPolicies = PropertyList.createFromArray(new int[] {policy.getResourceId()});
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(accessRights, selectedPolicies, getSessionInfo());
        
        Permission[] permissions = new Permission[] {PolicyConstants.PERM_DELETE};
        assertTrue("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_EDIT_AND_ASSIGN};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_ASSIGN};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        
        getPolicyService().revokePolicyFromPrincipal(policy, user);
        deleteAccount(user);
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(accessRights, getSessionInfo());
        getPolicyService().deleteAccessRights(accessRights.getResourceId());
        getPolicyService().deletePolicy(policy.getResourceId());
    }

    /**
     * @throws Exception
     */
    @Test
    public void checkCreateEditAssignAssignAccessRights() throws Exception {
        Realm realm = getDefaultRealm();
        Policy policy = createPolicy(realm);
        User user = createAccount();
        getPolicyService().grantPolicyToPrincipal(policy, user);
        AccessRights accessRights = createCreateEditAssignAccessRights(getResourceType().getPermissionClass());
        PropertyList selectedPolicies = PropertyList.createFromArray(new int[] {policy.getResourceId()});
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(accessRights, selectedPolicies, getSessionInfo());
        
        Permission[] permissions = new Permission[] {PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN};
        assertTrue("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_DELETE};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_EDIT_AND_ASSIGN};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_ASSIGN};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        
        getPolicyService().revokePolicyFromPrincipal(policy, user);
        deleteAccount(user);
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(accessRights, getSessionInfo());
        getPolicyService().deleteAccessRights(accessRights.getResourceId());
        getPolicyService().deletePolicy(policy.getResourceId());
    }

    /**
     * @throws Exception
     */
    @Test
    public void checkEditAssignAccessRights() throws Exception {
        Realm realm = getDefaultRealm();
        Policy policy = createPolicy(realm);
        int users = getDefaultUserDatabase().listAllUsers(UserDatabase.WILDCARD_SEARCH, Integer.MAX_VALUE).length ;
        User user = createAccount();
        assertEquals(getDefaultUserDatabase().listAllUsers(UserDatabase.WILDCARD_SEARCH, Integer.MAX_VALUE).length, users +1);
        getPolicyService().grantPolicyToPrincipal(policy, user);
        AccessRights accessRights = createEditAssignAccessRights(getResourceType().getPermissionClass());
        PropertyList selectedPolicies = PropertyList.createFromArray(new int[] {policy.getResourceId()});
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(accessRights, selectedPolicies, getSessionInfo());
        
        Permission[] permissions = new Permission[] {PolicyConstants.PERM_EDIT_AND_ASSIGN};
        assertTrue("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_DELETE};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_ASSIGN};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        
        getPolicyService().revokePolicyFromPrincipal(policy, user);
        deleteAccount(user);
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(accessRights, getSessionInfo());
        getPolicyService().deleteAccessRights(accessRights.getResourceId());
        getPolicyService().deletePolicy(policy.getResourceId());
    }

    /**
     * @throws Exception
     */
    @Test
    public void checkAssignAccessRights() throws Exception {
        Realm realm = getDefaultRealm();
        Policy policy = createPolicy(realm);
        User user = createAccount();
        getPolicyService().grantPolicyToPrincipal(policy, user);
        AccessRights accessRights = createAssignAccessRights(getResourceType().getPermissionClass());
        PropertyList selectedPolicies = PropertyList.createFromArray(new int[] {policy.getResourceId()});
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(accessRights, selectedPolicies, getSessionInfo());
        
        Permission[] permissions = new Permission[] {PolicyConstants.PERM_ASSIGN};
        assertTrue("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_DELETE};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_EDIT_AND_ASSIGN};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        
        getPolicyService().revokePolicyFromPrincipal(policy, user);
        deleteAccount(user);
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(accessRights, getSessionInfo());
        getPolicyService().deleteAccessRights(accessRights.getResourceId());
        getPolicyService().deletePolicy(policy.getResourceId());
    }

    /**
     * @throws Exception
     */
    @Test
    public void checkResourceAccessRights() throws Exception {
        Realm realm = getDefaultRealm();
    	Policy policy = createPolicy(realm);
    	User user = createAccount();
    	getPolicyService().grantPolicyToPrincipal(policy, user);
    	AccessRights accessRights = createAssignAccessRights(getResourceType().getPermissionClass());
    	PropertyList selectedPolicies = PropertyList.createFromArray(new int[] {policy.getResourceId()});
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(accessRights, selectedPolicies, getSessionInfo());
        
        T resource = createResource();
        assertFalse("Should not be.", getPolicyService().isPrincipalGrantedResourcesOfType(user, resource.getResourceType(), null));
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(resource, selectedPolicies, getSessionInfo());
        assertTrue("Should be.", getPolicyService().isPrincipalGrantedResourcesOfType(user, resource.getResourceType(), null));
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(resource, getSessionInfo());
        assertFalse("Should not be.", getPolicyService().isPrincipalGrantedResourcesOfType(user, resource.getResourceType(), null));
        
    	getPolicyService().revokePolicyFromPrincipal(policy, user);
    	deleteAccount(user);
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(accessRights, getSessionInfo());
    	deleteResource(resource);
    	getPolicyService().deleteAccessRights(accessRights.getResourceId());
    	getPolicyService().deletePolicy(policy.getResourceId());
    }

    /**
     * @throws Exception
     */
    @Test
    public void checkResourceAccessViaRole() throws Exception {
        User user = createAccount();
        Role role = createRole();
        user = updateAccountRoles(user, Collections.singleton(role));
        Realm realm = getDefaultRealm();
        Policy policy = createPolicy(realm);
        T resource = createResource();
        getPolicyService().attachResourceToPolicy(resource, policy, 0, realm);
        
        assertFalse("User should not have access", PolicyDatabaseFactory.getInstance().isPrincipalAllowed(user, resource, false));
        getPolicyService().grantPolicyToPrincipal(policy, role);
        assertTrue("User should have access", PolicyDatabaseFactory.getInstance().isPrincipalAllowed(user, resource, false));
        getPolicyService().revokePolicyFromPrincipal(policy, role);
        assertFalse("User should not have access", PolicyDatabaseFactory.getInstance().isPrincipalAllowed(user, resource, false));
        
        deleteResource(resource);
        getPolicyService().deletePolicy(policy.getResourceId());
        user = updateAccountRoles(user, Collections.<Role>emptyList());
        deleteRole(role);
        deleteAccount(user);
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void checkAllAccessRightsViaRole() throws Exception {
        User user = createAccount();
        Role role = createRole();
        user = updateAccountRoles(user, Collections.singleton(role));
        Realm realm = getDefaultRealm();
        Policy policy = createPolicy(realm);
        T resource = createResource();
        getPolicyService().attachResourceToPolicy(resource, policy, 0, realm);
        
        getPolicyService().grantPolicyToPrincipal(policy, role);
        AccessRights accessRights = createAllAccessRights(getResourceType().getPermissionClass());
        PropertyList selectedPolicies = PropertyList.createFromArray(new int[] {policy.getResourceId()});
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(accessRights, selectedPolicies, getSessionInfo());
        
        assertEquals("The newly created and default.", getPolicyService().getPermittingAccessRights(null, null, null, user).size(), getDefaultAccessRightCount() + 1);
        Permission[] permissions = new Permission[] {PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_DELETE};
        assertTrue("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(accessRights, getSessionInfo());
        getPolicyService().deleteAccessRights(accessRights.getResourceId());
        deleteResource(resource);
        getPolicyService().deletePolicy(policy.getResourceId());
        user = updateAccountRoles(user, Collections.<Role>emptyList());
        deleteRole(role);
        deleteAccount(user);
    }

    /**
     * @throws Exception
     */
    @Test
    public void checkNoAccessRightsViaRole() throws Exception {
        User user = createAccount();
        Role role = createRole();
        user = updateAccountRoles(user, Collections.singleton(role));
        Realm realm = getDefaultRealm();
        Policy policy = createPolicy(realm);
        T resource = createResource();
        getPolicyService().attachResourceToPolicy(resource, policy, 0, realm);
        
        getPolicyService().grantPolicyToPrincipal(policy, role);
        AccessRights accessRights = createAllAccessRights(getResourceType().getPermissionClass());
        PropertyList selectedPolicies = PropertyList.createFromArray(new int[] {policy.getResourceId()});
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(accessRights, selectedPolicies, getSessionInfo());
        
        Permission[] permissions = new Permission[] {};
        assertFalse("The permissions should not be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(accessRights, getSessionInfo());
        getPolicyService().deleteAccessRights(accessRights.getResourceId());
        deleteResource(resource);
        getPolicyService().deletePolicy(policy.getResourceId());
        user = updateAccountRoles(user, Collections.<Role>emptyList());
        deleteRole(role);
        deleteAccount(user);
    }

    /**
     * @throws Exception
     */
    @Test
    public void checkDeleteAccessRightsViaRole() throws Exception {
        User user = createAccount();
        Role role = createRole();
        user = updateAccountRoles(user, Collections.singleton(role));
        Realm realm = getDefaultRealm();
        Policy policy = createPolicy(realm);
        T resource = createResource();
        getPolicyService().attachResourceToPolicy(resource, policy, 0, realm);
        
        getPolicyService().grantPolicyToPrincipal(policy, role);
        AccessRights accessRights = createDeleteAccessRights(getResourceType().getPermissionClass());
        PropertyList selectedPolicies = PropertyList.createFromArray(new int[] {policy.getResourceId()});
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(accessRights, selectedPolicies, getSessionInfo());
        
        Permission[] permissions = new Permission[] {PolicyConstants.PERM_DELETE};
        assertTrue("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_EDIT_AND_ASSIGN};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_ASSIGN};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(accessRights, getSessionInfo());
        getPolicyService().deleteAccessRights(accessRights.getResourceId());
        deleteResource(resource);
        getPolicyService().deletePolicy(policy.getResourceId());
        user = updateAccountRoles(user, Collections.<Role>emptyList());
        deleteRole(role);
        deleteAccount(user);
    }

    /**
     * @throws Exception
     */
    @Test
    public void checkCreateEditAssignAssignAccessRightsViaRole() throws Exception {
        User user = createAccount();
        Role role = createRole();
        user = updateAccountRoles(user, Collections.singleton(role));
        Realm realm = getDefaultRealm();
        Policy policy = createPolicy(realm);
        T resource = createResource();
        getPolicyService().attachResourceToPolicy(resource, policy, 0, realm);
        
        getPolicyService().grantPolicyToPrincipal(policy, role);
        AccessRights accessRights = createCreateEditAssignAccessRights(getResourceType().getPermissionClass());
        PropertyList selectedPolicies = PropertyList.createFromArray(new int[] {policy.getResourceId()});
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(accessRights, selectedPolicies, getSessionInfo());
        
        Permission[] permissions = new Permission[] {PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN};
        assertTrue("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_DELETE};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_EDIT_AND_ASSIGN};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_ASSIGN};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(accessRights, getSessionInfo());
        getPolicyService().deleteAccessRights(accessRights.getResourceId());
        deleteResource(resource);
        getPolicyService().deletePolicy(policy.getResourceId());
        user = updateAccountRoles(user, Collections.<Role>emptyList());
        deleteRole(role);
        deleteAccount(user);
    }

    /**
     * @throws Exception
     */
    @Test
    public void checkEditAssignAccessRightsViaRole() throws Exception {
        User user = createAccount();
        Role role = createRole();
        user = updateAccountRoles(user, Collections.singleton(role));
        Realm realm = getDefaultRealm();
        Policy policy = createPolicy(realm);
        T resource = createResource();
        getPolicyService().attachResourceToPolicy(resource, policy, 0, realm);
        
        getPolicyService().grantPolicyToPrincipal(policy, role);
        AccessRights accessRights = createEditAssignAccessRights(getResourceType().getPermissionClass());
        PropertyList selectedPolicies = PropertyList.createFromArray(new int[] {policy.getResourceId()});
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(accessRights, selectedPolicies, getSessionInfo());
        
        Permission[] permissions = new Permission[] {PolicyConstants.PERM_EDIT_AND_ASSIGN};
        assertTrue("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_DELETE};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_ASSIGN};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(accessRights, getSessionInfo());
        getPolicyService().deleteAccessRights(accessRights.getResourceId());
        deleteResource(resource);
        getPolicyService().deletePolicy(policy.getResourceId());
        user = updateAccountRoles(user, Collections.<Role>emptyList());
        deleteRole(role);
        deleteAccount(user);
    }

    /**
     * @throws Exception
     */
    @Test
    public void checkAssignAccessRightsViaRole() throws Exception {
        User user = createAccount();
        Role role = createRole();
        user = updateAccountRoles(user, Collections.singleton(role));
        Realm realm = getDefaultRealm();
        Policy policy = createPolicy(realm);
        T resource = createResource();
        getPolicyService().attachResourceToPolicy(resource, policy, 0, realm);
        
        getPolicyService().grantPolicyToPrincipal(policy, role);
        AccessRights accessRights = createAssignAccessRights(getResourceType().getPermissionClass());
        PropertyList selectedPolicies = PropertyList.createFromArray(new int[] {policy.getResourceId()});
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(accessRights, selectedPolicies, getSessionInfo());
        
        Permission[] permissions = new Permission[] {PolicyConstants.PERM_ASSIGN};
        assertTrue("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_DELETE};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        permissions = new Permission[] {PolicyConstants.PERM_EDIT_AND_ASSIGN};
        assertFalse("The permissions should be permitted.", getPolicyService().isPermitted(getResourceType(), permissions, user, false));
        
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(accessRights, getSessionInfo());
        getPolicyService().deleteAccessRights(accessRights.getResourceId());
        deleteResource(resource);
        getPolicyService().deletePolicy(policy.getResourceId());
        user = updateAccountRoles(user, Collections.<Role>emptyList());
        deleteRole(role);
        deleteAccount(user);
    }

    /**
     * @throws Exception
     */
    @Test
    public void testFavorites() throws Exception{
        Realm realm = getDefaultRealm();
        Policy policy = createPolicy(realm);
        T resource = createResource();
        User user = createAccount();
        PropertyList selectedPolicies = PropertyList.createFromArray(new int[] {policy.getResourceId()});
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(resource, selectedPolicies, getSessionInfo());
        getPolicyService().grantPolicyToPrincipal(policy, user);
        
        assertEquals("There should be no favorites", 0, SystemDatabaseFactory.getInstance().getFavorites(resource.getResourceType().getResourceTypeId(), user).size());
        SystemDatabaseFactory.getInstance().addFavorite(resource.getResourceType().getResourceTypeId(), resource.getResourceId(), user.getPrincipalName());
        assertEquals("There should now be one favorites", 1, SystemDatabaseFactory.getInstance().getFavorites(resource.getResourceType().getResourceTypeId(), user).size());
        
        Favorite favorite = SystemDatabaseFactory.getInstance().getFavorite(resource.getResourceType().getResourceTypeId(), user, resource.getResourceId());
        assertEquals("The favorite and resource id should match.", resource.getResourceId(), favorite.getFavoriteKey());
        assertEquals("The favorite username should match the users principle name.", user.getPrincipalName(), favorite.getUsername());
        assertEquals("The favorite type id and resource type id should match.", resource.getResourceType().getResourceTypeId(), favorite.getType());
        
        SystemDatabaseFactory.getInstance().removeFavorite(resource.getResourceType().getResourceTypeId(), resource.getResourceId(), user.getPrincipalName());
        assertEquals("There should be no favorites", 0, SystemDatabaseFactory.getInstance().getFavorites(resource.getResourceType().getResourceTypeId(), user).size());
        
        getPolicyService().revokePolicyFromPrincipal(policy, user);
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(resource, getSessionInfo());
        deleteResource(resource);
        getResource(resource);
        getPolicyService().deletePolicy(policy.getResourceId());
    }
}