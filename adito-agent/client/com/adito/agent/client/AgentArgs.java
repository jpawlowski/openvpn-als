
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
			
package com.adito.agent.client;

import java.util.Locale;

public class AgentArgs {
    
    private String hostname;
    private int port = 443;
    boolean isSecure = true;
    private String username;
    private String password;
    private String ticket;
    private String logProperties;
    private int shutdown = -1;
    private String browserCommand;
    private String localProxyURL;
    private String pluginProxyURL;
    private String userAgent;
    private String extensionClasses;
    private String extensionId = "adito-agent";
    private String localeName = Locale.getDefault().toString();
    private AgentConfiguration agentConfiguration;
    private boolean disableNewSSLEngine;
    
    
    public String getBrowserCommand() {
        return browserCommand;
    }
    public void setBrowserCommand(String browserCommand) {
        this.browserCommand = browserCommand;
    }
    public String getExtensionClasses() {
        return extensionClasses;
    }
    public void setExtensionClasses(String extensionClasses) {
        this.extensionClasses = extensionClasses;
    }
    public String getHostname() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    public String getLocaleName() {
        return localeName;
    }
    public void setLocaleName(String localeName) {
        this.localeName = localeName;
    }
    public String getLocalProxyURL() {
        return localProxyURL;
    }
    public void setLocalProxyURL(String localProxyURL) {
        this.localProxyURL = localProxyURL;
    }
    public String getLogProperties() {
        return logProperties;
    }
    public void setLogProperties(String logProperties) {
        this.logProperties = logProperties;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPluginProxyURL() {
        return pluginProxyURL;
    }
    public void setPluginProxyURL(String pluginProxyURL) {
        this.pluginProxyURL = pluginProxyURL;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public int getShutdown() {
        return shutdown;
    }
    public void setShutdown(int shutdown) {
        this.shutdown = shutdown;
    }
    public String getTicket() {
        return ticket;
    }
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
    public String getUserAgent() {
        return userAgent;
    }
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public AgentConfiguration getAgentConfiguration() {
        return agentConfiguration;
    }
    public void setAgentConfiguration(AgentConfiguration agentConfiguration) {
        this.agentConfiguration = agentConfiguration;
    }
    public void setSecure(boolean isSecure) {
    	this.isSecure = isSecure;
    }
    public boolean isSecure() {
    	return isSecure;
    }
	public String getExtensionId() {
		return extensionId;
	}
	public void setExtensionId(String extensionId) {
		this.extensionId = extensionId;
	}
    public boolean isDisableNewSSLEngine() {
        return disableNewSSLEngine;
    }
    public void setDisableNewSSLEngine(boolean disableNewSSLEngine) {
        this.disableNewSSLEngine = disableNewSSLEngine;
    }
    
}
