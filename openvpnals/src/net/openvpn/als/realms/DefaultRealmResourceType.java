package net.openvpn.als.realms;

import net.openvpn.als.policyframework.DefaultResourceType;
import net.openvpn.als.policyframework.PolicyConstants;

/**
 * Implementation of a {@link net.openvpn.als.policyframework.ResourceType} for
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
