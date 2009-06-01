
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
			
package net.openvpn.als.applications;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.boot.ContextHolder;
import net.openvpn.als.boot.SystemProperties;
import net.openvpn.als.core.CoreEvent;
import net.openvpn.als.core.CoreListener;
import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.extensions.store.ExtensionStore;
import net.openvpn.als.extensions.types.PluginDefinition;
import net.openvpn.als.jdbc.DBUpgrader;
import net.openvpn.als.jdbc.JDBCDatabaseEngine;
import net.openvpn.als.jdbc.JDBCPreparedStatement;
import net.openvpn.als.jdbc.JDBCUtil;

/**
 * Implementation of a {@link net.openvpn.als.security.SystemDatabase} that uses
 * a JDBC compliant database to store OpenVPN-ALS's basic configuration and
 * resources.
 */
public class JDBCApplicationShortcutDatabase implements ApplicationShortcutDatabase, CoreListener {
    private static final Log log = LogFactory.getLog(JDBCApplicationShortcutDatabase.class);

    private JDBCDatabaseEngine db;

    /**
     * Constructor
     */
    public JDBCApplicationShortcutDatabase() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.core.Database#open(net.openvpn.als.core.CoreServlet)
     */
    public void open(CoreServlet controllingServlet) throws Exception {
        throw new Exception("Plugin databases need a PluginDefinition.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.Database#close()
     */
    public void close() throws Exception {
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.boot.Database#cleanup()
     */
    public void cleanup() throws Exception {
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.core.CoreListener#coreEvent(net.openvpn.als.core.CoreEvent)
     */
    public void coreEvent(CoreEvent evt) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.extensions.ApplicationShortcutDatabase#createApplicationShortcut(java.lang.String,
     *      java.lang.String, java.lang.String, java.util.Map)
     */
    public int createApplicationShortcut(String application, String name, String description, Map settings, boolean autoStart, int realmID) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("createApplicationShortcut.insert");
        try {
            ps.setString(1, application);
            ps.setString(2, name);
            ps.setString(3, description);
            ps.setInt(4, autoStart ? 1 : 0);
            Calendar now = Calendar.getInstance();
            ps.setString(5, db.formatTimestamp(now));
            ps.setString(6, db.formatTimestamp(now));
            ps.setInt(7, realmID);
            ps.execute();
            int id = db.getLastInsertId(ps, "createApplicationShortcut.lastInsertId");
            JDBCPreparedStatement ps3 = db.getStatement("createApplicationShortcut.insertParameters");
            try {
                for (Iterator it = settings.keySet().iterator(); it.hasNext();) {
                    String parameter = (String) it.next();
                    String value = (String) settings.get(parameter);
                    ps3.setInt(1, id);
                    ps3.setString(2, parameter);
                    ps3.setString(3, value);
                    ps3.execute();
                    ps3.reset();
                }
            } finally {
                ps3.releasePreparedStatement();
            }
            return id;
        } finally {
            ps.releasePreparedStatement();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.extensions.ApplicationShortcutDatabase#updateApplicationShortcut(int,
     *      java.lang.String, java.lang.String, java.util.Map)
     */
    public void updateApplicationShortcut(int id, String name, String description, Map settings, boolean autoStart) throws Exception {

        JDBCPreparedStatement ps = db.getStatement("updateApplicationShortcut.update");

        // Update the actual shortcut
        try {
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setInt(3, autoStart ? 1 : 0);
            Calendar now = Calendar.getInstance();
            ps.setString(4, db.formatTimestamp(now));
            ps.setInt(5, id);
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }

        // Delete the current parameters
        ps = db.getStatement("updateApplicationShortcut.deleteParameters");
        try {
            ps.setInt(1, id);
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }

        // Insert the parameters again
        ps = db.getStatement("createApplicationShortcut.insertParameters");
        try {
            for (Iterator it = settings.keySet().iterator(); it.hasNext();) {
                String parameter = (String) it.next();
                String value = (String) settings.get(parameter);
                ps.setInt(1, id);
                ps.setString(2, parameter);
                ps.setString(3, value);
                ps.execute();
                ps.reset();

            }
        } finally {
            ps.releasePreparedStatement();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.extensions.ApplicationShortcutDatabase#removeApplicationShortcuts(java.lang.String)
     */
    public void removeApplicationShortcuts(String applicationId) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("removeApplicationShortcuts.select");
        ps.setString(1, applicationId);
        try {
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    JDBCPreparedStatement ps2 = db.getStatement("removeApplicationShortcuts.delete.favorites");
                    ps2.setInt(1, ApplicationsPlugin.APPLICATION_SHORTCUT_RESOURCE_TYPE_ID);
                    ps2.setInt(2, rs.getInt("resource_id"));
                    try {
                        ps2.execute();
                    } finally {
                        ps2.releasePreparedStatement();
                    }
                    ps2 = db.getStatement("removeApplicationShortcuts.delete.shortcutParameters");
                    ps2.setString(1, String.valueOf(rs.getInt("resource_id")));
                    try {
                        ps2.execute();
                    } finally {
                        ps2.releasePreparedStatement();
                    }
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.releasePreparedStatement();
        }
        ps = db.getStatement("removeApplicationShortcuts.delete.shortcuts");
        ps.setString(1, applicationId);
        try {
            ps.execute();
        } finally {
            ps.releasePreparedStatement();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.extensions.ApplicationShortcutDatabase#getShortcuts()
     */
    public List<ApplicationShortcut> getShortcuts() throws Exception {

        JDBCPreparedStatement ps = db.getStatement("getShortcuts.selectAll");
        Vector<ApplicationShortcut> v = new Vector<ApplicationShortcut>();
        try {
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    v.add(buildShortcut(rs));
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
     * @see net.openvpn.als.extensions.ApplicationShortcutDatabase#getShortcut(int)
     */
    public ApplicationShortcut getShortcut(int shortcutId) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("getShortcut.select");
        ps.setInt(1, shortcutId);
        try {
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    return buildShortcut(rs);
                } else {
                    return null;
                }
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
     * @see net.openvpn.als.extensions.ApplicationShortcutDatabase#deleteShortcut(int)
     */
    public ApplicationShortcut deleteShortcut(int shortcutId) throws Exception {

        ApplicationShortcut sc = getShortcut(shortcutId);
        if (sc == null) {
            throw new Exception("Application shortcut " + shortcutId + " does not exist.");
        }

        JDBCPreparedStatement ps = db.getStatement("deleteShortcuts.delete.favorite");

        // Delete a favorite
        try {
            ps.setString(1, String.valueOf(shortcutId));
            ps.setInt(2, ApplicationsPlugin.APPLICATION_SHORTCUT_RESOURCE_TYPE_ID);
            ps.execute();
            ps.reset();
        } finally {
            ps.releasePreparedStatement();
        }

        // Delete a shortcut
        ps = db.getStatement("deleteShortcuts.delete.shortcut");
        try {
            ps.setInt(1, shortcutId);
            ps.execute();
            ps.reset();
        } finally {
            ps.releasePreparedStatement();
        }

        // Delete all parameters for a shortcut
        ps = db.getStatement("deleteShortcuts.delete.shortcutParameters");
        try {
            ps.setInt(1, shortcutId);
            ps.execute();
            ps.reset();
        } finally {
            ps.releasePreparedStatement();
        }

        return sc;
    }

    /**
     * @param rs
     * @return ApplicationShortcut
     * @throws Exception
     */
    ApplicationShortcut buildShortcut(ResultSet resultSet) throws Exception {
        int resourceId = resultSet.getInt("resource_id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        String application = resultSet.getString("application");
        Calendar dateCreated = JDBCUtil.getCalendar(resultSet, "date_created");
        Calendar dateAmended = JDBCUtil.getCalendar(resultSet, "date_amended");
        int realmId = resultSet.getInt("realm_id");
        boolean autoStart = resultSet.getBoolean("auto_start");
        Map<String, String> settings = getParameters(resourceId);
        return new DefaultApplicationShortcut(realmId, resourceId, name, description, dateCreated, dateAmended,
                        application, settings, autoStart);
    }

    private Map<String, String> getParameters(int resourceId) throws SQLException, ClassNotFoundException {
        JDBCPreparedStatement ps2 = db.getStatement("buildShortcut.select.parameters");
        ps2.setInt(1, resourceId);
        Map<String,String> settings = new HashMap<String,String>();
        try {
            ResultSet rs2 = ps2.executeQuery();
            try {
                while (rs2.next()) {
                    settings.put(rs2.getString("parameter"), rs2.getString("value"));
                }

            } finally {
                rs2.close();
            }
        } finally {
            ps2.releasePreparedStatement();
        }
        return settings;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.plugin.PluginDatabase#open(net.openvpn.als.core.CoreServlet, net.openvpn.als.plugin.PluginDefinition)
     */
    public void open(CoreServlet controllingServlet, PluginDefinition def) throws Exception {
        String dbName = SystemProperties.get("openvpnals.systemDatabase.jdbc.dbName", "explorer_configuration");
        controllingServlet.addDatabase(dbName, ContextHolder.getContext().getDBDirectory());
        String jdbcUser = SystemProperties.get("openvpnals.jdbc.username", "sa");
        String jdbcPassword = SystemProperties.get("openvpnals.jdbc.password", "");
        String vendorDB = SystemProperties.get("openvpnals.jdbc.vendorClass", "net.openvpn.als.jdbc.hsqldb.HSQLDBDatabaseEngine");

        if (log.isInfoEnabled()) {
            log.info("System database is being opened...");
            log.info("JDBC vendor class implementation is " + vendorDB);
        }

        db = (JDBCDatabaseEngine) Class.forName(vendorDB).newInstance();
        db.init("applicationShortcutsDatabase", dbName, jdbcUser, jdbcPassword, null);

        File upgradeDir = new File(def.getDescriptor().getApplicationBundle().getBaseDir(), "upgrade");
        DBUpgrader upgrader = new DBUpgrader(ExtensionStore.getInstance()
            .getExtensionBundle(ApplicationsPlugin.BUNDLE_ID)
            .getVersion(), db, ContextHolder.getContext()
                        .getDBDirectory(), upgradeDir);
        upgrader.upgrade();

        CoreServlet.getServlet().addCoreListener(this);
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.applications.ApplicationShortcutDatabase#getShortcuts(int)
     */
    public List<ApplicationShortcut> getShortcuts(int realmID) throws Exception {

        JDBCPreparedStatement ps = db.getStatement("getShortcuts.realm.selectAll");
        Vector<ApplicationShortcut> v = new Vector<ApplicationShortcut>();
        try {
            ps.setInt(1, realmID);
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    v.add(buildShortcut(rs));
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
     * @see net.openvpn.als.applications.ApplicationShortcutDatabase#getShortcut(java.lang.String, int)
     */
    public ApplicationShortcut getShortcut(String name, int realmID) throws Exception {
        JDBCPreparedStatement ps = db.getStatement("getShortcutByName.select");
        ps.setString(1, name);
        ps.setInt(2, realmID);
        try {
            ResultSet rs = ps.executeQuery();
            try {
                if (rs.next()) {
                    return buildShortcut(rs);
                } else {
                    return null;
                }
            } finally {
                rs.close();
            }
        } finally {
            ps.releasePreparedStatement();
        }
    }
}
