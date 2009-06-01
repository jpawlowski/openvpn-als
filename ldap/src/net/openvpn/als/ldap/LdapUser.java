/*
 *  OpenVPNALS
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package net.openvpn.als.ldap;

import net.openvpn.als.realms.Realm;
import net.openvpn.als.security.DefaultUser;
import net.openvpn.als.security.User;

import java.util.Date;

/**
 * Implementation of {@link User} that uses an Ldap Account for its
 * attributes.
 */
public final class LdapUser extends DefaultUser {

    private static final long serialVersionUID = -7675417973639150878L;

    private final String dn;

    /**
     * Constructor
     *
     * @param username           username
     * @param dn                 dn
     * @param email              email address
     * @param fullname           full name
     * @param lastPasswordChange date of last password change
     * @param realm              Realm
     */
    public LdapUser(String username, String dn, String email, String fullname, Date lastPasswordChange, Realm realm) {
        super(username, email, fullname, lastPasswordChange, realm);
        this.dn = dn;
    }

    /**
     * Get the original un-escaped dn
     *
     * @return dn
     */
    public String getDn() {
        return dn;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("LdapUser");
        sb.append("{dn='").append(dn).append('\'');
        sb.append('}');
        return sb.toString();
    }
}