
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
			
package com.adito.networkplaces.actions;

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

import com.adito.networkplaces.NetworkPlacePlugin;
import com.adito.networkplaces.forms.ShowNetworkPlacesForm;
import com.adito.policyframework.actions.AbstractFavoriteResourcesDispatchAction;
import com.adito.security.SessionInfo;
import com.adito.vfs.webdav.DAVProcessor;
import com.adito.vfs.webdav.DAVServlet;

/**
 * Implementation of {@link com.adito.policyframework.actions.AbstractFavoriteResourcesDispatchAction}
 * that lists all of the configured <i>Network Places</i>
 */
public class ShowNetworkPlacesDispatchAction extends AbstractFavoriteResourcesDispatchAction {

    static Log log = LogFactory.getLog(ShowNetworkPlacesDispatchAction.class);

    /**
     * Constructor
     */
    public ShowNetworkPlacesDispatchAction() {
        super(NetworkPlacePlugin.NETWORK_PLACE_RESOURCE_TYPE, NetworkPlacePlugin.NETWORK_PLACE_RESOURCE_TYPE);
    }

    /* (non-Javadoc)
     * @see org.apache.struts.actions.DispatchAction#unspecified(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        ActionForward fwd = super.unspecified(mapping, form, request, response);
        ShowNetworkPlacesForm showNetworkPlacesForm = (ShowNetworkPlacesForm) form;
        DAVProcessor processor = DAVServlet.getDAVProcessor(request);
        showNetworkPlacesForm.initialize(processor, this.getSessionInfo(request));
        showNetworkPlacesForm.checkSelectedView(request, response);
        return fwd;
    }

    /* (non-Javadoc)
     * @see com.adito.policyframework.actions.AbstractResourcesDispatchAction#remove(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward remove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        super.remove(mapping, form, request, response);
        ActionMessages msgs = new ActionMessages();
        msgs.add(Globals.MESSAGE_KEY, new ActionMessage("networkPlaces.message.deleted"));
        this.addMessages(request, msgs);
        return mapping.findForward("refresh");
    }

    /* (non-Javadoc)
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
    }
}