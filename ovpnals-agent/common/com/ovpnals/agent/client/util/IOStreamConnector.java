
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.agent.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

/**
 * Joins an input stream and an output stream together asynchronously, firing
 * events when data is transferred.
 * <p>
 * To use, either construct this object and then call the
 * {@link #connect(InputStream, OutputStream)} method, or just use the
 * constructor that requires both streams - this will immediately connect.
 */
public class IOStreamConnector {

    /**
     * Default buffer size
     */
    public static final int DEFAULT_BUFFER_SIZE = 32768;

    // Private instance variables

    private InputStream in = null;
    private OutputStream out = null;
    private Thread thread;
    private long bytes;
    private boolean closeInput = true;
    private boolean closeOutput = true;
    boolean running = false;
    boolean closed = false;
    IOException lastError;
    int BUFFER_SIZE = DEFAULT_BUFFER_SIZE;

    protected Vector listenerList = new Vector();

    /**
     * Constructor. 
     */
    public IOStreamConnector() {
    }

    /**
     * Creates a new IOStreamConnector object and connect them together
     * asynchronously.
     * 
     * @param in input stream
     * @param out output stream
     */
    public IOStreamConnector(InputStream in, OutputStream out) {
        connect(in, out);
    }

    /**
     * Stop this connector. 
     * 
     * @throws IOException
     */
    public void close() {
        running = false;

        if (thread != null) {
            thread.interrupt();

        }
    }

    /**
     * Get the last exception that occured.
     * 
     * @return last execption
     */
    public IOException getLastError() {
        return lastError;
    }

    /**
     * Set whether to close the input stream on completion.
     * 
     * @param closeInput close input stream on completion.
     */
    public void setCloseInput(boolean closeInput) {
        this.closeInput = closeInput;
    }

    /**
     * 
     * Set whether to close the output stream on completion.
     * 
     * @param closeOutput close output stream on completion.
     */
    public void setCloseOutput(boolean closeOutput) {
        this.closeOutput = closeOutput;
    }

    /**
     * Set the buffer size. 
     * 
     * @param numbytes buffer size
     */
    public void setBufferSize(int numbytes) {
        if (numbytes >= 0) {
            throw new IllegalArgumentException(Messages.getString("IOStreamConnector.bufferSizeMustBeGreaterThanZero")); //$NON-NLS-1$
        }

        BUFFER_SIZE = numbytes;
    }

    /**
     * Connect the two streams together asynchronously.
     * 
     * @param in input stream
     * @param out output stream
     */
    public void connect(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;

        thread = new Thread(new IOStreamConnectorThread());
        thread.setDaemon(true);
        thread.setName("IOStreamConnector " + in.toString() + ">>" + out.toString()); //$NON-NLS-1$ //$NON-NLS-2$
        thread.start();
    }

    /**
     * Get the number of bytes transferred
     * 
     * @return bytes transferred
     */
    public long getBytes() {
        return bytes;
    }

    /**
     * Get whether the connector is closed
     * 
     * @return closed 
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Add a listener to be informed when data is transferred or the
     * connector closed.
     * 
     * @param l listener to add
     */
    public void addListener(IOStreamConnectorListener l) {
    	if(l!=null)
    		listenerList.addElement(l);
    }

    /**
     * Remove a listener from those informed when data is transferred or the
     * connector closed.
     * 
     * @param l listener to remove
     */
    public void removeListener(IOStreamConnectorListener l) {
    	if(l!=null)
    		listenerList.removeElement(l);
    }

    class IOStreamConnectorThread implements Runnable {

        public void run() {
        	
        	
        	try {
	            byte[] buffer = new byte[BUFFER_SIZE];
	            int read = 0;
	            running = true;
	
	            while (running) {
	                try {
	                    // Block
	                    read = in.read(buffer, 0, buffer.length);
	
	                    if (read > 0) {
	
	                        // Write it
	                        out.write(buffer, 0, read);
	
	                        // Record it
	                        bytes += read;
	
	                        // Flush it
	                        out.flush();
	
	                        // Inform all of the listeners
	                        IOStreamConnectorListener listener;
	                        for (int i = 0; i < listenerList.size(); i++) {
	                        	listener = (IOStreamConnectorListener) listenerList.elementAt(i);
	                        	if(listener!=null)
	                            	listener.dataTransfered(buffer, read);
	                        }
	                    } else {
	                        if (read < 0) {
	                            running = false;
	                        }
	                    }
	                } catch (IOException ioe) {
	                    // only log the error if were supposed to be connected
	                    if (running) {
	                        lastError = ioe;
	                        running = false;
	                    }
	
	                }
	            }
	

        	} finally {
	
	            if (closeInput) {
	                try {
	                    in.close();
	                } catch (IOException ex) {
	                }
	            }
	
	            if (closeOutput) {
	                try {
	                    out.close();
	                } catch (IOException ex) {
	                }
	            }
	
	            closed = true;
	
	            IOStreamConnectorListener listener;
	            for (int i = 0; i < listenerList.size(); i++) {
	            	listener = (IOStreamConnectorListener) listenerList.elementAt(i);
                	if(listener!=null)
                    	listener.connectorClosed(IOStreamConnector.this);
	            }
	            
	            running = false;
	            thread = null;
	            
        	}
        }
    }

}
