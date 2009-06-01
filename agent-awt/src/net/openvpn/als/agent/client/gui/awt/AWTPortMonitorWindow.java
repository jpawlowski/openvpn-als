
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
			
package net.openvpn.als.agent.client.gui.awt;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Vector;

import com.sshtools.ui.awt.UIUtil;
import com.sshtools.ui.awt.grid.Grid;
import com.sshtools.ui.awt.options.Option;
import com.sshtools.ui.awt.options.OptionDialog;
import net.openvpn.als.agent.client.Agent;
import net.openvpn.als.agent.client.tunneling.AbstractPortItem;


/**
 * Displays a frame that monitors all active ports (local and
 * remote) and any tunnels that may be running over them.
 */
public class AWTPortMonitorWindow extends Frame {
    
    //  Private instance variables
    
    private Grid portGrid;
    private PortModel model;
    private Button stopButton;
    private Button closeButton;
    private Thread updateThread;

    /**
     * Constructor.
     *
     * @param vpnClient
     */
    public AWTPortMonitorWindow(Agent vpnClient) {
        super(Messages.getString("PortMonitor.title")); //$NON-NLS-1$
        setIconImage(UIUtil.loadImage(getClass(), "/images/frame-agent.gif")); //$NON-NLS-1$

        model = new PortModel();
        portGrid = new Grid(model);
        portGrid.setBackground(Color.white);
        portGrid.setForeground(Color.black);
        portGrid.setSelectionBackground(Color.blue.darker().darker());
        portGrid.setSelectionForeground(Color.white);
        portGrid.setColumnWidths(new int[] {  46, 220, 64, 64, 128, 64 } );
        portGrid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setAvailableActions();
            }            
        });
        
        closeButton = new Button(Messages.getString("PortMonitor.close")); //$NON-NLS-1$
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
            }            
        });
        
        stopButton = new Button(Messages.getString("PortMonitor.stop")); //$NON-NLS-1$
        stopButton.addActionListener(new ActionListener() {
            Option stop = new Option(Messages.getString("PortMonitor.stop")); //$NON-NLS-1$
           public void actionPerformed(ActionEvent evt) {
               Option opt = OptionDialog.prompt(AWTPortMonitorWindow.this, OptionDialog.WARNING, Messages.getString("PortMonitor.close.title"), //$NON-NLS-1$
                   Messages.getString("PortMonitor.close.text"), //$NON-NLS-1$
                   new Option[] { stop, OptionDialog.CHOICE_CANCEL } ); 
               if(opt == stop) {
                   for(Enumeration e = getSelectedPorts().elements(); e.hasMoreElements(); ) {
                       AbstractPortItem t = (AbstractPortItem)e.nextElement();
                       t.stop();
                   }
               }
           }
        });
        
        Panel p = new Panel(new BorderLayout());
        p.add(portGrid, BorderLayout.CENTER);
        
        Panel f = new Panel(new FlowLayout(FlowLayout.RIGHT));
        f.setBackground(Color.gray);
        f.setForeground(Color.black);
        f.add(stopButton);
        f.add(closeButton);
        
        setLayout(new BorderLayout());
        add(p, BorderLayout.CENTER);
        add(f, BorderLayout.SOUTH);
        pack();
        setAvailableActions();
    }
    
    /* (non-Javadoc)
     * @see java.awt.Component#setVisible(boolean)
     */
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(visible && updateThread == null) {
            updateThread = new Thread() {
                public void run() {
                    while(updateThread != null) {
                        try {
                            Thread.sleep(1000);
                        }
                        catch(Exception e) {                            
                        }
                        try {
                            Method invokeAndWaitMethod = Class.forName("java.awt.EventQueue").getMethod("invokeAndWait", new Class[] { Runnable.class }); //$NON-NLS-1$ //$NON-NLS-2$
                            Runnable r = new Runnable() {
                                public void run() {
                                    model.refresh();
                                }
                            };
                            invokeAndWaitMethod.invoke(null, new Object[] { r });
                        }
                        catch(Exception e) {
                            model.refresh();
                        }
                    }
                }
            };
            updateThread.start();
        }
        else if(!visible && updateThread != null){
            updateThread = null;
        }
    }
    
    /**
     * Get a list of {@link AbstractPortItem} objects that are 
     * currently selected.
     * 
     * @return selected ports
     */
    public Vector getSelectedPorts() {
        int[] r = portGrid.getSelectedRows();
        Vector v = new Vector();
        for(int i = 0 ; i < r.length; i++) {
            v.addElement(model.getItemAt(r[i]));
        }
        return v;
    }
    
    /**
     * Get the port model.
     * 
     * @return model
     */
    public PortModel getModel() {
        return model;
    }
    
    protected void setAvailableActions() {
        stopButton.setEnabled(portGrid.getSelectedRowCount() != 0);
    }

}
