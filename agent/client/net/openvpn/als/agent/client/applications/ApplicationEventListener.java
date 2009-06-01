
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
			
package net.openvpn.als.agent.client.applications;

import java.io.IOException;
import java.text.MessageFormat;

import net.openvpn.als.agent.client.Agent;
import net.openvpn.als.agent.client.Messages;
import net.openvpn.als.agent.client.TaskProgress;
import net.openvpn.als.agent.client.util.ApplicationLauncherEvents;
import net.openvpn.als.agent.client.util.TunnelConfiguration;

/**
 * Listens for events that occurs during an application launch inform the user
 * (e.g. through status messages and progress bars).
 */
public class ApplicationEventListener implements ApplicationLauncherEvents {

    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(ApplicationEventListener.class);
    // #endif

    private static final String LABEL_TITLE = Messages.getString("VPNClient.error");  //$NON-NLS-1$
    // Private instance variables
    private Agent client;
    private TaskProgress progress;
    private String application;
    private long totalNumBytes;

    /**
     * Constructor.
     * 
     * @param client client
     */
    public ApplicationEventListener(Agent client) {
        this.client = client;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.vpn.util.ApplicationLauncherEvents#startingLaunch(java.lang.String)
     */
    public void startingLaunch(String application) {
        this.application = application;
        progress = this.client.getGUI().createTaskProgress(
        	MessageFormat.format(Messages.getString("ApplicationEventListener.application"), //$NON-NLS-1$
                        new Object[] { application }), Messages.getString("ApplicationEventListener.title"), 100, false); //$NON-NLS-1$
        progress.updateValue(7);

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.vpn.util.ApplicationLauncherEvents#processingDescriptor()
     */
    public void processingDescriptor() {
        progress.setMessage(MessageFormat.format(
            Messages.getString("ApplicationEventListener.processing"), new Object[] { application })); //$NON-NLS-1$
        progress.updateValue(14);

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.vpn.util.ApplicationLauncherEvents#startDownload(long)
     */
    public void startDownload(long totalNumBytes) {
        this.totalNumBytes = totalNumBytes;
        progress.setMessage(Messages.getString("ApplicationEventListener.downloading")); //$NON-NLS-1$
        progress.updateValue(20);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.vpn.util.ApplicationLauncherEvents#progressedDownload(long)
     */
    public void progressedDownload(long bytesSoFar) {
        // The the file download takes up 70% of the total progress
        int percent = (int) (((float) bytesSoFar / (float) totalNumBytes) * 70f) + 20;
        progress.updateValue(percent);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.vpn.util.ApplicationLauncherEvents#completedDownload()
     */
    public void completedDownload() {
        progress.setMessage(Messages.getString("ApplicationEventListener.downloadComplete")); //$NON-NLS-1$
        progress.updateValue(90);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.vpn.util.ApplicationLauncherEvents#executingApplication(java.lang.String,
     *      java.lang.String)
     */
    public void executingApplication(String name, String cmdline) {
        progress.setMessage(MessageFormat.format(Messages.getString("ApplicationEventListener.launching"), new Object[] { name })); //$NON-NLS-1$
        progress.updateValue(95);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.vpn.util.ApplicationLauncherEvents#finishedLaunch()
     */
    public void finishedLaunch() {

        Thread t = new Thread() {
            public void run() {
                progress.updateValue(100);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                }
                progress.dispose();
            }
        };

        t.start();

    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.vpn.util.ApplicationLauncherEvents#createTunnel(java.lang.String,
     *      java.lang.String, int, boolean, boolean)
     */
    public TunnelConfiguration createTunnel(String name, String hostToConnect, int portToConnect, boolean usePreferredPort,
                               boolean singleConnection, String sourceInterface)throws IOException {
    	try {
			return client.getTunnelManager().startTemporaryLocalTunnel(name, hostToConnect, sourceInterface, portToConnect, usePreferredPort, singleConnection, null).getTunnel();
		} catch (IOException e) {
			// #ifdef DEBUG
			log.error("Failed to start application tunnel " + name, e); //$NON-NLS-1$
			// #endif
			throw e;
		}
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.openvpn.als.vpn.util.ApplicationLauncherEvents#debug(java.lang.String)
     */
    public void debug(String msg) {
        // #ifdef DEBUG
        log.info(msg);
        // #endif
    }

    public void error(String msg) {
        // #ifdef DEBUG
        log.error(msg);
        // #endif
        client.getGUI().popup(null, msg, LABEL_TITLE, "popup-error", -1);
    }
}