
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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Panel;

public class BorderPanel extends Panel {
    Insets insets;
    Color borderColor;

    public BorderPanel() {
        super();
        init();
    }

    public BorderPanel(LayoutManager layout) {
        super(layout);
        init();
    }

    void init() {
        insets = new Insets(1, 1, 1, 1);
    }

    public void setBorderColor(Color c) {
        borderColor = c;
        repaint();
    }

    public void paint(Graphics g) {
        if (borderColor != null) {
            g.setColor(borderColor);
            g.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
        }
        super.paint(g);
    }

    public Insets getInsets() {
        return borderColor == null ? super.getInsets() : insets;
    }
}