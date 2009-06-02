
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
			
package net.openvpn.als.security.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import net.openvpn.als.core.FieldValidationException;
import net.openvpn.als.core.forms.CoreForm;


/**
 * Form implementation this is used to enter the private key passphrase 
 * when it is required.
 * 
 * This will happen for when the <b>no</b> authentication modules used to login 
 * used the account password.
 * 
 * @see net.openvpn.als.security.actions.UpdatePrivateKeyPassphraseDispatchAction
 */
public class PromptForPrivateKeyPassphraseForm extends CoreForm {

    // Private instance varaibles
    private String passphrase;
    private String confirmPassphrase;
    private boolean newKey;
    
    /**
     * Set whether this is a password for a new key or not. If <code>true</code>
     * then the UI will make <i>Confirm Passphrase</i> available.
     * 
     * @param newKey new key
     */
    public void setNewKey(boolean newKey) {
        this.newKey = newKey;
    }
    
    /**
     * Get whether this is a password for a new key or not. If <code>true</code>
     * then the UI will make <i>Confirm Passphrase</i> available.
     * 
     * @return new key
     */
    public boolean getNewKey() {
        return newKey;
    }

    /**
     * Get the passphrase.
     * 
     * @return passphrase
     */
    public String getPassphrase() {
        return passphrase;
    }

    /**
     * Set the passphrase.
     * 
     * @param passphrase passphrase
     */
    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase.trim();
    }

    /**
     * Get the confirmed passphrase.
     * 
     * @return confirmed passphrase
     */
    public String getConfirmPassphrase() {
        return confirmPassphrase;
    }

    /**
     * Set the confirmed passphrase.
     * 
     * @param confirmPassphrase confirm passphrase
     */
    public void setConfirmPassphrase(String confirmPassphrase) {
        this.confirmPassphrase = confirmPassphrase.trim();
    }
    
    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, javax.servlet.http.HttpServletRequest request) {
        super.reset(mapping, request);
        passphrase = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(isCommiting()) {
            ActionErrors errors = new ActionErrors();        
            try {
                if (getPassphrase().length() == 0) {
                    throw new FieldValidationException("noPassphrase");
                }
                if(getNewKey()) {
                    if(!getPassphrase().equals(getConfirmPassphrase())) {        
                        throw new FieldValidationException("passphraseAndConfirmPassphraseDontMatch");
                    }
                }
            } catch (FieldValidationException fve) {
                errors.add(Globals.ERROR_KEY, new ActionMessage("promptForPrivateKeyPassphrase.error." + fve.getResourceKey()));
            } catch (Exception e) {
                errors.add(Globals.ERROR_KEY, new ActionMessage("promptForPrivateKeyPassphrase.error.validateFailed", e.getMessage()));
            }
            return errors;
        }
        else {
            return null;
        }
    }
}