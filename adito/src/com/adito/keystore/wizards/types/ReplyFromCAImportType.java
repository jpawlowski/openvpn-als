
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
 * that imports a reply from a CA.
 */
public class ReplyFromCAImportType extends AbstractKeyStoreImportType {
    
    
    /**
     * Constant for importing a reply from a CA 
     */
    public final static String REPLY_FROM_CA = "replyFromCA";

    /**
     * Constructor.
     */
    public ReplyFromCAImportType() {
        super(REPLY_FROM_CA, "keystore", false, false, 10);
    }

    /* (non-Javadoc)
     * @see com.adito.keystore.wizards.AbstractKeyStoreImportType#doInstall(java.io.File, java.lang.String, java.lang.String, com.adito.wizard.AbstractWizardSequence)
     */
    public void doInstall(File file, String alias, String passphrase, AbstractWizardSequence seq, SessionInfo sessionInfo) throws Exception {
        KeyStoreManager mgr = KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE);
        String pw = Property.getProperty(new ContextKey("webServer.keystore.sslCertificate.password"));
        mgr.importCert(Property.getProperty(new ContextKey("webServer.alias")), file, pw);
        mgr.reloadKeystore();
        Certificate certif = mgr.getCertificate(Property.getProperty(new ContextKey("webServer.alias")));
        CoreEvent coreEvent = new CoreEvent(this, CoreEventConstants.KEYSTORE_CERTIFICATE_SIGNED_IMPORTED, Property.getProperty(new ContextKey("webServer.alias")), seq.getSession())
                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_ALIAS, Property.getProperty(new ContextKey("webServer.alias")))
                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_TYPE, certif.getType())
                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_HOSTNAME, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "cn"))
                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_ORGANISATIONAL_UNIT, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "ou"))
                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_COMPANY, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "o"))
                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_STATE, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "st"))
                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_LOCATION, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "l"))
                        .addAttribute(CoreAttributeConstants.EVENT_ATTR_CERTIFICATE_COUNTRY_CODE, KeyStoreManager.getX509CertificateEntity((X509Certificate)certif, "c"));

        CoreServlet.getServlet().fireCoreEvent(coreEvent);
        
        Property.setProperty(new ContextKey("webServer.disableCertificateWarning"), true, sessionInfo);
    }
}
