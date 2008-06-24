package com.adito.security.actions;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.adito.boot.ContextKey;
import com.adito.boot.KeyStoreManager;
import com.adito.core.BundleActionMessage;
import com.adito.core.CoreAttributeConstants;
import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.core.CoreException;
import com.adito.core.CoreServlet;
import com.adito.core.GlobalWarning;
import com.adito.core.GlobalWarningManager;
import com.adito.core.RedirectWithMessages;
import com.adito.core.RequestParameterMap;
import com.adito.core.ServletRequestAdapter;
import com.adito.core.GlobalWarning.DismissType;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.PolicyUtil;
import com.adito.properties.Property;
import com.adito.properties.impl.profile.ProfilePropertyKey;
import com.adito.properties.impl.systemconfig.SystemConfigKey;
import com.adito.properties.impl.userattributes.UserAttributeKey;
import com.adito.security.AccountLockedException;
import com.adito.security.AuthenticationModule;
import com.adito.security.AuthenticationScheme;
import com.adito.security.Constants;
import com.adito.security.Credentials;
import com.adito.security.InputRequiredException;
import com.adito.security.InvalidLoginCredentialsException;
import com.adito.security.LogonController;
import com.adito.security.LogonControllerFactory;
import com.adito.security.LogonStateAndCache;
import com.adito.security.PasswordCredentials;
import com.adito.security.SessionInfo;
import com.adito.security.User;
import com.adito.security.forms.LogonForm;

/**
 * Logs a user into the Adito.
 * 
 * @author Lee David Painter
 */
public class LogonAction extends Action {

    private static Log log = LogFactory.getLog(LogonAction.class);

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {

        ActionMessages msgs = new ActionMessages();

        SessionInfo sessionInfo = LogonControllerFactory.getInstance().getSessionInfo(request);
        
        if (sessionInfo == null && request.getSession().getAttribute(Constants.SESSION_LOCKED) == null
                        && LogonControllerFactory.getInstance().hasClientLoggedOn(request, response) == LogonController.LOGGED_ON) {
            if (log.isDebugEnabled())
                log.debug(request.getRemoteHost() + " is already authenticated");

            return mapping.findForward("success");
        }
        
        /*
         * Get the authentication session and module to use to validate this
         * authentication attempt
         */
        AuthenticationScheme scheme = (AuthenticationScheme) request.getSession().getAttribute(Constants.AUTH_SESSION);
        LogonStateAndCache logonStateMachine = (LogonStateAndCache) request.getSession().getAttribute(
                        LogonStateAndCache.LOGON_STATE_MACHINE);

        // there are different users so we need to logon again, clearing the authentication scheme and logon machine.
        if (sessionInfo != null && logonStateMachine != null && !sessionInfo.getUser().equals(logonStateMachine.getUser())){
            request.getSession().removeAttribute(Constants.AUTH_SESSION);
            request.getSession().removeAttribute(LogonStateAndCache.LOGON_STATE_MACHINE);
            LogonControllerFactory.getInstance().logoffSession(request, response);
            msgs.add(Globals.ERROR_KEY, new ActionMessage("login.logonNotAllowed", "Session no longer valid, logon again."));
            saveErrors(request, msgs);
            return new RedirectWithMessages(mapping.findForward("logon"), request);
        }
        
        if (logonStateMachine == null) {
            logonStateMachine = new LogonStateAndCache(LogonStateAndCache.STATE_STARTED, request.getSession());
            request.getSession().setAttribute(LogonStateAndCache.LOGON_STATE_MACHINE, logonStateMachine);
        }
        if (scheme == null) {

            ActionForward fwd = null;
            try {
                fwd = ShowLogonAction.checkAuthSession(null, false, mapping, request, response, logonStateMachine);
            } catch(CoreException ce) {
            	
            } catch (Throwable e) {
                log.error("Logon not allowed.", e);
                ActionMessages errs = new ActionMessages();
                if(e instanceof CoreException) {
                	errs.add(Globals.ERROR_KEY, ((CoreException)e).getBundleActionMessage());
                }
                else {
	                errs.add(Globals.ERROR_KEY, new ActionMessage("login.logonNotAllowed",
	                                "Please contact your administrator."));
                }
                saveErrors(request, errs);
                request.getSession().removeAttribute(Constants.AUTH_SESSION);
                request.getSession().removeAttribute(LogonStateAndCache.LOGON_STATE_MACHINE);
                if (form != null)
                    form.reset(mapping, request);
                return new RedirectWithMessages(mapping.findForward("failed"), request);
            }
            if (fwd != null) {
                scheme = (AuthenticationScheme) request.getSession().getAttribute(Constants.AUTH_SESSION);
            }
        }

        if (scheme != null) {
            AuthenticationModule module = scheme.currentAuthenticationModule();
            if (module == null) {
                log.error("No authentication module.");
                request.getSession().removeAttribute(Constants.AUTH_SESSION);
                return mapping.findForward("logon");
            }

            try {            	
            	// If there is no user in the scheme then it is an invalid login
            	if(scheme.getUser() == null) {
            		throw new InvalidLoginCredentialsException();
            	}
            	
            	// Check the account is enabled and not locked
            	if(!PolicyUtil.isEnabled(scheme.getUser())) {
            		throw new AccountLockedException(scheme.getUsername(), "Account disabled.", true, 0);
            	}
            	
            	// Check for locks
            	LogonControllerFactory.getInstance().checkForAccountLock(scheme.getUsername(), scheme.getUser().getRealm().getResourceName());

            	// Authenticate
                authenticate(scheme, request);

                // Check logon is currently allowed
                String logonNotAllowedReason = LogonControllerFactory.getInstance().checkLogonAllowed(
                                scheme.getUser());

                if (logonNotAllowedReason != null) {
                    log.warn("Logon not allowed because '" + logonNotAllowedReason + "'");
                    msgs.add(Globals.ERROR_KEY, new ActionMessage("login.logonNotAllowed", logonNotAllowedReason));
                    saveErrors(request, msgs);
                    return new RedirectWithMessages(mapping.findForward("logon"), request);
                }

                // Check for the next authentication modules
                AuthenticationModule nextModule = scheme.nextAuthenticationModule();
                if (nextModule != null && request.getSession().getAttribute(Constants.SESSION_LOCKED) == null) {
                    if (log.isDebugEnabled())
                        log.debug("There are more authentication modules to satisfy (current mapping = " + mapping.getPath());
                    ActionForward fw = new RedirectWithMessages(mapping.findForward("logon"), request);
                    return fw;
                }

                return finishAuthentication(scheme, request, response);
            } catch (InputRequiredException ex) {
                // The page wants to display or redirect somewhere
            	if(ex.getForward()==null)
            		return mapping.findForward("logon");
            	else
            		return ex.getForward();
            } catch (AccountLockedException ale) {
                return accountLocked(mapping, request, ale, msgs);
            } catch (InvalidLoginCredentialsException ex) {
                log.error("[" + request.getRemoteHost()
                    + "] authentication failed", ex);

                LogonForm logonForm = (LogonForm) form;

                CoreServlet.getServlet().fireCoreEvent(
                    new CoreEvent(this, CoreEventConstants.LOGON, null, null, ex).addAttribute(
                        CoreAttributeConstants.EVENT_ATTR_IP_ADDRESS, request.getRemoteAddr()).addAttribute(
                        CoreAttributeConstants.EVENT_ATTR_HOST, request.getRemoteHost()).addAttribute(
                        CoreAttributeConstants.EVENT_ATTR_SCHEME, scheme.getSchemeName()).addAttribute(
                        CoreAttributeConstants.EVENT_ATTR_ACCOUNT, logonForm.getUsername()));

                
            	request.getSession().removeAttribute(LogonStateAndCache.LOGON_STATE_MACHINE);
                request.getSession().removeAttribute(Constants.AUTH_SESSION);

                try {
                    scheme.setAccountLock(LogonControllerFactory.getInstance().logonFailed(((LogonForm)form).getUsername(),
                                    ((LogonForm)form).getRealmName(), scheme.getAccountLock()));
                } catch (AccountLockedException ale) {
                	return accountLocked(mapping, request, ale, msgs);
                }

                msgs.add(Globals.ERROR_KEY, new ActionMessage("login.invalidCredentials"));
                saveErrors(request, msgs);
                return new RedirectWithMessages(mapping.findForward("logon"), request);
            } catch (Exception e) {
                log.error("Internal error authenticating.", e);
                msgs.add(Globals.ERROR_KEY, new BundleActionMessage("security", "login.error", e.getMessage()));
                saveErrors(request, msgs);
                request.getSession().setAttribute(Constants.EXCEPTION, e);
            	request.getSession().removeAttribute(LogonStateAndCache.LOGON_STATE_MACHINE);
                request.getSession().removeAttribute(Constants.AUTH_SESSION);
                return new RedirectWithMessages(mapping.findForward("logon"), request);
            }
        } else {
            ActionMessages errs = new ActionMessages();
            errs.add(Globals.MESSAGE_KEY, new BundleActionMessage("security", "login.logonNotAllowed", "No scheme available."));
            saveErrors(request, errs);
            request.getSession().removeAttribute(LogonStateAndCache.LOGON_STATE_MACHINE);
            request.getSession().removeAttribute(Constants.AUTH_SESSION);
            if (form != null)
                form.reset(mapping, request);
            return new RedirectWithMessages(mapping.findForward("logon"), request);
        }
    }

    /**
     * Complete the authentication process.
     * 
     * @param scheme scheme
     * @param request request
     * @param response response
     * @return forward to
     * @throws Exception on any error
     */
    public static ActionForward finishAuthentication(AuthenticationScheme scheme, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {

        // Check we have a user object
        if (scheme.getUser() == null) {
            throw new Exception("No authentication module provided a user.");
        }

        // now add the policies associated with this scheme to the http session
        // if the property says so.
        if (Property.getPropertyBoolean(new SystemConfigKey("security.enforce.policy.resource.access"))) {
            List signOnPolicies = PolicyDatabaseFactory.getInstance().getPoliciesAttachedToResource(scheme,
                            scheme.getUser().getRealm());
            scheme.getServletSession().setAttribute("auth.scheme.policies", signOnPolicies);
        }

        // If the user is a manager, check if there is a new Adito
        // version, or if there any exension updates
        if (PolicyDatabaseFactory.getInstance().isAnyAccessRightAllowed(scheme.getUser(), true, true, false)) {
            
            if ("false".equals(Property.getProperty(new ContextKey("webServer.disableCertificateWarning")))
                            && !KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE).isCertificateTrusted(
                                            Property.getProperty(new ContextKey("webServer.alias")))) {
                GlobalWarningManager.getInstance().addMultipleGlobalWarning(new GlobalWarning(GlobalWarning.MANAGEMENT_USERS, new BundleActionMessage("keystore",
                    "keyStore.untrustedCertificate.warning"), DismissType.DISMISS_FOR_USER));
            }
            
        }

        /*
         * Each authentication module needs to be informed that authentication
         * is now complete so it may perform any last minute checks
         */
        scheme.authenticationComplete(request, response);

        // Allow the home page to be redirected.
        request.getSession().setAttribute(Constants.REDIRECT_HOME, "true");

        // Authenitcation sequence complete
        if (log.isDebugEnabled())
            log.debug(scheme.getUsername() + " [" + request.getRemoteHost() + "] has been authenticated");

        // Forward control to the specified success URI (possibly from the
        // initial unautenticated request)
        String originalRequest = (String) request.getSession().getAttribute(Constants.ORIGINAL_REQUEST);
        ActionForward forward = null;

        // Where next?
        List profiles = (List)request.getSession().getAttribute(Constants.PROFILES);
        int selectProfileAtLogin = -1;
        try {
        	selectProfileAtLogin = Property.getPropertyInt(new UserAttributeKey(scheme.getUser(),  User.USER_STARTUP_PROFILE));
        }
        catch(NumberFormatException nfe) {
        }
        if (selectProfileAtLogin == -1 && profiles != null && profiles.size() > 1) {
            // Prompt for the profile
            forward = new ActionForward("/showSelectPropertyProfile.do");
        } else {
            if(null == originalRequest || "/showHome.do".equals(originalRequest) || "".equals(originalRequest)) {
                boolean admin = LogonControllerFactory.getInstance().isAdministrator(scheme.getUser());
                if (admin) {
                    originalRequest = "/showSystemConfiguration.do";
                } else {
                    originalRequest = "/showHome.do";
                }
                request.getSession().removeAttribute(Constants.ORIGINAL_REQUEST);
            }
            if (Property.getPropertyBoolean(new ProfilePropertyKey("client.autoStart", LogonControllerFactory.getInstance().getSessionInfo(request)))) {
                request.getSession().removeAttribute(Constants.ORIGINAL_REQUEST);
                request.getSession().setAttribute(Constants.REQ_ATTR_LAUNCH_AGENT_REFERER, originalRequest);
                forward = new ActionForward("/launchAgent.do", false);
            } else {
                forward = new ActionForward(originalRequest, true);
            }
        }
        return forward;

    }

    /**
     * Start the authentication process.
     * 
     * @param scheme scheme
     * @param request request
     * @throws Exception on any error
     */
    public static void authenticate(AuthenticationScheme scheme, HttpServletRequest request) throws Exception {
        AuthenticationModule module = scheme.currentAuthenticationModule();
        if (module == null) {
            throw new Exception("No current authentication module");
        }
        RequestParameterMap params = new RequestParameterMap(new ServletRequestAdapter(request));
        User currentUser = scheme.getUser();
        LogonStateAndCache logonStateMachine = (LogonStateAndCache) request.getSession().getAttribute(
                        LogonStateAndCache.LOGON_STATE_MACHINE);

        if (logonStateMachine == null) {
            logonStateMachine = new LogonStateAndCache(LogonStateAndCache.STATE_STARTED, request.getSession());
        }

        if (logonStateMachine.getState() == LogonStateAndCache.STATE_KNOWN_USERNAME_NO_SCHEME_SPOOF_PASSWORD_ENTRY) {
            scheme.addCredentials(new PasswordCredentials("", "".toCharArray()));
        } else if (logonStateMachine.getState() == LogonStateAndCache.STATE_UNKNOWN_USERNAME_PROMPT_FOR_PASSWORD) {
            Credentials creds = module.authenticate(request, params);
            if(creds!=null)
            	scheme.addCredentials(creds);
        } else {
        	Credentials creds = module.authenticate(request, params);
            if(creds!=null) {
            	scheme.addCredentials(creds);
            	logonStateMachine.setState(LogonStateAndCache.STATE_VALID_LOGON);
            }
            // Check we have a user object
            if (currentUser == null && scheme.getUser() == null) {
                throw new Exception("The first authentication did not provide a user.");
            }
        }

        PolicyUtil.checkLogin(scheme.getUser());
    }

	/**
	 * Set an account to be locked and create the appropriate error messages
	 * 
	 * @param mapping mapping
	 * @param request request
	 * @param ale lock exception
	 * @param msgs messages
	 * @return forward
	 */
	ActionForward accountLocked(ActionMapping mapping, HttpServletRequest request, AccountLockedException ale,
												ActionMessages msgs) {

		request.getSession().removeAttribute(Constants.AUTH_SESSION);
		request.getSession().removeAttribute(LogonStateAndCache.LOGON_STATE_MACHINE);
		msgs.add(Globals.ERROR_KEY, new ActionMessage(ale.isDisabled() ? "login.accountDisabled" : "login.accountLocked",
						String.valueOf(((ale.getTimeLeft() / 1000) + 59) / 60)));
		log.warn(ale.getUsername() + " [" + request.getRemoteHost() + "] account locked", ale);
		saveErrors(request, msgs);
		return new RedirectWithMessages(mapping.findForward("logon"), request);
	}

}