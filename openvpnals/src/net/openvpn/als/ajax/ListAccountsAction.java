
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
			
package net.openvpn.als.ajax;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ajaxtags.helpers.AjaxXmlBuilder;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.boot.ContextHolder;
import net.openvpn.als.boot.Util;
import net.openvpn.als.core.UserDatabaseManager;
import net.openvpn.als.install.forms.SelectUserDatabaseForm;
import net.openvpn.als.realms.Realm;
import net.openvpn.als.security.Constants;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.security.User;
import net.openvpn.als.security.UserDatabase;
import net.openvpn.als.wizard.AbstractWizardSequence;

/**
 * Implementation of {@link net.openvpn.als.ajax.AbstractAjaxXMLAction} that
 * returns an XML document containing a list of user accounts.
 * <p>
 * Two request parameters are supported. First, the optional <b>account</b>
 * which may contain a search string and secondly <b>maxRows</b> which is an
 * integer, defaults to 10 and determines the maximum number of results to
 * return. If <b>account</b> is not supplied, all accounts (up to the specified
 * maximum rows) will be returned.
 */
public class ListAccountsAction extends AbstractAjaxXMLAction {

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.ajax.AbstractAjaxAction#onAjaxRequest(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse,
     *      org.ajaxtags.helpers.AjaxXmlBuilder)
     */
    @SuppressWarnings("unchecked")
    protected void onAjaxRequest(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response,
                                 AjaxXmlBuilder builder) throws Exception {
        UserDatabase userDatabase;
        if (ContextHolder.getContext().isSetupMode()) {
            AbstractWizardSequence sequence = (AbstractWizardSequence) request.getSession().getAttribute(Constants.WIZARD_SEQUENCE);
            if (sequence == null) {
                log.error("No wizard sequence, cannot list users.");
                return;
            }
            userDatabase = (UserDatabase) sequence.getAttribute(SelectUserDatabaseForm.ATTR_USER_DATABASE_INSTANCE, null);
            if (userDatabase == null) {
                log.error("No user database, cannot list users.");
                return;
            }
        } else {
            SessionInfo sessionInfo = LogonControllerFactory.getInstance().getSessionInfo(request);
            if (sessionInfo == null) {
                throw new Exception("No authenticated.");
            }
            Realm realm = LogonControllerFactory.getInstance().getUser(request).getRealm();
            userDatabase = UserDatabaseManager.getInstance().getUserDatabase(realm);
        }

        String account = request.getParameter("account");
        String maxRows = request.getParameter("maxRows");
        int rows = Integer.parseInt(maxRows == null ? "10" : maxRows);
        String filter = (account == null ? "" : Util.urlDecode(account)) + "*";

        User[] users = userDatabase.listAllUsers(filter, rows);
        List<User> sortedUsers = new ArrayList<User>(Arrays.asList(users));
        Collections.sort(sortedUsers);
        for (User user : sortedUsers) {
            if (isPermitted(user)) {
                String encodedHtml = Util.encodeHTML(user.getPrincipalName());
                builder.addItem(encodedHtml, encodedHtml);
            }
        }
    }

    protected boolean isPermitted(User user) {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.SETUP_CONSOLE_CONTEXT;
    }
}