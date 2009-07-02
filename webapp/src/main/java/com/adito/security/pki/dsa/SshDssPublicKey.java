
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
			
package com.adito.security.pki.dsa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.maverick.util.ByteArrayReader;
import com.maverick.util.ByteArrayWriter;
import com.adito.security.pki.InvalidKeyException;
import com.adito.security.pki.InvalidSignatureException;
import com.adito.security.pki.SimpleASNWriter;
import com.adito.security.pki.SshPublicKey;

/**
 *
 *
 * @author $author$
 */
public class SshDssPublicKey extends SshPublicKey {
    private static Log log = LogFactory.getLog(SshDssPublicKey.class);
    private DSAPublicKey pubkey;

    /**
     * Creates a new SshDssPublicKey object.
     *
     * @param key
     */
    public SshDssPublicKey(DSAPublicKey key) {
        this.pubkey = key;
    }

    /**
     * Creates a new SshDssPublicKey object.
     *
     * @param key
     *
     * @throws InvalidKeyException
     */
    public SshDssPublicKey(byte[] key) throws InvalidKeyException {
        try {
            DSAPublicKeySpec dsaKey;

            // Extract the key information
            ByteArrayReader bar = new ByteArrayReader(key);
            String header = bar.readString();

            if (!header.equals(getAlgorithmName())) {
                throw new InvalidKeyException();
            }

            BigInteger p = bar.readBigInteger();
            BigInteger q = bar.readBigInteger();
            BigInteger g = bar.readBigInteger();
            BigInteger y = bar.readBigInteger();
            dsaKey = new DSAPublicKeySpec(y, p, q, g);

            KeyFactory kf = KeyFactory.getInstance("DSA");
            
            pubkey = (DSAPublicKey) kf.generatePublic(dsaKey);
        } catch (Exception e) {
            throw new InvalidKeyException();
        }
    }
    
    public PublicKey getPublicKey() {
    	return pubkey;
    }    

    /**
     *
     *
     * @return
     */
    public String getAlgorithmName() {
        return "ssh-dss";
    }

    /**
     *
     *
     * @return
     */
    public int getBitLength() {
        return pubkey.getY().bitLength();
    }

    /**
     *
     *
     * @return
     */
    public byte[] getEncoded() {
        try {
            ByteArrayWriter baw = new ByteArrayWriter();
            baw.writeString(getAlgorithmName());
            baw.writeBigInteger(pubkey.getParams().getP());
            baw.writeBigInteger(pubkey.getParams().getQ());
            baw.writeBigInteger(pubkey.getParams().getG());
            baw.writeBigInteger(pubkey.getY());

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
     * @return
     *
     * @throws InvalidSshKeySignatureException
     */
    public boolean verifySignature(byte[] signature, byte[] data)
        throws InvalidSignatureException {
        try {
            // Check for differing version of the transport protocol
            if (signature.length != 40) {
                ByteArrayReader bar = new ByteArrayReader(signature);
                byte[] sig = bar.readBinaryString();

                //if (log.isDebugEnabled()) {log.debug("Signature blob is " + new String(sig));}
                String header = new String(sig);
                if (log.isDebugEnabled())
                	log.debug("Header is " + header);

                if (!header.equals("ssh-dss")) {
                    throw new InvalidSignatureException();
                }

                signature = bar.readBinaryString();

                //if (log.isDebugEnabled()) {log.debug("Read signature from blob: " + new String(signature));}
            }

            // Using a SimpleASNWriter
            ByteArrayOutputStream r = new ByteArrayOutputStream();
            ByteArrayOutputStream s = new ByteArrayOutputStream();
            SimpleASNWriter asn = new SimpleASNWriter();
            asn.writeByte(0x02);

            if (((signature[0] & 0x80) == 0x80) && (signature[0] != 0x00)) {
                r.write(0);
                r.write(signature, 0, 20);
            } else {
                r.write(signature, 0, 20);
            }

            asn.writeData(r.toByteArray());
            asn.writeByte(0x02);

            if (((signature[20] & 0x80) == 0x80) && (signature[20] != 0x00)) {
                s.write(0);
                s.write(signature, 20, 20);
            } else {
                s.write(signature, 20, 20);
            }

            asn.writeData(s.toByteArray());

            SimpleASNWriter asnEncoded = new SimpleASNWriter();
            asnEncoded.writeByte(0x30);
            asnEncoded.writeData(asn.toByteArray());

            byte[] encoded = asnEncoded.toByteArray();

            if (log.isDebugEnabled()) {
                log.debug("Verifying host key signature");
                log.debug("Signature length is " +
                    String.valueOf(signature.length));

                String hex = "";

                for (int i = 0; i < signature.length; i++) {
                    hex += (Integer.toHexString(signature[i] & 0xFF) + " ");
                }

                log.debug("SSH: " + hex);
                hex = "";

                for (int i = 0; i < encoded.length; i++) {
                    hex += (Integer.toHexString(encoded[i] & 0xFF) + " ");
                }

                log.debug("Encoded: " + hex);
            }

            // The previous way

            /*byte[] encoded;
                         // Determine the encoded length of the big integers
                         int rlen = (((signature[0] & 0x80) == 0x80) ? 0x15 : 0x14);
                         log.debug("rlen=" + String.valueOf(rlen));
                         int slen = (((signature[20] & 0x80) == 0x80) ? 0x15 : 0x14);
                         log.debug("slen=" + String.valueOf(slen));
                 byte[] asn1r = { 0x30, (byte) (rlen + slen + 4), 0x02, (byte) rlen };
                         byte[] asn1s = { 0x02, (byte) slen };
                         // Create the encoded byte array
                 encoded = new byte[asn1r.length + rlen + asn1s.length + slen];
                         // Copy the data and encode it into the array
                         System.arraycopy(asn1r, 0, encoded, 0, asn1r.length);
                         // Copy the integer inserting a zero byte if signed
                         int roffset = (((signature[0] & 0x80) == 0x80) ? 1 : 0);
                 System.arraycopy(signature, 0, encoded, asn1r.length + roffset, 20);
                 System.arraycopy(asn1s, 0, encoded, asn1r.length + roffset + 20,
                asn1s.length);
                         int soffset = (((signature[20] & 0x80) == 0x80) ? 1 : 0);
                         System.arraycopy(signature, 20, encoded,
                asn1r.length + roffset + 20 + asn1s.length + soffset, 20);
             */
            Signature sig = Signature.getInstance("SHA1withDSA");
            sig.initVerify(pubkey);
            sig.update(data);

            return sig.verify(encoded);
        } catch (NoSuchAlgorithmException nsae) {
            throw new InvalidSignatureException();
        } catch (java.security.InvalidKeyException ike) {
            throw new InvalidSignatureException();
        } catch (IOException ioe) {
            throw new InvalidSignatureException();
        } catch (SignatureException se) {
            throw new InvalidSignatureException();
        }
    }
}
