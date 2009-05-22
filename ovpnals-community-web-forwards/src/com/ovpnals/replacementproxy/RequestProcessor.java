
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
			
package com.ovpnals.replacementproxy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ovpnals.boot.HttpConstants;
import com.ovpnals.boot.PropertyList;
import com.ovpnals.boot.RequestHandlerRequest;
import com.ovpnals.boot.Util;
import com.ovpnals.core.RequestParameterMap;
import com.ovpnals.core.RequestParameterMap.ProxyURIDetails;
import com.ovpnals.core.stringreplacement.VariableReplacement;
import com.ovpnals.policyframework.LaunchSession;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.webforwards.ReplacementProxyWebForward;



/**
 * This class handles the first stage in the replacement proxy process in that
 * it gathers information sent from the clients browser, locates the session
 * the request is attached to and extracts the parameters required for passing
 * on to the next stage (the {@link ProxiedRequestDispatcher}).
 */
public class RequestProcessor {
    final static Log log = LogFactory.getLog(RequestProcessor.class);

    private ContentCache cache;
    private boolean getFromCache;
    private ReplacementProxyWebForward webForward;
    private RequestParameterMap requestParameters ;
    private RequestHandlerRequest request;
    private HeaderMap headerMap;
    private LaunchSession launchSession;
    private URL requestBaseURL;
    private ProxyURIDetails proxyURIDetails;

    /**
     * Constructor.
     *
     * @param cache cache for storing cacheable pages
     * @param maxAge maximum age of objects in the cach
     * @param request the request
     * @param launchSession the session the web forward was launched under     * @param proxiedUrl the proxied (or target) URL
     */
    public RequestProcessor(ContentCache cache,
                            int maxAge,
                            RequestHandlerRequest request,
                            LaunchSession launchSession) throws MalformedURLException {
        this.cache = cache;
        this.request = request;
        this.launchSession = launchSession;
        this.webForward = (ReplacementProxyWebForward)launchSession.getResource();

        /* Get the base URL as requested by the client (i.e. the location of the
         * OpenVPN-ALS server as the client sees it
         */ 
        StringBuffer buf = new StringBuffer();
        buf.append(request.isSecure() ? "https" : "http");
        buf.append("://");
        buf.append(request.getHost());
        if( request.getPort() > 0 && ( ( request.isSecure() && request.getPort() != 443 ) ||
        	( !request.isSecure() && request.getPort() != 80 ) ) ) {
        	buf.append(":");
        	buf.append(request.getPort());
        }
        requestBaseURL = new URL(buf.toString());
    }
    
    /**
     * Get the base URL as requested by the client (i.e. the location of the
     * OpenVPN-ALS server as the client sees it
     * 
     * @return request base URL
     */
    public URL getRequestBaseURL() {
    	return requestBaseURL;
    }

    /**
     * Get the ID of the {@link LaunchSession} this web forward was launched
     * under.
     * 
     * @return launch ID
     */
    public String getLaunchId() {
        return launchSession.getId();
    }
    
    public LaunchSession getLaunchSession() {
    	return launchSession;
    }

    /**
     * Get the HTTP method used by the client (e.g. GET or POST).
     *  
     * @return method
     */
    public String getMethod() {
        return request.getMethod();
    }

    /**
     * Get the request.
     * 
     * @return request
     */
    public RequestHandlerRequest getRequest() {
        return request;
    }

    /**
     * Get the session this request was made under.
     * 
     * @return session
     */
    public SessionInfo getSessionInfo() {
        return launchSession.getSession();
    }

    /**
     * Get the request parameter map.
     * 
     * @return request parameter map.
     */
    public RequestParameterMap getRequestParameters() {
        return requestParameters;
    }

    /**
     * Get if the page may be retrieved from the cache.
     * @return
     */
    public boolean isGetFromCache() {
        return getFromCache;
    }

    /**
     * Get the path of the URI for the request, including any request 
     * parameters.
     * 
     * @return base URI
     */
    public String getUriEncoded() {
        String uriEncoded = Util.isNullOrTrimmedBlank(requestParameters.getProxiedURIDetails().getProxiedURL().getFile()) ? "/" : requestParameters.getProxiedURIDetails().getProxiedURL().getFile();
    	if (log.isDebugEnabled())
    		log.debug("Returning URI " + uriEncoded);
    	return uriEncoded;
    }
    
    public void processRequest() throws Exception {

        // Create our own map of headers so they can be edited
        headerMap = new HeaderMap();
        for(Enumeration e = request.getFieldNames(); e.hasMoreElements(); ) {
            String n = (String)e.nextElement();
            for(Enumeration e2 = request.getFieldValues(n); e2.hasMoreElements(); ) {
                String v = (String)e2.nextElement();
                headerMap.putHeader(n, v);
            }
        }

        // Build up the parameter map
        requestParameters = new RequestParameterMap(request);
        proxyURIDetails = requestParameters.getProxiedURIDetails();

        VariableReplacement r = new VariableReplacement();
        r.setRequest(request);
        r.setSession(launchSession.getSession());
        r.setPolicy(launchSession.getPolicy());
        String actualURL = r.replace(webForward.getDestinationURL());
        
        if (proxyURIDetails.getProxiedURL() == null) {
            throw new Exception("No sslex_url parameter provided.");
        }
        
        if (log.isDebugEnabled())
        	log.debug("Proxying [" + request.getMethod() + "] " + proxyURIDetails.getProxiedURL());

        URL proxiedURLBase = proxyURIDetails.getProxiedURLBase();
        if (log.isDebugEnabled())
        	log.debug("Proxied URL base " + proxiedURLBase.toExternalForm());

        // The web forward may restrict access to the target URL
        PropertyList restrictTo = webForward.getRestrictToHosts();
        if(!restrictTo.isEmpty()) {
            boolean found = proxiedURLBase.getHost().equals(new URL(actualURL).getHost());
            for(Iterator i = restrictTo.iterator(); !found && i.hasNext(); ) {
                String host = (String)i.next();
                if(proxiedURLBase.getHost().matches(Util.parseSimplePatternToRegExp(host))) {
                    found = true;
                }
            }
            if(!found) {
                throw new Exception("This resource (" + proxiedURLBase.toExternalForm() + ") is restricted to a list of target hosts. This host is not in the list.");
            }

        }

        // Determine if the page can be retrieved from cache

        getFromCache = false;
        Date expiryDate = null;
        if (cache != null && HttpConstants.METHOD_GET.equals(request.getMethod()) && cache.contains(proxyURIDetails.getProxiedURL())) {
            getFromCache = true;

            // HTTP 1.0
            String cacheControl = request.getField(HttpConstants.HDR_PRAGMA);
            if (cacheControl != null && cacheControl.equalsIgnoreCase("no-cache")) {
                getFromCache = false;
            } else {
                String ifModifiedSince = request.getField(HttpConstants.HDR_IF_MODIFIED_SINCE);
                if (ifModifiedSince != null) {
                    try {
                        // Dont get from cache if
                        getFromCache = false;
                    } catch (Exception e) {
                    }
                }
            }

            // HTTP 1.1
            if (getFromCache) {
                cacheControl = request.getField(HttpConstants.HDR_CACHE_CONTROL);
                if (cacheControl != null) {
                    StringTokenizer tok = new StringTokenizer(cacheControl, ";");
                    while (tok.hasMoreTokens()) {
                        String t = tok.nextToken().trim();
                        String tl = t.toLowerCase();
                        if (t.startsWith("no-cache") || t.startsWith("no-store")) {
                            getFromCache = false;
                        } else if (tl.startsWith("max-age")) {
                            expiryDate = new Date();
                            try {
                                expiryDate.setTime(expiryDate.getTime() - (Integer.parseInt(Util.valueOfNameValuePair(tl))));
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }

            // Check expiry
            if(getFromCache) {
                CacheingOutputStream cos = (CacheingOutputStream) cache.retrieve(proxyURIDetails.getProxiedURL());
                if(expiryDate == null || ( expiryDate != null && cos.getCachedDate().after(expiryDate) ) ) {
                    // Still ok
                }
                else {
                	if (log.isDebugEnabled())
                		log.debug("Page expired");
                    getFromCache = false;
                }
            }
            else {
            	if (log.isDebugEnabled())
            		log.debug("Not using cached page.");
            }
        }
    }

    public ReplacementProxyWebForward getWebForward() {
        return webForward;
    }

    /**
     * @return
     */
    public String getRequestMethod() {
        return request.getMethod();
    }

    /**
     * @return
     */
    public HttpSession getSession() {
        return getSessionInfo().getHttpSession();
    }

    /**
     * @param hdr
     * @return
     */
    public String getHeader(String hdr) {
        Enumeration e = headerMap.getHeaders(hdr);
        return e == null ? null : (String)e.nextElement();
    }

    /**
     * @return
     */
    public Enumeration getHeaderNames() {
        return headerMap.keys();
    }

    /**
     * @param hdr
     * @return
     */
    public Enumeration getHeaders(String hdr) {
        return headerMap.getHeaders(hdr);
    }

    class HeaderMap extends Hashtable {

        private static final long serialVersionUID = -6768313767635812871L;

        HeaderMap() {

        }

        void putHeader(String name, String value) {
            Vector l = (Vector)get(name);
            if(l == null) {
                l = new Vector();
                put(name, l);
            }
            l.addElement(value);
        }

        void setHeader(String name, String value) {
            Vector l = new Vector();
            l.addElement(value);
            put(name, l);
        }

        Enumeration getHeaders(String hdr) {
            Vector l = (Vector)get(hdr);
            return l == null ? null : l.elements();

        }
    }
}
