
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
			
package com.adito.core;

/**
 * Javascript is used in a number of places to create links for the user
 * to click on and create new windows and load a document or use the current
 * window and load a document. 
 * <p>
 * Implementations of this interface create the appropriate Javascript for 
 * their requirements.
 */
public interface JavascriptLink {
        
    /**
     * Generate the Javascript fragment appropriate for this link. This should
     * be used in onclick as it does not return the 'javascript:' scheme.
     * 
     * @return javascript fragement to open the window
     */
    public String toJavascript();
}
