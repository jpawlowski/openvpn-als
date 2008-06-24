
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
			
package com.adito.security;

/**
 * Exception thrown by the {@link com.adito.security.UserDatabase}.
 * The intention of this exception is to tell the caller that roles
 * are required for the specified users.  This is typically thrown if there
 * is an attempt to remove all of the roles from the user.
 */
public final class GroupsRequiredForUserException extends UserDatabaseException {
    private final String username;
    
    /**
     * @param username
     */
    public GroupsRequiredForUserException(String username) {
        super("");
        this.username = username;
    }
    
    /**
     * @return String
     */
    public String getUsername() {
        return username;
    }
}