
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

import java.util.Calendar;

import com.adito.security.SessionInfo;

/**
 * Resource interface, defines common methods for all resources.
 */
public interface Resource extends Comparable {
    /**
     * Describes what is required to be able to launch this resource,
     * if the resource is launch-able at all.
     * 
     * @see Resource#getLaunchRequirement()
     */
    public enum LaunchRequirement {
        /**
         * This resource is not launch-able at all
         */
        NOT_LAUNCHABLE("notLaunchable"),
        
        /**
         * The resource is launch-able and does not need a web session
         */
        LAUNCHABLE("launchable"),
        
        /**
         * The resource is launch-able, but needs a web session
         */
        REQUIRES_WEB_SESSION("requiresWebSession");

        private String name;
        
        LaunchRequirement(String name) {
            this.name = name;
        }
        
        /**
         * Get the name of this launch requirement.
         * 
         * @return name
         */
        public String getName() {
            return name;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        public String toString() {
            return name;
        }
    }
    
    /**
     * Constant for the max resource name length
     */
    public final int MAX_RESOURCE_NAME_LENGTH = 32;

    /**
     * @return Resource ID
     */
    public int getResourceId();

    /**
     * @return ResourceType
     */
    public ResourceType getResourceType();

    /**
     * @return Resource name
     */
    public String getResourceName();

    /**
     * @return String
     */
    public String getResourceDisplayName();

    /**
     * @return Resource description
     */
    public String getResourceDescription();

    /**
     * @param name Resource Name
     */
    public void setResourceName(String name);

    /**
     * @param description Resource description.
     */
    public void setResourceDescription(String description);

    /**
     * @return Date created
     */
    public Calendar getDateCreated();

    /**
     * @return Date changed
     */
    public Calendar getDateAmended();

    /**
     * @param date Date changed
     */
    public void setDateAmended(Calendar date);

    /**
     * @param sessionInfo
     * @return Resource requires session password.
     */
    public boolean sessionPasswordRequired(SessionInfo sessionInfo);

    /**
     * @return do resource parameters require session password.
     */
    public boolean paramsRequirePassword();

    /**
     * @return the realm ID the resource is in.
     */
    public int getRealmID();

    /**
     * Get the launch requirements of this resource. Some resource types
     * may need a web session to be able to be launched, some are not 
     * launch-able at all.
     * 
     * @return launch requirements
     */
    public LaunchRequirement getLaunchRequirement();
}
