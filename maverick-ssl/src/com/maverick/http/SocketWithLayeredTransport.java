
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
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.MessageFormat;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class SocketWithLayeredTransport extends Socket {



	LayeredTransport head = null;

    // #ifdef DEBUG
    org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(SocketWithLayeredTransport.class);
    // #endif

    public SocketWithLayeredTransport() {
    }
    
    public SocketWithLayeredTransport(String hostname, int port) throws UnknownHostException, IOException {
        super(hostname, port);
    }
    
    public SocketWithLayeredTransport(InetAddress address, int port, InetAddress localAddr, int localPort) throws IOException {
		super(address, port, localAddr, localPort);
	}

	public SocketWithLayeredTransport(InetAddress address, int port) throws IOException {
		super(address, port);
	}
	
    public synchronized void pushTransport(Object obj) throws IOException {
    	if(obj!=null) {
	        if (head == null)
	            head = new LayeredTransport(obj, this);
	        else
	            head = new LayeredTransport(obj, head);
    	}
    }

    public synchronized InputStream getInputStream() throws IOException {
        if (head == null)
            return getRawInputStream();
        else
            return head.getInputStream();
    }

    public synchronized OutputStream getOutputStream() throws IOException {
        if (head == null)
            return getRawOutputStream();
        else
            return head.getOutputStream();
    }
    
    protected InputStream getRawInputStream() throws IOException {
    	return super.getInputStream();
    }
    
    protected OutputStream getRawOutputStream() throws IOException {
    	return super.getOutputStream();
    }

    public void close() throws IOException {
        if (head != null)
            head.close();
        try {
            super.close();
        } catch (Throwable t) {
        }
    }

    public static String getExceptionMessageChain(Throwable t) {
        StringBuffer buf = new StringBuffer();
        while (t != null) {
            if (buf.length() > 0 && !buf.toString().endsWith(".")) { //$NON-NLS-1$
                buf.append(". "); //$NON-NLS-1$
            }
            if (t.getMessage() != null) {
                buf.append(t.getMessage().trim());
            }
            try {
                Method m = t.getClass().getMethod("getCause", (Class[]) null); //$NON-NLS-1$
                t = (Throwable) m.invoke(t, (Object[]) null);
            } catch (Throwable ex) {
            }
        }
        return buf.toString();
    }

    class LayeredTransport {

        Object transport;
        Object source;
        InputStream in, rawIn;
        OutputStream out, rawOut;
        Method close;
        Method rawClose;

        LayeredTransport(Object transport, Object source) throws IOException {

            try {

                // Get the source objects
                Method m = source.getClass().getMethod("getInputStream", (Class[]) null); //$NON-NLS-1$
                rawIn = (InputStream) m.invoke(source, (Object[]) null);

                m = source.getClass().getMethod("getOutputStream", (Class[]) null); //$NON-NLS-1$
                rawOut = (OutputStream) m.invoke(source, (Object[]) null);

                rawClose = source.getClass().getMethod("close", (Class[]) null); //$NON-NLS-1$

                // Get the transport objects
                m = transport.getClass().getMethod("initialize", new Class[] { InputStream.class, OutputStream.class }); //$NON-NLS-1$
                m.invoke(transport, new Object[] { rawIn, rawOut });

                m = transport.getClass().getMethod("getInputStream", (Class[]) null); //$NON-NLS-1$
                in = (InputStream) m.invoke(transport, (Object[]) null);

                m = transport.getClass().getMethod("getOutputStream", (Class[]) null); //$NON-NLS-1$
                out = (OutputStream) m.invoke(transport, (Object[]) null);

                close = transport.getClass().getMethod("close", (Class[]) null); //$NON-NLS-1$

                // Everything ok
                this.source = source;
                this.transport = transport;
            } catch (InvocationTargetException ite) {
                Throwable t = ite.getTargetException();

                if (t != null && t instanceof IOException) {
                    throw (IOException) t;
                } else {
                    throw new IOException(MessageFormat.format(Messages.getString("SocketWithLayeredTransport.failedToLayerTransport"), new Object[] { t == null ? ite.getMessage() : getExceptionMessageChain(t) })); //$NON-NLS-1$
                }

            } catch (Throwable ex) {
                // #ifdef DEBUG
                log.info(Messages.getString("SocketWithLayeredTransport.failedToCreateLayeredSocket"), ex); //$NON-NLS-1$
                // #endif
                throw new IOException(MessageFormat.format(Messages.getString("SocketWithLayeredTransport.failedToLayerTransport"), new Object[] { ex.getMessage() })); //$NON-NLS-1$
            }
        }

        public InputStream getInputStream() throws IOException {
            return in;
        }

        public OutputStream getOutputStream() throws IOException {
            return out;
        }

        public void close() throws IOException {
            try {
                close.invoke(transport, (Object[]) null);
            } catch (InvocationTargetException ex) {
            } catch (Throwable ex) {
                // #ifdef DEBUG
                log.info(Messages.getString("SocketWithLayeredTransport.failedToClose"), ex); //$NON-NLS-1$
                // #endif
            }
        }
    }
}
