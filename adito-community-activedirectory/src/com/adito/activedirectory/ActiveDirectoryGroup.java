package com.adito.activedirectory;

import java.io.Serializable;

import com.adito.realms.Realm;
import com.adito.security.Role;

/**
 * Implementation of a {@link com.adito.security.Role} that is created
 * from an Active Directory Group.
 */

public final class ActiveDirectoryGroup implements Role<ActiveDirectoryGroup>, Serializable {

    private static final long serialVersionUID = -7675417973639150879L;
    private final String sAMAccountName;
    private final String originalDn;
    private final String escapedDn;
    private final Long rid;
    private ActiveDirectoryGroup[] parents;
    private final Realm realm;

    public ActiveDirectoryGroup(String samAccountName, String dn, String escapedDn, Long rid, Realm realm) {
        this.sAMAccountName = samAccountName;
        this.originalDn = dn;
        this.escapedDn = escapedDn;
        this.rid = rid;
        this.realm = realm;
    }
        
    /*
     * (non-Javadoc)
     * @see com.adito.policyframework.Principal#getPrincipalName()
     */
    public String getPrincipalName() {
        return sAMAccountName;
    }

    /**
     * Get the original un-escaped dn
     * @return dn
     */
    public String getOriginalDn() {
        return originalDn;
    }
    
    /**
     * Get the escaped dn
     * @return
     */
    public String getDn() {
        return escapedDn;
    }

    /**
     * Get the RID for this role. This is used to lookup a users primary group
     * @return RID
     */
    public Long getRID() {
        return rid;
    }

    void setParents(ActiveDirectoryGroup[] parents) {
        this.parents = parents;
    }

    ActiveDirectoryGroup[] getParents() {
        return parents;
    }
    
    /*
     * (non-Javadoc)
     * @see com.adito.policyframework.Principal#getRealm()
     */
    public Realm getRealm() {
        return realm;
    }
    
    static Long getRIDFromSID(byte[] sid) {
        String rid = "";
        for (int i = 6; i > 0; i--) {
            rid += byteToHex(sid[i]);
        }

        long authority = Long.parseLong(rid);
        if (authority != 5) {
            return null;
        }
        
        rid = "";
        for (int j = 11; j > 7; j--) {
            rid += byteToHex(sid[j + (4 * 4)]);
        }
        return new Long(Long.parseLong(rid, 16));
    }
    
    private static String byteToHex(byte b) {
        String ret = Integer.toHexString(b & 0xFF);
        if (ret.length() < 2) {
            ret = "0" + ret;
        }
        return ret;
    }
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        buffer.append("principalName='").append(sAMAccountName).append("' ");
        buffer.append("originalDn='").append(originalDn).append("' ");
        buffer.append("dn='").append(escapedDn).append("' ");
        buffer.append("realm='").append(realm).append("'");
        buffer.append("']");
        return buffer.toString();
    }

    /**
     * Compare this user against another using the users name for comparison
     * @param o user to compare with
     * @return comparison
     */
    public int compareTo(ActiveDirectoryGroup o) {
        return getPrincipalName().compareTo(o.getPrincipalName());
    }

    public int hashCode() {
        return sAMAccountName.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ActiveDirectoryGroup) {
            return ((ActiveDirectoryGroup) obj).sAMAccountName.equals(sAMAccountName);
        }
        return false;
    }
}