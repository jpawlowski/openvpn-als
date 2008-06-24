
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
			
package com.adito.upgrade;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class TunnelsUpgrade extends AbstractDatabaseUpgrade {

    TunnelsUpgrade(File oldDbDir, File newDbDir) {
        super("SSL Tunnels", "Copies all SSL Tunnels to the new installation. "
                        + "Any user created tunnels are also copied (with the owner "
                        + "username appended to the new resource name). The resource " + "will not be attached to any policies.",
                        true, "explorer_configuration", "explorer_configuration", oldDbDir, newDbDir);
    }

    public void doUpgrade(Upgrader upgrader, Connection oldConx, Connection newConx) throws Exception {
        // // Auth Schemes
        upgrader.info("Migrating all SSL tunnels");
        Statement stmt = oldConx.createStatement();
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM TUNNELS");
            try {
                while (rs.next()) {
                    int tunnelId = rs.getInt("TUNNEL_ID");
                    PreparedStatement ps = newConx.prepareStatement("INSERT INTO TUNNELS (NAME,"
                                    + "DESCRIPTION, TYPE, TRANSPORT, USERNAME, SOURCE_PORT,"
                                    + " DESTINATION_PORT, DESTINATION_HOST, AUTO_START, "
                                    + "ALLOW_EXTERNAL_HOSTS, PARENT_RESOURCE_PERMISSION, "
                                    + "DATE_CREATED, DATE_AMENDED) VALUES (?,?,?,?,?,?,?,?,?,?,?,NOW(),NOW())");
                    try {
                        upgrader.info("    " + tunnelId);
                        String resourceName = "Tunnel ID " + tunnelId;
                        String username = rs.getString("USERNAME");
                        if (username.equals("")) {
                            resourceName += " (System)";
                        } else {
                            resourceName += " (" + username + ")";
                        }
                        ps.setString(1, resourceName);
                        ps.setString(2, resourceName);
                        ps.setInt(3, rs.getInt("TYPE"));
                        ps.setString(4, rs.getString("TRANSPORT"));
                        ps.setString(5, rs.getString("USERNAME"));
                        ps.setInt(6, rs.getInt("SOURCE_PORT"));
                        ps.setInt(7, rs.getInt("DESTINATION_PORT"));
                        ps.setString(8, rs.getString("DESTINATION_HOST"));
                        ps.setInt(9, rs.getInt("AUTO_START"));
                        ps.setInt(10, rs.getInt("ALLOW_EXTERNAL_HOSTS"));
                        ps.setInt(11, 0);
                        try {
                            ps.execute();
                        } catch (Exception e) {
                            upgrader.warn("Failed to insert tunnel " + tunnelId + ". Probably already exists.");
                        }
                    } finally {
                        ps.close();
                    }
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }

    }

}
