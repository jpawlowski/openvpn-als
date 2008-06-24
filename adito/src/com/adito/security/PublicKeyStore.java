package com.adito.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.crypto.Cipher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.crypto.encoders.Base64;
import com.adito.boot.RepositoryFactory;
import com.adito.boot.RepositoryStore;
import com.adito.boot.Util;
import com.adito.properties.Property;
import com.adito.properties.impl.systemconfig.SystemConfigKey;
import com.adito.security.pki.InvalidKeyException;
import com.adito.security.pki.SshKeyGenerator;
import com.adito.security.pki.SshPrivateKey;
import com.adito.security.pki.SshPrivateKeyFile;
import com.adito.security.pki.SshPublicKey;
import com.adito.security.pki.SshPublicKeyFile;

/**
 * Provides a repository backed PKI store.
 * @author lee
 *
 */
public class PublicKeyStore {

	private static PublicKeyStore instance;

	static Log log = LogFactory.getLog(PublicKeyStore.class);
	
	HashMap loadedPrivateKeys = new HashMap();
	HashMap loadedPublicKeys = new HashMap();
	RepositoryStore store;
	
	String keyType = "rsa";
	int bitLength = 1024;
	
	public static PublicKeyStore getInstance() {
		return instance==null ? instance = new PublicKeyStore() : instance;
	}
	
	PublicKeyStore() {
		store = RepositoryFactory.getRepository().getStore("PKI");
		
		try {			
			keyType = Property.getProperty(new SystemConfigKey("pki.algorithm"));			
			bitLength = Property.getPropertyInt(new SystemConfigKey("pki.bitLength"));
		} catch (Exception e) {
			log.error("Could not get PKI properties! defaults will be used");
			keyType = "rsa";
			bitLength = 1024;
		}
	}
	
    /**
     * Must be called after a user has logged on to initialize their private key. This will be used
     * to encrypt/decrpyt confidential data.
     * 
     * @param user
     * @param pw
     * @throws PromptForPasswordException The UI must prompt the user for their password and call this method again
     * @throws FatalKeyException A critical error has occurred.
     * @throws UpdatePrivateKeyPassphraseException The UI must prompt for the users old password so that their private key can be updated. 
     * The changePrivateKeyPassphrase method must be called prior to calling this method again.
     */
    public void verifyPrivateKey(String username, char[] pw) throws PromptForPasswordException, FatalKeyException, UpdatePrivateKeyPassphraseException {
    	
    	
    	if(!PublicKeyStore.getInstance().hasPrivateKey(username)) {

        	if(pw==null) {
        	
        		/**
        		 * We need to generate a private key so we must ask the user for their
        		 * current password so we can encrpyt it.
        		 */
        		throw new PromptForPasswordException();
        	}
        	
        	try {
        		/**
        		 * This call will generate a private key for the user and load it into memory
        		 */
				getPrivateKey(username, new String(pw));
			} catch (Exception e) {
				log.error("Error creating users private key", e);
				
				/**
				 * This is a critical error and will stop the user using confidential 
				 * attributes.
				 */
				throw new FatalKeyException();
			}
		}  
    	
        /**
    	 * The user has a private key so lets check to see if we need to ask for 
    	 * their password in order to decrypt it
    	 */
    	if(!PublicKeyStore.getInstance().hasLoadedKey(username)) {
    		
    		if(pw==null) {
    		   
    			/**
    			 * At some point we're going to have to ask for the users password
    			 * so that we can load the private key into memory. Should this be done now
    			 * or later?
    			 * 
    			 * If we do this now and persist keys in memory then the user will only
    			 * have to do this when they login, saving us from having to ask elsewhere.
    			 * The only issue is that when a password changes the user will not be prompted
    			 * until the server has been restarted, this may be sometime after the password
    			 * has changed.
    			 */
    			throw new PromptForPasswordException();
    			
    		} else {
    		   
    			if(!isPassphraseValid(username, new String(pw))) 
    				throw new UpdatePrivateKeyPassphraseException();
    			
    			try {
					PublicKeyStore.getInstance().getPrivateKey(username, new String(pw));
				} catch (IOException e) {
					log.error("Error loading users private key", e);
					throw new FatalKeyException();
					
				} catch (InvalidKeyException e) {
					throw new UpdatePrivateKeyPassphraseException();
				}
    		}
    	}    	
    }
    
    /**
     * Change the private key passphrase.
     * @param username username
     * @param oldPassphrase
     * @param newPassphrase
     * @throws FatalKeyException
     */
    public void changePrivateKeyPassphrase(String username, String oldPassphrase, String newPassphrase) throws FatalKeyException {
    	
    	
    	try {
			changePassphrase(username, oldPassphrase, newPassphrase);
		} catch (Exception e) {
			log.error("Error changing users private key passphrase", e);
			throw new FatalKeyException("Failed to change private key passphrase", e);
		} 
    	
    }	
	
    /**
     * Encrypt some text with the users public key. This can only be decrypted by the user.
     * @param text
     * @param username
     * @return
     * @throws FatalKeyException
     */
	public String encryptText(String text, String username) throws FatalKeyException {
		
		try {
			
			String cipherText = "";
			
			
			byte[] plainText = text.getBytes();
			
			for(int i=0;i<plainText.length;i+=117) {
				SshPublicKey pk = getPublicKey(username);
				
				Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				
				cipher.init(Cipher.ENCRYPT_MODE, pk.getPublicKey());
				
				byte[] ctext = cipher.doFinal(plainText, i, (plainText.length - i > 117 ? 117 : plainText.length - i));
			
				String section = new String(Base64.encode(ctext), "US-ASCII"); 
				cipherText += section + "\n";
				
				
				
			}
			
		
			return cipherText;
			
		} catch (Exception e) {
			log.error("Error encrpyting data", e);
			throw new FatalKeyException();
		} 
	}
	
	/**
	 * Decrypt some cipher text into plain text.
	 * @param text
	 * @param username
	 * @return
	 * @throws FatalKeyException
	 * @throws PrivateKeyNotInitializedException
	 */
	public String decryptText(String text, String username) throws FatalKeyException, PrivateKeyNotInitializedException {
		
		if(!hasLoadedKey(username))
			throw new PrivateKeyNotInitializedException();
		
		try {
			SshPrivateKey pk = (SshPrivateKey) loadedPrivateKeys.get(username);
			
			
			StringTokenizer blocks = new StringTokenizer(text, "\n");
			
			String plainText = "";
			
			
			while(blocks.hasMoreTokens()) {
				Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
				
				cipher.init(Cipher.DECRYPT_MODE, pk.getPrivateKey());
				
				byte[] encrypted = Base64.decode(blocks.nextToken());
				byte[] ctext = cipher.doFinal(encrypted);
				
				plainText += new String(ctext);
			}
			
			
			return plainText;
			
		} catch (Exception e) {
			log.error("Error decrypting cipher text for username " + username, e);
			throw new FatalKeyException();
		} 
	}
		
	
	/**
	 * Get a users private key. If the key is currently cached then return it, else try to unlock and cache
	 * @param username
	 * @param passphrase
	 * @return
	 * @throws IOException
	 * @throws InvalidKeyException
	 */
	protected SshPrivateKey getPrivateKey(String username, String passphrase) throws IOException, InvalidKeyException {
		
		if(loadedPrivateKeys.containsKey(username))
			return (SshPrivateKey) loadedPrivateKeys.get(username);
		
		SshPrivateKey pk = getPrivateKeyFromStore(username, passphrase);
		
		loadedPrivateKeys.put(username, pk);
		
		return pk;
	
	}

    /**
     * Remove the users cached keys. This would generally be called when the
     * users password changes.
     *  
     * @param username user to remove cache keys for
     */
    public void removeCachedKeys(String username) {
        loadedPrivateKeys.remove(username);  
        loadedPublicKeys.remove(username);
    }
    
    /**
     * Remove a users keys
     * 
     * @param username username to remove
     */
    public void removeKeys(String username) {
        removeCachedKeys(username);
        String filename = username + ".prv";
        if(store.hasEntry(filename)) {
            try {
                store.removeEntry(filename);
            } catch (IOException e) {
                log.error("Failed to remove private key for " + username + ".", e);
            }
        } 
        filename = username + ".pub";
        if(store.hasEntry(filename)) {
            try {
                store.removeEntry(filename);
            } catch (IOException e) {
                log.error("Failed to remove public key for " + username + ".", e);
            }
        } 
    }
	
	/**
	 * Get the users private key directly from the repository. This will not return any cached key,
	 * @param username
	 * @param passphrase
	 * @return
	 * @throws IOException
	 * @throws InvalidKeyException
	 */
	protected SshPrivateKey getPrivateKeyFromStore(String username, String passphrase)  throws IOException, InvalidKeyException {

		String filename = username + ".prv";

		if (!store.hasEntry(filename)) {
		     generateKey(username, keyType, bitLength, passphrase);
		}

		SshPrivateKeyFile f = SshPrivateKeyFile.parse(store.getEntryInputStream(filename));
		
		return f.toPrivateKey(passphrase);
	
	}
	
	/**
	 * Determine if the user has a private key
	 * @param username
	 * @return
	 */
	public boolean hasPrivateKey(String username) {
		return store.hasEntry(username + ".prv");
	}
	
	/**
	 * Determine if the users key is cached.
	 * @param username
	 * @return
	 */
	public boolean hasLoadedKey(String username) {
		return loadedPrivateKeys.containsKey(username); 
	}
	
	/**
	 * Determine whether the users passphrase has changed since last time.
	 * @param username
	 * @param passphrase
	 * @return
	 */
	public boolean isPassphraseValid(String username, String passphrase) {
		
		try {
			getPrivateKeyFromStore(username, passphrase);
			return true;
			
		} catch (IOException e) {
			return false;
		} catch (InvalidKeyException e) {
			return false;
		}
	}
	
	/**
	 * Get a users public key.
	 * @param username
	 * @return
	 * @throws IOException
	 * @throws InvalidKeyException
	 */
	protected SshPublicKey getPublicKey(String username) throws IOException, InvalidKeyException {

		if(loadedPublicKeys.containsKey(username))
			return (SshPublicKey) loadedPublicKeys.get(username);
		
		String filename = username + ".pub";
		
		if(store.hasEntry(filename)) {
		     SshPublicKeyFile f = SshPublicKeyFile.parse(store.getEntryInputStream(filename));
		     SshPublicKey pk = f.toPublicKey();
			 loadedPublicKeys.put(username, pk);
			 return pk;
		}
		else
			throw new IOException("User does not have a key in the repository!");
	}
	
	protected void changePassphrase(String username, String oldPassphrase, String newPassphrase) throws IOException, InvalidKeyException, PromptForPasswordException, FatalKeyException, UpdatePrivateKeyPassphraseException {		
		String filename = username + ".prv";		
		if(store.hasEntry(filename)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
			SshKeyGenerator.changePassphrase(store.getEntryInputStream(filename), baos, oldPassphrase, newPassphrase);
			
			/*
			 * Tried this, didn't work
			loadedPrivateKeys.put(username, SshPrivateKeyFile.parse(
				new ByteArrayInputStream(baos.toByteArray())));
				
			 */
            OutputStream out = store.getEntryOutputStream(filename);
            out.write(baos.toByteArray());
            out.flush();
            Util.closeStream(out);
		} else
			throw new IOException("User does not have a key in the repository!");
	}
	
	protected void generateKey(String username, String type, int bitlength, String passphrase) throws IOException, InvalidKeyException {
		
		SshKeyGenerator.generateKeyPair(type, bitlength, username, passphrase, store.getEntryOutputStream(username + ".prv"),
				store.getEntryOutputStream(username + ".pub"));
	}
	
}
