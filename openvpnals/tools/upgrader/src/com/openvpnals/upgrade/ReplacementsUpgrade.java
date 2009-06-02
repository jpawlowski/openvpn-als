
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

public class ReplacementsUpgrade extends AbstractDatabaseUpgrade {

    ReplacementsUpgrade(File oldDbDir, File newDbDir) {
        super(
                        "Replacements",
                        "Copies replacement patterns used by the replacement " +
                        "proxy web forwards (previously known as Secure Proxy). " +
                        "All new replacements will be deleted and the ones from " + 
                        "your old installation will be copied across. Only " +
                        "global replacements will be copied. DO NOT SELECT " +
                        "THIS UPGRADE UNLESS YOU HAVE CREATED " +
                        "CUSTOM REPLACEMENTS AND WISH TO KEEP THEM. ",
                        false, "explorer_configuration", "explorer_configuration", oldDbDir, newDbDir);
    }

    public void doUpgrade(Upgrader upgrader, Connection oldConx, Connection newConx) throws Exception {
        // // Auth Schemes
        upgrader.info("Migrating all replacements");
        Statement stmt = oldConx.createStatement();
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM REPLACEMENTS");
            try {
                while (rs.next()) {
                    Statement stmt2 = newConx.createStatement();
                    try {
                        stmt2.execute("DELETE FROM REPLACEMENTS");
                    }
                    finally {
                        stmt2.close();
                    }

                    PreparedStatement ps = newConx.prepareStatement(
                        "INSERT INTO REPLACEMENTS (USERNAME,"
                                    + "SITE_PATTERN,MIME_TYPE,SEQUENCE,MATCH_PATTERN,REPLACE_PATTERN,REPLACE_TYPE) VALUES (?,?,?,?,?,?,?)");
                    try {
                        String username =rs.getString("USERNAME");
                        int seq = rs.getInt("SEQUENCE");
                        if(username.equals("")) {
                            ps.setString(1, username);
                            ps.setString(2, rs.getString("SITE_PATTERN"));
                            ps.setString(3, rs.getString("MIME_TYPE"));
                            ps.setInt(4, seq);
                            ps.setString(5, rs.getString("MATCH_PATTERN"));
                            ps.setString(6, rs.getString("REPLACE_PATTERN"));
                            ps.setInt(7, rs.getInt("REPLACE_TYPE"));
                            try {
                                ps.execute();
                            } catch (Exception e) {
                                upgrader.warn("Failed to insert IP restriction for sequence " + seq + ". Probably already exists.");
                            }                            
                        }
                        else {
                            upgrader.warn("Skipping user replacement for user '" + username + "'.");
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
