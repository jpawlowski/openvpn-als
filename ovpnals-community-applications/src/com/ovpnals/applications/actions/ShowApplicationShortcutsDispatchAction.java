
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
			
package com.ovpnals.applications.actions;

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

import com.ovpnals.applications.ApplicationShortcutDatabaseFactory;
import com.ovpnals.applications.ApplicationsPlugin;
import com.ovpnals.applications.forms.ApplicationShortcutsForm;
import com.ovpnals.policyframework.ResourceUtil;
import com.ovpnals.policyframework.actions.AbstractFavoriteResourcesDispatchAction;
import com.ovpnals.security.SessionInfo;

/**
 * Implementation of a
 * {@link com.ovpnals.policyframework.actions.AbstractFavoriteResourcesDispatchAction}
 * that provides a list of <i>Application Shortucts</i>, one of OpenVPN-ALS's
 * main resource types.
 */
public class ShowApplicationShortcutsDispatchAction extends AbstractFavoriteResourcesDispatchAction {

    final static Log log = LogFactory.getLog(ShowApplicationShortcutsDispatchAction.class);

    /**
     * Constructor
     */
    public ShowApplicationShortcutsDispatchAction() {
        super(ApplicationsPlugin.APPLICATION_SHORTCUT_RESOURCE_TYPE, ApplicationsPlugin.APPLICATION_SHORTCUT_RESOURCE_TYPE);
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
        ActionForward fwd = super.unspecified(mapping, form, request, response);
        List resources = getSessionInfo(request).getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT ? ApplicationShortcutDatabaseFactory
                        .getInstance().getShortcuts(getSessionInfo(request).getUser().getRealm().getRealmID())
                        : ResourceUtil.getGrantedResource(getSessionInfo(request), getResourceType());
        ((ApplicationShortcutsForm) form).initialise(resources, getSessionInfo(request), "name", request);
        ((ApplicationShortcutsForm) form).checkSelectedView(request, response);
        return fwd;
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
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.policyframework.actions.AbstractResourcesDispatchAction#remove(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ActionForward fwd = super.remove(mapping, form, request, response);
        ActionMessages msgs = new ActionMessages();
        msgs.add(Globals.MESSAGE_KEY, new ActionMessage("tunnels.message.tunnelsRemoved", "1"));
        saveMessages(request, msgs);
        return fwd;

    }
}