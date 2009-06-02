
				/*
 *  OpenVPN-ALS
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
			
package net.openvpn.als.boot;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.crypto.asn1.ASN1Sequence;
import com.maverick.crypto.asn1.DERInputStream;
import com.maverick.crypto.asn1.x509.X509CertificateStructure;
import com.maverick.ssl.TrustedCACertStore;

/**
 * <p>
 * Manages one or more keystores. OpenVPN-ALS currently uses two keystores, one
 * for the SSL server certificate and one for storing all of the client
 * certificates.
 * </p>
 * 
 * <p>
 * Before a keystore manager may be used, it must be registered using
 * {@link #registerKeyStore(String, String, boolean, String, KeyStoreType)}
 * 
 * <p>
 * To obtain a keystore, use {@link #getInstance(String)}, passing the keystore
 * name. If the named keystore has not yet been initialised then it is created
 * and initialised, otherwise the last used instance is returned.
 * </p>
 * 
 * <p>
 * The key store files are held and manipulated in the <i>conf</i> directory.
 * All key stores are also updated to the
 * {@link net.openvpn.als.boot.Repository} which is loaded at start up,
 * replacing all the files.
 * </p>
 * 
 * @see net.openvpn.als.boot.Repository
 */
public class KeyStoreManager {

    // Public statics

    /**
     * The default key store name. Used to store OpenVPN-ALSs own server
     * certificate
     */
    public static final String DEFAULT_KEY_STORE = "default";

    /**
     * Key store name for Server authentication certificates used to store
     * certificates to use to connect to other servers
     */
    public static final String SERVER_AUTHENTICATION_CERTIFICATES_KEY_STORE = "serverAuthentication";

    /**
     * Key Store name for Server certificates that are trusted by OpenVPN-ALS
     * making outgoing connections to it.
     */
    public static final String TRUSTED_SERVER_CERTIFICATES_KEY_STORE = "trustedServer";

    /**
     * Default password used for storing the untrusted key
     */
    public static final String DEFAULT_KEY_PASSWORD = "openvpnals";

    /**
     * Repository name
     */
    public static final String KEYSTORE_REPOSITORY = "keystore";

    // Private instance variables

    private boolean keyStoreExists;
    private Date keystoreLastModified;
    private KeyStore keyStore;
    private boolean keyStoreEmpty;
    private File keyStoreFile;
    private Throwable keyStoreException;
    private String keyStoreName;
    private KeyStoreType keyStoreType;
    private String bundle;
    private boolean removeable;
    private String storePassword;

    // Private statics
    final static Log log = LogFactory.getLog(KeyStoreManager.class);
    static String KEY_TOOL = SystemProperties.get("java.home") + File.separator + "bin" + File.separator + "keytool";
    private static HashMap<String,KeyStoreManager> instances = new HashMap<String,KeyStoreManager>();

    /**
     * Constant for jks keystore 
     */
    public static final KeyStoreType TYPE_JKS = new KeyStoreType("JKS", "jks");
    
    /**
     * Constant for pkcs12 keystore   
     */
    public static final KeyStoreType TYPE_PKCS12 = new KeyStoreType("PKCS12", "p12");
    
    private static final List keyStoreTypes = Arrays.asList(new KeyStoreType[] { TYPE_JKS, TYPE_PKCS12 });
    /**
     * Constructor. Private to prevent direct instantiation
     * 
     * @param keyStoreName name of key store
     * @param bundle bundle key from which to get key store messages (title,
     *        description etc)
     * @param removeable admin may remove certificates manually in the key store
     *        management page
     * @param storePassword the keystore password
     * @param type 
     */
    private KeyStoreManager(String keyStoreName, String bundle, boolean removeable, String storePassword, KeyStoreType type) {
        super();

        this.keyStoreName = keyStoreName;
        this.bundle = bundle;
        this.removeable = removeable;
        this.storePassword = storePassword;
        this.keyStoreType = type;
        
        initKeyStoreFile();

        // Make sure that this keystore is synchronized with the repository

        try {
            synchronizeWithRepository();
        } catch (IOException ex) {
            log.error("The keystore could not be synchornized with the repository", ex);
        }
    }

    
    /**
     * Get an instance of a keystore manager given the keystore name
     * 
     * @param keyStoreName
     * @return keyStore instance
     */
    public static KeyStoreManager getInstance(String keyStoreName) {
        KeyStoreManager mgr = (KeyStoreManager) instances.get(keyStoreName);
        if (mgr == null) {
            throw new IllegalArgumentException("No keystore named " + keyStoreName);
        }
        return mgr;
    }
    
    /**
     * @return InputStream
     * @throws IOException
     */
    public InputStream getInputStream() throws IOException {
        return RepositoryFactory.getRepository().getStore(KEYSTORE_REPOSITORY).getEntryInputStream(keyStoreFile.getName()); 
    }

    /**
     * Get a list of registered {@link KeyStoreManager}s.
     * 
     * @return key stores
     */
    public static List<KeyStoreManager> getKeyStores() {
        List<KeyStoreManager> keyStoreList = new ArrayList<KeyStoreManager>();
        for (KeyStoreManager manager : instances.values()) {
            keyStoreList.add(manager);
        }
        return keyStoreList;
    }

    /**
     * Register a new keystore
     * 
     * @param name name of keystore
     * @param bundle bundle for messages
     * @param removeable <code>true</code> if certificates may manually be
     *        removed
     * @param storePassword key store password
     * @param type 
     */
    public static void registerKeyStore(String name, String bundle, boolean removeable, String storePassword, KeyStoreType type) {
    	if (log.isInfoEnabled())
    		log.info("Registering keystore " + name);
        KeyStoreManager mgr = new KeyStoreManager(name, bundle, removeable, storePassword, type);
        instances.put(name, mgr);
    }

    /**
     * Set the new store password. Note, this only sets the password to use for
     * reading the key store. It does not change the password of the key store
     * itself.
     * 
     * @param storePassword store password
     */
    public void setStorePassword(String storePassword) {
        this.storePassword = storePassword;
        reloadKeystore();
    }

    /**
     * Get if certificates are removeable by the administrator
     * 
     * @return removeable
     */
    public boolean getRemoveable() {
        return removeable;
    }

    /**
     * Get the bundle that contains messages for this key store
     * 
     * @return bundle
     */
    public String getBundle() {
        return bundle;
    }

    /**
     * Get the name that this key store was registered with
     * 
     * @return name
     */
    public String getName() {
        return keyStoreName;
    }

    /**
     * Get if the keystore currently exists
     * 
     * @return keystore exists
     */
    public boolean isKeyStoreExists() {
        try {
            checkKeyStore();
        } catch (Exception e) {
            log.error("Could not determine if key store exists.");
        }
        return keyStoreExists;
    }

    /**
     * Get if the keystore is empty.
     * 
     * @return keystore is empty
     */
    public boolean isKeyStoreEmpty() {
        try {
            checkKeyStore();
        } catch (Exception e) {
            log.error("Could not determine if key store exists.");
        }
        return keyStoreEmpty;
    }

    /**
     * Determine whether the certificate with the supplied alias is trusted or
     * not.
     * 
     * @param alias certificiate name
     * @return trust certificate
     */
    public boolean isCertificateTrusted(String alias) {
        try {
            checkKeyStore();
            if (isKeyStoreExists() && !isKeyStoreEmpty()) {
                return doIsCertificateTrused(alias, keyStore);
            }
        } catch (Exception e) {
            log.error("Could not determine if certificate " + alias + " is trusted.", e);
        }
        return false;
    }

    /**
     * If there were any errors loading or iniatilising the keystore, this
     * method will a non-null exception object detailing the error. If the
     * keystore was loaded successfully then <code>null</code> will be
     * returned.
     * 
     * @return exception caught whilst initialising the keystore
     */
    public Throwable getKeyStoreException() {
        checkKeyStore();
        return keyStoreException;
    }

    /**
     * Get the {@link KeyStore} this keystore manager is managing.
     * 
     * @return keystore
     */
    public KeyStore getKeyStore() {
    	if(keyStore==null)
    		this.reloadKeystore();
    	
        return keyStore;
    }
    
    /**
     * Get a certificate given its alias.
     * 
     * @param alias certificate alias.
     * @return certificate
     */
    public Certificate getCertificate(String alias) {
        try {
            checkKeyStore();
            if (isKeyStoreExists() && !isKeyStoreEmpty()) {
                return keyStore.getCertificate(alias);
            }
        } catch (Exception e) {
            log.error("Could not get certificate with alias " + alias + ".", e);
        }
        return null;
    }

    /**
     * Return an enumeration of {@link String} objects aliases or
     * <code>null</code> if the key store is not loaded.
     * 
     * @return enumeration of {@link Certificate} objects.
     */
    public Enumeration getCertificateAliases() {
        checkKeyStore();
        try {
            if (keyStore != null) {
                return keyStore.aliases();
            }
        } catch (Exception e) {
            log.error("Could not get certificates.", e);
        }
        return null;
    }

    /**
     * Get the number of keys / certificates in this key store
     * 
     * @return number of keys / certificates in this key store
     */
    public int getSize() {
        checkKeyStore();
        try {
            return keyStore != null ? keyStore.size() : 0;
        } catch (KeyStoreException e) {
            log.error("Failed to determine size of key store.", e);
        }
        return 0;
    }

    /**
     * Change the password used to encrypt this key store.
     * 
     * @param oldPassword old password
     * @param password new password
     * @throws Exception on any error
     */
    public void changeKeystorePassword(String oldPassword, String password) throws Exception {
        checkKeyStore();
        if (!isKeyStoreExists()) {
            throw new Exception("Key store doesn't exists. Password cannot be changed.");
        }
        CommandRunner runner = null;
        try {
            Vector<String> v = new Vector<String>();
            v.add(KEY_TOOL);
            v.add("-storepasswd");
            v.add("-new");
            v.add(password);
            v.add("-keystore");
            v.add(getKeyStoreFile().getAbsolutePath());
            v.add("-storepass");
            v.add(oldPassword);
            runner = new CommandRunner(v);
            runner.runCommand();
            this.storePassword = password;
        } catch (Exception e) {
            log.error("Failed to change keystore password.", e);
            throw new Exception(runner == null ? e.getMessage() : parseKeytoolOutput(runner.getOutput()));
        }

    }

    /**
     * Get a key pair from this key store
     * 
     * @param alias alias under which the pair is stored
     * @param password password protecting the keys if any
     * @return key pair
     */
    public KeyPair getKeyPair(String alias, char[] password) {
        try {
            checkKeyStore();
            if (isKeyStoreExists() && !isKeyStoreEmpty()) {
                Key key = keyStore.getKey(alias, password);
                if (key instanceof PrivateKey) {
                    Certificate cert = keyStore.getCertificate(alias);
                    PublicKey publicKey = cert.getPublicKey();
                    return new KeyPair(publicKey, (PrivateKey) key);
                }
            }
        } catch (Exception e) {
            log.error("Could not get key pair with alias " + alias + ".", e);
        }
        return null;
    }

    /**
     * Get a private key from this key store
     * 
     * @param alias alias under which the key is stored
     * @param password password protecting the key if any
     * @return key
     */
    public PrivateKey getPrivateKey(String alias, char[] password) {
        try {
            checkKeyStore();
            if (isKeyStoreExists() && !isKeyStoreEmpty()) {
                return (PrivateKey) keyStore.getKey(alias, password);
            }
        } catch (Exception e) {
            log.error("Could not get private key with alias " + alias + ".", e);
        }
        return null;
    }

    /**
     * Get the chain of certificates from the specified alias up to the root CA
     * certificate
     * 
     * @param alias alias
     * @return certificate chain
     */

    public Certificate[] getCertificateChain(String alias) {
        Certificate[] chain = null;
        try {
            checkKeyStore();
            if (isKeyStoreExists() && !isKeyStoreEmpty()) {
                chain = keyStore.getCertificateChain(alias);
            }
        } catch (Exception e) {
            log.error(e);
        }
        if (chain == null) {
            log.error("Could not get private key with alias " + alias + ".");
        }
        return chain;
    }

    /**
     * Utility method to extract an entity from a certificates subject DN
     * 
     * @param c certificate
     * @param entity entity to extract
     * @return entity value
     */
    public static String getX509CertificateEntity(X509Certificate c, String entity) {
        // This assumes the keystore returns the last certificate in the chain
        // the actual certifcate that is signed by a CA or untrusted cert
        Principal subjectPrincipal = c.getSubjectDN();
        StringTokenizer t = new StringTokenizer(subjectPrincipal.getName(), ",");
        while (t.hasMoreTokens()) {
            String e = t.nextToken().trim();
            String f = entity.trim() + "=";
            if (e.toLowerCase().startsWith(f.toLowerCase())) {
                return e.substring(f.length()).trim();
            }
        }
        return "";
    }

    /**
     * Reload the key store this manager is managing
     */
    public void reloadKeystore() {
        keyStoreExists = false;
        keyStoreException = null;
        keyStoreEmpty = true;
        keyStore = null;
        try {
            File keystoreFile = getKeyStoreFile();
            InputStream in = null;
            if (keystoreFile.exists() && keystoreFile.canRead()) {
                keyStoreExists = true;
                keyStoreException = null;
                keyStoreEmpty = true;
                keyStore = null;
                try {
                    keyStore = KeyStore.getInstance(keyStoreType.getName());
                    String keystorePassword = getKeyStorePassword();
                    if (keystoreFile.length() != 0) {
                        in = new FileInputStream(keystoreFile);
                        keyStore.load(in, keystorePassword.toCharArray());
                        keyStoreEmpty = keyStore.size() == 0;
                    }
                } finally {
                    Util.closeStream(in);
                }
            } else {
                // No change
            }
        } catch (Exception e) {
            log.error("Failed to check key store.", e);
            keyStoreException = e;
        }
    }
    

    /**
     * Check the check store to see if it has been modified since it was last
     * loaded, loading it if it has changed
     */
    public void checkKeyStore() {
        initKeyStoreFile();
        try {
            File keystoreFile = getKeyStoreFile();
            if (keystoreFile.exists() && keystoreFile.canRead()) {
                Date fileLastModified = new Date(keystoreFile.lastModified());
                if (keystoreLastModified == null || !keystoreLastModified.equals(fileLastModified)) {
                    keystoreLastModified = fileLastModified;
                    reloadKeystore();
                } else {
                    // No change
                }
            } else {
                keyStore = null;
                keyStoreExists = false;
                keyStoreEmpty = true;
                keyStoreException = null;
            }
        } catch (Exception e) {
            log.error("Failed to check key store.", e);
            keyStoreException = e;
        }
    }

    /**
     * Import a key in PKCS12 key format
     * 
     * @param keyFile file to import
     * @param password password for key
     * @param alias alias for key
     * @param newAlias 
     * @throws Exception on any error
     * @return the alias of the key imported
     */
    public String importPKCS12Key(File keyFile, String password, String alias, String newAlias) throws Exception {
        KeyStore kspkcs12 = KeyStore.getInstance("PKCS12");
        kspkcs12.load(new FileInputStream(keyFile), password == null ? null : password.toCharArray());
        boolean hasTemp = false;
        if(isKeyStoreEmpty()) {
            if(isKeyStoreExists()) {                
                deleteKeyStore();
            }
            createKeyStore();
        	String dname = "cn=tmp, ou=tmp, o=tmp, l=tmp, st=tmp, c=GB";
        	createKey("temporary-key", dname);
        	hasTemp = true;
        	reloadKeystore();
        }
        try {
        
            String firstAlias = (String) kspkcs12.aliases().nextElement();
            
        	if(Util.isNullOrTrimmedBlank(alias)) {
        		log.info("Alias not specified, importing first alias " + firstAlias);
        		alias = firstAlias;
        	}

            if(Util.isNullOrTrimmedBlank(newAlias)) {
                log.info("New alias not specified, using imported alias " + alias);
                newAlias = alias;
            }
        	
	        Certificate c[] = kspkcs12.getCertificateChain(alias);
	        // Make sure we don't have a null chain
	        if (c == null)
	            c = new Certificate[] {};
	        Key key = kspkcs12.getKey(alias, password == null ? null : password.toCharArray());
            if(key == null) {
                throw new Exception("No alias of '" + alias + "' in imported PKCS12 key file.");
            }
	        this.keyStore.setKeyEntry(newAlias, key, getKeyStorePassword().toCharArray(), c);
        } finally {
            if(hasTemp || keyStore.containsAlias("temporary-key"))
                this.keyStore.deleteEntry("temporary-key");
            OutputStream out = null;
            try {
                out = new FileOutputStream(keyStoreFile.getAbsolutePath());
                getKeyStore().store(out, getKeyStorePassword().toCharArray());
            } finally {
                Util.closeStream(out);
            }            
            updateRepository(false);
        }
        
        return newAlias;
    }

    /**
     * Get the key store file this manager is managing
     * 
     * @return file
     */
    public File getKeyStoreFile() {
        return keyStoreFile;
    }

    /**
     * Create a new private key given an alias an DN. Note that the
     * DN will be escaped as required by RFC2253
     * 
     * @param alias alias
     * @param dname DN
     * @throws Exception on any error
     */
    public void createKey(String alias, String dname) throws Exception {
        checkKeyStore();
        if (!isKeyStoreExists()) {
            throw new Exception("Key store doesn't exists. Key cannot be created.");
        }
        /*
         * Because an empty keystore file is not valid, delete the key first
         * then let genkey create a new keystore
         */
        if (isKeyStoreEmpty()) {
            if (!getKeyStoreFile().delete()) {
                throw new Exception("Could not delete key store.");
            }
        }
        CommandRunner runner = null;
        try {
            String keyStorePassword = getKeyStorePassword();
            Vector<String> v = new Vector<String>();
            v.add(KEY_TOOL);
            v.add("-genkey");
            v.add("-alias");
            v.add(alias);
            v.add("-keyalg");
            v.add("RSA");
            v.add("-keystore");
            v.add(keyStoreFile.getAbsolutePath());
            v.add("-dname");
            v.add(dname);
            v.add("-storetype");
            v.add(keyStoreType.getName());
            v.add("-storepass");
            v.add(keyStorePassword);
            v.add("-keypass");
            v.add(keyStorePassword);
            v.add("-validity");
            v.add("365");
            runner = new CommandRunner(v);
            runner.runCommand();

            updateRepository(false);
        } catch (Exception e) {
            log.error("Failed to create key.", e);
            throw new Exception(runner == null ? e.getMessage() : parseKeytoolOutput(runner.getOutput()));
        }
    }

    /**
     * Import a certificate from a file and store with the specified a alias.
     * File must be X509 and Base 64 or DER encoded.
     * 
     * @param alias alias to store cert. under
     * @param certFile file contain certificate
     * @param keyPass key password or <code>null</code> for default 
     * @throws Exception on any error
     */
    public void importCert(String alias, File certFile, String keyPass) throws Exception {
        checkKeyStore();
        if (!isKeyStoreExists()) {
        	createKeyStore();
        }
        /*
         * Because an empty keystore file is not valid, delete the key first
         * then let genkey create a new keystore
         */
        if (isKeyStoreEmpty()) {
            if (!getKeyStoreFile().delete()) {
                throw new Exception("Could not delete key store.");
            }
        }
        CommandRunner runner = null;
        try {
        	if (log.isInfoEnabled())
        		log.info("Importing certificate for " + alias + " from " + certFile.getAbsolutePath());
            String keyPassword = getKeyStorePassword();
            Vector<String> v = new Vector<String>();
            v.add(KEY_TOOL);
            v.add("-import");
            v.add("-trustcacerts");
            
            v.add("-noprompt");
            v.add("-file");
            v.add(certFile.getAbsolutePath());
            v.add("-alias");
            v.add(alias);
            v.add("-keystore");
            v.add(keyStoreFile.getAbsolutePath());
            v.add("-storepass");
            v.add(keyPassword);
            v.add("-keypass");
            
            v.add(keyPass == null ? DEFAULT_KEY_PASSWORD : keyPass);
            v.add("-storetype");
            v.add(keyStoreType.getName().toLowerCase());
            runner = new CommandRunner(v);
            runner.runCommand();

            updateRepository(false);
        } catch (Exception e) {
            log.error("Failed to import certficate.", e);
            throw new Exception(runner == null ? e.getMessage() : parseKeytoolOutput(runner.getOutput()));
        }
        if (log.isInfoEnabled())
        	log.info("Certificate for " + alias + " imported from " + certFile.getAbsolutePath());
    }

    /**
     * Generate a certificate sigining request for the key with the specfied
     * alias.
     * 
     * @param alias alias to generate CSR for
     * @param keyPass 
     * @return CSR as a string
     * @throws Exception on any error
     */
    public String generateCSR(String alias, String keyPass) throws Exception {
        checkKeyStore();
        if (!isKeyStoreExists()) {
            throw new Exception("Key store doesn't exists. CSR cannot be generated.");
        }
        CommandRunner runner = null;
        InputStream in = null;
        try {
            String keyPassword = getKeyStorePassword();
            Vector<String> v = new Vector<String>();
            v.add(KEY_TOOL);
            v.add("-certreq");
            v.add("-alias");
            v.add(alias);
            v.add("-keyalg");
            v.add("RSA");
            v.add("-keystore");
            v.add(keyStoreFile.getAbsolutePath());
            v.add("-storepass");
            v.add(keyPassword);
            v.add("-file");
            File csrFile = new File(ContextHolder.getContext().getConfDirectory(), "server-certificate.csr");
            v.add(csrFile.getAbsolutePath());            
            v.add("-keypass");
            v.add(keyPass == null ? DEFAULT_KEY_PASSWORD : keyPass);
            runner = new CommandRunner(v);
            runner.runCommand();
            in = new FileInputStream(csrFile);
            return Util.loadStreamToString(in, null);
        } catch (Exception e) {
            log.error("Failed to create key.", e);
            throw new Exception(runner == null ? e.getMessage() : parseKeytoolOutput(runner.getOutput()));
        } finally {
            Util.closeStream(in);
        }
    }

    /**
     * Create a new key store.
     * <p>
     * We dont actually create a keystore, we just create a zero length file as
     * there doesnt seem to be a way of creating an empty keystore using
     * keytool.
     * 
     * @throws IOException on any error
     */
    public void createKeyStore() throws IOException {
        if (isKeyStoreExists()) {
            throw new IOException("Key store already exists.");
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(getKeyStoreFile());
        } finally {
            Util.closeStream(out);
        }
    }


    /**
     * Delete the key store.
     * 
     * @throws IOException
     */
    public void deleteKeyStore() throws IOException {
        if (!isKeyStoreExists()) {
            throw new IOException("Key store does not exist.");
        }
        if (!getKeyStoreFile().delete()) {
            throw new IOException("Failed to delete " + getKeyStoreFile().getAbsolutePath() + ".");
        }

        updateRepository(true);
    }

    /**
     * Delete a certificate from the key store given its alias.
     * 
     * @param alias alias to remove
     * @throws Exception on any error
     */
    public void deleteCertificate(String alias) throws Exception {
        checkKeyStore();
        if (!isKeyStoreExists()) {
            throw new Exception("Key store doesn't exists. Certificate cannot be deleted.");
        }
        CommandRunner runner = null;
        try {
        	if (log.isInfoEnabled())
        		log.info("Deleting certificate for " + alias);
            String keyPassword = getKeyStorePassword();
            Vector<String> v = new Vector<String>();
            v.add(KEY_TOOL);
            v.add("-delete");
            v.add("-alias");
            v.add(alias);
            v.add("-keystore");
            v.add(keyStoreFile.getAbsolutePath());
            v.add("-storepass");
            v.add(keyPassword);
            runner = new CommandRunner(v);
            runner.runCommand();

            updateRepository(false);
        } catch (Exception e) {
            log.error("Failed to delete certificate.", e);
            throw new Exception(runner == null ? e.getMessage() : parseKeytoolOutput(runner.getOutput()));
        }
        if (log.isInfoEnabled())
        	log.info("Deleted certificate for " + alias);

    }

    /**
     * Get a {@link KeyStoreType} given its name.
     * 
     * @param name key store type name
     * @return key store type
     */
    public static KeyStoreType getKeyStoreType(String name) {
        for (Iterator i = keyStoreTypes.iterator(); i.hasNext();) {
            KeyStoreType t = (KeyStoreType) i.next();
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    /**
     * Get a list of supported {@link KeyStoreType} objects.
     * 
     * @return list of support key store types
     */
    public List getSupportedKeyStoreTypes() {
        return keyStoreTypes;
    }

    /**
     * Set the key store type for this key store manager.
     * 
     * @param keyStoreType key store type
     */
    public void setKeyStoreType(KeyStoreType keyStoreType) {
        this.keyStoreType = keyStoreType;
        initKeyStoreFile();
    }

    /**
     * Get the key store type for this key store manager.
     * 
     * @return key store type
     */
    public KeyStoreType getKeyStoreType() {
        return keyStoreType;
    }

    // Supporting methods

    void initKeyStoreFile() {
        this.keyStoreFile = new File(ContextHolder.getContext().getConfDirectory(), keyStoreName + ".keystore." + keyStoreType.getExtension());
    }

    void synchronizeWithRepository() throws IOException {

        RepositoryStore store = RepositoryFactory.getRepository().getStore(KEYSTORE_REPOSITORY);

        if (!store.hasEntry(keyStoreFile.getName())) {
            keyStoreFile.createNewFile();
        } else {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = store.getEntryInputStream(keyStoreFile.getName());
                out = new FileOutputStream(keyStoreFile);
                Util.copy(in, out);
            } finally {
                Util.closeStream(in);
                Util.closeStream(out);
            }
        }
    }
    
    

    void updateRepository(boolean remove) throws IOException {

        RepositoryStore store = RepositoryFactory.getRepository().getStore(KEYSTORE_REPOSITORY);

        if (remove) {
            store.removeEntry(keyStoreFile.getName());
        } else {
            OutputStream out = null;
            InputStream in = null;
            try {
                out = store.getEntryOutputStream(keyStoreFile.getName());
                in = new FileInputStream(keyStoreFile);

                Util.copy(in, out);
            } finally {
                Util.closeStream(in);
                Util.closeStream(out);
            }
        }

    }

    /**
     * Get the key store passwords
     * 
     * @return keystore password
     * @throws Exception
     */
    public String getKeyStorePassword() throws Exception {
        return storePassword;
    }

    boolean doIsCertificateTrused(String alias, KeyStore keyStore) throws Exception {

        Certificate[] certs = keyStore.getCertificateChain(alias);
        
        
//        try {
//        	((CustomSSLSocketFactory)CustomSSLSocketFactory.getDefault()).checkServerTrusted((X509Certificate[])certs, "");
//        	return true;
//        } catch(CertificateException ex) {
        if (certs == null) {
        	if (log.isInfoEnabled())
        		log.info("No certs for " + alias + ", untrusted.");
        } else if (certs.length > 1) {
            X509Certificate x509cert = (X509Certificate) certs[certs.length - 1];
            TrustedCACertStore store = new TrustedCACertStore();
            ByteArrayInputStream bin = new ByteArrayInputStream(x509cert.getEncoded());
            DERInputStream der = null;
            try {
                der = new DERInputStream(bin);

                ASN1Sequence certificate = (ASN1Sequence) der.readObject();
                com.maverick.crypto.asn1.x509.X509Certificate x509 = new com.maverick.crypto.asn1.x509.X509Certificate(
                    X509CertificateStructure.getInstance(certificate));
                return store.isTrustedCertificate(x509, false, false);
            } finally {
                Util.closeStream(der);
            }
        }
//        }
        return false;

    }

    String parseKeytoolOutput(String output) {
        if (output.startsWith("keytool error: ")) {
            int idx = output.indexOf(':', 14);
            if (idx != -1) {
                output = output.substring(idx + 1);
            }
        }
        return output;
    }

    /**
     * Deregister a keystore
     * 
     * @param name name of keystore
     */
    public static void deregisterKeyStore(String name) {
        if (log.isInfoEnabled())
            log.info("Deregistering keystore " + name);
        instances.remove(name);
    }
    
    
    
}
