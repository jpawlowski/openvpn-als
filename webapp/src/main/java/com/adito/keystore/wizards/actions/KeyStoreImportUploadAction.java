
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
			
package com.adito.keystore.wizards.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;

import com.adito.boot.Util;
import com.adito.core.actions.AuthenticatedAction;
import com.adito.keystore.wizards.AbstractKeyStoreImportType;
import com.adito.keystore.wizards.KeyStoreImportTypeManager;
import com.adito.keystore.wizards.forms.KeyStoreImportFileForm;
import com.adito.keystore.wizards.forms.KeyStoreImportTypeForm;
import com.adito.keystore.wizards.types.ReplyFromCAImportType;
import com.adito.policyframework.Permission;
import com.adito.policyframework.PolicyConstants;
import com.adito.security.Constants;
import com.adito.security.SessionInfo;
import com.adito.vfs.forms.UploadForm;
import com.adito.wizard.AbstractWizardSequence;

/**
 * Action to process the file upload initiated during the key store import
 * wizard.
 */
public class KeyStoreImportUploadAction extends AuthenticatedAction {

    static Log log = LogFactory.getLog(KeyStoreImportUploadAction.class);

    /**
     * Constructor
     */
    public KeyStoreImportUploadAction() {
        super(PolicyConstants.KEYSTORE_RESOURCE_TYPE, new Permission[] { PolicyConstants.PERM_CHANGE });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        String passphrase = request.getParameter("passphrase");
        String alias = request.getParameter("alias");
        UploadForm uploadForm = (UploadForm) form;
        FormFile uploadFile = uploadForm.getUploadFile();
        String fileName = uploadFile.getFileName();
        int fileSize = uploadFile.getFileSize();
        InputStream in = null;
        OutputStream out = null;
        File uploadedFile = File.createTempFile("uploadedFile", "");
        ActionMessages errs = new ActionMessages();

        try {
            if (fileName.trim().length() == 0) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("keyStoreImportWizard.keyStoreImportFile.noFileProvided"));
            } else {

                AbstractWizardSequence seq = (AbstractWizardSequence) request.getSession().getAttribute(Constants.WIZARD_SEQUENCE);

                AbstractKeyStoreImportType importType = KeyStoreImportTypeManager.getInstance().getType(
                    (String)seq.getAttribute(KeyStoreImportTypeForm.ATTR_TYPE, ReplyFromCAImportType.REPLY_FROM_CA));
                SessionInfo sessionInfo = getSessionInfo(request);
                importType.validate(errs, alias, passphrase, seq, sessionInfo);

                if (errs.size() == 0) {

                    in = uploadFile.getInputStream();
                    out = new FileOutputStream(uploadedFile);
                    Util.copy(in, out);

                    if (passphrase != null) {
                        seq.putAttribute(KeyStoreImportFileForm.ATTR_PASSPHRASE, passphrase);
                    }
                    seq.putAttribute(KeyStoreImportFileForm.ATTR_UPLOADED_FILE, uploadedFile);
                    seq.putAttribute(KeyStoreImportFileForm.ATTR_FILENAME, uploadFile);
                    if (alias != null) {
                        seq.putAttribute(KeyStoreImportFileForm.ATTR_ALIAS, alias.toLowerCase());
                    }

                    ActionMessages msgs = new ActionMessages();
                    msgs.add(Globals.MESSAGE_KEY, new ActionMessage("keyStoreImportWizard.keyStoreImportFile.uploaded", fileName,
                                    new Integer(fileSize)));
                }
            }
        } catch (Exception e) {
            log.error("Failed to upload to upload key store import file.", e);
            errs.add(Globals.ERROR_KEY, new ActionMessage("keyStoreImportWizard.keyStoreImportFile.failedToUploadFile", fileName, e
                            .getMessage() == null ? "No message provided." : e.getMessage()));
        } finally {
            Util.closeStream(in);
            Util.closeStream(out);
        }
        saveErrors(request, errs);
        return errs.size() == 0 ? mapping.findForward("success") : new ActionForward(mapping.getInput(), false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.adito.core.actions.CoreAction#getNavigationContext(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT;
    }

}
