
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.applications.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.ovpnals.applications.ApplicationShortcut;
import com.ovpnals.applications.ApplicationShortcutEventConstants;
import com.ovpnals.applications.types.HtmlType;
import com.ovpnals.boot.HttpConstants;
import com.ovpnals.boot.ReplacementEngine;
import com.ovpnals.boot.Replacer;
import com.ovpnals.boot.Util;
import com.ovpnals.core.CoreAttributeConstants;
import com.ovpnals.core.CoreEvent;
import com.ovpnals.core.CoreServlet;
import com.ovpnals.core.actions.AuthenticatedAction;
import com.ovpnals.core.stringreplacement.VariableReplacement;
import com.ovpnals.extensions.ExtensionDescriptor;
import com.ovpnals.extensions.store.ExtensionStore;
import com.ovpnals.policyframework.LaunchSession;
import com.ovpnals.policyframework.LaunchSessionFactory;
import com.ovpnals.policyframework.Policy;
import com.ovpnals.policyframework.PolicyDatabaseFactory;
import com.ovpnals.policyframework.ResourceAccessEvent;
import com.ovpnals.security.Constants;
import com.ovpnals.security.SessionInfo;

/**
 * Authenticated action that processes the HTML templates provided by
 * {@link com.ovpnals.extensions.ApplicationLauncher} extensions that have a
 * type of {@link com.ovpnals.applications.types.HtmlType}.
 * <p>
 * The HTML template source is loaded and content within it is replaced by the
 * application shortcuts parameters (and other standard replacements) before
 * being returned to the client.
 * <p>
 * This may be used to provide support for ActiveX or Java applet application
 * extensions.
 * <p>
 * The
 * {@link #onExecute(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)}
 * method requires a single <b>id</b> parameter that is the resource ID of the required
 * application shortcut.
 * <p>
 * A second optional <b>ovpnals</b> parameter may be provided that should
 * contain the HTTPS URL of the OpenVPN-ALS server as the client sees it it.
 * This information is made available as a further template replacement value.  
 */
public class GetHTMLApplicationAction extends AuthenticatedAction {

    final static Log log = LogFactory.getLog(GetHTMLApplicationAction.class);
    final static String VARIABLE_PATTERN = "\\$\\{[^}]*\\}";

    /**
     * Constructor
     */
    public GetHTMLApplicationAction() {
        super();
    }

    /* (non-Javadoc)
     * @see com.ovpnals.core.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        String launchSessionId = request.getParameter(LaunchSession.LAUNCH_ID);
        LaunchSession launchSession = LaunchSessionFactory.getInstance().getLaunchSession(launchSessionId);
        if (launchSession == null) {
            throw new Exception("No launch session id " + launchSessionId);
        }
        final ApplicationShortcut shortcut = (ApplicationShortcut)launchSession.getResource();
        launchSession.checkAccessRights(null, getSessionInfo(request));
        ExtensionDescriptor app = ExtensionStore.getInstance().getExtensionDescriptor(shortcut.getApplication());
        if (app == null) {
            throw new Exception("No application named " + shortcut.getApplication() + ".");
        }

        if (!(app.getExtensionType() instanceof HtmlType)) {
            throw new Exception(getClass().getName() + " only supports applications of type " + HtmlType.class + ".");
        }

        // Get the primary VPN client ticket

        HtmlType type = (HtmlType) app.getExtensionType();
        File file = new File(app.getApplicationBundle().getBaseDir(), type.getTemplate());
        if (log.isDebugEnabled())
        	log.debug("Loading template " + file.getAbsolutePath());

        InputStream in = null;
        StringBuffer template = new StringBuffer((int) file.length());
        try {
            in = new FileInputStream(file);
            String line = null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                if (template.length() != 0) {
                    template.append("\n");
                }
                template.append(line);
            }
        } finally {
            Util.closeStream(in);
        }

        if (log.isDebugEnabled())
        	log.debug("Parsing parameters.");
        for (Iterator i = shortcut.getParameters().entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();            
            String content = (String) entry.getValue();
            
            VariableReplacement r = new VariableReplacement();
            r.setApplicationShortcut(app, null);
            r.setServletRequest(request);
            r.setLaunchSession(launchSession);
            
            entry.setValue(r.replace(content)); 
        }

        if (log.isDebugEnabled())
        	log.debug("Template loaded, doing standard replacements.");

        VariableReplacement r = new VariableReplacement();
        r.setApplicationShortcut(app, shortcut.getParameters());
        r.setServletRequest(request);
        r.setLaunchSession(launchSession);        
        String templateText = r.replace(template.toString());

        ReplacementEngine engine = new ReplacementEngine();

        String tunnels = request.getParameter("tunnels");
        if (tunnels != null && !tunnels.equals("")) {
            StringTokenizer t = new StringTokenizer(tunnels, ",");
            while (t.hasMoreTokens()) {
                String name = null;
                String hostname = null;
                int port = -1;
                try {
                    String tunnel = t.nextToken();
                    StringTokenizer t2 = new StringTokenizer(tunnel, ":");
                    name = t2.nextToken();
                    hostname = t2.nextToken();
                    port = Integer.parseInt(t2.nextToken());
                } catch (Exception e) {
                    throw new Exception("Failed to parse tunnels parameter '" + tunnels + "'.", e);
                }
                final ExtensionDescriptor.TunnelDescriptor tunnelDescriptor = app.getTunnel(name);
                if (tunnelDescriptor == null) {
                    throw new Exception("No tunnel named " + name);
                }
                final String fHostname = hostname;
                final int fPort = port;
                String pattern = "\\$\\{tunnel:" + name + "\\.[^\\}]*\\}";
                engine.addPattern(pattern, new Replacer() {
                    public String getReplacement(Pattern pattern, Matcher matcher, String sequence) {
                        String match = matcher.group();
                        if (match.equals("${tunnel:" + tunnelDescriptor.getName() + ".hostname}")) {
                            return fHostname;
                        } else if (match.equals("${tunnel:" + tunnelDescriptor.getName() + ".port}")) {
                            return String.valueOf(fPort);
                        } else {
                            return "";
                        }
                    }
                }, null);

            }
        }

        // Get the location of OpenVPN-ALS as the client sees it
        String url = request.getParameter("ovpnals");
        if (url != null) {
            String host = request.getHeader(HttpConstants.HDR_HOST);
            if (host != null) {
                url = (request.isSecure() ? "https" : "http") + "://" + host;
            } else {

                throw new Exception("No ovpnals parameter supplied.");
            }
        }
        final URL ovpnalsUrl = new URL(url);
        engine.addPattern("\\$\\{ovpnals:[^\\}]*\\}", new Replacer() {
            public String getReplacement(Pattern pattern, Matcher matcher, String sequence) {
                String match = matcher.group();
                try {
                    String param = match.substring(14, match.length() - 1);
                    if (param.equals("host")) {
                        return ovpnalsUrl.getHost();
                    } else if (param.equals("port")) {
                        return String.valueOf(ovpnalsUrl.getPort() == -1 ? (ovpnalsUrl.getProtocol().equals("https") ? 443
                                        : 80) : ovpnalsUrl.getPort());
                    } else if (param.equals("protocol")) {
                        return ovpnalsUrl.getProtocol();
                    } else {
                        throw new Exception("Unknow variable.");
                    }
                } catch (Throwable t) {
                    log.error("Failed to replace " + match + ".", t);
                }
                return "";
            }
        }, null);

        String processed = engine.replace(templateText);
        if (log.isDebugEnabled())
        	log.debug("Returning " + processed);

        Util.noCache(response);

        response.setContentType("text/html");
        response.setContentLength(processed.length());
        request.setAttribute(Constants.REQ_ATTR_COMPRESS, Boolean.FALSE);

        OutputStream out = response.getOutputStream();
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
        pw.print(processed);
        pw.flush();

        Policy pol = PolicyDatabaseFactory.getInstance().getGrantingPolicyForUser(launchSession.getSession().getUser(), shortcut);
        CoreServlet.getServlet().fireCoreEvent(new ResourceAccessEvent(this, ApplicationShortcutEventConstants.APPLICATION_SHORTCUT_LAUNCHED, shortcut, pol, launchSession.getSession(), CoreEvent.STATE_SUCCESSFUL)
        .addAttribute(CoreAttributeConstants.EVENT_ATTR_APPLICATION_NAME, app.getName())
        .addAttribute(CoreAttributeConstants.EVENT_ATTR_APPLICATION_ID, shortcut.getApplication()));
        //////////////////////////////////////////////
        
        return null;
    }

    /* (non-Javadoc)
     * @see com.ovpnals.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.USER_CONSOLE_CONTEXT;
    }
}
