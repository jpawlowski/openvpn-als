
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
			
package com.maverick.ssl;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
class SSLCipherSuiteID {
    SSLCipherSuiteID(int id1, int id2) {
        this.id1 = id1;
        this.id2 = id2;
    }

    int id1;
    int id2;

    public boolean equals(Object obj) {
        if (obj instanceof SSLCipherSuiteID) {
            SSLCipherSuiteID s = (SSLCipherSuiteID) obj;
            return (s.id1 == id1 && s.id2 == id2);
        }

        return false;
    }
}
