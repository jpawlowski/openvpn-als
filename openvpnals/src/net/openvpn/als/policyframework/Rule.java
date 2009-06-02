package net.openvpn.als.policyframework;


/**
 * 
 * @author brett
 */
public interface Rule {
    
    /**
     * Get the ID of a rule
     * @return
     */
    public int getId();
    
    /**
     * Determine if the rule matches. 
     * 
     * @param resource resource the rule applies to
     * @param principal pricipal being tested.
     * @return allowed
     */
    public boolean isAllowed(AccessRights resource, Principal principal);
}
