
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
			
package com.ovpnals.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JDBCConnectionImpl {

    final static Log log = LogFactory.getLog(JDBCConnectionImpl.class);
    
    static long currentId = 0;

    // database instance name, such as myOracleDB
    private String key;
    private long id;
    private Connection conn;
    private Hashtable preparedStatements = new Hashtable();

    // the precious connection
    private JDBCConnectionImpl(String key, JDBCConnectionSettings settings) throws SQLException, 
                    ClassNotFoundException {
        id = currentId++;
        this.key = key;
        Class.forName(settings.driver);
        conn = DriverManager.getConnection(settings.url, settings.username, settings.password);
    }
    
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        conn.setAutoCommit(autoCommit);
    }

    String getKey() {
        return key;
    }

    boolean isClosed() throws SQLException {
        return conn.isClosed();
    }

    ResultSet executeQuery(String sqlString) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sqlString);
        return rs;
    }

    void execute(String sqlString) throws SQLException {
        Statement stmt = conn.createStatement();
        try {
            stmt.execute(sqlString);
        }
        finally {
            stmt.close();
        }
    }

    synchronized PreparedStatement aquirePreparedStatement(String key, String sqlString) throws SQLException {
        Vector avail = (Vector) preparedStatements.get(key);
        if (avail == null) {
            avail = new Vector();
            preparedStatements.put(key, avail);
        }

        if (avail.size() > 0) {
            return (PreparedStatement) avail.remove(0);
        } else
            return conn.prepareStatement(sqlString);
    }

    synchronized void releasePreparedStatement(String key, PreparedStatement ps) throws SQLException {
        ps.clearParameters();
        Vector avail = (Vector) preparedStatements.get(key);
        avail.add(ps);
    }

    public long getId() {
        return id;
    }
    
    public Connection getConnection() {
        return conn;
    }

    public static class JDBCPool {
        // dictionary of database names with corresponding vector of connections
        private Hashtable poolDictionary = new Hashtable();
        // dictionary of database settings
        private Hashtable poolSettings = new Hashtable();

        // methods and attributes for Singleton pattern
        private JDBCPool() {
        } // private constructor

        private static JDBCPool _instance; // get class instance

        // Singleton getter utilizing Double Checked Locking pattern
        public static JDBCPool getInstance() {
            if (_instance == null) {
                synchronized (JDBCPool.class) {
                    if (_instance == null) {
                        _instance = new JDBCPool();
                    }
                }
            }
            return _instance;
        }

        public synchronized void createImpl(String key, String driver, String url, String username, String password)
                        throws SQLException, ClassNotFoundException {

            Vector pool = new Vector();

            JDBCConnectionSettings settings = new JDBCConnectionSettings();
            settings.driver = driver;
            settings.url = url;
            settings.username = username;
            settings.password = password;

            poolDictionary.put(key, pool);
            poolSettings.put(key, settings);

            pool.addElement(new JDBCConnectionImpl(key, settings));

        }
        
        public Map getPools() {
            return poolDictionary;
        }

        public synchronized void closeAll() {
            for (Iterator i = poolDictionary.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry) i.next();
                if (log.isInfoEnabled())
                	log.info("Closing connections for " + entry.getKey());
                Vector pool = (Vector) entry.getValue();
                for (Iterator j = pool.iterator(); j.hasNext();) {
                    JDBCConnectionImpl conx = (JDBCConnectionImpl) j.next();
                    try {
                        conx.conn.close();
                    } catch (SQLException sqle) {
                        log.warn("Failed to close pooled connection. ", sqle);
                    }
                }
            }
            poolDictionary.clear();

        }

        // get connection from pool
        public synchronized JDBCConnectionImpl acquireImpl(String key) throws SQLException, ClassNotFoundException {
        	if (log.isDebugEnabled())
        		log.debug("Aquiring connection for " + key);

            // get pool matching database name
            Vector pool = (Vector) poolDictionary.get(key);
            if (pool != null) {
                while (pool.size() > 0) {
                	if (log.isDebugEnabled())
                		log.debug(pool.size() + " connections in pool.");
                    JDBCConnectionImpl impl = null;
                    // retrieve existing unused connection
                    impl = (JDBCConnectionImpl) pool.elementAt(pool.size() - 1);
                    // remove connection from pool
                    pool.removeElementAt(pool.size() - 1);

                    // If the connection has closed then drop it and select
                    // another
                    if (impl.isClosed()) {
                    	if (log.isDebugEnabled())
                    		log.debug("Connecting for " + key + " is closed, get the next one");
                        continue;
                    }

                    if (log.isDebugEnabled())
                    	log.debug("Found connection " + impl.toString());

                    return impl;
                }
            }
            if (log.isDebugEnabled())
            	log.debug("Pool is empty, getting ");

            JDBCConnectionSettings settings = (JDBCConnectionSettings) poolSettings.get(key);
            // pool is empty so create new connection
            return new JDBCConnectionImpl(key, settings);
        }

        // return connection to pool
        public synchronized void releaseImpl(JDBCConnectionImpl impl) {
            String key = impl.getKey();
            Vector pool = (Vector) poolDictionary.get(key);
            if (pool == null) {
                pool = new Vector();
                poolDictionary.put(key, pool);
            }

            pool.addElement(impl);
        }
    }

    static class JDBCConnectionSettings {
        String url;
        String driver;
        String cls;
        String username;
        String password;
    }

    public void rollback() throws SQLException {
        conn.rollback();
    }

    public void commit() throws SQLException {
        conn.commit();
    }
}
