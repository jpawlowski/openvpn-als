
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
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.adito.boot.PropertyList;
import com.adito.core.CoreUtil;

/**
 * Default implementation of an {@link com.adito.policyframework.AccessRights}
 */
public class DefaultAccessRights extends AbstractResource implements AccessRights {
    
    private List<AccessRight> permissions;
    private List<Rule> rules;    
    private String permissionClass;
    
    /**
     * Constructs a access rights from the specified values.
     * 
     * @param realmId the identifier of the <code>AccessRights</code> realm Id
     * @param permissionClass the permission class of the <code>AccessRights</code>
     */
    public DefaultAccessRights(int realmId, String permissionClass) {
        this(realmId, -1, "", "", null, permissionClass, Calendar.getInstance(), Calendar.getInstance());
    }

    /**
     * @param realmID
     * @param resourceID
     * @param resourceName
     * @param resourceDescription
     * @param permissions
     * @param permissionClass
     * @param dateCreated
     * @param dateAmended
     */
    public DefaultAccessRights(int realmID, int resourceID, String resourceName, String resourceDescription, List<AccessRight> permissions, String permissionClass, Calendar dateCreated, Calendar dateAmended) {
        super(realmID, PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE, resourceID, resourceName, resourceDescription, dateCreated, dateAmended);
        this.permissions = permissions == null ? new ArrayList<AccessRight>() : permissions;
        this.permissionClass = permissionClass;
        this.rules = new ArrayList<Rule>();
    }

    /* (non-Javadoc)
     * @see com.adito.policyframework.AccessRights#addAccessRight(com.adito.policyframework.AccessRight)
     */
    public void addAccessRight(AccessRight perm) {
        permissions.add(perm);
    }

    /* (non-Javadoc)
     * @see com.adito.policyframework.AccessRights#removeAccessRight(com.adito.policyframework.AccessRight)
     */
    public void removeAccessRight(AccessRight perm) {
        permissions.remove(perm);
    }

    /* (non-Javadoc)
     * @see com.adito.policyframework.AccessRights#getAccessRights()
     */
    public List<AccessRight> getAccessRights() {
        return permissions;
    }

    /* (non-Javadoc)
     * @see com.adito.policyframework.AccessRights#getRules()
     */
    public List getRules() {
        return rules;
    }

    /* (non-Javadoc)
     * @see com.adito.policyframework.AccessRights#addRule(com.adito.policyframework.Rule)
     */
    public void addRule(Rule rule) {
        rules.add(rule);
    }

    public void removeRule(Rule rule) {
        rules.remove(rule);
    }

    public String getAccessRightsClass() {
        return permissionClass;
    }

    public boolean containsAccessRight(AccessRight accessRight) {
        return permissions.contains(accessRight);
    }
    
    /* (non-Javadoc)
     * @see com.adito.policyframework.AccessRights#setAllAccessRights(javax.servlet.http.HttpSession, com.adito.boot.PropertyList)
     */
    public void setAllAccessRights(HttpSession httpSession, PropertyList permissionList) throws Exception {
        List<ResourceType> resourceTypes = PolicyDatabaseFactory.getInstance().getResourceTypes(permissionClass);
        for (ResourceType<AccessRights> resourceType : resourceTypes) {
            Collection<Permission> permissions = resourceType.getPermissions();
            for (Permission permission : permissions) {
                String permissionString = CoreUtil.getMessageResources(httpSession, permission.getBundle()).getMessage("permission." + permission.getId() + ".title").trim();
                String resourceTypeString = CoreUtil.getMessageResources(httpSession, resourceType.getBundle()).getMessage("resourceType." + resourceType.getResourceTypeId() + ".title").trim();
                String lableString = resourceTypeString + " " + permissionString;
                if (permissionList.contains(lableString))
                    this.addAccessRight(new AccessRight(resourceType, permission));
            }
        }
    }

}
