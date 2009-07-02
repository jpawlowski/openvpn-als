
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
			
package com.adito.security.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.adito.core.CoreAttributeConstants;
import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.core.CoreServlet;
import com.adito.core.CoreUtil;
import com.adito.core.UserDatabaseManager;
import com.adito.core.actions.AuthenticatedDispatchAction;
import com.adito.security.AuthenticationScheme;
import com.adito.security.Constants;
import com.adito.security.DefaultLogonController;
import com.adito.security.LogonControllerFactory;
import com.adito.security.PasswordCredentials;
import com.adito.security.PublicKeyStore;
import com.adito.security.SessionInfo;
import com.adito.security.UpdatePrivateKeyPassphraseException;
import com.adito.security.UserDatabase;
import com.adito.security.forms.PromptForPrivateKeyPassphraseForm;


/**
 * Implementation of {@link com.adito.core.actions.AuthenticatedDispatchAction}
 * that is used to prompt for the users private key passphrase. 
 * <p>
 * This will happen for when the <b>no</b> authentication modules used to login 
 * used the account password.
 *   
 * @see com.adito.security.forms.UpdatePrivateKeyPassphraseForm
 */
public class PromptForPrivateKeyPassphraseDispatchAction extends AuthenticatedDispatchAction {
    final static Log log = LogFactory.getLog(SetPasswordAction.class);

    /**
     * Constructor.
     */
    public PromptForPrivateKeyPassphraseDispatchAction() {
        super();
    }

    /* (non-Javadoc)
     * @see org.apache.struts.actions.DispatchAction#unspecified(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PromptForPrivateKeyPassphraseForm f = (PromptForPrivateKeyPassphraseForm) form;
        f.setNewKey(!PublicKeyStore.getInstance().hasPrivateKey(getSessionInfo(request).getUser().getPrincipalName()));
        return mapping.findForward("display");
    }

    /**
     * Commit the passphrase change.
     * 
     * @param mapping mappng
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception
     */
    public ActionForward commit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        PromptForPrivateKeyPassphraseForm f = (PromptForPrivateKeyPassphraseForm) form;
        SessionInfo session = getSessionInfo(request);
        UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(session.getUser().getRealm());
        if(!udb.checkPassword(session.getUser().getPrincipalName(),
        		f.getPassphrase())) {
        	// User has entered incorrect passphrase - go back
        	ActionErrors errs = new ActionErrors();
            errs.add(Globals.ERROR_KEY, new ActionMessage("promptForPrivateKeyPassphrase.error.incorrectPassphrase"));
            saveErrors(request.getSession(), errs);
            return mapping.getInputForward();        	
        }
        
        // Now check to see if the password has been added to the authentication scheme
        AuthenticationScheme scheme = (AuthenticationScheme) getSessionInfo(request).getHttpSession().getAttribute(Constants.AUTH_SESSION);
        if(LogonControllerFactory.getInstance().getPasswordFromCredentials(scheme)==null) {
        	// No so lets add it
        	scheme.addCredentials(new PasswordCredentials(getSessionInfo(request).getUser().getPrincipalName(), f.getPassphrase().toCharArray()));
        }
        
        try {
            PublicKeyStore.getInstance().verifyPrivateKey(getSessionInfo(request).getUser().getPrincipalName(), f.getPassphrase().toCharArray());
        }
        catch(UpdatePrivateKeyPassphraseException upkpe) {

        	// LDP - This code was incorrectly adding a bad logon warning. What we actually have to 
        	// do is redirect back again to the UpdatePrivateKeyPassphraseIntercerptListener
        	CoreUtil.removePageInterceptListener(request.getSession(), "promptForPrivateKeyPassphrase");
            CoreUtil.addPageInterceptListener(request.getSession(), new DefaultLogonController.UpdatePrivateKeyPassphraseInterceptListener());
            
            // Force the forward back to /showHome.do so that the intercerpt functions correctly
            return new ActionForward("/showHome.do");
        }
        
		CoreUtil.removePageInterceptListener(request.getSession(), "promptForPrivateKeyPassphrase");            
        CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this, CoreEventConstants.LOGON, getSessionInfo(request).getCredentials(), getSessionInfo(request)).addAttribute(
            CoreAttributeConstants.EVENT_ATTR_IP_ADDRESS, request.getRemoteAddr()).addAttribute(
                CoreAttributeConstants.EVENT_ATTR_HOST, request.getRemoteHost()));
        
        return mapping.findForward("success");
    }

    /**
     * Cancel and logout.
     * 
     * @param mapping mappng
     * @param form form
     * @param request request
     * @param response response
     * @return forward
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return mapping.findForward("cancel");
    }

    /* (non-Javadoc)
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

	@Override
	public ActionForward checkIntercept(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

}