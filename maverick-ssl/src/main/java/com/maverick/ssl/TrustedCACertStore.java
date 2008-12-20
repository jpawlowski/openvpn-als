
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
			
package com.maverick.ssl;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Hashtable;

import com.maverick.crypto.asn1.ASN1OctetString;
import com.maverick.crypto.asn1.ASN1Sequence;
import com.maverick.crypto.asn1.DERInputStream;
import com.maverick.crypto.asn1.DERObjectIdentifier;
import com.maverick.crypto.asn1.x509.CertificateException;
import com.maverick.crypto.asn1.x509.X509Certificate;
import com.maverick.crypto.asn1.x509.X509CertificateStructure;
import com.maverick.crypto.digests.MD5Digest;
import com.maverick.crypto.digests.SHA1Digest;
import com.maverick.crypto.publickey.PublicKey;
import com.maverick.crypto.publickey.Rsa;
import com.maverick.crypto.publickey.RsaPublicKey;

/**
 * 
 * @author Lee David Painter <a href="mailto:lee@localhost">&lt;lee@localhost&gt;</a>
 */
public class TrustedCACertStore {

    static Hashtable temporarilyTrusted = new Hashtable();

    public TrustedCACertStore() {
    }

    public boolean isTrustedCertificate(X509Certificate x509, boolean allowInvalidCertificates, boolean allowUntrustedCertificates)
                    throws SSLException {
        try {
            if (CertificateStore.getInstance().contains(x509.getIssuerDN().toString())) {


                X509Certificate trusted = (X509Certificate) CertificateStore.getInstance().get(x509.getIssuerDN().toString());

                // Verify the signature of the certificate with the trusted
                // certificate
                PublicKey publickey = trusted.getPublicKey();

                if (publickey instanceof RsaPublicKey) {
                    // Verify the signature
                    if (x509.getSigAlgName().equals("MD5WithRSAEncryption")) { //$NON-NLS-1$

                        try {
                            byte[] blob = x509.getSignature();

                            // Check for signed bit
                            if ((blob[0] & 0x80) == 0x80) {
                                blob = new byte[x509.getSignature().length + 1];
                                blob[0] = 0;
                                System.arraycopy(x509.getSignature(), 0, blob, 1, x509.getSignature().length);
                            }

                            BigInteger input = new BigInteger(blob);
                            RsaPublicKey r = (RsaPublicKey) trusted.getPublicKey();
                            BigInteger decoded = Rsa.doPublic(input, r.getModulus(), r.getPublicExponent());
                            BigInteger result = Rsa.removePKCS1(decoded, 0x01);
                            byte[] sig = result.toByteArray();

                            MD5Digest digest = new MD5Digest();
                            digest.update(x509.getTBSCertificate(), 0, x509.getTBSCertificate().length);
                            byte[] hash = new byte[digest.getDigestSize()];
                            digest.doFinal(hash, 0);

                            DERInputStream der = new DERInputStream(new ByteArrayInputStream(sig));

                            ASN1Sequence o = (ASN1Sequence) der.readObject();

                            ASN1Sequence o1 = (ASN1Sequence) o.getObjectAt(0);

                            DERObjectIdentifier o2 = (DERObjectIdentifier) o1.getObjectAt(0);
                            ASN1OctetString o3 = (ASN1OctetString) o.getObjectAt(1);

                            byte[] actual = o3.getOctets();

                            for (int i = 0; i < actual.length; i++) {
                                if (actual[i] != hash[i]) {
                                    return false;
                                }
                            }

                        } catch (IOException ex1) {
                            throw new SSLException(SSLException.INTERNAL_ERROR, ex1.getMessage());
                        }

                    } else if (x509.getSigAlgName().equals("SHA1WithRSAEncryption")) { //$NON-NLS-1$

                        try {
                            byte[] blob = x509.getSignature();

                            // Check for signed bit
                            if ((blob[0] & 0x80) == 0x80) {
                                blob = new byte[x509.getSignature().length + 1];
                                blob[0] = 0;
                                System.arraycopy(x509.getSignature(), 0, blob, 1, x509.getSignature().length);
                            }

                            BigInteger input = new BigInteger(blob);
                            RsaPublicKey r = (RsaPublicKey) trusted.getPublicKey();

                            BigInteger decoded = Rsa.doPublic(input, r.getModulus(), r.getPublicExponent());

                            BigInteger result = Rsa.removePKCS1(decoded, 0x01);
                            byte[] sig = result.toByteArray();

                            SHA1Digest digest = new SHA1Digest();
                            digest.update(x509.getTBSCertificate(), 0, x509.getTBSCertificate().length);
                            byte[] hash = new byte[digest.getDigestSize()];
                            digest.doFinal(hash, 0);

                            DERInputStream der = new DERInputStream(new ByteArrayInputStream(sig));

                            ASN1Sequence o = (ASN1Sequence) der.readObject();

                            ASN1Sequence o1 = (ASN1Sequence) o.getObjectAt(0);

                            DERObjectIdentifier o2 = (DERObjectIdentifier) o1.getObjectAt(0);
                            ASN1OctetString o3 = (ASN1OctetString) o.getObjectAt(1);

                            byte[] actual = o3.getOctets();

                            for (int i = 0; i < actual.length; i++) {
                                if (actual[i] != hash[i]) {
                                    return false;
                                }
                            }

                        } catch (IOException ex1) {
                            throw new SSLException(SSLException.INTERNAL_ERROR, ex1.getMessage());
                        }

                    } else
                        throw new SSLException(SSLException.UNSUPPORTED_CERTIFICATE,
                            MessageFormat.format(Messages.getString("TrustedCACertStore.signatureAlgorithmNotSupported"), new Object[] { x509.getSigAlgName() })); //$NON-NLS-1$

                    // Verify the validity
                    try {
                        trusted.checkValidity();
                        x509.checkValidity();
                    } catch (CertificateException ex2) {
                        if (allowInvalidCertificates) {
                            return true;
                        } else {
                            if (CertificatePrompt.prompt != null) {
                                String str = new String(x509.getSignature());
                                if (temporarilyTrusted.containsKey(str)
                                    || CertificatePrompt.prompt.invalid(x509) != CertificatePrompt.ABORT) {
                                    temporarilyTrusted.put(str, x509);
                                    return true;
                                }
                            }
                        }
                        return false;
                    }

                    return true;

                } else {
                    throw new SSLException(SSLException.UNSUPPORTED_CERTIFICATE,
                        Messages.getString("TrustedCACertStore.unsupportedPublicKeyInX509Cert")); //$NON-NLS-1$
                }

            } else {

                // System.out.println("Certificate is not trusted. checking
                // validity");
                try {
                    x509.checkValidity();
                } catch (CertificateException ex2) {
                    // System.out.println("Certificate is invalid (2)");
                    if (allowInvalidCertificates) {
                        // System.out.println("invalid cets = true");
                        return true;
                    } else {
                        if (CertificatePrompt.prompt != null) {
                            String str = new String(x509.getSignature());
                            if (temporarilyTrusted.containsKey(str)
                                || CertificatePrompt.prompt.invalid(x509) != CertificatePrompt.ABORT) {
                                temporarilyTrusted.put(str, x509);
                                return true;
                            }
                        }
                    }
                    return false;
                }

                // System.out.println("Checking for untrusted flag");

                if (allowUntrustedCertificates) {
                    // System.out.println("Certificate is ok untrusted=true");
                    return true;
                } else {
                    if (CertificatePrompt.prompt != null) {
                        String str = new String(x509.getSignature());
                        if (temporarilyTrusted.containsKey(str)
                            || CertificatePrompt.prompt.untrusted(x509) != CertificatePrompt.ABORT) {
                            temporarilyTrusted.put(str, x509);
                            return true;
                        }
                    }
                }
                return false;
            }
        } catch (CertificateException ex) {
            throw new SSLException(SSLException.UNSUPPORTED_CERTIFICATE, ex.getMessage());
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new SSLException(SSLException.UNSUPPORTED_CERTIFICATE,
                Messages.getString("TrustedCACertStore.errorGettingCertFromTrustStore")); //$NON-NLS-1$
        }
    }

    private void log(String msg, byte[] buf) {

        // System.out.print(msg + ": ");
        for (int i = 0; i < buf.length; i++) {
            // System.out.print(Integer.toHexString(buf[i] & 0xFF).toUpperCase()
            // + " ");
        }
        // System.out.println();
    }

    public static void main(String[] args) {

        TrustedCACertStore store = new TrustedCACertStore();

        DERInputStream der = null;
        InputStream in = null;
        try {
            in = new FileInputStream("c:\\exported.cer"); //$NON-NLS-1$
            der = new DERInputStream(in);

            ASN1Sequence certificate = (ASN1Sequence) der.readObject();
            com.maverick.crypto.asn1.x509.X509Certificate x509 = new com.maverick.crypto.asn1.x509.X509Certificate(X509CertificateStructure.getInstance(certificate));

            /*
             * if (store.isTrustedCertificate(x509, false, false)) {
             * //System.out.println("Success"); } else {
             * //System.out.println("Failure"); }
             */
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            // Util.closeStream(der);
            // Util.closeStream(in);
        }

    }
}
