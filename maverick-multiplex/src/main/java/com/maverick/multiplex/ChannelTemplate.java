
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
			
package com.maverick.multiplex;

import java.io.IOException;

/**
 * This Channel provides empty implementations of the abstract methods. 
 */
public class ChannelTemplate extends Channel {

    public ChannelTemplate(String type) {
        this(type, 65535, 65335);
    }
    
    public ChannelTemplate(String type, int localpacket, int localwindow) {
        super(type, localpacket, localwindow);
    }
    
    public byte[] create() throws IOException {
        return null;
    }

    public void onChannelClose() {
    }

    public void onChannelOpen(byte[] data) {
    }

    public byte[] open(byte[] data) throws IOException, ChannelOpenException {
        return null;
    }
}