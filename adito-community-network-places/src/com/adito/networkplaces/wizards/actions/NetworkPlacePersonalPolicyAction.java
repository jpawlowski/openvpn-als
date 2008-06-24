
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
			
package com.adito.networkplaces.wizards.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.core.CoreUtil;
import com.adito.networkplaces.wizards.forms.NetworkPlacePersonalPolicyForm;
import com.adito.policyframework.PolicyUtil;
import com.adito.wizard.actions.AbstractWizardPolicySelectionAction;

/**
 * This provides a personal policy.
 * Implementation of {@link com.adito.wizard.actions.AbstractWizardAction}.
 */
public class NetworkPlacePersonalPolicyAction extends AbstractWizardPolicySelectionAction {

    /**
     * Constructor
     */
    public NetworkPlacePersonalPolicyAction() {
        super();
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
        NetworkPlacePersonalPolicyForm networkPlacePersonalPolicyForm = (NetworkPlacePersonalPolicyForm)form;
        networkPlacePersonalPolicyForm.setPersonalPolicyName(PolicyUtil.getPersonalPolicyName(getSessionInfo(request).getUser().getPrincipalName()));
        
        return super.unspecified(mapping, form, request, response);
    }
}
