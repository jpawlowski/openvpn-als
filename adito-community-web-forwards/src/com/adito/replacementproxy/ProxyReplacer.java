
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.Replacer;
import com.adito.boot.Util;
import com.adito.core.RequestParameterMap.ProxyURIDetails;
import com.adito.util.Utils;

/**
 */
public class ProxyReplacer implements Replacer {

    final static Log log = LogFactory.getLog(ProxyReplacer.class);

    private RequestProcessor requestProcessor;
    private BaseSearch baseSearch;
    private ProxyURIDetails proxyDetails;

    /**
     *
     */
    public ProxyReplacer(RequestProcessor requestProcessor, BaseSearch baseSearch) {
        super();
        this.requestProcessor = requestProcessor;
        this.baseSearch = baseSearch;
        proxyDetails = requestProcessor.getRequestParameters().getProxiedURIDetails();
    }

    public String getProxiedPath(URL context, String path, String base) {
        // TODO leave alone?
        if (path.startsWith("#")) {
            return path;
        }

        // Some types we do not want to proxy at all
        String lc = path.toLowerCase();
        if (lc.startsWith("mailto:") || lc.startsWith("javascript:") || lc.startsWith("ftp:") || lc.startsWith("news")) {
            return path;
        }

        // Strip the reference of the path (either as a URL or a relative path)
        // and
        // store it for
        // later appending to the proxied path
        URL pathURL = null;
        String ref = "";
        try {
            pathURL = new URL(path);
            if (pathURL.getRef() != null) {
                ref = "#" + pathURL.getRef();
                int idx = path.lastIndexOf('#');
                path = path.substring(0, idx);
                pathURL = new URL(path);
            }
        } catch (MalformedURLException murle) {

            int idx = path.lastIndexOf('#');
            if (idx != -1) {
                ref = "#" + path.substring(idx + 1);
                path = path.substring(0, idx);
            }
        }

        // The web forward may restrict access to the target URL
//        if (requestProcessor.getWebForward().getRestrictToURL()) {
//            try {
//                URL webForwardURL = new URL(requestProcessor.getWebForward().getDestinationURL());
//                if (!(InetAddress.getByName(pathURL.getHost()).equals(InetAddress.getByName(webForwardURL.getHost())))) {
//                    throw new Exception("Restricted.");
//                }
//            } catch (Exception e) {
//                return "javascript: void();";
//            }
//
//        }

        /**
         * LDP - We have to unescape any HTML entities because our client will not process them
         * otherwise when a request is received.
         */
        String newPath = null;
        try {
            if (base != null) {
                if (pathURL != null) {
                    //newPath = "/replacementProxyEngine?" + LaunchSession.LONG_LAUNCH_ID + "=" + requestProcessor.getLaunchId() + "&sslex_url=" + Util.urlEncode(Utils.htmlunescape(pathURL.toExternalForm())) + ref;
                	newPath = "/replacementProxyEngine/" + requestProcessor.getLaunchId() + "/" + Util.urlEncode(Utils.htmlunescape(pathURL.toExternalForm())) + ref;
                } else {
                    // Relative so we need to prepend the base
                    if (path.startsWith("./")) {
                        path = path.substring(2);
                    }
                    try {
                        URL baseURL = new URL(base);
                        URL actual = new URL(baseURL, path);
                        //newPath = "/replacementProxyEngine?" + LaunchSession.LONG_LAUNCH_ID + "=" + requestProcessor.getLaunchId() + "&sslex_url=" + Util.urlEncode(Utils.htmlunescape(actual.toExternalForm())) + ref;
                        newPath = "/replacementProxyEngine/" + requestProcessor.getLaunchId() + "/" + Util.urlEncode(Utils.htmlunescape(actual.toExternalForm())) + ref;
                    } catch (MalformedURLException murle) {
                        log.error("Invalidate base URL.", murle);
                    }
                }
            } else {
                URL actual = new URL(context, path);
                //newPath = "/replacementProxyEngine?" + LaunchSession.LONG_LAUNCH_ID + "=" + requestProcessor.getLaunchId() + "&sslex_url=" + Util.urlEncode(Utils.htmlunescape(actual.toExternalForm())) + ref;
                newPath = "/replacementProxyEngine/" + requestProcessor.getLaunchId() + "/" + Util.urlEncode(Utils.htmlunescape(actual.toExternalForm())) + ref;
            }
        } catch (MalformedURLException ex) {
            log.error("Could not convert path from '" + path + "' using " + context.toExternalForm(), ex);
            newPath = path;
        }
        if (newPath == null) {
            log.warn("Failed to proxy path " + path + ", using original.");
            newPath = path;
        }
        if (log.isDebugEnabled())
        	log.debug("Created proxy path " + newPath);
        
        // Unescape the last encoded path
        int idx = newPath.lastIndexOf("%2F");
        newPath = newPath.substring(0, idx) + "/" + newPath.substring(idx + 3);
        
        return newPath;
    }

    public String getReplacement(Pattern pattern, Matcher matcher, String replacementPattern) {
        StringBuffer buf = new StringBuffer();
        char ch;
        boolean esc = false;
        try {
            for (int i = 0; i < replacementPattern.length(); i++) {
                ch = replacementPattern.charAt(i);
                if (esc) {
                    buf.append(ch);
                    esc = false;
                } else {
                    if (ch == '\\') {
                        esc = true;
                    } else if (ch == '%') {
                        i++;
                        ch = replacementPattern.charAt(i);
                        int group = Integer.parseInt(String.valueOf(ch));
                        String groupText = matcher.group(group);
                        if (groupText != null && !groupText.equals("")) {
                            buf.append(getProxiedPath(proxyDetails.getProxiedURLBase(), groupText, baseSearch.getBase()));
                        }
                    } else if (ch == '$') {
                        i++;
                        ch = replacementPattern.charAt(i);
                        int group = Integer.parseInt(String.valueOf(ch));
                        String groupText = matcher.group(group);
                        if (groupText != null && !groupText.equals("")) {
                            buf.append(groupText);
                        }
                    } else if (ch == '^') {
                        i++;
                        ch = replacementPattern.charAt(i);
                        if(ch == 'T') {
                            buf.append(requestProcessor.getLaunchId());
                        }
                        else {
                            int group = Integer.parseInt(String.valueOf(ch));
                            String groupText = matcher.group(group);
                            if (groupText != null && !groupText.equals("")) {
                                buf.append(new URL(proxyDetails.getProxiedURLBase(), groupText).toExternalForm());
                            }
                        }
                    } else if(ch == '~') {
                    	i++;
                        ch = replacementPattern.charAt(i);
                        int group = Integer.parseInt(String.valueOf(ch));
                        String groupText = matcher.group(group);
                        if (groupText != null && !groupText.equals("")) {
                        	groupText = groupText.replaceAll("\\\\", "");
                            buf.append(getProxiedPath(proxyDetails.getProxiedURLBase(), groupText, baseSearch.getBase()));
                        }
                    }else {
                        buf.append(ch);
                    }
                }
            }
            return buf.toString();
        } catch (Throwable t) {
            log.warn("Invalid replacement pattern " + replacementPattern, t);
        }
        return replacementPattern;
    }

}
