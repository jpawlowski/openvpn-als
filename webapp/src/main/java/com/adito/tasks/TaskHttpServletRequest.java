package com.adito.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.adito.core.MultiMap;

public class TaskHttpServletRequest implements HttpServletRequest {
    
    public static final String ATTR_TASK = "task";
    public static final String ATTR_TASK_FORWARD = "taskForward";
    public static final String ATTR_TASK_PROGRESS_HANDLED_EXTERNALLY = "taskProgressHandledExternally";
    
    private StringBuffer url;
    private String uri;
    private Hashtable<String, String[]> params;
    private Hashtable<String, Object> attributes;
    private String query;
    private String servletPath;
    
    private HttpSession session;
    private Cookie[] cookies;
    private String characterEncoding;
    private String authType;
    private Vector<String> headerNames;
    private String contextPath;
    private MultiMap headers;
    private String pathInfo;
    private String pathTranslated;
    private String remoteUser;
    private String requestedSessionId;
    private Principal userPrincipal;
    private boolean requestedSessionIdFromCookie;
    private boolean requestedSessionIdFromURL;
    private boolean requestedSessionIdValid;
    private String localAddr;
    private String localName;
    private int localPort;
    private Locale locale;
    private Vector<Locale> locales;
    private String protocol;
    private String remoteAddr;
    private String remoteHost;
    private int remotePort;
    private String scheme;
    private String serverName;
    private int serverPort;
    private boolean secure;
    
    public TaskHttpServletRequest(HttpServletRequest wrapping, Task task) {
        this.session = wrapping.getSession();
        String location = wrapping.getParameter("url");
        
        cookies = wrapping.getCookies();
        characterEncoding = wrapping.getCharacterEncoding();
        authType = wrapping.getAuthType();
        headerNames = new Vector<String>();
        headers = new MultiMap();
        for(Enumeration e = wrapping.getHeaderNames(); 
            e.hasMoreElements(); ) {
            String headerName = (String)e.nextElement();
            for(Enumeration f = wrapping.getHeaders(headerName); f.hasMoreElements(); ) {
                String headerValue = (String)f.nextElement();                
                headers.add(headerName, headerValue);
            }
        }
        contextPath = wrapping.getContextPath();
        pathInfo = wrapping.getPathInfo();
        pathTranslated = wrapping.getPathTranslated();
        remoteUser = wrapping.getRemoteUser(); // TODO check if needed
        requestedSessionId = wrapping.getRequestedSessionId(); // TODO check if needed
        userPrincipal = wrapping.getUserPrincipal(); // TODO check if needed
        requestedSessionIdFromCookie = wrapping.isRequestedSessionIdFromCookie();
        requestedSessionIdFromURL = wrapping.isRequestedSessionIdFromURL();
        requestedSessionIdValid = wrapping.isRequestedSessionIdValid();
        localAddr = wrapping.getLocalAddr();
        localName = wrapping.getLocalName();
        localPort = wrapping.getLocalPort();
        locale = wrapping.getLocale();
        locales = new Vector<Locale>();
        for(Enumeration e = wrapping.getLocales(); e.hasMoreElements(); locales.add((Locale)e.nextElement()) );
        protocol = wrapping.getProtocol(); 
        remoteAddr = wrapping.getRemoteAddr();
        remoteHost = wrapping.getRemoteHost();
        remotePort = wrapping.getRemotePort();
        scheme = wrapping.getScheme();
        serverName = wrapping.getServerName();
        serverPort = wrapping.getServerPort();
        secure = wrapping.isSecure();
        
        // Extract the query (everything after ?)
        int idx = location.indexOf('?');
        query = null;
        if(idx != -1) {
            query = location.substring(idx + 1);
        }
        
        // Extract the URI (everything before ?)
        uri = location;
        if(idx != -1) {
            uri = uri.substring(0, idx);
        }
        
        // Servlet path (same as URI?)
        servletPath = uri;
        
        // Extract parameters
        params = new Hashtable<String, String[]>();
        if(query != null) {
            StringTokenizer t = new StringTokenizer(query, "&");
            while(t.hasMoreTokens()) {
                String token = t.nextToken();
                idx = token.indexOf('=');
                String name = token;
                String val = null;
                if(idx != -1) {
                    name = token.substring(0, idx);
                    val = token.substring(idx + 1);
                }
                else {
                    val = "";
                }
                String[] vals = params.get(name);
                if(vals == null) {
                    vals = new String[] { val };
                }
                else {
                    String[] nvals = new String[vals.length + 1];
                    System.arraycopy(vals, 0, nvals, 0, vals.length);
                    nvals[vals.length] = val;
                    vals = nvals;
                }
                params.put(name, vals);
            }
        }
        
        // Initialise attributes
        attributes = new Hashtable<String, Object>();
        
        // Create the URL (the URL with protocol / host / post)
        try {
            URL u = new URL(new URL(wrapping.getRequestURL().toString()), uri);
            url = new StringBuffer(u.toExternalForm());
        } catch (MalformedURLException e) {
        }
        
        setAttribute(ATTR_TASK, task);
    }

    public String getServletPath() {
        return servletPath;
    }

    public String getQueryString() {
        return query;
    }

    public String getRequestURI() {
        return uri;
    }

    public StringBuffer getRequestURL() {
        return url;
    }

    public String getParameter(String arg0) {
        String[] vals = params.get(arg0);
        return vals == null ? null : vals[0];
    }

    public Map getParameterMap() {
        return params;
    }

    public Enumeration getParameterNames() {
        return params.keys();
    }

    public String[] getParameterValues(String arg0) {
        return params.get(arg0);
    }

    public String getMethod() {
        return "GET";
    }

    public HttpSession getSession(boolean arg0) {
        // We can never create a new session
        if(arg0)
            throw new IllegalStateException("Cannot create session");
        return getSession();
    }

    public Object getAttribute(String arg0) {
        return attributes.get(arg0);
    }

    public Enumeration getAttributeNames() {
        return attributes.keys();
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public int getContentLength() {
        return 0;
    }

    public String getContentType() {
        return null;
    }

    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    public BufferedReader getReader() throws IOException {
        return null;
    }

    public void removeAttribute(String arg0) {
        attributes.remove(arg0);
    }

    public void setAttribute(String arg0, Object arg1) {
        attributes.put(arg0, arg1);
    }

    public void setCharacterEncoding(String characterEncoding) throws UnsupportedEncodingException {
        this.characterEncoding = characterEncoding;
    }
    
    public Cookie[] getCookies() {
        return cookies;
    }

    public String getAuthType() {
        return authType;
    }

    public String getContextPath() {
        return contextPath;
    }

    public long getDateHeader(String name) {
        throw new IllegalArgumentException("Not implemented.");
    }

    public String getHeader(String name) {
        return headers.getString(name);
    }

    public Enumeration getHeaderNames() {
        return headerNames.elements();
    }

    public Enumeration getHeaders(String name) {
        return new Vector(headers.getValues(name)).elements();
    }

    public int getIntHeader(String name) {
        return Integer.parseInt(getHeader(name));
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public String getPathTranslated() {
        return pathTranslated;
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public String getRequestedSessionId() {
        return requestedSessionId;
    }

    public HttpSession getSession() {
        return session;
    }

    public Principal getUserPrincipal() {
        return userPrincipal;
    }

    public boolean isRequestedSessionIdFromCookie() {
        return requestedSessionIdFromCookie;
    }

    public boolean isRequestedSessionIdFromURL() {
        return requestedSessionIdFromURL;
    }

    public boolean isRequestedSessionIdFromUrl() {
        return isRequestedSessionIdFromURL();
    }

    public boolean isRequestedSessionIdValid() {
        return requestedSessionIdValid;
    }

    public boolean isUserInRole(String role) {
        throw new IllegalArgumentException("Not implemented.");
    }

    public String getLocalAddr() {
        return localAddr;
    }

    public String getLocalName() {
        return localName;
    }

    public int getLocalPort() {
        return localPort;
    }

    public Locale getLocale() {
        return locale;
    }

    public Enumeration getLocales() {
        return locales.elements();
    }

    public String getProtocol() {
        return protocol;
    }

    public String getRealPath(String path) {
        throw new IllegalArgumentException("Not implemented.");
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        throw new IllegalStateException("Request dispatcher not available.");
    }

    public String getScheme() {
        return scheme;
    }

    public String getServerName() {
        return serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public boolean isSecure() {
        return secure;
    }

}
