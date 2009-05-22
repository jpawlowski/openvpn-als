
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
			
package com.ovpnals.policyframework;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.cache.BaseStorageListener;
import org.apache.commons.cache.Cache;
import org.apache.commons.cache.EvictionPolicy;
import org.apache.commons.cache.FileStash;
import org.apache.commons.cache.GroupMapImpl;
import org.apache.commons.cache.LRUEvictionPolicy;
import org.apache.commons.cache.MemoryStash;
import org.apache.commons.cache.SimpleCache;
import org.apache.commons.cache.Stash;
import org.apache.commons.cache.StorageListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.boot.ContextHolder;
import com.ovpnals.boot.SystemProperties;
import com.ovpnals.core.BundleActionMessage;
import com.ovpnals.core.GlobalWarning;
import com.ovpnals.core.GlobalWarningManager;
import com.ovpnals.core.GlobalWarning.DismissType;

/**
 * @param <T> 
 */
public class PrincipalCache<T extends Principal> {
    private static final Log logger = LogFactory.getLog(PrincipalCache.class);
    private final int cacheSize;
    private final boolean inMemoryCache;
    private final boolean caseSensitive;
    private final String cacheType;
    private final Cache principalCache; 
    
    /**
     * @param cacheSize
     * @param inMemoryCache
     * @param caseSensitive
     * @param cacheType
     * @param messageBundle
     * @param cacheFullText
     */
    public PrincipalCache(int cacheSize, boolean inMemoryCache, boolean caseSensitive, String cacheType, String messageBundle, String cacheFullText) {
        this.cacheSize = cacheSize;
        this.inMemoryCache = inMemoryCache;
        this.caseSensitive = caseSensitive;
        this.cacheType = cacheType;
        this.principalCache = createCache(messageBundle, cacheFullText);
    }
    
    /**
     * @param principalName
     * @return <tt>true</tt> if the principal is found
     */
    public final synchronized boolean containsPrincipal(String principalName) {
        principalName = fixUpPrincipalName(principalName);
        return retrievePrincipal(principalName) != null;
    }
    
    /**
     * @param principalName
     * @return principal
     */
    @SuppressWarnings("unchecked")
    public final synchronized T retrievePrincipal(String principalName) {
        principalName = fixUpPrincipalName(principalName);
        return (T) principalCache.retrieve(principalName);
    }
    
    /**
     * @return principal names
     */
    public final synchronized Collection<String> retrievePrincipalNames() {
        Serializable[] keysForGroup = principalCache.getKeysForGroup(cacheType);
        Collection<String> principalNames = new ArrayList<String>(keysForGroup.length);
        for (Serializable principalName : keysForGroup) {
            principalNames.add(principalName.toString());
        }
        return principalNames;
    }
    
    /**
     * @return principals
     */
    @SuppressWarnings("unchecked")
    public final synchronized Iterator<T> retrievePrincipals() {
        Serializable[] keysForGroup = principalCache.getKeysForGroup(cacheType);
        return new PrincipalCacheIterator(keysForGroup, this);
    }

    /**
     * @param principal
     * @return principal key
     */
    public final synchronized String storePrincipal(T principal) {
        String fixUpPrincipalName = fixUpPrincipalName(principal.getPrincipalName());
        if (logger.isDebugEnabled()) {
            logger.debug("Caching " + fixUpPrincipalName);
            
        }
        principalCache.store(fixUpPrincipalName, (Serializable) principal, Long.MAX_VALUE, null, cacheType);
        return fixUpPrincipalName;
    }
    
    /**
     * @param missingPrincipals
     */
    public final synchronized void updateRemovedPrincipals(Collection<String> missingPrincipals) {
        for (String principalName : missingPrincipals) {
            removePrincipal(principalName);
        }
    }
    
    /**
     * @param principal
     */
    public final synchronized void removePrincipal(T principal) {
        removePrincipal(principal.getPrincipalName());
    }
    
    /**
     * @param principleName
     */
    public final void removePrincipal(String principleName) {
        String fixUpPrincipalName = fixUpPrincipalName(principleName);
        principalCache.store(fixUpPrincipalName, null, 0L, null, cacheType);
    }
    
    protected String fixUpPrincipalName(String principalName) {
        return toLowerCaseIfRequired(principalName);
    }

    /**
     * @param principalName
     * @return to lower case if required
     */
    private String toLowerCaseIfRequired(String principalName) {
        return caseSensitive ? principalName : principalName.toLowerCase();
    }

    /**
     */
    public synchronized void close() {
        closeCache(principalCache);
    }
    
    protected final Cache createCache(String messageBundle, String cacheFullText) {
        File cacheDirectory = new File(ContextHolder.getContext().getTempDirectory(), "cache");
        File cacheTypeDirectory = new File(cacheDirectory, cacheType);
        Stash stash = inMemoryCache ? new MemoryStash(cacheSize) : new FileStash(Long.MAX_VALUE, cacheSize, new File[]{cacheTypeDirectory}, true);

        // eviction can't be used in testing as the policy creates a thread
        // which is only stopped on JVM exit, hence breaking the tests
        boolean isTestMode = "true".equals(SystemProperties.get("ovpnals.testing", "false"));
        EvictionPolicy evictionPolicy = isTestMode ? null : new LRUEvictionPolicy();
        
        SimpleCache cache = new SimpleCache(stash, evictionPolicy, null, new GroupMapImpl());
        cache.registerStorageListener(getStorageListener(messageBundle, cacheFullText));
        return cache;
    }

    private StorageListener getStorageListener(final String messageBundle, final String cacheFullText) {
        return new BaseStorageListener() {
            private static final long serialVersionUID = 4283488241230531541L;
            private int storageCounter = 0;
            private boolean addedWarning;

            public synchronized void stored(Serializable arg0, Serializable arg1, Long arg2, Long arg3, Serializable arg4) {
                storageCounter++;
            }

            public synchronized void cleared(Serializable arg0) {
                if (storageCounter == cacheSize && !addedWarning) {
                    BundleActionMessage message = new BundleActionMessage(messageBundle, cacheFullText, String.valueOf(cacheSize));
                    GlobalWarningManager.getInstance().addMultipleGlobalWarning(new GlobalWarning(GlobalWarning.MANAGEMENT_USERS, message, DismissType.DISMISS_FOR_USER));
                    addedWarning = true;
                }
                storageCounter--;
            }

            public synchronized void cleared() {
                storageCounter = 0;
                addedWarning = false;
                GlobalWarningManager.getInstance().removeGlobalWarningFromAllSessions(cacheFullText);
            }
        };
    }
    
    protected static void closeCache(Cache cache) {
        try {
            cache.clear();
            cache.unregisterStorageListeners();
        } catch (Exception e) {
            logger.error("Failed to close cache", e);
        }
    }
}