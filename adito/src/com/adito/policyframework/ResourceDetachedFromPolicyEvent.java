
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

import com.adito.core.CoreAttributeConstants;
import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.security.SessionInfo;

/**
 * Extension of a {@link com.adito.core.CoreEvent} that should be
 * used when a {@link Resource} is detached from a {@link com.adito.policyframework.Policy}.
 */
public class ResourceDetachedFromPolicyEvent extends CoreEvent {

    
    Resource resource;
    Policy policy;
    
    /**
     * Constructor
     * 
     * @param source source of event
     * @param resource resource
     * @param policy policy
     * @param session session that fired the event
     * @param state event state
     */
    public ResourceDetachedFromPolicyEvent(Object source, Resource resource, Policy policy,
                    SessionInfo session, int state) {
        super(source, CoreEventConstants.RESOURCE_DETACHED_FROM_POLICY, resource, session, state);
        this.resource = resource;
        this.policy = policy;        
        addAttribute(CoreAttributeConstants.EVENT_ATTR_RESOURCE_NAME, resource.getResourceName());
        addAttribute(CoreAttributeConstants.EVENT_ATTR_POLICY_NAME, policy.getResourceName());
    }

    /**
     * Get the policy resource access was attempted under. This will only be
     * available on successful events
     * 
     * @return policy
     */
    public Policy getPolicy() {
        return policy;
    }

    /**
     * Get the resource. This will only be available on successful events
     * 
     * @return resource
     */
    public Resource getResource() {
        return resource;
    }    
}
