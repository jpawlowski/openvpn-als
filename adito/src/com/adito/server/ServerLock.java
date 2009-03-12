
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
			
package com.adito.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.ContextHolder;
import com.adito.boot.Util;
import com.adito.core.CoreServlet;

/**
 * Used to determine if Adito (or some other server) is already running
 * under a different runtime. The check also determines if the already running
 * the installation wizard.
 * <p>
 * This mechanism also provides a way for the server to be shutdown use a
 * scrip. Once created, the lock file is watched and if it changes to contain
 * the text "shutdown" then the server will be shutdown.
 */
public class ServerLock {
    
    /**
     * Name of the lock file
     */
    public final static String LOCK_NAME = "server.run";

    final static Log log = LogFactory.getLog(ServerLock.class);

    private File lockFile;
    private boolean locked;
    private boolean setup;
    private int port;
    private String bindAddress;
    private boolean started;
    private long lastLockChange;

    /**
     * Constructor. The lock will be searched for, and if a service is known to
     * be already running an exception is thrown.
     * 
     * @param bindAddress address of interface server is bound to
     * @throws IOException if the service is already running
     * 
     */
    public ServerLock(String bindAddress) throws IOException {

        this.bindAddress = bindAddress;
        // PLUNDEN: Removing the context
        // lockFile = new File(ContextHolder.getContext().getTempDirectory(), LOCK_NAME);
    	lockFile = new File((String)CoreServlet.getServlet().getServletContext().getAttribute("adito.directories.tmp"), LOCK_NAME);
        // end change

        /*
         * Check that Adito is not already running using file locks. If
         * the file exists but is not locked, then this may be have been an
         * unclean shutdown
         */
        if (lockFile.exists()) {
            FileInputStream lockIn = null;
            try {
                lockIn = new FileInputStream(lockFile);
                BufferedReader br = new BufferedReader(new InputStreamReader(lockIn));
                String r = br.readLine();
                if (r != null && !r.equals("")) {
                    try {
                        if(!r.equals("shutdown") || r.equals("restart")) {
                            StringTokenizer t = new StringTokenizer(r, ":");
                            setup = "true".equals(t.nextToken());
                            port = Integer.parseInt(t.nextToken());
                            checkStatus();
                        }
                    } catch (Exception e) {
                        System.err.println("Could not parse lock file.");
                        e.printStackTrace();
                    }
                }
            } finally {
                Util.closeStream(lockIn);
            }
        }
    }

    /**
     * Get if a service is already running.
     * 
     * @return server is locked
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Should be called when the service starts. This checks if a service is
     * already running, and if not creates the lock so future instances know.
     * 
     * @param port port the service is running on
     * @throws IOException
     */
    public void start(int port) throws IOException {
        this.port = port;
        // PLUNDEN: Removing the context
		// this.setup = ContextHolder.getContext().isSetupMode();
        this.setup = false;
		// end change
        

        /*
         * Check whether there is already a listener on the port, this means we
         * can throw a more meaningful exception than jetty does if a server
         * other than Adito is already running on this port
         */
        checkStatus();
        if (locked) {
            if (port == 443 || port == 8443) {
                throw new IOException("Some other server is already running on port " + port + "."
                                + "Most web servers will run on this port by default, so check if you have such "
                                + "a service is installed (IIS, Apache or Tomcat for example). Either shutdown "
                                + "and disable the conflicting server, or if you wish to run both services "
                                + "concurrently, change the port number on which one listens.");
            } else {
                throw new IOException("Some other server is already running on port " + port + "."
                                + "Check which other services you have enabled that may be causing "
                                + "this conflict. Then, either disable the service, change the port on "
                                + "which it is listening or change the port on which this server listens.");

            }
        }

        //
        PrintWriter pw = new PrintWriter(new FileOutputStream(lockFile));
        // PLUNDEN: Removing the context
		// pw.println(ContextHolder.getContext().isSetupMode() + ":" + port);
        pw.println(false + ":" + port);
		// end change
        pw.flush();
        pw.close();
        started = true;
        
        lastLockChange = lockFile.lastModified();
        
        /* Start watching the lock file, if it disappears then shut down the
         * server
         */
        Thread t = new Thread("ServerLockMonitor") {
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(5000);
                        if(lastLockChange != lockFile.lastModified()) {
                            lastLockChange = lockFile.lastModified();
                            if (log.isDebugEnabled())
                            	log.debug("Lock file changed, examining");
                            InputStream in = null;
                            try {
                                in = new FileInputStream(lockFile);;
                                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                                String s = br.readLine();
                                Util.closeStream(in); // close so we can delete
                                if("shutdown".equals(s)) {
                                    ContextHolder.getContext().shutdown(false);
                                    break;
                                }
                                else if("restart".equals(s)) {
                                    ContextHolder.getContext().shutdown(true);
                                    break;
                                }
                            }
                            catch(IOException ioe) {
                                Util.closeStream(in);
                                throw ioe;
                            }
                        }
                    }
                    catch(Exception e) {                        
                    }
                }
            }
        };
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
        

    }

    /**
     * Get if this instance started and is running
     * 
     * @return started
     * 
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Remove the lock and clean up. Should be called when the server shuts
     * down.
     */
    public void stop() {
    	if (log.isInfoEnabled())
    		log.info("Removing lock");
        lockFile.delete();
        started = false;
    }

    /**
     * Get if the currently running service is running the installation wizard.
     * 
     * @return running installation wizard
     */
    public boolean isSetup() {
        return setup;
    }

    /**
     * Get the port the currently running service is running on
     * 
     * @return port
     */
    public int getPort() {
        return port;
    }

    private void checkStatus() {
        Socket socket = null;
        try {
            int timeout = 5000; // 5 seconds
            if (log.isInfoEnabled())
            	log.info("Connecting to " + bindAddress + ":" + port + " to see if a server is already running.");
            SocketAddress socketAddress = new InetSocketAddress(bindAddress, port);
            socket = new Socket();
            socket.connect(socketAddress, timeout);
            locked = true;
        } catch (Exception e) {
            locked = false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {

                }
            }
        }
    }

}
