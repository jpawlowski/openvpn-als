package com.ovpnals.jdbc;

import java.io.Serializable;

import com.ovpnals.realms.Realm;
import com.ovpnals.security.Role;

/**
 * Implementation of a {@link com.ovpnals.security.Role}
 * for <i>JDBC built in Roles</i>.
 */
public class JDBCRole implements Role<JDBCRole>, Serializable {

    private final String rolename;
    private final Realm realm;

    JDBCRole(String roleName, Realm realm) {
        this.rolename = roleName;
        this.realm = realm;
    }

    /* (non-Javadoc)
     * @see com.ovpnals.policyframework.Principal#getPrincipalName()
     */
    public String getPrincipalName() {
        return rolename;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getPrincipalName();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(T)
     */
    public int compareTo(JDBCRole o) {
        return getPrincipalName().compareTo(o.getPrincipalName());
    }

    /* (non-Javadoc)
     * @see com.ovpnals.policyframework.Principal#getRealm()
     */
    public Realm getRealm() {
        return realm;
    }
}