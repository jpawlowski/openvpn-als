
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
			
package net.openvpn.als.extensions.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.core.actions.AuthenticatedAction;
import net.openvpn.als.extensions.ExtensionDescriptor;
import net.openvpn.als.policyframework.LaunchSession;
import net.openvpn.als.policyframework.LaunchSessionFactory;
import net.openvpn.als.security.Constants;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;

/**
 * Abstract action to launch an application via the <i>Agent Launcher Applet</i>. This 
 * should be extended and the {@link #getExtensionDescriptor(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)}
 * method implemented to return the descriptor of the extension to 
 * launch.
 */

public abstract class AbstractLaunchViaAppletAction extends AuthenticatedAction {
	
	final static Log log = LogFactory.getLog(AbstractLaunchViaAppletAction.class);

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.core.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    protected ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
    	ExtensionDescriptor desc = getExtensionDescriptor(mapping, form, request, response);
    	if(desc == null) {
    		throw new Exception("No application extension.");
    	}
        SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
        String ref = request.getParameter("returnTo");
        if (ref == null) {
           ref = CoreUtil.getReferer(request);
           ref = ref == null ? "/showHome.do" : ref;
        } 
        LaunchSession launchSession = LaunchSessionFactory.getInstance().createLaunchSession(session, null, null);
        launchSession.setAttribute(Constants.LAUNCH_ATTR_AGENT_RETURN_TO, ref);
        launchSession.setAttribute(Constants.LAUNCH_ATTR_AGENT_EXTENSION, desc);
        request.setAttribute(Constants.REQ_ATTR_LAUNCH_SESSION, launchSession);
        postSetup(mapping, form, request, response, launchSession);
        return mapping.findForward("launchViaAgentApplet");
    }

    /**
     * Invoked after launch session has been created.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param launchSession
     */
    protected abstract void postSetup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, LaunchSession launchSession);

	/**
     * Get the descriptor for the application to launch. If the
     * application cannot be launched for any reason an
     * {@link Exception} should be thrown.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return extension
     * @throws Exception on any error
     */
    protected abstract ExtensionDescriptor getExtensionDescriptor(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception;

}