
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
			
package com.adito.replacementproxy;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class CacheingOutputStream extends OutputStream implements Serializable {
  transient OutputStream out;
  private byte buf[];
  private List headers;
  private int idx;
  private String contentType;
  private Date cachedDate;

  public CacheingOutputStream(OutputStream out, int size, List headers, String contentType) {
    super();
    this.out = out;
    cachedDate = new Date();
    this.contentType = contentType;
    this.headers = headers;
    buf = new byte[size];
  }
  
  public Date getCachedDate() {
  	return cachedDate;
  }

  public byte[] getBytes() {
    byte b[] = new byte[idx];
    System.arraycopy(buf, 0, b, 0, idx);
    return b;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.Closeable#close()
   */
  public void close() throws IOException {
    out.close();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.OutputStream#write(byte[], int, int)
   */
  public synchronized void write(byte[] b, int off, int len) throws IOException {
    out.write(b, off, len);
    if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
      throw new IndexOutOfBoundsException();
    } else if (len == 0) {
      return;
    }
    int nidx = idx + len;
    if (nidx > buf.length) {
      byte newbuf[] = new byte[Math.max(buf.length << 1, nidx)];
      System.arraycopy(buf, 0, newbuf, 0, idx);
      buf = newbuf;
    }
    System.arraycopy(b, off, buf, idx, len);
    idx = nidx;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.OutputStream#write(int)
   */
  public synchronized void write(int b) throws IOException {
    out.write(b);
    int nidx = idx + 1;
    if (nidx > buf.length) {
      byte nbuf[] = new byte[Math.max(buf.length << 1, nidx)];
      System.arraycopy(buf, 0, nbuf, 0, idx);
      buf = nbuf;
    }
    buf[idx] = (byte) b;
    idx = nidx;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.io.Flushable#flush()
   */
  public void flush() throws IOException {
    out.flush();
  }
  /**
   * @return Returns the contentType.
   */
  public String getContentType() {
    return contentType;
  }
  /**
   * @param contentType The contentType to set.
   */
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }
  /**
   * @return Returns the headers.
   */
  public List getHeaders() {
    return headers;
  }
  /**
   * @param headers The headers to set.
   */
  public void setHeaders(List headers) {
    this.headers = headers;
  }
}