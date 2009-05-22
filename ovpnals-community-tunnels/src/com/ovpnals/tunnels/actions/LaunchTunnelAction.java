
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

import com.ovpnals.boot.Util;
import com.ovpnals.policyframework.LaunchSession;
import com.ovpnals.policyframework.Resource;
import com.ovpnals.policyframework.actions.AbstractRedirectLaunchAction;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.tunnels.TunnelPlugin;

/**
 * Implementation of {@link com.ovpnals.core.actions.AuthenticatedAction}
 * that launches a <i>Network Place</i>.
 */
public class LaunchTunnelAction extends AbstractRedirectLaunchAction {

    /**
     * Constructor.
     */
    public LaunchTunnelAction() {
        super(TunnelPlugin.SSL_TUNNEL_RESOURCE_TYPE, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.policyframework.actions.AbstractRedirectLaunchAction#doPrepareLink(com.ovpnals.policyframework.LaunchSession,
     *      java.lang.String)
     */
    protected String doPrepareLink(LaunchSession launchSession, String returnTo) {
        return "startTunnel.do?launchId=" + launchSession.getId() + "&returnTo=" + Util.urlEncode(returnTo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.policyframework.actions.AbstractLaunchAction#isAgentRequired(com.ovpnals.policyframework.Resource)
     */
    protected boolean isAgentRequired(Resource resource) {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.policyframework.actions.AbstractRedirectLaunchAction#isDirectLink(com.ovpnals.policyframework.LaunchSession)
     */
    protected boolean isDirectLink(LaunchSession launchSession) {
        return true;
    }
}
