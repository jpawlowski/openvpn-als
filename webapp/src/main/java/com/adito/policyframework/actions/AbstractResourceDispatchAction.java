
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
			
package com.adito.policyframework.actions;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.boot.PropertyList;
import com.adito.boot.Util;
import com.adito.core.CoreUtil;
import com.adito.core.UserDatabaseManager;
import com.adito.core.actions.AuthenticatedDispatchAction;
import com.adito.core.forms.CoreForm;
import com.adito.input.MultiSelectDataSource;
import com.adito.input.MultiSelectPoliciesSelectionModel;
import com.adito.navigation.FavoriteResourceType;
import com.adito.policyframework.NoPermissionException;
import com.adito.policyframework.OwnedResource;
import com.adito.policyframework.Permission;
import com.adito.policyframework.Policy;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.PolicyDataSource;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.PolicyExcludePersonalDataSource;
import com.adito.policyframework.PolicyUtil;
import com.adito.policyframework.Resource;
import com.adito.policyframework.ResourceStack;
import com.adito.policyframework.ResourceType;
import com.adito.policyframework.ResourceUtil;
import com.adito.policyframework.forms.AbstractFavoriteResourceForm;
import com.adito.policyframework.forms.AbstractResourceForm;
import com.adito.properties.Property;
import com.adito.properties.attributes.AttributeDefinition;
import com.adito.properties.attributes.AttributeValueItem;
import com.adito.properties.impl.policyattributes.PolicyAttributeKey;
import com.adito.properties.impl.policyattributes.PolicyAttributes;
import com.adito.properties.impl.resource.ResourceKey;
import com.adito.security.Constants;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.security.User;

/**
 * Abstract implementation of an
 * {@link com.adito.core.actions.AuthenticatedDispatchAction} that allows
 * viewing, editing and creating of
 * {@link com.adito.policyframework.Resource} implementations.
 */
public abstract class AbstractResourceDispatchAction extends AuthenticatedDispatchAction {
    final static Log log = LogFactory.getLog(AbstractResourceDispatchAction.class);

    protected Permission editPermission;
    protected Permission createPermission;
    protected Permission removePermission;
    protected Permission assignPermission;

    /**
     * Constructor that places now restriction on permissions required or
     * resources required.
     */
    public AbstractResourceDispatchAction() {
        super();
    }

    /**
     * Constructor for normal resource types that have the standard,
     * Create / Edit / Assign, Edit / Assign, Delete and Assign permissions
     * 
     * @param resourceType resource type
     */
    public AbstractResourceDispatchAction(ResourceType resourceType) {
        this(resourceType, new Permission[] { PolicyConstants.PERM_EDIT_AND_ASSIGN, PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_DELETE, PolicyConstants.PERM_ASSIGN, PolicyConstants.PERM_PERSONAL_CREATE_EDIT_AND_DELETE  },
                        PolicyConstants.PERM_EDIT_AND_ASSIGN, PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_DELETE,
                        PolicyConstants.PERM_ASSIGN);
    }

    /**
     * Constructor for specific cases
     * 
     * @param resourceType resource type of permissions required
     * @param requiredPermissions required permissions
     * @param editPermission required edit permission
     * @param createPermission required create permission
     * @param removePermission required remove permission
     * @param assignPermission required assign permission
     */
    public AbstractResourceDispatchAction(ResourceType resourceType, Permission[] requiredPermissions, Permission editPermission,
                                          Permission createPermission, Permission removePermission, Permission assignPermission) {
        super(resourceType, requiredPermissions);
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
        return edit(mapping, form, request, response);
    }

    /**
     * Clone a new instance of the resource. 
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward clone(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        assertCreatePermissions(request);
        Resource sourceResource = ResourceStack.popFromEditingStack(request.getSession());
        ResourceStack.pushToEditingStack(request.getSession(), sourceResource.getResourceType().cloneResource(sourceResource, getSessionInfo(request)));
        ActionForward fwd = edit(mapping, form, request, response);
        ((AbstractResourceForm) form).setCreating();
        return fwd;
    }

    /**
     * Create a new instance of the resource. Not all resource types will
     * require this as creation is usually done through a wizard.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        assertCreatePermissions(request);
        ResourceStack.pushToEditingStack(request.getSession(), createResource(mapping, form, request, response));
        ActionForward fwd = edit(mapping, form, request, response);
        ((AbstractResourceForm) form).setCreating();
        return fwd;
    }
    
 	protected void assertCreatePermissions(HttpServletRequest request) throws Exception, NoPermissionException {
        if (getCreateResourcePermission() != null) {
            if (getResourceType() == null) {
                throw new Exception(
                    "Concrete implementation of AbstractResourceDispatchAction does not provide the ResourceType that it is maintaining.");
            }
            PolicyUtil.checkPermission(getResourceType(), getCreateResourcePermission(), request);
        }
    }

    /**
     * If the resource supports creation using the edit screen (most dont, they
     * now use wizards) this method must return an instance of the support
     * resource. <code>null</code> should be returned if creation isn't
     * supported here
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return resource
     * @throws Exception on any error
     */
    public abstract Resource createResource(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception;

    /**
     * Commit the resource being edited.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception
     */
    public ActionForward commit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        AbstractResourceForm resourceForm = (AbstractResourceForm) form;
        resourceForm.apply();
        Resource resource = resourceForm.getResource();
        if(resourceForm.getEditing()) {
            resource.getResourceType().updateResource(resource, getSessionInfo(request));
        }
        else {
            resource = commitCreatedResource(mapping, resourceForm, request, response);
        	resourceForm.setResource(resource);
        }
        doUpdate(mapping, form, request, response);
        
        // Profiles are a special case that cannot have their policies changes
        if(resource.getResourceType() != PolicyConstants.PROFILE_RESOURCE_TYPE || !resource.getResourceName().equals("Default")) {
            PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(resource, resourceForm.getSelectedPoliciesList(), getSessionInfo(request));
        }
        if (resource.getResourceType() instanceof FavoriteResourceType) {
            ResourceUtil.setResourceGlobalFavorite(resource, ((AbstractFavoriteResourceForm) resourceForm).isFavorite());
        }

        // Update the attributes
        for(Iterator i = resourceForm.getAttributeValueItems().iterator(); i.hasNext(); ) {
           AttributeValueItem v = (AttributeValueItem)i.next();
           if(v.getDefinition().getVisibility() != AttributeDefinition.USER_CONFIDENTIAL_ATTRIBUTE) {
               if (v.getDefinition().getPropertyClass().getName().equals(PolicyAttributes.NAME)){
                   Property.setProperty(new PolicyAttributeKey(resource.getResourceId(), v.getName()), v.getDefinition().formatAttributeValue(v.getPropertyValue()), getSessionInfo(request));
               }
               else{
                   Property.setProperty(new ResourceKey(v.getDefinition().getName(), resource.getResourceType(), resource.getResourceId()), v.getDefinition().formatAttributeValue(v.getPropertyValue()), getSessionInfo(request));
               }
           }
        }
        return cleanUpAndReturnToReferer(mapping, form, request, response);
    }

    /**
     * <p> Method to be overridden when extra work needs to be done on update, in its abstract form it does nothing.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @throws Exception 
     */
    protected void doUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception{
    }

    /**
     * View the resource. This sets the read only flag on the form and the
     * process as it if were an edit.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception
     */
    public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ((AbstractResourceForm) form).setReadOnly();
        return edit(mapping, form, request, response);
    }

    /**
     * Edit the resource. An instance of the resource to edit should have been
     * passed in as a session attribute {@link Constants#EDITING_RESOURCE_STACK}.
     * <p>
     * Permissions will be checked and the form initialised.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ((AbstractResourceForm) form).setEditing();
        Resource resource = ResourceStack.peekEditingStack(request.getSession());
        if (resource == null){
            // we must have lost the session, go back to home
            return mapping.findForward("home");
        }
        SessionInfo session = this.getSessionInfo(request);
        try {
            checkValid(resource, new Permission[] { PolicyConstants.PERM_PERSONAL_CREATE_EDIT_AND_DELETE }, mapping, (AbstractResourceForm) form, request);
        } catch (NoPermissionException e) {
            checkValid(resource, new Permission[] { getEditResourcePermission(), getCreateResourcePermission(), getAssignPermission() }, mapping, (AbstractResourceForm) form, request);
        }
        User ownerUser = null;
        if (resource instanceof OwnedResource) {
            String owner = ((OwnedResource) resource).getOwnerUsername();
            if (owner != null && !owner.equals("")) {
                ownerUser = UserDatabaseManager.getInstance().getUserDatabase(session.getUser().getRealm()).getAccount(owner);
            }
        }
        PropertyList selectedPolicies = new PropertyList();
        MultiSelectPoliciesSelectionModel policyModel = initSelectModel(selectedPolicies, ((AbstractResourceForm) form).isShowPersonalPolicies(), resource, session);
        String referer = CoreUtil.getReferer(request);
        ((CoreForm) form).setReferer(referer);
        ((AbstractResourceForm) form).initialise(request, resource, true, policyModel, selectedPolicies, ownerUser, isAssignOnly(resource, session));
        return display(mapping, form, request, response);
    }

    /**
     * Werather the operation has assign only permissions.
     * 
     * @param resource
     * @param session
     * @return boolean
     * @throws Exception
     */
    private boolean isAssignOnly(Resource resource, SessionInfo session) throws Exception {
        if (!LogonControllerFactory.getInstance().isAdministrator(session.getUser())){
        	boolean canAssign = PolicyDatabaseFactory.getInstance().isPermitted(resource.getResourceType(), new Permission[] {PolicyConstants.PERM_ASSIGN}, session.getUser(), false);
        	boolean canEditAssign = PolicyDatabaseFactory.getInstance().isPermitted(resource.getResourceType(), new Permission[] {PolicyConstants.PERM_EDIT_AND_ASSIGN}, session.getUser(), false);
        	boolean canCreateEditAssign = PolicyDatabaseFactory.getInstance().isPermitted(resource.getResourceType(), new Permission[] {PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN}, session.getUser(), false);
            if (canAssign & !canEditAssign & !canCreateEditAssign)
                return true;
        }
        return false;
    }

    /**
     * Confirm removal of the resource. This would usually forward on to a
     * struts path that points to the
     * {@link com.adito.navigation.actions.ConfirmAction}.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward confirmRemove(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        return mapping.findForward("confirmRemove");
    }

    /**
     * Refresh this page. Simple forwards on to <i>refresh</i> forward from the
     * action mapping.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return mapping.findForward("refresh");
    }

    /**
     * Display the resource. The <i>Required Field</i> message will be also be
     * added.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward display(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        CoreUtil.addRequiredFieldMessage(this, request);
        return mapping.findForward("display");
    }

    /**
     * Get the permission that is required for editing the resource or
     * <code>null</code> if no permission is required.
     * 
     * @return edit permission
     */
    public Permission getEditResourcePermission() {
        return editPermission;
    }

    /**
     * Get the permission that is required for assign policies to a resource
     * <code>null</code> if no permission is required.
     * 
     * @return assign permission
     */
    public Permission getAssignPermission() {
        return assignPermission;
    }

    /**
     * Get the permission that is required for creating the resource or
     * <code>null</code> if no permission is required.
     * 
     * @return create permission
     */

    public Permission getCreateResourcePermission() {
        return createPermission;
    }

    /**
     * Get the permission that is required for removing the resource or
     * <code>null</code> if no permission is required.
     * 
     * @return remove permission
     */
    public Permission getRemoveResourcePermission() {
        return removePermission;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.actions.AuthenticatedDispatchAction#cleanUpAndReturnToReferer(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward cleanUpAndReturnToReferer(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
    	ResourceStack.popFromEditingStack(request.getSession());
        return super.cleanUpAndReturnToReferer(mapping, form, request, response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
    }

    /**
     * Check the resource is valid for management.
     * 
     * @param r resource
     * @param permissions permissions
     * @param mapping mappnig
     * @param form form
     * @param request request
     * @throws NoPermissionException
     */
    protected void checkValid(Resource r, Permission[] permissions, ActionMapping mapping, AbstractResourceForm form,
                    HttpServletRequest request) throws NoPermissionException {
        ResourceUtil.checkResourceManagementRights(r, this.getSessionInfo(request), permissions);
    }
    
    /**
     * Create the resource.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return newly created resource ID
     * @throws Exception on any error
     */
    protected Resource commitCreatedResource(ActionMapping mapping, AbstractResourceForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Resource resource = form.getResource();
        return resource.getResourceType().createResource(resource, getSessionInfo(request));
    }

    protected MultiSelectDataSource createAvailablePoliciesDataSource() throws Exception {
        return new PolicyDataSource();
    }
    
    protected MultiSelectDataSource createAvailablePoliciesExcludePersonalDataSource() throws Exception {
        return new PolicyExcludePersonalDataSource();
    }
    
    /**
     * Toggle show personal policies.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward
     * @throws Exception
     */
    public ActionForward toogleShowPersonalPolicies(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                    HttpServletResponse response) throws Exception {
        AbstractResourceForm policyForm = (AbstractResourceForm) form;

        Resource resource = ResourceStack.peekEditingStack(request.getSession());
        SessionInfo session = this.getSessionInfo(request);

        PropertyList selectedPolicies = new PropertyList();
        String requestSelectedPolicies = request.getParameter("selectedPolicies");
        if (!Util.isNullOrTrimmedBlank(requestSelectedPolicies)) {
            selectedPolicies.setAsTextFieldText(requestSelectedPolicies);
        }
        MultiSelectPoliciesSelectionModel policyModel = initSelectModel(selectedPolicies, ((AbstractResourceForm) form)
                        .isShowPersonalPolicies(), resource, session);

        policyForm.setPolicyModel(policyModel);
        policyForm.setSelectedPolicies(selectedPolicies);
            
        return display(mapping, form, request, response);
    }

    protected MultiSelectPoliciesSelectionModel initSelectModel(PropertyList selectedPolicies, boolean isShowPersonalPolicies, Resource resource, SessionInfo session) throws Exception {
        MultiSelectDataSource policies = createAvailablePoliciesDataSource();
        MultiSelectDataSource personalPolicies = null;
        if (!isShowPersonalPolicies) {
            personalPolicies = policies;
            policies = createAvailablePoliciesExcludePersonalDataSource();
        }
        List l = PolicyDatabaseFactory.getInstance().getPoliciesAttachedToResource(resource, session.getRealm());
        for (Iterator i = l.iterator(); i.hasNext();) {
            selectedPolicies.add(String.valueOf(((Policy) i.next()).getResourceId()));
        }
        // MultiSelectSelectionModel policyModel = new
        // MultiSelectSelectionModel(session, policies, selectedPolicies);
        MultiSelectPoliciesSelectionModel policyModel = new MultiSelectPoliciesSelectionModel(session, policies, personalPolicies,
                        selectedPolicies);
        return policyModel;
    }
    
    protected void saveError(HttpServletRequest request, String message, Resource resource) {
        saveError(request, message, resource.getResourceDisplayName());
    }
    
    protected void saveMessage(HttpServletRequest request, String message, Resource resource) {
        saveMessage(request, message, resource.getResourceDisplayName());
    }
}