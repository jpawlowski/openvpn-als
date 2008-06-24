
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
			
package com.adito.replacementproxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.ReplacementEngine;
import com.adito.boot.Replacer;
import com.adito.boot.RequestHandlerResponse;
import com.adito.boot.Util;
import com.adito.policyframework.LaunchSession;
import com.adito.security.User;
import com.adito.webforwards.WebForwardDatabaseFactory;
import com.adito.webforwards.WebForwardTypes;

/**
 */
public class ProxiedResponseDispatcher {

    final static Log log = LogFactory.getLog(ProxiedResponseDispatcher.class);

    private RequestProcessor requestProcessor;
    private ProxiedResponseProcessor responseProcessor;
    private RequestHandlerResponse response;
    private LaunchSession launchSession;
    private ContentCache cache;
    private SimpleDateFormat sdf;
    private ReplacementProxyMethodHandler proxyMethodHandler;

    public ProxiedResponseDispatcher(ReplacementProxyMethodHandler proxyMethodHandler,  RequestProcessor requestProcessor, ProxiedResponseProcessor responseProcessor,
                    RequestHandlerResponse response, LaunchSession launchSession, ContentCache cache) {
    	this.proxyMethodHandler = proxyMethodHandler;
        this.responseProcessor = responseProcessor;
        this.requestProcessor = requestProcessor;
        this.response = response;
        this.cache = cache;
        this.launchSession = launchSession;
        sdf = new SimpleDateFormat("EEEE, dd-MMM-yy HH:mm:ss zzz");

    }

    public void sendResponse() throws Exception {
        // Only set the content type for the response if the target server
        // returned it
        String type = responseProcessor.getContentType();
        if (type == null) {
            // If the mime type is not returned, then guess it from our own map
            type = requestProcessor.getSession().getServletContext().getMimeType(
                             requestProcessor.getRequestParameters().getProxiedURIDetails().getProxiedURL().getPath());
            if (log.isDebugEnabled())
            	log.debug("Guessed response type " + type + " from URL");
        }
        else {
        	if (log.isDebugEnabled())
        		log.debug("Sending response of type " + type);
        }
        if (type != null) {
            response.setField("Content-Type", type);
        }

        int code = responseProcessor.getRequestDispatcher().getResponseCode();
        if (log.isDebugEnabled())
        	log.debug("Sending response code " + code);
        response.setStatus(code);

        // Configure replacement proxy content encoding
        if(requestProcessor.getWebForward().getEncoding()==null || requestProcessor.getWebForward().getEncoding().equals(WebForwardTypes.DEFAULT_ENCODING)) {
            if(responseProcessor.getCharset() != null) {
                response.setCharacterEncoding(responseProcessor.getCharset());
            }
        } else
            response.setCharacterEncoding(requestProcessor.getWebForward().getEncoding());
        
        // Add all of the headers to the response
        copyHeaders();

        //
        OutputStream responseOut = getOutputStream(type);

        //
        InputStream serverIn = responseProcessor.getProxiedInputStream();
        User user = launchSession.getSession().getUser();

        if (serverIn != null) {

            // Get just type mime type part of the content type

            String mimeType = type;
            int idx = mimeType == null ? -1 : mimeType.indexOf(";");
            if (idx != -1) {
                mimeType = mimeType.substring(0, idx);
            }

            // If there is no content type or are no replaces
            List replacements = WebForwardDatabaseFactory.getInstance().getReplacementsForContent(
                            user.getPrincipalName(), Replacement.REPLACEMENT_TYPE_RECEIVED_CONTENT, mimeType,
                            requestProcessor.getRequestParameters().getProxiedURIDetails().getProxiedURL().toExternalForm());
            if (replacements.size() > 0) {
                // The default replacer
            	if (log.isDebugEnabled())
            		log.debug("Found " + replacements.size() + ", processing");
                ReplacementEngine replace = new ReplacementEngine();
                
                if(!requestProcessor.getWebForward().getEncoding().equals(WebForwardTypes.DEFAULT_ENCODING))
                    replace.setEncoding(requestProcessor.getWebForward().getEncoding());
                else
                    replace.setEncoding(responseProcessor.getCharset());
                
                replace.setCaseSensitive(false);
                replace.setDotAll(false);
                final BaseSearch baseSearch = new BaseSearch();
                replace.addPattern("(<base*\\s+(?:href)\\=['\\\"]*)([^\\s'>\\\"]*)([^>]*)(>)", baseSearch, "");

                Replacer replacer = new ProxyReplacer(requestProcessor, baseSearch);

                for (Iterator i = replacements.iterator(); i.hasNext();) {
                    Replacement r = (Replacement) i.next();
                    if (log.isDebugEnabled())
                    	log.debug("Adding replacement pattern '" + r.getMatchPattern() + "' = '" + r.getReplacePattern() + "'");
                    if (r.getReplacePattern().startsWith("#")) {
                        String cn = r.getReplacePattern().substring(1);
                        try {
                            Class clazz = Class.forName(cn);
                            Constructor c = clazz.getConstructor(new Class[] { URL.class, String.class });
                            Replacer re = (Replacer) (c.newInstance(new Object[] { requestProcessor.getRequestParameters().getProxiedURIDetails().getProxiedURLBase(), requestProcessor.getLaunchId() }));
                            if (log.isDebugEnabled())
                            	log.debug("Loaded custom replacer " + cn + ".");
                            replace.addPattern(r.getMatchPattern(), re, null);
                        } catch (Throwable t) {
                            log.error("Could not load custom replacer " + cn + ".", t);
                        }
                    } else {
                        replace.addPattern(r.getMatchPattern(), replacer, r.getReplacePattern());
                    }
                }

                OutputStream monitorOut = responseOut;
                int origLen = responseProcessor.getContentLength();
                int len = origLen == -1 ? 1024 : responseProcessor.getContentLength();
                if (log.isDebugEnabled())
                	log.debug("Reading response from target and processing into memory (" + len + " bytes buffer)");
                monitorOut = new ByteArrayOutputStream(len);
                long length = replace.replace(serverIn, monitorOut);
                if (log.isDebugEnabled())
                	log.debug("Replacement complete");
                                

                /* Tack some javascript on to the end of the page for JavaScript based automatic 
                 * authentication
                 */
               	if (mimeType.equals("text/html") && requestProcessor.getWebForward().getFormType().equals(WebForwardTypes.FORM_SUBMIT_JAVASCRIPT) 
               			&& !Boolean.TRUE.equals(launchSession.getAttribute(ProxiedRequestDispatcher.LAUNCH_ATTR_AUTH_POSTED))) {
               		length = proxyMethodHandler.addJavaScriptAuthenticationCode(launchSession, monitorOut, length);
                }

                if (origLen != -1) {
                	if (log.isDebugEnabled())
                		log.debug("New output length is " + length);
                    response.setContentLength((int) length);
                }
                if (log.isDebugEnabled())
                	log.debug("Writing respone back to client");
                responseOut.write(((ByteArrayOutputStream) monitorOut).toByteArray());
            } else {
            	if (log.isDebugEnabled())
            		log.debug("Just copying content type of " + type);
                Util.copy(serverIn, responseOut);
            }

            if (log.isDebugEnabled()) {
	            // Cache the response
	            if (responseProcessor.isCacheable()) {
	                log.debug("Caching page "
	                                + requestProcessor.getRequestParameters().getProxiedURIDetails().getProxiedURL()
	                                + (responseProcessor.getCacheExpiryDate() == null ? " (never expires)" : (" (expires on " + sdf
	                                                .format(responseProcessor.getCacheExpiryDate()))) + ")");
	                if (!cache.store(requestProcessor.getRequestParameters().getProxiedURIDetails().getProxiedURL(), (CacheingOutputStream) responseOut, responseProcessor
	                                .getCacheExpiryDate() == null ? null : new Long(responseProcessor.getCacheExpiryDate().getTime()),
	                                null)) {
	                    log.warn("Failed to cache page " + requestProcessor.getRequestParameters().getProxiedURIDetails().getProxiedURL());
	                } else {
	                    log.debug("Cached page " + requestProcessor.getRequestParameters().getProxiedURIDetails().getProxiedURL());
	                }
	            } else {
	                log.debug("Removing " + requestProcessor.getRequestParameters().getProxiedURIDetails().getProxiedURL() + " from cache");
	                if(cache != null) {
	                    cache.clear(requestProcessor.getRequestParameters().getProxiedURIDetails().getProxiedURL());
	                }
	            }
        	}
        } else {
            throw new Exception("No streams.");
        }
    }
    
    void copyHeaders() {

        for (Iterator i = responseProcessor.getHeaders().iterator(); i.hasNext();) {
            Header hi = (Header) i.next();
            
            /* This must be ADD, not SET as Set-Cookie could appear multiple times */
            response.addField(hi.getName(), hi.getVal());
        }
    }
    
    OutputStream getOutputStream(String type) throws IOException {
        if (responseProcessor.isCacheable()) {
        	if (log.isDebugEnabled())
        		log.debug("Opening output stream via a cache");
            return new CacheingOutputStream(response.getOutputStream(), responseProcessor.getContentLength() == -1 ? 1024
                            : responseProcessor.getContentLength(), responseProcessor.getHeaders(), type);
        } else {
        	if (log.isDebugEnabled())
        		log.debug("Opening non-cached output stream");
            return response.getOutputStream();
        }
    }
}
