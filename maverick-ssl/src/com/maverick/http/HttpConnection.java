
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.MessageFormat;

import com.maverick.ssl.SSLException;
import com.maverick.ssl.SSLIOException;
import com.maverick.ssl.SSLTransportFactory;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class HttpConnection {

    SocketWithLayeredTransport socket;
    InputStream in;
    OutputStream out;
    OutputStream monitorInOut;
    OutputStream monitorOutOut;
    HttpClient client;
    boolean isClosed = false;
    boolean canReuse = true;
    boolean keepAlive = true;
    long lastAccessed;
    HttpAuthenticator authenticator;

    public static final int CONNECTION_TIMEOUT_LIMIT = 120000;

    private static boolean linger = true;
    private static boolean noDelay = false;
    private boolean isPooled = true;

    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(HttpConnection.class);

    // #endif

    public HttpConnection(HttpClient client) throws UnknownHostException, IOException, HttpException,
        UnsupportedAuthenticationException, AuthenticationCancelledException {
        this.client = client;
        reconnect();
    }
    
    public HttpConnection(HttpClient client, SocketWithLayeredTransport socket) throws IOException {
    	this.client = client;
    	this.socket = socket;
    	this.isPooled = false;
    	
        int lingerTime = socket.getSoLinger();
        socket.setSoLinger(linger, false ? 0 : (lingerTime == -1 ? 0 : lingerTime));

        in = new HttpConnectionInputStream(socket.getInputStream(), 32768);
        out = new HttpConnectionOutputStream();

        isClosed = false;
        canReuse = true;
        
    }

    public static void setDefaultSoLinger(boolean linger) {
        HttpConnection.linger = linger;
    }

    public static void setDefaultNoDelay(boolean noDelay) {
        HttpConnection.noDelay = noDelay;
    }

    public synchronized void reconnect() throws UnknownHostException, IOException, HttpException,
                    UnsupportedAuthenticationException, AuthenticationCancelledException {
        close();
        if (!client.isProxyConfigured()) {
            // #ifdef DEBUG
            log.debug(MessageFormat.format(Messages.getString("HttpConnection.connectingTo"), new Object[] { client.hostname, new Integer(client.port) })); //$NON-NLS-1$
            // #endif
            this.socket = SocketWithLayeredTransportFactory.getDefault().createSocket(client.hostname, client.port);
            this.socket.setTcpNoDelay(noDelay);
            
            // LDP - Always push a transport even if its null. This allows us to pick up
            // a start event in child implementations of the socket so we can decide
            // when to start streaming across forwarded sockets that may or may not
            // require an SSL transport.
            this.socket.pushTransport(client.isSecure ? SSLTransportFactory.newInstance() : null);
            
        } else {

            synchronized (client) {
                switch (client.proxyType) {
                    case HttpClient.PROXY_HTTP:
                    case HttpClient.PROXY_HTTPS:

                        // #ifdef DEBUG
                        log.debug(MessageFormat.format(Messages.getString("HttpConnection.proxyConnect"), new Object[] { client.hostname, new Integer(client.port), client.proxyType == HttpClient.PROXY_HTTPS ? "https" : "http", client.proxyHost, new Integer(client.proxyPort) })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        // #endif
                        // Setup the proxy client connection
                        if (client.proxyClient == null) {
                            client.proxyClient = new HttpClient(client);
                        }

                        ConnectMethod proxyConnect = new ConnectMethod(client.hostname, client.port, client.isSecure);
                        // Execute and retreive the direct socket
                        HttpResponse response = client.proxyClient.execute(proxyConnect);

                        if (response.getStatus() == 200) {
                            socket = response.getConnection().socket;
                        } else
                            throw new IOException(MessageFormat.format(Messages.getString("HttpConnection.invalidHttpStatusCode"), new Object[] { new Integer(response.getStatus()) })); //$NON-NLS-1$
                        break;
                    default:
                        throw new IllegalArgumentException(MessageFormat.format(Messages.getString("HttpConnection.invalidProxyType"), new Object[] { new Integer(client.proxyType) })); //$NON-NLS-1$
                }
            }
        }

        int lingerTime = socket.getSoLinger();
        socket.setSoLinger(linger, false ? 0 : (lingerTime == -1 ? 0 : lingerTime));

        in = new HttpConnectionInputStream(socket.getInputStream(), 32768);
        out = new HttpConnectionOutputStream();

        isClosed = false;
        canReuse = true;
    }

    void stopMonitor() throws IOException {

        try {
            in.close();
        } catch (IOException ex) {
        }
        try {
            out.close();
        } catch (IOException ex) {
        }
        in = socket.getInputStream();
        out = new HttpConnectionOutputStream();
    }

    void updateState() {
        this.lastAccessed = System.currentTimeMillis();
    }

    void release() {

        if (!canReuse)
            close();
        
        if(isPooled)
        	client.connections.releaseConnection(this);
    }

    void verify() throws UnknownHostException, IOException, HttpException, UnsupportedAuthenticationException,
                    AuthenticationCancelledException {

        try {

            if (System.currentTimeMillis() > lastAccessed + CONNECTION_TIMEOUT_LIMIT)
                canReuse = false;
            else {

                try {
                    socket.setSoTimeout(1);
                    in.mark(1);
                    int byteRead = in.read();
                    if (byteRead == -1) {
                        // again - if the socket is reporting all data read,
                        // probably stale
                        // #ifdef DEBUG
                        log.debug(Messages.getString("HttpConnection.eof")); //$NON-NLS-1$
                        // #endif
                        canReuse = false;
                    } else {
                        in.reset();
                    }
                } finally {
                    socket.setSoTimeout(0);
                }
            }
        } catch (InterruptedIOException ex) {
            // Connection should be ok
        } catch (SSLIOException ex) {
            canReuse = (ex.getRealException().getStatus() == SSLException.READ_TIMEOUT);
        } catch (IOException ex) {
            // Connection is dead
            // #ifdef DEBUG
            log.debug(Messages.getString("HttpConnection.dead")); //$NON-NLS-1$
            // #endif
            canReuse = false;

        }

        if (!canReuse && isPooled)
            reconnect();
    }

    public boolean isClosed() {
        return isClosed;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public boolean canReuse() {
        return canReuse;
    }

    public synchronized void close() {
        try {
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException ex) {
            }
        } finally {
            if (monitorInOut != null) {
                try {
                    monitorInOut.close();
                } catch (IOException ioe) {
                }
            }
            if (monitorOutOut != null) {
                try {
                    monitorOutOut.close();
                } catch (IOException ioe) {
                }
            }
        }

        isClosed = true;
    }

    public OutputStream getOutputStream() throws IOException {
        return out;
        // return socket.getOutputStream();
    }

    public InputStream getInputStream() throws IOException {
        return in;
    }

    public int getPort() {
        return client.port;
    }

    public String getHost() {
        return client.hostname;
    }

    public String getHostHeaderValue() {
        return client.hostname
            + ((client.isSecure && client.port != 443) || (!client.isSecure && client.port != 80) ? (":" + client.port) : ""); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public boolean isSecure() {
        return client.isSecure;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setAuthenticator(HttpAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    public HttpAuthenticator getAuthenticator() {
        return authenticator;
    }

    class HttpConnectionInputStream extends BufferedInputStream {

        HttpConnectionInputStream(InputStream in, int len) {
            super(in, len);
        }

        public void close() throws IOException {
            updateState();
            socket.getInputStream().close();
        }

        public int read() throws IOException {
            updateState();
            return super.read();
        }

        public int read(byte[] buf, int off, int len) throws IOException {
            updateState();
            return super.read(buf, off, len);
        }
    }

    class HttpConnectionOutputStream extends OutputStream {

        public void close() throws IOException {
            updateState();
            socket.getOutputStream().close();
        }

        public void write(int b) throws IOException {
            updateState();
            socket.getOutputStream().write(b);
        }

        public void write(byte[] buf, int off, int len) throws IOException {
            updateState();
            socket.getOutputStream().write(buf, off, len);
        }
    }

    class MonitorInputStream extends InputStream {

        InputStream in = null;
        OutputStream monitorOut;

        MonitorInputStream(InputStream in, OutputStream monitorOut) {
            super();
            this.in = in;
            this.monitorOut = monitorOut;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.InputStream#available()
         */
        public int available() throws IOException {
            return in.available();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.InputStream#close()
         */
        public void close() throws IOException {
            in.close();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.InputStream#mark(int)
         */
        public synchronized void mark(int readlimit) {
            in.mark(readlimit);
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.InputStream#markSupported()
         */
        public boolean markSupported() {
            return in.markSupported();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.InputStream#read(byte[], int, int)
         */
        public int read(byte[] b, int off, int len) throws IOException {
            int r = in.read(b, off, len);
            if (r != -1) {
                try {
                    monitorOut.write(b, off, r);
                    monitorOut.flush();
                } catch (IOException ioe) {
                }
            }
            return r;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.InputStream#read(byte[])
         */
        public int read(byte[] b) throws IOException {
            int r = in.read(b);
            if (r != -1) {
                try {
                    monitorOut.write(b, 0, r);
                    monitorOut.flush();
                } catch (IOException ioe) {
                }
            }
            return r;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.InputStream#reset()
         */
        public synchronized void reset() throws IOException {
            in.reset();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.InputStream#skip(long)
         */
        public long skip(long n) throws IOException {
            return in.skip(n);
        }

        public int read() throws IOException {
            int r = in.read();
            if (r != -1) {
                try {
                    monitorOut.write(r);
                    monitorOut.flush();
                } catch (IOException ioe) {
                }
            }
            return r;
        }

    }

    class MonitorOutputStream extends OutputStream {

        OutputStream out;
        OutputStream monitorOut;

        MonitorOutputStream(OutputStream out, OutputStream monitorOut) {
            super();
            this.out = out;
            this.monitorOut = monitorOut;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.OutputStream#close()
         */
        public void close() throws IOException {
            out.close();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.OutputStream#flush()
         */
        public void flush() throws IOException {
            out.flush();
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.OutputStream#write(byte[], int, int)
         */
        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
            try {
                monitorOut.write(b, off, len);
                monitorOut.flush();
            } catch (IOException ioe) {
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.io.OutputStream#write(byte[])
         */
        public void write(byte[] b) throws IOException {
            out.write(b);
            try {
                monitorOut.write(b);
                monitorOut.flush();
            } catch (IOException ioe) {
            }
        }

        public void write(int b) throws IOException {
            out.write(b);
            try {
                monitorOut.write(b);
                monitorOut.flush();
            } catch (IOException ioe) {
            }
        }

    }

	public void setCanReuse(boolean canReuse) {
		this.canReuse = canReuse;
	}
}
