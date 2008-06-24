
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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.struts.util.MessageResources;

import com.adito.core.CoreAttributeConstants;
import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.core.CoreServlet;
import com.adito.core.CoreUtil;
import com.adito.security.SessionInfo;

/**
 * Implementation of a {@link com.adito.policyframework.ResourceType} for
 * <i>Resource Permission</i> resources.
 */
public class AccessRightsResourceType extends DefaultResourceType {

    /**
     * Constructor
     */
    public AccessRightsResourceType() {
        super(PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE_ID, "policyframework", PolicyConstants.SYSTEM_CLASS);
    }

    @Override
    public Collection getResources(SessionInfo session) throws Exception {
        return PolicyDatabaseFactory.getInstance().getAccessRights(session.getRealmId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.navigation.FavoriteResourceType#getResourceById(int)
     */
    public Resource getResourceById(int resourceId) throws Exception {
        return PolicyDatabaseFactory.getInstance().getAccessRight(resourceId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.DefaultResourceType#getResourceByName(java.lang.String,
     *      com.adito.security.SessionInfo)
     */
    public Resource getResourceByName(String resourceName, SessionInfo session) throws Exception {
        return PolicyDatabaseFactory.getInstance().getAccessRightsByName(resourceName, session.getUser().getRealm().getRealmID());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.boot.policyframework.ResourceType#removeResource(int,
     *      com.adito.security.SessionInfo)
     */
    public Resource removeResource(int resourceId, SessionInfo session) throws Exception {
        try {
            AccessRights resource = PolicyDatabaseFactory.getInstance().deleteAccessRights(resourceId);
            AccessRights accessRights = (AccessRights) resource;
            ResourceDeleteEvent event = new ResourceDeleteEvent(this, CoreEventConstants.DELETE_ACCESS_RIGHT, resource, session,
                            CoreEvent.STATE_SUCCESSFUL);
            event.addAttribute(CoreAttributeConstants.EVENT_ATTR_TYPE_ACCESS_RIGHT, getAccessRightType(session, accessRights
                            .getAccessRightsClass()));
            CoreServlet.getServlet().fireCoreEvent(addAccessRightsAttributes(event, accessRights));
            return resource;
        } catch (Exception e) {
            CoreServlet.getServlet().fireCoreEvent(
                new ResourceDeleteEvent(this, CoreEventConstants.DELETE_ACCESS_RIGHT, session, e));
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.boot.policyframework.ResourceType#updateResource(com.adito.boot.policyframework.Resource,
     *      com.adito.security.SessionInfo)
     */
    public void updateResource(Resource resource, SessionInfo session) throws Exception {
        try {
            AccessRights accessRights = (AccessRights) resource;
            PolicyDatabaseFactory.getInstance().updateAccessRights(accessRights);
            CoreEvent coreEvent = addAccessRightsAttributes(new ResourceChangeEvent(this, CoreEventConstants.UPDATE_ACCESS_RIGHT,
                            resource, session, CoreEvent.STATE_SUCCESSFUL), accessRights);

            List permissionsList = accessRights.getAccessRights();
            if (permissionsList != null) {
                int j = 0;
                for (Iterator i = permissionsList.iterator(); i.hasNext();) {
                    j++;
                    AccessRight permission = (AccessRight) i.next();

                    MessageResources mrPermission = CoreUtil.getMessageResources(session.getHttpSession(), permission
                                    .getPermission().getBundle());
                    String permissionName = mrPermission.getMessage("permission." + permission.getPermission().getId() + ".title");

                    MessageResources mrResourceType = CoreUtil.getMessageResources(session.getHttpSession(), permission
                                    .getResourceType().getBundle());
                    String resourceTypeName = mrResourceType.getMessage("resourceType."
                                    + permission.getResourceType().getResourceTypeId() + ".title");

                    coreEvent.addAttribute(CoreAttributeConstants.EVENT_ATTR_TYPE_PERMISSION + Integer.toString(j), permissionName
                                    + " " + resourceTypeName);
                }
            }
            coreEvent.addAttribute(CoreAttributeConstants.EVENT_ATTR_TYPE_ACCESS_RIGHT, getAccessRightType(session, accessRights
                            .getAccessRightsClass()));
            CoreServlet.getServlet().fireCoreEvent(coreEvent);
        } catch (Exception e) {
            CoreServlet.getServlet().fireCoreEvent(
                new ResourceChangeEvent(this, CoreEventConstants.UPDATE_ACCESS_RIGHT, session, e));
            throw e;
        }
    }

    CoreEvent addAccessRightsAttributes(CoreEvent evt, AccessRights resource) {
        return evt;
    }

    private static String getAccessRightType(SessionInfo session, String permissionClass) {
        MessageResources messageResources = CoreUtil.getMessageResources(session.getHttpSession(), "policyframework");
        String accessRightType = messageResources.getMessage("permission.type." + permissionClass);
        return accessRightType;
    }

    @Override
    public Resource createResource(Resource resource, SessionInfo session) throws Exception {
        AccessRights accessRights = (AccessRights) resource;
        return PolicyDatabaseFactory.getInstance().createAccessRights(accessRights);
    }
}