
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
			
package net.openvpn.als.wizard.actions;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.boot.PropertyList;
import net.openvpn.als.boot.Util;
import net.openvpn.als.core.RedirectException;
import net.openvpn.als.core.actions.AuthenticatedDispatchAction;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.Policy;
import net.openvpn.als.policyframework.PolicyDatabaseFactory;
import net.openvpn.als.policyframework.PolicyUtil;
import net.openvpn.als.policyframework.Resource;
import net.openvpn.als.policyframework.ResourceType;
import net.openvpn.als.policyframework.ResourceUtil;
import net.openvpn.als.policyframework.forms.AbstractWizardPersonalResourcePolicyForm;
import net.openvpn.als.policyframework.forms.AbstractWizardPolicySelectionForm;
import net.openvpn.als.security.Constants;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.wizard.AbstractWizardSequence;
import net.openvpn.als.wizard.WizardActionStatus;
import net.openvpn.als.wizard.WizardStep;
import net.openvpn.als.wizard.forms.AbstractWizardForm;

/**
 * Abstract class that extends
 * {@link net.openvpn.als.core.actions.AuthenticatedDispatchAction} and should
 * be used by all actions in a wizard sequence.
 */
public abstract class AbstractWizardAction extends AuthenticatedDispatchAction {

    final static Log log = LogFactory.getLog(AbstractWizardAction.class);

        /**
     * Use this constructor for actions that do not require any resource
     * permissions
     */
    public AbstractWizardAction() {
    }

    /**
     * Use this constructor for actions that require a resource permission to
     * operator
     * 
     * @param resourceType resource type
     * @param permissions permission required
     */
    public AbstractWizardAction(ResourceType resourceType, Permission permissions[]) {
        super(resourceType, permissions);
    }

    /**
     * Start the sequence. Subclasses that overide this method should always
     * invoke this as well.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        request.getSession().removeAttribute(Constants.WIZARD_SEQUENCE);
        return unspecified(mapping, form, request, response);
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
        AbstractWizardSequence seq = (AbstractWizardSequence) request.getSession().getAttribute(Constants.WIZARD_SEQUENCE);
        if (seq == null) {
            try {
                seq = createWizardSequence(mapping, form, request, response);
            }
            catch(RedirectException rde) {
                log.error("Redirect to . " + rde.getForward().getPath(), rde);
                return rde.getForward(); 
            }
            request.getSession().setAttribute(Constants.WIZARD_SEQUENCE, seq);
        }
        ((AbstractWizardForm) form).init(seq, request);
        seq.setCurrentPageForm((AbstractWizardForm) form);
        Util.noCache(response);
        return mapping.findForward("display");
    }

    /**
     * Create a new {@link net.openvpn.als.wizard.AbstractWizardSequence} to
     * store the state for this sequence of wizard pages. <strong>Only the first
     * action in the sequence should implement this method, all other pages
     * should thrown an exception when invoked (this default implementation
     * does just that)
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return wizard sequence object for storing state between pages
     * @throws Exception if object cannot be create or this page is not the
     *         first in a sequence
     */
    protected AbstractWizardSequence createWizardSequence(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                          HttpServletResponse response) throws Exception {
        throw new RedirectException(cancel(mapping, form, request, response), "Cannot create sequence on this page.");
    }

    /**
     * Move the wizard to the next page an Exception will be thrown.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward forward
     * @throws Exception on any error
     */
    public ActionForward next(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        applyToSequence(mapping, (AbstractWizardForm) form, request, response);
        return mapping.findForward("next");
    }

    /**
     * Move the wizard to a different step
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward forward
     * @throws Exception on any error
     */
    public ActionForward gotoStep(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        applyToSequence(mapping, (AbstractWizardForm) form, request, response);
        AbstractWizardSequence seq = getWizardSequence(request);
        return new ActionForward(((WizardStep) seq.getSteps().get(((AbstractWizardForm) form).getGotoStep())).getPath(), true);
    }

    /*
     * Apply the current form to the wizard sequence object
     */
    protected void applyToSequence(ActionMapping mapping, AbstractWizardForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        AbstractWizardSequence seq = (AbstractWizardSequence) request.getSession().getAttribute(Constants.WIZARD_SEQUENCE);
        if (seq == null) {
            throw new Exception("No sequence.");
        }
        form.apply(seq);
    }

    /**
     * Move the wizard to the previous page. If no previous page is available an
     * Exception will be thrown.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward
     * @throws Exception
     */
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return mapping.findForward("previous");
    }

    /**
     * Move the wizard to the last page.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward
     * @throws Exception
     */
    public ActionForward finish(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        applyToSequence(mapping, (AbstractWizardForm) form, request, response);
        AbstractWizardSequence seq = (AbstractWizardSequence) request.getSession().getAttribute(Constants.WIZARD_SEQUENCE);
        if (seq == null) {
            throw new Exception("No sequence.");
        }
        return seq.getFinishActionForward();
    }

    /**
     * Cancel the wizard and return to the page the user was at before starting
     * the wizard. This is done using the referer attribute of the sequence
     * object.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        AbstractWizardSequence seq = getWizardSequence(request);
        if(seq != null) {
            request.getSession().removeAttribute(Constants.WIZARD_SEQUENCE);
            for (Iterator i = seq.getForms().iterator(); i.hasNext();) {
                String formName = ((AbstractWizardForm) i.next()).getPageName() + "Form";
                request.getSession().removeAttribute(formName);
            }
            ActionForward fwd = new ActionForward(seq.getReferer(), true);
            return fwd;
        }
        return mapping.findForward("home");
    }

    /**
     * Convenience method to get the current
     * {@link net.openvpn.als.wizard.AbstractWizardSequence} for the session
     * 
     * @param request request from which to get session
     * @return current wizard sequence object or null if none
     */
    public AbstractWizardSequence getWizardSequence(HttpServletRequest request) {
        return (AbstractWizardSequence) request.getSession().getAttribute(Constants.WIZARD_SEQUENCE);
    }

    /**
     * Re-run the wizard
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward rerun(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return mapping.findForward("rerun");
    }

    /**
     * <p>
     * Attach the resource to the policies requested and add to favorites.
     * 
     * @param messagePrefix prefix in message resources
     * @param seq The WizardSequence for this wizard.
     * @param resource resource
     * @param addToFavorites addToFavorites
     * @param request
     * @return wizard status
     */
    
    protected WizardActionStatus attachToPoliciesAndAddToFavorites(String messagePrefix, AbstractWizardSequence seq, Resource resource, boolean addToFavorites, HttpServletRequest request) {
        PropertyList selectedPolicies = (PropertyList) seq.getAttribute(AbstractWizardPolicySelectionForm.ATTR_SELECTED_POLICIES,
            null);
        try {
            if (SessionInfo.USER_CONSOLE_CONTEXT == this.getSessionInfo(request).getNavigationContext()) {
                Policy policy = PolicyDatabaseFactory.getInstance().getPolicyByName(
                    PolicyUtil.getPersonalPolicyName(getSessionInfo(request).getUser().getPrincipalName()),
                    getSessionInfo(request).getUser().getRealm().getRealmID());
                if (null == policy) {
                    policy = PolicyDatabaseFactory.getInstance().createPolicy(
                        PolicyUtil.getPersonalPolicyName(getSessionInfo(request).getUser().getPrincipalName()),
                        PolicyUtil.getPersonalPolicyName(getSessionInfo(request).getUser().getPrincipalName()), Policy.TYPE_PERSONAL,
                        getSessionInfo(request).getUser().getRealm().getRealmID());
                    PolicyDatabaseFactory.getInstance().grantPolicyToPrincipal(policy, getSessionInfo(request).getUser());
                }
                PolicyDatabaseFactory.getInstance().attachResourceToPolicy(resource, policy, 0,
                    getSessionInfo(request).getUser().getRealm());
            } else {
                PolicyDatabaseFactory.getInstance().attachResourceToPolicyList(resource, selectedPolicies, getSessionInfo(request));
                ResourceUtil.setResourceGlobalFavorite(resource, addToFavorites);
            }
            return new WizardActionStatus(WizardActionStatus.COMPLETED_OK, messagePrefix + ".status.attachedToPolicies");
        } catch (Exception e) {
            log.error("Failed to create profile.", e);
            return new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS, messagePrefix
                          + ".status.failedToAttachToPolicies", e.getMessage());
        }
    }    
    
}
