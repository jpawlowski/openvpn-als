
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

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.policyframework.Policy;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.PolicyItem;
import com.adito.policyframework.forms.PoliciesForm;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;


/**
 * Implementation of an {@link com.adito.policyframework.actions.AbstractResourcesDispatchAction}
 * that lists all manageable policies.
 */
public class PoliciesDispatchAction extends AbstractResourcesDispatchAction<Policy, PolicyItem> {

    /**
     * Constructor
     */
    public PoliciesDispatchAction() {
        super(PolicyConstants.POLICY_RESOURCE_TYPE, PolicyConstants.POLICY_RESOURCE_TYPE);
    }

    /* (non-Javadoc)
     * @see com.adito.policyframework.actions.AbstractResourcesDispatchAction#list(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PoliciesForm policyForm = (PoliciesForm) form;
        List<Policy> policies = Collections.<Policy>emptyList();
        if (policyForm.isShowPersonalPolicies()) {
            policies = PolicyDatabaseFactory.getInstance().getPolicies(getSessionInfo(request).getUser().getRealm());
        } else {
            policies = PolicyDatabaseFactory.getInstance().getPoliciesExcludePersonal(getSessionInfo(request).getUser().getRealm());
        }
        policyForm.initialize(policies, Policy.class, PolicyItem.class, request.getSession(), "name");
        return super.list(mapping, form, request, response);
    }

    /* (non-Javadoc)
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
    }
    
    @Override
    protected void doRemove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.doRemove(mapping, form, request, response);
        // we now need to rebuild any menus, as more or less could be visible.
        LogonControllerFactory.getInstance().applyMenuItemChanges(request);
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
    public ActionForward toogleShowPersonalPolicies(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                throws Exception {
        PoliciesForm policyForm = (PoliciesForm) form;
        policyForm.setShowPersonalPolicies(!policyForm.isShowPersonalPolicies());
        return list(mapping, form, request, response);
    }

    /* (non-Javadoc)
     * @see com.adito.policyframework.actions.AbstractResourcesDispatchAction#remove(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        Policy resource = getSelectedResource(form);
        super.remove(mapping, form, request, response);
        saveMessage(request, "policy.deleted.message", resource);
        return getRedirectWithMessages(mapping, request);
    }

}