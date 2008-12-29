
				/*
 *  Adito
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
			
package com.adito.vfs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.adito.security.SessionInfo;
import com.adito.vfs.store.apps.ApplicationStore;
import com.adito.vfs.store.downloads.TempStore;
import com.adito.vfs.store.site.SiteStore;
import com.adito.vfs.utils.URI;

/**
 * Manages {@link VFSStore} implementations. The core and plugins
 * register new store implementations here.
 */
public class VFSProviderManager {
	
	//	Private instance variables
    
    private Map<String, VFSProvider> providers;
    private static VFSProviderManager instance;

    /*
     * Prevent instantiation
     */
    private VFSProviderManager() {
    	providers = new HashMap<String, VFSProvider>();
        registerProvider(new DefaultVFSProvider("apps", ApplicationStore.class, "vfs"));
        registerProvider(new DefaultVFSProvider("site", SiteStore.class, "vfs"));
        registerProvider(new DefaultVFSProvider("temp", TempStore.class, "vfs"));
    }
    
    /**
     * Get an instance of the store manager, lazily creating it. The
     * default store implementations will also be registered upon 
     * creation.
     * 
     * @return VFSStoreManager
     */
    public static VFSProviderManager getInstance() {
        if(instance == null) {
            instance = new VFSProviderManager();
        }
        return instance;
    }
    
    /**
     * Get a provider given its scheme name. <code>null</code> will 
     * be returned if no such store exists.
     * 
     * @param scheme
     * @return provider
     */
    public VFSProvider getProvider(String scheme) {
    	VFSProvider provider = providers.get(scheme);
    	if(provider == null) {
	    	for(VFSProvider p : providers.values()) {
	    		if(p.willHandle(scheme)) {
	    			return p;
	    		}
	    	}
    	}
    	return provider;
    }
    
    /**
     * Register a provider.
     * 
     * @param provider provider
     */
    public void registerProvider(VFSProvider provider) {
    	providers.put(provider.getScheme(), provider);
    } 
    
    /**
     * @param repository
     * @return Map<String,VFSStore>
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public Map<String,VFSStore> createStores(VFSRepository repository) throws InstantiationException, IllegalAccessException {
        Map<String,VFSStore> stores = new TreeMap<String,VFSStore>();
        for(VFSProvider provider : providers.values()) { 
            VFSStore store = (VFSStore)(provider.getStoreClass()).newInstance();
            store.init(repository, provider);
            stores.put(store.getName(), store);
        }
        return stores;
    }

    /**
     * Deregister a provider given its scheme.,
     * 
     * @param scheme scheme
     */
    public void deregisterProvider(String scheme) {
        providers.remove(scheme);        
    }

    /**
     * Get all providers.
     * 
     * @return providers
     */
    public Collection<VFSProvider> getProviders() {
    	List<VFSProvider> l = new ArrayList<VFSProvider>(providers.values());
    	Collections.sort(l);
        return l;        
    }    
}
