package com.sshtools.ui.awt.grid;

public interface TableModel {
  public int getRowCount();
  public int getColumnCount();
  public String getColumnName(int c);
  public Class getColumnClass(int r);
  public Object getValue(int r, int c);
  public void addTableModelListener(TableModelListener l);
  public void removeTableModelListener(TableModelListener l);
}