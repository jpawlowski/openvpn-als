
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

import com.adito.core.CoreException;

/**
 * Exception thrown by the {@link com.adito.security.UserDatabase}.
 */
public class UserDatabaseException extends CoreException {
    /**
     * Generic internal error processing a UserDatabase.
     */
    public static final int INTERNAL_ERROR = 0;
    
    /**
     * Error category
     */
    public final static String ERROR_CATEGORY = "userdatabase";
    
    /**
     * Default bundle name
     */
    public static final String DEFAULT_BUNDLE = "errors";
    
    /**
     * Constructor.
     * @param msg message
     */
    public UserDatabaseException(String msg) {
        this(msg, null);
    }

    /**
     * Constructor.
     * @param msg message 
     * @param cause underlying cause
     */
    public UserDatabaseException(String msg, Throwable cause) {
        super(INTERNAL_ERROR, ERROR_CATEGORY, DEFAULT_BUNDLE, cause, msg);
    }
    
    /**
     * Constructor.
     * @param errorCode 
     * @param bundle 
     */
    public UserDatabaseException(int errorCode, String bundle) {
        super(errorCode, ERROR_CATEGORY, bundle, null);
    }
}