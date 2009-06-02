
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
			
package net.openvpn.als.input.tags;

import java.util.List;

/**
 * Extension of the standard struts {@link org.apache.struts.taglib.html.PasswordTag}
 * that allows AUTOCOMPLETE to be turned off.
 */
public class PasswordTag extends org.apache.struts.taglib.html.PasswordTag {
    protected List scripts;
    protected boolean autocomplete = false; // Why would we ever want autocomplete on passwords in such a secure application?
    
    /**
     * Set whether autocomplete should be available for this field.
     * 
     * @param autocomplete autocomplete
     */
    public void setAutocomplete(boolean autocomplete) {
        this.autocomplete = autocomplete;
    }
    /* (non-Javadoc)
     * @see org.apache.struts.taglib.html.BaseHandlerTag#prepareOtherAttributes(java.lang.StringBuffer)
     */
    protected void prepareOtherAttributes(StringBuffer results) {
        super.prepareOtherAttributes(results);
        if(!autocomplete) {
            prepareAttribute(results, "AUTOCOMPLETE", "OFF");
        }
    }
}
