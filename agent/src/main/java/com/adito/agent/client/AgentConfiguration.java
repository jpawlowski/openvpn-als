
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

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import com.adito.agent.client.util.Utils;


/**
 * This class holds various configuration options for the <i>Agent</i>.
 * Each Agent instance is constructed with an instance of AgentConfiguration
 * as a parameter. These parameters are read from the profile selected
 * by the user during login.
 */
public class AgentConfiguration {
	
	//	Protected instance variables
	protected String guiClassName;
	protected boolean displayInformationPopups = true;
	protected boolean remoteTunnelsRequireConfirmation = false;
	protected int shutdownPeriod = 10000;
	protected boolean systemExitOnDisconnect = false;
    protected int webForwardInactivity = 300000; // 5 mins
    protected int tunnelInactivity = 600000; // 10 mins
    protected String agentType = "aditoAgent";
	protected boolean getResources = true;
	protected boolean cleanonExit = false;
	protected File cacheDir;
	protected Vector removeFiles = new Vector();
	protected int keepAlivePeriod = 30000;
	protected int keepAliveTimeout = 30000;
	protected String localhostAddress = "127.0.0.1";
	
	/**
	 * Get the <i>Localhost Address</i>. This is used whenever
	 * the 'localhost' address should be used. For example, this may be 
	 * set to 127.0.0.2 as a work around on systems where 127.0.0.1
	 * does not work correctly (RDP on Windows XP SP1).
	 * 
	 * @return localhost address
	 */
	public String getLocalhostAddress() {
		return localhostAddress;
	}
	
	/**
	 * Set the <i>Localhost Address</i>. This is used whenever
	 * the 'localhost' address should be used. For example, this may be 
	 * set to 127.0.0.2 as a work around on systems where 127.0.0.1
	 * does not work correctly (RDP on Windows XP SP1).
	 * 
	 * @return localhost address
	 */
	public void setLocalhostAddress(String localhostAddress) {
		this.localhostAddress = localhostAddress;
	}
    
    /**
     * Get the <i>Agent Type</i>. This is a name the agent will
     * be identified to the server. For example, the name may
     * be used on the server as if this client requires particular
     * authentication behaviour.
     * 
     * @return agent
     */
    public String getAgentType() {
    	return agentType;
    }
    
    /**
     * Get the <i>Agent Type</i>. This is a name the agent will
     * be identified to the server. For example, the name may
     * be used on the server as if this client requires particular
     * authentication behaviour.
     * 
     * @param agentType agent
     */
    public void setAgentType(String agentType) {
    	this.agentType = agentType;
    }

	/**
	 * Set whether the agent should attempt to get Adito resources and
	 * add launcher shortcuts to the {@link AgentClientGUI} implementation.
	 * 
	 * @param getResources whether to get Adito resources
	 */
	public void setGetResources(boolean getResources) {
		this.getResources = getResources;
	}

	/**
	 * Get whether the agent should attempt to get Adito resources and
	 * add launcher shortcuts to the {@link AgentClientGUI} implementation.
	 * 
	 * @return whether to get Adito resources
	 */
	public boolean isGetResources() {
		return getResources;
	}

    /**
     * Set the number of milliseconds a web forward tunnel must be inactive for
     * before being considered stale.
     * 
     * @param webForwardInactivity web forward tunnel inactivity timeout (ms)
     */
    public void setWebForwardInactivity(int webForwardInactivity) {
        this.webForwardInactivity = webForwardInactivity;
    }

    /**
     * Set the number of milliseconds a tunnel must be inactive for before being
     * considered stale.
     * 
     * @param tunnelInactivity tunnel inactivity timeout (ms)
     */
    public void setTunnelInactivity(int tunnelInactivity) {
        this.tunnelInactivity = tunnelInactivity;
    } 
    
    /**
     * Get the number of milliseconds a web forward tunnel must be inactive for
     * before being considered stale.
     * 
     * @return web forward tunnel inactivity timeout (ms)
     */
    public int getWebForwardInactivity() {
        return webForwardInactivity;
    }

    /**
     * Get the number of milliseconds a tunnel must be inactive for before being
     * considered stale.
     * 
     * @return tunnel inactivity timeout (ms)
     */
    public int getTunnelInactivity() {
        return tunnelInactivity;
    }
	
	/**
	 * Get if information popup messages should be displayed. Most messages are
	 * informational, it is only really remote tunnel confirmation and agent
	 * messages that are not.
	 * 
	 * @return display information popups
	 */
	public boolean isDisplayInformationPopups() {
		return displayInformationPopups;
	}
	
	/**
	 * Set if information popup messages should be displayed. Most messages are
	 * informational, it is only really remote tunnel confirmation and agent
	 * messages that are not.
	 * 
	 * @param displayInformationPopups display information popups
	 */
	public void setDisplayInformationPopups(boolean displayInformationPopups) {
		this.displayInformationPopups = displayInformationPopups;
	}
	
	/**
	 * Get the class name to use for the GUI.
	 * 
	 * @return gui class name
	 */
	public String getGUIClass() {
		return guiClassName;
	}
	
	/**
	 * Set the class name to use for the GUI.
	 * 
	 * @param guiClassName gui class
	 */
	public void setGUIClass(String guiClassName) {
		this.guiClassName = guiClassName;
	}
	
	/**
	 * Get if incoming remote tunnels should require acceptance by the 
	 * local user.
	 * 
	 * @return remote tunnels require confirmation
	 */
	public boolean isRemoteTunnelsRequireConfirmation() {
		return remoteTunnelsRequireConfirmation;
	}

	/**
	 * Set if incoming remote tunnels should require acceptance by the 
	 * local user.
	 * 
	 * @param remoteTunnelsRequireConfirmation  remote tunnels require confirmation
	 */
	public void setRemoteTunnelsRequireConfirmation(boolean remoteTunnelsRequireConfirmation) {
		this.remoteTunnelsRequireConfirmation = remoteTunnelsRequireConfirmation;
	}

	/**
	 * Get the shutdown period in milliseconds
	 * 
	 * @return shutdown period
	 */
	public int getShutdownPeriod() {
		return shutdownPeriod;
	}

	/**
	 * Set the shutdown period in milliseconds
	 * 
	 * @parm shutdownPeriod shutdown period
	 */
	public void setShutdownPeriod(int shutdownPeriod) {
		this.shutdownPeriod = shutdownPeriod;
	}

	/**
	 * Get if {@link System#exit(int)} should be called when
	 * the agent disconnects.
	 * 
	 * @return system exit on disconnect
	 */
	public boolean isSystemExitOnDisconnect() {
		return systemExitOnDisconnect;
	}

	/**
	 * Set if {@link System#exit(int)} should be called when
	 * the agent disconnects.
	 * 
	 * @param systemExitOnDisconnect system exit on disconnect
	 */
	public void setSystemExitOnDisconnect(boolean systemExitOnDisconnect) {
		this.systemExitOnDisconnect = systemExitOnDisconnect;
	}

	/**
	 * Get whether the agent should clean up all downloaded files
	 * (except itself) when it exists. Use with care and ensure {@link getCacheDir} is 
	 * pointing to the correct directory as all files in this 
	 * directory will be removed without confirmation.
	 * 
	 * @return clean on exit
	 * @see #getCacheDir()
	 */
	public boolean isCleanOnExit() {
		return cleanonExit;
	}

	/**
	 * Set whether the agent should clean up its cache directory upon
	 * logging out. Use with care and ensure {@link getCacheDir} is 
	 * pointing to the correct directory as all files in this 
	 * directory will be removed without confirmation.
	 * 
	 * @param clean on exit
	 * @see #getCacheDir()
	 */
	public void setCleanOnExit(boolean cleanonExit) {
		this.cleanonExit = cleanonExit;
	}
	
	/**
	 * Set the Agent cache directory. This is the temporary directory
	 * where resources such as application files are stored. 
	 * 
	 * @param cacheDir agent cache directory
	 */
	public void setCacheDir(File cacheDir) {
		this.cacheDir = cacheDir;
	}
	
	/**
	 * Get the Agent cache directory. This is the temporary directory
	 * where resources such as application files are stored.
	 * 
	 * @return agent cache directory
	 */
	public File getCacheDir() {
		return cacheDir;
	}

	/**
	 * The keep alive period. A fake request will be sent to the server 
	 * to ensure that traffic flows over the connection and that firewalls
	 * etc do not timeout the connection. 
	 * @return
	 */
	public int getKeepAlivePeriod() {
		return keepAlivePeriod;
	}

	/**
	 * The keep alive period. A fake request will be sent to the server 
	 * to ensure that traffic flows over the connection and that firewalls
	 * etc do not timeout the connection. 
	 * @param keepAlivePeriod
	 */
	public void setKeepAlivePeriod(int keepAlivePeriod) {
		this.keepAlivePeriod = keepAlivePeriod;
	}
	
	/**
	 * In addition to the cache dir, the command line can also specify files or directories
	 * that require removal when the agent exits. 
	 * @param f
	 */
	public void removeFileOnExit(File f) {
		removeFiles.addElement(f);
	}
	
	public Enumeration getFilesToRemove() {
		return removeFiles.elements();
	}

	/**
	 * The number of milliseconds to wait before timing out a keep alive
	 * request.
	 * @return
	 */
	public int getKeepAliveTimeout() {
		return keepAliveTimeout;
	}

	/**
	 * The number of milliseconds to wait before timing out a keep alive
	 * request.
	 * @param keepAliveTimeout
	 */
	public void setKeepAliveTimeout(int keepAliveTimeout) {
		this.keepAliveTimeout = keepAliveTimeout;
	}

}
