
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
			
package com.adito.extensions;

import com.adito.table.AbstractTableItemTableModel;


/**
 * Implementation of {@link AbstractTableItemTableModel} that is used
 * to provide a list of extension bundles.
 * 
 * @see ExtensionBundleItem
 */
public class ExtensionBundleItemModel extends AbstractTableItemTableModel {

    /* (non-Javadoc)
     * @see com.adito.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 2;
    }

    /* (non-Javadoc)
     * @see com.adito.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int col) {
        switch(col) {
            case 0:
                return "status";
            case 1:
                return "name";
            default:
                return "";
        }
    }

    /* (non-Javadoc)
     * @see com.adito.table.TableModel#getColumnClass(int)
     */
    public Class getColumnClass(int col) {
        return String.class;
    }

    public int getColumnWidth(int col) {
        return 0;
    }

    public String getId() {
        return "extensionStore";
    }

}