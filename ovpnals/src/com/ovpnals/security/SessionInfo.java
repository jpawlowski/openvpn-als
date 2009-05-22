
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
			
package com.ovpnals.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.crypto.digests.Hash;
import com.maverick.crypto.digests.MD5Digest;
import com.ovpnals.boot.Context;
import com.ovpnals.boot.ContextHolder;
import com.ovpnals.boot.Util;
import com.ovpnals.core.CoreEvent;
import com.ovpnals.core.CoreEventConstants;
import com.ovpnals.core.CoreListener;
import com.ovpnals.core.CoreServlet;
import com.ovpnals.core.CoreUtil;
import com.ovpnals.realms.Realm;

/**
 * Encapsulates everything known about an OpenVPN-ALS session.
 */
public class SessionInfo implements CoreListener {

    final static Log log = LogFactory.getLog(SessionInfo.class);

    private static List davUserAgents = null;
    private static Context context = ContextHolder.getContext();

    /* Logon type */

    /**
     * Originated from the user interface
     */
    public final static int UI = 0;

    /**
     * Originated from the VPN client
     */
    public final static int AGENT = 1;

    /**
     * Originated from a DAV client
     */
    public final static int DAV_CLIENT = 2;

    /* Navigation context */

    /**
     * User console for normal user.
     */
    public static final int USER_CONSOLE_CONTEXT = 1;

    /**
     * Management console for super user or users that have been delegated
     * responsibility for management tasks.
     */
    public static final int MANAGEMENT_CONSOLE_CONTEXT = 2;

    /**
     * Setup console that may be accessed from the management console or when
     * the server has been started in setup mode
     */
    public static final int SETUP_CONSOLE_CONTEXT = 4;

    /**
     * Help context works slighly differently in that the sessions current
     * context is not actually changed.
     */
    public static final int HELP_CONTEXT = 8;

    /**
     * Convenience mask for all contexts
     */
    public static final int ALL_CONTEXTS = 255;

    /* Private instance variables */
    private User user;
    private InetAddress address;
    private Calendar logonTime;
    private int type;
    private String logonTicket;
    private int navigationContext;
    private HttpSession session;
    private int id;
    private String userAgent;
    private long lastAccessTime;
    private boolean invalidating;
    private Map<String, Object> attributes;
    private List<SessionInfoListener> listeners = new ArrayList<SessionInfoListener>();
    String uid = null;

    private boolean temporary;
    
    /* Private static variables */

    private static HashMap sessions = new HashMap();
    private static int nextId = 1;

    /**
     * Create a new {@link SessionInfo} object, assiging it the next Id.
     * 
     * @param session {@link HttpSession} originator of this session
     * @param logonTicket logon ticket
     * @param user user object
     * @param address address
     * @param type client type
     * @param userAgent user agent if known
     * @return session
     */
    public static SessionInfo nextSession(HttpSession session, String logonTicket, User user, InetAddress address, int type,
                                          String userAgent) {
        synchronized (sessions) {
            SessionInfo info = new SessionInfo(nextId, session, logonTicket, user, address, type, userAgent);
            if (LogonControllerFactory.getInstance().isAdministrator(user)) {
                info.setNavigationContext(SessionInfo.MANAGEMENT_CONSOLE_CONTEXT);
            }
            sessions.put(String.valueOf(nextId), info);
            nextId++;
            return info;
        }
    }

    /**
     * Get a session given its Id.
     * 
     * @param id session id
     * @return session
     */
    public static SessionInfo getSession(int id) {
        return (SessionInfo) sessions.get(String.valueOf(id));
    }

    /**
     * Release a session so its ID can be re-used
     */
    public void release() {
        CoreServlet.getServlet().removeCoreListener(this);
        synchronized (sessions) {
            sessions.remove(String.valueOf(id));

            // TODO implement a more efficient way of getting the next session
            // id

            Map.Entry e;
            int next = 1;
            boolean found;
            while (true) {
                found = false;
                for (Iterator i = sessions.entrySet().iterator(); i.hasNext();) {
                    e = (Map.Entry) i.next();
                    if (((SessionInfo) e.getValue()).getId() == next) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    nextId = next;
                    break;
                }
                next++;

            }
        }
    }

    /* Private constructor to prevent instantiation */

    private SessionInfo(int id, HttpSession session, String logonTicket, User user, InetAddress address, int type, String userAgent) {
        attributes = new HashMap<String, Object>();
        this.user = user;
        this.id = id;
        this.session = session;
        this.logonTicket = logonTicket;
        this.address = address;
        navigationContext = USER_CONSOLE_CONTEXT;
        this.type = type;
        this.userAgent = userAgent;
        logonTime = new GregorianCalendar();
        lastAccessTime = System.currentTimeMillis();

        /**
         * Generate a unique session id
         */
        Hash hash = new Hash(new MD5Digest());
        hash.putString(String.valueOf(logonTime));
        if(session != null) {
            hash.putString(session.getId());
        }
        hash.putInt(id);
        hash.putString(user.getPrincipalName());
        hash.putString(address.getHostAddress());
        byte[] tmp = hash.doFinal();
        uid = Util.toHexString(tmp);
        
        CoreServlet.getServlet().addCoreListener(this);
    }

    public Object setAttribute(String key, Object value) {
        if(value instanceof SessionInfoListener &&
                        !listeners.contains(value)) {
            listeners.add((SessionInfoListener)value);
        }
        return attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public Object removeAttribute(String key) {
        Object val = attributes.remove(key);
        if(val instanceof SessionInfoListener) {
            listeners.remove((SessionInfoListener)val);
        }
        return val;
    }
    
    /**
     * Get if this session is attached to a web session (i.e.
     * returns non null from {@link #getHttpSession()}. 
     * 
     * @return attached to web session
     */
    public boolean isAttachedToWebSession() {
        return session != null;
    }
    
    public String getUniqueSessionId() {
    	return uid;
    }

    public void access() {
        if(session != null) {
            context.access(session);
        }
        else {
            throw new IllegalStateException("Not attached to a web session.");
        }
    }

    /**
     * Get the sequential ID of this session
     * 
     * @return Id
     */
    public int getId() {
        return id;
    }

    /**
     * Get the {@link HttpSession} that originated this logon session
     * 
     * @return {@link HttpSession} originator
     */
    public HttpSession getHttpSession() {
        return session;
    }

    /**
     * Get the logon ticket for this session
     * 
     * @return logon ticket
     */
    public String getLogonTicket() {
        return logonTicket;
    }

    /**
     * Get the type of session. May be one of {@link #UI}, {@link #AGENT} or
     * {@link #DAV_CLIENT}.
     * 
     * 
     * @return session type
     */
    public int getType() {
        return type;
    }

    /**
     * Set the type of session. May be one of {@link #UI}, {@link #AGENT} or
     * {@link #DAV_CLIENT}.
     * 
     * 
     * @param type session type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Get the internet address that this session originated from
     * 
     * @return originating internet address
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Get the time this session started
     * 
     * @return logon time
     */
    public Calendar getLogonTime() {
        return logonTime;
    }

    /**
     * Get the user that originated this session
     * 
     * @return user
     */
    public User getUser() {
        return user;
    }

    /**
     * Convenience method to get the {@link Realm} of the user that this
     * originated this session
     * 
     * @return realm
     */
    public Realm getRealm() {
        return user == null ? null : user.getRealm();
    }

    /**
     * Convenience method to get the resource ID of the {@link Realm} of the
     * user that this originated this session
     * 
     * @return realm
     */
    public int getRealmId() {
        Realm r = user == null ? null : user.getRealm();
        return r == null ? 0 : r.getResourceId();
    }

    /**
     * Set the {@link HttpSession} that owns or now owns this session.
     * 
     * @param session session
     */
    public void setSession(HttpSession session) {
        this.session = session;
    }
    
    /**
     * Is the navigation context currently set to user console?
     * @return <tt>true</tt> if the user is currently in the user console.
     */
    public boolean isUserConsoleContext() {
        return USER_CONSOLE_CONTEXT == getNavigationContext();
    }
    
    /**
     * Is the navigation context currently set to management console?
     * @return <tt>true</tt> if the user is currently in the management console.
     */
    public boolean isManagementConsoleContext() {
        return MANAGEMENT_CONSOLE_CONTEXT == getNavigationContext();
    }

    /**
     * Get the navigation context the user is currently in. May be one of
     * {@link #USER_CONSOLE_CONTEXT} or {@link #MANAGEMENT_CONSOLE_CONTEXT}.
     * 
     * @return navigation context
     */
    public int getNavigationContext() {
        return navigationContext;
    }

    /**
     * Set the navigation context the user is currently in. May be one of
     * {@link #USER_CONSOLE_CONTEXT} or {@link #MANAGEMENT_CONSOLE_CONTEXT}.
     * 
     * @param navigationContext new navigation context
     */
    public void setNavigationContext(int navigationContext) {
        this.navigationContext = navigationContext;
    }

    /**
     * Get the scheme that was used to logon.
     * 
     * @return logon scheme
     */
    public AuthenticationScheme getCredentials() {
        return (AuthenticationScheme) session.getAttribute(Constants.AUTH_SESSION);
    }

    /**
     * Get the user agent.
     * 
     * @return user agent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * Set the user for this session
     * 
     * @param user user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Try to determine what type of client is connecting based on the user
     * agent. If <code>null</code> is supplied then the type will just be
     * returned as {@link SessionInfo#UI}.
     * <p>
     * When not null, first the user agent is tested to see if it is the VPN
     * client (agent). If so then {@link SessionInfo#AGENT} will be returned.
     * <p>
     * If not the VPN client, then the file <i>conf/dav.agents</i> will be
     * examined to see if any of the patterns it contains match the supplied
     * agent string. If so, the the session is of type
     * {@link SessionInfo#DAV_CLIENT}.
     * <p>
     * If none of the conditions are met, then the default of
     * {@link SessionInfo#UI} is returned.
     * 
     * @param userAgent user agent string
     * @return session type
     * @see SessionInfo#UI
     * @see SessionInfo#DAV_CLIENT
     * @see SessionInfo#AGENT
     */
    public static int getSessionTypeForUserAgent(String userAgent) {
        if (userAgent == null) {
            return UI;
        }
        if (userAgent.equals("Agent")) {
            return AGENT;
        }
        if (davUserAgents == null) {
            davUserAgents = new ArrayList();
            File f = new File(context.getConfDirectory(), "dav.agents");
            FileInputStream fin = null;
            try {
                fin = new FileInputStream(f);
                BufferedReader br = new BufferedReader(new InputStreamReader(fin));
                String line = null;
                while ((line = br.readLine()) != null) {
                    line = Util.trimBoth(line);
                    if (!line.startsWith("#")) {
                        davUserAgents.add(line);
                    }
                }
            } catch (IOException ioe) {
                log.warn("Failed to read " + f.getAbsolutePath() + ". Will not be able to identify DAV clients.");
            } finally {
                Util.closeStream(fin);
            }
        }
        for (Iterator i = davUserAgents.iterator(); i.hasNext();) {
            String us = (String) i.next();
            if (userAgent.matches(us)) {
                return SessionInfo.DAV_CLIENT;
            }
        }
        return SessionInfo.UI;
    }

    public String toString() {
        return session.getId() + "/" + user.getPrincipalName();
    }

    public boolean isInvalidating() {
        return invalidating;
    }

    public void invalidate() {
        if (session != null) {
            invalidating = true;
            session.invalidate();
            invalidating = false;
        }
        for(SessionInfoListener l : listeners) {
            l.invalidated();
        }
    }

    public void coreEvent(CoreEvent evt) {
        if (evt.getId() == CoreEventConstants.GRANT_POLICY_TO_PRINCIPAL
                        || evt.getId() == CoreEventConstants.REVOKE_POLICY_FROM_PRINCIPAL
                        || evt.getId() == CoreEventConstants.RESOURCE_DETACHED_FROM_POLICY
                        || evt.getId() == CoreEventConstants.RESOURCE_ATTACHED_TO_POLICY) {
            if (session != null) {
                synchronized (session) {
                    CoreUtil.resetMainNavigation(getHttpSession());
                }
            }
        }
    }

    /**
     * Set whether this session is temporary. If it is, it will be destroyed when the request
     * is completed. This is used for WebDAV clients that do not support cookies.
     * 
     * @param temporary temporary
     */
    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }
    
    /**
     * Get whether this session is temporary.  If it is, it will be destroyed when the request
     * is completed. This is used for WebDAV clients that do not support cookies.
     * 
     * @return temporary
     */
    public boolean isTemporary() {
        return temporary;
    }

}