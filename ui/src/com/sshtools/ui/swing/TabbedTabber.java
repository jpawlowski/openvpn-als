/*
 *  SSHTools - Java SSH2 API
 *
 *  Copyright (C) 2002 Lee David Painter.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *
 *  You may also distribute it and/or modify it under the terms of the
 *  Apache style J2SSH Software License. A copy of which should have
 *  been provided with the distribution.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  License document supplied with your distribution for more details.
 *
 */

package com.sshtools.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 * 
 * @author $author$
 * @version $Revision: 1.2.6.1 $
 */

public class TabbedTabber

extends ClosableTabbedPane implements Tabber {

    /**
     * Creates a new TabbedTabber object.
     */

    public TabbedTabber() {
        this(TOP);

    }

    /**
     * Creates a new TabbedTabber object.
     * 
     * @param tabPlacement
     */

    public TabbedTabber(int tabPlacement) {
        super(tabPlacement);
        addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                if (getSelectedIndex() != -1) {
                    getTabAt(getSelectedIndex()).tabSelected();
                }

            }

        });

    }

    /**
     * 
     * 
     * @param i
     * 
     * @return
     */

    public Tab getTabAt(int i) {
        return ((TabPanel) getComponentAt(i)).getTab();

    }

    /**
     * 
     * 
     * @return
     */

    public boolean validateTabs() {
        for (int i = 0; i < getTabCount(); i++) {
            Tab tab = ((TabPanel) getComponentAt(i)).getTab();
            if (!tab.validateTab()) {
                setSelectedIndex(i);
                return false;
            }
        }
        return true;

    }

    /**
     * 
     */

    public void applyTabs() {
        for (int i = 0; i < getTabCount(); i++) {
            Tab tab = ((TabPanel) getComponentAt(i)).getTab();
            tab.applyTab();
        }

    }

    public synchronized Tab getSelectedTab() {
        int idx = getSelectedIndex();
        return idx == -1 ? null : getTabAt(idx);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sshtools.appframework.ui.Tabber#getComponent()
     */
    public Component getComponent() {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sshtools.appframework.ui.Tabber#removeAllTabs()
     */
    public void removeAllTabs() {
        removeAll();
    }

    /**
     * 
     * 
     * @param tab
     */

    public void addTab(Tab tab) {
        addTab(tab.getTabTitle(), tab.getTabIcon(), new TabPanel(tab), tab.getTabToolTipText());

    }

    class TabPanel

    extends JPanel {

        private Tab tab;

        TabPanel(Tab tab) {
            super(new BorderLayout());
            this.tab = tab;
            setOpaque(false);
            add(tab.getTabComponent(), BorderLayout.CENTER);

        }

        public Tab getTab() {
            return tab;

        }

    }

}
