
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
			
package net.openvpn.als.security.forms;

import org.apache.struts.action.ActionMapping;

import net.openvpn.als.core.forms.CoreForm;
import net.openvpn.als.security.IpRestriction;

/**
 * An implementation of the ActionForm object.
 * 
 * @author P.J.King
 * 
 */
public class ShowIpRestrictionsForm extends CoreForm {

    IpRestriction[] ipRestrictions;
    String ipAddress;
    String action;

    public void initialize(IpRestriction[] ipRestrictions) {
        this.ipRestrictions = ipRestrictions;
    }

    public String getAddress() {
        return ipAddress;
    }

    public IpRestriction[] getRestrictions() {
        return ipRestrictions;
    }

    public void setAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isAuthorized() {
        return action != null && action.equals("allow");
    }

    public boolean isDisplayable() {
        return ipRestrictions.length > 0;
    }

    public String getAction() {
        return action;
    }

    public void reset(ActionMapping mapping, javax.servlet.http.HttpServletRequest request) {

        ipRestrictions = null;
        action = "";
        ipAddress = "";
    }

}