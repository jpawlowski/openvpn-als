
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
			
package net.openvpn.als.boot;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * An map that stores and retrieves entries in a case insensitive way. For 
 * example, a value is placed in the map with a key of <b>FOObar</b>. The
 * value may then be retrieved later using <b>fooBAR</b>.
 * 
 */
public class CaseInsensitiveMap extends TreeMap {

    private static final long serialVersionUID = -963451042992843686L;

    /**
     * Constructor
     */
    public CaseInsensitiveMap() {
        super(new InsensitiveComparator());
    }
    
    static class InsensitiveComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            String s1 = (String)o1;
            String s2 = (String)o2;
            return s1.toUpperCase().compareTo(s2.toUpperCase());
          }                                    
          public boolean equals(Object o) {
            return compare(this, o)==0;
          }
        }

}
