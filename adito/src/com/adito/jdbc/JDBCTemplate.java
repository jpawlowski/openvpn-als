
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Wrapper to ease the problems with JDBC usage.
 */
public final class JDBCTemplate {
    private static final Object[] EMPTY_ARGS = {};
    private static final int[] EMPTY_TYPES = {};
    private final JDBCDatabaseEngine engine;

    /**
     * Constructs a new jdbc template with the specified database engine.
     * 
     * @param engine the engine to use.
     */
    public JDBCTemplate(JDBCDatabaseEngine engine) {
        this.engine = engine;
    }

    /**
     * Gets a statement using the specified key and sets the arguments on the
     * statement.
     * 
     * @param statementKey the key of the statement to execute
     * @param args the arguments to set on the statement
     * @param argTypes the argument types to set on the statement
     * @return the constructed <code>JDBCPreparedStatement</code>
     * @throws DataAccessException
     */
    private final JDBCPreparedStatement getStatement(String statementKey, Object[] args, int[] argTypes) throws DataAccessException {
        try {
            JDBCPreparedStatement statement = engine.getStatement(statementKey);
            setStatementArguments(statement, args, argTypes);
            return statement;
        } catch (ClassNotFoundException cnfe) {
            throw new DataAccessException("Failed to load the JDBC driver", cnfe);
        } catch (SQLException sqle) {
            throw new DataAccessException("Failed to get statement = '" + statementKey + "'", sqle);
        }
    }

    private static void setStatementArguments(JDBCPreparedStatement statement, Object[] args, int[] argTypes) throws SQLException {
        if (args == null) {
            throw new IllegalArgumentException("args cannot be null.");
        }

        if (argTypes == null) {
            throw new IllegalArgumentException("argTypes cannot be null.");
        }

        if (args.length != argTypes.length) {
            throw new IllegalArgumentException("args length = '" + args.length + "' does not match argTypes length = '"
                            + argTypes.length + "'");
        }

        for (int index = 0; index < args.length; index++) {
            setStatementArgument(statement, index + 1, args[index], argTypes[index]);
        }
    }

    private static void setStatementArgument(JDBCPreparedStatement statement, int index, Object arg, int argType)
                    throws SQLException {
        if (Types.INTEGER == argType) {
            statement.setInt(index, (Integer) arg);
        } else if (Types.BIGINT == argType) {
            statement.setLong(index, (Long) arg);
        } else if (Types.VARCHAR == argType) {
            statement.setString(index, (String) arg);
        } else if (Types.DATE == argType || Types.TIMESTAMP == argType) {
            if (arg instanceof Date) {
                statement.setTimestamp(index, (Date) arg);
            } else if (arg instanceof Calendar) {
                statement.setTimestamp(index, (Calendar) arg);
            } else {
                statement.setObject(index, arg);
            }
        } else if (Types.NULL == argType) {
            statement.setNull(index, (Integer) arg);
        } else if (Types.BINARY == argType) {
            JDBCInputStream stream = (JDBCInputStream) arg;
            statement.setBinaryStream(index, stream.getInputStream(), stream.getLength());
        } else {
            statement.setObject(index, arg);
        }
    }

    /**
     * Execute a query and return the single int result.
     * 
     * @param statementKey the key of the statement to execute
     * @return the mapped int
     * @throws DataAccessException
     */
    public int queryForInt(String statementKey) throws DataAccessException {
        return queryForInt(statementKey, EMPTY_ARGS, EMPTY_TYPES);
    }

    /**
     * Execute a query and return the single int result.
     * 
     * @param statementKey the key of the statement to execute
     * @param arg the argument to set on the statement
     * @param argType the argument type to set on the statement
     * @return the mapped int
     * @throws DataAccessException
     */
    public int queryForInt(String statementKey, Object arg, int argType) throws DataAccessException {
        return queryForInt(statementKey, new Object[] { arg }, new int[] { argType });
    }

    /**
     * Execute a query and return the single int result.
     * 
     * @param statementKey the key of the statement to execute
     * @param args the arguments to set on the statement
     * @param argTypes the argument types to set on the statement
     * @return the mapped int
     * @throws DataAccessException
     */
    public int queryForInt(String statementKey, Object[] args, int[] argTypes) throws DataAccessException {
        List<Integer> results = query(statementKey, args, argTypes, new JDBCRowMapper<Integer>() {
            public Integer mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
                return Integer.valueOf(resultSet.getInt(1));
            }
        });
        if (results.size() != 1) {
            throw new DataAccessException("Expected 1 result but found " + results.size());
        }
        return results.get(0);
    }

    /**
     * Execute a query and return the single result.
     * 
     * @param <T> the required type
     * @param statementKey the key of the statement to execute
     * @param mapper the object to map the ResultSet into the required Object
     * @return the mapped object
     * @throws DataAccessException
     */
    public <T> T queryForObject(String statementKey, JDBCRowMapper<T> mapper) throws DataAccessException {
        return queryForObject(statementKey, EMPTY_ARGS, EMPTY_TYPES, mapper);
    }

    /**
     * Execute a query and return the single result.
     * 
     * @param <T> the required type
     * @param statementKey the key of the statement to execute
     * @param arg the argument to set on the statement
     * @param argType the argument type to set on the statement
     * @param mapper the object to map the ResultSet into the required Object
     * @return the mapped object
     * @throws DataAccessException
     */
    public <T> T queryForObject(String statementKey, Object arg, int argType, JDBCRowMapper<T> mapper) {
        return queryForObject(statementKey, new Object[] { arg }, new int[] { argType }, mapper);
    }

    /**
     * Execute a query and return the single result.
     * 
     * @param <T> the required type
     * @param statementKey the key of the statement to execute
     * @param args the arguments to set on the statement
     * @param argTypes the argument types to set on the statement
     * @param mapper the object to map the ResultSet into the required Object
     * @return the mapped object
     * @throws DataAccessException
     */
    public <T> T queryForObject(String statementKey, Object[] args, int[] argTypes, JDBCRowMapper<T> mapper)
                    throws DataAccessException {
        List<T> results = query(statementKey, args, argTypes, mapper);
        if (results.size() != 1) {
            throw new DataAccessException("Expected 1 result but found " + results.size());
        }
        return results.get(0);
    }

    /**
     * Execute a query and return the single result or null if no result was
     * found.
     * 
     * @param <T> the required type
     * @param statementKey the key of the statement to execute
     * @param mapper the object to map the ResultSet into the required Object
     * @return the mapped object
     * @throws DataAccessException
     */
    public <T> T queryForObjectOrNull(String statementKey, JDBCRowMapper<T> mapper) throws DataAccessException {
        return queryForObjectOrNull(statementKey, EMPTY_ARGS, EMPTY_TYPES, mapper);
    }

    /**
     * Execute a query and return the single result or null if no result was
     * found.
     * 
     * @param <T> the required type
     * @param statementKey the key of the statement to execute
     * @param arg the argument to set on the statement
     * @param argType the argument type to set on the statement
     * @param mapper the object to map the ResultSet into the required Object
     * @return the mapped object
     * @throws DataAccessException
     */
    public <T> T queryForObjectOrNull(String statementKey, Object arg, int argType, JDBCRowMapper<T> mapper) {
        return queryForObjectOrNull(statementKey, new Object[] { arg }, new int[] { argType }, mapper);
    }

    /**
     * Execute a query and return the single result or null if no result was
     * found.
     * 
     * @param <T> the required type
     * @param statementKey the key of the statement to execute
     * @param args the arguments to set on the statement
     * @param argTypes the argument types to set on the statement
     * @param mapper the object to map the ResultSet into the required Object
     * @return the mapped object
     * @throws DataAccessException
     */
    public <T> T queryForObjectOrNull(String statementKey, Object[] args, int[] argTypes, JDBCRowMapper<T> mapper)
                    throws DataAccessException {
        List<T> results = query(statementKey, args, argTypes, mapper);
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Execute a query and verifies if there were and results.
     * 
     * @param statementKey the key of the statement to execute
     * @param arg the argument to set on the statement
     * @param argType the argument type to set on the statement
     * @return <tt>true</tt> if the query yielded any results.
     * @throws DataAccessException
     */
    public boolean queryForResult(String statementKey, Object arg, int argType) {
        return queryForResult(statementKey, new Object[] { arg }, new int[] { argType });
    }

    /**
     * Execute a query and verifies if there were and results.
     * 
     * @param statementKey the key of the statement to execute
     * @param args the arguments to set on the statement
     * @param argTypes the argument types to set on the statement
     * @return <tt>true</tt> if the query yielded any results.
     * @throws DataAccessException
     */
    public boolean queryForResult(String statementKey, Object[] args, int[] argTypes) {
        List<Object> results = query(statementKey, args, argTypes, new JDBCRowMapper<Object>() {
            public Object mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
                return resultSet.getObject(1);
            }
        });
        return results.isEmpty() ? false : true;
    }

    /**
     * Execute a query and map each row retrieved to a Java object.
     * 
     * @param <T> the required type
     * @param statementKey the key which defines the statement to execute
     * @param mapper the object to map the ResultSet into the required Object
     * @return the result List that contains the mapped objects
     * @throws DataAccessException
     */
    public <T> List<T> query(String statementKey, JDBCRowMapper<T> mapper) throws DataAccessException {
        return query(statementKey, EMPTY_ARGS, EMPTY_TYPES, mapper);
    }

    /**
     * Execute a query and map each row retrieved to a Java object.
     * 
     * @param <T> the required type
     * @param statementKey the key of the statement to execute
     * @param arg the argument to set on the statement
     * @param argType the argument type to set on the statement
     * @param mapper the object to map the ResultSet into the required Object
     * @return the result List that contains the mapped objects
     * @throws DataAccessException
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> query(String statementKey, Object arg, int argType, JDBCRowMapper<T> mapper) throws DataAccessException {
        return query(statementKey, new Object[] { arg }, new int[] { argType }, mapper);
    }

    /**
     * Execute a query and map each row retrieved to a Java object.
     * 
     * @param <T> the required type
     * @param statementKey the key of the statement to execute
     * @param args the arguments to set on the statement
     * @param argTypes the argument types to set on the statement
     * @param mapper the object to map the ResultSet into the required Object
     * @return the result List that contains the mapped objects
     * @throws DataAccessException
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> query(String statementKey, Object[] args, int[] argTypes, JDBCRowMapper<T> mapper)
                    throws DataAccessException {
        JDBCPreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = getStatement(statementKey, args, argTypes);
            resultSet = preparedStatement.executeQuery();
            List<T> results = new ArrayList<T>();
            int rowNumber = 0;
            while (resultSet.next()) {
                results.add(mapper.mapRow(resultSet, rowNumber++));
            }
            return results;
        } catch (SQLException sqle) {
            throw new DataAccessException("Failed to execute query", sqle);
        } finally {
            JDBCUtil.cleanup(resultSet);
            JDBCUtil.cleanup(preparedStatement);
        }
    }

    /**
     * Execute the specified query.
     * 
     * @param statementKey the key of the statement to execute
     * @return <tt>true</tt> if the execution was successful
     * @throws DataAccessException
     */
    public boolean execute(String statementKey) {
        return execute(statementKey, EMPTY_ARGS, EMPTY_TYPES);
    }

    /**
     * Execute the specified query.
     * 
     * @param statementKey the key of the statement to execute
     * @param arg the argument to set on the statement
     * @param argType the argument type to set on the statement
     * @return <tt>true</tt> if the execution was successful
     * @throws DataAccessException
     */
    public boolean execute(String statementKey, Object arg, int argType) {
        return execute(statementKey, new Object[] { arg }, new int[] { argType });
    }

    /**
     * Execute the specified query.
     * 
     * @param statementKey the key of the statement to execute
     * @param args the arguments to set on the statement
     * @param argTypes the argument types to set on the statement
     * @return <tt>true</tt> if the execution was successful
     * @throws DataAccessException
     */
    public boolean execute(String statementKey, Object[] args, int[] argTypes) {
        JDBCPreparedStatement preparedStatement = null;
        try {
            preparedStatement = getStatement(statementKey, args, argTypes);
            return preparedStatement.execute();
        } catch (SQLException sqle) {
            throw new DataAccessException("Failed to execute query", sqle);
        } finally {
            JDBCUtil.cleanup(preparedStatement);
        }
    }

    /**
     * Execute the specified insert query.
     * 
     * @param statementKey the key of the statement to execute
     * @param lastInsertIdStatemenKey the key of the statement which returns the
     *        last inserted records id
     * @param args the arguments to set on the statement
     * @param argTypes the argument types to set on the statement
     * @return <tt>true</tt> if the execution was successful
     * @throws DataAccessException
     */
    public int executeInsert(String statementKey, String lastInsertIdStatemenKey, Object[] args, int[] argTypes) {
        JDBCPreparedStatement preparedStatement = null;
        try {
            preparedStatement = getStatement(statementKey, args, argTypes);
            preparedStatement.startTransaction();
            preparedStatement.execute();

            int lastInsertId = engine.getLastInsertId(preparedStatement, lastInsertIdStatemenKey);
            preparedStatement.commit();
            return lastInsertId;
        } catch (SQLException sqle) {
            rollback(preparedStatement);
            throw new DataAccessException("Failed to execute insert", sqle);
        } finally {
            JDBCUtil.cleanup(preparedStatement, true);
        }
    }

    private static void rollback(JDBCPreparedStatement statement) {
        if (statement != null) {
            try {
                statement.rollback();
            } catch (SQLException e) {
                // ignore
            }
        }
    }
}