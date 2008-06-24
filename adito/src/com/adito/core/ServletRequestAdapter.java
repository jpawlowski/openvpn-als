
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

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.RequestHandlerRequest;
import com.adito.boot.RequestHandlerTunnel;

public class ServletRequestAdapter implements RequestHandlerRequest {

    final static Log log = LogFactory.getLog(ServletRequestAdapter.class);

    
    private HttpServletRequest request;

    public ServletRequestAdapter(HttpServletRequest request) {
        this.request = request;
    }

    public boolean isSecure() {
        return request.isSecure();
    }

    public String getURIEncoded() {
        return request.getRequestURI();
    }
    
     public Cookie[] getCookies() {
        return request.getCookies();
    }

    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    public String getRemoteHost() {
        return request.getRemoteHost();
    }

    public String getField(String name) {
        return request.getHeader(name);
    }

    public Enumeration getFieldValues(String name) {
        return request.getHeaders(name);
    }

    public String getMethod() {
        return request.getMethod();
    }

    public Enumeration getFieldNames() {
        return request.getHeaderNames();
    }

    public String getPath() {
        return request.getPathInfo();
    }

    public Map getParameters() {
        return request.getParameterMap();
    }

    public String getHost() {
        return request.getRemoteHost();
    }

    public InputStream getInputStream() {
        try {
            return request.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    public int getPort() {
        return request.getServerPort();
    }

    public void setTunnel(RequestHandlerTunnel tunnel) {
        throw new UnsupportedOperationException("You cannot set a tunnel on a HttpServletRequest");
            //request.getHttpConnection().setHttpTunnel(new TunnelAdapter(tunnel));
    }

    public void setTunnel(RequestHandlerTunnel tunnel, int timeoutMs) {
        throw new UnsupportedOperationException("You cannot set a tunnel on a HttpServletRequest");
            //request.getHttpConnection().setHttpTunnel(new TunnelAdapter(tunnel));
    }
    
    public void setAttribute(String name, Object value) {
    	request.setAttribute(name, value);
    }

    public Object getAttribute(String name) { 
    	return request.getAttribute(name);
    }
    
    public void setCharacterEncoding(String charset) throws UnsupportedEncodingException {
        request.setCharacterEncoding(charset);
    }

	public int getContentLength() {
		return request.getContentLength();
	}

	public String getContentType() {
		return request.getContentType();
	}
}
