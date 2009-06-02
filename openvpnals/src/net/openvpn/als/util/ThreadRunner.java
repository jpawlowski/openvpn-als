
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
			
package net.openvpn.als.util;

/**
 * General purpose runner for Runnable tasks.
 */
public final class ThreadRunner implements Runnable {
    private final Thread runner;
    private final Runnable runnable;
    private final int sleepPeriod;
    private boolean running;
    
    /**
     * Constructor
     * @param name
     * @param runnable
     * @param sleepPeriod
     */
    public ThreadRunner(String name, Runnable runnable, int sleepPeriod) {
        this.runner = new Thread(this);
        this.runner.setName(name + "-" + runner.getId());
        this.runner.setPriority(Thread.NORM_PRIORITY);
        this.runnable = runnable;
        this.sleepPeriod = sleepPeriod;
    }
    
    private synchronized boolean isRunning() {
        return running;
    }
    
    private synchronized void setRunning(boolean running) {
        this.running = running;
    }
    
    /**
     * Causes this runner to begin execution
     * <p>
     * @exception  IllegalThreadStateException  if the runner was already started.
     */
    public void start() {
        if(isRunning()) {
            throw new IllegalStateException("ThreadRunner is already running.");
        }
        setRunning(true);
        runner.start();
    }
    
    /**
     * Asks the runner to stop execution.  This will interrupt
     * the runner if sleeping or trying to end the current job.
     */
    public void stop() {
        setRunning(false);
        runner.interrupt();
    }
    
    public void run() {
        while (isRunning()) {
            runnable.run();
            sleep();
        }
    }
    
    private void sleep() {
        try {
            Thread.sleep(sleepPeriod);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}