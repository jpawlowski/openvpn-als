
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
			
package net.openvpn.als.replacementproxy;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.cache.CachedObjectInfo;
import org.apache.commons.cache.FileStash;

/**
 * Extension to {@link org.apache.commons.cache.FileStash}that does some extra
 * stuff in the {@link #clear()}method.
 * 
 * @author Brett Smith
 */

public class ContentStash extends FileStash {

    public ContentStash() {
        super();
    }

    public ContentStash(long arg0) {
        super(arg0);
    }

    public ContentStash(long arg0, long arg1) {
        super(arg0, arg1);
    }

    public ContentStash(long arg0, long arg1, File arg2, int arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public ContentStash(long arg0, long arg1, File arg2, int arg3, boolean arg4) {
        super(arg0, arg1, arg2, arg3, arg4);
    }

    public ContentStash(long arg0, long arg1, File[] arg2, boolean arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public ContentStash(long arg0, long arg1, String arg2, int arg3) {
        super(arg0, arg1, arg2, arg3);
    }

    public ContentStash(long arg0, long arg1, String arg2, int arg3, boolean arg4) {
        super(arg0, arg1, arg2, arg3, arg4);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.cache.Stash#clear()
     */
    public synchronized void clear() {
        Iterator it = _hash.entrySet().iterator();
        while (it.hasNext()) {
            try {
                Map.Entry en = (Map.Entry) it.next();
                CachedObjectInfo obj = (CachedObjectInfo) en.getValue();
                ((File) (obj.getKey())).delete();
            } catch (Exception e) {
                // ignored
            }
        }
        _hash.clear();
        _curBytes = 0;
    }

}
