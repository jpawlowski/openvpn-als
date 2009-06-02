
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
			
package net.openvpn.als.core;

public class CookieItem {
    private String realCookieName;

    private String fakeCookieName;

    public CookieItem(String realCookieName, String fakeCookieName) {
        this.realCookieName = realCookieName;
        this.fakeCookieName = fakeCookieName;
    }

    /**
     * @return Returns the fakeCookieName.
     */
    public String getFakeCookieName() {
        return fakeCookieName;
    }

    /**
     * @param fakeCookieName The fakeCookieName to set.
     */
    public void setFakeCookieName(String fakeCookieName) {
        this.fakeCookieName = fakeCookieName;
    }

    /**
     * @return Returns the realCookieName.
     */
    public String getRealCookieName() {
        return realCookieName;
    }

    /**
     * @param realCookieName The realCookieName to set.
     */
    public void setRealCookieName(String realCookieName) {
        this.realCookieName = realCookieName;
    }

    public static String generateFakeCookieName(String host, int port, String realCookieName) {

        return Math.abs((host + ":" + port).hashCode()) + "_" + realCookieName;
    }
}