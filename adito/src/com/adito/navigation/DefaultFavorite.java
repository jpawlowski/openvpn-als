
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
			
package com.adito.navigation;

/**
 * Default implementation of a {@link com.adito.navigation.Favorite}.
 * 
 * @see com.adito.navigation.Favorite
 */
public class DefaultFavorite implements Favorite {

    // Private instance variables

    private String username;
    private int type, id, resourceId;

    /**
     * Constructor
     * 
     * @param id favorite ID
     * @param type resource type ID
     * @param username username
     * @param resourceId resource Id
     */
    public DefaultFavorite(int id, int type, String username, int resourceId) {
        this.id = id;
        this.type = type;
        this.username = username;
        this.resourceId = resourceId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.navigation.Favorite#getId()
     */
    public int getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.navigation.Favorite#getUsername()
     */
    public String getUsername() {
        return username;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.navigation.Favorite#getType()
     */
    public int getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.navigation.Favorite#getFavoriteKey()
     */
    public int getFavoriteKey() {
        return resourceId;
    }

}