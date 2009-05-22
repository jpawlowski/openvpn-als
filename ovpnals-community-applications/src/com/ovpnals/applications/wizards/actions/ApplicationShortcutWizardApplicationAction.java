
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
			
package com.ovpnals.applications.wizards.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.applications.ApplicationsPlugin;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.wizard.AbstractWizardSequence;
import com.ovpnals.wizard.DefaultWizardSequence;
import com.ovpnals.wizard.WizardStep;
import com.ovpnals.wizard.actions.AbstractWizardAction;

/**
 * Extension of a {@link com.ovpnals.wizard.actions.AbstractWizardAction} 
 * that allows the application to use for this new application shortcut
 * 
 * @see com.ovpnals.extensions.ApplicationLauncher
 * @see com.ovpnals.extensions.ShortcutParameterItem
 */
public class ApplicationShortcutWizardApplicationAction extends AbstractWizardAction {

    // Private instance variables
    
    /**
     * Construtor
     */
    public ApplicationShortcutWizardApplicationAction() {
        super(ApplicationsPlugin.APPLICATION_SHORTCUT_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_PERSONAL_CREATE_EDIT_AND_DELETE });
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
        CoreUtil.addRequiredFieldMessage(this, request);
        return super.unspecified(mapping, form, request, response);
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
        ActionForward fwd = mapping.findForward("finish");
        DefaultWizardSequence seq = new DefaultWizardSequence(fwd, ApplicationsPlugin.MESSAGE_RESOURCES_KEY, "applicationShortcutWizard", CoreUtil.getReferer(request),
                        "applicationShortcutWizard", this.getSessionInfo(request));
        seq.addStep(new WizardStep("/applicationShortcutApplication.do", true));
        seq.addStep(new WizardStep("/applicationShortcutDetails.do"));
        seq.addStep(new WizardStep("/applicationShortcutAdditionalDetails.do"));
        boolean isUserConsole = getSessionInfo(request).isUserConsoleContext();
        seq.addStep(new WizardStep( isUserConsole ? "/applicationShortcutPersonalPolicy.do" : "/applicationShortcutPolicySelection.do"));
        seq.addStep(new WizardStep("/applicationShortcutSummary.do"));
        return seq;
    }
}
