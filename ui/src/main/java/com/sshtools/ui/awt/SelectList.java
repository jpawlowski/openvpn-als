package com.sshtools.ui.awt;

import java.awt.List;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class SelectList extends List implements MouseListener {

  public SelectList() {
    init(750);
  }

  public SelectList(int items) {
    this(items, 750);
  }

  public SelectList(int items, int waitInterval) {
    super(items);
    init(waitInterval);
  }

  private void init(int waitInterval) {
    this.waitInterval = waitInterval;
    addMouseListener(this);
  }

  public abstract void selected();

  public void mouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2)
      selected();
  }

  public void mousePressed(MouseEvent e) {
    if (timerThread == null) {
      released = false;
      timerThread = new Thread() {

        public void run() {
          try {
            Thread.sleep(waitInterval);
          } catch (InterruptedException interruptedexception) {
          }
          if (!released)
            selected();
          timerThread = null;
        }

      };
      timerThread.start();
    }
  }

  public void mouseReleased(MouseEvent e) {
    released = true;
  }

  public void mouseEntered(MouseEvent mouseevent) {
  }

  public void mouseExited(MouseEvent mouseevent) {
  }

  private Thread timerThread;
  private int waitInterval;
  private boolean released;

}