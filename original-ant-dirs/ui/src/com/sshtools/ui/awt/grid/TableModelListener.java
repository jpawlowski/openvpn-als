/*
 */
package com.sshtools.ui.awt.grid;

public interface TableModelListener {  
  public void layoutChanged();
  public void changed();
  public void rowInserted(int row);
  public void rowDeleted(int row);
  public void rowChanged(int row);
}
