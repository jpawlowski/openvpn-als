
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
 * Extended {@link Exception} that also requires a integer error code.
 *  
 */
public class CodedException extends Exception {
    private int code;

    /**
     * Constructor.
     * 
     * @param code error code
     *
     */
    public CodedException(int code) {
        super();
        init(code);
    }

    /**
     * Constructor.
     *
     * @param code error code
     * @param message
     * @param cause
     */
    public CodedException(int code, String message, Throwable cause) {
        super(message, cause);
        init(code);
    }

    /**
     * Constructor.
     *
     * @param code error code
     * @param message
     */
    public CodedException(int code, String message) {
        super(message);
        init(code);
    }

    /**
     * Constructor.
     *
     * @param code error code
     * @param cause
     */
    public CodedException(int code, Throwable cause) {
        super(cause);
        init(code);
    }

    /**
     * Get the error code
     * 
     * @return error code
     */
    public int getCode() {
        return code;
    }

    /**
     * Set the error code
     * 
     * @param code error code
     */
    public void setCode(int code) {
        this.code = code;
    }
    
    void init(int code) {
        this.code = code;
    }
}
