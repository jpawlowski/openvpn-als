package com.sshtools.ui.awt;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * <p>
 * AWT component to provide a tabbed pane similar to Swings JTabbedPane (albeit somewhat simpler!).
 * </p>
 *
 * <p>
 * Each component added except the first will be invisible until the user clicks on the tab heading either above, below. to the
 * left, or to the right of the visible component. Clicking on the heading will hide the current tab and make the new selection
 * visible.
 * </p>
 *
 * @author $Author: lee $
 */

public class TabbedPanel
    extends Container {

  /**
   * Tabs are placed above the components
   */
  public final static int TOP = 0;

  /**
   * Tabs are placed to the left of the components
   */
  public final static int LEFT = 1;

  /**
   * Tabs are placed below the components
   */
  public final static int BOTTOM = 2;

  /**
   * Tabs are placed to the right of the components
   */
  public final static int RIGHT = 3;

  //
  private final static Insets DEFAULT_INSETS = new Insets(2, 2, 2, 2);
  private final static int HORIZONTAL_GAP = 3;
  private final static int VERTICAL_GAP = 3;
  private final static int IMAGE_TEXT_GAP = 2;

  //  Private instance variables
  private int position;
  private FontMetrics metrics;
  private Hashtable tabs;
  private int sel;
  private TabbedLayout layout;
  private Vector listenerList;

  /**
   * <p>
   * Create a tabbed panel with the tabs at the specified position. Can be one of :-
   * </p>
   * <ul>
   * <li>TabbedPanel.TOP</li>
   * <li>TabbedPanel.LEFT</li>
   * <li>TabbedPanel.BOTTOM</li>
   * <li>TabbedPanel.RIGHT</li>
   * </ul>
   *
   * <p>
   * <b><font color="#ff0000">Note, only <i>TOP</i> is currently supported</font></b>
   * </p>
   *
   * @param position position
   */
  public TabbedPanel(int position) {
    super();

    tabs = new Hashtable();
    listenerList = new Vector();
    sel = -1;

    setLayout(layout = new TabbedLayout(3, 3));
    setPosition(position);

    addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        for (Enumeration e = tabs.elements(); e.hasMoreElements(); ) {
          TabWrapper w = (TabWrapper) e.nextElement();
          if (w.bounds != null && w.bounds.contains(evt.getX(), evt.getY())) {
            int idx = indexOfTab(w);
            if (idx != sel) {
              setSelectedTab(idx);
            }
            break;
          }
        }

      }
    });

    //        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
  }

  /**
   * Add an <code>ActionListener</code> to be informed when the user selects
   * a tab.
   *
   * @param l listener to add
   */
  public void addActionListener(ActionListener l) {
    listenerList.addElement(l);
  }

  /**
   * Remove an <code>ActionListener</code> so as to no longer be informed when
   * the user selects a tab.
   *
   * @param l listener to remove
   */
  public void removeActionListener(ActionListener l) {
    listenerList.removeElement(l);
  }

  /**
   * Set the title of a tab at the given index
   *
   * @param i index of tab
   * @param title new tab title
   */
  public void setTitleAt(int i, String title) {
    Component c = getComponent(sel);
    TabWrapper t = (TabWrapper) tabs.get(c);
    t.text = title;
    repaint();
  }

  /**
   * Set the selected tab given its index.
   *
   * @param idx index of tab to select
   */
  public void setSelectedTab(int idx) {
    sel = idx;
    if (sel != -1) {
      int s = listenerList.size();
      ActionEvent aevt = null;
      for (int i = s - 1; i >= 0; i--) {
        if (aevt == null) {
          aevt = new ActionEvent(TabbedPanel.this, ActionEvent.ACTION_PERFORMED,
                                 ""); //$NON-NLS-1$
        }
        ( (ActionListener) listenerList.elementAt(i)).actionPerformed(aevt);
      }
      Component c = getComponent(sel);
      TabWrapper t = (TabWrapper) tabs.get(c);
      if(t != null) {
          layout.show(this, (String) t.name);
      }
      repaint();
    }
  }

  /**
   * Get the index of the currently selected tab
   *
   * @return selected tab index
   */
  public int getSelectedTab() {
    return sel;
  }

  /**
   * <p>
   * Create a tabbed panel with the tabs at the specified position. Can be one of :-
   * </p>
   * <ul>
   * <li>TabbedPanel.TOP</li>
   * <li>TabbedPanel.LEFT</li>
   * <li>TabbedPanel.BOTTOM</li>
   * <li>TabbedPanel.RIGHT</li>
   * </ul>
   *
   * <p>
   * <b><font color="#ff0000">Note, only <i>TOP</i> is currently supported</font></b>
   * </p>
   *
   * @param position position
   */
  public void setPosition(int position) {
    doLayout();
    repaint();
  }

  public void remove(int idx) {
    Component c = getComponent(idx);
    TabWrapper t = (TabWrapper) tabs.get(c);
    tabs.remove(c);
    if (sel == idx) {
      setSelectedTab(tabs.size() - 1);
    }
    super.remove(idx);
  }

  /**
   * Add a tab.
   *
   * @param comp component
   * @param constraints tab name
   * @param index tab index
   * @param image tab image
   */
  public void add(Component comp, Object constraints, int index, Image image) {
    TabWrapper w = new TabWrapper(comp.getName(), String.valueOf(constraints),
                                  image, comp, constraints);
    tabs.put(comp, w);
    super.addImpl(comp, comp.getName(), index);
    if (sel == -1) {
      setSelectedTab(0);
    }
    else {
      repaint();
    }
  }

  public int getPosition() {
    return position;
  }

  public void removeNotify() {
    super.removeNotify();
    if (getComponentCount() == 0) {
      sel = -1;
    }
  }

  protected void addImpl(Component comp, Object constraints, int index) {
    add(comp, constraints, index, null);
  }

  public void add(Component comp, Object constraints, Image image) {
    add(comp, constraints, -1, image);
  }

  public Insets getInsets() {
    return DEFAULT_INSETS;
  }

  public void addNotify() {
    super.addNotify();
    metrics = getFontMetrics(getFont());
  }

  public void paint(Graphics g) {
    super.paint(g);

    Dimension s = getSize();
    Rectangle r = getHeadingBounds();
    int ncomponents = getComponentCount();
    int sel = getSelectedTab();

    // Work out the colors for the border
    Color c = getBackground();
    Color midlight = c.brighter();
    Color highlight = midlight.brighter();
    Color lowlight = c.darker();
    Color shadow = lowlight.darker();
    Color darkShadow = shadow.darker();

    //
    switch (position) {
      case TOP:

        //
        g.setColor(darkShadow);
        g.drawLine(0, s.height - 1, s.width - 1, s.height - 1);
        g.drawLine(s.width - 1, s.height - 1, s.width - 1, r.height - 1);
        //
        g.setColor(shadow);
        g.drawLine(1, s.height - 2, s.width - 2, s.height - 2);
        g.drawLine(s.width - 2, s.height - 2, s.width - 2, r.height);
        //
        g.setColor(highlight);
        g.drawLine(0, s.height - 2, 0, r.height);

        //  Paint the tabs
        int x = 0;
        int selx = -1;

        for (int i = 0; i < ncomponents; i++) {
          Component comp = getComponent(i);
          TabWrapper tab = (TabWrapper) tabs.get(comp);

          if (tab != null) {

            int hw = metrics.stringWidth(tab.text) + (HORIZONTAL_GAP * 2);
            tab.bounds = new Rectangle(x, 0, hw, r.height);

            if (sel == i) {
              selx = x;
              //                    g.setColor(darkShadow);
            }
            else {
              g.setColor(lowlight);
              g.fillRect(x, 3, hw - 1, r.height - 4);

              //
              g.setColor(midlight);
              g.drawLine(x, 3, x, r.height - 2);
              g.drawLine(x + 1, 2, x + hw - 2, 2);
              //
              g.setColor(darkShadow);
              g.drawLine(x + hw - 1, 3, x + hw - 1, r.height - 2);
              //
              g.setColor(shadow);
              g.drawLine(x + hw - 2, 4, x + hw - 2, r.height - 2);
              //
              g.setColor(highlight);
              g.drawLine(x, r.height - 1, x + hw - 1, r.height - 1);

              g.setColor(getForeground());
              g.drawString(tab.text, x + HORIZONTAL_GAP,
                           (r.height / 2) + (metrics.getHeight() / 2) - 1);
              //                    g.setColor(darkShadow);
            }

            x += hw;
          }

        }
        g.setColor(highlight);
        g.drawLine(x, r.height - 1, s.width - 2, r.height - 1);

        if (selx != -1) {
          x = selx;
          Component comp = getComponent(sel);
          TabWrapper tab = (TabWrapper) tabs.get(comp);

          int hw = metrics.stringWidth(tab.text) + (HORIZONTAL_GAP * 2);

          //                    g.setColor(getBackground());
          //                    g.fillRect(x, 3, hw - 1, r.height - 3);

          //
          g.setColor(highlight);
          g.drawLine(x, 1, x, r.height - 2);
          g.drawLine(x + 1, 0, x + hw - 1, 0);
          //
          g.setColor(darkShadow);
          g.drawLine(x + hw, 1, x + hw, r.height - 2);
          //
          g.setColor(shadow);
          g.drawLine(x + hw - 1, 2, x + hw - 1, r.height - 2);

          g.setColor(getForeground());
          g.drawString(tab.text, x + HORIZONTAL_GAP,
                       (r.height / 2) + (metrics.getHeight() / 2) - 3);
        }
        break;
    }
  }

  //
  private Dimension getHeadingSize() {
    Dimension s = new Dimension();
    int c = getComponentCount();
    for (int i = 0; i < c; i++) {
      Component comp = getComponent(i);
      TabWrapper tab = (TabWrapper) tabs.get(comp);
      if (tab != null) {
        int thw =
            (HORIZONTAL_GAP * 2)
            +
            (metrics != null ?
             metrics.stringWidth(tab.text == null ? "" : tab.text) : 0) //$NON-NLS-1$
            + (tab.image == null ? 0 : (IMAGE_TEXT_GAP + tab.image.getWidth(this)));
        int thh = (VERTICAL_GAP * 2) +
            Math.max(metrics != null ? metrics.getHeight() : 0,
                     tab.image == null ? 0 : tab.image.getHeight(this));
        if (position == TOP || position == BOTTOM) {
          s.width += thw;
          s.height = Math.max(s.height, thh);
        }
        else {
          s.height += thh;
          s.width = Math.max(s.width, thw);
        }
      }
      else {
        /*DEBUG*/System.err.println(Messages.getString("TabbedPanel.tabWrapperCouldNotBeFound") + i + " [" + comp + //$NON-NLS-1$ //$NON-NLS-2$
                                    " could not be found"); //$NON-NLS-1$
      }
    }
    return s;

  }

  /*
   * Get the bounds of the tab headings
   */
  private Rectangle getHeadingBounds() {
    Dimension s = getHeadingSize();
    Point loc = new Point();
    switch (position) {
      case BOTTOM:
        loc.y = getSize().height - s.height;
        break;
      case RIGHT:
        loc.x = getSize().width - s.width;
        break;
    }
    return new Rectangle(loc.x, loc.y, s.width, s.height);
  }

  private int indexOfTab(TabWrapper tab) {
    int c = getComponentCount();
    for (int i = 0; i < c; i++) {
      if (tab.component == getComponent(i)) {
        return i;
      }
    }
    return -1;
  }

  //  Debug
  public static void main(String[] args) {
    Frame frame = new Frame("Tabs"); //$NON-NLS-1$
    frame.setLayout(new BorderLayout());

    Label l1 = new Label("Test label 1"); //$NON-NLS-1$
    Panel p1 = new Panel(new BorderLayout());
    p1.add(l1, BorderLayout.CENTER);

    Label l2 = new Label("Test label 2"); //$NON-NLS-1$
    Panel p2 = new Panel(new BorderLayout());
    p2.add(l2, BorderLayout.CENTER);

    Label l3 = new Label("Test label 3"); //$NON-NLS-1$
    Panel p3 = new Panel(new BorderLayout());
    p3.add(l3, BorderLayout.CENTER);

    Label l4 = new Label("Test label 4"); //$NON-NLS-1$
    Panel p4 = new Panel(new BorderLayout());
    p4.add(l4, BorderLayout.CENTER);

    TabbedPanel tabs = new TabbedPanel(TabbedPanel.TOP);
    tabs.add("Test Tab 1", p1); //$NON-NLS-1$
    tabs.add(Messages.getString("TabbedPanel.11"), p2); //$NON-NLS-1$
    tabs.add(Messages.getString("TabbedPanel.12"), p3); //$NON-NLS-1$
    tabs.add(Messages.getString("TabbedPanel.13"), p4); //$NON-NLS-1$

    tabs.setSelectedTab(1);

    frame.add(tabs, BorderLayout.CENTER);
    frame.pack();
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        System.exit(0);
      }

    });
    frame.setVisible(true);
  }

  //  Supporting classes

  class TabWrapper {
    Component component;
    String text;
    Image image;
    Rectangle bounds;
    Object constraints;
    String name;

    TabWrapper(String name, String text, Image image, Component component,
               Object contraints) {
      this.name = name;
      this.text = text;
      this.image = image;
      this.component = component;
      this.constraints = contraints;
    }
  }

  class TabbedLayout
      extends CardLayout {
    TabbedLayout(int hgap, int vgap) {
      super(hgap, vgap);
    }

    public void layoutContainer(Container parent) {
      synchronized (parent.getTreeLock()) {
        Insets insets = parent.getInsets();
        int ncomponents = parent.getComponentCount();
        Component comp = null;
        boolean currentFound = false;
        Dimension s = getHeadingSize();
        int headingSize = position == TOP || position == BOTTOM ? s.height :
            s.width;
        insets =
            new Insets(
            insets.top + (position == TOP ? headingSize : 0),
            insets.left + (position == LEFT ? headingSize : 0),
            insets.bottom + (position == BOTTOM ? headingSize : 0),
            insets.right + (position == BOTTOM ? headingSize : 0));
        for (int i = 0; i < ncomponents; i++) {
          comp = parent.getComponent(i);
          comp.setBounds(
              getHgap() + insets.left,
              getVgap() + insets.top,
              parent.getSize().width -
              (getHgap() * 2 + insets.left + insets.right),
              parent.getSize().height -
              (getVgap() * 2 + insets.top + insets.bottom));
          if (comp.isVisible()) {
            currentFound = true;
          }
        }

        if (!currentFound && ncomponents > 0) {
          parent.getComponent(0).setVisible(true);
        }
      }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
     */
    public Dimension minimumLayoutSize(Container parent) {
      Dimension s = super.maximumLayoutSize(parent);
      Dimension c = getHeadingSize();
      return position == TOP
          || position == BOTTOM
          ? new Dimension(Math.max(s.width, c.width), s.height + c.height)
          : new Dimension(s.width + c.width, Math.max(s.height, c.height));
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
     */
    public Dimension preferredLayoutSize(Container parent) {
      Dimension s = super.preferredLayoutSize(parent);
      Dimension c = getHeadingSize();
      return position == TOP
          || position == BOTTOM
          ? new Dimension(Math.max(s.width, c.width), s.height + c.height)
          : new Dimension(s.width + c.width, Math.max(s.height, c.height));
    }
  }

  /**
   * @return
   */
  public int getSelectedTabIndex() {
    return sel;
  }

}
