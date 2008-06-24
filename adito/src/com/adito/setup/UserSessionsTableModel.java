
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
			
package com.adito.setup;

import java.net.InetAddress;
import java.util.Calendar;

import com.adito.table.AbstractTableItemTableModel;

public class UserSessionsTableModel extends AbstractTableItemTableModel {

    public int getColumnWidth(int col) {
        return 0;
    }

    public String getId() {
        return "status.sessions";
    }

    public int getColumnCount() {
        return 3;
    }

    public String getColumnName(int col) {
        switch(col) {
            case 0:
                return "user";
            case 2:
                return "address";
            default:
                return "dateTime";
        }
    }

    public Class getColumnClass(int col) {
        switch(col) {
            case 0:
                return String.class;
            case 1:
                return InetAddress.class;
            default:
                return Calendar.class;
        }
    }
}
