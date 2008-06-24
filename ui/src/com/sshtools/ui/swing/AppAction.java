
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
			
package com.sshtools.ui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.event.EventListenerList;


/**
 *
 * Extension of {@link AbstractAction}that is used throughout the application
 *
 * framework. Provides extra settings for actions that can be used to build
 *
 * action components.
 *
 * @author $Author: brett $
 *
 * @version $Revision: 1.1 $
 *
 */
public abstract class AppAction extends AbstractAction {
    /**  */
    public final static String IMAGE_DIR = "/com/sshtools/sshterm/";

    /**  */
    public final static String ON_TOOLBAR = "OnToolBar";

    /**  */
    public final static String TOOLBAR_GROUP = "ToolBarGroup";

    /**  */
    public final static String TOOLBAR_WEIGHT = "ToolBarWeight";

    /**  */
    public final static String ON_MENUBAR = "OnMenuBar";

    /**  */
    public final static String MENU_NAME = "MenuName";

    /**  */
    public final static String MENU_ITEM_GROUP = "MenuItemGroup";

    /**  */
    public final static String MENU_ITEM_WEIGHT = "MmenuItemWeight";

    /**  */
    public final static String TEXT_ON_TOOLBAR = "HideToolbarText";

    /**  */
    public final static String IS_TOGGLE_BUTTON = "IsToggleButton";

    /**  */
    public final static String IS_SELECTED = "IsSelected";

    /**  */
    public final static String LARGE_ICON = "LargeIcon";

    /**  */
    public final static String ON_CONTEXT_MENU = "OnContextMenu";

    /**  */
    public final static String CONTEXT_MENU_GROUP = "ContextMenuGroup";

    /**  */
    public final static String CONTEXT_MENU_WEIGHT = "ContextMenuWeight";

    /**  */
    public final static String MEDIUM_ICON = "ToolIcon";


    /**  */
    public final static String CATEGORY = "Category";

    // The listener to action events (usually the main UI)
    private EventListenerList listeners;

    /**
     */
    public AppAction() {
      this("");
    }

    /**
     * @param name
     */
    public AppAction(String name) {
        this(name, null);
    }

    /**
     * @param name
     */
    public AppAction(String name, Icon smallIcon) {
        if(name != null) {
            putValue(AppAction.NAME, name);
        }
        if(smallIcon != null) {
            putValue(AppAction.SMALL_ICON, smallIcon);
        }
    }

    /**
     * @return  
     */
    public String getActionCommand() {
        return (String) getValue(Action.ACTION_COMMAND_KEY);
    }

    /**
     * @return
     */
    public String getShortDescription() {
        return (String) getValue(Action.SHORT_DESCRIPTION);
    }

    /**
     * @return
     */
    public String getLongDescription() {
        return (String) getValue(Action.LONG_DESCRIPTION);
    }

    /**
     * @return
     */
    public String getName() {
        return (String) getValue(Action.NAME);
    }

    /**
     * @return
     */
    public ResourceIcon getSmallIcon() {
        return (ResourceIcon) getValue(Action.SMALL_ICON);
    }

    /**
     * @param evt
     *
     */
    public void actionPerformed(ActionEvent evt) {
        if (listeners != null) {
            Object[] listenerList = listeners.getListenerList();
            // Recreate the ActionEvent and stuff the value of the
            // ACTION_COMMAND_KEY
            ActionEvent e = new ActionEvent(evt.getSource(), evt.getID(),
                    (String) getValue(Action.ACTION_COMMAND_KEY));
            for (int i = 0; i <= (listenerList.length - 2); i += 2) {
                ((ActionListener) listenerList[i + 1]).actionPerformed(e);
            }
        }
    }

    /**
     * @param l
     *
     */
    public void addActionListener(ActionListener l) {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(ActionListener.class, l);
    }

    /**
     * @param l
     *
     */
    public void removeActionListener(ActionListener l) {
        if (listeners == null) { return; }
        listeners.remove(ActionListener.class, l);
    }

    /**
     * @param name
     *
     *
     *
     * @return
     */
    public ImageIcon getIcon(String name) {
        String imagePath = name.startsWith("/") ? name : (IMAGE_DIR + name);
        URL url = this.getClass().getResource(imagePath);
        if (url != null) { return new ImageIcon(url); }
        return null;
    }

    public boolean isSelected() {
        return Boolean.TRUE.equals(getValue(IS_SELECTED));
    }


    public void setSelected(boolean selected) {
        putValue(IS_SELECTED, Boolean.valueOf(selected));    
    }
}
