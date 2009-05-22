package com.adito.boot;


/**
 * An abstract collection of {@link com.adito.boot.RepositoryStore} objects.
 * <p>
 * The concept of a <i>Repository</i> has been introduced to Adito to
 * deal with the fact that at some point we will want to introduce load
 * balancing and fail-over facilities to the server.
 * <p>
 * Some key artifacts outside of the {@link com.adito.core.Database} 
 * implementations should also be shared amongst all instances that may
 * be running on a network of Adito servers. These include 
 * {@link com.adito.boot.KeyStoreManager} instances, 
 * {@link com.adito.extensions.store.ExtensionStore} instances and 
 * others.
 * <p>
 * Each repository implementation should be able to handle multiple stores.
 * Each of these named stores then may contain multiple named <i>Entries</i>.
 * Each entry is simply of blob of data that may be written to and read from
 * using I/O streams. 
 * 
 * @see com.adito.boot.RepositoryFactory
 * @see com.adito.boot.LocalRepository
 * @see com.adito.boot.RepositoryStore
 */
public interface Repository {
	
    /**
     * Get a store instance given its name. If the store does not exists then the
     * implementation should create it. The same instance should be returned
     * for every subsequent call.
     *  
     * @param storeName store name
     * @return store instance
     */
	public RepositoryStore getStore(String storeName);
	
}
