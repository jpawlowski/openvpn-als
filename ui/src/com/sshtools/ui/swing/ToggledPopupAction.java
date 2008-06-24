
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

import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

public abstract class ToggledPopupAction extends AbstractToggleableAction {
    JToggleButton toggle;
    ToggledPopupMenu popup;
    int edge;

    public ToggledPopupAction() {
        this(SwingConstants.NORTH);
    }
    
    public ToggledPopupAction(int edge) {
        super();
        this.edge  = edge;
    }
    
    public void setEdge(int edge) {
        this.edge = edge;
    }
    
    public void setPopup(ToggledPopupMenu popup) {
        this.popup = popup;
    }
    
    public void setToggle(JToggleButton toggle) {
        this.toggle = toggle;
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() instanceof JComponent) {
            JComponent c = (JComponent) evt.getSource();
            // if (!popup.ignoreNextToggleAction) {
            if (!toggle.isSelected()) {
                popup.setVisible(false);
            } else {
                switch(edge) {
                    case SwingConstants.NORTH:
                        popup.show(c, 0, popup.getPreferredSize().height * -1);
                        break;
                    case SwingConstants.EAST:
                        popup.show(c, toggle.getSize().width, 01);
                        break;
                }
            }
            // } else {
            // toggle.setSelected(false);
            // popup.ignoreNextToggleAction = false;
            // }
        }
    }
}