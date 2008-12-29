
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
import java.util.List;

import org.apache.struts.util.LabelValueBean;

import com.adito.core.CoreUtil;
import com.adito.input.MultiSelectDataSource;
import com.adito.security.SessionInfo;

/**
 * {@link com.adito.input.MultiSelectDataSource} that provides a list
 * of all available access rights.
 */
public class AccessRightsMultiSelectDataSource implements MultiSelectDataSource {

    
    private String checkPermissionClass;


    /**
     * Constructor
     * 
     * @param checkPermissionClass
     */
    public AccessRightsMultiSelectDataSource(String checkPermissionClass) {
        this.checkPermissionClass = checkPermissionClass;
    }

    
    /* (non-Javadoc)
     * @see com.adito.input.MultiSelectDataSource#getValues(com.adito.security.SessionInfo)
     */
    public Collection<LabelValueBean> getValues(SessionInfo sessionInfo) {
        List<LabelValueBean> l = new ArrayList<LabelValueBean>();
        try {
            List<ResourceType> r = PolicyDatabaseFactory.getInstance().getResourceTypes(this.checkPermissionClass);
            for (ResourceType<AccessRights> type : r) {
                Collection<Permission> permissions = type.getPermissions();
                for (Permission permission : permissions) {
                    String permissionString = CoreUtil.getMessageResources(sessionInfo.getHttpSession(), permission.getBundle()).getMessage("permission." + permission.getId() + ".title").trim();
                    String resourceTypeString = CoreUtil.getMessageResources(sessionInfo.getHttpSession(), type.getBundle()).getMessage("resourceType." + type.getResourceTypeId() + ".title").trim();
                    String lableString = resourceTypeString + " " + permissionString;
                    l.add(new LabelValueBean(lableString, lableString));
                }
            }
        } catch (Exception e) {
        }
        return l;
    }

}
