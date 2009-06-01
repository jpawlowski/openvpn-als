
				/*
 *  OpenVPNALS
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
			
package com.maverick.crypto.publickey;

import java.io.IOException;

import com.maverick.crypto.digests.SHA1Digest;
import java.math.BigInteger;


public class DsaPublicKey
    extends DsaKey implements PublicKey {

  protected BigInteger y;

  /**
   * Contruct an uninitialized DSA public key
   */
  public DsaPublicKey(BigInteger p,
                      BigInteger q,
                      BigInteger g,
                      BigInteger y) {
    super(p, q, g);
    this.y = y;
  }

  public DsaPublicKey() {

  }

  public BigInteger getY() {
    return y;
  }

  /**
   * Get the bit length of this key.
   * @return the bit length of key.
   */
  public int getBitLength() {
    return p.bitLength();
  }


  /**
   * Verify the signature of the data to determine whether the signature was
   * produced by the corresponding private key.
   * @param signature
   * @param data
   * @return <code>true</code> if the signature is valid, otherwise <code>false</code>
   * @throws IOException
   */
  public boolean verifySignature(byte[] signature, byte[] msg) {

      // Create a SHA1 hash of the message
      SHA1Digest h = new SHA1Digest();
      h.update(msg, 0, msg.length);
      byte[] data = new byte[h.getDigestSize()];
      h.doFinal(data, 0);

      return Dsa.verify(y, p, q, g, signature, data);
  }

  /**
   * return true if the value r and s represent a DSA signature for
   * the passed in message for standard DSA the message should be a
   * SHA-1 hash of the real message to be verified.
   *
   * @return <code>true</code> if the values of r and s represent a DSA
   * signature for the passed in message, otherwise <code>false</code>.
   */
  protected boolean verifySignature(
      byte[] msg,
      BigInteger r,
      BigInteger s) {

    // Create a SHA1 hash of the message
    SHA1Digest h = new SHA1Digest();
    h.update(msg, 0, msg.length);
    byte[] data = new byte[h.getDigestSize()];
    h.doFinal(data, 0);


    BigInteger m = new BigInteger(1, data);
    m = m.mod(q);

    if (BigInteger.valueOf(0).compareTo(r) >= 0 || q.compareTo(r) <= 0) {
      return false;
    }

    if (BigInteger.valueOf(0).compareTo(s) >= 0 || q.compareTo(s) <= 0) {
      return false;
    }

    BigInteger w = s.modInverse(q);
    BigInteger u1 = m.multiply(w).mod(q);
    BigInteger u2 = r.multiply(w).mod(q);

    BigInteger v = g.modPow(u1, p).multiply(y.modPow(u2, p)).mod(p).mod(q);

    return (v.compareTo(r) == 0);

  }
}
