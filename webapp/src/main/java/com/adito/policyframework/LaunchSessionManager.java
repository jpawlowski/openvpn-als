
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
			
package com.adito.policyframework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.adito.security.Constants;
import com.adito.security.SessionInfo;
import com.adito.security.SessionInfoListener;
import com.adito.security.User;
import com.adito.util.TicketGenerator;


/**
 * Manager for creating and locating {@link LaunchSession} objects.
 */
public class LaunchSessionManager {	 
		

	//	Private instance variables
	
	private Map<User, List<LaunchSession>> userSessionsList = new HashMap<User, List<LaunchSession>>();
	private Map<String, LaunchSession> launchSessions = new HashMap<String, LaunchSession>();
		
	/**
	 * Get all of the launch sessions for a specified user.
	 * 
	 * @param user user
	 * @return launch sessions
	 */
	public Collection<LaunchSession> getLaunchSessions(User user) {
		return userSessionsList.get(user);
	}
	
	/**
	 * Get all launch sessions
	 * 
	 * @return launch sessions
	 */
	public Collection<LaunchSession> getLaunchSession() {
		return launchSessions.values();
	}
	
	/**
	 * Get <i>Launch Session</i> for a given launch ID.
	 * <p>
	 * Note, whereever possible {@link #getLaunchSession(SessionInfo, String)}
	 * should be used as it will help keep the resource sessions secure and
	 * only accessable by their users sessions.
	 * 
	 * @param launchId resource sessions ID
	 * @return launch session
	 */
	public LaunchSession getLaunchSession(String launchId) {
		return (LaunchSession)launchSessions.get(launchId);		
	}

    /**
     * Get the <i>Resource Session</i> for a given resource ID from the
     * session. <code>null</code> will be returned if no such session exists.
     * <p>
     * Use of this method is preferable to {@link #getLaunchSession(String)}.
     * 
     * @param session session
     * @param launchId launchId
     * @return resource session
     */
    public LaunchSession getLaunchSession(SessionInfo session, String launchId) {
    	SessionsLaunchSessions launchSessions = (SessionsLaunchSessions)session.getAttribute(Constants.LAUNCH_SESSIONS);
        return launchSessions == null ? null : launchSessions.get(launchId);
    }
    
    /**
     * Remove a launch session from the session it is attached to.
     * 
     * @param launchSession
     */
    public void removeLaunchSession(LaunchSession launchSession) {
    	synchronized (launchSessions) {
			launchSessions.remove(launchSession.getId());
	    	SessionsLaunchSessions sessionLaunchSessions = (SessionsLaunchSessions)launchSession.getSession().getAttribute(Constants.LAUNCH_SESSIONS);
	    	if(sessionLaunchSessions != null) {
	    		sessionLaunchSessions.remove(launchSession.getId());
	    		if(sessionLaunchSessions.size() == 0) {
	    			launchSession.getSession().removeAttribute(Constants.LAUNCH_SESSIONS);
	    		}
	    	}
	    	
		}

        synchronized(userSessionsList) {
	        List<LaunchSession> us = userSessionsList.get(launchSession.getSession().getUser());
	        if(us != null) {
	        	us.remove(launchSession);
	        	if(us.size() == 0) {
	        		userSessionsList.remove(launchSession.getSession().getUser());
	        	}
	        }
        }
    }

    /**
     * Create a new resource session
     * 
     * @param session user session
     * @param resource resource
     * @param policy policy
     * @return resource session
     */
    public LaunchSession createLaunchSession(SessionInfo session, Resource resource, Policy policy) {
    	
    	// Create the new resource session, making sure there are no ID conflicts
        String launchId = null;
        LaunchSession launchSession = null;
        synchronized(launchSessions) {
        	
        	while(true) {
		        launchId = TicketGenerator.getInstance().generateUniqueTicket("l", 7);
		        if(!launchSessions.containsKey(launchId)) {
			        launchSession = new LaunchSession(launchId, session, resource, policy);
		        	launchSessions.put(launchId, launchSession);
		        	break;
		        }
        	}        	
        }

        // Get the map of resource sessions for this users session 
    	SessionsLaunchSessions sessionLaunchSessions = (SessionsLaunchSessions)session.getAttribute(Constants.LAUNCH_SESSIONS);
        if (sessionLaunchSessions == null) {
        	sessionLaunchSessions = new SessionsLaunchSessions(launchSession);
            session.setAttribute(Constants.LAUNCH_SESSIONS, sessionLaunchSessions);
        }
        sessionLaunchSessions.put(launchId, launchSession);
        

        // Update the user / resource sessions map
        synchronized(userSessionsList) {
	        List<LaunchSession> us = userSessionsList.get(session.getUser());
	        if(us == null) {
	        	us = new ArrayList<LaunchSession>();
	        	userSessionsList.put(session.getUser(), us);
	        }
	        us.add(launchSession);
        }
        
        return launchSession;
    }

	/**
	 * Get a collection of <i>Resource Sessions</i> for a given resource type.
	 * 
	 * @param session session
	 * @param resourceType resource type
	 * @return resource sessions
	 */
	public Collection<LaunchSession> getLaunchSessionsForType(SessionInfo session, ResourceType resourceType) {
		SessionsLaunchSessions launchSessions = (SessionsLaunchSessions)session.getAttribute(Constants.LAUNCH_SESSIONS);
	    List<LaunchSession> l = new ArrayList<LaunchSession>();
	    if (launchSessions != null) {
	        for (LaunchSession launchSession : launchSessions.values()) {
	            if (launchSession.getResource() != null && launchSession.getResource().getResourceType() == resourceType) {
	                l.add(launchSession);
	            }
	        }
	    }
	    return l;
	}


	/**
	 * Get the first <i>Launch Session</i> for a given resource 
	 * 
	 * @param session session
	 * @param resource resource
	 * @return resource 
	 */
	public LaunchSession getFirstLaunchSessionForResource(SessionInfo session, Resource resource) {
		LaunchSession launchSession = null;
		Iterator<LaunchSession> i = getLaunchSessionsForResource(session, resource).iterator();
		if(i.hasNext()) {
			launchSession = i.next();
		}
		return launchSession;
	}


	/**
	 * Get a collection of <i>Resource Sessions</i> for a given resource.
	 * 
	 * @param session session
	 * @param resource resource
	 * @return resource sessions
	 */
	public Collection<LaunchSession> getLaunchSessionsForResource(SessionInfo session, Resource resource) {
		SessionsLaunchSessions launchSessions = (SessionsLaunchSessions)session.getAttribute(Constants.LAUNCH_SESSIONS);
	    List<LaunchSession> l = new ArrayList<LaunchSession>();
	    if (launchSessions != null) {
	        for (LaunchSession launchSession : launchSessions.values()) {
	            if (launchSession.getResource() != null && launchSession.getResource().equals(resource)) {
	                l.add(launchSession);
	            }
	        }
	    }
	    return l;
	}

    /**
     * Get a policy for launching a resource given a request. The request is
     * examined for a <i>policy</i> parameter. If no policy paramter is found,
     * it is not a number or if the policy doesn't exist an {@link IllegalArgumentException}
     * will be thrown. If -1 is supplied, the first granting policy will be used. If no
     * policy grants access, then the first policy attached to the resource is
     * found.  
     * 
     * @param request request to retrieve policy parameter from
     * @param session session
     * @param resource resource
     * @return policy policy
     * @throws IllegalArgumentException if policy id missing or invalid
     * @throws Exception on any other error
     */
    public static Policy getLaunchRequestPolicy(HttpServletRequest request, SessionInfo session, Resource resource)
                    throws IllegalArgumentException, Exception {
        try {
        	// LDP - Allow the request to be null
        	
        	/* BPS - If you are going to make changes like this please please please please please update the javadoc to reflect it
        	 * Or at the very least remove the javadoc completely. Its far better to have no javadoc than javadoc that is 
        	 * misleading or downright wrong </rant>
        	 */
            int policyId = request==null ? -1 : Integer.parseInt(String.valueOf(request.getParameter("policy")));
            Policy policy = policyId == -1 ? PolicyDatabaseFactory.getInstance().getGrantingPolicyForUser(session.getUser(), resource)
                            : PolicyDatabaseFactory.getInstance().getPolicy(policyId);
            if (policy == null) {
                if(policyId == -1) {
                    List l = PolicyDatabaseFactory.getInstance().getPoliciesAttachedToResource(resource,
                                    session.getUser().getRealm());
                    if(l.size() == 0)
                        throw new Exception("Resource is not attached to any policies.");
                    return (Policy)l.get(0);
                }
                throw new IllegalArgumentException("No policy with ID of " + policy);
            }
            return policy;
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("No policy ID provided.");
        }
    }
    
    class SessionsLaunchSessions extends HashMap<String, LaunchSession> implements SessionInfoListener {
    	
    	private LaunchSession launchSession;
    	
    	SessionsLaunchSessions(LaunchSession launchSession) {
    		this.launchSession = launchSession;
    	}
    	 
		public void invalidated() {
			synchronized(userSessionsList) {
				List<LaunchSession> l = userSessionsList.get(launchSession.getSession().getUser());
				for(LaunchSession r : values()) {
					l.remove(r);
					launchSessions.remove(r.getId());
				}
				if(l.size() == 0) {
					userSessionsList.remove(launchSession.getSession().getUser());
				}
			}
		}    	
    }
}
