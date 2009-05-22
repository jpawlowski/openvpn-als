
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

import java.util.Collection;

import com.ovpnals.core.CoreEvent;
import com.ovpnals.jdbc.DataAccessException;
import com.ovpnals.jdbc.ResourceDatabase;
import com.ovpnals.policyframework.DuplicateResourceNameException;
import com.ovpnals.policyframework.NoPermissionException;
import com.ovpnals.policyframework.Resource;
import com.ovpnals.policyframework.ResourceType;
import com.ovpnals.security.SessionInfo;

/**
 * @param <T> 
 */
public class ResourceServiceAdapter<T extends Resource> implements ResourceService<T> {
    private final ResourceType<T> resourceType;

    /**
     * @param resourceType
     */
    public ResourceServiceAdapter(ResourceType<T> resourceType) {
        this.resourceType = resourceType;
    }

    public void setResourceDatabase(ResourceDatabase<T> resourceDatabase) {
    }

    public void setResourceType(ResourceType<T> resourceType) {
    }

    public void setCoreEventService(CoreEventService coreEventService) {
    }

    public void setPolicyService(PolicyService policyService) {
    }

    public int getCreateEventId() {
        return 0;
    }

    public void setCreateEventId(int createEventId) {
    }

    public int getEditEventId() {
        return 0;
    }

    public void setEditEventId(int editEventId) {
    }

    public int getRemoveEventId() {
        return 0;
    }

    public void setRemoveEventId(int removeEventId) {
    }

    public void destroy() throws Exception {
    }

    public void afterPropertiesSet() throws Exception {
    }

    public Collection<T> getResources(SessionInfo session) throws DataAccessException, NoPermissionException {
        try {
            return resourceType.getResources(session);
        } catch (Exception exp) {
            throw new DataAccessException("Failed to getResources", exp);
        }
    }

    public boolean isResourceNameInUse(String resourceName, SessionInfo session) throws DataAccessException {
        try {
            return resourceType.isResourceNameInUse(resourceName, session);
        } catch (Exception exp) {
            throw new DataAccessException("Failed to isResourceNameInUse", exp);
        }
    }

    public T getResourceById(int resourceId) throws DataAccessException {
        try {
            return resourceType.getResourceById(resourceId);
        } catch (Exception exp) {
            throw new DataAccessException("Failed to getResourceById", exp);
        }
    }

    public T getResourceByName(String resourceName, SessionInfo session) throws DataAccessException {
        try {
            return resourceType.getResourceByName(resourceName, session);
        } catch (Exception exp) {
            throw new DataAccessException("Failed to getResourceByName", exp);
        }
    }

    public T createResource(T resource, SessionInfo session) throws DataAccessException, DuplicateResourceNameException {
        try {
            return resourceType.createResource(resource, session);
        } catch (Exception exp) {
            throw new DataAccessException("Failed to createResource", exp);
        }
    }

    public void updateResource(T resource, SessionInfo session) throws DataAccessException, DuplicateResourceNameException,
                    NoPermissionException {
        try {
            resourceType.updateResource(resource, session);
        } catch (Exception exp) {
            throw new DataAccessException("Failed to updateResource", exp);
        }
    }

    public T removeResource(int resourceId, SessionInfo session) throws DataAccessException, NoPermissionException {
        try {
            return resourceType.removeResource(resourceId, session);
        } catch (Exception exp) {
            throw new DataAccessException("Failed to removeResource", exp);
        }
    }

    public void addEventProperties(CoreEvent event, T resource) {
    }
}
