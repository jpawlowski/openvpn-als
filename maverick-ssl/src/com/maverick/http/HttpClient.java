
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
			
package com.maverick.http;

import java.io.EOFException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class HttpClient {

    static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
	static final String AUTHORIZATION = "Authorization";
	static final String WWW_AUTHENTICATE = "WWW-Authenticate";
	static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";
	
	public static final int PROXY_NONE = 0;
    public static final int PROXY_HTTP = 1;
    public static final int PROXY_HTTPS = 2;

    public static String USER_AGENT = "Maverick-HttpClient/1.0"; //$NON-NLS-1$

    /**
     * The target server
     */
    String hostname;
    int port;
    boolean isSecure;
    PasswordCredentials credentials;
    AuthenticationPrompt prompt;
    String preferedAuthentication = HttpAuthenticatorFactory.BASIC;
    boolean preemptiveAuthentication = false;
    HttpConnectionManager connections = new HttpConnectionManager(this);
    boolean includeCookies = true;
    Vector cookies = new Vector();
    int maxAuthenticationAttempts = 5;
    int proxyMaxAuthenticationAttempts = 5;
    boolean proxyPreemptiveAuthentication = false;
    HttpClient proxyClient;
    boolean credentialsFailed = false;

    /**
     * Proxy information
     */
    String proxyHost;
    int proxyPort = -1;
    int proxyType = PROXY_NONE;

    PasswordCredentials proxyCredentials;
    AuthenticationPrompt proxyAuthenticationPrompt;
    String proxyPreferedAuthentication = HttpAuthenticatorFactory.BASIC;
    boolean isProxyClient = false;

    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(HttpClient.class);

    // #endif

    public HttpClient(String hostname, int port, boolean isSecure) {
        this.hostname = hostname;
        this.port = port;
        this.isSecure = isSecure;

    }

    /**
     * Set the default user agent.
     * 
     * @param userAgent default user agent
     */
    public static void setUserAgent(String userAgent) {
        USER_AGENT = userAgent;
    }

    HttpClient(HttpClient client) {
        this(client.proxyHost, client.proxyPort, client.proxyType == HttpClient.PROXY_HTTPS);
        setAuthenticationPrompt(client.proxyAuthenticationPrompt);
        setCredentials(client.proxyCredentials);
        setMaxAuthenticationAttempts(client.proxyMaxAuthenticationAttempts);
        setPreemtiveAuthentication(client.proxyPreemptiveAuthentication);
        setPreferredAuthentication(client.proxyPreferedAuthentication);
        this.isProxyClient = true;
    }

    boolean configureProxy() {
        /**
         * Attempt to automatically configure proxy from Maverick SSL proxy
         * settings
         */
        if (!isProxyClient && !isNonProxiedHost(hostname)) {
            if (System.getProperty("com.maverick.ssl.https.HTTPProxyHostname") != null) { //$NON-NLS-1$
                setProxyHost(System.getProperty("com.maverick.ssl.https.HTTPProxyHostname")); //$NON-NLS-1$
                // #ifdef DEBUG
                log.debug(MessageFormat.format(Messages.getString("HttpClient.setClientProxyHost"), new Object[] { proxyHost })); //$NON-NLS-1$
                // #endif

                if (System.getProperty("com.maverick.ssl.https.HTTPProxyPort") != null) { //$NON-NLS-1$
                    setProxyPort(Integer.parseInt(System.getProperty("com.maverick.ssl.https.HTTPProxyPort"))); //$NON-NLS-1$
                    // #ifdef DEBUG
                    log.debug(MessageFormat.format(Messages.getString("HttpClient.setClientProxyPort"), new Object[] { new Integer(proxyPort) })); //$NON-NLS-1$
                    // #endif
                } else
                    setProxyPort(80);

                if (System.getProperty("com.maverick.ssl.https.HTTPProxySecure") != null) { //$NON-NLS-1$
                    setProxyType(System.getProperty("com.maverick.ssl.https.HTTPProxySecure").equalsIgnoreCase("true") ? PROXY_HTTPS //$NON-NLS-1$ //$NON-NLS-2$
                        : PROXY_HTTP);
                } else
                    setProxyType(PROXY_HTTP);

                if (System.getProperty("com.maverick.ssl.https.HTTPProxyUsername") != null) { //$NON-NLS-1$
                	setProxyCredentials(new PasswordCredentials(System.getProperty("com.maverick.ssl.https.HTTPProxyUsername"),
                			System.getProperty("com.maverick.ssl.https.HTTPProxyPassword")==null ? "" : System.getProperty("com.maverick.ssl.https.HTTPProxyPassword")));
                } 
                
                return true;
            }
        }

        return false;
    }

    public boolean isProxyConfigured() {
        if (proxyType == PROXY_NONE)
            return configureProxy();
        else
            return true;
    }

    public static boolean isNonProxiedHost(String host) {
        String nonProxiedHosts = System.getProperty("com.maverick.ssl.https.HTTPProxyNonProxyHosts"); //$NON-NLS-1$
        if (nonProxiedHosts == null || nonProxiedHosts.equals("")) { //$NON-NLS-1$
            return false;
        }
        StringTokenizer t = new StringTokenizer(nonProxiedHosts, "|"); //$NON-NLS-1$
        while (t.hasMoreTokens()) {
            String token = t.nextToken();
            int idx = token.indexOf('*');
            if (idx != -1) {
                if (token.length() == 1) {
                    return true;
                }
                String before = token.substring(0, idx);
                String after = token.substring(idx + 1);
                if (((before.length() == 0) || host.startsWith(before)) && ((after.length() == 0) || host.endsWith(after))) {
                    return true;
                }
            } else {
                if (host.equalsIgnoreCase(token)) {
                    return true;
                }
            }
        }
        return false;
    }

    public HttpConnectionManager getConnectionManager() {
        return connections;
    }

    public String getHost() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public boolean isSecure() {
        return isSecure;
    }

    public void setMaxAuthenticationAttempts(int maxAuthenticationAttempts) {
        this.maxAuthenticationAttempts = maxAuthenticationAttempts;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setProxyMaxAuthenticationAttempts(int proxyMaxAuthenticationAttempts) {
        this.proxyMaxAuthenticationAttempts = proxyMaxAuthenticationAttempts;
    }

    public void setProxyType(int proxyType) {
        if (proxyType > PROXY_HTTPS || proxyType < PROXY_NONE) {
            throw new IllegalArgumentException(MessageFormat.format(Messages.getString("HttpClient.notValidProxyType"), new Object[] { new Integer(proxyType) })); //$NON-NLS-1$
        }
        this.proxyType = proxyType;
    }

    public void setProxyCredentials(PasswordCredentials proxyCredentials) {
        this.proxyCredentials = proxyCredentials;
    }

    public void setProxyPreemptiveAuthentication(boolean proxyPreemptiveAuthentication) {
        this.proxyPreemptiveAuthentication = proxyPreemptiveAuthentication;
    }

    public void setProxyAuthenticationPrompt(AuthenticationPrompt proxyAuthenticationPrompt) {
        this.proxyAuthenticationPrompt = proxyAuthenticationPrompt;
    }

    public void setProxyPreferedAuthentication(String scheme) {
        this.proxyPreferedAuthentication = scheme;
    }

    public void setAuthenticationPrompt(AuthenticationPrompt prompt) {
        this.prompt = prompt;
    }

    public void setPreferredAuthentication(String scheme) {
        this.preferedAuthentication = scheme;
    }

    public void setCredentials(PasswordCredentials credentials) {
        this.credentials = credentials;
        credentialsFailed = false;
    }

    public void close() {
        connections.closeConnections();
    }

    synchronized void prepareRequest(HttpRequest request, HttpMethod method, HttpConnection con) throws IOException, HttpException,
                    UnsupportedAuthenticationException, AuthenticationCancelledException {

        request.reset();

        if (preemptiveAuthentication && credentials != null) {
            // Discard old authenticator and create a new one
            BasicAuthentication authenticator = new BasicAuthentication(method.getURI(), con.getHost(), con.getPort(), con.isSecure());
            authenticator.setCredentials(credentials);
            authenticator.setConnection(con);
            authenticator.setChallenge("Basic"); //$NON-NLS-1$
            authenticator.setAuthenicationHeader(method.getName().equals("CONNECT") ? PROXY_AUTHENTICATE : WWW_AUTHENTICATE); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            authenticator.setAuthorizationHeader(method.getName().equals("CONNECT") ? PROXY_AUTHORIZATION : AUTHORIZATION); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            con.setAuthenticator(authenticator);
        }

        connections.checkConnection(con);

        if (includeCookies) {
            String cookiesHeader = ""; //$NON-NLS-1$
            for (Enumeration e = cookies.elements(); e.hasMoreElements();) {
                Cookie cookie = (Cookie) e.nextElement();

                // Evaluate whether the cookie should be included
                Date now = new Date();
                if (cookie.getExpires() == null || cookie.expires.after(now)) {
                    if (method.getURI().startsWith(cookie.getPath()) && getHost().endsWith(cookie.getDomain())
                        && (cookie.isSecure() == isSecure || !cookie.isSecure())) {
                        cookiesHeader += cookie + "; "; //$NON-NLS-1$
                    }
                }
            }

            if (!cookiesHeader.equals("")) { //$NON-NLS-1$
                request.setHeaderField("Cookie", cookiesHeader); //$NON-NLS-1$
            }
        }

        if (con.isKeepAlive())
            request.setHeaderField("Connection", "Keep-Alive"); //$NON-NLS-1$ //$NON-NLS-2$

        if (con.getAuthenticator() != null && con.getAuthenticator().canAuthenticate()) {
            // #ifdef DEBUG
            log.debug(MessageFormat.format(Messages.getString("HttpClient.settingAuthCredentials"), new Object[] { con.getAuthenticator().getScheme() })); //$NON-NLS-1$
            // #endif
            try {
                request.removeFields(con.getAuthenticator().getAuthorizationHeader());
                con.getAuthenticator().authenticate(request, method);
            } catch (Exception ex) {
                // #ifdef DEBUG
                log.info(Messages.getString("HttpClient.authenticatorException"), ex); //$NON-NLS-1$
                // #endif
                con.setAuthenticator(null);
            }
        }

    }

    HttpResponse execute(HttpRequest request, HttpMethod method, HttpConnection con) throws IOException, HttpException,
                    UnsupportedAuthenticationException, AuthenticationCancelledException {

        prepareRequest(request, method, con);

        // #ifdef DEBUG
        log.info(MessageFormat.format(Messages.getString("HttpClient.executingMethod"), new Object[] { method.getName(), con.getHost() })); //$NON-NLS-1$
        // #endif

        return processResponse(request, method, con, method.execute(request, con));
    }

    HttpConnection executeAsync(HttpRequest request, AsyncHttpMethod method, HttpConnection con) throws IOException, HttpException,
                    UnsupportedAuthenticationException, AuthenticationCancelledException {

        prepareRequest(request, method, con);

        // #ifdef DEBUG
        log.info(MessageFormat.format(Messages.getString("HttpClient.executingMethod"), new Object[] { method.getName(), con.getHost() })); //$NON-NLS-1$
        // #endif

        method.executeAsync(request, con);

        return con;
    }

    synchronized HttpResponse processResponse(HttpRequest request, HttpMethod method, HttpConnection con, HttpResponse response)
                    throws IOException, HttpException, UnsupportedAuthenticationException, AuthenticationCancelledException {

    	
        // Process any cookies
        if (includeCookies) {
            String[] cookies = response.getHeaderFields("Set-Cookie"); //$NON-NLS-1$
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    this.cookies.addElement(new Cookie(cookies[i]));
                }
            }
        }

        try {
            /**
             * Now process the response to see if we need to handle
             * authentication
             */
            switch (response.getStatus()) {
                case 401:
                case 407:
                    if (con.getAuthenticator() != null) {
                        int success = con.getAuthenticator().processResponse(response);

                        switch (success) {
                            case HttpAuthenticator.AUTHENTICATION_COMPLETED:
                                // w00t we're in
                                return response;
                            case HttpAuthenticator.AUTHENTICATION_FAILED:
                                // Up the number of cycles and try again
                                request.cycles++;
                                credentialsFailed = true;
                                break;
                            case HttpAuthenticator.AUTHENTICATION_IN_PROGRESS:
                            default:
                                // Do nothing just keep on authenticating
                        }

                    } else if (credentials == null && prompt == null) {
                        return response;
                    }

                    if (con.getAuthenticator() == null || request.cycles < maxAuthenticationAttempts)
                        return doAuthentication(response.getStatus() == 401 ? WWW_AUTHENTICATE : PROXY_AUTHENTICATE, response //$NON-NLS-1$ //$NON-NLS-2$
                        .getStatus() == 401 ? AUTHORIZATION : PROXY_AUTHORIZATION, request, method, response, //$NON-NLS-1$ //$NON-NLS-2$
                            con);
                    else
                        return response;
                default:
                    // Set the authenticator as complete and return
                    if (con.getAuthenticator() != null) {
                        credentials = con.getAuthenticator().credentials;
                        con.getAuthenticator().complete();
                    }
                    return response;

            }
        } catch (UnsupportedAuthenticationException ex) {
            /**
             * We dont support this type of authentication so return the
             * response
             */
            return response;
        }
    }

    private HttpResponse doAuthentication(String authenticateHeader, String authorizationHeader, HttpRequest request,
                                          HttpMethod method, HttpResponse response, HttpConnection con) throws IOException,
                    HttpException, UnsupportedAuthenticationException, AuthenticationCancelledException {

        // Check for failed authentication limit
        if (credentialsFailed) {
            con.setAuthenticator(null);
            return response;
        }
        // If we're called we are disgaurding the previous response
        response.close(false);

        // Authorization required
        String[] challenges = response.getHeaderFields(authenticateHeader);

        if (challenges == null)
            return response;

        // #ifdef DEBUG
        for (int i = 0; i < challenges.length; i++) {
            log.info(MessageFormat.format(Messages.getString("HttpClient.requiresAuthType"), new Object[] { con.getHost(), HttpAuthenticatorFactory.getAuthenticationMethod(challenges[i]) }));//$NON-NLS-1$
        }
        // #endif

        /**
         * If we don't already have an authenticator we should create one
         */
        if (credentials == null && prompt == null && con.getAuthenticator() == null) {
            // We cannot authenticate as we do not have any credentials
            // or a prompt
            return response;
        }

        /**
         * If we got this far then we can authenticate so try to create an
         * appropriate authenticator
         */
        if (con.getAuthenticator() == null || !con.getAuthenticator().getURI().startsWith(method.getURI())) {
            con.setAuthenticator(HttpAuthenticatorFactory.createAuthenticator(con,
                challenges,
                authenticateHeader,
                authorizationHeader,
                preferedAuthentication,
                method.getURI()));
        }

        if (credentials != null) {
            // #ifdef DEBUG
            log.info(Messages.getString("HttpClient.settingUserCreds")); //$NON-NLS-1$
            // #endif
            con.getAuthenticator().setCredentials(credentials);
        } else if (prompt != null && con.getAuthenticator().wantsPrompt()) {
            // #ifdef DEBUG
            log.info(Messages.getString("HttpClient.promptingForCreds")); //$NON-NLS-1$
            // #endif
            if (!prompt.promptForCredentials(authenticateHeader.equals(PROXY_AUTHENTICATE), con.getAuthenticator())) {
                throw new AuthenticationCancelledException();
            }
        }

        if (!con.canReuse())
            con.reconnect();

        try {
            return execute(request, method, con);
        } catch (EOFException ex) {
            // This was possibly caused by the connection not being reusable
            // This will not work with NTLM
            con.reconnect();
            return execute(request, method, con);
        }
    }

    public HttpResponse execute(HttpMethod method) throws UnknownHostException, IOException, HttpException,
                    UnsupportedAuthenticationException, AuthenticationCancelledException {
        // #ifdef DEBUG
        log.debug(MessageFormat.format(Messages.getString("HttpClient.executing"), new Object[] { method.getName() })); //$NON-NLS-1$
        // #endif
        for (int i = 0; i < 2; i++) {
            try {
                return execute(method, connections.getConnection());
            } catch (EOFException eof) {
                // #ifdef DEBUG
                if (i != 1) {
                    log.warn(MessageFormat.format(Messages.getString("HttpClient.eof.attemptingAgain"), new Object[] { new Integer(i) })); //$NON-NLS-1$
                } else {
                    log.warn(MessageFormat.format(Messages.getString("HttpClient.eof.givingUp"), new Object[] { new Integer(i) })); //$NON-NLS-1$                    
                }
                // #endif
            }
        }
        throw new EOFException(Messages.getString("HttpClient.couldNotConnect")); //$NON-NLS-1$
    }
    
    public HttpResponse execute(HttpMethod method, HttpConnection con) throws UnknownHostException, IOException, HttpException,
    UnsupportedAuthenticationException, AuthenticationCancelledException {
    	return execute(new HttpRequest(), method, con);
    }

    public HttpConnection executeAsync(AsyncHttpMethod method) throws UnknownHostException, IOException, HttpException,
                    UnsupportedAuthenticationException, AuthenticationCancelledException {
        return executeAsync(method, connections.getConnection());
    }
    
    public HttpConnection executeAsync(AsyncHttpMethod method, HttpConnection con) throws UnknownHostException, IOException, HttpException,
    UnsupportedAuthenticationException, AuthenticationCancelledException {
        // #ifdef DEBUG
        log.debug(MessageFormat.format(Messages.getString("HttpClient.executing"), new Object[] { method.getName() })); //$NON-NLS-1$
        // #endif
    	return executeAsync(new HttpRequest(), method, connections.getConnection());
    }


    public void setIncludeCookies(boolean includeCookies) {
        this.includeCookies = includeCookies;
    }

    public void setPreemtiveAuthentication(boolean preemptiveAuthentication) {
        this.preemptiveAuthentication = preemptiveAuthentication;
    }

	public void removeAllCookies() {
		cookies.removeAllElements();		
	}
}
