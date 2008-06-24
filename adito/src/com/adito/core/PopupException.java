
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
			
package com.adito.core;


/**
 * Exception thrown by {@link com.adito.core.actions.AbstractPopupAuthenticatedDispatchAction}
 * to be caught by the global exception handle and as used a marker to indicate
 * that a different error message screen layout should be used.
 */
public class PopupException extends Exception {

    /**
     * Constructor.
     *
     */
    public PopupException() {
        super();
    }

    /**
     * Constructor.
     *
     * @param message message
     */
    public PopupException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message message
     * @param cause cause
     */
    public PopupException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param cause cause
     */
    public PopupException(Throwable cause) {
        super(cause);
    }

}
