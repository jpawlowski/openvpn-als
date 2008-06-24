package com.adito.tasks;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TaskHttpServletResponse extends HttpServletResponseWrapper {
    
    final static Log log = LogFactory.getLog(TaskHttpServletResponse.class);
    
    private HttpServletResponse wrapping;
    private String contentType;
    private long contentLength;
    private Locale locale;
    private ServletOutputStream out;
    private PrintWriter writer;
    private boolean commited;
    
    public TaskHttpServletResponse(HttpServletResponse wrapping) {
        super(wrapping);
        locale = wrapping.getLocale();
        out = new DummyOutputStream();
        writer = new PrintWriter(out);
        commited = false;
        this.wrapping = wrapping;
    }

    public void addCookie(Cookie cookie) {
        log.error("Cannot add cookies when using task wrapper response");
    }

    public void addDateHeader(String name, long date) {
        log.error("Cannot add date headers when using task wrapper response");
    }

    public void addHeader(String name, String value) {
        log.error("Cannot add headers when using task wrapper response");

    }

    public void addIntHeader(String name, int value) {
        log.error("Cannot add int headers when using task wrapper response");
    }

    public boolean containsHeader(String name) {
        return false;
    }

    public void sendError(int sc) throws IOException {
        log.error("Cannot send error when using task wrapper response");
    }    

    public void sendError(int sc, String msg) throws IOException {
        log.error("Cannot send error with message when using task wrapper response");
    }

    public void sendRedirect(String location) throws IOException {
        log.error("Cannot send redirect when using task wrapper response");    }

    public void setDateHeader(String name, long date) {
        log.error("Cannot set date header when using task wrapper response");
    }

    public void setHeader(String name, String value) {
        log.error("Cannot set header when using task wrapper response");
    }

    public void setIntHeader(String name, int value) {
        log.error("Cannot set int header when using task wrapper response");
    }

    public void setStatus(int sc) {
        log.error("Cannot set status when using task wrapper response");
    }

    public void setStatus(int sc, String sm) {
        log.error("Cannot set status and message when using task wrapper response");
    }

    public void flushBuffer() throws IOException {
        log.error("Cannot flush buffer when using task wrapper response");
    }

    public String getContentType() {
        return contentType;
    }

    public Locale getLocale() {
        return locale;
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return out;
    }

    public PrintWriter getWriter() throws IOException {
        return writer;
    }

    public boolean isCommitted() {
        return commited;
    }

    public void reset() {
        log.error("Cannot reset when using task wrapper response");
    }

    public void resetBuffer() {
        log.error("Cannot reset buffer when using task wrapper response");
    }

    public void setBufferSize(int arg0) {
        log.error("Cannot set buffer size when using task wrapper response");
    }

    public void setCharacterEncoding(String arg0) {
        log.error("Cannot set character encoding when using task wrapper response");

    }

    public void setContentLength(int arg0) {
        log.error("Cannot set content length when using task wrapper response");
        contentLength = arg0; 
    }

    public void setContentType(String arg0) {
        log.error("Cannot set content type when using task wrapper response");
        contentType = arg0;
    }

    public void setLocale(Locale arg0) {
        log.error("Cannot set locale when using task wrapper response");
        locale = arg0;
    }
    
    class DummyOutputStream extends ServletOutputStream {

        public void write(int b) throws IOException {            
        }
        
    }

}
