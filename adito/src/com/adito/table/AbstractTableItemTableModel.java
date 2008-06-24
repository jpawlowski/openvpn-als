
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
			
package com.adito.table;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation of a {@link com.adito.table.TableModel} that 
 * maintains a list of {@link com.adito.table.TableItem} objects as
 * its datasource. 
 * 
 * @param <T> 
 */
public abstract class AbstractTableItemTableModel<T extends TableItem> implements TableItemModel<T> {
    
    protected List<T> items;

    /**
     */
    public AbstractTableItemTableModel() {
        super();
        items = new ArrayList<T>();
    }
    
    /**
     * @param col
     * @return int
     */
    public abstract int getColumnWidth(int col);
    
    /**
     * @return String[]
     */
    public String[] getColumnNames() {
        String[] cols = new String[getColumnCount()];
        for(int i = getColumnCount() - 1; i >= 0 ; i--) {
            cols[i] = getColumnName(i);
        }
        return cols;
    }
    
    public List<T> getItems() {
        // defensive copy
        return new ArrayList<T>(items);
    }
    
    public void addItem(T item) {
        items.add(item);
    }

    public void clear() {
        items.clear();        
    }

    /**
     * @param item
     */
    public void removeItem(T item) {
        items.remove(item);
    }

    public int getRowCount() {
        return items.size();
    }

    public Object getValue(int row, int col) {
        return getItem(row).getColumnValue(col);
    }

    public T getItem(int row) {
        return items.get(row);
    }
    
    public boolean contains(T item) {
        return items.contains(item);
    }
    
    public boolean getEmpty() {
        return items.size() == 0;
    }
}