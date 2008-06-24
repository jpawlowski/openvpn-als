
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
			
package com.adito.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Utilities useful when working with JDBC databases.
 */
public class JDBCUtil {

    /**
     * Convert a boolean value to the appropriate int.
     * @param value
     * @return int
     */
    public static int toInt(boolean value) {
        return value ? 1 : 0;
    }
    
    /**
     * Turn an array of Strings into a SQL compatible IN String.
     * @param inList
     * @return String
     */
    public static String toInString(String[] inList) {
        return toInString(inList, false);
    }
    
    /**
     * Turn an array of Strings into a SQL compatible IN String.
     * @param inList
     * @param quoteString if <tt>true</true> each String will be enclosed in quotes.
     * @return String
     */
    public static String toInString(String[] inList, boolean quoteString) {
        StringBuilder buffer = new StringBuilder();
        for (String value : inList) {
            if (buffer.length() > 0) {
                buffer.append(",");
            }
            
            if (quoteString) {
                buffer.append("'");
                buffer.append(value);
                buffer.append("'");
            } else {
                buffer.append(value);
            }
        }
        return buffer.toString();
    }
    
    /**
     * Turn an IN String back into an array of Strings.
     * @param inString
     * @return String array
     */
    public static String[] fromInString(String inString) {
        Collection<String> tokens = new ArrayList<String>();
        for (StringTokenizer tokenizer = new StringTokenizer(inString, ","); tokenizer.hasMoreTokens();) {
            tokens.add(tokenizer.nextToken());
        }
        return tokens.toArray(new String[tokens.size()]);
    }

    /**
     * Validates if the supplied value is null from the result set. This is
     * useful for primitive type checking.
     * 
     * @param resultSet
     * @param columnName
     * @return true if the column was null.
     * @throws SQLException
     */
    public static boolean isNull(ResultSet resultSet, String columnName) throws SQLException {
        return null == resultSet.getObject(columnName);
    }

    /**
     * Transforms a given column from the java.sql.ResultSet from a
     * java.sql.Timestamp to a java.util.Calendar.
     * 
     * @param resultSet
     * @param columnName
     * @return java.util.Calendar
     * @throws SQLException
     */
    public static Calendar getCalendar(ResultSet resultSet, String columnName) throws SQLException {
        return getCalendar(resultSet.getTimestamp(columnName));
    }

    /**
     * Transforms the supplied java.sql.Timestamp into a java.util.Calendar.
     * 
     * @param timestamp
     * @return java.util.Calendar
     */
    public static Calendar getCalendar(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp == null ? System.currentTimeMillis() : timestamp.getTime());
        return calendar;
    }
    
    /**
     * @param toFormat
     * @return String
     */
    public static String formatDate(Date toFormat) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(toFormat);
    }
    
    /**
     * Closes the supplied Connection handling any exceptions that may be thrown.
     * @param connection
     */
    public static void cleanup(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }
    
    /**
     * Closes the supplied Statement handling any exceptions that may be thrown.
     * @param statement
     */
    public static void cleanup(Statement statement) {
        if(statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }
    
    /**
     * Closes the supplied JDBCPreparedStatement handling any exceptions that may be thrown.
     * @param statement
     */
    public static void cleanup(JDBCPreparedStatement statement) {
        cleanup(statement, false);
    }

    /**
     * Closes the supplied JDBCPreparedStatement handling any exceptions that may be thrown.
     * @param statement
     * @param endTransaction <tt>true</tt> if an existing transaction should be ended before the statement is closed.
     */
    public static void cleanup(JDBCPreparedStatement statement, boolean endTransaction) {
        if(statement != null) {
            try {
                if(endTransaction) {
                    statement.endTransaction();
                }
                statement.releasePreparedStatement();
            } catch (SQLException e) {
                // ignore
            }
        }
    }

    /**
     * Closes the supplied ResultSet handling any exceptions that may be thrown.
     * @param resultSet
     */
    public static void cleanup(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                // ignored
            }
        }
    }
}