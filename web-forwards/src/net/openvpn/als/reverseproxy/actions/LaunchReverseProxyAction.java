
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
			
package net.openvpn.als.reverseproxy.actions;

import java.net.URL;
import java.util.Collection;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import net.openvpn.als.boot.HostService;
import net.openvpn.als.boot.Util;
import net.openvpn.als.core.CoreEvent;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.core.ServletRequestAdapter;
import net.openvpn.als.core.ServletResponseAdapter;
import net.openvpn.als.core.actions.AuthenticatedAction;
import net.openvpn.als.core.stringreplacement.VariableReplacement;
import net.openvpn.als.policyframework.LaunchSession;
import net.openvpn.als.policyframework.LaunchSessionFactory;
import net.openvpn.als.policyframework.NoPermissionException;
import net.openvpn.als.policyframework.ResourceAccessEvent;
import net.openvpn.als.properties.Property;
import net.openvpn.als.properties.impl.systemconfig.SystemConfigKey;
import net.openvpn.als.security.LogonControllerFactory;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.webforwards.ReverseProxyWebForward;
import net.openvpn.als.webforwards.WebForwardEventConstants;
import net.openvpn.als.webforwards.WebForwardPlugin;
import net.openvpn.als.webforwards.WebForwardTypeItem;
import net.openvpn.als.webforwards.WebForwardTypes;

/**
 * Implementation of {@link net.openvpn.als.core.actions.AuthenticatedAction}
 * that launches a <i>Reverse Proxy Web Forward</i>.
 */
public class LaunchReverseProxyAction extends AuthenticatedAction {

	/**
	 * Constructor.
	 * 
	 */
	public LaunchReverseProxyAction() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.core.actions.AuthenticatedAction#isIgnoreSessionLock()
	 */
	protected boolean isIgnoreSessionLock() {
		return true;
	}

	public ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
					throws Exception {
		// Get the web forward

		String launchId = request.getParameter(LaunchSession.LAUNCH_ID);
		if (Util.isNullOrTrimmedBlank(launchId)) {
			throw new Exception("No launch ID supplied.");
		}

		LaunchSession launchSession = LaunchSessionFactory.getInstance().getLaunchSession(launchId);
		ReverseProxyWebForward wf = (ReverseProxyWebForward) launchSession.getResource();
		
		/* Remove all other launch sessions for this resource, we can only ever have
		 * one at a time
		 */
		Collection<LaunchSession> sessions = LaunchSessionFactory.getInstance().getLaunchSessionsForType(launchSession.getSession(),
				WebForwardPlugin.WEBFORWARD_RESOURCE_TYPE);
		for (LaunchSession rs : sessions) {
			if (rs != launchSession && rs.getResource() instanceof ReverseProxyWebForward && rs.getResource().getResourceId() == wf.getResourceId()) {
				LaunchSessionFactory.getInstance().removeLaunchSession(rs);
			}
		}

		if (wf.getActiveDNS() && !isValidForActiveDNS(request.getServerName()))
			throw new Exception("Invalid host '" + request.getServerName() + "'; only FQDNs are valid for Active DNS forwarding");

		String path;
		String url = wf.getDestinationURL();
		String hostField = request.getHeader("Host");
		HostService hostService = hostField == null ? null : new HostService(hostField);
		SessionInfo session = getSessionInfo(request);

		try {
			launchSession.checkAccessRights(null, session);

			/* 
			 * This requires more thought.
			 * 
			 * 1. We can only have on launch session per resource
			 * 2. This doesn't take into account other features of reverse proxy
			 *    (authentication, encoding, host headers etc)
			 * 
			 */
			
			/**
			 * Setup other reverse proxies so they have access to each other. Only 
			 * reverse proxies with the same policy attached will be allowed.
			List resources = ResourceUtil.getGrantedResource(launchSession.getSession(), WebForwardPlugin.WEBFORWARD_RESOURCE_TYPE);
			
			Resource resource;
			for(Iterator it = resources.iterator(); it.hasNext();) {
				resource = (Resource) it.next();
				if(resource instanceof ReverseProxyWebForward && resource.getResourceId()!=launchSession.getResource().getResourceId()) {
					if(PolicyDatabaseFactory.getInstance().isResourceAttachedToPolicy(resource, launchSession.getPolicy(), launchSession.getSession().getRealm())) {
						LaunchSession ls = LaunchSessionFactory.getInstance().createLaunchSession(launchSession.getSession(), resource, launchSession.getPolicy());
						ls.checkAccessRights(null, session);
					}
					
				}
			}
			 */
			
			VariableReplacement r = new VariableReplacement();
			r.setServletRequest(request);
			r.setLaunchSession(launchSession);
			url = r.replace(url);

			CoreEvent evt = new ResourceAccessEvent(this,
                            WebForwardEventConstants.WEB_FORWARD_STARTED,
							wf,
							launchSession.getPolicy(),
							launchSession.getSession(),
							CoreEvent.STATE_SUCCESSFUL).addAttribute(WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_TYPE,
				((WebForwardTypeItem) WebForwardTypes.WEB_FORWARD_TYPES.get(wf.getType())).getName())
							.addAttribute(WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_URL, url);

			CoreServlet.getServlet().fireCoreEvent(evt);

			// Get the URL to redirect to
			if (wf.getActiveDNS()) {
				URL u = new URL(url);
				URL adu;
				if (Property.getPropertyInt(new SystemConfigKey("webforward.activeDNSFormat")) == 1) {
					adu = new URL("https", launchSession.getId() + "." + hostService.getHost(), hostService.getPort() == 0 ? -1
						: hostService.getPort(), u.getFile());
				} else {
					int idx = hostService.getHost().indexOf('.');
					adu = new URL("https",
									launchSession.getId() + "." + hostService.getHost().substring(idx + 1),
									hostService.getPort() == 0 ? -1 : hostService.getPort(),
									u.getFile());
				}
				path = adu.toExternalForm();

			} else if (wf.getHostHeader() != null && !wf.getHostHeader().equals("")) {
				URL u = new URL(url);

				URL adu = new URL("https", wf.getHostHeader(), hostService.getPort() == 0 ? -1 : hostService.getPort(), u.getFile());

				path = adu.toExternalForm();

				if (adu.getQuery() == null || adu.getQuery().equals("")) {
					path += "?" + LaunchSession.LAUNCH_ID + "=" + launchSession.getId();
				} else {
					path += "&" + LaunchSession.LAUNCH_ID + "=" + launchSession.getId();
				}

				/**
				 * Why do we need to use a JSP redirect? Because the new host
				 * will be created in a new session and we need the JSESSIONID
				 * which is only set once the first response has been returned
				 * to the browser. This redirect allows the browser to load a
				 * page on the new host and set the session cookie before an
				 * automatic redirect takes the user to the correct reverse
				 * proxy page.
				 */
				URL adu2 = new URL("https",
						/** 
						 * LDP Not sure why this was using hostService.getHost because my comment above
						 * clearly indicates that we have to redirect from the new host
						 */
						wf.getHostHeader(),
						hostService.getPort() == 0 ? -1 : hostService.getPort(),
						"/reverseProxyRedirect.jsp?redirectURL=" + Util.urlEncode(path));

				return new ActionForward(adu2.toExternalForm(), true);

			} else {
				URL u = new URL(url);
				path = u.getPath();
				if (u.getQuery() == null || u.getQuery().equals("")) {
					path += "?" + LaunchSession.LONG_LAUNCH_ID + "=" + launchSession.getId();
				} else {
					path += "?" + u.getQuery() + "&" + LaunchSession.LONG_LAUNCH_ID + "=" + launchSession.getId();
				}
				
				URL redir = new URL("https",
					hostService.getHost(),
					hostService.getPort() == 0 ? -1 : hostService.getPort(),
					path);
				path = redir.toExternalForm();
			}
		} catch (NoPermissionException npe) {

			CoreEvent evt = new ResourceAccessEvent(this,
                            WebForwardEventConstants.WEB_FORWARD_STARTED,
							wf,
							launchSession.getPolicy(),
							launchSession.getSession(),
							npe).addAttribute(WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_TYPE,
				((WebForwardTypeItem) WebForwardTypes.WEB_FORWARD_TYPES.get(wf.getType())).getName())
							.addAttribute(WebForwardEventConstants.EVENT_ATTR_WEB_FORWARD_URL, url);
			CoreServlet.getServlet().fireCoreEvent(evt);

			throw npe;
		}

		return new ActionForward(path, true);
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.core.actions.AuthenticatedAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
	}

	private boolean isValidForActiveDNS(String host) {

		StringTokenizer tokens = new StringTokenizer(host, ".");
		if (tokens.countTokens() == 1)
			return false;

		boolean numerical = true;
		while (tokens.hasMoreTokens()) {
			String token = tokens.nextToken();

			try {
				int val = Integer.parseInt(token);

				if (val > 255) {
					numerical = false;
					break;
				}
			} catch (NumberFormatException ex) {
				numerical = false;
				break;
			}
		}

		return !numerical;

	}

}
