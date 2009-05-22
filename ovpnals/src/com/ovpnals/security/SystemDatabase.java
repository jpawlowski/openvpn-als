
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
			
package com.ovpnals.security;

import java.util.List;

import com.ovpnals.core.Database;
import com.ovpnals.navigation.Favorite;

/**
 * The <i>System Configuration> {@link com.ovpnals.core.Database}
 * implementation is responsible for storing an retrieving OpenVPN-ALS's basic
 * resources and configuration.
 */
public interface SystemDatabase extends Database {

    /**
     * @param type
     * @param favoriteKey the3 id fo the resource.
     * @param username
     * @throws Exception
     */
    public void addFavorite(int type, int favoriteKey, String username) throws Exception;

    /**
     * @param type
     * @param favoriteKey
     * @param username
     * @throws Exception
     */
    public void removeFavorite(int type, int favoriteKey, String username) throws Exception;

    /**
     * @param ipadrress
     * @return boolean
     * @throws Exception
     */
    public boolean verifyIPAddress(String ipadrress) throws Exception;

    /**
     * @param ipAddress
     * @param ipRestrictions
     * @return boolean
     * @throws Exception
     */
    public boolean verifyIPAddress(String ipAddress, IpRestriction[] ipRestrictions) throws Exception;

    /**
     * @param id
     * @throws Exception
     */
    public void removeIpRestriction(int id) throws Exception;

    /**
     * Create an Ip restriction. 
     * 
     * @param addressPattern address pattern
     * @param type type (see {@link IpRestriction#getType()}.
     * @throws Exception
     */
    public void addIpRestriction(String addressPattern, int type) throws Exception;

    /**
     * Update an Ip restriction.
     * 
     * @param restriction restriction to update
     * @throws Exception on any error
     */
    public void updateIpRestriction(IpRestriction restriction) throws Exception;

    /**
     * Get an array of all configured IP restrictions
     * 
     * @return Ip restrictions
     * @throws Exception on any error
     */
    public IpRestriction[] getIpRestrictions() throws Exception;
    
    /**
     * Get an IP restriction given its Id. <code>null</code> will be returned
     * if there is no such restriction.
     * 
     * @param id id of restriction
     * @return ip restriction or <code>null</code> if no such restriction
     * @throws Exception on any other error
     */
    public IpRestriction getIpRestriction(int id) throws Exception;

    /**
     * Swap priorities of <i>restriction1</i> with <i>restriction2</i> 
     * and persist to database.  
     * 
     * @param restriction1 restriction 1
     * @param restriction2 restriction 2
     * @throws Exception if restriction cannot be swapped
     */
    public void swapIpRestrictions(IpRestriction restriction1, IpRestriction restriction2) throws Exception;

    /**
     * @param type
     * @param username
     * @return List<Favorite>
     * @throws Exception
     */
    public List<Favorite> getFavorites(int type, User username) throws Exception;

    /**
     * @param type
     * @param user
     * @param favoriteKey
     * @return Favorite
     * @throws Exception
     */
    public Favorite getFavorite(int type, User user, int favoriteKey) throws Exception;

    /**
     * @return List<AuthenticationScheme>
     * @throws Exception
     */
    public List<AuthenticationScheme> getAuthenticationSchemeSequences() throws Exception;

    /**
     * @param realmID the realm id the authetication scheme is in.
     * @return List<AuthenticationScheme>
     * @throws Exception
     */
    public List<AuthenticationScheme> getAuthenticationSchemeSequences(int realmID) throws Exception;

    /**
     * @param id
     * @return AuthenticationScheme
     * @throws Exception
     */
    public AuthenticationScheme getAuthenticationSchemeSequence(int id) throws Exception;
    
    /**
     * @param name 
     * @param realmID 
     * @return AuthenticationScheme
     * @throws Exception
     */
    public AuthenticationScheme getAuthenticationSchemeSequence(String name, int realmID) throws Exception;

    /**
     * @param realmID 
     * @param name
     * @param description
     * @param modules
     * @param enabled
     * @param priority
     * @return AuthenticationScheme
     * @throws Exception
     */
    public AuthenticationScheme createAuthenticationSchemeSequence(int realmID, String name, String description, String[] modules, boolean enabled, int priority) throws Exception;

    /**
     * @param sequence
     * @throws Exception
     */
    public void updateAuthenticationSchemeSequence(AuthenticationScheme sequence) throws Exception;
    
    /**
     * @param scheme
     * @param schemes
     * @throws Exception
     */
    public void moveAuthenticationSchemeUp(AuthenticationScheme scheme, List<AuthenticationScheme> schemes) throws Exception;

    /**
     * @param scheme
     * @param schemes
     * @throws Exception
     */
    public void moveAuthenticationSchemeDown(AuthenticationScheme scheme, List<AuthenticationScheme> schemes) throws Exception;

    /**
     * @param id
     * @throws Exception
     */
    public void deleteAuthenticationSchemeSequence(int id) throws Exception;

}