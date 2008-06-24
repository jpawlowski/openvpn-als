
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
			
package com.adito.core.actions;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.adito.boot.Branding;
import com.adito.boot.ContextHolder;
import com.adito.boot.Util;
import com.adito.core.CoreUtil;
import com.adito.core.RedirectWithMessages;
import com.adito.core.ServletRequestAdapter;
import com.adito.core.ServletResponseAdapter;
import com.adito.core.forms.CoreForm;
import com.adito.policyframework.NoPermissionException;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.ResourceType;
import com.adito.properties.PropertyProfile;
import com.adito.security.Constants;
import com.adito.security.LogonController;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.security.SystemDatabaseFactory;

/**
 * <p>
 * onward Abstract class for authenticated dispatch actions.
 */
public abstract class AuthenticatedDispatchAction extends DefaultDispatchAction implements CoreAction {
    static Log log = LogFactory.getLog(AuthenticatedAction.class);
    protected ResourceType resourceType, requiresResourcesOfType;
    protected Permission[] permissions;

    /**
     * Use this constructor for actions that do not require any resource
     * permissions
     */
    public AuthenticatedDispatchAction() {
    }
    
    
    /**
     * Use this constructor for actions that require a resource permission to
     * operator
     * 
     * @param resourceType resource type
     * @param permissions permission required
     */
    public AuthenticatedDispatchAction(ResourceType resourceType, Permission permissions[]) {
        this(resourceType, permissions, null);
    }

    /**
     * Use this constructor for actions that require a resource permission to
     * operator
     * 
     * @param resourceType resource type
     * @param permissions permission required
     * @param requiresResources requires access to resources of type
     */
    public AuthenticatedDispatchAction(ResourceType resourceType, Permission permissions[], ResourceType requiresResources) {
        this.resourceType = resourceType;
        this.requiresResourcesOfType = requiresResources;
        this.permissions = permissions;
    }

    /**
     * This abstract class will populate all the common variables required by an
     * action within the webstudio framework, such as current user, permission
     * database, user database etc
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * 
     * @exception Exception if business logic throws an exception
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        // Setup mode
        boolean setupMode = ContextHolder.getContext().isSetupMode();
        if (setupMode) {
            if ((getNavigationContext(mapping, form, request, response) & SessionInfo.SETUP_CONSOLE_CONTEXT) == 0) {
                return mapping.findForward("setup");
            } else {
                /*
                 * Make the mapping and form available, this helps with reusing
                 * some JSP pages
                 */
                request.setAttribute(Constants.REQ_ATTR_ACTION_MAPPING, mapping);
                request.setAttribute(Constants.REQ_ATTR_FORM, form);

                //
                CoreUtil.checkNavigationContext(this, mapping, form, request, response);
                return super.execute(mapping, form, request, response);
            }
        }
        try {
            try {
                if (!SystemDatabaseFactory.getInstance().verifyIPAddress(request.getRemoteAddr())) {
                    String link = null;
                    log.error(request.getRemoteHost() + " is not authorized");
                    if (log.isInfoEnabled())
                        log.info("Logging off, IP address verification failed.");

                    if(LogonControllerFactory.getInstance().hasClientLoggedOn(request, response) == LogonController.LOGGED_ON) {
                    	LogonControllerFactory.getInstance().logoffSession(request, response);
                    }
                    
                    if (link != null) {
                        ActionForward fwd = new ActionForward(link, true);
                        return fwd;
                    } else {
                        // Do not direct to logon page for Ajax requests
                        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            return null;
                        }
                        return (mapping.findForward("logon"));
                    }
                } else {
                    /*
                     * Make the mapping and form available, this helps with
                     * reusing some JSP pages
                     */
                    request.setAttribute(Constants.REQ_ATTR_ACTION_MAPPING, mapping);
                    request.setAttribute(Constants.REQ_ATTR_FORM, form);

                    int logonStatus = LogonControllerFactory.getInstance().hasClientLoggedOn(request, response);
                    if (logonStatus == LogonController.INVALID_TICKET) {
                        ActionMessages msgs = new ActionMessages();
                        msgs.add(Globals.ERROR_KEY, new ActionMessage("login.invalidTicket"));
                        saveErrors(request, msgs);
                    } else if (logonStatus == LogonController.LOGGED_ON) {
                        SessionInfo session = LogonControllerFactory.getInstance().getSessionInfo(request);
                        // Set the logon ticket / domain logon ticket again
                        LogonControllerFactory.getInstance().addCookies(new ServletRequestAdapter(request),
                            new ServletResponseAdapter(response),
                            (String) request.getSession().getAttribute(Constants.LOGON_TICKET), getSessionInfo(request));

                        ActionForward fwd = checkIntercept(mapping, request, response);
                        if (fwd != null) {
                           return fwd;
                        }

                        /*
                         * Make sure the current navigation context is correct.
                         * If not, then check the user can switch to the correct
                         * and switch it.
                         */
                        CoreUtil.checkNavigationContext(this, mapping, form, request, response);

                        PropertyProfile profile = null;
                        if (request.getSession().getAttribute(Constants.SESSION_LOCKED) == null) {
                            profile = (PropertyProfile) request.getSession().getAttribute(Constants.SELECTED_PROFILE);
                            if (profile == null) {
                                request.getSession().setAttribute(Constants.ORIGINAL_REQUEST, Util.getOriginalRequest(request));
                                return mapping.findForward("selectPropertyProfile");
                            }
                            doCheckPermissions(mapping, session, request);
                            return super.execute(mapping, form, request, response);
                        }
                    }
                }
            } catch (NoPermissionException e) {
                if (log.isDebugEnabled())
                    log.debug("User " + e.getPrincipalName()
                                    + " attempted to access page they do have have permission for. Resource type = "
                                    + e.getResourceType()
                                    + ". Now attempting to find the first valid item in the current menu tree to display.", e);
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return null;
            } catch (SecurityException ex) {
                // Not logged in or expired
            } catch (ServletException ex) {
                throw ex;
            }

            // Do not direct to logon page for Ajax requests
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                return null;
            }

            return gotoLogon(mapping, form, request, response);
        } catch (Throwable t) {
            log.error("Failed to process authenticated request.", t);
            throw t instanceof Exception ? (Exception) t : new Exception(t);
        }
    }

    protected void doCheckPermissions(ActionMapping mapping, SessionInfo session, HttpServletRequest request) throws Exception {

        // Check the user has the permissions to access this
        // page
        boolean ok = true;
        if (resourceType != null && permissions != null) {
            ok = PolicyDatabaseFactory.getInstance().isPermitted(resourceType, permissions, session.getUser(), false);
        }
        if (!ok && requiresResourcesOfType != null) {
            ok = PolicyDatabaseFactory.getInstance().isPrincipalGrantedResourcesOfType(session.getUser(), requiresResourcesOfType, null);
        }
        if (!ok) {
            throw new NoPermissionException(session.getUser(), resourceType);
        }
    }

    /**
     * Logon is required. By default this will direct to the logon page.
     * Subclasses may overide this method to go somewhere different.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception
     */
    protected ActionForward gotoLogon(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        request.getSession().setAttribute(Constants.ORIGINAL_REQUEST, Util.getOriginalRequest(request));
        return mapping.findForward("logon");
    }

    /**
     * Get the {@link SessionInfo} for this session. This will only be available
     * after
     * {@link #execute(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)}
     * has been called.
     * <p>
     * There are many places where the session info object is required. The
     * usual way is to use
     * {@link LogonController#getSessionInfo(HttpServletRequest)}. Whereever
     * possible that method should be replaced with a call to this method.
     * 
     * @param request request
     * @return session info for request
     */
    public SessionInfo getSessionInfo(HttpServletRequest request) {
        return LogonControllerFactory.getInstance().getSessionInfo(request);

    }

    /**
     * @return ResourceType
     */
    public ResourceType getResourceType() {
        return resourceType;
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return cleanUpAndReturnToReferer(mapping, form, request, response);
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward cleanUpAndReturnToReferer(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        String toRemove = null;
        for (Enumeration e = session.getAttributeNames(); toRemove == null && e.hasMoreElements();) {
            String n = (String) e.nextElement();
            if (session.getAttribute(n) == form) {
                toRemove = n;
            }
        }
        if (toRemove != null) {
            request.getSession().removeAttribute(toRemove);
        }
        request.getSession().removeAttribute(Constants.EDITING_ITEM);
        
        // First look for a 'done' forward in the current mapping. If there is
        // none, then use the referer in the form, otherwise redirect to home
    	ActionForward fwd = mapping.findForward("done");
    	if(fwd != null) {
            return new RedirectWithMessages(fwd, request);        		
    	}
        if (((CoreForm) form).getReferer() == null) {
            log.warn("Original referer was null, forwarding to home");
            return mapping.findForward("home");
        } else {
            return new RedirectWithMessages(((CoreForm) form).getReferer(), request);
        }
    }

    /**
     * @param response
     * @throws IOException
     */
    void sendAuthorizationError(HttpServletResponse response) throws IOException {
        response.setHeader("WWW-Authenticate", "Basic realm=\"" + Branding.PRODUCT_NAME + "\"");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
    
    protected void saveError(HttpServletRequest request, String message ) {
        saveError(request, message, "");
    }
    
    protected void saveError(HttpServletRequest request, String message, Object... objects) {
        saveError(request, new ActionMessage(message, objects));
    }

    protected void saveError(HttpServletRequest request, ActionMessage message) {
        ActionMessages actionMessages = new ActionMessages();
        actionMessages.add(Globals.ERROR_KEY, message);
        saveErrors(request, actionMessages);        
    }
    
    protected void saveMessage(HttpServletRequest request, String message ) {
        saveMessage(request, message, "");
    }

    protected void saveMessage(HttpServletRequest request, String message, Object... objects) {
        ActionMessages actionMessages = new ActionMessages();
        actionMessages.add(Globals.MESSAGE_KEY, new ActionMessage(message, objects));
        saveMessages(request, actionMessages);
    }
    
    protected static ActionForward getRedirectWithMessages(ActionMapping mapping, HttpServletRequest request) {
        return getRedirectWithMessages("refresh", mapping, request);
    }

    protected static ActionForward getRedirectWithMessages(String redirect, ActionMapping mapping, HttpServletRequest request) {
        return new RedirectWithMessages(mapping.findForward(redirect), request);
    }
}