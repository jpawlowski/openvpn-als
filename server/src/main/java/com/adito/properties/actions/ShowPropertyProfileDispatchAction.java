
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
			
package com.adito.properties.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.adito.core.CoreEventConstants;
import com.adito.core.CoreServlet;
import com.adito.core.CoreUtil;
import com.adito.policyframework.NoPermissionException;
import com.adito.policyframework.OwnedResource;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.PolicyUtil;
import com.adito.policyframework.Resource;
import com.adito.policyframework.ResourceChangeEvent;
import com.adito.policyframework.ResourceStack;
import com.adito.policyframework.ResourceUtil;
import com.adito.policyframework.actions.AbstractResourceDispatchAction;
import com.adito.policyframework.forms.AbstractResourceForm;
import com.adito.properties.DefaultPropertyProfile;
import com.adito.properties.ProfilesFactory;
import com.adito.properties.PropertyProfile;
import com.adito.properties.forms.PropertyProfileForm;
import com.adito.security.Constants;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.security.User;

/**
 * This dispatch action is responsible for allowing editing or creation of
 * property profiles. Property profiles are the one resource in Adito
 * that still have 'scope', i.e. a user may create personal profiles.
 * <p>
 * Although this class could easily support it, creation of global profiles is
 * now done through a wizard and thus a different action.
 * 
 * @see com.adito.properties.PropertyProfile
 * @see com.adito.properties.forms.PropertyProfileForm
 */

public class ShowPropertyProfileDispatchAction extends AbstractResourceDispatchAction {

    /**
     */
    public ShowPropertyProfileDispatchAction() {
        super(PolicyConstants.PROFILE_RESOURCE_TYPE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.actions.AbstractResourceDispatchAction#create(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ResourceStack.pushToEditingStack(request.getSession(), createResource(mapping, form, request, response));
        ActionForward fwd = edit(mapping, form, request, response);
        ((AbstractResourceForm) form).setCreating();
        return fwd;
    }

    protected void doCheckPermissions(ActionMapping mapping, SessionInfo session, HttpServletRequest request) throws Exception {
        if (session.getNavigationContext() == SessionInfo.USER_CONSOLE_CONTEXT) {
            PolicyUtil.checkPermission(PolicyConstants.PERSONAL_PROFILE_RESOURCE_TYPE, PolicyConstants.PERM_MAINTAIN, request);
        } else {
            super.doCheckPermissions(mapping, session, request);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.actions.AbstractResourceDispatchAction#createResource(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public Resource createResource(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        SessionInfo session = getSessionInfo(request);
        String principalName = session.isUserConsoleContext() ? session.getUser().getPrincipalName() : null;
        return new DefaultPropertyProfile(session.getRealmId(), principalName, "", "");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.actions.AbstractResourceDispatchAction#edit(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PropertyProfileForm pf = (PropertyProfileForm) form;
        ActionForward forward = super.edit(mapping, form, request, response);
        if (forward.getName().equals("home")){
            // super returned a home, so we must go home.
            return forward;
        }
        String scope = pf.getOwner() == null ? Constants.SCOPE_GLOBAL : Constants.SCOPE_PERSONAL;
        pf.setPropertyScope(scope);
        return forward;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.actions.AbstractResourceDispatchAction#commit(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward commit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ActionMessages messages = new ActionMessages();
        User user = LogonControllerFactory.getInstance().getUser(request);
        PropertyProfileForm profileForm = (PropertyProfileForm) form;
        PropertyProfile profile = (PropertyProfile) profileForm.getResource();
        if (profileForm.getPropertyScope().equals(Constants.SCOPE_GLOBAL)) {
            PolicyUtil.checkPermissions(PolicyConstants.PROFILE_RESOURCE_TYPE, new Permission[] {
                            PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_EDIT_AND_ASSIGN,
                            PolicyConstants.PERM_ASSIGN }, request);
        } else {
            PolicyUtil.checkPermission(PolicyConstants.PERSONAL_PROFILE_RESOURCE_TYPE, PolicyConstants.PERM_MAINTAIN, request);
        }
        profileForm.apply();
        if (profileForm.getEditing()) {
            PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(profile, profileForm.getSelectedPoliciesList(),
                getSessionInfo(request));
            profile.getResourceType().updateResource(profile, getSessionInfo(request));
        } else {
            int baseOn = profileForm.getSelectedPropertyProfile();
            User owner = profileForm.getOwner();
            int realmId = user.getRealm().getRealmID();

            try {
                PropertyProfile newProfile = ProfilesFactory.getInstance().createPropertyProfile(
                    owner == null ? null : owner.getPrincipalName(), profile.getResourceName(), profile.getResourceDescription(),
                    baseOn, realmId);

                PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(newProfile, profileForm.getSelectedPoliciesList(),
                    getSessionInfo(request));
                CoreServlet.getServlet().fireCoreEvent(
                    new ResourceChangeEvent(this, CoreEventConstants.CREATE_PROPERTY_PROFILE, newProfile, getSessionInfo(request),
                                    ResourceChangeEvent.STATE_SUCCESSFUL));
                profileForm.setResource(newProfile);
            } catch (Exception e) {
                CoreServlet.getServlet().fireCoreEvent(
                    new ResourceChangeEvent(this, CoreEventConstants.CREATE_PROPERTY_PROFILE, getSessionInfo(request), e));
                throw e;
            }
        }
        messages.add(Globals.MESSAGES_KEY, new ActionMessage("message.profileSaved"));
        saveMessages(request, messages);
        ResourceUtil.setAvailableProfiles(getSessionInfo(request));
        return cleanUpAndReturnToReferer(mapping, form, request, response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.actions.AbstractResourceDispatchAction#display(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward display(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ((PropertyProfileForm) form).setReferer(CoreUtil.getReferer(request));
        CoreUtil.addRequiredFieldMessage(this, request);
        return mapping.findForward("display");
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

    protected void checkValid(Resource r, Permission[] permissions, ActionMapping mapping, AbstractResourceForm form,
                              HttpServletRequest request) throws NoPermissionException {
        if (r instanceof OwnedResource && ((OwnedResource) r).getOwnerUsername() != null) {
            super.checkValid(r, new Permission[] { PolicyConstants.PERM_MAINTAIN }, mapping, form, request);
        } else {
            super.checkValid(r, permissions, mapping, form, request);
        }
    }

}