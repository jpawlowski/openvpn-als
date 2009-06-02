package net.openvpn.als.ajax;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.boot.ContextHolder;
import net.openvpn.als.boot.Util;
import net.openvpn.als.core.CoreUtil;
import net.openvpn.als.core.GlobalWarningManager;
import net.openvpn.als.core.UserDatabaseManager;
import net.openvpn.als.install.forms.SelectUserDatabaseForm;
import net.openvpn.als.security.Constants;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.security.User;
import net.openvpn.als.security.UserDatabase;
import net.openvpn.als.wizard.AbstractWizardSequence;

/**
 * Implementation of {@link net.openvpn.als.ajax.AbstractAjaxXMLAction} that
 * returns an XML document containing a list of user accounts.
 * <p>
 * Two request parameters are supported. First, the optional <b>account</b> 
 * which may contain a search string and secondly <b>maxRows</b> which is an
 * integer, defaults to 10 and determines the maximum number of results to
 * return. If <b>account</b> is not supplied, all accounts (up to the specified
 * maximum rows) will be returned.
 */

public class DismissGlobalWarningAction extends AbstractAjaxXMLAction {

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.ajax.AbstractAjaxAction#onAjaxRequest(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse,
     *      org.ajaxtags.helpers.AjaxXmlBuilder)
     */
    protected void onAjaxRequest(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
                    AjaxXmlBuilder builder) throws Exception {    	
    	String messageKey = request.getParameter("messageKey");
    	if(messageKey == null) {
    		throw new Exception("No messageKey parameter supplied.");
    	}
    	GlobalWarningManager.getInstance().dismissGlobalWarning(request.getSession(), messageKey);        
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.SETUP_CONSOLE_CONTEXT;
    }

}
