
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

import com.ovpnals.policyframework.DefaultResourceType;
import com.ovpnals.policyframework.Resource;
import com.ovpnals.security.SessionInfo;

/**
 * Implementation of a {@link com.ovpnals.policyframework.ResourceType}
 * which provides all the plumbing code we need.  All this needs is a ResourceService.
 * @param <T>
 */
public class ServiceResourceType<T extends Resource> extends DefaultResourceType<T> {
    /** Use serialVersionUID for interoperability */
    private static final long serialVersionUID = -5055355572584957763L;
    private transient ResourceService<T> resourceService;
    
    /**
     * @param id
     * @param bundle
     * @param permissionClass
     */
    public ServiceResourceType(int id, String bundle, String permissionClass) {
        super(id, bundle, permissionClass);
    }
    
    /**
     * @param resourceService
     */
    public void setResourceService(ResourceService<T> resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public Collection<T> getResources(SessionInfo session) throws Exception {
        return resourceService.getResources(session);
    }

    @Override
    public boolean isResourceNameInUse(String resourceName, SessionInfo session) {
        return resourceService.isResourceNameInUse(resourceName, session);
    }

    @Override
    public T getResourceById(int resourceId) {
        return resourceService.getResourceById(resourceId);
    }

    @Override
    public T getResourceByName(String resourceName, SessionInfo session) {
        return resourceService.getResourceByName(resourceName, session);
    }
    
    @Override
    public T createResource(T resource, SessionInfo session) throws Exception {
        return resourceService.createResource(resource, session);
    }

    @Override
    public void updateResource(T resource, SessionInfo session) throws Exception {
        resourceService.updateResource(resource, session);
    }

    @Override
    public T removeResource(int resourceId, SessionInfo session) throws Exception {
        return resourceService.removeResource(resourceId, session);
    }
}