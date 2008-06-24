/*
 *  Gruntspud
 *
 *  Copyright (C) 2002 Brett Smith.
 *
 *  Written by: Brett Smith <t_magicthize@users.sourceforge.net>
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package com.sshtools.ui.swing;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 * A TabToolBar creates a vertical tool bar with actions that may be grouped
 * into contexts, in a style similar to navigation bar used on the left hand
 * side in Outlook. See the addIcon() method for details on which action
 * properties are required
 */
public class TabToolBar extends JPanel {
    // Private instance variables
    private Vector contextList;
    private ContextPanel selectedContext;
    private GridBagConstraints gBC;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private FolderBar folderBar;
    private int fixedWidth = -1;
    private Color toolBarBackground;

    /**
     * Construct a new TabToolBar
     */
    public TabToolBar() {
        super(new GridBagLayout());

        toolBarBackground = UIManager.getColor("Panel.background").darker();

        // Intialise
        contextList = new Vector();
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        cardPanel.setBackground(toolBarBackground);

        // Create the constraints for use later
        gBC = new GridBagConstraints();
        gBC.anchor = GridBagConstraints.NORTH;
        gBC.fill = GridBagConstraints.HORIZONTAL;

        setBackground(toolBarBackground);
        setOpaque(true);
    }

    public void setFixedWidth(int fixedWidth) {
        this.fixedWidth = fixedWidth;
        doLayout();
    }

    public int getFixedWidth() {
        return fixedWidth;
    }

    /**
     * Set the selected context
     * 
     * @param context context
     */
    public void setSelectedContext(String context) {
        ContextPanel p = null;

        for (Iterator i = contextList.iterator(); i.hasNext();) {
            ContextPanel z = (ContextPanel) i.next();

            if (context.equals(z.name)) {
                p = z;
            }
        }

        if (p != null) {
            selectedContext = p;
            cardLayout.show(cardPanel, context);
            makeToolBar();
        }
    }

    /**
     * Sets a FolderBar associated with this tool bar. When actions are invoked,
     * the FolderBar will change to show the action details for the selected
     * action.
     * 
     * @param folder bar
     */
    public void setFolderBar(FolderBar folderBar) {
        this.folderBar = folderBar;
    }

    /**
     * Returns the FolderBar that is being changed upon actions..
     * 
     * @return folder bar
     */
    public FolderBar getFolderBar() {
        return folderBar;
    }

    /**
     * Add a new action to the toolbar, this must have a non null
     * DefaultAction.CONTEXT property or an IllegalArgumentException will be
     * thrown. The DefaultAction.LARGE_ICON property will be used for the icon,
     * and the Action.NAME property will be used for the text.
     * Action.LONG_DESCRIPTION will be used to any tool tip text.
     * 
     * @param action to build icon from
     * @throws IllegalArgumentExcption if context not set
     * @todo Make changes to the action reflect on the button correctly
     */
    public void addAction(AppAction action) {
        // If there is a folder bar in use, and it has no action, then set it
        // to be this one
        if ((getFolderBar() != null) && (getFolderBar().getAction() == null)) {
            getFolderBar().setAction(action);
        }

        // Get the context and its panel (or create the panel if its new)
        String context = (String) action.getValue(AppAction.CATEGORY);

        if (context == null) {
            throw new IllegalArgumentException("AppAction.CONTEXT parameter of action must not be null");
        }

        ContextPanel contextPanel = null;

        for (int i = 0; (i < contextList.size()) && (contextPanel == null); i++) {
            ContextPanel p = (ContextPanel) contextList.elementAt(i);

            if (p.name.equals(context)) {
                contextPanel = p;
            }
        }

        if (contextPanel == null) {
            contextPanel = new ContextPanel(context);
            cardPanel.add(contextPanel.name, contextPanel);
            contextList.addElement(contextPanel);
        }

        if (selectedContext == null) {
            selectedContext = contextPanel;
        }

        // Add the action to the appropriate context panel and layout the bar
        TabActionButton button = contextPanel.addIcon(action);

        if (getParent() instanceof JViewport && ((JViewport) getParent()).getView() instanceof ScrollingPanel) {
            ((ScrollingPanel) ((JViewport) getParent()).getView()).setIncrement(button.getPreferredSize().height);
        }

        makeToolBar();
    }

    /**
     * 
     */
    public void removeAllActions() {
        contextList.clear();
        cardPanel.removeAll();
        selectedContext = null;
        makeToolBar();
    }

    private void makeToolBar() {
        // Rebuild the panels
        invalidate();
        removeAll();
        for (int i = 0; i < contextList.size(); i++) {
            ContextPanel p = (ContextPanel) contextList.elementAt(i);

            // First add the context button
            UIUtil.jGridBagAdd(this, new TabButton(p.getContextAction()), gBC, GridBagConstraints.REMAINDER);

            // If this is the selected action, the now add the panel
            if (p == selectedContext) {
                cardLayout.show(cardPanel, p.name);

                gBC.weighty = 1.0;
                UIUtil.jGridBagAdd(this, cardPanel, gBC, GridBagConstraints.REMAINDER);
                gBC.weighty = 0.0;
            }
        }

        validate();
        repaint();
    }

    // Supporting classes
    public class ContextPanel extends JPanel {
        String name;
        AppAction action;
        GridBagConstraints gBC;

        public ContextPanel(String name) {
            super(new ListLayout());
            this.name = name;
            setOpaque(false);
            setBackground(toolBarBackground);
            action = new ContextAction(name, this);
        }

        public AppAction getContextAction() {
            return action;
        }

        public TabActionButton addIcon(AppAction action) {
            TabActionButton b = new TabActionButton(action);
            add(b);

            return b;
        }
    }

    public class ContextAction extends AppAction {
        ContextPanel context;

        ContextAction(String name, ContextPanel context) {
            super();
            putValue(AppAction.NAME, name);
            this.context = context;
        }

        public void actionPerformed(ActionEvent evt) {
            selectedContext = context;
            makeToolBar();
        }

        public boolean checkAvailable() {
            return true;
        }
    }

    public class TabButton extends JButton {
        public TabButton(AppAction a) {
            super(a);
            setFocusPainted(false);
            setDefaultCapable(false);
            setMargin(new Insets(1, 1, 1, 1));
        }
    }

    public class TabActionButton extends ActionButton {

        public TabActionButton(final AppAction a) {
            super(a, true, true);
            setHorizontalTextPosition(SwingConstants.CENTER);
            setVerticalTextPosition(SwingConstants.BOTTOM);
        }

        public Dimension getMaximimumSize() {
            return fixedWidth == -1 ? super.getMaximumSize() : new Dimension(fixedWidth, super.getMaximumSize().height);
        }

        public Dimension getMinimumSize() {
            return fixedWidth == -1 ? super.getMinimumSize() : new Dimension(fixedWidth, super.getMinimumSize().height);
        }

        public Dimension getPreferredSize() {
            return fixedWidth == -1 ? super.getPreferredSize() : new Dimension(fixedWidth, super.getPreferredSize().height);
        }
    }
}
