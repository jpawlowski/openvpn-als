
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
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Vector;

/**
 * 
 * Manages a pool of {@link HttpConnection} objects for an {@link HttpClient}.
 * 
 * @author Lee David Painter <a href="mailto: lee@localhost">&lt;lee@localhost&gt;</a>
 * @author Brett Smith <a href="mailto: brett@localhost">&lt;brett@localhost&gt;</a>
 */
public class HttpConnectionManager {

    private Vector connections = new Vector();
    HttpClient client;

    static int newIdx = 0;
    static int reuseIdx = 0;

    private int maxPoolSize = Integer.MAX_VALUE;

    // #ifdef DEBUG
    org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(HttpConnectionManager.class);

    // #endif

    public HttpConnectionManager(HttpClient client) {
        this.client = client;
    }

    /**
     * Get a connection. If there is a valid pooled available, then that will be
     * returned otherwise a new connection will be created.
     * 
     * @return HttpConnection
     * @throws IOException
     * @throws UnknownHostException
     */
    public synchronized HttpConnection getConnection() throws IOException, UnknownHostException, HttpException,
                    UnsupportedAuthenticationException, AuthenticationCancelledException {

        HttpConnection con;

        if (connections.size() == 0) {
            // #ifdef DEBUG
            log.info(Messages.getString("HttpConnectionManager.creatingNewConnection")); //$NON-NLS-1$
            // #endif
            con = new HttpConnection(client);
        } else {
            con = (HttpConnection) connections.elementAt(0);
            connections.removeElementAt(0);
            // #ifdef DEBUG
            log.info(MessageFormat.format(Messages.getString("HttpConnectionManager.reusingConnection"), new Object[] { new Integer(connections.size()), con.toString() })); //$NON-NLS-1$
            // #endif
            con.verify();
        }
        // #ifdef DEBUG
        log.info("Returning pooled connection"); //$NON-NLS-1$
        // #endif
        return con;
    }

    /**
     * Set the maximum size of the connection pool. Set to zero to place no
     * restriction.
     * 
     * @param maxPoolSize maximum pool size
     */
    public synchronized void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    /**
     * Release a connection back to the pool.
     * 
     * @param connection to release
     */
    public synchronized void releaseConnection(HttpConnection con) {
        // Can we reuse this connection?
        if (con.canReuse() && !con.isClosed()) {
            if (maxPoolSize == 0 || connections.size() < maxPoolSize)
                connections.addElement(con);
            else
                con.close();
            // #ifdef DEBUG
            log.info(MessageFormat.format(Messages.getString("HttpConnectionManager.releasedToPool"), new Object[] { new Integer(connections.size()), con.toString() })); //$NON-NLS-1$ //$NON-NLS-2$
            // #endif
            // Set the last time this was accessed.
            con.updateState();
        } else {
            // #ifdef DEBUG
            log.info(Messages.getString("HttpConnectionManager.willNotReuse")); //$NON-NLS-1$
            // #endif
        }

    }

    /**
     * Release all connections back to the pool.
     */
    public synchronized void releaseAllConnections() {
        for (Enumeration e1 = connections.elements(); e1.hasMoreElements();) {
            HttpConnection con = (HttpConnection) e1.nextElement();
            releaseConnection(con);
        }
    }

    /**
     * Check if a connection is closed or un-reusable and reconnect it
     * 
     * @param con connection
     * @throws UnknownHostException
     * @throws IOException
     * @throws HttpException
     * @throws UnsupportedAuthenticationException
     * @throws AuthenticationCancelledException
     */
    public void checkConnection(HttpConnection con) throws UnknownHostException, IOException, HttpException,
                    UnsupportedAuthenticationException, AuthenticationCancelledException {
        if (con.isClosed || !con.canReuse) {
            con.reconnect();
        }
    }

    /**
     * Close all connections
     */
    public synchronized void closeConnections() {

        for (Enumeration e1 = connections.elements(); e1.hasMoreElements();) {
            HttpConnection con = (HttpConnection) e1.nextElement();
            con.close();
        }
    }

    /**
     * Get the number of connections in the pool
     * 
     * @return number of pooled connections
     */
    public synchronized int getConnectionCount() {
        return connections.size();
    }

    /**
     * Get the number of connections currently open.
     * 
     * @return number of open connections
     */
    public synchronized int getOpenConnectionCount() {
        int open = 0;
        for (Enumeration e1 = connections.elements(); e1.hasMoreElements();) {
            HttpConnection con = (HttpConnection) e1.nextElement();
            if (!con.isClosed()) {
                open++;
            }
        }
        return open;

    }

}
