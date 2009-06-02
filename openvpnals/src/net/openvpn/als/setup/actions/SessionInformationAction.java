
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
			
package net.openvpn.als.setup.actions;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.core.UserDatabaseManager;
import net.openvpn.als.core.actions.AuthenticatedDispatchAction;
import net.openvpn.als.policyframework.PolicyConstants;
import net.openvpn.als.policyframework.PolicyUtil;
import net.openvpn.als.policyframework.Principal;
import net.openvpn.als.policyframework.forms.PrincipalInformationForm;
import net.openvpn.als.security.Constants;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.Role;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.setup.forms.SessionInformationForm;

/**
 * Action that display details about a {@link SessionInfo}.
 */
public class SessionInformationAction extends AuthenticatedDispatchAction {

	final static Log log = LogFactory.getLog(SessionInformationAction.class);

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
			PolicyUtil.checkPermission(PolicyConstants.STATUS_TYPE_RESOURCE_TYPE, PolicyConstants.PERM_VIEW, request);			
			String ticket = request.getParameter("ticket");
			if(ticket == null) {
				throw new Exception("No ticket parameter supplied.");
			}
			SessionInfo info = LogonControllerFactory.getInstance().getSessionInfo(ticket);
			request.setAttribute(Constants.REQ_ATTR_INFO_RESOURCE, info);
			return sessionInformation(mapping, form, request, response);
		} catch (Exception e) {
			log.error("Failed to get session information. ", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
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
	public ActionForward sessionInformation(ActionMapping mapping, ActionForm form, HttpServletRequest request,
												HttpServletResponse response) throws Exception {
		SessionInformationForm informationForm = (SessionInformationForm) form;
		SessionInfo session = (SessionInfo) request.getAttribute(Constants.REQ_ATTR_INFO_RESOURCE);
		informationForm.initialise(session);
		return mapping.findForward("display");
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
		return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
	}
}