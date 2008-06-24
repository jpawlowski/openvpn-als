
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
			
package com.adito.keystore.wizards.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.upload.FormFile;

import com.adito.keystore.wizards.AbstractKeyStoreImportType;
import com.adito.keystore.wizards.KeyStoreImportTypeManager;
import com.adito.keystore.wizards.types.ReplyFromCAImportType;
import com.adito.wizard.AbstractWizardSequence;
import com.adito.wizard.forms.DefaultWizardForm;

/**
 * Extension of {@link com.adito.wizard.forms.DefaultWizardForm}
 * that provides summary information for the key store import wizard.
 */
public class KeyStoreImportSummaryForm extends DefaultWizardForm {
    
    final static Log log = LogFactory.getLog(KeyStoreImportSummaryForm.class);
    
    // Private instance variables
    private String type;
    private String filename;
    private String alias;
    private AbstractKeyStoreImportType importType;

    /**
     * Constructor
     */
    public KeyStoreImportSummaryForm() {
        super(false, true, "/WEB-INF/jsp/content/keystore/keyStoreImportWizard/summary.jspf",
            "", true, true, "keyStoreImportSummary", "keystore", "keyStoreImportWizard.keyStoreImportSummary", 3);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.wizard.forms.AbstractWizardForm#init(com.adito.wizard.AbstractWizardSequence)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request)  throws Exception {
        type = (String)sequence.getAttribute(KeyStoreImportTypeForm.ATTR_TYPE, "");
        filename = ((FormFile)sequence.getAttribute(KeyStoreImportFileForm.ATTR_FILENAME, "")).getFileName();
        alias = (String)sequence.getAttribute(KeyStoreImportFileForm.ATTR_ALIAS, "");
        importType = KeyStoreImportTypeManager.getInstance().getType(
            (String)sequence.getAttribute(KeyStoreImportTypeForm.ATTR_TYPE, ReplyFromCAImportType.REPLY_FROM_CA));
    }

    /**
     * Get the filename upload
     * 
     * @return uploaded filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Get the type name of import. 
     * 
     * @return type of import
     */
    public String getType() {
        return type;
    }
    
    /**
     * Get the alias of the certificate (if provided)
     * 
     * @return alias
     */
    public String getAlias() {
        return alias;
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
