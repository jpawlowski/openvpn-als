
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
			
package net.openvpn.als.properties.impl.userattributes;

import net.openvpn.als.core.UserDatabaseManager;
import net.openvpn.als.properties.attributes.AbstractAttributeKey;
import net.openvpn.als.security.User;

public class UserAttributeKey extends AbstractAttributeKey {
    
    private User user;    

    public UserAttributeKey(String username, String name, int realm) throws IllegalArgumentException {
        super(name, UserAttributes.NAME);
        try {
            user = UserDatabaseManager.getInstance().getUserDatabase(realm).getAccount(username);
        }
        catch(Exception e) {
            throw new IllegalArgumentException("Could not retrieve user for username '" + username + "'");
        }
    }
    
    public UserAttributeKey(User user, String name) {
        super(name, UserAttributes.NAME);
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }

    public String getAttributeClassKey() {
        return getUser().getPrincipalName() + "_" + getUser().getRealm().getResourceId();
    }

}
