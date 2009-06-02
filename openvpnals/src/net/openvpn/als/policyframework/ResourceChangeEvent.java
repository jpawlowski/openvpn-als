
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

import net.openvpn.als.boot.Util;
import net.openvpn.als.core.CoreAttributeConstants;
import net.openvpn.als.core.CoreEvent;
import net.openvpn.als.security.SessionInfo;

/**
 * Extension of a {@link net.openvpn.als.core.CoreEvent} that should be
 * used when a {@link Resource} changes in some way.
 */
public class ResourceChangeEvent extends CoreEvent {
    
    private Resource resource;
    
    /**
     * Constructor for {@link CoreEvent#STATE_UNSUCCESSFUL} that also
     * takes the message from an exception and adds it as an attribute
     * 
     * @param source source of event
     * @param id event ID
     * @param session session that fired the event
     * @param exception exception
     */
    public ResourceChangeEvent(Object source, int id, 
                    SessionInfo session, Throwable exception) {
        super(source, id, exception, session, STATE_UNSUCCESSFUL);
        addAttribute(CoreAttributeConstants.EVENT_ATTR_EXCEPTION_MESSAGE, 
            Util.getExceptionMessageChain(exception));
    }

    /**
     * Constructor
     * 
     * @param source source of event
     * @param id event ID
     * @param resource resource changin
     * @param session session that fired the event
     */
    public ResourceChangeEvent(Object source, int id, Resource resource,
                               SessionInfo session) {
        this(source, id, resource, session, STATE_SUCCESSFUL);
    }
    
    /**
     * Constructor
     * 
     * @param source source of event
     * @param id event ID
     * @param resource resource changin
     * @param session session that fired the event
     * @param state event state
     */
    public ResourceChangeEvent(Object source, int id, Resource resource,
                    SessionInfo session, int state) {
        super(source, id, resource, session, state);
        this.resource = resource;
        if(state == CoreEvent.STATE_UNSUCCESSFUL || ( state == CoreEvent.STATE_SUCCESSFUL && resource != null) ) {
            if(resource != null) {
                if (resource.getResourceId() != -1){
                    addAttribute(CoreAttributeConstants.EVENT_ATTR_RESOURCE_NAME, resource.getResourceName());
                    addAttribute(CoreAttributeConstants.EVENT_ATTR_RESOURCE_DESCRIPTION, resource.getResourceDescription());
                }
                if(resource instanceof OwnedResource) {
                    OwnedResource or = (OwnedResource)resource;
                    if(or.getOwnerUsername() != null) {
                        addAttribute(CoreAttributeConstants.EVENT_ATTR_RESOURCE_OWNER, or.getOwnerUsername());
                    }
                }
            }
        }
        else {
            throw new IllegalArgumentException("Must provide a non-null resource if the event was successful.");
        }
    }
    
    /**
     * Get the resource
     * 
     * @return resource
     */
    public Resource getResource() {
        return resource;
    }

}
