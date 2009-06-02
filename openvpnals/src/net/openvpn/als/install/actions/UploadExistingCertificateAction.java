
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
			
package net.openvpn.als.install.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import net.openvpn.als.boot.Util;
import net.openvpn.als.core.actions.AuthenticatedAction;
import net.openvpn.als.install.forms.ImportExistingCertificateForm;
import net.openvpn.als.security.Constants;
import net.openvpn.als.security.SessionInfo;
import net.openvpn.als.vfs.forms.UploadForm;
import net.openvpn.als.wizard.AbstractWizardSequence;

/**
 * Implementatation of a {@link AuthenticatedAction} that allows a certificate
 * to be uploaded during the install wizard.
 * 
 * @see net.openvpn.als.install.forms.ConfigureUserDatabaseForm
 */
public class UploadExistingCertificateAction extends AuthenticatedAction {

    private static final Log log = LogFactory.getLog(UploadExistingCertificateAction.class);

    /* (non-Javadoc)
     * @see net.openvpn.als.core.actions.AuthenticatedAction#onExecute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward onExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        String alias = request.getParameter("alias");
        UploadForm uploadForm = (UploadForm) form;
        FormFile uploadFile = uploadForm.getUploadFile();
        String fileName = uploadFile.getFileName();

        if (fileName.trim().length() == 0) {
            saveError(request, "installation.uploadExistingCertificate.noFileProvided");
            return mapping.getInputForward();
        }

        if (uploadFile.getFileSize() == 0) {
            saveError(request, "installation.uploadExistingCertificate.invalidFile");
            return mapping.getInputForward();
        }

        if (log.isInfoEnabled())
            log.info("Uploading certificate with alias " + alias);
        File keystoreFile = File.createTempFile("uploadedFile", "");
        doUpload(request, uploadFile, keystoreFile);

        String keyStoreType = request.getParameter("keyStoreType");
        String password = request.getParameter("password");
        if (!validateKeyStore(keyStoreType, password, keystoreFile)) {
            saveError(request, "installation.uploadExistingCertificate.importFailure");
            return mapping.getInputForward();
        }

        AbstractWizardSequence seq = (AbstractWizardSequence) request.getSession().getAttribute(Constants.WIZARD_SEQUENCE);
        seq.putAttribute(ImportExistingCertificateForm.ATTR_PASSPHRASE, password);
        seq.putAttribute(ImportExistingCertificateForm.ATTR_UPLOADED_FILE, keystoreFile);
        seq.putAttribute(ImportExistingCertificateForm.ATTR_KEY_STORE_TYPE, keyStoreType);
        seq.putAttribute(ImportExistingCertificateForm.ATTR_ALIAS, alias.trim());
        saveMessage(request, "installation.uploadExisting.uploaded", fileName, uploadFile.getFileSize());
        return mapping.findForward("success");
    }

    private boolean doUpload(HttpServletRequest request, FormFile uploadFile, File keystoreFile) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = uploadFile.getInputStream();
            outputStream = new FileOutputStream(keystoreFile);
            Util.copy(inputStream, outputStream);
            return true;
        } catch (Exception e) {
            String errorMessage = e.getMessage() == null ? "No message provided." : e.getMessage();
            saveError(request, "installation.uploadExistingCertificate.failedToUploadFile", uploadFile.getFileName(), errorMessage);
            return false;
        } finally {
            Util.closeStream(inputStream);
            Util.closeStream(outputStream);
        }
    }

    private boolean validateKeyStore(String keyStoreType, String password, File keystoreFile) {
        InputStream inputStream = null;
        try {
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            inputStream = new FileInputStream(keystoreFile);
            keyStore.load(inputStream, password.toCharArray());
            return true;
        } catch (Exception e) {
            log.error("Validation of key store failed", e);
            return false;
        } finally {
            Util.closeStream(inputStream);
        }
    }

    public int getNavigationContext(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        return SessionInfo.MANAGEMENT_CONSOLE_CONTEXT | SessionInfo.SETUP_CONSOLE_CONTEXT;
    }
}