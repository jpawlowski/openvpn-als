
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
			
package net.openvpn.als.agent.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import net.openvpn.als.agent.DefaultAgentManager;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.core.actions.AuthenticatedAction;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;

/**
 * Shuts down the VPN client.
 */
public class ShutdownAgentAction extends AuthenticatedAction {

    static Log log = LogFactory.getLog(ShutdownAgentAction.class);

    /**
     * Constructor
     */
    public ShutdownAgentAction() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.core.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {


    	SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
    	
    	ActionMessages errs = new ActionMessages();
    	
    	if(session==null) {
    		errs.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("agent.notStarted"));
    		saveMessages(request, errs);
    	} else {
    		DefaultAgentManager.getInstance().unregisterAgent(session);
    	}
    	
    	String path = request.getParameter("path");
    	ActionForward fwd = new ActionForward(path==null ? CoreUtil.getReferer(request) : path, true);
        return fwd;
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
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.core.actions.AuthenticatedAction#requiresProfile()
     */
    protected boolean requiresProfile() {
        return false;
    }

    /*
     * Ignore session lock as we want to be able logoff from the 
     * session lock page.
     * 
     * @return ignore session locks
     */
    protected boolean isIgnoreSessionLock() {
        return true;
    }

}