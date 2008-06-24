
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

import java.util.Collection;

import com.adito.security.SessionInfo;

/**
 * Adito has the concept of resources. Resources are generally things
 * that may be assigned to a user to allow them to perform some action.
 * <p>
 * All resources must specify their type, and this object describes that type.
 * The resource type must be registered with the system using
 * {@link com.adito.policyframework.PolicyDatabase#registerResourceType(ResourceType)},
 * this allows extensions to define new resource types.
 * <p>
 * Each resource type contains a list of permissions of the specified
 * <strong>class</strong>, current permission classes include
 * {@link com.adito.policyframework.PolicyConstants#DELEGATION_CLASS},
 * {@link com.adito.policyframework.PolicyConstants#SYSTEM_CLASS} or
 * {@link com.adito.policyframework.PolicyConstants#PERSONAL_CLASS}.
 * <p>
 * To allow for internationalisation each defined resource type must provide the
 * ID of the resource bundle that contains its title and description.
 * <p>
 * Method implementations are also required for getting a resource given its id,
 * getting a resource given its name, deleting a resource and updating a resource
 * For the update and delete, it is the implementations responsibility to 
 * throw appropriate events.
 * 
 * @param <T> 
 * @see com.adito.policyframework.PolicyConstants
 */
public interface ResourceType<T extends Resource> extends Comparable<ResourceType<T>> {
    
    /**
     * Get if this resources of this type must be attached to a policy.
     * 
     * @return must be attached to policy
     */
    public boolean isPolicyRequired();
    
    /**
     * Get the unique ID of this resource type
     * @return resource type id
     */
    public int getResourceTypeId();
    
    /**
     * Get the ID of the message resource bundle that contains the title
     * and description of this resource type
     * 
     * @return bundle ID
     */
    public String getBundle();
    
    /**
     * Get the list of permissions that are appropriate for resources of
     * this type.
     * 
     * @return list of permission.
     */
    public Collection<Permission> getPermissions();
    
    /**
     * Add a new permission to those that are appropriate for resources of
     * this type.
     * 
     * @param permission permission to add
     */
    public void addPermission(Permission permission);
    
    /**
     * Get a permission at the specified index from the list that are 
     * appropriate for resources of this type.
     * 
     * @param idx index of permission to get
     * @return permission at specified index
     */
    public Permission getPermission(int idx);

    /**
     * Get the permission class. May currently be one of {@link com.adito.policyframework.PolicyConstants#DELEGATION_CLASS},
     * {@link com.adito.policyframework.PolicyConstants#SYSTEM_CLASS} or
     * {@link com.adito.policyframework.PolicyConstants#PERSONAL_CLASS}.
     * 
     * @return permission class
     */
    public String getPermissionClass();

    /**
     * Set the permission class. May currently be one of {@link com.adito.policyframework.PolicyConstants#DELEGATION_CLASS},
     * {@link com.adito.policyframework.PolicyConstants#SYSTEM_CLASS} or
     * {@link com.adito.policyframework.PolicyConstants#PERSONAL_CLASS}.
     * 
     * @param permissionClass permission class to set
     */
    public void setPermissionClass(String permissionClass);
    
    /**
     * Compare this resource type with another. Implementations of this 
     * should compare using the {@link #getResourceTypeId()}.
     * 
     * @param o other resource type
     * @return true if the IDs are equal
     */
    public boolean equals(Object o);

    /**
     * @param session
     * @return Collection<T>
     * @throws Exception 
     */
    Collection<T> getResources(SessionInfo session) throws Exception;

    /**
     * Verifies if the given resource name is already in use.  The default implementation of this
     * simply delegates the call to getResourceByName.  Anyone creating a resourceType should
     * use a better means of verifying this instead.
     * 
     * @param resourceName resource name
     * @param session 
     * @return resource instance
     * @throws Exception on any error
     */
    public boolean isResourceNameInUse(String resourceName, SessionInfo session) throws Exception;
    
    /**
     * Get an instance of the appropriate {@link Resource} implementation given
     * its ID.
     * 
     * @param resourceId resource ID
     * @return resource instance
     * @throws Exception on any error
     */
    public T getResourceById(int resourceId) throws Exception;
    
    /**
     * Get an instance of the appropriate {@link Resource} implementation given
     * its name.
     * 
     * @param resourceName resource name
     * @param session 
     * @return resource instance
     * @throws Exception on any error
     */
    public T getResourceByName(String resourceName, SessionInfo session) throws Exception;
    
    /**
     * Remove a {@link Resource} from its database given its ID and
     * fire the appropriate event.
     * 
     * @param resourceId resource to remove
     * @param session originating session 
     * @return resource removed
     * @throws Exception on any error
     */
    public T removeResource(int resourceId, SessionInfo session) throws Exception;
    
    /**
     * Update {@link Resource} in its database and fire the appropriate
     * event.
     * 
     * @param resource resource to update
     * @param session originating session  
     * @throws Exception on any error
     */
    public void updateResource(T resource, SessionInfo session) throws Exception;
    
    /**
     * Create {@link Resource} in its database and fire the appropriate
     * event.
     * 
     * @param resource resource to update
     * @param session originating session  
     * @return Rresource<T>
     * @throws Exception on any error
     */
    public T createResource(T resource, SessionInfo session) throws Exception;

    /**
     * Clone a resource. The resource name will automatically be adjust to 
     * <i>Copy of [resourceName]</i>. If a resource with this name already
     * exists then a number index will be added to the name until no such
     * named resource exists. E.g. <i>Copy (2) of [resourceName]</i>.
     * 
     * @param sourceResource source response
     * @param session originating session  
     * @return cloned resource
     * @throws CloneNotSupportedException
     */
    public T cloneResource(T sourceResource, SessionInfo session) throws CloneNotSupportedException;
}