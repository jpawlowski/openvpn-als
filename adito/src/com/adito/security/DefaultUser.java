
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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import com.adito.realms.Realm;

/**
 * Implementation of a {@link com.adito.security.User}
 * for <i>Default users</i>.
 */
public class DefaultUser implements User, Serializable {
    protected String principalName;
    protected String email;
    protected String fullname;
    protected Date lastPasswordChange;
    protected Role[] roles;
    private final Realm realm;

    /**
     * @param principalName
     * @param email
     * @param fullname
     * @param lastPasswordChange
     * @param realm 
     */
    public DefaultUser(String principalName, String email, String fullname, Date lastPasswordChange, Realm realm) {
        this.principalName = principalName;
        this.email = email;
        this.fullname = fullname;
        this.lastPasswordChange = lastPasswordChange;
        this.realm = realm;
    }

    /*
     * (non-Javadoc)
     * @see com.adito.security.User#getLastPasswordChange()
     */
    public Date getLastPasswordChange() {
        return lastPasswordChange;
    }

    public boolean requiresPasswordChange() {
        return lastPasswordChange == null;
    }

    /*
     * (non-Javadoc)
     * @see com.adito.permissions.Principal#getPrincipalName()
     */
    public String getPrincipalName() {
        return principalName;
    }

    /*
     * (non-Javadoc)
     * @see com.adito.security.User#getEmail()
     */
    public String getEmail() {
        return email;
    }

    /*
     * (non-Javadoc)
     * @see com.adito.security.User#getFullname()
     */
    public String getFullname() {
        return fullname;
    }

    /*
     * (non-Javadoc)
     * @see com.adito.security.User#getHomeNetworkPlaceUri()
     */
    public String getHomeNetworkPlaceUri() {
        return null;
    }

    public boolean memberOf(Role role) {
        if (roles == null) {
            return false;
        }
        
        for (Role assignedRole : roles) {
            if (assignedRole.getPrincipalName().equals(role.getPrincipalName())) {
                return true;
            }
        }
        return false;
    }
    
    /*
     * (non-Javadoc)
     * @see com.adito.security.User#getRoles()
     */
    public Role[] getRoles() {
        return roles;
    }

    /**
     * Set the roles
     * @param roles roles
     */
    public void setRoles(Role[] roles) {
        this.roles = roles;
    }
    
    /**
     * Set the principal name
     * @param principalName
     */
    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    /**
     * Set the email address
     * @param email email address
     */
    public void setEmail(String email) {
        this.email = email;        
    }

    /**
     * Set the full name
     * @param fullname full name
     */
    public void setFullname(String fullname) {
        this.fullname = fullname;
        
    }
    
    /**
     * Set the last password change
     * @param lastPasswordChange
     */
    public void setLastPasswordChange(Date lastPasswordChange) {
    	this.lastPasswordChange = lastPasswordChange;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        return o instanceof User && ((User)o).getPrincipalName().equals(getPrincipalName()) 
        	&& ((User)o).getRealm().equals(getRealm());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
	public int hashCode() {
		return ( getRealm().getResourceId() + "_" + getPrincipalName() ).hashCode();
	}

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        return o instanceof User ? (getPrincipalName().compareTo(((User)o).getPrincipalName())) : 1;
    }

    /* (non-Javadoc)
     * @see com.adito.policyframework.Principal#getRealm()
     */
    public Realm getRealm() {
        return this.realm;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append("[");
        buffer.append("principalName='").append(principalName).append("' ");
        buffer.append("email='").append(email).append("' ");
        buffer.append("fullname='").append(fullname).append("' ");
        buffer.append("lastPasswordChange='").append(lastPasswordChange).append("' ");
        buffer.append("roles='").append(roles == null ? "" : Arrays.asList(roles)).append("' ");
        buffer.append("realm='").append(realm).append("'");
        buffer.append("']");
        return buffer.toString();
    }
}