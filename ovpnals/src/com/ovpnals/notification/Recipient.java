
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
			
package com.ovpnals.notification;

public class Recipient implements Comparable {
    public static final int EOF = 0;
    
    public static final int ADMINS = 1;

    public static final int USER = 2;

    public static final int ROLE = 3;

    public static final int POLICY = 4;
    
    private int recipientType;
    private String recipientAlias;
    private String realmName;
    
    /**
     * @param recipientType
     * @param recipientAlias
     * @param realmName 
     */
    public Recipient(int recipientType, String recipientAlias, String realmName) {
        this.recipientType = recipientType;
        this.recipientAlias = recipientAlias;
        this.realmName = realmName;
    }
    
    /**
     * @return Returns the recipientAlias.
     */
    public String getRecipientAlias() {
        return recipientAlias;
    }
    /**
     * @param recipientAlias The recipientAlias to set.
     */
    public void setRecipientAlias(String recipientAlias) {
        this.recipientAlias = recipientAlias;
    }
    /**
     * @return Returns the recipientType.
     */
    public int getRecipientType() {
        return recipientType;
    }
    /**
     * @param recipientType The recipientType to set.
     */
    public void setRecipientType(int recipientType) {
        this.recipientType = recipientType;
    }

    public int compareTo(Object arg0) {
        Recipient r = (Recipient)arg0;
        int c =  new Integer(recipientType).compareTo(new Integer(r.getRecipientType()));
        return c == 0 ? recipientAlias.compareTo(r.getRecipientAlias()) : c;
    }

    /**
     * @return String
     */
    public String getRealmName() {
        return realmName;
    }

    /**
     * @param realmName
     */
    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

}
