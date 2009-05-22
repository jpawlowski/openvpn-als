
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

import com.ovpnals.boot.Util;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.core.actions.DefaultAction;
import com.ovpnals.navigation.forms.RedirectForm;
import com.ovpnals.security.Constants;
import com.ovpnals.security.SessionInfo;

/**
 * Simple action that is used to perform visible redirects (i.e. a page is
 * displayed to the user saying "You will be redirected to ..." or similar).
 * <p>
 * A meta refresh is used to perform
 */
public class RedirectAction extends DefaultAction {

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
		super.execute(mapping, form, request, response);
		String forwardTo = (String) request.getAttribute(Constants.REQ_ATTR_FORWARD_TO);
		if (forwardTo == null) {
			forwardTo = request.getParameter("forwardTo");
			if (forwardTo == null) {
				throw new Exception("No forwardTo parameter provided.");
			}
		}
		CoreUtil.checkSafeURI(forwardTo);
		String folder = (String) request.getAttribute(Constants.REQ_ATTR_FOLDER);
		if (folder == null) {
			folder = request.getParameter("folder");
		}
		String target = (String) request.getAttribute(Constants.REQ_ATTR_TARGET);
		if (target == null) {
			target = request.getParameter("target");
		}
		((RedirectForm) form).init(forwardTo, folder, target);
		
		// Workaround for IE7 not working with meta refresh tag
		String execOnLoad = (String) request.getAttribute(Constants.REQ_ATTR_EXEC_ON_LOAD);
		if (execOnLoad != null) {
			execOnLoad = execOnLoad + " ; setTimeout('doRedirect()', 3000);";
		}
		else {
			execOnLoad = "javascript: setTimeout('doRedirect()', 3000);";
		}		
		request.setAttribute(Constants.REQ_ATTR_EXEC_ON_LOAD, execOnLoad);
    	Util.noCache(response);		
		return mapping.findForward("display");
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
		return SessionInfo.ALL_CONTEXTS;
	}

}