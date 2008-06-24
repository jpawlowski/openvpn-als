
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
			
package com.adito.properties.impl.profile;

import com.adito.boot.AbstractPropertyKey;
import com.adito.boot.Util;
import com.adito.core.UserDatabaseManager;
import com.adito.properties.PropertyProfile;
import com.adito.security.Constants;
import com.adito.security.SessionInfo;
import com.adito.security.User;

public class ProfilePropertyKey extends AbstractPropertyKey {
    
    private int profile;
    private String username;
    private int realm;

    public ProfilePropertyKey(String name, SessionInfo session) {
    	super(name, ProfileProperties.NAME);
    	if(session != null && session.getHttpSession() != null &&
    					session.getHttpSession().getAttribute(Constants.SELECTED_PROFILE) != null) {
    		this.profile = ((PropertyProfile)session.getHttpSession().getAttribute(Constants.SELECTED_PROFILE)).getResourceId();
    		this.username = session.getUser().getPrincipalName();
    		this.realm = session.getRealmId();
    	}
    	else {
    		this.profile = 0;
    		this.username = null;
    		this.realm = UserDatabaseManager.getInstance().getDefaultUserDatabase().getRealm().getResourceId();
    	}
    }
    
    public ProfilePropertyKey(int profile, String username, String name, int realm) {
        super(name, ProfileProperties.NAME);
        this.profile = profile;
        this.username = username;
        this.realm = realm;
    }
    
    public ProfilePropertyKey(int profile, User user, String name) {
        this(profile, user.getPrincipalName(), name, user.getRealm().getResourceId());
    }
    
    public boolean isUserSpecific() {
        return !Util.isNullOrTrimmedBlank(username);
    }

    public ProfilePropertyKey(String name) {
        this(0, null, name, UserDatabaseManager.getInstance().getDefaultUserDatabase().getRealm().getResourceId());
    }

    public int getProfile() {
        return profile;
    }

    public int hashCode() {
        return getName().hashCode() + getProfile() + ( getUsername() != null ? getUsername().hashCode() : "".hashCode() );
    }

    public String getUsername() {
        return username;
    }

    public int getRealm() {
        return realm;
    }
    
    public boolean equals(Object o) {
        ProfilePropertyKey k = (ProfilePropertyKey)o;
        return super.equals(o) && getProfile() == k.getProfile() && getUsername().equals(k.getUsername());
    }

}
