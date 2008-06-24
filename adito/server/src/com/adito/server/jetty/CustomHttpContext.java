
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
import org.mortbay.http.ResourceCache;
import org.mortbay.util.Resource;

import com.adito.boot.ContextHolder;
import com.adito.boot.RequestHandler;

/**
 * <p>An extension to the standard Jetty {@link org.mortbay.http.HttpContext} 
 * that allows resources to be loaded from multiple {@link org.mortbay.http.ResourceCache}s.
 * 
 * <p>This is necessary for the plugin architecture so that plugins may register
 * their own <b>webapp</b> directories which can then be overlaid onto the 
 * namespace of the main Adito webapp. 
 */
public class CustomHttpContext extends HttpContext {

    // Package protected statics
    final static Log log = LogFactory.getLog(CustomHttpContext.class);

    // Private statics
    private static final long serialVersionUID = -4556775842230104865L;
    
    //	Private instance variables
    
    private List resourceCaches = new ArrayList();
    private RequestHandlerAdapter requestHandlerAdapater;

    /**
     * Constructor
     * 
     * @param server
     * @param path
     */
    public CustomHttpContext(HttpServer server, String path, boolean useDevConfig, ClassLoader bootLoader) throws Exception{
        super(server, path);
        addHandler(requestHandlerAdapater = new RequestHandlerAdapter());
        setParentClassLoader(bootLoader);
        setClassLoaderJava2Compliant(false);
        setTempDirectory(ContextHolder.getContext().getTempDirectory());
        setWelcomeFiles(new String[] { "showHome.do" });
        resourceCaches = new ArrayList();
    }

    /**
     * <p>
     * Add a new {@link RequestHandler}. Every time a request is received that
     * is not serviced by the main webapp, each registered handler will be
     * invoked until one deals with the request.
     * 
     * <p>
     * This shouldn't be called directly, but through
     * {@link com.adito.boot.Context#registerRequestHandler(RequestHandler)}
     * 
     * @param requestHandler handler to add
     */
    public void registerRequestHandler(RequestHandler requestHandler) {
        requestHandlerAdapater.registerRequestHandler(requestHandler);
    }

    /**
     * <p>
     * Remove a {@link RequestHandler} so that is no longer received unhandled
     * requests. See
     * {@link com.adito.boot.Context#deregisterRequestHandler(RequestHandler)}.
     * 
     * <p>
     * This shouldn't be called directly, but through
     * {@link com.adito.boot.Context#registerRequestHandler(RequestHandler)}
     * 
     * @param requestHandler handler to remove
     */
    public void deregisterRequestHandler(RequestHandler requestHandler) {
        requestHandlerAdapater.deregisterRequestHandler(requestHandler);
    }
    
    /**
     * <p>Add a new Resource Cache. Whenever a resource is requested, this
     * handler will search all registered resource caches until one can
     * locate it.
     * 
     * <p>This shouldn't be called directly, but through 
     * {@link com.adito.boot.Context#addResourceBase(URL)}
     * 
     * @param cache cache to add
     */
    public void addResourceCache(ResourceCache cache) {
        resourceCaches.add(cache);
    }

    /**
     * <p>Remove a Resource Cache. Whenever a resource is requested, this
     * handler will no longer use this cache.
     * 
     * <p>This shouldn't be called directly, but through 
     * {@link com.adito.boot.Context#removeResourceBase(URL)}
     *  
     * @param cache cache to remove
     */
    public void removeResourceCache(ResourceCache cache) {
        resourceCaches.remove(cache);
    }

    /* (non-Javadoc)
     * @see org.mortbay.http.ResourceCache#getResource(java.lang.String)
     */
    public Resource getResource(String pathInContext) throws IOException {
    	if (log.isDebugEnabled())
    		log.debug("Getting resource " + pathInContext + ", checking in plugins");
        // First try all the plugins with the path for an overidden resource
        for (Iterator i = resourceCaches.iterator(); i.hasNext();) {
            ResourceCache cache = (ResourceCache) i.next();
            Resource r = cache.getResource(pathInContext);
            if (r != null && r.exists() && !r.isDirectory()) {
            	if (log.isDebugEnabled())
            		log.debug("Found in " + cache.getBaseResource().toString());
                return r;
            }
        }
        if (log.isDebugEnabled())
        	log.debug("Checking for alias");

        // Get from the main webapp
        if (log.isDebugEnabled())
        	log.debug("Passsing to main webapp");
        return super.getResource(pathInContext);
    }
}
