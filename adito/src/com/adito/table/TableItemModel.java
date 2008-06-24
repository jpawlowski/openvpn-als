
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

import java.util.List;

/**
 * @param <T>
 */
public interface TableItemModel<T extends TableItem> extends TableModel {
    
    /**
     * Get the ID of this table. This is used to store the current page and 
     * sort state in the session
     * @return String
     */
    public String getId();
    
    /**
     * Remove all items
     */
    public void clear();

    /**
     * Get a table item at the given index.
     * 
     * @param index index
     * @return table item
     */
    public T getItem(int index);

    /**
     * Add a table item to the model
     * 
     * @param item item to add
     */
    public void addItem(T item);

    /**
     * Return all items
     * 
     * @return items
     */
    public List<T> getItems();

    /**
     * Get if the model contains an item
     * 
     * @param item
     * @return <code>true</code> if item is contained in model
     */
    public boolean contains(T item);
    
    /**
     * Get if the model is empty
     * 
     * @return empty
     */
    public boolean getEmpty();
}