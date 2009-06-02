
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
			
package net.openvpn.als.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An interface used to map result sets. Implementations of this interface map
 * the actual row, but do not need to handle any resulting exceptions.
 * 
 * @param <T>
 */
public interface JDBCRowMapper<T> {
    /**
     * Implementations should map the ResultSet to the required Object.
     * 
     * @param resultSet the ResultSet to map the values from
     * @param rowNumber the number of the current row
     * @return the object mapped from the ResultSet
     * @throws SQLException if a database access error occurs
     */
    T mapRow(ResultSet resultSet, int rowNumber) throws SQLException;
}