
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
			
package com.ovpnals.unixauth;

import java.util.Date;

import com.ovpnals.realms.Realm;
import com.ovpnals.security.DefaultUser;
import com.ovpnals.security.Role;

/**
 * Implementation of a {@link com.ovpnals.security.DefaultUser}
 * for <i>Unix users</i>.
 */
public class UNIXUser extends DefaultUser {

    private char[] password;
    private String home;
    private String shell;
    private int uid;
    private int gid;

    /**
     * @param username
     * @param email
     * @param password
     * @param uid
     * @param gid
     * @param fullname
     * @param home
     * @param shell
     * @param roles
     * @param realm 
     */
    public UNIXUser(String username, String email, char[] password, int uid, int gid, String fullname, String home, String shell, Role[] roles, Realm realm) {
        super(username, email, fullname, new Date(), realm);
        setRoles(roles);
        this.uid = uid;
        this.password = password;
        this.gid = gid;
        this.home = home;
        this.shell = shell;
    }

    /**
     * @return int
     */
    public int getGid() {
        return gid;
    }

    /**
     * @param gid
     */
    public void setGid(int gid) {
        this.gid = gid;
    }

    /**
     * @return String
     */
    public String getHome() {
        return home;
    }

    /**
     * @param home
     */
    public void setHome(String home) {
        this.home = home;
    }

    /**
     * @return char[]
     */
    public char[] getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(char[] password) {
        this.password = password;
    }

    /**
     * @return String
     */
    public String getShell() {
        return shell;
    }

    /**
     * @param shell
     */
    public void setShell(String shell) {
        this.shell = shell;
    }

    /**
     * @return int
     */
    public int getUid() {
        return uid;
    }

    /**
     * @param uid
     */
    public void setUid(int uid) {
        this.uid = uid;
    }


}