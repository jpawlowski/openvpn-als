
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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import com.maverick.crypto.asn1.ASN1Sequence;
import com.maverick.crypto.asn1.DERInputStream;
import com.maverick.crypto.asn1.x509.X509Certificate;
import com.maverick.crypto.asn1.x509.X509CertificateStructure;
import com.maverick.crypto.security.SecureRandom;

/**
 * A context for an SSL connection.
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class SSLContext {

    Hashtable cipherClassesByID = new Hashtable();
    Hashtable cipherIDsByName = new Hashtable();
    Vector cipherIDs = new Vector();
    SecureRandom rnd = SecureRandom.getInstance();

    boolean allowUntrustedCertificates = false;
    boolean allowInvalidCertificates = false;

    TrustedCACertStore cacerts;

    public SSLContext() throws IOException {

        cacerts = new TrustedCACertStore();

        addCipherSuite(0x00, 0x04, "SSL_RSA_WITH_RC4_128_MD5", //$NON-NLS-1$
            SSL_RSA_WITH_RC4_128_MD5.class);

        try {
            allowUntrustedCertificates = Boolean.valueOf(System.getProperty("com.maverick.ssl.allowUntrustedCertificates", "false")).booleanValue(); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (Exception ex) {
        }

        try {
            allowInvalidCertificates = Boolean.valueOf(System.getProperty("com.maverick.ssl.allowInvalidCertificates", "false")).booleanValue(); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (Exception ex1) {
        }

    }

    public void addCipherSuite(int id1, int id2, String name, Class suite) {

        SSLCipherSuiteID id = new SSLCipherSuiteID(id1, id2);
        cipherClassesByID.put(id, suite);
        cipherIDsByName.put(name, id);
        cipherIDs.addElement(id);
    }

    public TrustedCACertStore getTrustedCACerts() {
        return cacerts;
    }

    public Class getCipherSuiteClass(SSLCipherSuiteID id) {

        Enumeration e = cipherClassesByID.keys();
        while (e.hasMoreElements()) {
            SSLCipherSuiteID i = (SSLCipherSuiteID) e.nextElement();
            if (i.equals(id)) {
                return (Class) cipherClassesByID.get(i);
            }
        }

        return null;
    }

    public SSLCipherSuiteID[] getCipherSuiteIDs() {
        SSLCipherSuiteID[] ids = new SSLCipherSuiteID[cipherIDs.size()];
        cipherIDs.copyInto(ids);
        return ids;
    }

    public Random getRND() {
        return rnd;
    }

    public boolean isUntrustedCertificateAllowed() {
        return allowUntrustedCertificates;
    }

    public boolean isInvalidCertificateAllowed() {
        return allowInvalidCertificates;
    }

    public static void main(String[] args) {

        try {
            SSLContext ssl = new SSLContext();
            // Now read the certificate
            DERInputStream der = new DERInputStream(new FileInputStream("c:/exported.cer")); //$NON-NLS-1$

            ASN1Sequence certificate = (ASN1Sequence) der.readObject();

            // Get the x509 certificate structure
            X509Certificate x509 = new X509Certificate(X509CertificateStructure.getInstance(certificate));

            System.out.println(x509.getIssuerDN());
            System.out.println(x509.getSubjectDN());
            ssl.getTrustedCACerts().isTrustedCertificate(x509, true, true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
