package net.openvpn.als.core.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.core.actions.AuthenticatedAction;
import net.openvpn.als.core.forms.GetRSSFeedForm;
import net.openvpn.als.security.SessionInfo;


/**
 * Implementation of {@link net.openvpn.als.core.actions.AuthenticatedAction}
 * that retrieves an RSS feed and displays the panel for it.  
 */
public class GetRSSFeedAction extends AuthenticatedAction {

    /* (non-Javadoc)
     * @see net.openvpn.als.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.core.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
    	((GetRSSFeedForm)form).init(request.getParameter("feed"));
        return mapping.findForward("display");
    }

}
