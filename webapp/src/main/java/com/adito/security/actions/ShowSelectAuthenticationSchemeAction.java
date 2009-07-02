
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.apache.struts.util.LabelValueBean;

import com.adito.core.actions.DefaultAction;
import com.adito.security.AuthenticationScheme;
import com.adito.security.Constants;
import com.adito.security.DefaultAuthenticationScheme;
import com.adito.security.LogonStateAndCache;
import com.adito.security.SessionInfo;
import com.adito.security.forms.SchemeSelectionForm;

/**
 * Action to allow a new visitor to select the <i>Authentication Scheme</i>
 * they wish to use.
 */
public class ShowSelectAuthenticationSchemeAction extends DefaultAction {

    final static Log log = LogFactory.getLog(ShowSelectAuthenticationSchemeAction.class);

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
        ActionForward fwd = super.execute(mapping, form, request, response);
        if (fwd != null) {
            return fwd;
        }
        if (request.getSession().getAttribute(Constants.SESSION_LOCKED) != null) {
            ActionMessages messages = new ActionMessages();
            messages.add(Globals.MESSAGE_KEY, new ActionMessage("login.sessionLocked"));
            saveMessages(request, messages);
        }

        LogonStateAndCache logonStateMachine = (LogonStateAndCache) request.getSession().getAttribute(
                        LogonStateAndCache.LOGON_STATE_MACHINE);
        if (logonStateMachine == null) {
            // there is no state machine so go back to the logonpage.
            return new ActionForward("/showLogon.do");
        } else {
            List<LabelValueBean> l = new ArrayList<LabelValueBean>();
            for (Iterator i = logonStateMachine.getAuthSchemes().iterator(); i.hasNext();) {
                AuthenticationScheme seq = (DefaultAuthenticationScheme) i.next();
                LabelValueBean lvb = new LabelValueBean(seq.getResourceName(), String.valueOf(seq.getResourceId()));
                l.add(lvb);
            }
            logonStateMachine.setState(LogonStateAndCache.STATE_KNOWN_USERNAME_MULTIPLE_SCHEMES_SELECT);
            ((SchemeSelectionForm) form).setAuthenticationSchemes(l);
            return mapping.findForward("success");
        }
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
        return SessionInfo.ALL_CONTEXTS;
    }
}