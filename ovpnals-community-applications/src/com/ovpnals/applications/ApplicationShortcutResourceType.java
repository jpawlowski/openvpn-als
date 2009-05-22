
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
			
package com.ovpnals.applications;

import javax.servlet.http.HttpServletRequest;

import com.ovpnals.core.CoreEvent;
import com.ovpnals.core.CoreServlet;
import com.ovpnals.extensions.ExtensionDescriptor;
import com.ovpnals.extensions.store.ExtensionStore;
import com.ovpnals.navigation.FavoriteResourceType;
import com.ovpnals.navigation.WrappedFavoriteItem;
import com.ovpnals.policyframework.DefaultResourceType;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.policyframework.PolicyDatabaseFactory;
import com.ovpnals.policyframework.Resource;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SessionInfo;

/**
 * Implementation of a {@link com.ovpnals.policyframework.ResourceType} for
 * <i>Application Shortcut</i> resources.
 */
public class ApplicationShortcutResourceType extends DefaultResourceType implements FavoriteResourceType {

    /**
     * Constructor
     */
    public ApplicationShortcutResourceType() {
        super(ApplicationsPlugin.APPLICATION_SHORTCUT_RESOURCE_TYPE_ID, "policyframework", PolicyConstants.DELEGATION_CLASS);
    }

    public WrappedFavoriteItem createWrappedFavoriteItem(int resourceId, HttpServletRequest request, String type) throws Exception {
        SessionInfo sessionInfo = LogonControllerFactory.getInstance().getSessionInfo(request);
        ApplicationShortcut as = ApplicationShortcutDatabaseFactory.getInstance().getShortcut(resourceId);
        if (as == null) {
            return null;
        }
        ExtensionDescriptor des = ExtensionStore.getInstance().getExtensionDescriptor(as.getApplication());
        if (des == null) {
            throw new Exception("No application extension with ID of " + as.getApplication() + ", skipping favorite.");
        } else {
            SessionInfo inf = LogonControllerFactory.getInstance().getSessionInfo(request);
            ApplicationShortcutItem it = new ApplicationShortcutItem(des, as, PolicyDatabaseFactory.getInstance()
                            .getPoliciesAttachedToResource(as, sessionInfo.getUser().getRealm()), inf.getNavigationContext(), as
                            .sessionPasswordRequired(inf));
            if (as.getApplication() != null) {
                return new WrappedFavoriteItem(it, type);
            } else {
                throw new Exception("No application.");
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.navigation.FavoriteResourceType#getResourceById(int)
     */
    public Resource getResourceById(int resourceId) throws Exception {
        return ApplicationShortcutDatabaseFactory.getInstance().getShortcut(resourceId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.policyframework.DefaultResourceType#getResourceByName(java.lang.String,
     *      com.ovpnals.security.SessionInfo)
     */
    public Resource getResourceByName(String resourceName, SessionInfo session) throws Exception {
        return ApplicationShortcutDatabaseFactory.getInstance()
                        .getShortcut(resourceName, session.getUser().getRealm().getRealmID());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.boot.policyframework.ResourceType#removeResource(int,
     *      com.ovpnals.security.SessionInfo)
     */
    public Resource removeResource(int resourceId, SessionInfo session) throws Exception {
        try {
            ApplicationShortcut resource = ApplicationShortcutDatabaseFactory.getInstance().deleteShortcut(resourceId);
            CoreServlet.getServlet().fireCoreEvent(
                new ApplicationShortcutDeleteEvent(this, ApplicationShortcutEventConstants.REMOVE_APPLICATION_SHORTCUT, resource,
                                session, CoreEvent.STATE_SUCCESSFUL));
            return resource;
        } catch (Exception e) {
            CoreServlet.getServlet()
                            .fireCoreEvent(
                                new ApplicationShortcutDeleteEvent(this,
                                                ApplicationShortcutEventConstants.REMOVE_APPLICATION_SHORTCUT, session, e));
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.boot.policyframework.ResourceType#updateResource(com.ovpnals.boot.policyframework.Resource,
     *      com.ovpnals.security.SessionInfo)
     */
    public void updateResource(Resource resource, SessionInfo session) throws Exception {
        try {
            ApplicationShortcut applicationShortcut = (ApplicationShortcut) resource;
            ApplicationShortcutDatabaseFactory.getInstance().updateApplicationShortcut(resource.getResourceId(),
                resource.getResourceName(), resource.getResourceDescription(), applicationShortcut.getParameters(), applicationShortcut.isAutoStart());
            CoreServlet.getServlet().fireCoreEvent(
                new ApplicationShortcutChangeEvent(this, ApplicationShortcutEventConstants.UPDATE_APPLICATION_SHORTCUT,
                                (ApplicationShortcut) resource, session, CoreEvent.STATE_SUCCESSFUL));
        } catch (Exception e) {
            CoreServlet.getServlet()
                            .fireCoreEvent(
                                new ApplicationShortcutChangeEvent(this,
                                                ApplicationShortcutEventConstants.UPDATE_APPLICATION_SHORTCUT, session, e));
            throw e;
        }
    }

    @Override
    public Resource createResource(Resource resource, SessionInfo session) throws Exception {
        ApplicationShortcut applicationShortcut = (ApplicationShortcut) resource;
        int newResourceId = ApplicationShortcutDatabaseFactory.getInstance().createApplicationShortcut(
            applicationShortcut.getApplication(), applicationShortcut.getResourceName(),
            applicationShortcut.getResourceDescription(), applicationShortcut.getParameters(), applicationShortcut.isAutoStart(), applicationShortcut.getRealmID());
        return ApplicationShortcutDatabaseFactory.getInstance().getShortcut(newResourceId);
    }

}
