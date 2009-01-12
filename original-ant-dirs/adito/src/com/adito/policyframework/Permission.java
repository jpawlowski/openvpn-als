
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

import java.io.Serializable;

/**
 * Every {@link com.adito.policyframework.AccessRights} defined
 * by the super user or delegated administrator has attached to it a 
 * list of {@link com.adito.policyframework.AccessRight}
 * objects that defines what permissions the users in the selected policies
 * have. Each <code>AccessRight</code> consists of a single
 * {@link ResourceType} and an instance of this class.
 * 
 * @author Brett Smith
 * @see com.adito.policyframework.PolicyConstants for a list of standard permissions
 */

public class Permission implements Comparable<Permission>, Serializable {

    private int id;
    private String bundle;

    /**
     * Constructor
     * 
     * @param id unique ID of the permission
     * @param bundle the key of the message resource bundle that contains the title and description keys for this permission
     */
    public Permission(int id, String bundle) {
        this.id = id;
        this.bundle = bundle;
    }
    
    /**
     * Get the name of the bundle that contains the title and description keys for this permission
     * @return String
     */
    public String getBundle() {
        return bundle;
    }
    
    /**
     * Get the unique ID of this permission
     * 
     * @return unique ID
     */ 
    public int getId() {
        return id;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        return o instanceof Permission &&
            getId() == ((Permission)o).getId();
    }

    /**
     * Compare this permission with another using the ID for comparison
     * @param o other object
     * @return comparison
     */
    public int compareTo(Permission o) {
        return o instanceof Permission ? Integer.valueOf(getId()).compareTo(Integer.valueOf(((Permission)o).getId())) : 1;
    }
}