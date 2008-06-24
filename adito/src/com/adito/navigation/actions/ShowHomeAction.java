
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
			
package com.adito.navigation.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.core.CoreUtil;
import com.adito.core.FileDownloadPageInterceptListener;
import com.adito.core.RedirectWithMessages;
import com.adito.core.actions.AuthenticatedAction;
import com.adito.security.Constants;
import com.adito.security.SessionInfo;


/**
 * Implementation of {@link com.adito.core.actions.AuthenticatedAction}
 * that shows the home page appropriate for the current navigation context.
 */
public class ShowHomeAction extends AuthenticatedAction {
    /**
     * 
     * Constructor.
     */
    public ShowHomeAction() {
        super();
    }

    /* (non-Javadoc)
     * @see com.adito.security.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        /* 'Home' is available when in some kind of work flow such as a wizard
         * resource edit or file download. We need to clean up a bit
         */
        SessionInfo info = this.getSessionInfo(request);
        CoreUtil.clearFlow(request);
        CoreUtil.removePageInterceptListener(request.getSession(), FileDownloadPageInterceptListener.INTERCEPT_ID);
        
        // Forward on to the appropriate place
        
        switch(info.getNavigationContext()) {
        case SessionInfo.MANAGEMENT_CONSOLE_CONTEXT:
            return new RedirectWithMessages(mapping.findForward("managementConsole"), request);
        }
        return new RedirectWithMessages(mapping.findForward("userConsole"), request);
    }

    /* (non-Javadoc)
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
    }
}
