
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
			
package com.adito.security.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.adito.core.FieldValidationException;
import com.adito.core.forms.CoreForm;


/**
 * Form implementation this is used to enter the old password when the users
 * private key passphrase must be changed.
 * 
 * This may happen for example if the key was created using their account password
 * which has since changed.
 * 
 * @see com.adito.security.actions.UpdatePrivateKeyPassphraseDispatchAction
 */
public class UpdatePrivateKeyPassphraseForm extends CoreForm {

    // Private instance varaibles
    String oldPassphrase;
    boolean resetPrivateKey = false;

    /**
     * Get the old password.
     * 
     * @return old password
     */
    public String getOldPassphrase() {
        return oldPassphrase;
    }

    /**
     * Set the old password.
     * 
     * @param oldPassword old password
     */
    public void setOldPassphrase(String oldPassword) {
        this.oldPassphrase = oldPassword.trim();
    }
    
    /* (non-Javadoc)
     * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
     */
    public void reset(ActionMapping mapping, javax.servlet.http.HttpServletRequest request) {
        super.reset(mapping, request);
        oldPassphrase = null;
        resetPrivateKey = false;
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
                if (getOldPassphrase().length() == 0 && !resetPrivateKey) {
                    throw new FieldValidationException("noOldPassphrase");
                }
            } catch (FieldValidationException fve) {
                errors.add(Globals.ERROR_KEY, new ActionMessage("updatePrivateKeyPassphrase.error." + fve.getResourceKey()));
            } catch (Exception e) {
                errors.add(Globals.ERROR_KEY, new ActionMessage("updatePrivateKeyPassphrase.error.validateFailed", e.getMessage()));
            }
            return errors;
        }
        else {
            return null;
        }
    }

    public boolean isResetPrivateKey() {
        return resetPrivateKey;
    }

    public void setResetPrivateKey(boolean resetPrivateKey) {
        this.resetPrivateKey = resetPrivateKey;
    }
}