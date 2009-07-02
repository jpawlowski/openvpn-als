package com.adito.vfs.utils;

import java.util.HashMap;
import java.util.Map;

import com.adito.security.PasswordCredentials;

/**
 * <p>
 * A class to hold a stores authentication credentials.
 */
public class DAVCredentialsCache {

    Map cache;

    /**
     * Constructor
     */
    public DAVCredentialsCache() {
        this.cache = new HashMap();
    }

    /**
     * <p>
     * Add a set of credentials for the named mount.
     * 
     * @param store The store name.
     * @param name The moujnt name
     * @param credentials The credentials to be cashed.
     */
    public void addCredentials(String store, String name, PasswordCredentials credentials) {
        if (cache.containsKey(store)) {
            Map storeMap = (Map) cache.get(store);
            storeMap.put(name, credentials);
        } else {
            cache.put(store, new HashMap());
            this.addCredentials(store, name, credentials);
        }
    }

    /**
     * <p>
     * Get the names DAVCredentials.
     * 
     * @param store The store name.
     * @param name The moujnt name
     * @return The requested DAVCredentials.
     */
    public PasswordCredentials getDAVCredentials(String store, String name) {
        Map storeMap = (Map) cache.get(store);
        if (storeMap != null)
            return (PasswordCredentials) storeMap.get(name);
        else
            return null;
    }

}
