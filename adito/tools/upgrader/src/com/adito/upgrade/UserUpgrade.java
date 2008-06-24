
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

public class UserUpgrade extends AbstractDatabaseUpgrade {
    
    UserUpgrade(File oldDbDir, File newDbDir) {
        super("Users and Roles", "Copies users and roles created " +
                "when the Built-in User Database is in use.", true, 
                "explorer_accounts",
                "explorer_configuration", oldDbDir, newDbDir);
    }

    public void doUpgrade(Upgrader upgrader, Connection oldConx, Connection newConx) throws Exception {
//      Roles
        upgrader.info("Migrating all roles");
        Statement stmt = oldConx.createStatement();
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM ROLES");
            try {
                while (rs.next()) {
                    PreparedStatement ps = newConx.prepareStatement("INSERT INTO ROLES (ROLENAME) VALUES (?)");
                    try {
                        String rolename = rs.getString("ROLENAME");
                        upgrader.info("    " + rolename);
                        ps.setString(1, rolename);
                        try {
                            ps.execute();
                        } catch (Exception e) {
                            upgrader.warn("Failed to insert role " + rolename + ". Probably already exists.");
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
        // Users
        upgrader.info("Migrating all users");
        stmt = oldConx.createStatement();
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM USERS");
            try {
                while (rs.next()) {
                    String username = rs.getString("USERNAME");
                    upgrader.info("    " + username);
                    PreparedStatement ps = newConx.prepareStatement("INSERT INTO USERS (USERNAME, EMAIL, FULLNAME"
                                    + ", LAST_PASSWORD_CHANGE, PASSWORD) VALUES (?,?,?,?,?)");
                    try {
                        ps.setString(1, username);
                        ps.setString(2, rs.getString("EMAIL"));
                        ps.setString(3, rs.getString("FULLNAME"));
                        ps.setString(4, rs.getString("LAST_PASSWORD_CHANGE"));
                        ps.setString(5, rs.getString("PASSWORD"));
                        try {
                            ps.execute();
                        } catch (Exception e) {
                            upgrader.warn("Failed to insert user " + username + ". Probably already exists.");
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
        // User roles
        upgrader.info("Migrating all user roles");
        stmt = oldConx.createStatement();
        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM USER_ROLES");
            try {
                while (rs.next()) {
                    String username = rs.getString("USERNAME");
                    String rolename = rs.getString("ROLENAME");
                    upgrader.info("    " + username + "/" + rolename);
                    PreparedStatement ps = newConx
                                    .prepareStatement("INSERT INTO USER_ROLES (USERNAME, ROLENAME) VALUES (?," + "?)");
                    try {
                        ps.setString(1, username);
                        ps.setString(2, rolename);
                        try {
                            ps.execute();
                        } catch (Exception e) {
                            upgrader.warn("Failed to insert user role " + username + "/" + rolename
                                            + ".Probably already exists.");
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
