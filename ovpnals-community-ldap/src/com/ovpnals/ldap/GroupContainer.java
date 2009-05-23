
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.ldap;

import com.ovpnals.policyframework.PrincipalCache;
import com.ovpnals.security.Role;
import org.apache.commons.cache.Cache;

import java.util.Collection;

public final class GroupContainer extends PrincipalCache<LdapGroup> {

    private static final String GROUPS_CACHE_PREFIX = "groups";
    private static final String MESSAGE_BUNDLE = "ldap";
    private static final String CACHE_FULL_MESSAGE = "ldap.cache.group.full";
    public static final GroupContainer EMPTY_CACHE = new GroupContainer(0, true);
    
    private final Cache dnToRoleCache;
    
    GroupContainer(int cacheSize, boolean inMemoryCache) {
        super(cacheSize, inMemoryCache, false, "role", MESSAGE_BUNDLE, CACHE_FULL_MESSAGE);
        dnToRoleCache = createCache(MESSAGE_BUNDLE, CACHE_FULL_MESSAGE);
    }    
    
    synchronized Role getGroupByDn(String dn) {
        return (Role) dnToRoleCache.retrieve(dn.toLowerCase());
    }

    synchronized boolean containsDn(String dn) {
        return getGroupByDn(dn) != null;
    }

    synchronized String storeGroup(LdapGroup group) {
        String principalName = storePrincipal(group);
        final String dn = group.getDn().toLowerCase();
        dnToRoleCache.store(dn, group, Long.MAX_VALUE, null, GROUPS_CACHE_PREFIX);
        return principalName;
    }



    
    synchronized void updateRemovedGroups(Collection<String> missingGroups) {
        for (String groupName : missingGroups) {
            LdapGroup group = retrievePrincipal(groupName);
            if (group != null) {
                removeGroup(group);           
            }
        }
    }

    public synchronized void removeGroup(LdapGroup group) {
        removePrincipal(group);
        final String dn = group.getDn().toLowerCase();
        dnToRoleCache.store(dn, null, 0L, null, GROUPS_CACHE_PREFIX);

    }
    
    public synchronized void close() {
        super.close();
        closeCache(dnToRoleCache);
    }
}