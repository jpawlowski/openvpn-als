package net.openvpn.als.core;


/**
 * Constants used for event attributes
 */
public class CoreAttributeConstants {

    /**
     * Generic attribute for IP address
     */
    public static final String EVENT_ATTR_IP_ADDRESS = "ipAddress";

    /**
     * Generic attribute for host
     */
    public static final String EVENT_ATTR_HOST = "host";

    /**
     * Generic attribute for host
     */
    public static final String EVENT_ATTR_SCHEME = "scheme";

    /**
     * Generic attribute for a event specific string message
     */
    public static final String EVENT_ATTR_MESSAGE = "message";

    /*
     * SSL Certificate
     */

    /**
     * Certificate Alias
     */
    public final static String EVENT_ATTR_CERTIFICATE_ALIAS = "alias";

    /**
     * Certificate Type
     */
    public final static String EVENT_ATTR_CERTIFICATE_TYPE = "type";
    
    /**
     * Certificate Host Name
     */
    public final static String EVENT_ATTR_CERTIFICATE_HOSTNAME = "hostname";
    
    /**
     * Certificate Organisation Unit
     */
    public final static String EVENT_ATTR_CERTIFICATE_ORGANISATIONAL_UNIT = "organisationalUnit";
    
    /**
     * Certificate Company
     */
    public final static String EVENT_ATTR_CERTIFICATE_COMPANY = "company";
    
     /**
     * Certificate Country Code
      */
    public final static String EVENT_ATTR_CERTIFICATE_COUNTRY_CODE = "countryCode";
    
    /**
     * Certificate Location
     */
    public final static String EVENT_ATTR_CERTIFICATE_LOCATION = "location";
    
    /**
     * Certificate state
     */
    public final static String EVENT_ATTR_CERTIFICATE_STATE = "state";
    
    /**
     * Certificate Key Store Type
     */
    public static final String EVENT_ATTR_CERTIFICATE_KEY_STORE_TYPE = "keyStoreType";
    
    /**
     * Certificate Key Algorithme
     */
    public static final String EVENT_ATTR_CERTIFICATE_KEY_ALGORITHME = "keyAlgorithme";
    
    /**
     * Certificate Key Bit Length
     */
    public static final String EVENT_ATTR_CERTIFICATE_KEY_BIT_LENGTH = "keyBitLength";
    
    
    /*
     * Extension
     */

    /**
     * Extension id
     */
    public static final String EVENT_ATTR_EXTENSION_ID = "extensionId";

    /**
     * Extension name
     */
    public static final String EVENT_ATTR_EXTENSION_NAME = "extensionName";
    
    /**
     * Extension version
     */
    public static final String EVENT_ATTR_EXTENSION_TYPE = "extensionType";
    
    /**
     * Extension version
     */
    public static final String EVENT_ATTR_EXTENSION_VERSION = "extensionVersion";

    /*
     * Application shortcuts
     */

    /**
     * Application id
     */
    public static final String EVENT_ATTR_APPLICATION_ID = "applicationId";

    /**
     * Application name
     */
    public static final String EVENT_ATTR_APPLICATION_NAME = "applicationName";

    /*
     * IP Restrictions
     */
    
    /**
     * IP Restriction Id
     */
    public static final String EVENT_ATTR_IP_RESTRICTION_ID = "ipRestrictionId";
    
    /**
     * IP Restriction Address
     */
    public static final String EVENT_ATTR_IP_RESTRICTION_ADDRESS = "ipRestrictionAddress";
    
    /**
     * IP Restriction is allowed access
     */
    public static final String EVENT_ATTR_IP_RESTRICTION_IS_AUTHORIZED = "ipRestrictionIsAuthorized";

    
    /*
     * Generic resource update constants
     */

    /**
     * Resource ID
     */
    public static final String EVENT_ATTR_RESOURCE_ID = "resourceId";

    /**
     * Resource name
     */
    public static final String EVENT_ATTR_RESOURCE_NAME = "resourceName";

    /**
     * Resource description
     */
    public static final String EVENT_ATTR_RESOURCE_DESCRIPTION = "resourceDescription";

    /**
     * Parent Resource Permission
     */
    public static final String EVENT_ATTR_TYPE_PERMISSION = "typePermission";
    
    /**
     * Access right type
     */
    public static final String EVENT_ATTR_TYPE_ACCESS_RIGHT = "typeAccessRight";
    
    /**
     * If the resource is an {@link net.openvpn.als.policyframework.OwnedResource}
     * then this key stores the owner username
     */
    public static final String EVENT_ATTR_RESOURCE_OWNER = "resourceOwner";

    /**
     * Resource type ID
     */

    public static final String EVENT_ATTR_RESOURCE_TYPE_ID = "resourceTypeId";

    /**
     * Policy ID
     */
    public static final String EVENT_ATTR_POLICY_ID = "policyId";

    /**
     * Policy name
     */
    public static final String EVENT_ATTR_POLICY_NAME = "policyName";

    /**
     * Policy description
     */
    public static final String EVENT_ATTR_POLICY_DESCRIPTION = "policyDescription";
    
    /*
     * Account editing
     */

    /**
     * Generic principal ID (group id or account id)
     */
    public final static String EVENT_ATTR_PRINCIPAL_ID = "principalId";

    /**
     * Generic principal type (group or account)
     */
    public static final String EVENT_ATTR_PRINCIPAL_TYPE = "principalType";

    /**
     * Group for account
     */
    public static final String EVENT_ATTR_GROUP = "group";
    
    /**
     * Account for group
     */
    public static final String EVENT_ATTR_ACCOUNT = "account";
    
    
    
    /**
     * Account full name
     */
    public static final String EVENT_ATTR_FULL_NAME = "fullName";
    
    /**
     * Account email address
     */
    public static final String EVENT_ATTR_ACCOUNT_EMAIL = "accountEmail";
    
    /**
     * Account is enabled
     */
    public static final String EVENT_ATTR_ACCOUNT_IS_ENABLED = "accountIsEnabled";
    
    /**
     * Old value of changed property
     */
    public static final String EVENT_ATTR_PROPERTY_OLD_VALUE = "oldPropertyValue";
    
    /**
     * New value of changed property
     */
    public static final String EVENT_ATTR_PROPERTY_NEW_VALUE = "newPropertyValue";
    
    /**
     * Name of property change has changed
     */
    public static final String EVENT_ATTR_PROPERTY_NAME = "propertyName";
    
    /**
     * Name of property change has changed
     */
    public static final String EVENT_ATTR_PROPERTY_KEY = "propertyKey";
    
    /*
     * Property changes
     */
    
    /*
     * Other
     */
    
    /**
     * Exception message
     */
    public static final String EVENT_ATTR_EXCEPTION_MESSAGE = "exceptionMessage";

    
    /**
     * Date of last PIN number change
     */
    public static final String EVENT_ATTR_LAST_PIN_CHANGE = "lastPinChange";

    /**
     * Username whose attributes have changed
     */
    public static final String EVENT_ATTR_ATTRIBUTE_USER = "attributeUser";

    /**
     * Role id
     */
    public static final String EVENT_ATTR_ROLE_ID = "roleId";
    
    /**
     * Session id
     */
    public static final String EVENT_ATTR_SESSION_ID = "sessionId";
    
    /*
     * Messaging
     */
    
    /**
     * Comma separated list of recipients
     */
    public static final String EVENT_ATTR_MESSAGE_ID = "messageId";
    
    /**
     * Comma separated list of user recipients
     */
    public static final String EVENT_ATTR_MESSAGE_USER_RECIPIENTS = "messageUserRecipients";
    
    /**
     * Comma separated list of role recipients
     */
    public static final String EVENT_ATTR_MESSAGE_ROLE_RECIPIENTS = "messageRoleRecipients";
    
    /**
     * Comma separated list of policy recipients
     */
    public static final String EVENT_ATTR_MESSAGE_POLICY_RECIPIENTS = "messagePolicyRecipients";
    
    /**
     * Boolean indiciating if the message is urgent or not
     */
    public static final String EVENT_ATTR_MESSAGE_URGENT = "messageUrgent";
    
    /**
     * Text of message subject
     */
    public static final String EVENT_ATTR_MESSAGE_SUBJECT = "messageSubject";

    /**
     * Property profile name.
     */
    public static final String EVENT_ATTR_PROPERTY_PROFILE_NAME = "profileName";
    
    /**
     * Property class
     */
    public static final String EVENT_ATTR_PROPERTY_CLASS = "propertyClass";

    /**
     * Property profile description
     */
    public static final String EVENT_ATTR_PROPERTY_PROFILE_DESCRIPTION = "profileDescription";
    
    /**
     * The authentication schemes modules.
     */
    public static final String EVENT_ATTR_AUTHENTICATION_MODULE = "authenticationModule";

    
}
