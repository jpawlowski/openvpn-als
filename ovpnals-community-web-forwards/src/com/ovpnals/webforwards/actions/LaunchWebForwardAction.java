
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
			
package com.ovpnals.webforwards.actions;

import java.awt.Rectangle;

import com.ovpnals.core.WindowOpenJavascriptLink;
import com.ovpnals.policyframework.LaunchSession;
import com.ovpnals.policyframework.Resource;
import com.ovpnals.policyframework.actions.AbstractRedirectLaunchAction;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.webforwards.WebForward;
import com.ovpnals.webforwards.WebForwardPlugin;

/**
 * Implementation of {@link com.ovpnals.core.actions.AuthenticatedAction}
 * that launches a <i>Network Place</i>.
 */
public class LaunchWebForwardAction extends AbstractRedirectLaunchAction {

    /**
     * Constructor.
     * 
     */
    public LaunchWebForwardAction() {
    	super(WebForwardPlugin.WEBFORWARD_RESOURCE_TYPE, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT);
    }

    protected String doPrepareLink(LaunchSession launchSession, String returnTo) {
    	WebForward webForward = (WebForward)launchSession.getResource();
    	String uri = webForward.getLaunchUri(launchSession);
        return new WindowOpenJavascriptLink(uri, "wf_" + webForward.getResourceId(), new Rectangle(0, 0, 800, 600), true, true,
                        true, true, true).toJavascript();
    }

	protected boolean isAgentRequired(Resource resource) {
		return ((WebForward)resource).getType() == WebForward.TYPE_TUNNELED_SITE;
	}

	protected boolean isDirectLink(LaunchSession launchSession) {
		return false;
	}
}
