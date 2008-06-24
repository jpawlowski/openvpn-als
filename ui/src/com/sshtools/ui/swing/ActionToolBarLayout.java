/*
 */
package com.sshtools.ui.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;

public class ActionToolBarLayout implements LayoutManager {
  
  private boolean wrap;
  private int overunIndex;
  private int height;
  private Component expandComponent;

  public ActionToolBarLayout(Component expandComponent) {
    overunIndex = -1;
    setExpandComponent(expandComponent); 
  }
  
  /**
   * 
   */
  public ActionToolBarLayout() {
    this(null);
  }

  public void setExpandComponent(Component expandComponent) {
    this.expandComponent = expandComponent;
  }
  
  public Component getExpandComponent() {
    return expandComponent;
  }

  public void addLayoutComponent(String con, Component c) {
  }

  public void removeLayoutComponent(Component c) {
  }

  public void layoutContainer(Container target) {
    synchronized (target.getTreeLock()) {
      Insets insets = target.getInsets();
      Dimension s = target.getSize();
      int x = insets.left;
      int y = insets.top;
      int count = target.getComponentCount();
      int rowHeight = 0;
      int maxRowHeight = -1;
      Component c = null;
      Rectangle b = null;
      Dimension z = null;
      overunIndex = -1;
      Dimension e = expandComponent != null ? expandComponent.getPreferredSize() : null;
      for(int i = 0 ; i < count && overunIndex == -1 ; i++) {
        c = target.getComponent(i);
        if(c != expandComponent) {
          z = c.getPreferredSize();
          rowHeight = Math.max(rowHeight, z.height);
          if(maxRowHeight == -1) {
            maxRowHeight = rowHeight;
          }
          if(z.width + x >= ( ( s.width - insets.left ) - ( e != null ? e.width : 0 ) ) ) {
            if(wrap) {
              y += rowHeight;
              x = insets.left;
              rowHeight = 0;
              c.setBounds(x, y, z.width, maxRowHeight);
            }
            else {
              overunIndex = i;
            }
          }
          else {
            c.setBounds(x, y, z.width, maxRowHeight);
          }
          x += z.width;
        }
      }
      if(overunIndex != -1) {
        for(int i = overunIndex; i < count ; i++) {
          c = target.getComponent(i);
          if(c != expandComponent) {
            c.setBounds(0, 0, 0, 0);
          }
        }
      }
      if(e != null) {
        if(overunIndex != -1) {
          Rectangle r = new Rectangle(s.width - insets.right - e.width, insets.top, e.width, maxRowHeight);
          expandComponent.setBounds(r);
        }
        else {
          expandComponent.setBounds(0, 0, 0, 0);          
        }
      }
    }
  }

  public Dimension minimumLayoutSize(Container target) {
    synchronized (target.getTreeLock()) {
      Insets insets = target.getInsets();
      int count = target.getComponentCount();
      Dimension d = new Dimension(insets.left, 0);
      Component c = null;
      Dimension s = null;
      for(int i = 0 ; i < count; i++) {
        c = target.getComponent(i);
        s = c.getMinimumSize();
        d.width += s.width;
        d.height = Math.max(d.height, insets.top + insets.bottom + s.height);
      }
      return d;
    }
  }

  public Dimension preferredLayoutSize(Container target) {
    synchronized (target.getTreeLock()) {
      Insets insets = target.getInsets();
      int count = target.getComponentCount();
      Dimension s = target.getSize();
      Component c = null;
      Dimension t = new Dimension(0 , 0);
      Dimension z = null;
      Dimension e = expandComponent != null ? expandComponent.getPreferredSize() : null;
      int rowHeight = -1;
      int x = insets.left;
      int y = insets.top;
      int width = insets.left;
      int height = insets.top + insets.bottom;
      for(int i = 0 ; i < count; i++) {
        c = target.getComponent(i);
        if(c != expandComponent) {
          z = c.getPreferredSize();
          rowHeight = Math.max(rowHeight, z.height);
          if( z.width + x >= ( ( s.width - insets.left ) - ( e != null ? e.width : 0 ) ) ) {
            overunIndex = i;
            if(wrap) {
              y += rowHeight;
              x = insets.left;
              height = Math.max(height, y + rowHeight + insets.bottom);
              rowHeight = 0;
            }
            else {            
              height = Math.max(height, y + rowHeight + insets.bottom);
            }
          }
          else {
            height = Math.max(height, y + rowHeight + insets.bottom);
          }
          x += z.width;  
          width = Math.max(width, x);
        }
      }
      width += insets.right;      
      return new Dimension(width, height);
    }
  }

  /**
   * @param wrap wrap
   */
  public void setWrap(boolean wrap) {
    if(!this.wrap == wrap) {
      this.wrap = wrap;
      if(wrap) {
        overunIndex = -1;
      }
    }
  }

  public boolean isWrap() {
    return wrap;
  }

  public int getOverunIndex() {
    return overunIndex;
  }
}