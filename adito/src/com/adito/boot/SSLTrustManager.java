package com.adito.boot;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorResult;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SSLTrustManager implements X509TrustManager {

	private static Log log = LogFactory.getLog(SSLTrustManager.class);
    private static SSLTrustManager instance;
	private KeyStore trustcacerts;
    
 
    public SSLTrustManager() {
        String filename = SystemProperties.get("java.home") + "/lib/security/cacerts".replace('/', File.separatorChar);
        try {
            FileInputStream is = new FileInputStream(filename);
            trustcacerts = KeyStore.getInstance(KeyStore.getDefaultType());
            String password = "changeit";
            trustcacerts.load(is, password.toCharArray());

        } catch (Exception e) {
            log.error("Failed to load trusted cacerts keystore from " + filename);
        }
    }
    
    public static SSLTrustManager getInstance() {
    	return instance == null ? instance = new SSLTrustManager() : instance;
    }
    
    public static TrustManager[] getTrustManagerArray() {
        return new TrustManager[] { getInstance() };
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        throw new CertificateException("Client certs are not trusted by the custom SSL trust manager.");
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        /**
         * This looks for the certificate in our own trust store.
         */
    	if(!ContextHolder.getContext().getConfig().retrievePropertyBoolean(new ContextKey("ssl.strictSSLTrustMode")))
    		return;
    	
        try {
            KeyStoreManager km = KeyStoreManager.getInstance(KeyStoreManager.TRUSTED_SERVER_CERTIFICATES_KEY_STORE);

            if (!km.isKeyStoreEmpty()) {
                KeyStore trusted = km.getKeyStore();

                for (Enumeration e = trusted.aliases(); e.hasMoreElements();) {
                    String alias = (String) e.nextElement();
                    Certificate c = trusted.getCertificate(alias);

                    try {
                        chain[0].verify(c.getPublicKey());
                        return;
                    } catch (Exception ex) {

                    }
                }

                for (int i = 0; i < chain.length; i++) {
                    if (trusted.getCertificateAlias(chain[i]) != null) {
                        return;
                    }
                }
            }


        } catch (KeyStoreException e) {
            log.error("Unexpected keystore exception", e);
        }

        /**
         * If we got this far then the certificate was not in our trust store so
         * lets check the java cacerts store.
         */

        if (trustcacerts == null) {
            if (log.isInfoEnabled())
                log.info("Cannot validate from cacerts as the keystore failed to load.");
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
                if (log.isInfoEnabled())
                    log.info("Failed to validate certificate path", e);
            }
        }

        throw new CertificateException("Certificate chain is not trusted");
    }

    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
