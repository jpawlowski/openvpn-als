/*
 *  Adito-PAM
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
package com.adito.pam;

import java.util.ArrayList;
import java.util.Collection;

import com.adito.realms.Realm;
import com.adito.security.DefaultUser;
import com.adito.security.Role;

/**
 * This is the PAM definition of a Adito User.
 *
 */
public class PAMUser extends DefaultUser {

	private static final long serialVersionUID = 6218766721173719537L;
	private Collection<PAMGroup> groups;

	/**
	 * Construtor to instantiate a PAM User.
	 * @param principalName The user Id.
	 * @param realm The realm.
	 */
	public PAMUser(String principalName, Realm realm) {
		super(principalName, "", principalName, null, realm);
		groups = new ArrayList<PAMGroup>();
	}
	
	/**
	 * This method add the specified group to the user.
	 * @param group
	 */
	public void addGroup(PAMGroup group) {
		groups.add(group);
	}

	/* (non-Javadoc)
	 * @see com.adito.security.DefaultUser#getRoles()
	 */
	@Override
	public Role[] getRoles() {
		return groups.toArray(new PAMGroup[groups.size()]);
	}

}
