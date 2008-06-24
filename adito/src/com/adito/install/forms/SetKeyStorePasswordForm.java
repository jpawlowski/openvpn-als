
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
			
package com.adito.install.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.forms.DefaultWizardForm;

public class SetKeyStorePasswordForm extends DefaultWizardForm {
    

    // Private statics for sequence attributes
    public final static String ATTR_KEY_STORE_PASSWORD = "keyStorePassword";

    // Statics
    final static String DUMMY_PASSWORD = "**********";
    
    // Private instance variables
    private String confirmKeyPassword;
    private String keyPassword;
    private String currentPassword;

    public SetKeyStorePasswordForm() {
        super(true, true, "/WEB-INF/jsp/content/install/setKeyStorePassword.jspf",
            "keyPassword", false, false, "setKeyStorePassword", "install",
            "installation.setKeyStorePassword", 1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.forms.AbstractWizardForm#init(com.adito.wizard.AbstractWizardSequence)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request) throws Exception { 
        String password = (String)sequence.getAttribute(ATTR_KEY_STORE_PASSWORD, currentPassword);
        if (password != null && !password.equals("")) {
            keyPassword = DUMMY_PASSWORD;
            confirmKeyPassword = DUMMY_PASSWORD;
        } else {
            keyPassword = "";
            confirmKeyPassword = "";
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.forms.AbstractWizardForm#apply(com.adito.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        sequence.putAttribute(ATTR_KEY_STORE_PASSWORD, DUMMY_PASSWORD.equals(keyPassword) ? 
                        currentPassword :
                            keyPassword);
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    public String getKeyPassword() {
        return keyPassword;
    }

    public void setConfirmKeyPassword(String confirmKeyPassword) {
        this.confirmKeyPassword = confirmKeyPassword;
    }

    public String getConfirmKeyPassword() {
        return confirmKeyPassword;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping,
     *      javax.servlet.http.HttpServletRequest)
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(isCommiting() && keyPassword != null) {
            try {
                // keyPassword length is less than six characters long
                // This will also catch keyPassword when null
                if (keyPassword.length() < 6) {
                    throw new Exception("passwordToShort");
                }
                // keypasswords do not match
                if (!keyPassword.equals(DUMMY_PASSWORD) && !keyPassword.equals(confirmKeyPassword)) {
                    throw new Exception("keyPasswordsDoNotMatch");
                }
            } catch (Exception e) {
                // Always report to user when an error is encountered
                ActionErrors errs = new ActionErrors();
                errs.add(Globals.ERROR_KEY, new ActionMessage("installation.setKeyStorePassword.error." + e.getMessage()));
                return errs;
            }
        }
        return null;
    }
}
