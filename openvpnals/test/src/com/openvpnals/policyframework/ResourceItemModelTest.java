
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
			
package net.openvpn.als.policyframework;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

import net.openvpn.als.table.TableItemModel;

/**
 */
public class ResourceItemModelTest {
    
    /**
     */
    @Test
    public void modelSetup() {
        TableItemModel<ResourceItem> model = new ResourceItemModel<ResourceItem>("id");
        assertEquals("Has one column", 1, model.getColumnCount());
        assertEquals("Has correct column name", "name", model.getColumnName(0));
        assertEquals("Has correct column type", String.class, model.getColumnClass(0));
        assertTrue("Model is empty", model.getEmpty());
        assertEquals("Model has no rows", 0, model.getRowCount());
        assertEquals("Model has no items", 0, model.getItems().size());
    }
    
    /**
     */
    @Test(expected=IndexOutOfBoundsException.class)
    public void itemIndexOutOfBounds() {
        TableItemModel<ResourceItem> model = new ResourceItemModel<ResourceItem>("id");
        model.getItem(1);
    }

    /**
     */
    @Test(expected=IndexOutOfBoundsException.class)
    public void valueIndexOutOfBounds() {
        TableItemModel<ResourceItem> model = new ResourceItemModel<ResourceItem>("id");
        model.getValue(1, 0);
    }
    
    /**
     */
    @Test
    public void addTableItem() {
        final Resource resource = getResource();
        TableItemModel<ResourceItem> model = new ResourceItemModel<ResourceItem>("id");
        
        final ResourceItem<Resource> resourceItem = new ResourceItem<Resource>(resource, Collections.<Policy>emptyList());
        model.addItem(resourceItem);
        
        assertFalse("Model is not empty", model.getEmpty());
        assertEquals("Model has one row", 1, model.getRowCount());
        assertEquals("Model has one item", 1, model.getItems().size());
        assertTrue("Model contains table item", model.contains(resourceItem));
        ResourceItem item = model.getItem(0);
        Resource foundResource = item.getResource();
        assertEquals("Resources are the same", resource, foundResource);
        
        Object value = model.getValue(0, 0);
        assertNotNull("Value is not null", value);
        assertTrue("Value is a String", value instanceof String);
        assertEquals("Value is resource name", resource.getResourceDisplayName(), value);
    }
    
    /**
     */
    @Test
    public void clearTable() {
        final Resource resource = getResource();
        TableItemModel<ResourceItem> model = new ResourceItemModel<ResourceItem>("id");
        model.addItem(new ResourceItem<Resource>(resource, Collections.<Policy>emptyList()));
        model.clear();
        
        assertTrue("Model is empty", model.getEmpty());
        assertEquals("Model has no rows", 0, model.getRowCount());
        assertEquals("Model has no items", 0, model.getItems().size());
    }
    
    private static Resource getResource() {
        DefaultResourceType resourceType = new DefaultResourceType(1, "", "");
        Resource resource = new AbstractResource(0, resourceType, 0, "", "", null, null){};
        resource.setResourceName("resourceName");
        return resource;
    }
}