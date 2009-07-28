/*
 */
package com.sshtools.ui.awt.tooltips;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;

import com.sshtools.ui.awt.ImageTextLabel;


class TipWindow extends Window {
  private ImageTextLabel textLabel;
  private long lastShow;
  private boolean dismissed;
  private Component component;
  private String text;

  TipWindow(Frame owner) {
    super(owner);
    textLabel = new ImageTextLabel() {
      public void paint(Graphics g) {
        super.paint(g);
        g.setColor(getForeground());
        Dimension s = getSize();
        g.drawRect(0, 0, s.width -1 , s.height -1);
      }
    };
    textLabel.setMargin(new Insets(2, 2, 2, 2));
    setLayout(new GridLayout(1, 1));
    add(textLabel);
  }
  
  boolean isDismissed() {
    return dismissed;
  }

  boolean isOutOfDate() {
    return (System.currentTimeMillis() > (lastShow + 5000));
  }
  
  synchronized void dismiss() {
    dismissed = true;
    hide();
  }

  synchronized void popup(int x, int y, Component component, String text) {
     
    invalidate();
    textLabel.setText(text);
    textLabel.setForeground(ToolTipManager.getInstance().foreground);
    textLabel.setBackground(ToolTipManager.getInstance().background);
    validate();
    pack();
    try {
        if(x != -1 && y != -1) {
		    setLocation(x + 8, y + 8);
        }
        else {
		    Point p = component.getLocationOnScreen();
		    Dimension s = component.getSize();
		    setLocation(p.x + 8, p.y + s.height  + 8);
        }
	    setVisible(true);
	    toFront();
	    lastShow = System.currentTimeMillis();
	    dismissed = false;      
    }
    catch(IllegalComponentStateException icse) {
      
    }
  }
}