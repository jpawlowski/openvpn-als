
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
			
package com.adito.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.core.GlobalWarning.DismissType;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.ResourceType;
import com.adito.security.Constants;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.security.User;

/**
 * Manages global warnings.
 */
public class GlobalWarningManager implements CoreListener {

	final static Log log = LogFactory.getLog(GlobalWarningManager.class);

	private static GlobalWarningManager instance;

	private final Map<String, List<User>> dismissedWarnings = new HashMap<String, List<User>>();

	private GlobalWarningManager() {
		CoreServlet.getServlet().addCoreListener(this);
	}

	/**
	 * Get an instance of the global warning manager.
	 * 
	 * @return instance
	 */
	public static GlobalWarningManager getInstance() {
		if (instance == null) {
			instance = new GlobalWarningManager();
		}
		return instance;
	}

	/**
	 * Add a new global message that will be displayed to all users that have
	 * the specified permissions (type
	 * {@link GlobalWarning#USERS_WITH_PERMISSIONS}.
	 * 
	 * @param warning global warning
	 */
	@SuppressWarnings("unchecked")
	public void addGlobalWarningForUsersWithPermissions(GlobalWarning warning) {
		if (warning.getType() != GlobalWarning.USERS_WITH_PERMISSIONS) {
			throw new IllegalArgumentException("This method may only be used with global warnings of type USERS_WITH_PERMISSIONS");
		}
		HttpSession servletSession;
		for (Iterator i = CoreRequestProcessor.getSessions().entrySet().iterator(); i.hasNext();) {
			servletSession = (HttpSession) ((Map.Entry) i.next()).getValue();
			SessionInfo info = LogonControllerFactory.getInstance().getSessionInfo(servletSession);
			if (info != null) {
				try {
					if (PolicyDatabaseFactory.getInstance().isPermitted(warning.getRequiredResourceType(),
						warning.getRequiredPermissions(),
						info.getUser(),
						false)) {
						populateSession(servletSession, warning);
					}
				} catch (Exception e) {
					CoreUtil.log.error("Failed to add global warning. ", e);
				}
			}
		}
		List<GlobalWarning> servletContextGlobalWarnings = (List<GlobalWarning>) CoreServlet.getServlet()
						.getServletContext()
						.getAttribute(Constants.CONTEXT_GLOBAL_WARNINGS);
		if (servletContextGlobalWarnings == null) {
			servletContextGlobalWarnings = new ArrayList<GlobalWarning>();
		}
		servletContextGlobalWarnings.add(warning);
		CoreServlet.getServlet().getServletContext().setAttribute(Constants.CONTEXT_GLOBAL_WARNINGS, servletContextGlobalWarnings);
	}

	/**
	 * Add a new global message for types {@link GlobalWarning#SUPER_USER},
	 * {@link GlobalWarning#MANAGEMENT_USERS} or {@link GlobalWarning#ALL_USERS}.
	 * 
	 * @param warning global warning
	 * @throws IllegalArgumentException if incorrect type
	 * @see GlobalWarning
	 */
	@SuppressWarnings("unchecked")
	public void addMultipleGlobalWarning(GlobalWarning warning) {
		HttpSession servletSession = null;
		for (Iterator i = CoreRequestProcessor.getSessions().entrySet().iterator(); i.hasNext();) {
			servletSession = (HttpSession) ((Map.Entry) i.next()).getValue();
			SessionInfo info = LogonControllerFactory.getInstance().getSessionInfo(servletSession);
			if (info != null) {
				try {
					if ((warning.getType() == GlobalWarning.SUPER_USER && LogonControllerFactory.getInstance()
									.isAdministrator(info.getUser())) || (warning.getType() == GlobalWarning.MANAGEMENT_USERS && PolicyDatabaseFactory.getInstance()
									.isAnyAccessRightAllowed(info.getUser(), true, true, false))
						|| warning.getType() == GlobalWarning.ALL_USERS) {
						populateSession(servletSession, warning);
					}
				} catch (Exception e) {
					CoreUtil.log.error("Failed to add global warning.", e);
				}
			}
		}
		List<GlobalWarning> servletContextGlobalWarnings = (List<GlobalWarning>) CoreServlet.getServlet()
						.getServletContext()
						.getAttribute(Constants.CONTEXT_GLOBAL_WARNINGS);
		if (servletContextGlobalWarnings == null) {
			servletContextGlobalWarnings = new ArrayList<GlobalWarning>();
		}
		servletContextGlobalWarnings.add(warning);
		CoreServlet.getServlet().getServletContext().setAttribute(Constants.CONTEXT_GLOBAL_WARNINGS, servletContextGlobalWarnings);
	}

	/**
	 * Add a new global message for type {@link GlobalWarning#SINGLE_SESSION}.
	 * The message will only exist for the lifetime of the session.
	 * 
	 * @param warning warning
	 * @throws IllegalArgumentException if incorrect type
	 * @see GlobalWarning
	 */
	@SuppressWarnings("unchecked")
	public void addToSession(GlobalWarning warning) {
		if (warning.getType() != GlobalWarning.SINGLE_SESSION) {
			throw new IllegalArgumentException("This method may only be used with global warnings of type USERS_WITH_PERMISSIONS");
		}
		populateSession(warning.getSession(), warning);
	}

	/**
	 * Dismiss a global warning.
	 * 
	 * @param key message key
	 * @param servletSession session
	 */
	public void dismissGlobalWarning(HttpSession servletSession, String key) {
		List<GlobalWarning> globalWarnings = (List<GlobalWarning>) servletSession.getAttribute(Constants.SESSION_GLOBAL_WARNINGS);
		for (GlobalWarning warning : new ArrayList<GlobalWarning>(globalWarnings)) {
			if (warning.getMessage().getKey().equals(key)) {
				if (warning.getDismissType() == DismissType.DISMISS_FOR_SESSION) {
					removeGlobalWarning(servletSession, key);
				} else if (warning.getDismissType() == DismissType.DISMISS_FOR_USER) {
					SessionInfo sessionInfo = LogonControllerFactory.getInstance().getSessionInfo(servletSession);
					if (sessionInfo == null) {
						throw new IllegalArgumentException("No session info for servlet session.");
					}
					synchronized (dismissedWarnings) {
						List<User> users = dismissedWarnings.get(key);
						if (users == null) {
							users = new ArrayList<User>();
							dismissedWarnings.put(key, users);
						}
						if (!users.contains(sessionInfo.getUser())) {
							users.add(sessionInfo.getUser());
						}
					}
					removeGlobalWarning(servletSession, key);
				} else {
					throw new IllegalArgumentException("This global warning cannot be dismissed.");
				}
			}
		}
	}

	/**
	 * Remove a global warning givens its message resource key. If a
	 * <code>null</code> servlet session is provided then the global warning
	 * will be removed from all sessions
	 * 
	 * @param servletSession session to remove warning from or <code>null</code>
	 *        for all sessions
	 * @param key message resource key
	 */
	public void removeGlobalWarning(HttpSession servletSession, String key) {
		if (servletSession == null) {
			for (Iterator i = CoreRequestProcessor.getSessions().entrySet().iterator(); i.hasNext();) {
				servletSession = (HttpSession) ((Map.Entry) i.next()).getValue();
				removeGlobalWarning(servletSession, key);
			}
			List servletContextGlabalWarnings = (List) CoreServlet.getServlet()
							.getServletContext()
							.getAttribute(Constants.CONTEXT_GLOBAL_WARNINGS);
			if (servletContextGlabalWarnings != null) {
				Iterator iter = servletContextGlabalWarnings.iterator();
				while (iter.hasNext()) {
					GlobalWarning gw = (GlobalWarning) iter.next();
					BundleActionMessage element = gw.getMessage();
					if (element.getKey().equals(key)) {
						servletContextGlabalWarnings.remove(element);
						break;
					}
				}
			}
		} else {
			synchronized (servletSession) {
				List<GlobalWarning> l = (List<GlobalWarning>) servletSession.getAttribute(Constants.SESSION_GLOBAL_WARNINGS);
				if (l == null) {
					l = new ArrayList<GlobalWarning>();
					servletSession.setAttribute(Constants.SESSION_GLOBAL_WARNINGS, l);
				}
				for (int i = l.size() - 1; i >= 0; i--) {
					GlobalWarning gw = (GlobalWarning) l.get(i);
					if (gw.getMessage().getKey().equals(key)) {
						l.remove(i);
						break;
					}
				}
			}
		}
	}

	/**
	 * Convenience method to Remove all global warnings with the specified key
	 * from all active sessions
	 * 
	 * @param key message resources key of global warning
	 */
	public void removeGlobalWarningFromAllSessions(String key) {
		removeGlobalWarning(null, key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.core.CoreListener#coreEvent(com.adito.core.CoreEvent)
	 */
	@SuppressWarnings("unchecked")
	public void coreEvent(CoreEvent evt) {
		if (evt.getId() == CoreEventConstants.LOGON) {
			SessionInfo sessionInfo = evt.getSessionInfo();
			if (sessionInfo != null && sessionInfo.getHttpSession() != null) {
				// Only add warnings to web sessions
				HttpSession session = sessionInfo.getHttpSession();
				User user = sessionInfo.getUser();
				List<GlobalWarning> globalWarnings = (List<GlobalWarning>) session.getServletContext()
								.getAttribute(Constants.CONTEXT_GLOBAL_WARNINGS);
				if (globalWarnings != null) {
					for (GlobalWarning gw : globalWarnings) {
						List<User> dismissedByUsers = dismissedWarnings.get(gw.getMessage().getKey());
						if (dismissedByUsers == null || !dismissedByUsers.contains(user)) {
							try {
								if (gw.getType() == GlobalWarning.SUPER_USER && LogonControllerFactory.getInstance()
												.isAdministrator(user)) {
									populateSession(session, gw);
								} else if (gw.getType() == GlobalWarning.MANAGEMENT_USERS && PolicyDatabaseFactory.getInstance()
												.isAnyAccessRightAllowed(user, true, true, false)) {
									populateSession(session, gw);
								} else if (gw.getType() == GlobalWarning.USERS_WITH_PERMISSIONS && PolicyDatabaseFactory.getInstance()
												.isPermitted(gw.getRequiredResourceType(), gw.getRequiredPermissions(), user, false)) {
									populateSession(session, gw);
								} else if (gw.getType() == GlobalWarning.ALL_USERS) {
									populateSession(session, gw);
								}
							} catch (Exception e) {
								log.error("Failed to add global message. ", e);
							}
						}
					}
				}
			}
		}

	}

	private void populateSession(HttpSession servletSession, GlobalWarning warning) {
		synchronized (servletSession) {
			SessionInfo info = LogonControllerFactory.getInstance().getSessionInfo(servletSession);
			if (info != null) {
				List<GlobalWarning> l = (List<GlobalWarning>) servletSession.getAttribute(Constants.SESSION_GLOBAL_WARNINGS);
				if (l == null) {
					l = new ArrayList<GlobalWarning>();
					servletSession.setAttribute(Constants.SESSION_GLOBAL_WARNINGS, l);
				}
				GlobalWarning m = null;
				boolean found = false;
				for (Iterator<GlobalWarning> i = l.iterator(); !found && i.hasNext();) {
					m = i.next();
					if (m.getMessage().getBundle().equals(warning.getMessage().getBundle()) && m.getMessage()
									.getKey()
									.equals(warning.getMessage().getKey())) {
						found = true;
					}
				}
				if (!found) {
					l.add(warning);
				}
			}
		}
	}
}
