
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

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.cache.Cache;
import org.apache.commons.cache.FileStash;
import org.apache.commons.cache.SimpleCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.ContextHolder;
import com.adito.boot.Util;
import com.adito.core.CoreUtil;
import com.adito.core.UserDatabaseManager;
import com.adito.policyframework.Policy;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.ResourceUtil;
import com.adito.realms.Realm;

/**
 * <p>
 * State machine which holds the logon state so that the display to the user can
 * be obfuscated.
 */
public class LogonStateAndCache {

    final static Log log = LogFactory.getLog(LogonStateAndCache.class);

    public static final String LOGON_STATE_MACHINE = "logonStateMachine";

    public static final int STATE_INITIAL = 0;
    public static final int STATE_STARTED = 1;
    public static final int STATE_DISPLAY_USERNAME_ENTRY = 2;
    public static final int STATE_DISPLAY_USERNAME_ENTERED = 3;
    public static final int STATE_UNKNOWN_USERNAME = 4;
    public static final int STATE_UNKNOWN_USERNAME_PROMPT_FOR_PASSWORD = 5;
    public static final int STATE_USERNAME_KNOWN = 6;
    public static final int STATE_KNOWN_USERNAME_SINGLE_SCHEME = 7;
    public static final int STATE_KNOWN_USERNAME_MULTIPLE_SCHEMES = 8;
    public static final int STATE_KNOWN_USERNAME_WRONG_PASSWORD = 9;
    public static final int STATE_VALID_LOGON = 10;
    public static final int STATE_RETURN_TO_LOGON = 11;
    public static final int STATE_KNOWN_USERNAME_NO_SCHEME_SPOOF_PASSWORD_ENTRY = 12;
    public static final int STATE_KNOWN_USERNAME_MULTIPLE_SCHEMES_SELECT = 13;

    private int state = STATE_INITIAL;
    private User user;
    private String username;
    private List<Integer> resourceIds = null;
    private List<AuthenticationScheme> authSchemes = new ArrayList<AuthenticationScheme>();
    private AuthenticationScheme highestPriorityScheme = null;
    private String spoofedUsername;
    
    /* Spoof cache used to store fake authentication schemes
     * 
     * TODO Default to maximum of 2000 fake users. This should be configurable 
     */
    private static Cache spoofCache;    
    static {
    	File dir = new File(ContextHolder.getContext().getTempDirectory(), "spoof");
    	if(dir.exists()) {
    	    Util.delTree(dir);
    	}
    	spoofCache = new SimpleCache(new FileStash(FileStash.DEFAULT_MAX_BYTES, 2000, 
    			new File[] { dir }, true));
    }
   
    public LogonStateAndCache(int startState, HttpSession session) {
        super();
        session.setAttribute(LOGON_STATE_MACHINE, this);
        this.setState(startState);
        
    }

    public int getState() {
        return state;
    }
    
    public String getSpoofedUsername() {
        return spoofedUsername;
    }

    public void setState(int newState) {
        if (log.isDebugEnabled()){
            log.debug("State" + state + " is to be changed to " + newState);
        }
        this.state = newState;
        
        if (resourceIds != null && this.state == STATE_USERNAME_KNOWN){
            if (resourceIds.size() == 0) {
                this.setState(LogonStateAndCache.STATE_KNOWN_USERNAME_NO_SCHEME_SPOOF_PASSWORD_ENTRY);
            } else if (resourceIds.size() == 1) {
                this.setState(LogonStateAndCache.STATE_KNOWN_USERNAME_SINGLE_SCHEME);
            } else if (resourceIds.size() > 1) {
                this.setState(LogonStateAndCache.STATE_KNOWN_USERNAME_MULTIPLE_SCHEMES);
            }
        }
    }

    public void setUser(User user) throws Exception {
        this.user = user;
        this.authSchemes.clear();
        setResourceIds();
        this.highestPriorityScheme.setAccountLock(LogonControllerFactory.getInstance().checkForAccountLock(user.getPrincipalName(), user.getRealm().getResourceName()));
    }

    public boolean hasUser() {
        return user == null ? false : true;
    }

    public User getUser() {
        return user;
    }

    public boolean enabledSchemesGraeterThanOne() {
        return this.authSchemes.size() > 1;
    }

    public List getResourceIds() {
        return resourceIds;
    }

    private void setResourceIds() throws Exception {
        List resourceIds = ResourceUtil.getSignonAuthenticationSchemeIDs(user);

        int highestPriority = Integer.MAX_VALUE;
        highestPriorityScheme = null;
        for (AuthenticationScheme element : SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequences()) {
            if (resourceIds.contains(new Integer(element.getResourceId())) && !element.isSystemScheme() && element.getEnabled()) {
                this.authSchemes.add(element);
                if (element.getPriorityInt() < highestPriority) {
                    highestPriority = element.getPriorityInt();
                    highestPriorityScheme = element;
                }
            }
            else{
                resourceIds.remove(new Integer(element.getResourceId()));
            }
        }
        if(highestPriorityScheme == null) {
        	throw new Exception("User is not attached to any policies that are assigned to any valid authentication schemes. " +
        			"This may be because they were assigned a scheme that contains an authentication module that no longer exists.");
        }
        this.resourceIds = resourceIds;
        this.highestPriorityScheme.setUser(user);
        this.setState(LogonStateAndCache.STATE_USERNAME_KNOWN);
    }

    public AuthenticationScheme getHighestPriorityScheme() {
        return highestPriorityScheme;
    }

    public void forceHighestPriorityScheme(String id, String username) throws Exception {
        this.highestPriorityScheme = SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequence(Integer.parseInt(id));
    	if(!spoofCache.contains(username)) {
	        if (resourceIds.contains(new Integer(id))) {
	            this.highestPriorityScheme.setUser(user);
	        }
	        else {
	            throw new Exception("The selected scheme is not valid for the user.");
	        }
    	}
    }

    public List getAuthSchemes() {
        return authSchemes;
    }

    /**
     * Randomly choose a list of spoofed authentications schemes. This is
     * to prevent an attacker from determining if a username is invalid or
     * not by looking if there are multiple authentication schemes
     * available. If there are none, he can assume the user is invalid.
     * This method does its best to create a credible random list of 
     * possible schemes. None of them will actually work, but they 
     * will be presented to attacker.
     * 
     * @param username username
     * @throws Exception
     */
    public void setSpoofedHighestPriorityScheme(String username) throws Exception {
        Calendar now = Calendar.getInstance();
        authSchemes = new ArrayList<AuthenticationScheme>();
        spoofedUsername = username;

        // Get the valid schemes// Look for cached scheme list
        int[] authSchemeIds = (int[]) spoofCache.retrieve(username);
        if (spoofCache.contains(username)) {
            if (log.isDebugEnabled()) {
                log.debug("Using cached spoofed schemes for " + username);
            }
            for (int schemeId : authSchemeIds) {
                AuthenticationScheme scheme = SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequence(schemeId);
                // The scheme could have been deleted since it was cached 
                if (scheme != null) {
                    authSchemes.add(scheme);
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Building new list of spoofed schemes for " + username);
            }

            //          Get the valid schemes
            List<AuthenticationScheme> schemes = SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequences();
            for (AuthenticationScheme scheme : new ArrayList<AuthenticationScheme>(schemes)) {
                if (scheme.isSystemScheme()) {
                    schemes.remove(scheme);
                }
            }

            // Add any schemes that are available to anyone
            Realm realm = UserDatabaseManager.getInstance().getDefaultRealm();
            Policy p = PolicyDatabaseFactory.getInstance().getPolicy(PolicyDatabaseFactory.getInstance()
                .getEveryonePolicyIDForRealm(realm));
            for (AuthenticationScheme scheme : schemes) {
                if (PolicyDatabaseFactory.getInstance().isResourceAttachedToPolicy(scheme, p, realm)) {
                    authSchemes.add(scheme);
                }
            }

            // If no schemes were available to everyone, add a dummy default
            if (authSchemes.size() == 0) {
                AuthenticationScheme scheme = new DefaultAuthenticationScheme(-1, -1, "", "", now, now, true, 0);
                scheme.addModule("Password");
                authSchemes.add(scheme);
            }

            // If there is only one scheme, pick some randomly, each on gets 50/50 chance
            if (authSchemes.size() == 1 && schemes.size() > 1) {
                for (AuthenticationScheme scheme : schemes) {
                    if (scheme != authSchemes.get(0) && Math.random() >= 0.5) {
                        authSchemes.add(scheme);
                    }
                }
            }

            // If there is still only one scheme, pick a single random one
            if (authSchemes.size() == 1 && schemes.size() > 1) {
                authSchemes.add(authSchemes.get(1 + (int) (Math.random() * (authSchemes.size() - 1))));
            }

            /* Cache the scheme id's so if the same user ID is attempted
             * the same spoofed schemes will appear
             */
            int[] schemeNames = new int[authSchemes.size()];
            for (int idx = authSchemes.size() - 1; idx >= 0; idx--) {
                schemeNames[idx] = authSchemes.get(idx).getResourceId();
            }
            // TODO Cache them for 3 days - make configurable?                  
            if (log.isDebugEnabled()) {
                log.debug("Caching spoofed schemes for " + username);
            }
            CoreUtil.storeToCache(spoofCache, username, schemeNames, 360000 * 24 * 3, 0);
        }

        // 
        resourceIds = new ArrayList<Integer>();
        this.highestPriorityScheme = authSchemes.get(0);
    }

	/**
	 * Remove cached spoofed user information. This should be called
	 * as a user is succesfully found. This deals with the situation where
	 * a user tries to logon with an invalid name (a user that has not
	 * yet been created). This fails, but the administrator later adds
	 * the user. The user then tries to logon again before the spoof cache
	 * is cleared. Unless this method is called as soon as the valid
	 * username is found, the spoofing mechanism will think the user
	 * is still invalid.
	 *  
	 * @param username username to remove from spoof cache
	 */
	public void removeFromSpoofCache(String username) {
		spoofCache.clear(username);		
	}
}