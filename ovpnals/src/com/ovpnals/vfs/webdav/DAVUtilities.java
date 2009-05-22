/* ========================================================================== *
 * Copyright (C) 2004-2005 Pier Fumagalli <http://www.betaversion.org/~pier/> *
 *                            All rights reserved.                            *
 * ========================================================================== *
 *                                                                            *
 * Licensed under the  Apache License, Version 2.0  (the "License").  You may *
 * not use this file except in compliance with the License.  You may obtain a *
 * copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>.       *
 *                                                                            *
 * Unless  required  by applicable  law or  agreed  to  in writing,  software *
 * distributed under the License is distributed on an  "AS IS" BASIS, WITHOUT *
 * WARRANTIES OR  CONDITIONS OF ANY KIND, either express or implied.  See the *
 * License for the  specific language  governing permissions  and limitations *
 * under the License.                                                         *
 *                                                                            *
 * ========================================================================== */
package com.ovpnals.vfs.webdav;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.maverick.util.URLUTF8Encoder;
import com.ovpnals.boot.Util;
import com.ovpnals.core.stringreplacement.SessionInfoReplacer;
import com.ovpnals.security.SessionInfo;
import com.ovpnals.vfs.utils.URI;
import com.ovpnals.vfs.utils.URI.MalformedURIException;

/**
 * <p>
 * A collection of static utilities.
 * </p>
 * 
 * @author <a href="http://www.betaversion.org/~pier/">Pier Fumagalli</a>
 */
public class DAVUtilities {

    /**
     * <p>
     * A {@link String} of all acceptable characters in a URI.
     * </p>
     */
    // private static final String ACCEPTABLE = "ABCDEFGHIJLKMNOPQRSTUVWXYZ" +
    // // ALPHA
    // // (UPPER)
    // "abcdefghijklmnopqrstuvwxyz" + // ALPHA (LOWER)
    // "0123456789" + // DIGIT
    // "_-!.~'()*" + // UNRESERVED
    // ",;:$&+=" + // PUNCT
    // "?/[]@"; // RESERVED
    private static final String ACCEPTABLE = "ABCDEFGHIJLKMNOPQRSTUVWXYZ" + // ALPHA
                    // (UPPER)
                    "abcdefghijklmnopqrstuvwxyz" + // ALPHA (LOWER)
                    "0123456789" + // DIGIT
                    "_-!.~'()*" + // UNRESERVED
                    ",;:$+=" + // PUNCT
                    "?/@"; // RESERVED

    /**
     * <p>
     * The {@link SimpleDateFormat} RFC-822 date format.
     * </p>
     */
    private static final String FORMAT_822 = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";
    /**
     * <p>
     * The {@link TimeZone} to use for formatting RFC-822 dates.
     * </p>
     */
    private static final TimeZone TIMEZONE_822 = TimeZone.getTimeZone("GMT");

    /**
     * <p>
     * Deny public construction of {@link DAVUtilities} instances.
     * </p>
     */
    private DAVUtilities() {
        super();
    }

    /**
     * <p>
     * Return a {@link String} message given an HTTP status code.
     * </p>
     * 
     * @param status status code
     * @return status strings
     */
    public static String getStatusMessage(int status) {
        switch (status) {
            /* HTTP/1.1 RFC-2616 */
            case 100:
                return "100 Continue";
            case 101:
                return "101 Switching Protocols";
            case 200:
                return "200 OK";
            case 201:
                return "201 Created";
            case 202:
                return "202 Accepted";
            case 203:
                return "203 Non-Authoritative Information";
            case 204:
                return "204 No Content";
            case 205:
                return "205 Reset Content";
            case 206:
                return "206 Partial Content";
            case 300:
                return "300 Multiple Choices";
            case 301:
                return "301 Moved Permanently";
            case 302:
                return "302 Found";
            case 303:
                return "303 See Other";
            case 304:
                return "304 Not Modified";
            case 305:
                return "305 Use Proxy";
            case 306:
                return "306 (Unused)";
            case 307:
                return "307 Temporary Redirect";
            case 400:
                return "400 Bad Request";
            case 401:
                return "401 Unauthorized";
            case 402:
                return "402 Payment Required";
            case 403:
                return "403 Forbidden";
            case 404:
                return "404 Not Found";
            case 405:
                return "405 Method Not Allowed";
            case 406:
                return "406 Not Acceptable";
            case 407:
                return "407 Proxy Authentication Required";
            case 408:
                return "408 Request Timeout";
            case 409:
                return "409 Conflict";
            case 410:
                return "410 Gone";
            case 411:
                return "411 Length Required";
            case 412:
                return "412 Precondition Failed";
            case 413:
                return "413 Request Entity Too Large";
            case 414:
                return "414 Request-URI Too Long";
            case 415:
                return "415 Unsupported Media Type";
            case 416:
                return "416 Requested Range Not Satisfiable";
            case 417:
                return "417 Expectation Failed";
            case 500:
                return "500 Internal Server Error";
            case 501:
                return "501 Not Implemented";
            case 502:
                return "502 Bad Gateway";
            case 503:
                return "503 Service Unavailable";
            case 504:
                return "504 Gateway Timeout";
            case 505:
                return "505 HTTP Version Not Supported";

                /* DAV/1.0 RFC-2518 */
            case 102:
                return "102 Processing";
            case 207:
                return "207 Multi-Status";
            case 422:
                return "422 Unprocessable Entity";
            case 423:
                return "423 Locked";
            case 424:
                return "424 Failed Dependency";
            case 507:
                return "507 Insufficient Storage";

                /* Unknown */
            default:
                return null;
        }
    }

    /**
     * <p>
     * Format an {@link Object} according to the HTTP/1.1 RFC.
     * </p>
     * 
     * @param object the {@link Object} to format.
     * @return a {@link String} instance or <b>null</b> if the object was null.
     */
    public static String format(Object object) {
        if (object == null)
            return null;
        if (object instanceof String)
            return ((String) object);
        if (object instanceof Date) {
            SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_822, Locale.ENGLISH);
            formatter.setTimeZone(TIMEZONE_822);
            return formatter.format((Date) object);
        }
        return (object.toString());
    }

    /**
     * <p>
     * Return the HEX representation of an array of bytes.
     * </p>
     * 
     * @param buffer the array of bytes to convert in a HEX {@link String}.
     * @return a <b>non-null</b> {@link String} instance.
     */
    public static String toHexString(byte buffer[]) {
        char output[] = new char[buffer.length * 2];
        int position = 0;
        for (int x = 0; x < buffer.length; x++) {
            output[position++] = DAVUtilities.toHexDigit(buffer[x] >> 4);
            output[position++] = DAVUtilities.toHexDigit(buffer[x]);
        }
        return new String(output);
    }

    /**
     * <p>
     * Return the HEX representation of a long integer.
     * </p>
     * 
     * @param number the long to convert in a HEX {@link String}.
     * @return a <b>non-null</b> 16-characters {@link String} instance.
     */
    public static String toHexString(long number) {
        char output[] = new char[16];
        output[0] = DAVUtilities.toHexDigit((int) (number >> 60));
        output[1] = DAVUtilities.toHexDigit((int) (number >> 56));
        output[2] = DAVUtilities.toHexDigit((int) (number >> 52));
        output[3] = DAVUtilities.toHexDigit((int) (number >> 48));
        output[4] = DAVUtilities.toHexDigit((int) (number >> 44));
        output[5] = DAVUtilities.toHexDigit((int) (number >> 40));
        output[6] = DAVUtilities.toHexDigit((int) (number >> 36));
        output[7] = DAVUtilities.toHexDigit((int) (number >> 32));
        output[8] = DAVUtilities.toHexDigit((int) (number >> 28));
        output[9] = DAVUtilities.toHexDigit((int) (number >> 24));
        output[10] = DAVUtilities.toHexDigit((int) (number >> 20));
        output[11] = DAVUtilities.toHexDigit((int) (number >> 16));
        output[12] = DAVUtilities.toHexDigit((int) (number >> 12));
        output[13] = DAVUtilities.toHexDigit((int) (number >> 8));
        output[14] = DAVUtilities.toHexDigit((int) (number >> 4));
        output[15] = DAVUtilities.toHexDigit((int) (number));
        return new String(output);
    }

    /**
     * <p>
     * Return the HEX representation of an integer.
     * </p>
     * 
     * @param number the int to convert in a HEX {@link String}.
     * @return a <b>non-null</b> 8-characters {@link String} instance.
     */
    public static String toHexString(int number) {
        char output[] = new char[8];
        output[0] = DAVUtilities.toHexDigit((int) (number >> 28));
        output[1] = DAVUtilities.toHexDigit((int) (number >> 24));
        output[2] = DAVUtilities.toHexDigit((int) (number >> 20));
        output[3] = DAVUtilities.toHexDigit((int) (number >> 16));
        output[4] = DAVUtilities.toHexDigit((int) (number >> 12));
        output[5] = DAVUtilities.toHexDigit((int) (number >> 8));
        output[6] = DAVUtilities.toHexDigit((int) (number >> 4));
        output[7] = DAVUtilities.toHexDigit((int) (number));
        return new String(output);
    }

    /**
     * <p>
     * Return the HEX representation of a char.
     * </p>
     * 
     * @param number the char to convert in a HEX {@link String}.
     * @return a <b>non-null</b> 4-characters {@link String} instance.
     */
    public static String toHexString(char number) {
        char output[] = new char[4];
        output[0] = DAVUtilities.toHexDigit((int) (number >> 12));
        output[1] = DAVUtilities.toHexDigit((int) (number >> 8));
        output[2] = DAVUtilities.toHexDigit((int) (number >> 4));
        output[3] = DAVUtilities.toHexDigit((int) (number));
        return new String(output);
    }

    /**
     * <p>
     * Return the HEX representation of a byte.
     * </p>
     * 
     * @param number the byte to convert in a HEX {@link String}.
     * @return a <b>non-null</b> 2-characters {@link String} instance.
     */
    public static String toHexString(byte number) {
        char output[] = new char[2];
        output[0] = DAVUtilities.toHexDigit((int) (number >> 4));
        output[1] = DAVUtilities.toHexDigit((int) (number));
        return new String(output);
    }

    /**
     * <p>
     * Return the single digit character representing the HEX encoding of the
     * lower four bits of a given integer.
     * </p>
     * 
     * @param number number to conver
     * @return hex character
     */
    private static char toHexDigit(int number) {
        switch (number & 0x0F) {
            case 0x00:
                return '0';
            case 0x01:
                return '1';
            case 0x02:
                return '2';
            case 0x03:
                return '3';
            case 0x04:
                return '4';
            case 0x05:
                return '5';
            case 0x06:
                return '6';
            case 0x07:
                return '7';
            case 0x08:
                return '8';
            case 0x09:
                return '9';
            case 0x0A:
                return 'A';
            case 0x0B:
                return 'B';
            case 0x0C:
                return 'C';
            case 0x0D:
                return 'D';
            case 0x0E:
                return 'E';
            case 0x0F:
                return 'F';
        }
        String message = "Invalid HEX digit " + Integer.toHexString(number);
        throw new IllegalArgumentException(message);
    }

    /**
     * Encode a path suitable for use in a URI. Forward slashes will not be encoded.
     * 
     * @param path
     * @return encoding path
     */
    public static String encodePath(String path) {
        return encodePath(path, false, "UTF-8");
    }
    
    public static String encodePath(String path, boolean encodeSlash) {
        return encodePath(path, encodeSlash, "UTF-8");
    }

    public static String encodePath(String path, String charset) {
        return encodePath(path, false, charset);
    }    
    /**
     * Process a URI for replacements and encode the result correctly  
     * 
     * @param uri
     * @param session session info to use for replacements
     * @return processed uri
     * @throws MalformedURIException
     */
    public static URI processAndEncodeURI(String uri, SessionInfo session, String charset) throws MalformedURIException {
        // TODO We have problems with passwords containing @ characters here
        String path = session == null ? uri : SessionInfoReplacer.replace(session, uri);
        URI nuri = new URI(path);
        if(nuri.getUserinfo() != null) {
            nuri.setUserinfo(encodeURIUserInfo(nuri.getUserinfo()));
        }
        if(nuri.getPath() != null && !nuri.getPath().equals("")) {
            nuri.setPath(encodePath(nuri.getPath(), charset));
        }
        return nuri;
    }
    
    // NOTE this method is for the password hack in prcess
    
    public static URI processAndEncodeURI(String uri, SessionInfo session) throws MalformedURIException {        
        return processAndEncodeURI(uri, session, "UTF-8");
    }

    /**
     * Encode  a path suitable for use in a URI.
     * 
     * @param path path
     * @param encodeSlash encode forward slashes (/)
     * @return encoded path
     */
    public static String encodePath(String path, boolean encodeSlash, String charset) {
        /* Encode the string */
        StringBuffer buffer = new StringBuffer();
        byte encoded[];
        try {
            if(charset==null)
                encoded = path.getBytes();
            else
                encoded = path.getBytes(charset);
            for (int x = 0; x < encoded.length; x++) {
                if (((int) encoded[x] == '%' && encodeSlash) || ACCEPTABLE.indexOf((int) encoded[x]) < 0) {
                    buffer.append('%');
                    buffer.append(DAVUtilities.toHexString(encoded[x]));
                    continue;
                }
                buffer.append((char) encoded[x]);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return path;
        }

        return buffer.toString();
    }
    
    /**
     * Encode the user info part of a URI
     * 
     * @param uriUserInfo URI user info
     * @return encoded URI user info
     */
    public static String encodeURIUserInfo(String uriUserInfo) {
        int idx = uriUserInfo.indexOf(':');
        if(idx != -1) {
            return URLUTF8Encoder.encode(uriUserInfo.substring(0, idx), true) + ":" + 
            URLUTF8Encoder.encode(uriUserInfo.substring(idx + 1), true);
        }
        return Util.urlEncode(uriUserInfo);
    }

    /**
     * Get ETAG
     * 
     * @param path
     * @param lastModified
     * @return ETAG
     */
    public static String getETAG(String path, Date lastModified) {
        StringBuffer etag = new StringBuffer();
        etag.append('"');

        /* Append the MD5 hash of this resource name */
        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            digester.reset();
            digester.update(path.getBytes("UTF8"));
            etag.append(DAVUtilities.toHexString(digester.digest()));
            etag.append('-');
        } catch (Exception e) {
            // If we can't get the MD5 HASH, let's ignore and hope...
        }

        /* Append the hashCode of this resource name */
        etag.append(DAVUtilities.toHexString(path.hashCode()));

        /* Append the last modification date if possible */
        if (lastModified != null) {
            etag.append('-');
            etag.append(DAVUtilities.toHexString(lastModified.getTime()));
        }

        /* Close the ETag */
        etag.append('"');
        return (etag.toString());
    }

    /**
     * Strip the first element in a path
     * 
     * @param path
     * @return new path
     */
    public static String stripFirstPath(String path) {
        if (path.length() < 1) {
            return path;
        }
        int idx = path.indexOf('/', 1);
        return idx == -1 ? path : path.substring(idx);
    }

    /**
     * Strip all leading slashes
     * 
     * @param path
     * @return new path
     */
    public static String stripLeadingSlash(String path) {
        while (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    /**
     * Strip all trailing slashes
     * 
     * @param path
     * @return new path
     */
    public static String stripTrailingSlash(String path) {
        while (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
    
    /**
     * String the user info section from a URI
     * @param uri
     * @return
     */
    public static String stripUserInfo(String uri) {
    	
    	int idx1 = uri.indexOf("//");
    	int idx2 = uri.indexOf('@');
    	
    	if(idx1 > -1 && idx2 > -1 && idx1 < idx2) {
    		return uri.substring(0, idx1+2) + uri.substring(idx2);
    	} else
    		return uri;
    	
    }

    /**
     * Strips the directory from a file path (if any) using the specified
     * character as a separator. If an empty path is supplied then an empty is
     * returned.
     * 
     * @param path path
     * @param separator separator
     * @return path
     */
    public static String basename(String path, char separator) {
        if (path.equals("")) {
            return path;
        }
        while (path.endsWith(String.valueOf(separator))) {
            path = path.substring(0, path.length() - 1);
        }
        int idx = path.lastIndexOf(separator);
        return idx == -1 ? path : path.substring(idx + 1);
    }

    /**
     * Concatent two paths
     * 
     * @param original original path
     * @param append path to append
     * @return new path
     */
    public static String concatenatePaths(String original, String append) {
        if (append != null) {
            if (original.endsWith("/")) {
                original = original.concat(stripLeadingSlash(append));
            } else {
                if (append.startsWith("/")) {
                    original = original.concat(append);
                } else {
                    original = original.concat("/".concat(append));
                }
            }
        }
        return original;
    }

    /**
     * Get the parent path or <code>null</code> if at the root
     * 
     * @param path
     * @return parent path
     */
    public static String getParentPath(String path) {
        path = stripTrailingSlash(path);
        String parent = null;
        if (!path.equals("")) {
            int idx = path.lastIndexOf("/");
            if (idx == -1) {
                parent = "/";
            } else {
                parent = path.substring(0, idx + 1);
            }
        }
        return parent;
    }

    /**
     * Strips any trailing slashes then returns anything up to last slash (i.e.
     * the filename part is stripped leave the directory name). If the path is
     * already /, <code>null</code> will be returned.
     * 
     * @param path path
     * @return dirname
     */
    public static String dirname(String path) {
        String s = stripTrailingSlash(path);
        if (s.equals("")) {
            return null;
        }
        return s.substring(0, s.lastIndexOf("/"));
    }
}
