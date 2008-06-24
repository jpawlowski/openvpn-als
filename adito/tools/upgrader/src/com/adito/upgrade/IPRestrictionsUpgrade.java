
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

public class IPRestrictionsUpgrade extends AbstractDatabaseUpgrade {

    IPRestrictionsUpgrade(File oldDbDir, File newDbDir) {
        super("IP Restrictions", "Copies IP restrictions to the new installation.", true, "explorer_configuration",
                        "explorer_configuration", oldDbDir, newDbDir);
    }

    public void doUpgrade(Upgrader upgrader, Connection oldConx, Connection newConx) throws Exception {
        // // Auth Schemes
        upgrader.info("Migrating all IP restrictions");
        Statement stmt = oldConx.createStatement();
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM IP_RESTRICTIONS");
            try {
                while (rs.next()) {
                    int restrictionId = rs.getInt("RESTRICTION_ID");
                    PreparedStatement ps = newConx.prepareStatement("INSERT INTO IP_RESTRICTIONS (RESTRICTION_ID,"
                                    + "ADDRESS, TYPE) VALUES (?,?,?)");
                    try {
                        upgrader.info("    " + restrictionId);
                        ps.setInt(1, restrictionId);
                        ps.setString(2, rs.getString("ADDRESS"));
                        ps.setInt(3, rs.getInt("TYPE"));
                        try {
                            ps.execute();
                        } catch (Exception e) {
                            upgrader.warn("Failed to insert IP restriction " + restrictionId + ". Probably already exists.");
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
