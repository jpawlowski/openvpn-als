
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
			
package com.maverick.util;

import java.io.*;
import java.io.InputStream;

class EOLProcessorInputStream extends InputStream {

    EOLProcessor processor;
    InputStream in;
    DynamicBuffer buf = new DynamicBuffer();
    byte[] tmp = new byte[32768];

    public EOLProcessorInputStream(int inputStyle,
            int outputStyle,
            InputStream in) throws IOException {
        this.in = in;
        processor = new EOLProcessor(inputStyle,
                    outputStyle,
                    buf.getOutputStream());
    }

    /**
     * Reads the next byte of data from the input stream.
     *
     * @return the next byte of data, or <code>-1</code> if the end of the
     *   stream is reached.
     * @throws IOException if an I/O error occurs.
     * @todo Implement this java.io.InputStream method
     */
    public int read() throws IOException {
        fillBuffer(1);
        return buf.getInputStream().read();
    }

    public int available() throws IOException {
        return in.available();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        fillBuffer(len);
        return buf.getInputStream().read(b, off, len);
    }

    private void fillBuffer(int count) throws IOException {

        while(buf.available() < count) {
            int read = in.read(tmp);
            if(read == -1) {
                buf.close();
                return;
            }
            processor.processBytes(tmp, 0, read);
        }
    }
    
    public void close() throws IOException {
    	in.close();
    }
}
