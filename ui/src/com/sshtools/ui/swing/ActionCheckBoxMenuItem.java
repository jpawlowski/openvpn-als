
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

public class ActionCheckBoxMenuItem extends JCheckBoxMenuItem {
    public ActionCheckBoxMenuItem(ToggleableAction action) {
        super(action);
        Icon i = (Icon) action.getValue(AppAction.SMALL_ICON);
        if (i != null) {
            setIcon(i);
        }
        action.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("selected")) {
                    boolean sel = ((Boolean) evt.getNewValue()).booleanValue();
                    setSelected(sel);
                    setIcon((Icon) getAction().getValue(AppAction.SMALL_ICON));
                }
            }
        });
        setSelected(action.isSelected());
    }
}