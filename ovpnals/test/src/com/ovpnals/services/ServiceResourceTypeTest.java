
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
			
package com.ovpnals.services;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import com.ovpnals.policyframework.AbstractResource;
import com.ovpnals.policyframework.Resource;

/**
 */
public class ServiceResourceTypeTest {
    private final ServiceResourceType<Resource> resourceType;
    private final ResourceService<Resource> resourceService;
    
    /**
     */
    @SuppressWarnings("unchecked")
    public ServiceResourceTypeTest() {
        resourceType = new ServiceResourceType<Resource>(123, "", "");
        resourceService = createStrictMock(ResourceService.class);
        resourceType.setResourceService(resourceService);
    }
    
    /**
     */
    @Before
    public void setUp() {
        reset(resourceService);
    }
    
    /**
     */
    @Test
    public void isResourceNameInUse() {
        expect(resourceService.isResourceNameInUse("resourceName", null)).andReturn(false);
        replay(resourceService);
        boolean inUse = resourceType.isResourceNameInUse("resourceName", null);
        verify(resourceService);
        assertFalse("Cluster name is not in use", inUse);
    }
    
    /**
     */
    @Test
    public void getResourceById() {
        Resource resource = getDefaultResource();
        expect(resourceService.getResourceById(123)).andReturn(resource);
        replay(resourceService);
        Resource foundResource = resourceType.getResourceById(123);
        verify(resourceService);
        assertEquals("Resource matches", resource, foundResource);
    }
    
    /**
     */
    @Test
    public void getResourceByName() {
        Resource resource = getDefaultResource();
        expect(resourceService.getResourceByName("resourceName", null)).andReturn(resource);
        replay(resourceService);
        Resource foundResource = resourceType.getResourceByName("resourceName", null);
        verify(resourceService);
        assertEquals("Resource matches", resource, foundResource);
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void createResource() throws Exception {
        Resource resource = getDefaultResource();
        expect(resourceService.createResource(resource, null)).andReturn(resource);
        replay(resourceService);
        resourceType.createResource(resource, null);
        verify(resourceService);
    }

    /**
     * @throws Exception
     */
    @Test
    public void updateResource() throws Exception {
        Resource resource = getDefaultResource();
        resourceService.updateResource(resource, null);
        replay(resourceService);
        resourceType.updateResource(resource, null);
        verify(resourceService);
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void removeResource() throws Exception {
        Resource resource = getDefaultResource();
        expect(resourceService.removeResource(123, null)).andReturn(resource);
        replay(resourceService);
        Resource foundResource = resourceType.removeResource(123, null);
        verify(resourceService);
        assertEquals("Resource matches", resource, foundResource);
    }   
    
    private Resource getDefaultResource() {
        final Calendar date = Calendar.getInstance();
        return new AbstractResource(123, resourceType, 123, "resourceName", "resourceDescription", date, date) {};
    }
}