
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
			
package com.adito.upgrade;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JComponent;
import javax.swing.SwingConstants;

/**
 * Useful UI utilies.
 * 
 * @author $Author: lee $
 */
public class UIUtil implements SwingConstants {

    

    /**
     * Add a component to a container that is using a <code>GridBagLayout</code>,
     * together with its constraints and the
     * <code>GridBagConstraints.gridwidth</code> value.
     * 
     * @param parent parent container
     * @param componentToAdd component to add
     * @param constraints contraints
     * @param pos grid width position
     * 
     * @throws IllegalArgumentException
     */
    public static void jGridBagAdd(JComponent parent, Component componentToAdd, GridBagConstraints constraints, int pos) {
        if (!(parent.getLayout() instanceof GridBagLayout)) {
            throw new IllegalArgumentException("parent must have a GridBagLayout");
        }

        //
        GridBagLayout layout = (GridBagLayout) parent.getLayout();

        //
        constraints.gridwidth = pos;
        layout.setConstraints(componentToAdd, constraints);
        parent.add(componentToAdd);
    }

    /**
     * Position a component on the screen (must be a
     * <code>java.awt.Window</code> to be useful)
     * 
     * @param p postion from <code>SwingConstants</code>
     * @param c component
     */
    public static void positionComponent(int p, Component c) {

        positionComponent(p, c, c);

    }

    public static void positionComponent(int p, Component c, Component o) {
        Rectangle d = null;
        /*
         * TODO This is very lame doesnt require the component to position
         * around, just assuming its a window.
         */
        try {

            // #ifdef JAVA1
            /*
             * throw new Exception();
             */

            // #else
            GraphicsConfiguration config = o.getGraphicsConfiguration();
            GraphicsDevice dev = config.getDevice();
            d = config.getBounds();

            // #endif JAVA1
        } catch (Throwable t) {
        }
        positionComponent(p, c, d);
        
    }

    public static void positionComponent(int p, Component c, Rectangle d) {
        if (d == null) {
            Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
            d = new Rectangle(0, 0, s != null ? s.width : 800, s != null ? s.height : 600);
            System.out.println("Could not get metrics from graphics config, using default " + d);
        }

        switch (p) {
            case NORTH_WEST:
                c.setLocation(d.x, d.y);
                break;
            case NORTH:
                c.setLocation(d.x + (d.width - c.getSize().width) / 2, d.y);
                break;
            case NORTH_EAST:
                c.setLocation(d.x + (d.width - c.getSize().width), d.y);
                break;
            case WEST:
                c.setLocation(d.x, d.y + (d.height - c.getSize().height) / 2);
                break;
            case SOUTH_WEST:
                c.setLocation(d.x, d.y + (d.height - c.getSize().height));
                break;
            case EAST:
                c.setLocation(d.x + d.width - c.getSize().width, d.y + (d.height - c.getSize().height) / 2);
                break;
            case SOUTH_EAST:
                c.setLocation(d.x + (d.width - c.getSize().width), d.y + (d.height - c.getSize().height) - 30);
                break;
            case CENTER:
                c.setLocation(d.x + (d.width - c.getSize().width) / 2, d.y + (d.height - c.getSize().height) / 2);
                break;
        }
    }
}
