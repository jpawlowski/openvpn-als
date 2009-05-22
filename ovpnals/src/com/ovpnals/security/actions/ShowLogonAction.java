package com.ovpnals.security.actions;

import java.util.Calendar;
import java.util.GregorianCalendar;

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

import com.ovpnals.boot.Util;
import com.ovpnals.core.RedirectWithMessages;
import com.ovpnals.core.UserDatabaseManager;
import com.ovpnals.core.actions.DefaultAction;
import com.ovpnals.realms.Realm;
import com.ovpnals.security.AccountLockedException;
import com.ovpnals.security.AuthenticationModule;
import com.ovpnals.security.AuthenticationScheme;
import com.ovpnals.security.Constants;
import com.ovpnals.security.DefaultAuthenticationScheme;
import com.ovpnals.security.LogonController;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.LogonStateAndCache;
import com.ovpnals.security.PasswordAuthenticationModule;
import com.ovpnals.security.SecurityErrorException;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.SystemDatabaseFactory;
import com.ovpnals.security.User;
import com.ovpnals.security.UserDatabase;
import com.ovpnals.security.UserNotFoundException;
import com.ovpnals.security.forms.LogonForm;


/**
 * Entry point for authentication process. This action maintains the current
 * state of the authentication and displays the tiles required for each module.
 */
public class ShowLogonAction extends DefaultAction {

    final static Log log = LogFactory.getLog(ShowLogonAction.class);

    /* (non-Javadoc)
     * @see com.ovpnals.core.actions.DefaultAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        LogonForm logonForm = (LogonForm) form;
        
        /*
         * The logon page should not be shown if already logged on. It is
         * important that hasClientLoggedOn is called first, as this is what
         * places the session lock in the session attributes.
         */
        if (LogonControllerFactory.getInstance().hasClientLoggedOn(request, response) == LogonController.LOGGED_ON
                        && request.getSession().getAttribute(Constants.SESSION_LOCKED) == null) {
            return mapping.findForward("home");
        }

        Util.noCache(response);
        
        // Should we reset the logon process
        boolean reset = request.getParameter("reset") != null;
        // Or do we need to go back to the start
        boolean back = request.getParameter("back") != null;
        
        // Display any messages that should be displayed 
        displayMessages(request);
    
    	/*
    	 * All other states process as normal
    	 */
        ActionForward fwd;
        try {
            
        	if(back) {
        		/**
        		 * This is designed to take the user back to the start of their default scheme. This
        		 * can only be invoked from the authentication scheme selection page. 
        		 */
                LogonStateAndCache logonStateMachine = (LogonStateAndCache) request.getSession().getAttribute(
                        LogonStateAndCache.LOGON_STATE_MACHINE);
                logonStateMachine.setState(LogonStateAndCache.STATE_USERNAME_KNOWN);
                ((LogonForm) form).setUsername(logonStateMachine.getUser() == null ? logonStateMachine.getSpoofedUsername() : logonStateMachine.getUser().getPrincipalName());
                ((LogonForm) form).setHasMoreAuthenticationSchemes(logonStateMachine.enabledSchemesGraeterThanOne());
                return mapping.findForward("display");
        	} 
        	
            // Configure the logon state machine 
            LogonStateAndCache logonStateMachine = checkLogonStateMachine(request, response, logonForm, reset);
            
            /*
             * Only when in STARTED state do we collect the username
             */
            if(logonStateMachine.getState() == LogonStateAndCache.STATE_STARTED) {
            	logonStateMachine.setState(LogonStateAndCache.STATE_DISPLAY_USERNAME_ENTRY);
            	return mapping.findForward("username");
            }
            
            // try to check the auth session, if not you need to sign in again.
            fwd = checkAuthSession((LogonForm)form, reset, mapping, request, response, logonStateMachine);
        } catch (SecurityErrorException see) {
            ActionMessages errs = new ActionMessages();
            errs.add(Globals.ERROR_KEY, see.getBundleActionMessage());
            saveErrors(request, errs);
            request.getSession().removeAttribute(LogonStateAndCache.LOGON_STATE_MACHINE);
            if (form != null)
                form.reset(mapping, request);
            return new RedirectWithMessages(mapping.findForward("refresh"), request);
        } catch (Throwable e) {
            ActionMessages messages = new ActionMessages();
            log.error("Logon not allowed.", e);
            messages.add(Globals.ERROR_KEY, new ActionMessage("login.logonNotAllowed", "Please contact your administrator."));
            saveErrors(request, messages);
            request.getSession().removeAttribute(LogonStateAndCache.LOGON_STATE_MACHINE);
            if (form != null)
                form.reset(mapping, request);
            return new RedirectWithMessages(mapping.findForward("refresh"), request);
        }
        Util.noCache(response);
        return fwd;
    }

    /**
     * Check the state of the authentication session and get the next forward
     * that displays the next authentication module.
     * 
     * @param form form
     * @param reset reset authentcation
     * @param mapping mapping
     * @param request request
     * @param response response
     * @param logonStateMachine logon state machine
     * @return forward
     * @throws Exception on any error
     */
    public static ActionForward checkAuthSession(LogonForm form, boolean reset, ActionMapping mapping, HttpServletRequest request,
                    HttpServletResponse response, LogonStateAndCache logonStateMachine) throws Exception{
        AuthenticationScheme authScheme = (AuthenticationScheme) request.getSession().getAttribute(Constants.AUTH_SESSION);
        if (authScheme == null || reset || authScheme.getResourceId() != logonStateMachine.getHighestPriorityScheme().getResourceId()) {

            if (log.isDebugEnabled())
                log.debug("Creating new authentication session using scheme '" + "THE SCHEME NAME" + "'");

            // Try to initalise the highest priority scheme,
            if (request.getSession().getAttribute(Constants.SESSION_LOCKED) != null) {
                // If session locked then only display password

                SessionInfo info = LogonControllerFactory.getInstance().getSessionInfo(request);
                Calendar now = new GregorianCalendar();
                authScheme = new DefaultAuthenticationScheme(info.getRealmId(), Integer.MAX_VALUE, "Fake sheme", "Fake scheme",
                                now, now, true, 0);
                authScheme.addModule(PasswordAuthenticationModule.MODULE_NAME);
            } else {
                authScheme = logonStateMachine.getHighestPriorityScheme();
            }

            if (authScheme == null) {
                log.info("There are no authenticated schemes.");
                return null;
            } else {
                
                authScheme.setUser(logonStateMachine.getUser());
                authScheme.init(request.getSession());
                
                if (authScheme.nextAuthenticationModule() == null) {
                    throw new Exception("No authentication modules have been configured.");
                }
                request.getSession().setAttribute(Constants.AUTH_SESSION, authScheme);
                if (log.isDebugEnabled())
                    log.debug("Scheme " + authScheme.getSchemeName() + " initialised OK");
            }
        }

        while (true) {
            AuthenticationModule module = authScheme.currentAuthenticationModule();
            if (form != null) {
                form.setCurrentModuleIndex(authScheme.getCurrentModuleIndex());
            }

            // The module may wish to forward somewhere other than to the
            // default login page
            ActionForward forward = module.startAuthentication(mapping, request, response);

            if (module.isRequired()) {
                return forward;
            } else {
                // Are we at the end of the sequence
                if (authScheme.nextAuthenticationModule() == null) {
                    return LogonAction.finishAuthentication(authScheme, request, response);
                }
            }
        }
    }
    
    /**
     * Check the logon state machine and configure as necessary.
     * 
     * @param request request
     * @param response response
     * @param form form
     * @param reset reset logon
     * @return logon state machine
     * @throws Exception on any error
     */
    LogonStateAndCache checkLogonStateMachine(HttpServletRequest request, HttpServletResponse response, LogonForm form, boolean reset) throws Exception {

        LogonStateAndCache logonStateMachine = (LogonStateAndCache) request.getSession().getAttribute(
                        LogonStateAndCache.LOGON_STATE_MACHINE);

        /** Reset the logon state machine if :-
         * 
         * 1. This is the first connection from this browser session. 
         * 2. The 'reset' parameter has been passed on the request
         * 3. The logon state machine is in {@link LogonStateAndCache#STATE_RETURN_TO_LOGON}
         * 
         */
        UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(form.getRealmName());
        if (logonStateMachine == null || logonStateMachine.getState() == LogonStateAndCache.STATE_RETURN_TO_LOGON || reset) {
        	/*
        	 * If the client is already logged on, then we got here because
        	 * a <i>Session Lock</i> has occured and so only require 
        	 * the users password
        	 */
            if (LogonControllerFactory.getInstance().hasClientLoggedOn(request, response) == LogonController.LOGGED_ON) {
                logonStateMachine = new LogonStateAndCache(LogonStateAndCache.STATE_DISPLAY_USERNAME_ENTERED, request.getSession());
                logonStateMachine.setUser(LogonControllerFactory.getInstance().getUser(request));
                form.initUser();
                form.setHasMoreAuthenticationSchemes(false);
            } else {
            	/*
            	 * This is a brand new session so we require the username 
            	 */
                logonStateMachine = new LogonStateAndCache(LogonStateAndCache.STATE_STARTED, request.getSession());
                request.getSession().removeAttribute(Constants.AUTH_SESSION);
           	 	if(!Util.isNullOrTrimmedBlank(form.getUsername())) {
	                try {
		                User user = udb.getAccount(form.getUsername());
	                	logonStateMachine.removeFromSpoofCache(user.getPrincipalName());
	                	form.initUser();
		                try {
		                	logonStateMachine.setUser(user);
		                }
		                catch(AccountLockedException ale) {
		                	// Continue anyway and get the exception later
		                }
	           	 		logonStateMachine.setState(LogonStateAndCache.STATE_DISPLAY_USERNAME_ENTERED);
	                }
	                catch(UserNotFoundException unfe) {
	                    form.initUser();
	                    // Spoof some authentication schemes
	                    form.setHasMoreAuthenticationSchemes(true);
	                }
           	 	}
                
            }
        } else {
            /*
             * A username has been provided but it was unknown. The error message
             * indicating this will not be displayed until the next stage is
             * complete 
             */
        	if (logonStateMachine.getState() == LogonStateAndCache.STATE_UNKNOWN_USERNAME) {
	            logonStateMachine.setState(LogonStateAndCache.STATE_UNKNOWN_USERNAME_PROMPT_FOR_PASSWORD);
        	}
        	else  if (logonStateMachine.getState() == LogonStateAndCache.STATE_KNOWN_USERNAME_MULTIPLE_SCHEMES_SELECT){        		
        		/* 
        		 * The scheme to sign on with has changed, so we need to update the
                 * logon state machine.
                 */
                logonStateMachine.forceHighestPriorityScheme(request.getParameter("selectedAuthenticationScheme"), 
                		request.getParameter("username"));
                logonStateMachine.setState(LogonStateAndCache.STATE_KNOWN_USERNAME_MULTIPLE_SCHEMES);                
            } else if (logonStateMachine.getState() == LogonStateAndCache.STATE_KNOWN_USERNAME_NO_SCHEME_SPOOF_PASSWORD_ENTRY){
            	/*
                 * The scheme to sign on with has changed, so we need to update the
                 * logon state machine
                 */
                logonStateMachine.setSpoofedHighestPriorityScheme(request.getParameter("username"));
            }
            else if(logonStateMachine.getState() == LogonStateAndCache.STATE_DISPLAY_USERNAME_ENTRY){
            	/*
            	 * The username has been collected, now process it. 
            	 */
            	if(Util.isNullOrTrimmedBlank(form.getUsername())) {
            		/**
            		 * Page refreshed, stay in same state
            		 */
	                logonStateMachine.setState(LogonStateAndCache.STATE_STARTED);
            	}
            	else {
	                logonStateMachine.setState(LogonStateAndCache.STATE_DISPLAY_USERNAME_ENTERED);
	                try {
		                User user = udb.getAccount(form.getUsername());
	                	logonStateMachine.removeFromSpoofCache(user.getPrincipalName());
		                try {
		                	logonStateMachine.setUser(user);
		                }
		                catch(AccountLockedException ale) {
		                	// Continue anyway and get the exception later
		                }
	                }
	                catch(UserNotFoundException unfe) {
	                	// Continue anyway
	                	String username = request.getParameter("username");
	        	        Realm realm = UserDatabaseManager.getInstance().getDefaultRealm(); 
	                	logonStateMachine.setState(LogonStateAndCache.STATE_UNKNOWN_USERNAME_PROMPT_FOR_PASSWORD);
	                    logonStateMachine.setSpoofedHighestPriorityScheme(username);
	                    logonStateMachine.getHighestPriorityScheme().setAccountLock(LogonControllerFactory.getInstance().checkForAccountLock(username, realm.getResourceName()));
	                }
            	}
            }
        	
            ((LogonForm) form).setHasMoreAuthenticationSchemes(logonStateMachine.enabledSchemesGraeterThanOne());
        }
        
        return logonStateMachine;
    }
    
    void displayMessages(HttpServletRequest request) throws Exception {

        // Check for session lock and display the message
        
        if (request.getSession().getAttribute(Constants.SESSION_LOCKED) != null) {
            ActionMessages messages = new ActionMessages();
            messages.add(Globals.MESSAGE_KEY, new ActionMessage("login.sessionLocked"));
            addMessages(request, messages);
        }
        
        // Check Ip address

        if (!SystemDatabaseFactory.getInstance().verifyIPAddress(request.getRemoteAddr())) {
            ActionMessages errs = new ActionMessages();
            errs.add(Globals.ERROR_KEY, new ActionMessage("login.unauthorizedAddress"));
            addErrors(request, errs);
        }
    }

    /* (non-Javadoc)
     * @see com.ovpnals.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.ALL_CONTEXTS;
    }
}
