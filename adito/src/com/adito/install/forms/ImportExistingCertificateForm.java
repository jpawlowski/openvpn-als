
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

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.adito.boot.KeyStoreManager;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.forms.DefaultWizardForm;

/**
 * Form for importing existing certificate.
 */
public class ImportExistingCertificateForm extends DefaultWizardForm {

    /**
     * Constant for passphrase.
     */
    public static final String ATTR_PASSPHRASE = "passphrase";
    
    /**
     * Constant for the upload file.
     */
    public static final String ATTR_UPLOADED_FILE = "uploadedFile";
    
    /**
     * Constant for the key store type.
     */
    public static final String ATTR_KEY_STORE_TYPE = "keyStoreType";
    
    /**
     * Constant for alias.
     */
    public static final String ATTR_ALIAS = "alias";

    // Private instance variables
    
    private String keyStoreType;
    private String alias;
            
    /**
     * Constructor
     */
    public ImportExistingCertificateForm() {
        super(true, true, "/WEB-INF/jsp/content/install/importExistingCertificate.jspf",
            "password", false, false, "importExistingCertificate", 
            "install", "installation.importExistingCertificate", 1);
    }
    
    /* (non-Javadoc)
     * @see com.adito.wizard.forms.DefaultWizardForm#init(com.adito.wizard.AbstractWizardSequence, javax.servlet.http.HttpServletRequest)
     */
    public void init(AbstractWizardSequence wizardSequence, HttpServletRequest request) throws Exception {
        super.init(wizardSequence, request);
        keyStoreType = (String)wizardSequence.getAttribute(ATTR_KEY_STORE_TYPE, getAvailableKeyStoreTypes().get(0).toString());
        alias = "";
    }

    /**
     * @return List
     */
    public List getAvailableKeyStoreTypes() {
        return KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE).getSupportedKeyStoreTypes();
    }

    /**
     * @return String
     */
    public String getAlias() {
        return alias;
    }
    
    /**
     * @param alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    /**
     * @return String
     */
    public String getKeyStoreType() {
        return keyStoreType;
    }
    
    /**
     * @param keyStoreType
     */
    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    /* (non-Javadoc)
     * @see com.adito.wizard.forms.AbstractWizardForm#getFormEncoding()
     */
    public String getFormEncoding() {
        return "multipart/form-data";
    }

    /* (non-Javadoc)
     * @see com.adito.wizard.forms.AbstractWizardForm#getNextOnClick()
     */
    public String getNextOnClick() {
        return "uploadCertificate();";
    }
}
