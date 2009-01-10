
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
			
package com.adito.applications.server;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Simple class that will track files and remove them when either told to do
 * or upon finalization.
 */
public class ClientCacheRemover {

  static ClientCacheRemover instance;

  Vector files = new Vector();

  public static ClientCacheRemover getInstance() {
    if(instance==null)
      instance = new ClientCacheRemover();

    return instance;
  }

  public void trackFileForRemoval(File f) {
    files.addElement(f);
  }

  public void cleanup() {
    for(Enumeration e = files.elements(); e.hasMoreElements();){
      ((File)e.nextElement()).delete();
    }

    files.removeAllElements();
  }

  protected void finalize() {
    cleanup();
  }
}
