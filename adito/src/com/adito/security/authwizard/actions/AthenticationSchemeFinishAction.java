
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
			
package com.adito.security.authwizard.actions;

import java.util.ArrayList;
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
import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.core.CoreServlet;
import com.adito.policyframework.ResourceChangeEvent;
import com.adito.security.AuthenticationScheme;
import com.adito.security.AuthenticationSchemeResourceType;
import com.adito.security.SessionInfo;
import com.adito.security.SystemDatabaseFactory;
import com.adito.security.authwizard.forms.AthenticationSchemeDetailsForm;
import com.adito.security.authwizard.forms.AthenticationSchemeSelectionForm;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.WizardActionStatus;
import com.adito.wizard.actions.AbstractWizardAction;
import com.adito.wizard.forms.AbstractWizardFinishForm;

/**
 * Implementation of a {@link AbstractWizardAction}. performs the finish action
 * for the creation of an authentication scheme.
 */
public class AthenticationSchemeFinishAction extends AbstractWizardAction {

    private static final Log log = LogFactory.getLog(AthenticationSchemeFinishAction.class);

    /*
     * (non-Javadoc)
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
     * @see org.apache.struts.actions.DispatchAction#unspecified(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<WizardActionStatus> actionStatus = new ArrayList<WizardActionStatus>();
        AbstractWizardSequence seq = getWizardSequence(request);
        String name = (String) seq.getAttribute(AthenticationSchemeDetailsForm.ATTR_RESOURCE_NAME, null);
        String description = (String) seq.getAttribute(AthenticationSchemeDetailsForm.ATTR_RESOURCE_DESCRIPTION, null);
        PropertyList selectedModules = ((PropertyList) seq.getAttribute(AthenticationSchemeSelectionForm.ATTR_SELECTED_MODULES, new PropertyList()));
        AuthenticationScheme defaultAuthenticationScheme = null;
        try {
            try {
                int priority = AuthenticationSchemeResourceType.getAuthenticationSchemePriority(getSessionInfo(request));
                defaultAuthenticationScheme = SystemDatabaseFactory.getInstance().createAuthenticationSchemeSequence(
                                getSessionInfo(request).getUser().getRealm().getRealmID(), name, description,
                                selectedModules.asArray(), true, priority);
                CoreEvent evt = new ResourceChangeEvent(this, CoreEventConstants.CREATE_AUTHENTICATION_SCHEME, defaultAuthenticationScheme, getSessionInfo(request), CoreEvent.STATE_SUCCESSFUL);
                int authCounter = 1;
                for (Iterator i = selectedModules.iterator(); i.hasNext();) {
                    AuthenticationSchemeResourceType.addAuthenticationModule(evt, (String) i.next(), authCounter);
                    authCounter++;
                }
                CoreServlet.getServlet().fireCoreEvent(evt);
            } catch (Exception e) {
                CoreServlet.getServlet().fireCoreEvent(
                    new ResourceChangeEvent(this, CoreEventConstants.CREATE_AUTHENTICATION_SCHEME, getSessionInfo(request), e));
                throw e;
            }
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_OK,
                            "authwizard.athenticationSchemeFinish.status.authenticationSchemeCreated"));
        } catch (Exception e) {
            log.error("Failed to create profile.", e);
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                            "authwizard.athenticationSchemeFinish.status.failedToCreateAuthenticationScheme", e.getMessage()));
        }
        if (defaultAuthenticationScheme != null) {
            actionStatus.add(attachToPoliciesAndAddToFavorites("authwizard.athenticationSchemeFinish", seq, defaultAuthenticationScheme, false, request));
        }
        ((AbstractWizardFinishForm) form).setActionStatus(actionStatus);
        return super.unspecified(mapping, form, request, response);
    }
    
    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return Action forward.
     * @throws Exception
     */
    public ActionForward exit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return cancel(mapping, form, request, response);
    }
}
