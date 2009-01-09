
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
			
package com.adito.networkplaces;

import javax.servlet.http.HttpServletRequest;

import com.adito.boot.Util;
import com.adito.core.CoreAttributeConstants;
import com.adito.core.CoreEvent;
import com.adito.policyframework.Policy;
import com.adito.policyframework.Resource;
import com.adito.policyframework.ResourceAccessEvent;
import com.adito.security.SessionInfo;
import com.adito.vfs.VfsUtils;

/**
 * Extension of a {@link com.adito.policyframework.ResourceAccessEvent} that should be used
 * when a {@link NetworkPlace} is accessed in some way.
 */
public class NetworkPlacesAccessEvent extends ResourceAccessEvent {

    /**
     * Constructor for {@link CoreEvent#STATE_UNSUCCESSFUL} that also takes the
     * message from an exception and adds it as an attribute
     * 
     * @param source source of event
     * @param id event ID
     * @param session session that fired the event
     * @param exception exception
     */
    public NetworkPlacesAccessEvent(Object source, int id, SessionInfo session, Throwable exception) {
        super(source, id, session, exception);
    }

    /**
     * Constructor for {@link CoreEvent#STATE_UNSUCCESSFUL} that also takes the
     * message from an exception and adds it as an attribute
     * 
     * @param source source of event
     * @param id event ID
     * @param resource resource accessed
     * @param policy the policy resource access was attempted under
     * @param session session that fired the event
     * @param exception exception
     * @param request request
     * @param path path
     * @param uri uri
     */
    public NetworkPlacesAccessEvent(Object source, int id, Resource resource, Policy policy, SessionInfo session, Throwable exception, HttpServletRequest request, String path, String uri) {
        this(source, id, resource, policy, session, STATE_UNSUCCESSFUL, request, path, uri);
        if(exception != null) {
            String exceptionMessageChain = Util.getExceptionMessageChain(exception);
            String maskedExceptionMessage = VfsUtils.maskSensitiveArguments(exceptionMessageChain);
            addAttribute(CoreAttributeConstants.EVENT_ATTR_EXCEPTION_MESSAGE, maskedExceptionMessage);
        }
    }
    
    /**
     * Constructor
     * 
     * @param source source of event
     * @param id event ID
     * @param resource resource accessed
     * @param policy the policy resource access was attempted under
     * @param session session that fired the event
     * @param state event state
     * @param request request
     * @param path path
     * @param uri uri
     */
    public NetworkPlacesAccessEvent(Object source, int id, Resource resource, Policy policy, SessionInfo session, int state, HttpServletRequest request, String path, String uri) {
        this(source, id, resource, policy, session, state, request);
//        LDP - URI and path are practically identical so why log them both?!        
//        if(uri != null)
//            addAttribute(NetworkPlacesEventConstants.EVENT_ATTR_VFS_URI, uri);
        if(path != null)
            addAttribute(NetworkPlacesEventConstants.EVENT_ATTR_VFS_PATH, path);
    }


    /**
     * Constructor
     * 
     * @param source source of event
     * @param id event ID
     * @param resource resource accessed
     * @param policy the policy resource access was attempted under
     * @param session session that fired the event
     * @param state event state
     * @param request request
     */
    public NetworkPlacesAccessEvent(Object source, int id, Resource resource, Policy policy, SessionInfo session, int state, HttpServletRequest request) {
        super(source, id, resource, policy, session, state);
        if(request != null)
            addAttribute(NetworkPlacesEventConstants.EVENT_ATTR_VFS_USER_AGENT, request.getHeader("User-Agent"));
    }

}
