
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

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.Cookie;

/**
 * Encapsulates a response to send to back the client.
 * 
 * @see com.adito.boot.RequestHandler
 */
public interface RequestHandlerResponse {

    /**
     * Set the value of a named HTTP header field to send back to the client with the response.
     * If a header field with the provided name already exists its current
     * value will be replaced. The header will only be set if the response 
     * has not yet started being sent. If this is not the case, and the
     * protocol is HTTP 1.1 then the field will be sent as a trailer, otherwise
     * it will be ignored.
     *  
     * @param header header field name
     * @param value header value
     * @see #addField(String, String)
     */
    public void setField(String header, String value);
    
    /**
     * Add a named HTTP header field to send back to the client with the response.
     * If a header value with the provided name already exists it will be left
     * alone and a new field will be added. The header will only be set if the response 
     * has not yet started being sent. If this is not the case, and the
     * protocol is HTTP 1.1 then the field will be sent as a trailer, otherwise
     * it will be ignored.
     * 
     * @param header header field name
     * @param value header value
     * @see #setField(String, String)
     */
    public void addField(String header, String value);

    /**
     * Remove a named HTTP header field from the response. If the response
     * is already being sent then this method will have no effect. If there
     * are multiple headers with the specfied name then all headers with that
     * name should be removed.
     * 
     * @param header header field to remove
     */
    public void removeField(String header);
    
    /**
     * Send a HTTP response
     * 
     * @param status status code
     * @param message message
     * @throws IOException on any error
     * @see HttpConstants
     */
    public void sendError(int status, String message) throws IOException;
    
    /**
     * Set the HTTP status code.
     * 
     * @param status status code
     * @see HttpConstants
     */
    public void setStatus(int status);
    
    /**
     * Set the content length header.
     * 
     * @param length content length
     */
    public void setContentLength(int length);
    
    /**
     * Set the response code reason
     * 
     * @param reason response code reason
     */
    public void setReason(String reason);
    
    /**
     * Get the output stream on which this response will be written.
     * 
     * @return output stream
     * @throws IOException
     */
    public OutputStream getOutputStream() throws IOException;
    
    /**
     * Send an HTTP redirect
     * 
     * @param url url to redirect client to
     * @throws IOException on any error
     */
    public void sendRedirect(String url) throws IOException;
    
    
    /**
     * Add a cookie to the response.
     * @param cookie
     */
    public void addCookie(Cookie cookie);
    
    
    
    public void setCharacterEncoding(String charset);

}
