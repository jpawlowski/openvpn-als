
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
			
package com.adito.security.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.adito.boot.Util;
import com.adito.core.BundleActionMessage;
import com.adito.core.UserDatabaseManager;
import com.adito.core.forms.CoreForm;
import com.adito.security.Constants;
import com.adito.security.User;

public class LogonForm extends CoreForm {
    
    private String username;
    private String password;
    private boolean sessionLocked;
    private boolean hasMoreAuthenticationSchemes;
    private int currentModuleIndex;
    private boolean obfuscatedMode = true;
    private boolean javaScript = true;
    private String realmName = UserDatabaseManager.DEFAULT_REALM_NAME;
    

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password.trim();
    }

    /**
     * @return Returns the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username The username to set.
     */
    public void setUsername(String username) {
    	if(this.username == null && !Util.isNullOrTrimmedBlank(username)) {
    		this.username = username.trim();
    	}
    }
    
    public void initUser() {
    	username = null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        User sessionLockUser = (User)request.getSession().getAttribute(Constants.SESSION_LOCKED); 
        sessionLocked = sessionLockUser != null;
        username = sessionLocked ? sessionLockUser.getPrincipalName() : null;
        password = null;
        hasMoreAuthenticationSchemes = false;
        javaScript = true;
    }
    
    public boolean getUsernameRequired() {
        if (obfuscatedMode){
            return false;
            
        }
        else{
            return !getSessionLocked() && currentModuleIndex == 0;
        }
    }

    /**
     * @return Returns the sessionLocked.
     */
    public boolean getSessionLocked() {
        return sessionLocked;
    }

    /**
     * @param sessionLocked The sessionLocked to set.
     */
    public void setSessionLocked(boolean sessionLocked) {
        this.sessionLocked = sessionLocked;
    }

    public boolean getHasMoreAuthenticationSchemes() {
      return hasMoreAuthenticationSchemes;    
    }

    public void setHasMoreAuthenticationSchemes(boolean hasMoreAuthenticationSchemes) {
      this.hasMoreAuthenticationSchemes = hasMoreAuthenticationSchemes;    
    }

    /**
     * @param currentModuleIndex
     */
    public void setCurrentModuleIndex(int currentModuleIndex) {
        this.currentModuleIndex = currentModuleIndex;        
    }

    public String getRealmName() {
        return realmName;
}
    public void setRealmName(String domainName) {
        this.realmName = domainName;
    }
    
    @Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        ActionErrors errors = new ActionErrors();
        if (!javaScript) {
            errors.add(Globals.ERROR_KEY, new BundleActionMessage("security", "login.disabled.java.script.logon.disallowed"));
        }
        return errors;
    }

    public boolean isJavaScript() {
        return javaScript;
    }

    public void setJavaScript(boolean javaScript) {
        this.javaScript = javaScript;
    }
}
