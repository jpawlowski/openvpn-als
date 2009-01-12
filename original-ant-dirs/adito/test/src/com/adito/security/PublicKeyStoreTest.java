
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
			
package com.adito.security;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.adito.boot.Util;
import com.adito.testcontainer.AbstractTest;

/**
 */
public class PublicKeyStoreTest extends AbstractTest {
	
	static PublicKeyStore publicKeyStore;
	
	final static String USERNAME = "test1";
	final static String TEXT_TO_ENCRYPT = "{}-_=a:,123whydocatshavealltheflaps098!\"$5";
	final static String PASSPHRASE = "asecret";
	final static String NEW_PASSPHRASE = "aNotherSecret";
	

    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
    	setUp("");
    	publicKeyStore = PublicKeyStore.getInstance();
    }

    @Before
    @After
    public void intialize() throws Exception {
    	publicKeyStore.removeKeys(USERNAME);
    }

    @Test
    public void newKey() throws Exception {
    	publicKeyStore.verifyPrivateKey(USERNAME, PASSPHRASE.toCharArray());
    	assertTrue("Key creation", publicKeyStore.hasLoadedKey(USERNAME));
    }

    @Test 
    public void encryptStuff() throws Exception {
    	publicKeyStore.verifyPrivateKey(USERNAME, PASSPHRASE.toCharArray());
    	assertTrue("Key creation", publicKeyStore.hasLoadedKey(USERNAME));
    	String encrypted = publicKeyStore.encryptText(TEXT_TO_ENCRYPT, USERNAME);
    	String decrypted = publicKeyStore.decryptText(encrypted, USERNAME);
    	assertEquals("Encrypt and decrypted", TEXT_TO_ENCRYPT, decrypted);    	
    }

    @Test
    public void changePassphrase() throws Exception {
    	publicKeyStore.verifyPrivateKey(USERNAME, PASSPHRASE.toCharArray());
    	assertTrue("Key creation", publicKeyStore.hasLoadedKey(USERNAME));
    	publicKeyStore.changePrivateKeyPassphrase(USERNAME, PASSPHRASE, NEW_PASSPHRASE);
    	String encrypted = publicKeyStore.encryptText(TEXT_TO_ENCRYPT, USERNAME);
    	String decrypted = publicKeyStore.decryptText(encrypted, USERNAME);
    	assertEquals("Encrypt and decrypted", TEXT_TO_ENCRYPT, decrypted);    	
    }

    @Test
    public void passwordChangeRequired() throws Exception {
    	publicKeyStore.verifyPrivateKey(USERNAME, PASSPHRASE.toCharArray());
    	assertTrue("Key creation", publicKeyStore.hasLoadedKey(USERNAME));
    	publicKeyStore.changePrivateKeyPassphrase(USERNAME, PASSPHRASE, NEW_PASSPHRASE);
    	publicKeyStore.removeCachedKeys(USERNAME);
    	// Try and use the old password
    	try {
    		publicKeyStore.verifyPrivateKey(USERNAME, PASSPHRASE.toCharArray());
    		assertTrue("Verification should have thrown a UpdatePrivateKeyPassphraseException but didn't", false);
    	}
    	catch(UpdatePrivateKeyPassphraseException upkp) {
    		// All ok
    		publicKeyStore.verifyPrivateKey(USERNAME, NEW_PASSPHRASE.toCharArray());
        	String encrypted = publicKeyStore.encryptText(TEXT_TO_ENCRYPT, USERNAME);
        	String decrypted = publicKeyStore.decryptText(encrypted, USERNAME);
        	assertEquals("Encrypt and decrypted", TEXT_TO_ENCRYPT, decrypted);
    	}    	
    }

    @Test
    public void passwordPrompt() throws Exception {
    	publicKeyStore.verifyPrivateKey(USERNAME, PASSPHRASE.toCharArray());
    	assertTrue("Key creation", publicKeyStore.hasLoadedKey(USERNAME));
    	publicKeyStore.changePrivateKeyPassphrase(USERNAME, PASSPHRASE, NEW_PASSPHRASE);
    	publicKeyStore.removeCachedKeys(USERNAME);
    	
    	// Try and prompt for password
    	try {
    		publicKeyStore.verifyPrivateKey(USERNAME, null);
    		assertTrue("Verification should have thrown a PromptForPasswordException but didn't", false);
    	}
    	catch(PromptForPasswordException pfpe) {
    		publicKeyStore.verifyPrivateKey(USERNAME, NEW_PASSPHRASE.toCharArray());
    		// All ok
        	String encrypted = publicKeyStore.encryptText(TEXT_TO_ENCRYPT, USERNAME);
        	String decrypted = publicKeyStore.decryptText(encrypted, USERNAME);
        	assertEquals("Encrypt and decrypted", TEXT_TO_ENCRYPT, decrypted);
    	}    	
    }

    @Test
    public void failEncrypt() throws Exception {
    	try {
    		publicKeyStore.encryptText(TEXT_TO_ENCRYPT, USERNAME);
    		assertTrue("Verification should have thrown a FatalKeyException but didn't", false);
    	}
    	catch(FatalKeyException fke) {    
    		// All ok		
    	}
    }
}
