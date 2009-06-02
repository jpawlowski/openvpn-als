
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.security;

import net.openvpn.als.table.AbstractTableItemTableModel;

/**
 * Extension of {@link net.openvpn.als.table.AbstractTableItemTableModel}
 * to be used for display lists of users (i.e. {@link net.openvpn.als.security.UserItem}
 * objects).
 */
public class UserItemModel extends AbstractTableItemTableModel<UserItem> {

    /* (non-Javadoc)
     * @see net.openvpn.als.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 1;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int col) {
        return col == 0 ? "account" : null;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.table.TableModel#getColumnClass(int)
     */
    public Class<?> getColumnClass(int col) {
        return String.class;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.table.AbstractTableItemTableModel#getColumnWidth(int)
     */
    public int getColumnWidth(int col) {
        return 0;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.table.TableItemModel#getId()
     */
    public String getId() {
        return "accounts";
    }
}