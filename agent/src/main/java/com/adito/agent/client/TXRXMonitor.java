
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
			
package com.adito.agent.client;

import com.adito.agent.client.util.IOStreamConnector;
import com.adito.agent.client.util.IOStreamConnectorListener;


/**
 * Thread that monitors traffic between the agent and Adito
 * and updates the current {@link AgentClientGUI} to show when
 * data is received or sent.
 */
public class TXRXMonitor extends Thread {

    // #ifdef DEBUG
    static org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(TXRXMonitor.class);

    // #endif
    
    // Private instance variables
    private long lastRx, lastTx;
    private final Agent client;
    private IOStreamConnectorListener txIo, rxIo;

    /**
     * Constructor.
     *
     * @param client clirny
     */
    public TXRXMonitor(Agent client) {
        super(Messages.getString("TXRXMonitor.threadName")); //$NON-NLS-1$
        setDaemon(true);
        this.client = client;
    }
    
    /**
     * Get the transmit listener
     * 
     * @return transmit listener
     */
    public IOStreamConnectorListener getTxListener() {
        if(txIo == null) {
            txIo = new TXIOStreamConnectorListener();
        }
        return txIo;
    }
    
    /**
     * Get the transmit listener
     * 
     * @return transmit listener
     */
    public IOStreamConnectorListener getRxListener() {
        if(rxIo == null) {
            rxIo = new RXIOStreamConnectorListener();
        }
        return rxIo;
    }

    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    public void run() {

        long rx;
        long tx;
        try {
            while (true) {

                if (this.client.getState() != Agent.STATE_DISCONNECTED) {
                    rx = System.currentTimeMillis() - lastRx;
                    tx = System.currentTimeMillis() - lastTx;

                    if (rx < 500 && tx < 500) {
                        this.client.getGUI().showTxRx();
                    } else if (rx < 500) {
                        this.client.getGUI().showRx();
                    } else if (tx < 500) {
                        this.client.getGUI().showTx();
                    } else {
                        this.client.getGUI().showIdle();
                    }
                }
                else {
                	break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    // #ifdef DEBUG
                    log.info(Messages.getString("TXRXMonitor.interrupted")); //$NON-NLS-1$
                    // #endif
                    break;
                }
            }
        } catch (Throwable ex) {
            // #ifdef DEBUG
            Agent.log.info(Messages.getString("TXRXMonitor.failed"), ex); //$NON-NLS-1$
            // #endif
        }
    }

    class TXIOStreamConnectorListener implements IOStreamConnectorListener {
        public void connectorClosed(IOStreamConnector connector) {
        }

        public void dataTransfered(byte[] data, int count) {
            lastTx = System.currentTimeMillis();
        }
    }

    class RXIOStreamConnectorListener implements IOStreamConnectorListener {
        public void connectorClosed(IOStreamConnector connector) {
        }

        public void dataTransfered(byte[] data, int count) {
            lastRx = System.currentTimeMillis();
        }
    }
}