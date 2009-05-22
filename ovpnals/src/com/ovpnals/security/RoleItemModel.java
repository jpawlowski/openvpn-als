
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
			
package com.ovpnals.security;

import com.ovpnals.table.AbstractTableItemTableModel;


/**
 * Implementation of {@link AbstractTableItemTableModel} that is used
 * to provide a list of configured roles.
 * 
 * @see RoleItem
 */
public class RoleItemModel extends AbstractTableItemTableModel {

    /* (non-Javadoc)
     * @see com.ovpnals.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 1;
    }

    /* (non-Javadoc)
     * @see com.ovpnals.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int col) {
        return "role";
    }

    /* (non-Javadoc)
     * @see com.ovpnals.table.TableModel#getColumnClass(int)
     */
    public Class getColumnClass(int col) {
        return String.class;
    }

    public int getColumnWidth(int col) {
        return 0;
    }

    public String getId() {
        return "roles";
    }
}