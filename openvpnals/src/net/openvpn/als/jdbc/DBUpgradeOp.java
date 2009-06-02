
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
			
package net.openvpn.als.jdbc;

import java.io.File;

import net.openvpn.als.boot.VersionInfo;

/**
 * Represents a single SQL file to use to update a database to a specific 
 * version.
 */
public class DBUpgradeOp implements Comparable {
    private File file;
    private VersionInfo.Version version;
    private String name;
    
    DBUpgradeOp(File file) throws IllegalArgumentException {
        this.file = file;
        String n = file.getName();
        int idx = n.indexOf('-');
        if (idx == -1) {
          throw new IllegalArgumentException("File name not in correct format.");
        }
        String ver = n.substring(0, idx);
        version = new VersionInfo.Version(ver.replace('_', '.'));
        name = n.substring(idx + 1);
        if(!name.toLowerCase().endsWith(".sql")) {
            throw new IllegalArgumentException("File name not in correct format.");
        }
        name = name.substring(0, name.length() - 3);
    }
    
    /**
     * Get the file to use for this upgrade
     * 
     * @return upgrade file
     */
    public File getFile() {
        return file;
    }
    
    /**
     * Get the version that this upgrade will take the database schema to
     * 
     * @return version
     */
    public VersionInfo.Version getVersion() {
        return version;
    }
    
    /**
     * Get the database name to upgrade
     * 
     * @return database name to upgrade
     */
    public String getName() {
        return name;
    }
    
    /**
     * Compare two upgrade operations based on the name and version.
     * 
     * @param o other version
     * @return comparison
     */
    public int compareTo(Object o) {
        int i = getName().compareTo(((DBUpgradeOp)o).getName());
        return i == 0 ? getVersion().compareTo((((DBUpgradeOp)o).getVersion())) : i;
    }
    
}