
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
			
package com.ovpnals.applications;

import com.ovpnals.core.CoreAttributeConstants;
import com.ovpnals.extensions.ExtensionDescriptor;
import com.ovpnals.extensions.store.ExtensionStore;
import com.ovpnals.policyframework.ResourceChangeEvent;
import com.ovpnals.policyframework.ResourceDeleteEvent;
import com.ovpnals.security.SessionInfo;


/**
 * Extension of {@link ResourceChangeEvent} specifically for
 * {@link ApplicationShortcut} resources.
 */
public class ApplicationShortcutDeleteEvent extends ResourceDeleteEvent {

    /**
     * Constructor.
     *
     * @param source source
     * @param id id 
     * @param session session
     * @param exception error
     */
    public ApplicationShortcutDeleteEvent(Object source, int id, SessionInfo session, Throwable exception) {
        super(source, id, session, exception);
    }

    /**
     * Constructor.
     *
     * @param source source
     * @param id id  
     * @param shortcut client configuration
     * @param session session
     * @param state state
     */
    public ApplicationShortcutDeleteEvent(Object source, int id, ApplicationShortcut shortcut, SessionInfo session, int state) {
        super(source, id, shortcut, session, state);
        if(shortcut != null) {
            addAttribute(CoreAttributeConstants.EVENT_ATTR_APPLICATION_ID, shortcut.getApplication());
            try {
                ExtensionDescriptor des = ExtensionStore.getInstance().getExtensionDescriptor(shortcut.getApplication());
                if(des != null) {
                    addAttribute(CoreAttributeConstants.EVENT_ATTR_APPLICATION_NAME, des.getName());
                }
            }
            catch(Exception e) {                
            }
        }
    }
    
    /**
     * Get the application shortcut object. This will be <code>null</code>
     * if the event was constructed for an error.
     * 
     * @return application shortcut
     */
    public ApplicationShortcut getClientConfiguration() {
        return ((ApplicationShortcut)getResource());
    }

}
