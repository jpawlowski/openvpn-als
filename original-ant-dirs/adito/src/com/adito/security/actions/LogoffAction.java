
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
			
package com.adito.security.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.agent.DefaultAgentManager;
import com.adito.core.actions.DefaultAction;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.vfs.VFSRepository;

/**
 * Logs a user out of the Adito 
 */
public class LogoffAction extends DefaultAction {

	static Log log = LogFactory.getLog(LogoffAction.class);

	/* (non-Javadoc)
	 * @see com.adito.core.actions.DefaultAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
					throws Exception {

		try {
			SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
			if(session != null) {
			    VFSRepository.removeRepository(session);
                
				if(DefaultAgentManager.getInstance().hasActiveAgent(session))
					DefaultAgentManager.getInstance().unregisterAgent(session);

				String username = LogonControllerFactory.getInstance().getUser(request).getPrincipalName();
				username += " [" + request.getRemoteHost() + "]";
				if (log.isInfoEnabled())
					log.info("Logging off " + username);
	
				try {
					LogonControllerFactory.getInstance().logoffSession(request, response);
	
					if (log.isInfoEnabled())
						log.info(username + " has logged off");
				} catch (Exception ex) {
					if (log.isInfoEnabled())
						log.info("Logoff failed for " + username, ex);
				}
				
			}
		} catch (SecurityException ite) {
			// Dont care - session may have timed-out
		}

		getMessages(request).clear();
		getErrors(request).clear();
		return (mapping.findForward("success"));
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
}