
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

public class AuthSchemeUpgrade extends AbstractDatabaseUpgrade {

    AuthSchemeUpgrade(File oldDbDir, File newDbDir) {
        super("Authentication Schemes", 
            "Copies authentication schemes and the modules they contain to the . "
                        + "new installation. Any schemes created will be enabled by default but not policies "
                        + "will be attached.", true, "explorer_configuration", "explorer_configuration", oldDbDir, newDbDir);
    }

    public void doUpgrade(Upgrader upgrader, Connection oldConx, Connection newConx) throws Exception {
        // // Auth Schemes
        upgrader.info("Migrating all authentication schemes");
        Statement stmt = oldConx.createStatement();
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM AUTH_SCHEMES");
            try {
                while (rs.next()) {
                    String authScheme = rs.getString("ID");
                    PreparedStatement ps = newConx.prepareStatement("SELECT * FROM AUTH_SCHEMES WHERE RESOURCE_NAME = ?");
                    boolean found = false;
                    try {
                        ps.setString(1, authScheme);
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
                        upgrader.warn("    Auth scheme '" + authScheme + "' already exists, skipping");
                    } else {
                        insertScheme(upgrader, newConx, oldConx, authScheme);
                    }
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }

    }

    private void insertScheme(Upgrader upgrader, Connection newConx, Connection oldConx, String authScheme) throws Exception {

        PreparedStatement ps = newConx.prepareStatement("INSERT INTO AUTH_SCHEMES (RESOURCE_NAME,"
                        + "RESOURCE_DESCRIPTION, DATE_CREATED,DATE_AMENDED,ENABLED) VALUES (?,?,NOW(),NOW(), 1)");
        try {
            upgrader.info("    " + authScheme);
            ps.setString(1, authScheme);
            ps.setString(2, authScheme);
            try {
                ps.execute();
            } catch (Exception e) {
                upgrader.warn("Failed to insert auth scheme " + authScheme + ". Probably already exists.");
            }
            PreparedStatement ps2 = newConx.prepareStatement("SELECT RESOURCE_ID FROM AUTH_SCHEMES WHERE RESOURCE_NAME = ?");
            try {
                ps2.setString(1, authScheme);
                ResultSet rs2 = ps2.executeQuery();
                try {
                    if (rs2.next()) {
                        PreparedStatement ps3 = oldConx.prepareStatement("SELECT * FROM AUTH_SEQUENCE WHERE ID = ?");
                        try {
                            ps3.setString(1, authScheme);
                            ResultSet rs3 = ps3.executeQuery();
                            try {
                                while (rs3.next()) {
                                    String moduleId = rs3.getString("MODULE_ID");
                                    upgrader.warn("        [" + moduleId + "]");
                                    PreparedStatement ps4 = newConx
                                                    .prepareStatement("INSERT INTO AUTH_SEQUENCE (SCHEME_ID,MODULE_ID,SEQUENCE) VALUES(?,?,?)");
                                    try {
                                        ps4.setInt(1, rs2.getInt("RESOURCE_ID"));
                                        ps4.setString(2, moduleId);
                                        ps4.setInt(3, rs3.getInt("SEQUENCE"));
                                        try {
                                            ps4.execute();
                                        } catch (Exception e) {
                                            upgrader.warn("Failed to insert authentication sequence. Probably already exists.");
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
                } finally {
                    rs2.close();
                }
            } finally {
                ps2.close();
            }
        } finally {
            ps.close();
        }

    }

}
