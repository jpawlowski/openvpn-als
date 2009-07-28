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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.Icon;

/**
 * 
 * 
 * @author $author$
 */
public class StrokeIcon implements Icon {
    // Private instance variables
    private Dimension size;
    private Stroke stroke;

    /**
     * Creates a new ColorIcon object.
     */
    public StrokeIcon() {
        this(null);
    }

    /**
     * Creates a new ColorIcon object.
     * 
     * @param color
     */
    public StrokeIcon(Stroke stroke) {
        this(stroke, null);
    }

    /**
     * Creates a new ColorIcon object.
     * 
     * @param color
     * @param size
     * @param borderColor
     */
    public StrokeIcon(Stroke stroke, Dimension size) {
        setStroke(stroke);
        setSize(size);
    }

    /**
     * 
     * 
     * @param c
     * @param g
     * @param x
     * @param y
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(stroke);
        g2.drawLine(x, y, x + getIconWidth(), y);
    }

    /**
     * 
     * 
     * @param size
     */
    public void setSize(Dimension size) {
        this.size = size;
    }

    /**
     * 
     * 
     * @param color
     */
    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    /**
     * 
     * 
     * @return
     */
    public int getIconWidth() {
        return (size == null) ? 48 : size.width;
    }

    /**
     * 
     * 
     * @return
     */
    public int getIconHeight() {
        return (size == null) ? 16 : size.height;
    }
}