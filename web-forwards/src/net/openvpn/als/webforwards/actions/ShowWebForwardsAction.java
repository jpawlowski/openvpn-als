
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
			
package net.openvpn.als.webforwards.actions;

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

import net.openvpn.als.boot.HostService;
import net.openvpn.als.policyframework.ResourceUtil;
import net.openvpn.als.policyframework.actions.AbstractFavoriteResourcesDispatchAction;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.webforwards.WebForwardDatabaseFactory;
import net.openvpn.als.webforwards.WebForwardPlugin;
import net.openvpn.als.webforwards.forms.WebForwardsForm;

/**
 * Implementation of {@link net.openvpn.als.policyframework.actions.AbstractFavoriteResourcesDispatchAction}
 * that lists all assigned or manageable <i>Web Forwards</i>.
 */
public class ShowWebForwardsAction extends AbstractFavoriteResourcesDispatchAction {

    final static Log log = LogFactory.getLog(ShowWebForwardsAction.class);

    /**
     * Constructor
     */
    public ShowWebForwardsAction() {
        super(WebForwardPlugin.WEBFORWARD_RESOURCE_TYPE, WebForwardPlugin.WEBFORWARD_RESOURCE_TYPE);
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
        String hostField = request.getHeader("Host");
        ((WebForwardsForm) form).initialise(getSessionInfo(request).getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT ? 
                        WebForwardDatabaseFactory.getInstance().getWebForwards(getSessionInfo(request).getUser().getRealm().getRealmID()) : 
                            ResourceUtil.getGrantedResource(getSessionInfo(request), getResourceType()), hostField == null ? null : new HostService(hostField), this.getSessionInfo(request));
        ((WebForwardsForm) form).checkSelectedView(request, response);
        return fwd;
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
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.policyframework.actions.AbstractResourcesDispatchAction#remove(org.apache.struts.action.ActionMapping,
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