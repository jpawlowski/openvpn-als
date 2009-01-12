
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
			
package com.adito.networkplaces.model;

import java.util.Calendar;

import com.maverick.util.URLUTF8Encoder;
import com.adito.policyframework.LaunchSession;
import com.adito.vfs.webdav.DAVUtilities;

public class FolderItem extends FileSystemItem {

    private String folderPath;
    private String storeName;

    public FolderItem(LaunchSession launchSession, String fileName, String storeName, String folderPath, Calendar dateModified, String fileType, boolean selected, int idx) {
        super(launchSession, fileName, dateModified, fileType, selected, 0, idx);
        this.folderPath = folderPath;
        this.storeName = storeName;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String onClick(String path) {
    	if (getFileName().equals("../")){
            return "/fileSystem.do?" + LaunchSession.LAUNCH_ID + "=" + getLaunchSession().getId() + "&actionTarget=list&startRow=0&path=" + URLUTF8Encoder.encode(this.folderPath, false);
    	}
    	else{
            return "/fileSystem.do?" + LaunchSession.LAUNCH_ID + "=" + getLaunchSession().getId() + "&actionTarget=list&startRow=0&path=" + URLUTF8Encoder.encode(DAVUtilities.concatenatePaths(folderPath, getFileName()), false);
    	}
    }

    public String getWebFolderPath() {
        return "/fs/" + folderPath + "/" + getFileName();
    }
}
