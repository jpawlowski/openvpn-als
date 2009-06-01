
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.agent.client.gui.swt;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import com.maverick.http.HttpAuthenticator;
import net.openvpn.als.agent.client.ActionCallback;
import net.openvpn.als.agent.client.Agent;
import net.openvpn.als.agent.client.AgentAction;
import net.openvpn.als.agent.client.AgentClientGUI;
import net.openvpn.als.agent.client.Console;
import net.openvpn.als.agent.client.PortMonitor;
import net.openvpn.als.agent.client.TaskProgress;
import net.openvpn.als.agent.client.util.BrowserLauncher;

/**
 * {@link AbstractAWTGUI} implementation that uses the system tray API provided
 * with SWT.
 */
public class SWTSystemTrayGUI implements AgentClientGUI {

	// #ifdef DEBUG
	static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(SWTSystemTrayGUI.class);
	// #endif

	final int IDLE_ICON = 0;
	final int TX_ICON = 1;
	final int RX_ICON = 2;
	final int TXRX_ICON = 3;
	final int DISCONNECTED_ICON = 4;

	/**
	 * Default timeout
	 */
	public final static int DEFAULT_TIMEOUT = 10000;

	// Private instance variables

	private Image[] icons;
	private Tray tray;
	private TrayItem trayItem;
	private Display display;
	private Agent agent;
	private Menu popupMenu;
	private Shell shell;
	private Object initLock = new Object() {
	};
	private SWTBalloonWindow balloon;
	private Label messageLabel;
	private PortMonitor portMonitor;
	private PopupTimer popupTimer;
	private Console console;
	private int menuIdx = 0;

	private Hashtable menuLookup = new Hashtable();
	private Hashtable menuItemLookup = new Hashtable();

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.vpn.client.VPNClientGUI#init(net.openvpn.als.vpn.client.VPNClientGUIListener)
	 */
	public void init(Agent agent) {
		this.agent = agent;
		Thread t = new Thread() {
			public void run() {
				doInit();
				eventLoop();
			}
		};
		t.start();
		synchronized (initLock) {
			try {
				initLock.wait();
			} catch (InterruptedException e) {
			}
		}

	}

	public Menu getMenu() {
		return popupMenu;
	}

	public Agent getAgent() {
		return agent;
	}

	public Display getDisplay() {
		return display;
	}

	public Shell getShell() {
		return shell;
	}

	protected void doInit() {
		//
		display = new Display();
		shell = new Shell(display);

		// Load the icons
		icons = new Image[5];
		icons[IDLE_ICON] = loadImage(SWTSystemTrayGUI.class, "/images/tray-idle.gif"); //$NON-NLS-1$
		icons[TX_ICON] = loadImage(SWTSystemTrayGUI.class, "/images/tray-tx.gif"); //$NON-NLS-1$
		icons[RX_ICON] = loadImage(SWTSystemTrayGUI.class, "/images/tray-rx.gif"); //$NON-NLS-1$
		icons[TXRX_ICON] = loadImage(SWTSystemTrayGUI.class, "/images/tray-txrx.gif"); //$NON-NLS-1$
		icons[DISCONNECTED_ICON] = loadImage(SWTSystemTrayGUI.class, "/images/tray-disconnecting.gif"); //$NON-NLS-1$

		// Create the menu
		popupMenu = new Menu(shell, SWT.POP_UP);

		MenuItem open = new MenuItem(popupMenu, SWT.PUSH);
		open.setText(Messages.getString("GUI.menu.openBrowser")); //$NON-NLS-1$
		open.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				openBrowser(null);
			}
		});

		// #ifdef DEBUG
		MenuItem console = new MenuItem(popupMenu, SWT.PUSH);
		console.setText(Messages.getString("GUI.menu.debugConsole")); //$NON-NLS-1$
		console.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				getConsole().show();
			}
		});
		// #endif
		MenuItem ports = new MenuItem(popupMenu, SWT.PUSH);
		ports.setText(Messages.getString("GUI.menu.tunnelMonitor")); //$NON-NLS-1$
		ports.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				getPortMonitor().setVisible(!getPortMonitor().isVisible());
			}
		});
		MenuItem about = new MenuItem(popupMenu, SWT.PUSH);
		about.setText(Messages.getString("GUI.menu.about")); //$NON-NLS-1$
		about.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				about();
			}
		});

		// Create the menu items
		new MenuItem(popupMenu, SWT.SEPARATOR);
		MenuItem exit = new MenuItem(popupMenu, SWT.PUSH);
		exit.setText(Messages.getString("GUI.menu.exit")); //$NON-NLS-1$
		exit.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				exit();
			}
		});

		// Create the tray item
		tray = display.getSystemTray();
		if (tray == null) {
			// #ifdef DEBUG
			log.error("The system tray is not available");
			// #endif
		} else {
			// Create the icon
			trayItem = new TrayItem(tray, SWT.NONE);
			trayItem.setToolTipText(Messages.getString("GUI.appName"));
			trayItem.addListener(SWT.Show, new Listener() {
				public void handleEvent(Event event) {
				}
			});
			trayItem.addListener(SWT.Hide, new Listener() {
				public void handleEvent(Event event) {
				}
			});
			trayItem.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
				}
			});
			trayItem.addListener(SWT.DefaultSelection, new Listener() {
				public void handleEvent(Event event) {
				}
			});
			trayItem.addListener(SWT.MenuDetect, new Listener() {
				public void handleEvent(Event event) {
					popupMenu.setVisible(true);
				}
			});
			trayItem.setImage(icons[IDLE_ICON]);
		}
	}

	void about() {
		final Image image = loadImage(SWTSystemTrayGUI.class, "/images/frame-agent.png");
		SWTAboutDialog about = new SWTAboutDialog(shell,
						Messages.getString("About.close"),
						Messages.getString("About.title"),
						image,
						MessageFormat.format(Messages.getString("About.message"), new Object[] { agent.getServerVersion() }),
						Messages.getString("About.description"),
						Messages.getString("About.copyright"),
						Messages.getString("About.link"));

		if (agent.getState() != Agent.STATE_DISCONNECTED) {

			Composite shell = about.getAccessory();

			Composite c = new Composite(shell, 0);
			GridLayout gridLayout = new GridLayout();
			c.setLayout(gridLayout);

			Label host = new Label(c, SWT.WRAP);
			host.setText(MessageFormat.format(Messages.getString("About.host"), new Object[] { agent.getOpenVPNALSHost() }));
			GridData data = new GridData();
			data.horizontalAlignment = GridData.BEGINNING;
			data.grabExcessHorizontalSpace = true;
			host.setLayoutData(data);

			Label port = new Label(c, SWT.WRAP);
			port.setText(MessageFormat.format(Messages.getString("About.port"),
				new Object[] { String.valueOf(agent.getOpenVPNALSPort()) }));
			data = new GridData();
			data.horizontalAlignment = GridData.BEGINNING;
			data.grabExcessHorizontalSpace = true;
			port.setLayoutData(data);

			Label username = new Label(c, SWT.WRAP);
			username.setText(MessageFormat.format(Messages.getString("About.username"), new Object[] { agent.getUsername() }));
			data = new GridData();
			data.horizontalAlignment = GridData.BEGINNING;
			data.grabExcessHorizontalSpace = true;
			username.setLayoutData(data);

			Label serverVersion = new Label(c, SWT.WRAP);
			serverVersion.setText(MessageFormat.format(Messages.getString("About.serverVersion"),
				new Object[] { agent.getServerVersion() }));
			data = new GridData();
			data.horizontalAlignment = GridData.BEGINNING;
			data.grabExcessHorizontalSpace = true;
			serverVersion.setLayoutData(data);

			Label agentVersion = new Label(c, SWT.WRAP);
			agentVersion.setText(MessageFormat.format(Messages.getString("About.agentVersion"),
				new Object[] { agent.getClientVersion() }));
			data = new GridData();
			data.horizontalAlignment = GridData.BEGINNING;
			data.grabExcessHorizontalSpace = true;
			agentVersion.setLayoutData(data);
		}

		about.open();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.agent.client.AgentClientGUI#openBrowser(java.lang.String)
	 */
	public void openBrowser(String path) {
		try {
			String browserPath = "https://" + agent.getOpenVPNALSHost() + ":" + agent.getOpenVPNALSPort() + //$NON-NLS-1$ //$NON-NLS-2$ 
				(path == null ? "" : ("/" + path));
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

	void eventLoop() {
		synchronized (initLock) {
			initLock.notify();
		}
		while (true) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.vpn.client.VPNClientGUI#showIdle()
	 */
	public void showIdle() {
		setImage(icons[IDLE_ICON]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.vpn.client.VPNClientGUI#showDisconnected()
	 */
	public void showDisconnected() {
		setImage(icons[DISCONNECTED_ICON]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.vpn.client.VPNClientGUI#showTx()
	 */
	public void showTx() {
		setImage(icons[TX_ICON]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.vpn.client.VPNClientGUI#showRx()
	 */
	public void showRx() {
		setImage(icons[RX_ICON]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.vpn.client.VPNClientGUI#showTxRx()
	 */
	public void showTxRx() {
		setImage(icons[TXRX_ICON]);
	}

	void setImage(final Image image) {
		display.asyncExec(new Runnable() {
			public void run() {
				trayItem.setImage(image);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.vpn.client.VPNClientGUI#setInfo(java.lang.String)
	 */
	public void setInfo(final String info) {
		display.asyncExec(new Runnable() {
			public void run() {
				trayItem.setToolTipText(info);
			}
		});
	}

	protected Image loadImage(Class clazz, String path) {
		InputStream stream = clazz.getResourceAsStream(path);
		return stream == null ? null : new Image(display, stream);
	}

	public boolean confirm(final int dialogType, final String okText, final String cancelText, final String title,
							final String message) {
		final List l = new ArrayList();
		display.syncExec(new Runnable() {
			public void run() {
				int type = SWT.ICON_QUESTION;
				if (dialogType == INFORMATION) {
					type = SWT.ICON_INFORMATION;
				} else if (dialogType == ERROR) {
					type = SWT.ICON_ERROR;
				} else if (dialogType == WARNING) {
					type = SWT.ICON_WARNING;
				}
				MessageBox messageBox = new MessageBox(shell, type | SWT.OK | (cancelText != null ? SWT.CANCEL : 0));
				messageBox.setText(title);
				messageBox.setMessage(message);
				int buttonID = messageBox.open();
				switch (buttonID) {
					case SWT.OK:
						l.add(Boolean.TRUE);
					default:
						l.add(Boolean.FALSE);
				}
			}
		});
		return ((Boolean) l.get(0)).booleanValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.agent.client.AgentClientGUI#error(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.Throwable)
	 */
	public boolean error(String okText, String cancelText, String title, String message, Throwable ex) {
		return confirm(ERROR, okText, cancelText, title, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.agent.client.AgentClientGUI#getConsole()
	 */
	public synchronized Console getConsole() {
		if (console == null) {
			console = new SWTConsoleOutputStream("true".equals(System.getProperty("console.toSysOut", "false")) ? System.out : null, this); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return console;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.agent.client.AgentClientGUI#createTaskProgress(java.lang.String,
	 *      java.lang.String, long, boolean)
	 */
	public TaskProgress createTaskProgress(String message, String note, long maxValue, boolean allowCancel) {
		return new SWTProgressDialog(this, (int) maxValue, allowCancel, message, note);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.openvpn.als.agent.client.AgentClientGUI#popup(net.openvpn.als.agent.client.AgentClientGUI.ActionCallback,
	 *      java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public void popup(final ActionCallback callback, final String message, final String title, final String imageName,
						final int timeout) {
		display.syncExec(new Runnable() {
			public void run() {

				if (popupTimer != null) {
					popupTimer.setBalloon(null);
				}
				popupTimer = new PopupTimer();

				if (balloon != null && !balloon.getShell().isDisposed()) {
					balloon.setVisible(false);
				}
				balloon = new SWTBalloonWindow(display, SWT.ON_TOP | SWT.TITLE | SWT.CLOSE);
				balloon.setAutoLocation(SWT.BOTTOM | SWT.RIGHT);
				balloon.setLocation(56, 56);
				balloon.setAnchor(SWT.BOTTOM | SWT.RIGHT);
				balloon.setAutoAnchor(false);
				balloon.getContents().setLayout(new FillLayout());
				balloon.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {
						// #ifdef DEBUG
						log.debug("Balloon selected");
						// #endif
						if (callback != null) {
							callback.actionPerformed();
						}
						balloon.close();
					}
				});
				messageLabel = new Label(balloon.getContents(), SWT.WRAP);
				messageLabel.setForeground(balloon.getShell().getForeground());
				messageLabel.setBackground(balloon.getShell().getBackground());
				balloon.addSelectionControl(messageLabel);
				balloon.setText(title);

				// balloon.setText(title);
				messageLabel.setText(message == null ? "<No message supplied>" : message);
				messageLabel.setSize(messageLabel.computeSize(300, SWT.DEFAULT));
				Composite c = balloon.getContents();
				Point messageSize = messageLabel.getSize();
				Image image = imageName != null ? loadImage(SWTSystemTrayGUI.class, "/images/" + imageName + ".png") : null;
				if (image != null) {
					balloon.setImage(image);
					c.setSize(messageSize.x, messageSize.y);
				} else {
					c.setSize(messageSize);
				}
				if (!balloon.getShell().getVisible()) {
					balloon.setVisible(true);
				}
				if (timeout != 0) {
					popupTimer.setBalloon(balloon);
					display.timerExec(timeout == -1 ? DEFAULT_TIMEOUT : timeout, popupTimer);
				}
			}
		});
	}

	public PortMonitor getPortMonitor() {
		if (portMonitor == null) {
			createPortMonitor();
		}
		return portMonitor;
	}

	public boolean promptForCredentials(final boolean proxy, final HttpAuthenticator authenticator) {
		return ((Boolean) SWTRunner.syncExec(display, new SWTRunner() {
			public Object doRun() {
				return new Boolean(SWTAuthenticationDialog.promptForCredentials(proxy, shell, authenticator, null));
			}

		})).booleanValue();
	}

	public void dispose() {
		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				if (tray != null) {
					trayItem.dispose();
					tray.dispose();
				}
				getPortMonitor().dispose();
				getConsole().dispose();
			}
		});
	}

	public void addMenu(final String name) {

		display.syncExec(new Runnable() {
			public void run() {
				if (menuIdx == 0) {
					new MenuItem(popupMenu, SWT.SEPARATOR, 0);
				}
				MenuItem item = new MenuItem(popupMenu, SWT.CASCADE, menuIdx);
				menuIdx++;
				item.setText(name);
				Menu menu = new Menu(popupMenu);
				item.setMenu(menu);
				menuLookup.put(name, menu);
				menuItemLookup.put(name, item);
			}
		});
	}

	public void removeMenu(final String name) {	

		display.syncExec(new Runnable() {
			public void run() {
				Menu menu = (Menu)menuLookup.get(name);				
				MenuItem menuItem = (MenuItem)menuItemLookup.get(name);
				if(menu != null) {
					menu.dispose();
					menuItem.dispose();
					menuLookup.remove(name);
					menuItemLookup.remove(name);
				}
			}
		});
	}
	
	public void clearMenu(final String name) {	

		display.syncExec(new Runnable() {
			public void run() {
				Menu menu = (Menu)menuLookup.get(name);
				if(menu != null) {
					while(menu.getItemCount() > 0) {
						menu.getItem(0).dispose();
					}
				}
			}
		});		
	}
	
	public boolean isMenuExists(String name) {
		return menuLookup.containsKey(name);
	}
	

	public void addMenuItem(String name, final AgentAction action) {

		// #ifdef DEBUG
		log.debug("Adding menu item " + action.getAction() + " to " + name);
		// #endif
		final Menu parentMenu = name == null ? popupMenu : (Menu) menuLookup.get(name);
		if (parentMenu == null) {
			// #ifdef DEBUG
			log.error("No parent menu item " + name + " for " + action.getAction());
			// #endif
			return;
		}

		display.syncExec(new Runnable() {
			public void run() {
				MenuItem item = new MenuItem(parentMenu, SWT.PUSH);
				item.setText(action.getAction());
				item.addSelectionListener(new SelectionListener() {
					public void widgetDefaultSelected(SelectionEvent e) {
					}

					public void widgetSelected(SelectionEvent e) {
						action.actionPerformed();
					}
				});
			}
		});
	}

	public void addMenuSeperator(String name) {

		final Menu parentMenu = name == null ? popupMenu : (Menu) menuLookup.get(name);
		if (parentMenu == null)
			return;
		display.syncExec(new Runnable() {
			public void run() {
				new MenuItem(parentMenu, SWT.SEPARATOR);
			}
		});

	}

	protected void createPortMonitor() {
		display.syncExec(new Runnable() {
			public void run() {
				portMonitor = new SWTPortMonitor(SWTSystemTrayGUI.this);
			}
		});
	}

	protected void exit() {
		agent.disconnect();
	}

	class PopupTimer implements Runnable {

		private SWTBalloonWindow balloon;

		public void setBalloon(SWTBalloonWindow balloon) {
			this.balloon = balloon;
		}

		public void run() {
			display.syncExec(new Runnable() {
				public void run() {
					if (balloon != null && !balloon.getShell().isDisposed()) {
						balloon.setVisible(false);
					}
				}
			});
		}
	}

}