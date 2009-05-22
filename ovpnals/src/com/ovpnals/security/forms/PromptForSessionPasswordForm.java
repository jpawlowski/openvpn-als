
				/*
 *  OpenVPN-ALS
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
			
package com.ovpnals.security.forms;

import org.apache.struts.action.ActionMapping;

import com.ovpnals.core.forms.CoreForm;

/**
 * <p>
 * Form to hold the prompted session password.
 */
public class PromptForSessionPasswordForm extends CoreForm {

    // Private instance varaibles
    private String password;
    private String forwardTo;
    private String target;
    private String folder;

    /**
     * Get the password.
     * 
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password.
     * 
     * @param password password
     */
    public void setPassword(String password) {
        this.password = password.trim();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, javax.servlet.http.HttpServletRequest request) {
        super.reset(mapping, request);
        password = null;
    }

    /**
     * Set where the browser should go to when the password has been entered.
     * 
     * @param forwardTo forward to address
     */
    public void setForwardTo(String forwardTo) {
        this.forwardTo = forwardTo;        
    }

    /**
     * Get the address to forward to.
     * 
     * @return forward to address
     */
    public String getForwardTo() {
        return forwardTo;
    }

    /**
     * Set the target frame of the forwardTo to forward to.
     * 
     * @param target target frame
     */
    public void setTarget(String target) {
        this.target = target;        
    }

    /**
     * Get the target frame of the forwardTo to forward to.
     * 
     * @return target frame
     */
    public String getTarget() {
        return target;        
    }

    /**
     * Set the folder of the forwardTo to forward to. This is used for web folders
     * only
     * 
     * @param folder folder
     */
    public void setFolder(String folder) {
        this.folder = folder;        
    }

    /**
     * Get the folder of the forwardTo to forward to. This is used for web folders
     * only
     * 
     * @return folder
     */
    public String getFolder() {
        return folder;        
    }
}