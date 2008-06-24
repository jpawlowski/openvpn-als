
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
			
package com.adito.security.pki;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.adito.boot.Util;

public class SshKeyGenerator {



    /**
     * Creates a new SshKeyGenerator object.
     */
    public SshKeyGenerator() {
    }

    /**
     *
     *
     * @param type
     * @param bits
     * @param filename
     * @param username
     * @param passphrase
     *
     * @throws IOException
     */
    public static void generateKeyPair(String type, int bits, 
        String username, String passphrase, OutputStream prvOut, OutputStream pubOut) throws IOException, InvalidKeyException {
        
    	String keyType = type;

        if (keyType.equalsIgnoreCase("DSA")) {
            keyType = "ssh-dss";
        }

        if (keyType.equalsIgnoreCase("RSA")) {
            keyType = "ssh-rsa";
        }

        final SshKeyPair pair = SshKeyPairFactory.newInstance(keyType);
        System.out.println("Generating " + String.valueOf(bits) + " bit " +
            keyType + " key pair");

        pair.generate(bits);
        
        // Now save the files
        SshPublicKeyFile pub = SshPublicKeyFile.create(pair.getPublicKey(),
                new SECSHPublicKeyFormat(username,
                    String.valueOf(bits) + "-bit " + type));
        
        pubOut.write(pub.getBytes());
        Util.closeStream(pubOut);


        SshPrivateKeyFile prv = SshPrivateKeyFile.create(pair.getPrivateKey(),
                passphrase,
                new SshtoolsPrivateKeyFormat(username,
                    String.valueOf(bits) + "-bit " + type));
        prvOut.write(prv.getBytes());
        Util.closeStream(prvOut);
    }


    /**
     *
     *
     * @param f
     * @param oldPassphrase
     * @param newPassphrase
     *
     * @throws IOException
     * @throws InvalidKeyException
     */
    public static void changePassphrase(InputStream prvIn, OutputStream prvOut, String oldPassphrase,
        String newPassphrase) throws IOException, InvalidKeyException {
        // Open up the file with its current format
        SshPrivateKeyFile file = SshPrivateKeyFile.parse(prvIn);
        file.changePassphrase(oldPassphrase, newPassphrase);
        
        Util.closeStream(prvIn);
        
        try {
            prvOut.write(file.getBytes());
        } finally {
            Util.closeStream(prvOut);
        }
    }




}
