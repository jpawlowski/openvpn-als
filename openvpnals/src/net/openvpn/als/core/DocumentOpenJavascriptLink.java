
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

import net.openvpn.als.boot.Util;


/**
 * Constructs a fragment of JavaScript to open a link using <i>document.open()</i>.
 */
public class DocumentOpenJavascriptLink implements JavascriptLink {
    
    // Private instance variables
    private String uri;
    private String target;
    
    /**
     * Constructor for links that go to _self
     * 
     * @param uri uri to open (must be encoded)
     *
     */
    public DocumentOpenJavascriptLink(String uri) {
        this(uri, null);
    }
    
    /**
     * Constructor.
     * 
     * @param uri uri to open (must be encoded)
     * @param target target to open in (_blank, _self etc)
     *
     */
    public DocumentOpenJavascriptLink(String uri, String target) {
        this.uri = uri;    
        this.target = target;
    }
    
    /**
     * Get the URI
     * 
     * @return uri
     */
    public String getURI() {
        return uri;
    }
    
    /**
     * Generate the Javascript fragment.
     * 
     * @return javascript fragement to open the window
     */
    public String toJavascript() {
        StringBuffer buf = new StringBuffer();
        buf.append("open('");
        buf.append(Util.escapeForJavascriptString(uri));
        buf.append("'");
        if(target != null) {
            buf.append(",'");
            buf.append(target);
            buf.append("'");
        }
        buf.append(");");
        return buf.toString();
    }
}
