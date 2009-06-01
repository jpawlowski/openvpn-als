
				/*
 *  OpenVPNALS
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
			
package net.openvpn.als.applications.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.openvpn.als.applications.ApplicationShortcut;
import net.openvpn.als.boot.Util;
import net.openvpn.als.extensions.store.ExtensionStore;
import net.openvpn.als.security.SessionInfo;


/**
 * Launches a server application.
 */
public class ServerApplicationLauncher extends Thread {
    
	
	final static Log log = LogFactory.getLog(ServerApplicationLauncher.class);
    
	Map<String, String> parameters;
    boolean running = false;
    String name;
    ServerLauncher launcher;
    String exitMessage = null;

    /**
     * Constructor.
     *
     * @param parameters
     * @param id
     * @param session
     * @param shortcut
     * @throws IOException
     */
    public ServerApplicationLauncher(Map<String, String> parameters, String id, SessionInfo session, ApplicationShortcut shortcut) throws IOException {
        super(id + " launcher");
        this.name = id;
        this.parameters = parameters;
        running = true;
        try {
            launcher = new ServerLauncher(ExtensionStore.getInstance().getExtensionDescriptor(id), session, shortcut, parameters);
            launcher.prepare();
            launcher.start();
            exitMessage = launcher.exitMessage.equals("") ? "OK" : launcher.exitMessage;
        } catch (Throwable t) {
            if (t instanceof IOException) {
                throw (IOException) t;
            } else {
                /* DEBUG */log.error("Failed to launch. ", t);
                throw new IOException("Failed to launch. " + t.getMessage());
            }
        }
    }

    public void run() {
        FileOutputStream out = null;
        FileInputStream in = null;
        File tmp = null; 
        try {
        	tmp = File.createTempFile("montitor", ".out");
            out = new FileOutputStream(tmp);
            ProcessMonitor monitor = launcher.getApplicationType().getProcessMonitor();
            if (monitor != null) {
                monitor.watch(out, out);
            }
            
          
            if(log.isInfoEnabled()) {
            	String line;
            	BufferedReader reader = new BufferedReader(new InputStreamReader(in = new FileInputStream(tmp)));
            	while((line = reader.readLine())!=null) {
            		log.info(line);
            	}
            }
        } catch (Throwable ex) {
            log.error("Exception during process monitoring.", ex);
        } finally {
        	
        	Util.closeStream(out);
        	Util.closeStream(in);
            Util.delTree(tmp);
            Util.delTree(launcher.getInstallDir());
        }
        running = false;

    }
}