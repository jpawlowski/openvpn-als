/*
 */
package com.ovpnals.core.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import com.ovpnals.boot.ContextHolder;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.security.SessionInfo;

/**
 * Default {@link com.ovpnals.core.actions.CoreAction} implementation that
 * extends the default struts {@link org.apache.struts.action.Action}. All
 * visitors may use these actions (there is no logon requirement or restriction
 * testing).
 */
public abstract class DefaultAction extends Action implements CoreAction {

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ActionForward fwd = checkIntercept(mapping, request, response);
        if (fwd == null) {
	        if (isInstallMode()) {
	            if ((getNavigationContext(mapping, form, request, response) & SessionInfo.SETUP_CONSOLE_CONTEXT) == 0) {
	                return mapping.findForward("setup");
	            }
	        }
	        fwd = super.execute(mapping, form, request, response);
	        if(fwd == null) {
                /* TODO
                 * 
                 * This is silly, it means we can't tell in any subclasses if a call
                 * to super.execute() should NOT forward somewhere and continue
                 * processing as normal 
                 */
	            fwd = mapping.findForward("display");	  
	        }
        }
        return fwd;
    }

    /**
     * Get if the server is in install mode.
     * 
     * @return in install mode
     */
    public boolean isInstallMode() {
        return ContextHolder.getContext().isSetupMode();
    }

    /**
     * Check whether there are any page intercepts queue. The forward
     * will be returned if there are, otherwise <code>null</code>.
     * 
     * @param mapping mapping
     * @param request request
     * @param response response
     * @return forward forward
     * @throws Exception on any error
     */
    public ActionForward checkIntercept(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return CoreUtil.checkIntercept(this, mapping, request, response);
    }


    /**
     * Add request warnings 
     * 
     * @param request request 
     * @param warnings warnings
     */
    protected void addWarnings(HttpServletRequest request, ActionMessages warnings) {
        CoreUtil.addWarnings(request, warnings);
    }

    /**
     * Get the current warnings, creating them if none exists
     *
     * @return the warnings that already exist in the request, or a new ActionMessages object if empty.
     * @param request The servlet request we are processing
     */
    protected ActionMessages getWarnings(HttpServletRequest request) {
        return CoreUtil.getWarnings(request);
    }

    /**
     * Save the specified warnings messages.
     *
     * @param request request
     * @param warnings warnings
     */
    protected void saveWarnings(HttpServletRequest request, ActionMessages warnings) {
        CoreUtil.saveWarnings(request, warnings);
    }
}