
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
			
package com.adito.boot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StopContextListenerThread extends Thread {
	
	final static Log log = LogFactory.getLog(StopContextListenerThread.class);
    
    ContextListener listener;
    
    public StopContextListenerThread(ContextListener listener) {
        super("StopContextListener");
        this.listener = listener;
    }
    
    public void run() {
    	listener.stopped();
    }

    public void waitForStop() {
        start();
        for(int j = 0 ; j < 2; j++) {
            try {
                join(30000);
            } catch (InterruptedException e) {
                break;
            }
            if(isAlive() && j == 0) {
                log.warn(listener + " is preventing the server from shutting down, waiting another 30 seconds then giving up.");
            }
        }            
    }
}