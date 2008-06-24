package com.adito.core;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.adito.boot.RequestHandlerResponse;

public class ServletResponseAdapter implements RequestHandlerResponse {

	HttpServletResponse response;
	public ServletResponseAdapter(HttpServletResponse response) {
		this.response = response;
	}
	public void setField(String header, String value) {
		response.setHeader(header, value);
	}

	public void addField(String header, String value) {
		response.addHeader(header, value);
	}

	public void removeField(String header) {
		response.setHeader(header, null);

	}

	public void sendError(int status, String message) throws IOException {
		response.sendError(status, message);
	}

	public void setStatus(int status) {
		response.setStatus(status);
	}

	public void setContentLength(int length) {
		response.setContentLength(length);
	}

	public void setReason(String reason) {
		
	}

	public OutputStream getOutputStream() throws IOException {
		return response.getOutputStream();
	}

	public void sendRedirect(String url) throws IOException {
		response.sendRedirect(url);
	}

	public void addCookie(Cookie cookie) {
		response.addCookie(cookie);

	}
    
    public void setCharacterEncoding(String charset) {
        response.setCharacterEncoding(charset);
    }    

}
