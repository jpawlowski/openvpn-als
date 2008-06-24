
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
			
package com.adito.tasks.shutdown;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.adito.boot.ContextHolder;

/**
 */
public class ShutdownTimerTask extends TimerTask {
	/**
	 */
	public static String NAME = "shutdown.task";
    private final static Log log = LogFactory.getLog(ShutdownTimerTask.class);
    /** the minimum shutdown time is 10 seconds */
    private static final long DEFAULT_DELAY = 10000;
    private final boolean restart;
    private final long delay;
    private final Date shutdownAt;
    
    /**
     * Default Constructor.
     * 
     * @param restart
     * @param delayInMinutes
     */
    public ShutdownTimerTask(boolean restart, int delayInMinutes) {
        this.restart = restart;
        this.delay = delayInMinutes == 0 ? DEFAULT_DELAY : (delayInMinutes * 60 * 1000);
        this.shutdownAt = new Date(System.currentTimeMillis() + getDelay());
    }

    /**
     */
    public void run() {
        if (log.isInfoEnabled()) {
            log.info("About to perform shutdown task.");
        }
        ContextHolder.getContext().shutdown(restart);
    }

    /**
     * @return long
     */
    public long getDelay() {
        return delay < DEFAULT_DELAY ? DEFAULT_DELAY : delay;
    }

    /**
     * @return Date
     */
    public Date getShutDownTime() {
        return shutdownAt;
    }
    
    /**
     * @return String
     */
    public String getShutDownTimeString(){
        DateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(getShutDownTime());
    }
}