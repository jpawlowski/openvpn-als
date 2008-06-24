
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

import org.apache.struts.action.ActionMessages;

import com.adito.boot.KeyStoreManager;
import com.adito.core.CoreAttributeConstants;
import com.adito.core.CoreEvent;
import com.adito.core.CoreEventConstants;
import com.adito.core.CoreServlet;
import com.adito.keystore.wizards.AbstractKeyStoreImportType;
import com.adito.security.SessionInfo;
import com.adito.wizard.AbstractWizardSequence;


/**
 * Implementation of a {@link com.adito.keystore.wizards.AbstractKeyStoreImportType}
 * that imports a server authentication key.
 */
public class ServerAuthenticationKeyImportType extends AbstractKeyStoreImportType {
    
    
    /**
     * Constant for importing a certificate authentication for a server SSL explorer will connect to  
     */
    public final static String SERVER_AUTHENTICATION_KEY = "serverAuthenticationKey";

    /**
     * Constructor.
     */
    public ServerAuthenticationKeyImportType() {
        super(SERVER_AUTHENTICATION_KEY, "keystore", true, true, 40);
    }

    /* (non-Javadoc)
     * @see com.adito.keystore.wizards.AbstractKeyStoreImportType#validate(org.apache.struts.action.ActionMessages, java.lang.String, java.lang.String, com.adito.wizard.AbstractWizardSequence, com.adito.security.SessionInfo)
     */
    public void validate(ActionMessages errs, String alias, String passphrase, AbstractWizardSequence seq, SessionInfo sessionInfo) {
       // We *can* take an alias but do not have to
    }

    /* (non-Javadoc)
     * @see com.adito.keystore.wizards.AbstractKeyStoreImportType#doInstall(java.io.File, java.lang.String, java.lang.String, com.adito.wizard.AbstractWizardSequence)
     */
    public void doInstall(File file, String alias, String passphrase, AbstractWizardSequence seq, SessionInfo sessionInfo) throws Exception {
        KeyStoreManager mgr = KeyStoreManager.getInstance(KeyStoreManager.SERVER_AUTHENTICATION_CERTIFICATES_KEY_STORE);
        alias = mgr.importPKCS12Key(file, passphrase, alias, alias);
        mgr.reloadKeystore();
        Certificate certif = mgr.getCertificate(alias);

        CoreEvent coreEvent = new CoreEvent(this, CoreEventConstants.KEYSTORE_SERVER_AUTHENTICATION_CERTIFICATE_IMPORTED, KeyStoreManager.SERVER_AUTHENTICATION_CERTIFICATES_KEY_STORE, seq.getSession())
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
}
