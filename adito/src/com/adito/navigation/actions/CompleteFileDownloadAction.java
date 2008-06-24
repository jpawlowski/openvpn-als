
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.adito.core.CoreUtil;
import com.adito.core.DownloadContent;
import com.adito.core.FileDownloadPageInterceptListener;
import com.adito.core.actions.DefaultAction;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;

/**
 * <p>Action invoked when the user clicks on the <strong>Close</strong> button
 * after a file download has completed.
 * 
 * <p>The queued download is removed at this point and the file may not be
 * downloaded again.
 */

public class CompleteFileDownloadAction extends DefaultAction {
    final static Log log = LogFactory.getLog(CompleteFileDownloadAction.class);

        /* (non-Javadoc)
         * @see com.adito.core.actions.DefaultAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
         */
        public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {        
        String id = request.getParameter("id");
        if (id == null) {
            return mapping.findForward("failed");
        }
        FileDownloadPageInterceptListener l = (FileDownloadPageInterceptListener) CoreUtil.getPageInterceptListenerById(request
                        .getSession(), FileDownloadPageInterceptListener.INTERCEPT_ID);
        if (l != null) {
            DownloadContent download = l.getDownload(Integer.parseInt(id));
            if(download == null) {
                log.warn("Expected download " + id + " to be available but it wasn't");
                return mapping.findForward("failed");
            }
            else {
                download.completeDownload(LogonControllerFactory.getInstance().getSessionInfo(request));
                l.removeDownload(download.getId());
                if(l.size() == 0) {
                    CoreUtil.removePageInterceptListener(request.getSession(), l);
                }
                ActionForward fwd = download.getForward();
                if(fwd != null) {
                    return fwd;
                }   
            }
        }
        return mapping.findForward("success");
    }

    /* (non-Javadoc)
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.ALL_CONTEXTS;
    }
    
}