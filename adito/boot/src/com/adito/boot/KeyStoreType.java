
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
			
package com.adito.boot;

/**
 * Describes a key store type, i.e. file formats that the securit implementation
 * supports. By default this will be <i>JKS</i> and <i>PKCS12</i>. 
 * 
 */

public class KeyStoreType {
    
    // Private instance variables
    
    private String name;
    private String extension;

    /**
     * Constructoir
     * 
     * @param name name of key store type
     * @param extension file extension to use
     */
    public KeyStoreType(String name, String extension) {
        this.name = name;
        this.extension = extension;
    }

    /**
     * Get the file extension to use for key stores of this type
     * 
     * @return file extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Get the name of the key store type
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

}
