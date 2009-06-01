
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
			
package net.openvpn.als.webforwards.webforwardwizard.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.policyframework.Permission;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.webforwards.WebForward;
import net.openvpn.als.webforwards.WebForwardPlugin;
import net.openvpn.als.webforwards.webforwardwizard.forms.WebForwardTypeSelectionForm;
import net.openvpn.als.wizard.actions.AbstractWizardAction;
import net.openvpn.als.wizard.forms.AbstractWizardForm;

/**
 * The web forward attributes.
 */
public class WebForwardSpecificDetailsAction extends AbstractWizardAction {

    /**
     */
    public final static String ATTR_USER = "user";

    /**
     * Construtor
     */
    public WebForwardSpecificDetailsAction() {
        super(WebForwardPlugin.WEBFORWARD_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN, PolicyConstants.PERM_PERSONAL_CREATE_EDIT_AND_DELETE });
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

    public ActionForward toggleActiveDns(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        applyToSequence(mapping, (AbstractWizardForm) form, request, response);
        return unspecified(mapping, form, request, response);
    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.wizard.actions.AbstractWizardAction#next(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward next(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        int type = ((Integer) getWizardSequence(request).getAttribute(WebForwardTypeSelectionForm.ATTR_TYPE, new Integer(0))).intValue();
        if (type == WebForward.TYPE_TUNNELED_SITE) {
            applyToSequence(mapping, (AbstractWizardForm) form, request, response);
            if (SessionInfo.USER_CONSOLE_CONTEXT == this.getSessionInfo(request).getNavigationContext())
                return mapping.findForward("nextPersonal");
            else
                return mapping.findForward("nextSkipAuthentication");
        }
        return super.next(mapping, form, request, response);
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.wizard.actions.AbstractWizardAction#unspecified(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CoreUtil.addRequiredFieldMessage(this, request);
        return super.unspecified(mapping, form, request, response);
    }
}