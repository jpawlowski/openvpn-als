
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
			
package com.adito.unixauth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.adito.realms.Realm;
import com.adito.security.Role;

/**
 * Implementation of a {@link com.adito.security.Role}
 * for <i>Unix Roles</i>.
 */
public class UNIXRole implements Role<UNIXRole>, Serializable {

    private String name;
    private int gid;
    private String[] members;
    private final Realm realm;
    
    /**
     * @param realm 
     * @param etcGroupEntry
     */
    public UNIXRole(Realm realm, String etcGroupEntry) {
    	this.realm = realm;
        String[] elements = etcGroupEntry.split(":");
        name = elements[0];
        if (elements.length > 2 && !name.equals("+")) {
            gid = Integer.parseInt(elements[2]);
            List<String> m = new ArrayList<String>();
            if (elements.length > 3) {
                StringTokenizer z = new StringTokenizer(elements[3], ",");
                while (z.hasMoreTokens()) {
                    m.add(z.nextToken());
                }
            }
            members = new String[m.size()];
            m.toArray(members);
        }
        else {
            throw new IllegalArgumentException("Invalid format.");
        }
    }

    /**
     * @return int
     */
    public int getGid() {
        return gid;
    }

    /**
     * @param username
     * @return boolean
     */
    public boolean containsMember(String username) {
        for (int i = 0; i < members.length; i++) {
            if (members[i].equals(username)) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.permissions.Principal#getPrincipalName()
     */
    public String getPrincipalName() {
        return name;
    }
    
    public String toString() {
        return getPrincipalName();
    }

    public int compareTo(UNIXRole o) {
        return getPrincipalName().compareTo(o.getPrincipalName());
    }

    /* (non-Javadoc)
     * @see com.adito.policyframework.Principal#getRealm()
     */
    public Realm getRealm() {
        return realm;
    }
}