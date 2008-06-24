
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
			
package com.adito.extensions.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.core.actions.AuthenticatedDispatchAction;
import com.adito.extensions.ExtensionBundle;
import com.adito.extensions.forms.ExtensionBundleInformationForm;
import com.adito.extensions.store.ExtensionStore;
import com.adito.security.Constants;
import com.adito.security.SessionInfo;

/**
 * Action that display details about a {@link ExtensionBundle}.
 */
public class ExtensionBundleInformationAction extends AuthenticatedDispatchAction {

    final static Log log = LogFactory.getLog(ExtensionBundleInformationAction.class);

    /**
     * Constructor
     */
    public ExtensionBundleInformationAction() {
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
        try {
            String bundleId = request.getParameter("bundleId");
            ExtensionBundle bundle;
            try {
                bundle = ExtensionStore.getInstance().getExtensionBundle(bundleId);
            }
            catch(Exception e) {
                bundle = ExtensionStore.getInstance().getDownloadableExtensionStoreDescriptor(true).getApplicationBundle(bundleId);
            }
            request.setAttribute(Constants.REQ_ATTR_INFO_RESOURCE, bundle);
            return extensionBundleInformation(mapping, form, request, response);
        } catch (Exception e) {            
            log.error("Failed to get extension information. ", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return null;
        }
    }

    /**
     * Display information about an extension bundle
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward extensionBundleInformation(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                    HttpServletResponse response) throws Exception {
        ((ExtensionBundleInformationForm) form).initialise((ExtensionBundle) request.getAttribute(Constants.REQ_ATTR_INFO_RESOURCE));
        return mapping.findForward("display");
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
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }
}