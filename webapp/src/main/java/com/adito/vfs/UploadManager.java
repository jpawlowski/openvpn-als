
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
			
package com.adito.vfs;

import java.util.HashMap;
import java.util.Map;

import com.adito.core.CoreUtil;
import com.adito.vfs.actions.ShowUploadAction;


/**
 * 
 * Maintains a list of {@link UploadDetails} ands allows the upload system
 * to access them via their id.
 * <p>
 * In general, a user would request an upload. The controlling action would
 * then create a new upload details object and add it to their sessions
 * upload manager. Control would then be passed to {@link ShowUploadAction}
 * given it the upload id. 
 * <p>
 * In general, you would never need to create an instance of this class
 * yourself and should instead use {@link CoreUtil#addUpload(javax.servlet.http.HttpSession, UploadDetails)}.
 */
public class UploadManager {

    // Private instance variables
    private Map uploads = new HashMap();
    private int id = 0;
    
    /**
     * Add a new upload and return its id.
     * 
     * @param upload upload
     * @return id
     */
    public synchronized int addUpload(UploadDetails upload) {
        id++;
        uploads.put(new Integer(id), upload);
        return id;
    }
    
    /**
     * Remove an upload given its id
     * 
     * @param id id
     * @return removed details
     */
    public UploadDetails removeUpload(int id) {
        return (UploadDetails)uploads.remove(new Integer(id));
    }
    
    /**
     * Get an upload given its id. <code>null</code> will be returned if no
     * such upload exists.
     * 
     * @param id id
     * @return upload
     */
    public UploadDetails getUpload(int id) {
        return (UploadDetails)uploads.get(new Integer(id));
    }

    /**
     * Get if there are no uploads being managed.
     * 
     * @return empty
     */
    public boolean isEmpty() {
        return uploads.size() == 0;
    }

}
