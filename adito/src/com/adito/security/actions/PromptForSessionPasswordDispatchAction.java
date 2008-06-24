
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
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.adito.core.CoreUtil;
import com.adito.core.UserDatabaseManager;
import com.adito.core.actions.AuthenticatedDispatchAction;
import com.adito.core.forms.CoreForm;
import com.adito.security.AuthenticationScheme;
import com.adito.security.Constants;
import com.adito.security.InvalidLoginCredentialsException;
import com.adito.security.PasswordCredentials;
import com.adito.security.SessionInfo;
import com.adito.security.UserDatabase;
import com.adito.security.forms.PromptForSessionPasswordForm;

/**
 * <p>
 * Action to prompt the currently logged on user to enter their session
 * password.
 */
public class PromptForSessionPasswordDispatchAction extends AuthenticatedDispatchAction {
    final static Log log = LogFactory.getLog(SetPasswordAction.class);

    /**
     * Constructor.
     */
    public PromptForSessionPasswordDispatchAction() {
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
        ((CoreForm) form).setReferer(CoreUtil.getReferer(request));
        ((PromptForSessionPasswordForm)form).setForwardTo(request.getParameter("forwardTo"));
        ((PromptForSessionPasswordForm)form).setTarget(request.getParameter("target"));
        ((PromptForSessionPasswordForm)form).setFolder(request.getParameter("folder"));
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
        PromptForSessionPasswordForm pfspf = (PromptForSessionPasswordForm) form;
        AuthenticationScheme scheme = (AuthenticationScheme) getSessionInfo(request).getHttpSession().getAttribute(Constants.AUTH_SESSION);
        try {
            SessionInfo session = getSessionInfo(request);
            UserDatabase udb = UserDatabaseManager.getInstance().getUserDatabase(session.getUser().getRealm());
            String username = session.getUser().getPrincipalName();
            if (!udb.checkPassword(username, pfspf.getPassword())) {
                throw new Exception("Incorrect password.");
            }
            scheme.addCredentials(new PasswordCredentials(username, pfspf.getPassword().toCharArray()));
            request.setAttribute(Constants.REQ_ATTR_FORWARD_TO, ((PromptForSessionPasswordForm)form).getForwardTo());
            request.setAttribute(Constants.REQ_ATTR_TARGET, ((PromptForSessionPasswordForm)form).getTarget());
            request.setAttribute(Constants.REQ_ATTR_FOLDER, ((PromptForSessionPasswordForm)form).getFolder());
            return mapping.findForward("redirect");
        } catch (InvalidLoginCredentialsException e) {
            ActionMessages mesgs = new ActionMessages();
            mesgs.add(Globals.ERROR_KEY, new ActionMessage("promptForSessionPassword.invalidCredentials"));
            saveErrors(request, mesgs);
            return mapping.findForward("display");
        }
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
        return new ActionForward(((CoreForm) form).getReferer(), true);
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
        return SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

}