
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
			
package com.adito.security;

/**
 */
public class PasswordCredentials implements Credentials {

    private char[] password;
    private String username;
    /**
     * 
     */
    public PasswordCredentials(String username, char[] password) {
        this.username = username;
        this.password = password;
    }
    
    public char[] getPassword() {
        return password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public boolean equals(Object arg0) {
    	
    	if(arg0 instanceof PasswordCredentials) {
	        PasswordCredentials tmp = (PasswordCredentials) arg0;
	        if (this.username.equals(tmp.username)){
	        	if (this.password.equals(tmp.password)){
	        	return true;
	        	}
	        }
    	}
    	
    	return false;
    }
    

}
