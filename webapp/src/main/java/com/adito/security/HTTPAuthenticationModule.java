
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.Branding;

/**
 * Concrete implementation of {@link com.adito.security.AbstractHTTPAuthenticationModule}
 * that allows users to logon to the user interface using HTTP authentication.
 */
public class HTTPAuthenticationModule extends AbstractHTTPAuthenticationModule {

    final static Log log = LogFactory.getLog(HTTPAuthenticationModule.class);
    
    /**
     * Default realm
     */
    public final static String DEFAULT_REALM = Branding.PRODUCT_NAME;
    /**
     * The name of this authentication module
     */
    public static final String MODULE_NAME = "HTTP";

    /**
     * Constructor.
     */
    public HTTPAuthenticationModule() {
        super(MODULE_NAME, true, DEFAULT_REALM);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.security.AuthenticationModule#getInclude()
     */
    public String getInclude() {
        return "/WEB-INF/jsp/auth/httpAuth.jspf";
    }
}