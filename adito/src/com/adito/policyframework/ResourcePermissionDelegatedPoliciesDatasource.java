
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
			
package com.adito.policyframework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.struts.util.LabelValueBean;

import com.adito.input.MultiSelectDataSource;
import com.adito.security.SessionInfo;

public class ResourcePermissionDelegatedPoliciesDatasource implements MultiSelectDataSource {
    private AccessRights parent;

    public ResourcePermissionDelegatedPoliciesDatasource(AccessRights parent) {
        this.parent = parent;
    }

    public Collection<LabelValueBean> getValues(SessionInfo session) {
        List l = new ArrayList();
        List pol;
        try {
            pol = PolicyDatabaseFactory.getInstance().getPolicies(session.getUser().getRealm());
            Policy p;
            for (Iterator i = pol.iterator(); i.hasNext();) {
                p = (Policy) i.next();
                l.add(new LabelValueBean(p.getResourceName(), String.valueOf(p.getResourceId())));
            }
        } catch (Exception e) {
        }
        return l;
    }

}