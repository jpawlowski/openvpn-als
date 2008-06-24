
				/*
 *  Adito
 *
 *  Copyright (C) 2003-2006 3SP LTD. All Rights Reserved
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
			

package com.sshtools.ui.swing;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Extension of Swing {@link DefaultTableCellRenderer}that renders <code>Boolean</code>
 * values as one of two icons.
 *
 * @author $Author: brett $
 */

public class BooleanIconRenderer
    extends DefaultTableCellRenderer {

  //  Private instance variables

  private Icon trueIcon;

  private Icon falseIcon;

  /**
   * Construct a new BooleanIconRenderer.
   *
   * @param trueIcon icon for <code>Boolean.TRUE</code> values
   * @param falseIcon icons for <code>Boolean.FALSE</code> vlaues
   */

  public BooleanIconRenderer(Icon trueIcon, Icon falseIcon) {

    this.trueIcon = trueIcon;

    this.falseIcon = falseIcon;

    setHorizontalAlignment(JLabel.CENTER);

  }

  /*
   * (non-Javadoc)
   *
   * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
   *      java.lang.Object, boolean, boolean, int, int)
   */

  public Component getTableCellRendererComponent(JTable table, Object value,

                                                 boolean isSelected,
                                                 boolean hasFocus, int row,
                                                 int column) {

    super.getTableCellRendererComponent(table, value, isSelected, hasFocus,

                                        row, column);

    setText(null);

    setIcon( ( (Boolean) value).booleanValue() ? trueIcon : falseIcon);

    return this;

  }

  public String getText() {

    return null;

  }

}
