/*
 */
package com.sshtools.ui.awt.grid;

import java.awt.Component;


public interface TableCellRenderer {
  public Component getTableCellRendererComponent(Grid grid, Object value, int row, int col, boolean sel);
}