
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
			
package com.ovpnals.setup.actions;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.ovpnals.agent.DefaultAgentManager;
import com.ovpnals.core.RedirectWithMessages;
import com.ovpnals.core.actions.AuthenticatedAction;
import com.ovpnals.policyframework.Permission;
import com.ovpnals.policyframework.PolicyConstants;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SessionInfo;

public class LogoffSessionAction extends AuthenticatedAction {

	static Log log = LogFactory.getLog(LogoffSessionAction.class);

	public LogoffSessionAction() {
		super(PolicyConstants.STATUS_TYPE_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_VIEW });
	}

	public ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
					throws Exception {
		String ticket = request.getParameter("ticket");
		if (ticket == null || ticket.equals("")) {
			throw new Exception("No ticket parameter provided.");
		} else {
			
			try {
				if (ticket.equals(this.getSessionInfo(request).getLogonTicket())) {
					log.error("You cannot log yourself off.");
					ActionMessages mesgs = new ActionMessages();
					mesgs.add(Globals.ERROR_KEY, new ActionMessage("status.sessions.cannotLogoffYourself"));
					saveErrors(request, mesgs);
					return new RedirectWithMessages(mapping.findForward("success"), request);
				}
				Map map = LogonControllerFactory.getInstance().getActiveSessions();
				synchronized (map) {
					SessionInfo info = (SessionInfo) map.get(ticket);
					if (info == null) {
						throw new Exception("No session with ticket " + ticket);
					} else {
						if(info.getType() == SessionInfo.UI || info.getType() == SessionInfo.DAV_CLIENT) {
							info.invalidate();
						}
						else if(info.getType() == SessionInfo.AGENT) {
				    		DefaultAgentManager.getInstance().unregisterAgent(info);
						}
					}
				}
			} catch(IllegalStateException ex) {

				// Something strange happened, force a logoff of this ticket
				try {
					LogonControllerFactory.getInstance().logoff(ticket);
				} catch(Throwable t) {}
			}
		}
		return mapping.findForward("success");
	}

	public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
	}
}