
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


/**
 * Interface describing the capabilities of VFS providers. Implementations
 * of this interface are registed with the {@link VFSProviderManager} and
 * are used to create {@link VFSStore} instances.
 */
public interface VFSProvider extends Comparable<VFSProvider> {
	
	/**
	 * Element is not applicable
	 */
	public final static int ELEMENT_NOT_APPLICABLE = 1;
	
	/**
	 * Element is applicable required
	 */
	public final static int ELEMENT_REQUIRED = 2;
	
	/**
	 * Element is applicable but not required
	 */
	public final static int ELEMENT_NOT_REQUIRED = 3;

	/**
	 * Get the class of the {@link VFSStore} implementation.
	 * 
	 * @return store class
	 */
	public Class getStoreClass();
	
	/**
	 * Get the message resource bundle ID
	 * 
	 * @return bundle
	 */
	public String getBundle();
	
	/**
	 * Get the scheme name store implemetations handle
	 * 
	 * @return name
	 */
	public String getScheme();

    /**
     * Get if fire resource access events should be fired.
     * 
     * @return fire events
     */
    public boolean isFireEvents();

    /**
     * Determine whether this provider handles a particular scheme (or protocol).
     * This will usually be trued if the store name (i.e. that returned by
     * {@link #getScheme()} is the same as scheme in the URI for the resource.
     * <p>
     * 
     * @param scheme
     * @return store handles scheme
     */
    public boolean willHandle(String scheme);

	/**
	 * Get if network places for this store requires a host. Will be
	 * one of {@link #ELEMENT_NOT_APPLICABLE}, {@link #ELEMENT_NOT_REQUIRED}
	 * or {@link #ELEMENT_REQUIRED}.
	 * 
	 * @return requires host
	 */
	public int getHostRequirement();

	/**
	 * Get if network places for this store requires  a port.  Will be
	 * one of {@link #ELEMENT_NOT_APPLICABLE}, {@link #ELEMENT_NOT_REQUIRED}
	 * or {@link #ELEMENT_REQUIRED}.
	 * 
	 * @return requires port
	 */
	public int getPortRequirement();

	/**
	 * Get if network places for this store requires user  info. Will be
	 * one of {@link #ELEMENT_NOT_APPLICABLE}, {@link #ELEMENT_NOT_REQUIRED}
	 * or {@link #ELEMENT_REQUIRED}.
	 * 
	 * @return requires user info
	 */
	public int getUserInfoRequirement();

	/**
	 * Get if network places for this store requires a path. Will be
	 * one of {@link #ELEMENT_NOT_APPLICABLE}, {@link #ELEMENT_NOT_REQUIRED}
	 * or {@link #ELEMENT_REQUIRED}.
	 * 
	 * @return requires path
	 */
	public int getPathRequirement();
	
	/**
	 * Get if this store supports a hidden file flag
	 * 
	 * @return hidden files support
	 */
	public boolean isHiddenFilesSupported();
}
