package com.adito.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.vfs.webdav.DAVUtilities;

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
public abstract class JDBCDatabaseEngine {

    Log log = LogFactory.getLog(JDBCDatabaseEngine.class);
    Connection conn;
    Properties SQL;
    String vendor;
    String db;
    String driver;
    String alias;
    String username;
    String password;
    
    // database instance name, such as myOracleDB
    // the connection pool for that database instance
    private JDBCConnectionImpl.JDBCPool connectionPool;

    public JDBCDatabaseEngine(String vendor, String driver) {
        this.vendor = vendor;
        this.driver = driver;
    }

    public abstract String getURL();

    public String getDatabase() {
        return db;
    }
    
    public String getAlias() {
        return alias;
    }

    public void init(String alias, String db, String username, String password, ClassLoader classLoader) throws SQLException, ClassNotFoundException {
        init(alias, db, username, password, null, classLoader);
    }

    /**
     * Initialize the DB connection using a specified resource path. This
     * location must contain both the default SQL resources and any vendor
     * specific resources.
     * 
     * @param alias alias
     * @param db String
     * @param username String
     * @param password String
     * @param defaultResourcePath String
     * @param classLoader class loader to load resources with 
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void init(String alias, String db, String username, String password, String defaultResourcePath, ClassLoader classLoader) throws SQLException,
                    ClassNotFoundException {
        try {

            this.db = db;
            this.alias = alias;
            this.username = username;
            this.password = password;
            
            /**
             * Load the SQL resources. This code assumes that if a default
             * resource path is supplied, for example "/com/adito/xtra"
             * then both generic SQL resources and database vendor specific
             * resource files are located in the same package.
             * 
             * Otherwise this code will load default resources from the
             * com.adito.core.jdbc package and vendor specific resources
             * from the package that contains the vendor implementation class.
             */

            String defaultResources;
            if (defaultResourcePath == null)
                defaultResources = "/prepared/" + alias + ".properties";
            else
                defaultResources = defaultResourcePath + "/" + alias + ".properties";

            /**
             * Get the database specific resources
             */
            String dbResources;

            if (defaultResourcePath == null)
                dbResources = "/prepared/" + vendor.toLowerCase() + "/" + alias + ".properties";
            else
                dbResources = defaultResourcePath + "/" + vendor.toLowerCase() + "/" + alias + ".properties";
            
            /**
             * Load the default resources into our properties object
             */
            SQL = new Properties();
            InputStream in = classLoader == null ? getClass().getResourceAsStream(defaultResources)
                                : classLoader.getResourceAsStream(DAVUtilities.stripLeadingSlash(defaultResources));
            if(in != null) {
                try {
                    SQL.load(in);
                }
                finally {
                    in.close();
                }
            }

            /**
             * If we have any database specific then override
             */
            in = classLoader == null ? getClass().getResourceAsStream(dbResources)
                    : classLoader.getResourceAsStream(DAVUtilities.stripLeadingSlash(dbResources));
            if (in != null) {
                try {
                    SQL.load(in);
                }
                finally {
                    in.close();
                }
            }
            connectionPool = JDBCConnectionImpl.JDBCPool.getInstance();
            String url = getURL();
            if (log.isInfoEnabled())
            	log.info("Aliasing database " + alias + " to " + db + " using driver " + driver + " and URL " + url);
            connectionPool.createImpl(alias + db, driver, getURL(), username, password);
        } catch (IOException ex) {
        	if (log.isInfoEnabled())
        		log.info("Failed to load database resources for " + db, ex);
            throw new SQLException("Failed to load database resources for " + db);
        }
    }

    public JDBCPreparedStatement getStatement(String key) throws SQLException, ClassNotFoundException {
        return getStatement(null, key);
    }

    public JDBCPreparedStatement getStatement(JDBCPreparedStatement ps,  String key) throws SQLException, ClassNotFoundException {

        if (SQL.containsKey(key)) {
            String sql = SQL.getProperty(key);
            if (log.isDebugEnabled())
            	log.debug("Aquiring statement for " + key + " = '" + sql + "'");
            return aquirePreparedStatement(key, sql, ps);
        }

        throw new SQLException("Unable to locate database resource " + key + " in " + getAlias());
    }

    public JDBCConnectionImpl aquireConnection() throws SQLException, ClassNotFoundException {
        return connectionPool.acquireImpl(alias + db);
    }

    public void releaseConnection(JDBCConnectionImpl con) throws SQLException {
        connectionPool.releaseImpl(con);
    }

    JDBCPreparedStatement aquirePreparedStatement(String key, String sql, JDBCPreparedStatement ps) throws SQLException, ClassNotFoundException {
        return new JDBCPreparedStatement(key, sql, this, ps == null ? aquireConnection() : ps.getConnection(), ps != null);
    }

    public void releasePreparedStatement(JDBCPreparedStatement ps) throws SQLException {
        ps.releasePreparedStatement();
    }

    // send a request to the database and return the result
    public ResultSet executeQuery(String sqlString) throws SQLException, ClassNotFoundException {
        JDBCConnectionImpl impl = connectionPool.acquireImpl(alias + db);
        try {
            ResultSet rs = impl.executeQuery(sqlString);
            return rs;
        } finally {
            connectionPool.releaseImpl(impl);
        }
    }

    public void execute(String sqlString) throws SQLException, ClassNotFoundException {
        JDBCConnectionImpl impl = connectionPool.acquireImpl(alias + db);
        try {
            impl.execute(sqlString);
        } finally {
            connectionPool.releaseImpl(impl);
        }
    }

    public boolean isDatabaseExists() {
        return true;
    }
    
    public long getLastInsertIdLong(JDBCPreparedStatement ps, String key) throws SQLException {
        String sql = SQL.getProperty(key);
        if (log.isDebugEnabled())
        	log.debug("Aquiring statement for " + key + " = '" + sql + "'");
        ps.reprepare(key, sql);
        ResultSet rs = ps.executeQuery();
        try {
            if (!rs.next()) {
                throw new SQLException("Failed to select last inserted ID from table");
            }
            return rs.getLong(1);
        }
        finally {
            rs.close();
        }
    }

    public int getLastInsertId(JDBCPreparedStatement ps, String key) throws SQLException {
        String sql = SQL.getProperty(key);
        if (log.isDebugEnabled())
        	log.debug("Aquiring statement for " + key + " = '" + sql + "'");
        ps.reprepare(key, sql);
        ResultSet rs = ps.executeQuery();
        try {
            if (!rs.next()) {
                throw new SQLException("Failed to select last inserted ID from table");
            }
            return rs.getInt(1);
        }
        finally {
            rs.close();
        }
    }

    public abstract String formatTimestamp(Calendar c);

    public void stop() {
        if(connectionPool != null) {
            connectionPool.closeAll();
        }
    }
}