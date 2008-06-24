
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
			
package com.adito.keystore.wizards.types;

import java.io.File;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.adito.boot.ContextKey;
import com.adito.boot.KeyStoreManager;
import com.adito.core.CoreAttributeConstants;
import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.core.CoreServlet;
import com.adito.keystore.wizards.AbstractKeyStoreImportType;
import com.adito.properties.Property;
import com.adito.security.SessionInfo;
import com.adito.wizard.AbstractWizardSequence;


/**
 * Implementation of a {@link com.adito.keystore.wizards.AbstractKeyStoreImportType}
 * that imports a trusted server certificate.
 */
public class TrustedServerCertificateImportType extends AbstractKeyStoreImportType {
    

    /**
     * Constant for importing server certificates that Adito should trust
     */
    public final static String TRUSTED_SERVER_CERTIFICATE = "trustedServerCertificate";

    /**
     * Constructor.
     */
    public TrustedServerCertificateImportType() {
        super(TRUSTED_SERVER_CERTIFICATE, "keystore", false, true, 30);
        setRestartRequired(false);
    }

    /* (non-Javadoc)
     * @see com.adito.keystore.wizards.AbstractKeyStoreImportType#validate(org.apache.struts.action.ActionMessages, java.lang.String, java.lang.String, com.adito.wizard.AbstractWizardSequence, com.adito.security.SessionInfo)
     */
    public void validate(ActionMessages errs, String alias, String passphrase, AbstractWizardSequence seq, SessionInfo sessionInfo) {
        super.validate(errs, alias, passphrase, seq, sessionInfo);
        if(alias==null || alias.equals("")) {
            errs.add(Globals.ERROR_KEY, new ActionMessage("keyStoreImportWizard.keyStoreImportFile.noNameProvided"));                        
        }
        else {
            KeyStoreManager mgr = KeyStoreManager.getInstance(KeyStoreManager.TRUSTED_SERVER_CERTIFICATES_KEY_STORE);
            if(mgr.getCertificate(alias.toLowerCase()) != null) {
                errs.add(Globals.ERROR_KEY, new ActionMessage("keyStoreImportWizard.keyStoreImportFile.duplicateName", alias.toLowerCase()));                            
            }
        }
    }

    /* (non-Javadoc)
     * @see com.adito.keystore.wizards.AbstractKeyStoreImportType#doInstall(java.io.File, java.lang.String, java.lang.String, com.adito.wizard.AbstractWizardSequence)
     */
    public void doInstall(File file, String alias, String passphrase, AbstractWizardSequence seq, SessionInfo sessionInfo) throws Exception {
        KeyStoreManager mgr = KeyStoreManager.getInstance(KeyStoreManager.TRUSTED_SERVER_CERTIFICATES_KEY_STORE);
        mgr.importCert(alias, file, null);
        mgr.reloadKeystore();
        Certificate certif = mgr.getCertificate(alias);

        CoreEvent coreEvent = new CoreEvent(this, CoreEventConstants.KEYSTORE_TRUSTED_CERTIFICATE_IMPORTED, Property.getProperty(new ContextKey("webServer.alias")), seq.getSession())
                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_ALIAS, alias)
                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_TYPE, certif.getType())
                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_HOSTNAME, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "cn"))
                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_ORGANISATIONAL_UNIT, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "ou"))
                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_COMPANY, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "o"))
                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_STATE, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "st"))
                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_LOCATION, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "l"))
                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_COUNTRY_CODE, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "c"));
        
        CoreServlet.getServlet().fireCoreEvent(coreEvent);
    }

    /* (non-Javadoc)
     * @see com.adito.keystore.wizards.AbstractKeyStoreImportType#init(javax.servlet.http.HttpServletRequest)
     */
    public void init(HttpServletRequest request) {
        super.init(request);
        ActionMessages messages =
            (ActionMessages) request.getAttribute(Globals.MESSAGE_KEY);
        if (messages == null) {
            messages = new ActionMessages();
        }
        messages.add(Globals.MESSAGE_KEY, new ActionMessage("keyStoreImportWizard.keyStoreImportFile.caseWarnimg"));
        request.setAttribute(Globals.MESSAGE_KEY, messages);
    }

}
