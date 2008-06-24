
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

/**
 */
public interface TableModel {

    /**
     * Get the number of columns in the table.
     * 
     * @return columns
     */
    public int getColumnCount();

    /**
     * Get the name of a column
     * 
     * @param col column index
     * @return column name
     */
    public String getColumnName(int col);

    /**
     * Get the class of a column
     * 
     * @param col column index
     * @return column class
     */
    public Class<?> getColumnClass(int col);

    /**
     * Get the number of rows in the table
     * 
     * @return rows
     */
    public int getRowCount();
    
    /**
     * Get the value of a single cell
     * 
     * @param row row
     * @param col column
     * @return value
     */
    public Object getValue(int row, int col);    
}