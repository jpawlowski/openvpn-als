package net.openvpn.als.boot;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SSLKeyManager {

	private static Log log = LogFactory.getLog(SSLKeyManager.class);
	
    public static KeyManager[] getKeyManagerArray() {
        KeyManager[] retVal = null;

        try {
            KeyStoreManager km = KeyStoreManager.getInstance(KeyStoreManager.SERVER_AUTHENTICATION_CERTIFICATES_KEY_STORE);
            if (!km.isKeyStoreEmpty()) {
                KeyStore keyStore = km.getKeyStore();
                // Get a key manager factory out of the key store
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
                keyManagerFactory.init(keyStore, KeyStoreManager.getInstance(KeyStoreManager.SERVER_AUTHENTICATION_CERTIFICATES_KEY_STORE).getKeyStorePassword().toCharArray());
                retVal = keyManagerFactory.getKeyManagers();
            } else
                return null;
        } catch (NoSuchAlgorithmException e) {
            log.fatal("Error getting algorithm.", e);
        } catch (CertificateException e) {
            log.fatal("Error loading certificate.", e);
        } catch (IOException e) {
            log.fatal("I/O issue.", e);
        } catch (KeyStoreException e1) {
            log.fatal("Error loading keystore instance.", e1);
        } catch (UnrecoverableKeyException e) {
            log.fatal("Can't recover the key.", e);
        } catch (Exception e) {
            log.fatal("Unknown error", e);
        }
        return retVal;
    }
}
