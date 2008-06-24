
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
			
package com.adito.agent.client.gui.awt;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import com.maverick.http.HttpAuthenticator;
import com.sshtools.ui.awt.Toaster;
import com.sshtools.ui.awt.UIUtil;
import com.sshtools.ui.awt.options.Option;
import com.sshtools.ui.awt.options.OptionDialog;
import com.adito.agent.client.ActionCallback;
import com.adito.agent.client.Agent;
import com.adito.agent.client.AgentClientGUI;
import com.adito.agent.client.Console;
import com.adito.agent.client.PortMonitor;
import com.adito.agent.client.TaskProgress;
import com.adito.agent.client.tunneling.AbstractPortItem;
import com.adito.agent.client.util.BrowserLauncher;

/**
 * Abstract implementation of a {@link AgentClientGUI} that provides a
 * <i>Toaster</i> style popup component.
 */
public abstract class AbstractAWTGUI implements AgentClientGUI {

    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(AbstractAWTGUI.class);
    // #endif

	// Private instance variables
	private Toaster popup;
	private Image popupImage;
	private PortMonitor portMonitor;
	private Console console;
    private Agent agent;

	/**
	 * Constructor.
	 */
	public AbstractAWTGUI() {
		OptionDialog.INFORMATION_ICON = "/images/dialog-information.gif"; //$NON-NLS-1$
		OptionDialog.WARNING_ICON = "/images/dialog-warning.gif"; //$NON-NLS-1$
		OptionDialog.QUESTION_ICON = "/images/dialog-question.gif"; //$NON-NLS-1$
		OptionDialog.ERROR_ICON = "/images/dialog-error.gif"; //$NON-NLS-1$
		popup = new Toaster(Toaster.BOTTOM_RIGHT, new Dimension(300, 100));
		popup.setTextAlign(Component.LEFT_ALIGNMENT);
		Font norm = new Font("Arial", Font.PLAIN, 10); //$NON-NLS-1$
		Font title = new Font("Arial Bold", Font.BOLD, 11); //$NON-NLS-1$
		popup.setTextFont(norm);
		popup.setTitleFont(title);
		popupImage = UIUtil.loadImage(AbstractAWTGUI.class, "/images/agent.gif");

	}
    
    /* (non-Javadoc)
     * @see com.adito.agent.client.AgentClientGUI#init(com.adito.agent.client.Agent)
     */
    public void init(Agent agent) {
        this.agent = agent;        
    }

    /**
     * Get the Agent instance this GUI is being used for
     * 
     * @return agent
     */
    public Agent getAgent() {
        return agent;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.agent.client.AgentClientGUI#confirm(int, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public boolean confirm(int dialogType, String okText, String cancelText, String title, String message) {
		Option ok = new Option(okText);
		Option cancel = cancelText == null ? null : new Option(cancelText);
		if (OptionDialog.prompt(getGUIComponent(), dialogType, title, message, cancel == null ? new Option[] { ok }
			: new Option[] { ok, cancel }) != ok) { //$NON-NLS-1$$  //$NON-NLS-2$$
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.agent.client.AgentClientGUI#error(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.Throwable)
	 */
	public boolean error(String okText, String cancelText, String title, String message, Throwable ex) {
		Option ok = new Option(okText);
		Option cancel = cancelText == null ? null : new Option(cancelText);
		if (OptionDialog.error(getGUIComponent(), title, message, ex, cancel == null ? new Option[] { ok }
			: new Option[] { ok, cancel }) != ok) { //$NON-NLS-1$$  //$NON-NLS-2$$
			return false;
		}
		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.vpn.client.VPNClientGUI#getGUIComponent()
	 */
	public abstract Component getGUIComponent();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.vpn.client.VPNClientGUI#popup(java.awt.event.ActionListener,
	 *      java.lang.String, java.lang.String, java.awt.Image, int)
	 */
	public void popup(final ActionCallback callback, String message, String title, String imageName, int timeout) {
		Image image = imageName == null ? null : UIUtil.loadImage(AbstractAWTGUI.class, "/images/" + imageName + ".gif");
		ActionListener l = callback == null ? null : new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				callback.actionPerformed();
			}
		};
		popup.popup(l, message, title, image == null ? popupImage : image, timeout);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.agent.client.AgentClientGUI#createTaskProgress(java.lang.String,
	 *      java.lang.String, int, int)
	 */
	public TaskProgress createTaskProgress(String message, String title, long maxValue, boolean allowCancel) {
		TaskProgress progress = new TaskProgressBar(getGUIComponent(), message, title, maxValue, allowCancel);
		return progress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.agent.client.AgentClientGUI#getPortMonitor()
	 */
	public PortMonitor getPortMonitor() {
		if (portMonitor == null) {
			portMonitor = new AWTPortMonitor(agent);
		}
		return portMonitor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.adito.agent.client.AgentClientGUI#getConsole()
	 */
	public synchronized Console getConsole() {
		if (console == null) {
			console = new AWTConsoleOutputStream("true".equals(System.getProperty("console.toSysOut", "false")) ? System.out : null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return console;
	}

	/* (non-Javadoc)
	 * @see com.adito.agent.client.AgentClientGUI#promptForCredentials(com.maverick.http.HttpAuthenticator)
	 */
	public boolean promptForCredentials(boolean proxy, HttpAuthenticator authenticator) {
		return AWTAuthenticationDialog.promptForCredentials(proxy, authenticator);
	}

    /* (non-Javadoc)
     * @see com.adito.agent.client.AgentClientGUI#openBrowser(java.lang.String)
     */
    public void openBrowser(String path) {
        try {
        	String browserPath = "https://" + agent.getAditoHost() + ":" + agent.getAditoPort() + //$NON-NLS-1$ //$NON-NLS-2$ 
        	( path == null ? "" : ( "/" + path ));
            // #ifdef DEBUG
            log.info("Opening browser to " + browserPath);
            // #endif
            BrowserLauncher.openURL(browserPath); 
        } catch (IOException ioe) {
            // #ifdef DEBUG
            log.error(ioe);
            // #endif
        }        
    }

	public void dispose() {		
		getPortMonitor().dispose();
		getConsole().dispose();
	}

	/**
	 * Adapts the AWT progress monitor to {@link TaskProgress}
	 */
	class TaskProgressBar extends ProgressBar implements TaskProgress {

		public TaskProgressBar(Component parentComponent, String message, String title, long maxValue, boolean allowCancel) {
			super(parentComponent, message, title, maxValue, allowCancel);
		}

	}

	class AWTPortMonitor implements PortMonitor {

		AWTPortMonitorWindow portMonitorWindow;

		AWTPortMonitor(Agent agent) {
			portMonitorWindow = new AWTPortMonitorWindow(agent);
			portMonitorWindow.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent evt) {
					portMonitorWindow.setVisible(false);
				}
			});
		}

		public void addPortItem(AbstractPortItem portItem) {
			portMonitorWindow.getModel().addPortItem(portItem);
		}

		public int getIndexForId(int id) {
			return portMonitorWindow.getModel().getIndexForId(id);
		}

		public AbstractPortItem getItemAt(int idx) {
			return portMonitorWindow.getModel().getItemAt(idx);
		}

		public boolean isVisible() {
			return portMonitorWindow.isVisible();
		}

		public void removeItemAt(int idx) {
			portMonitorWindow.getModel().removeItemAt(idx);
		}

		public void setVisible(boolean visible) {
			boolean wasVisible = isVisible();
			portMonitorWindow.setVisible(visible);
			if (visible && !wasVisible) {
				portMonitorWindow.toFront();
			}
		}

		public void updateItemAt(int idx) {
			portMonitorWindow.getModel().updateItemAt(idx);
		}

		public void dispose() {
			portMonitorWindow.dispose();			
		}
	}

}