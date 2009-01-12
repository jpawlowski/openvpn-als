package com.adito.policyframework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Abstract implementation of a
 * {@link com.adito.policyframework.PolicyDatabase} that provides basic
 * functionality for registering resource types.
 */
public abstract class AbstractPolicyDatabase implements PolicyDatabase {

    protected Map<String,ResourceType> resourceTypes = new HashMap<String,ResourceType>();

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.PolicyDatabase#registerResourceType(com.adito.policyframework.ResourceType)
     */
    public void registerResourceType(ResourceType resourceType) throws Exception {
        resourceTypes.put(String.valueOf(resourceType.getResourceTypeId()), resourceType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.PolicyDatabase#deregisterResourceType(com.adito.policyframework.ResourceType)
     */
    public void deregisterResourceType(ResourceType resourceType) throws Exception {
        resourceTypes.remove(String.valueOf(resourceType.getResourceTypeId()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.PolicyDatabase#getResourceType(int)
     */
    public ResourceType getResourceType(int resourceTypeId) {
        return resourceTypes.get(String.valueOf(resourceTypeId));
    }

    /* (non-Javadoc)
     * @see com.adito.policyframework.PolicyDatabase#getResourceTypes(java.lang.String)
     */
    public List<ResourceType> getResourceTypes(String permissionClass) throws Exception {
        List<ResourceType> l = new ArrayList<ResourceType>();
        for (Map.Entry<String, ResourceType> entry : resourceTypes.entrySet()) {
            if (permissionClass == null || permissionClass.equals(((ResourceType) entry.getValue()).getPermissionClass())) {
                l.add(entry.getValue());
            }
        }
        return l;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.policyframework.PolicyDatabase#getResourceTypeCount()
     */
    public int getResourceTypeCount() {
        return resourceTypes.size();
    }
}
