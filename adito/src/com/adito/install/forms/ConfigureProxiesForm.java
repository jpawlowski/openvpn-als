
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
			
package com.adito.install.forms;

import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.UnknownHostException;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.adito.boot.ContextKey;
import com.adito.boot.PropertyList;
import com.adito.extensions.store.ExtensionStore;
import com.adito.properties.Property;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.forms.DefaultWizardForm;

/**
 */
public class ConfigureProxiesForm extends DefaultWizardForm {
    
    final static Log log = LogFactory.getLog(ConfigureProxiesForm.class);

    // Private statics for sequence attributes
    /**
     */
    public final static String ATTR_USE_SOCKS_PROXY = "useSOCKSProxy";
    /**
     */
    public final static String ATTR_USE_HTTP_PROXY = "useHTTPProxy";
    /**
     */
    public final static String ATTR_SOCKS_PROXY_HOSTNAME = "SOCKSProxyHostname";
    /**
     */
    public final static String ATTR_SOCKS_PROXY_PORT = "SOCKSProxyPort";
    /**
     */
    public final static String ATTR_SOCKS_PROXY_USERNAME = "SOCKSProxyUsername";
    /**
     */
    public final static String ATTR_SOCKS_PROXY_PASSWORD = "SOCKSProxyPassword";
    /**
     */
    public final static String ATTR_HTTP_PROXY_HOSTNAME = "HTTPProxyHostname";
    /**
     */
    public final static String ATTR_HTTP_PROXY_PORT = "HTTPProxyPort";
    /**
     */
    public final static String ATTR_HTTP_PROXY_USERNAME = "HTTPProxyUsername";
    /**
     */
    public final static String ATTR_HTTP_PROXY_PASSWORD = "HTTPProxyPassword";
    /**
     */
    public final static String ATTR_EXTENSION_STORE_EXCEPTION = "extensionStoreException";
    /**
     */
    public final static Object ATTR_HTTP_NON_PROXY_HOSTS = "HTTPNonProxyHosts";
    
    // Private instance variables
    private boolean useSOCKSProxy;
    private boolean useHTTPProxy;
    private String socksProxyHostname;
    private String socksProxyPort;
    private String socksProxyUsername;
    private String socksProxyPassword;
    private String httpProxyHostname;
    private String httpProxyPort;
    private String httpProxyUsername;
    private String httpProxyPassword;
    private PropertyList httpNonProxyHosts;

    /**
     */
    public ConfigureProxiesForm() {
        super(true, true, "/WEB-INF/jsp/content/install/configureProxies.jspf", 
            "useHTTPProxy", true, false, "configureProxies", "install", "installation.configureProxies", 5);
    }

    /*
     * (non-Javadoc)
     * @see com.adito.wizard.forms.AbstractWizardForm#init(com.adito.wizard.AbstractWizardSequence)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception {
        try {
            if(sequence.getAttribute(ATTR_SOCKS_PROXY_HOSTNAME, null) == null) {
                socksProxyHostname = Property.getProperty(new ContextKey("proxies.socksProxyHost"));
                if(socksProxyHostname.equals("")) {
                    socksProxyUsername = "";
                    socksProxyPort = "";
                    socksProxyPassword = "";
                }
                else {
                    useSOCKSProxy = true;
                    socksProxyUsername = Property.getProperty(new ContextKey("proxies.socksProxyUser"));
                    socksProxyPassword = Property.getProperty(new ContextKey("proxies.socksProxyPassword"));
                    socksProxyPort = Property.getProperty(new ContextKey("proxies.socksProxyPort"));
                }                    
            }
            else {
                useSOCKSProxy = ((String)sequence.getAttribute(ATTR_USE_SOCKS_PROXY, 
                    String.valueOf(Property.getProperty(new ContextKey("webServer.port"))))).equals("true");
                socksProxyHostname = (String)sequence.getAttribute(ATTR_SOCKS_PROXY_HOSTNAME, "");
                socksProxyPort = (String)sequence.getAttribute(ATTR_SOCKS_PROXY_PORT, "");
                socksProxyUsername = (String)sequence.getAttribute(ATTR_SOCKS_PROXY_USERNAME, "");
                socksProxyPassword = (String)sequence.getAttribute(ATTR_SOCKS_PROXY_PASSWORD, "");
            }

            if(sequence.getAttribute(ATTR_HTTP_PROXY_HOSTNAME, null) == null) {
                httpProxyHostname = Property.getProperty(new ContextKey("proxies.http.proxyHost"));
                httpNonProxyHosts = Property.getPropertyList(new ContextKey("proxies.http.nonProxyHosts"));
                if(httpProxyHostname.equals("")) {
                    httpProxyUsername = "";
                    httpProxyPort = "";
                    httpProxyPassword = "";
                    httpNonProxyHosts.clear();
                }
                else {
                    useHTTPProxy = true;
                    httpProxyUsername = Property.getProperty(new ContextKey("proxies.http.proxyUser"));
                    httpProxyPassword = Property.getProperty(new ContextKey("proxies.http.proxyPassword"));
                    httpProxyPort = Property.getProperty(new ContextKey("proxies.http.proxyPort"));
                }                    
            }
            else {
                useHTTPProxy = ((String)sequence.getAttribute(ATTR_USE_HTTP_PROXY, 
                    String.valueOf(Property.getPropertyBoolean(new ContextKey("webServer.port"))))).equals("true");
                httpProxyHostname = (String)sequence.getAttribute(ATTR_HTTP_PROXY_HOSTNAME, "");
                httpProxyPort = (String)sequence.getAttribute(ATTR_HTTP_PROXY_PORT, "");
                httpProxyUsername = (String)sequence.getAttribute(ATTR_HTTP_PROXY_USERNAME, "");
                httpProxyPassword = (String)sequence.getAttribute(ATTR_HTTP_PROXY_PASSWORD, "");
                httpNonProxyHosts = (PropertyList)sequence.getAttribute(ATTR_HTTP_NON_PROXY_HOSTS, null);
            }
        }
        catch(Exception e) {
            log.error("Failed to initialise form.");
        }
    }

    /*
     * (non-Javadoc)
     * @see com.adito.wizard.forms.AbstractWizardForm#apply(com.adito.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        sequence.putAttribute(ATTR_USE_SOCKS_PROXY, String.valueOf(useSOCKSProxy));
        sequence.putAttribute(ATTR_USE_HTTP_PROXY, String.valueOf(useHTTPProxy));
        sequence.putAttribute(ATTR_SOCKS_PROXY_HOSTNAME, socksProxyHostname);
        sequence.putAttribute(ATTR_SOCKS_PROXY_PORT, socksProxyPort);
        sequence.putAttribute(ATTR_SOCKS_PROXY_USERNAME, socksProxyUsername);
        sequence.putAttribute(ATTR_SOCKS_PROXY_PASSWORD, socksProxyPassword);
        sequence.putAttribute(ATTR_HTTP_PROXY_HOSTNAME, httpProxyHostname);
        sequence.putAttribute(ATTR_HTTP_PROXY_PORT, httpProxyPort);
        sequence.putAttribute(ATTR_HTTP_PROXY_USERNAME, httpProxyUsername);
        sequence.putAttribute(ATTR_HTTP_PROXY_PASSWORD, httpProxyPassword);
        sequence.putAttribute(ATTR_HTTP_NON_PROXY_HOSTS, httpNonProxyHosts);

        
        String socksUsername = null, socksPassword = null, httpUsername = null, httpPassword = null;
        
    // Configure proxy settings as entered in the wizard sequence. 
        if(sequence.getAttribute(ConfigureProxiesForm.ATTR_USE_SOCKS_PROXY, "").equals("true")) {            
        	if (log.isInfoEnabled())
        		log.info("Configuring outgoing TCP/IP connections to use a SOCKS proxy server.");
            System.setProperty("socksProxyHost", (String)sequence.getAttribute(ConfigureProxiesForm.ATTR_SOCKS_PROXY_HOSTNAME, ""));
            System.setProperty("socksProxyPort", (String)sequence.getAttribute(ConfigureProxiesForm.ATTR_SOCKS_PROXY_PORT, "1080"));
            socksUsername = (String)sequence.getAttribute(ConfigureProxiesForm.ATTR_SOCKS_PROXY_USERNAME, "");
            socksPassword = (String)sequence.getAttribute(ConfigureProxiesForm.ATTR_SOCKS_PROXY_PASSWORD, "");
        } 
        if(sequence.getAttribute(ConfigureProxiesForm.ATTR_USE_HTTP_PROXY, "").equals("true")) {            
        	if (log.isInfoEnabled())
        		log.info("Configuring outgoing web connections to use a HTTP proxy server.");
            System.setProperty("http.proxyHost", (String)sequence.getAttribute(ConfigureProxiesForm.ATTR_HTTP_PROXY_HOSTNAME, ""));
            System.setProperty("com.maverick.ssl.https.HTTPProxyHostname", (String)sequence.getAttribute(ConfigureProxiesForm.ATTR_HTTP_PROXY_HOSTNAME, ""));
            System.setProperty("http.proxyPort", (String)sequence.getAttribute(ConfigureProxiesForm.ATTR_HTTP_PROXY_PORT, "3128"));
            System.setProperty("com.maverick.ssl.https.HTTPProxyPort", (String)sequence.getAttribute(ConfigureProxiesForm.ATTR_HTTP_PROXY_PORT, "3128"));
            PropertyList list =(PropertyList)sequence.getAttribute(ConfigureProxiesForm.ATTR_HTTP_NON_PROXY_HOSTS, null);            
            StringBuffer hosts = new StringBuffer();
            for (Iterator i = list.iterator(); i.hasNext();) {
                if (hosts.length() != 0) {
                    hosts.append("|");
                }
                hosts.append(i.next());
            }
            System.setProperty("http.nonProxyHosts", hosts.toString());
            System.setProperty("com.maverick.ssl.https.HTTPProxyNonProxyHosts", hosts.toString());
            httpUsername = (String)sequence.getAttribute(ConfigureProxiesForm.ATTR_HTTP_PROXY_USERNAME, "");
            httpPassword = (String)sequence.getAttribute(ConfigureProxiesForm.ATTR_HTTP_PROXY_PASSWORD, "");
            System.setProperty("com.maverick.ssl.https.HTTPProxySecure", "false");
        }
        if(httpUsername != null || socksUsername != null) {
            Authenticator.setDefault(new ProxyAuthenticator(socksUsername, socksPassword, httpUsername, httpPassword));
        }
        
        ExtensionStore.getInstance().resetExtensionStoreUpdate();
        try {
            ExtensionStore.getInstance().getDownloadableExtensionStoreDescriptor(true);
            sequence.removeAttribute(ATTR_EXTENSION_STORE_EXCEPTION);
        }
        catch(Exception e) {
            log.error("Failed to connect to extension store.", e);
            sequence.putAttribute(ATTR_EXTENSION_STORE_EXCEPTION, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errs = new ActionErrors();
        if (isCommiting() && (useHTTPProxy || useSOCKSProxy)) {
            validateHostAndPortValues(errs);
            validateHostnames(errs);
        }
        return errs;
    }
    
    private void validateHostAndPortValues(ActionErrors errs) {
        String hostName = useHTTPProxy ? httpProxyHostname : socksProxyHostname;
        String portString = useHTTPProxy ? httpProxyPort : socksProxyPort;

        if ("".equals(hostName)) {
            errs.add(Globals.ERROR_KEY, new ActionMessage("installation.configureProxies.error.emptyHostName", hostName));
        }
        
        if ("".equals(portString)) {
            errs.add(Globals.ERROR_KEY, new ActionMessage("installation.configureProxies.error.emptyPort", hostName));
        } else {
            try {
                int port = Integer.parseInt(portString);
                if(!isPortValid(port)) {
                    errs.add(Globals.ERROR_KEY, new ActionMessage("installation.configureProxies.error.invalidPort"));
                }
            } catch (NumberFormatException e) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("installation.configureProxies.error.invalidPort"));
            }
        }
    }
    
    private static boolean isPortValid(int port) {
        return port >= 1 && port <= 65535;
    }

    private void validateHostnames(ActionErrors errs) {
        String hostName = useHTTPProxy ? httpProxyHostname : socksProxyHostname;
        if (!isValidIpAddress(hostName)) {
            errs.add(Globals.ERROR_KEY, new ActionMessage("installation.configureProxies.error.invalidHostName", hostName));
        }

        for (String address : httpNonProxyHosts) {
            if (!isValidIpAddress(address)) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("installation.configureProxies.error.invalidHttpNonProxyHost", address));
            }
        }
    }

    private static boolean isValidIpAddress(String ipAddress) {
        try {
            InetAddress.getByName(ipAddress);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }
    
    /**
     * @return Returns the useHTTPProxy.
     */
    public boolean getUseHTTPProxy() {
        return useHTTPProxy;
    }

    /**
     * @param useHTTPProxy The useHTTPProxy to set.
     */
    public void setUseHTTPProxy(boolean useHTTPProxy) {
        this.useHTTPProxy = useHTTPProxy;
    }

    /**
     * @return Returns the useSOCKSProxy.
     */
    public boolean getUseSOCKSProxy() {
        return useSOCKSProxy;
    }

    /**
     * @param useSOCKSProxy The useSOCKSProxy to set.
     */
    public void setUseSOCKSProxy(boolean useSOCKSProxy) {
        this.useSOCKSProxy = useSOCKSProxy;
    }

    /**
     * @return Returns the httpProxyHostname.
     */
    public String getHttpProxyHostname() {
        return httpProxyHostname;
    }

    /**
     * @param httpProxyHostname The httpProxyHostname to set.
     */
    public void setHttpProxyHostname(String httpProxyHostname) {
        this.httpProxyHostname = httpProxyHostname;
    }

    /**
     * @return String
     */
    public String getHttpNonProxyHosts() {
        return httpNonProxyHosts.getAsTextFieldText();
    }

    /**
     * @param httpNonProxyHosts
     */
    public void setHttpNonProxyHosts(String httpNonProxyHosts) {
        this.httpNonProxyHosts.setAsTextFieldText(httpNonProxyHosts);
    }

    /**
     * @return Returns the httpProxyPassword.
     */
    public String getHttpProxyPassword() {
        return httpProxyPassword;
    }

    /**
     * @param httpProxyPassword The httpProxyPassword to set.
     */
    public void setHttpProxyPassword(String httpProxyPassword) {
        this.httpProxyPassword = httpProxyPassword;
    }

    /**
     * @return Returns the httpProxyPort.
     */
    public String getHttpProxyPort() {
        return httpProxyPort;
    }

    /**
     * @param httpProxyPort The httpProxyPort to set.
     */
    public void setHttpProxyPort(String httpProxyPort) {
        this.httpProxyPort = httpProxyPort;
    }

    /**
     * @return Returns the httpProxyUsername.
     */
    public String getHttpProxyUsername() {
        return httpProxyUsername;
    }

    /**
     * @param httpProxyUsername The httpProxyUsername to set.
     */
    public void setHttpProxyUsername(String httpProxyUsername) {
        this.httpProxyUsername = httpProxyUsername;
    }

    /**
     * @return Returns the socksProxyHostname.
     */
    public String getSocksProxyHostname() {
        return socksProxyHostname;
    }

    /**
     * @param socksProxyHostname The socksProxyHostname to set.
     */
    public void setSocksProxyHostname(String socksProxyHostname) {
        this.socksProxyHostname = socksProxyHostname;
    }

    /**
     * @return Returns the socksProxyPassword.
     */
    public String getSocksProxyPassword() {
        return socksProxyPassword;
    }

    /**
     * @param socksProxyPassword The socksProxyPassword to set.
     */
    public void setSocksProxyPassword(String socksProxyPassword) {
        this.socksProxyPassword = socksProxyPassword;
    }

    /**
     * @return Returns the socksProxyPort.
     */
    public String getSocksProxyPort() {
        return socksProxyPort;
    }

    /**
     * @param socksProxyPort The socksProxyPort to set.
     */
    public void setSocksProxyPort(String socksProxyPort) {
        this.socksProxyPort = socksProxyPort;
    }

    /**
     * @return Returns the socksProxyUsername.
     */
    public String getSocksProxyUsername() {
        return socksProxyUsername;
    }

    /**
     * @param socksProxyUsername The socksProxyUsername to set.
     */
    public void setSocksProxyUsername(String socksProxyUsername) {
        this.socksProxyUsername = socksProxyUsername;
    }
    
    class ProxyAuthenticator extends Authenticator {
        
        String socksUsername, socksPassword, httpUsername, httpPassword;
        
        ProxyAuthenticator(String socksUsername, String socksPassword,
            String httpUsername, String httpPassword) {
            this.socksUsername = socksUsername;
            this.socksPassword = socksPassword;
            this.httpUsername = httpUsername;
            this.httpPassword = httpPassword;
        }

        public PasswordAuthentication getPasswordAuthentication() {
        	if (log.isInfoEnabled())
        		log.info("Requesting " + getRequestingProtocol() + " proxy authentication for " + getRequestingSite() + " ("
                            + getRequestingHost() + ":" + getRequestingPort() + "), prompt = " + getRequestingPrompt());
            String user = null;
            String pass = null;
            try {
                if (getRequestingProtocol().startsWith("SOCKS")) {
                    user = socksUsername;
                    pass = socksPassword;
                } else {
                    user = httpUsername;
                    pass = httpPassword;
                }
            } catch (Exception e) {
                log.error("Failed to get proxy authentication details.");
                return null;
            }
            return new PasswordAuthentication(user, pass.toCharArray());
        }

    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        useSOCKSProxy = false;
        useHTTPProxy = false;
    }
}