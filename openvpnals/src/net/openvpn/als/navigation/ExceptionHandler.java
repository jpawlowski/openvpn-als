
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.navigation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ExceptionConfig;

import net.openvpn.als.core.PopupException;
import net.openvpn.als.security.Constants;


/**
 * Catches all exceptions thrown by the controller and directs to the 
 * appropriate error page. 
 */
public class ExceptionHandler extends org.apache.struts.action.ExceptionHandler {

    final static Log log = LogFactory.getLog(ExceptionHandler.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ExceptionHandler#execute(java.lang.Exception,
     *      org.apache.struts.config.ExceptionConfig,
     *      org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(Exception ex, ExceptionConfig ae, ActionMapping mapping, ActionForm formInstance,
                                 HttpServletRequest request, HttpServletResponse response) throws ServletException {
        log.error("An error occured during action processing. ", ex);
        if (ex instanceof PopupException) {
            request.getSession().setAttribute(Constants.EXCEPTION, ex.getCause());
            return mapping.findForward("popupException");
        } else {
            request.getSession().setAttribute(Constants.EXCEPTION, ex);
            return mapping.findForward("exception");
        }
    }
}
