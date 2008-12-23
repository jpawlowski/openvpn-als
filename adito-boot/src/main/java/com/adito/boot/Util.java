/*
 */
package com.adito.boot;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utilities used throughout the Adito boot environment, server
 * implementation and web application.
 */
public class Util {

    final static Log log = LogFactory.getLog(Util.class);

    /**
     * Default buffer size for stream utility methods
     */
    public static int BUFFER_SIZE = 8192;

    /*
     * Prevent instantiation
     */
    private Util() {
        super();
    }

    /**
     * Get the statement from the current stack trace given its depth. I.e, a
     * depth of 0 will return this method, a depth of 1 will return the method
     * that called this method etc.
     * 
     * @param depth depth
     * @return statement as string
     */
    public static String getCurrentStatement(int depth) {
        try {
            throw new Exception();
        } catch (Exception e) {
            try {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                BufferedReader reader = new BufferedReader(new StringReader(sw.toString()));
                reader.readLine();
                reader.readLine();
                sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw, true);
                for (int i = 0; i < depth; i++) {
                    String s = reader.readLine();
                    pw.println(s.substring(7));
                }
                return sw.toString();

            } catch (Throwable t) {
                return "Unknown.";
            }
        }
    }

    /**
     * Trim spaces from both ends of string
     * 
     * @param string string to trim
     * @return trimmed string
     */
    public static String trimBoth(String string) {
        string = string.trim();
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) != ' ') {
                return string.substring(i);
            }
        }
        return string;
    }

    /**
     * Close an output stream, ignoing any exceptions. No error will be thrown
     * if the provided stream is <code>null</code>
     * 
     * @param outputStream stream to close
     */
    public static void closeStream(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException ioe) {

            }
        }
    }

    /**
     * Close an input stream, ignoing any exceptions. No error will be thrown if
     * the provided stream is <code>null</code>
     * 
     * @param inputStream stream to close
     */
    public static void closeStream(InputStream inputStream) {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ioe) {

            }
        }
    }

    /**
     * Extract the value portion of a string in the format of a named value pair
     * i.e. <i>[name]=[value]</i>.
     * 
     * @param nameEqualsValue string
     * @return value portion of name / value pair
     */
    public static String valueOfNameValuePair(String nameEqualsValue) {
        String value = nameEqualsValue.substring(nameEqualsValue.indexOf('=') + 1).trim();

        int i = value.indexOf(';');
        if (i > 0)
            value = value.substring(0, i);
        if (value.startsWith("\"")) {
            value = value.substring(1, value.indexOf('"', 1));
        }

        else {
            i = value.indexOf(' ');
            if (i > 0)
                value = value.substring(0, i);
        }
        return value;
    }

    /**
     * Convert a byte array to a hex string
     * 
     * @param data
     * @return hex string
     */
    public static String toHexString(byte[] data) {
        return toHexString(data, 0, data.length);
    }

    /**
     * Convert a byte array to a hex string
     * 
     * @param data bytes to convert
     * @param offset offset in array to start from
     * @param len number of bytes to convert
     * @return hex string
     */
    public static String toHexString(byte[] data, int offset, int len) {
        StringBuffer buf = new StringBuffer();
        for (int i = offset; i < len; i++) {
            String s = Integer.toHexString(((byte)data[i] & 0xFF));
            if (s.length() < 2) {
                buf.append("0");
            }
            buf.append(s);
        }
        return buf.toString();
    }

    /**
     * Rebuild the URI of the request by concatenating the servlet path and and
     * request parameters
     * 
     * @param request request to extra path from
     * @return path
     */
    public static String getOriginalRequest(HttpServletRequest request) {
        StringBuffer req = new StringBuffer(request.getServletPath());
        if (request.getQueryString() != null && request.getQueryString().length() > 0) {
            req.append("?");
            req.append(request.getQueryString());
        }
        return req.toString();
    }

    /**
     * Read an input stream and load it into a string.
     * 
     * @param in input stream
     * @param charsetName encoding or <code>null</code> for default
     * @return string
     * @throws IOException on any error
     */
    public static String loadStreamToString(InputStream in, String charsetName) throws IOException {
        StringBuffer licenseText = new StringBuffer();
        BufferedReader br = new BufferedReader(charsetName == null ? new InputStreamReader(in) : new InputStreamReader(in,
                        charsetName));
        try {
            char[] buf = new char[65536];
            int r = 0;
            while ((r = br.read(buf)) != -1)
                licenseText.append(buf, 0, r);
        } finally {
            br.close();
        }
        return licenseText.toString();
    }

    /**
     * Dump all session attributes to {@link System#err}.
     * 
     * @param session session to get attributes from
     */
    public static void dumpSessionAttributes(HttpSession session) {
        System.err.println("Session attributes for " + session.getId());
        for (Enumeration e = session.getAttributeNames(); e.hasMoreElements();) {
            String n = (String) e.nextElement();
            System.err.println("   " + n + " = " + session.getAttribute(n));
        }
    }

    /**
     * Dump all request attributes to {@link System#err}.
     * 
     * @param request request to get attributes from
     */
    public static void dumpRequestAttributes(HttpServletRequest request) {
        System.err.println("Request attributes for " + request.getPathTranslated());
        for (Enumeration e = request.getAttributeNames(); e.hasMoreElements();) {
            String n = (String) e.nextElement();
            System.err.println("   " + n + " = " + request.getAttribute(n));
        }
    }

    /**
     * Dump all request headers to {@link System#err}.
     * 
     * @param request request to get headers from
     */
    public static void dumpRequestHeaders(HttpServletRequest request) {
        System.err.println("Request headers for " + request.getPathTranslated());
        for (Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
            String n = (String) e.nextElement();
            for(Enumeration e2 = request.getHeaders(n); e2.hasMoreElements(); ) {
                String v = (String)e2.nextElement();
                System.err.println("   " + n + " = " + v);
            }
        }
    }

    /**
     * Dump all servlet context attributes to {@link System#err}.
     * 
     * @param context context to get attributes from
     */
    public static void dumpServletContextAttributes(ServletContext context) {
        System.err.println("Servlet context attributes for");
        for (Enumeration e = context.getAttributeNames(); e.hasMoreElements();) {
            String n = (String) e.nextElement();
            System.err.println("   " + n + " = " + context.getAttribute(n));
        }

    }

    /**
     * Dump all request parameters to {@link System#err}
     * 
     * @param request request to get parameters from
     */
    public static void dumpRequestParameters(HttpServletRequest request) {
        System.err.println("Request parameters for session #" + request.getSession().getId());
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            String n = (String) e.nextElement();
            String[] vals = request.getParameterValues(n);
            for (int i = 0; i < vals.length; i++) {
                System.err.println("   " + n + " = " + vals[i]);
            }
        }

    }

    /**
     * Dump all request parameters and some other useful stuff from
     * the request to {@link System#err}
     * 
     * @param request request to get parameters from
     */
    public static void dumpRequest(HttpServletRequest request) {
        System.err.println("Context Path " + request.getContextPath());
        System.err.println("Path Translated " + request.getPathTranslated());
        System.err.println("Path Info " + request.getPathInfo());
        System.err.println("Query: " + request.getQueryString());
        System.err.println("Request URI: " + request.getRequestURI());
        System.err.println("Request URL: " + request.getRequestURL());
        System.err.println("Is Secure: " + request.isSecure());
        System.err.println("Scheme: " + request.getScheme());
        dumpRequestParameters(request);
        dumpRequestAttributes(request);
        dumpRequestHeaders(request);

    }

    /**
     * Dump the contents of a {@link Map} to {@link System#err}.
     * 
     * @param map map to dump
     */
    public static void dumpMap(Map map) {
        System.err.println("Map dump");
        for (Iterator i = map.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            System.err.println("   Key = " + entry.getKey() + " Val = " + entry.getValue());
        }

    }

    /**
     * Dump an exception to {@link System#err}.
     * 
     * @param exception exception to dump
     */
    public static void printStackTrace(Throwable exception) {
        Exception e;
        try {
            throw new Exception();
        } catch (Exception ex) {
            e = ex;
        }
        StackTraceElement[] trace = e.getStackTrace();
        System.err.println("[REMOVE-ME] - " + trace[1].getClassName() + ":" + trace[1].getLineNumber());
        exception.printStackTrace();

    }

    /**
     * Concatenate all of the non null messages from an exception chain,
     * appending full stops after each messages if they do not end with one.
     * 
     * @param t trace
     * @return exception message chain text
     */
    public static String getExceptionMessageChain(Throwable t) {
        StringBuffer buf = new StringBuffer();
        while (t != null) {
            if (buf.length() > 0 && !buf.toString().endsWith(".")) {
                buf.append(". ");
            }
            if (t.getMessage() != null) {
                buf.append(t.getMessage().trim());
            }
            t = t.getCause();
        }
        return buf.toString();
    }

    /**
     * Print a TODO message to {@link System#err}, including the current class
     * and line number call was made along with a specified message.
     * <p>
     * Use for temporary debug. Calling statement should be removed.
     * </p>
     * 
     * @param message message to display
     */
    public static void toDo(String message) {
        Exception e;
        try {
            throw new Exception();
        } catch (Exception ex) {
            e = ex;
        }
        StackTraceElement[] trace = e.getStackTrace();
        System.err.println("[***TODO***] - " + trace[1].getClassName() + ":" + trace[1].getLineNumber() + " - " + message);
    }

    /**
     * Print some temporary debug to {@link System#err}, including the current
     * class and line number call was made along with a specified message.
     * <p>
     * Use for temporary debug. Calling statement should be removed.
     * </p>
     * 
     * @param message message to display
     */
    public static void removeMe(String message) {
        Exception e;
        try {
            throw new Exception();
        } catch (Exception ex) {
            e = ex;
        }
        StackTraceElement[] trace = e.getStackTrace();
        System.err.println("[REMOVE-ME] - " + trace[1].getClassName() + ":" + trace[1].getLineNumber() + " - " + message);

    }

    /**
     * This method will replace '&' with "&amp;", '"' with "&quot;", '<' with
     * "&lt;" and '>' with "&gt;".
     * 
     * @param html html to encode
     * @return encoded html
     * @see #decodeHTML(String)
     */
    public static String encodeHTML(String html) {
        // Does java have a method of doing this?
        StringBuffer buf = new StringBuffer();
        char ch;
        for (int i = 0; i < html.length(); i++) {
            ch = html.charAt(i);
            switch (ch) {
                case '&':

                    // May be already encoded
                    if (((i + 5) < html.length()) && html.substring(i + 1, i + 5).equals("amp;")) {
                        buf.append(ch);
                    } else {
                        buf.append("&#38;");
                    }
                    break;
                case '"':
                    buf.append("&#34;");
                    break;
                case '<':
                    buf.append("&#60;");
                    break;
                case '>':
                    buf.append("&#62;");
                    break;
                default:
                    buf.append(ch);
            }
        }
        return buf.toString();
    }

    /**
     * Decode HTML entities
     * 
     * @param html encoded html 
     * @return decoded html
     * @see #encodeHTML(String)
     */
    public static String decodeHTML(String html) {
        // Does java have a method of doing this?
        StringBuffer buf = new StringBuffer();
        char ch;
        for (int i = 0; i < html.length(); i++) {
            ch = html.charAt(i);
            switch (ch) {
                case '&':
                    String s = html.substring(i);
                    if (s.startsWith("&amp;")) {
                        buf.append("&");
                        i += 4;
                    } else if (s.startsWith("&quote;")) {
                        buf.append("\"");
                        i += 6;
                    } else if (s.startsWith("&lt;")) {
                        buf.append("<");
                        i += 3;
                    } else if (s.startsWith("&gt;")) {
                        buf.append(">");
                        i += 3;
                    }
                    break;
                default:
                    buf.append(ch);
            }
        }
        return buf.toString();
    }

    /**
     * Escape a string so it is suitable for including as a Javascript string.
     * 
     * @param string string to escape
     * @param doSingle replace single quotes
     * @param doDouble replace double quote quotes
     * @return escaped string
     */
    public static String escapeForJavascriptString(String string, boolean doSingle, boolean doDouble) {
        if(string == null) {
            return "";
        }
        string = trimBoth(string);
        string = string.replaceAll("\\\\", "\\\\\\\\");
        if(doSingle)
        	string = string.replaceAll("'", "\\\\'");
        if(doDouble)
        	string = string.replaceAll("\"", "\\\\'");
        String[] lines = string.split("\n");
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < lines.length; i++) {
            if (buf.length() > 0) {
                buf.append("<br/>");
            }
            buf.append(trimBoth(lines[i]));
        }
        return buf.toString();
    }

    /**
     * Escape a string so it is suitable for including as a Javascript string.
     * 
     * @param string
     * @return escaped string
     */
    public static String escapeForJavascriptString(String string) {
    	return escapeForJavascriptString(string, true, true);
    }

    /**
     * Delete a file or a directory and all of its children.
     * <p>
     * <b>Use with care ;-)</b>
     * 
     * @param file file or directory to delete
     * @return file or directory deleted ok
     */
    public static boolean delTree(File file) {
    	if (log.isDebugEnabled())
    		log.debug("Deleting " + file.getAbsolutePath());
        if (file.isDirectory()) {
            File[] f = file.listFiles();
            if (f != null) {
                for (int i = 0; i < f.length; i++) {
                    if (!delTree(f[i])) {
                        return false;
                    }
                }
            }
        }
        if (!file.delete()) {
            log.warn("Failed to remove " + file.getAbsolutePath());
            return false;
        }
        return true;
    }

    /**
     * Trim a string to the specified size, optionally appending elipses (..) if
     * the string is too large. Eclipses are included in the final size.
     * Otherwise, the string is simply cut off at its maximum size.
     * 
     * @param text text
     * @param size maximum size
     * @param addElipses add elipses if text to larget
     * @return trimmed string
     */
    public static String trimToSize(String text, int size, boolean addElipses) {
        return text.length() <= size ? text
                        : (text.substring(0, size - (addElipses ? (size > 3 ? 3 : size) : 0)) + (addElipses ? " .." : ""));
    }

    /**
     * Encode a url. First UTF-8 is tried, and if that fails US-ASCII.
     * 
     * @param url url to encode
     * @return encoded url
     */
    public static String urlEncode(String url) {
    	return urlEncode(url, SystemProperties.get("adito.urlencoding", "UTF-8"));
    }
    
    /**
     * Encode a url. First UTF-8 is tried, and if that fails US-ASCII.
     * 
     * @param url url to encode
     * @param charset the character set to encode with
     * @return encoded url
     */
    public static String urlEncode(String url, String charset) {
        try {
            // W3C recommended
            return URLEncoder.encode(url,  charset);
        } catch (UnsupportedEncodingException uee) {
            try {
                //
                return URLEncoder.encode(url, "us-ascii");
            } catch (UnsupportedEncodingException uee2) {
                log.error("URL could not be encoded! This should NOT happen!!!");
                return url;
            }
        }
    }

    /**
     * Decode a url. First UTF-8 is tried, and if that fails US-ASCII.
     * 
     * @param url url to decode
     * @return decoded url
     */
    public static String urlDecode(String url) {
        try {
            // W3C recommended
            return URLDecoder.decode(url, SystemProperties.get("adito.urlencoding", "UTF-8"));
        } catch (UnsupportedEncodingException uee) {
            try {
                //
                return URLDecoder.decode(url, "us-ascii");
            } catch (UnsupportedEncodingException uee2) {
                log.error("URL could not be decoded! This should NOT happen!!!");
                return url;
            }
        }
    }

    /**
     * Add headers to a response that will prevent compliant clients from
     * caching.
     * 
     * @param response response to add appropriate headers to
     */
    public static void noCache(HttpServletResponse response) {
        response.setHeader("Pragma", "no-cache");
        // You cannot use setDateHeader with -1. This actually sets a date
        // rather than sending Expires: -1
        //response.setDateHeader("Expires", new Date().getTime());
        //response.setHeader("Expires", "-1");
        response.setHeader("Cache-Control", "no-cache");
    }

    /**
     * Decode a string based on the either the _charset_ request parameter that
     * may have been suplied with a request or the requests character encoding.
     * <p>
     * TODO Make sure this still works and it being used correctly, im not so
     * sure it is!
     * 
     * @param request request to get encoding from
     * @param string string to decode
     * @return decoded string
     */
    public static String decodeRequestString(HttpServletRequest request, String string) {
        String enc = request.getParameter("_charset_");
        if (enc != null && !enc.equals("ISO-8859-1")) {
            try {
                return new String(string.getBytes("ISO-8859-1"), enc);
            } catch (Exception e) {
            }
        }
        enc = request.getCharacterEncoding();
        if (enc != null && !enc.equals("ISO-8859-1")) {
            try {
                return new String(string.getBytes("ISO-8859-1"), enc);
            } catch (Exception e) {
            }

        }
        return string;
    }

    /**
     * Create a {@link Map} from a {@link java.util.List}. The key and value
     * objects of each entry will be identical.
     * 
     * @param list list to turn into map
     * @return map
     */
    public static Map listToHashMapKeys(List list) {
        HashMap map = new HashMap();
        Object k;
        for (Iterator i = list.iterator(); i.hasNext();) {
            k = i.next();
            map.put(k, k);
        }
        return map;
    }

    /**
     * Copy from an input stream to an output stream. It is up to the caller to
     * close the streams.
     * 
     * @param in input stream
     * @param out output stream
     * @throws IOException on any error
     */
    public static void copy(InputStream in, OutputStream out) throws IOException {
        copy(in, out, -1);
    }


    /**
     * Copy the specified number of bytes from an input stream to an output
     * stream. It is up to the caller to close the streams.
     * 
     * @param in input stream
     * @param out output stream
     * @param count number of bytes to copy
     * @throws IOException on any error
     */
    public static void copy(InputStream in, OutputStream out, long count) throws IOException {
    	copy(in, out, count, BUFFER_SIZE);
    }

    /**
     * Copy the specified number of bytes from an input stream to an output
     * stream. It is up to the caller to close the streams.
     * 
     * @param in input stream
     * @param out output stream
     * @param count number of bytes to copy
     * @param bufferSize buffer size
     * @throws IOException on any error
     */
    public static void copy(InputStream in, OutputStream out, long count, int bufferSize) throws IOException {
        byte buffer[] = new byte[bufferSize];
        int i = bufferSize;
        if (count >= 0) {
            while (count > 0) {
                if (count < bufferSize)
                    i = in.read(buffer, 0, (int) count);
                else
                    i = in.read(buffer, 0, bufferSize);

                if (i == -1)
                    break;

                count -= i;
                out.write(buffer, 0, i);
                // LDP - Do not remove this flush!
                out.flush();
            }
        } else {
            while (true) {
                i = in.read(buffer, 0, bufferSize);
                if (i < 0)
                    break;
                if (log.isDebugEnabled())
                	log.debug("Transfered " + i + " bytes");
                out.write(buffer, 0, i);
                // LDP - Do not remove this flush!
                out.flush();
            }
        }
        
    }

    /**
     * Copy a file to another file.
     * 
     * @param f file to copy
     * @param t target file
     * @throws IOException on any error
     */
    public static void copy(File f, File t) throws IOException {
        copy(f, t, false);
    }

    /**
     * Copy a file to another file.
     * 
     * @param f file to copy
     * @param t target file
     * @param onlyIfNewer only copy if the target file is new
     * @throws IOException on any error
     */
    public static void copy(File f, File t, boolean onlyIfNewer) throws IOException {
        if (!onlyIfNewer || f.lastModified() > t.lastModified()) {
        	if (log.isDebugEnabled())
        		log.debug("Copying " + f.getAbsolutePath() + " to " + t.getAbsolutePath());
            InputStream in = new FileInputStream(f);
            try {
                OutputStream out = new FileOutputStream(t);
                try {
                    copy(in, out);
                } finally {
                    out.close();
                }
            } finally {
                in.close();
            }
            t.setLastModified(f.lastModified());
        } else {
        	if (log.isDebugEnabled())
        		log.debug("Skipping copying of file " + f.getAbsolutePath() + " as the target is newer than the source.");
        }
    }

    /**
     * Copy a file to a directory.
     * 
     * @param from file to copy
     * @param toDir target directory
     * @param replace replace existing file
     * @param onlyIfNewer only copy if the target file is new
     * @throws IOException on any error
     */
    public static void copyToDir(File from, File toDir, boolean replace, boolean onlyIfNewer) throws IOException {
        if (!toDir.exists()) {
            throw new IOException("Destination directory " + toDir.getAbsolutePath() + " doesn't exist.");
        }
        if (from.isDirectory()) {
            File toDirDir = new File(toDir, from.getName());
            if (toDirDir.exists() && replace) {
                delTree(toDirDir);
            }
            if (!toDirDir.exists()) {
            	if (log.isDebugEnabled())
            		log.debug("Creating directory " + toDirDir.getAbsolutePath());
                if (!toDirDir.mkdirs()) {
                    throw new IOException("Failed to create directory " + toDirDir.getAbsolutePath());
                }
            }
            File[] f = from.listFiles();
            if (f != null) {
                for (int i = 0; i < f.length; i++) {
                    copyToDir(f[i], toDirDir, replace, onlyIfNewer);
                }
            } else {
                throw new IOException("Failed to list " + from.getAbsolutePath());
            }
        } else if (from.isFile()) {
            copy(from, new File(toDir, from.getName()), onlyIfNewer);
        } else {
            throw new IOException(from.getAbsolutePath() + " is not a plain file or directory.");
        }
    }

    /**
     * Return an empty string when null passed, otherwise return the string.
     * 
     * @param string string or null
     * @return string or empty string
     */
    public static String emptyWhenNull(String string) {
        return string == null ? "" : string;
    }

    /**
     * Turn a constant name into an english like phrase. E.g. <i>HTTP_ERROR</i>
     * would be turned into <i>Http Error</i>.
     * 
     * @param constant constant name
     * @return readable name
     */
    public static String makeConstantReadable(String constant) {
        StringBuffer buf = new StringBuffer();
        char ch;
        boolean firstChar = true;
        for (int i = 0; i < constant.length(); i++) {
            ch = constant.charAt(i);
            if (ch == '_') {
                ch = ' ';
                firstChar = true;
            } else {
                if (firstChar) {
                    ch = Character.toUpperCase(ch);
                    firstChar = false;
                } else {
                    ch = Character.toLowerCase(ch);
                }
            }
            buf.append(ch);
        }
        return buf.toString();
    }

    /**
     * Turn a key into an english like phrase. E.g. <i>webForwardURL</i>
     * would be turned into <i>Web Forward URL</i>.
     * 
     * @param constant constant name
     * @return readable name
     */
    public static String makeKeyReadable(String constant) {
        // vFSPath
        StringBuffer buf = new StringBuffer();
        char ch;
        char lastChar = 0;
        for (int i = 0; i < constant.length(); i++) {
            ch = constant.charAt(i);
            if(i == 0) {
                ch = Character.toUpperCase(ch);   
            }
            else {
                if(Character.isUpperCase(ch)) {
                    if(!Character.isUpperCase(lastChar)) {
                        buf.append(" ");
                    }
                }
            }
            buf.append(ch);
            lastChar = ch;
        }
        return buf.toString();
    }

    /**
     * Turn a constant like name into an key like structure. E.g. <i>HTTP_ERROR</i>
     * would be turned into <i>httpError</i>.
     * 
     * @param constant constant
     * @return key
     */
    public static String makeConstantKey(String constant) {
        StringBuffer buf = new StringBuffer();
        char ch;
        boolean firstChar = false;
        for (int i = 0; i < constant.length(); i++) {
            ch = constant.charAt(i);
            if (ch == '_') {
                firstChar = true;
            } else {
                if (firstChar) {
                    ch = Character.toUpperCase(ch);
                    firstChar = false;
                } else {
                    ch = Character.toLowerCase(ch);
                }
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    /**
     * Re-process the case of a space separated string of words. The first
     * character is capitalised, all others or lower cased.
     * 
     * @param unCased uncased string
     * @return cased string
     */
    public static String reCase(String unCased) {
        StringBuffer buf = new StringBuffer();
        char ch;
        boolean wordNext = false;
        for (int i = 0; i < unCased.length(); i++) {
            ch = unCased.charAt(i);
            if (ch == ' ') {
                wordNext = true;
            } else {
                if (wordNext) {
                    ch = Character.toUpperCase(ch);
                    wordNext = false;
                } else {
                    ch = Character.toLowerCase(ch);
                }
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    /**
     * Read from the provided input stream until the buffer is full, blocking if
     * there are no bytes available until there are.
     * 
     * @param in input stream
     * @param buf buffer to read into
     * @return bytes read
     * @throws IOException on any error
     */
    public static int readFullyIntoBuffer(InputStream in, byte[] buf) throws IOException {
        int read;
        while ((read = in.read(buf)) > -1) {
            ;
        }
        return read;
    }

    /**
     * Parse a simple pattern into a regular expression. Simple expressions
     * simply support '*' and '?' for 'any characters' and 'single character'.
     * By simplifying of the pattern may be defeated by starting the string with
     * a '#' or any exact matched may be specified by starting the string with a
     * '='.
     * 
     * @param simplePattern simple pattern
     * @return regular expression pattern
     * 
     */
    public static String parseSimplePatternToRegExp(String simplePattern) {
        if (simplePattern.startsWith("#")) {
            simplePattern = simplePattern.substring(1);
        } else if (simplePattern.startsWith("=")) {
            simplePattern = "^" + Util.simplePatternToRegExp(simplePattern.substring(1)) + "$";
        } else {
            simplePattern = "^" + Util.simplePatternToRegExp(simplePattern)
                            + (simplePattern.indexOf('*') == -1 && simplePattern.indexOf('?') == -1 ? ".*" : "") + "$";
        }
        return simplePattern;
    }

    /*
     * Convert a simple pattern into a regular expression. Simple expressions
     * simply support '*' and '?' for 'any characters' and 'single character'.
     * 
     * @param simplePattern simple pattern @return regular expression pattern
     */
    static String simplePatternToRegExp(String simplePattern) {
        int c = simplePattern.length();
        char ch;
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < c; i++) {
            ch = simplePattern.charAt(i);
            if (Character.isLetterOrDigit(ch)) {
                buf.append(ch);
            } else if (ch == '*') {
                buf.append(".*");
            } else if (ch == '?') {
                buf.append(".?");
            } else {
                buf.append("\\");
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    /**
     * Get the class path from a class loader (must be an instance
     * of a {@link java.net.URLClassLoader}.
     * 
     * @param classLoader
     * @return class path
     */
    public static String getClassPath(ClassLoader classLoader) {
        StringBuffer buf = new StringBuffer();
        if(classLoader instanceof URLClassLoader) {
            URLClassLoader urlc = (URLClassLoader)classLoader;
            URL[] urls = urlc.getURLs();
            for(int i = 0 ; i < urls.length; i++) {
                if(urls[i].getProtocol().equals("file")) {
                    File f = new File(Util.urlDecode(urls[i].getPath()));
                    if(buf.length() > 0) {
                        buf.append(File.pathSeparator);
                    }
                    buf.append(f.getPath());
                }
            }
        }
        return buf.toString();
    }

    /**
     * Append a string to another string inserting a comma if the original
     * string is not blank. If the original string is <code>null</code> it will be
     * treated as if it were blank. The value to append cannot be <code>null</code>.
     * 
     * @param original original string 
     * @param value string to append 
     * @return new string
     */
    public static String appendToCommaSeparatedList(String original, String value) {
        if(original == null || original.equals("")) {
            return value;
        }
        return original + "," + value;
    }

    /**
     * Append a string to the value of a system property insert a comma
     * if the original value is not blank. If the current value of the 
     * original string is <code>null</code> it will be
     * treated as if it were blank. The value to append cannot be <code>null</code>.
     * 
     * @param systemPropertyName system property name to append 
     * @param value string to append 
     */
    public static void appendToCommaSeparatedSystemProperty(String systemPropertyName, String value) {
        System.setProperty(systemPropertyName, appendToCommaSeparatedList(SystemProperties.get(systemPropertyName), value));
    }

    /**
     * Get the simple name of a class.
     * 
     * @param cls class
     * @return simple name
     */
    public static String getSimpleClassName(Class cls) {
        int idx = cls.getName().lastIndexOf(".");
        return idx == -1 ? cls.getName() : cls.getName().substring(idx + 1);
    }

    /**
     * Split a string into an array taking into account delimiters, quotes and
     * escapes
     * 
     * @param str string to split
     * @param delim delimiter
     * @param quote quote character
     * @param escape escape character
     * @return array
     */
    
    public static String[] splitString(String str, char delim, char quote, char escape) {
        Vector v = new Vector();
        StringBuffer str1 = new StringBuffer();
        char ch = ' ';
        boolean inQuote = false;
        boolean escaped = false;
    
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
    
            if ((escape != -1) && (ch == escape) && !escaped) {
                escaped = true;
            } else {
                if ((quote != -1) && (ch == quote) && !escaped) {
                    inQuote = !inQuote;
                } else if (!inQuote && (ch == delim && !escaped)) {
                    v.addElement(str1.toString());
                    str1.setLength(0);
                } else {
                    str1.append(ch);
                }
                if (escaped) {
                    escaped = false;
                }
            }
        }
    
        if (str.length() > 0) {
            v.addElement(str1.toString());
    
        }
        String[] array;
        array = new String[v.size()];
        v.copyInto(array);
    
        return array;
    }

    /**
     * Escape a string suitable for RFC2253.
     * 
     * @param string
     * @return escaped string
     */
    public static String escapeForDNString(String string) {
        char ch;
        StringBuffer b = new StringBuffer(string.length());
        for(int i = 0 ; i < string.length(); i++) {
            ch = string.charAt(i);
            if(ch == ',' || ch == '+' || ch == '"' || ch == '<' || ch == '>' || ch == ';' || ch == '.') {
                b.append("\\");
                b.append(Integer.toHexString(ch));
            }
            else {
                b.append(ch);
            }
        }
        return b.toString();
    }

    
    public static boolean checkVersion(String actual, String required) {

        int[] applicationVersion = getVersion(actual);
        int[] installedJREVersion = getVersion(required);

        for (int i = 0; i < applicationVersion.length && i < installedJREVersion.length; i++) {
            if (applicationVersion[i] < installedJREVersion[i])
                return false;
        }

        return true;
    }

    public static int[] getVersion(String version) {

        int idx = 0;
        int pos = 0;
        int[] result = new int[0];
        do {

            idx = version.indexOf('.', pos);
            int v;
            if (idx > -1) {
                v = Integer.parseInt(version.substring(pos, idx));
                pos = idx + 1;
            } else {
                try {
                    int sub = version.indexOf('_', pos);
                    if (sub == -1) {
                        sub = version.indexOf('-', pos);
                    }
                    if (sub > -1) {
                        v = Integer.parseInt(version.substring(pos, sub));
                    } else {
                        v = Integer.parseInt(version.substring(pos));
                    }
                } catch (NumberFormatException ex) {
                    // Ignore the exception and return what version we have
                    break;
                }
            }
            int[] tmp = new int[result.length + 1];
            System.arraycopy(result, 0, tmp, 0, result.length);
            tmp[tmp.length - 1] = v;
            result = tmp;

        } while (idx > -1);

        return result;
    }

    public static String escapeForRegexpReplacement(String repl) {
        StringBuffer buf = new StringBuffer(repl.length());
        char ch;
        int len = repl.length();
        for(int i = 0 ; i < len; i++) {
            ch = repl.charAt(i);
            if(ch == '\\') {
                buf.append(ch);
            }
            else if(ch == '$') {
                buf.append('\\');
            }
            buf.append(ch);
        }
        return buf.toString();
    }    
    
    /**
     * Get a day of week constant suitable for use with {@link Calendar}
     * given an english day day. This may be any case and only the first
     * 3 characters are tested for (i.e. sun, mon, tue, etc ..)
     * 
     * @param text
     * @return day of week
     */
    public static int getDayOfWeekForText(String text) {
        text = text.toLowerCase();
        if(text.startsWith("sun")) {
            return Calendar.SUNDAY;
        }
        else if(text.startsWith("mon")) {
            return Calendar.MONDAY;
        }
        else if(text.startsWith("tue")) {
            return Calendar.TUESDAY;
        }
        else if(text.startsWith("wed")) {
            return Calendar.WEDNESDAY;
        }
        else if(text.startsWith("thu")) {
            return Calendar.THURSDAY;
        }
        else if(text.startsWith("fri")) {
            return Calendar.FRIDAY;
        }
        else if(text.startsWith("sat")) {
            return Calendar.SATURDAY;
        }
        return 0;
    }

    /**
     * Set a value in the system.properties file without destroying the
     * format. Note, be careful with this, its pretty simple at the moment
     * and doesn't encode property values.
     * 
     * @param key key (required)
     * @param value value or <code>null</code> to comment
     * @param comment comment to set for new value or <code>null</code> to omit
     * @throws IOException on any error
     */
    public static void setSystemProperty(String key, String value, String comment) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        File f = new File(ContextHolder.getContext().getConfDirectory(), "system.properties");
        File tf = new File(ContextHolder.getContext().getConfDirectory(), "system.properties.tmp");
        File of = new File(ContextHolder.getContext().getConfDirectory(), "system.properties.old");
        try {
            in = new FileInputStream(f);
            out = new FileOutputStream(tf);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            PrintWriter pw = new PrintWriter(out);
            String line = null;
            boolean found = false;
            while( ( line = br.readLine() ) != null ) {
                if(found) {
                    pw.println(line);
                }
                else {
                    String trimLine = Util.trimBoth(line);
                    boolean commented = false;
                    int idx = 0;
                    while(idx < trimLine.length() && trimLine.charAt(idx) == '#') {
                        commented = true;
                        idx++;
                    }
                    String tVal = trimLine.substring(idx);
                    if(tVal.startsWith(key + "=")) {
                        found = true;
                        if(commented) {
                            if(value == null) {
                            //  leave alone
                            }
                            else {
                                // set value
                                pw.println(key + "=" + value);
                            }
                        }
                        else {
                            if(value == null) {
                                // comment
                                pw.println("#" + line);
                            }
                            else {
                                // set value
                                pw.println(key + "=" + value);
                            }
                        }
                    }
                    else {
                        pw.println(line);
                    }
                    trimLine.startsWith("#");
                }
                
            }
            if(!found) {
                if(comment != null) {
                    pw.println();
                    pw.println(comment);
                }
                pw.println(key + "=" + value);
            }
            pw.flush();
            
        } finally {
            Util.closeStream(in);
            Util.closeStream(out);
        }
        
        // Move files
        if(of.exists() && !of.delete()) {
            log.warn("Failed to delete old backup system properties file");
        } 
        copy(f, of);
        copy(tf, f);
        if(!tf.delete()) {
            log.warn("Failed to delete temporary system properties file");
        } 
        

    }

    /**
     * Convert an array of objects to a comma separated string (using
     * each elements {@link Object#toString()} method.
     * 
     * @param elements
     * @return as comma separated string
     */
    public static String arrayToCommaSeparatedList(Object[] elements) {
        StringBuffer buf = new StringBuffer();
        for(int i = 0 ; i < elements.length; i++) {
            if(i > 0) 
                buf.append(",");
            buf.append(elements[i].toString());
        }
        return buf.toString();
    }
    
    /**
     * @param csv
     * @return
     */
    public static Map<String, String> toMap(String csv) {
        return toMap(csv, ",");
    }
    
    /**
     * @param csv
     * @param delim 
     * @return
     */
    public static Map<String, String> toMap(String csv, String delim) {
        Map<String, String> values = new HashMap<String, String>();
        for (StringTokenizer tokenizer = new StringTokenizer(csv, delim); tokenizer.hasMoreTokens();) {
            String token = tokenizer.nextToken();
            int indexOfEquals = token.indexOf('=');
            int indexAfterEquals = indexOfEquals + 1;
            
            if (indexOfEquals != -1 && indexAfterEquals < token.length()) {
                String key = token.substring(0, indexOfEquals);
                String value = token.substring(indexAfterEquals, token.length());
                values.put(key, value);
            }
        }
        return values;
    }

    /**
     * Test if a string is <code>null</code> if it is an
     * empty string when trimmed.
     * 
     * @param string
     * @return null or trimmed blank string
     */
    public static boolean isNullOrTrimmedBlank(String string) {
        return string == null || string.trim().length() == 0;
    }

    /**
     * Return a trimmed string regardless of whether the
     * source string is <code>null</code> (in which case an
     * empty string will be retuned). 
     * 
     * @param string
     * @return trimmed or blank string
     */
    public static String trimmedOrBlank(String string) {
        return string == null ? "" : string.trim();
    }

    /**
     * Return a trimmed string (both ends) regardless of whether the
     * source string is <code>null</code> (in which case an
     * empty string will be retuned). 
     * 
     * @param string
     * @return trimmed or blank string
     */
    public static String trimmedBothOrBlank(String string) {
        return trimBoth(string == null ? "" : string.trim());
    }

	/**
	 * Attempt to make a file executable. Only current works on
	 * systems that have the <b>chmod</b> command available.
	 * 
	 * @param binLocation
	 * @throws IOException on any error
	 */
	public static void makeExecutable(File binLocation) throws IOException {
		Process p = Runtime.getRuntime().exec(new String[] { "chmod", "ug+rx", binLocation.getAbsolutePath() });
		try {
			copy(p.getErrorStream(), new ByteArrayOutputStream());
		}
		finally {
			try {
				if(p.waitFor() != 0) {
					throw new IOException("Failed to set execute permission. Return code " + p.exitValue() + ".");
				}
			} catch (InterruptedException e) {
			}
		}
		
	}
}