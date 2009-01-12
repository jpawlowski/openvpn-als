
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
			

package com.sshtools.ui.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Vector;

public class ActionMenu {

    public final static Action SEPARATOR = new AbstractAction("separator") { //$NON-NLS-1$
        public void actionPerformed(ActionEvent e) {            
        }        
    };

    private int weight;
    private int mnemonic;
    private String name;
    private String displayName;
    private Vector children;
    private Vector listeners;
    private String toolTip;

    private ActionMenu() {
        // separator
    }

    public ActionMenu(String name, String displayName, int mnemonic, int weight, String toolTip) {
        this.name = name;
        this.displayName = displayName;
        this.mnemonic = mnemonic;
        this.weight = weight;
        this.toolTip = toolTip;
        children = new Vector();
    }
    
    public String getToolTip() {
        return toolTip;
    }
    
    public void setToolTip() {
        this.toolTip = toolTip;
    }

    public int compareTo(Object o) {
        double oweight = ((ActionMenu) o).weight;
        return (weight < oweight ? -1 : (weight == oweight ? 0 : 1));
    }

    /**
     * @return Returns the displayName.
     */

    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName
     *            The displayName to set.
     */

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @return Returns the mnemonic.
     */

    public int getMnemonic() {
        return mnemonic;
    }

    /**
     * @param mnemonic
     *            The mnemonic to set.
     */

    public void setMnemonic(int mnemonic) {
        this.mnemonic = mnemonic;
    }

    /**
     * @return Returns the name.
     */

    public String getName() {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the weight.
     */

    public int getWeight() {
        return weight;
    }

    /**
     * @param weight
     *            The weight to set.
     */

    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     *  
     */
    public void addSeparator() {
        children.addElement(SEPARATOR);
    }

    /**
     * @param item
     */
    public void add(Action item) {
        children.addElement(item);        
    }

    /**
     * @return
     */
    public Enumeration children() {
        return children.elements();
    }

    /**
     * @return
     */
    public int getChildCount() {
        return children.size();
    }
    
    public void addActionListener(ActionListener l) {
        if (listeners == null) {
            listeners = new Vector();
        }
        listeners.addElement(l);
    }

    public void removeActionListener(ActionListener l) {
        if (listeners != null) {
            listeners.removeElement(l);
        }
    }

    public boolean action() {
        ActionEvent evt = null;
        for (int i = listeners == null ? -1 : listeners.size() - 1; i >= 0; i--) {
            if (evt == null) {
                evt = new ActionEvent(this, 1001, getName());
            }
            ((ActionListener) listeners.elementAt(i)).actionPerformed(evt);
        }
        return false;
    }

    /**
     * @param clicked
     * @return
     */
    public Action getChild(int clicked) {
        return (Action)children.elementAt(clicked);
    }

    /**
     * 
     */
    public void removeAllChildren() {
        children.removeAllElements();        
    }

}