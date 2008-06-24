package com.maverick.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.X509TrustManager;


public class SSLTransportTrustManager implements X509TrustManager {
	
	private KeyStore trustcacerts;
    
 
    public SSLTransportTrustManager() {
        String filename = System.getProperty("java.home") + "/lib/security/cacerts".replace('/', File.separatorChar);
        try {
            FileInputStream is = new FileInputStream(filename);
            trustcacerts = KeyStore.getInstance(KeyStore.getDefaultType());
            String password = "changeit";
            trustcacerts.load(is, password.toCharArray());

        } catch (Exception e) {
           
        }
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        throw new CertificateException("Client certs are not trusted by the custom SSL trust manager.");
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        /**
         * Check the Maverick system property
         */
    	if("true".equalsIgnoreCase(System.getProperty("com.maverick.ssl.allowUntrustedCertificates")))
    		return;
    	
        /**
         * If we got this far then the certificate was not in our trust store so
         * lets check the java cacerts store.
         */

        if (trustcacerts == null) {
        	throw new CertificateException("No trust store found!");
        } else {
            try {
                CertificateFactory certFact = CertificateFactory.getInstance("X.509");
                CertPath path = certFact.generateCertPath(Arrays.asList(chain));
                PKIXParameters params = new PKIXParameters(trustcacerts);
                params.setRevocationEnabled(false);
                CertPathValidator certPathValidator = CertPathValidator.getInstance(CertPathValidator.getDefaultType());
                CertPathValidatorResult result = certPathValidator.validate(path, params);
                PKIXCertPathValidatorResult pkixResult = (PKIXCertPathValidatorResult) result;
                TrustAnchor ta = pkixResult.getTrustAnchor();
                X509Certificate cert = ta.getTrustedCert();
                return;
            } catch (Exception e) {
            }
        }

        throw new CertificateException("Certificate chain is not trusted");
    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
