
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

import com.maverick.http.AuthenticationPrompt;
import com.maverick.http.HttpAuthenticator;

/**
 * In order to be able to control the <i>Agent</i>, a <i>GUI</i> must be
 * available. The actual implementation of the GUI may vary from platform to
 * platform. For example, on Windows the <i>SystemTrayGUI</i> is used which
 * adds new actions to the tray area. Currently, all ofther platforms use the
 * <i>BasicFrameGUI</i>
 */
public interface AgentClientGUI extends AuthenticationPrompt {

	/**
	 * Dialog type of information
	 */
    public static final int INFORMATION = 0;

	/**
	 * Dialog type of question
	 */
    public static final int QUESTION = 1;

	/**
	 * Dialog type of warning
	 */
    public static final int WARNING = 2;

	/**
	 * Dialog type of error
	 */
    public static final int ERROR = 3;

    /**
     * Initialise the GUI. The provided listener should have
     * its methods invoked to control the client.
     * 
     * @param agent agent
     */
    public void init(Agent agent);

    /**
     * The client is now idle, update the GUI component to
     * reflect this. 
     */
    public void showIdle();

    /**
     * The client is now disconnected, update the GUI component to
     * reflect this.
     */
    public void showDisconnected();


    /**
     * The client is now transmitting data, update the GUI component to
     * reflect this.
     */
    public void showTx();


    /**
     * The client is now receiving data, update the GUI component to
     * reflect this.
     */
    public void showRx();


    /**
     * The client is now transmitting and receving, update the GUI component to
     * reflect this.
     */
    public void showTxRx();


    /**
     * Set the information text
     * 
     * @param info information text
     */
    public void setInfo(String info);

    /**
     * Display a confirmation dialog.
     * 
     * @param dialogType (see {@link #INFORMATION}, {@link #ERROR}, {@link #QUESTION} and {@link #WARNING} constants
     * @param okText text for OK button
     * @param cancelText text for Cancel button or <code>null</code> to omit the cancel button
     * @param title title
     * @param message message
	 * @return ok pressed 
     */
    public boolean confirm(int dialogType, String okText, String cancelText, String title, String message);
    

	/**
	 * Display an error dialog
	 * 
     * @param okText text for OK button
     * @param cancelText text for Cancel button or <code>null</code> to omit the cancel button
     * @param title title
     * @param message message
	 * @param ex exception
	 * @return ok pressed 
	 */
	public boolean error(String okText, String cancelText, String title, String message, Throwable ex);

    /**
     * Popup a new message.
     * 
     * @param callback callback invoked if message is click
     * @param message message text
     * @param title message title
     * @param image message image
     * @param timeout time for message to stay visible
     */
    public void popup(ActionCallback callback, String message, String title, String imageName, int timeout);
    
    /**
     * Create and display new task progress monitor
     */
    public TaskProgress createTaskProgress(String message, String note, long maxValue, boolean allowCancel);
    
    /**
     * Get the port monitor
     * 
     * @return port monitor
     */
    public PortMonitor getPortMonitor();
    
    /**
     * Get the debug console
     * 
     * @return console
     */
    public Console getConsole();

	/**
	 * Prompt for HTTP credentials.
	 * 
	 * @param proxy authentication is for proxy
	 * @param authenticator authenticator
	 * @return ok
	 */
	public boolean promptForCredentials(boolean proxy, HttpAuthenticator authenticator);
    
    /**
     * Dispose of the GUI
     */
    public void dispose();

	/**
	 * Add a new submenu to the root menu.
	 * 
	 * @param name name
	 */
	public void addMenu(final String name);

	/**
	 * Remove a submenu from the root menu.
	 * 
	 * @param name name
	 */
	public void removeMenu(String name);

	/**
	 * Clear all items from a menu
	 * 
	 * @param name name
	 */
	public void clearMenu(String name);

	/**
	 * Get if a root menu exists
	 * 
	 * @param name name
	 * @return menu exists
	 */
	public boolean isMenuExists(String name);

    /**
     * Add a new action to the GUI's menu. The action text may be
     * retrieved from {@link AgentAction#getAction()}, 
     * {@link AgentAction#actionPerformed()} must be
     * invoked to perform the action. The item may be added to
     * add sub-menu (see {@link #addMenu(String)} if the parentName
     * argument is not null.
     * 
     * @param parentName sub-menu to add to or <code>null</code> to add to parent
     * @param action action to add
     */
	public void addMenuItem(final String parentName, final AgentAction action);

    /**
     * Add a separator the GUI's menu. The item may be added to
     * add sub-menu (see {@link #addMenu(String)} if the parentName
     * argument is not null. 
     * 
     * @param parentName sub-menu to add to or <code>null</code> to add to parent
     */	
	public void addMenuSeperator(final String parentName);
	
	/**
	 * Open the system browser, optionally to the specified path. The
	 * protocol, host and port elements of the URL will automatically
	 * be created and prepend to any supplied path. If path is <code>null</code>
	 * then the browser will just be opened and pointed to the Adito
	 * index page.
	 * 
	 * @param path path
	 */
	public void openBrowser(String path);
	
	
}