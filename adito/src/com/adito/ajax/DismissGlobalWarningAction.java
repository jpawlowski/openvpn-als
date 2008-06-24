package com.adito.ajax;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.adito.boot.ContextHolder;
import com.adito.boot.Util;
import com.adito.core.CoreUtil;
import com.adito.core.GlobalWarningManager;
import com.adito.core.UserDatabaseManager;
import com.adito.install.forms.SelectUserDatabaseForm;
import com.adito.security.Constants;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.security.User;
import com.adito.security.UserDatabase;
import com.adito.wizard.AbstractWizardSequence;

/**
 * Implementation of {@link com.adito.ajax.AbstractAjaxXMLAction} that
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
     * @see com.adito.ajax.AbstractAjaxAction#onAjaxRequest(org.apache.struts.action.ActionMapping,
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
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.SETUP_CONSOLE_CONTEXT;
    }

}
