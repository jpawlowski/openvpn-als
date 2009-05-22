
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
			
package com.ovpnals.webforwards.webforwardwizard.actions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.boot.PropertyList;
import com.ovpnals.core.CoreEvent;
import com.ovpnals.core.CoreServlet;
import com.ovpnals.policyframework.ResourceChangeEvent;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.User;
import com.ovpnals.webforwards.AbstractAuthenticatingWebForward;
import com.ovpnals.webforwards.WebForward;
import com.ovpnals.webforwards.WebForwardDatabaseFactory;
import com.ovpnals.webforwards.WebForwardEventConstants;
import com.ovpnals.webforwards.WebForwardTypeItem;
import com.ovpnals.webforwards.WebForwardTypes;
import com.ovpnals.webforwards.webforwardwizard.forms.WebForwardAuthenticationDetailsForm;
import com.ovpnals.webforwards.webforwardwizard.forms.WebForwardResourceDetailsForm;
import com.ovpnals.webforwards.webforwardwizard.forms.WebForwardSpecificDetailsForm;
import com.ovpnals.webforwards.webforwardwizard.forms.WebForwardTypeSelectionForm;
import com.ovpnals.wizard.AbstractWizardSequence;
import com.ovpnals.wizard.WizardActionStatus;
import com.ovpnals.wizard.actions.AbstractFinishWizardAction;
import com.ovpnals.wizard.forms.AbstractWizardFinishForm;

/**
 * The final action in which the resource is created.
 */
public class WebForwardFinishAction extends AbstractFinishWizardAction {
    final static Log log = LogFactory.getLog(WebForwardFinishAction.class);

    /**
     * Constructor.
     */
    public WebForwardFinishAction() {
        super();
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
        return SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
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
        List<WizardActionStatus> actionStatus = new ArrayList<WizardActionStatus>();
        AbstractWizardSequence seq = getWizardSequence(request);
        String name = (String) seq.getAttribute(WebForwardResourceDetailsForm.ATTR_RESOURCE_NAME, null);
        String description = (String) seq.getAttribute(WebForwardResourceDetailsForm.ATTR_RESOURCE_DESCRIPTION, null);
        boolean favorite = ((Boolean) seq.getAttribute(WebForwardResourceDetailsForm.ATTR_FAVORITE, Boolean.FALSE)).booleanValue();

        int type = ((Integer) seq.getAttribute(WebForwardTypeSelectionForm.ATTR_TYPE, new Integer(0))).intValue();
        String category = (String) seq.getAttribute(WebForwardSpecificDetailsForm.ATTR_CATEGORY, null);
        String destinationURL = (String) seq.getAttribute(WebForwardSpecificDetailsForm.ATTR_DESTINATION_URL, null);

        PropertyList restrictToHosts = ((PropertyList) seq.getAttribute(WebForwardSpecificDetailsForm.ATTR_RESTRICT_TO_HOSTS, null));
        String encodeing = (String) seq.getAttribute(WebForwardSpecificDetailsForm.ATTR_ENCODEING, "");

        String authenticationUsername = (String) seq.getAttribute(WebForwardAuthenticationDetailsForm.ATTR_AUTHENTICATION_USERNAME,
            null);
        String authenticationPassword = (String) seq.getAttribute(WebForwardAuthenticationDetailsForm.ATTR_AUTHENTICATION_PASSWORD,
            null);
        String preferredAuthenticationScheme = (String) seq.getAttribute(
            WebForwardAuthenticationDetailsForm.ATTR_PREFERRED_AUTHENTICATION_SCHEME, null);

        String paths = (String) seq.getAttribute(WebForwardSpecificDetailsForm.ATTR_PATHS, "");
        String hostHeader = (String) seq.getAttribute(WebForwardSpecificDetailsForm.ATTR_HOST_HEADER, "");
        boolean activeDNS = ((Boolean) seq.getAttribute(WebForwardSpecificDetailsForm.ATTR_ACTIVE_DNS, Boolean.FALSE))
                        .booleanValue();

        String formParameters = (String) seq.getAttribute(WebForwardAuthenticationDetailsForm.ATTR_FORM_PARAMETERS, "");
        String formType = (String) seq.getAttribute(WebForwardAuthenticationDetailsForm.ATTR_FORM_TYPE, "");
        boolean autoStart = false; // TODO this needs to be hooked in
        User user = this.getSessionInfo(request).getUser();
        
        WebForward webForward = null;
        try {
            try {
                Calendar now = Calendar.getInstance();

                if (type == WebForward.TYPE_TUNNELED_SITE) {
                    com.ovpnals.webforwards.TunneledSiteWebForward sswf = new com.ovpnals.webforwards.TunneledSiteWebForward(user.getRealm().getRealmID(), 
                                    -1, destinationURL, name, description, category, autoStart, now, now);
                    webForward = WebForwardDatabaseFactory.getInstance().createWebForward(sswf);
                    CoreEvent evt = new ResourceChangeEvent(this, WebForwardEventConstants.CREATE_WEB_FORWARD, webForward, this
                                    .getSessionInfo(request), CoreEvent.STATE_SUCCESSFUL).addAttribute(
                                                    WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_CATEGORY, webForward.getCategory()).addAttribute(
                                                                    WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_TYPE,
                        ((WebForwardTypeItem) WebForwardTypes.WEB_FORWARD_TYPES.get(webForward.getType())).getName()).addAttribute(
                                        WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_URL, webForward.getDestinationURL());
                    CoreServlet.getServlet().fireCoreEvent(evt);
                } else if (type == WebForward.TYPE_REPLACEMENT_PROXY) {
                    com.ovpnals.webforwards.ReplacementProxyWebForward spwf = new com.ovpnals.webforwards.ReplacementProxyWebForward(user.getRealm().getRealmID(), 
                                    -1, destinationURL, name, description, category, authenticationUsername,
                                    authenticationPassword, preferredAuthenticationScheme, encodeing, restrictToHosts, formType,
                                    formParameters, autoStart, now, now);

                    AbstractAuthenticatingWebForward abstractAuthenticatingWebForward = (AbstractAuthenticatingWebForward) WebForwardDatabaseFactory
                                    .getInstance().createWebForward(spwf);
                    CoreEvent evt = new ResourceChangeEvent(this, WebForwardEventConstants.CREATE_WEB_FORWARD, abstractAuthenticatingWebForward, this
                                    .getSessionInfo(request), CoreEvent.STATE_SUCCESSFUL).addAttribute(
                                                    WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_CATEGORY, abstractAuthenticatingWebForward.getCategory()).addAttribute(
                                                                    WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_TYPE,
                        ((WebForwardTypeItem) WebForwardTypes.WEB_FORWARD_TYPES.get(abstractAuthenticatingWebForward.getType())).getName()).addAttribute(
                                        WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_URL, abstractAuthenticatingWebForward.getDestinationURL()).addAttribute(
                                                        WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_PREFERED_AUTH_SCHEME,
                        abstractAuthenticatingWebForward.getPreferredAuthenticationScheme()).addAttribute(
                                        WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_AUTH_USERNAME,
                        abstractAuthenticatingWebForward.getAuthenticationUsername()).addAttribute(
                                        WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_AUTH_FORM_TYPE,
                        abstractAuthenticatingWebForward.getFormType()).addAttribute(
                                        WebForwardEventConstants.EVENT_ATTR_REPLACEMENT_WEB_FORWARD_ENCODEING, spwf.getEncoding());

                    spwf.addFormParametersToEvent(evt, WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_AUTH_FORM_PARAMETERS);
                    spwf
                                    .addRestrictToHostsToEvent(evt,
                                                    WebForwardEventConstants.EVENT_ATTR_REPLACEMENT_WEB_FORWARD_RESTRICT_TO_HOSTS);
                    CoreServlet.getServlet().fireCoreEvent(evt);
                    webForward = abstractAuthenticatingWebForward;
                } else if (type == WebForward.TYPE_PATH_BASED_REVERSE_PROXY || type == WebForward.TYPE_HOST_BASED_REVERSE_PROXY) {
                    com.ovpnals.webforwards.ReverseProxyWebForward rpwf = new com.ovpnals.webforwards.ReverseProxyWebForward(user.getRealm().getRealmID(), 
                                    -1, type, destinationURL, name, description, category, authenticationUsername,
                                    authenticationPassword, preferredAuthenticationScheme, formType, formParameters, paths,
                                    hostHeader, activeDNS, autoStart, now, now, encodeing);
                    AbstractAuthenticatingWebForward abstractAuthenticatingWebForward = (AbstractAuthenticatingWebForward) WebForwardDatabaseFactory
                                    .getInstance().createWebForward(rpwf);
                    CoreEvent evt = new ResourceChangeEvent(this, WebForwardEventConstants.CREATE_WEB_FORWARD, abstractAuthenticatingWebForward, this
                                    .getSessionInfo(request), CoreEvent.STATE_SUCCESSFUL).addAttribute(
                                                    WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_CATEGORY, abstractAuthenticatingWebForward.getCategory()).addAttribute(
                                                                    WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_TYPE,
                        ((WebForwardTypeItem) WebForwardTypes.WEB_FORWARD_TYPES.get(abstractAuthenticatingWebForward.getType())).getName()).addAttribute(
                                        WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_URL, abstractAuthenticatingWebForward.getDestinationURL()).addAttribute(
                                                        WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_PREFERED_AUTH_SCHEME,
                        abstractAuthenticatingWebForward.getPreferredAuthenticationScheme()).addAttribute(
                                        WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_AUTH_USERNAME,
                        abstractAuthenticatingWebForward.getAuthenticationUsername()).addAttribute(
                                        WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_AUTH_FORM_TYPE,
                        abstractAuthenticatingWebForward.getFormType()).addAttribute(
                                        WebForwardEventConstants.EVENT_ATTR_REVERSE_WEB_FORWARD_ACTIVE_DNS, String.valueOf(rpwf.getActiveDNS()))
                                    .addAttribute(WebForwardEventConstants.EVENT_ATTR_REVERSE_WEB_FORWARD_HOST_HEADER,
                                        rpwf.getHostHeader());

                    rpwf.addPathsToEvent(evt, WebForwardEventConstants.EVENT_ATTR_REVERSE_WEB_FORWARD_PATHS);
                    rpwf.addCustomHeadersToEvent(evt, WebForwardEventConstants.EVENT_ATTR_REVERSE_WEB_FORWARD_CUSTOM_HEADERS);
                    rpwf.addFormParametersToEvent(evt, WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_AUTH_FORM_PARAMETERS);
                    CoreServlet.getServlet().fireCoreEvent(evt);
                    webForward = abstractAuthenticatingWebForward;
                }

            } catch (Exception e) {
                CoreServlet.getServlet().fireCoreEvent(
                    new ResourceChangeEvent(this, WebForwardEventConstants.CREATE_WEB_FORWARD, this.getSessionInfo(request), e));
                throw e;
            }
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_OK,
                            "webForwardWizard.webForwardFinish.status.profileCreated"));
        } catch (Exception e) {
            log.error("Failed to create profile.", e);
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                            "webForwardWizard.webForwardFinish.status.failedToCreateProfile", e.getMessage()));
        }
        // TODO do the attaching.
        if (webForward != null) {
            actionStatus.add(attachToPoliciesAndAddToFavorites("webForwardWizard.webForwardFinish", seq, webForward, favorite, request));
        }
        ((AbstractWizardFinishForm) form).setActionStatus(actionStatus);
        return super.unspecified(mapping, form, request, response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.wizard.actions.AbstractFinishWizardAction#exit(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward exit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return cancel(mapping, form, request, response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.wizard.actions.AbstractWizardAction#createWizardSequence(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected AbstractWizardSequence createWizardSequence(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                          HttpServletResponse response) throws Exception {
        throw new Exception("Cannot create sequence on this page.");
    }

}
