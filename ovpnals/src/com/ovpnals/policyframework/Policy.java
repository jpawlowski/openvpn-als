package com.ovpnals.policyframework;

import java.io.Serializable;


public interface Policy extends Resource, Serializable {

    // Policy types
    
    public final static int TYPE_NORMAL = 1;
    public final static int TYPE_INVISIBLE = 2;
    public final static int TYPE_PERSONAL = 3;
    
    // Principal types (currently informational only - used in policy / principal relationship)
    
    public final static int PRINCIPAL_USER = 0;
    public final static int PRINCIPAL_GROUP = 1;

    /**
     * Get the type of policy object. Can be one of
     * {@link Policy#TYPE_INVISIBLE} or {@link Policy#TYPE_NORMAL}.
     */
    public int getType();

    /**
     * Set the type of policy object. Can be one of
     * {@link Policy#TYPE_INVISIBLE} or {@link Policy#TYPE_NORMAL}.
     * 
     * @param type type
     */
    public void setType(int type);

    /**
     * Set the human readable name of this policy
     * 
     * @param name name
     */
    public void setResourceName(String name);

    /**
     * Set the description for this policy
     * 
     * @param description description
     */
    public void setResourceDescription(String description);
    
    /**
     * Compare this policy to another. Polcies are equal if their
     * polciy IDs are the same.
     * 
     * @param o policy to compare against
     */
    public boolean equals(Object o);
    
    /**
     * The roles attached to the policy
     * @return int
     */
    public int getAttachedGroups();

    /**
     * The users attached to the policy
     * @return int
     */
    public int getAttachedUsers();

}
