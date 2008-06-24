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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;

import com.adito.boot.SystemProperties;
import com.adito.core.BundleActionMessage;
import com.adito.core.CoreException;
import com.adito.core.CoreUtil;
import com.adito.policyframework.LaunchSession;
import com.adito.security.AuthenticationModuleManager;
import com.adito.security.AuthenticationScheme;
import com.adito.security.Constants;
import com.adito.security.LogonControllerFactory;
import com.adito.security.SessionInfo;
import com.adito.security.WebDAVAuthenticationModule;
import com.adito.vfs.VFSRepository;
import com.adito.vfs.VFSResource;

/**
 * A servlet capable of processing very simple <a
 * href="http://www.rfc-editor.org/rfc/rfc2518.txt">WebDAV</a> requests.
 * <p>
 * This code was originally based on Pier Fumagallis WebDAV servlet, but as
 * Adito has developed, much of the original code has been replaced.
 * <p>
 * The servlet is now fully integrated into Adito and its <i>Virtual File
 * System</i>, so any mount can be exposed via WebDAV.
 * 
 * @author <a href="http://localhost/">3SP</a>
 * @author <a href="http://www.betaversion.org/~pier/">Pier Fumagalli</a>
 */
public class DAVServlet implements Servlet, DAVListener {

    final static Log log = LogFactory.getLog(DAVServlet.class);

    private static String PROCESSOR_ATTR = "davServlet.processor";
    private static String SESSION_INVALIDATE_LISTENER_ATTR = "davServlet.sessionInvalidateListener";

    private ServletContext context = null;
    private ServletConfig config = null;
    private static List<DAVProcessor> processors = new ArrayList<DAVProcessor>();

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        /* Remember the configuration instance */
        this.context = config.getServletContext();
        this.config = config;

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Servlet#destroy()
     */
    public void destroy() {
        for (DAVProcessor processor : processors) {
            processor.getRepository().removeListener(this);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Servlet#getServletConfig()
     */
    public ServletConfig getServletConfig() {
        return (this.config);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Servlet#getServletInfo()
     */
    public String getServletInfo() {
        return "WebDAV";
    }

    /**
     * Get the {@link DAVProcessor} from the requests session.
     * 
     * @param req request
     * @return processor
     * @throws CoreException on any error
     * @throws Exception on any error
     */
    public static DAVProcessor getDAVProcessor(HttpServletRequest req) throws CoreException, Exception {
        return getDAVProcessor(req.getSession());
    }

    /**
     * Get the {@link DAVProcessor} from the session.
     * 
     * @param session sesison
     * @return processor
     * @throws CoreException on any error
     * @throws Exception on any error
     */
    public static DAVProcessor getDAVProcessor(HttpSession session) throws Exception {
        DAVProcessor processor = (DAVProcessor) session.getAttribute(PROCESSOR_ATTR);
        if (processor == null) {
            VFSRepository repository = VFSRepository.getRepository(session);
            processor = new DAVProcessor(repository);
            processors.add(processor);
            session.setAttribute(PROCESSOR_ATTR, processor);
            session.setAttribute(SESSION_INVALIDATE_LISTENER_ATTR, new SessionInvalidateListener(processor));
            if (log.isInfoEnabled())
                log.info("Initialized repository");
        }
        return processor;
    }

    /**
     * Get the resource give its path and various session related attributes.
     * 
     * @param launchSession launch sesion
     * @param request request
     * @param response response
     * @param path resource path
     * @return resource object
     * @throws DAVBundleActionMessageException on any VFS or DAV related errors
     * @throws Exception on any other error
     */
    public static VFSResource getDAVResource(LaunchSession launchSession, HttpServletRequest request, HttpServletResponse response,
                                             String path) throws DAVBundleActionMessageException, Exception {
        VFSResource res = null;
        try {
            DAVProcessor processor = getDAVProcessor(request);
            DAVTransaction transaction = new DAVTransaction(request, response);
            res = processor.getRepository().getResource(launchSession, path, transaction.getCredentials());
            // res.start(transaction);
            res.verifyAccess();
        } catch (DAVAuthenticationRequiredException e) {
            DAVServlet.sendAuthorizationError(request, response, e.getHttpRealm());
            throw e;
        }
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Servlet#service(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse)
     */
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        debugRequest(req);
        DAVTransaction transaction = null;
        try {
            transaction = new DAVTransaction(req, res);
            DAVProcessor processor = null;
            try {
                processor = DAVServlet.getDAVProcessor(req);
            } catch (CoreException e) {
                ActionMessages mesgs = (ActionMessages) request.getAttribute(Globals.ERROR_KEY);
                if (mesgs == null) {
                    mesgs = new ActionMessages();
                    request.setAttribute(Globals.ERROR_KEY, mesgs);
                }
                mesgs.add(Globals.MESSAGE_KEY, e.getBundleActionMessage());
                return;
            } catch (Exception e1) {
                throw new IOException(e1.getMessage());
            }

            /**
             * LDP - JB I have removed the authentication code here and instead
             * placed in DAVTransaction. This allows the transaction to
             * correctly obtain sessionInfo through WEBDav.
             */
            /* Mark our presence */
            res.setHeader("Server", this.context.getServerInfo() + " WebDAV");
            res.setHeader("MS-Author-Via", "DAV");
            res.setHeader("DAV", "1");

            /*
             * Get the current SessionInfo if possible, but do not request
             * authentication yet This is because the VFSResource that we want
             * is in a mount that does not require Adito authentication
             * (required for some ActiveX and Applet extensions).
             * 
             * SessionInfo will be null if session can yet be determined
             */
            if (!transaction.attemptToAuthorize()) {
                return;
            }
            SessionInfo sessionInfo = transaction.getSessionInfo();

            // Timeout block is not needed if we have no session
            int timeoutId = sessionInfo == null ? -1 : LogonControllerFactory.getInstance().addSessionTimeoutBlock(
                transaction.getSessionInfo().getHttpSession(), "DAV Transaction");
            ;
            try {
                processor.process(transaction);
            } catch (DAVAuthenticationRequiredException dare) {
                /*
                 * If the session is temporary, then we are probably dealing
                 * with a client that doesn't support cookies. This means that
                 * secondary authentication that may be required for some mounts
                 * won't work as we cannot have two different sets of
                 * credentials without session tracking
                 */
                if (sessionInfo != null && sessionInfo.isTemporary()) {
                    throw new IOException("Mount requires further authentication. This cannot work "
                                    + "on WebDAV clients that do not support cookies.");
                } else {
                    throw dare;
                }

            } finally {
                if (timeoutId != -1)
                    LogonControllerFactory.getInstance().removeSessionTimeoutBlock(transaction.getSessionInfo().getHttpSession(),
                        timeoutId);
            }
        } catch (DAVRedirection redir) {
            String redirPath = "/fs" + redir.getLocation().getFullPath();
            req.getRequestDispatcher(redirPath).forward(req, res);
        } catch (DAVAuthenticationRequiredException e) {
            // We need to be able to authenticate the Adito session was
            // well
            // sendAuthorizationError(req, res, e.getMount().getMountString());
            sendAuthorizationError(req, res, e.getHttpRealm());
        } catch (DAVBundleActionMessageException ex) {
            log.error("Network Places Request Failed: " + req.getPathInfo(), ex);
            BundleActionMessage bam = ex.getBundleActionMessage();
            MessageResources mr = CoreUtil.getMessageResources(req.getSession(), bam.getBundle());
            // TODO locale
            String val = mr == null ? null : mr.getMessage(bam.getKey());
            res.sendError(DAVStatus.SC_INTERNAL_SERVER_ERROR, val == null ? (ex.getMessage() == null ? "No message supplied." : ex
                            .getMessage()) : val);
        } catch (DAVException ex) {
            res.setStatus(ex.getStatus());
        } catch (LockedException ex) {
            res.sendError(DAVStatus.SC_LOCKED, ex.getMessage());
        } catch (Throwable t) {
            log.error("Network Places Request Failed: " + req.getPathInfo(), t);
            res.sendError(DAVStatus.SC_INTERNAL_SERVER_ERROR, t.getMessage() == null ? "<null>" : t.getMessage());
        } finally {
            if (transaction != null && transaction.getSessionInfo() != null && transaction.getSessionInfo().isTemporary()) {
                transaction.getSessionInfo().getHttpSession().invalidate();
            }
        }
    }

    private void debugRequest(HttpServletRequest req) {
        if (SystemProperties.get("adito.webdav.debug", "false").equalsIgnoreCase("true")) {
            for (Enumeration e = req.getHeaderNames(); e.hasMoreElements();) {
                String header = (String) e.nextElement();
                for (Enumeration e2 = req.getHeaders(header); e2.hasMoreElements();) {
                    log.info(header + ": " + (String) e2.nextElement());
                }
            }
        }

        if (log.isDebugEnabled())
            log.debug("Processing " + req.getMethod() + " " + req.getPathInfo());
    }

    /**
     * Add the headers required for the browser to popup an authentication
     * dialog for a specified realm, and send the
     * {@link HttpServletResponse.SC_UNAUTHORIZED} HTTP response code.
     * 
     * @param request request
     * @param response response
     * @param realm realm.
     * @throws IOException
     */
    public static void sendAuthorizationError(HttpServletRequest request, HttpServletResponse response, String realm)
                    throws IOException {
        /*
         * If this is for the default realm (i.e Adito Authentication, we
         * need to set up an authentication scheme
         */
        if (realm.equals(WebDAVAuthenticationModule.DEFAULT_REALM)) {
            configureAuthenticationScheme(request, response);
        }

        // Configure the response

        if (log.isDebugEnabled())
            log.debug("Sending auth request for realm " + realm);
        response.setHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        request.getSession().setAttribute(DAVTransaction.ATTR_EXPECTING_REALM_AUTHENTICATION, realm);

    }

    protected static AuthenticationScheme configureAuthenticationScheme(HttpServletRequest request, HttpServletResponse response)
                    throws IOException {
        AuthenticationScheme seq = AuthenticationModuleManager.getInstance().getSchemeForAuthenticationModuleInUse(
            WebDAVAuthenticationModule.MODULE_NAME);
        if (seq == null || !seq.getEnabled()) {
            log
                            .error("User cannot authenticate via WebDAV using only HTTP BASIC authentication as the current policy does not allow this.");
            response
                            .sendError(DAVStatus.SC_FORBIDDEN,
                                "You cannot authenticate via WebDAV using only HTTP BASIC authentication as the current policy does not allow this.");
            return seq;
        }
        seq.addModule(WebDAVAuthenticationModule.MODULE_NAME);
        try {
            seq.init(request.getSession());
        } catch (Exception e) {
            IOException ioe = new IOException("Failed to authentication scheme.");
            ioe.initCause(e);
            throw ioe;
        }
        seq.nextAuthenticationModule();
        request.getSession().setAttribute(Constants.AUTH_SENT, Boolean.TRUE);
        request.getSession().setAttribute(Constants.AUTH_SESSION, seq);
        return seq;
    }

    /**
     * <p>
     * Receive notification of an event occurred in a specific
     * {@link VFSRepository}.
     * </p>
     */
    public void notify(VFSResource resource, int event) {
        String message = "Unknown event";
        switch (event) {
            case DAVListener.COLLECTION_CREATED:
                message = "Collection created";
                break;
            case DAVListener.COLLECTION_REMOVED:
                message = "Collection removed";
                break;
            case DAVListener.RESOURCE_CREATED:
                message = "Resource created";
                break;
            case DAVListener.RESOURCE_REMOVED:
                message = "Resource removed";
                break;
            case DAVListener.RESOURCE_MODIFIED:
                message = "Resource modified";
                break;
        }
        if (log.isDebugEnabled())
            log.debug(message + ": \"" + resource.getRelativePath() + "\"");
    }

    static class SessionInvalidateListener implements HttpSessionBindingListener {

        private DAVProcessor processor;

        /**
         * 
         */
        SessionInvalidateListener(DAVProcessor processor) {
            this.processor = processor;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
         */
        public void valueBound(HttpSessionBindingEvent arg0) {
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
         */
        public void valueUnbound(HttpSessionBindingEvent arg0) {
            processors.remove(processor);
            // processor.getRepository().removeListener(DAVServlet.this);
        }

    }
}
