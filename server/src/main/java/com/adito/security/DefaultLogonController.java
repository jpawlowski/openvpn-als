
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
			
package com.adito.security;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

import com.adito.boot.ContextHolder;
import com.adito.boot.DefaultPropertyDefinition;
import com.adito.boot.HostService;
import com.adito.boot.HttpConstants;
import com.adito.boot.PropertyClassManager;
import com.adito.boot.RequestHandlerRequest;
import com.adito.boot.RequestHandlerResponse;
import com.adito.boot.SystemProperties;
import com.adito.core.CoreAttributeConstants;
import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.core.CoreServlet;
import com.adito.core.CoreUtil;
import com.adito.core.PageInterceptException;
import com.adito.core.PageInterceptListener;
import com.adito.core.ServletRequestAdapter;
import com.adito.core.ServletResponseAdapter;
import com.adito.core.UserDatabaseManager;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.PolicyUtil;
import com.adito.policyframework.ResourceUtil;
import com.adito.properties.ProfilesFactory;
import com.adito.properties.ProfilesListDataSource;
import com.adito.properties.Property;
import com.adito.properties.PropertyProfile;
import com.adito.properties.impl.realms.RealmKey;
import com.adito.properties.impl.systemconfig.SystemConfigKey;
import com.adito.properties.impl.systemconfig.SystemConfiguration;
import com.adito.properties.impl.userattributes.UserAttributeKey;
import com.adito.realms.Realm;
import com.adito.security.actions.PromptForPrivateKeyPassphraseDispatchAction;
import com.adito.security.actions.UpdatePrivateKeyPassphraseDispatchAction;
import com.adito.setup.SystemInformationProvider;
import com.adito.setup.SystemInformationRegistry;
import com.adito.util.TicketGenerator;

/**
 * This class is the default implementation of the
 * {@link com.adito.security.LogonController} and maintains and validates
 * all logons to Adito whether the be through the web based user
 * interface or other sub-systems such as the <i>Embedded Client</i>.
 */
public class DefaultLogonController implements LogonController {
	protected static Log log = LogFactory.getLog(DefaultLogonController.class);
	private Map<String, SessionInfo> logons = new HashMap<String, SessionInfo>();
	Map<String, SessionInfo> logonsBySessionId = new HashMap<String, SessionInfo>();

	int sessionTimeoutBlockId;
	HashMap lockedUsers = new HashMap();
	List authenticationModules;
	HashMap authorizedTickets = new HashMap();

	/**
	 * Constructor.
	 */
	public DefaultLogonController() {
		lockedUsers = new HashMap();

		PropertyClassManager.getInstance()
						.getPropertyClass(SystemConfiguration.NAME)
						.registerPropertyDefinition(new DefaultPropertyDefinition(DefaultPropertyDefinition.TYPE_INTEGER,
										"security.maxUserCount",
										"",
										99999,
										"0",
										2,
										true));

		SystemInformationRegistry.getInstance().registerProvider(new MostUsersOnline());
		SystemInformationRegistry.getInstance().registerProvider(new CurrentUsersOnline());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.security.LogonController#init()
	 */
	public void init() {
	    new HorribleHackReaperThread();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.security.LogonController#isAdministrator(com.adito.policyframework.Principal)
	 */
	public boolean isAdministrator(User principal) {
		// In setup mode everyone is an administrator
		if (ContextHolder.getContext().isSetupMode()) {
			return true;
		}
		try {
			// Now check the default administrators
			if (principal == null) {
				log.error("NULL principal object passed to isAdministrator!");
				return false;
			}

			if (principal.getPrincipalName() == null) {
				log.error("NULL principal name in principal object passed to isAdministrator!");
				return false;
			}

			List administrators = Property.getPropertyList(new RealmKey("security.administrators", principal.getRealm()
							.getRealmID()));

			for (Iterator it = administrators.iterator(); it.hasNext();) {
				if (principal.getPrincipalName().equals((String) it.next()))
					return true;
			}
		} catch (Exception e) {
			log.error("Failed to determine administrator status.", e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.security.LogonController#addSessionTimeoutBlock(javax.servlet.http.HttpSession,
	 *      java.lang.String)
	 */
	public synchronized int addSessionTimeoutBlock(HttpSession session, String reason) {
		Map sessionTimeoutBlocks = (Map) session.getAttribute(Constants.SESSION_TIMEOUT_BLOCKS);
		if (sessionTimeoutBlocks == null) {
			sessionTimeoutBlocks = new HashMap();
			session.setAttribute(Constants.SESSION_TIMEOUT_BLOCKS, sessionTimeoutBlocks);
		}
		sessionTimeoutBlocks.put(String.valueOf(++sessionTimeoutBlockId), reason);
		if (log.isDebugEnabled())
			log.debug("Preventing session timeout on session " + session.getId()
				+ " (id of "
				+ sessionTimeoutBlockId
				+ ") because '"
				+ reason
				+ "'. There are now "
				+ sessionTimeoutBlocks.size()
				+ " reasons not to timeout the session.");
		session.setMaxInactiveInterval(-1);
		return sessionTimeoutBlockId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.security.LogonController#removeSessionTimeoutBlock(javax.servlet.http.HttpSession,
	 *      int)
	 */
	public synchronized void removeSessionTimeoutBlock(HttpSession session, int sessionTimeoutBlockId) {
		try {
			Map sessionTimeoutBlocks = (Map) session.getAttribute(Constants.SESSION_TIMEOUT_BLOCKS);
			if (sessionTimeoutBlocks != null) {
				String reason = (String) sessionTimeoutBlocks.get(String.valueOf(sessionTimeoutBlockId));
				if (reason == null) {
					log.warn("No session timeout block with id of " + sessionTimeoutBlockId);
				} else {
					sessionTimeoutBlocks.remove(String.valueOf(sessionTimeoutBlockId));
					if (log.isDebugEnabled())
						log.debug("Removing session timeout block " + sessionTimeoutBlockId
							+ " for session "
							+ session.getId()
							+ " ('"
							+ reason
							+ "'). There are now "
							+ sessionTimeoutBlocks.size()
							+ " reasons not to timeout the session.");
				}
				if (sessionTimeoutBlocks.size() == 0) {
					session.removeAttribute(Constants.SESSION_TIMEOUT_BLOCKS);
					User user = (User) session.getAttribute(Constants.USER);
					int minutes = CoreUtil.getUsersProfilePropertyIntOrDefault(session, "webServer.sessionInactivityTimeout", user);
					if (log.isDebugEnabled())
						log.debug("Initialising timeout for session " + session.getId() + " to " + minutes + " minutes");
					session.setMaxInactiveInterval(minutes == 0 ? -1 : minutes * 60);
				}
			}
		} catch (IllegalStateException ise) {
			log.warn("Couldnt remove session timeout block.", ise);
		}
	}

	public void logoffSession(HttpServletRequest request, HttpServletResponse response) throws SecurityErrorException {
		if (log.isInfoEnabled())
			log.info("Logging off session " + request.getSession().getId());
		if (request.getSession().getAttribute(Constants.LOGON_TICKET) == null) {
			throw new SecurityErrorException(SecurityErrorException.INTERNAL_ERROR, "The current session does not contain a logon ticket");
		} else {
			String ticket = (String) request.getSession().getAttribute(Constants.LOGON_TICKET);
			SessionInfo session = getSessionInfo(ticket);
			logoff(ticket);

			if (request.getCookies() != null) {
				for (int i = 0; i < request.getCookies().length; i++) {
					Cookie cookie = request.getCookies()[i];
					if (cookie.getName().equals(Constants.LOGON_TICKET) || cookie.getName().equals(Constants.DOMAIN_LOGON_TICKET)) {
						cookie.setMaxAge(0);
						response.addCookie(cookie);
					}
				}
			}
			request.getSession().removeAttribute(Constants.LOGON_TICKET);
			session.invalidate();
		}
	}

	public List<SessionInfo> getSessionInfo(String username, int sessionType) {
		List<SessionInfo> info = null;
		for (Map.Entry<String, SessionInfo> entry : logons.entrySet()) {
			SessionInfo inf = (SessionInfo) entry.getValue();
			if (inf.getUser().getPrincipalName().equals(username) && (sessionType == -1 || (sessionType != -1 && sessionType == inf.getType()))) {
				if (info == null) {
					info = new ArrayList<SessionInfo>();
				}
				info.add(inf);
			}
		}
		return info;
	}

	public int getUserStatus(User user) throws Exception {
		if (user != null && lockedUsers.containsKey(user.getPrincipalName())
			&& ((AccountLock) lockedUsers.get(user.getPrincipalName())).getLocks() > 0) {
			return ACCOUNT_LOCKED;
		} else if (getSessionInfo(user.getPrincipalName(), -1) != null) {
			return ACCOUNT_ACTIVE;
		} else {
			if (user == null) {
				return ACCOUNT_UNKNOWN;
			} else {
				boolean disabled = !PolicyUtil.isEnabled(user);
				// if (!admin && disabled) {
				if (disabled) {
					return ACCOUNT_DISABLED;
				} else {
					// boolean admin = isAdministrator(user, true, false, true);
					boolean logonAuthorized = PolicyUtil.canLogin(user);
					// if (!admin && !logonAuthorized) {
					if (!logonAuthorized) {
						return ACCOUNT_REVOKED;
					} else {
						return ACCOUNT_GRANTED;
					}
				}
			}
		}
	}

	private void checkForMultipleSessions(User user, InetAddress address, int sessionType) throws UserDatabaseException {
		int type = Property.getPropertyInt(new RealmKey("security.multipleSessions", user.getRealm().getResourceId()));
		List activeSessions;
		switch (type) {
			case 0:
				break; // No restrction
			case 1:
				activeSessions = getSessionInfo(user.getPrincipalName(), sessionType);
				if (activeSessions != null) {
					throw new UserDatabaseException("You are already logged on, and this systems policy is to only allow one session per user.");
				}
				break;
			case 2:
				activeSessions = getSessionInfo(user.getPrincipalName(), sessionType);
				if (activeSessions != null) {
					for (Iterator i = activeSessions.iterator(); i.hasNext();) {
						SessionInfo info = (SessionInfo) i.next();
						if (!info.getAddress().equals(address)) {
							throw new UserDatabaseException("You are already logged on at a different address, and this systems policy is to only allow one session per user / address.");
						}
					}
				}
				break;
			default:
				throw new UserDatabaseException("Unknown multiple session restrictions type " + type + ".");
		}
	}

	public void initialiseSession(HttpSession session, User user) throws UserDatabaseException {
		if (log.isInfoEnabled())
			log.info("Initialising session " + session.getId()
				+ " with user "
				+ (user == null ? "[none]" : user.getPrincipalName()));
		PropertyProfile profile = (PropertyProfile) session.getAttribute(Constants.SELECTED_PROFILE);
		session.setAttribute(Constants.USER, user);
		String logonInfo = MessageResources.getMessageResources("com.adito.navigation.ApplicationResources")
						.getMessage("footer.info",
							user.getPrincipalName(),
							SimpleDateFormat.getDateTimeInstance().format(new Date()));
		session.setAttribute(Constants.LOGON_INFO, logonInfo);
		try {
			List profiles = ResourceUtil.filterResources(user, ProfilesFactory.getInstance()
							.getPropertyProfiles(user.getPrincipalName(), true, user.getRealm().getResourceId()), true);
			session.setAttribute(Constants.PROFILES, profiles);
			if (profiles.size() == 0) {
				throw new UserDatabaseException("You do not have permission to use any profiles.");
			}
			String startupProfile = Property.getProperty(new UserAttributeKey(user, User.USER_STARTUP_PROFILE));
			if (profiles.size() < 2) {
				profile = (PropertyProfile) profiles.get(0);
			} else if (!startupProfile.equals(ProfilesListDataSource.SELECT_ON_LOGIN)) {
				int profileId = Integer.parseInt(startupProfile);
				profile = null;
				for (Iterator i = profiles.iterator(); i.hasNext();) {
					PropertyProfile p = (PropertyProfile) i.next();
					if (profileId == p.getResourceId()) {
						profile = p;
						break;
					}
				}
				if (profile == null) {
					profile = ProfilesFactory.getInstance().getPropertyProfile(null,
						"Default",
						UserDatabaseManager.getInstance().getDefaultUserDatabase().getRealm().getResourceId());
				}
			}
			if (profile != null) {
				if (log.isInfoEnabled())
					log.info("Switching user " + user.getPrincipalName() + " to profile " + profile.getResourceName());
				session.setAttribute(Constants.SELECTED_PROFILE, profile);
			}
		} catch (Exception e) {
			throw new UserDatabaseException("Failed to initialise profiles.", e);
		}
		final String logonTicket = (String) session.getAttribute(Constants.LOGON_TICKET);
		session.setAttribute(Constants.LOGOFF_HOOK, new HttpSessionBindingListener() {
			public void valueBound(HttpSessionBindingEvent evt) {
			}

			public void valueUnbound(HttpSessionBindingEvent evt) {
				if (log.isDebugEnabled())
					log.debug("Session unbound");
				// We should should only log off completely if no other
				// session has
				// the logon ticket
				SessionInfo currentTicketSessionInfo = ((SessionInfo) logons.get(logonTicket));
				if (currentTicketSessionInfo == null || evt.getSession().getId().equals(currentTicketSessionInfo.getHttpSession()
								.getId())) {
					if (log.isDebugEnabled())
						log.debug("Session (" + evt.getSession().getId()
							+ ") unbound is the current session for ticket "
							+ logonTicket
							+ " so a logoff will be performed.");
					logoff(logonTicket);
				} else {
					if (log.isDebugEnabled())
						log.debug("Session unbound is NOT the current session, ignoring.");
				}
			}
		});
		if (log.isDebugEnabled())
			log.debug("Using profile: " + (profile == null ? "DEFAULT" : profile.getResourceName()) + ")");
		session.removeAttribute(Constants.SESSION_LOCKED);

		resetSessionTimeout(user, profile, session);
	}

	public void resetSessionTimeout(User user, PropertyProfile profile, HttpSession session) {
		try {
			Map sessionTimeoutBlocks = (Map) session.getAttribute(Constants.SESSION_TIMEOUT_BLOCKS);
			int minutes = 0;
			if (sessionTimeoutBlocks == null || sessionTimeoutBlocks.size() == 0) {
				minutes = CoreUtil.getUsersProfilePropertyIntOrDefault(session, "webServer.sessionInactivityTimeout", user);
			}
			if (log.isDebugEnabled())
				log.debug("Resetting timeout for session " + session.getId() + " to " + minutes + " minutes");
			session.setMaxInactiveInterval(minutes == 0 ? -1 : minutes * 60);
		} catch (Exception e) {
			log.error("Failed to reset session timeout.", e);
		}
	}

	public AccountLock checkForAccountLock(String username, String realmName) throws SecurityErrorException,
					AccountLockedException {
		// Get the user lockout policy
		int maxLogonAttemptsBeforeLock = 0;
		int lockDuration = 0;
		Realm realm;
		try {
			realm = UserDatabaseManager.getInstance().getRealm(realmName);
		} catch (Exception e1) {
			throw new SecurityErrorException(SecurityErrorException.INTERNAL_ERROR, e1, "Failed to determine the realm name " + realmName + ".");
		}

		try {
			maxLogonAttemptsBeforeLock = Property.getPropertyInt(new RealmKey("security.maxLogonAttemptsBeforeLock",
							realm.getResourceId()));
			lockDuration = Property.getPropertyInt(new RealmKey("security.lockDuration", realm.getResourceId()));
		} catch (Exception e) {
			throw new SecurityErrorException(SecurityErrorException.INTERNAL_ERROR, e, "Failed to determine password lockout policy.");
		}
		// Get the current lock (if any)
		AccountLock lock = "true".equals(SystemProperties.get("adito.recoveryMode", "false")) ? null
			: (AccountLock) lockedUsers.get(username);
		// If the user is currently locked, check if the lock has expired yeet
		if (lock != null && maxLogonAttemptsBeforeLock > 0 && lockDuration > 0 && lock.getLockedTime() != -1) {
			long expires = lock.getLockedTime() + (1000 * lockDuration);
			long now = System.currentTimeMillis();
			if (now < expires) {
				throw new AccountLockedException(username, "Account temporarily locked. Please try later.", false, expires - now);
			}
			// There was a lock, it is now expired
			lock.setAttempts(0);
			lock.setLockedTime(-1);
		}
		return lock;
	}

	// public User doClientLogon(String username, String password, String
	// realmName) throws UserDatabaseException,
	// InvalidLoginCredentialsException,
	// AccountLockedException {
	// // Get the user lockout policy
	// int maxLogonAttemptsBeforeLock = 0;
	// int maxLocksBeforeDisable = 0;
	// int lockDuration = 0;
	// try {
	// maxLogonAttemptsBeforeLock = Property.getPropertyInt(new
	// SystemConfigKey("security.maxLogonAttemptsBeforeLock"));
	// maxLocksBeforeDisable = Property.getPropertyInt(new
	// SystemConfigKey("security.maxLocksBeforeDisable"));
	// lockDuration = Property.getPropertyInt(new
	// SystemConfigKey("security.lockDuration"));
	// } catch (Exception e) {
	// throw new UserDatabaseException("Failed to determine password lockout
	// policy.", e);
	// }
	// // Get the current lock (if any)
	// AccountLock lock =
	// "true".equals(SystemProperties.get("adito.recoveryMode", "false")) ?
	// null
	// : (AccountLock) lockedUsers.get(username);
	// // If the user is currently locked, check if the lock has expired yeet
	// if (lock != null && maxLogonAttemptsBeforeLock > 0 && lockDuration > 0 &&
	// lock.getLockedTime() != -1) {
	// long expires = lock.getLockedTime() + (1000 * lockDuration);
	// long now = System.currentTimeMillis();
	// if (now < expires) {
	// throw new AccountLockedException("Account temporarily locked. Please try
	// later.", false, expires - now);
	// }
	// // There was a lock, it is now expired
	// lock.setAttempts(0);
	// lock.setLockedTime(-1);
	// }
	// try {
	// User user =
	// UserDatabaseManager.getInstance().getRealm(realmName).getUserDatabase().logon(username,
	// password);
	// // Sucessful login, remove any locks
	// unlockUser(username);
	// return user;
	// } catch (InvalidLoginCredentialsException ilce) {
	// if (lock == null && maxLogonAttemptsBeforeLock > 0 && lockDuration > 0) {
	// lock = createLock(username);
	// }
	// if (lock != null) {
	// lock.setAttempts(lock.getAttempts() + 1);
	// if (lock.getAttempts() >= maxLogonAttemptsBeforeLock) {
	// lock.setLocks(lock.getLocks() + 1);
	// if (lock.getLocks() >= maxLocksBeforeDisable) {
	// try {
	// // Disable the user
	// User user =
	// UserDatabaseManager.getInstance().getRealm(realmName).getUserDatabase().logon(username,
	// password);
	// if (PolicyUtil.isEnabled(user)) {
	// PolicyUtil.setEnabled(user, false, lock, null);
	// }
	// } catch (Exception e) {
	// log.error(e);
	// }
	// throw new AccountLockedException("Account disabled, please contact your
	// administrator.", true, 0);
	// } else {
	// lock.setLockedTime(System.currentTimeMillis());
	// throw new AccountLockedException("Account temporarily locked. Please try
	// later.", false,
	// lockDuration * 1000);
	// }
	// }
	// }
	// throw ilce;
	// } catch (AccountLockedException ale) {
	// throw ale;
	// } catch (Exception e) {
	// throw new UserDatabaseException("Failed to logon. ", e);
	// }
	// }

	public AccountLock logonFailed(String username, String realmName, AccountLock lock) throws SecurityErrorException,
					AccountLockedException {
		// Get the user lockout policy
		int maxLogonAttemptsBeforeLock = 0;
		int maxLocksBeforeDisable = 0;
		int lockDuration = 0;
		UserDatabase udb = null;
		try {
			udb = UserDatabaseManager.getInstance().getUserDatabase(realmName);
		} catch (Exception e1) {
			throw new SecurityErrorException(SecurityErrorException.INTERNAL_ERROR, e1, "Failed to determine the realm name " + realmName + ".");
		}

		try {
			maxLogonAttemptsBeforeLock = Property.getPropertyInt(new RealmKey("security.maxLogonAttemptsBeforeLock", udb.getRealm()
							.getResourceId()));
			maxLocksBeforeDisable = Property.getPropertyInt(new RealmKey("security.maxLocksBeforeDisable", udb.getRealm()
							.getResourceId()));
			lockDuration = Property.getPropertyInt(new RealmKey("security.lockDuration", udb.getRealm().getResourceId()));
		} catch (Exception e) {
			throw new SecurityErrorException(SecurityErrorException.INTERNAL_ERROR, e, "Failed to determine password lockout policy.");
		}
		if (lock == null && maxLogonAttemptsBeforeLock > 0 && lockDuration > 0) {
			lock = createLock(username);
		}
		if (lock != null) {
			lock.setAttempts(lock.getAttempts() + 1);
			if (lock.getAttempts() >= maxLogonAttemptsBeforeLock) {
				lock.setLocks(lock.getLocks() + 1);
				if (lock.getLocks() >= maxLocksBeforeDisable) {
					try {
						// Disable the user
						User user = udb.getAccount(username);
						if (PolicyUtil.isEnabled(user)) {
							PolicyUtil.setEnabled(user, false, lock, null);
						}
					} catch (Exception e) {
						log.error(e);
					}
					throw new AccountLockedException(username, "Account disabled, please contact your administrator.", true, 0);
				} else {
					lock.setLockedTime(System.currentTimeMillis());
					throw new AccountLockedException(username, "Account temporarily locked. Please try later.", false, lockDuration * 1000);
				}
			}
		}
		return lock;
	}

	public void logoff(String ticket) {

		SessionInfo session = (SessionInfo) logons.remove(ticket);
		/**
		 * LDP - What happens if logoff is called twice? Previously we assumed this
		 * would never happen
		 */
		if(session==null)
			return;
		
		if (log.isInfoEnabled())
			log.info("Logging off " + ticket);
		List<String> ticketsToRemove = new ArrayList<String>();
		synchronized (logonsBySessionId) {
			for (Map.Entry<String, SessionInfo> entry : logonsBySessionId.entrySet()) {
				if (entry.getValue().getLogonTicket().equals(ticket)) {
					ticketsToRemove.add(entry.getKey());
				}
			}
			for (String key : ticketsToRemove) {
				logonsBySessionId.remove(key);
			}
		}
        session.release();
		CoreServlet.getServlet().fireCoreEvent(new CoreEvent(this, CoreEventConstants.LOGOFF, null, session));
	}

	public Map getActiveSessions() {
		return logons;
	}

	public User getUser(HttpSession session, String logonTicket) throws SecurityErrorException {
		if (logonTicket == null) {
			logonTicket = (String) session.getAttribute(Constants.LOGON_TICKET);
		}
		if (logonTicket == null) {
			throw new SecurityErrorException(SecurityErrorException.ERR_INVALID_TICKET, "No ticket was provided or found in the session object (" + session.getId() + ")");
		}
		SessionInfo info = (SessionInfo) logons.get(logonTicket);
		if (info == null) {
			throw new SecurityErrorException(SecurityErrorException.ERR_INVALID_TICKET, "No session info. object could be found for the ticket (" + session.getId() + ")");
		}
		User user = info.getUser();
		return user;
	}

	public User getUser(HttpServletRequest request) throws SecurityErrorException {
		return getUser(request, null);
	}

	public User getUser(HttpServletRequest request, String logonTicket) throws SecurityErrorException {
		return getUser(request.getSession(), logonTicket);
	}

	public int hasClientLoggedOn(HttpServletRequest request, HttpServletResponse response) throws SecurityErrorException {
		// Get the logon cookie
		String logonCookie = null;
		if (request.getCookies() != null) {
			for (int i = 0; i < request.getCookies().length; i++) {
				Cookie cookie = request.getCookies()[i];
				if (cookie.getName().equals(Constants.LOGON_TICKET) || cookie.getName().equals(Constants.DOMAIN_LOGON_TICKET)) {
					logonCookie = cookie.getValue();
				}
			}
		}
		// If there is a logon ticket in the requests attributes then reassign
		// as we've just been issued a new ticket.
		if (request.getAttribute(Constants.LOGON_TICKET) != null)
			logonCookie = (String) request.getAttribute(Constants.LOGON_TICKET);
		// First check the users session for a logonticket
		String sessionLogonTicket = (String) request.getSession().getAttribute(Constants.LOGON_TICKET);
		if (sessionLogonTicket != null) {
			// Make sure we are still receiving the logon ticket
			/**
			 * LDP - Users are having too many issues with this change. If we
			 * still have a ticket in the session then the HTTP session must
			 * still be alive and the the cookie has simply expired before the
			 * HTTP session (or the browser has elected not to send it). We
			 * should allow this to continue and refresh the cookie here.
			 */
			/*
			 * if(logonCookie == null &&
			 * request.getAttribute(Constants.LOGON_TICKET) == null) {
			 * 
			 * 
			 * log.warn("Lost logon ticket. It is likely that logon cookie has
			 * expired. "); return INVALID_TICKET; } else
			 */
			if (logonCookie == null) {

				SessionInfo session = getSessionInfo(sessionLogonTicket);
				if (session == null)
					return NOT_LOGGED_ON;
				addCookies(new ServletRequestAdapter(request), new ServletResponseAdapter(response), sessionLogonTicket, session);
			}
			// Still check that the cookie is what we expect it to be
			if (logonCookie != null && !sessionLogonTicket.equals(logonCookie)) {
				log.warn("Expected a different logon ticket.");
				return NOT_LOGGED_ON;
			}
			
			if(checkRemoteAddress(sessionLogonTicket, request.getRemoteAddr())) {
				return LOGGED_ON;
			}
		} else {
			if (logonCookie != null && logons.containsKey(logonCookie)) {
				if(checkRemoteAddress(logonCookie, request.getRemoteAddr())) {
					refreshLogonTicket(request, response, logonCookie);
					return LOGGED_ON;
				}
			}
		}
		return NOT_LOGGED_ON;
	}
	
	private boolean checkRemoteAddress(String logonTicket, String remoteAddr) {

		try {
			SessionInfo session = getSessionInfo(logonTicket);
			
			if(Property.getPropertyBoolean(new RealmKey("security.checkRemoteAddress", session.getRealmId()))) {
				InetAddress addr = InetAddress.getByName(remoteAddr);
				if(log.isDebugEnabled())
					log.debug("Verifying " + addr.getHostAddress() + " is original address " + session.getAddress().getHostAddress());
				return session!=null && session.getAddress().equals(addr);
			} else
				return true;

		} catch (UnknownHostException e) {
			log.error("Failed to determine remote address", e);
			return false;
		}
	}

	private void refreshLogonTicket(HttpServletRequest request, HttpServletResponse response, String logonTicket)
					throws SecurityErrorException {
		if (log.isInfoEnabled())
			log.info("Refreshing logon ticket " + logonTicket);
		User user = getUser(request, logonTicket);
		request.getSession().setAttribute(Constants.USER, user);
		request.getSession().setAttribute(Constants.LOGON_TICKET, logonTicket);
		request.setAttribute(Constants.LOGON_TICKET, logonTicket);
		SessionInfo info = (SessionInfo) logons.get(logonTicket);
		if (info == null) {
			InetAddress address;
			try {
				address = InetAddress.getByName(request.getRemoteAddr());
			} catch (UnknownHostException uhe) {
				throw new SecurityErrorException(SecurityErrorException.ERR_INVALID_TICKET, "Could not refresh logon ticket. " + uhe.getMessage());
			}
			String userAgent = request.getHeader("User-Agent");
			info = SessionInfo.nextSession(request.getSession(), logonTicket, user, address, SessionInfo.UI, userAgent);
		} else {
			moveSessionTimeoutBlocks(info.getHttpSession(), request.getSession());
			info.setSession(request.getSession());
		}
		request.getSession().setAttribute(Constants.SESSION_INFO, info);

		/**
		 * LDP - Allow for the session info to be looked up using the session
		 * id.
		 */
		try {
			String sessionIdentifier = SystemProperties.get("adito.cookie", "JSESSIONID");
			String sessionId = null;
			Cookie[] cookies = request.getCookies();
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equalsIgnoreCase(sessionIdentifier)) {
					sessionId = cookies[i].getValue();
					break;
				}
			}
			if (sessionId != null) {
				logonsBySessionId.put(sessionId, info);
			} else
				log.warn("Could not find session id using identifier " + sessionIdentifier + " in HTTP request");
		} catch (Exception ex) {
			log.warn("Failed to determine HTTP session id", ex);
		}
		addSession(logonTicket, info, request, response);
		try {
			if (Property.getPropertyBoolean(new SystemConfigKey("security.session.lockSessionOnBrowserClose"))) {
				if (log.isInfoEnabled())
					log.info("New session - will force the user to authenticate again");
				request.getSession().setAttribute(Constants.SESSION_LOCKED, user);
			}
			else {
			    ResourceUtil.setAvailableProfiles(info);
			}
		} catch (Exception e) {
			log.warn("Failed to set session lock.", e);
		}
	}

	public void addSession(String logonTicket, SessionInfo info, HttpServletRequest request, HttpServletResponse response) {
		logons.put(logonTicket, info);
		addCookies(new ServletRequestAdapter(request), new ServletResponseAdapter(response), logonTicket, info);
	}

	public void addCookies(RequestHandlerRequest request, RequestHandlerResponse response, String logonTicket, SessionInfo session) {
		
		
		if(request.getAttribute("sslx.logon.cookie")!=null)
			return;
		
		/**
		 * Set the normal logon ticket without a domain - this works in almost
		 * all circumstances
		 */
		Cookie cookie = new Cookie(Constants.LOGON_TICKET, logonTicket);
		cookie.setMaxAge(Property.getPropertyInt(new SystemConfigKey("security.session.maxCookieAge")));
		cookie.setPath("/");
		cookie.setSecure(true);
		response.addCookie(cookie);
		/**
		 * Set a logon ticket for the domain - this is require to make active
		 * dns work.
		 */
		Cookie cookie2 = new Cookie(Constants.DOMAIN_LOGON_TICKET, logonTicket);
		cookie2.setMaxAge(Property.getPropertyInt(new SystemConfigKey("security.session.maxCookieAge")));
		cookie2.setPath("/");
		// We now set the domain on the cookie so the new Active DNS feature for
		// Reverse Proxy works correctly
		String host = request.getField("Host");
		if (host != null) {
			HostService hostService = new HostService(host);
			cookie2.setDomain(hostService.getHost());
		}
		cookie2.setSecure(true);
		response.addCookie(cookie2);
		
		
		request.setAttribute("sslx.logon.cookie", new Object());
		
		/**
		 * LDP - This code was not setting the domain on the ticket. I've
		 * converted to the new format of having two seperate tickets to ensure
		 * tickets are sent across domains
		 */
		/*
		 * Cookie cookie = new Cookie(Constants.LOGON_TICKET, logonTicket); try {
		 * cookie.setMaxAge(Integer.parseInt(CoreServlet.getServlet().getPropertyDatabase().getProperty(0,
		 * null, "security.session.maxCookieAge"))); if
		 * ("true".equals(CoreServlet.getServlet().getPropertyDatabase().getProperty(0,
		 * null, "security.session.lockSessionOnBrowserClose"))) { if
		 * (log.isInfoEnabled()) log.info("New session - will force the user to
		 * authenticate again"); // initialiseSession(request.getSession(),
		 * user); // List profiles = //
		 * CoreServlet.getServlet().getPropertyDatabase().getPropertyProfiles(user.getUsername(), //
		 * false); // request.getSession().setAttribute(Constants.PROFILES, //
		 * profiles);
		 * request.getSession().setAttribute(Constants.SESSION_LOCKED, user); } }
		 * catch (Exception e) { log.error(e); cookie.setMaxAge(900); }
		 * cookie.setPath("/"); cookie.setSecure(true);
		 * response.addCookie(cookie);
		 */
		//
	}

	private SessionInfo addLogonTicket(HttpServletRequest request, HttpServletResponse response, User user, InetAddress address,
										int sessionType) {
		String logonTicket = TicketGenerator.getInstance().generateUniqueTicket("SLX");
		if (log.isInfoEnabled())
			log.info("Adding logon ticket to session " + request.getSession().getId());
		request.getSession().setAttribute(Constants.LOGON_TICKET, logonTicket);
		request.setAttribute(Constants.LOGON_TICKET, logonTicket);
		String userAgent = request.getHeader("User-Agent");
		SessionInfo info = SessionInfo.nextSession(request.getSession(), logonTicket, user, address, sessionType, userAgent);
		request.getSession().setAttribute(Constants.SESSION_INFO, info);
		try {
			String sessionIdentifier = SystemProperties.get("adito.cookie", "JSESSIONID");
			String sessionId = null;
			Cookie[] cookies = request.getCookies();
			for (int i = 0; cookies != null && i < cookies.length; i++) {
				if (cookies[i].getName().equalsIgnoreCase(sessionIdentifier)) {
					sessionId = cookies[i].getValue();
					break;
				}
			}
			if (sessionId != null) {
				logonsBySessionId.put(sessionId, info);
			} else
				log.warn("Could not find session id using identifier " + sessionIdentifier + " in HTTP request");
		} catch (Exception ex) {
			log.warn("Failed to determine HTTP session id", ex);
		}
		logons.put(logonTicket, info);
		/**
		 * Set the normal logon ticket without a domain - this works in almost
		 * all circumstances
		 */
		Cookie cookie = new Cookie(Constants.LOGON_TICKET, logonTicket);
		cookie.setMaxAge(Property.getPropertyInt(new SystemConfigKey("security.session.maxCookieAge")));
		cookie.setPath("/");
		cookie.setSecure(true);
		response.addCookie(cookie);
		/**
		 * Set a logon ticket for the domain - this is require to make active
		 * dns work.
		 */
		Cookie cookie2 = new Cookie(Constants.DOMAIN_LOGON_TICKET, logonTicket);
		cookie2.setMaxAge(Property.getPropertyInt(new SystemConfigKey("security.session.maxCookieAge")));
		cookie2.setPath("/");
		// We now set the domain on the cookie so the new Active DNS feature for
		// Reverse Proxy works correctly
		String host = request.getHeader("Host");
		if (host != null) {
			HostService hostService = new HostService(host);
			cookie2.setDomain(hostService.getHost());
		}
		cookie.setSecure(true);
		response.addCookie(cookie2);
		return info;
	}

	public void unlockUser(String username) {
		if (log.isInfoEnabled())
			log.info("Unlocking user " + username);
		lockedUsers.remove(username);
	}

	/**
	 * @param request
	 * @param response
	 * @param scheme
	 */
	public void logon(HttpServletRequest request, HttpServletResponse response, AuthenticationScheme scheme) throws Exception {
		User user = scheme.getUser();

		// Check logon is currently allowed
		String logonNotAllowedReason = LogonControllerFactory.getInstance().checkLogonAllowed(user);

		if (logonNotAllowedReason != null) {
			log.warn("Logon not allowed because '" + logonNotAllowedReason + "'");
			throw new Exception(logonNotAllowedReason);
		}

		if (log.isInfoEnabled()) {
			log.info("Session logon ticket is " + (String) request.getSession().getAttribute(Constants.LOGON_TICKET));
			log.info("Logging on " + scheme.getUsername() + " for scheme " + scheme.getSchemeName());
		}
		// Sucessful login, remove any locks
		unlockUser(scheme.getUsername());

		String host = (request.isSecure() || SystemProperties.get("jetty.force.HTTPSRedirect", "false").equals("true") ? "https"
			: "http") + "://"
			+ request.getHeader(HttpConstants.HDR_HOST);

		request.getSession().setAttribute(Constants.HOST, host);
		SessionInfo info = null;
		boolean fireEvent = false;
		if (request.getSession().getAttribute(Constants.SESSION_LOCKED) == null) {
			InetAddress address = InetAddress.getByName(request.getRemoteAddr());
			int sessionType = SessionInfo.getSessionTypeForUserAgent(request.getHeader("User-Agent"));
			checkForMultipleSessions(user, address, sessionType);
			info = addLogonTicket(request, response, user, address, sessionType);
			try {
				info.getHttpSession().setAttribute(Constants.VPN_AUTOSTART,
					CoreUtil.getUsersProfileProperty(info.getHttpSession(), "client.autoStart", user));
			} catch (Exception e) {
				throw new SecurityErrorException(SecurityErrorException.INTERNAL_ERROR, e.getMessage());
			}
			fireEvent = true;
		}
		// Initialise the session
		initialiseSession(request.getSession(), user);
		// Build the menus
		CoreUtil.resetMainNavigation(request.getSession());

		char[] pw = getPasswordFromCredentials(scheme);
		String mode = Property.getProperty(new SystemConfigKey("security.privateKeyMode"));
		if (!mode.equals("disabled")) {

			try {
				PublicKeyStore.getInstance().verifyPrivateKey(user.getPrincipalName(), pw);
			} catch (PromptForPasswordException e) {
				CoreUtil.addPageInterceptListener(request.getSession(), new PromptForPrivateKeyPassphraseInterceptListener());
			} catch (UpdatePrivateKeyPassphraseException e) {
				if (mode.equals("prompt")) {
					CoreUtil.addPageInterceptListener(request.getSession(), new PromptForPrivateKeyPassphraseInterceptListener());
				} else {
					CoreUtil.addPageInterceptListener(request.getSession(), new UpdatePrivateKeyPassphraseInterceptListener());
				}
			}
        }

		/*
		 * Make sure the logon event gets fired after the private key has
		 * been initialised. This is repeated in the actions the page
		 * intercept listeners redirect to
		 */
		if (fireEvent) {
			CoreServlet.getServlet()
							.fireCoreEvent(new CoreEvent(this, CoreEventConstants.LOGON, scheme, info).addAttribute(CoreAttributeConstants.EVENT_ATTR_IP_ADDRESS,
								request.getRemoteAddr())
											.addAttribute(CoreAttributeConstants.EVENT_ATTR_HOST, request.getRemoteHost())
											.addAttribute(CoreAttributeConstants.EVENT_ATTR_SCHEME, scheme.getSchemeName()));
		} 
	}

	/**
	 * @param scheme
	 * @return
	 */
	public char[] getPasswordFromCredentials(AuthenticationScheme scheme) {

		if (scheme != null) {
			for (Iterator i = scheme.credentials(); i.hasNext();) {
				Credentials cred = (Credentials) i.next();

				if (cred instanceof PasswordCredentials) {
					if (((PasswordCredentials) cred).getPassword() != null) {
						return ((PasswordCredentials) cred).getPassword();
					}
				}
			}
		}
		return null;
	}

	public SessionInfo getSessionInfo(HttpServletRequest request) {
		/**
		 * LDP - This was only ever looking at the HTTP session. This causes
		 * problems if the browser creates a new session but the logon ticket is
		 * still valid. Look at the cookies if the ticket cannot be found.
		 */

	    /**
	     * BPS - This is wrong and should be solved another way. 
	     * getSessionInfo is *supposed* to return null if the HttpSession is 
	     * not attached to a SessionInfo. This code is only partially 
	     * reconfiguring the session and will prevent hasClientLoggedOn
	     * from completing its job properly. This is a possible culprit for 
	     * the SessionInfos hanging around as the session
	     * binding listeners will not be set up correctly.
	     */
		SessionInfo session = getSessionInfo(request.getSession());

		if (session == null) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (int i = 0; i < cookies.length; i++) {
					if (cookies[i].getName().equals(Constants.LOGON_TICKET) || cookies[i].getName()
									.equals(Constants.DOMAIN_LOGON_TICKET)) {
						session = getSessionInfo(cookies[i].getValue());
						if (session != null) {
                            log.error("----------------------------------------------------------");
                            log.error("A call has been made to getSessionInfo(HttpServletRequest)");
                            log.error("but the SessionInfo was not contained in the HttpSession");
                            log.error("However, there appears to valid cookies that DO point to a");
                            log.error("valid SessionInfo.");
                            dumpSessionStuff(session);
							request.getSession().setAttribute(Constants.LOGON_TICKET, session.getLogonTicket());
							request.getSession().setAttribute(Constants.SESSION_INFO, session);
							break;
						}
					}
				}
			}
		}

		return session;
	}

	public SessionInfo getSessionInfo(HttpSession session) {
		String logonTicket = (String) session.getAttribute(Constants.LOGON_TICKET);
		if (logonTicket != null) {
			return getSessionInfo(logonTicket);
		}
		return null;
	}

	public SessionInfo getSessionInfoBySessionId(String sessionId) {
		return (SessionInfo) logonsBySessionId.get(sessionId);
	}

	public SessionInfo getSessionInfo(String logonTicket) {
		return (SessionInfo) logons.get(logonTicket);
	}

	public void attachSession(String sessionId, SessionInfo session) {
		logonsBySessionId.put(sessionId, session);
	}

	public void registerAuthorizationTicket(String ticket, SessionInfo session) {
		authorizedTickets.put(ticket, session);
	}

	public SessionInfo removeAuthorizationTicket(String ticket) {
		return (SessionInfo) authorizedTickets.remove(ticket);
	}

	public SessionInfo getAuthorizationTicket(String ticket) {
		return (SessionInfo) authorizedTickets.get(ticket);
	}

	AccountLock createLock(String username) {
		AccountLock lock = new AccountLock(username);
		lockedUsers.put(username, lock);
		return lock;
	}

	public String checkLogonAllowed(User user) {

		updateMostUsersEverOnline();
		return null;
	}

	private synchronized void moveSessionTimeoutBlocks(HttpSession oldSession, HttpSession newSession) {
		Map sessionTimeoutBlocks = (Map) oldSession.getAttribute(Constants.SESSION_TIMEOUT_BLOCKS);
		if (sessionTimeoutBlocks != null) {
			newSession.setAttribute(Constants.SESSION_TIMEOUT_BLOCKS, sessionTimeoutBlocks);
		}
		Integer vpnClientSessionTimeoutBlockId = (Integer) oldSession.getAttribute(Constants.AGENT_SESSION_TIMEOUT_BLOCK_ID);
		if (vpnClientSessionTimeoutBlockId != null) {
			newSession.setAttribute(Constants.AGENT_SESSION_TIMEOUT_BLOCK_ID, vpnClientSessionTimeoutBlockId);
		}
		newSession.setMaxInactiveInterval(sessionTimeoutBlocks == null || sessionTimeoutBlocks.size() == 0 ? oldSession.getMaxInactiveInterval()
			: -1);
	}

	public static class UpdatePrivateKeyPassphraseInterceptListener implements PageInterceptListener {

		public String getId() {
			return "updatePrivateKeyPassphrase";
		}

		public ActionForward checkForForward(Action action, ActionMapping mapping, HttpServletRequest request,
												HttpServletResponse response) throws PageInterceptException {
			if (!(action instanceof UpdatePrivateKeyPassphraseDispatchAction)) {
				return new ActionForward("/updatePrivateKeyPassphrase.do", true);
			}
			return null;
		}

		public boolean isRedirect() {
			return false;
		}
	}

	class PromptForPrivateKeyPassphraseInterceptListener implements PageInterceptListener {

		public String getId() {
			return "promptForPrivateKeyPassphrase";
		}

		public ActionForward checkForForward(Action action, ActionMapping mapping, HttpServletRequest request,
												HttpServletResponse response) throws PageInterceptException {
			if (!(action instanceof PromptForPrivateKeyPassphraseDispatchAction)) {
				try {
					if ("automatic".equals(Property.getProperty(new SystemConfigKey("security.privateKeyMode")))) {
						return new ActionForward("/promptForPrivateKeyPassphraseAuto.do", true);
					} else {
						return new ActionForward("/promptForPrivateKeyPassphrase.do", true);
					}
				} catch (Exception e) {
					log.error("Failed to determine private key mode.", e);
				}
			}
			return null;
		}

		public boolean isRedirect() {
			return false;
		}
	}

	protected int getActiveSessionCount() {
		return getActiveSessions().size();
	}

	protected void updateMostUsersEverOnline() {

		try {
			int concurrentSessions = getActiveSessionCount() + 1;
			if (Property.getPropertyInt(new SystemConfigKey("security.maxUserCount")) < (concurrentSessions)) {
				Property.setProperty(new SystemConfigKey("security.maxUserCount"), concurrentSessions, null);
			}
		} catch (Exception ex) {
			log.error("Could not update most users online property", ex);
		}
	}
	
	//////// TEMPORARY CODE ///////////

    private void dumpSessionStuff(SessionInfo sesh) {
        log.error("User: " + sesh.getUser().getPrincipalName());
        log.error("Type: " + sesh.getType());
        log.error("User Agent: " + sesh.getUserAgent());
        log.error("Address: " + sesh.getAddress());
        log.error("Logon Time: " + SimpleDateFormat.getDateTimeInstance().format(new Date(sesh.getLogonTime().getTimeInMillis())));
        log.error("------------------------------------------------------");
    }

	class MostUsersOnline implements SystemInformationProvider {
		public String getBundle() {
			return "security";
		}

		public String getName() {
			return "security.maxUserCount";
		}

		public String getValue() {
			return String.valueOf(Property.getProperty(new SystemConfigKey("security.maxUserCount")));
		}
	}

	class CurrentUsersOnline implements SystemInformationProvider {
		public String getBundle() {
			return "security";
		}

		public String getName() {
			return "security.currentUserCount";
		}

		public String getValue() {
			return String.valueOf(getActiveSessionCount());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.security.LogonController#applyMenuItemChanges(javax.servlet.http.HttpServletRequest)
	 */
	public void applyMenuItemChanges(HttpServletRequest request) {
		for (Iterator i = logons.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			SessionInfo sessionInfo = (SessionInfo) entry.getValue();
			if (sessionInfo.getType() == SessionInfo.UI) {
			    try {
    				// remove the menu tree.
    				sessionInfo.getHttpSession().removeAttribute(Constants.MENU_TREE);
    				String username = sessionInfo.getUser().getPrincipalName();
					// clean up any policy setups as they have changed now.
					PolicyDatabaseFactory.getInstance().cleanup();
					Realm realm = sessionInfo.getUser().getRealm();
					// update the sessions user, so any changes are known.
					sessionInfo.setUser(UserDatabaseManager.getInstance().getUserDatabase(realm).getAccount(username));
				} catch (Exception e) {
				}
			}
		}
	}
	
	/**
	 * This thread is a temporary fix until we can find where SessionInfo
	 * objects are not getting removed from the list of active sessions when 
	 * an HttpSession gets invalidated
	 */
	class HorribleHackReaperThread extends Thread {
	    
	    HorribleHackReaperThread() {
	        super("HorribleHackReaperThread");
	        setPriority(Thread.MIN_PRIORITY);
	        setDaemon(true);
	        start();
	    }
	    
	    public void run() {
	        try {
    	        while(true) {
    	            Thread.sleep(60000);
    	            Map sessions = getActiveSessions();
    	            synchronized(sessions) {
    	                List<SessionInfo> toRemove = new ArrayList<SessionInfo>();
    	                for(Iterator i = sessions.values().iterator(); i.hasNext(); ) {
    	                    SessionInfo sesh = (SessionInfo)i.next();
    	                    if(sesh.getHttpSession() != null) {
    	                        try {
    	                            sesh.getHttpSession().getAttribute(Constants.SESSION_INFO);
    	                        }
    	                        catch(IllegalStateException ise) {
    	                            log.error("------------------------------------------------------");
    	                            log.error("An Adito session that is attached to an");
    	                            log.error("invalid HttpSession has been discovered. This");
                                    log.error("may cause other problems so it has been removed.");
                                    log.error("Please report this 3SP together with the information");
                                    log.error("displayed below so that we determine the underlying");
                                    log.error("cause of this problem.");
                                    dumpSessionStuff(sesh);
                                    toRemove.add(sesh);
    	                        }
    	                    }
    	                }
    	                for(SessionInfo sesh : toRemove) {
                            logons.remove(sesh.getLogonTicket());
                            logonsBySessionId.remove(sesh.getHttpSession().getId());
    	                }
    	            }    	            
    	        }
	        }
	        catch(Exception e) {
	            log.error("SessionInfo reaper thread has died.", e);
	        }
	    }
	}
}
