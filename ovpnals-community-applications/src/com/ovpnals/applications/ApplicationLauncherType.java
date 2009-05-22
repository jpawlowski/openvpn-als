
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
			
package com.ovpnals.applications;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.extensions.ExtensionDescriptor;
import com.ovpnals.extensions.ExtensionException;
import com.ovpnals.extensions.ExtensionType;
import com.ovpnals.policyframework.LaunchSession;

/**
 * Extension of {@link ExtensionType} for extensions that provide <i>Application Extension</i>
 * that may have <i>Application Shortcuts</i> created for them and may be <i>Launched</i>
 * by a user.
 */
public interface ApplicationLauncherType extends ExtensionType {

	/**
	 * Launch the application.
	 * 
	 * @param parameters shortcut parameters
	 * @param descriptor extension descriptor
	 * @param shortcut shortcut
	 * @param mapping mapping
	 * @param launchSession launch session
	 * @param returnTo forward to return to or <code>null</code> to return to default
	 * @return forward or <code>null</code> for default redirect after launch
	 * @throws ExtensionException
	 */
	public ActionForward launch(Map<String, String> parameters, ExtensionDescriptor descriptor, ApplicationShortcut shortcut, ActionMapping mapping, LaunchSession launchSession, String returnTo, HttpServletRequest request)
					throws ExtensionException;

	/**
	 * Get if the agent is required for this launch.
	 * 
	 * @param shortcut shortcut
	 * @param descriptor descriptor
	 * @return agent required
	 */
	public boolean isAgentRequired(ApplicationShortcut shortcut, ExtensionDescriptor descriptor);

	/**
	 * Get if the this launcher is a server side launchers.
	 * 
	 * @return server side
	 */
	public boolean isServerSide();

}
