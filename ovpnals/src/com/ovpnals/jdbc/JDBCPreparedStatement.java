package com.ovpnals.jdbc;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 */
public class JDBCPreparedStatement {

    String key;
    PreparedStatement ps;
    JDBCDatabaseEngine con;
    JDBCConnectionImpl impl;
    String sql;
    boolean transaction;

    JDBCPreparedStatement(String key, String sql, JDBCDatabaseEngine con, JDBCConnectionImpl impl, boolean transaction)
        throws SQLException {

        this.key = key;
        this.con = con;
        this.impl = impl;
        this.sql = sql;
        this.ps = impl.aquirePreparedStatement(key, sql);
        this.transaction = transaction;
    }

    public JDBCConnectionImpl getConnection() {
        return impl;
    }

    public void reprepare(String key, String sql) throws SQLException {
        if (this.ps != null) {
            impl.releasePreparedStatement(this.key, ps);
        }
        this.key = key;
        this.sql = sql;
        this.ps = impl.aquirePreparedStatement(key, sql);
        reset();
    }

    public boolean execute() throws SQLException {
        return ps.execute();
    }

    public void reset() throws SQLException {
        ps.clearParameters();
    }

    public ResultSet executeQuery() throws SQLException {
        return ps.executeQuery();

    }

    public int executeUpdate() throws SQLException {
        return ps.executeUpdate();

    }

    public PreparedStatement getPreparedStatement() {
        return ps;
    }

    /**
     * Release the statement and the connection
     * 
     * @throws SQLException
     */
    public void releasePreparedStatement() throws SQLException {
        ps.clearParameters();
        impl.releasePreparedStatement(key, ps);
        if (!transaction) {
            con.releaseConnection(impl);
        }
    }

    public void setTimestamp(int idx, Calendar calendar) throws SQLException {
        ps.setTimestamp(idx, new Timestamp(calendar.getTimeInMillis()));
    }
    
  	public void setTimestamp(int idx, Date date) throws SQLException {
        ps.setTimestamp(idx, new Timestamp(date.getTime()));
    }

    public void setString(int idx, String str) throws SQLException {
        ps.setString(idx, str);
    }

    public void setNull(int idx, int parameterType) throws SQLException {
        ps.setNull(idx, parameterType);
    }

    public void setInt(int idx, int val) throws SQLException {
        ps.setInt(idx, val);
    }

    public void setLong(int idx, long val) throws SQLException {
        ps.setLong(idx, val);
    }

    public void setObject(int idx, Object val) throws SQLException {
        ps.setObject(idx, val);
    }

    public void setBinaryStream(int idx, InputStream stream, int length) throws SQLException {
        ps.setBinaryStream(idx, stream, length);
    }

    public String toString() {
        return ps.toString();
    }

    public void startTransaction() throws SQLException {
        if (transaction) {
            throw new SQLException("Transaction already started.");
        }
        transaction = true;
        impl.setAutoCommit(false);
    }

    public void endTransaction() throws SQLException {
        if (!transaction) {
            throw new SQLException("Transaction already not started.");
        }
        transaction = false;
        impl.setAutoCommit(true);
        con.releaseConnection(impl);
    }

    public void rollback() throws SQLException {
        impl.rollback();
    }

    public void commit() throws SQLException {
        impl.commit();
    }
}