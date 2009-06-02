
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
			
package net.openvpn.als.services;

import java.util.Collection;

import net.openvpn.als.core.CoreEvent;
import net.openvpn.als.jdbc.DataAccessException;
import net.openvpn.als.jdbc.ResourceDatabase;
import net.openvpn.als.policyframework.DuplicateResourceNameException;
import net.openvpn.als.policyframework.NoPermissionException;
import net.openvpn.als.policyframework.Resource;
import net.openvpn.als.policyframework.ResourceType;
import net.openvpn.als.security.SessionInfo;

/**
 * @param <T>
 */
public interface ResourceService<T extends Resource> extends InitializingBean, DisposableBean {
    
    /**
     * @param resourceDatabase
     */
    void setResourceDatabase(ResourceDatabase<T> resourceDatabase);
    
    /**
     * @param resourceType
     */
    void setResourceType(ResourceType<T> resourceType);

    /**
     * @param coreEventService
     */
    void setCoreEventService(CoreEventService coreEventService);

    /**
     * @param policyService
     */
    void setPolicyService(PolicyService policyService);
    
    /**
     * @return int
     */
    int getCreateEventId();

    /**
     * @param createEventId
     */
    void setCreateEventId(int createEventId);

    /**
     * @return int
     */
    int getEditEventId();
    
    /**
     * @param editEventId
     */
    void setEditEventId(int editEventId);

    /**
     * @return int
     */
    int getRemoveEventId();
    
    /**
     * @param removeEventId
     */
    void setRemoveEventId(int removeEventId);

    /**
     * @param session
     * @return Collection
     * @throws DataAccessException 
     * @throws NoPermissionException 
     */
    Collection<T> getResources(SessionInfo session) throws DataAccessException, NoPermissionException;

    /**
     * @param resourceName
     * @param session
     * @return boolean
     * @throws DataAccessException 
     */
    boolean isResourceNameInUse(String resourceName, SessionInfo session) throws DataAccessException;

    /**
     * @param resourceId
     * @return T
     * @throws DataAccessException 
     */
    T getResourceById(int resourceId) throws DataAccessException;

    /**
     * @param resourceName
     * @param session
     * @return T
     * @throws DataAccessException 
     */
    T getResourceByName(String resourceName, SessionInfo session) throws DataAccessException;

    /**
     * @param resource
     * @param session
     * @return T
     * @throws DataAccessException 
     * @throws DuplicateResourceNameException 
     */
    T createResource(T resource, SessionInfo session) throws DataAccessException, DuplicateResourceNameException;

    /**
     * @param resource
     * @param session
     * @throws DataAccessException 
     * @throws DuplicateResourceNameException 
     * @throws NoPermissionException
     */
    void updateResource(T resource, SessionInfo session) throws DataAccessException, DuplicateResourceNameException, NoPermissionException;

    /**
     * @param resourceId
     * @param session
     * @return T
     * @throws DataAccessException 
     * @throws NoPermissionException
     */
    T removeResource(int resourceId, SessionInfo session) throws DataAccessException, NoPermissionException;

    /**
     * This hook method is intended for sub-classes who wish to add 
     * properties to the event e.g. resourceName, resourceDescription.
     * @param event
     * @param resource
     */
    void addEventProperties(CoreEvent event, T resource);
}