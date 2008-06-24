package com.adito.realms;

import java.io.Serializable;
import java.util.Calendar;

import com.adito.policyframework.AbstractResource;
import com.adito.policyframework.PolicyConstants;

/**
 * Default implementation of a
 * {@link com.adito.properties.PropertyProfile}.
 */
public class DefaultRealm extends AbstractResource implements Realm, Serializable {
    private static final long serialVersionUID = 4283488241230531541L;
    private String type;
    private boolean hasUserDatabase = false;
    
    /**
     * Required for Serialization!
     */
    private DefaultRealm() {
    }
    
    /**
     * @param type
     * @param resourceId
     * @param resourceName
     * @param resourceDescription
     * @param dateCreated
     * @param dateAmended
     */
    public DefaultRealm(String type, int resourceId, String resourceName, String resourceDescription, Calendar dateCreated, Calendar dateAmended) {
        // note the realm id and the resourtce id are the same.
        super(resourceId, PolicyConstants.REALMS_RESOURCE_TYPE, resourceId, resourceName, resourceDescription, dateCreated, dateAmended);
        this.type = type;
    }

    /* (non-Javadoc)
     * @see com.adito.realms.Realm#getType()
     */
    public String getType() {
        return type;
    }

    /* (non-Javadoc)
     * @see com.adito.realms.Realm#setType(java.lang.String)
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return boolean
     */
    public boolean isHasUserDatabase() {
        return hasUserDatabase;
    }
}
