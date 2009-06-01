package com.sshtools.ui.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

/**
 * An extension of <em>JTabbedPane</em> that renders a close graphic onto each
 * tab.
 * 
 * @author $Author: brett $
 */
public class ClosableTabbedPane extends JTabbedPane implements MouseListener {
    private Vector actions;
    private JPopupMenu popup;

    public ClosableTabbedPane() {
        this(TOP);
    }

    public ClosableTabbedPane(int tabPlacement) {
        super(tabPlacement);
        init();
    }

    public void setIconAt(int index, Icon icon) {
        super.setIconAt(index, new CloseTabIcon(icon));
    }

    private void init() {
        actions = new Vector();
        addMouseListener(this);
    }

    public void removeAllActions() {
        actions.removeAllElements();
    }

    public void addAction(Action action) {
        if (!actions.contains(action)) {
            actions.add(action);
        }
    }

    public void removeAction(Action action) {
        actions.remove(action);
    }

    public void addTab(String title, Component component) {
        this.addTab(title, component, null);
    }

    public void addTab(String title, Component component, Icon extraIcon) {
        super.addTab(title, extraIcon == null ? null : new CloseTabIcon(extraIcon), component);
    }

    public void addTab(String title, Icon extraIcon, Component component, String toolTip) {
        super.addTab(title, extraIcon == null ? null : new CloseTabIcon(extraIcon), component, toolTip);
    }

    public void insertTab(String title, Component component, int idx) {
        this.insertTab(title, component, null, idx);
    }

    public void insertTab(String title, Component component, Icon extraIcon, int idx) {
        super.insertTab(title, extraIcon == null ? null : new CloseTabIcon(extraIcon), component, null, idx);
    }

    public void mouseClicked(MouseEvent e) {
        int tabNumber = getUI().tabForCoordinate(this, e.getX(), e.getY());
        if (tabNumber < 0) {
            return;
        }
        CloseTabIcon cti = ((CloseTabIcon) getIconAt(tabNumber));
        if (cti != null) {
            Rectangle rect = cti.getBounds();
            if (rect.contains(e.getPoint())) {
                Component c = (Component) getSelectedComponent();
                if (c != null) {
                    if (popup == null) {
                        popup = new JPopupMenu(""); //$NON-NLS-1$
                    }
                    popup.setLabel(getTitleAt(getSelectedIndex()));
                    popup.invalidate();
                    popup.removeAll();
                    Action action;
                    for (Enumeration en = actions.elements(); en.hasMoreElements();) {
                        action = (Action) en.nextElement();
                        if (action != null)
                            popup.add(action);
                    }
                    popup.validate();
                    Rectangle r = getUI().getTabBounds(this, tabNumber);
                    popup.show(ClosableTabbedPane.this, r.x, r.y + r.height);
                }
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    class CloseTabIcon implements Icon {
        private int x_pos;

        private int y_pos;

        private int width;

        private int height;

        private Icon fileIcon;

        public CloseTabIcon(Icon fileIcon) {
            this.fileIcon = fileIcon;
            width = fileIcon == null ? 16 : fileIcon.getIconWidth();
            height = fileIcon == null ? 16 : fileIcon.getIconHeight();
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            this.x_pos = x;
            this.y_pos = y;
            if (fileIcon != null) {
                fileIcon.paintIcon(c, g, x, y);
            }
        }

        public int getIconWidth() {
            return width;
        }

        public int getIconHeight() {
            return height;
        }

        public Rectangle getBounds() {
            return new Rectangle(x_pos, y_pos, width, height);
        }
    }
}