
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
			
package com.ovpnals.policyframework;

import java.io.Serializable;


/**
 * Every {@link com.ovpnals.policyframework.AccessRights} contains
 * of at least one AccessRight. This provides the 
 * {@link com.ovpnals.policyframework.ResourceType} and 
 * {@link com.ovpnals.policyframework.Permission} that
 * make up a single permission.
 * <p>
 * See {@link com.ovpnals.policyframework.PolicyConstants} for a bunch
 * of constants for resource types and permission.
 * 
 * @see com.ovpnals.policyframework.PolicyConstants
 */
public class AccessRight implements Serializable {
    
    // Private instance variables
    
    private ResourceType resourceType;
    private Permission permission;

    /**
     * Constructor 
     * 
     * @param resourceType resource type
     * @param permission resource permission
     */
    public AccessRight(ResourceType resourceType, Permission permission) {
        if (resourceType == null) {
            throw new IllegalArgumentException("Null resource type.");
        }
        if (permission == null) {
            throw new IllegalArgumentException("Null permission.");
        }
        this.resourceType = resourceType;
        this.permission = permission;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        return o instanceof AccessRight ? getPermission().equals(
            ((AccessRight) o).getPermission())
                        && getResourceType().equals(((AccessRight) o).getResourceType()) : false;
    }

    /**
     * Get the resource permission.
     * 
     * @return returns the permission.
     */
    public Permission getPermission() {
        return permission;
    }

    /**
     * Set the resource permission.
     * 
     * @param permission The permission to set.
     */
    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    /**
     * Get the resource type. See {@link PolicyConstants} for a list
     * of resource types.
     * 
     * @return returns the resourceType.
     */
    public ResourceType getResourceType() {
        return resourceType;
    }

    /**
     * Set the resource type. See {@link PolicyConstants} for a list
     * of resource types.
     * 
     * @param resourceType The resourceType to set.
     */
    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }
}
