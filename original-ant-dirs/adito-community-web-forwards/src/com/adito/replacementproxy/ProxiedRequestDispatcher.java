
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.http.HttpAuthenticatorFactory;
import com.maverick.http.HttpClient;
import com.maverick.http.HttpResponse;
import com.maverick.http.PasswordCredentials;
import com.maverick.util.IOUtil;
import com.adito.boot.Branding;
import com.adito.boot.CaseInsensitiveMap;
import com.adito.boot.HttpConstants;
import com.adito.boot.SystemProperties;
import com.adito.boot.Util;
import com.adito.core.CookieItem;
import com.adito.core.CookieMap;
import com.adito.core.MultiMap;
import com.adito.core.RequestParameterMap;
import com.adito.core.RequestParameterMap.ProxyURIDetails;
import com.adito.core.stringreplacement.SessionInfoReplacer;
import com.adito.policyframework.LaunchSession;
import com.adito.reverseproxy.SessionClients;
import com.adito.security.Constants;
import com.adito.util.ProxiedHttpMethod;
import com.adito.webforwards.WebForwardDatabaseFactory;
import com.adito.webforwards.WebForwardTypes;

/**
 */
public class ProxiedRequestDispatcher {
	final static Log log = LogFactory.getLog(ProxiedRequestDispatcher.class);
	static CaseInsensitiveMap ignoreHeaders = new CaseInsensitiveMap();

	final static String sessionIdCookieName = SystemProperties.get("adito.cookie", "JSESSIONID");
	
	/**
	 * Launch session attribute for storing whether authentication has been
	 * posted yet
	 */
	public static final String LAUNCH_ATTR_AUTH_POSTED = "authPosted";

	static {

		ignoreHeaders.put(HttpConstants.HDR_PROXY_CONNECTION, Boolean.TRUE);
		ignoreHeaders.put(HttpConstants.HDR_ACCEPT_ENCODING, Boolean.TRUE);
		ignoreHeaders.put(HttpConstants.HDR_TRANSFER_ENCODING, Boolean.TRUE);
		ignoreHeaders.put(HttpConstants.HDR_TE, Boolean.TRUE);
		ignoreHeaders.put(HttpConstants.HDR_TRAILER, Boolean.TRUE);
		ignoreHeaders.put(HttpConstants.HDR_PROXY_AUTHORIZATION, Boolean.TRUE);
		ignoreHeaders.put(HttpConstants.HDR_PROXY_AUTHENTICATE, Boolean.TRUE);
		ignoreHeaders.put(HttpConstants.HDR_UPGRADE, Boolean.TRUE);

	}

	private RequestProcessor requestProcessor;
	private LaunchSession launchSession;
	private HttpResponse serverResponse;
	private int responseCode;
	private String responseMessage;
	private CookieMap cookieMap;

	public ProxiedRequestDispatcher(RequestProcessor requestProcessor, LaunchSession launchSession, CookieMap cookieMap) {
		this.launchSession = launchSession;
		this.requestProcessor = requestProcessor;
		this.cookieMap = cookieMap;
	}

	/**
	 * Send the request to the target server.
	 * 
	 * @return request successful
	 * @throws Exception on any error
	 */
	public boolean sendProxiedRequest() throws Exception {

		byte[] content = null;
		OutputStream serverOut = null;
		HttpClient client;
		SessionClients clients = null;
		HttpSession session = requestProcessor.getSession();

		// Manage the sessions clients
		synchronized (session) {
			clients = (SessionClients) session.getAttribute(Constants.HTTP_CLIENTS);
			if (clients == null) {
				clients = new SessionClients();
				session.setAttribute(Constants.HTTP_CLIENTS, clients);
			}
		}
		
		RequestParameterMap requestParameters = requestProcessor.getRequestParameters(); 
		URL proxiedURL = requestParameters.getProxiedURIDetails().getProxiedURL();

		synchronized (clients) {
			String key = proxiedURL.getHost() + ":"
				+ (proxiedURL.getPort() > 0 ? proxiedURL.getPort() : proxiedURL.getProtocol().equals("https") ? 443 : 80)
				+ ":"
				+ proxiedURL.getProtocol().equals("https")
				+ ":"
				+ requestProcessor.getWebForward().getResourceId()
				+ Thread.currentThread().getName();
			client = (HttpClient) clients.get(key);

			if (client == null) {
				client = new HttpClient(proxiedURL.getHost(), (proxiedURL.getPort() > 0 ? proxiedURL.getPort()
					: proxiedURL.getProtocol().equals("https") ? 443 : 80), proxiedURL.getProtocol().equals("https"));

				if (!requestProcessor.getWebForward().getPreferredAuthenticationScheme().equals(HttpAuthenticatorFactory.NONE) && !requestProcessor.getWebForward()
								.getAuthenticationUsername()
								.equals("")
					&& !requestProcessor.getWebForward().getAuthenticationPassword().equals("")) {
					PasswordCredentials pwd = new PasswordCredentials();
					pwd.setUsername(SessionInfoReplacer.replace(requestProcessor.getSessionInfo(), requestProcessor.getWebForward()
									.getAuthenticationUsername()));
					pwd.setPassword(SessionInfoReplacer.replace(requestProcessor.getSessionInfo(), requestProcessor.getWebForward()
									.getAuthenticationPassword()));
					client.setCredentials(pwd);
				}

				// Set the preferred scheme
				client.setPreferredAuthentication(requestProcessor.getWebForward().getPreferredAuthenticationScheme());

				// Do not track cookies, browser will instead
				client.setIncludeCookies(false);
				
				// If we're using basic authentication then preempt the 401
				// response
				client.setPreemtiveAuthentication(requestProcessor.getWebForward()
								.getPreferredAuthenticationScheme()
								.equalsIgnoreCase("BASIC"));
				clients.put(key, client);
			}
		}

		if (log.isDebugEnabled())
			log.debug("Connecting to [" + proxiedURL + "] ");

		ProxiedHttpMethod method;

       	if (!requestProcessor.getWebForward().getFormType().equals(WebForwardTypes.FORM_SUBMIT_NONE) &&
       			!requestProcessor.getWebForward().getFormType().equals("") &&
       			!requestProcessor.getWebForward().getFormType().equals(WebForwardTypes.FORM_SUBMIT_JAVASCRIPT) && 
       			!Boolean.TRUE.equals(launchSession.getAttribute(LAUNCH_ATTR_AUTH_POSTED))) {

            /**
             * This code will automatically submit form parameters.
             * 
             * LDP - Use the full URI with parameters as we need to ensure parameters are sent as they are received.
             */
			method = new ProxiedHttpMethod(requestProcessor.getWebForward().getFormType(),
					SessionInfoReplacer.replace(requestProcessor.getSessionInfo(), 
					requestProcessor.getUriEncoded()),
					requestProcessor.getWebForward().getFormType().equals(WebForwardTypes.FORM_SUBMIT_POST)? new MultiMap() : requestParameters,
					requestProcessor.getSessionInfo(),
					requestProcessor.getWebForward().getFormType().equals(WebForwardTypes.FORM_SUBMIT_POST));

			if (requestProcessor.getWebForward().getEncoding() != null && !requestProcessor.getWebForward().getEncoding().equals(WebForwardTypes.DEFAULT_ENCODING))
                method.setCharsetEncoding(requestProcessor.getWebForward().getEncoding());

            StringTokenizer tokens = new StringTokenizer(requestProcessor.getWebForward().getFormParameters(), "\n");
            int idx;
            String param;
            while (tokens.hasMoreTokens()) {
                param = SessionInfoReplacer.replace(requestProcessor.getLaunchSession().getSession(), tokens.nextToken().trim());
                idx = param.indexOf('=');
                if (idx > -1 && idx < param.length()-1) {
                    method.addParameter(param.substring(0, idx), param.substring(idx + 1));
                } else
                    method.addParameter(param, "");
            }
            
            launchSession.setAttribute(LAUNCH_ATTR_AUTH_POSTED, Boolean.TRUE);
        } else {
        	/**
             * LDP - Use the full URI with parameters as we need to ensure parameters are sent as they are received.
        	 */
        	method = new ProxiedHttpMethod(requestProcessor.getMethod(),
					SessionInfoReplacer.replace(requestProcessor.getSessionInfo(), 
				    requestProcessor.getUriEncoded()),
				    requestParameters,
					requestProcessor.getSessionInfo(),
					requestProcessor.getRequest().getContentType()!=null && 
					requestProcessor.getRequest().getContentType().startsWith("application/x-www-form-urlencoded"));

            if (requestProcessor.getWebForward().getEncoding() != null && !requestProcessor.getWebForward().getEncoding().equals(WebForwardTypes.DEFAULT_ENCODING))
                method.setCharsetEncoding(requestProcessor.getWebForward().getEncoding());
        }

		int contentLength = 0;
		String contentType = null;
		
		for (Enumeration e = requestProcessor.getHeaderNames(); e.hasMoreElements();) {

			String hdr = (String) e.nextElement();

			if (ignoreHeaders.containsKey(hdr)) {
				if (log.isDebugEnabled())
					log.debug("Ignoring " + hdr + " = " + requestProcessor.getHeader(hdr));
				continue;
			}

			// See if there any replacements for this header
			List replacements = WebForwardDatabaseFactory.getInstance().getReplacementsForContent(launchSession.getSession().getUser().getPrincipalName(),
				Replacement.REPLACEMENT_TYPE_SENT_HEADER,
				hdr,
				proxiedURL.toExternalForm());

			Enumeration vals = requestProcessor.getHeaders(hdr);
			while (vals.hasMoreElements()) {
				String val = (String) vals.nextElement();

				// Do the replacements
				for (Iterator i = replacements.iterator(); i.hasNext();) {
					Replacement r = (Replacement) i.next();
					val = val.replaceAll(r.getMatchPattern(), r.getReplacePattern());
				}

				if (val != null) {
					if (hdr.equalsIgnoreCase(HttpConstants.HDR_HOST)) {
						if (proxiedURL.getPort() == -1) {
							val = proxiedURL.getHost();
						} else {
							val = proxiedURL.getHost() + ":" + proxiedURL.getPort();
						}
					} else if (hdr.equalsIgnoreCase(HttpConstants.HDR_COOKIE)) {
						// We shouldnt supply our local cookies
						if (log.isDebugEnabled())
							log.debug(" Splitting cookie " + val);
						String[] cookieVals = val.split("\\;");
						StringBuffer newVal = new StringBuffer();
						for (int i = 0; i < cookieVals.length; i++) {
							if (log.isDebugEnabled())
								log.debug("Cookie = " + cookieVals[i]);
							int idx = cookieVals[i].indexOf('=');
							String cn = "";
							String cv = "";
							if(idx==-1) {
								cn = Util.trimBoth(cookieVals[i]);
							} else if(idx < cookieVals[i].length()-1) {
								cn = Util.trimBoth(cookieVals[i].substring(0, idx));
								cv = Util.trimBoth(cookieVals[i].substring(idx + 1));			
							} else {
								cn = Util.trimBoth(cookieVals[i].substring(0, idx));					
							}
							if (cn.equals("webForward") || cn.equals(Constants.LOGON_TICKET)
								|| cn.equals(Constants.DOMAIN_LOGON_TICKET)
								|| (cn.equals(sessionIdCookieName) && cv.equals(requestProcessor.getSession().getId()))) {
								if (log.isDebugEnabled())
									log.debug("  Omiting cookie " + cn + "=" + cv);
							} else {
								// TODO is it ok to store the cookie map in
								// memory?
								CookieItem cookie = cookieMap.getByFakeCookieName(cn);
								if (cookie == null) {
									if (log.isDebugEnabled())
										log.debug("  Cookie " + cn + " unmapped, ignoring");
									// Un-mapped cookie, ignore
								} else {
									if (log.isDebugEnabled())
										log.debug("  Including cookie " + cn + "=" + cv);
									if (newVal.length() > 0) {
										newVal.append("; ");
									}
									newVal.append(cookie.getRealCookieName());
									newVal.append("=");
									newVal.append(Util.urlDecode(cv));
								}
							}
						}
						if (newVal.length() == 0) {
							if (log.isDebugEnabled())
								log.debug("Send no cookies");
							val = null;
						} else {
							val = newVal.toString();
							if (log.isDebugEnabled())
								log.debug("Using cooking val of " + val);
						}
					}
					// Change the refererer
					else if (hdr.equalsIgnoreCase(HttpConstants.HDR_REFERER)) {
						try {
							URL refUrl = new URL(val);
							refUrl.getQuery();
							if (log.isDebugEnabled())
								log.debug("Splitting refererer query string [" + val + "] " + refUrl.getQuery());
							if (refUrl.getFile() != null) {
							    
							    ProxyURIDetails uriDetails = RequestParameterMap.parseProxyPath(refUrl.getFile(), "UTF-8");
							    if(uriDetails.getProxiedURL() == null) {
							        /* If the referer is not a proxied URL then don't send a referer. This
							         * way a target server won't know its a request from Adito by
							         * examining the referer
							         */ 
							        val = null;
							    }
							    else {
							        val = uriDetails.getProxiedURL().toExternalForm();
							    }
							}
						} catch (MalformedURLException murle) {

						}
					} else if (hdr.equalsIgnoreCase(HttpConstants.HDR_CONTENT_LENGTH)) {
						contentLength = Integer.parseInt(val);
						continue;
					} else if(hdr.equalsIgnoreCase(HttpConstants.HDR_CONTENT_TYPE)) {
						contentType = val;
						continue;
					} else if (hdr.equalsIgnoreCase(HttpConstants.HDR_CONNECTION)) {
						// Handled by the Maverick HTTP client
						continue;
					}

					if (val != null) {
						method.getProxiedRequest().addHeaderField(hdr, val);
					}

					if (log.isDebugEnabled())
						log.debug("Adding request property " + hdr + " = " + val);
				}
			}
		}

		// Proxy headers
		method.getProxiedRequest().setHeaderField("Via", Branding.PRODUCT_NAME);
		
		if(requestParameters.isMultipart() && requestParameters.getMultipartDataLength()  > 0) {
		    method.setContent(getDebugStream(requestParameters.getMultipartData()), requestParameters.getMultipartDataLength(), requestParameters.getOriginalContentType());
        }
        else if(!requestParameters.isWwwFormURLEncoded() && contentLength > 0) {
            method.setContent(getDebugStream(requestProcessor.getRequest().getInputStream()), requestParameters.getOriginalContentLength(), requestParameters.getOriginalContentType());
        }

		serverResponse = client.execute(method);

		responseCode = serverResponse.getStatus();
		responseMessage = serverResponse.getReason();

		return true;
	}
	
	InputStream getDebugStream(InputStream in) throws IOException {
	    if(log.isDebugEnabled()) {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        IOUtil.copy(in, baos);
	        byte[] buf = baos.toByteArray();
	        log.debug("Sending content :-\n" + new String(buf));
	        return new ByteArrayInputStream(buf);
	    }
	    return in;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public HttpResponse getServerResponse() {
		return serverResponse;
	}

	public String getResponseMessage() {
		return responseMessage;
	}
}
