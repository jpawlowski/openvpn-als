package com.adito.policyframework;

import com.adito.properties.PropertyProfile;
import com.adito.properties.PropertyProfileResourceType;
import com.adito.security.AuthenticationScheme;
import com.adito.security.AuthenticationSchemeResourceType;

/**
 * Constants used throughout the policy framework.
 */
public interface PolicyConstants {

    /**
     * A special policy with a Name of 'Everyone'. All users in the realm are
     * part of this policy.
     */
    public final static String EVERYONE_POLICY_NAME = "Everyone";
    
    /**
     * A special policy prefix name ofr personal policies.
     */
    public final static String PERSONAL_PREFIX = "(Personal)";


    /**
     * The Id for the permission that allows users to create resources and
     * assign policies to them.
     */
    public final static int PERM_CREATE_AND_ASSIGN_ID = 1;

    /**
     * The Id for the permission that allows users to edit resources and assign
     * policies to them.
     */
    public final static int PERM_EDIT_AND_ASSIGN_ID = 2;

    /**
     * The Id for the permission that allows users to assign policies to
     * resources.
     */
    public final static int PERM_ASSIGN_ID = 3;

    /**
     * Id for permission that allows an item to be edited.
     */
    public final static int PERM_EDIT_ID = 4;

    /**
     * Id for permission that allows an item to be created.
     */
    public final static int PERM_CREATE_ID = 5;

    /**
     * Id for permission that allows items or resources to be deleted.
     */
    public final static int PERM_DELETE_ID = 6;

    /**
     * Id for permission that allows resources to be copied.
     */
    public final static int PERM_COPY_ID = 7;

    /**
     * Id for permission that allows configuration of policies within policies.
     */
    public final static int PERM_NEST_ID = 8;

    /**
     * Id for generic permission that allows some item to be changed in some
     * way.
     */
    public static final int PERM_CHANGE_ID = 9;

    /**
     * Id for generic permission thats some item or resource to be viewed
     * (although not neccessarily edited)
     */
    public static final int PERM_VIEW_ID = 10;

    /**
     * Id for permission that allows some action to be performed.
     */
    public static final int PERM_PERFORM_ID = 11;

    /**
     * Id for permission that allows some entity to be updated.
     */
    public static final int PERM_UPDATE_ID = 12;

    /**
     * Id for permission that allows some entity to be installed
     */
    public static final int PERM_INSTALL_ID = 13;

    /**
     * Id for permission that allows some type of entity to be maintained.
     * 
     */
    public static final int PERM_MAINTAIN_ID = 14;

    /**
     * Id for permission that allows type of entity to be used.
     */
    public static final int PERM_USE_ID = 15;

    /**
     * Id for permission that allows some type of sub-system to be controlled
     */
    public static final int PERM_CONTROL_ID = 16;

    /**
     * Id for permission that allows some type of entity to be sent somewhere.
     */
    public static final int PERM_SEND_ID = 17;

    /**
     * Id for permission that allows the server to be shutdown.
     */
    public static final int PERM_SHUTDOWN_ID = 18;

    /**
     * Id for permission that allows the server to be restarted.
     */
    public static final int PERM_RESTART_ID = 19;

    /**
     * Id for permission that allows items or resources to be cleared.
     */
    public static final int PERM_CLEAR_ID = 20;
    
    /**
     * Id for permission that allows a personal resource to be created edited and deleted
     */
    public static final int PERM_PERSONAL_CREATE_EDIT_AND_DELETE_ID = 23;

    /**
     * Permission that allows users to create resources and assign policies to
     * them.
     */
    public final static Permission PERM_CREATE_EDIT_AND_ASSIGN = new Permission(PERM_CREATE_AND_ASSIGN_ID, "policyframework");

    /**
     * The permission that allows users to edit resources and assign policies to
     * them.
     */
    public final static Permission PERM_EDIT_AND_ASSIGN = new Permission(PERM_EDIT_AND_ASSIGN_ID, "policyframework");

    /**
     * Permission that allows an item to be created.
     */
    public final static Permission PERM_CREATE = new Permission(PERM_CREATE_ID, "policyframework");

    /**
     * Permission that allows an item to be edited.
     */
    public final static Permission PERM_EDIT = new Permission(PERM_EDIT_ID, "policyframework");

    /**
     * Permission that allows configuration of policies within policies.
     */
    public final static Permission PERM_NEST = new Permission(PERM_NEST_ID, "policyframework");

    /**
     * Permission that allows items or resources to be deleted.
     */
    public final static Permission PERM_DELETE = new Permission(PERM_DELETE_ID, "policyframework");
    /**
     * Permission that allows resources to be copied.
     */
    public final static Permission PERM_COPY = new Permission(PERM_COPY_ID, "policyframework");

    /**
     * Generic permission that allows some item to be changed in some
     * way.
     */
    public final static Permission PERM_CHANGE = new Permission(PERM_CHANGE_ID, "policyframework");

    /**
     * Generic permission thats some item or resource to be viewed
     * (although not neccessarily edited)
     */
    public final static Permission PERM_VIEW = new Permission(PERM_VIEW_ID, "policyframework");
    
    /**
     * Permission that allows some action to be performed.
     */
    public final static Permission PERM_PERFORM = new Permission(PERM_PERFORM_ID, "policyframework");
    
    /**
     * Permission that allows some entity to be installed
     */
    public final static Permission PERM_INSTALL = new Permission(PERM_INSTALL_ID, "policyframework");
    
    /**
     * Permission that allows some entity to be updated
     */
    public final static Permission PERM_UPDATE = new Permission(PERM_UPDATE_ID, "policyframework");
    
    /**
     * Permission that allows some entity or group of entities to be maintained
     */
    public static final Permission PERM_MAINTAIN = new Permission(PERM_MAINTAIN_ID, "policyframework");
    
    /**
     * Permission that allows some entity or group of entities to be used
     */
    public static final Permission PERM_USE = new Permission(PERM_USE_ID, "policyframework");
    
    /**
     * Permission that allows some entity or group of entities to be controlled
     */
    public static final Permission PERM_CONTROL = new Permission(PERM_CONTROL_ID, "policyframework");

    /**
     * Permission that allows some entity or group of entities to be sent somewhere
     */
    public static final Permission PERM_SEND = new Permission(PERM_SEND_ID, "policyframework");
    
    /**
     * Permission that allows the server to be shut down
     */
    public static final Permission PERM_SHUTDOWN = new Permission(PERM_SHUTDOWN_ID, "policyframework");
    
    /**
     * Permission that allows the server to be restarted
     */
    public static final Permission PERM_RESTART = new Permission(PERM_RESTART_ID, "policyframework");
    
    /**
     * permission that allows users to assign policies to resources.
     */
    public static final Permission PERM_ASSIGN = new Permission(PERM_ASSIGN_ID, "policyframework");

    /**
     * Permission that allows items or resources to be deleted.
     */
    public static final Permission PERM_CLEAR = new Permission(PERM_CLEAR_ID, "policyframework");

    /**
     * Permission that allows realm properties to be changed.
     */
    public static final Permission PERM_PERSONAL_CREATE_EDIT_AND_DELETE = new Permission(PERM_PERSONAL_CREATE_EDIT_AND_DELETE_ID, "policyframework");

    /*
     * Permission classes
     */
    
    /**
     * Permission class for <i>Delegation Resource Permissions</i>
     */
    public final static String DELEGATION_CLASS = "delegation";
    
    /**
     * Permission class for <i>System Resource Permissions</i>
     */
    public final static String SYSTEM_CLASS = "system";

    /**
     * Permission class for <i>Personal Resource Permissions</i>
     */    
    public final static String PERSONAL_CLASS = "personal";

    /*
     * Global Resource Permission Id's
     */

    /**
     * ID for the PROFILE_RESOURCE_TYPE
     */
    public final static int PROFILE_RESOURCE_TYPE_ID = 1;
    
    /**
     * ID for the POLICY_RESOURCE_TYPE
     */
    public final static int POLICY_RESOURCE_TYPE_ID = 5;
    
    /**
     * ID for the ACCESS_RIGHTS_RESOURCE
     */
    public final static int ACCESS_RIGHTS_RESOURCE_TYPE_ID = 6;

    /**
     * The ResourceType for Profiles
     */
    public static final ResourceType<PropertyProfile> PROFILE_RESOURCE_TYPE = new PropertyProfileResourceType(PROFILE_RESOURCE_TYPE_ID,
                    DELEGATION_CLASS);
    
    /**
     * The ResourceType for Policies
     */
    public static final ResourceType<Policy> POLICY_RESOURCE_TYPE = new PolicyResourceType();
    
    /**
     * The ResourceType for Access Rights
     */
    public static final ResourceType<AccessRights> ACCESS_RIGHTS_RESOURCE_TYPE = new AccessRightsResourceType();

    // System Resource Permissions (delegatable)

    
    /**
     * ID for the SERVICE_CONTROL_RESOURCE_TYPE
     */
    public final static int SERVICE_CONTROL_RESOURCE_TYPE_ID = 1000; // 
    
    /**
     * ID for the SYSTEM_CONFIGURATION_RESOURCE_TYPE
     */
    public final static int SYSTEM_CONFIGURATION_RESOURCE_TYPE_ID = 1001; //
    
    /**
     * ID for the KEYSTORE_RESOURCE_TYPE
     */
    public final static int KEYSTORE_RESOURCE_TYPE_ID = 1002; //
    
    /**
     * ID for the AUTHENTICATION_SCHEMES_RESOURCE_TYPE
     */
    public final static int AUTHENTICATION_SCHEMES_RESOURCE_TYPE_ID = 1003;
    
    /**
     * ID for the ACCOUNTS_AND_GROUPS_RESOURCE
     */
    public final static int ACCOUNTS_AND_GROUPS_RESOURCE_TYPE_ID = 1004;
    
    /**
     * ID for the IP_RESTRICTIONS_RESOURCE_TYPE
     */
    public final static int IP_RESTRICTIONS_RESOURCE_TYPE_ID = 1006;
    
    /**
     * ID for the EXTENSIONS_RESOURCE_TYPE
     */
    public final static int EXTENSIONS_RESOURCE_TYPE_ID = 1007; //
    
    /**
     * ID for the MESSAGE_QUEUE_RESOURCE_TYPE
     */
    public final static int MESSAGE_QUEUE_RESOURCE_TYPE_ID = 1008;
    
    /**
     * ID for the STATUS_TYPE_RESOURCE_TYPE
     */
    public final static int STATUS_TYPE_RESOURCE_TYPE_ID = 1009;
    
    /**
     * ID for the REPLACEMENTS_RESOURCE_TYPE
     */
    public final static int REPLACEMENTS_RESOURCE_TYPE_ID = 1010;
    
    /**
     * ID for the USER_ATTRIBUTE_DEFINITIONS_RESOURCE_TYPE
     */
    public final static int ATTRIBUTE_DEFINITIONS_RESOURCE_TYPE_ID = 1011;
    
    /**
     * ID for the REALM_RESOURCE_TYPE
     */
    public final static int REALM_RESOURCE_TYPE_ID = 1012;
    
    /**
     * The ResourceType for Services
     */
    public final static ResourceType SERVICE_CONTROL_RESOURCE_TYPE = new DefaultResourceType(SERVICE_CONTROL_RESOURCE_TYPE_ID,
                    "policyframework", SYSTEM_CLASS);
    
    /**
     * The ResourceType for System configuration
     */
    public static final ResourceType SYSTEM_CONFIGURATION_RESOURCE_TYPE = new DefaultResourceType(
                    SYSTEM_CONFIGURATION_RESOURCE_TYPE_ID, "policyframework", SYSTEM_CLASS);
    
    /**
     * The ResourceType for keystore
     */
    public static final ResourceType KEYSTORE_RESOURCE_TYPE = new DefaultResourceType(KEYSTORE_RESOURCE_TYPE_ID, "policyframework",
                    SYSTEM_CLASS);
    
    /**
     * The ResourceType for authenticaton schemes
     */
    public static final ResourceType<AuthenticationScheme> AUTHENTICATION_SCHEMES_RESOURCE_TYPE = new AuthenticationSchemeResourceType();
    
    /**
     * The ResourceType for accounts and groups
     */
    public static final ResourceType<Resource> ACCOUNTS_AND_GROUPS_RESOURCE_TYPE = new DefaultResourceType(ACCOUNTS_AND_GROUPS_RESOURCE_TYPE_ID, "policyframework",
                    SYSTEM_CLASS);
    
    /**
     * The ResourceType for IP restrictions
     */
    public static final ResourceType IP_RESTRICTIONS_RESOURCE_TYPE = new DefaultResourceType(IP_RESTRICTIONS_RESOURCE_TYPE_ID,
                    "policyframework", SYSTEM_CLASS);
    
    /**
     * The ResourceType for extensions
     */
    public static final ResourceType EXTENSIONS_RESOURCE_TYPE = new DefaultResourceType(EXTENSIONS_RESOURCE_TYPE_ID,
                    "policyframework", SYSTEM_CLASS);
    
    /**
     * The ResourceType for message queue
     */
    public static final ResourceType MESSAGE_QUEUE_RESOURCE_TYPE = new DefaultResourceType(MESSAGE_QUEUE_RESOURCE_TYPE_ID,
                    "policyframework", SYSTEM_CLASS);
    
    /**
     * The ResourceType for status
     */
    public static final ResourceType STATUS_TYPE_RESOURCE_TYPE = new DefaultResourceType(STATUS_TYPE_RESOURCE_TYPE_ID,
                    "policyframework", SYSTEM_CLASS);
    
    /**
     * The ResourceType for replacements
     */
    public static final ResourceType REPLACEMENTS_RESOURCE_TYPE = new DefaultResourceType(REPLACEMENTS_RESOURCE_TYPE_ID,
                    "policyframework", SYSTEM_CLASS);
    
    /**
     * The ResourceType for attribute definition
     */
    public static final ResourceType ATTRIBUTE_DEFINITIONS_RESOURCE_TYPE = new DefaultResourceType(ATTRIBUTE_DEFINITIONS_RESOURCE_TYPE_ID,
                    "policyframework", SYSTEM_CLASS);
    
    /**
     * The ResourceType for realms
     */
    public static final ResourceType REALMS_RESOURCE_TYPE = new DefaultResourceType(REALM_RESOURCE_TYPE_ID,
                    "realms", SYSTEM_CLASS);

    /**
     * ID for the PERSONAL_PROFILE_RESOURCE_TYPE
     */
    public final static int PERSONAL_PROFILE_RESOURCE_TYPE_ID = 2001;

    /**
     * ID for the PASSWORD_RESOURCE_TYPE
     */
    public final static int PASSWORD_RESOURCE_TYPE_ID = 2002;

    /**
     * ID for the AGENT_RESOURCE_TYPE
     */
    public final static int AGENT_RESOURCE_TYPE_ID = 2004;

    /**
     * ID for the ATTRIBUTES_RESOURCE_TYPE
     */
    public final static int ATTRIBUTES_RESOURCE_TYPE_ID = 2006;

    /**
     * ID for the LANGUAGE_RESOURCE_TYPE
     */
    public final static int LANGUAGE_RESOURCE_TYPE_ID = 2007;

    /**
     * The ResourceType for personal profiles
     */
    public static final ResourceType PERSONAL_PROFILE_RESOURCE_TYPE = new PropertyProfileResourceType(
                    PERSONAL_PROFILE_RESOURCE_TYPE_ID, PERSONAL_CLASS);

    /**
     * The ResourceType for passwords
     */
    public static final ResourceType PASSWORD_RESOURCE_TYPE = new DefaultResourceType(PASSWORD_RESOURCE_TYPE_ID, "policyframework",
                    PERSONAL_CLASS);

    /**
     * The ResourceType for agent
     */
    public static final ResourceType AGENT_RESOURCE_TYPE = new DefaultResourceType(AGENT_RESOURCE_TYPE_ID,
                    "policyframework", PERSONAL_CLASS);

    /**
     * The ResourceType for attributes
     */
    public static final ResourceType ATTRIBUTES_RESOURCE_TYPE = new DefaultResourceType(ATTRIBUTES_RESOURCE_TYPE_ID,
                    "policyframework", PERSONAL_CLASS);

    /**
     * The ResourceType for languages
     */
    public static final ResourceType LANGUAGE_RESOURCE_TYPE = new DefaultResourceType(LANGUAGE_RESOURCE_TYPE_ID,
                    "policyframework", PERSONAL_CLASS);
}
