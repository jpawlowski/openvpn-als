/*
 *  OpenVPNALS-PAM
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
package net.openvpn.als.pam;

import java.io.Serializable;

import net.openvpn.als.realms.Realm;
import net.openvpn.als.security.Role;

/**
 * This is the PAM Role implementation
 *
 */
public class PAMGroup implements Role<PAMGroup>, Serializable {
	
	private static final long serialVersionUID = 1L;
	private String principalName;
	private Realm realm;

	/**
	 * @param principalName Group Name
	 * @param realm Realm
	 * Default constructor
	 */
	public PAMGroup(String principalName, Realm realm) {
		this.principalName = principalName;
		this.realm = realm;
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.policyframework.Principal#getPrincipalName()
	 */
	public String getPrincipalName() {
		return principalName;
	}

	/* (non-Javadoc)
	 * @see net.openvpn.als.policyframework.Principal#getRealm()
	 */
	public Realm getRealm() {
		return realm;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(PAMGroup o) {
		return getPrincipalName().compareTo(o.getPrincipalName());
	}

}
