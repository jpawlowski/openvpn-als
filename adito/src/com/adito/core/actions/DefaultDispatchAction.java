/*
 */
package com.adito.core.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import com.adito.boot.ContextHolder;
import com.adito.core.CoreUtil;
import com.adito.security.SessionInfo;

/**
 * <p> Default dispatch action.
 */
public abstract class DefaultDispatchAction extends DispatchAction implements CoreAction {

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
        if (fwd != null) {
            return fwd;
        }
        if (isSetupMode()) {
            if ((getNavigationContext(mapping, form, request, response) & SessionInfo.SETUP_CONSOLE_CONTEXT) == 0) {
                return mapping.findForward("setup");
            }
        }
        return super.execute(mapping, form, request, response);
    }

    /**
     * @return boolean
     */
    public boolean isSetupMode() {
    	// PLUNDEN: Removing the context
		// return ContextHolder.getContext().isSetupMode();
    	return false;
		// end change
    }

    /**
     * @param mapping
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
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
