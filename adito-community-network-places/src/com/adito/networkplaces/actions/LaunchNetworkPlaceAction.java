
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

import java.awt.Rectangle;

import com.adito.boot.Util;
import com.adito.core.WindowOpenJavascriptLink;
import com.adito.networkplaces.NetworkPlace;
import com.adito.networkplaces.NetworkPlaceItem;
import com.adito.networkplaces.NetworkPlacePlugin;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.Resource;
import com.adito.policyframework.actions.AbstractRedirectLaunchAction;
import com.adito.security.SessionInfo;

/**
 * Implementation of {@link com.adito.core.actions.AuthenticatedAction}
 * that launches a <i>Network Place</i>.
 */
public class LaunchNetworkPlaceAction extends AbstractRedirectLaunchAction {

    /**
     * Constructor.
     */
    public LaunchNetworkPlaceAction() {
    	super(NetworkPlacePlugin.NETWORK_PLACE_RESOURCE_TYPE, SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.actions.AuthenticatedAction#isIgnoreSessionLock()
     */
    protected boolean isIgnoreSessionLock() {
        return true;
    }

	protected String doPrepareLink(LaunchSession launchSession, String returnTo) {
		String launchUri = ((NetworkPlace)launchSession.getResource()).getLaunchUri(launchSession);
        return new WindowOpenJavascriptLink(launchUri, 
            "vfs_" + launchSession.getResource().getResourceId() + "_" + System.currentTimeMillis(),
            new Rectangle(20, 20, NetworkPlaceItem.WINDOW_WIDTH, NetworkPlaceItem.WINDOW_HEIGHT), true, false, false, true, true).toJavascript();
	}

	protected boolean isAgentRequired(Resource resource) {
		return false;
	}

	protected boolean isDirectLink(LaunchSession launchSession) {
		return false;
	}
}
