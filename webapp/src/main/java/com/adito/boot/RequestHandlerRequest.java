
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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.Cookie;

/**
 * Encapsulates a request received by the server implementation and passed on to
 * all registered {@link com.adito.boot.RequestHandler} implementations.
 * 
 * @see com.adito.boot.RequestHandler
 */
public interface RequestHandlerRequest {


    static final int REQUEST_SCOPE = 1;
    static final int SESSION_SCOPE = 2;    
	/**
	 * Get the URI for the request
	 * 
	 * @return uri
	 */

	public String getURIEncoded();

	/**
	 * Get if the request was made securely (i.e. HTTPS).
	 * 
	 * @return is secure
	 */
	public boolean isSecure();

	/**
	 * Get the value of an HTTP header field sent by the client given its name.
	 * If more than one header exists then the first value will be returned.
	 * <p>
	 * <code>null</code> will be returned if no such header was sent.
	 * 
	 * @param name
	 *            name of header
	 * @return header value
	 */
	public String getField(String name);

	/**
	 * Get an enumeration of values for all HTTP header fields sent by the
	 * client that have the given name.
	 * 
	 * @param name
	 *            name of
	 * @return enumeration of header names
	 */
	public Enumeration getFieldValues(String name);

	/**
	 * Get the request method. E.g. GET, POST etc
	 * 
	 * @return request method
	 */
	public String getMethod();

	/**
	 * Get an enumeration of all HTTP header field names
	 * 
	 * @return enumeration of all HTTP header field names
	 */
	public Enumeration getFieldNames();

	/**
	 * Get the request path
	 * 
	 * @return get request path
	 */
	public String getPath();

	/**
	 * Return all request parameters as a map.
	 * 
	 * @return request parameters as map
	 */
	public Map getParameters();

	/**
	 * Get the request host name. The host is obtained either from an absolute
	 * URI, the <i>Host</i> request header, the connection or the local host
	 * name.
	 * 
	 * @return host
	 */
	public String getHost();

	/**
	 * Get the request input stream
	 * 
	 * @return request input stream
	 */
	public InputStream getInputStream();

	/**
	 * Get the request port. The port is obtained either from an absolute URI,
	 * the <i>Host</i> request header, the connection or the default.
	 * <p>
	 * A value of 0 should be interpreted as the default port for the type of
	 * connection
	 * 
	 * @return port
	 */
	public int getPort();

	/**
	 * Configure a tunnel for this request. See {@link RequestHandlerTunnel} for
	 * more details.
	 * 
	 * @param tunnel
	 *            tunnel
	 */
	public void setTunnel(RequestHandlerTunnel tunnel);
	
	
	/**
	 * Configure a tunnel with a socket timeout. 
	 * @param tunnel
	 * @param timeoutMs
	 */
	public void setTunnel(RequestHandlerTunnel tunnel, int timeoutMs);
	

	/**
	 * Get the IP address of the client that made the request
	 * 
	 * @return remote address
	 */
	public String getRemoteAddr();

	/**
	 * Get the host name of the client that made the request. If the IP address
	 * could not be resolved the IP address will be returned.
	 * 
	 * @return remote host name or address
	 */
	public String getRemoteHost();

	/**
	 * Get an array of all the cookies sent with this request.
	 * 
	 * @return array of cookies
	 */
	public Cookie[] getCookies();
	
	
	/**
     * Set an arbitrary attribute on the request.
     *  
	 * @param name name 
	 * @param value value
	 */
	public void setAttribute(String name, Object value);
	
	/**
     * Get the value of a previously set request attribute.
     *  
     * @param name name
     * @return value or <code>null</code> if no such attribute exists 
	 */
	public Object getAttribute(String name);
    
    /**
     * Set the character encoding of the request. This is used to decode string parameters
     * in POST methods.
     * @param charset
     * @throws UnsupportedEncodingException
     */
    public void setCharacterEncoding(String charset) throws UnsupportedEncodingException;
    
    /**
     * Get the content type for this request.  This is retrieved from
     * the <i>Content-Type</i> header.
     * 
     * @return content type
     */
    public String getContentType();
    
    /**
     * Get the content length for this request. This is retrieved from
     * the <i>Content-Length</i> header.
     * 
     * @return content length
     */
    public int getContentLength();

}
