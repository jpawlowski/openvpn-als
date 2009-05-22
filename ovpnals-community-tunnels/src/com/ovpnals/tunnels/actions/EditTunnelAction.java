
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
			
package com.ovpnals.tunnels.actions;

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

import com.ovpnals.policyframework.Resource;
import com.ovpnals.policyframework.actions.AbstractResourceDispatchAction;
import com.ovpnals.policyframework.forms.AbstractResourceForm;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.tunnels.Tunnel;
import com.ovpnals.tunnels.TunnelDatabaseFactory;
import com.ovpnals.tunnels.TunnelPlugin;
import com.ovpnals.tunnels.forms.TunnelForm;

/**
 * Implementation of
 * {@link com.ovpnals.policyframework.actions.AbstractResourceDispatchAction}
 * that handles the updating of <i>SSL Tunnels</i>. These work slightly
 * differently to the other OpenVPN-ALS resources as communication occurs with
 * the VPN client.
 */
public class EditTunnelAction extends AbstractResourceDispatchAction {

    static Log log = LogFactory.getLog(EditTunnelAction.class);

    /**
     * Constructor
     */
    public EditTunnelAction() {
        super(TunnelPlugin.SSL_TUNNEL_RESOURCE_TYPE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.policyframework.actions.AbstractResourceDispatchAction#edit(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return super.edit(mapping, form, request, response);
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
     * @see com.ovpnals.policyframework.actions.AbstractResourceDispatchAction#createResource(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public Resource createResource(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return null;
    }

    /* (non-Javadoc)
     * @see com.ovpnals.policyframework.actions.AbstractResourceDispatchAction#commit(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward commit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        ActionMessages msgs = new ActionMessages();
        msgs.add(Globals.MESSAGE_KEY, new ActionMessage("editTunnel.message.saved"));
        this.addMessages(request, msgs);
        return super.commit(mapping, form, request, response);
    }
    
    /* (non-Javadoc)
     * @see com.ovpnals.policyframework.actions.AbstractResourceDispatchAction#commitCreatedResource(org.apache.struts.action.ActionMapping, com.ovpnals.policyframework.forms.AbstractResourceForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected Resource commitCreatedResource(ActionMapping mapping, AbstractResourceForm form, HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        TunnelForm tf = (TunnelForm) form;
        Tunnel tunnel = (Tunnel) tf.getResource();

        return TunnelDatabaseFactory.getInstance().createTunnel(tunnel.getRealmID(), tunnel.getResourceName(),
            tunnel.getResourceDescription(), tunnel.getType(), tunnel.isAutoStart(), tunnel.getTransport(), tunnel.getUsername(),
            tunnel.getSourcePort(), tunnel.getDestination(), tunnel.getSourceInterface());
    }
}