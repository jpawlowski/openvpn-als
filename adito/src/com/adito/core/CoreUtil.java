
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.cache.Cache;
import org.apache.commons.cache.CacheStat;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.taglib.tiles.ComponentConstants;
import org.apache.struts.tiles.ComponentContext;
import org.apache.struts.util.MessageResources;
import org.apache.struts.util.ModuleUtils;

import com.adito.boot.ContextHolder;
import com.adito.boot.PropertyDefinition;
import com.adito.boot.SystemProperties;
import com.adito.boot.Util;
import com.adito.core.actions.CoreAction;
import com.adito.core.actions.LicenseAgreementDispatchAction;
import com.adito.core.forms.CoreForm;
import com.adito.extensions.types.Plugin;
import com.adito.extensions.types.PluginType;
import com.adito.policyframework.NoPermissionException;
import com.adito.policyframework.PolicyDatabaseFactory;
import com.adito.policyframework.ResourceStack;
import com.adito.properties.Property;
import com.adito.properties.PropertyProfile;
import com.adito.properties.impl.profile.ProfilePropertyKey;
import com.adito.security.AuthenticationScheme;
import com.adito.security.Constants;
import com.adito.security.DefaultAuthenticationScheme;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.security.SystemDatabaseFactory;
import com.adito.security.User;
import com.adito.security.UserDatabase;
import com.adito.tasks.TaskHttpServletRequest;
import com.adito.vfs.UploadDetails;
import com.adito.vfs.UploadManager;
import com.adito.vfs.store.downloads.TempStore;
import com.adito.vfs.webdav.DAVUtilities;

/**
 * Useful utility method used throught the core webapplication.
 */
public class CoreUtil {
    final static Log log = LogFactory.getLog(CoreUtil.class);

    /**
     * Method to get the session temp download file.
     * 
     * @param session
     * @return File
     * @throws Exception
     */
    public static File getTempDownloadDirectory(SessionInfo session) throws Exception {
        final File tempDownloadDirectory = new File(ContextHolder.getContext().getTempDirectory(), TempStore.TEMP_DOWNLOAD_MOUNT_NAME);
        // create the download store if it does not exist.
        if (!tempDownloadDirectory.exists()) {        	
            if (!tempDownloadDirectory.mkdirs()) {
                throw new Exception("Could not create temporary download directory " + tempDownloadDirectory.getAbsolutePath()
                    + ".");
            }
        }
        // now create the actual session folder
        final File tempSessionDownloadDirectory = new File(tempDownloadDirectory, session.getUser().getPrincipalName() + "."
            + session.getHttpSession().getId());
        if (!tempSessionDownloadDirectory.exists()) {
        	
        	// Hook for cleaning up on logout
        	session.getHttpSession().setAttribute(DownloadContent.FILES_DOWNLOAD_CLEANUP_SESSION_HOOK, new HttpSessionBindingListener() {

				public void valueBound(HttpSessionBindingEvent event) {					
				}

				public void valueUnbound(HttpSessionBindingEvent event) {
					if(log.isInfoEnabled()) {
						log.info("Cleaning up temporary download directory " + tempSessionDownloadDirectory);
					}
					Util.delTree(tempSessionDownloadDirectory);
				}        		
        	});
        	
        	
            if (!tempSessionDownloadDirectory.mkdirs()) {
                throw new Exception("Could not create temporary session download directory " + tempSessionDownloadDirectory.getAbsolutePath()
                    + " for user "
                    + session.getUser().getPrincipalName()
                    + ".");
            }
        }
        return tempSessionDownloadDirectory;
    }


	/**
	 * Store to cache, checking for serializable.
	 * 
	 * @param cache cache to store object in 
	 * @param key cache key
	 * @param object object to cache
	 * @param ttl time-to-live in milliseconds
	 * @param cost cost
	 */
	public static void storeToCache(Cache cache, String key, Serializable object, long ttl, long cost) {
		if (log.isDebugEnabled()) {
			log.debug("Caching under " + key + ", ttl=" + ttl + ", cost="
					+ cost);
		}

        // NOTE Temporary code to make sure policy objects are serializable, in development and testing
        if ("true".equals(SystemProperties.get("adito.useDevConfig")) | "true".equals(SystemProperties.get("adito.testing"))) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
            } catch (Exception e) {
                String string = "********** Failed to cache policy database object. There is probably a non-serializable object somewhere in the object graph. PLEASE FIX ME ****************";
                System.err
                        .println(string);
                e.printStackTrace();
                throw new RuntimeException(string);
            }
        }

		cache.store(key, object, new Long(ttl
				+ System.currentTimeMillis()), cost);
		if (log.isDebugEnabled()) {
			log.debug("NUM_RETRIEVE_REQUESTED "
					+ cache.getStat(CacheStat.NUM_RETRIEVE_REQUESTED));
			log.debug("NUM_RETRIEVE_FOUND "
					+ cache.getStat(CacheStat.NUM_RETRIEVE_FOUND));
			log.debug("NUM_RETRIEVE_NOT_FOUND "
					+ cache.getStat(CacheStat.NUM_RETRIEVE_NOT_FOUND));
			log.debug("NUM_STORE_REQUESTED "
					+ cache.getStat(CacheStat.NUM_STORE_REQUESTED));
			log.debug("NUM_STORE_STORED "
					+ cache.getStat(CacheStat.NUM_STORE_STORED));
			log.debug("NUM_STORE_NOT_STORED "
					+ cache.getStat(CacheStat.NUM_STORE_NOT_STORED));
			log.debug("CUR_CAPACITY "
					+ cache.getStat(CacheStat.CUR_CAPACITY));
		}
	}

    /**
     * Get a cookie object from a request given its name. <code>null</code>
     * will be returned if the cookie cannot be found
     * 
     * @param name cookie name
     * @param request request.
     * @return cookie object
     */
    public static Cookie getCookie(String name, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(name)) {
                    return cookies[i];
                }
            }
        }
        return null;
    }

    /**
     * Get a cookie value from a request given its name. The specified default
     * will be returned if the cookie cannot be found
     * 
     * @param name cookie name
     * @param request request
     * @param defaultValue default value
     * @return cookie value
     */
    public static String getCookieValue(String name, HttpServletRequest request, String defaultValue) {
        Cookie c = getCookie(name, request);
        return c == null ? defaultValue : c.getValue();
    }

    /**
     * Get the ID of the sessions current property profile
     * 
     * @param session session
     * @return property profile ID
     */
    public static int getCurrentPropertyProfileId(HttpSession session) {
        PropertyProfile p = (PropertyProfile) session.getAttribute(Constants.SELECTED_PROFILE);
        if (p != null) {
            return p.getResourceId();
        }
        return 0;
    }

    /**
     * Get the path to the theme the user has selected or default if there is
     * none
     * 
     * @param session
     * @return String
     */
    public static String getThemePath(HttpSession session) {
        try {
            SessionInfo info = LogonControllerFactory.getInstance().getSessionInfo(session);
            if (info == null) {
                return Property.getProperty(new ProfilePropertyKey(0, null, "ui.theme", UserDatabaseManager.getInstance()
                                .getDefaultUserDatabase().getRealm().getResourceId()));
            } else {
                return Property.getProperty(new ProfilePropertyKey(getCurrentPropertyProfileId(session), info.getUser().getPrincipalName(),
                                "ui.theme", info.getUser().getRealm().getResourceId()));
            }
        } catch (Exception e) {
        }
        return "/theme/default";
    }

    /**
     * Get if tool tips are enabled for the specified session
     * @param session
     * @return boolean
     */
    public static boolean getToolTipsEnabled(HttpSession session) {
        try {
            SessionInfo info = LogonControllerFactory.getInstance().getSessionInfo(session);
            if (info == null) {
                return Property.getPropertyBoolean(new ProfilePropertyKey(0, null, "ui.toolTips", UserDatabaseManager.getInstance()
                                .getDefaultUserDatabase().getRealm().getResourceId()));
            } else {
                return Property.getPropertyBoolean(new ProfilePropertyKey(getCurrentPropertyProfileId(session), info.getUser().getPrincipalName(),
                                "ui.toolTips", info.getUser().getRealm().getResourceId()));
            }
        } catch (Exception e) {
        }
        return true;
    }

    /**
     * Add a page intercept listener to the provided session. This will be
     * invoked on every page request until it is removed. The listener will then
     * have the oppurtunity to return a forward redirecting the user if some
     * condition is satisified.
     * 
     * @param servletSession session to add listener to
     * @param listener listener to add
     */
    @SuppressWarnings("unchecked")
    public static void addPageInterceptListener(HttpSession servletSession, PageInterceptListener listener) {
        synchronized (servletSession) {
            List<PageInterceptListener> pagetInterceptListeners = (List<PageInterceptListener>) servletSession.getAttribute(Constants.PAGE_INTERCEPT_LISTENERS);
            if (pagetInterceptListeners == null) {
                pagetInterceptListeners = new ArrayList<PageInterceptListener>();
                servletSession.setAttribute(Constants.PAGE_INTERCEPT_LISTENERS, pagetInterceptListeners);
            }
            pagetInterceptListeners.add(listener);
        }
    }

    /**
     * Remove a page intercept listener from the provided session. This listener
     * will no longer be messaged upon every required
     * 
     * @param servletSession session to remove listener from
     * @param listener listener to remove
     */
    @SuppressWarnings("unchecked")
    public static void removePageInterceptListener(HttpSession servletSession, PageInterceptListener listener) {
        synchronized (servletSession) {
            List<PageInterceptListener> pagetInterceptListeners = (List<PageInterceptListener>) servletSession.getAttribute(Constants.PAGE_INTERCEPT_LISTENERS);
            if (pagetInterceptListeners != null) {
                pagetInterceptListeners.remove(listener);
                if (pagetInterceptListeners.size() == 0) {
                    servletSession.removeAttribute(Constants.PAGE_INTERCEPT_LISTENERS);
                }
                PageInterceptListener pil = (PageInterceptListener) servletSession.getAttribute(Constants.PAGE_INTERCEPTED);
                if (pil == listener) {
                    servletSession.removeAttribute(Constants.PAGE_INTERCEPTED);
                }
            }
        }
    }

    /**
     * Get a page intercept listener given its id. <code>null</code> will be
     * returned if no listener with the given id exists.
     * 
     * @param servletSession session that contains the listener
     * @param id listener id
     * @return listener
     */
    @SuppressWarnings("unchecked")
    public static PageInterceptListener getPageInterceptListenerById(HttpSession servletSession, String id) {
        synchronized (servletSession) {
            List<PageInterceptListener> pagetInterceptListeners = (List<PageInterceptListener>) servletSession.getAttribute(Constants.PAGE_INTERCEPT_LISTENERS);
            if (pagetInterceptListeners != null) {

                for (PageInterceptListener listener : pagetInterceptListeners) {
                    if (listener.getId().equals(id)) {
                        return listener;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Convenience method to remove a page intercept listener from the provided
     * session given its ID. This listener will no longer be messaged upon every
     * required
     * @param session
     * @param id
     * 
     */
    public static void removePageInterceptListener(HttpSession session, String id) {
        PageInterceptListener l = getPageInterceptListenerById(session, id);
        if (l != null) {
            removePageInterceptListener(session, l);
        }
    }

    /**
     * Check if there are page intercepts for the current action. If there are a
     * forward will be returned pointing to which page should be displayed next
     * 
     * @param action action object
     * @param mapping action mapping
     * @param request request object
     * @param response response object
     * @return forward
     * @throws Exception on any error
     */
    @SuppressWarnings("unchecked")
    public static ActionForward checkIntercept(Action action, ActionMapping mapping, HttpServletRequest request,
                                               HttpServletResponse response) throws Exception {

        // Cannot intercept during Ajax
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return null;
        }
        
        // Cannot intercept during task
        if(request.getAttribute(TaskHttpServletRequest.ATTR_TASK) != null) {
            return null;
        }
        
        ActionForward fwd = null;
        try {
            List<PageInterceptListener> pagetInterceptListeners = (List<PageInterceptListener>) request.getSession()
                            .getAttribute(Constants.PAGE_INTERCEPT_LISTENERS);
            if (pagetInterceptListeners != null) {
                PageInterceptListener currentIntercept = (PageInterceptListener) request.getSession()
                                .getAttribute(Constants.PAGE_INTERCEPTED);
                PageInterceptListener pil = null;
                if (currentIntercept != null) {
                    pil = currentIntercept;
                    fwd = currentIntercept.checkForForward(action, mapping, request, response);
                } else {
                    for (Iterator i = pagetInterceptListeners.iterator(); fwd == null && i.hasNext();) {
                        pil = (PageInterceptListener) i.next();
                        fwd = pil.checkForForward(action, mapping, request, response);
                    }
                }
                if (fwd != null) {
                    if (!pil.isRedirect()) {
                        request.getSession().setAttribute(Constants.PAGE_INTERCEPTED, pil);
                    } else {
                        CoreUtil.removePageInterceptListener(request.getSession(), pil);
                    }
                    return fwd;
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Page intercept failed.", e);
            throw e;
        }
    }

    /**
     * Initialise a
     * 
     * @param f
     * @param request
     * 
     */
    public static void initCoreForm(CoreForm f, HttpServletRequest request) {
        f.setReferer(CoreUtil.getReferer(request));
    }

    /**
     * @param session
     * @param agreement
     */
    @SuppressWarnings("unchecked")
    public static void requestLicenseAgreement(HttpSession session, LicenseAgreement agreement) {
        List<LicenseAgreement> l = (List<LicenseAgreement>) session.getAttribute(Constants.LICENSE_AGREEMENTS);
        if (l == null) {
            l = new ArrayList<LicenseAgreement>();
            session.setAttribute(Constants.LICENSE_AGREEMENTS, l);
            log.info("Requesting license agreement for " + agreement.getLicenseTextFile().getAbsolutePath());
            addPageInterceptListener(session, new PageInterceptListener() {
                public String getId() {
                    return "licenseAgreement";
                }

                public boolean isRedirect() {
                    return true;
                }

                public ActionForward checkForForward(Action action, ActionMapping mapping, HttpServletRequest request,
                                                     HttpServletResponse response) throws PageInterceptException {
                    if (!(action instanceof LicenseAgreementDispatchAction)) {
                        return new ActionForward("/showLicenseAgreement.do", true);
                    }
                    return null;
                }
            });
        }
        l.add(agreement);
    }

    /**
     * @param request
     * @param warnings
     */
    public static void addWarnings(HttpServletRequest request, ActionMessages warnings) {
        if (warnings == null) {
            return;
        }
        ActionMessages requestWarnings = (ActionMessages) request.getAttribute(Constants.REQ_ATTR_WARNINGS);
        if (requestWarnings == null) {
            requestWarnings = new ActionMessages();
        }
        requestWarnings.add(warnings);
        if (requestWarnings.isEmpty()) {
            request.removeAttribute(Constants.REQ_ATTR_WARNINGS);
            return;
        }
        request.setAttribute(Constants.REQ_ATTR_WARNINGS, requestWarnings);
    }

    /**
     * Get the current warnings, creating them if none exists
     * 
     * @return the warnings that already exist in the request, or a new
     *         ActionMessages object if empty.
     * @param request The servlet request we are processing
     */
    public static ActionMessages getWarnings(HttpServletRequest request) {
        ActionMessages warnings = (ActionMessages) request.getAttribute(Constants.REQ_ATTR_WARNINGS);
        if (warnings == null) {
            warnings = new ActionMessages();
        }
        return warnings;
    }

    /**
     * Save the specified warnings messages.
     * 
     * @param request request
     * @param warnings warnings
     */
    public static void saveWarnings(HttpServletRequest request, ActionMessages warnings) {
        if ((warnings == null) || warnings.isEmpty()) {
            request.removeAttribute(Constants.REQ_ATTR_WARNINGS);
            return;
        }
        request.setAttribute(Constants.REQ_ATTR_WARNINGS, warnings);
    }

    /**
     * @param request
     * @return String
     */
    public static String getReferer(HttpServletRequest request) {
        String ref = request.getHeader("Referer");
        if (ref != null) {
            ref = processRefererString(ref);
        } else {
            /*
             * IE sometimes doesnt send the referer (from javascript for
             * instance) so we should try using our referer parameter
             * work-around.
             */
            ref = getRequestReferer(request);
        }
        return ref;
    }
    
    /**
     * @param request
     * @return String
     */
    public static String getRequestReferer(HttpServletRequest request) {
        if (isRefererInRequest(request)) {
            return processRefererString(request.getParameter("referer"));
        }
        return null;
    }

    /**
     * @param request
     * @return boolean
     */
    public static boolean isRefererInRequest(HttpServletRequest request) {
        return request.getParameter("referer") != null;
    }

    /**
     * @param redirect
     * @return String
     */
    static String processRefererString(String redirect) {
        try {
            URL u = new URL(redirect);
            String query = u.getQuery();
            if (query != null && !query.equals("")) {
                StringBuffer nq = new StringBuffer();
                StringTokenizer t = new StringTokenizer(query, "&");
                String parm = null;
                while (t.hasMoreTokens()) {
                    parm = t.nextToken();
                    if (!parm.startsWith("referer=") && !parm.startsWith("vpnMessage=") && !parm.startsWith("vpnError=")) {
                        if (nq.length() > 0) {
                            nq.append("&");
                        }
                        nq.append(parm);
                    }
                }
                query = nq.length() == 0 ? null : nq.toString();
            }
            StringBuffer file = new StringBuffer();
            if (u.getPath() != null) {
                file.append(u.getPath());
            }
            if (query != null) {
                file.append("?");
                file.append(query);
            }
            if (u.getRef() != null) {
                file.append("#");
                file.append(u.getRef());
            }
            u = new URL(u.getProtocol(), u.getHost(), u.getPort(), file.toString());
            return u.toExternalForm();
        } catch (MalformedURLException mrule) {
            int idx = redirect.indexOf("?");
            if (idx != -1) {
                String query = redirect.substring(idx + 1);
                redirect = redirect.substring(0, idx);
                if (query.length() > 0) {
                    StringBuffer nq = new StringBuffer();
                    StringTokenizer t = new StringTokenizer(query, "&");
                    String parm = null;
                    while (t.hasMoreTokens()) {
                        parm = t.nextToken();
                        if (!parm.startsWith("vpnMessage=") && !parm.startsWith("vpnError=")) {
                            if (nq.length() > 0) {
                                nq.append("&");
                            }
                            nq.append(parm);
                        }
                    }
                    query = nq.length() == 0 ? null : nq.toString();
                    if (query != null) {
                        redirect = redirect + "?" + query;
                    }
                }
            }
            return redirect;
        }
    }
    
    /**
     * Remove a parameter from a path
     * 
     * @param path path to remove parameter from
     * @param name name of parameter
     * @return path with parameter removed
     */
    public static String removeParameterFromPath(String path, String name) {
        boolean first = true;
        int idx = path.indexOf("?" + name + "=");
        if(idx == -1) {
            first = false;
            idx = path.indexOf("&" + name + "=");
        }
        if(idx != -1) {
            int eidx = path.indexOf('&', idx + 1);
            if(eidx == -1) {
                path = path.substring(0, idx);
            }
            else {
                path = path.substring(0, idx) + ( first ? "?" : "&" ) + path.substring(eidx + 1, path.length());
            }
        }
        return path;
    }

    /**
     * Add a new parameter to an already encoded request path
     * 
     * @param path orginal path
     * @param name new parameter name
     * @param value new parameter value
     * @return new path
     */
    public static String addParameterToPath(String path, String name, String value) {
        StringBuffer buf = new StringBuffer(path);
        int idx = path.indexOf("?");
        if (idx != -1) {
            buf.append("&");
        } else {
            buf.append("?");
        }
        buf.append(name);
        buf.append("=");
        buf.append(Util.urlEncode(value));
        return buf.toString();
    }

    /**
     * @param forward
     * @param name
     * @param value
     * @return ActionForward
     */
    public static ActionForward addParameterToForward(ActionForward forward, String name, String value) {
        ActionForward f = new ActionForward(forward);
        f.setPath(addParameterToPath(forward.getPath(), name, value));
        return f;
    }

    /**
     * @param pageContext
     */
    public static void dumpComponentContext(PageContext pageContext) {
        ComponentContext compContext = (ComponentContext) pageContext.getAttribute(ComponentConstants.COMPONENT_CONTEXT,
            PageContext.REQUEST_SCOPE);
        if (log.isInfoEnabled())
            log.info("Component context dump");
        for (Iterator e = compContext.getAttributeNames(); e.hasNext();) {
            String n = (String) e.next();
            Object value = compContext.getAttribute(n);
            if (log.isInfoEnabled())
                log.info("   " + n + " = " + value);
        }
    }

    /**
     * Get the proxy URL to use for a user.
     * 
     * @param user user
     * @param propertyProfile property profile ID (0 for default / global)
     * @return String
     * @throws Exception
     */
    public static String getProxyURL(User user, int propertyProfile) throws Exception {
        String type = Property.getProperty(new ProfilePropertyKey(propertyProfile, user.getPrincipalName(), "clientProxy.type", user.getRealm().getResourceId()));
        if (type.equals("http") || type.equals("https")) {
            String hostname = Property.getProperty(new ProfilePropertyKey(propertyProfile, user.getPrincipalName(), "clientProxy.hostname", user.getRealm().getResourceId()));
            if (!hostname.equals("")) {
                StringBuffer url = new StringBuffer();
                url.append(type);
                url.append("://");
                
                String username = Property.getProperty(new ProfilePropertyKey(propertyProfile, user.getPrincipalName(), "clientProxy.username", user.getRealm().getResourceId()));
                String domain = Property.getProperty(new ProfilePropertyKey(propertyProfile, user.getPrincipalName(), "clientProxy.ntlmDomain", user.getRealm().getResourceId()));
                String auth = Property.getProperty(new ProfilePropertyKey(propertyProfile, user.getPrincipalName(), "clientProxy.preferredAuthentication", user.getRealm().getResourceId()));

                if (!username.equals("")) {

                    if (!domain.equals("")) {
                        url.append(DAVUtilities.encodeURIUserInfo(domain + "\\"));
                    }

                    url.append(DAVUtilities.encodeURIUserInfo(username));
                    String password = Property.getProperty(new ProfilePropertyKey(propertyProfile, user.getPrincipalName(), "clientProxy.password", user.getRealm().getResourceId()));
                    if (!password.equals("")) {
                        url.append(":");
                        url.append(DAVUtilities.encodeURIUserInfo(password));
                    }
                    url.append("@");
                }
                url.append(hostname);
                int port = Property.getPropertyInt(new ProfilePropertyKey(propertyProfile, user.getPrincipalName(), "clientProxy.port", user.getRealm().getResourceId()));
                if (port != 0) {
                    url.append(":");
                    url.append(port);
                }
                url.append("?");
                url.append(auth);
                return url.toString();
            }
        } else if (type.equals("browser")) {
            String auth = Property.getProperty(new ProfilePropertyKey(propertyProfile, user.getPrincipalName(), "clientProxy.preferredAuthentication", user.getRealm().getResourceId()));
            return "browser://" + auth;
        }
        return null;
    }

    /**
     * @param action
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public static void checkNavigationContext(CoreAction action, ActionMapping mapping, ActionForm form,
                                              HttpServletRequest request, HttpServletResponse response) throws Exception {
        int navigationContext = action.getNavigationContext(mapping, form, request, response);
        if (!ContextHolder.getContext().isSetupMode()) {
            SessionInfo info = LogonControllerFactory.getInstance().getSessionInfo(request);
            if ((navigationContext & info.getNavigationContext()) == 0) {
                if ((navigationContext & SessionInfo.MANAGEMENT_CONSOLE_CONTEXT) != 0) {                	
                	if(!PolicyDatabaseFactory.getInstance().isAnyAccessRightAllowed(info.getUser(), true, true, false)) {
                        throw new NoPermissionException("You do not have permission to use the management console.");                		
                	}
                    info.setNavigationContext(SessionInfo.MANAGEMENT_CONSOLE_CONTEXT);
                    CoreUtil.resetMainNavigation(request.getSession());
                } else if ((navigationContext & SessionInfo.USER_CONSOLE_CONTEXT) != 0) {
                    info.setNavigationContext(SessionInfo.USER_CONSOLE_CONTEXT);
                    CoreUtil.resetMainNavigation(request.getSession());
                } else if ((navigationContext & SessionInfo.HELP_CONTEXT) != 0) {
                    // do nothing
                } else {
                    throw new NoPermissionException("Action does not define any valid navigation contexts that it should be available in.");
                }
            }
        }
    }

    /**
     * @param parameterList
     * @return Properties
     * @throws ParseException
     */
    public static Properties parseActionParameter(String parameterList) throws ParseException {
        Properties p = new Properties();
        String[] properties = parameterList.split(",");
        for (int i = 0; i < properties.length; i++) {
            String n = properties[i];
            int idx = n.indexOf('=');
            if (idx == -1) {
                throw new ParseException("Parameter list in incorrect format. [<name>=<value>[,<name>=<value>] ..]", 0);
            }
            String v = n.substring(idx + 1);
            n = n.substring(0, idx);
            p.setProperty(n, v);
        }
        return p;
    }

    /**
     * @param serverInterfaceItem
     * @return List<CoreSelectableItem>
     */
    public static List<CoreSelectableItem> getSelectedItems(List serverInterfaceItem) {
        List<CoreSelectableItem> l = new ArrayList<CoreSelectableItem>();
        for (Iterator i = serverInterfaceItem.iterator(); i.hasNext();) {
            CoreSelectableItem item = (CoreSelectableItem) i.next();
            if (item.getSelected()) {
                l.add(item);
            }
        }
        return l;
    }

    /**
     * @param serverInterfaceItem
     * @return List<CoreSelectableItem>
     */
    public static List<CoreSelectableItem> getDeselectedItems(List serverInterfaceItem) {
        List<CoreSelectableItem> l = new ArrayList<CoreSelectableItem>();
        for (Iterator i = serverInterfaceItem.iterator(); i.hasNext();) {
            CoreSelectableItem item = (CoreSelectableItem) i.next();
            if (!item.getSelected()) {
                l.add(item);
            }
        }
        return l;
    }

    /**
     * @param items
     */
    public static void deselectAllItems(List items) {
        for (Iterator i = items.iterator(); i.hasNext();) {
            CoreSelectableItem item = (CoreSelectableItem) i.next();
            item.setSelected(false);
        }
    }

    /**
     * @param items
     */
    public static void selectAllItems(List items) {
        for (Iterator i = items.iterator(); i.hasNext();) {
            CoreSelectableItem item = (CoreSelectableItem) i.next();
            item.setSelected(false);
        }
    }

    /**
     * Check whether the session associated in the middle of wizard.
     * 
     * @param session session
     * @return in a wizard
     */
    public static boolean isInWizard(HttpSession session) {
        return session.getAttribute(Constants.WIZARD_SEQUENCE) != null;
    }

    /**
     * Reset the main navigation menu so it gets rebuilt upon the next request
     * 
     * @param session
     * 
     */
    public static void resetMainNavigation(HttpSession session) {
        session.removeAttribute(Constants.MENU_TREE);
        session.removeAttribute(Constants.NAV_BAR);
    }

    /**
     * Get if the menu is currently available. This is often used to determine
     * if the user is on a page that cannot be safely navigated away from.
     * 
     * @param request
     * 
     * @return menu is available
     */
    public static boolean isMenuAvailable(HttpServletRequest request) {
        return request.getAttribute(Constants.SELECTED_MENU) != null && request.getSession()
                        .getAttribute(Constants.PAGE_INTERCEPTED) == null;
    }

    /**
     * @param action
     * @param request
     */
    public static void addRequiredFieldMessage(Action action, HttpServletRequest request) {
        ActionMessages mesgs = (ActionMessages) request.getAttribute(Globals.MESSAGE_KEY);
        if (mesgs == null) {
            mesgs = new ActionMessages();
            request.setAttribute(Globals.MESSAGE_KEY, mesgs);
        }
        mesgs.add(Globals.MESSAGE_KEY, new BundleActionMessage("navigation",
                        "info.requiredFieldIndicator",
                        "<img src=\"" + getThemePath(request.getSession()) + "/images/required.gif" + "\" border=\"0\"/>"));
    }

    /**
     * @param originalPath
     * @return String
     */
    public static String platformPath(String originalPath) {
        String p = originalPath.replace("/", File.separator).replace("\\", File.separator);
        if (log.isDebugEnabled())
            log.debug("Original path of '" + originalPath + "' is '" + p + "' for platform");
        return p;
    }

    /**
     * @param f
     * @return long
     * @throws IOException
     */
    public static long generateChecksum(File f) throws IOException {
        Adler32 alder = new Adler32();
        FileInputStream fin = new FileInputStream(f);
        CheckedInputStream in = new CheckedInputStream(fin, alder);
        byte[] buf = new byte[32768];
        Util.readFullyIntoBuffer(in, buf);
        alder = (Adler32) in.getChecksum();
        try {
            in.close();
        } catch (IOException ex) {
        }
        try {
            fin.close();
        } catch (IOException ex1) {
        }
        return alder.getValue();
    }

    /**
     * Dump tile attributes to {@link System#err}.
     * 
     * @param pageContext page context from which to get tile.
     */
    public static void dumpTileScope(PageContext pageContext) {
        ComponentContext compContext = (ComponentContext) pageContext.getAttribute(ComponentConstants.COMPONENT_CONTEXT,
            PageContext.REQUEST_SCOPE);
        System.err.println("Tile attributes");
        for (Iterator i = compContext.getAttributeNames(); i.hasNext();) {
            String n = (String) i.next();
            System.err.println("   " + n + " = " + compContext.getAttribute(n));
        }
    }

    /**
     * Get message resources given the ID and the session. <code>null</code>
     * will be returned if no such resources exist.
     * 
     * @param session session
     * @param key bundle key
     * @return resources
     */
    public static MessageResources getMessageResources(HttpSession session, String key) {
        return getMessageResources(session.getServletContext(), key);
    }
    
    /**
     * Get message resources given the ID and the session. <code>null</code>
     * will be returned if no such resources exist.
     * 
     * @param context session
     * @param key bundle key
     * @return resources
     */
    public static MessageResources getMessageResources(ServletContext context, String key) {
        ModuleConfig moduleConfig = ModuleUtils.getInstance().getModuleConfig("", context);
        return (MessageResources) context.getAttribute(key + moduleConfig.getPrefix());
    }
    
    public static String getMessage(HttpSession session, String bundle, String key) {
    	MessageResources resources = getMessageResources(session, bundle);
    	Locale locale = (Locale)session.getAttribute(Globals.LOCALE_KEY);
    	if (null != resources) {
    	    return resources.getMessage(locale, key);
    	} else {
    	    return null;
    	}
    }
    
    public static String getMessage(SessionInfo session, String bundle, String key) {
    	return getMessage(session.getHttpSession(), bundle, key);
    }
    
    public static String getMessage(HttpServletRequest request, String bundle, String key) {
    	return getMessage(request.getSession(), bundle, key);
    }

    /**
     * Adds a new path to the paths search for native libraries. Because
     * <i>java.library.path</i> cannot be changed at runtime. This method is a
     * workaround that directly changes a private variables in the Sun classes,
     * so will probably not work on other JVMs.
     * 
     * @param path path to add
     * @throws IOException ioe
     */
    public static void addLibraryPath(String path) throws IOException {
        try {
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = (String[]) field.get(null);
            for (int i = 0; i < paths.length; i++) {
                if (path.equals(paths[i])) {
                    return;
                }
            }
            String[] tmp = new String[paths.length + 1];
            System.arraycopy(paths, 0, tmp, 0, paths.length);
            tmp[paths.length] = path;
            field.set(null, tmp);
        } catch (IllegalAccessException e) {
            throw new IOException("Failed to get permissions to set library path");
        } catch (NoSuchFieldException e) {
            /*
             * This will likely happen if not Suns JVM. Just in case it does
             * work, we'll set java.library.path
             */
            System.setProperty("java.library.path", SystemProperties.get("java.library.path") + File.pathSeparator + path);
            log.warn("Failed to set library path using Sun JDK workaround. Just setting java.library.path in case " + "it works. If it doesn't, plugins that use native libraries will probably fail. To fix "
                + "this you will have to alter "
                + ContextHolder.getContext().getConfDirectory().getAbsolutePath()
                + File.separator
                + "wrapper.conf to include the additional library path '"
                + path
                + "'.");
        }
    }

    /**
     * Replace all occurences of string <i>token</i> in <i>source</i> with
     * <i>value</i>.
     * 
     * @param source source to search for occurences of <i>token</i>
     * @param token string to search for
     * @param value value to replace occurences of <i>token</i> with
     * @return processed string
     */
    public static String replaceAllTokens(String source, String token, String value) {
        return StringUtils.replace(source, token, value);
    }

    /**
     * Add a new upload to the sessions upload manager, creating one if needed.
     * 
     * @param session session
     * @param upload upload
     * @return id;
     */
    public static int addUpload(HttpSession session, UploadDetails upload) {
        synchronized (session) {
            UploadManager mgr = (UploadManager) session.getAttribute(Constants.UPLOAD_MANAGER);
            if (mgr == null) {
                mgr = new UploadManager();
                session.setAttribute(Constants.UPLOAD_MANAGER, mgr);
            }
            return mgr.addUpload(upload);
        }
    }

    /**
     * Remove an upload given its id, removing the upload manager if it is
     * empty.
     * 
     * @param session session
     * @param uploadId upload id
     * @return removed upload details
     */
    public static UploadDetails removeUpload(HttpSession session, int uploadId) {
        UploadManager mgr = (UploadManager) session.getAttribute(Constants.UPLOAD_MANAGER);
        if (mgr != null) {
            UploadDetails details = mgr.removeUpload(uploadId);
            if (mgr.isEmpty()) {
                session.removeAttribute(Constants.UPLOAD_MANAGER);
            }
            return details;
        }
        return null;
    }

    /**
     * Get an upload given its id. <code>null</code> will be returned if no
     * upload exists.
     * 
     * @param session session
     * @param id
     * @return upload
     */
    public static UploadDetails getUpload(HttpSession session, int id) {
        UploadManager mgr = (UploadManager) session.getAttribute(Constants.UPLOAD_MANAGER);
        if (mgr != null) {
            return mgr.getUpload(id);
        }
        return null;
    }

    /**
     * @param clazz
     * @param tag
     * @return Tag
     */
    public static Tag getParentTagOfClass(Class clazz, Tag tag) {
        while ((tag = tag.getParent()) != null) {
            if (tag.getClass().equals(clazz)) {
                return tag;
            }
        }
        return null;
    }

    /**
     * Get if a specific authentication module has been configured for use in
     * any schemes and is enabled
     * 
     * @param moduleName
     * @return module is use
     * @throws Exception if authentication schemes cannot be loaded
     */
    public static boolean isAuthenticationModuleInUse(String moduleName) throws Exception {
        List authenticationSchemes = SystemDatabaseFactory.getInstance().getAuthenticationSchemeSequences();
        for (Iterator i = authenticationSchemes.iterator(); i.hasNext();) {
            AuthenticationScheme seq = (DefaultAuthenticationScheme) i.next();
            if (seq.hasModule(moduleName) && seq.getEnabled()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Store the provided user interface state name / value pair in a cookie
     * 
     * @param name ui state cookie name
     * @param value ui state cookie value
     * @param request request
     * @param response response
     */
    public static void storeUIState(String name, String value, HttpServletRequest request, HttpServletResponse response) {
        Cookie c = getCookie(name, request);
        if (c != null) {
            c.setValue(value);
        } else {
            c = new Cookie(name, value);
        }
        c.setMaxAge(-1);
        response.addCookie(c);
    }

    /**
     * Get the real request URI from within a tile
     * 
     * @param request request
     * @return real request URI
     * 
     */
    public static String getRealRequestURI(HttpServletRequest request) {
        HttpServletRequest tmpRequest = request;
        while (tmpRequest instanceof HttpServletRequestWrapper) {
            tmpRequest = (HttpServletRequest) ((HttpServletRequestWrapper) tmpRequest).getRequest();
        }
        return tmpRequest.getRequestURI();
    }

    /**
     * Get if the request is for a page in the user console.
     * 
     * @param request
     * @return boolean in user console
     */
    public static boolean isInUserConsole(HttpServletRequest request) {
        SessionInfo session = (SessionInfo) request.getSession().getAttribute(Constants.SESSION_INFO);
        return session != null && session.getNavigationContext() == SessionInfo.USER_CONSOLE_CONTEXT;
    }

    /**
     * Get if the request is for a page in the management console.
     * 
     * @param request
     * @return boolean in user console
     */
    public static boolean isInManagementConsole(HttpServletRequest request) {
        SessionInfo session = (SessionInfo) request.getSession().getAttribute(Constants.SESSION_INFO);
        return session != null && session.getNavigationContext() == SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

    /**
     * @param session
     * @param propertyName
     * @param user
     * @return String
     */
    public static String getUsersProfileProperty(HttpSession session, String propertyName, User user) {
        return Property.getProperty(getPropertyProfileKey(session, propertyName, user));
    }

    /**
     * @param session
     * @param propertyName
     * @param user
     * @return boolean
     */
    public static boolean getUsersProfilePropertyBoolean(HttpSession session, String propertyName, User user) {
        return Property.getPropertyBoolean(getPropertyProfileKey(session, propertyName, user));
    }

    /** 
     * @param session
     * @param propertyName
     * @param user
     * @return int
     */
    public static int getUsersProfilePropertyInt(HttpSession session, String propertyName, User user) {
        return Property.getPropertyInt(getPropertyProfileKey(session, propertyName, user));
    }
    
    /** 
     * @param session
     * @param propertyName
     * @param user
     * @return int
     */
    public static int getUsersProfilePropertyIntOrDefault(HttpSession session, String propertyName, User user) {
        try {
            return Property.getPropertyInt(getPropertyProfileKey(session, propertyName, user));
        } catch (Exception expt) {
            PropertyDefinition definition = Property.getDefinition(getPropertyProfileKey(session, propertyName, null));
            return Integer.valueOf(definition.getDefaultValue());
        }
    }

    private static ProfilePropertyKey getPropertyProfileKey(HttpSession session, String propertyName, User user) {
        int currentPropertyProfileId = getCurrentPropertyProfileId(session);
        String username = user == null ? null : user.getPrincipalName();
        UserDatabase defaultUserDatabase = UserDatabaseManager.getInstance().getDefaultUserDatabase();
        int realmId = user == null ? defaultUserDatabase.getRealm().getResourceId() : user.getRealm().getResourceId();
        return new ProfilePropertyKey(currentPropertyProfileId, username, propertyName, realmId);
    }

    /**
     * Utility method that requires the auditing plugin to be available that all
     * of the constants in the provided class as event codes.
     * <p>
     * The bundle containing I18n message resource is also required.
     * <p>
     * Reflection is used to prevent introducing any compile time dependencies
     * on the auditing module.
     * 
     * @param bundle bundle containing message resources
     * @param clazz class containing event codes
     */
    public static void updateEventsTable(String bundle, Class clazz) {
        Plugin auditingPlugin = PluginType.getPlugin("adito-enterprise-auditing");
        if (auditingPlugin == null) {
            if(log.isDebugEnabled())
                log.warn("Could not locate auditing plugin. No events codes can be updated.");
        } else {
            try {
                Method m = auditingPlugin.getClass().getMethod("getDatabase", new Class[] {});
                Database d = (Database) m.invoke(auditingPlugin, new Object[] {});
                m = d.getClass().getMethod("updateEventsTable", new Class[] { String.class, Class.class });
                m.invoke(d, new Object[] { bundle, clazz });
            } catch (Exception e) {
                log.error("Failed to register event codes.", e);
            }
        }
    }

	/**
	 * Clean flow state.
	 * 
	 * @param request request
	 */
	public static void clearFlow(HttpServletRequest request) {        
        request.getSession().removeAttribute(Constants.WIZARD_SEQUENCE);
        request.getSession().removeAttribute(Constants.SUSPENDED_WIZARD_SEQUENCE);
        ResourceStack.popFromEditingStack(request.getSession());
        request.getSession().removeAttribute(Constants.EDITING_ITEM);		
	}
	
	/**
	 * Check if a  URI string is safe, only allowing relative
	 * or URI, absolute URI or HTTPS URI. An exception is 
	 * thrown if the uri contains an 
	 * 
	 *  @param uri uri to check
	 *  @throws IllegalArgumentException if unsafe
	 */
	public static void checkSafeURI(String uri) {
		URI location;
		try {
			location = new URI(uri);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(uri + " is not safe.");
		}
		if( location.getScheme() != null && !"http".equalsIgnoreCase(location.getScheme()) && 
				!"https".equalsIgnoreCase(location.getScheme()) ) {
			throw new IllegalArgumentException(uri + " is not safe.");
		}
	}
	
	/**
	 * Filter a URI string, only allowing relative
	 * or URI, absolute URI or HTTPS URI.
	 * 
	 *  @param uri uri to filter
	 *  @param replacement replacement URI if filter matches
	 *  @return filtered URI
	 */
	public static String filterSafeURI(String uri, String replacement) {
		try {
			checkSafeURI(uri);
		} catch (IllegalArgumentException e) {
			return replacement;
		}
		return uri;
	}


	/**
	 * Get a date formatter appropriate for the logged on users
	 * profile.
	 * 
	 * @param request
	 * @return date formatter
	 */
	public static DateFormat getDateFormat(HttpServletRequest request) {
		return getDateFormat(request, null);
	}

	/**
	 * Get a date formatter appropriate for the logged on users
	 * profile.
	 * 
	 * @param request request
	 * @param timeFormat time format
	 * @return date formatter
	 */
	public static DateFormat getDateFormat(HttpServletRequest request, String timeFormat) {
	    return getDateFormat(LogonControllerFactory.getInstance().getSessionInfo(request), timeFormat);
	}

	/**
	 * Get a date formatter appropriate for the logged on users
	 * profile.
	 * 
	 * @param sessionInfo sessionInfo
	 * @param timeFormat time format
	 * @return date formatter
	 */
	public static DateFormat getDateFormat(SessionInfo sessionInfo, String timeFormat) {
	    return new SimpleDateFormat(Property.getProperty(new ProfilePropertyKey("ui.dateFormat", sessionInfo))
                        + (timeFormat == null ? "" : (" " + timeFormat)));
	}
	
	/**
	 * Turns an Exception stacktrace into a String.
	 * @param t
	 * @return String representation of the stacktrace.
	 */
	public static String toString(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        t.printStackTrace(printWriter);      
        return stringWriter.toString();
    }
}