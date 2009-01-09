
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
			
package com.adito.networkplaces;

import com.adito.core.CoreException;


/**
 * Specialisation of {@link CoreException} for exceptions generate during
 * use of network places.
 */
public class NetworkPlacesException extends CoreException {

    
    /**
     * An attempt to paste a file or a folder into the same place as 
     * where the source file is located.
     */
    public final static int ERR_VFS_CANNOT_PASTE_TO_SOURCE = 1;
	
	/**
	 * Error category
	 */
	public final static String ERROR_CATEGORY = "networkPlaces";

	/**
	 * Constructor.
	 *
	 * @param code
	 */
	public NetworkPlacesException(int code) {
		super(code, ERROR_CATEGORY, NetworkPlacePlugin.MESSAGE_RESOURCES_KEY, (Throwable)null);
	}

	/**
	 * Constructor.
	 *
	 * @param code
	 * @param cause
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public NetworkPlacesException(int code, Throwable cause, String arg0, String arg1, String arg2, String arg3) {
		super(code, ERROR_CATEGORY, NetworkPlacePlugin.MESSAGE_RESOURCES_KEY, cause, arg0, arg1, arg2, arg3);
	}

	/**
	 * Constructor.
	 *
	 * @param code
	 * @param cause
	 * @param arg0
	 */
	public NetworkPlacesException(int code,Throwable cause, String arg0) {
		super(code, ERROR_CATEGORY, NetworkPlacePlugin.MESSAGE_RESOURCES_KEY, cause, arg0);
	}

	/**
	 * Constructor.
	 *
	 * @param code
	 * @param bundle
	 * @param cause
	 */
	public NetworkPlacesException(int code, Throwable cause) {
		super(code, ERROR_CATEGORY, NetworkPlacePlugin.MESSAGE_RESOURCES_KEY, cause);
	}

	/**
	 * Constructor.
	 *
	 * @param code
	 * @param arg0
	 */
	public NetworkPlacesException(int code, String arg0) {
		super(code, ERROR_CATEGORY, arg0);
	}
}
