package com.adito.realms;

import com.adito.policyframework.DefaultResourceType;
import com.adito.policyframework.PolicyConstants;

/**
 * Implementation of a {@link com.adito.policyframework.ResourceType} for
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
