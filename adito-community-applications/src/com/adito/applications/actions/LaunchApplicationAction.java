
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
			
package com.adito.applications.actions;

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

import com.adito.applications.ApplicationLauncherType;
import com.adito.applications.ApplicationShortcut;
import com.adito.applications.ApplicationShortcutEventConstants;
import com.adito.applications.ApplicationsPlugin;
import com.adito.core.BundleActionMessage;
import com.adito.core.CoreAttributeConstants;
import com.adito.core.CoreEvent;
import com.adito.core.CoreServlet;
import com.adito.core.RedirectWithMessages;
import com.adito.extensions.ExtensionDescriptor;
import com.adito.extensions.ExtensionBundle.ExtensionBundleStatus;
import com.adito.extensions.store.ExtensionStore;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.Resource;
import com.adito.policyframework.ResourceAccessEvent;
import com.adito.policyframework.actions.AbstractLaunchAction;
import com.adito.security.SessionInfo;

/**
 * Implementation of {@link com.adito.core.actions.AuthenticatedAction}
 * that launches an <i>Application Shortcut</i>.
 * <p>
 * The actual launch is delegated to the extension type (which must be a
 * {@link ApplicationLauncherType}.
 */
public class LaunchApplicationAction extends AbstractLaunchAction {

	final static Log log = LogFactory.getLog(LaunchApplicationAction.class);

	/**
	 * Constructor.
	 * 
	 */
	public LaunchApplicationAction() {
		super(ApplicationsPlugin.APPLICATION_SHORTCUT_RESOURCE_TYPE,
						SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.policyframework.actions.AbstractLaunchAction#isAgentRequired(com.adito.policyframework.Resource)
	 */
	protected boolean isAgentRequired(Resource resource) {
		ApplicationShortcut shortcut = (ApplicationShortcut) resource;
		try {
			ExtensionDescriptor descriptor = ExtensionStore.getInstance().getExtensionDescriptor(shortcut.getApplication());
			return ((ApplicationLauncherType) descriptor.getExtensionType()).isAgentRequired(shortcut, descriptor);
		} catch (Exception e) {
			log.error("Failed to determine if agent is required. Assuming not.");
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.policyframework.actions.AbstractLaunchAction#launch(org.apache.struts.action.ActionMapping,
	 *      com.adito.policyframework.LaunchSession,
	 *      javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	protected ActionForward launch(ActionMapping mapping, LaunchSession launchSession, HttpServletRequest request, String returnTo)
					throws Exception {
		ApplicationShortcut shortcut = (ApplicationShortcut) launchSession.getResource();
		ExtensionDescriptor descriptor = ExtensionStore.getInstance().getExtensionDescriptor(shortcut.getApplication());
		HashMap<String, String> parameters = new HashMap<String, String>();
		Enumeration e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String nameParam = (String) e.nextElement();
			parameters.put(nameParam, request.getParameter(nameParam));
		}

		// Do the launch
		try {
			if(descriptor.getApplicationBundle().getStatus() != ExtensionBundleStatus.ACTIVATED) {
				throw new Exception("Extension bundle " + descriptor.getApplicationBundle().getId() +" is not activated, cannot launch applicaiton.");
			}
			ActionForward fwd = ((ApplicationLauncherType) descriptor.getExtensionType()).launch(parameters,
				descriptor,
				shortcut,
				mapping,
				launchSession,
				returnTo,
				request);

			CoreServlet.getServlet().fireCoreEvent(new ResourceAccessEvent(this,
					ApplicationShortcutEventConstants.APPLICATION_SHORTCUT_LAUNCHED,
							launchSession.getResource(),
							launchSession.getPolicy(),
							launchSession.getSession(),
							CoreEvent.STATE_SUCCESSFUL).addAttribute(CoreAttributeConstants.EVENT_ATTR_APPLICATION_NAME,
				descriptor.getName()).addAttribute(CoreAttributeConstants.EVENT_ATTR_APPLICATION_ID, descriptor.getId()));

			/*
			 * If the launch implementation returns its own forward, it is
			 * reponsible for setting up its 'launched' message
			 */
			if (fwd == null) {
				ActionMessages msgs = new ActionMessages();
				msgs.add(Globals.MESSAGE_KEY, new BundleActionMessage(ApplicationsPlugin.MESSAGE_RESOURCES_KEY,
								"launchApplication.launched",
								shortcut.getResourceName()));
				saveMessages(request, msgs);
				return new RedirectWithMessages(returnTo, request);
			}
			return fwd;
		} catch (Exception ex) {
			CoreServlet.getServlet().fireCoreEvent(new ResourceAccessEvent(this,
					ApplicationShortcutEventConstants.APPLICATION_SHORTCUT_LAUNCHED,
							launchSession.getSession(),
							ex).addAttribute(CoreAttributeConstants.EVENT_ATTR_APPLICATION_NAME, descriptor.getName())
							.addAttribute(CoreAttributeConstants.EVENT_ATTR_APPLICATION_ID, descriptor.getId()));
			throw ex;

		}
	}
}
