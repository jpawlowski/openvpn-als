
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
			
package com.ovpnals.policyframework;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.ovpnals.core.CoreUtil;

/**
 * Implementation of a {@link com.ovpnals.policyframework.ResourceItem}
 * which represents a {@link com.ovpnals.policyframework.PolicyItem}.
 */
public class PolicyItem extends ResourceItem<Policy> {

    /**
     * Construct a new audit report item with the specified audit report.
     * 
     * @param resource the policy this item represents.
     * @param policies the policies attached to this item.
     */
    public PolicyItem(Policy resource, List<Policy> policies) {
        super(resource, policies);
    }
    
    public String getSmallIconPath(HttpServletRequest request) {
        return CoreUtil.getThemePath(request.getSession()) + "/images/actions/policy.gif";
    }

}
