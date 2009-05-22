
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
			
package com.ovpnals.security.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.ovpnals.core.forms.CoreForm;
import com.ovpnals.input.validators.IPV4AddressValidator;
import com.ovpnals.input.validators.IPV6AddressValidator;
import com.ovpnals.security.IpRestriction;

/**
 * Implementation of a {@link com.ovpnals.core.forms.CoreForm} that allows
 * an administrator to create an <i>IP Restrictions</i>.
 * 
 * @see com.ovpnals.security.IpRestrictionItem
 */
public class IpRestrictionForm extends CoreForm {
    
    /**
     * String constant for allow type
     */
    public final static String ALLOW_TYPE = "allow";
    
    /**
     * String constant for deny type
     */
    public final static String DENY_TYPE = "deny";
    

    private String type;
    private boolean addressEnabled;
    private boolean editing;
    private IpRestriction restriction;

    /**
     * Sets the IP Restriction to empty strings.
     * 
     * @param restriction restriction to edit
     * @param editing user is editing the restriction (as opposed to creating)
     */
    public void initialize(IpRestriction restriction, boolean editing) {
        this.restriction = restriction;
        addressEnabled = !editing || !restriction.isDefault();
        type = restriction.getAllowed() ? ALLOW_TYPE : DENY_TYPE;
        this.editing = editing;
    }
    
    /**
     * Get the IP restriction object being edited
     * 
     * @return IP restriction
     */
    public IpRestriction getRestriction() {
        return restriction;
    }
    
    /**
     * Get if the IP restriction is being edited.
     * 
     * @return editing
     */
    public boolean isEditing() {
        return editing;
    }
    
    /**
     * Get if the address field should be enabled. This will be <code>false</code>
     * if the IP restriction is the default rule and the user is editing.
     * 
     * @return address enabled
     */
    public boolean isAddressEnabled() {
        return addressEnabled;
    }
    
    /**
     * Apply the remaining details to the restriction object (this just
     * sets the type according to the type value and address collected by the form) 
     */
    public void apply() {
        restriction.setType(IpRestriction.getType(restriction.getAddress(), type.equals(ALLOW_TYPE)));
    }

    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        ActionErrors errors = new ActionErrors();
        if (isCommiting()) {        	
            if (restriction.getAddress() == null || restriction.getAddress().equals("")) {
                errors.add(Globals.ERROR_KEY, new ActionMessage("editIpRestriction.error.noIpAddress"));
            } 
            if(!isEditing() && restriction.getAddress().equals("*")) {
                errors.add(Globals.ERROR_KEY, new ActionMessage("editIpRestriction.error.usingDefaultPattern"));
            }
            else if(restriction.getAddress().equals("127.0.0.1") || restriction.getAddress().equals("::1") || restriction.getAddress().equals("0:0:0:0:0:0:0:1")) {
            	errors.add(Globals.ERROR_KEY, new ActionMessage("editIpRestriction.error.localhost"));
            }
            else if(!restriction.getAddress().equals("*") && !IPV6AddressValidator.isIpAddressExpressionValid(restriction.getAddress()) && !IPV4AddressValidator.isIpAddressExpressionValid(restriction.getAddress())) {
            	errors.add(Globals.ERROR_KEY, new ActionMessage("editIpRestriction.error.invalidIpv4Address"));
            }
        }
        return errors;
    }

    /**
     * Gets the IP Restriction type. Will be one of {@link #ALLOW_TYPE} or 
     * {@link #DENY_TYPE}.
     * 
     * @return type 
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the IP Restriction type. Should be one of {@link #ALLOW_TYPE} or 
     * {@link #DENY_TYPE}.
     * 
     * @param type type
     */
    public void setType(String type) {
        this.type = type;
    }
}