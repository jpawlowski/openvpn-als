
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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.PropertyList;
import com.adito.core.UserDatabaseManager;
import com.adito.security.UserDatabaseDefinition;
import com.adito.tasks.TaskUtil;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.forms.DefaultWizardForm;

public class InstallationSummaryForm extends DefaultWizardForm {
    
    final static Log log = LogFactory.getLog(InstallationSummaryForm.class);

    // Private statics for sequence attributes
    
    // Private instance variables
    private String certificateSource;
    private String webServerPort;
    private String webServerProtocol;
    private List webServerListeningInterfaces;
    private List webServerValidExternalHostnames;
    private boolean useSOCKSProxy;
    private boolean useHTTPProxy;
    private String socksProxyHostname;
    private String socksProxyPort;
    private boolean socksProxyAuthenticate;
    private String httpProxyHostname;
    private String httpProxyPort;
    private boolean httpProxyAuthenticate;
    private List extensionsToInstall;
    private UserDatabaseDefinition userDatabaseDefinition;
    private boolean configurePassword;
    private HttpSession session;

    public InstallationSummaryForm() {
        super(false, true, "/WEB-INF/jsp/content/install/installationSummary.jspf",
            "", true, true, "installationSummary", "install", "installation.installationSummary", 6);
    }

    @Override
    public String getFinishOnClick() {
        return TaskUtil.getTaskPathOnClick(getWizardSequence().getFinishActionForward().getPath(), "install", "install", session, 440, 150);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.forms.AbstractWizardForm#init(com.adito.wizard.AbstractWizardSequence)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request)  throws Exception {
        super.init(sequence, request);
        session = request.getSession();
        certificateSource = (String)sequence.getAttribute(SelectCertificateSourceForm.ATTR_CERTIFICATE_SOURCE, "");
        webServerPort =  (String)sequence.getAttribute(WebServerForm.ATTR_WEB_SERVER_PORT, "");
        webServerProtocol =  (String)sequence.getAttribute(WebServerForm.ATTR_WEB_SERVER_PROTOCOL, "http");
        webServerListeningInterfaces =  PropertyList.createFromTextFieldText((String)sequence.getAttribute(WebServerForm.ATTR_LISTENING_INTERFACES, ""));
        webServerValidExternalHostnames =  PropertyList.createFromTextFieldText((String)sequence.getAttribute(WebServerForm.ATTR_VALID_EXTERNAL_HOSTS, ""));
        useSOCKSProxy = "true".equals((String)sequence.getAttribute(ConfigureProxiesForm.ATTR_USE_SOCKS_PROXY, ""));
        useHTTPProxy = "true".equals((String)sequence.getAttribute(ConfigureProxiesForm.ATTR_USE_HTTP_PROXY, ""));
        socksProxyHostname = (String)sequence.getAttribute(ConfigureProxiesForm.ATTR_SOCKS_PROXY_HOSTNAME, "");
        socksProxyPort = (String)sequence.getAttribute(ConfigureProxiesForm.ATTR_SOCKS_PROXY_PORT, "");
        socksProxyAuthenticate= !("".equals((String)sequence.getAttribute(ConfigureProxiesForm.ATTR_SOCKS_PROXY_USERNAME, "")));
        httpProxyHostname = (String)sequence.getAttribute(ConfigureProxiesForm.ATTR_HTTP_PROXY_HOSTNAME, "");
        httpProxyPort = (String)sequence.getAttribute(ConfigureProxiesForm.ATTR_HTTP_PROXY_PORT, "");
        httpProxyAuthenticate= !("".equals((String)sequence.getAttribute(ConfigureProxiesForm.ATTR_HTTP_PROXY_USERNAME, "")));
        
        userDatabaseDefinition = UserDatabaseManager.getInstance().getUserDatabaseDefinition((String)sequence.getAttribute(SelectUserDatabaseForm.ATTR_USER_DATABASE, ""));
        
    }

    /**
     * @return Returns the certificateSource.
     */
    public String getCertificateSource() {
        return certificateSource;
    }

    /**
     * @return Returns the httpProxyHostname.
     */
    public String getHttpProxyHostname() {
        return httpProxyHostname;
    }

    /**
     * @return Returns the httpProxyPort.
     */
    public String getHttpProxyPort() {
        return httpProxyPort;
    }

    /**
     * @return Returns the socksProxyHostname.
     */
    public String getSocksProxyHostname() {
        return socksProxyHostname;
    }

    /**
     * @return Returns the socksProxyPort.
     */
    public String getSocksProxyPort() {
        return socksProxyPort;
    }

    /**
     * @return Returns the useHTTPProxy.
     */
    public boolean getUseHTTPProxy() {
        return useHTTPProxy;
    }

    /**
     * @return Returns the useSOCKSProxy.
     */
    public boolean getUseSOCKSProxy() {
        return useSOCKSProxy;
    }

    /**
     * @return Returns the webServerListeningInterfaces.
     */
    public List getWebServerListeningInterfaces() {
        return webServerListeningInterfaces;
    }

    /**
     * @return Returns the webServerPort.
     */
    public String getWebServerPort() {
        return webServerPort;
    }

    /**
     * @return Returns the webServerValidExternalHostnames.
     */
    public List getWebServerValidExternalHostnames() {
        return webServerValidExternalHostnames;
    }

    /**
     * @return Returns the httpProxyAuthenticate.
     */
    public boolean getHttpProxyAuthenticate() {
        return httpProxyAuthenticate;
    }

    /**
     * @return Returns the socksProxyAuthenticate.
     */
    public boolean getSocksProxyAuthenticate() {
        return socksProxyAuthenticate;
    }

    /**
     * @return Returns the extensionsToInstall.
     */
    public List getExtensionsToInstall() {
        return extensionsToInstall;
    }

    /**
     * @return Returns the configurePassword.
     */
    public boolean getConfigurePassword() {
        return configurePassword;
    }

    /**
     * @return Returns the userDatabase.
     */
    public UserDatabaseDefinition getUserDatabaseDefinition() {
        return userDatabaseDefinition;
    }

    public String getWebServerProtocol() {
        return webServerProtocol;
    }

    public void setWebServerProtocol(String webServerProtocol) {
        this.webServerProtocol = webServerProtocol;
    }
}
