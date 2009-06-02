
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
			
package net.openvpn.als.security;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.openvpn.als.boot.RequestHandlerRequest;
import net.openvpn.als.boot.RequestHandlerResponse;
import net.openvpn.als.properties.PropertyProfile;

/**
 */
public interface LogonController {
    public final static int ACCOUNT_UNKNOWN = -1;
    public final static int ACCOUNT_GRANTED = 0;
    public final static int ACCOUNT_DISABLED = 1;
    public final static int ACCOUNT_LOCKED = 2;
    public final static int ACCOUNT_REVOKED = 3;
    public final static int ACCOUNT_ACTIVE = 4;
    public final static int NOT_LOGGED_ON = 0;
    public final static int LOGGED_ON = 1;
    public final static int INVALID_TICKET  = 2;

    /**
     * Initialize the controller with the user and system databases.
     *
     * @param udb
     * @param sdb
     */
    public abstract void init();

    public abstract boolean isAdministrator(User principal);

    public abstract int addSessionTimeoutBlock(HttpSession session, String reason);

    public abstract void removeSessionTimeoutBlock(HttpSession session, int sessionTimeoutBlockId);

    public abstract void logoffSession(HttpServletRequest request, HttpServletResponse response) throws SecurityErrorException;

    public abstract List<SessionInfo> getSessionInfo(String username, int sessionType);

    public abstract SessionInfo getSessionInfo(String logonTicket);

    public abstract int getUserStatus(User user) throws Exception;

    /*public abstract String logonClient(HttpServletRequest request, HttpServletResponse response, String username, String password,
                    Properties properties) throws InvalidTicketException, UserDatabaseException, InvalidLoginCredentialsException,
                    AccountLockedException, UnknownHostException;
    public User doClientLogon(String username, String password) throws UserDatabaseException, InvalidLoginCredentialsException, AccountLockedException;
    */

    public abstract void initialiseSession(HttpSession session, User user) throws UserDatabaseException;

    public void resetSessionTimeout(User user, PropertyProfile profile, HttpSession session);

    public abstract AccountLock checkForAccountLock(String username, String realmName) throws SecurityErrorException, AccountLockedException;

    public abstract AccountLock logonFailed(String username, String realmName, AccountLock lock) throws SecurityErrorException, AccountLockedException;

    /*public abstract void removeVPNClient(VPNSession session);(/)*/

    public abstract void logoff(String ticket);

    /*public abstract VPNSession getPrimaryVPNSession(List vpnSessions);

    public abstract String setupVPNSession(SessionInfo sessionInfo)
                    throws InvalidTicketException;

    public abstract VPNSession getVPNSessionByTicket(String ticket);

    public abstract List getVPNSessionsByLogon(HttpServletRequest request);

    public abstract List getVPNSessionsByLogon(String ticket);

    public abstract VPNSession getPendingVPNSession(HttpServletRequest request);

    public abstract VPNSession getPendingVPNSession(String ticket);*/

    public abstract Map getActiveSessions();

    /*public abstract void deregisterVPNClient(VPNSession vpnSession) throws IllegalStateException;*/

    /*public abstract String registerVPNClient(HttpServletRequest request, String authorizationTicket, int clientPort,
                    Properties properties, int type) throws InvalidTicketException;
*/
    /*public abstract boolean waitForClientRegistration(String ticket, int timeout);*/

    public abstract User getUser(HttpSession session, String logonTicket) throws SecurityErrorException;

    public abstract User getUser(HttpServletRequest request) throws SecurityErrorException;

    public abstract User getUser(HttpServletRequest request, String logonTicket) throws SecurityErrorException;

    public abstract int hasClientLoggedOn(HttpServletRequest request, HttpServletResponse response)
                    throws SecurityErrorException;

    /*public abstract VPNSession getVPNSession(HttpServletRequest request);

    public abstract boolean verifyPendingVPNAuthorization(String ticket);

    public abstract boolean verifyPendingVPNAuthorization(HttpServletRequest request);
*/

    public abstract void unlockUser(String username);

/*    public abstract void waitForFirstClientHearbeat(HttpServletRequest request) throws Exception;*/

    /**
     * @param request
     * @param response
     * @param authSession
     */
    public abstract void logon(HttpServletRequest request, HttpServletResponse response, AuthenticationScheme authSession)
                    throws Exception;

    /**
     * @param scheme
     * @return
     */
    public abstract char[] getPasswordFromCredentials(AuthenticationScheme scheme);

    /**
     * @param session
     * @return
     */
    /*public abstract boolean isVPNSessionValid(VPNSession session);*/

    /**
     * @param request
     * @return
     */
    public abstract SessionInfo getSessionInfo(HttpServletRequest request) ;
    
    public abstract SessionInfo getSessionInfo(HttpSession session) ;


    public abstract SessionInfo getSessionInfoBySessionId(String sessionId);

    public void addCookies(RequestHandlerRequest request,
			RequestHandlerResponse response, String logonTicket, SessionInfo session);

    public void addSession(String logonTicket, SessionInfo info, HttpServletRequest request, HttpServletResponse response);

    public abstract String checkLogonAllowed(User username);
    
    public void attachSession(String sessionId, SessionInfo session);
    
    public void registerAuthorizationTicket(String ticket, SessionInfo session);
    
    public SessionInfo removeAuthorizationTicket(String ticket);
    
    public SessionInfo getAuthorizationTicket(String ticket);
    
    /**
     * Method to clear the policy cash, remove the menu items form 
	 * the session and reset the user in the session.
     * @param request
     */
    public void applyMenuItemChanges(HttpServletRequest request);

}
