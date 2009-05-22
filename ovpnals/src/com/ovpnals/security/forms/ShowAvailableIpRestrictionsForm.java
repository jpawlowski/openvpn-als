
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

import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionMapping;

import com.ovpnals.security.IpRestriction;
import com.ovpnals.security.IpRestrictionItem;
import com.ovpnals.security.IpRestrictionItemModel;
import com.ovpnals.table.forms.AbstractPagerForm;

/**
 * Implementation of a {@link com.ovpnals.table.forms.AbstractPagerForm}
 * that allows an administrator to create and delete <i>IP Restrictions</i>.
 * 
 * @see com.ovpnals.security.IpRestrictionItem
 */
public class ShowAvailableIpRestrictionsForm extends AbstractPagerForm {

    // Private instance variables

    private IpRestriction[] ipRestrictions;
    private String action;

    /**
     * Constructor. Creates a new {@link IpRestrictionItemModel} object.
     */
    public ShowAvailableIpRestrictionsForm() {
        super(new IpRestrictionItemModel());
        getPager().setSorts(false);
    }

    /**
     * Builds the ipRestrictions page model and initialises the super class.
     * 
     * @param ipRestrictions list of IP Restriction objects
     * @param session session
     */
    public void initialize(IpRestriction[] ipRestrictions, HttpSession session) {
        super.initialize(session, "");
        for (int i = 0; ipRestrictions != null && i < ipRestrictions.length; i++) {
            getModel().addItem(new IpRestrictionItem(ipRestrictions[i], i > 1, i > 0 && i < ( ipRestrictions.length - 1 ),
                !ipRestrictions[i].isDefault()));
        }
        getPager().rebuild(getFilterText());
    }

    /**
     * Get the IP Restrictions list.
     * 
     * @return ipResrictions list of IP Restriction objects
     */
    public IpRestriction[] getIpRestrictions() {
        return ipRestrictions;
    }

    /**
     * Get if this IP Restriction is authorised.
     * 
     * @return is authorised
     */
    public boolean isAuthorized() {
        return action != null && action.equals("allow");
    }

    /**
     * Get if this IP Restriction is displayable.
     * 
     * @return is displayable
     */
    public boolean isDisplayable() {
        return ipRestrictions.length > 0;
    }

    /* (non-Javadoc)
     * @see com.ovpnals.table.forms.AbstractPagerForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, javax.servlet.http.HttpServletRequest request) {
        ipRestrictions = null;
        action = "";
    }
}