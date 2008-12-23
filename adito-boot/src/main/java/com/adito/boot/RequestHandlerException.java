
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
			
package com.adito.boot;

/**
 * Exception thrown during request handling
 * @see com.adito.boot.RequestHandler
 */
public class RequestHandlerException extends Exception {
    
    private static final long serialVersionUID = 1073812238913397873L;
    private int code;

    /**
     * Constructor
     * 
     * @param message message
     * @param code code
     */
    public RequestHandlerException(String message, int code) {
        super(message);
        this.code = code;
    }

    /**
     * Get the code
     * 
     * @return code
     */
    public int getCode() {
        return code;
    }


}
