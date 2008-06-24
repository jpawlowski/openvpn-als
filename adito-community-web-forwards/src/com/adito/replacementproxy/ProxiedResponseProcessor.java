
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

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.http.HttpResponse;
import com.adito.boot.CaseInsensitiveMap;
import com.adito.boot.HttpConstants;
import com.adito.boot.Util;
import com.adito.core.CookieItem;
import com.adito.core.CookieMap;
import com.adito.policyframework.LaunchSession;
import com.adito.util.Utils;

/**
 */
public class ProxiedResponseProcessor {
	final static Log log = LogFactory.getLog(ProxiedResponseProcessor.class);

	private ProxiedRequestDispatcher requestDispatcher;
	private int maxAge;
	private ContentCache cache;
	private SimpleDateFormat sdf;
	private RequestProcessor requestProcessor;
	// private boolean keepAlive;
	private Date expiryDate;
	private CookieMap cookieMap;
	private String contentType;
	private int contentLength;
	private List headers;
	private boolean cacheable;
	private InputStream serverIn;
	private String charset;
	static CaseInsensitiveMap ignoreHeaders = new CaseInsensitiveMap();

	static {
		ignoreHeaders.put(HttpConstants.HDR_PROXY_CONNECTION, Boolean.TRUE);
		ignoreHeaders.put(HttpConstants.HDR_ACCEPT_ENCODING, Boolean.TRUE);
		ignoreHeaders.put(HttpConstants.HDR_TRANSFER_ENCODING, Boolean.TRUE);
		ignoreHeaders.put(HttpConstants.HDR_TE, Boolean.TRUE);
		ignoreHeaders.put(HttpConstants.HDR_TRAILER, Boolean.TRUE);
		ignoreHeaders.put(HttpConstants.HDR_PROXY_AUTHORIZATION, Boolean.TRUE);
		ignoreHeaders.put(HttpConstants.HDR_PROXY_AUTHENTICATE, Boolean.TRUE);
		ignoreHeaders.put(HttpConstants.HDR_UPGRADE, Boolean.TRUE);
		ignoreHeaders.put(HttpConstants.HDR_CONTENT_ENCODING, Boolean.TRUE);
	}

	/**
	 * 
	 */
	public ProxiedResponseProcessor(RequestProcessor requestProcessor, ProxiedRequestDispatcher requestDispatcher, int maxAge,
									ContentCache cache, CookieMap cookieMap) {
		this.requestDispatcher = requestDispatcher;
		this.requestProcessor = requestProcessor;
		this.maxAge = maxAge;
		this.cache = cache;
		this.cookieMap = cookieMap;
		// keepAlive = requestDispatcher.isKeepAlive();
		sdf = new SimpleDateFormat("EEEE, dd-MMM-yy HH:mm:ss zzz");
	}

	public ProxiedRequestDispatcher getRequestDispatcher() {
		return requestDispatcher;
	}

	public void processResponse() throws Exception {

		HttpResponse serverResponse = requestDispatcher.getServerResponse();
		headers = new ArrayList();

		// Determine if the response can be cached, and if so, for how long
		expiryDate = maxAge == 0 ? null : new Date(System.currentTimeMillis() + maxAge);
		cacheable = false;
		if (cache != null && HttpConstants.METHOD_GET.equals(requestProcessor.getRequestMethod())
			&& requestDispatcher.getResponseCode() == HttpConstants.RESP_200_OK) {
			cacheable = true;

			// HTTP 1.0
			String cacheControl = serverResponse.getHeaderField(HttpConstants.HDR_PRAGMA);
			if (cacheControl != null && cacheControl.equalsIgnoreCase("no-cache")) {
				if (log.isDebugEnabled())
					log.debug("Not caching as server explicitly requested not to.");
				cacheable = false;
			} else {
				String expires = serverResponse.getHeaderField(HttpConstants.HDR_EXPIRES);
				if (expires != null) {
					try {
						expiryDate = sdf.parse(expires);
					} catch (Exception e2) {
					}
				}
			}

			// HTTP 1.1
			if (cacheable) {
				cacheControl = serverResponse.getHeaderField(HttpConstants.HDR_CACHE_CONTROL);
				if (cacheControl != null) {
					StringTokenizer tok = new StringTokenizer(cacheControl, ";");
					while (tok.hasMoreTokens()) {
						String t = tok.nextToken().trim();
						String tl = t.toLowerCase();
						if (t.startsWith("no-cache") || t.startsWith("no-store")) {
							cacheable = false;
							if (log.isDebugEnabled())
								log.debug("Not caching as server explicitly requested not to.");
						} else if (tl.startsWith("max-age")) {
							try {
								expiryDate.setTime(expiryDate.getTime() - (Integer.parseInt(Util.valueOfNameValuePair(tl))));
							} catch (Exception e2) {
							}
						}
					}
				}
			}
		}

		String contentEncoding = serverResponse.getHeaderField(HttpConstants.HDR_CONTENT_ENCODING);
		serverIn = serverResponse.getInputStream();

		if ("gzip".equals(contentEncoding)) {
			serverIn = new GZIPInputStream(serverIn);
		} else if ("identity".equals(contentEncoding) || contentEncoding == null) {
			// Plain
		} else {
			throw new Exception("Invalid content encoding " + serverResponse.getHeaderField(HttpConstants.HDR_CONTENT_ENCODING));
		}

		String[] challenges = serverResponse.getHeaderFields("www-authenticate");

		if (challenges != null) {
			serverResponse.removeFields("www-authenticate");

			for (int i = 0; i < challenges.length; i++) {
				if (challenges[i].toLowerCase().startsWith("basic") || challenges[i].toLowerCase().startsWith("digest")
					|| challenges[i].toLowerCase().startsWith("ntlm")) {
					if(i==0)
						serverResponse.setHeaderField("WWW-Authenticate", challenges[i]);
					else
						serverResponse.addHeaderField("WWW-Authenticate", challenges[i]);
				}
			}
		}

		// response.setStatus(serverResponse.getStatus());
		// response.setReason(serverResponse.getReason());

		serverResponse.removeFields("Server");
		serverResponse.removeFields("Date");

		for (Enumeration e = serverResponse.getHeaderFieldNames(); e.hasMoreElements();) {
			String hdr = (String) e.nextElement();
			if (log.isDebugEnabled())
				log.debug("Received header " + hdr);
			String[] val = serverResponse.getHeaderFields(hdr);

			for (int i = 0; i < val.length; i++) {

				if (hdr.equalsIgnoreCase("Content-Type")) {

					StringTokenizer tok = new StringTokenizer(val[i], ";");
					while (tok.hasMoreTokens()) {
						String t = tok.nextToken().trim();
						String tl = t.toLowerCase();
						if (tl.startsWith("charset=")) {
							charset = Util.valueOfNameValuePair(t);
						} else {
							contentType = Util.valueOfNameValuePair(t);
						}
					}

					contentType = val[i];
					if (log.isDebugEnabled())
						log.debug("Received content type " + contentType + " (charset = " + charset + ")");
				} else if (hdr.equalsIgnoreCase("Content-Length")) {
					try {
						contentLength = Integer.parseInt(val[i]);
					} catch (Exception ex) {

					}
					if (log.isDebugEnabled())
						log.debug("Received content length " + contentLength);
				} else {
					if (hdr.equalsIgnoreCase("Location")) {
						
						if(log.isDebugEnabled())
							log.debug("Processing Location header value '" + val[i] + "'");
						
						URL actual;
						try {
							actual = new URL(Util.urlDecode(val[i]));
						} catch(MalformedURLException ex) {
							actual = new URL(requestProcessor.getRequestParameters().getProxiedURIDetails().getProxiedURLBase(), Util.urlDecode(val[i]));
						}

						if(cache != null) {
						    cache.clear(actual.toExternalForm());
						}
						

	                    URL newVal = new URL(requestProcessor.getRequestBaseURL(), 
	                        "/replacementProxyEngine/" + requestProcessor.getLaunchId() + 
	                        "/" + Util.urlEncode(Utils.htmlunescape(
	                            actual.toExternalForm())));
						
						
//						URL newVal = new URL(requestProcessor.getRequestBaseURL(), "/replacementProxyEngine?" + LaunchSession.LONG_LAUNCH_ID
//							+ "="
//							+ requestProcessor.getLaunchId()
//							+ "&sslex_url="
//							+ Util.urlEncode(actual.toExternalForm()));
	                    
						if (log.isDebugEnabled())
							log.debug("Found location of header " + val[i]
								+ " changing to "
								+ newVal
								+ " ( removed "
								+ actual.toExternalForm()
								+ " from cache");
						val[i] = newVal.toExternalForm();
					} else if (hdr.equalsIgnoreCase("Set-Cookie")) {
						val[i] = parseCookie(val[i]);
					}
					if (log.isDebugEnabled())
						log.debug("Adding header " + hdr + " = " + val[i]);
					headers.add(new Header(hdr, val[i]));
				}

			}
		}

	}

	public List getHeaders() {
		return headers;
	}

	/**
	 * @return
	 */
	public String getContentType() {
		return contentType;
	}

	public String getCharset() {
		return charset;
	}

	public int getContentLength() {
		return contentLength;
	}

	/**
	 * @return
	 */
	public boolean isCacheable() {
		return cacheable;
	}

	public InputStream getProxiedInputStream() {
		return serverIn;
	}

	/**
	 * @return
	 */
	public Date getCacheExpiryDate() {
		return expiryDate;
	}
	
	String parseCookie(String val) {
		StringBuffer newVal = new StringBuffer();		
		StringTokenizer t = new StringTokenizer(val, ";");
		String elementName = null;
		String elementValue = null;

		try {
			while (t.hasMoreTokens()) {				
				/* Get the name and value of the cookie field element */
				String name = Util.trimBoth(t.nextToken());
				int idx = name.indexOf('=');
				if (idx > -1) {
					elementValue = name.substring(idx + 1);
					elementName = name.substring(0, idx);
				} else {
					elementName = name;
					elementValue = "";
				}
				
				/* Ignore domain and path, fake cookie names and include all other elements */
				
				if (elementName.equalsIgnoreCase("path") || elementName.equalsIgnoreCase("domain")) {
					// Ignore path and domain 
				} else if (elementName.equalsIgnoreCase("expires") ||
								elementName.equalsIgnoreCase("max-age") ||
								elementName.equalsIgnoreCase("secure") ||
								elementName.equalsIgnoreCase("version") ||
								elementName.equalsIgnoreCase("comment") || 
								elementName.equalsIgnoreCase("httponly")) {
					// Include these
					if(newVal.length() > 0) {
						newVal.append("; ");
					}
					newVal.append(elementName);
					if(!elementValue.equals("")) {
						newVal.append("=");
						newVal.append(elementValue);
					}
				} else {
					// Assume to be a cookie name
					String fakeCookieName = Math.abs((requestProcessor.getRequestParameters().getProxiedURIDetails().getProxiedURL().getProtocol() 
							+ ":" 
							+ requestProcessor.getRequestParameters().getProxiedURIDetails().getProxiedURL().getHost()).hashCode()) + "_" + elementName;
					CookieItem cookieItem = new CookieItem(elementName, fakeCookieName);
					cookieMap.put(cookieItem);
					if(newVal.length() > 0) {
						newVal.append("; ");
					}
					newVal.append(fakeCookieName);
					if(!elementValue.equals("")) {
						newVal.append("=");
						newVal.append(Util.urlEncode(elementValue));
					}					
				}
			}
		} catch (Exception ex) {
			log.warn("Invalid cookie.", ex);
		}
		return newVal.toString();
	}
	


}
