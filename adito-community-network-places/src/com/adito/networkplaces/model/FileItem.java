
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


public class FileItem extends FileSystemItem {

    public FileItem(LaunchSession launchSession, String fileName, long bytes, Calendar dateModified, String fileType, boolean selected, int idx) {
        super(launchSession, fileName, dateModified, fileType, selected, bytes, idx);
    }


    public String onClick(String path) {
        return URLUTF8Encoder.encode("/fs/" + path + "/" + this.getFileName(), false) + "?" + LaunchSession.LAUNCH_ID + "=" + getLaunchSession().getId() + "&ext=" + getFileName();
    }

    public String getWebFolderPath() {
        return "";
    }
}
