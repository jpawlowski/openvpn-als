/*
 */
package com.sshtools.ui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class ArrowIcon implements Icon, SwingConstants {
    private Color shadow;
    private Color darkShadow;
    private Color highlight;
    private int direction;

    public ArrowIcon(int direction, Color shadow, Color darkShadow, Color highlight) {
        this.shadow = shadow;
        this.darkShadow = darkShadow;
        this.highlight = highlight;
        setDirection(direction);
    }

    public ArrowIcon(int direction) {
        this(direction, UIManager.getColor("controlShadow"), UIManager.getColor("controlDkShadow"), UIManager
            .getColor("controlLtHighlight"));
    }

    public int getIconHeight() {
        return 16;
    }

    public int getIconWidth() {
        return 16;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int dir) {
        direction = dir;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        int h = getIconHeight();
        int w = getIconWidth();
        int size = Math.min((h - 4) / 3, (w - 4) / 3);
        size = Math.max(size, 2);
        paintTriangle(g, x + ((w - size) / 2), y + ((h - size) / 2), size, direction, true);
    }

    public void paintTriangle(Graphics g, int x, int y, int size, int direction, boolean isEnabled) {
        Color oldColor = g.getColor();
        int mid, i, j;
        j = 0;
        size = Math.max(size, 2);
        mid = (size / 2) - 1;
        g.translate(x, y);
        if (isEnabled)
            g.setColor(darkShadow);
        else
            g.setColor(shadow);
        switch (direction) {
            case NORTH:
                for (i = 0; i < size; i++) {
                    g.drawLine(mid - i, i, mid + i, i);
                }
                if (!isEnabled) {
                    g.setColor(highlight);
                    g.drawLine(mid - i + 2, i, mid + i, i);
                }
                break;
            case SOUTH:
                if (!isEnabled) {
                    g.translate(1, 1);
                    g.setColor(highlight);
                    for (i = size - 1; i >= 0; i--) {
                        g.drawLine(mid - i, j, mid + i, j);
                        j++;
                    }
                    g.translate(-1, -1);
                    g.setColor(shadow);
                }
                j = 0;
                for (i = size - 1; i >= 0; i--) {
                    g.drawLine(mid - i, j, mid + i, j);
                    j++;
                }
                break;
            case WEST:
                for (i = 0; i < size; i++) {
                    g.drawLine(i, mid - i, i, mid + i);
                }
                if (!isEnabled) {
                    g.setColor(highlight);
                    g.drawLine(i, mid - i + 2, i, mid + i);
                }
                break;
            case EAST:
                if (!isEnabled) {
                    g.translate(1, 1);
                    g.setColor(highlight);
                    for (i = size - 1; i >= 0; i--) {
                        g.drawLine(j, mid - i, j, mid + i);
                        j++;
                    }
                    g.translate(-1, -1);
                    g.setColor(shadow);
                }
                j = 0;
                for (i = size - 1; i >= 0; i--) {
                    g.drawLine(j, mid - i, j, mid + i);
                    j++;
                }
                break;
        }
        g.translate(-x, -y);
        g.setColor(oldColor);
    }
}