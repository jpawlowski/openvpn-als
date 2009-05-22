
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
			
package com.ovpnals.agent.client.launcher;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;

import com.sshtools.ui.awt.ImageCanvas;
import com.sshtools.ui.awt.UIUtil;
import com.sshtools.ui.awt.options.OptionDialog;
import com.ovpnals.agent.client.util.AbstractApplicationLauncher;
import com.ovpnals.agent.client.util.ApplicationLauncherEvents;
import com.ovpnals.agent.client.util.ProcessMonitor;
import com.ovpnals.agent.client.util.TunnelConfiguration;
import com.ovpnals.agent.client.util.Utils;
import com.ovpnals.agent.client.util.XMLElement;


/**
 * Applet used for downloading, installing an launching the <i>OpenVPN-ALS Agent</i> or
 * other application extensions.
 */
public class AgentLauncher extends Applet implements ApplicationLauncherEvents {
    private boolean isStandalone = false;
    String ticket;
    ProgressBar progress;
    String cmdline;
    String name;
    boolean monitor = false;
    boolean debug = false;
    boolean cleanOnExit = false;
    String cacheDirectory;
    Button launch;
    String localProxyURL;
    ProcessMonitor processMonitor;
    long totalNumBytes;
    String userAgent;
    Locale locale;
    Panel mainPanel;
    int timeout;
    URL debugCodebase;

    // New properties
    String appName = null;
    String extensionId = "ovpnals-agent"; //$NON-NLS-1$
    boolean isAgent = true;
    String launcherImage = "/images/launcher-agent.gif"; //$NON-NLS-1$
    String localeName = "en"; //$NON-NLS-1$

    /**
     * Get a parameter value of a default if it does not exist
     * @param key
     * @param def
     * @return parameter value 
     */
    public String getParameter(String key, String def) {
        return isStandalone ? System.getProperty(key, def) : (getParameter(key) != null ? getParameter(key) : def);
    }

    /* (non-Javadoc)
     * @see java.applet.Applet#init()
     */
    public void init() {
        // Get the message resources 
        loadResourceBundles();
        
        // Start
        
        appName = Messages.getString("VPNLauncher.applicationName"); //$NON-NLS-1$
        System.out.println(Messages.getString("VPNLauncher.sysout.initialising")); //$NON-NLS-1$

        // #ifdef MSJAVA
        /*
         * try { if (Class.forName("com.ms.security.PolicyEngine") != null) {
         * com.ms.security.PolicyEngine.assertPermission(com.ms.security.PermissionID.SYSTEM);
         * com.ms.security.PolicyEngine.assertPermission(com.ms.security.PermissionID.PROPERTY); } }
         * catch (Throwable cnfe) { cnfe.printStackTrace(); }
         */
        // #endif
        // #ifdef NETSCAPE
        /*
         * try {
         * netscape.security.PrivilegeManager.enablePrivilege("UniversalExecAccess"); }
         * catch (Exception cnfe) {
         * System.out.println("netscape.security.PrivilegeManager class not
         * found"); cnfe.printStackTrace(); }
         */
        // #endif
        /**
         * This section loads the launcher type information
         */
        System.out.println(Messages.getString("VPNLauncher.sysout.gettingLauncherType")); //$NON-NLS-1$

        // Allow the socket to timeout 10 seconds after the agent registration timeout
        timeout = Integer.parseInt(getParameter("timeout", "60000")) + 10000;
        
        appName = getParameter("appName", appName); //$NON-NLS-1$
        extensionId = getParameter("extensionId", extensionId); //$NON-NLS-1$
        launcherImage = getParameter("launcherImage", launcherImage); //$NON-NLS-1$
        isAgent = Boolean.valueOf(getParameter("isAgent", String.valueOf(isAgent))).booleanValue(); //$NON-NLS-1$

        System.out.println(Messages.getString("VPNLauncher.sysout.retrievingDebugStatus")); //$NON-NLS-1$
        debug = Boolean.valueOf(getParameter("debug", "false")).booleanValue(); //$NON-NLS-1$ //$NON-NLS-2$debug = Boolean.valueOf(getParameter("debug", "false")).booleanValue(); //$NON-NLS-1$ //$NON-NLS-2$
        if(debug) {
	        String debugCodebaseLocation = getParameter("debugCodebase", "");
	        if(!debugCodebaseLocation.equals("")) {
	        	try {
	        		debugCodebase = new URL(debugCodebaseLocation);
	        	}
	        	catch(MalformedURLException murle) {        		
	        	}
	        }
        }
        cleanOnExit = Boolean.valueOf(getParameter("cleanOnExit", "false")).booleanValue(); //$NON-NLS-1$ //$NON-NLS-
        cacheDirectory = getParameter("dir", ".ovpnals"); //$NON-NLS-1$ //$NON-NLS-

        if (!isAgent) {
            try {
                // Set a fake codebase so that images are loaded from the root
                // of OpenVPN-ALS (i.e. in the images folders)
            	URL cb = getActualCodebase();
                UIUtil.setCodeBase(new URL(cb.getProtocol(), cb.getHost(),
                                cb.getPort() == -1 ? 443 : cb.getPort(), "")); //$NON-NLS-1$
            } catch (MalformedURLException ex) {
            }
        }
        System.out.println(MessageFormat.format(Messages.getString("VPNLauncher.sysout.launchingApplication"), //$NON-NLS-1$ 
                        new Object[] { appName, extensionId, launcherImage, new Boolean(isAgent) }));
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see java.applet.Applet#start()
     */
    public void start() {
        if ("true".equals(getParameter("autoStart", "false"))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            System.out.println(Messages.getString("VPNLauncher.sysout.autostartLaunching")); //$NON-NLS-1$
            launch();
        } else {
            System.out.println(Messages.getString("VPNLauncher.sysout.nonAutostartLaunching")); //$NON-NLS-1$
        }
    }

    /* (non-Javadoc)
     * @see java.applet.Applet#stop()
     */
    public void stop() {
        System.out.println(Messages.getString("VPNLauncher.sysout.stopping")); //$NON-NLS-1$
    }
    
    /**
     * The actual code base may come from a parameter if debuggin
     * 
     * @return actual codebase
     */
    public URL getActualCodebase() {
    	if(debugCodebase != null) {
    		return debugCodebase;
    	}
    	return getCodeBase();
    }

    /**
     * Launch the application
     */
    public void launch() {
        try {

            // Remove me!!! Testing only
            // com.maverick.ssl.https.HTTPSURLStreamHandlerFactory.installHTTPSSupport();

            System.out.println(MessageFormat.format(Messages.getString("VPNLauncher.sysout.launching"), new Object[] { appName } )); //$NON-NLS-1$

            // Get the pending VPN session ticket from the browser
            ticket = this.getParameter("ticket", null); //$NON-NLS-1$

            // We cannot have a null ticket!
            if (ticket == null && isAgent) {
                // Show an error message...
                System.out.println(Messages.getString("VPNLauncher.sysout.nullTicket")); //$NON-NLS-1$
                return;
            }

            /**
             * Determine if we need to monitor the executed process. If we do
             * then all of the output from the process will be written to
             * System.out which should make it appear in the console.
             * 
             * If are monitoring and we close the applet it causes some problems
             * with the process which results in an unresponsive period. Not
             * sure what this is but once this applet is deployed and tested we
             * should have no need to monitor.
             */
            System.out.println(Messages.getString("VPNLauncher.sysout.retrievingMonitorStatus")); //$NON-NLS-1$
            monitor = Boolean.valueOf(getParameter("monitor", "false")).booleanValue(); //$NON-NLS-1$ //$NON-NLS-2$

            // Determine proxy URL
            System.out.println(Messages.getString("VPNLauncher.sysout.checkingBrowserProxySettings")); //$NON-NLS-1$

            userAgent = getParameter("userAgent", "Unknown"); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println(MessageFormat.format(Messages.getString("VPNLauncher.sysout.userAgent"), new Object[] {  userAgent } )); //$NON-NLS-1$

            localProxyURL = getParameter("proxyURL", ""); //$NON-NLS-1$ //$NON-NLS-2$
            String pluginProxyURL = ""; //$NON-NLS-1$

            if (localProxyURL.toLowerCase().startsWith("browser://")) { //$NON-NLS-1$
                System.out.println(Messages.getString("VPNLauncher.sysout.checkingJavaProxyProperties")); //$NON-NLS-1$

                /*
                 * If the launcher is running using the plugin we might be able
                 * to extract the proxy settings from there
                 */
                String browserProxy = System.getProperty("javaplugin.proxy.config.list"); //$NON-NLS-1$
                if (browserProxy != null && !browserProxy.equals("")) { //$NON-NLS-1$
                    StringTokenizer t = new StringTokenizer(browserProxy, ","); //$NON-NLS-1$
                    while (t.hasMoreTokens()) {
                        String type = t.nextToken();
                        int idx = type.indexOf('=');
                        if (idx != -1) {
                            String value = type.substring(idx + 1);
                            type = type.substring(0, idx);
                            if (type.equals("http")) { //$NON-NLS-1$
                                pluginProxyURL = "http://" + value; //$NON-NLS-1$
                                break;
                            }
                        } else {
                            // Unknown form
                        }
                    }
                }

            } else if (localProxyURL != null && !localProxyURL.equals("")) { //$NON-NLS-1$
                if (!localProxyURL.startsWith("http://") && !localProxyURL.startsWith("https://") //$NON-NLS-1$ //$NON-NLS-2$
                                && !localProxyURL.startsWith("browser://")) //$NON-NLS-1$
                    localProxyURL = "http://" + localProxyURL; //$NON-NLS-1$
            }

            // Add the required parameters for our ApplicationLauncher
            final Hashtable params = new Hashtable();
            params.put("id", extensionId); //$NON-NLS-1$
            params.put("ticket", ticket); //$NON-NLS-1$
            params.put("cleanOnExit", String.valueOf(cleanOnExit)); //$NON-NLS-1$
            params.put("userAgent", userAgent); //$NON-NLS-1$
            params.put("pluginProxyURL", pluginProxyURL); //$NON-NLS-1$
            
            if(getParameter("customParameters")!=null) {
            	StringTokenizer t = new StringTokenizer(getParameter("customParameters"), ",");
            	int idx;
            	while(t.hasMoreTokens()) {
            		String p = t.nextToken();
            		System.out.println("Received custom parameter " + p);
            		if((idx = p.indexOf('=')) > -1) {
            			params.put(p.substring(0, idx), p.substring(idx+1));
            		} else {
            			params.put(p, "");
            		}
            	}
            }
            
            if (isAgent) {

                setIfNotEmpty("java.version", params); //$NON-NLS-1$
                setIfNotEmpty("java.vendor", params); //$NON-NLS-1$
                setIfNotEmpty("sun.os.patch.level", params); //$NON-NLS-1$
                setIfNotEmpty("os.name", params); //$NON-NLS-1$
                setIfNotEmpty("os.version", params); //$NON-NLS-1$
                setIfNotEmpty("os.arch", params); //$NON-NLS-1$
            }
            Thread thread = new Thread() {

                public void run() {

                    // #ifdef MSJAVA
                    /*
                     * try { System.out.println("Asserting SYSTEM permission");
                     * 
                     * com.ms.security.PolicyEngine.assertPermission(com.ms.security.PermissionID.SYSTEM);
                     * System.out.println("Asserting FILEIO permission");
                     * 
                     * com.ms.security.PolicyEngine.assertPermission(com.ms.security.PermissionID.FILEIO);
                     * System.out.println("Asserting EXEC permission");
                     * com.ms.security.PolicyEngine.assertPermission(com.ms.security.PermissionID.EXEC);
                     * System.out.println("Asserting PROPERTY permission");
                     * 
                     * com.ms.security.PolicyEngine.assertPermission(com.ms.security.PermissionID.PROPERTY);
                     * System.out.println("Asserting USERFILEIO permission");
                     * 
                     * com.ms.security.PolicyEngine.assertPermission(com.ms.security.PermissionID.USERFILEIO); }
                     * catch(Throwable t) { t.printStackTrace(); }
                     */ 
                    // #endif
                    System.out.println(Messages.getString("VPNLauncher.sysout.startingApplicationLauncher")); //$NON-NLS-1$

                    System.out.println(System.getProperties().toString());
                    try {
                        startingLaunch(appName);
                        URL codebase = getActualCodebase();
                        AbstractApplicationLauncher launcher = new AgentLauncherApplicationLauncher(new File(Utils.getHomeDirectory(), cacheDirectory),  codebase.getProtocol(), null, codebase //$NON-NLS-1$
                                        .getHost().equals("") ? "localhost" : codebase.getHost(), //$NON-NLS-1$ //$NON-NLS-2$
                                        	codebase.getPort() == -1 ? (codebase.getProtocol()!=null && codebase.getProtocol().equalsIgnoreCase("http") ? 80 : 443) : codebase.getPort(), params, AgentLauncher.this);

                        launcher.setDebug(debug);

                        System.out.println(MessageFormat.format(Messages.getString("VPNLauncher.sysout.settingLocalProxy"), new Object[] { localProxyURL } ) ); //$NON-NLS-1$
                        launcher.setLocalProxyURL(localProxyURL);
                        launcher.prepare();
                        System.out.println(Messages.getString("VPNLauncher.sysout.preparedLauncher")); //$NON-NLS-1$

                        /*
                         * Only start the synchronisation thread if this is 
                         * an agent as it is the only type that sends a 
                         * sync message 
                         */
                        if(isAgent) {
	                        SynchronizationThread sync = new SynchronizationThread(launcher);
	                        System.out.println(Messages.getString("VPNLauncher.sysout.startingSyncThread")); //$NON-NLS-1$
	                        sync.start();
	                        // Give the connection time to get established
	                        try {
	                            Thread.sleep(1000);
	                        } catch (InterruptedException ie) {
	                            ie.printStackTrace();
	                        }
                        }

                        launcher.start();
                        
                        /*
                         * If this is not the agent then there is no
                         * synchronisation thread that will handle the 
                         * redirect so we do it directly. 
                         */
                        if(!isAgent) {
                            String returnTo = getParameter("returnTo", ""); //$NON-NLS-1$ //$NON-NLS-2$
                            URL returnToUrl = returnTo.equals("") ? getDocumentBase() : new URL(getDocumentBase(), returnTo); //$NON-NLS-1$
                            returnToUrl = new URL(addRedirectParameter(returnToUrl.toExternalForm(), "vpnMessage", appName + " launched.")); //$NON-NLS-1$ //$NON-NLS-2$
                            System.out.println(MessageFormat.format(Messages.getString("VPNLauncher.sysout.redirecting"), new Object[] { returnToUrl.toExternalForm() })); //$NON-NLS-1$
                            setMessage(Messages.getString("VPNLauncher.complete")); //$NON-NLS-1$
                            getAppletContext().showDocument(returnToUrl);
                        }
                        else {

                            /*
                             * Monitor the process if required
                             */
                        	
	                        if (monitor) {
	                            System.out.println(MessageFormat.format(Messages.getString("VPNLauncher.sysout.monitoring"), new Object[] { appName })); //$NON-NLS-1$
	                            processMonitor = launcher.getApplicationType().getProcessMonitor();
	                            if (processMonitor != null) {
	                                MonitorOutputStream out = new MonitorOutputStream();
	                                finishedLaunch();
	                                int exitcode = processMonitor.watch(out, out);
	                                System.out.println(MessageFormat.format(Messages.getString("VPNLauncher.sysout.finished"), new Object[] { appName, new Integer(exitcode) })); //$NON-NLS-1$
	                            } else {
	                                System.out.println(Messages.getString("VPNLauncher.sysout.noProgressMonitor")); //$NON-NLS-1$
	                            }
	                        }
                        }

                    } catch (Exception ex) {
                        // Show an error message .....
                        setMessage(ex.getMessage());
                        ex.printStackTrace();
                    } finally {
                        finishedLaunch();
                    }
                }
            };

            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
            // addLaunchButton(); // Add the launch button in case the vpn
            // client
            // is closed during the lifetime of this applet
        }

    }

    protected static void setIfNotEmpty(String name, Hashtable p) {
        String v = System.getProperty(name);
        if (v != null && !v.equals("")) { //$NON-NLS-1$
            p.put(name, v);
        }
    }

    // Get Applet information
    public String getAppletInfo() {
        return Messages.getString("VPNLauncher.appletInfo"); //$NON-NLS-1$
    }

    // Get parameter info
    public String[][] getParameterInfo() {
        String[][] pinfo = { { "ticket", "String", "" }, }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        return pinfo;
    }

    public void startDownload(long totalNumBytes) {
        progress.setMessage(Messages.getString("VPNLauncher.downloadingFiles")); //$NON-NLS-1$
        this.totalNumBytes = totalNumBytes;
        progress.updateValue(1L);
        progress.updateValue(20);
    }

    public void progressedDownload(long bytesSoFar) {
        int percent = (int) (((float) bytesSoFar / (float) totalNumBytes) * 70f) + 20;
        progress.updateValue(percent);
    }

    public void completedDownload() {
        progress.setMessage(Messages.getString("VPNLauncher.downloadComplete")); //$NON-NLS-1$
        progress.updateValue(90);
    }

    public void executingApplication(String name, String cmdline) {
        progress.setMessage(MessageFormat.format(Messages.getString("VPNLauncher.executingApplication"), new Object[] { name } ) ); //$NON-NLS-1$
        progress.updateValue(95);
    }

    public TunnelConfiguration createTunnel(String name, String hostToConnect, int portToConnect, boolean usePreferredPort,
                    boolean singleConnection, String sourceInterface) {
        // We should not be creating any tunnels
        return null;
    }

    public void debug(String msg) {
        System.out.println(msg);
    }

    public void startingLaunch(String application) {
        progress = new ProgressBar(this,
                        MessageFormat.format(Messages.getString("VPNLauncher.startingLaunch"), new Object[] { application } ), "Agent Launcher", 100L, false); //$NON-NLS-1$ //$NON-NLS-2$
        progress.updateValue(7L);
    }

    public void processingDescriptor() {
        progress.updateValue(14L);
        progress.setMessage(Messages.getString("VPNLauncher.processingApplicationDescriptor")); //$NON-NLS-1$
    }

    public void finishedLaunch() {
        if (progress.getCurrentValue() != 100) {
            progress.setMessage(Messages.getString("VPNLauncher.launchedAgent")); //$NON-NLS-1$
            progress.updateValue(100);
            Thread t = new Thread() {
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                    }
                    progress.dispose();
                }
            };

            t.start();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.applet.Applet#destroy()
     */
    public void destroy() {
        System.out.println(Messages.getString("VPNLauncher.destroyingApplet")); //$NON-NLS-1$
        super.destroy();
    }

    class SynchronizationThread extends Thread {

        AbstractApplicationLauncher launcher;

        SynchronizationThread(AbstractApplicationLauncher launcher) {
            this.launcher = launcher;
        }

        public void run() {

            try {

                String returnTo = getParameter("returnTo", ""); //$NON-NLS-1$ //$NON-NLS-2$
                setMessage(Messages.getString("VPNLauncher.synchronizing")); //$NON-NLS-1$

                /**
                 * Connect to the server to synchronize with the VPN
                 * session. This will allow us to refresh the browser page
                 * once we get notification that the client has registered
                 * with the server or kill the process after the timeout has
                 * elasped.
                 */
                URL codebase = getActualCodebase();
                URL url = new URL(codebase.getProtocol(), codebase.getHost().equals("") ? "localhost" :  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                	codebase.getHost(), codebase.getPort() == -1 ? codebase.getProtocol()!=null && codebase.getProtocol().equalsIgnoreCase("http") ? 80 : 443
                                : codebase.getPort(),
                                "/registerClientSynchronization.do?ticket=" + ticket); //$NON-NLS-1$
                System.out.println(MessageFormat.format(Messages.getString("VPNLauncher.sysout.sendingSync"), new Object[] { url })); //$NON-NLS-1$

                URLConnection con = url.openConnection();
                setReadTimeout(con, timeout);

                con.connect();
                System.out.println(MessageFormat.format(Messages.getString("VPNLauncher.sysout.connectedTo"), new Object[] { url } )); //$NON-NLS-1$

                XMLElement result = new XMLElement();
                result.parseFromReader(new InputStreamReader(con.getInputStream()));

                if (result.getName().equalsIgnoreCase("success")) { //$NON-NLS-1$
                    System.out.println(Messages.getString("VPNLauncher.sysout.syncOk")); //$NON-NLS-1$

                    /*
                     * TODO A nasty hack - If the returnTo points back to
                     * the VPN Client, then make sure the port is correct
                     * (the server would not have known the port for the
                     * client at the point the returnTO link was created).
                     */
                    URL returnToUrl = returnTo.equals("") ? getDocumentBase() : new URL(getDocumentBase(), returnTo); //$NON-NLS-1$
                    if (returnToUrl.getProtocol().equals("http") && returnToUrl.getHost().equals("localhost") //$NON-NLS-1$ //$NON-NLS-2$
                                    && returnToUrl.getPort() == -1) {
                        returnToUrl = new URL(returnToUrl.getProtocol(), returnToUrl.getHost(), Integer.parseInt(result
                                        .getAttribute("clientPort").toString()), returnToUrl.getFile()); //$NON-NLS-1$
                    }
                    returnToUrl = new URL(addRedirectParameter(returnToUrl.toExternalForm(),
                                    "vpnMessage", appName + " launched.")); //$NON-NLS-1$ //$NON-NLS-2$
                    System.out.println(MessageFormat.format(Messages.getString("VPNLauncher.sysout.redirecting"), new Object[] { returnToUrl.toExternalForm() })); //$NON-NLS-1$
                    getAppletContext().showDocument(returnToUrl);
                    setMessage(Messages.getString("VPNLauncher.complete")); //$NON-NLS-1$

                } else {
                    // Show an error message and kill the process
                    System.out.println(Messages.getString("VPNLauncher.sysout.failedSync")); //$NON-NLS-1$
                    System.out.println(result.getContent());
                    ProcessMonitor monitor = launcher.getApplicationType().getProcessMonitor();
                    if (monitor != null) {
                        // We cant kill the process as there may be error
                        // dialogs
                        // monitor.kill();
                        setMessage(Messages.getString("VPNLauncher.failedSync")); //$NON-NLS-1$
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                addLaunchButton(); // Add the launch button in case the vpn
                // client is closed during the lifetime of
                // this applet
            } finally {
                progress.dispose();
            }

        }
    }

    private String addRedirectParameter(String redirect, String name, String value) {
        StringBuffer buf = new StringBuffer(redirect);
        int idx = redirect.indexOf('?');
        if (idx == -1) {
            buf.append("?"); //$NON-NLS-1$
        } else {
            buf.append("&"); //$NON-NLS-1$
        }
        buf.append(name);
        buf.append('=');
        buf.append(URLEncoder.encode(value));
        return buf.toString();
    }
    
    private void loadResourceBundles() {
        localeName = getParameter("locale", localeName); //$NON-NLS-1$
        locale = Utils.createLocale(localeName);
        URL codebase = getActualCodebase();
//        NetworkClassLoader cl = new NetworkClassLoader(getClass().getClassLoader());
        ClassLoader cl = getClass().getClassLoader();
        try {
            URL url = new URL(codebase.getProtocol(), codebase.getHost().equals("") ? "localhost" : codebase.getHost(), codebase.getPort() == -1 ? codebase.getProtocol()!=null && codebase.getProtocol().equalsIgnoreCase("http") ? 80 : 443
                            : codebase.getPort(), "/loadMessageResources/");
//            cl.addURL(url);    
//            System.out.println("Loading resources from " + url);
            Messages.setBundle(Utils.getBundle(
                "com.ovpnals.agent.client.launcher.ApplicationResources",  // $NON-NLS-1$ 
                locale, 
                cl, url));
            com.ovpnals.agent.client.util.Messages.setBundle(Utils.getBundle(
                "com.ovpnals.agent.client.util.ApplicationResources",  // $NON-NLS-1$ 
                locale, 
                cl, url));
            com.ovpnals.agent.client.util.types.Messages.setBundle(Utils.getBundle(
                "com.ovpnals.agent.client.util.types.ApplicationResources",  // $NON-NLS-1$ 
                locale, 
                cl, url));
            com.sshtools.ui.awt.Messages.setBundle(Utils.getBundle(
                "com.sshtools.ui.awt.ApplicationResources",  // $NON-NLS-1$ 
                locale, 
                cl, url));
        }
        catch(MalformedURLException murle) {
            murle.printStackTrace();
        }
    }
    
    private static void setReadTimeout(URLConnection conx, int timeout) {
    	try {
    		Method m = conx.getClass().getMethod("setReadTimeout", new Class[] { int.class });
    		m.invoke(conx, new Object[] { new Integer(timeout) } );
    	}
    	catch(Throwable t) {
    		// Cannot set read timeout. Sync errors may occur
    	}
    }

    private void jbInit() throws Exception {
        String background = getParameter("background", "#e6e6e6"); //$NON-NLS-1$ //$NON-NLS-2$
        setBackground(Color.decode(background));
        String foreground = getParameter("foreground", "#000000"); //$NON-NLS-1$ //$NON-NLS-2$
        setForeground(Color.decode(foreground));
        setLayout(new BorderLayout());

        URL loc = new URL(getCodeBase().toExternalForm() + launcherImage);        
        Image img = Toolkit.getDefaultToolkit().getImage(loc);
        if(img != null) {
            ImageCanvas ic = new ImageCanvas(UIUtil.waitFor(img, this));
            ic.setValign(ImageCanvas.CENTER_ALIGNMENT);
            add(ic, BorderLayout.WEST);
        }
        mainPanel = new Panel(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        if ("true".equals(getParameter("autoStart", "false"))) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            mainPanel.add(new Label(Messages.getString("VPNLauncher.launching")), BorderLayout.CENTER); //$NON-NLS-1$
        } /*
             * else { addLaunchButton(); }
             */
    }

    private void setMessage(String text) {
        mainPanel.invalidate();
        mainPanel.removeAll();
        mainPanel.add(new Label(text), BorderLayout.CENTER);
        mainPanel.validate();

    }

    private void addLaunchButton() {

        mainPanel.invalidate();
        mainPanel.removeAll();
        launch = new Button(Messages.getString("VPNLauncher.launcher")); //$NON-NLS-1$
        launch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                launch();
            }
        });
        mainPanel.add(launch, BorderLayout.SOUTH);
        mainPanel.validate();
    }

    public void error(String msg) {
        System.out.println(msg);
        OptionDialog.error(mainPanel, Messages.getString("VPNLauncher.error"), msg); //$NON-NLS-1$
    }

    class MonitorOutputStream extends OutputStream {
        public void write(int b) throws IOException {
            System.out.write(b);
        }

        public void write(byte[] buf, int off, int len) throws IOException {
            System.out.write(buf, off, len);
        }
    }
}