
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
			
package com.ovpnals.properties;

import java.util.Calendar;

import com.ovpnals.policyframework.AbstractResource;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.policyframework.Resource;
import com.ovpnals.policyframework.ResourceType;

/**
 * Default implementation of a {@link com.ovpnals.properties.PropertyProfile}.
 */
public class DefaultPropertyProfile extends AbstractResource implements PropertyProfile {
        
    // Private instance variables
    
    private String user;
    
    /**
     * @param realmID
     * @param user
     * @param resourceName
     * @param resourceDescription
     */
    public DefaultPropertyProfile(int realmID, String user, String resourceName, String resourceDescription) {
        this(realmID, -1, user, resourceName, resourceDescription, Calendar.getInstance(), Calendar.getInstance());
    }
    
    /**
     * Constructor
     * @param realmID
     * @param resourceId
     * @param user
     * @param resourceName
     * @param resourceDescription
     * @param dateCreated
     * @param dateAmended
     */
    public DefaultPropertyProfile(int realmID, int resourceId, String user, String resourceName, String resourceDescription,
                    Calendar dateCreated, Calendar dateAmended) {
        super(realmID, PolicyConstants.PROFILE_RESOURCE_TYPE, resourceId, resourceName, resourceDescription, dateCreated, dateAmended);
        this.user = user;
    }

    /* (non-Javadoc)
     * @see com.ovpnals.boot.PropertyProfile#getLabel()
     */
    public String getLabel() {
        return resourceName + (user == null || user.equals("") ? " (Global)" : " (Personal)");
    }

    /* (non-Javadoc)
     * @see com.ovpnals.boot.policyframework.OwnedResource#getOwnerUsername()
     */
    public String getOwnerUsername() {
        return user;
    }

    /* (non-Javadoc)
     * @see com.ovpnals.boot.policyframework.Resource#getResourceType()
     */
    public ResourceType getResourceType() {
        return PolicyConstants.PROFILE_RESOURCE_TYPE;
    }

    /* (non-Javadoc)
     * @see com.ovpnals.policyframework.AbstractResource#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        return getResourceName().compareTo(((Resource) o).getResourceName());
    }
}
