package com.sshtools.ui.awt;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.FilteredImageSource;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Vector;

import com.sshtools.ui.awt.tooltips.ToolTipManager;

/**
 * A menubar implementation
 * 
 * @author $Autho$
 */
public class ActionMenuBar extends Panel implements FocusListener, ComponentListener, PropertyChangeListener {

    Vector menus = new Vector();
    Window menuWindow;
    MenuCanvas menuCanvas;
    MenuAction currentMenu;
    Frame parentFrame;
    Color disabledForeground = SystemColor.textInactiveText;
    boolean toolTipsEnabled;
    String icons;
    String iconType = ActionButton.SMALL_ICONS;
    int imageTextGap = 4;
    Separator separator;
    Color baseBackground = null;
    Color baseForeground = null;

    public ActionMenuBar() {
        super();
        separator = new Separator(Separator.HORIZONTAL);
        separator.setPreferredSize(new Dimension(0, 5));
        setLayout(new ToolLayout(separator));
        setBackground(SystemColor.control);
        setForeground(SystemColor.controlText);
        //    add(separator);
    }

    public void setImageTextGap(int imageTextGap) {
        invalidate();
        this.imageTextGap = imageTextGap;
        validate();
        repaint();
    }

    public void setIconType(String iconType) {
        invalidate();
        this.iconType = iconType;
        validate();
        repaint();
    }

    public void setToolTipsEnabled(boolean toolTipsEnabled) {
        this.toolTipsEnabled = toolTipsEnabled;
        if (!toolTipsEnabled) {
            ToolTipManager.getInstance().hide();
        }
    }

    public void setDisabledForeground(Color disabledForeground) {
        this.disabledForeground = disabledForeground;
        repaint();
    }

    public void addActionMenu(ActionMenu actionMenu) {
        final MenuAction menuAction = new MenuAction(actionMenu);
        ActionButton button = new ActionButton(menuAction) {
            public boolean isFocusTraversable() {
                return true;
            }

            public boolean isFocusable() {
                return true;
            }

        };
        button.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                if (currentMenu != null) {
                    hideMenuWindow();
                    showMenuWindow(menuAction);
                }
            }

        });
        for (Enumeration e = actionMenu.children(); e.hasMoreElements();) {
            Action action = (Action) e.nextElement();
            action.addPropertyChangeListener(this);
        }
        menuAction.setButton(button);
        if(baseForeground != null) {
            button.setBaseForeground(baseForeground);
        }
        if(baseBackground != null) {
            button.setBaseBackground(baseBackground);
        }
        menus.addElement(menuAction);
        button.addFocusListener(this);
        add(button);
    }

    public void remove(int index) {
        synchronized (getTreeLock()) {
            super.remove(index);
            MenuAction menuAction = (MenuAction) menus.elementAt(index);
            menuAction.button.removeFocusListener(this);
            menus.removeElementAt(index);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#finalize()
     */
    protected void finalize() throws Throwable {
        super.finalize();
        if (menuWindow != null) {
            parentFrame.removeComponentListener(this);
        }
    }

    void showMenuWindow(MenuAction menu) {
        currentMenu = menu;
        if (menuWindow == null) {
            parentFrame = UIUtil.getFrameAncestor(this);
            if (parentFrame == null) {
                parentFrame = UIUtil.getSharedFrame();
            }
            parentFrame.addComponentListener(this);
            menuWindow = new Window(parentFrame);
            menuCanvas = new MenuCanvas();
            if(baseBackground != null) {
                menuCanvas.setBaseBackground(baseBackground);
            }
            if(baseForeground != null) {
                menuCanvas.setBaseForeground(baseForeground);
            }
            menuCanvas.itemHeight = currentMenu.button.getSize().height;
            menuWindow.setLayout(new GridLayout(1, 1));
            menuWindow.add(menuCanvas);
        }
        int x = currentMenu.button.getLocationOnScreen().x;
        int y = currentMenu.button.getLocationOnScreen().y;
        y += currentMenu.button.getSize().height;
        menuWindow.setLocation(x, y);
        Dimension d = menuCanvas.getPreferredSize();
        menuCanvas.setMenu(currentMenu.menu);
        menuWindow.setSize(0, 0);
        menuWindow.pack();
        menuWindow.setVisible(true);
        currentMenu.button.setPressed(true);

    }

    void hideMenuWindow() {
        if (menuWindow != null) {
            menuWindow.setVisible(false);
            if (currentMenu != null) {
                currentMenu.button.setPressed(false);
            }
            currentMenu = null;
        }
    }

    /**
     * @return
     */
    public int getMenuCount() {
        return menus.size();
    }

    class MenuAction extends AbstractAction {

        private ActionMenu menu;
        private ActionButton button;

        MenuAction(ActionMenu menu) {
            super(menu.getDisplayName());
            this.menu = menu;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
                showMenuWindow(this);
            } else {
                hideMenuWindow();
            }
        }

        void setButton(ActionButton button) {
            this.button = button;
        }

    }

    class MenuCanvas extends Canvas implements MouseMotionListener, MouseListener {

        ActionMenu menu;
        int borderWidth = 1;
        Insets insets = new Insets(1, 16, 1, 16);
        Color borderColor = SystemColor.controlShadow;
        int sel = -1;
        Color selectionBackground = SystemColor.controlLtHighlight;
        Color selectionForeground = SystemColor.textText;
        int itemHeight = 16;
        String toolTipText;
        Image backingStore;

        MenuCanvas() {
            addMouseMotionListener(this);
            addMouseListener(this);
            setBackground(SystemColor.control);
            setForeground(SystemColor.controlText);
        }
        
        void setBaseBackground(Color base) {
            if(base == null) {
                selectionBackground = SystemColor.controlHighlight;
                setBackground(SystemColor.control);
            }
            else {
                selectionBackground = base.darker();
                setBackground(base);
            }
        }
        
        void setBaseForeground(Color base) {
            if(base == null) {
                borderColor = SystemColor.controlShadow;
                selectionForeground = SystemColor.controlText;
                setForeground(SystemColor.controlText);
            }
            else {
                borderColor=  base.darker();
                selectionForeground = base.brighter();
                setForeground(base);
            }
        }

        public Dimension getPreferredSize() {
            if (menu == null || getGraphics() == null) {
                return new Dimension(0, 0);
            }
            int w = 0;
            int h = 0;
            int iw = 0;
            FontMetrics fm = getFontMetrics(getFont());
            for (Enumeration e = menu.children(); e.hasMoreElements();) {
                Action action = (Action) e.nextElement();
                if(action.getName().equals(ActionMenu.SEPARATOR.getName())) {
                    h += separator.getPreferredSize().height;
                }
                else {
	                String imagePath = null;
	                if (iconType.equals(ActionButton.SMALL_ICONS)) {
	                    imagePath = (String) action.getValue(Action.SMALL_IMAGE_PATH);
	                } else if (iconType.equals(ActionButton.LARGE_ICONS)) {
	                    imagePath = (String) action.getValue(Action.IMAGE_PATH);
	                }
	                String n = (String) action.getValue(Action.SHORT_DESCRIPTION);
	                n = n == null ? action.getName() : n;
	                w = Math.max(w, fm.stringWidth(n));
	                itemHeight = Math.max(itemHeight, fm.getHeight());
	                if (imagePath != null) {
	                    Image img = UIUtil.loadImage(action.getClass(), imagePath);
	                    if (img != null) {
	                        UIUtil.waitFor(img, this);
	                        iw = Math.max(iw, img.getWidth(this));
	                        itemHeight = Math.max(itemHeight, img.getHeight(this));
	                    }
	                }
	                h += itemHeight;
                }
            }
            if (iw != 0) {
                w += iw;
                w += imageTextGap;
            }
            if (insets != null) {
                w += insets.left + insets.right;
                h += insets.top + insets.bottom;
            }
            w += borderWidth * 2;
            h += borderWidth * 2;
            return new Dimension(w, h);
        }

        public void update(Graphics g) {
            paint(g);
        }

        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        public void setMenu(ActionMenu menu) {
            this.menu = menu;
            menuCanvas.sel = -1;
        }

        public void paint(Graphics ig) {
            Dimension s = getSize();
            if (backingStore == null
                            || (backingStore != null && (s.width != backingStore.getWidth(this) || s.height != backingStore
                                            .getHeight(this)))) {
                if (backingStore != null) {
                    backingStore.getGraphics().dispose();
                }
                backingStore = createImage(s.width, s.height);
            }
            Graphics g = backingStore.getGraphics();
            g.setColor(getBackground());
            g.fillRect(0, 0, s.width - 1, s.height - 1);
            int px = 0;
            int py = 0;
            g.setColor(borderColor);
            for (int i = 0; i < borderWidth; i++) {
                g.drawRect(px, py, s.width - px - 1, s.height - py - 1);
                px++;
                py++;
            }
            if (insets != null) {
                px += insets.left;
                py += insets.top;
            }
            if (menu != null) {
                Action m = null;
                FontMetrics fm = getFontMetrics(getFont());
                //                py += fm.getHeight() - fm.getDescent();
                int i = 0;
                for (Enumeration e = menu.children(); e.hasMoreElements();) {
                    m = (Action) e.nextElement();
                    if(m.getName().equals(ActionMenu.SEPARATOR.getName())) {
                        int h = separator.getPreferredSize().height;
                        int sw = s.width - ( insets == null ? 0 : ( ( insets.left + insets.right ) / 2 ) ) - ( borderWidth * 2 );
                        separator.setBounds(0, 0, sw, h);
                        int sx = (s.width - sw ) / 2;
                        g.translate(sx, py);
                        separator.paint(g);
                        g.translate(-sx, -py);
                        py += h;
                    }
                    else {
	                    String n = (String) m.getValue(Action.SHORT_DESCRIPTION);
	                    n = n == null ? m.getName() : n;
	                    if (m.isEnabled()) {
	                        if (i == sel) {
	                            g.setColor(selectionBackground);
	                            int ty = borderWidth + (insets != null ? insets.top : 0) + (py - borderWidth - insets.top );
	                            g.fillRect(borderWidth, ty, s.width - (borderWidth * 2), itemHeight);
	                            if (borderWidth != 0) {
	                                g.setColor(borderColor);
	                                g.drawLine(borderWidth, ty, (borderWidth * 2) + s.width - 1, ty);
	                                g.drawLine(borderWidth, ty + itemHeight - 1, (borderWidth * 2) + s.width - 1, ty + itemHeight - 1);
	                            }
	                            g.setColor(selectionForeground);
	                        } else {
	                            g.setColor(getForeground());
	                        }
	                    } else {
	                        g.setColor(disabledForeground);
	                    }
	                    String imagePath = null;
	                    if (iconType.equals(ActionButton.SMALL_ICONS)) {
	                        imagePath = (String) m.getValue(Action.SMALL_IMAGE_PATH);
	                    } else if (iconType.equals(ActionButton.LARGE_ICONS)) {
	                        imagePath = (String) m.getValue(Action.IMAGE_PATH);
	                    }
	                    int toff = 0;
	                    if (imagePath != null) {
	                        Image img = UIUtil.loadImage(m.getClass(), imagePath);
	                        if(!m.isEnabled()) {
	                            img = createImage(new FilteredImageSource(img.getSource(),
	                            new GrayFilter()));
	                            UIUtil.waitFor(img, this);
	                        }
	                        
	                        if (img != null) {
	                            g.drawImage(img, px, py + ((itemHeight - img.getHeight(this)) / 2), this);
	                            toff = imageTextGap + img.getWidth(this);
	                        }
	                    }
	                    g.drawString(n, px + toff, py + fm.getHeight() - fm.getDescent() + ((itemHeight - fm.getHeight()) / 2));
	                    py += itemHeight;
                    }
                    i++;
                }
            }
            ig.drawImage(backingStore, 0, 0, this);
        }

        int getIndexForLocation(int x, int ey) {
            int idx = -1;
            if (menu != null && getGraphics() != null) {
                int y = borderWidth;
                if (insets != null) {
                    y += insets.top;
                }
                FontMetrics fm = getFontMetrics(getFont());
                Action m = null;
                for (Enumeration e = menu.children(); e.hasMoreElements();) {
                    idx++;
                    m = (Action) e.nextElement();                   
                    if(!m.getName().equals(ActionMenu.SEPARATOR.getName())) {
                        y+= itemHeight;
                        if(y >= ey) {
                            return idx;
                        }
                    }
                    else {
                        y += separator.getPreferredSize().height;
                    }
                }
            }
            return idx;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
         */
        public void mouseDragged(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
         */
        public void mouseMoved(MouseEvent e) {
            sel = getIndexForLocation(e.getX(), e.getY());
            if (sel != -1) {
                Action action = menu.getChild(sel);
                if (action.isEnabled()) {
                    String tip = (String) action.getValue(Action.LONG_DESCRIPTION);
                    if (tip != null) {
                        Point p = getLocationOnScreen();
                        if (toolTipsEnabled) {
                            ToolTipManager.getInstance().requestToolTip(this, p.x + e.getX() + 4, p.y + e.getY() + 4, tip);
                        }
                    }
                } else {
                    sel = -1;
                    if (toolTipsEnabled) {
                        ToolTipManager.getInstance().hide();
                    }
                }
                repaint();
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent e) {
            if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
                int clicked = getIndexForLocation(e.getX(), e.getY());
                if (clicked != -1 && clicked < menu.getChildCount()) {
                    Action action = menu.getChild(clicked);
                    action.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, action.getName(), e.getModifiers()));
                }
            }
            hideMenuWindow();

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
         */
        public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
         */
        public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    public void focusGained(FocusEvent e) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    public void focusLost(FocusEvent e) {
        hideMenuWindow();
    }

    /**
     *  
     */
    public void removeAllMenuItems() {
        for (Enumeration e = menus.elements(); e.hasMoreElements();) {
            MenuAction act = (MenuAction) e.nextElement();
            for (Enumeration e2 = act.menu.children(); e2.hasMoreElements();) {
                Action a = (Action) e2.nextElement();
                a.removePropertyChangeListener(this);
            }
        }
        hideMenuWindow();
        menus.removeAllElements();
        removeAll();
        doLayout();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    public void componentResized(ComponentEvent e) {
        hideMenuWindow();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
     */
    public void componentMoved(ComponentEvent e) {
        hideMenuWindow();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
     */
    public void componentShown(ComponentEvent e) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
     */
    public void componentHidden(ComponentEvent e) {
        hideMenuWindow();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        repaint();
    }

    /**
     * @param background
     */
    public void setBaseBackground(Color baseBackground) {
        setBackground(baseBackground == null ? SystemColor.control : baseBackground);
        this.baseBackground = baseBackground;
        for(Enumeration e = menus.elements(); e.hasMoreElements(); ) {
            MenuAction m = (MenuAction)e.nextElement();
            m.button.setBaseBackground(baseBackground);
        }
        if(menuCanvas != null) {
            menuCanvas.setBaseBackground(baseBackground);
        }
    }

    /**
     * @param background
     */
    public void setBaseForeground(Color baseForeground) {
        setForeground(baseForeground == null ? SystemColor.controlText : baseForeground);
        this.baseForeground = baseForeground;
        for(Enumeration e = menus.elements(); e.hasMoreElements(); ) {
            MenuAction m = (MenuAction)e.nextElement();
            m.button.setBaseForeground(baseForeground);
        }
        if(menuCanvas != null) {
            menuCanvas.setBaseForeground(baseForeground);
        }
    }
}