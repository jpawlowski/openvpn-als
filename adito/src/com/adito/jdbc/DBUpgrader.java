
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.ContextHolder;
import com.adito.boot.Util;
import com.adito.boot.VersionInfo;

/**
 * Checks database schemas to see if they are up to date, running the database
 * upgrade scripts if they are not.
 * <p>
 * Current database versions are stored using the Java Preferences API.
 * <p>
 * TODO This class currently assumes that HSQLDB is in use (to check for the
 * existance of databases), although it should be relatively to adapt it for use
 * with other database implementations.
 */
public class DBUpgrader {

    final static Log log = LogFactory.getLog(DBUpgrader.class);

    //
    private static HashMap removed = new HashMap();
    private static HashMap removeProcessed = new HashMap();

    // Private instance variables

    private JDBCDatabaseEngine engine;
    private File dbDir;
    private VersionInfo.Version newDbVersion;
    private File upgradeDir;
    private File versionsFile;
    private boolean useDbNameForVersionCheck;

    /**
     * Constructor
     * 
     * @param newDbVersion the new version of the database
     * @param engine the database engine to use to execute the commands
     * @param dbDir directory containing databases
     * @param upgradeDir directory containing upgrade script
     */
    public DBUpgrader(VersionInfo.Version newDbVersion, JDBCDatabaseEngine engine, File dbDir, File upgradeDir) {
        this(null, newDbVersion, engine, dbDir, upgradeDir);
    }

    /**
     * Constructor
     * 
     * @param versionsFile file to store database version
     * @param newDbVersion the new version of the database
     * @param engine the database engine to use to execute the commands
     * @param dbDir directory containing databases
     * @param upgradeDir directory containing upgrade script
     */
    public DBUpgrader(File versionsFile, VersionInfo.Version newDbVersion, JDBCDatabaseEngine engine, File dbDir, File upgradeDir) {
        this(versionsFile, newDbVersion, engine, dbDir, upgradeDir, false);
    }

    /**
     * Constructor
     * 
     * @param versionsFile file to store database version
     * @param newDbVersion the new version of the database
     * @param engine the database engine to use to execute the commands
     * @param dbDir directory containing databases
     * @param upgradeDir directory containing upgrade script
     * @param useDbNameForVersionCheck use database name for version check
     *        instead of alias
     */
    public DBUpgrader(File versionsFile, VersionInfo.Version newDbVersion, JDBCDatabaseEngine engine, File dbDir, File upgradeDir,
                      boolean useDbNameForVersionCheck) {
        super();
        this.versionsFile = versionsFile;
        this.upgradeDir = upgradeDir;
        this.engine = engine;
        this.dbDir = dbDir;
        this.newDbVersion = newDbVersion;
        this.useDbNameForVersionCheck = useDbNameForVersionCheck;
    }

    /**
     * Check the database schema and perform any upgrades.
     * 
     * @throws Exception on any error
     */
    public void upgrade() throws Exception {
        Properties versions = null;
        if (versionsFile == null) {
            /* If required, convert from the old preferences node to the new
             * file (version 0.2.5)
             */
            versionsFile = new File(ContextHolder.getContext().getDBDirectory(), "versions.log");
            Preferences p = ContextHolder.getContext().getPreferences().node("dbupgrader");
            if (p.nodeExists("currentDataVersion")) {
                log.warn("Migrating database versions from preferences to properties file in "
                                + ContextHolder.getContext().getDBDirectory().getAbsolutePath() + ".");
                versions = new Properties();
                p = p.node("currentDataVersion");
                String[] c = p.keys();
                for (int i = 0; i < c.length; i++) {
                    versions.put(c[i], p.get(c[i], ""));
                }
                FileOutputStream fos = new FileOutputStream(versionsFile);
                try {
                    versions.store(fos, "Database versions");
                } finally {
                    Util.closeStream(fos);
                }
                p.removeNode();
            }
        }

        // Load the database versions
        if (versions == null) {
            versions = new Properties();
            if (versionsFile.exists()) {
                FileInputStream fin = new FileInputStream(versionsFile);
                try {
                    versions.load(fin);
                } finally {
                    Util.closeStream(fin);
                }
            }
        }

        try {
            String dbCheckName = useDbNameForVersionCheck ? engine.getDatabase() : engine.getAlias();

            if ((!engine.isDatabaseExists() || removed.containsKey(engine.getDatabase()))
                            && !removeProcessed.containsKey(dbCheckName)) {
                versions.remove(dbCheckName);
                removeProcessed.put(dbCheckName, Boolean.TRUE);
                if (log.isInfoEnabled())
                    log.info("Database for " + dbCheckName + " (" + engine.getDatabase()
                                    + ") has been removed, assuming this is a re-install.");
                removed.put(engine.getDatabase(), Boolean.TRUE);
            }

            // Check for any SQL scripts to run to bring the databases up to
            // date
            VersionInfo.Version currentDataVersion = new VersionInfo.Version(versions.getProperty(dbCheckName, "0.0.0"));
            if (log.isInfoEnabled()) {
                log.info("New logical database version for " + engine.getAlias() + " is " + newDbVersion);
                log.info("Current logical database version for " + engine.getAlias() + " is " + currentDataVersion);
                //
                log.info("Upgrade script directory is " + upgradeDir.getAbsolutePath());
            }
            List upgrades = getSortedUpgrades(upgradeDir);
            File oldLog = new File(upgradeDir, "upgrade.log");
            if (!dbDir.exists()) {
                if (!dbDir.mkdirs()) {
                    throw new Exception("Failed to create database directory " + dbDir.getAbsolutePath());
                }
            }
            File logFile = new File(dbDir, "upgrade.log");
            if (oldLog.exists()) {
                if (log.isInfoEnabled())
                    log.info("Moving upgrade.log to new location (as of version 0.1.5 it resides in the db directory.");
                if (!oldLog.renameTo(logFile)) {
                    throw new Exception("Failed to move upgrade log file from " + oldLog.getAbsolutePath() + " to "
                                    + logFile.getAbsolutePath());
                }
            }
            HashMap completedUpgrades = new HashMap();
            if (!logFile.exists()) {
                OutputStream out = null;
                try {
                    out = new FileOutputStream(logFile);
                    PrintWriter writer = new PrintWriter(out, true);
                    writer.println("# This file contains a list of database upgrades");
                    writer.println("# that have completed correctly.");
                } finally {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                }
            } else {
                InputStream in = null;
                try {
                    in = new FileInputStream(logFile);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (!line.equals("") && !line.startsWith("#")) {
                            completedUpgrades.put(line, line);
                        }
                    }
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }
            OutputStream out = null;
            try {
                out = new FileOutputStream(logFile, true);
                PrintWriter writer = new PrintWriter(out, true);
                Class.forName("org.hsqldb.jdbcDriver"); // shouldnt be needed,
                // but
                // just in
                // case
                for (Iterator i = upgrades.iterator(); i.hasNext();) {
                    DBUpgradeOp upgrade = (DBUpgradeOp) i.next();
                    boolean runBefore = completedUpgrades.containsKey(upgrade.getFile().getName());
                    if (log.isInfoEnabled())
                        log.info("Checking if upgrade " + upgrade.getFile() + " [" + upgrade.getVersion()
                                        + "] needs to be run. Run before = " + runBefore + ". Current data version = "
                                        + currentDataVersion + ", upgrade version = " + upgrade.getVersion());
                    if ((!runBefore || (currentDataVersion.getMajor() == 0 && currentDataVersion.getMinor() == 0 && currentDataVersion
                                    .getBuild() == 0))
                                    && upgrade.getVersion().compareTo(currentDataVersion) >= 0
                                    && upgrade.getVersion().compareTo(newDbVersion) < 0) {
                        if (log.isInfoEnabled())
                            log.info("Running script " + upgrade.getName() + " [" + upgrade.getVersion() + "] on database "
                                            + engine.getDatabase());

                        // Get a JDBC connection
                        JDBCConnectionImpl conx = engine.aquireConnection();
                        try {
                            runSQLScript(conx, upgrade.getFile());
                            completedUpgrades.put(upgrade.getFile().getName(), upgrade.getFile().getName());
                            writer.println(upgrade.getFile().getName());
                        } finally {
                            engine.releaseConnection(conx);
                        }
                    }
                }
                versions.put(dbCheckName, newDbVersion.toString());

                if (log.isInfoEnabled())
                    log.info("Logical database " + engine.getAlias() + " (" + engine.getDatabase() + ") is now at version "
                                    + newDbVersion);
            } finally {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            }
        } finally {
            FileOutputStream fos = new FileOutputStream(versionsFile);
            try {
                versions.store(fos, "Database versions");
            } finally {
                Util.closeStream(fos);
            }
        }
    }

    private List getSortedUpgrades(File upgradeDir) {
        List sortedUpgrades = new ArrayList();
        File[] files = upgradeDir.listFiles();
        for (int i = 0; files != null && i < files.length; i++) {
            String n = files[i].getName();
            if (n.endsWith(engine.getAlias() + ".sql")) {
                sortedUpgrades.add(new DBUpgradeOp(files[i]));
            } else {
                if (log.isDebugEnabled())
                    log.debug("Skipping script " + n);
            }

        }
        Collections.sort(sortedUpgrades);
        return sortedUpgrades;
    }

    private void runSQLScript(JDBCConnectionImpl con, File sqlFile) throws SQLException, IllegalStateException, IOException {

        InputStream in = null;
        try {
            in = new FileInputStream(sqlFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            StringBuffer cmdBuffer = new StringBuffer();
            boolean quoted = false;
            char ch;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.equals("") && !line.startsWith("//") && !line.startsWith("--") && !line.startsWith("#")) {
                    quoted = false;
                    for (int i = 0; i < line.length(); i++) {
                        ch = line.charAt(i);
                        if (ch == '\'') {
                            if (quoted) {
                                if ((i + 1) < line.length() && line.charAt(i + 1) == '\'') {
                                    i++;
                                    cmdBuffer.append(ch);
                                } else {
                                    quoted = false;
                                }
                            } else {
                                quoted = true;
                            }
                            cmdBuffer.append(ch);
                        } else if (ch == ';' && !quoted) {
                            if (cmdBuffer.length() > 0) {
                                executeSQLStatement(con, cmdBuffer.toString());
                                cmdBuffer.setLength(0);
                            }
                        } else {
                            if (i == 0 && ch != ' ' && cmdBuffer.length() > 0 && !quoted) {
                                cmdBuffer.append(' ');
                            }
                            cmdBuffer.append(ch);
                        }
                    }
                }
            }
            if (cmdBuffer.length() > 0) {
                executeSQLStatement(con, cmdBuffer.toString());
                cmdBuffer.setLength(0);
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private void executeSQLStatement(JDBCConnectionImpl con, String cmd) throws SQLException {
        /*
         * A hack to get around the problem of moving the HSQLDB stored
         * procedures. Prior to version 0.1.13, these functions existed in the
         * class com.adito.DBFunctions. At version 0.1.13, this were moved
         * to com.adito.server.hsqldb.DBFunctions. This meant that on a
         * fresh install, when the original 'CREATE ALIAS' statement is
         * encountered it can no longer find the class. The 'CREATE ALIAS' in
         * the 0.1.13 upgrade scripts have the correct classname so upgrades are
         * not affected by this.
         * 
         * This then happend *AGAIN* for 0.2.0.
         * 
         * TODO remove this code when we clear out all the database upgrade
         * scripts and start again
         * 
         */
        if (cmd.startsWith("CREATE ALIAS ")) {
            int idx = cmd.indexOf("com.adito.DBFunctions.");
            if (idx != -1) {
                cmd = cmd.substring(0, idx) + "com.adito.server.hsqldb.DBFunctions." + cmd.substring(idx + 28);
            }
            idx = cmd.indexOf("com.adito.server.hsqldb.DBFunctions.");
            if (idx != -1) {
                cmd = cmd.substring(0, idx) + "com.adito.jdbc.hsqldb.DBFunctions." + cmd.substring(idx + 42);
            }

        }

        if (log.isDebugEnabled())
            log.debug("Executing \"" + cmd + "\"");
        con.execute(cmd);
    }

}
