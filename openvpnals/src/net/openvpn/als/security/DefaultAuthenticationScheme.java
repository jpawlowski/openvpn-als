
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.policyframework.AbstractResource;
import net.openvpn.als.policyframework.PolicyConstants;

/**
 * This is the default implementation of an <i>Authentication Scheme</i> that
 * loads the scheme from the <i>System Database</i> as if it were a {@link net.openvpn.als.policyframework.Resource}.
 */
public class DefaultAuthenticationScheme extends AbstractResource implements AuthenticationScheme, Serializable {
    
    final static Log log = LogFactory.getLog(DefaultAuthenticationScheme.class);
    
    // Private instance variables
    
    private List<String> modules;
    private HttpSession servletSession;
    private int current;
    private List<AuthenticationModule> authenticationModules;
    private User user;
    private List<Credentials> allCredentials;
    private AccountLock lock;
    private boolean enabled;
    private int priority;

    /**
     * Constructor
     * @param realmID 
     * @param resourceName
     * @param resourceDescription
     * @param enabled enabled
     * @param priority 
     */
    public DefaultAuthenticationScheme(int realmID, String resourceName, String resourceDescription, boolean enabled, int priority) {
        this(realmID, -1, resourceName, resourceDescription, Calendar.getInstance(), Calendar.getInstance(), enabled, priority);
    }
    
    /**
     * Constructor
     * @param realmID 
     * @param resourceId
     * @param resourceName
     * @param resourceDescription
     * @param dateAmended
     * @param dateCreated
     * @param enabled enabled
     * @param priority 
     */
    public DefaultAuthenticationScheme(int realmID, int resourceId, String resourceName, String resourceDescription, Calendar dateAmended, Calendar dateCreated, boolean enabled, int priority) {
        super(realmID, PolicyConstants.AUTHENTICATION_SCHEMES_RESOURCE_TYPE, resourceId, resourceName, resourceDescription, dateAmended, dateCreated);
        current = -1;
        modules = new ArrayList<String>();
        allCredentials = new ArrayList<Credentials>();
        this.enabled = enabled;
        this.priority = priority;
    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#addModule(java.lang.String)
     */
    public void addModule(String module) {
        if(!modules.contains(module)) {
            modules.add(module);
        }
    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#hasModule(java.lang.String)
     */
    public boolean hasModule(String name) {
        return modules.contains(name);
    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#removeModule(java.lang.String)
     */
    public void removeModule(String module) {
        modules.remove(module);
    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#modules()
     */
    public Iterator<String> modules() {
        return modules.iterator();
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#init(javax.servlet.http.HttpSession)
     */
    public void init(HttpSession servletSession) throws Exception {
        this.servletSession = servletSession;   
        authenticationModules = new ArrayList<AuthenticationModule>();   
        for(Iterator<String> i = modules.iterator(); i.hasNext(); ) {
            String moduleId = i.next();
            AuthenticationModule module = AuthenticationModuleManager.getInstance().createModule(moduleId);
            authenticationModules.add(module);
            module.init(this);
        }
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#getCurrentModuleIndex()
     */
    public int getCurrentModuleIndex() {
        return current;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#getUser()
     */
    public User getUser() {
        return user;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#setUser(net.openvpn.als.security.User)
     */
    public void setUser(User user) {
        this.user = user;        
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#getServletSession()
     */
    public HttpSession getServletSession() {
        return servletSession;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#nextAuthenticationModule()
     */
    public AuthenticationModule nextAuthenticationModule() {
        if( ( current + 1 ) < authenticationModules.size()) {
            AuthenticationModule mod = (AuthenticationModule)authenticationModules.get(++current);
            return mod;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#currentAuthenticationModule()
     */
    public AuthenticationModule currentAuthenticationModule() {
        if(current != -1) {
            AuthenticationModule mod = (AuthenticationModule)authenticationModules.get(current);
            return mod;
        }
        return null;        
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#authenticationComplete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void authenticationComplete(HttpServletRequest request, HttpServletResponse response) throws Exception {
        for(Iterator i = authenticationModules.iterator();i.hasNext(); ) {
            AuthenticationModule mod = (AuthenticationModule)i.next();
            if (log.isDebugEnabled())
            	log.debug("Informing module " + mod.getName() + " that authentication is complete");
            mod.authenticationComplete();
            
            // Only inform the first module when the session is locked
            if(request.getSession().getAttribute(Constants.SESSION_LOCKED) != null) {
                break;
            }
        }
        LogonControllerFactory.getInstance().logon(request, response, this);
        
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#getUsername()
     */
    public String getUsername() {
        return user.getPrincipalName();
    }


    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#addCredentials(net.openvpn.als.security.Credentials)
     */
    public void addCredentials(Credentials credentials) {
        allCredentials.add(credentials);                
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#credentials()
     */
    public Iterator credentials() {
        return allCredentials.iterator();
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#setAccountLock(net.openvpn.als.security.AccountLock)
     */
    public void setAccountLock(AccountLock lock) {
        this.lock = lock;        
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#getAccountLock()
     */
    public AccountLock getAccountLock() {
        return lock;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#moveUp(java.lang.String)
     */
    public void moveUp(String module) {
        int idx = modules.indexOf(module);
        if(idx > 0) {
            String swap = modules.get(idx - 1);
            modules.remove(idx - 1);
            modules.add(idx, swap);
        }        
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#clearModules()
     */
    public void clearModules() {
        modules.clear();
        
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#moveDown(java.lang.String)
     */
    public void moveDown(String module) {
        int idx = modules.indexOf(module);
        if( ( idx + 1 ) < modules.size() ) {
            String swap = modules.get(idx + 1);
            modules.remove(idx + 1);
            modules.add(idx, swap);
        }        
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#getModuleCount()
     */
    public int getModuleCount() {
        return modules.size();
    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#getModule(int)
     */
    public String getModule(int index) {
        return modules.get(index);
    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#getSessionLocked()
     */
    public boolean getSessionLocked() {
        return getServletSession() != null ? ( getServletSession().getAttribute(Constants.SESSION_LOCKED) != null ) : false;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#getSchemeName()
     */
    public String getSchemeName() {
        return getResourceName();
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#getEnabled()
     */
    public boolean getEnabled() {
        return enabled;
    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#isSystemScheme()
     */
    public boolean isSystemScheme() {
        if(modules.size() == 0) {
            return false;
        }
        for(Iterator i = modules(); i.hasNext(); ) {
            String mod = (String)i.next();
            if(!AuthenticationModuleManager.getInstance().getModuleDefinition(mod).getSystem()) {
                return false;
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#getModules()
     */
    public String[] getModules(){
        return modules.toArray(new String[modules.size()]);
    }
    
    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#getPriority()
     */
    public String getPriority() {
        return String.valueOf(priority);
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#getPriorityInt()
     */
    public int getPriorityInt(){
        return priority;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.security.AuthenticationScheme#setPriorityInt(int)
     */
    public void setPriorityInt(int priority){
        this.priority = priority;
    }
}
