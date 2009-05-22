
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
			
package com.ovpnals.security.pki.rsa;


import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import com.ovpnals.security.pki.InvalidKeyException;
import com.ovpnals.security.pki.SshKeyPair;
import com.ovpnals.security.pki.SshPrivateKey;
import com.ovpnals.security.pki.SshPublicKey;
import com.ovpnals.security.pki.Utils;


/**
 *
 *
 * @author $author$
 */
public class SshRsaKeyPair extends SshKeyPair {
    private RSAPrivateKey prvKey;
    private RSAPublicKey pubKey;

    /**
     * Creates a new SshRsaKeyPair object.
     */
    public SshRsaKeyPair() {
    }

    /**
     *
     *
     * @param encoded
     *
     * @return
     *
     * @throws InvalidKeyException
     */
    public SshPrivateKey decodePrivateKey(byte[] encoded)
        throws InvalidKeyException {
        return new SshRsaPrivateKey(encoded);
    }

    /**
     *
     *
     * @param encoded
     *
     * @return
     *
     * @throws InvalidKeyException
     */
    public SshPublicKey decodePublicKey(byte[] encoded)
        throws InvalidKeyException {
        return new SshRsaPublicKey(encoded);
    }

    /**
     *
     *
     * @param bits
     */
    public void generate(int bits) {
        try {
            // Initialize the generator
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(bits, Utils.getRND());

            KeyPair pair = keyGen.generateKeyPair();

            // Get the keys and set
            setPrivateKey(new SshRsaPrivateKey(
                    (RSAPrivateKey) pair.getPrivate(),
                    (RSAPublicKey) pair.getPublic()));
        } catch (NoSuchAlgorithmException nsae) {
            prvKey = null;
            pubKey = null;
        }
    }
}
