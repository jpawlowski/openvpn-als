
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
			
package net.openvpn.als.properties.actions;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.policyframework.NoPermissionException;
import net.openvpn.als.policyframework.OwnedResource;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.policyframework.PolicyUtil;
import net.openvpn.als.policyframework.ResourceUtil;
import net.openvpn.als.policyframework.actions.AbstractResourcesDispatchAction;
import net.openvpn.als.policyframework.forms.AbstractResourcesForm;
import net.openvpn.als.properties.ProfilesFactory;
import net.openvpn.als.properties.PropertyProfile;
import net.openvpn.als.properties.forms.ProfilesForm;
import net.openvpn.als.properties.forms.PropertyProfileItem;
import net.openvpn.als.security.Constants;
import net.openvpn.als.security.SessionInfo;

/**
 * This dispatch action is used for display and maintaing lists of property
 * profiles. Property profiles are the one type of resource in OpenVPNALS that
 * still has a 'scope', i.e. Personal Profiles may still be created.
 */

public class ShowProfilesDispatchAction extends AbstractResourcesDispatchAction<PropertyProfile, PropertyProfileItem> {

    /**
     * Constructor
     */
    public ShowProfilesDispatchAction() {
        super(PolicyConstants.PROFILE_RESOURCE_TYPE, PolicyConstants.PROFILE_RESOURCE_TYPE);
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
        ActionForward fwd = super.unspecified(mapping, form, request, response);
        ProfilesForm profilesForm = (ProfilesForm) form;
        SessionInfo session = this.getSessionInfo(request);
        List globalProfiles = null;
        List personalProfiles = null;
        if (session.getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT) {
            profilesForm.setProfileScope(Constants.SCOPE_GLOBAL);
            globalProfiles = ProfilesFactory.getInstance().getPropertyProfiles(null, true,
                            session.getUser().getRealm().getResourceId());
        } else {
            profilesForm.setProfileScope(Constants.SCOPE_PERSONAL);
            globalProfiles = ResourceUtil.filterOwned(ResourceUtil.getGrantedResource(
                            getSessionInfo(request), getResourceType()));            
            personalProfiles = ResourceUtil.filterResources(session.getUser(), ProfilesFactory.getInstance().getPropertyProfiles(
                session.getUser().getPrincipalName(), true, session.getUser().getRealm().getResourceId()), false);
        }
        PropertyProfile profile = (PropertyProfile) request.getSession().getAttribute(Constants.SELECTED_PROFILE);
        if (profile != null) {
            profilesForm.initialize(globalProfiles, personalProfiles, request.getSession(), profile.getResourceId());
        } else {
            profilesForm.initialize(globalProfiles, personalProfiles, request.getSession(), -1);
        }
        profilesForm.checkSelectedView(request, response);
        return fwd;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.policyframework.actions.AbstractResourcesDispatchAction#create(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        if (getSessionInfo(request).getNavigationContext() == SessionInfo.USER_CONSOLE_CONTEXT) {
            PolicyUtil.checkPermission(PolicyConstants.PERSONAL_PROFILE_RESOURCE_TYPE, PolicyConstants.PERM_MAINTAIN, request);
        } else {
            return super.create(mapping, form, request, response);
        }
        return mapping.findForward("create");
    }

    /**
     * Confirm removal of a resource
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward confirmRemove(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        ProfilesForm f = (ProfilesForm) form;
        PropertyProfile profile = (PropertyProfile) getResourceById(f.getSelectedResource());
        if (profile == null) {
            throw new Exception("Selected profile '" + f.getSelectedResource() + "' doesn't exist.");
        }
        if(profile.getOwnerUsername() != null) {
            SessionInfo session = getSessionInfo(request);
            if(!session.getUser().getPrincipalName().equals(profile.getOwnerUsername())) {
                throw new Exception("Cannot delete profiles owned by others.");
            }
            return mapping.findForward("confirmRemove");
        }
        else {
            return super.confirmRemove(mapping, form, request, response);
        }
    }


    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.policyframework.actions.AbstractResourcesDispatchAction#remove(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ProfilesForm f = (ProfilesForm) form;
        ActionMessages errors = new ActionMessages();
        PropertyProfile profile = (PropertyProfile) getResourceById(f.getSelectedResource());
        if (profile == null) {
            throw new Exception("Selected profile '" + f.getSelectedResource() + "' doesn't exist.");
        }
        if (profile.getOwnerUsername() == null && Constants.SCOPE_PERSONAL.equals(f.getProfileScope())) {
            errors.add(Globals.ERROR_KEY, new ActionMessage("error.deleteProfile.cantDeleteGlobalProfile"));
        }
        if (profile.getResourceName().equalsIgnoreCase("Default")
                        && (profile.getOwnerUsername() == null || profile.getOwnerUsername().equals(""))) {
            errors.add(Globals.ERROR_KEY, new ActionMessage("error.deleteProfile.cantDeleteDefaultProfile"));
        }
        saveErrors(request, errors);
        CoreUtil.resetMainNavigation(request.getSession());
        if (errors.size() > 0) {
            String returnTo = request.getParameter("returnTo");
            return returnTo != null ? new ActionForward(returnTo, false) : mapping.getInputForward();
        } else {
            saveMessage(request, "message.profileDeleted", profile);
            return super.remove(mapping, form, request, response);
        }
    }

    /**
     * Dispatch action target called when the user wishes to select a profile.
     * After checking if the user has access, the profile will be made the
     * current profile for the session.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward select(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ProfilesForm f = (ProfilesForm) form;
        PropertyProfile profile = ProfilesFactory.getInstance().getPropertyProfile(f.getSelectedResource());
        SessionInfo session = getSessionInfo(request);
        ResourceUtil.checkResourceAccessRights(profile, session);
        request.getSession().setAttribute(Constants.SELECTED_PROFILE, profile);
        ActionMessages messages = new ActionMessages();
        messages.add(Globals.MESSAGES_KEY, new ActionMessage("message.profileSelected", profile.getResourceName()));
        saveMessages(request, messages);
        return mapping.findForward("refresh");
    }

    /**
     * An alternative method of selecting a profile, this one forwards to
     * 'selectPropertyProfile' which also allows the selected to be chosen as a
     * default
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward selectOrDefault(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        return mapping.findForward("selectOrDefault");
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
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
    }

    protected void checkValid(PropertyProfile r, Permission[] permission, ActionMapping mapping, AbstractResourcesForm form,
                    HttpServletRequest request) throws NoPermissionException {
        if (r instanceof OwnedResource && ((OwnedResource) r).getOwnerUsername() != null) {
            super.checkValid(r, new Permission[] { PolicyConstants.PERM_MAINTAIN }, mapping, form, request);
        } else {
            super.checkValid(r, permission, mapping, form, request);
        }
    }
}
