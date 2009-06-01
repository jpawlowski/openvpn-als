
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
			
package net.openvpn.als.tunnels.wizards.actions;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

import net.openvpn.als.boot.HostService;
import net.openvpn.als.core.CoreAttributeConstants;
import net.openvpn.als.core.CoreEvent;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.security.User;
import net.openvpn.als.tunnels.TransportType;
import net.openvpn.als.tunnels.Tunnel;
import net.openvpn.als.tunnels.TunnelDatabaseFactory;
import net.openvpn.als.tunnels.TunnelsEventConstants;
import net.openvpn.als.tunnels.wizards.forms.DefaultTunnelDetailsForm;
import net.openvpn.als.tunnels.wizards.forms.TunnelDetailsForm;
import net.openvpn.als.wizard.AbstractWizardSequence;
import net.openvpn.als.wizard.WizardActionStatus;
import net.openvpn.als.wizard.actions.AbstractWizardAction;
import net.openvpn.als.wizard.forms.AbstractWizardFinishForm;

public class TunnelFinishAction extends AbstractWizardAction {
    final static Log log = LogFactory.getLog(TunnelFinishAction.class);

    public TunnelFinishAction() {
        super();
    }

    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.USER_CONSOLE_CONTEXT | SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                    HttpServletResponse response) throws Exception {
        List actionStatus = new ArrayList();
        AbstractWizardSequence seq = getWizardSequence(request);
        SessionInfo info = this.getSessionInfo(request);
        User user = (User) seq.getAttribute(TunnelDetailsAction.ATTR_USER, null);
        String name = (String) seq.getAttribute(DefaultTunnelDetailsForm.ATTR_RESOURCE_NAME, null);
        String description = (String) seq.getAttribute(DefaultTunnelDetailsForm.ATTR_RESOURCE_DESCRIPTION, null);
        boolean favorite = ((Boolean) seq.getAttribute(DefaultTunnelDetailsForm.ATTR_FAVORITE, Boolean.FALSE)).booleanValue();
        int type = ((Integer) seq.getAttribute(TunnelDetailsForm.ATTR_TYPE, null)).intValue();
        String transport = (String) seq.getAttribute(TunnelDetailsForm.ATTR_TRANSPORT, "");
        int sourcePort = ((Integer) seq.getAttribute(TunnelDetailsForm.ATTR_SOURCE_PORT, null)).intValue();
        String destinationHost = (String) seq.getAttribute(TunnelDetailsForm.ATTR_DESTINATION_HOST, null);
        int destinationPort = ((Integer) seq.getAttribute(TunnelDetailsForm.ATTR_DESTINATION_PORT, null)).intValue();
        String sourceInterface = ((String) seq.getAttribute(TunnelDetailsForm.ATTR_SOURCE_INTERFACE, null));
        boolean autoStart = ((Boolean) seq.getAttribute(TunnelDetailsForm.ATTR_AUTO_START, null)).booleanValue();
        Tunnel tunnel = null;
        try {
            try {
                tunnel = TunnelDatabaseFactory.getInstance().createTunnel(user.getRealm().getRealmID(), name, description, type, autoStart, transport,
                                user.getPrincipalName(), sourcePort, new HostService(destinationHost, destinationPort),
                                sourceInterface);
                CoreServlet.getServlet().fireCoreEvent(
                                new CoreEvent(this, TunnelsEventConstants.CREATE_TUNNEL, null, info, CoreEvent.STATE_SUCCESSFUL)
                                                .addAttribute(CoreAttributeConstants.EVENT_ATTR_RESOURCE_NAME, name)
                                                .addAttribute(CoreAttributeConstants.EVENT_ATTR_RESOURCE_DESCRIPTION, description)
                                                .addAttribute(TunnelsEventConstants.EVENT_ATTR_TUNNEL_SOURCE_PORT,
                                                                Integer.toString(sourcePort)).addAttribute(
                                                                                TunnelsEventConstants.EVENT_ATTR_TUNNEL_DESTINATION,
                                                                String.valueOf(destinationHost + ":" + destinationPort))
                                                .addAttribute(TunnelsEventConstants.EVENT_ATTR_TUNNEL_TRANSPORT, transport)
                                                .addAttribute(
                                                                TunnelsEventConstants.EVENT_ATTR_TUNNEL_TYPE,
                                                                ((LabelValueBean) TransportType.TYPES.get(tunnel.getType()))
                                                                                .getLabel()));
            } catch (Exception e) {
                CoreServlet.getServlet().fireCoreEvent(
                                new CoreEvent(this, TunnelsEventConstants.CREATE_TUNNEL, null, info, CoreEvent.STATE_UNSUCCESSFUL)
                                                .addAttribute(CoreAttributeConstants.EVENT_ATTR_RESOURCE_NAME, name)
                                                .addAttribute(CoreAttributeConstants.EVENT_ATTR_RESOURCE_DESCRIPTION, description)
                                                .addAttribute(TunnelsEventConstants.EVENT_ATTR_TUNNEL_SOURCE_PORT,
                                                                Integer.toString(sourcePort)).addAttribute(
                                                                                TunnelsEventConstants.EVENT_ATTR_TUNNEL_DESTINATION,
                                                                String.valueOf(destinationHost + ":" + destinationPort))
                                                .addAttribute(TunnelsEventConstants.EVENT_ATTR_TUNNEL_TRANSPORT, transport)
                                                .addAttribute(
                                                                TunnelsEventConstants.EVENT_ATTR_TUNNEL_TYPE,
                                                                ((LabelValueBean) TransportType.TYPES.get(tunnel.getType()))
                                                                                .getLabel()));
                throw e;
            }
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_OK,
                            "tunnelWizard.tunnelFinish.status.profileCreated"));
        } catch (Exception e) {
            log.error("Failed to create profile.", e);
            actionStatus.add(new WizardActionStatus(WizardActionStatus.COMPLETED_WITH_ERRORS,
                            "tunnelWizard.tunnelFinish.status.failedToCreateProfile", e.getMessage()));
        }
        if (tunnel != null) {
            actionStatus.add(attachToPoliciesAndAddToFavorites("tunnelWizard.tunnelFinish", seq, tunnel, favorite, request));
        }
        ((AbstractWizardFinishForm) form).setActionStatus(actionStatus);
        return super.unspecified(mapping, form, request, response);
    }

    public ActionForward exit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        return cancel(mapping, form, request, response);
    }
}