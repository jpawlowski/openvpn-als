
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

public class WebForwardsUpgrade extends AbstractDatabaseUpgrade {

    WebForwardsUpgrade(File oldDbDir, File newDbDir) {
        super("Web Forwards", "Copies all web forwards to the new format. " +
            "All three types are migrated. Any user created web " +
            "forwards are also copied (with the owner username appended to " +
            "the new resource name). The resource will not be attached to " +
            "any policies.", true, "explorer_configuration", "explorer_configuration", oldDbDir, newDbDir);
    }

    public void doUpgrade(Upgrader upgrader, Connection oldConx, Connection newConx) throws Exception {
        // // Auth Schemes
        upgrader.info("Migrating all web forwards");
        Statement stmt = oldConx.createStatement();
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM WEBFORWARD");
            try {
                while (rs.next()) {
                    String shortName = rs.getString("SHORT_NAME");
                    String username = rs.getString("USERNAME");
                    shortName = shortName + " (" + (username.equals("") ? "System" : username) + ")";
                    PreparedStatement ps = newConx.prepareStatement("SELECT * FROM WEBFORWARD WHERE SHORT_NAME = ?");
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
                        insertWebForward(upgrader, newConx, oldConx, rs, shortName);
                    }
                }
            } finally {
                rs.close();
            }
        } finally {
            stmt.close();
        }

    }

    void insertWebForward(Upgrader upgrader, Connection newConx, Connection oldConx, ResultSet rs, String shortName) throws Exception {

        PreparedStatement ps = newConx.prepareStatement("INSERT INTO WEBFORWARD (DESTINATION_URL,"
                        + "TYPE, SHORT_NAME, DESCRIPTION, CATEGORY, " + "PARENT_RESOURCE_PERMISSION, "
                        + "DATE_CREATED, DATE_AMENDED) VALUES " + "(?,?,?,?,?,?,NOW(),NOW())");
        try {
            upgrader.info("    " + shortName);
            ps.setString(1, rs.getString("DESTINATION_URL"));
            int type = rs.getInt("TYPE");
            ps.setInt(2, type);
            ps.setString(3, shortName);
            String description = rs.getString("DESCRIPTION"); 
            ps.setString(4, description.equals("") ? shortName : description);
            ps.setString(5, rs.getString("CATEGORY"));
            ps.setInt(6, 0);
            ps.execute();
            // Tunneled, nothing additional
            if (type != 0) {
                PreparedStatement ps2 = newConx.prepareStatement("SELECT ID FROM WEBFORWARD WHERE SHORT_NAME = ?");
                try {
                    ps2.setString(1, shortName);
                    ResultSet rs2 = ps2.executeQuery();
                    try {
                        if (rs2.next()) {
                            int oldResourceId = rs.getInt("ID");
                            int newResourceId = rs2.getInt("ID");
                            if (type == 1) {
                                // replacement
                                updateReplacement(upgrader, newConx, oldConx, oldResourceId, newResourceId);
                            } else if (type == 2) {
                                // reverse
                                updateReverse(upgrader, newConx, oldConx, oldResourceId, newResourceId);
                            } else {
                                upgrader.warn("Invalid type " + type + ".");
                            }
                        } else {
                            throw new Exception("Failed to get new resource Id");
                        }
                    } finally {
                        rs2.close();
                    }
                } finally {
                    ps2.close();
                }
            }
        } catch (Exception e) {
            upgrader.warn("Failed to insert web forward " + shortName + ". Probably already exists.");
        } finally {
            ps.close();
        }
    }

    void updateReplacement(Upgrader upgrader, Connection newConx, Connection oldConx, int oldResourceId, int newResourceId) throws Exception {
        PreparedStatement ps3 = oldConx.prepareStatement("SELECT * FROM SECURE_PROXY_OPTIONS WHERE WEBFORWARD_ID = ?");
        try {
            ps3.setInt(1, oldResourceId);
            ResultSet rs3 = ps3.executeQuery();
            try {
                while (rs3.next()) {
                    PreparedStatement ps4 = newConx.prepareStatement(
                        "INSERT INTO SECURE_PROXY_OPTIONS (WEBFORWARD_ID," +
                        "ENCODING,AUTHENTICATION_USERNAME,AUTHENTICATION_PASSWORD," +
                        "PREFERRED_AUTHENTICATION_SCHEME,FORM_TYPE,FORM_PARAMETERS," +
                        "RESTRICT_TO_HOSTS) VALUES(?,?,?,?,?,?,?,?)");
                    try {
                        ps4.setInt(1, newResourceId);
                        String encoding = rs3.getString("ENCODING");
                        ps4.setString(2, encoding == null || encoding.equals("") ? "Default" : encoding);
                        ps4.setString(3, rs3.getString("AUTHENTICATION_USERNAME"));
                        ps4.setString(4, rs3.getString("AUTHENTICATION_PASSWORD"));
                        ps4.setString(5, rs3.getString("PREFERRED_AUTHENTICATION_SCHEME"));
                        ps4.setString(6, "NONE");
                        ps4.setString(7, "");
                        ps4.setString(8, "");
                        try {
                            ps4.execute();
                        } catch (Exception e) {
                            upgrader.warn("Failed to insert replacement proxy options. Probably already exists.");
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

    void updateReverse(Upgrader upgrader, Connection newConx, Connection oldConx, int oldResourceId, int newResourceId) throws Exception {
        PreparedStatement ps3 = oldConx.prepareStatement("SELECT * FROM REVERSE_PROXY_OPTIONS WHERE WEBFORWARD_ID = ?");
        try {
            ps3.setInt(1, oldResourceId);
            ResultSet rs3 = ps3.executeQuery();
            try {
                while (rs3.next()) {
                    PreparedStatement ps4 = newConx.prepareStatement(
                        "INSERT INTO REVERSE_PROXY_OPTIONS (WEBFORWARD_ID," +
                        "AUTHENTICATION_USERNAME,AUTHENTICATION_PASSWORD," +
                        "PREFERRED_AUTHENTICATION_SCHEME,ACTIVE_DNS,HOST_HEADER," +
                        "FORM_TYPE,FORM_PARAMETERS) VALUES(?,?,?,?,?,?,?,?)");
                    try {
                        ps4.setInt(1, newResourceId);
                        ps4.setString(2, rs3.getString("AUTHENTICATION_USERNAME"));
                        ps4.setString(3, rs3.getString("AUTHENTICATION_PASSWORD"));
                        ps4.setString(4, rs3.getString("PREFERRED_AUTHENTICATION_SCHEME"));
                        ps4.setInt(5, rs3.getInt("ACTIVE_DNS"));
                        ps4.setString(6, rs3.getString("HOST_HEADER"));
                        ps4.setString(7, "NONE");
                        ps4.setString(8, "");
                        try {
                            ps4.execute();
                        } catch (Exception e) {
                            upgrader.warn("Failed to insert replacement proxy options. Probably already exists.");
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
        upgrader.info("        Doing paths");
        ps3 = oldConx.prepareStatement("SELECT * FROM REVERSE_PROXY_PATHS WHERE WEBFORWARD_ID = ?");
        try {
            ps3.setInt(1, oldResourceId);
            ResultSet rs3 = ps3.executeQuery();
            try {
                while (rs3.next()) {
                    PreparedStatement ps4 = newConx.prepareStatement(
                        "INSERT INTO REVERSE_PROXY_PATHS (PATH,WEBFORWARD_ID) " +
                        "VALUES(?,?)");
                    try {
                        String path = rs3.getString("PATH");
                        upgrader.info("            [" + path + "]");
                        ps4.setString(1, path);
                        ps4.setInt(2, newResourceId);
                        try {
                            ps4.execute();
                        } catch (Exception e) {
                            upgrader.warn("Failed to insert replacement proxy path. Probably already exists.");
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
