
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
			
package com.adito.tunnels.actions;

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

import com.adito.agent.DefaultAgentManager;
import com.adito.boot.Util;
import com.adito.core.BundleActionMessage;
import com.adito.core.CoreException;
import com.adito.core.RedirectWithMessages;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.LaunchSessionFactory;
import com.adito.policyframework.NoPermissionException;
import com.adito.policyframework.PolicyException;
import com.adito.policyframework.actions.AbstractFavoriteResourcesDispatchAction;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.security.User;
import com.adito.tunnels.TunnelPlugin;
import com.adito.tunnels.TunnelingService;
import com.adito.tunnels.forms.TunnelsForm;

/**
 * Implementation of
 * {@link com.adito.policyframework.actions.AbstractFavoriteResourcesDispatchAction}
 * that lists all of the configured <i>SSL Tunnels</i>.
 */
public class ShowTunnelsAction extends AbstractFavoriteResourcesDispatchAction {

    final static Log log = LogFactory.getLog(ShowTunnelsAction.class);

    /**
     * Constructor
     */
    public ShowTunnelsAction() {
        super(TunnelPlugin.SSL_TUNNEL_RESOURCE_TYPE, TunnelPlugin.SSL_TUNNEL_RESOURCE_TYPE);
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
        ActionForward fwd = super.unspecified(mapping, form, request, response);
        User user = LogonControllerFactory.getInstance().getUser(request);
        TunnelsForm tunnelsForm = (TunnelsForm) form;
        SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
        tunnelsForm.initialise(session, user, ".name");
        tunnelsForm.checkSelectedView(request, response);
        Util.noCache(response);
        return fwd;
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
    
    /**
     * Remve a tunnel.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ActionForward fwd = super.remove(mapping, form, request, response);
        ActionMessages msgs = new ActionMessages();
        msgs.add(Globals.MESSAGE_KEY, new ActionMessage("tunnels.message.tunnelsRemoved", "1"/*String.valueOf(sel.size())*/));
        saveMessages(request, msgs);
        return fwd;
    }

    /**
     * Stop the selected tunnel
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return forward
     * @throws Exception
     */
    public ActionForward stop(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        String launchId = request.getParameter(LaunchSession.LAUNCH_ID);
        if (launchId == null)
            throw new PolicyException(PolicyException.INTERNAL_ERROR, "No launchId parameter.");
        LaunchSession launchSession = LaunchSessionFactory.getInstance().getLaunchSession(launchId);
        if (launchSession != null) {
	        try {
	            ((TunnelingService) DefaultAgentManager.getInstance().getService(TunnelingService.class)).stopTunnels(launchSession);
	            ActionMessages msgs = new ActionMessages();
	            msgs.add(Globals.MESSAGE_KEY, new BundleActionMessage(TunnelPlugin.MESSAGE_RESOURCES_KEY, "tunnels.stopped", launchSession.getResource().getResourceName()));
	            saveMessages(request, msgs);
	            if (!Util.isNullOrTrimmedBlank(request.getParameter("returnTo"))) {
	                return new RedirectWithMessages(request.getParameter("returnTo"), request);
	            }
	            return new RedirectWithMessages(mapping.findForward("refresh"), request);
	        } catch (CoreException ce) {
	            ActionMessages msgs = new ActionMessages();
	            msgs.add(Globals.ERROR_KEY, ce.getBundleActionMessage());
	            saveErrors(request, msgs);
	            return mapping.findForward("display");
	
	        } catch (NoPermissionException e) {
	            ActionMessages msgs = new ActionMessages();
	            msgs.add(Globals.ERROR_KEY, new ActionMessage("tunnels.noPermission", launchSession.getResource().getResourceName()));
	            saveErrors(request, msgs);
	        }
        }
        else {
        	log.warn("No launch value ID, user probably double clicked tunnel.");
        }

        if (!Util.isNullOrTrimmedBlank(request.getParameter("returnTo"))) {
            return new RedirectWithMessages(request.getParameter("returnTo"), request);
        }
        return mapping.findForward("display");
    }

}