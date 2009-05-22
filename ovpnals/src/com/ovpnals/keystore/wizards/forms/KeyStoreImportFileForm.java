
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

import org.apache.struts.upload.FormFile;

import com.ovpnals.keystore.wizards.AbstractKeyStoreImportType;
import com.ovpnals.keystore.wizards.KeyStoreImportTypeManager;
import com.ovpnals.keystore.wizards.types.ReplyFromCAImportType;
import com.ovpnals.wizard.AbstractWizardSequence;
import com.ovpnals.wizard.forms.DefaultWizardForm;

/**
 * Extension of a {@link com.ovpnals.wizard.forms.DefaultWizardForm} used
 * for entering the file upload details for a key store import.
 * <p>
 * Note, due to the nature of the Java keystore the alias will be lower
 * case regardless of the case the user gives it.
 */

public class KeyStoreImportFileForm extends DefaultWizardForm {
    // Statics for sequence attributes
    
    /**
     * Constant for passphrase wizard seqeunce attribute
     */
    public static final String ATTR_PASSPHRASE = "passphrase";
    
    /**
     * Constant for uploaded file wizard sequence attribute (will be a File
     * object)
     */
    public static final String ATTR_UPLOADED_FILE = "uploadedFile";
    
    /**
     * Constant for filename wizard sequence attribute
     */
    public static final Object ATTR_FILENAME = "filename";
    
    /**
     * Constant for alias wizard sequence attribute
     */
    public static final Object ATTR_ALIAS = "alias";

    // Private instance variables
    private String alias;
    private String passphrase;
    private FormFile uploadFile;
    private AbstractKeyStoreImportType importType;

    /**
     * Constructor
     */
    public KeyStoreImportFileForm() {
        super(true, true, "/WEB-INF/jsp/content/keystore/keyStoreImportWizard/file.jspf",
            "passphrase", false, false, "keyStoreImportFile", "keystore", "keyStoreImportWizard.keyStoreImportFile", 2);
    }

    /* (non-Javadoc)
     * @see com.ovpnals.wizard.forms.AbstractWizardForm#getFormEncoding()
     */
    public String getFormEncoding() {
        return "multipart/form-data";
    }

    /* (non-Javadoc)
     * @see com.ovpnals.wizard.forms.AbstractWizardForm#getNextOnClick()
     */
    public String getNextOnClick() {
        return "upload();";
    }

    /* (non-Javadoc)
     * @see com.ovpnals.wizard.forms.AbstractWizardForm#init(com.ovpnals.wizard.AbstractWizardSequence, javax.servlet.http.HttpServletRequest)
     */
    public void init(AbstractWizardSequence wizardSequence, HttpServletRequest request) throws Exception {
        super.init(wizardSequence, request);
        uploadFile = (FormFile)wizardSequence.getAttribute(ATTR_FILENAME, null);
        passphrase = (String)wizardSequence.getAttribute(ATTR_PASSPHRASE, "");
        alias = (String)wizardSequence.getAttribute(ATTR_ALIAS, "");
        importType = KeyStoreImportTypeManager.getInstance().getType(
                (String)wizardSequence.getAttribute(KeyStoreImportTypeForm.ATTR_TYPE, ReplyFromCAImportType.REPLY_FROM_CA));
    }

    /* (non-Javadoc)
     * @see com.ovpnals.wizard.forms.AbstractWizardForm#apply(com.ovpnals.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence) throws Exception {
        super.apply(sequence);
        sequence.putAttribute(ATTR_FILENAME, uploadFile);
        sequence.putAttribute(ATTR_ALIAS, alias == null ? "" : alias.toLowerCase());
        sequence.putAttribute(ATTR_PASSPHRASE, passphrase == null ? "" : passphrase);
    }
    
    /**
     * Read-only getter to determine if the <b>Name</b> field should be
     * requested
     * 
     * @return ask for name
     */
    public boolean getAskForName() {
        return KeyStoreImportTypeManager.getInstance().getType((String)getWizardSequence().getAttribute(KeyStoreImportTypeForm.ATTR_TYPE, 
            ReplyFromCAImportType.REPLY_FROM_CA)).isRequiresAlias();
    }
    
    /**
     * Read-only getter to determine if the <b>Passphrase</b> field should be
     * requested
     * 
     * @return ask for passphrase
     */
    public boolean getAskForPassphrase() {
        return KeyStoreImportTypeManager.getInstance().getType((String)getWizardSequence().getAttribute(KeyStoreImportTypeForm.ATTR_TYPE, 
            ReplyFromCAImportType.REPLY_FROM_CA)).isRequiresPassphrase();
    }

    /**
     * Get the alias.
     * 
     * @return alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Set the alias. 
     * 
     * @param alias alias 
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }
     
    /**
     * Get the passphrase
     * 
     * @return passphrase
     */
    public String getPassphrase() {
        return passphrase;
    }

    /**
     * Set the passphrase
     * 
     * @param passphrase passphrase
     */
    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    /**
     * Get the file to upload
     * 
     * @return upload file
     */
    public FormFile getUploadFile() {
        return uploadFile;
    }

    /**
     * Set the file to upload
     * 
     * @param uploadFile upload file
     */
    public void setUploadFile(FormFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    /* (non-Javadoc)
     * @see com.ovpnals.wizard.forms.AbstractWizardForm#getFocussedField()
     */
    public String getFocussedField() {
        return getAskForName() ? "name" : (getAskForPassphrase() ? "passphrase" : "uploadFile") ;
    }
    
    /**
     * Get the selected import type
     * 
     * @return import type
     */
    public AbstractKeyStoreImportType getImportType() {
        return importType;
    }
}
