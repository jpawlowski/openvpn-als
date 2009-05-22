
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


import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;
import com.ovpnals.security.pki.InvalidKeyException;
import com.ovpnals.security.pki.InvalidSignatureException;
import com.ovpnals.security.pki.SshPublicKey;


/**
 *
 *
 * @author $author$
 */
public class SshRsaPublicKey extends SshPublicKey {
    RSAPublicKey pubKey;

    /**
     * Creates a new SshRsaPublicKey object.
     *
     * @param key
     */
    public SshRsaPublicKey(RSAPublicKey key) {
        pubKey = key;
    }
    
    
    public PublicKey getPublicKey() {
    	return pubKey;
    }

    /**
     * Creates a new SshRsaPublicKey object.
     *
     * @param encoded
     *
     * @throws InvalidKeyException
     */
    public SshRsaPublicKey(byte[] encoded) throws InvalidKeyException {
        try {
            //this.hostKey = hostKey;
            RSAPublicKeySpec rsaKey;

            // Extract the key information
            ByteArrayReader bar = new ByteArrayReader(encoded);
            String header = bar.readString();

            if (!header.equals(getAlgorithmName())) {
                throw new InvalidKeyException();
            }

            BigInteger e = bar.readBigInteger();
            BigInteger n = bar.readBigInteger();
            rsaKey = new RSAPublicKeySpec(n, e);

            try {
                KeyFactory kf = KeyFactory.getInstance("RSA");
                pubKey = (RSAPublicKey) kf.generatePublic(rsaKey);
            } catch (NoSuchAlgorithmException nsae) {
                throw new InvalidKeyException();
            } catch (InvalidKeySpecException ikpe) {
                throw new InvalidKeyException();
            }
        } catch (IOException ioe) {
            throw new InvalidKeyException();
        }
    }

    /**
     *
     *
     * @return String
     */
    public String getAlgorithmName() {
        return "ssh-rsa";
    }

    /**
     *
     *
     * @return int
     */
    public int getBitLength() {
        return pubKey.getModulus().bitLength();
    }

    /**
     *
     *
     * @return byte[]
     */
    public byte[] getEncoded() {
        try {
            ByteArrayWriter baw = new ByteArrayWriter();
            baw.writeString(getAlgorithmName());
            baw.writeBigInteger(pubKey.getPublicExponent());
            baw.writeBigInteger(pubKey.getModulus());

            return baw.toByteArray();
        } catch (IOException ioe) {
            return null;
        }
    }

    /**
     *
     *
     * @param signature
     * @param data
     *
     * @return boolean
     *
     * @throws InvalidSignatureException
     */
    public boolean verifySignature(byte[] signature, byte[] data)
        throws InvalidSignatureException {
        try {
            // Check for older versions of the transport protocol
            if (signature.length != 128) {
                ByteArrayReader bar = new ByteArrayReader(signature);
                byte[] sig = bar.readBinaryString();
                String header = new String(sig);

                if (!header.equals(getAlgorithmName())) {
                    throw new InvalidSignatureException();
                }

                signature = bar.readBinaryString();
            }

            Signature s = Signature.getInstance("SHA1withRSA");
            s.initVerify(pubKey);
            s.update(data);

            return s.verify(signature);
        } catch (NoSuchAlgorithmException nsae) {
            throw new InvalidSignatureException();
        } catch (IOException ioe) {
            throw new InvalidSignatureException();
        } catch (java.security.InvalidKeyException ike) {
            throw new InvalidSignatureException();
        } catch (SignatureException se) {
            throw new InvalidSignatureException();
        }
    }
}
