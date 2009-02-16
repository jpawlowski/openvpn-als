
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
			

/*
 * Parts of the code that deal with multipart/form-data parsing are (loosely)
 * based on Jettys MultipartRequest. See license below.
 *
 * 24/02/2005 - brett@localhost
 *
 */

//========================================================================
//$Id$
//Copyright 1996-2004 Mort Bay Consulting Pty. Ltd.
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================
package com.adito.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.ContextHolder;
import com.adito.boot.HttpConstants;
import com.adito.boot.RequestHandlerRequest;
import com.adito.boot.SystemProperties;
import com.adito.boot.Util;
import com.adito.core.stringreplacement.SessionInfoReplacer;
import com.adito.policyframework.LaunchSession;
import com.adito.security.SessionInfo;

/**
 * 
 * Extracts request parameters given a {@link RequestHandlerRequest}.
 * <p>
 * If the request has a <i>Content type</i> of <b>multipart/form-data</b>,
 * then parameters are read from the input stream. Requests of this type will
 * return <code>true</code> for {@link #isFormData()}.
 * <p>
 * If the request does not have a content type of <b>multipart/form-data</b>,
 * then the request parameters are read directly from the
 * {@link RequestHandlerRequest#getParameters()} method. Requests of this type
 * will return <code>false</code> for {@link #isFormData()}.
 * <p>
 * <b>IMPORTANT</b>. Names and values stored in this map must <b>NOT</b> be
 * encoded in any way. Names and values should be encoded as and when needed.
 * <p>
 * The <i>Proxied URL</i> (i.e. the actual URL to load) will also be extracted
 * from the request and can be retrieved using {@link #getProxiedURL()}.
 */
public class RequestParameterMap extends MultiMap {

    final static Log log = LogFactory.getLog(RequestParameterMap.class);

    /**
     * Get the encoding name for ISO_8859_1. This may be configured using the
     * system property <code>ISO_8859_1</code>.
     */
    public final static String ISO_8859_1;
    static {
        String iso = SystemProperties.get("ISO_8859_1");
        if (iso != null)
            ISO_8859_1 = iso;
        else {
            try {
                new String(new byte[] { (byte) 20 }, "ISO-8859-1");
                iso = "ISO-8859-1";
            } catch (java.io.UnsupportedEncodingException e) {
                iso = "ISO8859_1";
            }
            ISO_8859_1 = iso;
        }
    }

    private static final long serialVersionUID = -2005125971106236972L;
    private static long tempIdx = 1;

    // Private instance variables

    private LineInput input;
    private String boundary;
    private byte[] boundaryBytes;
    private boolean finished;
    private File multipartFile;
    private int ch;
    private boolean wwwwFormURLEncoded;
    private boolean multipart;
    private String urlCharacterEncoding;
    private ProxyURIDetails proxyURIDetails;

    private int contentLength;

    private String contentType;

    /**
     * Constructor.
     */
    public RequestParameterMap() {
        super();
        ch = -2;
    }

    /**
     * Constructor.
     * 
     * @param request request to extract parameters from
     * @throws IOException
     */
    public RequestParameterMap(RequestHandlerRequest request) throws IOException {
        this(request, null);
    }

    /**
     * Constructor.
     * 
     * @param request request to extract parameters from
     * @param urlCharacterEncoding encoding to use for URL encoding. Defaults to
     *        system property <code>adito.urlencoding</code> which
     *        itself defaults to UTF-8
     * @throws IOException
     */
    public RequestParameterMap(RequestHandlerRequest request, String urlCharacterEncoding) throws IOException {
        this();

        // Determine what type of request this is
        contentType = request.getContentType();
        contentLength = request.getContentLength();
        multipart = contentType != null && contentType.toLowerCase().startsWith("multipart/form-data");
        wwwwFormURLEncoded = "application/x-www-form-urlencoded".equalsIgnoreCase(contentType);
        String requestPath = request.getURIEncoded();

        // TODO detect this?
        this.urlCharacterEncoding = urlCharacterEncoding == null ? SystemProperties.get("adito.urlencoding", "UTF-8") : urlCharacterEncoding;

        /*
         * The launch session and URL may be provided in one of two ways.
         * 
         * 1. As a request to /replacementProxyEngine with the launch session
         * and target URL provided as sslx_launchId and sslx_url respectively
         * 
         * 2. In the new format /replacementProxyEngine/[launchId]/[encodedURL]
         */

        proxyURIDetails = parseProxyPath(requestPath, this.urlCharacterEncoding);

        if (multipart) {
            // If its a multipart then set up for reading stream
            initMultipart(contentType, request.getInputStream());
        } else {
            /*
             * Let the servlet engine deal with it
             * 
             * Parameter names and values from the request are not encoded so
             * can be placed straight in the map.
             */
            Map params = request.getParameters();
            for (Iterator e = params.keySet().iterator(); e.hasNext();) {
                String n = (String) e.next();
                Object obj = params.get(n);
                if (!checkForSSLXUrl(proxyURIDetails, n, obj, this.urlCharacterEncoding)) {
                    if (obj instanceof String[]) {
                        addIfNotAUriParameter(n, ((String[]) obj)[0]);
                    } else if (obj instanceof String) {
                        addIfNotAUriParameter(n, (String) obj);
                    } else
                        log.warn("Parameter value is an unexepected type " + obj.getClass().getName());
                }
            }
        }

    }

    /**
     * Get the {@link ProxyURIDetails} object. This encapsulates the <i>Proxy
     * URI</i> that is a special URI that contains both the <i>Launch ID</i>
     * and the <i>Proxied URL</i> (i.e. the target). It also contains a map of
     * all parameters supplied as part of the URI.
     * 
     * @return proxy URI
     */
    public ProxyURIDetails getProxiedURIDetails() {
        return proxyURIDetails;
    }

    public static ProxyURIDetails parseProxyPath(String requestPath, String urlCharacterEncoding)
                    throws UnsupportedEncodingException, MalformedURLException {
        ProxyURIDetails proxyURIDetails = null;
        if (requestPath != null && requestPath.startsWith("/replacementProxyEngine/")) {
            proxyURIDetails = processNewStyleReplacementUri(requestPath, urlCharacterEncoding);
        } else {
            proxyURIDetails = processOldStyleReplacementUri(requestPath, urlCharacterEncoding);
        }
        if (proxyURIDetails.getProxiedURLBase() != null) {
            if (proxyURIDetails.getUriParameters().size() != 0) {
                StringBuffer buf = new StringBuffer();
                if (proxyURIDetails.getProxiedURLBase().getPath() != null) {
                    buf.append(proxyURIDetails.getProxiedURLBase().getPath());
                }
                buf.append("?");
                buf.append(proxyURIDetails.getUriParametersAsEncodedString(urlCharacterEncoding));
                proxyURIDetails.setProxiedURL(new URL(proxyURIDetails.getProxiedURLBase(), buf.toString()));
            } else
                proxyURIDetails.setProxiedURL(proxyURIDetails.getProxiedURLBase());
        }
        return proxyURIDetails;
    }

    /**
     * Get the character encoding to use for URLS
     * 
     * @return url character encoding
     */
    public String getUrlCharacterEncoding() {
        return urlCharacterEncoding;
    }

    /**
     * Get if this map was initialised from a request with a content type of
     * <b>application/x-www-form-urlencoded</b>.
     * 
     * @return is form data
     */
    public boolean isWwwFormURLEncoded() {
        return wwwwFormURLEncoded;
    }

    /**
     * Get if this map was initialised from a request with a content type of
     * <b>multipart/form-data</b>.
     * 
     * @return is form data
     */
    public boolean isMultipart() {
        return multipart;
    }

    /**
     * TODO: This current calls getParameter to ensure that only a single
     * paramter is returned. Implementations that use this type of Map should be
     * changed to support multiple values for a single key, or to use
     * getParameter if they do not require multiple value support.
     * 
     * @param name
     * @return the first value in the list
     */
    public Object get(Object name) {
        return getParameter(name);
    }

    /**
     * If this map was initialised from a <b>multipart/form-data</b> request,
     * then this method returns an input stream of the content received. This
     * content is actually read from a temporary file which is deleted when the
     * stream is closed.
     * 
     * @return form data stream
     * @throws IOException if map not initialised from <b>multipart/form-data</b>
     *         request
     * @throws FileNotFoundException
     */
    public InputStream getMultipartData() throws IOException, FileNotFoundException {
        if (multipartFile == null) {
            throw new IOException("This request was not of type multipart/form-data.");
        }
        return new FileMultipartInputStream(multipartFile);
    }

    /**
     * If this map was initialised from a <b>multipart/form-data</b> request,
     * then this method returns the number of bytes in the content.
     * 
     * @return form data length
     * @throws IOException
     */
    public long getMultipartDataLength() throws IOException {
        if (multipartFile == null) {
            throw new IOException("This request was not of type multipart/form-data.");
        }
        return multipartFile.length();
    }

    /**
     * Get all of the parameters NOT supplied as part of the URI as a string.
     * The names and values will be encoded. There will be no leading ? or
     * trailing &amp;. An empty string will be returned if there are no
     * parameters.
     * 
     * @return parameters as an encoded string
     * @throws UnsupportedEncodingException
     */
    public String getParametersAsEncodedString() throws UnsupportedEncodingException {
        return getMapEncodedAsString(this, urlCharacterEncoding);
    }

    /**
     * Get a parameters value given its name. The value returned is unencoded.
     * 
     * @param name parameter name
     * @return encoded value
     */
    public String getParameter(Object name) {
        List list = getValues(name);
        if (list != null && list.size() >= 1) {
            return (String) list.get(0);
        } else
            return null;
    }

    public List getParameterValues(Object name) {
        return getValues(name);
    }

    /**
     * Get an {@link Iterator} of all parameters names contained in this map.
     * 
     * @return parameters name
     */
    public Iterator getParameterNames() {
        return keySet().iterator();
    }

    private void readBytes(InputStream in, OutputStream out, OutputStream recorded) throws IOException {

        int c;
        boolean cr = false;
        boolean lf = false;

        // loop for all lines`
        while (true) {
            int b = 0;
            while ((c = (ch != -2) ? ch : readWrite(in, recorded)) != -1) {
                ch = -2;

                // look for CR and/or LF
                if (c == 13 || c == 10) {
                    if (c == 13) {
                        ch = readWrite(in, recorded);
                    }
                    break;
                }

                // look for boundary
                if (b >= 0 && b < boundaryBytes.length && c == boundaryBytes[b])
                    b++;
                else {
                    // this is not a boundary
                    if (cr && out != null)
                        out.write(13);
                    if (lf && out != null)
                        out.write(10);
                    cr = lf = false;

                    if (b > 0 && out != null)
                        out.write(boundaryBytes, 0, b);
                    b = -1;

                    if (out != null)
                        out.write(c);
                }
            }

            // check partial boundary
            if ((b > 0 && b < boundaryBytes.length - 2) || (b == boundaryBytes.length - 1)) {
                if (cr && out != null)
                    out.write(13);
                if (lf && out != null)
                    out.write(10);
                cr = lf = false;
                if (out != null)
                    out.write(boundaryBytes, 0, b);
                b = -1;
            }

            // boundary match
            if (b > 0 || c == -1) {
                if (b == boundaryBytes.length)
                    finished = true;
                if (ch == 10)
                    ch = -2;
                break;
            }

            // handle CR LF
            if (cr && out != null)
                out.write(13);
            if (lf && out != null)
                out.write(10);
            cr = (c == 13);
            lf = (c == 10 || ch == 10);
            if (ch == 10)
                ch = -2;
        }
    }

    private int readWrite(InputStream in, OutputStream out) throws IOException {
        int i = in.read();
        if (i != -1 && out != null) {
            out.write(i);
        }
        return i;

    }

    private static void appendToUriString(StringBuffer buf, String name, String val, String urlCharacterEncoding)
                    throws UnsupportedEncodingException {
        if (buf.length() > 0) {
            buf.append("&");
        }
        buf.append(URLEncoder.encode(name, urlCharacterEncoding));
        buf.append("=");
        buf.append(URLEncoder.encode(val, urlCharacterEncoding));
    }

    private static boolean checkForSSLXUrl(ProxyURIDetails proxyURIDetails, String name, Object value, String urlCharacterEncoding)
                    throws UnsupportedEncodingException {
        String val;

        if (value instanceof String[]) {
            val = ((String[]) value)[0];
        } else if(value instanceof List){
            val = (String)((List) value).get(0);
        } else {
            val = (String) value;
        }
        

        if (name.equals("sslex_url")) {
            proxyURIDetails.proxiedURLBase = parseProxiedURL(URLDecoder.decode(val, urlCharacterEncoding), proxyURIDetails
                            .getUriParameters());
            return true;
        } else if (name.equals(LaunchSession.LONG_LAUNCH_ID)) {
            proxyURIDetails.launchId = val;
            return true;
        }
        return false;
    }

    private void initMultipart(String contentType, InputStream in) throws IOException, FileNotFoundException {
    	// PLUNDEN: Removing the context
        // multipartFile = new File(ContextHolder.getContext().getTempDirectory(), "mpr" + ( tempIdx++ ) + ".tmp");
    	multipartFile = new File(SystemProperties.get("adito.directories.tmp", "tmp"), "mpr" + ( tempIdx++ ) + ".tmp");
        // end change
        FileOutputStream mpOut = new FileOutputStream(multipartFile);

        try {

            input = new LineInput(in);
            boundary = "--" + Util.valueOfNameValuePair(contentType.substring(contentType.indexOf("boundary=")));
            boundaryBytes = (boundary + "--").getBytes(ISO_8859_1);// Get
            // first
            // boundary
            String line = input.readLine();
            if (!line.equals(boundary)) {
                throw new IOException("Missing initial multi part boundary");
            }

            byte[] buf = (line + "\r\n").getBytes();
            mpOut.write(buf);

            while (!finished) {
                String contentDisposition = null;

                Map<String, List<String>> parms = new TreeMap<String, List<String>>();
                while ((line = input.readLine()) != null) {
                    if (line.length() == 0)
                        break;
                    int idx = line.indexOf(':', 0);
                    if (idx > 0) {
                        String key = line.substring(0, idx).trim();
                        String value = line.substring(idx + 1, line.length()).trim();

                        List<String> cur = (List<String>) parms.get(key);
                        if (cur == null) {
                            cur = new ArrayList<String>();
                            parms.put(key, cur);
                        }
                        cur.add(value);

                        if (key.equalsIgnoreCase("content-disposition"))
                            contentDisposition = value;
                    }
                }
                boolean form = false;
                if (contentDisposition == null) {
                    throw new IOException("Missing content-disposition");
                }
                StringTokenizer tok = new StringTokenizer(contentDisposition, ";");
                String name = null;
                String filename = null;
                while (tok.hasMoreTokens()) {
                    String t = tok.nextToken().trim();
                    String tl = t.toLowerCase();
                    if (t.startsWith("form-data"))
                        form = true;
                    else if (tl.startsWith("name="))
                        name = Util.valueOfNameValuePair(t);
                    else if (tl.startsWith("filename="))
                        filename = Util.valueOfNameValuePair(t);
                }

                // Check disposition
                if (!form) {
                    log.warn("Non form-data part in multipart/form-data");
                    continue;
                }

                if (name.equals("sslex_url")) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    readBytes(input, baos, null);
                    parseProxiedURL(new String(baos.toString()), this);
                } else {
                    for (Iterator i = parms.keySet().iterator(); i.hasNext();) {
                        String key = (String) i.next();
                        List list = (List) parms.get(key);
                        for (Iterator j = list.iterator(); j.hasNext();) {
                            String val = (String) j.next();
                            buf = (key + ": " + val + "\r\n").getBytes();
                            mpOut.write(buf);
                        }
                    }
                    buf = "\r\n".getBytes();
                    mpOut.write(buf);

                    if (filename != null) {
                        readBytes(input, null, mpOut);
                    } else {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        readBytes(input, baos, mpOut);
                        put(Util.urlDecode(name), baos.toString());
                    }
                }
            }
        } finally {
            Util.closeStream(mpOut);
        }

    }

    private static URL parseProxiedURL(String location, MultiMap map) {
        try {
            URL proxiedURL = new URL(location);
            URL proxiedURLBase = proxiedURL;

            // Extract parameters from the proxied URL
            String query = proxiedURL.getQuery();
            if (query != null) {
                proxiedURLBase = new URL(proxiedURL.getProtocol(), proxiedURL.getHost(), proxiedURL.getPort() < 1 ? -1 : proxiedURL
                                .getPort(), proxiedURL.getPath());
                parseQuery(map, query);
            }
            return proxiedURLBase;
        } catch (MalformedURLException murle) {
            log.error("Invalid proxied URL '" + location + "'");
        }
        return null;
    }

    private static ProxyURIDetails processNewStyleReplacementUri(String requestPath, String urlCharacterEncoding)
                    throws UnsupportedEncodingException {
        int idx = requestPath.indexOf('/', 1);
        int idx2 = requestPath.indexOf('/', idx + 1);
        String launchId = requestPath.substring(idx + 1, idx2);
        // In this case all of the parameters supplied will be URI
        // parameters
        MultiMap uriParameters = new MultiMap();
        URL url = parseProxiedURL(URLDecoder.decode(requestPath.substring(idx2 + 1), urlCharacterEncoding), uriParameters);
        return new ProxyURIDetails(url, launchId, uriParameters);
    }

    private static ProxyURIDetails processOldStyleReplacementUri(String requestPath, String urlCharacterEncoding)
                    throws UnsupportedEncodingException {
        // In this case the URI parameters might contain a launch ID and
        // proxied URL
        MultiMap uriParameters = new MultiMap();
        MultiMap oldStyleUriParameters = new MultiMap();
        int idx = requestPath.indexOf('?');
        ProxyURIDetails proxyURIDetails = new ProxyURIDetails(null, null, uriParameters);
        if (idx != -1) {
            /*
             * Try and locate the sslx url and launch session, putting
             * everything else in our URI paramers map
             */
            parseQuery(oldStyleUriParameters, requestPath.substring(idx + 1));
            for (Iterator e = oldStyleUriParameters.keySet().iterator(); e.hasNext();) {
                String n = (String) e.next();
                Object obj = oldStyleUriParameters.get(n);
                if (!checkForSSLXUrl(proxyURIDetails, n, obj, urlCharacterEncoding)) {
                    if (obj instanceof List) {
                        uriParameters.put(n, ((List) obj).get(0));
                    } else if (obj instanceof String) {
                        uriParameters.put(n, obj.toString());
                    } else
                        log.warn("Parameter value is an unexepected type " + obj.getClass().getName());
                }
            }
        }
        return proxyURIDetails;
    }

    private void addIfNotAUriParameter(String n, String val) {
        // Skip anything that is a known URI parameter
        if (!proxyURIDetails.getUriParameters().containsKey(n) || !proxyURIDetails.getUriParameters().getValues(n).contains(val)) {
            put(n, val.toString());
        }
    }

    private static String getMapEncodedAsString(MultiMap map, String urlCharacterEncoding) throws UnsupportedEncodingException {
        StringBuffer buf = new StringBuffer();
        for (Iterator e = map.keySet().iterator(); e.hasNext();) {
            String n = (String) e.next();
            Object obj = map.get(n);
            if (obj instanceof List) {
                appendToUriString(buf, n, (String)((List) obj).get(0), urlCharacterEncoding);
            } else if (obj instanceof String) {
                appendToUriString(buf, n, (String) obj, urlCharacterEncoding);
            }
        }
        return buf.toString();
    }

    private static void parseQuery(MultiMap map, String query) {
        StringTokenizer t = new StringTokenizer(query, "&");
        while (t.hasMoreTokens()) {
            String parm = t.nextToken();
            int pidx = parm.indexOf('=');
            String name = pidx == -1 ? parm : parm.substring(0, pidx);
            String value = pidx == -1 ? "" : parm.substring(pidx + 1);
            map.add(name, value);
        }
    }

    class FileMultipartInputStream extends FileInputStream {

        private File file;

        FileMultipartInputStream(File file) throws FileNotFoundException {
            super(file);
            this.file = file;
        }

        public void close() throws IOException {
            super.close();
            file.delete();
        }

    }

    public static class ProxyURIDetails {
        private URL proxiedURLBase, proxiedURL;
        private String launchId;
        private MultiMap parameters;

        private ProxyURIDetails(URL proxiedURLBase, String launchId, MultiMap parameters) {
            this.proxiedURLBase = proxiedURLBase;
            this.launchId = launchId;
            this.parameters = parameters;
            proxiedURL = proxiedURLBase;
        }

        /**
         * Get an parameters that were supplied as part of the request URI.
         * 
         * @return request URI parameters
         */
        public MultiMap getUriParameters() {
            return parameters;
        }

        /**
         * Get the <i>Proxied URL</i>, i.e. the URL of the page to actually
         * load from the target server. This will include any request parameters
         * supplied when in the URI. It will exclude any parameters required for
         * the operation of replacement proxy such as sslx_url and launch_id.
         * 
         * @return proxied URL
         */
        public URL getProxiedURL() {
            return proxiedURL;
        }

        /**
         * Get the <i>Proxied URL base</i>, i.e. the URL of the page to
         * actually load from the target server <b>excluding</b> <u>any</u>
         * request parameters.
         * 
         * @return proxied URL base
         */
        public URL getProxiedURLBase() {
            return proxiedURLBase;
        }

        /**
         * Get the ID of the {@link LaunchSession} that launched the web forward
         * this request is part of. This is specified by the request parameter
         * with the name of the contstant {@link LaunchSession#LONG_LAUNCH_ID}
         * value.
         * 
         * @return launch ID
         */
        public String getLaunchId() {
            return launchId;
        }

        private void setProxiedURL(URL proxiedURL) {
            this.proxiedURL = proxiedURL;
        }

        /**
         * Get all of the parameters supplied as part of the URI as a string.
         * The names and values will be encoded. There will be no leading ? or
         * trailing &amp;. An empty string will be returned if there are no
         * parameters.
         * 
         * @param urlCharacterEncoding URL character encoding
         * @return uri parameters as an encoded string
         * @throws UnsupportedEncodingException
         */
        public String getUriParametersAsEncodedString(String urlCharacterEncoding) throws UnsupportedEncodingException {
            return getMapEncodedAsString(getUriParameters(), urlCharacterEncoding);
        }

        /**
         * Get a URI suitable for use in an HTTP request that has been processed
         * for replacements.
         * 
         * @param sessionInfo
         * @return processed request URI
         */
        public String getProcessedRequestURI(SessionInfo sessionInfo) {
            String uriEncoded = Util.isNullOrTrimmedBlank(getProxiedURL().getFile()) ? "/" : getProxiedURL().getFile();
            if (log.isDebugEnabled())
                log.debug("Returning URI " + uriEncoded);
            return uriEncoded;
        }
    }

    /**
     * Get the content length from the original request. This will be -1 if the
     * content length is not known.
     * 
     * @return original content length
     */
    public long getOriginalContentLength() {
        return contentLength;
    }

    /**
     * Get the content type from the original request. This will be
     * <code>null</code> if the content length is not known.
     * 
     * @return original type length
     */
    public String getOriginalContentType() {
        return contentType;
    }

}
