
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
			
package com.adito.networkplaces;

/**
 * Constants used for event attributes
 */
public class NetworkPlacesEventConstants {
    /**
     * VFS Uri 
     */
    public static final String EVENT_ATTR_VFS_URI = "VfsUri";
    
    /**
     * VFS Upload Source 
     */
    public static final String EVENT_ATTR_VFS_UPLOAD_DESTINATION_URI = "VfsUploadDestinationUri";

    /**
     * VFS Upload Source 
     */
    public static final String EVENT_ATTR_VFS_UPLOAD_DESTINATION_PATH = "VfsUploadDestinationPath";

    /**
     * VFS read only
     */
    public static final String EVENT_ATTR_VFS_READ_ONLY = "VfsReadOnly";

    /**
     * VFS show hidden
     */
    public static final String EVENT_ATTR_VFS_SHOW_HIDDEN = "VfsShowHidden";

    /**
     * VFS Allow recursive
     */
    public static final String EVENT_ATTR_VFS_ALLOW_RECURSIVE = "VfsAllowRecursive";

    /**
     * VFS no delete
     */
    public static final String EVENT_ATTR_VFS_NO_DELETE = "VfsNoDelete";

    /**
     * VFS path within the Uri 
     */
    public static final String EVENT_ATTR_VFS_PATH = "VfsPath";
    
    /**
     * VFS file name 
     */
    public static final String EVENT_ATTR_VFS_FILE_NAME = "VfsFileName";
    
    /**
     * VFS directory name 
     */
    public static final String EVENT_ATTR_VFS_DIRECTORY_NAME = "VfsDirectoryName";

    /**
     * VFS is cut or copy operation 
     */
    public static final String EVENT_ATTR_VFS_OPERATION = "VfsOperation";

    /**
     * vfs re-name old name
     */
    public static final String EVENT_ATTR_VFS_OLD_NAME = "VfsOldName";

    /**
     * vfs re-name new name
     */
    public static final String EVENT_ATTR_VFS_NEW_NAME = "VfsNewName";

    /**
     * vfs user agent
     */
    public static final String EVENT_ATTR_VFS_USER_AGENT = "VfsUserAgent";

    /**
     * A VFS directory has been listed
     */
    public static final int VFS_DIRECTORY_LISTED = 621;

    /**
     * A VFS directory has been created
     */
    public static final int VFS_DIRECTORY_CREATED = 622;

    /**
     * A VFS file has been removed
     */
    public static final int VFS_FILE_REMOVED = 623;

    /**
     * A VFS file, directory or zip has been downloaded
     */
    public static final int VFS_FILE_DOWNLOAD_STARTED = 624;

    /**
     * A VFS file, directory or zip has been downloaded
     */
    public static final int VFS_FILE_DOWNLOAD_COMPLETE = 625;

    /**
     * A VFS file or directory was copied
     */
    public static final int VFS_FILE_COPY = 626;

    /**
     * A VFS directory has been removed
     */
    public static final int VFS_REMOVE = 627;

    /**
     * A VFS upload file
     */
    public static final int VFS_UPLOAD_FILE = 628;

    /**
     * A VFS zip file
     */
    public static final int VFS_ZIP_DOWNLOAD = 629;

    /**
     * A VFS Paste operation
     */
    public static final int VFS_PASTE_OPERATION = 630;

    /**
     * A VFS re-name operation
     */
    public static final int VFS_RENAME = 631;

    /**
     * Network place created
     */
    public static final int CREATE_NETWORK_PLACE = 2020;

    /**
     * Network place updated
     */
    public static final int UPDATE_NETWORK_PLACE = 2019;

    /**
     * Network place deleted
     */
    public static final int DELETE_NETWORK_PLACE = 2021;
}
