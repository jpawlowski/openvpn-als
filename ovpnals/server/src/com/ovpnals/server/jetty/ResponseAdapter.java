
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.server.jetty;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.Cookie;

import org.mortbay.http.HttpResponse;

import com.ovpnals.boot.RequestHandlerResponse;

public class ResponseAdapter implements RequestHandlerResponse {

    private HttpResponse response;

    public ResponseAdapter(HttpResponse response) {
        this.response = response;
    }
    
    public HttpResponse getHttpResponse() {
    	return response;
    }

    public void setField(String header, String value) {
        response.setField(header, value);
    }

    public void sendRedirect(String url) throws IOException {
        response.sendRedirect(url);
    }

    public void addCookie(Cookie cookie) {
    	response.addSetCookie(cookie);
    }
    public void sendError(int code, String message) throws IOException {
        response.sendError(code, message);

    }

    public void setStatus(int status) {
        response.setStatus(status);

    }

    public void setContentLength(int length) {
        response.setContentLength(length);

    }

    public void setReason(String reason) {
        response.setReason(reason);
    }

    public void removeField(String name) {
        response.removeField(name);
    }

    public void addField(String header, String value) {
        response.addField(header, value);
    }

    public OutputStream getOutputStream() {
        return response.getOutputStream();
    }
    
    public void setCharacterEncoding(String charset) {
        response.setCharacterEncoding(charset, true);
    }

}
