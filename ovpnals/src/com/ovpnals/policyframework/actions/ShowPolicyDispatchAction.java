
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

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.core.CoreAttributeConstants;
import com.ovpnals.core.CoreEvent;
import com.ovpnals.core.CoreEventConstants;
import com.ovpnals.core.CoreServlet;
import com.ovpnals.core.UserDatabaseManager;
import com.ovpnals.input.MultiSelectDataSource;
import com.ovpnals.policyframework.DefaultAccessRights;
import com.ovpnals.policyframework.DefaultPolicy;
import com.ovpnals.policyframework.DelegatedPoliciesDataSource;
import com.ovpnals.policyframework.Policy;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.policyframework.PolicyDatabaseFactory;
import com.ovpnals.policyframework.Principal;
import com.ovpnals.policyframework.Resource;
import com.ovpnals.policyframework.forms.PolicyForm;
import com.ovpnals.properties.Property;
import com.ovpnals.properties.attributes.AttributeDefinition;
import com.ovpnals.properties.attributes.AttributeValueItem;
import com.ovpnals.properties.impl.policyattributes.PolicyAttributeKey;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.User;
import com.ovpnals.security.UserDatabase;

/**
 * Implementation of a
 * {@link com.ovpnals.policyframework.actions.AbstractResourceDispatchAction}
 * that allows editing of a <i>Policy</i> resource.
 */
public class ShowPolicyDispatchAction extends AbstractResourceDispatchAction {

    final static Log log = LogFactory.getLog(ShowPolicyDispatchAction.class);

    /**
     * Constructor
     */
    public ShowPolicyDispatchAction() {
        super(PolicyConstants.POLICY_RESOURCE_TYPE);
    }

    public ActionForward commit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ActionForward fwd = super.commit(mapping, form, request, response);
        PolicyForm policyForm = (PolicyForm) form;
        Policy pol = (Policy) policyForm.getResource();
        UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(getSessionInfo(request).getUser().getRealm());
        if (pol.getResourceId() != PolicyDatabaseFactory.getInstance().getEveryonePolicyIDForRealm(udb.getRealm())) {
            List wasAttached = PolicyDatabaseFactory.getInstance().getPrincipalsGrantedPolicy(pol, udb.getRealm()); // objects
            List nowAttached = policyForm.getSelectedAccountsList();
            for (Iterator i = wasAttached.iterator(); i.hasNext();) {
                Principal p = (Principal) i.next();
                try {
                    if (!nowAttached.contains(p.getPrincipalName())) {
                        CoreServlet.getServlet().fireCoreEvent(
                            new CoreEvent(this, CoreEventConstants.REVOKE_POLICY_FROM_PRINCIPAL, null, null,
                                            CoreEvent.STATE_SUCCESSFUL).addAttribute(
                                CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_TYPE, p instanceof User ? "user" : "group")
                                            .addAttribute(CoreAttributeConstants.EVENT_ATTR_POLICY_NAME, pol.getResourceName())
                                            .addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID, p.getPrincipalName()));

                    }
                } catch (Exception e) {
                    CoreServlet.getServlet().fireCoreEvent(
                        new CoreEvent(this, CoreEventConstants.REVOKE_POLICY_FROM_PRINCIPAL, null, null, e).addAttribute(
                            CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_TYPE, p instanceof User ? "user" : "group").addAttribute(
                            CoreAttributeConstants.EVENT_ATTR_POLICY_NAME, pol.getResourceName()).addAttribute(
                            CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID, p.getPrincipalName()));
                    throw e;
                }
            }

            // TODO fire revoking events
            PolicyDatabaseFactory.getInstance().revokePolicyFromAllPrincipals(pol, udb.getRealm());
            for (Iterator i = nowAttached.iterator(); i.hasNext();) {
                Principal p = udb.getAccount((String) i.next());
                try {
                    PolicyDatabaseFactory.getInstance().grantPolicyToPrincipal(pol, p);
                    CoreServlet.getServlet().fireCoreEvent(
                        new CoreEvent(this, CoreEventConstants.GRANT_POLICY_TO_PRINCIPAL, null, null, CoreEvent.STATE_SUCCESSFUL)
                                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_TYPE,
                                            "user").addAttribute(
                                            CoreAttributeConstants.EVENT_ATTR_POLICY_NAME, pol.getResourceName()).addAttribute(
                                            CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID, p.getPrincipalName()));
                } catch (Exception e) {
                    CoreServlet.getServlet().fireCoreEvent(
                        new CoreEvent(this, CoreEventConstants.GRANT_POLICY_TO_PRINCIPAL, null, null, e).addAttribute(
                            CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_TYPE, "user").addAttribute(
                            CoreAttributeConstants.EVENT_ATTR_POLICY_NAME, pol.getResourceName()).addAttribute(
                            CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID, p.getPrincipalName()));
                    throw e;
                }
            }
            for (Iterator i = policyForm.getSelectedRolesList().iterator(); i.hasNext();) {
                Principal p = udb.getRole((String) i.next());
                try {
                    PolicyDatabaseFactory.getInstance().grantPolicyToPrincipal(pol, p);
                    CoreServlet.getServlet().fireCoreEvent(
                        new CoreEvent(this, CoreEventConstants.GRANT_POLICY_TO_PRINCIPAL, null, null, CoreEvent.STATE_SUCCESSFUL)
                                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_TYPE, "group")
                                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_POLICY_NAME, pol.getResourceName())
                                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID, p.getPrincipalName()));
                } catch (Exception e) {
                    CoreServlet.getServlet().fireCoreEvent(
                        new CoreEvent(this, CoreEventConstants.GRANT_POLICY_TO_PRINCIPAL, null, null, CoreEvent.STATE_UNSUCCESSFUL)
                                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_TYPE, "group")
                                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_POLICY_NAME, pol.getResourceName())
                                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID, p.getPrincipalName()));
                    throw e;
                }
            }
        }
        // Update the attributes
        for(AttributeValueItem v : policyForm.getAttributeValueItems()) {
           if(v.getDefinition().getVisibility() != AttributeDefinition.USER_CONFIDENTIAL_ATTRIBUTE) {
               Property.setProperty(new PolicyAttributeKey(policyForm.getResourceId(), v.getDefinition().getName()), v.getDefinition().formatAttributeValue(v.getValue()), getSessionInfo(request));
           }
        }
        saveMessage(request, "editPolicy.message.saved", pol);
        return getRedirectWithMessages(mapping, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.policyframework.actions.AbstractResourceDispatchAction#createResource(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public Resource createResource(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        SessionInfo sessionInfo = getSessionInfo(request);
        int selectedRealmId = sessionInfo.getRealmId();
        return new DefaultPolicy(selectedRealmId);
    }

	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionForward forward = super.edit(mapping, form, request, response);
        if (forward.getName().equals("home")){
            // super returned a home, so we must go home.
            return forward;
        }
		((PolicyForm)form).initAttributes(request);
		return forward;
	}

	/*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.policyframework.actions.AbstractResourceDispatchAction#createAvailablePoliciesDataSource(com.ovpnals.policyframework.Resource,
     *      org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected MultiSelectDataSource createAvailablePoliciesDataSource(Resource resource, ActionMapping mapping, ActionForm form,
                                                                      HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return new DelegatedPoliciesDataSource((Policy) resource, PolicyConstants.POLICY_RESOURCE_TYPE,
                        PolicyConstants.SYSTEM_CLASS, getSessionInfo(request).getUser());
    }
    
    @Override
    protected void doUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.doUpdate(mapping, form, request, response);
        // we now need to rebuild any menus, as more or less could be visable.
        LogonControllerFactory.getInstance().applyMenuItemChanges(request);
    }

}