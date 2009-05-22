
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
			
package com.ovpnals.keystore.wizards.forms;

import javax.servlet.http.HttpServletRequest;

import com.ovpnals.keystore.wizards.types.ReplyFromCAImportType;
import com.ovpnals.wizard.AbstractWizardSequence;
import com.ovpnals.wizard.forms.DefaultWizardForm;

/**
 * Extension of {@link com.ovpnals.wizard.forms.DefaultWizardForm} used
 * in the key store import wizard. Here the administrator selects the type
 * of import.
 */

public class KeyStoreImportTypeForm extends DefaultWizardForm {
    
    // Statics for sequence attributes
    
    /**
     * Constant for import type wizard sequence attribute 
     */
    public final static String ATTR_TYPE = "type";

    // Private instance variables
    
    private String type;

    /**
     * Constructor.
     */
    public KeyStoreImportTypeForm() {
        super(true, false, "/WEB-INF/jsp/content/keystore/keyStoreImportWizard/type.jspf", 
            "rootServerCertificate", true, false, "keyStoreImportType", "keystore", "keyStoreImportWizard.keyStoreImportType", 1);
    }

    /* (non-Javadoc)
     * @see com.ovpnals.wizard.forms.AbstractWizardForm#init(com.ovpnals.wizard.AbstractWizardSequence)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request)  throws Exception {
        setType((String)sequence.getAttribute(ATTR_TYPE, ReplyFromCAImportType.REPLY_FROM_CA));
    }

    /**
     * Get the type of import. 
     * 
     * @return the type of import
     */
    public String getType() {
        return type;
    }

    /**
     * Set the type of import. 
     * 
     * @param type the type of import
     */
    public void setType(String type) {
        this.type = type;
    }

    /* (non-Javadoc)
     * @see com.ovpnals.wizard.forms.AbstractWizardForm#apply(com.ovpnals.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence)  throws Exception {
        sequence.putAttribute(ATTR_TYPE, getType());
    }
}
