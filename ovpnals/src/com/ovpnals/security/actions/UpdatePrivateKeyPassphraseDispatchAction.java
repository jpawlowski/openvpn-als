
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.security.actions;

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

import com.ovpnals.core.CoreAttributeConstants;
import com.ovpnals.core.CoreEvent;
import com.ovpnals.core.CoreEventConstants;
import com.ovpnals.core.CoreServlet;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.core.actions.AuthenticatedDispatchAction;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.PublicKeyStore;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.UpdatePrivateKeyPassphraseException;
import com.ovpnals.security.forms.UpdatePrivateKeyPassphraseForm;

/**
 * Implementation of
 * {@link com.ovpnals.core.actions.AuthenticatedDispatchAction} that is used
 * when the passphrase of the users private must be changed.
 * <p>
 * This may happen for example if the key was created using their account
 * password which has since changed.
 * 
 * @see com.ovpnals.security.forms.UpdatePrivateKeyPassphraseForm
 */
public class UpdatePrivateKeyPassphraseDispatchAction extends AuthenticatedDispatchAction {
    final static Log log = LogFactory.getLog(SetPasswordAction.class);

    /**
     * Constructor.
     */
    public UpdatePrivateKeyPassphraseDispatchAction() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.actions.DispatchAction#unspecified(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
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
        UpdatePrivateKeyPassphraseForm f = (UpdatePrivateKeyPassphraseForm) form;
        
        if (f.isResetPrivateKey()){
            // user has opted to reset his key, this will mean all personal info will be lost. 
            PublicKeyStore.getInstance().removeKeys(getSessionInfo(request).getUser().getPrincipalName());
            return cleanUpAndReturn(mapping, request, mapping.findForward("confirmReset"));
        }
        else{
            /*
             * Need to verify using the old password so the confidential
             * user attributes can be decrypted
             */
            try {
                PublicKeyStore.getInstance().verifyPrivateKey(getSessionInfo(request).getUser().getPrincipalName(), f.getOldPassphrase().toCharArray());
            } catch (UpdatePrivateKeyPassphraseException upkpe) {
                // incorrect passphrase
                ActionErrors errs = new ActionErrors();
                errs.add(Globals.ERROR_KEY, new ActionMessage("updatePrivateKeyPassphrase.error.incorrectPassphrase"));
                saveErrors(request.getSession(), errs);
                return mapping.getInputForward();
            }

            /*
             * Now change the passphrase
             */
            PublicKeyStore.getInstance().changePrivateKeyPassphrase(
                            getSessionInfo(request).getUser().getPrincipalName(),
                            f.getOldPassphrase(),
                            new String(LogonControllerFactory.getInstance().getPasswordFromCredentials(
                                            getSessionInfo(request).getCredentials())));
            return cleanUpAndReturn(mapping, request, mapping.findForward("success"));
        }
    }

    private ActionForward cleanUpAndReturn(ActionMapping mapping, HttpServletRequest request, ActionForward af) {
        CoreUtil.removePageInterceptListener(request.getSession(), "updatePrivateKeyPassphrase");
        /*
         * And update the user attributes and fire the logon event
         */
        CoreServlet.getServlet().fireCoreEvent(
                        new CoreEvent(this, CoreEventConstants.LOGON, getSessionInfo(request).getCredentials(),
                                        getSessionInfo(request)).addAttribute(CoreAttributeConstants.EVENT_ATTR_IP_ADDRESS,
                                        request.getRemoteAddr()).addAttribute(CoreAttributeConstants.EVENT_ATTR_HOST,
                                        request.getRemoteHost()));

        return af;
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

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

}