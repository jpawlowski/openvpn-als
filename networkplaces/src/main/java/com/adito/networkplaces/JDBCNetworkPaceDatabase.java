
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
			
package com.adito.networkplaces;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.ContextHolder;
import com.adito.boot.SystemProperties;
import com.adito.core.CoreEvent;
import com.adito.core.CoreListener;
import com.adito.core.CoreServlet;
import com.adito.extensions.store.ExtensionStore;
import com.adito.extensions.types.PluginDefinition;
import com.adito.jdbc.DBUpgrader;
import com.adito.jdbc.JDBCDatabaseEngine;
import com.adito.jdbc.JDBCPreparedStatement;
import com.adito.jdbc.JDBCUtil;

/**
 * Implementation of a {@link com.adito.security.SystemDatabase} that uses
 * a JDBC compliant database to store Adito's network place configuration
 * and resources.
 */
public class JDBCNetworkPaceDatabase implements NetworkPlaceDatabase, CoreListener {
    private static final Log log = LogFactory.getLog(JDBCNetworkPaceDatabase.class);

    private JDBCDatabaseEngine db;

    /**
     * Constructor
     */
    public JDBCNetworkPaceDatabase() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.Database#open(com.adito.core.CoreServlet)
     */
    public void open(CoreServlet controllingServlet) throws Exception {
    	throw new Exception("Plugin databases need a PluginDefinition.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.boot.Database#close()
     */
    public void close() throws Exception {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.boot.Database#cleanup()
     */
    public void cleanup() throws Exception {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.CoreListener#coreEvent(com.adito.core.CoreEvent)
     */
    public void coreEvent(CoreEvent evt) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.security.SystemDatabase#createNetworkPlace(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, int, java.lang.String, java.lang.String, boolean,
     *      boolean, boolean, boolean)
     */
    public NetworkPlace createNetworkPlace(String scheme, String shortName, String description, String host, String uri, int port,
                    String username, String password, boolean readOnly, boolean allowResursive, boolean noDelete, boolean showHidden, boolean autoStart, int realmID)
                    throws Exception {
        JDBCPreparedStatement ps = db.getStatement("createNetworkPlace.insert");
        try {
            ps.setString(1, scheme);
            ps.setString(2, host);
            ps.setString(3, uri);
            ps.setInt(4, port);
            ps.setString(5, username);
            ps.setString(6, password);
            ps.setString(7, shortName);
            ps.setString(8, description);
            ps.setInt(9, readOnly ? 1 : 0);
            ps.setInt(10, allowResursive ? 1 : 0);
            ps.setInt(11, noDelete ? 1 : 0);
            ps.setInt(12, showHidden ? 1 : 0);
            ps.setInt(13, autoStart ? 1 : 0);
            Calendar now = Calendar.getInstance();
            ps.setString(14, db.formatTimestamp(now));
            ps.setString(15, db.formatTimestamp(now));
            ps.setInt(16, realmID);
            ps.execute();
            int id = db.getLastInsertId(ps, "createNetworkPlace.lastInsertId");
            return this.getNetworkPlace(id);
        } finally {
            ps.releasePreparedStatement();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.security.SystemDatabase#getNetworkPlace(int)
     */
    public NetworkPlace getNetworkPlace(int resourceId) throws Exception {
        JDBCPreparedStatement ps = null;
        ps = db.getStatement("getNetworkPlace.select");
        ps.setInt(1, resourceId);
        try {
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    return buildNetworkPlace(rs);
                }
                return null;
            } finally {
                rs.close();
            }
        } finally {
            ps.releasePreparedStatement();
        }
    }

    /* (non-Javadoc)
     * @see com.adito.networkplaces.NetworkPlaceDatabase#getNetworkPlace(java.lang.String, int)
     */
    public NetworkPlace getNetworkPlace(String name, int realmID) throws Exception {
        JDBCPreparedStatement ps = null;
        ps = db.getStatement("getNetworkPlace.select.name");
        ps.setString(1, name);
        ps.setInt(2, realmID);
        try {
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    return buildNetworkPlace(rs);
                }
                return null;
            } finally {
                rs.close();
            }
        } finally {
            ps.releasePreparedStatement();
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see com.adito.security.SystemDatabase#updateNetworkPlace(int,
     *      java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String, int, java.lang.String,
     *      java.lang.String, boolean, boolean, boolean, boolean)
     */
    public void updateNetworkPlace(int resourceId, String scheme, String resourceName, String resourceDescription, String host,
                    String uri, int port, String username, String password, boolean readOnly, boolean allowResursive,
                    boolean noDelete, boolean showHidden, boolean autoStart) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("updateNetworkPlace.update");
        try {
            ps.setString(1, resourceName);
            ps.setString(2, scheme);
            ps.setString(3, host);
            ps.setString(4, uri);
            ps.setInt(5, port);
            ps.setString(6, username);
            ps.setString(7, password);
            ps.setString(8, resourceDescription);
            ps.setInt(9, readOnly ? 1 : 0);
            ps.setInt(10, allowResursive ? 1 : 0);
            ps.setInt(11, noDelete ? 1 : 0);
            ps.setInt(12, showHidden ? 1 : 0);
            ps.setInt(13, autoStart ? 1 : 0);
            Calendar now = Calendar.getInstance();
            ps.setString(14, db.formatTimestamp(now));
            ps.setInt(15, resourceId);
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.security.SystemDatabase#getNetworkPlaces()
     */
    public List<NetworkPlace> getNetworkPlaces() throws Exception {
        List<NetworkPlace> v = new ArrayList<NetworkPlace>();
        JDBCPreparedStatement ps = db.getStatement("getNetworkPlaces.select");
        try {
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    v.add(buildNetworkPlace(rs));
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.releasePreparedStatement();
        }
        return v;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.security.SystemDatabase#deleteNetworkPlace(int)
     */
    public NetworkPlace deleteNetworkPlace(int id) throws Exception {
        NetworkPlace np = getNetworkPlace(id);
        if (np == null) {
            throw new Exception("Network Place " + id + " doesn't exist.");
        }
        JDBCPreparedStatement ps = null;
        ps = db.getStatement("deleteNetworkPlace.delete");
        try {
            ps.setInt(1, id);
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }
        return np;
    }

    /**
     * Build the network place from the result set.
     * 
     * @param rs
     * @return NetworkPlace
     * @throws Exception
     */
    NetworkPlace buildNetworkPlace(ResultSet resultSet) throws Exception {
        int realmId = resultSet.getInt("realm_id");
        int resourceId = resultSet.getInt("resource_id");
        String scheme = resultSet.getString("scheme");
        String shortName = resultSet.getString("short_name");
        String description = resultSet.getString("description");
        String host = resultSet.getString("host");
        String path = resultSet.getString("path");
        int port = resultSet.getInt("port");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        boolean readOnly = resultSet.getBoolean("read_only");
        boolean allowRecursive = resultSet.getBoolean("allow_resursive");
        boolean noDelete = resultSet.getBoolean("no_delete");
        boolean showHidden = resultSet.getBoolean("show_hidden");
        boolean autoStart = resultSet.getBoolean("auto_start");
        Calendar dateCreated = JDBCUtil.getCalendar(resultSet, "date_created");
        Calendar dateAmended = JDBCUtil.getCalendar(resultSet, "date_amended");
        return new DefaultNetworkPlace(realmId, resourceId, scheme, shortName, description, host, path, port, username,
                        password, NetworkPlace.TYPE_NORMAL, readOnly, allowRecursive, noDelete, showHidden, autoStart,
                        dateCreated, dateAmended);
    }

	/* (non-Javadoc)
	 * @see com.adito.plugin.PluginDatabase#open(com.adito.core.CoreServlet, com.adito.plugin.PluginDefinition)
	 */
	public void open(CoreServlet controllingServlet, PluginDefinition def) throws Exception {
        String dbName = SystemProperties.get("adito.systemDatabase.jdbc.dbName", "explorer_configuration");
        controllingServlet.addDatabase(dbName, ContextHolder.getContext().getDBDirectory());
        String jdbcUser = SystemProperties.get("adito.jdbc.username", "sa");
        String jdbcPassword = SystemProperties.get("adito.jdbc.password", "");
        String vendorDB = SystemProperties.get("adito.jdbc.vendorClass", "com.adito.jdbc.hsqldb.HSQLDBDatabaseEngine");

        if (log.isInfoEnabled()) {
            log.info("Network Places database is being opened...");
            log.info("JDBC vendor class implementation is " + vendorDB);
        }

        db = (JDBCDatabaseEngine) Class.forName(vendorDB).newInstance();
        db.init("networkPlacesDatabase", dbName, jdbcUser, jdbcPassword, getClass().getClassLoader());

        File upgradeDir = new File(def.getDescriptor().getApplicationBundle().getBaseDir(), "upgrade");
        DBUpgrader upgrader = new DBUpgrader(ExtensionStore.getInstance()
            .getExtensionBundle(NetworkPlacePlugin.BUNDLE_ID)
            .getVersion(), db, ContextHolder.getContext()
                        .getDBDirectory(), upgradeDir);
        upgrader.upgrade();

        CoreServlet.getServlet().addCoreListener(this);
	}

    /* (non-Javadoc)
     * @see com.adito.networkplaces.NetworkPlaceDatabase#getNetworkPlaces(int)
     */
    public List<NetworkPlace> getNetworkPlaces(int realmID) throws Exception {
        List<NetworkPlace> v = new ArrayList<NetworkPlace>();
        JDBCPreparedStatement ps = db.getStatement("getNetworkPlaces.realm.select");
        try {
            ps.setInt(1, realmID);
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    v.add(buildNetworkPlace(rs));
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.releasePreparedStatement();
        }
        return v;
    }
}
