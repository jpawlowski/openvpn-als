
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

import com.ovpnals.core.CoreException;

/**
 */
public class SecurityErrorException extends CoreException {
    
	/**
	 * Error category
	 */
	public final static String ERROR_CATEGORY = "security";


	/**
	 * General authentication error
	 */
	public final static int INTERNAL_ERROR = 0;
    
    /**
     * An invalid ticket has been provided, probably by an external component
     * such as the launcher applet or agent.
     */
    public final static int ERR_INVALID_TICKET = 1;

	/**
	 * Constructor.
	 *
	 * @param code
	 */
	public SecurityErrorException(int code) {
		super(code, ERROR_CATEGORY);
	}

	/**
	 * Constructor.
	 *
	 * @param code
	 * @param bundle
	 * @param cause
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public SecurityErrorException(int code, String bundle, Throwable cause, String arg0, String arg1, String arg2, String arg3) {
		super(code, ERROR_CATEGORY, bundle, cause, arg0, arg1, arg2, arg3);
	}

	/**
	 * Constructor.
	 *
	 * @param code
	 * @param bundle
	 * @param cause
	 * @param arg0
	 */
	public SecurityErrorException(int code, String bundle, Throwable cause, String arg0) {
		super(code, ERROR_CATEGORY, bundle, cause, arg0);
	}

	/**
	 * Constructor.
	 *
	 * @param code
	 * @param arg0
	 */
	public SecurityErrorException(int code, String arg0) {
		super(code, ERROR_CATEGORY, arg0);
	}

    /**
     * Constructor.
     *
     * @param code
     * @param arg0
     * @param arg1
     */
    public SecurityErrorException(int code, String arg0, String arg1) {
        super(code, ERROR_CATEGORY, DEFAULT_BUNDLE, null, arg0, arg1, null, null);
    }

	/**
	 * Constructor.
	 *
	 * @param code
	 * @param cause
	 */
	public SecurityErrorException(int code, Throwable cause) {
		super(code, ERROR_CATEGORY, DEFAULT_BUNDLE, cause, cause.getMessage());
	}

	/**
	 * Constructor.
	 *
	 * @param code
	 * @param cause
	 * @param arg0 
	 */
	public SecurityErrorException(int code, Throwable cause, String arg0) {
		super(code, ERROR_CATEGORY, DEFAULT_BUNDLE, cause, arg0);
	}

    /**
     * Constructor.
     * 
     * @param code
     * @param arg0
     * @param arg1
     * @param arg2
     * @param cause
     */
    public SecurityErrorException(int code, String arg0, String arg1, String arg2, Throwable cause) {
        super(code, ERROR_CATEGORY, DEFAULT_BUNDLE, cause, arg0, arg1, arg2, null);
    }
}
