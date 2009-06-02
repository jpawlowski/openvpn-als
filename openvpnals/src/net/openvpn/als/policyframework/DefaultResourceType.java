
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
			
package net.openvpn.als.policyframework;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.openvpn.als.security.SessionInfo;

/**
 * Default implementation of a
 * {@link net.openvpn.als.policyframework.ResourceType}.
 * 
 * @param <T> 
 * @see net.openvpn.als.policyframework.PolicyConstants
 */
public class DefaultResourceType<T extends Resource> implements ResourceType<T>, Serializable {

	// Private instance variables

	private int id;
	private String bundle;
	private List<Permission> permissions;
	private String permissionClass;
	private boolean policyRequired;

	/**
	 * Constructor for resource types. If the permission class is
	 * {@link PolicyConstants#DELEGATION_CLASS} then {@link #isPolicyRequired()}
	 * will return <code>true</code>, all other classes will return
	 * <code>false</code>
	 * 
	 * @param id unique ID of the resource type
	 * @param bundle bundle that contains the title / description keys
	 * @param permissionClass permission class
	 */
	public DefaultResourceType(int id, String bundle, String permissionClass) {
		this(id, bundle, permissionClass, PolicyConstants.DELEGATION_CLASS.equals(permissionClass));
	}

	/**
	 * Constructor
	 * 
	 * @param id unique ID of the resource type
	 * @param bundle bundle that contains the title / description keys
	 * @param permissionClass permission class
	 * @param policyRequired <code>true</code> if this resource type must be
	 *        attached to a policy
	 */
	public DefaultResourceType(int id, String bundle, String permissionClass, boolean policyRequired) {
		this.id = id;
		this.bundle = bundle;
		this.permissionClass = permissionClass;
		permissions = new ArrayList<Permission>();
		this.policyRequired = policyRequired;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.boot.policyframework.ResourceType#getPermissionClass()
	 */
	public String getPermissionClass() {
		return permissionClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.boot.policyframework.ResourceType#setPermissionClass(java.lang.String)
	 */
	public void setPermissionClass(String permissionClass) {
		this.permissionClass = permissionClass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.boot.policyframework.ResourceType#getResourceTypeId()
	 */
	public int getResourceTypeId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.boot.policyframework.ResourceType#getBundle()
	 */
	public String getBundle() {
		return bundle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.boot.policyframework.ResourceType#getPermissions()
	 */
	public Collection<Permission> getPermissions() {
		return permissions;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.boot.policyframework.ResourceType#addPermission(net.openvpn.als.boot.policyframework.Permission)
	 */
	public void addPermission(Permission permission) {
        if (!permissions.contains(permission)){
            permissions.add(permission);
        }
        else{
            throw new RuntimeException("Attempting to add duplicate permission, this is not allowed.");
        }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.boot.policyframework.ResourceType#getPermission(int)
	 */
	public Permission getPermission(int id) {
		Permission p = null;
		for (Iterator i = permissions.iterator(); i.hasNext();) {
			p = (Permission) i.next();
			if (p.getId() == id) {
				return p;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return o instanceof ResourceType && getResourceTypeId() == ((ResourceType<T>) o).getResourceTypeId();
	}

	/**
	 * Compare two resources types using the ID
	 * 
	 * @param o other resource type
	 * @return comparison
	 */
	public int compareTo(ResourceType<T> o) {
		int i = getPermissionClass().compareTo(o.getPermissionClass());
		return i == 0 ? new Integer(getResourceTypeId()).compareTo(new Integer(o.getResourceTypeId())) : i;
	}

	/**
	 * Return a string representation of this resource type. Only really used
	 * for debugging.
	 * 
	 * @return string representation of this resource type.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer("ResourceType '");
		buf.append(getResourceTypeId());
		buf.append("' [permissionClass=");
		buf.append(getPermissionClass());
		buf.append(",bundle=");
		buf.append(getBundle());
		buf.append("]");
		return buf.toString();
	}

    public Collection<T> getResources(SessionInfo session) throws Exception {
        throw new UnsupportedOperationException("Not supported.");
    }

	public boolean isResourceNameInUse(String resourceName, SessionInfo session) throws Exception {
        T resource = getResourceByName(resourceName, session);
        return resource != null;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.boot.policyframework.ResourceType#getResourceById(int)
	 */
	public T getResourceById(int resourceId) throws Exception {
		throw new UnsupportedOperationException("Not supported.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.policyframework.ResourceType#getResourceByName(java.lang.String,
	 *      net.openvpn.als.security.SessionInfo)
	 */
	public T getResourceByName(String resourceName, SessionInfo session) throws Exception {
		throw new UnsupportedOperationException("Not supported.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.boot.policyframework.ResourceType#removeResource(int,
	 *      net.openvpn.als.security.SessionInfo)
	 */
	public T removeResource(int resourceId, SessionInfo session) throws Exception {
		throw new UnsupportedOperationException("Not supported.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.boot.policyframework.ResourceType#updateResource(net.openvpn.als.boot.policyframework.Resource,
	 *      net.openvpn.als.security.SessionInfo)
	 */
	public void updateResource(T resource, SessionInfo session) throws Exception {
		throw new UnsupportedOperationException("Not supported.");
	}

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.policyframework.ResourceType#createResource(net.openvpn.als.boot.policyframework.Resource,
     *      net.openvpn.als.security.SessionInfo)
     */
    public T createResource(T resource, SessionInfo session) throws Exception {
        throw new UnsupportedOperationException("Not supported.");
    }
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.policyframework.ResourceType#isPolicyRequired()
	 */
	public boolean isPolicyRequired() {
		return policyRequired;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.policyframework.ResourceType#cloneResource(net.openvpn.als.policyframework.Resource)
	 */
	public T cloneResource(T sourceResource, SessionInfo session) throws CloneNotSupportedException {
		/* Because getting a resource should always return a new instance, this will
		 * will suffice as a clone operation
		 */
		try {
			T r = getResourceById(sourceResource.getResourceId());
			int idx = 1;
			while (true) {
				r.setResourceName((idx == 1 ? "Copy of " : ("Copy (" + idx + ") ")) + sourceResource.getResourceName());
				if (sourceResource.getResourceType().getResourceByName(r.getResourceName(), session) == null) {
					break;
				}
				idx++;
			}
			return r;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CloneNotSupportedException();
		}
	}
}
