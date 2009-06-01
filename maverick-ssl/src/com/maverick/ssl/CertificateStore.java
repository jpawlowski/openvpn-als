
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
			
package com.maverick.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Hashtable;

import com.maverick.crypto.asn1.ASN1Sequence;
import com.maverick.crypto.asn1.DERInputStream;
import com.maverick.crypto.asn1.x509.CertificateException;
import com.maverick.crypto.asn1.x509.X509Certificate;
import com.maverick.crypto.asn1.x509.X509CertificateStructure;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class CertificateStore {

    // #ifdef DEBUG
    org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(CertificateStore.class);
    // #endif

    Hashtable certificates = new Hashtable();
    static CertificateStore instance;

    public CertificateStore() throws IOException {

        addTrustedCACertificate("/gtecybertrustca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/baltimorecodesigningca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/baltimorecybertrustca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/entrust2048ca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/entrustclientca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/entrustglobalclientca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/entrustserverca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/entrustgsslca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/equifaxsecureca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/equifaxsecureebusinessca1.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/equifaxsecureebusinessca2.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/equifaxsecureglobalebusinessca1.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/geotrustglobalca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/gtecybertrustglobalca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/gtecybertrust5ca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/thawtepersonalbasicca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/thawtepersonalfreemailca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/thawtepersonalpremiumca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/thawtepremiumserverca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/thawteserverca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/verisignclass1ca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/verisignclass2ca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/verisignclass3ca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/verisignclass4ca.cert"); //$NON-NLS-1$
        addTrustedCACertificate("/verisignserverca.cert"); //$NON-NLS-1$
        // addTrustedCACertificate("/UTN-USERFirst-Hardware.cert");
        addTrustedCACertificate("/AddTrustUTNServerCA.cert"); //$NON-NLS-1$

    }

    public static CertificateStore getInstance() throws IOException {
        return instance == null ? instance = new CertificateStore() : instance;
    }

    public boolean contains(String dn) throws CertificateException {
        return certificates.containsKey(dn);
    }

    public X509Certificate get(String sig) {
        return (X509Certificate) certificates.get(sig);
    }

    public void addTrustedCACertificate(InputStream in) {

        DERInputStream der = null;
        try {

            der = new DERInputStream(in);

            ASN1Sequence certificate = (ASN1Sequence) der.readObject();

            X509Certificate x509 = new X509Certificate(X509CertificateStructure.getInstance(certificate));

            if (certificates.containsKey(x509.getSubjectDN().toString())) {
                // #ifdef DEBUG
                if (log.isDebugEnabled())
                    log.debug(Messages.getString("CertificateStore.alreadyExists") + x509.getSubjectDN().toString()); //$NON-NLS-1$
                // #endif
            } else {
                // #ifdef DEBUG
                if (log.isDebugEnabled())
                    log.debug(MessageFormat.format(Messages.getString("CertificateStore.addingTrustedCA"), new Object[] { x509.getSubjectDN().toString() })); //$NON-NLS-1$
                // #endif
                certificates.put(x509.getSubjectDN().toString(), x509);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
            try {
                if (der != null) {
                    der.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    public void addTrustedCACertificate(File certificateFile) throws IOException {
        InputStream in = new FileInputStream(certificateFile);
        addTrustedCACertificate(in);
    }

    public void addTrustedCACertificate(String resource) throws IOException {
        InputStream in = TrustedCACertStore.class.getResourceAsStream(resource);
        if (in == null) {
            throw new IOException(MessageFormat.format(Messages.getString("CertificateStore.couldNotLocateTrustedCAResource"), new Object[] { resource }));//$NON-NLS-1$
        }
        addTrustedCACertificate(in);
    }
}
