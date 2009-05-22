package com.ovpnals.ajax;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.boot.ContextHolder;
import com.ovpnals.boot.Util;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.core.GlobalWarningManager;
import com.ovpnals.core.UserDatabaseManager;
import com.ovpnals.install.forms.SelectUserDatabaseForm;
import com.ovpnals.security.Constants;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.User;
import com.ovpnals.security.UserDatabase;
import com.ovpnals.wizard.AbstractWizardSequence;

/**
 * Implementation of {@link com.ovpnals.ajax.AbstractAjaxXMLAction} that
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
     * @see com.ovpnals.ajax.AbstractAjaxAction#onAjaxRequest(org.apache.struts.action.ActionMapping,
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
     * @see com.ovpnals.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.SETUP_CONSOLE_CONTEXT;
    }

}
