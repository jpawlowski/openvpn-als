
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.policyframework;

import java.io.Serializable;
import java.util.Calendar;

import com.ovpnals.boot.PropertyList;
import com.ovpnals.properties.Property;
import com.ovpnals.properties.impl.resource.ResourceKey;
import com.ovpnals.security.AuthenticationScheme;
import com.ovpnals.security.Constants;
import com.ovpnals.security.LogonControllerFactory;
import com.ovpnals.security.SessionInfo;

/**
 * Abstract implementation of a {@link Resource}. Provides setters and getters
 * for attributes common to all resource types.
 * 
 * @since 0.2.0
 */

public abstract class AbstractResource implements Resource, Serializable {

    // Protected instance variables
    protected int resourceId;
    protected String resourceName;
    protected String resourceDescription;
    protected Calendar dateCreated;
    protected Calendar dateAmended;
    protected ResourceType resourceType;
    protected int realmID;
    private LaunchRequirement launchRequirement = LaunchRequirement.NOT_LAUNCHABLE;

    /**
     * Required for Serialization!
     */
    protected AbstractResource() {
    }

    /**
     * Constructor.
     * 
     * @param realmID the id of the realm the resource is in.
     * @param resourceType resource type.
     * @param resourceId resource Id
     * @param resourceName resource name
     * @param resourceDescription resource description
     * @param dateCreated date created
     * @param dateAmended date amended
     */
    public AbstractResource(int realmID, ResourceType resourceType, int resourceId, String resourceName,
                            String resourceDescription, Calendar dateCreated, Calendar dateAmended) {
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.resourceDescription = resourceDescription;
        this.dateCreated = dateCreated;
        this.dateAmended = dateAmended;
        this.realmID = realmID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o) {
        return o instanceof Resource && ((Resource) o).getResourceType().equals(getResourceType())
                        && ((Resource) o).getResourceId() == getResourceId() && ((Resource) o).getRealmID() == getRealmID();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.boot.policyframework.Resource#getResourceId()
     */
    public int getResourceId() {
        return resourceId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.boot.policyframework.Resource#getResourceType()
     */
    public ResourceType getResourceType() {
        return resourceType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.boot.policyframework.Resource#getResourceName()
     */
    public String getResourceName() {
        return resourceName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.boot.policyframework.Resource#getResourceDisplayName()
     */
    public String getResourceDisplayName() {
        return getResourceName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.boot.policyframework.Resource#getResourceDescription()
     */
    public String getResourceDescription() {
        return resourceDescription;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.boot.policyframework.Resource#setResourceName(java.lang.String)
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * Set this resources ID
     * 
     * @param resourceId resource Id
     */
    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.boot.policyframework.Resource#setResourceDescription(java.lang.String)
     */
    public void setResourceDescription(String resourceDescription) {
        this.resourceDescription = resourceDescription;
    }

    /**
     * Compare this resource with another using the resource name for
     * comparison.
     * 
     * @param o resource to compare with
     * @return comparison result
     */
    public int compareTo(Object o) {
        return getResourceName().compareTo(((Resource) o).getResourceName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.boot.policyframework.Resource#getDateCreated()
     */
    public Calendar getDateCreated() {
        return dateCreated;
    }

    /**
     * Set the date this resource was created
     * 
     * @param dateCreated date created
     */
    public void setDateCreated(Calendar dateCreated) {
        this.dateCreated = dateCreated;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.boot.policyframework.Resource#getDateAmended()
     */
    public Calendar getDateAmended() {
        return dateAmended;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.boot.policyframework.Resource#setDateAmended(java.util.Calendar)
     */
    public void setDateAmended(Calendar dateAmended) {
        this.dateAmended = dateAmended;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.policyframework.Resource#requiresPassword()
     */
    public boolean sessionPasswordRequired(SessionInfo sessionInfo) {
        boolean hasSessionPassword = false;
        AuthenticationScheme scheme = sessionInfo.getHttpSession() == null ? null : (AuthenticationScheme) sessionInfo
                        .getHttpSession().getAttribute(Constants.AUTH_SESSION);
        if (scheme != null) {
            char[] pw = LogonControllerFactory.getInstance().getPasswordFromCredentials(scheme);
            if (pw != null) {
                hasSessionPassword = true;
            }
        }

        if (!hasSessionPassword & paramsRequirePassword())
            return true;
        else
            return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.policyframework.Resource#paramsRequirePassword()
     */
    public boolean paramsRequirePassword() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString());
        builder.append("[id='").append(getResourceId());
        builder.append("', resourceName='" + getResourceName());
        builder.append("', resourceDescription='" + getResourceDescription());
        builder.append("', realmId='" + getRealmID()).append("']");
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.policyframework.Resource#getRealmID()
     */
    public int getRealmID() {
        return realmID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ovpnals.policyframework.ResourceType#getLaunchRequirement()
     */
    public LaunchRequirement getLaunchRequirement() {
        return launchRequirement;
    }

    /**
     * Set the launch requirement for this resource. By default this is
     * {@link LaunchRequirement#NOT_LAUNCHABLE}.
     * 
     * @param launchRequirement launch requirement
     * @see #getLaunchRequirement()
     */
    public void setLaunchRequirement(LaunchRequirement launchRequirement) {
        this.launchRequirement = launchRequirement;
    }

}
