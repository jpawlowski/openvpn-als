
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
			
package net.openvpn.als.vfs;

import java.util.Collection;

import net.openvpn.als.policyframework.LaunchSession;
import net.openvpn.als.vfs.utils.URI;
import net.openvpn.als.vfs.webdav.DAVAuthenticationRequiredException;
import net.openvpn.als.vfs.webdav.DAVException;

/**
 * A store is a child of a {@link net.openvpn.als.vfs.VFSRepository}
 * and  provides the entry point for a particular type of file system
 * (e.g. Local, SMB, FTP etc). 
 * <p>
 * It returns a list of {@link net.openvpn.als.vfs.VFSMount} objects
 * appropriate for the file system and the users security policy.
 * <p>
 * Either the core or plugins may register new store implementations using
 * {@link net.openvpn.als.vfs.VFSProviderManager#registerStore(Class)}.
 * 
 * @see net.openvpn.als.vfs.VFSProviderManager
 */
public interface VFSStore {

    /**
     * Get the root resource for this store.
     * 
     * @return root resource resource
     * @throws DAVException on any error
     */
    public VFSResource getStoreResource() throws DAVException;

    /**
     * Get the name of this name
     * 
     * @return store name
     */
    public String getName();
    
    /**
     * Get the provider for this store instance. This will only
     * be available after the store has been initialised.
     * 
     * @return provider
     */
    public VFSProvider getProvider();

    /**
     * Initialise the store
     * 
     * @param repository repository
     * @param provider provider
     */
    public void init(VFSRepository repository, VFSProvider provider);

    /**
     * Get the repository that initialised this store.  This will only
     * be available after the store has been initialised.
     * 
     * @return response
     */
    public VFSRepository getRepository();

    /**
     * Get a mount object given its name name. The mount name should be the name
     * of the mount only (no leading or trailing slashes). <code>null</code>
     * will be returned if no such mount exists.
     * 
     * @param mountString mount string
     * @param launchSession launch session
     * @return mount
     * @throws DAVException on any error.
     * @throws DAVAuthenticationRequiredException if OpenVPNALS authentication is required for the mount
     */
    public VFSMount getMountFromString(String mountString, LaunchSession launchSession) throws DAVException, DAVAuthenticationRequiredException;
    
    /**
     * Get the mount path name for a given mount. The store path
     * will be prepended to the supplied mount name.
     * 
     * @param mountName mount name
     * @return mount path
     */
    public String getMountPath(String mountName);

    /**
     * Get a list of all the available mount names
     * 
     * @return iterator of appropriate mounts
     * @throws Exception on any error
     */
    public Collection<String> getMountNames() throws Exception;

    /**
     * Get the guest username to use for this store / transaction
     * 
     * @return guest username
     * @see #getGuestPassword
     */
    public String getGuestUsername();

    /**
     * Get the guest password to use for this store / transaction
     * 
     * @return guest password
     * @see #getGuestUsername
     */
    public char[] getGuestPassword();

    /**
     * Get the encoding.
     * 
     * @return encoding
     */
    public String getEncoding();

}
