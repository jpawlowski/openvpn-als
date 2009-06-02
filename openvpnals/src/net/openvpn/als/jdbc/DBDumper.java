
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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Dumps the contents of a database to an output stream as SQL.
 */

public class DBDumper {

    final static Log log = LogFactory.getLog(DBDumper.class);

    /**
     * Dump table creation and data. It is up to the caller to close the stream and connections when
     * finished with.
     * 
     * @param writer write SQL to this writer.
     * @param conx connection to get data from
     * @param quoteChar character to use to quote strings
     * @throws Exception on any error
     */
    public void dumpToSQL(PrintWriter writer, JDBCConnectionImpl conx, char quoteChar) throws Exception {
        dumpTable(writer, conx, quoteChar, null);
        dumpData(writer, conx, quoteChar, null);
    }

    /**
     * Dump table creation SQL. It is up to the caller to close the stream and connections when
     * finished with.
     * 
     * @param writer write SQL to this writer.
     * @param conx connection to get data from
     * @param quoteChar character to use to quote strings
     * @param tables array of table names or <code>null</code> to dump all in
     *        database
     * @throws Exception on any error
     */
    public void dumpTable(PrintWriter writer, JDBCConnectionImpl conx, char quoteChar, String[] tables) throws Exception {
        Connection jdbcConnection = conx.getConnection();
        DatabaseMetaData dbMetaData = jdbcConnection.getMetaData();

        if (tables == null) {
            ResultSet rs = dbMetaData.getTables(null, null, null, null);
            try {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    String tableType = rs.getString("TABLE_TYPE");
                    if (tableType.equalsIgnoreCase("TABLE")) {
                        dumpTable(writer, conx, quoteChar, new String[] { tableName });
                    }
                }
            } finally {
                rs.close();
            }
        } else {
            for (int i = 0; i < tables.length; i++) {
                String tableName = tables[i];
                log.info("Dumping table creation for " + tableName);
                writer.println("CREATE TABLE " + tableName + " (");
                boolean first = true;

                // Columns
                ResultSet rs2 = dbMetaData.getColumns(null, null, tableName, "%");
                try {
                    while (rs2.next()) {
                        if (first) {
                            first = false;
                        } else {
                            writer.println(",");
                        }
                        String columnName = rs2.getString("COLUMN_NAME");
                        String columnType = rs2.getString("TYPE_NAME");
                        int columnSize = rs2.getInt("COLUMN_SIZE");
                        String nullable = rs2.getString("IS_NULLABLE");
                        String nullString = "NULL";
                        if ("NO".equalsIgnoreCase(nullable)) {
                            nullString = "NOT NULL";
                        }
                        writer.print("    " + columnName + " " + columnType);
                        if (columnSize != 0) {
                            if (columnType.equalsIgnoreCase("varchar") && columnSize > 255) {
                                columnSize = 255;
                            }
                            writer.print(" (" + columnSize + ")");
                        }
                        writer.print(" " + nullString);

                    }
                } finally {
                    rs2.close();
                }

                // Keys
                try {
                    rs2 = dbMetaData.getPrimaryKeys(null, null, tableName);
                    String primaryKeyName = null;
                    StringBuffer primaryKeyColumns = new StringBuffer();
                    while (rs2.next()) {
                        String thisKeyName = rs2.getString("PK_NAME");
                        if ((thisKeyName != null && primaryKeyName == null) || (thisKeyName == null && primaryKeyName != null)
                                        || (thisKeyName != null && !thisKeyName.equals(primaryKeyName))
                                        || (primaryKeyName != null && !primaryKeyName.equals(thisKeyName))) {
                            if (primaryKeyColumns.length() > 0) {
                                writer.print(",\n    PRIMARY KEY ");
                                if (primaryKeyName != null) {
                                    writer.print(primaryKeyName);
                                }
                                writer.print("(" + primaryKeyColumns.toString() + ")");
                            }
                            primaryKeyColumns = new StringBuffer();
                            primaryKeyName = thisKeyName;
                        }
                        if (primaryKeyColumns.length() > 0) {
                            primaryKeyColumns.append(", ");
                        }
                        primaryKeyColumns.append(rs2.getString("COLUMN_NAME"));
                    }
                    if (primaryKeyColumns.length() > 0) {
                        writer.print(",\n    PRIMARY KEY ");
                        if (primaryKeyName != null) {
                            writer.print(primaryKeyName);
                        }
                        writer.print(" (" + primaryKeyColumns.toString() + ")");
                    }
                } finally {
                    rs2.close();
                }
                writer.println("\n);");
                writer.println();
            }
        }
    }

    /**
     * Dump table creation SQL. It is up to the caller to close the stream and connections when
     * finished with.
     * 
     * @param writer write SQL to this writer.
     * @param conx connection to get data from
     * @param quoteChar character to use to quote strings
     * @param tables array of table names or <code>null</code> to dump all in
     *        database
     * @throws Exception on any error
     */
    public void dumpData(PrintWriter writer, JDBCConnectionImpl conx, char quoteChar, String[] tables) throws Exception {
        Connection jdbcConnection = conx.getConnection();
        DatabaseMetaData dbMetaData = jdbcConnection.getMetaData();

        if (tables == null) {
            ResultSet rs = dbMetaData.getTables(null, null, null, null);
            try {
                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    String tableType = rs.getString("TABLE_TYPE");
                    if (tableType.equalsIgnoreCase("TABLE")) {
                        dumpData(writer, conx, quoteChar, new String[] { tableName });
                    }
                }
            } finally {
                rs.close();
            }
        } else {
            for (int i = 0; i < tables.length; i++) {
                String tableName = tables[i];
                log.info("Dumping data for table " + tableName);
                // Data
                PreparedStatement stmt = jdbcConnection.prepareStatement("SELECT * FROM " + tableName);
                try {
                    ResultSet rs2 = stmt.executeQuery();
                    try {
                        while (rs2.next()) {
                            dumpRow(writer, rs2);
                        }
                    } finally {
                        rs2.close();
                    }
                } finally {
                    stmt.close();
                }
                writer.println();
            }
        }
    }
    
    /**
     * Dump a single result set row as an INSERT statement.
     * 
     * @param writer
     * @param resultSet
     * @throws SQLException
     */
    public void dumpRow(PrintWriter writer, ResultSet resultSet) throws SQLException {
        String tableName = resultSet.getMetaData().getTableName(1);
        int columnCount = resultSet.getMetaData().getColumnCount();
        writer.print("INSERT INTO " + tableName + " VALUES (");
        for (int j = 0; j < columnCount; j++) {
            if (j > 0) {
                writer.print(", ");
            }
            Object value = resultSet.getObject(j + 1);
            if (value == null) {
                writer.print("NULL");
            } else {
                String outputValue = value.toString();
                if (value instanceof Number) {
                    writer.print(outputValue);
                } else {
                    /*
                     * TODO
                     * 
                     * This escaping will current only work
                     * for HSQLDB. This needs to be moved up
                     * into the engine.
                     */
                    outputValue = outputValue.replaceAll("'", "''");
                    writer.print("'" + outputValue + "'");
                }
            }
        }
        writer.println(");");
    }

}
