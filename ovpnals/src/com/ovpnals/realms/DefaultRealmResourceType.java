package com.ovpnals.realms;

import com.ovpnals.policyframework.DefaultResourceType;
import com.ovpnals.policyframework.PolicyConstants;

/**
 * Implementation of a {@link com.ovpnals.policyframework.ResourceType} for
 * <i>Realms</i> resources.
 */
public class DefaultRealmResourceType extends DefaultResourceType {

    /**
     * Constructor
     */
    public DefaultRealmResourceType() {
        super(PolicyConstants.REALM_RESOURCE_TYPE_ID, "realms", PolicyConstants.DELEGATION_CLASS);
    }

}
