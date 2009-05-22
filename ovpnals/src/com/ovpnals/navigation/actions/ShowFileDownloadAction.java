
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
			
package com.ovpnals.navigation.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.core.CoreUtil;
import com.ovpnals.core.FileDownloadPageInterceptListener;
import com.ovpnals.core.actions.DefaultAction;
import com.ovpnals.navigation.forms.FileDownloadForm;
import com.ovpnals.security.SessionInfo;

/**
 * Action to show the file download screen.
 */
public class ShowFileDownloadAction extends DefaultAction {

    /* (non-Javadoc)
     * @see com.ovpnals.core.actions.DefaultAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {        
        String id = request.getParameter("id");
        if(id == null) {
            throw new Exception("No download Id provided.");
        }
        FileDownloadPageInterceptListener l = (FileDownloadPageInterceptListener)
        	CoreUtil.getPageInterceptListenerById(request.getSession(), FileDownloadPageInterceptListener.INTERCEPT_ID);
        if(l == null) {
            throw new Exception(
                "The requested download is no longer available. Some types of " + 
                "content may only be downloaded once.");
        }
        ((FileDownloadForm)form).init(l.getDownload(Integer.parseInt(id)));
        return mapping.findForward("success");
    }

    /* (non-Javadoc)
     * @see com.ovpnals.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.ALL_CONTEXTS;
    }

}