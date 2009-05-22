
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
			
package com.ovpnals.server;

import com.ovpnals.boot.SystemProperties;

/**
 * <p>A simple utility to output the value of a system property specified on the
 * command line.
 * 
 * <p>This is used by the installer and service scripts to determine the location
 * of the running JVM.</p> 
 */
public class GetSystemProperty {
    
    /**
     * Expects a single argument that is the name of a Java system property.
     * The value of this property is then simply outputted on stdout.
     * 
     * @param args supply a single argument containing system property name
     */
  	public static void main(String[] args) {
  	  System.out.println(SystemProperties.get(args[0]));
  	}
}
