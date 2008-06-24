
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
			
package com.adito.tunnels;

import java.io.File;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.ContextHolder;
import com.adito.boot.HostService;
import com.adito.boot.SystemProperties;
import com.adito.core.CoreEvent;
import com.adito.core.CoreListener;
import com.adito.core.CoreServlet;
import com.adito.extensions.store.ExtensionStore;
import com.adito.extensions.types.PluginDefinition;
import com.adito.jdbc.DBUpgrader;
import com.adito.jdbc.JDBCDatabaseEngine;
import com.adito.jdbc.JDBCPreparedStatement;

/**
 * Implementation of a {@link com.adito.security.SystemDatabase} that uses
 * a JDBC compliant database to store Adito's basic configuration and
 * resources.
 */
public class JDBCTunnelDatabase implements TunnelDatabase, CoreListener {
    private static final Log log = LogFactory.getLog(JDBCTunnelDatabase.class);

    private JDBCDatabaseEngine db;

    /**
     * Constructor
     */
    public JDBCTunnelDatabase() {
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
        // The removeUser() method is not user anymore
//        if (evt.getId() == CoreEventConstants.USER_REMOVED) {
//            User user = (User) evt.getParameter();
//
//            // LDP - Fix as null user might be passed?
//            if (user == null)
//                return;
//
//            try {
//                removeUser(user.getPrincipalName());
//            } catch (Exception e) {
//                log.error("Failed to remove user from system database.", e);
//            }
//        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.security.SystemDatabase#updateTunnel(int,
     *      java.lang.String, java.lang.String, int, boolean, java.lang.String,
     *      java.lang.String, int, com.adito.boot.HostService, boolean,
     *      int)
     */
    public void updateTunnel(int id, String name, String description, int type, boolean autoStart, String transport,
                    String username, int sourcePort, HostService destination, String sourceInterface) throws Exception {

        JDBCPreparedStatement ps2 = db.getStatement("updateTunnel.update");
        try {
            ps2.setString(1, name);
            ps2.setString(2, description);
            ps2.setInt(3, type);
            ps2.setInt(4, autoStart ? 1 : 0);
            ps2.setString(5, transport);
            ps2.setString(6, username == null ? "" : username);
            ps2.setInt(7, sourcePort);
            ps2.setInt(8, destination.getPort());
            ps2.setString(9, destination.getHost());
            ps2.setString(10, sourceInterface);
            Calendar now = Calendar.getInstance();
            ps2.setString(11, db.formatTimestamp(now));
            ps2.setInt(12, id);
            ps2.execute();
        } finally {
            ps2.releasePreparedStatement();
        }
    }

    /* (non-Javadoc)
     * @see com.adito.tunnels.TunnelDatabase#createTunnel(int, java.lang.String, java.lang.String, int, boolean, java.lang.String, java.lang.String, int, com.adito.boot.HostService, java.lang.String)
     */
    public Tunnel createTunnel(int realmID, String name, String description, int type, boolean autoStart, String transport, String username,
                    int sourcePort, HostService destination, String sourceInterface) throws Exception {

        JDBCPreparedStatement ps2 = db.getStatement("createTunnel.insert");
        try {
            ps2.setString(1, name);
            ps2.setString(2, description);
            ps2.setInt(3, type);
            ps2.setInt(4, autoStart ? 1 : 0);
            ps2.setString(5, transport);
            ps2.setString(6, username == null ? "" : username);
            ps2.setInt(7, sourcePort);
            ps2.setInt(8, destination.getPort());
            ps2.setString(9, destination.getHost());
            ps2.setString(10, sourceInterface);
            Calendar now = Calendar.getInstance();
            ps2.setString(11, db.formatTimestamp(now));
            ps2.setString(12, db.formatTimestamp(now));
            ps2.setInt(13, realmID);
            ps2.execute();
            return new DefaultTunnel(realmID, name, description, db.getLastInsertId(ps2, "createTunnel.lastInsertId"), type, autoStart,
                            transport, username, sourcePort, destination, sourceInterface, now, now);

        } finally {
            ps2.releasePreparedStatement();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.security.SystemDatabase#removeTunnel(int)
     */
    public Tunnel removeTunnel(int id) throws Exception {
        Tunnel t = getTunnel(id);
        if (t == null) {
            throw new Exception("No tunnel with " + id);
        }
        JDBCPreparedStatement ps = db.getStatement("removeTunnel.deleteFavorite");
        try {
            ps.setInt(1, TunnelPlugin.SSL_TUNNEL_RESOURCE_TYPE_ID);
            ps.setString(2, String.valueOf(id));
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }
        ps = db.getStatement("removeTunnel.deleteTunnel");
        try {
            ps.setInt(1, id);
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }
        return t;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.security.SystemDatabase#getTunnels(java.lang.String)
     */
    public List<Tunnel> getTunnels() throws Exception {
        JDBCPreparedStatement ps = db.getStatement("getTunnels.select");
        List<Tunnel> v = new ArrayList<Tunnel>();
        try {
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    v.add(buildTunnel(rs));
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
     * @see com.adito.security.SystemDatabase#getTunnel(int)
     */
    public Tunnel getTunnel(int id) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("getTunnel.select.id");
        ps.setInt(1, id);
        try {
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    return buildTunnel(rs);
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.releasePreparedStatement();
        }
        return null;
    }

    Tunnel buildTunnel(ResultSet rs) throws Exception {
        Timestamp cd = rs.getTimestamp("date_created");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(cd == null ? System.currentTimeMillis() : cd.getTime());
        Timestamp ad = rs.getTimestamp("date_amended");
        Calendar a = Calendar.getInstance();
        a.setTimeInMillis(ad == null ? System.currentTimeMillis() : ad.getTime());
        return new DefaultTunnel(rs.getInt("realm_id"), rs.getString("name"), rs.getString("description"), rs.getInt("tunnel_id"), rs.getInt("type"), rs
                        .getBoolean("auto_start"), rs.getString("transport"), rs.getString("username"), rs.getInt("source_port"),
                        new HostService(rs.getString("destination_host"), rs.getInt("destination_port")), rs
                                        .getString("source_interface"), c, a);
    }

    // 
    // Supporting classes
    //

    class CacheList extends ArrayList implements Serializable {
        private static final long serialVersionUID = 6613983448357872637L;
    }

	public void open(CoreServlet controllingServlet, PluginDefinition def) throws Exception {
        String dbName = SystemProperties.get("adito.systemDatabase.jdbc.dbName", "explorer_configuration");
        controllingServlet.addDatabase(dbName, ContextHolder.getContext().getDBDirectory());
        String jdbcUser = SystemProperties.get("adito.jdbc.username", "sa");
        String jdbcPassword = SystemProperties.get("adito.jdbc.password", "");
        String vendorDB = SystemProperties.get("adito.jdbc.vendorClass", "com.adito.jdbc.hsqldb.HSQLDBDatabaseEngine");

        if (log.isInfoEnabled()) {
            log.info("System database is being opened...");
            log.info("JDBC vendor class implementation is " + vendorDB);
        }

        db = (JDBCDatabaseEngine) Class.forName(vendorDB).newInstance();
        db.init("tunnelsDatabase", dbName, jdbcUser, jdbcPassword, null);

        File upgradeDir = new File(def.getDescriptor().getApplicationBundle().getBaseDir(), "upgrade");
        DBUpgrader upgrader = new DBUpgrader(ExtensionStore.getInstance()
            .getExtensionBundle(TunnelPlugin.BUNDLE_ID)
            .getVersion(), db, ContextHolder.getContext()
                        .getDBDirectory(), upgradeDir);
        upgrader.upgrade();

        CoreServlet.getServlet().addCoreListener(this);
	}

    /* (non-Javadoc)
     * @see com.adito.tunnels.TunnelDatabase#getTunnels(int)
     */
    public List<Tunnel> getTunnels(int realmID) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("getTunnels.realm.select");
        List<Tunnel> v = new ArrayList<Tunnel>();
        try {
            ps.setInt(1, realmID);
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    v.add(buildTunnel(rs));
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.releasePreparedStatement();
        }
        return v;
    }

    /* (non-Javadoc)
     * @see com.adito.tunnels.TunnelDatabase#getTunnel(java.lang.String, int)
     */
    public Tunnel getTunnel(String name, int realmID) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("getTunnel.select.name");
        ps.setString(1, name);
        ps.setInt(2, realmID);
        try {
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    return buildTunnel(rs);
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.releasePreparedStatement();
        }
        return null;
    }
}
