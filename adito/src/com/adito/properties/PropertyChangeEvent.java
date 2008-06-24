
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
			
package com.adito.properties;

import com.adito.boot.PropertyDefinition;
import com.adito.core.CoreAttributeConstants;
import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.properties.impl.profile.ProfileProperties;
import com.adito.security.SessionInfo;

/**
 * Event fired when a property stored in the {@link com.adito.properties.PropertyDatabase}
 * is changed.
 */
public class PropertyChangeEvent extends CoreEvent {

    // Private instance variables
    
    private String oldValue;
    private String newValue;
    private PropertyProfile profile;
    
    /**
     * Constructor
     * 
     * @param source source of event
     * @param def definition of property that has changed
     * @param session the session were the property change occured or <code>null</code> if the change was not user initiated
     * @param oldValue old value of property
     * @param newValue new value of property
     * @param state event state. Can be one of {@link CoreEvent#STATE_SUCCESSFUL} or {@link CoreEvent#STATE_UNSUCCESSFUL}  
     */
    public PropertyChangeEvent(Object source, PropertyDefinition def, SessionInfo session, String oldValue, String newValue, int state) {
        this(source, CoreEventConstants.PROPERTY_CHANGED, def, session, oldValue, newValue, state);
    }

    
    /**
     * Constructor
     * 
     * @param source source of event
     * @param code event code
     * @param def definition of property that has changed
     * @param session the session were the property change occured or <code>null</code> if the change was not user initiated
     * @param oldValue old value of property
     * @param newValue new value of property
     * @param state event state. Can be one of {@link CoreEvent#STATE_SUCCESSFUL} or {@link CoreEvent#STATE_UNSUCCESSFUL}  
     */
    public PropertyChangeEvent(Object source, int code, PropertyDefinition def, SessionInfo session, String oldValue, String newValue, int state) {    
        this(source, code, def, session, null, oldValue, newValue, state);
    }

    
    /**
     * Constructor
     * 
     * @param source source of event
     * @param code event code
     * @param def definition of property that has changed
     * @param session the session were the property change occured or <code>null</code> if the change was not user initiated
     * @param profile property profile that change
     * @param oldValue old value of property
     * @param newValue new value of property
     * @param state event state. Can be one of {@link CoreEvent#STATE_SUCCESSFUL} or {@link CoreEvent#STATE_UNSUCCESSFUL}  
     */
    public PropertyChangeEvent(Object source, int code, PropertyDefinition def, SessionInfo session, PropertyProfile profile, String oldValue, String newValue, int state) {    
        super(source, code, def, session, state);
        if (oldValue == null) {
        	oldValue = "";
        }
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.addAttribute(CoreAttributeConstants.EVENT_ATTR_PROPERTY_CLASS, def.getPropertyClass().getName());
        this.addAttribute(CoreAttributeConstants.EVENT_ATTR_PROPERTY_OLD_VALUE, oldValue == null ? def.getDefaultValue() : oldValue);
        this.addAttribute(CoreAttributeConstants.EVENT_ATTR_PROPERTY_NEW_VALUE, newValue);
        this.addAttribute(CoreAttributeConstants.EVENT_ATTR_PROPERTY_NAME, def == null ? "<unknown>" : def.getLabel());
        this.addAttribute(CoreAttributeConstants.EVENT_ATTR_PROPERTY_KEY, def == null ? "<unknown>" : def.getName());
        this.profile = profile;
        if(profile != null && def != null && def.getPropertyClass() instanceof ProfileProperties) {
            addAttribute(CoreAttributeConstants.EVENT_ATTR_PROPERTY_PROFILE_NAME, profile.getResourceName());
        }
    }
    
    /**
     * Get the old value of the property that was changed
     * 
     * @return old value
     */
    public String getOldValue() {
        return oldValue;
    }
    
    /**
     * Get the new value of the property that was changed
     * 
     * @return new value
     */
    public String getNewValue() {
        return newValue;
    }
    
    /**
     * Get the property definition that changed
     * 
     * @return property definition
     */
    public PropertyDefinition getDefinition() {
        return (PropertyDefinition)getParameter();
    }

}
