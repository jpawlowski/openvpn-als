
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
			
package com.adito.policyframework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

/**
 */
public class ResourceItemTest {

    /**
     */
    @Test
    public void constructor() {
        Resource originalResource = getResource();
        ResourceItem<Resource> resourceItem = getResourceItem(originalResource);
        
        Resource resource = resourceItem.getResource();
        assertNotNull("Resource is not null", resource);
        assertEquals("Resource matches original", originalResource, resource);
        
        List<Policy> policies = resourceItem.getPolicies();
        assertNotNull("Policies list is not null", policies);
        assertTrue("Policies list is empty", policies.isEmpty());
        
        assertNull("Last launched policy is null", resourceItem.getLastLaunchedPolicy());
        assertFalse("Mulitple policies is false", resourceItem.getMultiplePolicies());
        assertEquals("First policy name is empty", "", resourceItem.getFirstPolicyName());
        
        assertEquals("Column value is correct", resource.getResourceDisplayName(), resourceItem.getColumnValue(0));
        assertEquals("Column value is correct", resource.getResourceDisplayName(), resourceItem.getColumnValue(1));
        
        assertNotNull("Small icon path is not null", resourceItem.getSmallIconPath(null));
        assertTrue("Small icon path is not empty", resourceItem.getSmallIconPath(null).length() > 0);

        assertNotNull("Large icon path is not null", resourceItem.getLargeIconPath(null));
        assertTrue("Large icon path is not empty", resourceItem.getLargeIconPath(null).length() > 0);
        
        assertEquals("Large icon additional icon is correct", "", resourceItem.getLargeIconAdditionalText(null));
        assertEquals("Large icon additional icon is correct", "", resourceItem.getLargeIconAdditionalText(null));
        assertEquals("Large icon additional text is correct", "", resourceItem.getLargeIconAdditionalText(null));
        assertEquals("Link is correct", "#", resourceItem.getLink(0, null));
        assertEquals("Link is correct", "#", resourceItem.getLink(0, "", null));
        assertEquals("On click is correct", "", resourceItem.getOnClick(0, null));
    }
    
    /**
     */
    @Test
    public void nullResourceName() {
        Resource originalResource = getResource();
        originalResource.setResourceName(null);
        ResourceItem<Resource> resourceItem = getResourceItem(originalResource);
        assertEquals("Column value is correct", "<Unknown>", resourceItem.getColumnValue(0));
    }
    
    /**
     */
    @Test
    public void withPolicies() {
        Policy firstPolicy = new DefaultPolicy (1, "first", "", 0, null, null, 0);
        Policy secondPolicy = new DefaultPolicy (2, "second", "", 0, null, null, 0);
        List<Policy> originalPolicies = new ArrayList<Policy>(2);
        originalPolicies.add(firstPolicy);
        originalPolicies.add(secondPolicy);
        
        Resource originalResource = getResource();
        ResourceItem<Resource> resourceItem = getResourceItem(originalResource, originalPolicies);
        
        List<Policy> policies = resourceItem.getPolicies();
        assertNotNull("Policies list is not null", policies);
        assertFalse("Policies list is empty", policies.isEmpty());
        assertEquals("Policies list is equal", originalPolicies, policies);
        
        assertNull("Last launched policy is null", resourceItem.getLastLaunchedPolicy());
        assertTrue("Mulitple policies is false", resourceItem.getMultiplePolicies());
        assertEquals("First policy name is correct", firstPolicy.getResourceName(), resourceItem.getFirstPolicyName());
    }
    
    private static ResourceItem<Resource> getResourceItem(Resource resource) {
        return new ResourceItem<Resource>(resource) {
            @Override
            protected String getThemePath(HttpServletRequest request) {
                return "";
            }
        };
    }

    private static ResourceItem<Resource> getResourceItem(Resource resource, List<Policy> policies) {
        return new ResourceItem<Resource>(resource, policies) {
            @Override
            protected String getThemePath(HttpServletRequest request) {
                return "";
            }
        };
    }
    
    private static Resource getResource() {
        DefaultResourceType resourceType = new DefaultResourceType(1, "", "");
        Resource resource = new AbstractResource(0, resourceType, 0, "", "", null, null){};
        resource.setResourceName("resourceName");
        return resource;
    }
}