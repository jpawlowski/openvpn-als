
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
			
package com.sshtools.ui.swing;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;


/**
 * 
 */
public class ConsoleOutputStream extends OutputStream {

    // Private instace variables
    private OutputStream oldSysOut;
    private DebugConsole console;
    private Color color;
    
    public ConsoleOutputStream(OutputStream oldSysOut, Color color, DebugConsole console) {
        this.oldSysOut = oldSysOut;
        this.console = console;
        this.color = color;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#write(int)
     */
    public void write(int b) throws IOException {
        console.append(String.valueOf((char) b), color);
        if (oldSysOut != null) {
            oldSysOut.write(b);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    public void write(byte[] buf, int off, int len) throws IOException {
        console.append(new String(buf, off, len), color);
        if (oldSysOut != null) {
            oldSysOut.write(buf, off, len);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#flush()
     */
    public void flush() throws IOException {
        super.flush();
        if (oldSysOut != null) {
            oldSysOut.flush();
        }
    }
}