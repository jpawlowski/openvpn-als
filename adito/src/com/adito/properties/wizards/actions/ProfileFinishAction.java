
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
			
package com.adito.properties.wizards.actions;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.boot.PropertyList;
import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.core.CoreServlet;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.Resource;
import com.adito.policyframework.ResourceChangeEvent;
import com.adito.policyframework.ResourceUtil;
import com.adito.properties.ProfilesFactory;
import com.adito.properties.PropertyProfile;
import com.adito.properties.wizards.forms.ProfileDetailsForm;
import com.adito.properties.wizards.forms.ProfilePolicySelectionForm;
import com.adito.security.Constants;
import com.adito.security.SessionInfo;
import com.adito.security.User;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.WizardActionStatus;
import com.adito.wizard.actions.AbstractWizardAction;
import com.adito.wizard.forms.AbstractWizardFinishForm;

/**
 * <p>
 * Finish action for the creation of a profile.
 */
public class ProfileFinishAction extends AbstractWizardAction {
    final static Log log = LogFactory.getLog(ProfileFinishAction.class);

    /**
     * Constructor
     */
    public ProfileFinishAction() {
        super();
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
        return SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.actions.AbstractWizardAction#unspecified(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        // Do the install
        List actionStatus = new ArrayList();
        AbstractWizardSequence seq = getWizardSequence(request);
        SessionInfo info = this.getSessionInfo(request);
        User user = seq.getSession().getUser();
        String scope = (String) seq.getAttribute(ProfileDetailsAction.ATTR_PROFILE_SCOPE, null);
        int baseOn = ((Integer) seq.getAttribute(ProfileDetailsForm.ATTR_BASE_ON, null)).intValue();
        String username = Constants.SCOPE_GLOBAL.equals(scope) ? null : user.getPrincipalName();
        int realmId = user.getRealm().getRealmID();
        String shortName = (String) seq.getAttribute(ProfileDetailsForm.ATTR_RESOURCE_NAME, null);
        String description = (String) seq.getAttribute(ProfileDetailsForm.ATTR_RESOURCE_DESCRIPTION, null);
        PropertyProfile newProfile = null;
        try {
            try {
                // TODO get the creating resource permission
                newProfile = ProfilesFactory.getInstance().createPropertyProfile(username, shortName, description, baseOn, realmId);
                CoreServlet.getServlet().fireCoreEvent(
                    new ResourceChangeEvent(this, CoreEventConstants.CREATE_PROPERTY_PROFILE, newProfile, info,
                                    CoreEvent.STATE_SUCCESSFUL));
            } catch (Exception e) {
                CoreServlet.getServlet().fireCoreEvent(
                    new ResourceChangeEvent(this, CoreEventConstants.CREATE_PROPERTY_PROFILE, info, e));
                throw e;
            }
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_OK,
                            "profileWizard.profileFinish.status.profileCreated"));
        } catch (Exception e) {
            log.error("Failed to create profile.", e);
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                            "profileWizard.profileFinish.status.failedToCreateProfile", e.getMessage()));
        }
        if (newProfile != null) {
            actionStatus.add(attachToPolicies(seq, info, newProfile));
        }
        ResourceUtil.setAvailableProfiles(info);
        ((AbstractWizardFinishForm) form).setActionStatus(actionStatus);
        return super.unspecified(mapping, form, request, response);
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return Action forward
     * @throws Exception
     */
    public ActionForward exit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return cancel(mapping, form, request, response);
    }

    /**
     * @param seq
     * @param info
     * @param resource
     * @return WizardActionStatus
     */
    WizardActionStatus attachToPolicies(AbstractWizardSequence seq, SessionInfo info, Resource resource) {
        PropertyList selectedPolicies = (PropertyList) seq.getAttribute(ProfilePolicySelectionForm.ATTR_SELECTED_POLICIES, null);
        try {
            PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(resource, selectedPolicies, info);
            return new WizardActionStatus(WizardActionStatus.COMPLETED_OK, "profileWizard.profileFinish.status.attachedToPolicies");
        } catch (Exception e) {
            log.error("Failed to create profile.", e);
            return new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                            "profileWizard.profileFinish.status.failedToAttachToPolicies", e.getMessage());
        }
    }

}
