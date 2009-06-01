
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
			
package net.openvpn.als.activedirectory;

import java.util.Calendar;
import java.util.Date;

import net.openvpn.als.boot.Util;
import net.openvpn.als.realms.Realm;
import net.openvpn.als.security.DefaultUser;
import net.openvpn.als.security.User;

/**
 * Implementation of {@link User} that uses an Active Directory Account for its
 * attributes.
 */
public final class ActiveDirectoryUser extends DefaultUser {
    private static final long serialVersionUID = 2475206921518033947L;
    private final String userPrincipalName; 
    private final String defaultDomain;
    private final String originalDn;
    private final String escapedDn;
    
    /**
     * Constructor
     * @param username username
     * @param userPrincipalName userPrincipalName
     * @param email email address
     * @param fullname full name
     * @param lastPasswordChange date of last password change
     * @param realm Realm
     */
    ActiveDirectoryUser(String username, String userPrincipalName, String defaultDomain, String email, String fullname, String dn, String escapedDn, Date lastPasswordChange, Realm realm) {
        super(selectUsername(username, userPrincipalName), email, fullname, lastPasswordChange, realm);
        this.userPrincipalName = selectUserPrincipalName(username, userPrincipalName);
        this.defaultDomain = defaultDomain;
        this.originalDn = dn;
        this.escapedDn = escapedDn;
    }
    
    private static String selectUsername(String username, String userPrincipalName) {
        if (Util.isNullOrTrimmedBlank(userPrincipalName)) {
            return username;
        }
        return parseUsername(userPrincipalName);
    }
    
    private static String parseUsername(String userPrincipalName) {
        int indexOf = userPrincipalName.indexOf("@");
        return indexOf == -1 ? userPrincipalName : userPrincipalName.substring(0, indexOf);
    }
    
    private static String selectUserPrincipalName(String username, String userPrincipalName) {
        if (Util.isNullOrTrimmedBlank(userPrincipalName)) {
            return username;
        }
        return fixUserPrincipalName(userPrincipalName);
    }
    
    /**
     * For some reason the userPrincipalName's domain must be upper case.  If not
     * then the user can't login as the realm cannot be found.
     * @param userPrincipalName
     * @return String
     */
    private static String fixUserPrincipalName(String userPrincipalName) {
        int indexOf = userPrincipalName.indexOf("@");
        if (indexOf == -1) {
            return userPrincipalName;
        }
        String domain = stripDomain(userPrincipalName).toUpperCase();
        return userPrincipalName.substring(0, ++indexOf) + domain;
    }
    
    @Override
    public String getPrincipalName() {
        if (stripDomain(getUserPrincipalName()).equals(defaultDomain)) {
            return super.getPrincipalName();
        }
        return getUserPrincipalName();
    }
    
    private static String stripDomain(String userPrincipalName) {
        int indexOf = userPrincipalName.indexOf("@");
        if (indexOf == -1) {
            return userPrincipalName;
        }
        indexOf++; // need to parse after the @ symbol
        return userPrincipalName.substring(indexOf, userPrincipalName.length());
    }

    /**
     * Get the userPrincipalName of the user
     * @return String 
     */
    public String getUserPrincipalName() {
        return userPrincipalName;
    }
    
    /**
     * Get the original un-escaped dn
     * @return dn
     */
    public String getOriginalDn() {
        return originalDn;
    }
    
    /**
     * Get the escaped dn
     * @return
     */
    public String getDn() {
        return escapedDn;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.security.DefaultUser#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer(super.toString());
        buffer.append("[");
        buffer.append("userPrincipalName='").append(userPrincipalName).append("' ");
        buffer.append("defaultDomain='").append(defaultDomain).append("' ");
        buffer.append("originalDn='").append(originalDn).append("' ");
        buffer.append("dn='").append(escapedDn).append("'");
        buffer.append("]");
        return buffer.toString();
    }
}