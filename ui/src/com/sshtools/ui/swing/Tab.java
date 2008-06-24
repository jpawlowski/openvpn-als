/*
 *  SSHTools - Java SSH2 API
 *
 *  Copyright (C) 2002 Lee David Painter.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *
 *  You may also distribute it and/or modify it under the terms of the
 *  Apache style J2SSH Software License. A copy of which should have
 *  been provided with the distribution.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  License document supplied with your distribution for more details.
 *
 */

package com.sshtools.ui.swing;

import java.awt.Component;

import javax.swing.Icon;

/**
 *
 *
 * @author $author$
 * @version $Revision: 1.1.6.1 $
 */

public interface Tab {

  /**
   *
   *
   * @return
   */

  public String getTabCategory();

  /**
   *
   *
   * @return
   */

  public Icon getTabIcon();

  /**
   * @return
   */
  public Icon getTabLargeIcon();


  /**
   *
   *
   * @return
   */

  public String getTabTitle();

  /**
   *
   *
   * @return
   */

  public String getTabToolTipText();

  /**
   *
   *
   * @return
   */

  public int getTabMnemonic();

  /**
   *
   *
   * @return
   */

  public Component getTabComponent();

  /**
   *
   *
   * @return
   */

  public boolean validateTab();

  /**
   *
   */

  public void applyTab();

  /**
   *
   */

  public void tabSelected();
}
