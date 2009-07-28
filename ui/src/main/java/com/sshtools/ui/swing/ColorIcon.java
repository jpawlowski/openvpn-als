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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * 
 * 
 * @author $author$
 */
public class ColorIcon implements Icon {
    // Private instance variables
    private Dimension size;
    private Color color;
    private Color borderColor;

    /**
     * Creates a new ColorIcon object.
     */
    public ColorIcon() {
        this(null);
    }

    /**
     * Creates a new ColorIcon object.
     * 
     * @param color
     */
    public ColorIcon(Color color) {
        this(color, null);
    }

    /**
     * Creates a new ColorIcon object.
     * 
     * @param color
     * @param borderColor
     */
    public ColorIcon(Color color, Color borderColor) {
        this(color, null, borderColor);
    }

    /**
     * Creates a new ColorIcon object.
     * 
     * @param color
     * @param size
     * @param borderColor
     */
    public ColorIcon(Color color, Dimension size, Color borderColor) {
        setColor(color);
        setSize(size);
        setBorderColor(borderColor);
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
        g.setColor((color == null) ? Color.white : color);
        g.fillRect(x, y, getIconWidth(), getIconHeight());

        if (borderColor != null) {
            g.setColor(borderColor);
            g.drawRect(x, y, getIconWidth(), getIconHeight());
        }

        
        if(color == null) {
            g.setColor(Color.black);
            g.drawLine(x, y, x + getIconWidth(), y + getIconHeight());
        }
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
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * 
     * 
     * @param borderColor
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * 
     * 
     * @return
     */
    public int getIconWidth() {
        return (size == null) ? 16 : size.width;
    }

    /**
     * 
     * 
     * @return
     */
    public int getIconHeight() {
        return (size == null) ? 16 : size.height;
    }

    public Color getColor() {
        return color;
    }
}