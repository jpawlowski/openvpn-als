
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
			
package com.ovpnals.agent.client.gui.awt;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;

import com.sshtools.ui.awt.ImageCanvas;
import com.sshtools.ui.awt.UIUtil;
import com.sshtools.ui.awt.tooltips.ToolTipManager;
import com.ovpnals.agent.client.Agent;
import com.ovpnals.agent.client.AgentAction;

/**
 * Implementation of an {@link AbstractAWTGUI} that uses a simple frame to provide
 * a GUI for the Agent. This is most likely to be used on platforms that do not
 * support system tray functionality.
 */
public class BasicFrameGUI extends AbstractAWTGUI {

    // Private instance variables

    private Image idle;
    private Image tx;
    private Image rx;
    private Image txrx;
    private Image disconnected;
    private Image banner;
    private ImageCanvas activityPanel;
    private Frame frame;
    private Menu file;
    private MenuBar menu;
    private Hashtable menuLookup;

    /* (non-Javadoc)
     * @see com.ovpnals.agent.client.AgentClientGUI#init(com.ovpnals.agent.client.Agent)
     */
    public void init(Agent agent) {
        super.init(agent);

        MenuItem open = new MenuItem(Messages.getString("GUI.menu.openBrowser")); //$NON-NLS-1$
        open.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                openBrowser(null);
            }
        });

        // #ifdef DEBUG
        MenuItem console = new MenuItem(Messages.getString("GUI.menu.debugConsole")); //$NON-NLS-1$
        console.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getConsole().show();
            }
        });
        // #endif

        MenuItem ports = new MenuItem(Messages.getString("GUI.menu.tunnelMonitor")); //$NON-NLS-1$
        ports.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getPortMonitor().setVisible(!getPortMonitor().isVisible());
            }
        });

        MenuItem exit = new MenuItem(Messages.getString("GUI.menu.exit")); //$NON-NLS-1$
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getAgent().disconnect();
            }
        });

        Panel main = new Panel(new BorderLayout());
        main.setBackground(Color.black);
        

        banner = UIUtil.loadImage(getClass(), "/images/banner-small.gif"); //$NON-NLS-1$
        idle = UIUtil.loadImage(getClass(), "/images/tray-idle.gif"); //$NON-NLS-1$        
        tx = UIUtil.loadImage(getClass(), "/images/tray-tx.gif"); //$NON-NLS-1$
        rx = UIUtil.loadImage(getClass(), "/images/tray-rx.gif"); //$NON-NLS-1$
        txrx = UIUtil.loadImage(getClass(), "/images/tray-txrx.gif"); //$NON-NLS-1$
        disconnected = UIUtil.loadImage(getClass(), "/images/tray-disconnecting.gif"); //$NON-NLS-1$

        UIUtil.waitFor(banner, main);
        UIUtil.waitFor(idle, main);
        UIUtil.waitFor(tx, main);
        UIUtil.waitFor(rx, main);
        UIUtil.waitFor(txrx, main);
        UIUtil.waitFor(disconnected, main);
        
        ImageCanvas bannerCanvas = new ImageCanvas(banner); 
        bannerCanvas.setHalign(ImageCanvas.LEFT_ALIGNMENT);
        bannerCanvas.setBackground(Color.black);
        main.add(bannerCanvas, BorderLayout.CENTER);
        
        activityPanel = new ImageCanvas(idle);
        activityPanel.setSize(new Dimension(32, 32));
        activityPanel.setBorder(4);
        
        main.add(activityPanel, BorderLayout.EAST);

        menu = new MenuBar();
        menuLookup = new Hashtable();

        file = new Menu(Messages.getString("GUI.menu.file")); //$NON-NLS-1$
        // #ifdef DEBUG
        file.add(console);
        // #endif
        file.add(open);
        file.add(ports);
        file.addSeparator();
        file.add(exit);
        menu.add(file);

        frame = new Frame(Messages.getString("GUI.title")); //$NON-NLS-1$
        frame.add(main);
        frame.setSize(440, 96);
        frame.setMenuBar(menu);
        frame.setIconImage(UIUtil.loadImage(getClass(), "/images/frame-agent.gif")); //$NON-NLS-1$
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                getAgent().disconnect();
            }
        });
    }
    
    /* (non-Javadoc)
     * @see com.ovpnals.agent.client.AgentClientGUI#addMenu(java.lang.String)
     */
    public void addMenu(final String name) {    	
    	Menu foo = new Menu(name);
    	menuLookup.put(name, foo);    	
    	menu.add(foo);
    }

	/* (non-Javadoc)
	 * @see com.ovpnals.agent.client.AgentClientGUI#removeMenu(java.lang.String)
	 */
	public void removeMenu(String name) {
		Menu menu = (Menu)menuLookup.get(name);
		if(menu != null) {
			menuLookup.remove(name);
			this.menu.remove(menu);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.ovpnals.agent.client.AgentClientGUI#clearMenu(java.lang.String)
	 */
	public void clearMenu(String name) {
		Menu menu = (Menu)menuLookup.get(name);
		if(menu != null) {
			while(menu.getItemCount() > 0) {
				menu.remove(0);
			}
		}		
	}
	
	/* (non-Javadoc)
	 * @see com.ovpnals.agent.client.AgentClientGUI#isMenuExists(java.lang.String)
	 */
	public boolean isMenuExists(String name) {
		return menuLookup.containsKey(name);
	}
    
    /* (non-Javadoc)
     * @see com.ovpnals.agent.client.AgentClientGUI#addMenuItem(java.lang.String, com.ovpnals.agent.client.AgentAction)
     */
    public void addMenuItem(final String parentName, final AgentAction action) {
    	
    	Menu menu = parentName == null ? file : (Menu) menuLookup.get(parentName);
        if(menu==null)
        	return;
        MenuItem item = new MenuItem(action.getAction());
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                action.actionPerformed();
            }
        });
        menu.add(item);
    }

    /* (non-Javadoc)
     * @see com.ovpnals.vpn.client.VPNClientGUI#showIdle()
     */
    public void showIdle() {
        activityPanel.setImage(idle);
    }

    /* (non-Javadoc)
     * @see com.ovpnals.vpn.client.VPNClientGUI#showDisconnected()
     */
    public void showDisconnected() {
        activityPanel.setImage(disconnected);
    }

    /* (non-Javadoc)
     * @see com.ovpnals.vpn.client.VPNClientGUI#showTx()
     */
    public void showTx() {
        activityPanel.setImage(tx);
    }

    /* (non-Javadoc)
     * @see com.ovpnals.vpn.client.VPNClientGUI#showRx()
     */
    public void showRx() {
        activityPanel.setImage(rx);
    }

	/* (non-Javadoc)
	 * @see com.ovpnals.agent.client.gui.awt.AbstractAWTGUI#dispose()
	 */
	public void dispose() {
		frame.dispose();
		super.dispose();
	}

	/* (non-Javadoc)
	 * @see com.ovpnals.agent.client.AgentClientGUI#addMenuSeperator(java.lang.String)
	 */
	public void addMenuSeperator(String parentName) {
	
		Menu m = parentName == null ? file : (Menu)menuLookup.get(parentName);
		if(m==null)
			return;
		
		m.addSeparator();
		
	}

    /* (non-Javadoc)
     * @see com.ovpnals.vpn.client.VPNClientGUI#showTxRx()
     */
    public void showTxRx() {
        activityPanel.setImage(txrx);
    }

    /* (non-Javadoc)
     * @see com.ovpnals.vpn.client.VPNClientGUI#setInfo(java.lang.String)
     */
    public void setInfo(String info) {
        ToolTipManager.getInstance().requestToolTip(activityPanel, info);
    }

    /* (non-Javadoc)
     * @see com.ovpnals.vpn.client.AbstractGUI#getGUIComponent()
     */
    public Component getGUIComponent() {
        return frame;
    }

}
