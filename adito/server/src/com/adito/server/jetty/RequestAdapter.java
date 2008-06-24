
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
			
package com.adito.server.jetty;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.http.HttpRequest;

import com.adito.boot.RequestHandlerRequest;
import com.adito.boot.RequestHandlerTunnel;

public class RequestAdapter implements RequestHandlerRequest {

    final static Log log = LogFactory.getLog(RequestAdapter.class);

    private HttpRequest request;

    public RequestAdapter(HttpRequest request) {
        this.request = request;
    }
    
    public HttpRequest getHttpRequest() {
    	return request;
    }

    public String getURIEncoded() {
        return request.getURI().toString();
    }

    public boolean isSecure() {
        return request.isConfidential();
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
        return request.getField(name);
    }

    public Enumeration getFieldValues(String name) {
        return request.getFieldValues(name);
    }

    public String getMethod() {
        return request.getMethod();
    }

    public Enumeration getFieldNames() {
        return request.getFieldNames();
    }

    public String getPath() {
        return request.getPath();
    }

    public Map getParameters() {
        return request.getParameters();
    }

    public String getHost() {
        return request.getHost();
    }

    public InputStream getInputStream() {
        return request.getInputStream();
    }

    public int getPort() {
        return request.getPort();
    }

    public void setTunnel(RequestHandlerTunnel tunnel) {
        request.getHttpConnection().setHttpTunnel(new TunnelAdapter(tunnel));
    }
    
    public void setTunnel(RequestHandlerTunnel tunnel, int timeoutMs) {
        request.getHttpConnection().setHttpTunnel(new TunnelAdapter(tunnel, timeoutMs));
    }
    
    public void setAttribute(String name, Object value) {
    	request.setAttribute(name, value);
    }

    public Object getAttribute(String name) {
    	return request.getAttribute(name);
    }
    
    public void setCharacterEncoding(String charset) {
        request.setCharacterEncoding(charset, false);
    }


	public int getContentLength() {
		return request.getContentLength();
	}

	public String getContentType() {
		return request.getContentType();
	}

}
