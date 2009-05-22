
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
			
package com.ovpnals.properties.attributes.actions;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

import com.ovpnals.boot.PropertyClassManager;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.core.actions.AuthenticatedDispatchAction;
import com.ovpnals.properties.attributes.AttributeDefinition;
import com.ovpnals.properties.attributes.forms.AttributeDefinitionInformationForm;
import com.ovpnals.security.Constants;
import com.ovpnals.security.SessionInfo;

/**
 * Action that display details about an {@link AttributeDefinition}.
 */
public class AttributeDefinitionInformationAction extends AuthenticatedDispatchAction {

    final static Log log = LogFactory.getLog(AttributeDefinitionInformationAction.class);

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
            String name = request.getParameter("name");
            String propertyClass = request.getParameter("propertyClass");
            AttributeDefinition def = (AttributeDefinition)PropertyClassManager.getInstance().getPropertyClass(propertyClass).getDefinition(name);
            request.setAttribute(Constants.REQ_ATTR_INFO_RESOURCE, def);
            return attributeInformation(mapping, form, request, response);
        } catch (Exception e) {            
            log.error("Failed to get attribute information. ", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return null;
        }
    }

    /**
     * Display information about an attribute definition
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward attributeInformation(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                    HttpServletResponse response) throws Exception {
    	AttributeDefinition def = (AttributeDefinition) request.getAttribute(Constants.REQ_ATTR_INFO_RESOURCE); 
        MessageResources mr = null;
        if(def.getMessageResourcesKey() != null) {
            mr = CoreUtil.getMessageResources(request.getSession(), def.getMessageResourcesKey()); 
        }
        Locale locale = (Locale)request.getSession().getAttribute(Globals.LOCALE_KEY);
        ((AttributeDefinitionInformationForm) form).initialise(mr, def, locale);
        return mapping.findForward("display");
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
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }
}