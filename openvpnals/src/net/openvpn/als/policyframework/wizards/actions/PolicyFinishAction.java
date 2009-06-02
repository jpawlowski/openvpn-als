
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
			
package net.openvpn.als.policyframework.wizards.actions;

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

import net.openvpn.als.boot.PropertyList;
import net.openvpn.als.core.CoreAttributeConstants;
import net.openvpn.als.core.CoreEvent;
import net.openvpn.als.core.CoreEventConstants;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.core.UserDatabaseManager;
import net.openvpn.als.policyframework.Policy;
import net.openvpn.als.policyframework.PolicyDatabaseFactory;
import net.openvpn.als.policyframework.ResourceChangeEvent;
import net.openvpn.als.policyframework.wizards.forms.PolicyDetailsForm;
import net.openvpn.als.policyframework.wizards.forms.PolicyPrincipalSelectionForm;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.Role;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.security.User;
import net.openvpn.als.security.UserDatabase;
import net.openvpn.als.wizard.AbstractWizardSequence;
import net.openvpn.als.wizard.WizardActionStatus;
import net.openvpn.als.wizard.actions.AbstractWizardAction;
import net.openvpn.als.wizard.forms.AbstractWizardFinishForm;

/**
 * The <i>PolicyFinishAction> {@link net.openvpn.als.wizard.actions.AbstractWizardAction} implementation
 * is responsible finishing the creation of a policy.
 */
public class PolicyFinishAction extends AbstractWizardAction {
    final static Log log = LogFactory.getLog(PolicyFinishAction.class);

    /**
     * 
     */
    public PolicyFinishAction() {
        super();
    }

    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        // Do the install
        List<WizardActionStatus> actionStatus = new ArrayList<WizardActionStatus>();
        AbstractWizardSequence seq = getWizardSequence(request);
        String policyName = (String) seq.getAttribute(PolicyDetailsForm.ATTR_RESOURCE_NAME, null);
        String policyDescription = (String) seq.getAttribute(PolicyDetailsForm.ATTR_RESOURCE_DESCRIPTION, null);
        try {
            Policy pol =PolicyDatabaseFactory.getInstance().createPolicy(policyName, policyDescription, Policy.TYPE_NORMAL, getSessionInfo(request).getUser().getRealm().getRealmID());
     
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_OK,
                            "policyWizard.policyFinish.status.policyCreated"));

            CoreEvent coreEvent = new ResourceChangeEvent(this, CoreEventConstants.CREATE_POLICY, pol, getSessionInfo(request),
                    CoreEvent.STATE_SUCCESSFUL);
            CoreServlet.getServlet().fireCoreEvent(coreEvent);
            
            try {
            	UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(LogonControllerFactory.getInstance().getSessionInfo(request).getUser().getRealm());
                for (Iterator i = ((PropertyList) seq.getAttribute(PolicyPrincipalSelectionForm.ATTR_SELECTED_ACCOUNTS, null))
                                .iterator(); i.hasNext();) {
                    User user = udb.getAccount((String) i.next());
                    PolicyDatabaseFactory.getInstance().grantPolicyToPrincipal(pol,
                                    user);
                    CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this, CoreEventConstants.GRANT_POLICY_TO_PRINCIPAL, pol, getSessionInfo(request),
                            CoreEvent.STATE_SUCCESSFUL).addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_TYPE, "user").addAttribute(
                            CoreAttributeConstants.EVENT_ATTR_POLICY_NAME,
                            pol.getResourceName()).addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID, user.getPrincipalName()));
                }
                for (Iterator i = ((PropertyList) seq.getAttribute(PolicyPrincipalSelectionForm.ATTR_SELECTED_ROLES, null))
                                .iterator(); i.hasNext();) {
                    Role role = udb.getRole((String) i.next());
                    PolicyDatabaseFactory.getInstance().grantPolicyToPrincipal(pol,
                                    role);

                    CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this, CoreEventConstants.GRANT_POLICY_TO_PRINCIPAL, pol, getSessionInfo(request),
                        CoreEvent.STATE_SUCCESSFUL).addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_TYPE, "group").addAttribute(
                        CoreAttributeConstants.EVENT_ATTR_POLICY_NAME,
                        pol.getResourceName()).addAttribute(CoreAttributeConstants.EVENT_ATTR_PRINCIPAL_ID, role.getPrincipalName()));
                }
                actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_OK,
                                "policyWizard.policyFinish.status.grantedToPrincipals"));
            } catch (Exception e) {
                log.error("Failed to grant principals to policy.", e);
                actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                                "policyWizard.policyFinish.status.failedToGrantToPrincipals", e.getMessage()));
                CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this, CoreEventConstants.GRANT_POLICY_TO_PRINCIPAL, null, getSessionInfo(request), e));
            }
        } catch (Exception e) {
            log.error("Failed to create policy.", e);
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                            "policyWizard.policyFinish.status.failedToCreatePolicy", e.getMessage()));
            CoreServlet.getServlet().fireCoreEvent(new ResourceChangeEvent(this, CoreEventConstants.CREATE_POLICY, getSessionInfo(request), e));
        }
        ((AbstractWizardFinishForm) form).setActionStatus(actionStatus);
        return super.unspecified(mapping, form, request, response);
    }

    /**
     * @param mapping Action forwards
     * @param form Action form
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return Action Forward
     * @throws Exception
     */
    public ActionForward exit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return cancel(mapping, form, request, response);
    }
}
