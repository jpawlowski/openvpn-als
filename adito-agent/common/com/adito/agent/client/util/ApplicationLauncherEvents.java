
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
			
package com.adito.agent.client.util;

import java.io.IOException;


/**
 * Callback interface for events will occur during the launching of an
 * application.
 */
public interface ApplicationLauncherEvents {

    /**
     * Application is starting to launch
     * 
     * @param application
     */
    public void startingLaunch(String application);

    /**
     * Processing the application extension descriptor
     */
    public void processingDescriptor();

    /**
     * Starting to download all files. 
     * 
     * @param totalNumBytes total number of bytes to download
     */
    public void startDownload(long totalNumBytes);

    /**
     * Download is progressing.
     * 
     * @param bytesSoFar bytes downloaded so far
     */
    public void progressedDownload(long bytesSoFar);

    /**
     * Download is complete.
     */
    public void completedDownload();

    /**
     * Starting to execute application.
     * 
     * @param name application name
     * @param cmdline command line arguments
     */
    public void executingApplication(String name, String cmdline);

    /**
     * Completed launching the application.
     */
    public void finishedLaunch();

    /**
     * Create an application tunnel for the application to use.
     * 
     * @param name name
     * @param hostToConnect host to connect
     * @param portToConnect port to connect
     * @param usePreferredPort use preferred port
     * @param singleConnection accept only a single connection then exit
     * @param sourceInterface source interface
     * @return tunnel configuration
     * @throws IOException 
     */
    public TunnelConfiguration createTunnel(String name, String hostToConnect, int portToConnect, boolean usePreferredPort,
                               boolean singleConnection, String sourceInterface) throws IOException;

    /**
     * Write message to debug log.
     * 
     * @param msg message
     */
    public void debug(String msg);
    
    /**
     * Process the msg.
     * @param msg message
     */
    public void error(String msg);
}