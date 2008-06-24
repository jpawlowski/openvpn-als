
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
			
package com.adito.core;

/**
 * Constants for event IDs.
 * 
 * @see com.adito.core.CoreEvent
 * @see com.adito.core.CoreListener
 */
public interface CoreEventConstants {

    /**
     * A new HTTP session has been created (i.e. a new client browser has made a
     * connection.
     */
    public final static int NEW_HTTP_SESSION = 0;

    /**
     * Server is being started
     */
    public static final int SERVER_STARTED = 1;

    /**
     * Server is stopping
     */
    public static final int SERVER_STOPPING = 2;

    /**
     * Server has stopped
     */
    public static final int SERVER_STOPPED = 3;

    /**
     * A new user has successfully logged on
     */
    public final static int LOGON = 300;

    /**
     * An application using the embedded client API has logged on
     */
    public final static int EMBEDDED_CLIENT_LOGON = 301;

    /**
     * A user has logged off
     */
    public final static int LOGOFF = 302;

    /**
     * An application using the embedded client API has logged off
     */
    public static final int EMBEDDED_CLIENT_LOGOFF = 303;

    /**
     * An account has been locked
     */
    public static final int ACCOUNT_LOCKED = 304;

    /**
     * A system property, profile property or user attribute value has changed
     */
    public final static int PROPERTY_CHANGED = 305;

    /**
     * A new keystore has been created
     */
    public final static int KEYSTORE_CREATED = 400;

    /**
     * A keystore has been imported
     */
    public final static int KEYSTORE_IMPORTED = 401;

    /**
     * A keystore has been deleted
     */
    public final static int KEYSTORE_DELETED = 402;

    /**
     * A certificate has been created
     * 
     */
    public final static int KEYSTORE_CERTIFICATE_CREATED = 403;

    /**
     * A CSR has been generated
     */
    public final static int KEYSTORE_CERTIFICATE_CSR_GENERATED = 405;

    /**
     * A signed certificate has been imported
     */
    public final static int KEYSTORE_CERTIFICATE_SIGNED_IMPORTED = 406;

    /**
     * A certificate has been deleted
     */
    public final static int KEYSTORE_CERTIFICATE_DELETED = 407;

    /**
     * A PKCS12 keystore has been imported
     */
    public final static int KEYSTORE_PKCS12_KEY_KEY_IMPORTED = 409;

    /**
     * A trusted certificate was imported
     */
    public final static int KEYSTORE_TRUSTED_CERTIFICATE_IMPORTED = 410;

    /**
     * A client certificate was imported
     */
    public final static int KEYSTORE_SERVER_AUTHENTICATION_CERTIFICATE_IMPORTED = 411;

    /**
     * A root certificate was imported
     */
    public final static int KEYSTORE_ROOT_CERTIFICATE_IMPORTED = 412;

    /**
     * An agent (VPN client) has registered
     */
    public final static int AGENT_REGISTER = 501;

    /**
     * An agent (VPN client) has de-registered
     */
    public final static int AGENT_DEREGISTER = 502;
    
    /**
     * An agent (VPN client) has been started
     */
    public final static int AGENT_LAUNCHED = 503;
    
    /**
     * An agent (VPN client) has been stopped
     */
    public final static int AGENT_STOPPED = 504;

    /**
     * A new user has been created
     */
    public final static int USER_CREATED = 100;

    /**
     * A user has been removed
     */
    public final static int USER_REMOVED = 101;

    /**
     * A user has been edited
     */
    public final static int USER_EDITED = 102;

    /**
     * A role has been created
     */
    public final static int GROUP_CREATED = 200;

    /**
     * A role has been removed
     */
    public final static int GROUP_REMOVED = 201;

    /**
     * A role has been updated
     */
    public final static int GROUP_UPDATED = 202;

    /**
     * An account has been enabled
     */
    public static final int GRANT_ACCESS = 2000;

    /**
     * An account has been disabled
     */
    public static final int REVOKE_ACCESS = 2001;

    /**
     * Personal answers have been set
     */
    public static final int SET_PERSONAL_ANSWER = 2026;

    /**
     * An authentication scheme has been created
     */
    public static final int CREATE_AUTHENTICATION_SCHEME = 2028;

    /**
     * An authentication scheme has been removed
     */
    public static final int DELETE_AUTHENTICATION_SCHEME = 2029;

    /**
     * An authentication scheme has been updated
     */
    public static final int UPDATE_AUTHENTICATION_SCHEME = 2030;

    /**
     * A user has changed their password
     */
    public static final int CHANGE_PASSWORD = 2034;

    /*
     * Extension
     */

    /**
     * Extension installed
     */
    public static final int INSTALL_EXTENSION = 2080;

    /**
     * Extension updated
     */
    public static final int UPDATE_EXTENSION = 2081;
    
    /**
     * Extension removed
     */
    public static final int REMOVE_EXTENSION = 2082;
    
    /**
     * Removing extension (occurs before {@link #REMOVE_EXTENSION})
     */
    public static final int REMOVING_EXTENSION = 2083;

    /*
     * IP Restrictions
     */

    /**
     * IP Restriction created
     */
    public static final int CREATE_IP_RESTRICTION = 2150;
    
    /**
     * IP Restriction edited
     */
    public static final int EDIT_IP_RESTRICTION = 2152;

    /**
     * IP Restriction deleted
     */
    public static final int DELETE_IP_RESTRICTION = 2151;

    /**
     * IP Restriction moved up
     */
    public static final int IP_RESTRICTION_MOVE_UP = 2153;
    
    /**
     * IP Restriction moved up
     */
    public static final int IP_RESTRICTION_MOVE_DOWN = 2154;
    
    /*
     * Property profiles
     */

    /**
     * Property profile created
     */
    public static final int CREATE_PROPERTY_PROFILE = 2005;

    /**
     * Property profile updated
     */
    public static final int UPDATE_PROPERTY_PROFILE = 2048;

    /**
     * Property profile deleted
     */
    public static final int DELETE_PROPERTY_PROFILE = 2049;

    /*
     * Policy framework events
     */

    /**
     * Resource attached to policy. Usually during resource creation or resource
     * edit
     */
    public static final int RESOURCE_ATTACHED_TO_POLICY = 2100;

    /**
     * Resource detached from policy. Usually during resource creation or
     * resource edit
     */
    public static final int RESOURCE_DETACHED_FROM_POLICY = 2101;

    /**
     * Policy granted to principal
     */
    public static final int GRANT_POLICY_TO_PRINCIPAL = 2102;

    /**
     * Policy revoked from principal
     */
    public static final int REVOKE_POLICY_FROM_PRINCIPAL = 2103;

    /**
     * Policy created
     */
    public static final int CREATE_POLICY = 2041;

    /**
     * Policy updated
     */
    public static final int UPDATE_POLICY = 2042;

    /**
     * Policy deleted
     */
    public static final int DELETE_POLICY = 2043;

    /**
     * Access right created
     */
    public static final int CREATE_ACCESS_RIGHT = 2050;

    /**
     * Access right updated
     */
    public static final int UPDATE_ACCESS_RIGHT = 2051;

    /**
     * Access right deleted
     */
    public static final int DELETE_ACCESS_RIGHT = 2052;

    /* User Attributes */

    /**
     * User attribute definition created
     */
    public static final int ATTRIBUTE_DEFINITION_CREATED = 2060;

    /**
     * User attribute definition updated
     */
    public static final int ATTRIBUTE_DEFINITION_UPDATED = 2061;

    /**
     * User attribute definition removed
     */
    public static final int ATTRIBUTE_DEFINITION_REMOVED = 2062;

    /*
     * Messaging
     */

    /**
     * Message queued in notification
     */
    public static final int MESSAGE_QUEUED = 2070;

    /**
     * Message queued in notification
     */
    public static final int MESSAGE_SENT = 2071;
    
    /**
     * Message queued in notification
     */
    public static final int MESSAGE_QUEUE_CLEARED = 2072;
}
