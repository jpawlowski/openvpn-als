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

/**
 * $Log: AbstractLayout.java,v $
 * Revision 1.1  2006/10/08 22:58:36  brett
 * Lots of refactoring and tidying up. Converse now depends on ui.
 *
 * Revision 1.1  2004/02/22 22:19:48  brett
 * Reworking of the connection profile editor to be an outlook
 * style side tool bar.
 *
 * Revision 1.5  2003/11/22 17:33:35  t_magicthize
 * Started work on internationalisation. Split the gruntspud.ui package up into more manageable parts. Added option to show text on some actions.
 *
 * Revision 1.4  2003/07/21 20:25:10  t_magicthize
 * Preparation for release.
 *
 * Revision 1.3  2003/03/30 19:21:50  t_magicthize
 * See RELEASE_NOTES.txt (0.4.0-beta)
 *
 * Revision 1.2  2003/01/30 23:37:39  t_magicthize
 * Global source format using jalopy
 *
 * Revision 1.1  2002/12/23 01:22:09  t_magicthize
 * Many improvements. The new preferences dialog UI in the standalone version. Many other small bug fixes.
 *
 * Revision 1.2  2001/12/11 22:24:43  Hymndinner
 * Added 'Log' statements to all source in this tree.
 *
 */
package com.sshtools.ui.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.io.Serializable;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 */
public abstract class AbstractLayout
    implements LayoutManager2, Serializable {
    protected int hgap;
    protected int vgap;

    /**
     * Creates a new AbstractLayout object.
     */
    public AbstractLayout() {
        this(0, 0);
    }

    /**
     * Creates a new AbstractLayout object.
     *
     * @param hgap DOCUMENT ME!
     * @param vgap DOCUMENT ME!
     */
    public AbstractLayout(int hgap, int vgap) {
        setHgap(hgap);
        setVgap(vgap);
    }

    /**
     * Get the horizontal gap between components.
     **/
    public int getHgap() {
        return hgap;
    }

    /**
     * Get the vertical gap between components.
     **/
    public int getVgap() {
        return vgap;
    }

    /**
     * Set the horizontal gap between components.
     * @param gap The horizontal gap to be set
     **/
    public void setHgap(int gap) {
        hgap = gap;
    }

    /**
     * Set the vertical gap between components.
     * @param gap The vertical gap to be set
     **/
    public void setVgap(int gap) {
        vgap = gap;
    }

    /**
     * Returns the maximum dimensions for this layout given
     * the component in the specified target container.
     * @param target The component which needs to be laid out
     **/
    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Returns the alignment along the x axis. This specifies how
     * the component would like to be aligned relative to other
     * components. The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     **/
    public float getLayoutAlignmentX(Container parent) {
        return 0.5f;
    }

    /**
     * Returns the alignment along the y axis. This specifies how
     * the component would like to be aligned relative to other
     * components. The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     **/
    public float getLayoutAlignmentY(Container parent) {
        return 0.5f;
    }

    /**
     * Invalidates the layout, indicating that if the layout
     * manager has cached information it should be discarded.
     **/
    public void invalidateLayout(Container target) {
    }

    /**
     * Adds the specified component with the specified name
     * to the layout. By default, we call the more recent
     * addLayoutComponent method with an object constraint
     * argument. The name is passed through directly.
     * @param name The name of the component
     * @param comp The component to be added
     **/
    public void addLayoutComponent(String name, Component comp) {
        addLayoutComponent(comp, name);
    }

    /**
     * Add the specified component from the layout.
     * By default, we let the Container handle this directly.
     * @param comp The component to be added
     * @param constraints The constraints to apply when laying out.
     **/
    public void addLayoutComponent(Component comp, Object constraints) {
    }

    /**
     * Removes the specified component from the layout.
     * By default, we let the Container handle this directly.
     * @param comp the component to be removed
     **/
    public void removeLayoutComponent(Component comp) {
    }

    /**
     * Return a string representation of the layout manager
     **/
    public String toString() {
        return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
    }
}
