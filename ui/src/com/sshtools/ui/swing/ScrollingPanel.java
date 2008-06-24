package com.sshtools.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.plaf.basic.BasicArrowButton;

public class ScrollingPanel extends JPanel implements ActionListener {
  
  protected JButton north;
  protected JButton south;
  protected JViewport viewport;
  protected int incr = 48;
  
  public ScrollingPanel(Component component) {
    setLayout(new BorderLayout());
    north = new BasicArrowButton(BasicArrowButton.NORTH);
    south = new BasicArrowButton(BasicArrowButton.SOUTH);
    viewport = new JViewport();
    add(north, BorderLayout.NORTH);
    add(viewport, BorderLayout.CENTER);
    add(south, BorderLayout.SOUTH);
    viewport.setView(component);
    north.addActionListener(this);
    south.addActionListener(this);
    setAvailableActions();
  }

  public void setIncrement(int incr) {
    this.incr = incr;
  }

  public void actionPerformed(ActionEvent event) {
    Dimension view = new Dimension(getSize().width, getSize().height - north.getPreferredSize().height - south.getPreferredSize().height);
    Dimension pane = viewport.getView().getPreferredSize();
    Point top = viewport.getViewPosition();
    if (event.getSource() == north) {
      if (top.y < incr) {
        viewport.setViewPosition(new Point(0, 0));
      } else {
        viewport.setViewPosition(new Point(0, top.y - incr));
      }
    }
    if (event.getSource() == south) {
      int max = pane.height - view.height;
      if (top.y > (max - incr)) {
        view = viewport.getExtentSize();
        max = Math.max(pane.height - view.height, 0);
        viewport.setViewPosition(new Point(0, max));
      } else {
        viewport.setViewPosition(new Point(0, top.y + incr));
      }
    }
    setAvailableActions();
  }

  public void setAvailableActions() {
    Dimension view = getSize();
    Dimension pane = viewport.getView().getPreferredSize();
    Point top = viewport.getViewPosition();
    //north.setEnabled(top.y > 0);
    //south.setEnabled((top.y + view.height) < pane.height);
  }
}
