
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
			
package com.adito.policyframework.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import com.adito.agent.DefaultAgentManager;
import com.adito.boot.Util;
import com.adito.core.BundleActionMessage;
import com.adito.core.CoreException;
import com.adito.core.CoreUtil;
import com.adito.core.RedirectWithMessages;
import com.adito.core.actions.AuthenticatedAction;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.LaunchSessionFactory;
import com.adito.policyframework.LaunchSessionManager;
import com.adito.policyframework.Policy;
import com.adito.policyframework.Resource;
import com.adito.policyframework.ResourceType;
import com.adito.security.SessionInfo;

/**
 * Abstract implementation of {@link com.adito.core.actions.AuthenticatedAction}
 * that 'Launches' some kind of <i>Resource</i>.
 * <p>
 */
public abstract class AbstractLaunchAction extends AuthenticatedAction {
	
	private ResourceType resourceType;
	private int navigationContext;

    /**
     * Constructor.
     * 
     * @param resourceType 
     * @param navigationContext 
     * 
     */
    public AbstractLaunchAction(ResourceType resourceType, int navigationContext) {
    	super();
    	this.resourceType = resourceType;
    	this.navigationContext = navigationContext;
    }

    /* (non-Javadoc)
     * @see com.adito.core.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        String id = request.getParameter("resourceId");
        String returnTo = request.getParameter("returnTo");
        if (Util.isNullOrTrimmedBlank(id)) {
            throw new Exception("No resource ID supplied.");
        }
        if (Util.isNullOrTrimmedBlank(returnTo)) {
            throw new Exception("No returnTo path supplied.");
        }
        Resource resource = resourceType.getResourceById(Integer.parseInt(id));
        if(resource == null) {
            throw new Exception("No resource with ID " + id);
        }
        SessionInfo session = getSessionInfo(request);
        Policy policy = null;
        try {
        	policy = LaunchSessionManager.getLaunchRequestPolicy(request, session, resource);
        } catch (Exception expt) {
        	ActionMessages errs = new ActionMessages();
        	errs.add(Globals.ERROR_KEY,  new BundleActionMessage("policyframework", "resourceNotAttachedToPolicy"));
        	saveErrors(request, errs);
        	return new RedirectWithMessages(returnTo, request);
        }
    	Util.noCache(response);
        if(resource.sessionPasswordRequired(session)) {
            // Prompt for the session password then come back here
    		return new ActionForward("/promptForSessionPassword.do?forwardTo=" +  Util.urlEncode(CoreUtil.getRealRequestURI(request) + "?resourceId=" + id + "&policy=" + policy.getResourceId() + "&returnTo=" + Util.urlEncode(returnTo)), true);
        }
        else {
            // Launch the agent and return here
        	if(isAgentRequired(resource) && !DefaultAgentManager.getInstance().hasActiveAgent(request)) {
        		return new ActionForward("/launchAgent.do?returnTo=" +  Util.urlEncode(CoreUtil.getRealRequestURI(request) + "?resourceId=" + id + "&policy=" + policy.getResourceId() + "&returnTo=" + Util.urlEncode(returnTo)), true);
        	}
        	else {
                LaunchSession launchSession = LaunchSessionFactory.getInstance().createLaunchSession(session, resource, policy);
                launchSession.checkAccessRights(null, session);
                try {
                	return launch(mapping, launchSession, request, returnTo);
                }
                catch(CoreException ce) {
                	ActionMessages errs = new ActionMessages();
                	errs.add(Globals.ERROR_KEY, ce.getBundleActionMessage());
                	saveErrors(request, errs);
                	return new RedirectWithMessages(returnTo, request);
                }
        	}
        }
    }

    protected abstract ActionForward launch(ActionMapping mapping, LaunchSession launchSession, HttpServletRequest request, String returnTo)
                    throws Exception;
    
    protected abstract boolean isAgentRequired(Resource resource);

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */

    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
    	return navigationContext;
    }
}
