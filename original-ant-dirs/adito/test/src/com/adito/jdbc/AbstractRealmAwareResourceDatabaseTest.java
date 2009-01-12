
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
			
package com.adito.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import com.adito.policyframework.Resource;

/**
 * @param <T> 
 */
public abstract class AbstractRealmAwareResourceDatabaseTest<T extends Resource> extends AbstractResourceDatabaseTest<T> {
    /**
     * @param database
     * @throws Exception 
     */
    public AbstractRealmAwareResourceDatabaseTest(ResourceDatabase<T> database) throws Exception {
        super(database);
    }

    /**
     * @throws Exception 
     */
    @Test
    public void noResourcesInRealm() throws Exception {
        T resource = getDefaultResource();
        int resourceId = getResourceDatabase().insertResource(resource);

        try {
            Collection<T> createdRealm = getResourceDatabase().getResources(getSelectedRealmId());
            assertNotNull("Should not be null", createdRealm);
            assertEquals("Should be one resource found", createdRealm.size(), getInitialResourceCount() + 1);

            Collection<T> emptyRealm = getResourceDatabase().getResources(123);
            assertNotNull("Should not be null", emptyRealm);
            assertEquals("Should be no resources found", emptyRealm.size(), getInitialResourceCount());
        } finally {
            getResourceDatabase().removeResource(resourceId);
        }
    }
    
    /**
     * @throws Exception 
     */
    @Test
    public void isUnknownResourceNameInUseInRealm() throws Exception {
        boolean isInUseCreatedRealm = getResourceDatabase().isResourceNameInUse("resourceName", getSelectedRealmId());
        assertFalse("Resource name should not be in use", isInUseCreatedRealm);
        
        boolean isInUseEmptyRealm = getResourceDatabase().isResourceNameInUse("resourceName", 123);
        assertFalse("Resource name should not be in use", isInUseEmptyRealm);
    }
    
    /**
     * @throws Exception 
     */
    @Test
    public void isResourceNameInUseInRealm() throws Exception {
        T resource = getDefaultResource();
        int resourceId = getResourceDatabase().insertResource(resource);
        
        boolean isInUseCreatedRealm = getResourceDatabase().isResourceNameInUse(resource.getResourceName(), getSelectedRealmId());
        assertTrue("Resource name should be in use", isInUseCreatedRealm);
        
        boolean isInUseEmptyRealm = getResourceDatabase().isResourceNameInUse(resource.getResourceName(), 123);
        assertFalse("Resource name should not be in use", isInUseEmptyRealm);
        
        getResourceDatabase().removeResource(resourceId);
        
        isInUseCreatedRealm = getResourceDatabase().isResourceNameInUse(resource.getResourceName(), getSelectedRealmId());
        assertFalse("Resource name should not be in use", isInUseCreatedRealm);
    }
    
    /**
     * @throws Exception 
     */
    @Test
    public void getUnknownResourceNameInRealm() throws Exception {
        T resourceByName = getResourceDatabase().getResourceByName("unknown", 123);
        assertNull("Resource should not be found", resourceByName);
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void getResourceNameInRealm() throws Exception {
        T resource = getDefaultResource();
        int resourceId = getResourceDatabase().insertResource(resource);

        try {
            T foundById = getResourceDatabase().getResourceByName(resource.getResourceName(), getSelectedRealmId());
            assertResourceEquals(resourceId, resource, foundById);
            assertEquals("dateAmended should match", resource.getDateAmended(), foundById.getDateAmended());
            
            T resourceByName = getResourceDatabase().getResourceByName(resource.getResourceName(), 123);
            assertNull("Resource should not be found", resourceByName);
        } finally {
            getResourceDatabase().removeResource(resourceId);
        }
    }

    /**
     */
    @Test
    public void insertResourceInRealm() {
        final int secondRealmId = 123;
        T resourceOne = getDefaultResource();
        int resourceOneId = getResourceDatabase().insertResource(resourceOne);
        T resourceTwo = getDefaultResource(secondRealmId);
        int resourceTwoId = getResourceDatabase().insertResource(resourceTwo);

        Collection<T> createdRealm = getResourceDatabase().getResources(getSelectedRealmId());
        assertNotNull("Should not be null", createdRealm);
        assertEquals("Should be one resource found", createdRealm.size(), getInitialResourceCount() + 1);

        Collection<T> emptyRealm = getResourceDatabase().getResources(secondRealmId);
        assertNotNull("Should not be null", emptyRealm);
        assertEquals("Should be one resource found", emptyRealm.size(), getInitialResourceCount() + 1);
        
        T foundOneById = getResourceDatabase().getResourceById(resourceOneId);
        assertResourceEquals(resourceOneId, resourceOne, foundOneById);
        getResourceDatabase().removeResource(resourceOneId);
        
        T foundTwoById = getResourceDatabase().getResourceById(resourceTwoId);
        assertResourceEquals(resourceTwoId, resourceTwo, foundTwoById);
        getResourceDatabase().removeResource(resourceTwoId);
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void removeResourceWhenTwoExistInRealm() throws Exception {
        final int firstRealmId = 123;
        int resourceOneId = getResourceDatabase().insertResource(getDefaultResource(firstRealmId));
        T foundResource = getResourceDatabase().getResourceById(resourceOneId);
        int resourceTwoId = getResourceDatabase().insertResource(getDefaultResource());

        getResourceDatabase().removeResource(resourceTwoId);

        Collection<T> firstRealm = getResourceDatabase().getResources(firstRealmId);
        assertNotNull("Should not be null", firstRealm);
        assertEquals("Should be one resource found", firstRealm.size(), getInitialResourceCount() + 1);

        Collection<T> secondRealm = getResourceDatabase().getResources(getSelectedRealmId());
        assertNotNull("Should not be null", secondRealm);
        assertEquals("Should be no resources found", secondRealm.size(), getInitialResourceCount());

        T resourceOne = getResourceDatabase().getResourceById(resourceOneId);
        assertResourceEquals(resourceOneId, foundResource, resourceOne);
        assertEquals("dateAmended should match", foundResource.getDateAmended(), resourceOne.getDateAmended());

        getResourceDatabase().removeResource(resourceOneId);
    }
        
    protected void assertResourceEquals(int resourceId, T resource, T foundResource) {
        super.assertResourceEquals(resourceId, resource, foundResource);
        assertEquals("realmId should match", resource.getRealmID(), foundResource.getRealmID());
    }
}