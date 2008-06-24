
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

import javax.servlet.ServletException;
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

import com.adito.boot.Branding;
import com.adito.boot.Util;
import com.adito.core.CoreUtil;
import com.adito.core.ServletRequestAdapter;
import com.adito.core.ServletResponseAdapter;
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
import com.adito.security.User;

/**
 * 
 * Generic action to be used for all authenticated actions by the Adito
 * project. This checks that the user is logged on and if not directs them to
 * the logon page. Once logon is complete, the user will be directed to the
 * request path.
 * @since 0.1
 * @see com.adito.core.actions.AuthenticatedDispatchAction
 */
public abstract class AuthenticatedAction extends DefaultAction implements CoreAction {

    static Log log = LogFactory.getLog(AuthenticatedAction.class);

    // Private instance variables

    private boolean requiresAdministrator;
    private ResourceType resourceType;
    private Permission[] permissions;

    /**
     * Use this constructor for actions that do not require any resource
     * permissions
     */
    public AuthenticatedAction() {
    }

   
    /**
     * Use this constructor for actions that require a resource permission to
     * operator
     * 
     * @param resourceType resource type
     * @param permissions permission required
     */
    public AuthenticatedAction(ResourceType resourceType, Permission permissions[]) {
        if (resourceType == null || permissions == null || permissions.length < 1) {
            throw new IllegalArgumentException("Must provide a resource type and at least 1 permission.");
        }
        this.resourceType = resourceType;
        this.permissions = permissions;
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
     * @param request TODO
     * 
     * @return session info for request
     */
    public SessionInfo getSessionInfo(HttpServletRequest request) {
        return LogonControllerFactory.getInstance().getSessionInfo(request);

    }

    public final ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        // Setup mode
        boolean installMode = isInstallMode();
        if (installMode) {
            if ((getNavigationContext(mapping, form, request, response) & SessionInfo.SETUP_CONSOLE_CONTEXT) == 0) {
                return mapping.findForward("setup");
            } else {
                /*
                 * Make the mapping and form available, this helps with reusing
                 * some JSP pages
                 */
                request.setAttribute(Constants.REQ_ATTR_ACTION_MAPPING, mapping);
                request.setAttribute(Constants.REQ_ATTR_FORM, form);

                CoreUtil.checkNavigationContext(this, mapping, form, request, response);
                return onExecute(mapping, form, request, response);
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
                        return new ActionForward(link, true);
                    } else {
                        // Do not direct to logon page for Ajax requests
                        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            return null;
                        }
                        return mapping.findForward("logon");
                    }

                } else {

                    int logonStatus = LogonControllerFactory.getInstance().hasClientLoggedOn(request, response);
                    if (logonStatus == LogonController.INVALID_TICKET) {
                        ActionMessages msgs = new ActionMessages();
                        msgs.add(Globals.ERROR_KEY, new ActionMessage("login.invalidTicket"));
                        saveErrors(request, msgs);
                    } else if (logonStatus == LogonController.LOGGED_ON) {

                        User currentUser = LogonControllerFactory.getInstance().getUser(request);

                        // Set the logon ticket / domain logon ticket again
                        LogonControllerFactory.getInstance().addCookies(new ServletRequestAdapter(request),
                            new ServletResponseAdapter(response),
                            (String) request.getSession().getAttribute(Constants.LOGON_TICKET), getSessionInfo(request));

                        if (!LogonControllerFactory.getInstance().isAdministrator(getSessionInfo(request).getUser())
                                        && requiresAdministrator) {
                            response.sendError(403, "You do not have permission to access this area");
                            return null;
                        } else {
                            /*
                             * Make the mapping and form available, this helps
                             * with reusing some JSP pages
                             */
                            request.setAttribute(Constants.REQ_ATTR_ACTION_MAPPING, mapping);
                            request.setAttribute(Constants.REQ_ATTR_FORM, form);

                            // Check for intercepts, but don't forward if the
                            // result of an Ajax action

                            ActionForward fwd = checkIntercept(mapping, request, response);
                            if(fwd != null) {
                                return fwd;
                            }

                            /*
                             * Make sure the current navigation context is
                             * correct. If not, then check the user can switch
                             * to the correct and switch it.
                             */
                            CoreUtil.checkNavigationContext(this, mapping, form, request, response);

                            // Check the user has the permissions to access this
                            // page
                            if (resourceType != null) {
                                if (!PolicyDatabaseFactory.getInstance().isPermitted(resourceType, permissions, currentUser, false)) {
                                    throw new NoPermissionException("Action denied for current user");
                                }
                            }

                            if (request.getSession().getAttribute(Constants.SESSION_LOCKED) == null || isIgnoreSessionLock()) {
                                if (requiresProfile()) {
                                    PropertyProfile profile = (PropertyProfile) request.getSession().getAttribute(
                                        Constants.SELECTED_PROFILE);
                                    if (profile == null) {
                                        request.getSession().setAttribute(Constants.ORIGINAL_REQUEST,
                                            Util.getOriginalRequest(request));
                                        return mapping.findForward("selectPropertyProfile");
                                    }
                                }
                                return onExecute(mapping, form, request, response);
                            }
                        }
                    }
                }
            } catch (NoPermissionException e) {
                if (log.isDebugEnabled())
                    log.debug("User attempted to access page they do have have permission for. Resource type = " 
                                    + resourceType
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
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return null;
            }

            return gotoLogon(mapping, form, request, response);
        } catch (Throwable t) {
            log.error("Failed to process authenticated request.", t);
            throw t instanceof Exception ? (Exception) t : new Exception(t);
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
    protected ActionForward gotoLogon(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().setAttribute(Constants.ORIGINAL_REQUEST, Util.getOriginalRequest(request));
        return mapping.findForward("logon");
    }

    /**
     * Get the resource type that was passed in on the constructor. The resource
     * type will be supplied if this particular action implementation deals with
     * resources controlled by the policy framework. This is used to check
     * permissions
     * 
     * @return resource type
     */
    public ResourceType getResourceType() {
        return resourceType;
    }

    /**
     * Get if this action requires a profile to be selected. Some actions may
     * not require a profile to be present (the main one being the profile
     * selection page!). If no profile is found in the session and this method
     * returned <code>true</code> then the user will be directed to the
     * 'selectPropertyProfile' page.
     * 
     * @return requires a profile
     */

    protected boolean requiresProfile() {
        return true;
    }

    /**
     * Get if this action requires authentication to operator.
     * 
     * @return authentication
     */
    protected boolean requiresAuthentication() {
        return true;
    }

    /**
     * Get if this action should ignore any session locks
     * 
     * @return ignore session locks
     */
    protected boolean isIgnoreSessionLock() {
        return false;
    }

    /*
     * Send SC_AUTHORIZED to the client browser forcing HTTP authentication with
     * the realm "Adito".
     * 
     * @param response response to write authentication request to.
     */
    void sendAuthorizationError(HttpServletResponse response) throws IOException {
        response.setHeader("WWW-Authenticate", "Basic realm=\"" + Branding.PRODUCT_NAME + "\"");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    /**
     * This method is called when all the default checks have take place.
     * Subclass would do their actual processing here.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception on any error
     */
    protected ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward("success");
    }

    /**
     * Return the navigation context this action may be used in as a mask. If
     * the user is not in the appropriate navigation then they will be
     * automatically redirected to the action that switches contexts.
     * 
     * @param mapping mapping
     * @param form form
     * @param request request
     * @param response response
     * @return navigation context
     * @see SessionInfo#MANAGEMENT_CONSOLE_CONTEXT
     * @see SessionInfo#USER_CONSOLE_CONTEXT
     * @see SessionInfo#getNavigationContext()
     */
    public abstract int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response);
    
    protected void saveError(HttpServletRequest request, String message ) {
        saveMessage(request, message, "");
    }
    
    protected void saveError(HttpServletRequest request, String message, Object... objects) {
        ActionMessages actionMessages = new ActionMessages();
        actionMessages.add(Globals.ERROR_KEY, new ActionMessage(message, objects));
        saveErrors(request, actionMessages);
    }
    
    protected void saveMessage(HttpServletRequest request, String message ) {
        saveMessage(request, message, "");
    }

    protected void saveMessage(HttpServletRequest request, String message, Object... objects) {
        ActionMessages actionMessages = new ActionMessages();
        actionMessages.add(Globals.MESSAGE_KEY, new ActionMessage(message, objects));
        saveErrors(request, actionMessages);
    }
}