
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
			
package com.maverick.http;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class ChunkedContentSource implements ContentSource {

    ChunkedOutputStream actualOut = new ChunkedOutputStream();
    HttpConnection con;

    long bytesTransfered = 0;

    public ChunkedContentSource() throws IOException {

    }

    public long getBytesTransferred() {
        return bytesTransfered;
    }

    public void setHeaders(HttpRequest request, HttpConnection con) {
        this.con = con;
        request.setHeaderField("transfer-encoding", "chunked"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public OutputStream getOutputStream() {
        return actualOut;
    }

    class ChunkedOutputStream extends OutputStream {

        public void write(byte[] buf, int off, int len) throws IOException {
            bytesTransfered += len;
            con.getOutputStream().write((Integer.toHexString(len) + "\r\n").getBytes("UTF8")); //$NON-NLS-1$ //$NON-NLS-2$
            con.getOutputStream().write(buf, off, len);
            con.getOutputStream().write("\r\n".getBytes("UTF8")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        public void write(int b) throws IOException {
            write(new byte[] { (byte) b }, 0, 1);
        }

        public void close() throws IOException {
            con.getOutputStream().write("0\r\n\r\n".getBytes("UTF8")); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
}
