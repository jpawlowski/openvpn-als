
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
			
package com.ovpnals.vfs;

/**
 *
 */
public final class VFSFileLock implements Comparable<VFSFileLock> {
    
    private final String fileName;
    private final String fileURI;
    private final boolean active;
    private final String handle;
    
    /**
     * Constructor
     * @param fileName
     * @param fileURI
     * @param active
     * @param handle
     */
    public VFSFileLock(String fileName, String fileURI, boolean active, String handle) {
        this.fileName = fileName;
        this.fileURI = fileURI;
        this.active = active;
        this.handle = handle;
    }
    
    /**
     * @return fileName
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * @return fileURI
     */
    public String getFileURI() {
        return fileURI;
    }

    /**
     * @return isActive
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @return handle
     */
    public String getHandle() {
        return handle;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(VFSFileLock o) {
        return getFileName().compareTo(o.getFileName());
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("['");
        buffer.append("fileName=").append(getFileName()).append("', ");
        buffer.append("fileURI=").append(getFileURI()).append("', ");
        buffer.append("active='").append(isActive()).append("', ");
        buffer.append("handle='").append(getHandle()).append("'");
        buffer.append("]");
        return buffer.toString();
    }
}