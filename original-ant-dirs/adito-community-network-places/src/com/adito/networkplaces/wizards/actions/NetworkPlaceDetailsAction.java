
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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.adito.core.CoreUtil;
import com.adito.networkplaces.NetworkPlacePlugin;
import com.adito.networkplaces.wizards.forms.NetworkPlaceDetailsForm;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.security.Constants;
import com.adito.security.SessionInfo;
import com.adito.vfs.VFSProvider;
import com.adito.vfs.VFSProviderManager;
import com.adito.wizard.actions.AbstractWizardAction;

/**
 * <p>
 * This class is an implementation of
 * {@link com.adito.wizard.actions.AbstractWizardAction}, it provides the
 * form for the default resource details to be entered (i.e. name, description).
 */
public class NetworkPlaceDetailsAction extends AbstractWizardAction {

    /**
     * Constructer to initialise the
     * {@link com.adito.wizard.actions.AbstractWizardAction} for the
     * network place resource.
     */
    public NetworkPlaceDetailsAction() {
        super(NetworkPlacePlugin.NETWORK_PLACE_RESOURCE_TYPE, new Permission[] {
            PolicyConstants.PERM_CREATE_EDIT_AND_ASSIGN,
            PolicyConstants.PERM_PERSONAL_CREATE_EDIT_AND_DELETE
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

    /* (non-Javadoc)
     * @see com.adito.wizard.actions.AbstractWizardAction#next(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward next(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward fwd = super.next(mapping, form, request, response);
        if(((NetworkPlaceDetailsForm)form).isAutomaticallyDetected()) {
            ActionMessages msgs = new ActionMessages();
            msgs.add(Constants.REQ_ATTR_WARNINGS, new ActionMessage("createNetworkPlace.warning.automaticallyConverted", 
                    CoreUtil.getMessageResources(request.getSession(), ((NetworkPlaceDetailsForm)form).getProvider().getBundle()).getMessage((Locale)request.getSession().getAttribute(Globals.LOCALE_KEY), "vfs.provider." + ((NetworkPlaceDetailsForm)form).getScheme() + ".name")));
            CoreUtil.saveWarnings(request, msgs);
            return mapping.getInputForward();
        }
        
        if (SessionInfo.USER_CONSOLE_CONTEXT == this.getSessionInfo(request).getNavigationContext())
            return mapping.findForward("nextPersonal");
        else
            return fwd;
    }

    /**
     * Change the selected scheme.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return 
     * @throws Exception
     */
    public ActionForward changeScheme(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        NetworkPlaceDetailsForm networkPlaceDetailsForm = (NetworkPlaceDetailsForm) form;
        VFSProvider provider = VFSProviderManager.getInstance().getProvider(networkPlaceDetailsForm.getScheme());
        if (provider == null) {
            provider = NetworkPlaceDetailsForm.DEFAULT_PROVIDER;
        }
        networkPlaceDetailsForm.changeProvider(provider);
        networkPlaceDetailsForm.setAllowRecursive(true);
        return mapping.findForward("display");
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
}
