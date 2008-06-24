
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
import java.util.List;

import javax.servlet.http.HttpSession;

import com.adito.boot.PropertyList;

/**
 * An interface for a list of access rights and rules, 
 */
public interface AccessRights extends Resource, Serializable {

    /**
     * Add a {@link AccessRight} to this delegation resource
     * 
     * @param perm permission to add
     */
    public void addAccessRight(AccessRight perm);

    /**
     * Remove a {@link AccessRight} from this delegation resource
     * 
     * @param perm permission to remove
     */
    public void removeAccessRight(AccessRight perm);

    /**
     * Get a {@link List} of all permissions this resource permission has.
     * 
     * @return permissions
     */
    public List<AccessRight> getAccessRights();

    /**
     * Set the resource ID. You should never need to call this directly as the
     * database will do it when creating a resource permission
     * 
     * @param id id
     */
    public void setResourceId(int id);

    /**
     * Get the permission class. Will be one of {@link AccessRight}
     * 
     * @return String 
     */
    public String getAccessRightsClass();

    /**
     * Get any rules associated with this resource
     * 
     * @return rules
     */
    public List getRules();

    /**
     * Add a rule to this resource
     * 
     * @param rule rule to add
     */
    public void addRule(Rule rule);

    /**
     * Remove a rule from this resource
     * 
     * @param rule rule to remove
     */
    public void removeRule(Rule rule);

    /**
     * Get if this resource permission contains the specified permission
     * 
     * @param accessRight resource type resource permission wrapper
     * @return contains permission
     */
    public boolean containsAccessRight(AccessRight accessRight);

    /**
     * Set all the permissions from the property list in the from 'ResoureType Permission'.
     * 
     * @param httpSession
     * @param permissionList
     * @throws Exception
     */
    public void setAllAccessRights(HttpSession httpSession, PropertyList permissionList) throws Exception;
}
