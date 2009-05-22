
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
			
package com.ovpnals.applications.server;

import java.util.Hashtable;

public class ByteArrayClassLoader
    extends ClassLoader {

  static ByteArrayClassLoader instance;

  Hashtable classes = new Hashtable();
  ClassLoader parent;

  public ByteArrayClassLoader(ClassLoader parent) {
    this.parent = parent;
  }

  public Class createFromByteArray(String name, byte[] buf, int off, int len) {

    if(!classes.containsKey(name)) {
      classes.put(name, defineClass(name, buf, off, len));
    }
    return (Class) classes.get(name);

  }

  public Class loadClass(String name, boolean resolve) throws ClassNotFoundException {

    if(classes.containsKey(name)) {
      return (Class) classes.get(name);
    } else
      return parent.loadClass(name);
  }

  public static ByteArrayClassLoader getInstance() { return instance==null? instance = new ByteArrayClassLoader(ByteArrayClassLoader.class.getClassLoader()) : instance; }
}
