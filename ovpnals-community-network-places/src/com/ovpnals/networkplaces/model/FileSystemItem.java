
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
			
package com.ovpnals.networkplaces.model;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.vfs.FileType;

import com.ovpnals.policyframework.LaunchSession;
import com.ovpnals.table.TableItem;
import com.ovpnals.vfs.webdav.DAVUtilities;

/**
 * <p>
 * FileSystemItems are the entries in the file system view. Implements <@link
 * com.ovpnals.table.TableItem> and <@link java.lang.Comparable>.  
 */
public abstract class FileSystemItem implements TableItem, Comparable {

    private String fileName;
    private Calendar dateModified;
    private String fileType;
    private boolean checked;
    private String bytes;
    private long size;
    private boolean sortFoldersFirst;
    private boolean sortCaseSensitive;
    private int idx;
    private LaunchSession launchSession;

    /**
     * Constructor sets up class attributes.
     * 
     * @param launchSession the launch session that launched the view that contains this item
     * @param fileName The name of the file.
     * @param dateModified The date the file was last modified.
     * @param fileType The type of file.
     * @param checked weather the item is to be selected.
     * @param bytes The number of bytes in the file.
     * @param idx index of item
     */
    public FileSystemItem(LaunchSession launchSession, String fileName, Calendar dateModified, String fileType, boolean checked, long bytes, int idx) {
        this.fileName = fileName;
        this.dateModified = dateModified;
        this.fileType = fileType;
        this.checked = checked;
        this.bytes = formatSize(bytes);
        this.size = bytes;
        this.launchSession = launchSession;
        this.sortFoldersFirst = true;
        this.sortCaseSensitive = false;
        this.idx = idx;
    }
    
    /**
     * Get the launched session that launched the view that contains
     * this item.
     * 
     * @return resource session
     */
    public LaunchSession getLaunchSession() {
        return launchSession;
    }

    /**
     * @return Get the date the file was last modified.
     */
    public String getDateModified() {
        return (new SimpleDateFormat()).format(dateModified.getTime());
    }

    /**
     * @param dateModified Set the date the file was last modified.
     */
    public void setDateModified(Calendar dateModified) {
        this.dateModified = dateModified;
    }

    /**
     * @return Get the file name.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return Get the encoded file name.
     */
    public String getEncodedFileName() {
        return DAVUtilities.encodePath((getFileName()));
    }

    /**
     * @param fileName Set the file name.
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return Get the file type.
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * @param fileType Set the file type.
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    /**
     * @return Get the selected state.
     */
    public boolean getChecked() {
        return checked;
    }

    /**
     * @param checked Set the selected state.
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /* (non-Javadoc)
     * @see com.ovpnals.table.TableItem#getColumnValue(int)
     */
    public Object getColumnValue(int col) {
        switch (col) {
            case 0:
                return this;
            case 1:
                return dateModified;
            case 2:
                return new Long(size);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(Object arg0) {
        FileSystemItem fsi = (FileSystemItem) arg0;
        int lessThan = -1;
        int moreThan = 1;
        if (!sortFoldersFirst) {
            lessThan = 1;
            moreThan = -1;
        }
        if (fsi.getFileType().equals(FileType.FOLDER.getName()) && this.getFileType().equals(FileType.FILE.getName())) {
            return moreThan;
        } else if (fsi.getFileType().equals(FileType.FILE.getName()) && this.getFileType().equals(FileType.FOLDER.getName())) {
            return lessThan;
        } else {
            if (!sortCaseSensitive)
                return fileName.compareToIgnoreCase(fsi.getFileName());
            else
                return fileName.compareTo(fsi.getFileName());
        }
    }

    /**
     * @param path The current path to this location.
     * @return Return the String defining the path when clicked.
     */
    public abstract String onClick(String path);

    /**
     * @return The String to open a folder in web folder view.
     */
    public abstract String getWebFolderPath();

    /**
     * @return The Size of the file in bytes.
     */
    public String getBytes() {
        return bytes;
    }

    /**
     * @param bytes The Size of the file in bytes.
     */
    public void setBytes(long bytes) {
        this.bytes = formatSize(bytes);
    }

    /**
     * @param val Format the size to a specified number of decimal places.
     * @return The new formatted String.
     */
    private String formatSizeDPS(String val) {
        int position = 2 + 1;
        if (val.indexOf(".") + position < val.length()) {
            return val.substring(0, val.indexOf(".") + position);
        } else {
            return val;
        }
    }

    private String formatSize(long bytes) {

        NumberFormat formatMb = NumberFormat.getNumberInstance();
        NumberFormat formatGb = NumberFormat.getNumberInstance();
        NumberFormat formatKb = NumberFormat.getNumberInstance();

        if ((bytes / 1099511627776L) > 0) {
            // Were in the gigabytes
            return formatSizeDPS(formatGb.format((double) bytes / 1099511627776L)) + " GB";
        } else if ((bytes / 1048576) > 0) {
            // Were in the megabytes
            return formatSizeDPS(formatMb.format((double) bytes / 1048576)) + " MB";
        } else {
            // Were still in Kilobytes
            return formatSizeDPS(formatKb.format((double) bytes / 1024)) + " KB";
        }
    }

    public boolean isSortFoldersFirst() {
        return sortFoldersFirst;
    }

    public void setSortFoldersFirst(boolean sortFoldersFirst) {
        this.sortFoldersFirst = sortFoldersFirst;
    }

    public boolean isSortCaseSensitive() {
        return sortCaseSensitive;
    }

    public void setSortCaseSensitive(boolean sortCaseSensitive) {
        this.sortCaseSensitive = sortCaseSensitive;
    }
    
    /**
     * Return a string representation of this file system item. This is
     * used by the generic filter.
     * 
     * @return string representation of file system item
     */
    public String toString() {
        return getFileName();
    }
    
    public int getIdx() {
        return idx;
    }

}
