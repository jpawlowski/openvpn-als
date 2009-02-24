
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
 * A set of constants useful in working with the HTTP protocol, including
 * headers methods and repsonses.
 * 
 */
public class HttpConstants {
    
    /*
     * Headers
     */


	/**
	 * If unmodified since
	 */
	public static final String HDR_IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
	
    /**
     * If modified since
     */
    public static final String HDR_IF_MODIFIED_SINCE = "If-Modified-Since";

    /**
     * Proxy Connection
     */
    public static final String HDR_PROXY_CONNECTION = "Proxy-Connection";

    /**
     * Close
     */
    public static final String HDR_CLOSE = "Close";

    /**
     * Cookie
     */
    public static final String HDR_COOKIE = "Cookie";

    /**
     * X-Forwarded-For
     */
    public static final String HDR_X_FORWARDED_FOR = "X-Forwarded-For";

    /**
     * Accept-Encoding
     */
    public static final String HDR_ACCEPT_ENCODING = "Accept-Encoding";

    /**
     * Transfer-Encoding
     */
    public static final String HDR_TRANSFER_ENCODING = "Transfer-Encoding";

    /**
     * TE
     */
    public static final String HDR_TE = "TE";

    /**
     * Trailer
     */
    public static final String HDR_TRAILER = "Trailer";

    /**
     * Proxy Authorization
     */
    public static final String HDR_PROXY_AUTHORIZATION = "Proxy-Authorization";

    /**
     * Proxy Authenitcate
     */
    public static final String HDR_PROXY_AUTHENTICATE = "Proxy-Authenticate";

    /**
     * Upgrade
     */
    public static final String HDR_UPGRADE = "Upgrade";

    /**
     * keep-alive
     */
    public static final String HDR_KEEP_ALIVE = "Keep-Alive";

    /**
     * Content-Type
     */
    public static final String HDR_CONTENT_TYPE = "Content-Type";

    /**
     * Content-Length
     */
    public static final String HDR_CONTENT_LENGTH = "Content-Length";

    /**
     * Host
     */
    public final static String HDR_HOST = "Host";
    
    /**
     * Referer
     */
    public final static String HDR_REFERER = "Referer";
    
    /**
     * Referer
     */
    public final static String HDR_CONNECTION = "Connection";
    
    /**
     * Authorization
     */
    public static final String HDR_AUTHORIZATION = "Authorization";
    
    /**
     * WWW-Authenticate
     */
    public static final String HDR_WWW_AUTHENTICATE = "WWW-Authenticate";

    /**
     * Content-Encoding
     */
    public static final String HDR_CONTENT_ENCODING = "Content-Encoding";

	/**
	 * Location 
	 */
	public static final String HDR_LOCATION = "Location";

    /**
     * Cache-Control
     */
    public static final String HDR_CACHE_CONTROL = "Cache-Control";

	/**
	 * Allow
	 */
	public static final String HDR_ALLOW = null;

	/**
	 * Server header
	 */
	public static final String HDR_SERVER = "Server";

	/**
	 * Server header
	 */
	public static final String HDR_DATE = "Date";

    /**
     * Pragma
     */
    public static final String HDR_PRAGMA = "Pragma";
    
    /**
     * Expires
     */
    public static final String HDR_EXPIRES = "Expires";

	/**
	 * Set cookie
	 */
	public static final String HDR_SET_COOKIE = "Set-Cookie";

	/**
	 * Last modified
	 */
	public static final String HDR_LAST_MODIFIED = "Last-Modified";
    
    /*
     * Http methods
     */
    
    /**
     * Connect
     */
    public final static String METHOD_CONNECT = "CONNECT";
    
    /**
     * Get
     */
    public final static String METHOD_GET = "GET";
    
    /**
     * Get
     */
    public final static String METHOD_POST = "POST";
    
    /**
     * 200 - OK
     */
    public static final int RESP_200_OK = 200;

	/**
	 * 304 - Not modified
	 */
	public static final int RESP_304_NOT_MODIFIED = 304;
    
    /**
     * 401 - Unauthorized
     */
    public static final int RESP_401_UNAUTHORIZED = 401;
    
    /**
     * 403 - Forbidden
     */
    public final static int RESP_403_FORBIDDEN = 403;

	/**
	 * 404 - Not found
	 */
	public static final int RESP_404_NOT_FOUND = 404;

	/**
	 * 404 - Not found
	 */
	public static final int RESP_405_METHOD_NOT_ALLOWED = 405;
    
    /**
     * 407 - Proxy authentication required
     */
    public static final int RESP_407_PROXY_AUTHENTICATION_REQUIRED = 407;

	/**
	 * Precondition failed
	 */
	public static final int RESP_412_PRECONDITION_FAILED = 411;
    
    /**
     * 500 - Internal server error
     */
    public static final int RESP_500_INTERNAL_SERVER_ERROR = 500;

    /**
     * 503 - Service unavailable
     */
    public static final int RESP_503_SERVICE_UNAVAILABLE = 503;

}
