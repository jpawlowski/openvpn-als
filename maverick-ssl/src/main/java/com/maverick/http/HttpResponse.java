
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * 
 * @author Lee David Painter
 */
public class HttpResponse extends HttpHeader {

    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(HttpResponse.class);
    // #endif

    protected ByteArrayOutputStream chunked;
    protected String begin;

    private String version = ""; //$NON-NLS-1$
    private int status;
    private String reason = ""; //$NON-NLS-1$

    boolean closeConnection = false;
    int contentLength = 0;

    boolean foundContinue = false;
    boolean release = true;

    InputStream in = new ByteArrayInputStream(new byte[] {});

    HttpConnection con;
    HttpContinue cont;

    public HttpResponse(HttpConnection con) throws IOException {
        this(con, false);
    }

    public HttpResponse(HttpConnection con, HttpContinue cont) throws IOException {
        this.con = con;
        this.cont = cont;
        doResponse(false);
    }

    public HttpResponse(HttpConnection con, boolean headerOnly) throws IOException {
        this.con = con;
        doResponse(headerOnly);
    }

    private void doResponse(boolean headerOnly) throws IOException {

        do {

            begin = readLine(con.getInputStream());

            while (begin.trim().length() == 0) {
                begin = readLine(con.getInputStream());
            }

            // #ifdef DEBUG
            log.debug(MessageFormat.format(Messages.getString("HttpResponse.startLine"), new Object[] { begin })); //$NON-NLS-1$
            // #endif
            processResponse();

            if (status == 100) {
                if (cont != null)
                    cont.continueRequest(con);
                foundContinue = true;

                String tmp;
                while (true) {

                    /**
                     * Fix for IIS web servers, they are sending additional
                     * headers with the 100 Continue response.
                     */
                    tmp = readLine(con.getInputStream());

                    // #ifdef DEBUG
                    log.debug(MessageFormat.format(Messages.getString("HttpResponse.received100"), new Object[] { tmp })); //$NON-NLS-1$
                    // #endif

                    if (tmp.equals("")) //$NON-NLS-1$
                        break;
                }

            }
        } while (status >= 100 && status < 200);

        processHeaderFields(con.getInputStream());

        if (!headerOnly) {

            /**
             * LDP - Moved transfer encoding check to here, seems the more
             * logical place to have it, plus I needed to work around the stream
             * being overidden by the Connection: close header.
             */
            if (getHeaderField("transfer-encoding") != null) { //$NON-NLS-1$
                if (getHeaderField("transfer-encoding").equalsIgnoreCase( //$NON-NLS-1$
                "chunked")) { //$NON-NLS-1$
                    in = new ChunkedInputStream(con.getInputStream());
                    // Remove the transfer-encoding header
                    removeFields("transfer-encoding"); //$NON-NLS-1$
                }
            } else if (getHeaderField("Content-Length") != null) { //$NON-NLS-1$
                contentLength = Integer.parseInt(getHeaderField("Content-Length")); //$NON-NLS-1$
                in = new ContentInputStream(contentLength);
            } else if (getHeaderField("Connection") != null && //$NON-NLS-1$
                getHeaderField("Connection").equalsIgnoreCase("close")) { //$NON-NLS-1$ //$NON-NLS-2$

                // Set the connection as unusable by others
                con.canReuse = false;

                /**
                 * LDP - Since the connection is being closed we could have some
                 * content and no content-length header so just make this
                 * responses inputstream the connections inputstream, it will go
                 * EOF once drained.
                 */
                in = con.getInputStream();
            } else if (getHeaderField("Proxy-Connection") != null && //$NON-NLS-1$
                getHeaderField("Proxy-Connection").equalsIgnoreCase("close")) { //$NON-NLS-1$ //$NON-NLS-2$

                // Set the connection as unusable by others
                con.canReuse = false;

                /**
                 * LDP - Since the connection is being closed we could have some
                 * content and no content-length header so just make this
                 * responses inputstream the connections inputstream, it will go
                 * EOF once drained.
                 */
                in = con.getInputStream();
            } else {
                /**
                 * No data to read so return an empty stream.
                 */
                in = new ByteArrayInputStream(new byte[] {});
            }
        }

        /**
         * Finally check the connection close status again to set the canReuse
         * flag on the HttpConnection.
         */
        if (getHeaderField("Connection") != null && //$NON-NLS-1$
            getHeaderField("Connection").equalsIgnoreCase("close")) { //$NON-NLS-1$ //$NON-NLS-2$

            // Set the connection as unusable by others
            con.canReuse = false;

        } else if (getHeaderField("Proxy-Connection") != null && //$NON-NLS-1$
            getHeaderField("Proxy-Connection").equalsIgnoreCase("close")) { //$NON-NLS-1$ //$NON-NLS-2$

            // Set the connection as unusable by others
            con.canReuse = false;
        }

    }

    public void close() {
        close(true);
    }

    public boolean hasContinue() {
        return foundContinue;
    }

    public synchronized void close(boolean release) {

        /**
         * LDP - I've added this release flag because the doAuthentication
         * method of HttpClient was calling close to drain the connection so
         * that it could be reused. This was correct but the connection was
         * prematurely being put back into the connection manager, and was being
         * reused by other threads. Since we want to keep hold of the existing
         * connection, in case of NTLM authentication we want to drain but not
         * release.
         */
        if (con == null)
            return;

        try {
            if (in instanceof ChunkedInputStream) {
                ((ChunkedInputStream) in).drain();
            } else if (in instanceof ContentInputStream) {
                ((ContentInputStream) in).drain();
            }
        } catch (IOException ex) {
            // Exception during skip better close this connection
            con.canReuse = false;
        } finally {
            if (release) {
                con.release();
                con = null;
            }
        }
    }
    
    public String getContentType() {
    	return getHeaderField("Content-Type");
    }
    
    public String getContentTypeWithoutParameter() {
    	String contentType = getContentType();
    	if(contentType == null) {
    		return null;
    	}
    	int idx = contentType.indexOf(';');
    	return idx == -1 ? contentType : contentType.substring(0, idx);
    }

    protected String readLine(InputStream in) throws IOException {
        StringBuffer lineBuf = new StringBuffer();
        int c;

        while (true) {
            c = in.read();

            if (c == -1) {
                if (lineBuf.length() == 0)
                    throw new EOFException(Messages.getString("HttpResponse.unexpectedEOF")); //$NON-NLS-1$

                break;
            }

            if (c != '\n') {
                lineBuf.append((char) c);
            } else {
                break;
            }
        }

        return lineBuf.toString().trim();
    }

    public String getStartLine() {
        return begin;
    }

    protected void processHeaderFields(InputStream in) throws IOException {
        clearHeaderFields();

        StringBuffer lineBuf = new StringBuffer();
        String lastHeaderName = null;
        int c;

        while (true) {
            c = in.read();

            if (c == -1) {
                throw new IOException(Messages.getString("HttpResponse.headerCorrupt")); //$NON-NLS-1$
            }

            if (c != '\n') {
                lineBuf.append((char) c);
            } else {
                String line = lineBuf.toString().trim();
                lineBuf.setLength(0);
                if (line.length() != 0) {
                    lastHeaderName = processNextLine(line, lastHeaderName);
                } else {
                    break;
                }
            }
        }
    }

    private String processNextLine(String line, String lastHeaderName) throws IOException {
        String name;
        String value;
        char c = line.charAt(0);

        if ((c == ' ') || (c == '\t')) {
            name = lastHeaderName;
            value = getHeaderField(lastHeaderName) + " " + line.trim(); //$NON-NLS-1$
        } else {
            int n = line.indexOf(':');

            if (n == -1) {
                throw new IOException(MessageFormat.format(Messages.getString("HttpResponse.corruptField"), new Object[] { line })); //$NON-NLS-1$ //$NON-NLS-2$
            }

            name = line.substring(0, n);
            value = line.substring(n + 1).trim();
        }

        // #ifdef DEBUG
        log.debug(MessageFormat.format(Messages.getString("HttpResponse.receivedHeader"), new Object[] { name, value })); //$NON-NLS-1$
        // #endif
        addHeaderField(name, value);

        return name;
    }

    public InputStream getInputStream() {
        return in;
    }

    public HttpConnection getConnection() {
        return con;
    }

    public String getVersion() {
        return version;
    }

    public int getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    private void processResponse() throws IOException {
        StringTokenizer tokens = new StringTokenizer(begin, WHITE_SPACE, false);
        reason = ""; //$NON-NLS-1$
        try {
            version = tokens.nextToken();
            status = Integer.parseInt(tokens.nextToken());
            while (tokens.hasMoreTokens()) {
                reason += tokens.nextToken() + " "; //$NON-NLS-1$
            }
            reason = reason.trim();
        } catch (NoSuchElementException e) {
            throw new IOException(MessageFormat.format(Messages.getString("HttpResponse.failedToReadResponse"), new Object[] { begin })); //$NON-NLS-1$ 
        } catch (NumberFormatException e) {
            throw new IOException(MessageFormat.format(Messages.getString("HttpResponse.failedToReadResponse"), new Object[] { begin })); //$NON-NLS-1$
        }
    }

    /**
     * We will use this stream to return data that is encoded using the
     * "Transfer-Encoding: chunked" header.
     */
    class ChunkedInputStream extends InputStream {

        long chunkLength;
        InputStream in;

        ChunkedInputStream(InputStream in) throws IOException {

            this.in = in;
            // Read from the InputStream until we receive chunk size of zero
            chunkLength = Long.parseLong(readLine(in), 16);
        }

        public int read() throws IOException {
            byte[] b = new byte[1];
            int read = read(b, 0, 1);
            if (read == -1)
                return -1;
            else
                return b[0] & 0xFF;
        }

        public void drain() throws IOException {
            long len;
            byte[] buf = new byte[65535];
            while (contentLength > 0) {
                len = con.getInputStream().read(buf, 0, buf.length);

                if (contentLength > 0)
                    contentLength -= len;
                else
                    break;
            }
        }

        public synchronized int read(byte[] buf, int off, int len) throws IOException {

            if (chunkLength == 0 || con == null) {
                return -1;
            }
            int read;
            int count = 0;
            while (len > 0 && chunkLength > 0) {

                read = in.read(buf, off, (int) (len > chunkLength ? chunkLength : len));

                if (read == -1)
                    throw new EOFException(Messages.getString("HttpResponse.unexpectedEOFDuringChunking")); //$NON-NLS-1$

                chunkLength -= read;
                len -= read;
                off += read;
                count += read;

                if (chunkLength == 0) {
                    readLine(in);
                    chunkLength = Long.parseLong(readLine(in), 16);
                    if (chunkLength == 0)
                        close();
                }
            }

            return count;
        }

        public synchronized void close() throws IOException {

            if (con != null) {
                readLine(in);
                HttpResponse.this.close(true);
            }
        }
    }

    /**
     * We will use this to return standard content
     */
    class ContentInputStream extends InputStream {

        long contentLength;

        ContentInputStream(long contentLength) {
            this.contentLength = contentLength;
        }

        public synchronized int available() {
            return (int) contentLength;
        }

        public synchronized long skip(long length) throws IOException {
            return con.getInputStream().skip(length);
        }

        public void drain() throws IOException {
            long len;

            while (contentLength > 0) {
                len = con.getInputStream().skip(contentLength);

                if (contentLength > 0)
                    contentLength -= len;
                else
                    break;
            }
        }

        public synchronized int read() throws IOException {

            if (contentLength == 0 || con == null) {
                return -1;
            } else {
                int b = con.getInputStream().read();
                if (b == -1)
                    throw new EOFException(MessageFormat.format(Messages.getString("HttpResponse.unexpectedEOFInResponseExpected"), new Object[] { new Long(contentLength) })); //$NON-NLS-1$
                contentLength--;

                if (contentLength == 0)
                    close();

                return b;
            }
        }

        public synchronized int read(byte[] buf, int off, int len) throws IOException {
            if (contentLength == 0 || con == null) {
                return -1;
            } else {
                int read = con.getInputStream().read(buf, off, (contentLength > len ? len : (int) contentLength));
                if (read == -1)
                    throw new EOFException(MessageFormat.format(Messages.getString("HttpResponse.unexpectedEOFInResponseExpected"), new Object[] { new Long(contentLength) })); //$NON-NLS-1$ //$NON-NLS-2$
                contentLength -= read;

                if (contentLength == 0)
                    close();

                return read;
            }

        }

        public synchronized void close() {
            // Release the connection back to the client pool
            if (con != null)
                HttpResponse.this.close(true);
        }
    }

}
