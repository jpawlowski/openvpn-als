
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
			
package com.adito.webforwards;

/**
 * Constants used for event attributes
 */
public class WebForwardEventConstants {

    /**
     * Web forward type 
     */
    public static final String EVENT_ATTR_WEB_FORWARD_TYPE = "webForwardType";
    
    /**
     * Web forward destination URL 
     */
    public static final String EVENT_ATTR_WEB_FORWARD_URL = "webForwardDestinationUrl";
    
    /**
     * Web forward category 
     */
    public static final String EVENT_ATTR_WEB_FORWARD_CATEGORY = "webForwardDestinationCategory";
    
    /**
     * Web forward authentication username
     */
    public static final String EVENT_ATTR_WEB_FORWARD_AUTH_USERNAME = "authenticationUsername";
    
    /**
     * Web forward prefered authentication scheme
     */
    public static final String EVENT_ATTR_WEB_FORWARD_PREFERED_AUTH_SCHEME = "preferredAuthenticationScheme";
    
    /**
     * Web forward form type
     */
    public static final String EVENT_ATTR_WEB_FORWARD_AUTH_FORM_TYPE = "formType";

    /**
     * Web forward form parameters
     */
    public static final String EVENT_ATTR_WEB_FORWARD_AUTH_FORM_PARAMETERS = "formParameters";
    
    /**
     * Replacement Web forward restrict to hosts
     */
    public static final String EVENT_ATTR_REPLACEMENT_WEB_FORWARD_RESTRICT_TO_HOSTS = "restrictToHosts";
    
    /**
     * Replacement Web forward encodeing
     */
    public static final String EVENT_ATTR_REPLACEMENT_WEB_FORWARD_ENCODEING = "encoding";
    
    /**
     * Reverse Web forward paths
     */
    public static final String EVENT_ATTR_REVERSE_WEB_FORWARD_PATHS = "paths";
    
    /**
     * Reverse Web forward active dns
     */
    public static final String EVENT_ATTR_REVERSE_WEB_FORWARD_ACTIVE_DNS = "activeDNS";
    
    /**
     * Reverse Web forward host header
     */
    public static final String EVENT_ATTR_REVERSE_WEB_FORWARD_HOST_HEADER = "hostHeader";

    /**
     * Reverse Web forward custom headers
     */
    public static final String EVENT_ATTR_REVERSE_WEB_FORWARD_CUSTOM_HEADERS = "customHeaders";

    /**
     * Type of replacement
     */
    public static final String EVENT_ATTR_REPLACEMENT_TYPE = "replacementType";
    
    /**
     * Pattern for replacement
     */
    public static final String EVENT_ATTR_REPLACEMENT_PATTERN = "replacementPattern";
    
    /**
     * Sequence of replacement
     */
    public static final String EVENT_ATTR_REPLACEMENT_SEQUENCE = "replacementSequence";
    
    /**
     * A web forward has just been started
     */
    public static final int WEB_FORWARD_STARTED = 610;

    /**
     * A resource has been loaded through a web forward
     */
    public static final int WEB_FORWARD_RESOURCE_LOADED = 611;

    /**
     * A replacement has been deleted
     */
    public static final int DELETE_REPLACEMENT = 2022;
    
    /**
     * A replacement has been updated
     */
    public static final int UPDATE_REPLACEMENT = 2023;

    /**
     * A replacement has been created
     */
    public static final int CREATE_REPLACEMENT = 2024;

    /**
     * A replacement has been moved down
     */
    public static final int REPLACEMENT_PRECEDENCE_CHANGED = 2025;
    
    /**
     * Web forward created
     */
    public static final int CREATE_WEB_FORWARD = 2016;

    /**
     * Web forward updated
     */
    public static final int UPDATE_WEB_FORWARD = 2017;

    /**
     * Web forward deleted
     */
    public static final int DELETE_WEBFORWARD = 2018;

}
