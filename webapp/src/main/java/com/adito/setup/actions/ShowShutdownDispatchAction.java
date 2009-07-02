/*
 */
package com.adito.setup.actions;

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

import com.adito.boot.ContextHolder;
import com.adito.core.CoreUtil;
import com.adito.core.actions.AuthenticatedDispatchAction;
import com.adito.policyframework.NoPermissionException;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.policyframework.PolicyDatabase;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.setup.forms.ShutdownForm;

/**
 * Action to shut the server down.
 */
public class ShowShutdownDispatchAction extends AuthenticatedDispatchAction {

    final static Log log = LogFactory.getLog(ShowShutdownDispatchAction.class);

    /**
     * Constructor
     */
    public ShowShutdownDispatchAction() {
        super(PolicyConstants.SERVICE_CONTROL_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_SHUTDOWN,
            PolicyConstants.PERM_RESTART });
    }

    /* (non-Javadoc)
     * @see org.apache.struts.actions.DispatchAction#unspecified(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {

        CoreUtil.clearFlow(request);

        ShutdownForm shutdownForm = (ShutdownForm) form;

        PolicyDatabase policyDatabase = PolicyDatabaseFactory.getInstance();
        if (shutdownForm.getAlreadyPerforming()) {
            shutdownForm.setShutdownType(ShutdownForm.SHUTTING_DOWN);
        } else if (policyDatabase.isPermitted(resourceType, new Permission[] { PolicyConstants.PERM_SHUTDOWN,
            PolicyConstants.PERM_RESTART }, getSessionInfo(request).getUser(), true)  && ContextHolder.getContext().isRestartAvailableMode()) {
            // Both
            shutdownForm.setShutdownType(ShutdownForm.BOTH);
            shutdownForm.setShutdownOperation(ShutdownForm.RESTART);
        } else if (policyDatabase.isPermitted(resourceType,
            new Permission[] { PolicyConstants.PERM_SHUTDOWN },
            getSessionInfo(request).getUser(),
            true)) {
            // Shutdown
            shutdownForm.setShutdownType(ShutdownForm.SHUTDOWN);
            shutdownForm.setShutdownOperation(ShutdownForm.SHUTDOWN);
        } else if (policyDatabase.isPermitted(resourceType,
            new Permission[] { PolicyConstants.PERM_RESTART },
            getSessionInfo(request).getUser(),
            true) && ContextHolder.getContext().isRestartAvailableMode()) {
            // Restart
            shutdownForm.setShutdownOperation(ShutdownForm.RESTART);
            shutdownForm.setShutdownType(ShutdownForm.RESTART);
        } else {
            throw new NoPermissionException("Cannot shutdown or restart.");
        }

        /*
         * Hack to prevent getting in a loop if sending and message then
         * cancelling
         */
        String referer = CoreUtil.getReferer(request);
        if (referer != null && referer.indexOf("/sendMessage.do") == -1) {
            shutdownForm.setReferer(referer);
        }

        int users = LogonControllerFactory.getInstance().getActiveSessions().size();
        if (users > 1) {
            ActionMessages msgs = new ActionMessages();
            msgs.add(Globals.MESSAGE_KEY, new ActionMessage("shutdown.userWarning", new Integer(users)));
            saveMessages(request, msgs);
        }

        return mapping.findForward("display");
    }

    /* (non-Javadoc)
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.SETUP_CONSOLE_CONTEXT;
    }
}
