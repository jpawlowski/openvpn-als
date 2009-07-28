/*
 */
package com.sshtools.ui.swing;

import java.awt.Component;

/**
 * @author magicthize
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface Tabber {
  
  public int getTabCount();
  
  /**
   *
   *
   * @param i
   *
   * @return
   */
  public abstract Tab getTabAt(int i);

  /**
   *
   *
   * @return
   */
  public abstract boolean validateTabs();

  /**
   *
   */
  public abstract void applyTabs();

  /**
   *
   *
   * @param tab
   */
  public abstract void addTab(Tab tab);

  /**
   *
   *
   * @param tab
   */
  public abstract Component getComponent();

  /**
   * 
   */
  public abstract void removeAllTabs();
}