
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
			
package net.openvpn.als.policyframework.actions;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.policyframework.AccessRights;
import net.openvpn.als.policyframework.AccessRightsItem;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.policyframework.PolicyDatabaseFactory;
import net.openvpn.als.policyframework.PolicyUtil;
import net.openvpn.als.policyframework.forms.AccessRightsListForm;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.security.User;

/**
 * Implementation of a
 * {@link net.openvpn.als.policyframework.actions.AbstractResourcesDispatchAction}
 * that allows listing and maintenance of <i>Resource Permissions</i>.
 */
public class AccessRightsListDispatchAction extends AbstractResourcesDispatchAction<AccessRights, AccessRightsItem> {

    /**
     * Constructor
     */
    public AccessRightsListDispatchAction() {
        super(PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE, PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.actions.DispatchAction#unspecified(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        PolicyUtil.checkPermissions(PolicyConstants.ACCESS_RIGHTS_RESOURCE_TYPE, new Permission[]{ PolicyConstants.PERM_EDIT_AND_ASSIGN, PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_DELETE, PolicyConstants.PERM_ASSIGN}, request);
        AccessRightsListForm accessRightsListForm = (AccessRightsListForm) form;
        User user = LogonControllerFactory.getInstance().getUser(request);
        List resourcePermissions = PolicyDatabaseFactory.getInstance().getAccessRights(user.getRealm().getRealmID());
        accessRightsListForm.initialize(resourcePermissions, request.getSession());
        return super.unspecified(mapping, form, request, response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

    @Override
    protected void doRemove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        super.doRemove(mapping, form, request, response);
        // we now need to rebuild any menus, as more or less could be visible.
        LogonControllerFactory.getInstance().applyMenuItemChanges(request);
    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.policyframework.actions.AbstractResourcesDispatchAction#remove(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        AccessRights resource = (AccessRights) getSelectedResource(form);
        super.remove(mapping, form, request, response);
        saveMessage(request, "access.rights.deleted.message", resource);
        return getRedirectWithMessages(mapping, request);
    }
}