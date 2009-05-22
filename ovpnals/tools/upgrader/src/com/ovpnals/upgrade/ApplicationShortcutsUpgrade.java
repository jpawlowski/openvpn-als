
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
			
package com.ovpnals.upgrade;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ApplicationShortcutsUpgrade extends AbstractDatabaseUpgrade {

    ApplicationShortcutsUpgrade(File oldDbDir, File newDbDir) {
        super("Application Shortcuts", "Copies all web forwards to the new format. " +
            "Any user created shortcuts " +
            "are also copied (with the owner username appended to " +
            "the new resource name). The resource will not be attached to " +
            "any policies.", true, "explorer_configuration", "explorer_configuration", oldDbDir, newDbDir);
    }

    public void doUpgrade(Upgrader upgrader, Connection oldConx, Connection newConx) throws Exception {
        // // Auth Schemes
        upgrader.info("Migrating all application shortcuts");
        Statement stmt = oldConx.createStatement();
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM APPLICATION_SHORTCUTS");
            try {
                while (rs.next()) {
                    String shortName = "Application Shortcut " + rs.getString("SHORTCUT_ID");
                    String username = rs.getString("USERNAME");
                    shortName = shortName + " (" + (username.equals("") ? "System" : username) + ")";
                    PreparedStatement ps = newConx.prepareStatement("SELECT * FROM APPLICATION_SHORTCUTS WHERE NAME = ?");
                    boolean found = false;
                    try {
                        ps.setString(1, shortName);
                        ResultSet rs2 = ps.executeQuery();
                        try {
                            if (rs2.next()) {
                                found = true;
                            }
                        } finally {
                            rs2.close();
                        }
                    } finally {
                        ps.close();
                    }
                    if (found) {
                        upgrader.warn("    Application shortcut '" + shortName + "' already exists, skipping");
                    } else {
                        insertApplicationShortcut(upgrader, newConx, oldConx, rs, shortName);
                    }
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }

    }

    void insertApplicationShortcut(Upgrader upgrader, Connection newConx, Connection oldConx, ResultSet rs, String shortName) throws Exception {

        PreparedStatement ps = newConx.prepareStatement("INSERT INTO APPLICATION_SHORTCUTS (NAME,"
                        + "DESCRIPTION, APPLICATION, PARENT_RESOURCE_PERMISSION, "
                        + "DATE_CREATED, DATE_AMENDED) VALUES " + "(?,?,?,?,NOW(),NOW())");
        try {
            upgrader.info("    " + shortName);
            ps.setString(1, shortName);
            ps.setString(2, rs.getString("DESCRIPTION"));
            ps.setString(3, rs.getString("APPLICATION"));
            ps.setInt(4, 0);
            ps.execute();
            PreparedStatement ps2 = newConx.prepareStatement("SELECT RESOURCE_ID FROM APPLICATION_SHORTCUTS WHERE NAME = ?");
            try {
                ps2.setString(1, shortName);
                ResultSet rs2 = ps2.executeQuery();
                try {
                    if (rs2.next()) {
                        int oldResourceId = rs.getInt("SHORTCUT_ID");
                        int newResourceId = rs2.getInt("RESOURCE_ID");
                        updateParameters(upgrader, newConx, oldConx, oldResourceId, newResourceId);
                    } else {
                        throw new Exception("Failed to get new resource Id");
                    }
                } finally {
                    rs2.close();
                }
            } finally {
                ps2.close();
            }
        } catch (Exception e) {
            upgrader.warn("Failed to insert application shortcut " + shortName + ". Probably already exists.", e);
        } finally {
            ps.close();
        }
    }

    void updateParameters(Upgrader upgrader, Connection newConx, Connection oldConx, int oldResourceId, int newResourceId) throws Exception {
        PreparedStatement ps3 = oldConx.prepareStatement("SELECT * FROM APPLICATION_SHORTCUTS_PARAMETERS WHERE SHORTCUT_ID = ?");
        try {
            ps3.setInt(1, oldResourceId);
            ResultSet rs3 = ps3.executeQuery();
            try {
                while (rs3.next()) {
                    PreparedStatement ps4 = newConx.prepareStatement(
                        "INSERT INTO APPLICATION_SHORTCUTS_PARAMETERS " +
                        "(SHORTCUT_ID,PARAMETER, VALUE) VALUES(?,?,?)");
                    try {
                        ps4.setInt(1, newResourceId);
                        ps4.setString(2, rs3.getString("PARAMETER"));
                        ps4.setString(3, rs3.getString("VALUE"));
                        try {
                            ps4.execute();
                        } catch (Exception e) {
                            upgrader.warn("Failed to insert application shortcut parameters. Probably already exists.", e);
                        }
                    } finally {
                        ps4.close();
                    }
                }
            } finally {
                rs3.close();
            }
        } finally {
            ps3.close();
        }
    }
}
