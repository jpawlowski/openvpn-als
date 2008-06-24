
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
			
package com.adito.agent.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.adito.agent.DefaultAgentManager;
import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.actions.AbstractLaunchViaAppletAction;
import com.adito.extensions.store.ExtensionStore;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.NoPermissionException;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.PolicyUtil;
import com.adito.security.Constants;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;


/**
 * Launch the <i>Agent</i>.
 */
public class LaunchAgentAction extends AbstractLaunchViaAppletAction {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.extensions.actions.AbstractLaunchViaAppletAction#getExtensionDescriptor(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ExtensionDescriptor getExtensionDescriptor(ActionMapping mapping, ActionForm form, HttpServletRequest request,
															HttpServletResponse response) throws Exception {
		try {
			PolicyUtil.checkPermission(PolicyConstants.AGENT_RESOURCE_TYPE, PolicyConstants.PERM_USE, request);
		} catch (NoPermissionException e) {
			throw new NoPermissionException("Contact your administrator to use the agent.",
							e,
							LogonControllerFactory.getInstance().getUser(request),
							PolicyConstants.AGENT_RESOURCE_TYPE);
		}
		return ExtensionStore.getInstance().getExtensionDescriptor("adito-agent");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.core.actions.AuthenticatedAction#getNavigationContext(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		return SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
	}

	@Override
	protected void postSetup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
								LaunchSession launchSession) {
		String ticket = DefaultAgentManager.getInstance().registerPendingAgent(getSessionInfo(request));
		launchSession.setAttribute(Constants.LAUNCH_ATTR_AGENT_TICKET, ticket);

	}

}
