
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

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.adito.policyframework.Resource;

/**
 */
public interface AuthenticationScheme extends Resource {

    /**
     * @param session
     * @throws Exception
     */
    public void init(HttpSession session) throws Exception;

    /**
     * @return int
     */
    public int getCurrentModuleIndex();

    /**
     * @return int
     */
    public int getModuleCount();

    /**
     * Get if this scheme contains the specified module
     * 
     * @param name name of module to test for
     * @return has module
     */
    public boolean hasModule(String name);
    
    /**
     * Remove a module from this scheme
     * 
     * @param module module to remove
     */
    public void removeModule(String module);

    /**
     * Get an iterator of modules contained within this scheme.
     * 
     * @return modules
     */
    public Iterator<String> modules();

    /**
     * Add a new authentication module to this scheme
     * 
     * @param module name of module to add
     */
    public void addModule(String module);

    /**
     * Move the specified module up one in the list. If the module is 
     * already at the top of the scheme no action will occur.
     * 
     * @param module module to move up in the scheme
     */
    public void moveUp(String module);

    /**
     * Move the specified module down one in the list. If the module is 
     * already at the bottom of the scheme no action will occur.
     * 
     * @param module module to move down in the scheme
     */
    public void moveDown(String module);

    /**
     * Remove all modules from this sequence
     */
    public void clearModules();

    /**
     * @return User
     */
    public User getUser();

    /**
     * @param user
     */
    public void setUser(User user);

    /**
     * @return HttpSession
     */
    public HttpSession getServletSession();

    /**
     * @return AuthenticationModule
     */
    public AuthenticationModule nextAuthenticationModule();

    /**
     * @return AuthenticationModule
     */
    public AuthenticationModule currentAuthenticationModule();

    /**
     * @param request
     * @param response
     * @throws Exception
     */
    public void authenticationComplete(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * @return String
     */
    public String getUsername();

    /**
     * @return String[]
     */
    public String[] getModules();

    /**
     * Get the module at the specified index.
     * 
     * @param index index of module
     * @return module
     */
    public String getModule(int index);

    /**
     * @return String
     */
    public String getSchemeName();

    /**
     * @param credentials
     */
    public void addCredentials(Credentials credentials);

    /**
     * @return Iterator
     */
    public Iterator credentials();

    /**
     * @param lock
     */
    public void setAccountLock(AccountLock lock);

    /**
     * @return AccountLock
     */
    public AccountLock getAccountLock();

    /**
     * @return boolean
     */
    public boolean getSessionLocked();

    /**
     * @return boolean
     */
    public boolean getEnabled();

    /**
     * @return int
     */
    public int getPriorityInt();
    
    /**
     * @return String
     */
    public String getPriority();

    /**
     * @param priority
     */
    public void setPriorityInt(int priority);

    /**
     * Set whether this scheme is enabled
     * 
     * @param enabled enabled
     */
    public void setEnabled(boolean enabled);
    
    /**
     * Get if the scheme contains only system authentication modules
     * 
     * @return system authentication modules only
     */
    public boolean isSystemScheme();
}
