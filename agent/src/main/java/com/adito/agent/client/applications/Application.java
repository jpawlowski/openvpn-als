
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
			
package com.adito.agent.client.applications;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Hashtable;

import com.adito.agent.client.Agent;
import com.adito.agent.client.Messages;
import com.adito.agent.client.util.AbstractApplicationLauncher;
import com.adito.agent.client.util.FileCleaner;
import com.adito.agent.client.util.ProcessMonitor;

/**
 * Thread that downloads an <i>Application Extension</i> from the Adito
 * server, processes its extension descriptor and launches it.
 */
public class Application extends Thread {

    // Private instance variables
    private Agent client;
    String name;
    Hashtable parameters;
    private AbstractApplicationLauncher launcher;
    private ApplicationEventListener events;
    private String descriptor;
    
    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(Application.class);

    // #endif

    /**
     * Constructor.
     * 
     * @param client client
     * @param parameters application parameters
     * @param name application name
     * @throws IOException on any error
     */
    public Application(Agent client, Hashtable parameters, String name, String descriptor) throws IOException {
        super(MessageFormat.format(Messages.getString("Application.threadName"), new Object[] { name })); //$NON-NLS-1$
        this.client = client;
        this.name = name;
        this.descriptor = descriptor;
        this.parameters = parameters;
        events = new ApplicationEventListener(this.client);
        events.startingLaunch(name);

        launcher = new AgentApplicationLauncher(this.client, name, parameters, descriptor, events);
        //#ifdef DEBUG
        launcher.setDebug(true);
        //#endif
        launcher.prepare();

    }
    
   /**
    * Get the launcher
    * 
    * @return launcher
    */
    public AbstractApplicationLauncher getLauncher() {
    	return launcher;
    }

    /**
     * Get the redirect parameters for the application type.
     * 
     * @return redirect parameters
     */
    public String getRedirectParameters() {
        return launcher.getApplicationType().getRedirectParameters();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread#run()
     */
    public void run() {
    	
    	
    	FileOutputStream out = null;
    	boolean hasErrored = false;;
    	
        try {
        	launcher.start();
                        
        } catch (Throwable t) {
        	hasErrored = true;
        	
            // #ifdef DEBUG
            log.error("Failed to launch application " + name, t); //$NON-NLS-1$
            // #endif
            
            client.getGUI().popup(null, 
            		MessageFormat.format(Messages.getString("Application.error.message"), new Object[] { launcher.getName(), t.getMessage()}), 
            		Messages.getString("Application.error.title"), "popup-error", 0);
            
        } finally {
            
        	events.finishedLaunch();
        	
        	if(!hasErrored) {
	            try {
					out = new FileOutputStream(FileCleaner.deleteOnExit(new File(launcher.getInstallDir(), launcher.getName() + ".out"))); //$NON-NLS-1$
					ProcessMonitor monitor = launcher.getApplicationType().getProcessMonitor();
					if (monitor != null) {
					    monitor.watch(out, out);
					}
				} catch (Throwable e) {
				} finally {
		            if (out != null) {
		                try {
		                    out.close();
		                } catch (IOException ioe) {

		                }
		            }  
				}
        	}
            
          
        }    	
    }
}