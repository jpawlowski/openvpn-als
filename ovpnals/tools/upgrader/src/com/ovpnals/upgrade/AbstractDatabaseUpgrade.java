
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
import java.sql.DriverManager;

public abstract class AbstractDatabaseUpgrade {
    
    protected String oldDbName;
    protected String newDbName;
    protected File oldDbDir;
    protected File newDbDir;
    protected String name;
    protected String description;
    protected boolean selectedByDefault;

    AbstractDatabaseUpgrade(String name, String description,  boolean selectedByDefault, String oldDbName, String newDbName, File oldDbDir, File newDbDir) {
        this.name = name;
        this.description = description;
        this.selectedByDefault = selectedByDefault;
        this.oldDbName = oldDbName;
        this.newDbName = newDbName;
        this.oldDbDir = oldDbDir;
        this.newDbDir = newDbDir;
    }
    
    
    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public boolean isSelectedByDefault() {
        return selectedByDefault;
    }
    
    public void upgrade(Upgrader upgrader) throws Exception {
        if (!new File(oldDbDir, oldDbName + ".data").exists()) {
            throw new Exception("Old " + oldDbName + " database does not appear to exist.");
        }
        if (!new File(newDbDir, newDbName + ".data").exists()) {
            throw new Exception(
                            "New " + newDbName + " database does not appear to exist, you must run the installation wizard on your new installation and select 'Built in' user database");
        }
        Class.forName("org.hsqldb.jdbcDriver");
        upgrader.info("Connecting to old " + oldDbName + " database");
        Connection oldConx = DriverManager.getConnection("jdbc:hsqldb:" + oldDbDir.getAbsolutePath() + File.separator
                        + oldDbName, "sa", "");
        oldConx.setAutoCommit(true);
        try {
            upgrader.info("Connecting to new " + newDbName + " database");
            Connection newConx = DriverManager.getConnection("jdbc:hsqldb:" + newDbDir.getAbsolutePath() + File.separator
                            + newDbName, "sa", "");
            newConx.setAutoCommit(true);
            try {
                doUpgrade(upgrader, oldConx, newConx);
            }
            finally {
                newConx.close();                
            }
        }
        finally {
            oldConx.close();
        }
        
    }
    
    public abstract void doUpgrade(Upgrader upgrader, Connection oldConx, Connection newConx) throws Exception;


}
