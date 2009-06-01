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

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.SwingConstants;

/**
 * Create a {@link Icon} from an existing {@link Icon} by overlaying another 
 * {@link Icon}.
 * <p>
 * The overlaid icon may be placed at any compass point.
 * 
 * @author Brett Smith <a href="mailto: brett@3sp.com">&lt;brett@3sp.com&gt;</a>
 * @see SwingConstants
 */
public class OverlayIcon implements Icon {
    
    // Private instance variables
    
    private Icon icon;
    private Icon overlayIcon;
    private int position;

    /**
     * Constructor for the OverlayIcon object
     * 
     * @param overlayIcon Description of the Parameter
     * @param icon Description of the Parameter
     * @param position Description of the Parameter
     */
    public OverlayIcon(Icon overlayIcon, Icon icon, int position) {
        this.icon = icon;
        this.overlayIcon = overlayIcon;
        this.position = position;
    }

    /**
     * Get the overlay icon
     * 
     * @return overlay icon
     */
    public Icon getOverlayIcon() {
        return overlayIcon;
    }

    /* (non-Javadoc)
     * @see javax.swing.Icon#getIconHeight()
     */
    public int getIconHeight() {
        return (icon == null) ? 16 : icon.getIconHeight();
    }

    /* (non-Javadoc)
     * @see javax.swing.Icon#getIconWidth()
     */
    public int getIconWidth() {
        return (icon == null) ? 16 : icon.getIconWidth();
    }

    /* (non-Javadoc)
     * @see javax.swing.Icon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        if (icon != null) {
            icon.paintIcon(c, g, x, y);

            switch (position) {
                // TODO complete compass points and center
                case SwingConstants.NORTH_WEST:
                    overlayIcon.paintIcon(c, g, x, y);
                    break;
                case SwingConstants.SOUTH_WEST:
                    overlayIcon.paintIcon(c, g, x, (y + icon.getIconHeight()) - overlayIcon.getIconHeight());
                    break;
                case SwingConstants.NORTH_EAST:
                    overlayIcon.paintIcon(c, g, (x + icon.getIconWidth()) - overlayIcon.getIconWidth(), y);
                default:
                    overlayIcon.paintIcon(c, g, (x + icon.getIconWidth()) - overlayIcon.getIconWidth(), (y + icon.getIconHeight())
                                    - overlayIcon.getIconHeight());
            }
        }
    }
}
