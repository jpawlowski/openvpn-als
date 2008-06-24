
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
			
package com.adito.services;

import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Collections;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import com.adito.core.CoreEvent;
import com.adito.jdbc.DataAccessException;
import com.adito.jdbc.ResourceDatabase;
import com.adito.policyframework.AbstractResource;
import com.adito.policyframework.CoreEventArgumentMatcher;
import com.adito.policyframework.DuplicateResourceNameException;
import com.adito.policyframework.NoPermissionException;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.Resource;
import com.adito.policyframework.ResourceChangeEvent;
import com.adito.policyframework.ResourceDeleteEvent;
import com.adito.policyframework.ResourceType;
import com.adito.security.SessionInfo;

/**
 */
public class ResourceServiceTest {
    private final ResourceService<Resource> resourceService;
    private final IMocksControl mocksControl;
    private final ResourceDatabase<Resource> resourceDatabase;
    private final ResourceType<Resource> resourceType;
    private final CoreEventService coreEventService;
    private final PolicyService policyService;

    /**
     */
    @SuppressWarnings("unchecked")
    public ResourceServiceTest() {
        resourceService = new ResourceServiceImpl<Resource>() {
            @Override
            protected int getSelectedRealm(SessionInfo session) {
                return 1;
            }
        };
        mocksControl = createStrictControl();

        resourceDatabase = mocksControl.createMock(ResourceDatabase.class);
        resourceService.setResourceDatabase(resourceDatabase);
        resourceType = mocksControl.createMock(ResourceType.class);
        resourceService.setResourceType(resourceType);
        coreEventService = mocksControl.createMock(CoreEventService.class);
        resourceService.setCoreEventService(coreEventService);
        policyService = mocksControl.createMock(PolicyService.class);
        resourceService.setPolicyService(policyService);
    }

    /**
     */
    @Before
    public void setUp() {
        mocksControl.reset();
    }

    /**
     * @throws Exception 
     */
    @Test(expected = IllegalArgumentException.class)
    public void nullResourceDatabase() throws Exception {
        resourceService.setResourceDatabase(null);
        resourceService.afterPropertiesSet();
    }
    
    /**
     * @throws Exception 
     * @throws Exception 
     */
    @Test(expected = IllegalArgumentException.class)
    public void nullResourceType() throws Exception {
        resourceService.setResourceType(null);
        resourceService.afterPropertiesSet();
    }
    
    /**
     * @throws Exception 
     * @throws Exception 
     */
    @Test(expected = IllegalArgumentException.class)
    public void nullCoreEventService() throws Exception {
        resourceService.setCoreEventService(null);
        resourceService.afterPropertiesSet();
    }
    
    /**
     * @throws Exception 
     * @throws Exception 
     */
    @Test(expected = IllegalArgumentException.class)
    public void nullPolicyService() throws Exception {
        resourceService.setPolicyService(null);
        resourceService.afterPropertiesSet();
    }

    /**
     */
    @Test
    public void setEventIds() {
        resourceService.setCreateEventId(1);
        resourceService.setEditEventId(2);
        resourceService.setRemoveEventId(3);
        assertEquals("Create event id is correct", 1, resourceService.getCreateEventId());
        assertEquals("Edit event id is correct", 2, resourceService.getEditEventId());
        assertEquals("Remove event id is correct", 3, resourceService.getRemoveEventId());
    }
    
    /**
     * @throws NoPermissionException
     */
    @Test
    public void getResources() throws NoPermissionException {
        expect(resourceDatabase.getResources(1)).andReturn(Collections.<Resource> emptyList());
        mocksControl.replay();

        resourceService.getResources(null);
        mocksControl.verify();
    }

    /**
     */
    @Test
    public void isResourceNameInUse() {
        Resource resource = getDefaultResource();
        String resourceName = resource.getResourceName();

        expect(resourceDatabase.isResourceNameInUse(resourceName, 1)).andReturn(false);
        mocksControl.replay();

        resourceService.isResourceNameInUse(resourceName, null);
        mocksControl.verify();
    }

    /**
     */
    @Test
    public void getResourceById() {
        Resource resource = getDefaultResource();
        int resourceId = resource.getResourceId();

        expect(resourceDatabase.getResourceById(resourceId)).andReturn(resource);
        mocksControl.replay();

        resourceService.getResourceById(resourceId);
        mocksControl.verify();
    }

    /**
     */
    @Test
    public void getResourceByName() {
        Resource resource = getDefaultResource();
        String resourceName = resource.getResourceName();

        expect(resourceDatabase.getResourceByName(resourceName, 1)).andReturn(resource);
        mocksControl.replay();

        resourceService.getResourceByName(resourceName, null);
        mocksControl.verify();
    }

    /**
     * @throws DuplicateResourceNameException 
     */
    @Test
    public void createResource() throws DuplicateResourceNameException {
        Resource resource = getDefaultResource();

        expect(resourceDatabase.isResourceNameInUse(resource.getResourceName(), 1)).andReturn(false);
        expect(resourceDatabase.insertResource(resource)).andReturn(1);
        CoreEvent changeEvent = getChangeEvent(resourceService.getCreateEventId(), CoreEvent.STATE_SUCCESSFUL);
        coreEventService.fireCoreEvent(eqCoreEvent(changeEvent));
        expect(resourceDatabase.getResourceById(1)).andReturn(resource);
        mocksControl.replay();

        resourceService.createResource(resource, null);
        mocksControl.verify();
    }
    
    /**
     * @throws NoPermissionException 
     */
    @Test
    public void createDuplicateResource() throws NoPermissionException {
        Resource resource = getDefaultResource();

        expect(resourceDatabase.isResourceNameInUse(resource.getResourceName(), 1)).andReturn(true);
        CoreEvent changeEvent = getChangeEvent(resourceService.getCreateEventId(), CoreEvent.STATE_UNSUCCESSFUL);
        coreEventService.fireCoreEvent(eqCoreEvent(changeEvent));
        mocksControl.replay();

        try {
            resourceService.createResource(resource, null);
        } catch (DuplicateResourceNameException e) {
            // nothing to do
        }
        mocksControl.verify();
    }


    /**
     * @throws DuplicateResourceNameException 
     */
    @Test
    public void createResourceWithDataAccessException() throws DuplicateResourceNameException {
        Resource resource = getDefaultResource();

        expect(resourceDatabase.isResourceNameInUse(resource.getResourceName(), 1)).andReturn(false);
        expect(resourceDatabase.insertResource(resource)).andThrow(
            new DataAccessException("Failed to insert resource."));
        CoreEvent changeEvent = getChangeEvent(resourceService.getCreateEventId(), CoreEvent.STATE_UNSUCCESSFUL);
        coreEventService.fireCoreEvent(eqCoreEvent(changeEvent));
        mocksControl.replay();

        try {
            resourceService.createResource(resource, null);
            fail("Should have thrown an exception");
        } catch (DataAccessException e) {
            // nothing to do
        }

        mocksControl.verify();
    }

    /**
     * @throws DuplicateResourceNameException 
     * @throws NoPermissionException
     */
    @Test
    public void updateResource() throws DuplicateResourceNameException, NoPermissionException {
        Resource resource = getDefaultResource();

        expect(resourceDatabase.getResourceById(resource.getResourceId())).andReturn(resource);
        policyService.checkPermission(resourceType, PolicyConstants.PERM_EDIT_AND_ASSIGN, (SessionInfo) null);
        resourceDatabase.updateResource(resource);
        CoreEvent changeEvent = getChangeEvent(resourceService.getEditEventId(), CoreEvent.STATE_SUCCESSFUL);
        coreEventService.fireCoreEvent(eqCoreEvent(changeEvent));
        mocksControl.replay();

        resourceService.updateResource(resource, null);
        mocksControl.verify();
    }
    
    /**
     * @throws NoPermissionException 
     */
    @Test
    public void updateDuplicateResource() throws NoPermissionException {
        Resource resource = getDefaultResource();
        Resource updatedResource = getDefaultResource();
        updatedResource.setResourceName("updatedResourceName");
        
        expect(resourceDatabase.getResourceById(resource.getResourceId())).andReturn(updatedResource);
        expect(resourceDatabase.isResourceNameInUse(resource.getResourceName(), 1)).andReturn(true);
        CoreEvent changeEvent = getChangeEvent(resourceService.getEditEventId(), CoreEvent.STATE_UNSUCCESSFUL);
        coreEventService.fireCoreEvent(eqCoreEvent(changeEvent));
        mocksControl.replay();

        try {
            resourceService.updateResource(resource, null);
        } catch (DuplicateResourceNameException e) {
            // nothing to do
        }
        mocksControl.verify();
    }

    /**
     * @throws DuplicateResourceNameException 
     * @throws NoPermissionException
     */
    @Test
    public void updateResourceWithNoPermissionException() throws DuplicateResourceNameException, NoPermissionException {
        Resource resource = getDefaultResource();
        
        expect(resourceDatabase.getResourceById(resource.getResourceId())).andReturn(resource);
        policyService.checkPermission(resourceType, PolicyConstants.PERM_EDIT_AND_ASSIGN, (SessionInfo) null);
        expectLastCall().andThrow(new NoPermissionException("Failed to update resource."));
        CoreEvent changeEvent = getChangeEvent(resourceService.getEditEventId(), CoreEvent.STATE_UNSUCCESSFUL);
        coreEventService.fireCoreEvent(eqCoreEvent(changeEvent));
        mocksControl.replay();
        
        try {
            resourceService.updateResource(resource, null);
            fail("Should have thrown an exception");
        } catch (NoPermissionException e) {
            // nothing to do
        }
        
        mocksControl.verify();
    }
    
    /**
     * @throws DuplicateResourceNameException 
     * @throws NoPermissionException
     */
    @Test
    public void updateResourceWithDataAccessException() throws DuplicateResourceNameException, NoPermissionException {
        Resource resource = getDefaultResource();

        expect(resourceDatabase.getResourceById(resource.getResourceId())).andReturn(resource);
        policyService.checkPermission(resourceType, PolicyConstants.PERM_EDIT_AND_ASSIGN, (SessionInfo) null);
        resourceDatabase.updateResource(resource);
        expectLastCall().andThrow(new DataAccessException("Failed to update resource."));
        CoreEvent changeEvent = getChangeEvent(resourceService.getEditEventId(), CoreEvent.STATE_UNSUCCESSFUL);
        coreEventService.fireCoreEvent(eqCoreEvent(changeEvent));
        mocksControl.replay();

        try {
            resourceService.updateResource(resource, null);
            fail("Should have thrown an exception");
        } catch (DataAccessException e) {
            // nothing to do
        }

        mocksControl.verify();
    }
    
    /**
     * @throws NoPermissionException
     */
    @Test
    public void removeResource() throws NoPermissionException {
        Resource resource = getDefaultResource();

        policyService.checkPermission(resourceType, PolicyConstants.PERM_DELETE, (SessionInfo) null);
        expect(resourceDatabase.removeResource(resource.getResourceId())).andReturn(resource);
        CoreEvent deleteEvent = getDeleteEvent(resourceService.getRemoveEventId(), CoreEvent.STATE_SUCCESSFUL);
        coreEventService.fireCoreEvent(eqCoreEvent(deleteEvent));
        mocksControl.replay();

        resourceService.removeResource(resource.getResourceId(), null);
        mocksControl.verify();
    }

    /**
     * @throws NoPermissionException
     */
    @Test
    public void removeResourceWithNoPermissionException() throws NoPermissionException {
        Resource resource = getDefaultResource();

        policyService.checkPermission(resourceType, PolicyConstants.PERM_DELETE, (SessionInfo) null);
        expectLastCall().andThrow(new NoPermissionException("Failed to remove resource."));
        CoreEvent deleteEvent = getDeleteEvent(resourceService.getRemoveEventId(), CoreEvent.STATE_UNSUCCESSFUL);
        coreEventService.fireCoreEvent(eqCoreEvent(deleteEvent));
        mocksControl.replay();

        try {
            resourceService.removeResource(resource.getResourceId(), null);
            fail("Should have thrown an exception");
        } catch (NoPermissionException e) {
            // nothing to do
        }

        mocksControl.verify();
    }
    
    /**
     * @throws NoPermissionException
     */
    @Test
    public void removeResourceWithDataAccessException() throws NoPermissionException {
        Resource resource = getDefaultResource();

        policyService.checkPermission(resourceType, PolicyConstants.PERM_DELETE, (SessionInfo) null);
        resourceDatabase.removeResource(resource.getResourceId());
        expectLastCall().andThrow(new DataAccessException("Failed to remove resource."));
        CoreEvent deleteEvent = getDeleteEvent(resourceService.getRemoveEventId(), CoreEvent.STATE_UNSUCCESSFUL);
        coreEventService.fireCoreEvent(eqCoreEvent(deleteEvent));
        mocksControl.replay();

        try {
            resourceService.removeResource(resource.getResourceId(), null);
            fail("Should have thrown an exception");
        } catch (DataAccessException e) {
            // nothing to do
        }

        mocksControl.verify();
    }

    private Resource getDefaultResource() {
        final Calendar date = Calendar.getInstance();
        return new AbstractResource(123, resourceType, 123, "resourceName", "resourceDescription", date, date) {};
    }

    private CoreEvent getChangeEvent(int eventId, int status) {
        return new ResourceChangeEvent(this, eventId, getDefaultResource(), null, status);
    }

    private CoreEvent getDeleteEvent(int eventId, int status) {
        return new ResourceDeleteEvent(this, eventId, getDefaultResource(), null, status);
    }
    
    private static CoreEvent eqCoreEvent(CoreEvent event) {
        EasyMock.reportMatcher(new CoreEventArgumentMatcher(event));
        return null;
    }
}