
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
			
package com.adito.security.pki.rsa;



import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;
import com.adito.security.pki.InvalidKeyException;
import com.adito.security.pki.SshPrivateKey;
import com.adito.security.pki.SshPublicKey;


/**
 *
 *
 * @author $author$
 */
public class SshRsaPrivateKey extends SshPrivateKey {
    RSAPrivateKey prvKey;
    RSAPublicKey pubKey;

    /**
     * Creates a new SshRsaPrivateKey object.
     *
     * @param prv
     * @param pub
     */
    public SshRsaPrivateKey(RSAPrivateKey prv, RSAPublicKey pub) {
        prvKey = prv;
        pubKey = pub;
    }
    
    
    public PrivateKey getPrivateKey() {
    	return prvKey;
    }

    /**
     * Creates a new SshRsaPrivateKey object.
     *
     * @param encoded
     *
     * @throws InvalidKeyException
     */
    public SshRsaPrivateKey(byte[] encoded) throws InvalidKeyException {
        try {
            // Extract the key information
            ByteArrayReader bar = new ByteArrayReader(encoded);

            // Read the public key
            String header = bar.readString();

            if (!header.equals(getAlgorithmName())) {
                throw new InvalidKeyException();
            }

            BigInteger e = bar.readBigInteger();
            BigInteger n = bar.readBigInteger();

            // Read the private key
            BigInteger p = bar.readBigInteger();
            RSAPrivateKeySpec prvSpec = new RSAPrivateKeySpec(n, p);
            RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(n, e);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            prvKey = (RSAPrivateKey) kf.generatePrivate(prvSpec);
            pubKey = (RSAPublicKey) kf.generatePublic(pubSpec);
        } catch (Exception e) {
            throw new InvalidKeyException();
        }
    }

    /**
     *
     *
     * @param obj
     *
     * @return
     */
    public boolean equals(Object obj) {
        if (obj instanceof SshRsaPrivateKey) {
            return prvKey.equals(((SshRsaPrivateKey) obj).prvKey);
        }

        return false;
    }

    /**
     *
     *
     * @return
     */
    public int hashCode() {
        return prvKey.hashCode();
    }

    /**
     *
     *
     * @return
     */
    public String getAlgorithmName() {
        return "ssh-rsa";
    }

    /**
     *
     *
     * @return
     */
    public int getBitLength() {
        return prvKey.getModulus().bitLength();
    }

    /**
     *
     *
     * @return
     */
    public byte[] getEncoded() {
        try {
            ByteArrayWriter baw = new ByteArrayWriter();

            // The private key consists of the public key blob
            baw.write(getPublicKey().getEncoded());

            // And the private data
            baw.writeBigInteger(prvKey.getPrivateExponent());

            return baw.toByteArray();
        } catch (IOException ioe) {
            return null;
        }
    }

    /**
     *
     *
     * @return
     */
    public SshPublicKey getPublicKey() {
        return new SshRsaPublicKey(pubKey);
    }

    /**
     *
     *
     * @param data
     *
     * @return
     */
    public byte[] generateSignature(byte[] data) {
        try {
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initSign(prvKey);
            sig.update(data);

            ByteArrayWriter baw = new ByteArrayWriter();
            baw.writeString(getAlgorithmName());
            baw.writeBinaryString(sig.sign());

            return baw.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }
}
