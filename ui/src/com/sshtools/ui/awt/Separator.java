
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
			
package com.sshtools.ui.awt;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.SystemColor;

/**
 * <p>
 * Simple component to draw an etched separator line.
 * </p>
 * 
 * @author $Author: james $
 */

public class Separator extends Canvas {

    /**
     * Horizontal
     */
    public final static int HORIZONTAL = 0;

    /**
     * Vertical
     */
    public final static int VERTICAL = 1;

    //  Private instance variables
    private int orientation;
    private Color background;
    private Dimension preferredSize;

    /**
     * <p>
     * Construct a new Separator with a given orientation. Can be one of :-
     * </p>
     * 
     * <ul>
     * <li>{@link Seperator.HORIZONTAL}</li>
     * <li>{@link Seperator.VERTICAL}</li>
     * </ul>
     * 
     * @param orientation
     *            orientation
     */
    public Separator(int orientation) {
        super();
        setOrientation(orientation);
    }

    public void setBackground(Color background) {
        super.setForeground(background);
        this.background = background;
    }

    /**
     * <p>
     * Set the orientation of the separator. Can be one of :-
     * </p>
     * 
     * <ul>
     * <li>{@link Seperator.HORIZONTAL}</li>
     * <li>{@link Seperator.VERTICAL}</li>
     * </ul>
     * 
     * @param orientation
     *            orientation
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
        repaint();
    }

    public void paint(Graphics g) {
        Dimension d = getSize();
        
        // First try basing color on background color of this component
        Color fg = background;
        
        // No background, traverse parents until a background color is found
        Component co = getParent();
        while( fg == null && co != null) {
            fg = co.getBackground();
            co = co.getParent();
        }
        
        // Use system color
        Color l1 = null;
        Color l2 = null;
        if(fg == null) {
            l1 = SystemColor.controlHighlight;
            l2 = SystemColor.controlShadow;
        }
        else {
            float[] hsbvals = new float[3];
            Color.RGBtoHSB(fg.getRed(), fg.getGreen(), fg.getBlue(), hsbvals);
            l1 = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2] * 0.9f);
            l2 = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2] * 1.1f);
        }
        
        g.setColor(l1);
        switch (orientation) {
        case HORIZONTAL:
            int c = ( d.height - 1 ) / 2;
            g.drawLine(0, c, d.width, c);
            g.setColor(l2);
            g.drawLine(0, c - 1, d.width, c - 1);
            break;
        case VERTICAL:
            int m = ( d.width - 1 ) / 2;
            g.drawLine(m, 0, m, d.height);
            g.setColor(l2);
            g.drawLine(m - 1, 0, m - 1, d.height - 1);
            break;
        }
    }

    public Dimension getPreferredSize() {
        if(preferredSize != null) {
            return preferredSize;
        }
        else {
            switch (orientation) {
            case HORIZONTAL:
                return new Dimension(0, 2);
            default:
                return new Dimension(2, 0);
            }
        }
    }
    
    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }
}