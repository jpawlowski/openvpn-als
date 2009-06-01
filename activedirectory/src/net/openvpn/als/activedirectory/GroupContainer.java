
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
			
package net.openvpn.als.activedirectory;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.cache.Cache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.policyframework.PrincipalCache;
import net.openvpn.als.security.Role;

public final class GroupContainer extends PrincipalCache<ActiveDirectoryGroup> {
    private static final Log logger = LogFactory.getLog(GroupContainer.class);
    private static final String GROUPS_CACHE_PREFIX = "groups";
    private static final String MESSAGE_BUNDLE = "activeDirectory";
    private static final String CACHE_FULL_MESSAGE = "activeDirectory.cache.group.full";
    public static final GroupContainer EMPTY_CACHE = new GroupContainer(0, true);
    
    private final Cache dnToRoleCache;
    private final Cache groupsByRidCache;
    private final Cache parentGroupsByDnCache;
    
    GroupContainer(int cacheSize, boolean inMemoryCache) {
        super(cacheSize, inMemoryCache, false, "role", MESSAGE_BUNDLE, CACHE_FULL_MESSAGE);
        dnToRoleCache = createCache(MESSAGE_BUNDLE, CACHE_FULL_MESSAGE);
        groupsByRidCache = createCache(MESSAGE_BUNDLE, CACHE_FULL_MESSAGE);
        parentGroupsByDnCache = createCache(MESSAGE_BUNDLE, CACHE_FULL_MESSAGE);
    }    
    
    synchronized Role getGroupByDn(String dn) {
        return (Role) dnToRoleCache.retrieve(dn.toLowerCase());
    }

    synchronized boolean containsDn(String dn) {
        return getGroupByDn(dn) != null;
    }

    synchronized String storeGroup(ActiveDirectoryGroup group, String[] parents) {
        String principalName = storePrincipal(group);
        final String dn = group.getOriginalDn().toLowerCase();
        dnToRoleCache.store(dn, group, Long.MAX_VALUE, null, GROUPS_CACHE_PREFIX);
        if (group.getRID() != null) { // Only NT Authority groups will be used
            groupsByRidCache.store(group.getRID(), group, Long.MAX_VALUE, null, GROUPS_CACHE_PREFIX);
        }
        parentGroupsByDnCache.store(dn, parents, Long.MAX_VALUE, null, GROUPS_CACHE_PREFIX);
        return principalName;
    }

    synchronized ActiveDirectoryGroup getByRid(Long rid) {
        return (ActiveDirectoryGroup) groupsByRidCache.retrieve(rid);
    }

    synchronized void buildHierarchy() {
        Serializable[] keysForGroup = parentGroupsByDnCache.getKeysForGroup(GROUPS_CACHE_PREFIX);
        for (Serializable key : keysForGroup) {
            buildHierarchy((String) key);
        }
    }

    synchronized void buildHierarchy(String roleDn) {
        roleDn = roleDn.toLowerCase();
        ActiveDirectoryGroup role = (ActiveDirectoryGroup) dnToRoleCache.retrieve(roleDn);
        if (role != null) {
            Map<String, ActiveDirectoryGroup> parents = new HashMap<String, ActiveDirectoryGroup>();
            addParents(roleDn, parents);
            Collection<ActiveDirectoryGroup> values = new TreeSet<ActiveDirectoryGroup>(parents.values());
            values.remove(role); // groups can be recursive so it might contain itself
            ActiveDirectoryGroup[] toArray = values.toArray(new ActiveDirectoryGroup[parents.size()]);
            role.setParents(toArray);
        }
    }
    
    private void addParents(String roleDn, Map<String, ActiveDirectoryGroup> groups) {
        String[] parents = (String[]) parentGroupsByDnCache.retrieve(roleDn);
        if (parents == null) {
            return;
        }

        for (String parentDn : parents) {
            parentDn = parentDn.toLowerCase();
            // need to guard against recursive groups, if we've seen it before ignore it
            if (!groups.containsKey(parentDn)) {
                if (containsDn(parentDn)) {
                    ActiveDirectoryGroup foundRole = (ActiveDirectoryGroup) dnToRoleCache.retrieve(parentDn);
                    if (foundRole != null) {
                        groups.put(parentDn, foundRole);
                    } else if (logger.isDebugEnabled()) {
                        logger.debug("Found NULL group in parent list");
                    }
                }
                addParents(parentDn, groups);
            }
        }
    }
    
    synchronized void updateRemovedGroups(Collection<String> missingGroups) {
        for (String groupName : missingGroups) {
            ActiveDirectoryGroup group = retrievePrincipal(groupName);
            if (group != null) {
                removeGroup(group);           
            }
        }
    }

    public synchronized void removeGroup(ActiveDirectoryGroup group) {
        removePrincipal(group);
        final String dn = group.getOriginalDn().toLowerCase();
        dnToRoleCache.store(dn, null, 0L, null, GROUPS_CACHE_PREFIX);
        if (group.getRID() != null) { // Only NT Authority groups will be used
            groupsByRidCache.store(group.getRID(), group, 0L, null, GROUPS_CACHE_PREFIX);
        }
        parentGroupsByDnCache.store(dn, null, 0L, null, GROUPS_CACHE_PREFIX);
    }
    
    public synchronized void close() {
        super.close();
        closeCache(dnToRoleCache);
        closeCache(groupsByRidCache);
        closeCache(parentGroupsByDnCache);
    }
}