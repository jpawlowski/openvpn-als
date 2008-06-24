
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
 * <i>System Authentication Module</i> only used for authentication of
 * the <i>Embedded Client</i>. This is a user / password
 * only authentication module.
 */
public class EmbeddedClientAuthenticationModule extends AbstractPasswordAuthenticationModule {

    /**
     * The name of this authentication module
     */
    public static final String MODULE_NAME = "EmbeddedClient";
    
    /**
     * Constructor
     */
    public EmbeddedClientAuthenticationModule() {
        super(MODULE_NAME, true);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.adito.security.AuthenticationModule#getInclude()
     */
    public String getInclude() {
        return null;
    }
}
