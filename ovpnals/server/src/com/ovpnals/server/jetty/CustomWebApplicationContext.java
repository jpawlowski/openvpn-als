
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.http.ResourceCache;
import org.mortbay.jetty.servlet.AbstractSessionManager;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.util.Resource;

import com.ovpnals.boot.Branding;
import com.ovpnals.boot.ContextHolder;
import com.ovpnals.boot.SystemProperties;

/**
 * <p>
 * An extension to the standard Jetty
 * {@link org.mortbay.jetty.servlet.WebApplicationContext} that allows resources
 * to be loaded from multiple {@link org.mortbay.http.ResourceCache}s.
 * 
 * <p>
 * This is necessary for the plugin architecture so that plugins may register
 * their own <b>webapp</b> directories which can then be overlaid onto the
 * namespace of the main OpenVPN-ALS webapp.
 * 
 * <p>
 * Other OpenVPN-ALS specific webapp configuration is also performed here,
 * including setting up the special /defaultStyle.css alias that is used to
 * workaround the change to the way the CSS file is load. If this alias was not
 * set up, upgraders would have lost their CSS as /defaultStyle.css no longer
 * really exists.
 * 
 * <p>
 * Plugins also register new classpaths here so that any Java bytecode they may
 * require (be it in .CLASS format or .JAR format) may be loaded in the same
 * Class Loader as the main webapp.
 */
public class CustomWebApplicationContext extends WebApplicationContext {

    final static Log log = LogFactory.getLog(CustomWebApplicationContext.class);
    private List<ResourceCache> resourceCaches;
    private String additionalClasspath;
    private List<ResourceCache> reverseCaches;
    private Map<String, ResourceCache> resourceCacheMap;
    private ResourceCache mainWebappResourceCache;
    private Map<String, CacheState> cacheState;

    /**
     * Constructor
     * 
     * @param useDevConfig <code>true</code> if running in development mode
     * @throws Exception on any error
     */
    public CustomWebApplicationContext(boolean useDevConfig, ClassLoader bootLoader) throws Exception {
        super("webapp");
        Resource webInf = getWebInf();
        additionalClasspath = "";
        resourceCacheMap = new HashMap<String, ResourceCache>();
        reverseCaches = new ArrayList<ResourceCache>();
        cacheState = new HashMap<String, CacheState>();
        setContextPath("/");
        setDefaultsDescriptor("/com/ovpnals/boot/webdefault.xml");
        setDisplayName(Branding.PRODUCT_NAME);
        setTempDirectory(ContextHolder.getContext().getTempDirectory());
        setResourceAlias("/defaultStyle.css", "/css/defaultStyle.jsp");
        setParentClassLoader(bootLoader);
        setWelcomeFiles(new String[] { "showHome.do" });
        resourceCaches = new ArrayList<ResourceCache>();
        
        if("true".equals(SystemProperties.get("ovpnals.paranoidSessionManager", "true"))) {
            ((AbstractSessionManager)getServletHandler().getSessionManager()).setUseRequestedId(false);
            ((AbstractSessionManager)getServletHandler().getSessionManager()).setSecureCookies(true);
        }
    }

    @Override
    public void setClassPath(String classPath) {
        super.setClassPath(classPath);
        File webappBuild = new File(new File("build"), "webapp");
        if(webappBuild.exists()) {
            addClassPath(webappBuild.toURI().toString());
        }
    }

    /**
     * Get all resource caches
     * 
     * @return resource caches
     */
    public Collection<ResourceCache> getResourceCaches() {
        return resourceCaches;
    }

    /**
     * <p>
     * Add a new Resource Cache. Whenever a resource is requested, this handler
     * will search all registered resource caches until one can locate it.
     * 
     * <p>
     * This shouldn't be called directly, but through
     * {@link com.ovpnals.boot.Context#addResourceBase(URL)}
     * 
     * @param cache cache to add
     */
    public void addResourceCache(ResourceCache cache) {
        resourceCaches.add(cache);
        reverseCaches.clear();
        cacheState.clear();
        reverseCaches.addAll(resourceCaches);
        Collections.reverse(reverseCaches);
    }

    /**
     * <p>
     * Remove a Resrouce Cache. Whenever a resource is requested, this handler
     * will no longer use this cache.
     * 
     * <p>
     * This shouldn't be called directly, but through
     * {@link com.ovpnals.boot.Context#removeResourceBase(URL)}
     * 
     * @param cache cache to remove
     */
    public void removeResourceCache(ResourceCache cache) {
        resourceCaches.remove(cache);
        reverseCaches.clear();
        cacheState.clear();
        reverseCaches.addAll(resourceCaches);
        Collections.reverse(reverseCaches);
    }

    protected void addComponent(Object o) {
        if (o instanceof ResourceCache) {
            mainWebappResourceCache = ((ResourceCache) o);
        }
        super.addComponent(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.mortbay.http.ResourceCache#getResource(java.lang.String)
     */
    public Resource getResource(String pathInContext) throws IOException {
        boolean fullResourceCache = SystemProperties.get("ovpnals.fullResourceCache",
            String.valueOf(!(SystemProperties.get("ovpnals.useDevConfig", "false").equals("true")))).equals("true");
        Resource r;
        if (log.isDebugEnabled())
            log.debug("Request for " + pathInContext);

        // This is a work around to prevent WEB-INF getting listed by using the
        // path //WEB-INF
        if (pathInContext.indexOf("//WEB-INF") != -1) {
            return null;
        }

        /*
         * When in 'Full resource cache' mode, check if we already have cached
         * the resource
         */
        if (fullResourceCache && cacheState.containsKey(pathInContext)) {
            r = cacheState.get(pathInContext).getResource();
            if (log.isDebugEnabled())
                if (r == null)
                    log.debug("Resource " + pathInContext + " is permanently as missing.");
                else
                    log.debug("Resource " + pathInContext + " found in permanent cache");
            return r;
        }

        /*
         * Determine if the resource has already been found in a resource cache
         * (be it an extensions resource cache or the cores)
         */

        ResourceCache o = fullResourceCache ? null : (ResourceCache) resourceCacheMap.get(pathInContext);
        if (o == null) {

            /*
             * The existence of the resource has not yet been determined. Search
             * all resource caches in reverse until the extension is found. When
             * found, store which cache it was found in for quick look up in the
             * future.
             */
            if (log.isDebugEnabled())
                log.debug("Resource " + pathInContext + " not found in any resource cache, checking in plugins");

            for (Iterator i = reverseCaches.iterator(); i.hasNext();) {
                ResourceCache cache = (ResourceCache) i.next();
                r = cache.getResource(pathInContext);
                if (r != null && r.exists() && !r.isDirectory()) {
                    if (fullResourceCache) {
                        if (log.isDebugEnabled())
                            log.debug("    Found in " + cache.getBaseResource().toString());
                        cacheState.put(pathInContext, new CacheState(CacheState.FOUND, pathInContext, r));
                    } else {
                        if (log.isDebugEnabled())
                            log.debug("    Found in " + cache.getBaseResource().toString());
                        resourceCacheMap.put(pathInContext, cache);
                    }
                    return r;
                }
            }

            /*
             * The resource cannot be found in this caches base directory
             */
            if (log.isDebugEnabled())
                log.debug("   Not found");
        } else {
            /*
             * We know what cache the resource came from so check it still
             * exists and return. This will only happen when not in full cache
             * mode
             */
            r = o.getResource(pathInContext);
            if (r != null && r.exists() && !r.isDirectory()) {
                if (log.isDebugEnabled())
                    log.debug("    Found in " + o.getBaseResource().toString());
                return r;
            }
        }

        if (log.isDebugEnabled())
            log.debug("Checking for alias in plugins");
        String resourceAlias = getResourceAlias(pathInContext);
        if (resourceAlias != null) {

            /*
             * The resource was not found with its real name in any caches base
             * directory, so repeat the operation but look for the alias
             */

            if (log.isDebugEnabled())
                log.debug("    Found alias of " + resourceAlias + ", checking in plugins");
            for (Iterator i = reverseCaches.iterator(); i.hasNext();) {
                ResourceCache cache = (ResourceCache) i.next();
                r = cache.getResource(resourceAlias);

                /*
                 * When checking for resource modification, check for existence
                 * of file. This allows file to be removed at runtime without
                 * adding overhead when used on deployed server
                 */

                if (r != null && r.exists() && !r.isDirectory()) {
                    if (fullResourceCache) {
                        if (log.isDebugEnabled())
                            log.debug("    Found in " + cache.getBaseResource().toString());
                        cacheState.put(pathInContext, new CacheState(CacheState.FOUND, pathInContext, r));
                        return r;
                    } else {
                        if (log.isDebugEnabled())
                            log.debug("    Found in " + cache.getBaseResource().toString());
                        resourceCacheMap.put(pathInContext, cache);
                        return r;
                    }
                }
            }
            if (log.isDebugEnabled())
                log.debug("   Not found");
        }

        /*
         * The resource could not be found in any caches base directory, so pass
         * to the main webapp
         */

        if (log.isDebugEnabled())
            log.debug("Passing to main webapp");
        r = super.getResource(pathInContext);
        if (r != null && r.exists() && !r.isDirectory()) {

            /*
             * The resource has been found in the main webapps base directory,
             * store where it was found for quick lookup in future requests
             */

            if (log.isDebugEnabled())
                log.debug("    Found in main webapp");

            if (fullResourceCache) {
                cacheState.put(pathInContext, new CacheState(CacheState.FOUND, pathInContext, r));
            } else {
                resourceCacheMap.put(pathInContext, mainWebappResourceCache);
            }
            return r;
        } else {
            if (fullResourceCache) {
                if (log.isDebugEnabled())
                    log.debug("    Not found, caching as missing");
                cacheState.put(pathInContext, new CacheState(CacheState.MISSING, pathInContext, r));
            }
        }

        /* Not found at all */

        if (log.isDebugEnabled())
            log.debug("    Found in main webapp");

        return r;
    }
}