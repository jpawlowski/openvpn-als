
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
			
package com.ovpnals.properties.impl.resource;

import com.ovpnals.policyframework.ResourceType;
import com.ovpnals.properties.attributes.AbstractAttributeKey;

/**
 */
public class ResourceKey extends AbstractAttributeKey {
    
    int resourceId;
    ResourceType type;
    /**
     * Constructor
     * 
     * @param name name
     * @param resourceId realm ID
     */
    public ResourceKey(String name, ResourceType type, int resourceId) {
        super(name, ResourceAttributes.NAME);
        this.resourceId = resourceId;
        this.type = type;
    }
	
	public int getResourceId() {
		return resourceId;
	}
	
	public ResourceType getResourceType() {
		return type;
	}

    @Override
    public String getAttributeClassKey() {
        return type.toString() + resourceId;
    }
    
}
