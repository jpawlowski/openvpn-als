
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
			
package net.openvpn.als.setup.forms;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import net.openvpn.als.core.CoreServlet;
import net.openvpn.als.core.forms.CoreForm;
import net.openvpn.als.tasks.shutdown.ShutdownTimerTask;
import net.openvpn.als.tasks.timer.StoppableTimer;

public class ShutdownForm extends CoreForm {

    public static final String SHUTDOWN = "shutdown";
    public static final String RESTART = "restart";
    public static final String BOTH = "both";
    public static final String SHUTTING_DOWN = "shutting_down";
    
    private String shutdownDelay = "0";

    private String shutdownType;
    private String shutdownOperation;
    private boolean alreadyPerforming = false;
    private String shutdownTime;

    /**
     * @return Returns the shutdownType.
     */
    public String getShutdownType() {
        return shutdownType;
    }

    /**
     * @param shutdownType The shutdownType to set.
     */
    public void setShutdownType(String shutdownType) {
        this.shutdownType = shutdownType;
    }

	public String getShutdownDelay() {
		return shutdownDelay;
	}

	public void setShutdownDelay(String shutdownTime) {
		this.shutdownDelay = shutdownTime;
	}
	
	public boolean isImmediate(){
		if (getShutdownDelay().equals("0"))
			return true;
		else
			return false;
	}

	public boolean getAlreadyPerforming(){
		return this.alreadyPerforming;
	}
	
	public void setAlreadyPerforming(boolean alreadyPerforming) {
		this.alreadyPerforming = alreadyPerforming;
	}
    
    public String getShutdownTime() {
        return shutdownTime;
    }

	public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
	    StoppableTimer timer = (StoppableTimer) CoreServlet.getServlet().getServletContext().getAttribute(StoppableTimer.NAME);
	    if (timer.containsTimerTask(ShutdownTimerTask.NAME)){
            shutdownTime = ((ShutdownTimerTask)timer.getTimerTask(ShutdownTimerTask.NAME)).getShutDownTimeString();
	    	alreadyPerforming = true;
	    }
	    else {
            shutdownTime = null;
	    	alreadyPerforming = false;
	    }

	    ActionErrors errors = new ActionErrors();
        
        try {
            int shutdownDelayInt = Integer.parseInt(getShutdownDelay());
            if(!getAlreadyPerforming() && shutdownDelayInt < 0) {
                errors.add(Globals.ERROR_KEY, new ActionMessage("shutdown.global.error.message.negative"));
                setShutdownDelay("0");
                return errors;
            }
        } catch (Exception e) {
            errors.add(Globals.ERROR_KEY, new ActionMessage("shutdown.global.error.message.decimal"));
            setShutdownDelay("0");
        }
        
        return errors;
	}
    
    public void setShutdownOperation(String shutdownOperation) {
        this.shutdownOperation = shutdownOperation;
    }
    
    public String getShutdownOperation() {
        return shutdownOperation;
    }
	
	
}