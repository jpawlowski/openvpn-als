
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.table;

/**
 * Interface to be implement by all objects that are displayed as rows
 * in <i>Paged Table</i>.
 */
public interface TableItem {
    
    /**
     * Get the value of a column (first column is <i>zero</i>). Primarily
     * this is used for sorting and filter.
     * <p>
     * For sorting the object must implement {@link Comparable}. For filtering
     * the objects <code>toString()</code> method is used (except for dates
     * which are formatted according to the default locale into a string first).
     *   
     * @param col column index
     * @return column value
     */
    public Object getColumnValue(int col);

}
