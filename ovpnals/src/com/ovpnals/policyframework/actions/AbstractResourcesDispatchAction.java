
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
			
package com.ovpnals.policyframework.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.core.CoreUtil;
import com.ovpnals.policyframework.NoPermissionException;
import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.policyframework.PolicyDatabaseFactory;
import com.ovpnals.policyframework.PolicyUtil;
import com.ovpnals.policyframework.Resource;
import com.ovpnals.policyframework.ResourceItem;
import com.ovpnals.policyframework.ResourceStack;
import com.ovpnals.policyframework.ResourceType;
import com.ovpnals.policyframework.ResourceUtil;
import com.ovpnals.policyframework.forms.AbstractResourcesForm;
import com.ovpnals.security.Constants;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.User;
import com.ovpnals.table.actions.AbstractPagerAction;

/**
 * Abstract class which adds generic behaviour for resources, extending the
 * {@link com.ovpnals.table.actions.AbstractPagerAction}
 * 
 * @param <T>
 * @param <R>
 */
public abstract class AbstractResourcesDispatchAction<T extends Resource, R extends ResourceItem<T>> extends AbstractPagerAction {
    protected Permission editPermission;
    protected Permission createPermission;
    protected Permission removePermission;
    protected Permission assignPermission;

    /**
     * Constructor
     */
    public AbstractResourcesDispatchAction() {
        super();
    }

    /**
     * Constructor
     * 
     * @param resourceType
     * @param requiredPermissions
     * @param editPermission
     * @param createPermission
     * @param removePermission
     * @param assignPermission
     */
    public AbstractResourcesDispatchAction(ResourceType<T> resourceType, Permission[] requiredPermissions,
                                           Permission editPermission, Permission createPermission, Permission removePermission,
                                           Permission assignPermission) {
        this(resourceType, requiredPermissions, editPermission, createPermission, removePermission, assignPermission, null);
    }

    /**
     * Constructor for normal resource types that have the standard, Create /
     * Edit / Assign, Edit / Assign, Delete and Assign permissions
     * 
     * @param resourceType resource type
     * @param requiresResources requires actual resources of type
     */
    public AbstractResourcesDispatchAction(ResourceType<T> resourceType, ResourceType<T> requiresResources) {
        this(resourceType, new Permission[] { PolicyConstants.PERM_EDIT_AND_ASSIGN, PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN,
                        PolicyConstants.PERM_DELETE, PolicyConstants.PERM_ASSIGN,
                        PolicyConstants.PERM_PERSONAL_CREATE_EDIT_AND_DELETE }, PolicyConstants.PERM_EDIT_AND_ASSIGN,
                        PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_DELETE, PolicyConstants.PERM_ASSIGN,
                        requiresResources);
    }

    /**
     * Constructor for normal resource types that need their permissions
     * specifically stating.
     * 
     * @param resourceType
     * @param requiredPermissions
     * @param editPermission
     * @param createPermission
     * @param removePermission
     * @param assignPermission
     * @param requiresResources
     */
    public AbstractResourcesDispatchAction(ResourceType<T> resourceType, Permission[] requiredPermissions,
                                           Permission editPermission, Permission createPermission, Permission removePermission,
                                           Permission assignPermission, ResourceType<T> requiresResources) {
        super(resourceType, requiredPermissions, requiresResources);
        this.editPermission = editPermission;
        this.createPermission = createPermission;
        this.removePermission = removePermission;
        this.assignPermission = assignPermission;
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
        return list(mapping, form, request, response);
    }

    /**
     * Confirm removal of a resource
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward confirmRemove(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        try {
            int selectedResource = Integer.parseInt(request.getParameter("selectedResource"));
            User user = LogonControllerFactory.getInstance().getUser(request);
            T resource = getResourceById(selectedResource);
            PolicyDatabaseFactory.getInstance().isPersonalPermitted(resource, permissions, user);
            PolicyUtil.checkPermission(getResourceType(), PolicyConstants.PERM_PERSONAL_CREATE_EDIT_AND_DELETE,
                request);
        } catch (NoPermissionException e) {
            if (getRemovePermission() != null) {
                if (getResourceType() == null) {
                    throw new Exception("Concrete implementation of AbstractResourcesDispatchAction does not provide the ResourceType that it is maintaining.");
                }
                PolicyUtil.checkPermission(getResourceType(), getRemovePermission(), request);
            }
        }
        return mapping.findForward("confirmRemove");
    }

    /**
     * Create a resource
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        if (getCreatePermission() != null) {
            if (getResourceType() == null) {
                throw new Exception(
                    "Concrete implementation of AbstractResourcesDispatchAction does not provide the ResourceType that it is maintaining.");
            }
            PolicyUtil.checkPermission(getResourceType(), getCreatePermission(), request);
        }
        return mapping.findForward("create");
    }

    /**
     * Remove a resource.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        AbstractResourcesForm<R> resourcesForm = (AbstractResourcesForm<R>) form;
        T resource = getSelectedResource(form);
        Permission[] permissions = new Permission[] { getRemovePermission() };
        checkValid(mapping, resourcesForm, request, resource, permissions);

        doRemove(mapping, form, request, response);
        SessionInfo sessionInfo = getSessionInfo(request);
        PolicyDatabaseFactory.getInstance().detachResourceFromPolicyList(resource, sessionInfo);
        return getRedirectWithMessages(mapping, request);
    }
    
    /**
     * Perform the removal of a resource
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    protected void doRemove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        AbstractResourcesForm<R> resourcesForm = (AbstractResourcesForm<R>) form;
        getResourceType().removeResource(resourcesForm.getSelectedResource(), getSessionInfo(request));        
    }

    /**
     * View the resource.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ActionForward editForward = edit(mapping, form, request, response);
        ActionForward viewForward = mapping.findForward("view");
        if (viewForward == null) {
            return editForward;
        }
        return viewForward;
    }

    /**
     * Show information about the resource
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward forward
     * @throws Exception on any error
     */
    @SuppressWarnings("unchecked")
    public ActionForward information(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        T resource = getSelectedResource(form);
        try {
            ResourceUtil.checkResourceManagementRights(resource, getSessionInfo(request), new Permission[] {
                            PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_EDIT_AND_ASSIGN,
                            PolicyConstants.PERM_DELETE });
        } catch (NoPermissionException npe) {
            ResourceUtil.checkResourceAccessRights(resource, getSessionInfo(request));
        }
        request.setAttribute(Constants.REQ_ATTR_INFO_RESOURCE, resource);
        return mapping.findForward("resourceInformation");
    }

    /**
     * Edit the resource.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        AbstractResourcesForm<R> resourcesForm = (AbstractResourcesForm<R>) form;
        T resource = getSelectedResource(form);
        Permission[] permissions = new Permission[] { getEditPermission(), getCreatePermission(), getAssignPermission() };
        checkValid(mapping, resourcesForm, request, resource, permissions);

        ResourceStack.pushToEditingStack(request.getSession(), resource);
        return mapping.findForward("edit");
    }

    /**
     * Clone the resource.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public ActionForward clone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        AbstractResourcesForm<R> resourcesForm = (AbstractResourcesForm<R>) form;
        T resource = getSelectedResource(form);
        checkValid(resource, new Permission[] { getCreatePermission() }, mapping, resourcesForm, request);
        ResourceStack.pushToEditingStack(request.getSession(), resource);
        return mapping.findForward("clone");
    }

    /**
     * Display the resource.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return mapping.findForward("display");
    }

    /**
     * Change the selected view to {@link AbstractResourcesForm#ICONS_VIEW}.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    @SuppressWarnings("unchecked")
    public ActionForward viewIcons(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return viewAs(mapping, form, request, response, AbstractResourcesForm.ICONS_VIEW);
    }

    /**
     * Change the selected view to {@link AbstractResourcesForm#LIST_VIEW}.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    @SuppressWarnings("unchecked")
    public ActionForward viewList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return viewAs(mapping, form, request, response, AbstractResourcesForm.LIST_VIEW);
    }

    @SuppressWarnings("unchecked")
    private ActionForward viewAs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
                                 String viewType) {
        AbstractResourcesForm resourcesForm = (AbstractResourcesForm) form;
        resourcesForm.setSelectedView(viewType);
        int navigationContext = getSessionInfo(request).getNavigationContext();
        CoreUtil.storeUIState("ui_view_" + resourcesForm.getModel().getId() + "_" + navigationContext, viewType, request, response);
        return mapping.findForward("display");
    }

    @SuppressWarnings("unchecked")
    protected final T getSelectedResource(ActionForm form) throws Exception {
        AbstractResourcesForm<R> resourcesForm = (AbstractResourcesForm<R>) form;
        int resourceId = resourcesForm.getSelectedResource();
        T resource = getResourceById(resourceId);
        if (resource == null) {
            throw new Exception("No resource with ID " + resourceId);
        }
        return resource;
    }

    /**
     * @param resourceId
     * @return Resource
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public T getResourceById(int resourceId) throws Exception {
        ResourceType<T> resourceType = (ResourceType<T>) getResourceType();
        return resourceType.getResourceById(resourceId);
    }

    /**
     * @param resource
     * @param permission
     * @param mapping
     * @param form
     * @param request
     * @throws NoPermissionException
     */
    protected void checkValid(T resource, Permission[] permission, ActionMapping mapping, AbstractResourcesForm<R> form,
                              HttpServletRequest request) throws NoPermissionException {
        ResourceUtil.checkResourceManagementRights(resource, getSessionInfo(request), permission);
    }

    private void checkValid(ActionMapping mapping, AbstractResourcesForm<R> form, HttpServletRequest request, T resource,
                            Permission[] permissions) throws NoPermissionException {
        try {
            checkValid(resource, new Permission[] { PolicyConstants.PERM_PERSONAL_CREATE_EDIT_AND_DELETE }, mapping, form, request);
        } catch (NoPermissionException e) {
            checkValid(resource, permissions, mapping, form, request);
        }
    }

    /**
     * Get the edit permission.
     * 
     * @return Permission
     */
    public Permission getEditPermission() {
        return editPermission;
    }

    /**
     * Get the assign permission
     * 
     * @return Permission
     */
    public Permission getAssignPermission() {
        return assignPermission;
    }

    /**
     * Get the create permission
     * 
     * @return Permission
     */
    public Permission getCreatePermission() {
        return createPermission;
    }

    /**
     * Get the removal/delete permission
     * 
     * @return Permission
     */
    public Permission getRemovePermission() {
        return removePermission;
    }

    protected void saveError(HttpServletRequest request, String message, Resource resource) {
        saveError(request, message, resource.getResourceDisplayName());
    }

    protected void saveMessage(HttpServletRequest request, String message, Resource resource) {
        saveMessage(request, message, resource.getResourceDisplayName());
    }
}