package com.sshtools.ui.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

public class ActionToolBar extends JToolBar {
  private ExpandToolBarToggleButton button;

  private int buttonIndex;

  /**
   *  
   */
  public ActionToolBar() {
    super();
    init();
  }

  /**
   * @param orientation
   */
  public ActionToolBar(int orientation) {
    super(orientation);
    init();
  }

  /**
   * @param name
   */
  public ActionToolBar(String name) {
    super(name);
    init();
  }

  /**
   * @param name
   * @param orientation
   */
  public ActionToolBar(String name, int orientation) {
    super(name, orientation);
    init();
  }

  public void setWrap(boolean wrap) {
    boolean oldWrap = isWrap();
    if (wrap != oldWrap) {
      ((ActionToolBarLayout) getLayout()).setWrap(wrap);
      checkButton();
    }
  }

  public boolean isWrap() {
    return getLayout() instanceof ActionToolBarLayout && ((ActionToolBarLayout) getLayout()).isWrap();
  }

  private void init() {
    ActionToolBarLayout layout = new ActionToolBarLayout();
    setLayout(layout);
    checkButton();
    setWrap(false);
  }

  private void checkButton() {
    invalidate();
    if(getLayout() instanceof ActionToolBarLayout) { // Hack to cope with L&F changes
      if (button != null) {
        remove(button);
        ((ActionToolBarLayout) getLayout()).setExpandComponent(null);
        buttonIndex = -1;
      }
      if (!isWrap()) {
        button = new ExpandToolBarToggleButton();
        button.setFocusPainted(false);
        ((ActionToolBarLayout) getLayout()).setExpandComponent(button);
        add(button);
        buttonIndex = getComponentIndex(button);
      }
    }
    validate();
    repaint();
  }
  
  class ExpandToolBarToggleButton extends JButton implements ActionListener {
    JPopupMenu popup;

    ExpandToolBarToggleButton() {
      super(new ArrowIcon(SwingConstants.SOUTH));
      popup = new JPopupMenu();
      addActionListener(this);
      addMouseListener(new MouseAdapter() {

        public void mouseEntered(MouseEvent e) {
            if(isEnabled()) {
                setBorderPainted(true);
                setContentAreaFilled(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            setBorderPainted(false);
            setContentAreaFilled(false);
        }
    });
    setBorderPainted(false);
    setContentAreaFilled(false);
    }

    public Insets getMargin() {
        return new Insets(0, 0, 0, 0);
    }

    public boolean isRequestFocusEnabled() {
        return false;
    }

    public boolean isFocusTraversable() {
        return false;
    }

    public void actionPerformed(ActionEvent evt) {
      int overun = ((ActionToolBarLayout) ActionToolBar.this.getLayout())
          .getOverunIndex();
      if (overun != -1) {
        popup.invalidate();
        popup.removeAll();
        int count = ActionToolBar.this.getComponentCount();
        Component c;
        for (int i = overun; i < count; i++) {
          c = ActionToolBar.this.getComponent(i);
          if (c != this) {
            if (c instanceof ToolBarSeparator) {
              popup.addSeparator();
            } else {
              AbstractButton button = (AbstractButton) c;
              Action appAction = (Action) button.getAction();
              if (appAction != null) {
                popup.add(appAction);
              }
            }
          }
        }
        popup.show(this, getSize().width - popup.getPreferredSize().width,
            getSize().height);
      }
    }
  }
  
  public Dimension getMinimumSize() {
      return new Dimension(1, super.getMinimumSize().height);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.Container#remove(java.awt.Component)
   */
  public void remove(Component comp) {
    if (comp != button) {
      super.remove(comp);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.Container#remove(int)
   */
  public void remove(int index) {
    if (index != buttonIndex) {
      super.remove(index);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.awt.Container#removeAll()
   */
  public void removeAll() {
    super.removeAll();
    checkButton();
  }
}