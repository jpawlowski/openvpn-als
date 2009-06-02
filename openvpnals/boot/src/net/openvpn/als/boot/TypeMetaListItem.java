
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
			
package net.openvpn.als.boot;

import java.io.Serializable;

/**
 * Represents a choice in a {@link net.openvpn.als.boot.PropertyDefinition} that
 * is of type {@link net.openvpn.als.boot.PropertyDefinition#TYPE_LIST}.
 * <p>
 * The message resource bundle key is also required, being use on when the
 * HTML select component is renderered to get the localised text for the
 * value. 
 */
public class TypeMetaListItem implements Serializable {
    // Private instance variables
    private String val;
    private String messageResourcesKey;
    
    /**
     * Constructor
     * 
     * @param val value for choice 
     * @param messageResourcesKey message resources 
     */
    public TypeMetaListItem(String val, String messageResourcesKey) {
        this.val = val;
        this.messageResourcesKey = messageResourcesKey;
    }
    
    /**
     * Get the value of this item
     * 
     * @return value
     */
    public String getValue() {
        return val;
    }
    
    /**
     * Get the message resources bundle key that contains the localised text
     * to display for the value.
     * 
     * @return message resources bundle key
     */
    public String getMessageResourcesKey() {
        return messageResourcesKey;
    }
}