
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.util.LabelValueBean;

import com.ovpnals.input.MultiSelectDataSource;
import com.ovpnals.realms.Realm;
import com.ovpnals.security.SessionInfo;

/**
 */
public class DelegatedPoliciesExcludePesronalDataSource implements MultiSelectDataSource {

    final static Log log = LogFactory.getLog(DelegatedPoliciesDataSource.class);

    private Policy checkPolicy;
    private ResourceType checkResourceType;
    private String checkPermissionClass;
    private Realm realm;

    public DelegatedPoliciesExcludePesronalDataSource(Policy checkPolicy, ResourceType checkResourceType, String checkPermissionClass, Realm realm) {
        this.checkPolicy = checkPolicy;
        this.checkResourceType = checkResourceType;
        this.checkPermissionClass = checkPermissionClass;
        this.realm = realm;
    }

    public Collection<LabelValueBean> getValues(SessionInfo session) {
        List l = new ArrayList();
        try {
            Policy pol = null;
            boolean ok = true;
            List policies = PolicyDatabaseFactory.getInstance().getPoliciesExcludePersonal(realm);
            Collections.sort(policies);
            for (Iterator i = policies.iterator(); ok && i.hasNext();) {
                pol = (Policy) i.next();
                if (checkPolicy != null) {
                    if (pol.getResourceId() == checkPolicy.getResourceId()) {
                        ok = false;
                    }
                }
                if (ok) {
                    l.add(new LabelValueBean(pol.getResourceName(), String.valueOf(pol.getResourceId())));
                }
                ok = true;
            } 
        } catch (Exception e) {
            log.error("Failed to list policies.", e);
        }
        return l;
    }
}
