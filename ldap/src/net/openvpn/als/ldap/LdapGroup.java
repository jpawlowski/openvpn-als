package net.openvpn.als.ldap;

import net.openvpn.als.realms.Realm;
import net.openvpn.als.security.Role;

import java.io.Serializable;

/**
 * Implementation of a {@link net.openvpn.als.security.Role} that is created
 * from an Ldap Group.
 */

public final class LdapGroup implements Role<LdapGroup>, Serializable {

    private static final long serialVersionUID = -7675417973639150879L;
    private final String groupName;
    private final String dn;
    private final Realm realm;

    public LdapGroup(String groupName, String dn, Realm realm) {
        this.groupName = groupName;
        this.dn = dn;
        this.realm = realm;
    }
        
    /*
     * (non-Javadoc)
     * @see net.openvpn.als.policyframework.Principal#getPrincipalName()
     */
    public String getPrincipalName() {
        return groupName;
    }

    /**
     * Get the original un-escaped dn
     * @return dn
     */
    public String getDn() {
        return dn;
    }
    

    /*
     * (non-Javadoc)
     * @see net.openvpn.als.policyframework.Principal#getRealm()
     */
    public Realm getRealm() {
        return realm;
    }
   
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        buffer.append("groupName='").append(groupName).append("' ");
        buffer.append("dn='").append(dn).append("' ");
        buffer.append("realm='").append(realm).append("'");
        buffer.append("']");
        return buffer.toString();
    }

    /**
     * Compare this user against another using the users name for comparison
     * @param o user to compare with
     * @return comparison
     */
    public int compareTo(LdapGroup o) {
        return getPrincipalName().compareTo(o.getPrincipalName());
    }

    public int hashCode() {
        return groupName.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof LdapGroup) {
            return ((LdapGroup) obj).groupName.equals(groupName);
        }
        return false;
    }
}