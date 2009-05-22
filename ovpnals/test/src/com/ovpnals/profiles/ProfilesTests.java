
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
			
package com.ovpnals.profiles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ovpnals.boot.PropertyList;
import com.ovpnals.core.CoreServlet;
import com.ovpnals.core.UserDatabaseManager;
import com.ovpnals.jdbc.JDBCPropertyDatabase;
import com.ovpnals.policyframework.AccessRights;
import com.ovpnals.policyframework.Policy;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.policyframework.PolicyDatabaseFactory;
import com.ovpnals.policyframework.ResourceType;
import com.ovpnals.properties.DefaultPropertyProfile;
import com.ovpnals.properties.PropertyProfile;
import com.ovpnals.realms.Realm;
import com.ovpnals.security.User;
import com.ovpnals.testcontainer.policyframework.AbstractTestPolicyEnabledResource;

/**
 */
public class ProfilesTests extends AbstractTestPolicyEnabledResource<PropertyProfile> {

    private static JDBCPropertyDatabase propertyDatabase;
    
    /**
     * @throws Exception
     */
    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        setUp("");
        propertyDatabase = new JDBCPropertyDatabase();
        propertyDatabase.open(CoreServlet.getServlet());
    }

    /**
     * @throws Exception
     */
    @AfterClass
    public static void after() throws Exception {
        propertyDatabase.close();
    }
    
    @Override
    public ResourceType getResourceType() throws Exception {
      return PolicyConstants.PROFILE_RESOURCE_TYPE;
    }

    
    @Override
    public PropertyProfile getEmptyResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new DefaultPropertyProfile(-1, -1, "", "", "", calendar, calendar);
    }

    @Override
    public PropertyProfile getNormalResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new DefaultPropertyProfile(getDefaultRealm().getRealmID(), -1, null, "resourceName", "resourceDescription", calendar, calendar);
    }

    @Override
    public PropertyProfile getNullResource() throws Exception {
        Calendar calendar = Calendar.getInstance();
        return new DefaultPropertyProfile(-1, -1, null, null, null, calendar, calendar);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PropertyProfile> getAllResources() throws Exception {
        return propertyDatabase.getPropertyProfiles(null, true, UserDatabaseManager.getInstance().getDefaultRealmID());
    }

    @Override
    public PropertyProfile createResource(PropertyProfile resource) throws Exception {
        return propertyDatabase.createPropertyProfile(resource.getOwnerUsername(), resource.getResourceName(), resource.getResourceDescription(), 0, UserDatabaseManager.getInstance().getDefaultRealmID());
    }

    @Override
    public PropertyProfile deleteResource(PropertyProfile resource) throws Exception {
        return propertyDatabase.deletePropertyProfile(resource.getResourceId());
    }

    @Override
    public PropertyProfile getResource(PropertyProfile resource) throws Exception {
        return propertyDatabase.getPropertyProfile(resource.getResourceId());
    }

    @Override
    public PropertyProfile updateResource(PropertyProfile resource) throws Exception {
        propertyDatabase.updatePropertyProfile(resource.getResourceId(), resource.getResourceName(), resource.getResourceDescription());
        return getResource(resource);
    }
    
    @Test
    public void checkResourceAccessRights() throws Exception {
        Realm realm = getDefaultRealm();
        Policy policy = createPolicy(realm);
        User user = createAccount();
        getPolicyService().grantPolicyToPrincipal(policy, user);
        
        AccessRights accessRights = createAssignAccessRights(getResourceType().getPermissionClass());
        PropertyList selectedPolicies = PropertyList.createFromArray(new int[] {policy.getResourceId()});
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(accessRights, selectedPolicies, getSessionInfo());
        PropertyProfile resource = createResource();
        assertTrue("Should be, as one already exists.", getPolicyService().isPrincipalGrantedResourcesOfType(user, resource.getResourceType(), null));
        assertEquals("Should be only one.", getPolicyService().getGrantedResourcesOfType(user, getResourceType()).size(), 1);
        
        PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(resource, selectedPolicies, getSessionInfo());
        assertEquals("Should be two.", getPolicyService().getGrantedResourcesOfType(user, getResourceType()).size(), 2);
        
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(resource, getSessionInfo());
        assertEquals("Should be only one.", getPolicyService().getGrantedResourcesOfType(user, getResourceType()).size(), 1);
        getPolicyService().revokePolicyFromPrincipal(policy, user);
        deleteAccount(user);
        
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(accessRights, getSessionInfo());
        deleteResource(resource);
        getPolicyService().deleteAccessRights(accessRights.getResourceId());
        getPolicyService().deletePolicy(policy.getResourceId());
    }
    
    /**
     * Test the creation of PropertyProfile with a no existing user, the PropertyProfile created is a general profile.
     * @throws Exception
     */
    @Test
    public void createProfileWithoutUser() throws Exception {
        DefaultPropertyProfile newProfile = new DefaultPropertyProfile(getDefaultRealm().getRealmID(), -1, "test3", "profile", "A profile", Calendar.getInstance(), Calendar.getInstance());
        assertEquals("There should not be any PropertyProfile", 0, propertyDatabase.getPropertyProfiles("test1", true, 1).size());
        PropertyProfile createdProfile = createResource(newProfile);
        assertEquals("There should be two PropertyProfile", 1, propertyDatabase.getPropertyProfiles("test3", true, 1).size());
        deleteResource(createdProfile);
        assertEquals("There should not be any PropertyProfile", 0, propertyDatabase.getPropertyProfiles("test1", true, 1).size());
    }
}