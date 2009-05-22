
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
			
package com.ovpnals.testcontainer.policyframework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ovpnals.boot.PropertyList;
import com.ovpnals.policyframework.AccessRight;
import com.ovpnals.policyframework.AccessRights;
import com.ovpnals.policyframework.DefaultAccessRights;
import com.ovpnals.policyframework.Policy;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.policyframework.PolicyDatabaseFactory;
import com.ovpnals.policyframework.Resource;
import com.ovpnals.policyframework.ResourceType;
import com.ovpnals.realms.Realm;
import com.ovpnals.security.AuthenticationScheme;
import com.ovpnals.security.SystemDatabaseFactory;
import com.ovpnals.testcontainer.AbstractTest;

/**
 * @param <T> 
 */
public abstract class AbstractTestResource<T extends Resource> extends AbstractTest {

    /**
     * @throws Exception
     */
    @Before
    @After
    public void initialize() throws Exception {
        deleteMultipleResources(getAllResources());
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void attachAndDetachResourceFromPolicy() throws Exception {
        Realm realm = getDefaultRealm();
        Policy policy = createPolicy(realm);
        T resource = createResource();
        PropertyList selectedPolicies = PropertyList.createFromArray(new int[] {policy.getResourceId()});
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(resource, selectedPolicies, getSessionInfo());
        assertTrue("The resource should be attached to the policy", getPolicyService().isResourceAttachedToPolicy(resource, policy, realm));
        assertTrue("The resource type must match the resource type of the type created.", getResourceType().equals(resource.getResourceType()));
        
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(resource, getSessionInfo());
        assertFalse("The resource should not be attached to the policy", getPolicyService().isResourceAttachedToPolicy(resource, policy, realm));
        T deletedResource = deleteResource(resource);
        T checkWF = getResource(resource);
        assertNotSame("The resource should have been deleted", deletedResource, checkWF);
        getPolicyService().deletePolicy(policy.getResourceId());
    }

    /**
     * @throws Exception
     */
    @Test
    public void createAndDeleteAccessRights() throws Exception {
        Realm realm = getDefaultRealm();
        assertEquals("There should be one access rights.", getPolicyService().getAccessRights().size(), getDefaultAccessRightCount());
        AccessRights accessRights = createAllAccessRights(getResourceType().getPermissionClass());
        assertEquals("There should be two access rights.", getPolicyService().getAccessRights().size(), getDefaultAccessRightCount() + 1);
        AccessRights accessRights2 = getPolicyService().getAccessRight(accessRights.getResourceId());
        assertEquals("The acces rights should be the same", accessRights, accessRights2);
        AccessRights accessRights3 = getPolicyService().getAccessRightsByName(accessRights.getResourceName(), realm.getRealmID());
        assertEquals("The acces rights should be the same", accessRights, accessRights3);
        getPolicyService().deleteAccessRights(accessRights.getResourceId());
        assertEquals("There should be one access rights.", getPolicyService().getAccessRights().size(), getDefaultAccessRightCount());
    }

    /**
     * @throws Exception
     */
    @Test
    public void assignAndUnassignAccessRights() throws Exception {
        Realm realm = getDefaultRealm();
        Policy policy = createPolicy(realm);
        AccessRights accessRights = createAllAccessRights(getResourceType().getPermissionClass());
        
        assertFalse("The resource should not be attached", getPolicyService().isResourceAttachedToPolicy(accessRights, policy, realm));
        PropertyList selectedPolicies = PropertyList.createFromArray(new int[] {policy.getResourceId()});
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(accessRights, selectedPolicies, getSessionInfo());
        assertTrue("The resource should be attached", getPolicyService().isResourceAttachedToPolicy(accessRights, policy, realm));
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(accessRights, getSessionInfo());
        assertFalse("The resource should not be attached", getPolicyService().isResourceAttachedToPolicy(accessRights, policy, realm));
        
        // other resources should still be attached.
        List<AuthenticationScheme> authenticationSchemeSequences = SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequences();
        Policy everyone = getPolicyService().getPolicyByName("Everyone", realm.getResourceId());
        for (AuthenticationScheme scheme : authenticationSchemeSequences) {
            assertTrue("Auth scheme " + scheme.getResourceName() + " not assigned to everyone.", getPolicyService().isResourceAttachedToPolicy(scheme, everyone, realm));
        }
        AccessRights personal =  getPolicyService().getAccessRightsByName("Global Permissions", realm.getRealmID());
        assertTrue("Profile " + personal.getResourceName() + " not assigned to everyone.", getPolicyService().isResourceAttachedToPolicy(personal, everyone, realm));
        
        getPolicyService().deleteAccessRights(accessRights.getResourceId());
        getPolicyService().deletePolicy(policy.getResourceId());
    }
    
    /**
     * @return T
     * @throws Exception
     */
    public abstract T getNormalResource() throws Exception;
    
    /**
     * @throws Exception
     */
    @Test
    public void createNormalResource() throws Exception {
        T resource = getNormalResource();
        assertTrue("Should have none", getAllResources().isEmpty());
        T createdResource = createResource(resource);
        assertEquals("There should be only one", 1, getAllResources().size());
        deleteResource(createdResource);
        assertTrue("Should have none", getAllResources().isEmpty());
    }

    /**
     * @return T
     * @throws Exception
     */
    public abstract T getEmptyResource() throws Exception;
    
    /**
     * @throws Exception
     */
    @Test
    public void createEmptyResource() throws Exception {
        T resource = getEmptyResource();
        assertTrue("Should have none", getAllResources().isEmpty());
        T createdResource = createResource(resource);
        assertEquals("There should be only one", 1, getAllResources().size());
        deleteResource(createdResource);
        assertTrue("Should have none", getAllResources().isEmpty());
    }

    /**
     * @return T
     * @throws Exception
     */
    public abstract T getNullResource() throws Exception;
    
    /**
     * @throws Exception
     */
    @Test
    public void createNullResource() throws Exception {
        T resource = getNullResource();
        assertTrue("Should have none", getAllResources().isEmpty());
        
        try {
            createResource(resource);
            fail("This should have failed");
        }
        catch (Exception e) {
            // ignore
        }
        assertTrue("Should have none", getAllResources().isEmpty());
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void createResourceWithSameName() throws Exception {
        T resource = getNormalResource();
        assertTrue("Should have none", getAllResources().isEmpty());
        
        T createdResourceOne = createResource(resource);
        assertEquals("There should be only one", 1, getAllResources().size());
        T createdResourceTwo = createResource(resource);
        assertEquals("There should be two", 2, getAllResources().size());
        
        deleteResource(createdResourceOne);
        deleteResource(createdResourceTwo);
        assertTrue("Should have none", getAllResources().isEmpty());
    }

    /**
     * @throws Exception
     */
    @Test
    public void createTenResources() throws Exception {
        T normalResource = getNormalResource();
        assertTrue("Should have none", getAllResources().isEmpty());
        List<T> resources = createMultipleResources(normalResource, 10);
        assertEquals("There should be ten resources", 10, getAllResources().size());
        deleteMultipleResources(resources);
        assertTrue("Should have none", getAllResources().isEmpty());
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void updateUnknownResource() throws Exception {
        T resource = getNormalResource();
        assertTrue("Should have none", getAllResources().isEmpty());
        T updatedResource = updateResource(resource);
        assertNull(updatedResource);
    }

    /**
     * @throws Exception
     */
    @Test
    public void updateResourceName() throws Exception {
        T normalResource = getNormalResource();
        assertTrue("Should have none", getAllResources().isEmpty());
        T createdResource = createResource(normalResource);
        assertEquals("There should be only one", 1, getAllResources().size());
        
        final String newName = "thisIsMyNewName";
        createdResource.setResourceName(newName);
        T updatedResource = updateResource(createdResource);
        assertEquals("Resource name should have been updated", newName, updatedResource.getResourceName());

        deleteResource(createdResource);
        assertTrue("Should have none", getAllResources().isEmpty());
    }

    /**
     * @throws Exception
     */
    @Test
    public void updateResourceDescription() throws Exception {
        T normalResource = getNormalResource();
        assertTrue("Should have none", getAllResources().isEmpty());
        T createdResource = createResource(normalResource);
        assertEquals("There should be only one", 1, getAllResources().size());
        
        final String newDescription = "thisIsMyNewDescription";
        createdResource.setResourceDescription(newDescription);
        T updatedResource = updateResource(createdResource);
        assertEquals("Resource description should have been updated", newDescription, updatedResource.getResourceDescription());

        deleteResource(createdResource);
        assertTrue("Should have none", getAllResources().isEmpty());
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void deleteUnknownResource() throws Exception {
        T resource = getNormalResource();
        assertTrue("Should have none", getAllResources().isEmpty());
        T createdResource = createResource(resource);
        assertEquals("There should be only one", 1, getAllResources().size());
        deleteResource(createdResource);
        assertTrue("Should have none", getAllResources().isEmpty());
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void deleteResource() throws Exception {
        T resource = getNormalResource();
        assertTrue("Should have none", getAllResources().isEmpty());

        try {
            deleteResource(resource);
            fail("This should have failed");
        }
        catch(Exception e) {
            // ignore
        }
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void deleteResourceTwice() throws Exception {
        T resource = getNormalResource();
        assertTrue("Should have none", getAllResources().isEmpty());
        T createdResource = createResource(resource);
        assertEquals("There should be only one", 1, getAllResources().size());
        deleteResource(createdResource);
        assertTrue("Should have none", getAllResources().isEmpty());
        
        try {
            deleteResource(createdResource);
            fail("This should have failed");
        }
        catch(Exception e) {
            // ignore
        }
    }
    
    /**
     * @throws Exception
     */
    @Test 
    public void getUnknownResource() throws Exception {
        T normalResource = getNormalResource();
        T foundResource = getResource(normalResource);
        assertNull(foundResource);
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void getResource() throws Exception {
        T normalResource = getNormalResource();
        T createdResource = createResource(normalResource);
        
        T foundResource = getResource(createdResource);
        assertNotNull(foundResource);
        assertEquals("Found same Resource we created", createdResource, foundResource);
        deleteResource(foundResource);
    }
    
    protected static Policy createPolicy(Realm realm) throws Exception {
        return createPolicy("Policy A", "Policy A description", Policy.TYPE_NORMAL, realm);
    }
    
    protected static Policy createPolicy(String name, String description, int type, Realm realm) throws Exception {
        return getPolicyService().createPolicy(name, description, type, realm.getRealmID());
    }
    
    protected AccessRights createAllAccessRights(String delegationClass) throws Exception {
        List<AccessRight> accessRights = new ArrayList<AccessRight>();
        accessRights.add(new AccessRight(getResourceType(), PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN));
        accessRights.add(new AccessRight(getResourceType(), PolicyConstants.PERM_DELETE));
        return createAccessRights(delegationClass, accessRights);
    }

    protected AccessRights createCreateEditAssignAccessRights(String delegationClass) throws Exception {
        List<AccessRight> accessRights = Collections.singletonList(new AccessRight(getResourceType(), PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN));
        return createAccessRights(delegationClass, accessRights);
    }
    
    protected AccessRights createEditAssignAccessRights(String delegationClass) throws Exception{
        List<AccessRight> accessRights = Collections.singletonList(new AccessRight(getResourceType(), PolicyConstants.PERM_EDIT_AND_ASSIGN));
        return createAccessRights(delegationClass, accessRights);
    }
    
    protected AccessRights createAssignAccessRights(String delegationClass) throws Exception{
        List<AccessRight> accessRights = Collections.singletonList(new AccessRight(getResourceType(), PolicyConstants.PERM_ASSIGN));
        return createAccessRights(delegationClass, accessRights);
    }
    
    protected AccessRights createDeleteAccessRights(String delegationClass) throws Exception{
        List<AccessRight> accessRights = Collections.singletonList(new AccessRight(getResourceType(), PolicyConstants.PERM_DELETE));
        return createAccessRights(delegationClass, accessRights);
    }
    
    private static AccessRights createAccessRights(String delegationClass, List<AccessRight> accessRights) throws Exception {
        Calendar calendar = Calendar.getInstance();
        DefaultAccessRights defaultAccessRights = new DefaultAccessRights(getDefaultRealm().getRealmID(), 0, "Access1", "First set of access rights.", accessRights, delegationClass, calendar, calendar);
        return getPolicyService().createAccessRights(defaultAccessRights);
    }

    /**
     * @param resource
     * @return Resource
     * @throws Exception
     */
    public abstract T updateResource(T resource)  throws Exception;
    
    /**
     * @param resource
     * @return Resource
     * @throws Exception
     */
    public abstract T deleteResource(T resource)  throws Exception;
    
    /**
     * @param resource
     * @return Resource
     * @throws Exception
     */
    public abstract T getResource(T resource) throws Exception;
    
    /**
     * @param resource
     * @return Resource
     * @throws Exception
     */
    public abstract T createResource(T resource) throws Exception;
    
    /**
     * @return Resource
     * @throws Exception
     */
    public final T createResource() throws Exception {
        return createResource(getNormalResource());
    }
   
    /**
     * @return Resource
     * @throws Exception
     */
    public abstract ResourceType getResourceType() throws Exception;
    
    /**
     * @return List<Resource>
     * @throws Exception
     */
    public abstract List<T> getAllResources() throws Exception;
    
    
    protected List<T> createMultipleResources(T resource, int number) throws Exception {
        List<T> resources = new ArrayList<T>(number);
        for(int i = 0; i < number; i++) {
            resource.setResourceName("Resource name " + i);
            resources.add(createResource(resource));
        }
        return resources;
    }
    
    protected void deleteMultipleResources(List<T> resources) throws Exception {
        for (T resource : resources) {
            deleteResource(resource);
        }
    }
    
    /**
     * @return int
     */
    public int getDefaultAccessRightCount() {
        return 1;
    }
}