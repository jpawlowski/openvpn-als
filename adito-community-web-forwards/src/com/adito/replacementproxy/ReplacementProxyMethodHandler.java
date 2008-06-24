
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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.ContextHolder;
import com.adito.boot.RequestHandlerException;
import com.adito.boot.RequestHandlerRequest;
import com.adito.boot.RequestHandlerResponse;
import com.adito.boot.SystemProperties;
import com.adito.core.CookieMap;
import com.adito.core.CoreUtil;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.LaunchSessionFactory;
import com.adito.properties.Property;
import com.adito.properties.impl.systemconfig.SystemConfigKey;
import com.adito.security.Constants;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.security.User;
import com.adito.webforwards.AbstractAuthenticatingWebForwardHandler;
import com.adito.webforwards.WebForwardPlugin;

/**
 * Request handler that deals with both <i>Replacement Proxy</i> web forwards.
 */
public class ReplacementProxyMethodHandler extends AbstractAuthenticatingWebForwardHandler {

	final static String sessionCookie = SystemProperties.get("adito.cookie", "JSESSIONID");

	static HashSet<String> ignoredHeaders = new HashSet<String>();

	static {
		ignoredHeaders.add("Location".toUpperCase());
		ignoredHeaders.add("Server".toUpperCase());
		ignoredHeaders.add("Date".toUpperCase());
	}

	static Log log = LogFactory.getLog(ReplacementProxyMethodHandler.class);

	public boolean handle(String pathInContext, String pathParams, RequestHandlerRequest request, RequestHandlerResponse response)
					throws RequestHandlerException, IOException {
		if (log.isDebugEnabled())
			log.debug("Checking for Replacement proxy request: " + pathInContext);
		
		LaunchSession launchSession = null;
		
		String launchId;

		try {
			String requestPath = request.getPath();
			if (requestPath.startsWith("/replacementProxyEngine")) {


				/* The launch session and URL may be provided in one of two ways.
				 * 
				 * 1. As a request to /replacementProxyEngine with the launch session
				 *    and target URL provided as sslx_launchId and sslx_url respectively
				 *    
				 * 2. In the new format /replacementProxyEngine/[launchId]/[encodedURL]
				 */
				
				if(requestPath.startsWith("/replacementProxyEngine/")) {
					int idx = requestPath.indexOf('/', 1);
					int idx2 = requestPath.indexOf('/', idx + 1);
					launchId = requestPath.substring(idx + 1, idx2);
				}
				else {
					launchId = (String) request.getParameters().get(LaunchSession.LONG_LAUNCH_ID);
				}
				
				if (launchId != null) {
					LaunchSession foundLaunchSession = LaunchSessionFactory.getInstance().getLaunchSession(launchId);
					if (foundLaunchSession == null) {
						response.sendError(404, "Invalid launch session. Your login session has probobably timed out.");
						return true;
					}
					if (foundLaunchSession.isTracked() && foundLaunchSession.getResource()
									.getResourceType()
									.equals(WebForwardPlugin.WEBFORWARD_RESOURCE_TYPE)) {
						launchSession = foundLaunchSession;
						if (log.isDebugEnabled()) {
							log.debug("Found a web forward launch session provided by " + LaunchSession.LONG_LAUNCH_ID
								+ " parameter in request.");
						}
						SessionInfo session = locateSession(request, response);
						if (session == null) {
							throw new Exception("Session could not be located.");
						}
                        LogonControllerFactory.getInstance().addCookies(request, response, session.getLogonTicket(), session);
						launchSession.checkAccessRights(null, session);
						return handleReplacementProxy(request, response, launchSession);
					}
				}
			}
		} catch (Exception e) {
			log.error("Failed to process web forward.", e);
			if (launchSession != null) {
				launchSession.getSession().getHttpSession().setAttribute(Constants.EXCEPTION, e);
				try {
				    response.sendRedirect("/showPopupException.do");
				}
				catch(IllegalStateException ise) {
				    // Response may have already been commited
				}
			} else {
				throw new RequestHandlerException("Failed to process web forward.", 500);
			}
			return true;
		}
		return false;
	}

	private boolean handleReplacementProxy(RequestHandlerRequest request, RequestHandlerResponse response,
											LaunchSession launchSession) throws Exception {

		int maxAge = Property.getPropertyInt(new SystemConfigKey("webForwards.cache.maxUserAge")) * 1000 * 60;
		User user = launchSession.getSession().getUser();
		ContentCache cache = getCache(launchSession.getSession(), user);
		CookieMap cookieMap = getCookieMap(launchSession);

		final RequestProcessor requestProcessor = new RequestProcessor(cache, maxAge, request, launchSession);

		try {

			// Process the request
			requestProcessor.processRequest();

			// Get the page from cache if the request processor determined it
			// could be
			if (requestProcessor.isGetFromCache()) {
				if (log.isDebugEnabled())
					log.debug("Found page in cache");
				CacheingOutputStream cos = (CacheingOutputStream) cache.retrieve(requestProcessor.getRequestParameters()
								.getProxiedURIDetails().getProxiedURL());
				respondFromCache(cos, request, response);
				return true;
			}

			// Send the request to the proxied host
			ProxiedRequestDispatcher requestDispatcher = new ProxiedRequestDispatcher(requestProcessor, launchSession, cookieMap);

			if (!requestDispatcher.sendProxiedRequest()) {
				response.sendError(requestDispatcher.getResponseCode(), requestDispatcher.getResponseMessage());
				return true;
			}

			// Process the proxied hosts response =
			ProxiedResponseProcessor proxiedResponseProcessor = new ProxiedResponseProcessor(requestProcessor,
							requestDispatcher,
							maxAge,
							cache,
							cookieMap);
			proxiedResponseProcessor.processResponse();

			// Send the response
			ProxiedResponseDispatcher responseDispatcher = new ProxiedResponseDispatcher(this, requestProcessor,
							proxiedResponseProcessor,
							response,
							launchSession,
							cache);

			responseDispatcher.sendResponse();
		} catch (Throwable t) {
			log.error("Serious error proxying request.", t);
			if (t instanceof Exception) {
				throw (Exception) t;
			}
			throw new Exception("Internal error.", t);
		}
		return true;
	}

	/**
	 * Get the cookie for the session.
	 * 
	 * @param session session
	 * @return cookie map
	 */
	public CookieMap getCookieMap(LaunchSession launchSession) {
		if (log.isDebugEnabled())
			log.debug("Getting cookie map for " + launchSession.getId() + " (" + launchSession.hashCode() + ")");
		CookieMap cookieMap = (CookieMap) launchSession.getAttribute(Constants.ATTR_COOKIE_MAP);
		if (cookieMap == null) {
			if (log.isDebugEnabled())
				log.debug("Creating new cookie map");
			cookieMap = new CookieMap();
			launchSession.setAttribute(Constants.ATTR_COOKIE_MAP, cookieMap);
		}
		return cookieMap;

	}

	private ContentCache getCache(SessionInfo session, User user) throws Exception {

		ContentCache cache = (ContentCache) session.getHttpSession().getAttribute(Constants.ATTR_CACHE);
		if (cache == null) {
			int maxObjs = Property.getPropertyInt(new SystemConfigKey("webForwards.cache.maxUserObjects"));
			if (maxObjs != 0) {
				String dir = CoreUtil.replaceAllTokens(Property.getProperty(new SystemConfigKey("webForwards.cache.directory")),
					"%TMP%",
					ContextHolder.getContext().getTempDirectory().getAbsolutePath());
				File cacheDir = new File(dir);
				if (!cacheDir.exists()) {
					if (!cacheDir.mkdirs()) {
						throw new Exception("Could not create cache directory " + cacheDir.getAbsolutePath() + ".");
					}
				}
				cache = new ContentCache(user,
								cacheDir,
								Property.getPropertyInt(new SystemConfigKey("webForwards.cache.maxUserSize")),
								maxObjs);
				session.getHttpSession().setAttribute(Constants.ATTR_CACHE, cache);
			}
		}
		return cache;
	}

	/**
	 * @param cos
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void respondFromCache(CacheingOutputStream cos, RequestHandlerRequest request, RequestHandlerResponse response)
					throws IOException {
		for (Iterator i = cos.getHeaders().iterator(); i.hasNext();) {
			Header h = (Header) i.next();
			response.setField(h.getName(), h.getVal());
		}
		response.setField("Content-Type", cos.getContentType());
		OutputStream out = response.getOutputStream();
		byte[] buf = cos.getBytes();
		response.setContentLength(buf.length);
		out.write(buf);
		out.flush();

	}

}
