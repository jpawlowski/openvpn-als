
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
			
package com.ovpnals.policyframework.actions;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.core.actions.AuthenticatedDispatchAction;
import com.ovpnals.policyframework.Policy;
import com.ovpnals.policyframework.PolicyDatabaseFactory;
import com.ovpnals.policyframework.Resource;
import com.ovpnals.policyframework.ResourceType;
import com.ovpnals.policyframework.forms.ResourceInformationForm;
import com.ovpnals.security.Constants;
import com.ovpnals.security.SessionInfo;

/**
 * Action that display details about a {@link Resource}.
 */
public class ResourceInformationAction extends AuthenticatedDispatchAction {

    final static Log log = LogFactory.getLog(ResourceInformationAction.class);

    /**
     * Constructor
     */
    public ResourceInformationAction() {
        super();
    }

    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
    	int resourceId = Integer.parseInt(request.getParameter("resourceId"));
    	int resourceTypeId = Integer.parseInt(request.getParameter("resourceType"));
    	ResourceType resourceType = PolicyDatabaseFactory.getInstance().getResourceType(resourceTypeId);
    	request.setAttribute(Constants.REQ_ATTR_INFO_RESOURCE, resourceType.getResourceById(resourceId));
    	return information(mapping, form, request, response);
    }

    public ActionForward information(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        Resource r = (Resource)request.getAttribute(Constants.REQ_ATTR_INFO_RESOURCE);
        Collection<Policy> policies = null;
        if(r.getResourceType().isPolicyRequired()) {
            policies = PolicyDatabaseFactory.getInstance().getPoliciesAttachedToResource(r, getSessionInfo(request).getRealm());
        }
    	((ResourceInformationForm)form).initialise(r, policies);
        return mapping.findForward("display");
    }

    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return new ActionForward(((ResourceInformationForm)form).getReferer(), true);
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
        return SessionInfo.ALL_CONTEXTS;
    }
}