
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
			
package net.openvpn.als.networkplaces.wizards.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.networkplaces.NetworkPlacePlugin;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.wizard.AbstractWizardSequence;
import net.openvpn.als.wizard.DefaultWizardSequence;
import net.openvpn.als.wizard.WizardStep;
import net.openvpn.als.wizard.actions.AbstractWizardAction;

/**
 * Action for network places resource details.
 */
public class DefaultNetworkPlaceDetailsAction extends AbstractWizardAction {
    
    /**
     * Constructor
     */
    public DefaultNetworkPlaceDetailsAction() {
        super(NetworkPlacePlugin.NETWORK_PLACE_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_PERSONAL_CREATE_EDIT_AND_DELETE });
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
        return SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }     
    
    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.wizard.actions.AbstractWizardAction#unspecified(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        CoreUtil.addRequiredFieldMessage(this, request);
        return super.unspecified(mapping, form, request, response);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.wizard.actions.AbstractWizardAction#previous(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        throw new Exception("No previous steps.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.wizard.actions.AbstractWizardAction#createWizardSequence(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected AbstractWizardSequence createWizardSequence(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        ActionForward fwd = mapping.findForward("finish");
        DefaultWizardSequence seq = new DefaultWizardSequence(fwd, NetworkPlacePlugin.MESSAGE_RESOURCES_KEY, "networkPlaceWizard",
            CoreUtil.getReferer(request), "networkPlaceWizard", this.getSessionInfo(request));
        seq.addStep(new WizardStep("/defaultNetworkPlaceDetails.do", true));
        seq.addStep(new WizardStep("/networkPlaceDetails.do"));
        boolean isUserConsole = getSessionInfo(request).isUserConsoleContext();
        seq.addStep(new WizardStep( isUserConsole ? "/networkPlacePersonalPolicy.do" : "/networkPlacePolicySelection.do"));
        seq.addStep(new WizardStep("/networkPlaceSummary.do"));
        return seq;
    }
}
