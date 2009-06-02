
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
			
package net.openvpn.als.install.forms;

import javax.servlet.http.HttpServletRequest;

import net.openvpn.als.boot.KeyStoreManager;
import net.openvpn.als.wizard.AbstractWizardSequence;
import net.openvpn.als.wizard.forms.DefaultWizardForm;


/**
 * Extensions of a {@link net.openvpn.als.wizard.forms.DefaultWizardForm} that
 * allows a source for the SSL certificate to use. The user may choose to
 * create a new certificate, use the existing one (if it exists) or import
 * a certificate.
 */
public class SelectCertificateSourceForm extends DefaultWizardForm {
    
    /**
     * Constant for create new certificate. See {@link #setCertificateSource(String)}. 
     */
    public final static String CREATE_NEW_CERTIFICATE = "createNew";

    /**
     * Constant for import existing certificate. See {@link #setCertificateSource(String)}. 
     */
    public final static String IMPORT_EXISTING_CERTIFICATE = "importExisting";

    /**
     * Constant for use current certificate. See {@link #setCertificateSource(String)}. 
     */
    public final static String USE_CURRENT_CERTIFICATE = "useCurrent";
    
    // Statics for sequence attributes
    
    /**
     * Selected Certificate source
     */
    public final static String ATTR_CERTIFICATE_SOURCE = "certificateSource";

    // Private instance variables
    
    private String certificateSource;

    /**
     * Constructor
     */
    public SelectCertificateSourceForm() {
        super(true, false, "/WEB-INF/jsp/content/install/selectCertificateSource.jspf",
            "", true, false, "selectCertificateSource", "install", "installation.selectCertificateSource", 1);
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#init(net.openvpn.als.wizard.AbstractWizardSequence)
     */
    public void init(AbstractWizardSequence sequence, HttpServletRequest request)  throws Exception {
        setCertificateSource((String)sequence.getAttribute(ATTR_CERTIFICATE_SOURCE, getCertificatesAvailable() ? USE_CURRENT_CERTIFICATE : CREATE_NEW_CERTIFICATE));
    }

    /**
     * Get the currently selected certificate source. Can be one
     * of {@link SelectCertificateSourceForm#CREATE_NEW_CERTIFICATE}, {@link #IMPORT_EXISTING_CERTIFICATE}
     * or {@link #USE_CURRENT_CERTIFICATE}.
     * 
     * @return the selected certificate source.
     */
    public String getCertificateSource() {
        return certificateSource;
    }

    /**
     * Set the currently selected certificate source. Can be one
     * of {@link SelectCertificateSourceForm#CREATE_NEW_CERTIFICATE}, {@link #IMPORT_EXISTING_CERTIFICATE}
     * or {@link #USE_CURRENT_CERTIFICATE}.
     * 
     * @param certificateSource the selected certificate source.
     */
    public void setCertificateSource(String certificateSource) {
        this.certificateSource = certificateSource;
    }
    
    /**
     * Get if the key store exists, is not empty and does not contain any errors. If so
     * the user may select 'Use Current' 
     * 
     * @return certificates available
     */
    public boolean getCertificatesAvailable() {
        return KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE).isKeyStoreExists() && 
            !KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE).isKeyStoreEmpty() &&
            KeyStoreManager.getInstance(KeyStoreManager.DEFAULT_KEY_STORE).getKeyStoreException() == null;
    }

    /* (non-Javadoc)
     * @see net.openvpn.als.wizard.forms.AbstractWizardForm#apply(net.openvpn.als.wizard.AbstractWizardSequence)
     */
    public void apply(AbstractWizardSequence sequence)  throws Exception {
        sequence.putAttribute(ATTR_CERTIFICATE_SOURCE, getCertificateSource());
    }
}
