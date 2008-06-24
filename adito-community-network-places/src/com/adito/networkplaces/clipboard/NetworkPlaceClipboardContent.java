
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
			
package com.adito.networkplaces.clipboard;

import com.adito.vfs.clipboard.ClipboardContent;


public class NetworkPlaceClipboardContent implements ClipboardContent {

	private boolean deleteOnPaste = false;
	private String davPath = null;

	public NetworkPlaceClipboardContent(String davPath) {
		this.davPath = davPath;
	}

	public NetworkPlaceClipboardContent(String davPath, boolean deleteOnPaste) {
		this.davPath = davPath;
		this.deleteOnPaste = deleteOnPaste;
	}
	
	public String getPath(){
		return this.davPath;
	}

	public boolean deleteOnPaste() {
		return this.deleteOnPaste;
	}
    
    public String toString() {
        return davPath;
    }

}
