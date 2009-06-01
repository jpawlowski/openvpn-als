
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
			
package com.maverick.ssl.https;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;

import com.maverick.http.AuthenticationCancelledException;
import com.maverick.http.ConnectMethod;
import com.maverick.http.HttpClient;
import com.maverick.http.HttpException;
import com.maverick.http.HttpMethod;
import com.maverick.http.HttpResponse;
import com.maverick.http.PasswordCredentials;
import com.maverick.http.UnsupportedAuthenticationException;
import com.maverick.ssl.SSLException;
import com.maverick.ssl.SSLIOException;
import com.maverick.ssl.SSLSocket;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class HttpsURLConnection extends HttpURLConnection {

    boolean debugging;
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    Vector requestKeys, requestValues;
    Vector headers = new Vector(), headerKeys = new Vector();
    Hashtable headerValues = new Hashtable();
    Socket socket;
    PushbackInputStream input;
    int responseCode = -1;
    String responseMessage;

    /*
     * public static final String httpsProxyHostPropertyName =
     * "ssl.https.proxy.host";
     * 
     * public static final String httpsProxyPortPropertyName =
     * "ssl.https.proxy.port";
     */

    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(HttpsURLConnection.class);
    // #endif

    public static final String httpProxyHostProperty = "com.maverick.ssl.https.HTTPProxyHostname"; //$NON-NLS-1$
    public static final String httpProxyPortProperty = "com.maverick.ssl.https.HTTPProxyPort"; //$NON-NLS-1$
    public static final String httpProxyUsernameProperty = "com.maverick.ssl.https.HTTPProxyUsername"; //$NON-NLS-1$
    public static final String httpProxyPasswordProperty = "com.maverick.ssl.https.HTTPProxyPassword"; //$NON-NLS-1$
    public static final String httpProxySecureProperty = "com.maverick.ssl.https.HTTPProxySecure"; //$NON-NLS-1$
    public static final String httpProxyNonProxyHostsProperty = "com.maverick.ssl.https.HTTPProxyNonProxyHosts"; //$NON-NLS-1$

    static Vector defaultRequestKeys = new Vector(), defaultRequestValues = new Vector();

    static {
        defaultRequestKeys.addElement("User-agent"); //$NON-NLS-1$
        defaultRequestValues.addElement(HttpClient.USER_AGENT);
    }

    public HttpsURLConnection(URL url) {
        super(url);
        synchronized (defaultRequestKeys) {
            requestKeys = (Vector) defaultRequestKeys.clone();
            requestValues = (Vector) defaultRequestValues.clone();
        }
    }

    public void addRequestProperty(String name, String value) {
        setRequestProperty(name, value);
    }

    public static void setDefaultRequestProperty(String key, String value) {
        synchronized (defaultRequestKeys) {
            int i = 0;
            while ((i < defaultRequestKeys.size()) && !(key.equalsIgnoreCase((String) defaultRequestKeys.elementAt(i)))) {
                ++i;
            }
            if (i < defaultRequestKeys.size()) {
                defaultRequestValues.removeElementAt(i);
                defaultRequestKeys.removeElementAt(i);
            }
            if (value != null) {
                defaultRequestValues.addElement(value);
                defaultRequestKeys.addElement(key);
            }
        }
    }

    public static String getDefaultRequestProperty(String key) {
        synchronized (defaultRequestKeys) {
            int i = 0;
            while ((i < defaultRequestKeys.size()) && !(key.equalsIgnoreCase((String) defaultRequestKeys.elementAt(i)))) {
                ++i;
            }
            if (i < defaultRequestKeys.size()) {
                return (String) defaultRequestValues.elementAt(i);
            } else {
                return null;
            }
        }
    }

    public synchronized void setRequestProperty(String key, String value) {
        if (connected) {
            throw new IllegalStateException(Messages.getString("HttpsURLConnection.alreadyConnected")); //$NON-NLS-1$
        }
        int i = 0;
        while ((i < requestKeys.size()) && !(key.equalsIgnoreCase((String) requestKeys.elementAt(i)))) {
            ++i;
        }
        if (i < requestKeys.size()) {
            requestValues.removeElementAt(i);
            requestKeys.removeElementAt(i);
        }
        if (value != null) {
            requestValues.addElement(value);
            requestKeys.addElement(key);
        }
    }

    public String getRequestProperty(String key) {
        int i = 0;
        while ((i < requestKeys.size()) && !(key.equalsIgnoreCase((String) requestKeys.elementAt(i)))) {
            ++i;
        }
        if (i < requestKeys.size()) {
            return (String) requestValues.elementAt(i);
        } else {
            return null;
        }
    }

    public OutputStream getOutputStream() throws IOException {
        if (!doOutput) {
            throw new IOException(Messages.getString("HttpsURLConnection.protocolOutputNotConfigured")); //$NON-NLS-1$
        }
        if (connected) {
            throw new IOException(Messages.getString("HttpsURLConnection.alreadyConnected")); //$NON-NLS-1$
        }
        return output;
    }

    private boolean isNonProxiedHost(String host) {
        String nonProxiedHosts = System.getProperty(httpProxyNonProxyHostsProperty);
        if (nonProxiedHosts == null || nonProxiedHosts.equals("")) { //$NON-NLS-1$
            return false;
        }
        StringTokenizer t = new StringTokenizer(nonProxiedHosts, "|"); //$NON-NLS-1$
        // TODO add wildcard logic like the sun implementation
        while (t.hasMoreTokens()) {
            if (host.equalsIgnoreCase(t.nextToken())) {
                return true;
            }
        }
        return false;
    }

    public synchronized void connect() throws IOException {

        if (!connected) {

            // #ifdef DEBUG
            log.info(MessageFormat.format(Messages.getString("HttpsURLConnection.connecting"), new Object[] { url.getHost(), new Integer(url.getPort() == -1 ? 443 : url.getPort()) })); //$NON-NLS-1$
            // #endif

            String proxyHost = System.getProperty(httpProxyHostProperty);
            if (proxyHost != null && !isNonProxiedHost(url.getHost())) {

                boolean isSecure = Boolean.valueOf(System.getProperty(httpProxySecureProperty, "true")).booleanValue(); //$NON-NLS-1$
                String proxyPort = System.getProperty(httpProxyPortProperty);
                String proxyUsername = System.getProperty(httpProxyUsernameProperty);
                String proxyPassword = System.getProperty(httpProxyPasswordProperty);

                // #ifdef DEBUG
                log.info(MessageFormat.format(Messages.getString("HttpsURLConnection.requiresProxyConnection"), new Object[] { isSecure ? "https" : "http://", proxyHost, new Integer(proxyPort) })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                log.info(MessageFormat.format(Messages.getString("HttpsURLConnection.proxyUsername"), new Object[] { proxyUsername == null || proxyUsername.equals("") ? "not set" : proxyUsername })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                // #endif
                if (proxyPort == null) {
                    throw new IOException(Messages.getString("HttpsURLConnection.noProxyPort")); //$NON-NLS-1$
                }

                try {
                    int port = Integer.parseInt(proxyPort);

                    HttpClient client = new HttpClient(proxyHost, port, isSecure);
                    HttpMethod method = new ConnectMethod(url.getHost(), url.getPort() == -1 ? 443 : url.getPort(), true);

                    PasswordCredentials credentials = new PasswordCredentials();
                    credentials.setUsername(proxyUsername);
                    credentials.setPassword(proxyPassword);

                    client.setCredentials(credentials);

                    HttpResponse response = client.execute(method);
                    socket = response.getConnection().getSocket();
                } catch (HttpException ex) {
                    // #ifdef DEBUG
                    log.info(MessageFormat.format(Messages.getString("HttpsURLConnection.proxyConnectionFailed"), new Object[] { ex.getMessage(), new Integer(ex.getStatus()) })); //$NON-NLS-1$
                    // #endif
                    throw new IOException(MessageFormat.format(Messages.getString("HttpsURLConnection.proxyConnectionFailed"), new Object[] { ex.getMessage(), new Integer(ex.getStatus()) })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                } catch (UnsupportedAuthenticationException ex) {
                    // #ifdef DEBUG
                    log.info(Messages.getString("HttpsURLConnection.proxyAuthenticationFailed"), ex); //$NON-NLS-1$
                    // #endif
                    throw new IOException(ex.getMessage());
                } catch (AuthenticationCancelledException ex) {
                    throw new IOException(Messages.getString("HttpsURLConnection.userCancelledAuthenitcation")); //$NON-NLS-1$
                }
            } else {
                String host = url.getHost();
                if (host == null) {
                    throw new IOException(Messages.getString("HttpsURLConnection.noHost")); //$NON-NLS-1$
                }
                int port = url.getPort();
                try {
                    socket = new SSLSocket(host, port == -1 ? 443 : port);
                } catch (SSLException ex1) {
                    throw new SSLIOException(ex1);
                }
            }

            try {
                writeRequest(socket.getOutputStream());
                readResponse(input = new PushbackInputStream(socket.getInputStream(), 2048));
            } catch (IOException ex) {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
                throw ex;
            }
            connected = true;
        }
    }

    void writeRequest(OutputStream out) throws IOException {
        DataOutputStream data = new DataOutputStream(new BufferedOutputStream(out));
        if ((doOutput) && (output == null)) {
            throw new IOException(Messages.getString("HttpsURLConnection.noPOSTData")); //$NON-NLS-1$
        }
        if (ifModifiedSince != 0) {
            Date date = new Date(ifModifiedSince);
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss z"); //$NON-NLS-1$
            formatter.setTimeZone(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
            setRequestProperty("If-Modified-Since", formatter.format(date)); //$NON-NLS-1$
        }
        if (doOutput) {
            setRequestProperty("Content-length", "" + output.size()); //$NON-NLS-1$ //$NON-NLS-2$
        }

        data.writeBytes((doOutput ? "POST" : "GET") + " " + (url.getFile().equals("") ? "/" : url.getFile()) + " HTTP/1.0\r\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

        for (int i = 0; i < requestKeys.size(); ++i) {
            String key = (String) requestKeys.elementAt(i);
            if (!key.startsWith("Proxy-")) { //$NON-NLS-1$
                data.writeBytes(key + ": " + requestValues.elementAt(i) + "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        data.writeBytes("\r\n"); //$NON-NLS-1$
        data.flush();
        if (doOutput) {
            output.writeTo(out);
        }
        out.flush();
    }

    void readResponse(PushbackInputStream in) throws IOException {
        DataInputStream data = new DataInputStream(in);
        String line;
        while (((line = data.readLine()) != null) && (line.length() > 0)) {
            headers.addElement(line);
            int index = line.indexOf(':');
            if (index >= 0) {
                String key = line.substring(0, index);
                String value = line.substring(index + 1).trim();
                headerKeys.addElement(key);
                headerValues.put(key.toLowerCase(), value);
            } else {
                // If the first line back is not a header, the unread as the
                // rest is going to be content
                if (headerValues.size() == 0) {

                    // This is a response code
                    if (line.startsWith("HTTP/")) { //$NON-NLS-1$
                        try {
                            int idx = line.indexOf(' ');
                            while (line.charAt(++idx) == ' ') {
                                ;
                            }
                            responseMessage = line.substring(idx + 4);
                            responseCode = Integer.parseInt(line.substring(idx, idx + 3));
                        } catch (Throwable t) {
                            responseCode = 200;
                        }
                    } else {
                        // Just content
                        responseCode = 200;
                        byte[] unread = line.getBytes();
                        in.unread(unread);
                        break;
                    }

                }
            }
        }
    }

    public int getResponseCode() {
        if (!connected) {
            throw new IllegalStateException(Messages.getString("HttpsURLConnection.notConnected")); //$NON-NLS-1$
        }
        // if (responseCode == -1) {
        // responseCode = 200; // Its possible that there wont be a response
        // code
        // if(headers.size() > 0) {
        // String response = getHeaderField(0);
        // System.out.println("!!!!! REMOVE ME
        // maverick/src/com/maverick/ssl/https/HttpsURLConnection.getResponseCode()
        // - response = " + response);
        // int index = response.indexOf(' ');
        // while (response.charAt(++index) == ' ') {
        // ;
        // }
        // responseMessage = response.substring(index + 4);
        // try {
        // responseCode = Integer.parseInt(response.substring(index, index +
        // 3));
        // }
        // catch(NumberFormatException nfe) {
        // // ????
        // }
        // }
        // }
        return responseCode;
    }

    public String getResponseMessage() {
        if (!connected) {
            throw new IllegalStateException(Messages.getString("HttpsURLConnection.notConnected")); //$NON-NLS-1$
        }
        getResponseCode();
        return responseMessage;
    }

    public String getHeaderField(String name) {
        if (!connected) {
            throw new IllegalStateException(Messages.getString("HttpsURLConnection.notConnected")); //$NON-NLS-1$
        }
        return (String) headerValues.get(name.toLowerCase());
    }

    public String getHeaderFieldKey(int n) {
        if (!connected) {
            throw new IllegalStateException(Messages.getString("HttpsURLConnection.notConnected")); //$NON-NLS-1$
        }
        if (n < headerKeys.size()) {
            return (String) headerKeys.elementAt(n);
        } else {
            return null;
        }
    }

    public String getHeaderField(int n) {
        if (!connected) {
            throw new IllegalStateException(Messages.getString("HttpsURLConnection.notConnected")); //$NON-NLS-1$
        }
        if (n < headers.size()) {
            return (String) headers.elementAt(n);
        } else {
            return null;
        }
    }

    public InputStream getInputStream() throws IOException {
        if (!doInput) {
            throw new IOException(Messages.getString("HttpsURLConnection.protocolInputNotConfigured")); //$NON-NLS-1$
        }
        connect();
        return input;
    }

    public Socket getSocket() {
        if (!connected) {
            throw new IllegalStateException(Messages.getString("HttpsURLConnection.notConnected")); //$NON-NLS-1$
        }
        return socket;
    }

    public void disconnect() {

    }

    public boolean usingProxy() {
        return System.getProperty(httpProxyHostProperty) != null;
    }
}
