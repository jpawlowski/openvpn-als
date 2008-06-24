
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
			
package com.adito.webforwards.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.maverick.http.HttpAuthenticatorFactory;
import com.adito.policyframework.Resource;
import com.adito.policyframework.actions.AbstractResourceDispatchAction;
import com.adito.security.SessionInfo;
import com.adito.webforwards.WebForwardPlugin;
import com.adito.webforwards.WebForwardTypes;
import com.adito.webforwards.forms.WebForwardForm;

/**
 * Class for editing web forwards.
 */
public class EditWebForwardAction extends AbstractResourceDispatchAction {

    static Log log = LogFactory.getLog(EditWebForwardAction.class);

    /**
     * Construtor.
     */
    public EditWebForwardAction() {
        super(WebForwardPlugin.WEBFORWARD_RESOURCE_TYPE);
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
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.actions.AbstractResourceDispatchAction#createResource(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public Resource createResource(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return null;
    }
    
    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward toggleActiveDns(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        WebForwardForm webForwardForm = (WebForwardForm) form;
        if(webForwardForm.isActiveDNS()) {
            webForwardForm.setHostHeader("");
        }
        return mapping.findForward("display");
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward changeAuthenticationType(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("display");
    }

    /* (non-Javadoc)
     * @see com.adito.policyframework.actions.AbstractResourceDispatchAction#commit(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward commit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        WebForwardForm webForwardForm = (WebForwardForm) form;
        if (WebForwardForm.ATTR_NO_AUTHENTICATION.equals(webForwardForm.getAuthenticationType())) {
            webForwardForm.setPreferredAuthenticationScheme(HttpAuthenticatorFactory.NONE);
            webForwardForm.setAuthenticationUsername("");
            webForwardForm.setAuthenticationPassword("");
            webForwardForm.setFormType(WebForwardTypes.FORM_SUBMIT_NONE);
            webForwardForm.setFormParameters("");
        } else if (WebForwardForm.ATTR_FORM_BASED_AUTHENTICATION.equals(webForwardForm.getAuthenticationType())) {
            webForwardForm.setPreferredAuthenticationScheme(HttpAuthenticatorFactory.NONE);
            webForwardForm.setAuthenticationUsername("");
            webForwardForm.setAuthenticationPassword("");
        } else if (WebForwardForm.ATTR_HTTP_BASED_AUTHENTICATION.equals(webForwardForm.getAuthenticationType())) {
            webForwardForm.setFormType(WebForwardTypes.FORM_SUBMIT_NONE);
            webForwardForm.setFormParameters("");
        }
        ActionForward actionForward = super.commit(mapping, form, request, response);
        saveMessage(request, "editWebForward.message.saved");
        return actionForward;
    }
}