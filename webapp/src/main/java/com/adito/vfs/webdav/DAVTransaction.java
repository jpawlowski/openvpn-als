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
package com.adito.vfs.webdav;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.crypto.encoders.Base64;
import com.adito.boot.HttpConstants;
import com.adito.boot.SystemProperties;
import com.adito.boot.Util;
import com.adito.core.ServletRequestAdapter;
import com.adito.core.ServletResponseAdapter;
import com.adito.core.UserDatabaseManager;
import com.adito.policyframework.LaunchSession;
import com.adito.policyframework.LaunchSessionFactory;
import com.adito.properties.Property;
import com.adito.properties.impl.systemconfig.SystemConfigKey;
import com.adito.security.AccountLockedException;
import com.adito.security.AuthenticationModuleManager;
import com.adito.security.AuthenticationScheme;
import com.adito.security.Constants;
import com.adito.security.DefaultAuthenticationScheme;
import com.adito.security.InvalidLoginCredentialsException;
import com.adito.security.LogonController;
import com.adito.security.LogonControllerFactory;
import com.adito.security.PasswordCredentials;
import com.adito.security.SessionInfo;
import com.adito.security.SystemDatabaseFactory;
import com.adito.security.User;
import com.adito.security.UserNotFoundException;
import com.adito.security.WebDAVAuthenticationModule;
import com.adito.security.actions.LogonAction;
import com.adito.vfs.VFSResource;

/**
 * <p>
 * A simple wrapper isolating the Java Servlet API from this <a
 * href="http://www.rfc-editor.org/rfc/rfc2518.txt">WebDAV</a> implementation.
 * </p>
 * 
 * @author <a href="http://www.betaversion.org/~pier/">Pier Fumagalli</a>
 */
public class DAVTransaction {

    private static Log log = LogFactory.getLog(DAVTransaction.class);

    public final static String ATTR_EXPECTING_REALM_AUTHENTICATION = "expectingRealmAuth";
    public final static String ATTR_DEREGISTER_SUB_AUTHS = "deregisterSubAuths";
    public final static String ATTR_AUTH_ATTEMPTS = "authAttempts";

    /**
     * <p>
     * The identifyication of the <code>infinity</code> value in the
     * <code>Depth</code> header.
     * </p>
     */
    public static final int INFINITY = Integer.MAX_VALUE;

    /**
     * <p>
     * The nested {@link HttpServletRequest}.
     * </p>
     */
    private HttpServletRequest req = null;
    /**
     * <p>
     * The nested {@link HttpServletResponse}.
     * </p>
     */
    private HttpServletResponse res = null;
    /**
     * <p>
     * The {@link URI} associated with the base of the repository.
     * </p>
     */
    private URI base = null;
    /**
     * <p>
     * The path for this transaction. contains user etc.
     * </p>
     */
    private String path;
    /**
     * <p>
     * A cache of resources for the life of this transaction
     */
    private Map resourceCache;

    /**
     * The session
     */
    private SessionInfo sessionInfo;

    /**
     * <p>
     * The current credentials object being used for authentication to the
     * resources
     */
    // private DAVCredentials currentCredentials;
    /* ====================================================================== */
    /* Constructors */
    /* ====================================================================== */

    /**
     * <p>
     * Create a new {@link DAVTransaction} instance.
     * </p>
     * 
     * @throws URISyntaxException
     */
    public DAVTransaction(ServletRequest request, ServletResponse response)
    // throws ServletException, DAVAuthenticationRequiredException {
        throws ServletException, URISyntaxException {
        if (request == null)
            throw new NullPointerException("Null request");
        if (response == null)
            throw new NullPointerException("Null response");
        this.req = (HttpServletRequest) request;
        this.res = (HttpServletResponse) response;
        this.resourceCache = new HashMap();

        /*
         * First see if the launch ID has been provided as a parameter. If it
         * has we can just get the resource session directly. This should happen
         * for web folders that are first launched from an active user session
         * or from a file download from the network place HTML file browser.
         */

        String launchId = request.getParameter(LaunchSession.LAUNCH_ID);

        if (launchId != null) {
            LaunchSession launchSession = LaunchSessionFactory.getInstance().getLaunchSession(launchId);
            if (launchSession != null) {
                sessionInfo = launchSession.getSession();
                LogonControllerFactory.getInstance().addCookies(new ServletRequestAdapter((HttpServletRequest) request),
                    new ServletResponseAdapter((HttpServletResponse) response), launchSession.getSession().getLogonTicket(),
                    launchSession.getSession());
                sessionInfo.access();
            } else if (log.isDebugEnabled())
                log.debug("Could not locate session using ticket");
        }
        sessionInfo = LogonControllerFactory.getInstance().getSessionInfo(req);
        configureFromRequest();
    }

    public void putCachedResource(VFSResource resource) {
        String key = DAVUtilities.concatenatePaths(resource.getMount().getMountString(), resource.getRelativePath());
        resourceCache.put(key, resource);
    }

    public VFSResource getCachedResource(String fullPath) {
        return (VFSResource) resourceCache.get(fullPath);
    }

    public boolean attemptToAuthorize() throws IOException, UserNotFoundException, Exception {
        if (!verifyIp()) {
            return false;
        }

        String expectingRealm = (String) req.getSession().getAttribute(ATTR_EXPECTING_REALM_AUTHENTICATION);

        /* Attempt authentication if cookieless clients are allowed to connect or
         * if we are definitely expecting some realm to be authenticated 
         */
        if (Property.getPropertyBoolean(new SystemConfigKey("security.allowUntrackedWebDAVSessions")) || expectingRealm != null) {
            for (Enumeration e = req.getHeaders(HttpConstants.HDR_AUTHORIZATION); e.hasMoreElements();) {
                String val = (String) e.nextElement();
                authorize(expectingRealm, val);
            }
        }

        return true;

    }

    boolean verifyIp() {
        try {
            if (SystemDatabaseFactory.getInstance().verifyIPAddress(req.getRemoteAddr())) {
                return true;
            }
        } catch (Exception e) {
            log.error("Failed to verify IP address. Considering unauthorized.", e);
        }
        if (log.isDebugEnabled())
            log.debug(req.getRemoteHost() + " is not authorized");
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }

    /**
     * Authorise the provided realm using the data the
     * {@link HttpConstants#HDR_AUTHORIZATION} header.
     * 
     * @param expectingRealm realm authenticating against
     * @param authorization authorisation data from
     *        {@link HttpConstants#HDR_AUTHORIZATION} header.
     * 
     * @throws IOException on any serious error
     * @throws UserNotFoundException if user cannot be found
     * @throws DAVAuthenticationRequiredException if authorisation data is wrong
     */
    public void authorize(String expectingRealm, String authorization) throws IOException, UserNotFoundException,
                    DAVAuthenticationRequiredException {
        int idx = authorization.indexOf(' ');
        if (idx == -1 || idx == authorization.length() - 1) {
            throw new DAVAuthenticationRequiredException(expectingRealm);
        }
        // Authenticate the user
        String method = authorization.substring(0, idx);
        if (!method.equalsIgnoreCase("basic")) {
            throw new DAVAuthenticationRequiredException(expectingRealm);
        }

        // Extract the credentials - should be ticket:tunnel
        String encoded = authorization.substring(idx + 1);
        String credentials = new String(Base64.decode(encoded));

        idx = credentials.indexOf(':');
        if (idx == 0 || idx == -1) {
            throw new DAVAuthenticationRequiredException(expectingRealm);
        }

        // Get the user credentials
        String username = credentials.substring(0, idx);

        if (expectingRealm == null) {

            /*
             * If we wern't expecting authentication, but we got it anyway, the
             * client probably doesn't support cookies.
             */
            AuthenticationScheme authScheme = (DefaultAuthenticationScheme) req.getSession().getAttribute(Constants.AUTH_SESSION);
            if (authScheme != null) {
                throw new IOException("Not expecting a realm, yet an authentication session is available. This is unexpected!");
            }

            doAuth(expectingRealm, username, DAVServlet.configureAuthenticationScheme(req, res));

            /*
             * We now can get the sessionInfo object for this session and make
             * it temporary this will ensure it is destroyed once the request is
             * complete.
             */
            sessionInfo = LogonControllerFactory.getInstance().getSessionInfo(req);
            sessionInfo.setTemporary(true);

        } else if (expectingRealm.equals(WebDAVAuthenticationModule.DEFAULT_REALM)) {
            AuthenticationScheme authScheme = (DefaultAuthenticationScheme) req.getSession().getAttribute(Constants.AUTH_SESSION);
            if (authScheme == null) {
                throw new IOException("No authentication scheme initialised.");
            }

            doAuth(expectingRealm, username, authScheme);

            /*
             * We now can get the sessionInfo object for this session and make
             * it temporary this will ensure it is destroyed once the request is
             * complete.
             */
            if (sessionInfo == null) {
                sessionInfo = LogonControllerFactory.getInstance().getSessionInfo(req);
            }
        } else {

            if (log.isDebugEnabled())
                log.debug("Logging " + username + " [" + req.getRemoteHost() + "] onto realm " + expectingRealm
                                + " using Basic authentication for session " + req.getSession().getId());
            // subAuths.put(expectingRealm, new AuthPair(username,
            // password.toCharArray()));
        }
        req.getSession().removeAttribute(ATTR_EXPECTING_REALM_AUTHENTICATION);
        req.getSession().removeAttribute(Constants.AUTH_SENT);

        /* Logging method */
        if (log.isDebugEnabled())
            log.debug(req.getMethod() + ' ' + req.getRequestURI() + ' ' + req.getProtocol());
    }

    private void doAuth(String expectingRealm, String username, AuthenticationScheme authScheme)
                    throws DAVAuthenticationRequiredException, IOException {
        if (authScheme == null) {
            throw new DAVAuthenticationRequiredException("No valid authentication scheme.");
        }
        // Find user
        try {
            User user = UserDatabaseManager.getInstance().getDefaultUserDatabase().getAccount(username);
            authScheme.setUser(user);
            LogonAction.authenticate(authScheme, req);
            LogonAction.finishAuthentication(authScheme, req, res);
        } catch (InvalidLoginCredentialsException ilce) {
            // Incorrect details, try again
            throw new DAVAuthenticationRequiredException(expectingRealm);
        } catch (Exception e) {
            IOException ioe = new IOException("Failed to authenticate using scheme.");
            ioe.initCause(e);
            throw ioe;
        }
    }

    public HttpServletResponse getResponse() {
        return (HttpServletResponse) res;
    }

    /* ====================================================================== */
    /* Request methods */
    /* ====================================================================== */

    /**
     * <p>
     * Get the request object.
     * </p>
     */
    public HttpServletRequest getRequest() {
        return req;
    }

    /**
     * <p>
     * Return the path originally requested by the client.
     * </p>
     */
    public String getMethod() {
        return this.req.getMethod();
    }

    /**
     * <p>
     * Return the path for this transaction. This will be the path as the client
     * sees it less the first element
     * 
     * @return path
     */
    public String getPath() {
        return path;
        // String path = this.req.getPathInfo();
        // if (path == null) return "";
        // if ((path.length() > 0) && (path.charAt(0) == '/')) {
        // return path.substring(1);
        // } else {
        // return path;
        // }
    }

    public boolean isRequiredRootRedirect() {
        return false;
    }

    /**
     * <p>
     * Return the path originally requested by the client encoded.
     * </p>
     */
    public String getPathEncoded() {
        return DAVUtilities.encodePath(getPath());
    }

    /**
     * <p>
     * Return the depth requested by the client for this transaction.
     * </p>
     */
    public int getDepth() {
        String depth = req.getHeader("Depth");
        if (depth == null)
            return INFINITY;
        if ("infinity".equals(depth))
            return INFINITY;
        try {
            return Integer.parseInt(depth);
        } catch (NumberFormatException exception) {
            throw new DAVException(412, "Unable to parse depth", exception);
        }
    }

    /**
     * <p>
     * Return a {@link URI}
     */
    public URI getDestination() {
        String destination = this.req.getHeader("Destination");
        if (destination != null)
            try {
                return this.base.relativize(new URI(destination.replaceAll(" ", "%20")));
            } catch (URISyntaxException exception) {
                throw new DAVException(412, "Can't parse destination", exception);
            }
        return null;
    }

    /**
     * <p>
     * Return the overwrite flag requested by the client for this transaction.
     * </p>
     */
    public boolean getOverwrite() {
        String overwrite = req.getHeader("Overwrite");
        if (overwrite == null)
            return true;
        if ("T".equals(overwrite))
            return true;
        if ("F".equals(overwrite))
            return false;
        throw new DAVException(412, "Unable to parse overwrite flag");
    }

    /**
     * <p>
     * Check if the client requested a date-based conditional operation.
     * </p>
     */
    public Date getIfModifiedSince() {
        String name = "If-Modified-Since";
        if (this.req.getHeader(name) == null)
            return null;
        return new Date(this.req.getDateHeader(name));
    }

    /* ====================================================================== */
    /* Response methods */
    /* ====================================================================== */

    /**
     * <p>
     * Set the HTTP status code of the response.
     * </p>
     */
    public void setStatus(int status) {
        this.res.setStatus(status);
    }

    /**
     * <p>
     * Set the HTTP <code>Content-Type</code> header.
     * </p>
     */
    public void setContentType(String type) {
        this.res.setContentType(type);
    }

    /**
     * <p>
     * Set an HTTP header in the response.
     * </p>
     */
    public void setHeader(String name, String value) {
        this.res.setHeader(name, value);
    }

    /**
     * <p>
     * Set an HTTP header in the response.
     * </p>
     * 
     * @param name name
     * @param value value
     */
    public void setDateHeader(String name, int value) {
        this.res.setDateHeader(name, value);
    }

    /* ====================================================================== */
    /* I/O methods */
    /* ====================================================================== */

    /**
     * <p>
     * Read from the body of the original request.
     * </p>
     */
    public InputStream getInputStream() throws IOException {
        /* We don't support ranges */
        if (req.getHeader("Content-Range") != null)
            throw new DAVException(501, "Content-Range not supported");

        if (this.req.getContentLength() >= 0)
            this.req.getInputStream();
        String len = this.req.getHeader("Content-Length");
        if (len != null)
            try {
                if (Long.parseLong(len) > 0)
                    return this.req.getInputStream();
            } catch (NumberFormatException exception) {
                // Unparseable content length header...
            }

        // Do not throw an exception, this could be null without an error
        // condition
        return null;
    }

    /**
     * <p>
     * Write the body of the response.
     * </p>
     */
    public OutputStream getOutputStream() throws IOException {
        if (SystemProperties.get("adito.webdav.debug", "false").equals("true"))
            return new TempOutputStream(this.res.getOutputStream());
        else
            return this.res.getOutputStream();
    }

    class TempOutputStream extends OutputStream {

        OutputStream out;
        StringBuffer wbBuf;

        TempOutputStream(OutputStream out) {
            this.out = out;
            wbBuf = new StringBuffer();
        }

        public void write(byte[] buf, int off, int len) throws IOException {
            wbBuf.append(new String(buf, off, len));
            out.write(buf, off, len);
        }

        public void write(int b) throws IOException {
            wbBuf.append((byte) b);
            out.write((byte) b);
        }

        public void flush() throws IOException {
            log.info(wbBuf.toString());
            wbBuf.setLength(0);
            out.flush();
        }

    }

    /**
     * <p>
     * Write the body of the response.
     * </p>
     */
    public PrintWriter write(String encoding) throws IOException {
        return new PrintWriter(new OutputStreamWriter(this.getOutputStream(), encoding));
    }

    /* ====================================================================== */
    /* Lookup methods */
    /* ====================================================================== */

    /**
     * <p>
     * Look up the final URI of a {@link VFSResource} as visible from the HTTP
     * client requesting this transaction.
     * </p>
     */
    public URI lookup(VFSResource resource) {
        URI uri = resource.getRelativeURI();
        URI resolved = null;
        if (uri == null || uri.toString().equals("")) {
            resolved = this.base;
        } else {
            resolved = this.base.resolve(uri).normalize();
            ;
        }
        return resolved;
    }

    public PasswordCredentials getCredentials() {
        String authorization = req.getHeader("Authorization");

        if (authorization == null) {
            return null;
        }

        int idx = authorization.indexOf(' ');

        if (idx == -1 || idx == authorization.length() - 1) {
            return null;
        }

        // Authenticate the user
        String method = authorization.substring(0, idx);

        if (!method.equalsIgnoreCase("basic")) {
            return null;
        }

        // Extract the credentials - should be ticket:tunnel
        String encoded = authorization.substring(idx + 1);

        String credentials = new String(Base64.decode(encoded));
        idx = credentials.indexOf(':');

        if (idx == 0 || idx == -1) {
            return null;
        }

        // Get the user credentials
        String username = credentials.substring(0, idx);
        String password = credentials.substring(idx + 1);

        return new PasswordCredentials(username, password.toCharArray());
    }

    /**
     * Get the session info for this transaction. The user and other session
     * related objects may be found here.
     * 
     * @return session info
     */
    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    /**
     * Check if the supplied resource path is valid for this transaction path.
     * This will be used to force a redirect to the required path if not.
     * 
     * @param fullResourcePath
     * @return is resource path
     */
    public boolean isResourcePath(String fullResourcePath) {
        String fullUri = DAVUtilities.stripTrailingSlash(DAVUtilities.stripLeadingSlash(fullResourcePath));
        return fullUri.equals(getPath());
    }

    void configureFromRequest() throws URISyntaxException {
        String scheme = this.req.getScheme();
        String host = this.req.getServerName();
        String basePath = DAVUtilities.concatenatePaths(this.req.getServletPath(), this.req.getPathInfo());
        int port = this.req.getServerPort();
        this.base = new URI(scheme, null, host, port, basePath, null, null);
        this.base = this.base.normalize();
        path = DAVUtilities.stripTrailingSlash(DAVUtilities.stripLeadingSlash(DAVUtilities.stripFirstPath(base.getPath())));

    }
}