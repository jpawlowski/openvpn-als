
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.upgrade;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class NetworkPlacesUpgrade extends AbstractDatabaseUpgrade {

    NetworkPlacesUpgrade(File oldDbDir, File newDbDir) {
        super("Network Places", "Copies all network places to the new format. "
                        + "All are assumed to be SMB mounts. Any user created network "
                        + "places are also copied (with the owner username appended to "
                        + "the new resource name). The resource will not be attached to " + "any policies.", true,
                        "explorer_configuration", "explorer_configuration", oldDbDir, newDbDir);
    }

    public void doUpgrade(Upgrader upgrader, Connection oldConx, Connection newConx) throws Exception {
        // // Auth Schemes
        upgrader.info("Migrating all network places");
        Statement stmt = oldConx.createStatement();
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM NETWORK_PLACES");
            try {
                while (rs.next()) {
                    String shortName = rs.getString("SHORT_NAME");
                    String username = rs.getString("username");
                    String uri = rs.getString("uri");
                    if (uri.equals("/fs/cifs") || uri.equals("/fs/cifs/")) {
                        upgrader.warn("    Skipping network neighbourhood network place, no longer supported.");
                    } else {
                        shortName = shortName + " (" + (username.equals("") ? "System" : username) + ")";
                        PreparedStatement ps = newConx.prepareStatement("SELECT * FROM NETWORK_PLACES WHERE SHORT_NAME = ?");
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
                            upgrader.warn("    Network place '" + shortName + "' already exists, skipping");
                        } else {
                            insertNetworkPlace(upgrader, newConx, uri, shortName);
                        }
                    }
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }

    }

    void insertNetworkPlace(Upgrader upgrader, Connection newConx, String uri, String shortName) throws Exception {

        PreparedStatement ps = newConx.prepareStatement("INSERT INTO NETWORK_PLACES (SHORT_NAME,"
                        + "DESCRIPTION, PATH, READ_ONLY, ALLOW_RESURSIVE, "
                        + "NO_DELETE, SHOW_HIDDEN, PARENT_RESOURCE_PERMISSION, " + "DATE_CREATED, DATE_AMENDED,SCHEME) VALUES "
                        + "(?,?,?,?,?,?,?,?,NOW(),NOW(),?)");
        try {
            upgrader.info("    " + shortName);
            ps.setString(1, shortName);
            ps.setString(2, shortName);
            if (uri.equals("/fs/cifs")) {
                uri = "smb://";
            } else if (uri.startsWith("\\\\")) {
                uri = "smb:" + uri.replace('\\', '/');
            } else {
                uri = "smb:/" + uri.substring(8);
            }
            ps.setString(3, uri);
            ps.setBoolean(4, false);
            ps.setBoolean(5, true);
            ps.setBoolean(6, false);
            ps.setBoolean(7, true);
            ps.setInt(8, 0);
            ps.setString(9, "smb");
            try {
                ps.execute();
            } catch (Exception e) {
                upgrader.warn("Failed to insert network place " + shortName + ". Probably already exists.");
            }
        } finally {
            ps.close();
        }
    }

}
