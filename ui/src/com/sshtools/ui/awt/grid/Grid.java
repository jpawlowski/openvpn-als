/*
 * Copyrights and Licenses
 * 
 * This product includes Hypersonic SQL. Originally developed by Thomas Mueller
 * and the Hypersonic SQL Group.
 * 
 * Copyright (c) 1995-2000 by the Hypersonic SQL Group. All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - All advertising materials mentioning features or use
 * of this software must display the following acknowledgment: "This product
 * includes Hypersonic SQL." - Products derived from this software may not be
 * called "Hypersonic SQL" nor may "Hypersonic SQL" appear in their names
 * without prior written permission of the Hypersonic SQL Group. -
 * Redistributions of any form whatsoever must retain the following
 * acknowledgment: "This product includes Hypersonic SQL." This software is
 * provided "as is" and any expressed or implied warranties, including, but not
 * limited to, the implied warranties of merchantability and fitness for a
 * particular purpose are disclaimed. In no event shall the Hypersonic SQL Group
 * or its contributors be liable for any direct, indirect, incidental, special,
 * exemplary, or consequential damages (including, but not limited to,
 * procurement of substitute goods or services; loss of use, data, or profits;
 * or business interruption). However caused any on any theory of liability,
 * whether in contract, strict liability, or tort (including negligence or
 * otherwise) arising in any way out of the use of this software, even if
 * advised of the possibility of such damage. This software consists of
 * voluntary contributions made by many individuals on behalf of the Hypersonic
 * SQL Group.
 * 
 * 
 * For work added by the HSQL Development Group:
 * 
 * Copyright (c) 2001-2002, The HSQL Development Group All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer, including earlier license
 * statements (above) and comply with all above license conditions.
 * 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution, including earlier
 * license statements (above) and comply with all above license conditions.
 * 
 * Neither the name of the HSQL Development Group nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL HSQL DEVELOPMENT GROUP, HSQLDB.ORG, OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * Work added by 3SP <a href="http://3sp.com">http://3sp.com</a> for 
 * AWT UI components are licensed under the GPL.
 * LICENSE.txt in the root of this module. 
 */
package com.sshtools.ui.awt.grid;

import java.awt.AWTEventMulticaster;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.Shape;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.sshtools.ui.awt.ImageTextLabel;
import com.sshtools.ui.awt.UIUtil;

//sqlbob@users 20020401 - patch 1.7.0 by sqlbob (RMP) - enhancements
/**
 */
public class Grid extends Panel implements TableModelListener, AdjustmentListener {
  
  public final static int AUTO_RESIZE_OFF = 0;
  public final static int AUTO_RESIZE_EVENT = 1;
  public final static int AUTO_RESIZE_FIRST_COLUMN = 2;
  public final static int AUTO_RESIZE_LAST_COLUMN = 3;
  
  // drawing
  //private Dimension dMinimum;
  private Dimension dPreferred;

  //boucherb@users changed access for databasemanager2
  protected Font fFont;

  //--------------------------------------------------
  private FontMetrics fMetrics;

  private Graphics gImage;

  private Image iImage;

  // height / width
  private int iWidth, iHeight;

  private int iRowHeight, iFirstRow;

  private int iGridWidth, iGridHeight;

  private int iX, iY;

  // data
  //boucherb@users changed access for databasemanager2
  //protected String[] sColHead = new String[0];
  //protected Vector vData = new Vector();
  //--------------------------------------------------
  private int iColWidth[];

  //boucherb@users changed access for databasemanager2
  //--------------------------------------------------
  // scrolling
  private Scrollbar sbHoriz, sbVert;

  private int iSbWidth, iSbHeight;

  private boolean bDrag;

  private int iXDrag, iColDrag;

  private TableModel model;
  
  private boolean[][] sel;
  
  private int lastDragSel, lastShiftSel;
  
  private boolean clearNextDrag;
  
  private Color selectionBackground, selectionForeground;
  
  private TableCellRenderer cellRenderer;
  
  private Hashtable renderers, numberedRenderers;
  
  private int autoResizeMode = 0;
  
  private boolean showGrid;
  
  private ActionListener listener;

  /**
   * Constructor declaration
   *  
   */
  public Grid() {
    super();
    showGrid = false;
    renderers = new Hashtable();
    numberedRenderers = new Hashtable();
    fFont = new Font("Dialog", Font.PLAIN, 12); //$NON-NLS-1$
    selectionBackground = Color.blue.darker();
    selectionForeground = Color.white;
    setBackground(SystemColor.text);
    setForeground(SystemColor.textText);
    cellRenderer = new DefaultTableCellRenderer();
    setLayout(null);
    sbHoriz = new Scrollbar(Scrollbar.HORIZONTAL);
    sbHoriz.addAdjustmentListener(this);
    add(sbHoriz);
    sbVert = new Scrollbar(Scrollbar.VERTICAL);
    sbVert.addAdjustmentListener(this);
    dPreferred = new Dimension(480,320);
    add(sbVert);
    addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        requestFocus();
        int y = e.getY();
        int x = e.getX();
        //  get row
        if(y > iRowHeight) {
          int row = rowForY(y);
          if(row < model.getRowCount()) {
            int col = columnForX(x);
            boolean ctrl =  ( e.getModifiers() & MouseEvent.CTRL_MASK ) != 0; 
            boolean shift =  ( e.getModifiers() & MouseEvent.SHIFT_MASK ) != 0;
            
            if(!ctrl) {
              clearSel();
            }
            
            //  select the range if this is a shift selection. 
            if(shift) {
              if(lastShiftSel != -1) {
                int incr = lastShiftSel < row ? 1 : -1;
                for(int j = lastShiftSel; j != ( row + incr) ; j += incr ) {
                  selRow(j, true);
                }
              }
              else {
                selRow(row, true);
                lastShiftSel = row;
              }
            }
            else {          
            //  select entire row
              boolean doSel = ctrl ? ( !sel[row][col] ) : true; 
              selRow(row, doSel);
              lastShiftSel = doSel ? row : -1;
            }
            
            //  repaint
            repaint();
            
            //
            lastDragSel = -1;
           }
        }
      }
      
      public void mouseReleased(MouseEvent evt) {
        if(lastDragSel != -1) {
          clearNextDrag = true;
        }
      }

      public void mouseExited(MouseEvent e) {
        if (bDrag) {
          setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
          bDrag = false;
        }
      }
    });
    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (bDrag && x < iWidth) {
          int w = x - iXDrag;
          if (w < 0) {
            w = 0;
          }
          iColWidth[iColDrag] = w;
          adjustScroll();
          repaint();
        }
        else {
          //  Drag selection
          if(y > iRowHeight) {
            if(clearNextDrag) {
              clearSel();
              clearNextDrag = false;
            }
            int row = rowForY(y);
            if(row < model.getRowCount()) {
              if(lastDragSel != -1) {
                //  TODO allow up drag selection without clearing previous selection 
                if(row < lastDragSel) {
                  for(int j = row + 1; j <= lastDragSel; j++) {
                    selRow(j, false);                  
                  }
                  selRow(row, true);                
                }
                else {
                  for(int i = lastDragSel; i <= row; i++) {
                    selRow(i, true);                
                  }                
                }
              }
              else {
                selRow(row, true);
              }
              repaint();
              lastDragSel = row;
             }
          }
        }
      }

      public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if (y <= iRowHeight) {
          int xb = x;
          x += iX - iGridWidth;
          int i = model.getColumnCount() - 1;
          for (; i >= 0; i--) {
            if (x > -7 && x < 7) {
              break;
            }
            x += iColWidth[i];
          }
          if (i >= 0) {
            if (!bDrag) {
              setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
              bDrag = true;
              iXDrag = xb - iColWidth[i];
              iColDrag = i;
            }
            return;
          }
        }
        if (bDrag) {
          setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
          bDrag = false;
        }
      }
      
    });
  }
  
  public void addActionListener(ActionListener l) {
    listener = AWTEventMulticaster.add(listener, l);
  }
  
  public void removeActionListener(ActionListener l) {
    listener = AWTEventMulticaster.remove(listener, l);
  }
  
  private void selRow(int r, boolean selected) {    
    //  select entire row
    boolean s[] = sel[r];
    for(int i = 0 ; i < s.length; i++) {
      s[i] = selected;
    } 
    if(listener != null) {
      listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "selection")); //$NON-NLS-1$
    }
  }
  
  private int columnForX(int x) {
    int w = 0;
    for(int i = 0 ; i < iColWidth.length; i++) {
      if(x < w)
        return i - 1;
      else
        w += iColWidth[i];
    }
    return -1;
  }
  
  private int rowForY(int y) {
    return ( y - iRowHeight + iY ) / iRowHeight;
  }
  
  protected TableCellRenderer getRendererForColumn(int col) {
    Integer i = new Integer(col);
    TableCellRenderer r = (TableCellRenderer)numberedRenderers.get(i);
    if(r == null) {
      r = (TableCellRenderer)renderers.get(model.getColumnClass(col));
      if(r == null) {
        r = cellRenderer;
      }
    }
    return r;    
  }

  /**
   * Return the first selected row, or -1 if nothing is selected
   * 
   * @return selected row
   */
  public int getSelectedRow() {
    //  TODO this badly needs a more efficient method 
    for(int i = 0 ; i < sel.length; i++) {
      for(int j = 0 ; j < sel[i].length; j++) {
        if(sel[i][j]) {
          return i;
        }
      }
    }
    return -1;
  }
  
  /**
   * Clear the selection
   * 
   * @param model
   */
  public void clearSelection() {
    clearSel();
    repaint();
  }
  
  /**
   * Set the selection background colour
   * 
   * @param selectionBackground selection background colour
   */
  public void setSelectionBackground(Color selectionBackground) {
    this.selectionBackground = selectionBackground;
    repaint();
  }
  
  /**
   * Get the selection background colour
   * 
   * @return selection background colour
   */
  public Color getSelectionBackground() {
    return selectionBackground;
  }
  
  /**
   * Set the selection foreground colour
   * 
   * @param selectionForeground selection foreground colour
   */
  public void setSelectionForeground(Color selectionForeground) {
    this.selectionForeground = selectionForeground;
    repaint();
  }
  
  /**
   * Get the selection foreground colour
   * 
   * @return selection foreground colour
   */
  public Color getSelectionForeground() {
    return selectionForeground;
  }
  
  /**
   * Set the cell renderer for a given column class
   * 
   * @param columnClass class to set renderer for
   * @param renderer renderer to use
   */
  public void setCellRenderer(Class columnClass, TableCellRenderer renderer) {
    renderers.put(columnClass, renderer);
  }
  
  /**
   * Set the cell renderer for a given number
   * 
   * @param column column number
   * @param renderer renderer to use
   */
  public void setCellRenderer(int column, TableCellRenderer renderer) {
    numberedRenderers.put(new Integer(column), renderer);
  }
  
  private void clearSel() {
    sel = new boolean[model.getRowCount()][model.getColumnCount()];
  }

  /**
   * Constructor declaration
   * 
   * @param model model
   *  
   */
  public Grid(TableModel model) {
    this();
    setModel(model);
  }

  /**
   * Set model
   * 
   * @param model model
   */
  public synchronized void setModel(TableModel model) {
    if(this.model != null) {
      this.model.removeTableModelListener(this);
    }
    this.model = model;
    resetGrid();
    model.addTableModelListener(this);
  }
  
  private void resetGrid() {
    iColWidth = new int[model == null ? 0 : model.getColumnCount()];
    for (int i = 0; i < iColWidth.length; i++) {
      iColWidth[i] = 100;
    }
    iRowHeight = 0;
    changed();
  }
  
  public synchronized void setColumnWidths(int[] colWidth) {
    this.iColWidth = colWidth;
    adjustScroll();
    repaint();
  }

  public static void main(String[] args) {
    Frame frame = new Frame("Test table"); //$NON-NLS-1$
    TableModel model = new TestTableModel(1000);
    Grid table = new Grid(model);
    table.setCellRenderer(Image.class, new TestImageTableCellRenderer());
    frame.setLayout(new BorderLayout());
    frame.add(table, BorderLayout.CENTER);
    frame.setSize(new Dimension(800, 600));
    UIUtil.positionComponent(UIUtil.CENTER, frame);
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        System.exit(0);
      }
    });
    frame.setVisible(true);
  }

  /**
   * Method declaration
   * 
   * @param d
   */
//  public void setMinimumSize(Dimension d) {
//    dMinimum = d;
//  }

  /**
   * Method declaration
   * 
   * @param x
   * @param y
   * @param w
   * @param h
   */
  public void setBounds(int x, int y, int w, int h) {
    // fredt@users 20011210 - patch 450412 by elise@users
    super.setBounds(x, y, w, h);
    iSbHeight = sbHoriz.getPreferredSize().height;
    iSbWidth = sbVert.getPreferredSize().width;
    iHeight = h - iSbHeight;
    iWidth = w - iSbWidth;
    sbHoriz.setBounds(0, iHeight, iWidth, iSbHeight);
    sbVert.setBounds(iWidth, 0, iSbWidth, iHeight);
    adjustScroll();
    iImage = null;
    repaint();
  }

  /**
   * Method declaration
   *  
   */
  public void update() {
    adjustScroll();
    repaint();
  }

  /**
   * Method declaration
   *  
   */
  void adjustScroll() {
    if (iRowHeight == 0) { return; }
    int w = 0;
    for (int i = 0; i < model.getColumnCount(); i++) {
      w += iColWidth[i];
    }
    iGridWidth = w;
    iGridHeight = iRowHeight * (model.getRowCount() + 1);
    sbHoriz.setValues(iX, iWidth, 0, iGridWidth);
    int v = iY / iRowHeight, h = iHeight / iRowHeight;
    sbVert.setValues(v, h, 0, model.getRowCount() + 1);
    iX = sbHoriz.getValue();
    iY = iRowHeight * sbVert.getValue();
  }

  /**
   * Method declaration
   * 
   * @param g
   */
  public void paint(Graphics g) {
    if (g == null) { return; }
    if (model == null) {
      super.paint(g);
      return;
    }
    int cc = model.getColumnCount();
    int rc = model.getRowCount();
    if (iWidth <= 0 || iHeight <= 0) { return; }
    g.setColor(SystemColor.control);
    g.fillRect(iWidth, iHeight, iSbWidth, iSbHeight);
    if (iImage == null) {
      iImage = createImage(iWidth, iHeight);
      gImage = iImage.getGraphics();
      gImage.setFont(fFont);
      if (fMetrics == null) {
        fMetrics = gImage.getFontMetrics();
      }
    }
    if (iRowHeight == 0) {
      iRowHeight = fMetrics.getHeight() + 4;
      adjustScroll();
    }
    gImage.setColor(Color.white);
    gImage.fillRect(0, 0, iWidth, iHeight);
    gImage.setColor(Color.darkGray);
    gImage.drawLine(0, iRowHeight, iWidth, iRowHeight);
    int x = -iX;
    for (int i = 0; i < cc; i++) {
      int w = iColWidth[i];
      gImage.setColor(SystemColor.control);
      gImage.fillRect(x + 1, 0, w - 2, iRowHeight);
      gImage.setColor(Color.black);
      gImage.drawString(model.getColumnName(i), x + 2, iRowHeight - 5);
      gImage.setColor(Color.darkGray);
      gImage.drawLine(x + w - 1, 0, x + w - 1, iRowHeight - 1);
      gImage.setColor(Color.white);
      gImage.drawLine(x + w, 0, x + w, iRowHeight - 1);
      x += w;
    }
    gImage.setColor(SystemColor.control);
    gImage.fillRect(0, 0, 1, iRowHeight);
    gImage.fillRect(x + 1, 0, iWidth - x, iRowHeight);
    gImage.drawLine(0, 0, 0, iRowHeight - 1);
    int y = iRowHeight + 1 - iY;
    int j = 0;
    while (y < iRowHeight + 1) {
      j++;
      y += iRowHeight;
    }
    iFirstRow = j;
    y = iRowHeight + 1;
    boolean s;    
    Component component;
    TableCellRenderer renderer;
    for (; y < iHeight && j < rc; j++, y += iRowHeight) {
      x = -iX;
      for (int i = 0; i < cc; i++) {
        int w = iColWidth[i];
        s = j < sel.length && i < sel[j].length ? sel[j][i] : false;
        Color b = s ? selectionBackground : getBackground(), t = s ? selectionForeground : getForeground();
        gImage.setColor(b);
        gImage.fillRect(x, y, w - 1, iRowHeight - 1);
        gImage.setColor(t);
        
        renderer = getRendererForColumn(i);
        
        component = renderer.getTableCellRendererComponent(this, model.getValue(j, i), j, i, s);
        //  TODO Currently, ordinary components cannot be used as a renderer - something to do with the peer not be created 
        component.setBounds(0, 0, w - 1, iRowHeight - 1);
        gImage.translate(x , y);
        Shape clip = gImage.getClip();
        gImage.setClip(0, 0, w -1 , iRowHeight - 1);
        component.paint(gImage);
        gImage.translate(-x , -y);
        gImage.setClip(clip);        
        
        //gImage.drawString(getDisplay(i, j), x + 2, y + iRowHeight - 5);
        if(showGrid) {
	        gImage.setColor(Color.lightGray);
	        gImage.drawLine(x + w - 1, y, x + w - 1, y + iRowHeight - 1);
	        gImage.drawLine(x, y + iRowHeight - 1, x + w - 1, y + iRowHeight - 1);
        }
        x += w;
      }
      gImage.setColor(getBackground());
      gImage.fillRect(x, y, iWidth - x, iRowHeight - 1);
    }
    g.drawImage(iImage, 0, 0, this);
  }

  /**
   * Method declaration
   * 
   * @param g
   */
  public void update(Graphics g) {
    paint(g);
  }   

  /**
   * Method declaration
   * 
   * @return
   */
  public Dimension preferredSize() {
    return dPreferred == null ? super.preferredSize() : dPreferred;
  }

  /**
   * Method declaration
   * 
   * @return
   */
  public Dimension getPreferredSize() {
    return dPreferred == null ? super.getPreferredSize() : dPreferred;
  }

  static class TestTableModel implements TableModel {
    
    Image image;
    int rows;
    
    TestTableModel(int rows) {
      this.rows = rows;
      image = UIUtil.loadImage(TestTableModel.class, "test-image.png"); //$NON-NLS-1$
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.sshtools.ui.awt.TableModel#getRowCount()
     */
    public int getRowCount() {
      return rows;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sshtools.ui.awt.TableModel#getColumnCount()
     */
    public int getColumnCount() {
      return 6;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sshtools.ui.awt.TableModel#getValue(int, int)
     */
    public Object getValue(int r, int c) {
      if(c == 0) {
        return image;
      }
      else {
        return r + "," + c; //$NON-NLS-1$
      }      
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sshtools.ui.awt.TableModel#getColumnName(int)
     */
    public String getColumnName(int c) {
      if(c ==0) {
        return Messages.getString("Grid.image");       //$NON-NLS-1$
      }
      else {
        return String.valueOf(c);        
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sshtools.ui.awt.TableModel#getColumnClass(int)
     */
    public Class getColumnClass(int r) {
      if(r == 0) {
        return Image.class;
      }
      else {
        return String.class;
      }
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.awt.grid.TableModel#addTableModelListener(com.sshtools.ui.awt.grid.TableModelListener)
     */
    public void addTableModelListener(TableModelListener l) {
      // TODO Auto-generated method stub
      
    }

    /* (non-Javadoc)
     * @see com.sshtools.ui.awt.grid.TableModel#removeTableModelListener(com.sshtools.ui.awt.grid.TableModelListener)
     */
    public void removeTableModelListener(TableModelListener l) {
      // TODO Auto-generated method stub
      
    }
  }
  
  class DefaultTableCellRenderer extends ImageTextLabel implements TableCellRenderer {
      

      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); //$NON-NLS-1$
      
    public Component getTableCellRendererComponent(Grid grid, Object value, int row, int col, boolean sel) {
      setBackground(sel ? grid.getSelectionBackground() : grid.getBackground());
      setForeground(sel ? grid.getSelectionForeground() : grid.getForeground());
      setFont(grid.getFont());
      if(value instanceof Integer || value instanceof Double || 
                      value instanceof Long || value instanceof Float ||
                      value instanceof Short || value instanceof Byte) {
          setHorizontalAlignment(RIGHT_ALIGNMENT);
          setText(String.valueOf(value));
      }
      else if(value instanceof Date) {
          setHorizontalAlignment(LEFT_ALIGNMENT);
          setText(sdf.format((Date)value));
      }
      else {
          setHorizontalAlignment(LEFT_ALIGNMENT);
          setText(String.valueOf(value));          
      }
      return this;
    }
  }
  
  static class TestImageTableCellRenderer extends ImageTextLabel implements TableCellRenderer {
    public Component getTableCellRendererComponent(Grid grid, Object value, int row, int col, boolean sel) {
      setBackground(sel ? grid.getSelectionBackground() : grid.getBackground());
      setForeground(sel ? grid.getSelectionForeground() : grid.getForeground());
      setFont(grid.getFont());
      setImage((Image)value);
      return this;
    }
  }

  /* (non-Javadoc)
   * @see com.sshtools.ui.awt.grid.TableModelListener#layoutChanged()
   */
  public void layoutChanged() {
    resetGrid();
  }

  /* (non-Javadoc)
   * @see com.sshtools.ui.awt.grid.TableModelListener#changed()
   */
  public void changed() {
    sel = new boolean[model == null ? 0 : model.getRowCount()][model == null ? 0 : model.getColumnCount()];
    rowsChanged();
  }

  /* (non-Javadoc)
   * @see com.sshtools.ui.awt.grid.TableModelListener#rowInserted(int)
   */
  public void rowInserted(int row) {
    rowsChanged();    
  }

  /* (non-Javadoc)
   * @see com.sshtools.ui.awt.grid.TableModelListener#rowDeleted(int)
   */
  public void rowDeleted(int row) {
    rowsChanged();        
  }

  /* (non-Javadoc)
   * @see com.sshtools.ui.awt.grid.TableModelListener#rowChanged(int)
   */
  public void rowChanged(int row) {
    rowsChanged();            
  }
  
  private void rowsChanged() {
      lastDragSel = -1;
      clearNextDrag = true;
      iImage = null;
      adjustScroll();
      repaint();   
  }

  /* (non-Javadoc)
   * @see java.awt.event.AdjustmentListener#adjustmentValueChanged(java.awt.event.AdjustmentEvent)
   */
  public void adjustmentValueChanged(AdjustmentEvent e) {
    iX = sbHoriz.getValue();
    iY = iRowHeight * sbVert.getValue();
    repaint();
  }

  /**
   * @return
   */
  public int[] getSelectedRows() {
    Vector v = new Vector();
    for(int i = 0 ; i < sel.length; i++) {
      for(int j = 0 ; j < sel[i].length; j++) {
        if(sel[i][j]) {
          v.addElement(new Integer(i));
          break;
        }
      }
    }
    int[] sel = new int[v.size()];
    int idx = 0;
    for(int i = v.size() - 1; i >= 0 ; i--) {
      sel[i] = ((Integer)v.elementAt(i)).intValue();
    }
    return sel;
  }

  public void setScrollPosition(int i) {
    sbVert.setValue(i);
    adjustmentValueChanged(null);
  }

  public int getScrollPosition() {
    return sbVert.getValue();
  }
  
  public int getScrollBlockIncrement() {
    return sbVert.getBlockIncrement();
  }
  
  public int getScrollUnitIncrement() {
    return sbVert.getUnitIncrement();
  }

  /**
   * @return
   */
  public int getSelectedRowCount() {
    return getSelectedRows().length;
  }

  /**
   * @param b
   */
  public void setShowGrid(boolean showGrid) {
    this.showGrid = showGrid;
    repaint();    
  }
}