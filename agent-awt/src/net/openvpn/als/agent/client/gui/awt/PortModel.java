
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

import java.util.Date;
import java.util.Vector;

import com.sshtools.ui.awt.grid.TableModel;
import com.sshtools.ui.awt.grid.TableModelListener;
import net.openvpn.als.agent.client.tunneling.AbstractPortItem;


/**
 * Implementation of a {@link TableModel} that lists all of
 * the active ports (local and remote) and totals of any
 * tunnels that are running over them.
 * 
 * @see AWTPortMonitorWindow
 * @see AbstractPortItem
 */
public class PortModel implements TableModel {
    
    //  Private instance variables

    private Vector portItems, listeners;
    private String[] columns;
    private Class[] columnClasses;
    
    /**
     * Constructor.
     *
     */
    public PortModel() {
        portItems = new Vector();
        listeners = new Vector();
        columns = new String[] { 
                        Messages.getString("PortModel.type"), Messages.getString("PortModel.name"), Messages.getString("PortModel.localPort"), Messages.getString("PortModel.active"), Messages.getString("PortModel.lastData"), Messages.getString("PortModel.total") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        };
        columnClasses = new Class[] {
                        String.class, String.class, Integer.class, Integer.class, Date.class, Integer.class
        };
    }
    
    /**
     * Add a new port to this model.
     * 
     * @param port port to add
     */
    public void addPortItem(AbstractPortItem port) {
        portItems.addElement(port);
        for(int i = listeners.size() - 1 ; i >= 0 ; i--) {
            ((TableModelListener)listeners.elementAt(i)).rowInserted(portItems.size() - 1);
        }
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.awt.grid.TableModel#getRowCount()
     */
    public int getRowCount() {
        return portItems.size();
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.awt.grid.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return columns.length;
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.awt.grid.TableModel#getColumnName(int)
     */
    public String getColumnName(int c) {
        return columns[c];
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.awt.grid.TableModel#getColumnClass(int)
     */
    public Class getColumnClass(int r) {
        return columnClasses[r];
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.awt.grid.TableModel#getValue(int, int)
     */
    public Object getValue(int r, int c) {
        AbstractPortItem t = getItemAt(r);
        switch(c) {
            case 0:
                return t.getType();
            case 1:
                return t.getName();
            case 2:
                return new Integer(t.getLocalPort());
            case 3:
                return new Integer(t.getActiveTunnelCount());
            case 4:
                return new Date(t.getDataLastTransferred());
            default:
                return new Integer(t.getTotalTunnelCount());                    
        }
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.awt.grid.TableModel#addTableModelListener(com.sshtools.ui.awt.grid.TableModelListener)
     */
    public void addTableModelListener(TableModelListener l) {
        listeners.addElement(l);            
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.awt.grid.TableModel#removeTableModelListener(com.sshtools.ui.awt.grid.TableModelListener)
     */
    public void removeTableModelListener(TableModelListener l) {
        listeners.removeElement(l);
    }

    /**
     * Get the port at the given row index.
     * 
     * @param idx row index
     * @return port item
     */
    public AbstractPortItem getItemAt(int idx) {
        return (AbstractPortItem)portItems.elementAt(idx);
    }

    /**
     * Remote the port at the given row index.
     * 
     * @param idx row index to remove 
     */
    public void removeItemAt(int idx) {
        portItems.removeElementAt(idx);
        for(int i = listeners.size() - 1 ; i >= 0 ; i--) {
            ((TableModelListener)listeners.elementAt(i)).rowDeleted(idx);
        }
    }

    /**
     * Update the port at the given row index. Note, this doesn't
     * change the item, it merely first the events.
     * 
     * @param idx row index to update
     */
    public void updateItemAt(int idx) {
        for(int i = listeners.size() - 1 ; i >= 0 ; i--) {
            ((TableModelListener)listeners.elementAt(i)).rowChanged(idx);
        }
    }


    /**
     * Get the index of an item given the resource ID.
     *   
     * @param connection connection to find
     * @return row index
     */
    public int getIndexForId(int id) {    
        for(int i = portItems.size() - 1 ; i >= 0 ; i--) {
            AbstractPortItem pi = (AbstractPortItem)portItems.elementAt(i);
            if(pi.getConfiguration().getId() == id) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Refresh the list of ports.
     */
    public void refresh() {
        for(int i = listeners.size() - 1 ; i >= 0 ; i--) {
            ((TableModelListener)listeners.elementAt(i)).rowChanged(-1);
        }        
    }
    
}