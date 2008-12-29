
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

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.tiles.TilesRequestProcessor;

import com.adito.boot.ContextHolder;
import com.adito.boot.HostService;
import com.adito.boot.PropertyList;
import com.adito.boot.SystemProperties;
import com.adito.boot.Util;
import com.adito.navigation.MenuTree;
import com.adito.navigation.NavigationManager;
import com.adito.properties.Property;
import com.adito.properties.impl.systemconfig.SystemConfigKey;
import com.adito.security.Constants;
import com.adito.tasks.TaskHttpServletRequest;

/**
 * Extension of {@link org.apache.struts.tiles.TilesRequestProcessor} that
 * <strong>all</strong> requests to the struts application pass through.
 * <p>
 * Here a map of all active session is maintained and a check is made to see if
 * the navigation menus have been constructed.
 */
public class CoreRequestProcessor extends TilesRequestProcessor {

    final static Map<String, HttpSession> sessions = new HashMap<String, HttpSession>();
    
    /**
     * Get an unmodifiable map of all sessions
     * 
     * @return map of all sessions
     */
    public static Map<String, HttpSession> getSessions() {
        return Collections.unmodifiableMap(sessions);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.RequestProcessor#process(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public void process(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        
        if (session.getAttribute(Constants.SESSION_HOOK) == null) {

            /* This map of sessions is maintained for code that wants access
             * to a list of all currently active HTTP sessions. 
             * 
             * TODO This is probably no longer necessary as the list of 
             * currently active SessionInfo objects should be sufficient
             * for all cases. Please investigate if it can be removed.  
             */ 
            sessions.put(session.getId(), session);
            session.setAttribute(Constants.SESSION_HOOK, new HttpSessionBindingListener() {

                public void valueBound(HttpSessionBindingEvent arg0) {
                }

                public void valueUnbound(HttpSessionBindingEvent arg0) {
                    sessions.remove(arg0.getSession().getId());
                }
            });

            if (ContextHolder.getContext().isSetupMode()) {
                // We should never timeout during setup / installation
                session.setMaxInactiveInterval(Integer.MAX_VALUE);
            } else {
                // Redirect to a valid host if not in setup mode and the feature is in use
                if(!checkForRedirect(request, response)) {
                    return;
                }
            }

            // Set the default locale
            setDefaultLocale(request, session);

            CoreServlet.getServlet().fireCoreEvent(new NewHTTPSessionEvent(this, request, response));
        }

        /** Repopulate any messages from {@link RedirectWithMessages} */
        RedirectWithMessages.repopulate(request);

        try {
            super.process(request, response);
        } catch (ServletException se) {
            /*
             * TODO This hack is so we can redirect to the logon page if the
             * user tries to commit a form after their session has timed out.
             * Find a better way
             */
            if (se.getMessage() != null && se.getMessage().indexOf("BeanUtils.populate") != -1) {
                log.error("User probably commited a form after their session had timed out.", se);
                log.error("Cause.", se.getRootCause());
                request.getSession().getServletContext().getRequestDispatcher("/showHome.do").forward(request, response);
            } else {
                log.error("Error processing request. ", se);
                throw se;
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.struts.tiles.TilesRequestProcessor#doForward(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doForward(String uri, HttpServletRequest request, HttpServletResponse response) throws IOException,
                    ServletException {
        if (request.getAttribute(TaskHttpServletRequest.ATTR_TASK_FORWARD) != null) {
            log.warn("Not forwarding as part of task");
        } else {
            super.doForward(uri, request, response);
        }
    }

	private void setDefaultLocale(HttpServletRequest request, HttpSession session) {
		String defaultLocale = Property.getProperty(new SystemConfigKey("ui.defaultLanguage"));
		try {
            if (Util.isNullOrTrimmedBlank(SystemProperties.get("adito.defaultLanguage", ""))) {
    		    Cookie[] c = request.getCookies();
    		    if (c != null) {
    		        for (int i = 0; i < c.length; i++) {
    		            if (c[i].getName().equals(SystemProperties.get("adito.cookie", "SSLX_SSESHID") + "_LANG")) {
    		                defaultLocale = c[i].getValue();
    		                break;
    		            }
    		        }
    		    }
            } else {
                defaultLocale = SystemProperties.get("adito.defaultLanguage", "");
            }
		    Locale locale;
		    if(Util.isNullOrTrimmedBlank(defaultLocale))
		    	locale = Locale.getDefault();
		    else { 
		    	/* We do our own parsing of the locale string because java Locale
		    	 * changes the case!
		    	 */
				StringTokenizer t = new StringTokenizer(defaultLocale, "_");
				String lang = t.nextToken();
				String country = t.hasMoreTokens() ? t.nextToken() : "";
				String variant = t.hasMoreTokens() ? t.nextToken() : "";
				locale = new Locale(lang, country, variant);
		    }
		    session.setAttribute(Globals.LOCALE_KEY, locale);
		} catch (Exception e) {
		    log.error("Failed to set default locale.", e);
		}
	}
    
    @Override
    protected ActionForward processActionPerform(HttpServletRequest request, HttpServletResponse response, Action action, ActionForm form, ActionMapping mapping) throws IOException, ServletException {
    	    	
        ActionForward actionForward = super.processActionPerform(request, response, action, form, mapping);
        if(actionForward != null && request.getAttribute(TaskHttpServletRequest.ATTR_TASK) != null) {
            request.setAttribute(TaskHttpServletRequest.ATTR_TASK_FORWARD, actionForward);
        }

        // Get any page tasks for this page
        String servletPath = request.getServletPath();
        if (servletPath.startsWith("/") && servletPath.endsWith(".do")) {
            servletPath = servletPath.substring(1, servletPath.length() - 3);
            MenuTree pageTaskMenuTree = NavigationManager.getMenuTree(PageTaskMenuTree.PAGE_TASK_MENU_TREE);
            MenuItem pageTasks = pageTaskMenuTree.getMenuItem(servletPath);
            if (pageTasks != null) {
                request.setAttribute(Constants.PAGE_TASKS, pageTaskMenuTree.rebuildMenus(pageTasks, request));
            } else {
                request.removeAttribute(Constants.PAGE_TASKS);
            }
            MenuTree toolBarMenuTree = NavigationManager.getMenuTree(ToolBarMenuTree.TOOL_BAR_MENU_TREE);
            MenuItem toolBarItems = toolBarMenuTree.getMenuItem(servletPath);
            if (toolBarItems != null) {
                request.setAttribute(Constants.TOOL_BAR_ITEMS, toolBarMenuTree.rebuildMenus(toolBarItems, request));
            } else {
                request.removeAttribute(Constants.TOOL_BAR_ITEMS);
            }
        } else {
            request.removeAttribute(Constants.PAGE_TASKS);
            request.removeAttribute(Constants.TOOL_BAR_ITEMS);
        }
        
        
        processActionMessages(request, response);
        return actionForward;
    }

    @Override
    protected boolean processValidate(HttpServletRequest request, HttpServletResponse response, ActionForm form, ActionMapping mapping) throws IOException, ServletException {
   	
    	
        boolean validated = super.processValidate(request, response, form, mapping);
        if(!validated) {
            processActionMessages(request, response);
        }
        return validated;
    }

    private static void processActionMessages(HttpServletRequest request, HttpServletResponse response) {
        if (isRunningUnitTests()) {
            addHeader(request, response, "unitTestMessages", Globals.MESSAGE_KEY);
            addHeader(request, response, "unitTestErrors", Globals.ERROR_KEY);
        }
    }

    private static void addHeader(HttpServletRequest request, HttpServletResponse response, String headerName, String key) {
        String actionMessages = getActionMessages(request, key);
        if (actionMessages.length() != 0) {
                response.setHeader(headerName, actionMessages);
        }
    }
    
    private static String getActionMessages(HttpServletRequest request, String key) {
        ActionMessages messages = (ActionMessages) request.getAttribute(key);
        if (messages != null) {
            StringBuffer buffer = new StringBuffer();
            for (Iterator itr = messages.get(); itr.hasNext();) {
                ActionMessage next = (ActionMessage) itr.next();
                buffer.append(next.getKey()).append(",");
            }
            return buffer.toString();
        }
        return "";
    }
    
    private static boolean isRunningUnitTests() {
        String isRunningUnitTests = SystemProperties.get("adito.testing", "false");
        return Boolean.valueOf(isRunningUnitTests);
    }

    private static boolean checkForRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = Property.getProperty(new SystemConfigKey("webServer.invalidHostnameAction"));
        PropertyList validExternalHosts = Property.getPropertyList(new SystemConfigKey("webServer.validExternalHostnames"));
        ;
        if (validExternalHosts.size() != 0) {
            Iterator<String> it = validExternalHosts.iterator();
            String host = request.getHeader("Host");
            HostService hostService = host == null ? null : new HostService(host);
            boolean hostOk = false;
            String firstHost = (String) it.next();
            if (hostService != null && !hostService.getHost().equals("")) {
                if (hostService.getHost().startsWith("activeproxy")) {
                    int idx = hostService.getHost().indexOf(".");
                    hostService.setHost(hostService.getHost().substring(idx + 1));
                }
                String thisHost = firstHost;
                do {
                    if (hostService.getHost().equals(thisHost)) {
                        hostOk = true;
                    } else {
                        if (it.hasNext()) {
                            thisHost = it.next();
                        }
                    }
                } while (!hostOk && it.hasNext());
            }
            if (!hostOk) {
                if (action.equals("redirect")) {
                    String path = (request.isSecure() ? "https" : "http") + "://" + firstHost;
                    if (ContextHolder.getContext().getPort() != 443) {
                        path += ":" + ContextHolder.getContext().getPort();
                    }
                    path += Util.getOriginalRequest(request);
                    request.getSession().invalidate();
                    response.sendRedirect(path);
                    return false;
                } else if (action.equals("error")) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return false;
                } else if (action.equals("disconnect")) {
                    response.getOutputStream().close();
                    return false;
                }
            }
        }
        return true;
    }
}