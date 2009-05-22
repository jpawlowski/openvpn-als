
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
			
package com.ovpnals.ajax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.boot.ContextHolder;
import com.ovpnals.boot.Util;
import com.ovpnals.core.UserDatabaseManager;
import com.ovpnals.realms.Realm;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.Role;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.security.User;
import com.ovpnals.security.UserDatabase;

/**
 * Implementation of {@link com.ovpnals.ajax.AbstractAjaxXMLAction} that
 * returns an XML document containing a list of roles.
 * <p>
 * Two request parameters are supported. First, the optional <b>role</b> which
 * may contain a search string and secondly <b>maxRows</b> which is an integer,
 * defaults to 10 and determines the maximum number of results to return. If
 * <b>account</b> is not supplied, all roles (up to the specified maximum rows)
 * will be returned.
 */
public class ListRolesAction extends AbstractAjaxXMLAction {

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.core.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @SuppressWarnings("unchecked")
    protected void onAjaxRequest(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
                                 AjaxXmlBuilder builder) throws Exception {
        UserDatabase userDatabase;
        if (ContextHolder.getContext().isSetupMode()) {
            userDatabase = UserDatabaseManager.getInstance().getDefaultUserDatabase();
        } else {
            Realm realm = LogonControllerFactory.getInstance().getUser(request).getRealm();
            userDatabase = UserDatabaseManager.getInstance().getUserDatabase(realm);
        }

        String role = request.getParameter("role");
        String maxRows = request.getParameter("maxRows");
        int rows = Integer.parseInt(maxRows == null ? "10" : maxRows);
        String filter = (role == null ? "" : Util.urlDecode(role)) + "*";

        Role[] roles = userDatabase.listAllRoles(filter, rows);
        List<Role> sortedRoles = new ArrayList<Role>(Arrays.asList(roles));
        Collections.sort(sortedRoles);
        for (Role foundRole : sortedRoles) {
            String encodedHtml = Util.encodeHTML(foundRole.getPrincipalName());
            builder.addItem(encodedHtml, encodedHtml);
        }
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
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }
}