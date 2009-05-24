
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
			
package com.ovpnals.activedirectory;

/**
 * This class encapsulates the group type behaviour.
 * For more information http://msdn2.microsoft.com/en-us/library/ms675935.aspx
 */
public final class ActiveDirectoryGroupTypes {
    /** Specifies a group that is created by the system. */
    public static final int SYSTEM_GROUP = 0x00000001;
    /** Specifies a group with global scope. */
    public static final int GROUP_WITH_GLOBAL_SCOPE = 0x00000002;
    /** Specifies a group with domain local scope. */
    public static final int GROUP_WITH_DOMAIN_LOCAL_SCOPE = 0x00000004; 
    /** Specifies a group with universal scope. */
    public static final int GROUP_WITH_UNIVERSAL_SCOPE = 0x00000008;
    /** Specifies an APP_BASIC group for Windows Server Authorization Manager. */
    public static final int APP_BASIC_GROUP = 0x00000010; 
    /** Specifies an APP_QUERY group for Windows Server Authorization Manager. */
    public static final int APP_QUERY_GROUP = 0x00000020; 
    /** Specifies a security group. If this flag is not set, then the group is a distribution group. */ 
    public static final int SECURITY_GROUP = 0x80000000; 
    
    private ActiveDirectoryGroupTypes() {
    }
}