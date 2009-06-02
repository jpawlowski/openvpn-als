
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Collection;

import org.junit.After;
import org.junit.Test;

import net.openvpn.als.policyframework.Resource;
import net.openvpn.als.testcontainer.AbstractTest;

/**
 * @param <T>
 */
public abstract class AbstractResourceDatabaseTest<T extends Resource> extends AbstractTest {
    private final ResourceDatabase<T> database;
    private final int selectedRealmId;

    /**
     * @param database
     * @throws Exception
     */
    public AbstractResourceDatabaseTest(ResourceDatabase<T> database) throws Exception {
        this.database = database;
        this.selectedRealmId = getSessionInfo().getRealmId();
    }

    protected final ResourceDatabase<T> getResourceDatabase() {
        return database;
    }

    protected final int getSelectedRealmId() {
        return selectedRealmId;
    }

    /**
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        Collection<T> resources = database.getResources(getSelectedRealmId());
        for (T resource : resources) {
            database.removeResource(resource.getResourceId());
        }
    }

    /**
     * @throws Exception
     */
    @Test
    public void noResources() throws Exception {
        Collection<T> resources = database.getResources(getSelectedRealmId());
        assertNotNull("Should not be null", resources);
        assertEquals("Should be no resources found", getInitialResourceCount(), resources.size());
    }

    /**
     * @throws Exception
     */
    @Test
    public void isUnknownResourceNameInUse() throws Exception {
        boolean isInUse = database.isResourceNameInUse("unknown", getSelectedRealmId());
        assertFalse("Resource name should not be in use", isInUse);
    }

    /**
     * @throws Exception
     */
    @Test
    public void isResourceNameInUse() throws Exception {
        T resource = getDefaultResource();
        int resourceId = database.insertResource(resource);

        boolean isInUse = database.isResourceNameInUse(resource.getResourceName(), getSelectedRealmId());
        assertTrue("Resource name should be in use", isInUse);
        database.removeResource(resourceId);
    }

    /**
     */
    @Test(expected = DataAccessException.class)
    public void getUnknownResourceId() {
        database.getResourceById(-1);
    }

    /**
     */
    @Test
    public void getResourceById() {
        T resource = getDefaultResource();
        int resourceId = database.insertResource(resource);

        T foundById = database.getResourceById(resourceId);
        assertResourceEquals(resourceId, resource, foundById);
        assertEquals("dateAmended should match", resource.getDateAmended(), foundById.getDateAmended());
        database.removeResource(resourceId);
    }

    /**
     * @throws Exception
     */
    @Test
    public void getUnknownResourceName() throws Exception {
        T resourceByName = database.getResourceByName("unknown", getSelectedRealmId());
        assertNull("Resource should not be found", resourceByName);
    }

    /**
     * @throws Exception
     */
    @Test
    public void getResourceByName() throws Exception {
        T resource = getDefaultResource();
        int resourceId = database.insertResource(resource);

        T foundById = database.getResourceByName(resource.getResourceName(), getSelectedRealmId());
        assertResourceEquals(resourceId, resource, foundById);
        assertEquals("dateAmended should match", resource.getDateAmended(), foundById.getDateAmended());
        database.removeResource(resourceId);
    }

    /**
     * @throws Exception
     */
    @Test
    public void insertInvalidResourceName() throws Exception {
        T resource = getDefaultResource();
        resource.setResourceName(null);

        try {
            database.insertResource(resource);
            fail("A DataAccessException should have been thrown as the name was null");
        } catch (DataAccessException e) {
            // nothing to do here
        }

        Collection<T> resources = database.getResources(getSelectedRealmId());
        assertNotNull("Should not be null", resources);
        assertEquals("Should be no resources found", getInitialResourceCount(), resources.size());
    }

    /**
     * @throws Exception
     */
    @Test
    public void insertResource() throws Exception {
        T resource = getDefaultResource();
        int resourceId = database.insertResource(resource);

        Collection<T> resources = database.getResources(getSelectedRealmId());
        assertNotNull("Should not be null", resources);
        assertEquals("Should be one resource found", getInitialResourceCount() + 1, resources.size());

        T foundById = database.getResourceById(resourceId);
        assertResourceEquals(resourceId, resource, foundById);
        database.removeResource(resourceId);
    }

    /**
     */
    @Test
    public void updateInvalidResourceName() {
        T resource = getDefaultResource();
        int resourceId = database.insertResource(resource);
        T foundById = database.getResourceById(resourceId);
        assertResourceEquals(resourceId, resource, foundById);

        try {
            foundById.setResourceDescription(null);
            database.updateResource(foundById);
            fail("A SQLException should have been thrown as the name was null");
        } catch (DataAccessException e) {
            // nothing to do here
        }

        T updatedResource = database.getResourceById(resourceId);
        assertResourceEquals(resourceId, resource, updatedResource);
        database.removeResource(resourceId);
    }

    /**
     */
    @Test
    public void updateDateAmendedHasNoEffect() {
        T resource = getDefaultResource();
        int resourceId = database.insertResource(resource);
        T foundById = database.getResourceById(resourceId);
        assertResourceEquals(resourceId, resource, foundById);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1000);
        foundById.setDateAmended(calendar);
        database.updateResource(foundById);

        T updatedResource = database.getResourceById(resourceId);
        assertNotSame("Date amended name should not have changed", calendar, updatedResource.getDateAmended());
        database.removeResource(resourceId);
    }

    /**
     * @throws Exception
     */
    @Test
    public void updateResource() throws Exception {
        int resourceId = database.insertResource(getDefaultResource());

        T foundResource = database.getResourceById(resourceId);
        foundResource.setResourceDescription("newDescription");
        database.updateResource(foundResource);

        assertResource(resourceId, foundResource);
        database.removeResource(resourceId);
    }

    protected void assertResource(int resourceId, T foundResource) throws Exception {
        T foundById = database.getResourceById(resourceId);
        assertResourceEquals(resourceId, foundResource, foundById);
        T foundByName = database.getResourceByName(foundResource.getResourceName(), getSelectedRealmId());
        assertResourceEquals(resourceId, foundResource, foundByName);
    }

    /**
     */
    @Test(expected = DataAccessException.class)
    public void removeUnknownResource() {
        database.removeResource(-1);
    }

    /**
     * @throws Exception
     */
    @Test
    public void removeResource() throws Exception {
        T resource = getDefaultResource();
        int resourceId = database.insertResource(resource);
        database.removeResource(resourceId);

        Collection<T> resources = database.getResources(getSelectedRealmId());
        assertNotNull("Should not be null", resources);
        assertEquals("Should be no resources found", getInitialResourceCount(), resources.size());
    }

    /**
     * @throws Exception
     */
    @Test
    public void removeResourceTwice() throws Exception {
        T resource = getDefaultResource();
        int resourceId = database.insertResource(resource);
        database.removeResource(resourceId);

        Collection<T> resources = database.getResources(getSelectedRealmId());
        assertNotNull("Should not be null", resources);
        assertEquals("Should be no resources found", getInitialResourceCount(), resources.size());

        try {
            database.removeResource(resourceId);
            fail("Removing an unknown resource should have caused an exception");
        } catch (DataAccessException e) {
            // nothing to do
        }
    }

    /**
     * @throws Exception
     */
    @Test
    public void removeResourceWhenTwoExist() throws Exception {
        int resourceOneId = database.insertResource(getDefaultResource());
        T foundResource = database.getResourceById(resourceOneId);
        int resourceTwoId = database.insertResource(getDefaultResource());

        Collection<T> resources = database.getResources(getSelectedRealmId());
        assertNotNull("Should not be null", resources);
        assertEquals("Should be two resources found", getInitialResourceCount() + 2, resources.size());

        database.removeResource(resourceTwoId);

        resources = database.getResources(getSelectedRealmId());
        assertNotNull("Should not be null", resources);
        assertEquals("Should be one resource found", getInitialResourceCount() + 1, resources.size());

        T resourceOne = database.getResourceById(resourceOneId);
        assertResourceEquals(resourceOneId, foundResource, resourceOne);
        assertEquals("dateAmended should match", foundResource.getDateAmended(), resourceOne.getDateAmended());
    }

    protected void assertResourceEquals(int resourceId, T resource, T foundResource) {
        assertEquals("resourceType should match", resource.getResourceType(), foundResource.getResourceType());
        assertEquals("resourceId should match", resourceId, foundResource.getResourceId());
        assertEquals("resourceName should match", resource.getResourceName(), foundResource.getResourceName());
        assertEquals("resourceDescription should match", resource.getResourceDescription(), foundResource.getResourceDescription());
        assertEquals("dataCreated should match", resource.getDateCreated(), foundResource.getDateCreated());
    }

    /**
     * Override this method if, by default, there are entries in the database.
     * @return resource count, representing how many entries are expected by default.
     */
    protected int getInitialResourceCount() {
        return 0;
    }

    protected final T getDefaultResource() {
        return getDefaultResource(getSelectedRealmId());
    }
    
    protected abstract T getDefaultResource(int selectedRealmId);
}