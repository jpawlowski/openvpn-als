
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

import java.util.Collection;

import com.adito.core.CoreEvent;
import com.adito.jdbc.DataAccessException;
import com.adito.jdbc.ResourceDatabase;
import com.adito.policyframework.DuplicateResourceNameException;
import com.adito.policyframework.NoPermissionException;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.Resource;
import com.adito.policyframework.ResourceChangeEvent;
import com.adito.policyframework.ResourceDeleteEvent;
import com.adito.policyframework.ResourceType;
import com.adito.security.SessionInfo;

/**
 * @param <T>
 */
public class ResourceServiceImpl<T extends Resource> implements ResourceService<T> {
    private ResourceDatabase<T> resourceDatabase;
    private ResourceType<T> resourceType;
    private CoreEventService coreEventService;
    private PolicyService policyService;
    private int createEventId;
    private int editEventId;
    private int removeEventId;

    public final void setResourceDatabase(ResourceDatabase<T> resourceDatabase) {
        this.resourceDatabase = resourceDatabase;
    }

    public final void setResourceType(ResourceType<T> resourceType) {
        this.resourceType = resourceType;
    }

    public final void setCoreEventService(CoreEventService coreEventService) {
        this.coreEventService = coreEventService;
    }

    public final void setPolicyService(PolicyService policyService) {
        this.policyService = policyService;
    }

    public final int getCreateEventId() {
        return createEventId;
    }

    public final void setCreateEventId(int createEventId) {
        this.createEventId = createEventId;
    }

    public final int getEditEventId() {
        return editEventId;
    }

    public final void setEditEventId(int editEventId) {
        this.editEventId = editEventId;
    }

    public final int getRemoveEventId() {
        return removeEventId;
    }

    public final void setRemoveEventId(int removeEventId) {
        this.removeEventId = removeEventId;
    }

    public void afterPropertiesSet() throws Exception {
        if (resourceDatabase == null) {
            throw new IllegalArgumentException("resourceDatabase must be set.");
        }

        if (resourceType == null) {
            throw new IllegalArgumentException("resourceType must be set.");
        }

        if (coreEventService == null) {
            throw new IllegalArgumentException("coreEventService must be set.");
        }

        if (policyService == null) {
            throw new IllegalArgumentException("policyService must be set.");
        }
    }
    
    public void destroy() throws Exception {
    }

    public Collection<T> getResources(SessionInfo session) throws NoPermissionException {
        int selectedRealmId = getSelectedRealm(session);
        return resourceDatabase.getResources(selectedRealmId);
    }

    public final boolean isResourceNameInUse(String resourceName, SessionInfo session) {
        int selectedRealmId = getSelectedRealm(session);
        return resourceDatabase.isResourceNameInUse(resourceName, selectedRealmId);
    }

    public T getResourceById(int resourceId) {
        return resourceDatabase.getResourceById(resourceId);
    }

    public T getResourceByName(String resourceName, SessionInfo session) {
        int selectedRealmId = getSelectedRealm(session);
        return resourceDatabase.getResourceByName(resourceName, selectedRealmId);
    }

    public T createResource(T resource, SessionInfo session) throws DuplicateResourceNameException {
        try {
            assertUniqueResourceName(resource, session);
            int resourceId = resourceDatabase.insertResource(resource);
            fireCoreEvent(buildChangeEvent(getCreateEventId(), resource, session));
            return getResourceById(resourceId);
        } catch (DataAccessException dae) {
            fireCoreEvent(new ResourceChangeEvent(this, getCreateEventId(), session, dae));
            throw dae;
        } catch (DuplicateResourceNameException drne) {
            fireCoreEvent(new ResourceChangeEvent(this, getCreateEventId(), session, drne));
            throw drne;
        }
    }

    public void updateResource(T resource, SessionInfo session) throws DuplicateResourceNameException, NoPermissionException {
        try {
            T oldResource = getResourceById(resource.getResourceId());
            if (!oldResource.getResourceName().equals(resource.getResourceName())) {
                assertUniqueResourceName(resource, session);
            }

            assertPermission(PolicyConstants.PERM_EDIT_AND_ASSIGN, session);
            resourceDatabase.updateResource(resource);
            fireCoreEvent(buildChangeEvent(getEditEventId(), resource, session));
        } catch (DataAccessException dae) {
            fireCoreEvent(new ResourceChangeEvent(this, getEditEventId(), session, dae));
            throw dae;
        } catch (DuplicateResourceNameException drne) {
            fireCoreEvent(new ResourceChangeEvent(this, getEditEventId(), session, drne));
            throw drne;
        } catch (NoPermissionException npe) {
            fireCoreEvent(new ResourceChangeEvent(this, getEditEventId(), session, npe));
            throw npe;
        }
    }

    private void assertUniqueResourceName(T resource, SessionInfo session) throws DuplicateResourceNameException {
        if (isResourceNameInUse(resource.getResourceName(), session)) {
            throw new DuplicateResourceNameException(resource);
        }
    }

    public T removeResource(int resourceId, SessionInfo session) throws NoPermissionException {
        try {
            assertPermission(PolicyConstants.PERM_DELETE, session);
            T removeResource = resourceDatabase.removeResource(resourceId);
            fireCoreEvent(buildDeleteEvent(getRemoveEventId(), removeResource, session));
            return removeResource;
        } catch (DataAccessException dae) {
            fireCoreEvent(new ResourceDeleteEvent(this, getRemoveEventId(), session, dae));
            throw dae;
        } catch (NoPermissionException npe) {
            fireCoreEvent(new ResourceDeleteEvent(this, getRemoveEventId(), session, npe));
            throw npe;
        }
    }

    protected int getSelectedRealm(SessionInfo session) {
        try {
            return session.getRealmId();
        } catch (Exception exp) {
            throw new DataAccessException("Failure to retrieve selectedRealmId", exp);
        }
    }
    
    protected final void assertPermission(Permission permission, SessionInfo session) throws NoPermissionException {
        policyService.checkPermission(resourceType, permission, session);
    }

    protected final void fireCoreEvent(CoreEvent event) {
        coreEventService.fireCoreEvent(event);
    }

    protected final CoreEvent buildChangeEvent(int eventId, T resource, SessionInfo session) {
        ResourceChangeEvent event = new ResourceChangeEvent(this, eventId, resource, session);
        addEventProperties(event, resource);
        return event;
    }

    protected final CoreEvent buildDeleteEvent(int eventId, T resource, SessionInfo session) {
        ResourceDeleteEvent event = new ResourceDeleteEvent(this, eventId, resource, session);
        addEventProperties(event, resource);
        return event;
    }

    public void addEventProperties(CoreEvent event, T resource) {
    }
}